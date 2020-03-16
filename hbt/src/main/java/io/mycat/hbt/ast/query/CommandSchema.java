package io.mycat.hbt.ast.query;

import io.mycat.hbt.HBTOp;
import io.mycat.hbt.ast.base.NodeVisitor;
import io.mycat.hbt.ast.base.Schema;
import lombok.Getter;


@Getter
public class CommandSchema extends Schema {
    private final Schema schema;

    public CommandSchema(HBTOp op, Schema schema) {
        super(op);
        this.schema = schema;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}