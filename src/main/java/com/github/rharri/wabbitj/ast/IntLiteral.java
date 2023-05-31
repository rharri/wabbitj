package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public record IntLiteral(int value) implements Expression {

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitIntLiteral(this);
    }
}
