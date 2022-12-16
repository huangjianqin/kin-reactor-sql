package org.kin.reactor.sql.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class JDKDateFormatter implements DateFormatter {
    /** 时间格式逻辑判断 */
    private final Predicate<String> predicate;
    /** 时间格式转换实现 */
    private final SimpleDateFormat formatter;

    public JDKDateFormatter(Predicate<String> predicate, SimpleDateFormat formatter) {
        this.predicate = predicate;
        this.formatter = formatter;
    }

    @Override
    public boolean support(String str) {
        return this.predicate.test(str);
    }

    @Override
    public Date format(String str) {
        try {
            return this.formatter.parse(str);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getPattern() {
        return this.formatter.toPattern();
    }
}
