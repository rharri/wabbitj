package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public record FloatLiteral(float value, int line, int column) implements Expression, SourceLocation {

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitFloatLiteral(this);
    }
}
