package org.kin.reactor.sql.feature;

import org.kin.reactor.sql.Record;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 复用部分function实例
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class Functions {
    private Functions() {
    }

    public static final Function<Record, Object> DO_NOTHING_FUNC = v -> v;
    public static final Function<Record, Object> RETURN_NULL_FUNC = v -> null;
    public static final Function<Record, Object> RETURN_NOW_MS_FUNC = v -> System.currentTimeMillis();
    public static final BiFunction<Record, Object, Boolean> ALWAYS_FALSE_BI_FUNC = (r, v) -> false;

}
