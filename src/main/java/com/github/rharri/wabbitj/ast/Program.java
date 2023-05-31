package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public record Program(Statements statements) implements AbstractSyntaxTree {

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitProgram(this);
    }
}
