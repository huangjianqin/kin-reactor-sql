package org.kin.reactor.sql.visitor;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

/**
 * 从from table中获取table名, 如果不是from table格式, 则返回null
 * 不支持取alias
 * @author huangjianqin
 * @date 2022/12/12
 */
public final class TableGetter extends FromItemVisitorAdapter {
    /** raw table name */
    private String table;

    @Override
    public void visit(Table table) {
        this.table = table.getName();
    }

    //getter
    public String getTable() {
        return table;
    }
}
