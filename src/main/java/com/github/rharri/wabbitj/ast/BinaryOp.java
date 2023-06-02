package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public record BinaryOp(Operator operator, Expression lhs, Expression rhs, int line, int column)
        implements Expression, SourceLocation {

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitBinaryOp(this);
    }
}
