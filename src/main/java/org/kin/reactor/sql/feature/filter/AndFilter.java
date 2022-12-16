package org.kin.reactor.sql.feature.filter;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import org.kin.reactor.sql.ReactorSql;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;

import java.util.function.BiFunction;

/**
 * and
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class AndFilter implements FilterFeature<AndExpression>{
    @Override
    public String id() {
        return FeatureIds.AND;
    }

    @Override
    public BiFunction<Record, Object, Boolean> predicate(AndExpression and) {
        Expression left = and.getLeftExpression();
        Expression right = and.getRightExpression();

        BiFunction<Record, Object, Boolean> leftFilter = FilterFeature.createFilterOrThrow(left);
        BiFunction<Record, Object, Boolean> rightFilter = FilterFeature.createFilterOrThrow(right);

        return (r, c) -> leftFilter.apply(r, c) && rightFilter.apply(r, c);
    }
}
