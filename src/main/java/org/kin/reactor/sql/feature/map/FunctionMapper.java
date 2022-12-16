package org.kin.reactor.sql.feature.map;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import org.kin.reactor.sql.Record;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 多参函数处理
 *
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class FunctionMapper implements ValueMapFeature<Function> {
    /** feature id */
    private final String id;
    private final int minParamNum;
    private final int maxParamNum;
    private final java.util.function.Function<List<Object>, Object> mapper;

    public FunctionMapper(String id, int minParamNum, int maxParamNum,
                          java.util.function.Function<List<Object>, Object> mapper) {
        this.id = id;
        this.minParamNum = minParamNum;
        this.maxParamNum = maxParamNum;
        this.mapper = mapper;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public java.util.function.Function<Record, Object> map(Function function) {
        //参数数量校验
        List<Expression> params;
        if (function.getParameters() == null && minParamNum != 0) {
            throw new UnsupportedOperationException(String.format("function '%s' must have at least %d params", function, minParamNum));
        }
        if (function.getParameters() == null) {
            return v -> mapper.apply(Collections.emptyList());
        }
        params = function.getParameters().getExpressions();
        int paramNum = params.size();
        if (paramNum > maxParamNum || paramNum < minParamNum) {
            throw new UnsupportedOperationException(String.format("function '%s' must have %d to %d params, but actually %d",
                    function, minParamNum, maxParamNum, paramNum));
        }

        //参数映射
        List<java.util.function.Function<Record, Object>> paramMappers = params.stream()
                .map(ValueMapFeature::createMapperOrThrow)
                .collect(Collectors.toList());

        return v -> mapper.apply(paramMappers.stream().map(func -> func.apply(v)).collect(Collectors.toList()));
    }
}
