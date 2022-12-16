package org.kin.reactor.sql.feature.map;

import org.kin.reactor.sql.utils.ObjectUtils;

import java.util.function.BiFunction;

/**
 * 二元计算操作, v+10
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class BinaryCalculatorMapper extends BinaryMapper {
    public BinaryCalculatorMapper(String id, BiFunction<Number, Number, Object> calculator) {
        super(id, (left,right)-> calculator.apply(ObjectUtils.castNumber(left), ObjectUtils.castNumber(right)));
    }
}
