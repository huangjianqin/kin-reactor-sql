package org.kin.reactor.sql.feature.filter;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;

import java.util.function.BiFunction;

/**
 * or
 * @author huangjianqin
 * @date 2022/12/15
 */
public final class OrFilter implements FilterFeature<OrExpression>{
    @Override
    public String id() {
        return FeatureIds.OR;
    }

    @Override
    public BiFunction<Record, Object, Boolean> predicate(OrExpression or) {
        Expression leftExpr = or.getLeftExpression();
        Expression rightExpr = or.getRightExpression();

        BiFunction<Record, Object, Boolean> leftPredicate = FilterFeature.createFilterOrThrow(leftExpr);
        BiFunction<Record, Object, Boolean> rightPredicate = FilterFeature.createFilterOrThrow(rightExpr);

        return (r, c) -> leftPredicate.apply(r, c) || rightPredicate.apply(r, c);
    }
}
