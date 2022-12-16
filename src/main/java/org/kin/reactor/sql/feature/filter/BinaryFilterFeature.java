package org.kin.reactor.sql.feature.filter;

import net.sf.jsqlparser.expression.Expression;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.feature.map.ValueMapFeature;
import org.kin.reactor.sql.utils.ObjectUtils;
import reactor.util.function.Tuple2;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 二元条件判断操作符
 *
 * @author huangjianqin
 * @date 2022/12/15
 */
abstract class BinaryFilterFeature implements FilterFeature<Expression> {
    /** feature id */
    private final String id;

    protected BinaryFilterFeature() {
        throw new UnsupportedOperationException();
    }

    protected BinaryFilterFeature(String id) {
        this.id = FeatureIds.wrapFilterId(id);
    }

    @Override
    public final String id() {
        return id;
    }

    @Override
    public final BiFunction<Record, Object, Boolean> predicate(Expression expression) {
        Tuple2<Function<Record, Object>, Function<Record, Object>> tuple2 = ValueMapFeature.createBinaryMapper(expression);
        Function<Record, Object> leftMapper = tuple2.getT1();
        Function<Record, Object> rightMapper = tuple2.getT2();

        return (r, c) -> predicate(leftMapper.apply(r), rightMapper.apply(r));
    }

    /**
     * 条件判断
     *
     * @param left  左表达式
     * @param right 右表达式
     * @return 条件判断结果
     */
    public boolean predicate(Object left, Object right) {
        try {
            if (left instanceof Map && ((Map<?, ?>) left).size() == 1) {
                left = ((Map<?, ?>) left).values().iterator().next();
            }
            if (right instanceof Map && ((Map<?, ?>) right).size() == 1) {
                right = ((Map<?, ?>) right).values().iterator().next();
            }
            if (left instanceof Date || right instanceof Date ||
                    left instanceof LocalDateTime || right instanceof LocalDateTime ||
                    left instanceof Instant || right instanceof Instant) {
                return doPredicate(ObjectUtils.castDate(left), ObjectUtils.castDate(right));
            }
            if (left instanceof Number || right instanceof Number) {
                return doPredicate(ObjectUtils.castNumber(left), ObjectUtils.castNumber(right));
            }
            if (left instanceof String || right instanceof String) {
                return doPredicate(String.valueOf(left), String.valueOf(right));
            }
            return doPredicate(left, right);
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 数字判断
     *
     * @param left  左数字
     * @param right 右数字
     * @return 判断结果
     */
    protected abstract boolean doPredicate(Number left, Number right);

    /**
     * 时间判断
     *
     * @param left  左时间
     * @param right 右时间
     * @return 判断结果
     */
    protected abstract boolean doPredicate(Date left, Date right);

    /**
     * 字符串判断
     *
     * @param left  左字符串
     * @param right 右字符串
     * @return 判断结果
     */
    protected abstract boolean doPredicate(String left, String right);

    /**
     * Object对象判断
     *
     * @param left  左Object对象
     * @param right 右Object对象
     * @return 判断结果
     */
    protected abstract boolean doPredicate(Object left, Object right);
}

