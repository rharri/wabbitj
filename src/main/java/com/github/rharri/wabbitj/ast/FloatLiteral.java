package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

import java.util.Objects;
import java.util.Optional;

public class FloatLiteral implements Expression {
    private final float floatingPointValue;

    private FloatLiteral(float floatingPointValue) {
        this.floatingPointValue = floatingPointValue;
    }

    public static FloatLiteral newInstance(float floatingPointValue) {
        return new FloatLiteral(floatingPointValue);
    }

    @Override
    public Optional<Object> accept(NodeVisitor visitor) {
        return visitor.visitFloatLiteral(this);
    }

    @Override
    public String toString() {
        return "FloatLiteral [floatingPointValue=" + floatingPointValue + "]";
    }

    public float floatingPointValue() {
        return floatingPointValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FloatLiteral) obj;
        return this.floatingPointValue == that.floatingPointValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(floatingPointValue);
    }
}
