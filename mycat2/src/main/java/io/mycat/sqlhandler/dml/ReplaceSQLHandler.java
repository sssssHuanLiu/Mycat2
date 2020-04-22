package io.mycat.sqlhandler.dml;

import com.alibaba.fastsql.sql.ast.statement.SQLReplaceStatement;
import io.mycat.sqlhandler.AbstractSQLHandler;
import io.mycat.meta.MetadataService;
import io.mycat.proxy.session.MycatSession;
import io.mycat.util.Receiver;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Resource
public class ReplaceSQLHandler extends AbstractSQLHandler<SQLReplaceStatement> {
    @Resource
    private MetadataService mycatMetadataService;

    @PostConstruct
    public void init(){

    }

    @Override
    protected int onExecute(SQLRequest<SQLReplaceStatement> request, Receiver response, MycatSession session) {
        //直接调用已实现好的
        request.getContext().replaceStatementHandler().handleReplace(request.getAst(), response);
        return CODE_200;
    }
}