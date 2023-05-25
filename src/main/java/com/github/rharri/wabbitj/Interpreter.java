package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.*;

import java.util.Optional;

public class Interpreter implements NodeVisitor {

    private final JavaRuntime runtime;

    private Interpreter(JavaRuntime runtime) {
        this.runtime = runtime;
    }

    public static Interpreter newInstance(JavaRuntime runtime) {
        return new Interpreter(runtime);
    }

    @Override
    public Optional<Object> visitProgram(Program program) {
        return program.statements().accept(this);
    }

    @Override
    public Optional<Object> visitStatements(Statements statements) {
        for (AbstractSyntaxTree statement : statements.getStatements()) {
            statement.accept(this);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Object> visitPrint(Print print) {
        Optional<Object> expression = print.expression().accept(this);
        runtime.println(expression.orElseThrow());
        return Optional.empty();
    }

    @Override
    public Optional<Object> visitIntLiteral(IntLiteral intLiteral) {
        return Optional.of(intLiteral.digits());
    }
}
