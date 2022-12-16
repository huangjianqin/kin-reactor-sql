package org.kin.reactor.sql.utils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * 默认时间转换实现
 *
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class DefaultDateFormatter implements DateFormatter {
    /** 时间格式转换实现 */
    private final DateTimeFormatter formatter;
    /** 时间格式正则表达式 */
    private final Pattern regex;
    /** 时间格式 */
    private final String formatterString;

    public DefaultDateFormatter(Pattern regex, String formatter) {
        this.regex = regex;
        this.formatter = DateTimeFormat.forPattern(formatter);
        this.formatterString = formatter;
    }

    @Override
    public boolean support(String str) {
        return this.regex.matcher(str).matches();
    }

    @Override
    public Date format(String str) {
        return this.formatter.parseDateTime(str).toDate();
    }

    @Override
    public String getPattern() {
        return this.formatterString;
    }
}

