package org.kin.reactor.sql;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.kin.reactor.sql.utils.StringUtils;
import org.kin.reactor.sql.visitor.TableGetter;
import reactor.util.annotation.Nullable;

/**
 * 使用入口, 如果相同sql, 建议复用实例, 减少sql解析开销
 *
 * @author huangjianqin
 * @date 2022/12/12
 */
public final class ReactorSql {
    /** raw sql */
    private final String sql;
    /** parsed sql */
    private final PlainSelect parsedSql;

    private ReactorSql(String sql) {
        this.sql = sql;
        try {
            this.parsedSql = ((PlainSelect) ((Select) CCJSqlParserUtil.parse(sql)).getSelectBody());
        } catch (JSQLParserException e) {
            throw new SelectSqlParseException(e);
        }
    }

    public static ReactorSql create(String sql) {
        return new ReactorSql(sql);
    }

    /**
     * 准备执行sql
     *
     * @return {@link ReactorSqlExecutor}实例
     */
    public ReactorSqlExecutor prepare() {
        return new ReactorSqlExecutor(parsedSql);
    }

    /**
     * 从from table中获取table名, 如果不是from table格式, 则返回null
     *
     * @return table名
     */
    @Nullable
    public String getTable() {
        TableGetter getter = new TableGetter();
        parsedSql.getFromItem().accept(getter);
        return StringUtils.trimDeclaration(getter.getTable());
    }

    //getter
    public String getSql() {
        return sql;
    }

    PlainSelect getParsedSql() {
        return parsedSql;
    }
}
