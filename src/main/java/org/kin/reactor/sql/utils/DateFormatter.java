package org.kin.reactor.sql.utils;

import reactor.util.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 时间转换接口
 *
 * @author huangjianqin
 * @date 2022/12/14
 */
public interface DateFormatter {
    List<DateFormatter> SUPPORT_FORMATTER = Arrays.asList(
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}[0-9]{2}[0-9]{2}"), "yyyyMMdd"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}"), "yyyy-MM-dd"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}/[0-9]{2}/[0-9]{2}"), "yyyy/MM/dd"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}年[0-9]{2}月[0-9]{2}日"), "yyyy年MM月dd日"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}年[0-9]月[0-9]日"), "yyyy年M月d日"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}年[0-9]{2}月[0-9]日"), "yyyy年MM月d日"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}年[0-9]月[0-9]{2}日"), "yyyy年M月dd日"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]-[0-9]"), "yyyy-M-d"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]"), "yyyy-MM-d"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]-[0-9]{2}"), "yyyy-M-dd"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}/[0-9]/[0-9]"), "yyyy/M/d"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}/[0-9]{2}/[0-9]"), "yyyy/MM/d"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}/[0-9]/[0-9]{2}"), "yyyy/M/dd"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"), "yyyy-MM-dd HH:mm:ss"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]-[0-9] [0-9]{2}:[0-9]{2}:[0-9]{2}"), "yyyy-M-d HH:mm:ss"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]-[0-9] [0-9]:[0-9]:[0-9]"), "yyyy-M-d H:m:s"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9] [0-9]{2}:[0-9]{2}:[0-9]{2}"), "yyyy-MM-d HH:mm:ss"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"), "yyyy-M-dd HH:mm:ss"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]-[0-9]{2} [0-9]:[0-9]:[0-9]"), "yyyy-M-dd H:m:s"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9] [0-9]:[0-9]:[0-9]"), "yyyy-MM-d H:m:s"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"), "yyyy/MM/dd HH:mm:ss"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}/[0-9]/[0-9] [0-9]{2}:[0-9]{2}:[0-9]{2}"), "yyyy/M/d HH:mm:ss"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}/[0-9]/[0-9] [0-9]:[0-9]:[0-9]"), "yyyy/M/d H:m:s"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}/[0-9]{2}/[0-9] [0-9]{2}:[0-9]{2}:[0-9]{2}"), "yyyy/MM/d HH:mm:ss"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}/[0-9]/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"), "yyyy/M/dd HH:mm:ss"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}/[0-9]/[0-9]{2} [0-9]:[0-9]:[0-9]"), "yyyy/M/dd H:m:s"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}/[0-9]{2}/[0-9] [0-9]:[0-9]:[0-9]"), "yyyy/MM/d H:m:s"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\\+[0-9]{4}"), "yyyy-MM-dd HH:mm:ssZ"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}"), "yyyy-MM-dd'T'HH:mm:ss"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\+[0-9]{4}"), "yyyy-MM-dd'T'HH:mm:ssZ"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}年[0-9]{2}月[0-9]{2}日[0-9]{2}时[0-9]{2}分[0-9]{2}秒"), "yyyy年MM月dd日HH时mm分ss秒"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}年[0-9]{2}月[0-9]{2}日 [0-9]{2}时[0-9]{2}分[0-9]{2}秒"), "yyyy年MM月dd日 HH时mm分ss秒"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2}"), "HH:mm:ss"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}[0-9]{2}[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"), "yyyyMMdd HH:mm:ss"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}"), "yyyyMMddHHmmss"),
            new DefaultDateFormatter(Pattern.compile("[0-9]{4}[0-9]{2}[0-9]{2} [0-9]{2}[0-9]{2}[0-9]{2}"), "yyyyMMdd HHmmss"),
            new JDKDateFormatter((str) -> str.contains("年") && str.contains("CST") && str.split("[ ]").length == 6, new SimpleDateFormat("yyyy年 MM月 dd日 EEE HH:mm:ss 'CST'", Locale.CHINESE)),
            new JDKDateFormatter((str) -> str.contains("年") && str.contains("GMT") && str.split("[ ]").length == 6, new SimpleDateFormat("yyyy年 MM月 dd日 EEE HH:mm:ss 'GMT'", Locale.CHINESE)),
            new JDKDateFormatter((str) -> str.contains("CST") && str.split("[ ]").length == 6, new SimpleDateFormat("EEE MMM dd HH:mm:ss 'CST' yyyy", Locale.US)),
            new JDKDateFormatter((str) -> str.contains("GMT") && str.split("[ ]").length == 6, new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.US)),
            new JDKDateFormatter((str) -> str.endsWith("AM") || str.endsWith("PM") && str.split("[ ]").length == 5, new SimpleDateFormat("MMM d, yyyy K:m:s a", Locale.ENGLISH)));

    /**
     * 将时间字符串转换成{@link  Date}实例
     *
     * @param dateString 时间字符串
     * @return {@link  Date}实例
     */
    static Date fromString(String dateString) {
        DateFormatter formatter = getFormatter(dateString);
        return formatter != null ? formatter.format(dateString) : null;
    }

    /**
     * 根据时间字符串匹配合适的时间转换实现
     *
     * @param dateString 时间字符串
     * @return 时间转换实现
     */
    @Nullable
    static DateFormatter getFormatter(String dateString) {
        if (dateString != null && dateString.length() >= 4) {
            Iterator<DateFormatter> iterator = SUPPORT_FORMATTER.iterator();

            DateFormatter formatter;
            do {
                if (!iterator.hasNext()) {
                    return null;
                }

                formatter = iterator.next();
            } while (!formatter.support(dateString));

            return formatter;
        } else {
            return null;
        }
    }

    /**
     * 是否支持解析该时间字符串
     *
     * @param str 时间字符串
     * @return 是否支持解析该时间字符串
     */
    boolean support(String str);

    /**
     * 格式化成{@link  Date}实例
     *
     * @param str 时间字符串
     * @return {@link  Date}实例
     */
    Date format(String str);

    /**
     * 时间格式
     *
     * @return 时间格式
     */
    String getPattern();
}
