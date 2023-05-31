package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public record Print(Expression expression) implements Statement {

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitPrint(this);
    }
}
