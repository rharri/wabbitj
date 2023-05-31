package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public record UnaryOp(Operator operator, Expression operand) implements Expression {

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitUnaryOp(this);
    }
}
