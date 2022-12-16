package org.kin.reactor.sql.feature.map;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.utils.ExpressionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * coalesce(..., ...)
 * 遇到非null值即停止并返回该值. 如果所有的表达式都是空值, 最终将返回一个空值
 *
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class CoalesceMapper implements ValueMapFeature<Function> {
    @Override
    public String id() {
        return FeatureIds.COALESCE;
    }

    @Override
    public java.util.function.Function<Record, Object> map(Function function) {
        List<Expression> parameters = ExpressionUtils.getFunctionParams(function);
        if (Objects.isNull(parameters) || parameters.isEmpty()) {
            throw new UnsupportedOperationException(String.format("function '%s' must have params", function));
        }

        //转换后的函数参数
        List<java.util.function.Function<Record, Object>> paramMappers = parameters
                .stream()
                .map(ValueMapFeature::createMapperOrThrow)
                .collect(Collectors.toList());

        return v -> {
            for (java.util.function.Function<Record, Object> mapper : paramMappers) {
                Object tmp = mapper.apply(v);
                if (tmp != null) {
                    return tmp;
                }
            }

            return null;
        };
    }
}
