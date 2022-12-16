package org.kin.reactor.sql;

import net.sf.jsqlparser.expression.Expression;
import org.kin.reactor.sql.feature.Feature;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.feature.filter.*;
import org.kin.reactor.sql.feature.map.*;
import org.kin.reactor.sql.utils.CalculateUtils;
import org.kin.reactor.sql.utils.CompareUtils;
import org.kin.reactor.sql.utils.MD5;
import org.kin.reactor.sql.utils.ObjectUtils;
import reactor.util.annotation.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 全局内置支持的{@link org.kin.reactor.sql.feature.Feature}实现
 *
 * @author huangjianqin
 * @date 2022/12/12
 */
public final class GlobalFeatures {
    private GlobalFeatures() {
    }

    private static final Map<String, Feature> GLOBAL_FEATURES = new HashMap<>();

    static {
        //filter
        EqualsFilter eq = new EqualsFilter("=", false);
        addGlobal(eq);
        EqualsFilter neq = new EqualsFilter("!=", true);
        addGlobal(neq);
        addGlobal(new EqualsFilter("<>", true));
        addGlobal(new LikeFilter());
        GreaterThanFilter gt = new GreaterThanFilter();
        addGlobal(gt);
        GreaterThanEqualsFilter gte = new GreaterThanEqualsFilter();
        addGlobal(gte);
        LessThanFilter lt = new LessThanFilter();
        addGlobal(lt);
        LessThanEqualsFilter lte = new LessThanEqualsFilter();
        addGlobal(lte);
        addGlobal(new AndFilter());
        addGlobal(new OrFilter());
        addGlobal(new BetweenFilter());
        addGlobal(new InFilter());

        //value mapper
        addGlobal(new PropertyMapper());
        addGlobal(new CaseWhenMapper());
        addGlobal(new IfValueMapper());
        addGlobal(new BinaryMapper("eq", eq::predicate));
        addGlobal(new BinaryMapper("neq", neq::predicate));
        addGlobal(new BinaryMapper("str_like", (left, right) -> LikeFilter.predicate(false, left, right)));
        addGlobal(new BinaryMapper("str_nlike", (left, right) -> LikeFilter.predicate(true, left, right)));
        addGlobal(new BinaryMapper("gt", gt::predicate));
        addGlobal(new BinaryMapper("gte", gte::predicate));
        addGlobal(new BinaryMapper("lt", lt::predicate));
        addGlobal(new BinaryMapper("lte", lte::predicate));
        addGlobal(new NowMapper());
        addGlobal(new CastMapper());
        addGlobal(new DateFormatMapper());
        addGlobal(new CoalesceMapper());
        addGlobal(new BinaryCalculatorMapper("+", CalculateUtils::add));
        addGlobal(new BinaryCalculatorMapper("-", CalculateUtils::subtract));
        addGlobal(new BinaryCalculatorMapper("*", CalculateUtils::multiply));
        addGlobal(new BinaryCalculatorMapper("/", CalculateUtils::division));
        addGlobal(new BinaryCalculatorMapper("%", CalculateUtils::mod));
        addGlobal(new BinaryCalculatorMapper("&", CalculateUtils::bitAnd));
        addGlobal(new BinaryCalculatorMapper("|", CalculateUtils::bitOr));
        addGlobal(new BinaryCalculatorMapper("^", CalculateUtils::bitMutex));
        addGlobal(new BinaryCalculatorMapper("<<", CalculateUtils::leftShift));
        addGlobal(new BinaryCalculatorMapper(">>", CalculateUtils::rightShift));
        addGlobal(new BinaryCalculatorMapper(">>>", CalculateUtils::unsignedRightShift));

        //function
        //value||'s'
        addGlobal(new BinaryMapper("||", (left, right) -> {
            if (left == null) {
                left = "";
            }
            if (right == null) {
                right = "";
            }
            return String.valueOf(left).concat(String.valueOf(right));
        }));
        //concat(value,'s')
        addGlobal(new FunctionMapper("concat", 1, 9999, params -> params
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining())));
        addGlobal(new FunctionMapper("substr", 2, 3, params -> {
            String str = String.valueOf(params.get(0));
            int start = ObjectUtils.castNumber(params.get(1)).intValue();
            int length = params.size() == 2 ? str.length() : ObjectUtils.castNumber(params.get(2)).intValue();

            if (start < 0) {
                start = str.length() + start;
            }

            if (start < 0 || str.length() < start) {
                return "";
            }
            int endIndex = start + length;
            if (str.length() < endIndex) {
                return str.substring(start);
            }
            return str.substring(start, start + length);
        }));
        //contains_all(val,'a','b','c')
        addGlobal(new FunctionMapper("contains_all", 2, 999, params -> {
            List<Object> list = ObjectUtils.castList(params.get(0));
            for (int i = 1; i < params.size(); i++) {
                if (!list.contains(list.get(i))) {
                    return false;
                }
            }

            return true;
        }));
        //not_contains(val,'a','b','c')
        addGlobal(new FunctionMapper("not_contains", 2, 999, params -> {
            List<Object> list = ObjectUtils.castList(params.get(0));
            for (int i = 1; i < params.size(); i++) {
                if (list.contains(list.get(i))) {
                    return false;
                }
            }

            return true;
        }));
        //contains_any(val,'a','b','c')
        addGlobal(new FunctionMapper("contains_any", 2, 999, params -> {
            List<Object> list = ObjectUtils.castList(params.get(0));
            for (int i = 1; i < params.size(); i++) {
                if (list.contains(list.get(i))) {
                    return true;
                }
            }

            return false;
        }));
        //in(val,'a','b','c')
        addGlobal(new FunctionMapper("in", 2, 9999, params -> {
            Object first = params.get(0);
            for (int i = 1; i < params.size(); i++) {
                if (CompareUtils.equals(first, params.get(i))) {
                    return true;
                }
            }

            return false;
        }));
        //nin(val,'a','b','c')
        addGlobal(new FunctionMapper("nin", 2, 9999, params -> {
            Object first = params.get(0);
            for (int i = 1; i < params.size(); i++) {
                if (CompareUtils.equals(first, params.get(i))) {
                    return false;
                }
            }

            return true;
        }));
        //btw(val,1,10)
        Function<List<Object>, Object> btw = params -> {
            Object first = params.get(0);
            Object left = params.size() > 1 ? params.get(1) : null;
            Object right = params.size() > 2 ? params.get(params.size() - 1) : null;
            return BetweenFilter.predicate(first, left, right);
        };
        addGlobal(new FunctionMapper("btw", 2, 3, btw));
        //range
        addGlobal(new FunctionMapper("range", 2, 3, btw));
        //nbtw(val,1,10)
        addGlobal(new FunctionMapper("nbtw", 2, 3, params -> !ObjectUtils.castBoolean(btw)));
        addGlobal(new BinaryCalculatorMapper("bit_left_shift", CalculateUtils::leftShift));
        addGlobal(new BinaryCalculatorMapper("bit_right_shift", CalculateUtils::rightShift));
        addGlobal(new BinaryCalculatorMapper("bit_unsigned_shift", CalculateUtils::unsignedRightShift));
        addGlobal(new BinaryCalculatorMapper("bit_and", CalculateUtils::bitAnd));
        addGlobal(new BinaryCalculatorMapper("bit_or", CalculateUtils::bitOr));
        addGlobal(new BinaryCalculatorMapper("bit_mutex", CalculateUtils::bitMutex));
        //math
        addGlobal(new SingleParamFuncMapper("bit_not", v -> CalculateUtils.bitNot(ObjectUtils.castNumber(v))));
        addGlobal(new SingleParamFuncMapper("bit_count", v -> CalculateUtils.bitCount(ObjectUtils.castNumber(v))));
        addGlobal(new SingleParamFuncMapper("math.log", v -> Math.log(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.log1p", v -> Math.log1p(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.log10", v -> Math.log10(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.exp", v -> Math.exp(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.expm1", v -> Math.expm1(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.rint", v -> Math.rint(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.sin", v -> Math.sin(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.asin", v -> Math.asin(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.sinh", v -> Math.sinh(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.cos", v -> Math.cos(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.cosh", v -> Math.cosh(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.acos", v -> Math.acos(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.tan", v -> Math.tan(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.tanh", v -> Math.tanh(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.atan", v -> Math.atan(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.ceil", v -> Math.ceil(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.round", v -> Math.round(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.floor", v -> Math.floor(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.abs", v -> Math.abs(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.degrees", v -> Math.toDegrees(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new SingleParamFuncMapper("math.radians", v -> Math.toRadians(ObjectUtils.castNumber(v).doubleValue())));
        addGlobal(new FunctionMapper("math.max", 1, 9999, params -> params.stream().max(CompareUtils::compare).orElse(0D)));
        addGlobal(new FunctionMapper("math.min", 1, 9999, params -> params.stream().min(CompareUtils::compare).orElse(0D)));
        addGlobal(new FunctionMapper("math.avg", 1, 9999, params -> params.stream().collect(Collectors.averagingDouble(i -> ObjectUtils.castNumber(i).doubleValue()))));
        addGlobal(new FunctionMapper("math.count", 1, 9999, List::size));
        addGlobal(new BinaryCalculatorMapper("math.plus", CalculateUtils::add));
        addGlobal(new BinaryCalculatorMapper("math.sub", CalculateUtils::subtract));
        addGlobal(new BinaryCalculatorMapper("math.mul", CalculateUtils::multiply));
        addGlobal(new BinaryCalculatorMapper("math.div", CalculateUtils::division));
        addGlobal(new BinaryCalculatorMapper("math.mod", CalculateUtils::mod));
        addGlobal(new BinaryCalculatorMapper("math.atan2", (v1, v2) -> Math.atan2(v1.doubleValue(), v2.doubleValue())));
        addGlobal(new BinaryCalculatorMapper("math.ieee_rem", (v1, v2) -> Math.IEEEremainder(v1.doubleValue(), v2.doubleValue())));
        addGlobal(new BinaryCalculatorMapper("math.copy_sign", (v1, v2) -> Math.copySign(v1.doubleValue(), v2.doubleValue())));
        //math end
        addGlobal(new SingleParamFuncMapper("is_null", Objects::isNull));
        addGlobal(new SingleParamFuncMapper("non_null", Objects::nonNull));
        addGlobal(new SingleParamFuncMapper("is_str", p -> p instanceof String));
        addGlobal(new SingleParamFuncMapper("is_bool", p -> boolean.class.equals(p) || Boolean.class.equals(p)));
        addGlobal(new SingleParamFuncMapper("is_int", p -> short.class.equals(p) || Short.class.equals(p) ||
                int.class.equals(p) || Integer.class.equals(p) ||
                long.class.equals(p) || Long.class.equals(p)));
        addGlobal(new SingleParamFuncMapper("is_float", p -> float.class.equals(p) || Float.class.equals(p) ||
                double.class.equals(p) || Double.class.equals(p)));
        addGlobal(new SingleParamFuncMapper("is_num", p -> p instanceof Number));
        addGlobal(new SingleParamFuncMapper("is_map", p -> p instanceof Map));
        addGlobal(new SingleParamFuncMapper("is_array", p -> p.getClass().isArray() || p instanceof Collection));
        //array
        addGlobal(new SingleParamFuncMapper("len", p -> {
            if (p.getClass().isArray()) {
                return ((Object[]) p).length;
            }

            if (p instanceof Collection) {
                return ((Collection<?>) p).size();
            }

            if (p instanceof Map) {
                return ((Map<?, ?>) p).size();
            }

            throw new UnsupportedOperationException("target is not array or list");
        }));
        addGlobal(new FunctionMapper("sublist", 2, 2, params -> {
            Object first = params.get(0);
            int startIdx = ObjectUtils.castNumber(params.get(1)).intValue();
            if (first.getClass().isArray()) {
                Object[] arr = (Object[]) first;
                return Arrays.copyOfRange(arr, startIdx, arr.length);
            }

            if (first instanceof List) {
                List<?> list = (List<?>) first;
                return list.subList(startIdx, list.size());
            }

            throw new UnsupportedOperationException("target is not array or list");
        }));
        addGlobal(new SingleParamFuncMapper("md5", p -> MD5.common().encode(p.toString())));
    }

    /**
     * 添加全局特性
     *
     * @param feature 特性
     */
    static void addGlobal(Feature feature) {
        GLOBAL_FEATURES.put(feature.id(), feature);
    }

    /**
     * 获取内置支持{@link Feature}实现
     *
     * @param id  feature id
     * @param <T> {@link Feature}真实类型
     * @return {@link Feature}实现
     */
    @Nullable
    public static <T extends Feature> T getFeature(String id) {
        try {
            return getFeatureOrThrow(id);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取内置支持{@link Feature}实现, 如果不存在则抛异常
     *
     * @param id  feature id
     * @param <T> {@link Feature}真实类型
     * @return {@link Feature}实现
     */
    @SuppressWarnings("unchecked")
    public static <T extends Feature> T getFeatureOrThrow(String id) {
        Feature feature = GLOBAL_FEATURES.get(id);
        if (Objects.isNull(feature)) {
            throw new UnsupportedOperationException(String.format("not support feature '%s'", id));
        }

        return (T) feature;
    }

    /**
     * 获取内置支持{@link FilterFeature}实现, 如果不存在则抛异常
     *
     * @param id  feature id
     * @param <T> {@link FilterFeature}真实类型
     * @return {@link FilterFeature}实现
     */
    public static <T extends FilterFeature<? extends Expression>> T getFilterFeatureOrThrow(String id) {
        return getFeatureOrThrow(FeatureIds.wrapFilterId(id));
    }

    /**
     * 获取内置支持{@link FilterFeature}实现
     *
     * @param id  feature id
     * @param <T> {@link FilterFeature}真实类型
     * @return {@link FilterFeature}实现
     */
    public static <T extends FilterFeature<? extends Expression>> T getFilterFeature(String id) {
        return getFeature(FeatureIds.wrapFilterId(id));
    }

    /**
     * 获取内置支持{@link ValueMapFeature}实现, 如果不存在则抛异常
     *
     * @param id  feature id
     * @param <T> {@link ValueMapFeature}真实类型
     * @return {@link ValueMapFeature}实现
     */
    public static <T extends ValueMapFeature<? extends Expression>> T getValueMapFeatureOrThrow(String id) {
        return getFeatureOrThrow(FeatureIds.wrapMapperId(id));
    }

    /**
     * 获取内置支持{@link ValueMapFeature}实现
     *
     * @param id  feature id
     * @param <T> {@link ValueMapFeature}真实类型
     * @return {@link ValueMapFeature}实现
     */
    public static <T extends ValueMapFeature<? extends Expression>> T getValueMapFeature(String id) {
        return getFeature(FeatureIds.wrapMapperId(id));
    }
}
