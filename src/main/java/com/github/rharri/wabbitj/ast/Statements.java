package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Statements implements AbstractSyntaxTree {
    private final List<Statement> statements;

    private Statements() {
        this.statements = new ArrayList<>();
    }

    public static Statements newInstance() {
        return new Statements();
    }

    public void add(Statement statement) {
        this.statements.add(statement);
    }

    public List<Statement> getStatements() {
        return Collections.unmodifiableList(this.statements);
    }

    @Override
    public Optional<Object> accept(NodeVisitor visitor) {
        return visitor.visitStatements(this);
    }

    @Override
    public String toString() {
        return "Statements [statements=" + statements + "]";
    }
}
