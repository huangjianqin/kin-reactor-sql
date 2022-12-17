package org.kin.reactor.sql;

import reactor.util.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * sql执行过程中每行数据, 可能会聚合多个表的数据
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class Record {
    /** 当datasource纯基础类型时, 使用$this可以引用该值 */
    public final static String THIS = "this";
    /** 原始数据 */
    private final Object raw;
    private final Context context;
    /** key -> table name或者alias, value -> 普通实例或者多层Map<String, Object> */
    private final Map<String, Object> records = new HashMap<>(8);
    /** key -> 最终column name 或者alias, value -> 普通实例或者多层Map<String, Object> */
    private final Map<String, Object> results = new HashMap<>(8);

    public Record(Object raw, Object record, Context context) {
        this.raw = raw;
        if (record != null) {
            records.put(THIS, record);
        }
        this.context = context;
    }

    /**
     * 获取表数据或者指定column名的数据(函数, 或者常量)
     * @param name  table name或者alias
     * @return  普通实例或者多层Map<String, Object>
     */
    @Nullable
    public Object getRecord(String name) {
        return this.records.get(name);
    }

    /**
     * 获取this表数据
     * @return  获取this表数据
     */
    public Object getRecord() {
        return this.records.get(THIS);
    }

    /**
     * 添加数据
     * @param name  表名或者新增的column名
     * @param record    值
     * @return  this
     */
    public Record addRecord(String name, Object record) {
        if (name == null || record == null) {
            return this;
        }
        records.put(name, record);
        return this;
    }

    /**
     * 将this表数据全部put进result map
     * @return  this
     */
    @SuppressWarnings("unchecked")
    public Record saveResult() {
        Object record = getRecord();
        if (record instanceof Map) {
            ((Map<String, Object>)record).forEach(this::saveResult);
            return this;
        } else {
            return saveResult(THIS, record);
        }
    }

    /**
     * 将{@code name}的column值put进result map
     * @param name column名
     * @param value 值
     * @return  this
     */
    public Record saveResult(String name, Object value) {
        if (name == null || value == null) {
            return this;
        }
        if (name.equals(THIS) || value instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (null != entry.getKey() && null != entry.getValue()) {
                    results.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
        } else {
            results.put(name, value);
        }
        return this;
    }

    public Result toResult(){
        return new Result(raw, results);
    }

    //getter
    public Context getContext() {
        return context;
    }

    public Map<String, Object> getResults() {
        return results;
    }
}
