package org.kin.reactor.sql.feature;

/**
 * 一些内置支持的feature id
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class FeatureIds {
    private FeatureIds() {
    }

    //------------------------------------------------------------filter
    public static final String AND = wrapFilterId("and");
    public static final String BETWEEN = wrapFilterId("between");

    public static final String OR = wrapFilterId("or");
    public static final String LIKE = wrapFilterId("like");
    public static final String IN = wrapFilterId("in");

    /**
     * 自动包装filter feature id前缀
     * @return 前缀+id
     */
    public static String wrapFilterId(String id){
        return "filter:".concat(id);
    }
    //------------------------------------------------------------filter end

    //------------------------------------------------------------value mapper
    public static final String CASE_WHEN = wrapMapperId("case");
    public static final String CAST = wrapMapperId("cast");
    public static final String COALESCE = wrapMapperId("coalesce");
    public static final String DATE_FORMAT = wrapMapperId("date_format");
    public static final String IF = wrapMapperId("if");
    public static final String NOW = wrapMapperId("now");
    public static final String PROPERTY = wrapMapperId("property");

    /**
     * 自动包装value mapper feature id前缀
     * @return 前缀+id
     */
    public static String wrapMapperId(String id){
        return "value-map:".concat(id);
    }
    //------------------------------------------------------------value mapper end
}
