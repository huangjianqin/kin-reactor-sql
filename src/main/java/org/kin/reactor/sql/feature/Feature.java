package org.kin.reactor.sql.feature;

/**
 * sql特性标识
 * @author huangjianqin
 * @date 2022/12/12
 */
@FunctionalInterface
public interface Feature {
    /**
     * 返回feature唯一标识
     * @return feature唯一标识
     */
    String id();
}
