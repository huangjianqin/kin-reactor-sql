package org.kin.reactor.sql.feature.filter;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.feature.map.ValueMapFeature;
import org.kin.reactor.sql.utils.CompareUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * in
 * @author huangjianqin
 * @date 2022/12/15
 */
public final class InFilter implements FilterFeature<InExpression>{
    @Override
    public String id() {
        return FeatureIds.IN;
    }

    @Override
    public BiFunction<Record, Object, Boolean> predicate(InExpression inExpression) {
        //... in
        Expression left = inExpression.getLeftExpression();
        //in (a, b, c)
        ItemsList rightItems = inExpression.getRightItemsList();

        List<Function<Record, Object>> rightItemMappers = new ArrayList<>();

        if (rightItems instanceof ExpressionList) {
            rightItemMappers.addAll(((ExpressionList) rightItems).getExpressions().stream()
                    .map(ValueMapFeature::createMapperOrThrow)
                    .collect(Collectors.toList()));
        }
        if (rightItems instanceof SubSelect) {
            throw new UnsupportedOperationException("not support in select");
        }

        Function<Record, Object> leftMapper = ValueMapFeature.createMapperOrThrow(left);

        boolean not = inExpression.isNot();
        return (r, c) -> {
            Object mappedLeft = leftMapper.apply(r);
            for (Function<Record, Object> rightItemMapper : rightItemMappers) {
                if (CompareUtils.equals(rightItemMapper.apply(r), mappedLeft)) {
                    return !not;
                }
            }

            return not;
        };
    }
}
