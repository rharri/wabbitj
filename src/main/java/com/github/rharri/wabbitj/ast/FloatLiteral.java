package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public record FloatLiteral(float value) implements Expression {

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitFloatLiteral(this);
    }
}
