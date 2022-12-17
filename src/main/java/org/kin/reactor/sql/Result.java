package org.kin.reactor.sql;

import reactor.util.annotation.Nullable;

import java.util.Map;

/**
 * sql执行完后每行数据
 * @author huangjianqin
 * @date 2022/12/12
 */
public final class Result {
    /** 原始数据 */
    private final Object raw;
    /** key -> column name, value -> value */
    private final Map<String, Object> columns;

    public Result(Object raw, Map<String, Object> columns) {
        this.raw = raw;
        this.columns = columns;
    }

    /**
     * 获取指定column对应的值
     * @return  column name
     * @param <T>   column value class
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T get(String column){
        return (T) columns.get(column);
    }

    /**
     * 返回原始数据
     * @return  原始数据
     * @param <T>   原始数据类型
     */
    @SuppressWarnings("unchecked")
    public <T> T getRaw() {
        return (T) raw;
    }

    /**
     * 返回所有列数据
     * @return  所有列数据
     */
    public Map<String, Object> all() {
        return columns;
    }

    @Override
    public String toString() {
        return "Result{" +
                "raw=" + raw +
                ", columns=" + columns +
                '}';
    }
}
