package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

import java.util.Objects;
import java.util.Optional;

public class FloatLiteral implements Expression {
    private final float value;

    private FloatLiteral(float value) {
        this.value = value;
    }

    public static FloatLiteral newInstance(float value) {
        return new FloatLiteral(value);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitFloatLiteral(this);
    }

    @Override
    public String toString() {
        return "FloatLiteral [value=" + value + "]";
    }

    public float getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FloatLiteral) obj;
        return this.value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
