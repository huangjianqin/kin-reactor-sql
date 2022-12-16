package org.kin.reactor.sql.feature.map;

import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.WhenClause;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.feature.Functions;
import org.kin.reactor.sql.feature.filter.FilterFeature;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * case val
 * when ... then ...
 * when ... then ...
 * else ... end
 *
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class CaseWhenMapper implements ValueMapFeature<CaseExpression> {
    @Override
    public String id() {
        return FeatureIds.CASE_WHEN;
    }

    @Override
    public Function<Record, Object> map(CaseExpression caseExpression) {
        Expression switchExpr = caseExpression.getSwitchExpression();

        Function<Record, Object> valueMapper =
                switchExpr == null
                        //case when
                        ? Functions.DO_NOTHING_FUNC
                        // case column when
                        : ValueMapFeature.createMapperOrThrow(switchExpr);

        //多重case
        Map<BiFunction<Record, Object, Boolean>, Function<Record, Object>> cases = new LinkedHashMap<>();
        //解析when
        for (WhenClause whenClause : caseExpression.getWhenClauses()) {
            Expression when = whenClause.getWhenExpression();
            Expression then = whenClause.getThenExpression();
            cases.put(when(when), then(then));
        }
        Function<Record, Object> thenElse = then(caseExpression.getElseExpression());

        return r -> {
            Object switchValue = valueMapper.apply(r);
            for (Map.Entry<BiFunction<Record, Object, Boolean>, Function<Record, Object>> entry : cases.entrySet()) {
                if (entry.getKey().apply(r, switchValue)) {
                    return entry.getValue().apply(r);
                }
            }
            return thenElse.apply(r);
        };
    }

    /**
     * 解析then表达式
     *
     * @param expression then表达式
     * @return then逻辑func
     */
    private Function<Record, Object> then(Expression expression) {
        if (expression == null) {
            return Functions.DO_NOTHING_FUNC;
        }
        return ValueMapFeature.createMapperOrThrow(expression);
    }

    /**
     * 解析when表达式
     *
     * @param expression when表达式
     * @return 条件判断func
     */
    private BiFunction<Record, Object, Boolean> when(Expression expression) {
        if (expression == null) {
            return Functions.ALWAYS_FALSE_BI_FUNC;
        }
        return FilterFeature.createFilterOrThrow(expression);
    }

}
