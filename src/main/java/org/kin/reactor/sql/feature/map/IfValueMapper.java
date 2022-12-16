package org.kin.reactor.sql.feature.map;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.feature.Functions;
import org.kin.reactor.sql.feature.filter.FilterFeature;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * if(... < ...) ...
 * if(... between .. and ...) ...
 *
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class IfValueMapper implements ValueMapFeature<Function> {
    @Override
    public String id() {
        return FeatureIds.IF;
    }

    @Override
    public java.util.function.Function<Record, Object> map(Function function) {
        List<Expression> expressions;
        if (function.getParameters() == null ||
                Objects.isNull(expressions = function.getParameters().getExpressions()) ||
                expressions.size() < 2) {
            throw new IllegalArgumentException("function param num must be at least 2" + function);
        }

        BiFunction<Record, Object, Boolean> ifPredicate = FilterFeature.createFilterOrThrow(expressions.get(0));

        java.util.function.Function<Record, Object> ifMapper = ValueMapFeature.createMapperOrThrow(expressions.get(1));
        java.util.function.Function<Record, Object> elseMapper = expressions.size() == 3
                ? ValueMapFeature.createMapperOrThrow(expressions.get(2)) : Functions.RETURN_NULL_FUNC;

        return r -> {
            if (ifPredicate.apply(r, r)) {
                return ifMapper.apply(r);
            } else {
                return elseMapper.apply(r);
            }
        };
    }
}
