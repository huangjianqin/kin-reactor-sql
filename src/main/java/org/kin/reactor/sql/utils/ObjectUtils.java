package org.kin.reactor.sql.utils;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

/**
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class ObjectUtils {
    private ObjectUtils() {
    }

    /**
     * {@code obj}类型转换
     * @param obj   实例
     * @param type  类型
     * @return  类型转换后的实例
     */
    public static Object castValue(Object obj, String type) {

        switch (type) {
            case "string":
            case "varchar":
                return castString(obj);
            case "number":
            case "decimal":
                return new BigDecimal(castString(obj));
            case "int":
            case "integer":
                return castNumber(obj).intValue();
            case "long":
                return castNumber(obj).longValue();
            case "double":
                return castNumber(obj).doubleValue();
            case "bool":
            case "boolean":
                return castBoolean(obj);
            case "byte":
                return castNumber(obj).byteValue();
            case "float":
                return castNumber(obj).floatValue();
            case "date":
                return castDate(obj);
            default:
                return obj;
        }
    }

    /**
     * {@code obj}转换成数字类型
     * @param obj 实例
     * @return 数字实例
     */
    public static Number castNumber(Object obj) {
        if (obj instanceof CharSequence) {
            String stringValue = String.valueOf(obj);
            if (stringValue.startsWith("0x")) {
                return Long.parseLong(stringValue.substring(2), 16);
            }
            try {
                BigDecimal decimal = new BigDecimal(stringValue);
                if (decimal.scale() == 0) {
                    return decimal.longValue();
                }
                return decimal.doubleValue();
            } catch (NumberFormatException ignore) {

            }
        }
        if (obj instanceof Character) {
            return (int) (Character) obj;
        }
        if (obj instanceof Boolean) {
            return ((Boolean) obj) ? 1 : 0;
        }
        if (obj instanceof Number) {
            return ((Number) obj);
        }
        if (obj instanceof Date) {
            return ((Date) obj).getTime();
        }

        throw new UnsupportedOperationException("can not cast to number:" + obj);
    }

    /**
     * {@code obj}转换成字符串类型
     * @param obj 实例
     * @return 字符串实例
     */
    public static String castString(Object obj) {
        if (obj instanceof byte[]) {
            return new String((byte[]) obj);
        }
        if (obj instanceof char[]) {
            return new String((char[]) obj);
        }
        return String.valueOf(obj);
    }

    /**
     * {@code obj}转换成boolean类型
     * @param obj 实例
     * @return boolean实例
     */
    public static boolean castBoolean(Object obj) {
        if(obj instanceof Boolean){
            return ((Boolean) obj);
        }
        String strVal = String.valueOf(obj);

        return "true".equalsIgnoreCase(strVal) ||
                "y".equalsIgnoreCase(strVal) ||
                "ok".equalsIgnoreCase(strVal) ||
                "yes".equalsIgnoreCase(strVal) ||
                "1".equalsIgnoreCase(strVal);
    }

    /**
     * {@code obj}转换成date类型
     * @param obj 实例
     * @return date实例
     */
    public static Date castDate(Object obj) {
        if (obj instanceof String) {
            if (StringUtils.isNumber(obj)) {
                obj = Long.parseLong(String.valueOf(obj));
            } else {
                String maybeTimeValue = String.valueOf(obj);
                LocalDateTime time = LocalDateTime.now();
                //在时间中包含以下字符表示使用当前时间
                if (maybeTimeValue.contains("yyyy")) {
                    maybeTimeValue = maybeTimeValue.replace("yyyy", String.valueOf(time.getYear()));
                }
                if (maybeTimeValue.contains("MM")) {
                    maybeTimeValue = maybeTimeValue.replace("MM", String.valueOf(time.getMonthValue()));
                }
                if (maybeTimeValue.contains("dd")) {
                    maybeTimeValue = maybeTimeValue.replace("dd", String.valueOf(time.getDayOfMonth()));
                }
                if (maybeTimeValue.contains("hh")) {
                    maybeTimeValue = maybeTimeValue.replace("hh", String.valueOf(time.getHour()));
                }
                if (maybeTimeValue.contains("mm")) {
                    maybeTimeValue = maybeTimeValue.replace("mm", String.valueOf(time.getMinute()));
                }
                if (maybeTimeValue.contains("ss")) {
                    maybeTimeValue = maybeTimeValue.replace("ss", String.valueOf(time.getSecond()));
                }
                Date date = DateFormatter.fromString(maybeTimeValue);
                if (null != date) {
                    return date;
                }
            }
        }
        if (obj instanceof Number) {
            return new Date(((Number) obj).longValue());
        }
        if (obj instanceof Instant) {
            obj = Date.from(((Instant) obj));
        }

        if (obj instanceof LocalDateTime) {
            obj = Date.from(((LocalDateTime) obj).atZone(ZoneId.systemDefault()).toInstant());
        }
        if (obj instanceof LocalDate) {
            obj = Date.from(((LocalDate) obj).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        if (obj instanceof ZonedDateTime) {
            obj = Date.from(((ZonedDateTime) obj).toInstant());
        }
        if (obj instanceof Date) {
            return ((Date) obj);
        }
        throw new UnsupportedOperationException("can not cast to date:" + obj);
    }

    /**
     * {@code obj}转换成list
     * @param obj   实例
     * @return  list
     */
    public static List<Object> castList(Object obj) {
        if (obj instanceof Collection) {
            return new ArrayList<>(((Collection<?>) obj));
        }
        if (obj instanceof Object[]) {
            return Arrays.asList(((Object[]) obj));
        }
        return Collections.singletonList(obj);
    }
}
