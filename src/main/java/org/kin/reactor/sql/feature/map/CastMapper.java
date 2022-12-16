package org.kin.reactor.sql.feature.map;

import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.Expression;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.utils.ObjectUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * cast(... as ..) ..
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class CastMapper implements ValueMapFeature<CastExpression>{
    @Override
    public String id() {
        return FeatureIds.CAST;
    }

    @Override
    public Function<Record, Object> map(CastExpression cast) {
        //类型
        String type = cast.getType().getDataType().toLowerCase();
        //要转换的实例
        Function<Record, Object> mapper = ValueMapFeature.createMapperOrThrow(cast.getLeftExpression());

        return r -> ObjectUtils.castValue(mapper.apply(r), type);
    }
}
