package org.kin.reactor.sql.feature.map;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.StringValue;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.feature.Functions;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * now(...)
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class NowMapper implements ValueMapFeature<Function>{
    @Override
    public String id() {
        return FeatureIds.NOW;
    }

    @Override
    public java.util.function.Function<Record, Object> map(Function now) {
        if (now.getParameters() != null) {
            //有参
            for (Expression expr : now.getParameters().getExpressions()) {
                if (expr instanceof StringValue) {
                    //单个参数
                    StringValue format = ((StringValue) expr);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format.getValue());
                    return v -> formatter.format(LocalDateTime.now());
                }
            }
        }
        //无参
        return Functions.RETURN_NOW_MS_FUNC;
    }
}
