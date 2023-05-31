package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public record Grouping(Expression expression) implements Expression {

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitGrouping(this);
    }
}
