package org.kin.reactor.sql.feature.map;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import org.kin.reactor.sql.ReactorSql;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.Feature;
import org.kin.reactor.sql.visitor.ValueMapParser;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 值映射
 *
 * @author huangjianqin
 * @date 2022/12/14
 */
public interface ValueMapFeature<E extends Expression> extends Feature {
    /**
     * 值映射逻辑
     *
     * @param expression 值映射表达式
     * @return 值映射逻辑
     */
    Function<Record, Object> map(E expression);

    /**
     * 根据值映射表达式创建值映射逻辑
     *
     * @param expression 值映射表达式
     * @return predicate逻辑
     */
    static Function<Record, Object> createMapper(Expression expression) {
        ValueMapParser valueMapParser = new ValueMapParser();
        expression.accept(valueMapParser);
        return valueMapParser.getResult();
    }

    /**
     * 根据值映射表达式创建值映射逻辑, 如果不支持则直接抛异常
     *
     * @param expression 值映射表达式
     * @return predicate逻辑
     */
    static Function<Record, Object> createMapperOrThrow(Expression expression) {
        Function<Record, Object> func = createMapper(expression);
        if (Objects.isNull(func)) {
            throw new UnsupportedOperationException("not support mapper: " + expression);
        }
        return func;
    }

    /**
     * 根据表达式创建对比函数,如: a > b , gt(a,b);
     * 并返回左右函数的二元组,{@link Tuple2#getT1()}为左边的表达式转换函数,{@link Tuple2#getT2()} 为右边的操作函数
     * <p>
     * 仅支持只有2个参数的sql函数
     *
     * @param expression 表达式
     * @return 函数二元组
     */
    static Tuple2<Function<Record, Object>, Function<Record, Object>> createBinaryMapper(Expression expression) {
        Expression left;
        Expression right;
        if (expression instanceof net.sf.jsqlparser.expression.Function) {
            net.sf.jsqlparser.expression.Function function = ((net.sf.jsqlparser.expression.Function) expression);
            List<Expression> expressions;
            //只能有2个参数
            if (function.getParameters() == null
                    || Objects.isNull(expressions = function.getParameters().getExpressions())
                    || expressions.size() != 2) {
                throw new IllegalArgumentException("binary mapper parameters number must be 2 :" + expression);
            }
            left = expressions.get(0);
            right = expressions.get(1);
        } else if (expression instanceof BinaryExpression) {
            BinaryExpression bie = ((BinaryExpression) expression);
            left = bie.getLeftExpression();
            right = bie.getRightExpression();
        } else {
            throw new UnsupportedOperationException("unsupported binary mapper:" + expression);
        }
        Function<Record, Object> leftMapper = createMapperOrThrow(left);
        Function<Record, Object> rightMapper = createMapperOrThrow(right);
        return Tuples.of(leftMapper, rightMapper);
    }
}
