package org.kin.reactor.sql.feature.filter;

import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.feature.map.ValueMapFeature;
import reactor.util.function.Tuple2;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * like
 *
 * @author huangjianqin
 * @date 2022/12/15
 */
public final class LikeFilter implements FilterFeature<LikeExpression> {
    @Override
    public String id() {
        return FeatureIds.LIKE;
    }

    @Override
    public BiFunction<Record, Object, Boolean> predicate(LikeExpression like) {
        boolean not = like.isNot();
        Tuple2<Function<Record, Object>, Function<Record, Object>> tuple2 = ValueMapFeature.createBinaryMapper(like);

        Function<Record, Object> leftMapper = tuple2.getT1();
        Function<Record, Object> rightMapper = tuple2.getT2();

        return (r, c) -> predicate(not, leftMapper.apply(r), rightMapper.apply(r));
    }

    /**
     * like通用判断逻辑
     */
    public static boolean predicate(boolean not, Object left, Object right) {
        String strLeft = String.valueOf(left);
        String strRight = String.valueOf(right).replace("%", ".*");
        return not != (strLeft.matches(strRight));
    }
}
