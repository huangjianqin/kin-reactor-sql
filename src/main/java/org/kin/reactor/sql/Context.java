package org.kin.reactor.sql;

import reactor.core.publisher.Flux;

import java.util.*;

/**
 * sql应用于数据流的上下文, 用于保留数据流以及自定义参数
 *
 * @author huangjianqin
 * @date 2022/12/12
 */
public final class Context{
    /** key -> datasource name, value -> datasource flux */
    private final Map<String, Flux<Object>> dataSources = new HashMap<>(4);
    /** 参数定义, 比如:1, :2 */
    private List<Object> params = new ArrayList<>(4);
    /** 命名参数定义 */
    private final Map<String, Object> namedParams = new HashMap<>(4);

    /**
     * 绑定datasource
     * @param name  datasource名字
     * @param datasource    datasource flux
     */
    public void with(String name, Flux<Object> datasource){
        dataSources.put(name, datasource);
    }

    /**
     * 绑定sql下标参数, 下标自增
     *
     * @param param 参数值
     */
    public void bind(Object param) {
        params.add(param);
    }

    /**
     * 给指定下标的参数绑定值, 如:5
     *
     * @param index 参数下标
     * @param param 参数值
     */
    public void bind(int index, Object param) {
        if (index >= params.size()) {
            List<Object> oldParams = params;
            params = new ArrayList<>(index / 2 * 2);
            params.addAll(oldParams);
        }
        params.set(index, param);
    }

    /**
     * 绑定命名参数, 如:a
     *
     * @param name  参数名
     * @param param 参数值
     */
    public void bind(String name, Object param) {
        if (name != null && param != null) {
            namedParams.put(name, param);
        }
    }

    /**
     * 根据执行datasource名字获取datasource
     *
     * @param name datasource名字
     * @return datasource flux
     */
    public Flux<Object> getDataSource(String name) {
        if (!dataSources.containsKey(name)) {
            throw new IllegalStateException(String.format("can not find data source named '%s'", name));
        }

        return dataSources.get(name);
    }

    /**
     * 根据下标获取参数
     *
     * @param index 参数下标
     * @return 参数值optional
     */
    public Optional<Object> getParam(int index) {
        if (params.size() <= index) {
            return Optional.empty();
        }
        return Optional.ofNullable(params.get(index));
    }

    /**
     * 根据参数名获取参数
     *
     * @param name 参数名
     * @return 参数值optional
     */
    public Optional<Object> getParam(String name) {
        return Optional.ofNullable(namedParams.get(name));
    }
}
