package org.kin.reactor.sql.visitor;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import org.kin.reactor.sql.GlobalFeatures;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.Feature;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.feature.filter.FilterFeature;
import org.kin.reactor.sql.feature.map.ValueMapFeature;
import org.kin.reactor.sql.utils.CompareUtils;
import org.kin.reactor.sql.utils.ObjectUtils;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * 解析where
 *
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class FilterParser implements ExpressionVisitorAdapter {
    private BiFunction<Record, Object, Boolean> result;

    @Override
    public void visit(Function function) {
        //where gt(...,1)
        java.util.function.Function<Record, Object> mapper = ValueMapFeature.createMapper(function);
        if (Objects.nonNull(mapper)) {
            result = (r, c) -> ObjectUtils.castBoolean(mapper.apply(r));
        } else {
            FilterFeature<Function> filterFeature = GlobalFeatures.getFilterFeatureOrThrow(function.getName());
            result = filterFeature.predicate(function);
        }
    }

    @Override
    public void visit(AndExpression andExpression) {
        //and
        FilterFeature<AndExpression> filterFeature = GlobalFeatures.getFeatureOrThrow(FeatureIds.AND);
        result = filterFeature.predicate(andExpression);
    }

    @Override
    public void visit(OrExpression orExpression) {
        //or
        FilterFeature<OrExpression> filterFeature = GlobalFeatures.getFeatureOrThrow(FeatureIds.OR);
        result = filterFeature.predicate(orExpression);
    }

    @Override
    public void visit(Between between) {
        //between
        FilterFeature<Between> filterFeature = GlobalFeatures.getFeatureOrThrow(FeatureIds.BETWEEN);
        result = filterFeature.predicate(between);
    }

    @Override
    public void visit(InExpression inExpression) {
        //in
        FilterFeature<InExpression> filterFeature = GlobalFeatures.getFeatureOrThrow(FeatureIds.IN);
        result = filterFeature.predicate(inExpression);
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        // case when
        java.util.function.Function<Record, Object> mapper = ValueMapFeature.createMapperOrThrow(caseExpression);
        result = (r, c) -> ObjectUtils.castBoolean(mapper.apply(r));
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        //case val when 1.0 then
        result = (r, c) -> CompareUtils.equals(c, doubleValue.getValue());
    }

    @Override
    public void visit(LongValue longValue) {
        //case val when 1 then
        result = (r, c) -> CompareUtils.equals(c, longValue.getValue());
    }

    @Override
    public void visit(DateValue dateValue) {
        //case val when {d ''} then
        result = (r, c) -> CompareUtils.equals(c, dateValue.getValue());
    }

    @Override
    public void visit(TimeValue timeValue) {
        //case val when {t ''} then
        result = (r, c) -> CompareUtils.equals(c, timeValue.getValue());
    }

    @Override
    public void visit(StringValue stringValue) {
        //case val when '1' then
        result = (r, c) -> CompareUtils.equals(c, stringValue.getValue());
    }

    @Override
    public void visit(IsNullExpression expr) {
        //is null
        //not null
        boolean not = expr.isNot();
        java.util.function.Function<Record, Object> leftMapper = ValueMapFeature.createMapperOrThrow(expr.getLeftExpression());
        if (not) {
            result = (r, c) -> Objects.nonNull(leftMapper.apply(r));
        } else {
            result = (r, c) -> Objects.isNull(leftMapper.apply(r));
        }
    }

    @Override
    public void visit(IsBooleanExpression expr) {
        //not true
        //is true
        boolean not = expr.isNot();
        boolean isTrue = expr.isTrue();
        ValueMapFeature<Expression> feature = GlobalFeatures.getFeatureOrThrow(FeatureIds.PROPERTY);
        java.util.function.Function<Record, Object> leftMapper = feature.map(expr.getLeftExpression());
        result = (r, c) -> !not == isTrue == ObjectUtils.castBoolean(leftMapper.apply(r));
    }

    @Override
    public void visit(NullValue nullValue) {
        //case val when null then
        result = (r, c) -> c == null;
    }

    @Override
    public void visit(Column tableColumn) {
        //where ...
        ValueMapFeature<Column> feature = GlobalFeatures.getFeatureOrThrow(FeatureIds.PROPERTY);
        java.util.function.Function<Record, Object> mapper = feature.map(tableColumn);
        result = (r, c) -> ObjectUtils.castBoolean(mapper.apply(r));
    }

    @Override
    public void visit(NotExpression not) {
        //where not
        java.util.function.Function<Record, Object> mapper = ValueMapFeature.createMapperOrThrow(not);
        result = (r, c) -> !ObjectUtils.castBoolean(mapper.apply(r));
    }

    @Override
    public void visit(BinaryExpression expression) {
        // where a = ? and b = ?
        String exprStr = expression.getStringExpression();
        FilterFeature<Expression> filterFeature = GlobalFeatures.getFilterFeature(exprStr);
        if (Objects.nonNull(filterFeature)) {
            result = filterFeature.predicate(expression);
        } else {
            ValueMapFeature<Expression> valueMapFeature = GlobalFeatures.getValueMapFeature(exprStr);
            if (Objects.nonNull(valueMapFeature)) {
                java.util.function.Function<Record, Object> mapper = valueMapFeature.map(expression);
                result = (r, c) -> CompareUtils.equals(c, mapper.apply(r));
            }
        }
    }

    @Override
    public void visit(ComparisonOperator expression) {
        // = > < ...
        FilterFeature<Expression> filterFeature = GlobalFeatures.getFilterFeatureOrThrow(expression.getStringExpression());
        result = filterFeature.predicate(expression);
    }

    //getter
    public BiFunction<Record, Object, Boolean> getResult() {
        return result;
    }
}
