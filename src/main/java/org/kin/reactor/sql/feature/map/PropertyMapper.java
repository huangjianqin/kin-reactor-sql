package org.kin.reactor.sql.feature.map;

import net.sf.jsqlparser.schema.Column;
import org.kin.reactor.sql.Record;
import org.kin.reactor.sql.feature.FeatureIds;
import org.kin.reactor.sql.utils.PropertyUtils;

import java.util.function.Function;

/**
 * @author huangjianqin
 * @date 2022/12/14
 */
public final class PropertyMapper implements ValueMapFeature<Column> {
    @Override
    public String id() {
        return FeatureIds.PROPERTY;
    }

    @Override
    public Function<Record, Object> map(Column column) {
        //分两部分
        String[] fullName = PropertyUtils.splitDot(column.getFullyQualifiedName(), 2);

        String name = fullName.length == 2 ? fullName[1] : fullName[0];
        String tableName = fullName.length == 1 ? Record.THIS : fullName[0];

        return r -> getProperty(tableName, name, r);
    }

    /**
     * 获取column值
     *
     * @param tableName 表名
     * @param name      字段名
     * @param record    记录
     * @return 字段值
     */
    private Object getProperty(String tableName, String name, Record record) {
        Object temp = record.getRecord(tableName);

        //从table取属性
        if (null != temp) {
            temp = PropertyUtils.getProperty(name, temp);
        }

        //从result取属性
        if (null == temp) {
            temp = PropertyUtils.getProperty(name, record.asMap());
        }

        //从record取属性
        if (null == temp) {
            temp = record.getRecord(name);
        }

        return temp;
    }
}
