package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Statements implements AbstractSyntaxTree {
    private final List<Statement> statements;

    public Statements() {
        this.statements = new ArrayList<>();
    }

    public void add(Statement statement) {
        statements.add(statement);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitStatements(this);
    }

    public List<Statement> statements() {
        return Collections.unmodifiableList(statements);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Statements) obj;
        return Objects.equals(this.statements, that.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statements);
    }

    @Override
    public String toString() {
        return "Statements[" +
                "statements=" + statements + ']';
    }
}
