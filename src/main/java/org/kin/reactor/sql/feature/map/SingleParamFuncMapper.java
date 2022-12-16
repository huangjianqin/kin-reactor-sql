package org.kin.reactor.sql.feature.map;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import org.apache.commons.collections.CollectionUtils;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 单参数函数
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class SingleParamFuncMapper implements ValueMapFeature<Function>{
    /** feature id */
    private final String id;
    /** 函数逻辑 */
    private final java.util.function.Function<Object, Object> func;

    public SingleParamFuncMapper(String id, java.util.function.Function<Object, Object> func) {
        this.id = FeatureIds.wrapMapperId(id);
        this.func = func;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public java.util.function.Function<Record, Object> map(Function function) {
        List<Expression> expressions;
        if (function.getParameters() == null || CollectionUtils.isEmpty(expressions = function.getParameters().getExpressions())) {
            throw new UnsupportedOperationException(String.format("function '%s' must have one param", function));
        }

        java.util.function.Function<Record, Object> mapper = ValueMapFeature.createMapperOrThrow(expressions.get(0));

        return v -> func.apply(mapper.apply(v));
    }
}
