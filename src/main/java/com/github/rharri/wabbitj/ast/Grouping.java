package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public class Grouping implements Expression {

    private final Expression expression;

    private Grouping(Expression expression) {
        this.expression = expression;
    }

    public static Grouping newInstance(Expression expression) {
        return new Grouping(expression);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitGrouping(this);
    }

    public Expression getExpression() {
        return expression;
    }
}
