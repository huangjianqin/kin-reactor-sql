package org.kin.reactor.sql.feature.map;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.StringValue;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.utils.ObjectUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * date_format(...,'yyyy-MM-dd')
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class DateFormatMapper implements ValueMapFeature<Function>{
    @Override
    public String id() {
        return FeatureIds.DATE_FORMAT;
    }

    @Override
    public java.util.function.Function<Record, Object> map(Function expression) {
        try {
            //函数参数
            List<Expression> paramExprs = expression.getParameters().getExpressions();

            if (paramExprs.size() < 2) {
                throw new UnsupportedOperationException("date_format function must need at least two parameter");
            }

            //值
            Expression val = paramExprs.get(0);
            //时间格式
            Expression formatExpr = paramExprs.get(1);
            //第三个参数时区
            ZoneId tz = paramExprs.size() > 2 ? ZoneId.of(((StringValue) paramExprs.get(2)).getValue()) : ZoneId.systemDefault();

            if (formatExpr instanceof StringValue) {
                java.util.function.Function<Record, Object> mapper = ValueMapFeature.createMapperOrThrow(val);
                StringValue strFormat = ((StringValue) formatExpr);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(strFormat.getValue());
                return r -> formatter.format(ObjectUtils.castDate(mapper.apply(r)).toInstant().atZone(tz));
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("date_format function execute error", e);
        }

        throw new UnsupportedOperationException(String.format("expression '%s' can not be date_format function", expression));
    }
}
