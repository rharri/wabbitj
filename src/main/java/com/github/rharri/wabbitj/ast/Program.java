package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public record Program(Statements statements) implements AbstractSyntaxTree {

    public Program(Statements statements) {
        Statements copy = new Statements();
        for (Statement statement : statements.statements())
            copy.add(statement);
        this.statements = copy;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitProgram(this);
    }
}
