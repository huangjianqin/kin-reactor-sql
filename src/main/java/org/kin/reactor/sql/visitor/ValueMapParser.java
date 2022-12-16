package org.kin.reactor.sql.visitor;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.schema.Column;
import org.kin.reactor.sql.GlobalFeatures;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.Feature;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.feature.filter.FilterFeature;
import org.kin.reactor.sql.feature.map.ValueMapFeature;
import org.kin.reactor.sql.utils.ObjectUtils;
import org.kin.reactor.sql.utils.PropertyUtils;
import org.reactivestreams.Publisher;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * 解析值转换
 *
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class ValueMapParser implements ExpressionVisitorAdapter {
    private java.util.function.Function<Record, Object> result;

    @Override
    public void visit(Function function) {
        // select if(val < 1,true,false)
        ValueMapFeature<Function> feature = GlobalFeatures.getValueMapFeatureOrThrow(function.getName());
        result = feature.map(function);
    }

    @Override
    public void visit(ArrayExpression arrayExpr) {
        //select arr[0]
        Expression indexExpr = arrayExpr.getIndexExpression();
        Expression objExpr = arrayExpr.getObjExpression();
        //arr
        java.util.function.Function<Record, Object> objMapper = ValueMapFeature.createMapperOrThrow(objExpr);
        //[...], 可以是下标, 也可以是属性名
        java.util.function.Function<Record, Object> indexMapper = ValueMapFeature.createMapperOrThrow(indexExpr);

        result = r -> PropertyUtils.getProperty(indexMapper.apply(r), objMapper.apply(r));
    }

    @Override
    public void visit(CaseExpression caseExpr) {
        //select case when ... then
        ValueMapFeature<CaseExpression> feature = GlobalFeatures.getFeatureOrThrow(FeatureIds.CASE_WHEN);
        result = feature.map(caseExpr);
    }

    @Override
    public void visit(CastExpression castExpr) {
        // select cast(... as long)
        ValueMapFeature<CastExpression> feature = GlobalFeatures.getFeatureOrThrow(FeatureIds.CAST);
        result = feature.map(castExpr);
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        //select ?1
        //从1开始
        int idx = jdbcParameter.isUseFixedIndex() ? jdbcParameter.getIndex() : jdbcParameter.getIndex() - 1;
        result = r -> r.getContext().getParam(idx);
    }

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {
        //select :name
        String name = jdbcNamedParameter.getName();
        result = r -> r.getContext().getParam(name);
    }

    @Override
    public void visit(LongValue longValue) {
        //select 1
        result = r -> longValue.getValue();
    }

    @Override
    public void visit(StringValue stringValue) {
        //select '1'
        result = r -> stringValue.getValue();
    }

    @Override
    public void visit(Column tableColumn) {
        // select ...
        ValueMapFeature<Column> feature = GlobalFeatures.getFeatureOrThrow(FeatureIds.PROPERTY);
        result = feature.map(tableColumn);
    }

    @Override
    public void visit(NumericBind bind) {
        // select :1
        int idx = bind.getBindId();
        result = r -> r.getContext().getParam(idx);

    }

    @Override
    public void visit(SignedExpression signedExpr) {
        // select -value,~value
        char sign = signedExpr.getSign();
        //值表达式逻辑
        java.util.function.Function<Record, Object> valMapper = ValueMapFeature.createMapperOrThrow(signedExpr.getExpression());
        //操作符逻辑
        java.util.function.Function<Number, Number> signMapper;
        switch (sign) {
            case '-':
                signMapper = n -> {
                    Number number = ObjectUtils.castNumber(n);
                    if (number instanceof Integer) {
                        return -(Integer) number;
                    }
                    if (number instanceof Long) {
                        return -(Long) number;
                    }
                    if (number instanceof Double) {
                        return -(Double) number;
                    }
                    if (number instanceof Float) {
                        return -(Float) number;
                    }
                    return -number.doubleValue();
                };
                break;
            case '~':
                signMapper = n -> ~n.longValue();
                break;
            default:
                signMapper = java.util.function.Function.identity();
        }

        result = r -> signMapper.apply(ObjectUtils.castNumber(valMapper.apply(r)));
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        //select 1.0
        result = r -> doubleValue.getValue();
    }

    @Override
    public void visit(HexValue hexValue) {
        //select 0x01
        result = r -> hexValue.getValue();
    }

    @Override
    public void visit(DateValue dateValue) {
        //select {d 'yyyy-mm-dd'}
        result = r -> dateValue.getValue();
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        //select {ts 'yyyy-mm-dd hh:mm:ss.f . . .'}
        result = r -> timestampValue.getValue();
    }

    @Override
    public void visit(BinaryExpression expression) {
        //select a+b
        //运算符号
        ValueMapFeature<BinaryExpression> feature = GlobalFeatures.getValueMapFeature(expression.getStringExpression());
        if (Objects.nonNull(feature)) {
            result = feature.map(expression);
        }
        if (result == null) {
            //条件判断符号
            BiFunction<Record, Object, Boolean> filterOrThrow = FilterFeature.createFilter(expression);
            if (Objects.nonNull(filterOrThrow)) {
                result = r -> filterOrThrow.apply(r, r.getRecord());
            }
        }
    }

    public java.util.function.Function<Record, Object> getResult() {
        return result;
    }
}
