package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

import java.util.Objects;
import java.util.Optional;

public final class Program implements AbstractSyntaxTree {
    private final Statements statements;

    private Program(Statements statements) {
        this.statements = statements;
    }

    public static Program newInstance(Statements statements) {
        return new Program(statements);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitProgram(this);
    }

    @Override
    public String toString() {
        return "Program [statements=" + statements + "]";
    }

    public Statements statements() {
        return statements;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Program) obj;
        return Objects.equals(this.statements, that.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statements);
    }

}
