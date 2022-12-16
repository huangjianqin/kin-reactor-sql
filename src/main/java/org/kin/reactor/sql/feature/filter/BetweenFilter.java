package org.kin.reactor.sql.feature.filter;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.feature.map.ValueMapFeature;
import org.kin.reactor.sql.utils.ObjectUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * {left} between {bStart} amd {bEnd}
 *
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class BetweenFilter implements FilterFeature<Between> {
    @Override
    public String id() {
        return FeatureIds.BETWEEN;
    }

    @Override
    public BiFunction<Record, Object, Boolean> predicate(Between between) {
        Expression left = between.getLeftExpression();
        Expression bStart = between.getBetweenExpressionStart();
        Expression bEnd = between.getBetweenExpressionEnd();

        Function<Record, Object> leftMapper = ValueMapFeature.createMapperOrThrow(left);
        Function<Record, Object> bStartMapper = ValueMapFeature.createMapperOrThrow(bStart);
        Function<Record, Object> bEndMapper = ValueMapFeature.createMapperOrThrow(bEnd);

        boolean isNot = between.isNot();

        return (r, c) -> isNot != predicate(leftMapper.apply(r), bStartMapper.apply(r), bEndMapper.apply(r));
    }

    /**
     * between逻辑统一判断
     *
     * @param left   检查字段
     * @param bStart 左区间
     * @param bEnd   右区间
     * @return 字段是否在指定范围内
     */
    public static boolean predicate(Object left, Object bStart, Object bEnd) {
        if (left == null || bStart == null || bEnd == null) {
            return false;
        }

        if (left.equals(bStart) || left.equals(bEnd)) {
            return true;
        }

        if (left instanceof Date || bStart instanceof Date || bEnd instanceof Date) {
            left = ObjectUtils.castDate(left);
            bStart = ObjectUtils.castDate(bStart);
            bEnd = ObjectUtils.castDate(bEnd);
        }

        if (left instanceof Number || bStart instanceof Number || bEnd instanceof Number) {
            double doubleVal = ObjectUtils.castNumber(left).doubleValue();
            return doubleVal >= ObjectUtils.castNumber(bStart).doubleValue() && doubleVal <= ObjectUtils.castNumber(bEnd).doubleValue();
        }

        Object[] arr = new Object[]{left, bStart, bEnd};
        Arrays.sort(arr);
        return arr[1] == left;
    }
}
