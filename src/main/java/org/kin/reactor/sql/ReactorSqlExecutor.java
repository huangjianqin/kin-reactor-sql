package org.kin.reactor.sql;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.*;
import org.kin.reactor.sql.feature.filter.FilterFeature;
import org.kin.reactor.sql.feature.map.ValueMapFeature;
import org.kin.reactor.sql.utils.StringUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * sql executor
 * @author huangjianqin
 * @date 2022/12/13
 */
public final class ReactorSqlExecutor {
    /** parsed sql */
    private final PlainSelect parsedSql;
    /** sql 执行上下文*/
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
    public Flux<Result> apply(Flux<?> datasource) {
        //raw record
        Flux<Record> recordFlux = datasource.map(obj -> Record.newRecord(null, obj, context))
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

    private Flux<Result> selectMapper(Flux<Record> recordFlux){
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
                    String fAlias = StringUtils.cleanDoubleQuotation(alias);
                    // select a,b,c
                    Function<Record, Object> mapper = ValueMapFeature.createMapperOrThrow(expression);
                    mappers.put(fAlias, mapper);
                }

                @Override
                public void visit(AllColumns columns) {
                    //select *
                    allMapper.add(Record::putRecordToResult);
                }

                @Override
                public void visit(AllTableColumns columns) {
                    //select t.*
                    String name;
                    Alias alias = columns.getTable().getAlias();
                    if (alias == null) {
                        name = StringUtils.cleanDoubleQuotation(columns.getTable().getName());
                    } else {
                        name = StringUtils.cleanDoubleQuotation(alias.getName());
                    }
                    allMapper.add(record -> {
                        Object val = record.getRecord(name);
                        if (val instanceof Map) {
                            record.setResults(((Map) val));
                        } else {
                            record.setResult(name, val);
                        }
                    });
                }
            });
        }

        return recordFlux.map(record -> {
            for (Map.Entry<String, Function<Record, Object>> entry : mappers.entrySet()) {
                record.setResult(entry.getKey(), entry.getValue().apply(record));
            }

            for (Consumer<Record> consumer : allMapper) {
                consumer.accept(record);
            }

            return record.toResult();
        });
    }
}
