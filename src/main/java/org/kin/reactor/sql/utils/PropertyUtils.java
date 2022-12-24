package org.kin.reactor.sql.utils;

import org.kin.reactor.sql.Record;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class PropertyUtils {
    private PropertyUtils() {
    }
    /** property 分割符regex */
    public static final Pattern SPLIT_PATTERN = Pattern.compile("[.]");
    /** property cast regex  */
    public static final Pattern CAST_PATTERN = Pattern.compile("::");

    /**
     * 以.分割
     */
    public static String[] splitDot(String str, int limit) {
        return SPLIT_PATTERN.split(str, limit);
    }

    /**
     * 以::分割
     */
    public static String[] splitCast(String str) {
        return CAST_PATTERN.split(str);
    }

    /**
     * 获取属性值
     * @param property  属性名 或者 数组下标
     * @param source    属性来源, 可以是对象, 也可以是数组
     * @return  属性值
     */
    public static Object getProperty(Object property, Object source) {
        if (source == null) {
            return null;
        }
        if (property instanceof String) {
            //去掉首尾""
            property = StringUtils.trimDeclaration((String) property);
        }

        //当前值
        if (Record.THIS.equals(property) || "$".equals(property) || "*".equals(property)) {
            return source;
        }

        //数字, 可能是获取数组中的值
        if (property instanceof Number) {
            int index = ((Number) property).intValue();
            return ObjectUtils.castList(source).get(index);
        }

        Function<Object, Object> mapper = Function.identity();
        //property转换成字符串
        String propName = String.valueOf(property);

        //类型转换,类似PostgreSQL的写法,name::string
        if (propName.contains("::")) {
            String[] cast = splitCast(propName);
            propName = cast[0];
            mapper = v -> ObjectUtils.castValue(v, cast[1]);
        }

        //尝试先获取一次值，大部分是这种情况, 避免不必要的判断
        Object direct = doGetProperty0(propName, source);
        if (direct != null) {
            return mapper.apply(direct);
        }

        //值为null, 可能是其他情况
        Object tmp = source;
        // a.b.c 的情况
        String[] props = splitDot(propName, 2);
        if (props.length <= 1) {
            return null;
        }

        //获取属性a或b是属于优化, 一般不会定义a.b.c.d.e.f.g这么长
        while (props.length > 1) {
            //尝试a
            tmp = doGetProperty0(props[0], tmp);
            if (tmp == null) {
                return null;
            }
            //尝试b
            Object fast = doGetProperty0(props[1], tmp);
            if (fast != null) {
                return mapper.apply(fast);
            }

            //尝试c.d.e
            if (props[1].contains(".")) {
                props = splitDot(props[1], 2);
            } else {
                return null;
            }
        }

        return mapper.apply(tmp);
    }

    /**
     * 从{@code value}获取指定属性的属性值
     * @param propName  属性名
     * @param value 来源
     * @return  属性值
     */
    private static Object doGetProperty0(String propName, Object value) {
        if (Record.THIS.equals(propName) || "$".equals(propName)) {
            return value;
        }
        if (value instanceof Map) {
            return ((Map<?, ?>) value).get(propName);
        }
        return doGetProperty1(propName, value);
    }

    /**
     * 从{@code value}获取指定属性的属性值
     * @param propName  属性名
     * @param value 来源
     * @return  属性值
     */
    private static Object doGetProperty1(String propName, Object value) {
        try {
            return org.apache.commons.beanutils.PropertyUtils.getProperty(value, propName);
        } catch (Exception e) {
            return null;
        }
    }
}
