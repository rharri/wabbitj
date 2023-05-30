package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

import java.util.Objects;

public final class IntLiteral implements Expression {
    private final int value;

    private IntLiteral(int value) {
        this.value = value;
    }

    public static IntLiteral newInstance(int value) {
        return new IntLiteral(value);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitIntLiteral(this);
    }

    @Override
    public String toString() {
        return "IntLiteral [value=" + value + "]";
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (IntLiteral) obj;
        return this.value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
