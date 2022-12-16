package org.kin.reactor.sql.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author huangjianqin
 * @date 2022/12/15
 */
public final class CalculateUtils {
    private CalculateUtils() {
    }

    /**
     * 二进制操作, 与
     */
    public static long bitAnd(Number left, Number right) {
        return left.longValue() & right.longValue();
    }

    /**
     * 二进制操作, 或
     */
    public static long bitOr(Number left, Number right) {
        return left.longValue() | right.longValue();
    }

    /**
     * 二进制操作, 异或
     */
    public static long bitMutex(Number left, Number right) {
        return left.longValue() ^ right.longValue();
    }

    /**
     * 计算bit数
     */
    public static long bitCount(Number left) {
        return Long.bitCount(left.longValue());
    }

    /**
     * 二进制操作, 左移
     */
    public static long leftShift(Number left, Number right) {
        return left.longValue() << right.longValue();
    }

    /**
     * 二进制操作, 无符号右移
     */
    public static long unsignedRightShift(Number left, Number right) {
        return left.longValue() >>> right.longValue();
    }

    /**
     * 二进制操作, 右移
     */
    public static long rightShift(Number left, Number right) {
        return left.longValue() >> right.longValue();
    }

    /**
     * 二进制操作, 非
     */
    public static long bitNot(Number left) {
        return ~left.longValue();
    }

    /**
     * 取模
     */
    public static Number mod(Number left, Number right) {
        if (left instanceof Double
                || left instanceof Float) {
            return left.doubleValue() % right.doubleValue();
        }

        if (left instanceof BigDecimal && right instanceof BigDecimal) {
            return ((BigDecimal) left).remainder(((BigDecimal) right));
        }

        return left.longValue() % right.longValue();
    }

    /**
     * 除
     */
    public static Number division(Number left, Number right) {
        if (left instanceof Double
                || left instanceof Float) {
            return left.doubleValue() / right.doubleValue();
        }

        if (left instanceof BigDecimal && right instanceof BigDecimal) {
            return ((BigDecimal) left).divide(((BigDecimal) right), RoundingMode.HALF_UP);
        }

        return left.longValue() / right.longValue();
    }

    /**
     * 乘
     */
    public static Number multiply(Number left, Number right) {
        if (left instanceof Double
                || left instanceof Float) {
            return left.doubleValue() * right.doubleValue();
        }

        if (left instanceof BigDecimal && right instanceof BigDecimal) {
            return ((BigDecimal) left).multiply(((BigDecimal) right));
        }

        return left.longValue() * right.longValue();
    }

    /**
     * 加
     */
    public static Number add(Number left, Number right) {
        if (left instanceof Double
                || left instanceof Float) {
            return left.doubleValue() + right.doubleValue();
        }

        if (left instanceof BigDecimal && right instanceof BigDecimal) {
            return ((BigDecimal) left).add(((BigDecimal) right));
        }

        return left.longValue() + right.longValue();
    }

    /**
     * 减
     */
    public static Number subtract(Number left, Number right) {
        if (left instanceof Double
                || left instanceof Float) {
            return left.doubleValue() - right.doubleValue();
        }

        if (left instanceof BigDecimal && right instanceof BigDecimal) {
            return ((BigDecimal) left).subtract(((BigDecimal) right));
        }

        return left.longValue() - right.longValue();
    }
}
