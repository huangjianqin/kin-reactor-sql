package org.kin.reactor.sql.feature.filter;

import net.sf.jsqlparser.expression.Expression;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.Feature;
import org.kin.reactor.sql.visitor.FilterParser;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * where条件过滤
 *
 * @author huangjianqin
 * @date 2022/12/14
 */
public interface FilterFeature<E extends Expression> extends Feature {
    /**
     * 根据条件表达式创建predicate逻辑
     *
     * @param expression 条件表达式
     * @return predicate逻辑
     */
    BiFunction<Record, Object, Boolean> predicate(E expression);

    /**
     * 根据where表达式创建predicate逻辑
     *
     * @param expression where表达式
     * @return predicate逻辑
     */
    static BiFunction<Record, Object, Boolean> createFilter(Expression expression) {
        FilterParser filterParser = new FilterParser();
        expression.accept(filterParser);
        return filterParser.getResult();
    }

    /**
     * 根据where表达式创建predicate逻辑, 如果不支持则直接抛异常
     *
     * @param expression where表达式
     * @return predicate逻辑
     */
    static BiFunction<Record, Object, Boolean> createFilterOrThrow(Expression expression) {
        BiFunction<Record, Object, Boolean> func = createFilter(expression);
        if (Objects.isNull(func)) {
            throw new UnsupportedOperationException("not support filter: " + expression);
        }
        return func;
    }
}
