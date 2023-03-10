package org.kin.reactor.sql;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.*;
import org.kin.reactor.sql.feature.filter.FilterFeature;
import org.kin.reactor.sql.feature.map.ValueMapFeature;
import org.kin.reactor.sql.utils.StringUtils;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * sql executor
 *
 * @author huangjianqin
 * @date 2022/12/13
 */
public final class ReactorSqlExecutor {
    /** parsed sql */
    private final PlainSelect parsedSql;
    /** sql 执行上下文 */
    private final Context context = new Context();

    public ReactorSqlExecutor(PlainSelect parsedSql) {
        this.parsedSql = parsedSql;
    }

    /**
     * 绑定sql下标参数, 下标自增
     *
     * @param param 参数值
     */
    public ReactorSqlExecutor bind(Object param) {
        context.bind(param);
        return this;
    }

    /**
     * 给指定下标的参数绑定值, 如:5
     *
     * @param index 参数下标
     * @param param 参数值
     */
    public ReactorSqlExecutor bind(int index, Object param) {
        context.bind(index, param);
        return this;
    }

    /**
     * 绑定命名参数, 如:a
     *
     * @param name  参数名
     * @param param 参数值
     */
    public ReactorSqlExecutor bind(String name, Object param) {
        context.bind(name, param);
        return this;
    }

    /**
     * 将sql逻辑应用到指定{@code source}数据流中
     *
     * @param datasource 数据流
     * @return sql select result
     */
    public <RAW, ACT> Flux<Result> apply(Flux<RAW> datasource) {
        return apply(datasource, Function.identity());
    }

    /**
     * 将sql逻辑应用到指定{@code source}数据流中
     * mapper的使用场景是针对数据流对象是数据+元数据的组合体, sql逻辑仅允许访问数据, 不允许访问元数据, 但得到处理结果后, 需要结合元数据进行下一步逻辑操作的场景
     * 当sql逻辑处理完后, 可以使用{@link  Result#getRaw()}对原始数据进行访问
     *
     * @param datasource 数据流
     * @param mapper    从<RAW>提取真实需要处理的数据<ACT>
     * @return sql select result
     */
    public <RAW, ACT> Flux<Result> apply(Flux<RAW> datasource, Function<RAW, ACT> mapper) {
        //raw record
        Flux<Record> recordFlux = datasource.map(obj -> new Record(obj, mapper.apply(obj), context))
                .elapsed()
                .index((index, item) -> {
                    Map<String, Object> rowInfo = new HashMap<>(2);
                    //行号
                    rowInfo.put("index", index + 1);
                    //自上一行数据已经过去的时间ms
                    rowInfo.put("elapsed", item.getT1());
                    item.getT2().addRecord("rowInfo", rowInfo);
                    return item.getT2();
                });

        //where
        Expression where = parsedSql.getWhere();
        if (Objects.nonNull(where)) {
            BiFunction<Record, Object, Boolean> filterFunc = FilterFeature.createFilterOrThrow(where);
            recordFlux = recordFlux.filter(r -> filterFunc.apply(r, r.getRecord()));
        }

        return selectMapper(recordFlux);
    }

    /**
     * 处理select
     */
    private Flux<Result> selectMapper(Flux<Record> recordFlux) {
        //单值映射
        Map<String, Function<Record, Object>> mappers = new LinkedHashMap<>();
        //所有值
        List<Consumer<Record>> allMapper = new ArrayList<>();

        for (SelectItem selectItem : parsedSql.getSelectItems()) {
            selectItem.accept(new SelectItemVisitorAdapter() {
                @Override
                public void visit(SelectExpressionItem item) {
                    //select a,b,c
                    Expression expression = item.getExpression();
                    String alias = item.getAlias() == null ? expression.toString() : item.getAlias().getName();
                    //如果使用函数, 但没有as, 则以函数名作为最终结果的列名
                    String fAlias = StringUtils.cleanFunc(StringUtils.trimDeclaration(alias));
                    //table.a.b, 去掉table
                    if (fAlias.contains(".")) {
                        fAlias = fAlias.substring(fAlias.indexOf(".") + 1);
                    }
                    //select a,b,c
                    Function<Record, Object> mapper = ValueMapFeature.createMapperOrThrow(expression);
                    if (Objects.nonNull(mappers.put(fAlias, mapper))) {
                        throw new IllegalStateException(String.format("column name conflict '%s'", fAlias));
                    };
                }

                @Override
                public void visit(AllColumns columns) {
                    //select *
                    allMapper.add(Record::saveResult);
                }

                @Override
                public void visit(AllTableColumns columns) {
                    //select t.*
                    String name;
                    Alias alias = columns.getTable().getAlias();
                    if (alias == null) {
                        name = StringUtils.trimDeclaration(columns.getTable().getName());
                    } else {
                        name = StringUtils.trimDeclaration(alias.getName());
                    }
                    allMapper.add(record -> record.saveResult(name, record.getRecord(name)));
                }
            });
        }

        //结果映射
        return recordFlux.map(record -> {
            for (Map.Entry<String, Function<Record, Object>> entry : mappers.entrySet()) {
                record.saveResult(entry.getKey(), entry.getValue().apply(record));
            }

            for (Consumer<Record> consumer : allMapper) {
                consumer.accept(record);
            }

            return record.toResult();
        });
    }
}
