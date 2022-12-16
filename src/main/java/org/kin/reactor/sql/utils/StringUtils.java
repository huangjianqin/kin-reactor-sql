package org.kin.reactor.sql.utils;

import java.util.regex.Pattern;

/**
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class StringUtils {
    /** 匹配int的正则表达式 */
    private static final Pattern INT_STR_REGEX = Pattern.compile("[-+]?\\d+");
    /** 匹配double的正则表达式 */
    private static final Pattern DOUBLE_STR_REGEX = Pattern.compile("[-+]?\\d+\\.\\d+");

    private StringUtils() {
    }

    /**
     * 去掉首尾的"
     * @param s 字符串
     * @return 去掉首尾的"后的字符串
     */
    public static String cleanDoubleQuotation(String s){
        if (s == null) {
            return null;
        }
        boolean startWith = s.charAt(0) == '\"';
        boolean endWith = s.charAt(s.length() - 1) == '\"';

        if (!startWith && !endWith) {
            return s;
        }

        if (startWith && endWith) {
            return s.substring(1, s.length() - 1);
        }
        if (startWith) {
            return s.substring(1);
        }

        return s.substring(0, s.length() - 1);
    }

    /**
     * 判断实例{@link #toString()}是否为空串
     * @param obj   实例
     * @return  {@link #toString()}是否为空串
     */
    public static boolean isBlank(Object obj) {
        return obj == null || "".equals(obj.toString());
    }

    /**
     * 判断对象是否是数字类型
     * @param obj   实例
     * @return  是否是数字类型
     */
    public static boolean isNumber(Object obj) {
        if (obj instanceof Number) {
            return true;
        } else {
            return isInt(obj) || isDouble(obj);
        }
    }

    /**
     * 判断对象是否是整形
     *  @param obj   实例
     *  @return  是否是整形
     */
    public static boolean isInt(Object obj) {
        if (isBlank(obj)) {
            return false;
        } else {
            return obj instanceof Integer ||
                    INT_STR_REGEX.matcher(obj.toString()).matches();
        }
    }

    /**
     * 判断对象是否是浮点数
     *  @param obj   实例
     *  @return  是否是浮点数
     */
    public static boolean isDouble(Object obj) {
        if (isBlank(obj)) {
            return false;
        } else {
            return obj instanceof Double ||
                    obj instanceof Float ||
                    DOUBLE_STR_REGEX.matcher(obj.toString()).matches();
        }
    }
}
