package org.kin.reactor.sql.feature.map;

import net.sf.jsqlparser.expression.Expression;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;
import reactor.util.function.Tuple2;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 二元函数操作, gt
 * @author huangjianqin
 * @date 2022/12/14
 */
public class BinaryMapper implements ValueMapFeature<Expression>{
    /** feature id */
    private final String id;
    /** 二元函数逻辑 */
    private final BiFunction<Object, Object, Object> func;

    public BinaryMapper(String id, BiFunction<Object, Object, Object> func) {
        this.id = FeatureIds.wrapMapperId(id);
        this.func = func;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Function<Record, Object> map(Expression expression) {
        Tuple2<Function<Record, Object>, Function<Record, Object>> tuple2 = ValueMapFeature.createBinaryMapper(expression);

        Function<Record, Object> leftMapper = tuple2.getT1();
        Function<Record, Object> rightMapper = tuple2.getT2();

        return v -> func.apply(leftMapper.apply(v), rightMapper.apply(v));
    }
}
