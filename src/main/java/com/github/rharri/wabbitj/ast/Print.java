package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

import java.util.Objects;
import java.util.Optional;

public final class Print implements Statement {
    private final Expression expression;

    private Print(Expression expression) {
        this.expression = expression;
    }

    public static Print newInstance(Expression expression) {
        return new Print(expression);
    }

    @Override
    public Optional<Object> accept(NodeVisitor visitor) {
        return visitor.visitPrint(this);
    }

    @Override
    public String toString() {
        return "Print [expression=" + expression + "]";
    }

    public Expression expression() {
        return expression;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Print) obj;
        return Objects.equals(this.expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }
}
