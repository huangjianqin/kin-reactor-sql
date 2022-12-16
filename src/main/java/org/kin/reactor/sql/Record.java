package org.kin.reactor.sql;

import reactor.util.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * sql执行过程中每行数据
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class Record {
    /** 当datasource纯基础类型时, 使用$this可以引用该值 */
    public final static String THIS = "this";
    private final Context context;
    /** key -> table name或者alias, value -> 普通实例或者多层Map<String, Object> */
    private final Map<String, Object> records = new HashMap<>(8);
    private final Map<String, Object> results = new HashMap<>(8);
    private String name;

    public static Record newRecord(String name, Object row, Context context) {
        if (row instanceof Record) {
            Record record = ((Record) row);
            if (null != name) {
                record.setName(name);
                record.addRecord(name, record.getRecord());
            }
            return record;
        }
        return new Record(name, row, context);
    }

    private Record(String name, Object record, Context context) {
        if (name != null) {
            records.put(name, record);
        }
        if (record != null) {
            records.put(THIS, record);
        }
        this.context = context;
    }

    /**
     * 获取select记录
     * @param name  table name或者alias
     * @return  普通实例或者多层Map<String, Object>
     */
    @Nullable
    public Object getRecord(String name) {
        return this.records.get(name);
    }

    public Object getRecord() {
        return this.records.get(THIS);
    }

    public Map<String, Object> asMap() {
        return results;
    }

    public Record addRecord(String name, Object record) {
        if (name == null || record == null) {
            return this;
        }
        records.put(name, record);
        return this;
    }

    @SuppressWarnings("unchecked")
    public Record putRecordToResult() {
        Object record = getRecord();
        if (record instanceof Map) {
            return setResults((Map<String, Object>) record);
        } else {
            return setResult(THIS, record);
        }
    }

    public Record setResult(String name, Object value) {
        if (name == null || value == null) {
            return this;
        }
        if (name.equals(THIS) && value instanceof Map) {
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

    public Record setResults(Map<String, Object> values) {
        values.forEach(this::setResult);
        return this;
    }

    public Result toResult(){
        return new Result(results);
    }

    //getter
    public Context getContext() {
        return context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
