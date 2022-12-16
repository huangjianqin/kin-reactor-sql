package org.kin.reactor.sql.utils;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

import java.util.Collections;
import java.util.List;

/**
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class ExpressionUtils {
    private ExpressionUtils() {
    }

    /**
     * 获取sql函数参数
     * @param function  sql函数
     * @return  sql函数参数表达式
     */
    public static List<Expression> getFunctionParams(Function function) {
        ExpressionList list = function.getParameters();
        List<Expression> expressions;
        if (list != null) {
            expressions = list.getExpressions();
        } else {
            expressions = Collections.emptyList();
        }
        return expressions;
    }
}
