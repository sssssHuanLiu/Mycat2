package io.mycat.commands;

import io.mycat.MycatDataContext;
import io.mycat.client.SQLRequest;
import io.mycat.util.Response;

public class UseStatementCommand implements MycatCommand{
    @Override
    public boolean run(SQLRequest request, MycatDataContext context, Response response) {
        context.useShcema(request.get("schema"));
        response.sendOk();
        return true;
    }

    @Override
    public boolean explain(SQLRequest request, MycatDataContext context, Response response) {
        response.sendExplain(UseStatementCommand.class,"use " + request.get("schema"));
        return true;
    }

    @Override
    public String getName() {
        return "useStatement";
    }
}