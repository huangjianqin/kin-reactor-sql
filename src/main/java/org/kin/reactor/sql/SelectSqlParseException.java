package org.kin.reactor.sql;

/**
 * select sql解析异常
 * @author huangjianqin
 * @date 2022/12/12
 */
public final class SelectSqlParseException extends RuntimeException {
    public SelectSqlParseException(Throwable cause) {
        super("select sql parse error", cause);
    }
}
