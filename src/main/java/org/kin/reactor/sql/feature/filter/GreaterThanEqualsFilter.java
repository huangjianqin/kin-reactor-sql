package org.kin.reactor.sql.feature.filter;

import java.util.Date;

/**
 * >=
 * @author huangjianqin
 * @date 2022/12/15
 */
public final class GreaterThanEqualsFilter extends BinaryFilterFeature{
    public GreaterThanEqualsFilter() {
        super(">=");
    }

    @Override
    protected boolean doPredicate(Number left, Number right) {
        return left.doubleValue() >= right.doubleValue();
    }

    @Override
    protected boolean doPredicate(Date left, Date right) {
        return left.getTime() >= right.getTime();
    }

    @Override
    protected boolean doPredicate(String left, String right) {
        return left.compareTo(right) >= 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean doPredicate(Object left, Object right) {
        if (left instanceof Comparable) {
            return ((Comparable<Object>) left).compareTo(right) >= 0;
        }
        return false;
    }
}
