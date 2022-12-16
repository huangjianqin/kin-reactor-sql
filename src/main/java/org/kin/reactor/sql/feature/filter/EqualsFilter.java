package org.kin.reactor.sql.feature.filter;

import org.kin.reactor.sql.utils.CompareUtils;

import java.util.Date;

/**
 * =  !=  <>
 * @author huangjianqin
 * @date 2022/12/15
 */
public final class EqualsFilter extends BinaryFilterFeature{
    /** 是否'非'('不')判断 */
    private final boolean not;

    public EqualsFilter(String id, boolean not) {
        super(id);
        this.not = not;
    }

    @Override
    protected boolean doPredicate(Number left, Number right) {
        return not != CompareUtils.equals(left, right);
    }

    @Override
    protected boolean doPredicate(Date left, Date right) {
        return not != CompareUtils.equals(left, right);
    }

    @Override
    protected boolean doPredicate(String left, String right) {
        return not != CompareUtils.equals(left, right);
    }

    @Override
    protected boolean doPredicate(Object left, Object right) {
        return not != CompareUtils.equals(left, right);
    }
}
