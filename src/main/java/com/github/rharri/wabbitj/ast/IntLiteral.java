package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

import java.util.Objects;
import java.util.Optional;

public final class IntLiteral implements Expression {
    private final int digits;

    private IntLiteral(int digits) {
        this.digits = digits;
    }

    public static IntLiteral newInstance(int digits) {
        return new IntLiteral(digits);
    }

    @Override
    public Optional<Object> accept(NodeVisitor visitor) {
        return visitor.visitIntLiteral(this);
    }

    @Override
    public String toString() {
        return "IntLiteral [digits=" + digits + "]";
    }

    public int digits() {
        return digits;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (IntLiteral) obj;
        return this.digits == that.digits;
    }

    @Override
    public int hashCode() {
        return Objects.hash(digits);
    }
}
