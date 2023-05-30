package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.*;

import java.util.ArrayDeque;
import java.util.Deque;

public class Interpreter implements NodeVisitor {

    private final JavaRuntime runtime;
    private final Deque<WabbitValue<?>> stack = new ArrayDeque<>();

    private Interpreter(JavaRuntime runtime) {
        this.runtime = runtime;
    }

    public static Interpreter newInstance(JavaRuntime runtime) {
        return new Interpreter(runtime);
    }

    @Override
    public void visitProgram(Program program) {
        program.statements().accept(this);
    }

    @Override
    public void visitStatements(Statements statements) {
        for (AbstractSyntaxTree statement : statements.getStatements()) {
            statement.accept(this);
        }
    }

    @Override
    public void visitPrint(Print print) {
        print.expression().accept(this);
        WabbitValue<?> expression = stack.removeFirst();
        runtime.println(expression.value());
    }

    @Override
    public void visitIntLiteral(IntLiteral intLiteral) {
        stack.add(new WabbitValue<>(WabbitType.INT, intLiteral.getValue()));
    }

    @Override
    public void visitFloatLiteral(FloatLiteral floatLiteral) {
        stack.add(new WabbitValue<>(WabbitType.FLOAT, floatLiteral.getValue()));
    }

    @Override
    public void visitSumTerm(SumTerm sumTerm) {
        sumTerm.getLhs().accept(this);
        sumTerm.getRhs().accept(this);

        WabbitValue<?> lhs = stack.removeFirst();
        WabbitValue<?> rhs = stack.removeFirst();

        if (lhs.value() instanceof Integer && rhs.value() instanceof Integer) {
            int sum = runtime.sumInteger.apply((int)lhs.value(), (int)rhs.value());
            stack.add(new WabbitValue<>(WabbitType.INT, sum));
        } else if (lhs.value() instanceof Float && rhs.value() instanceof Float) {
            float sum = runtime.sumFloat.apply((float)lhs.value(), (float)rhs.value());
            stack.add(new WabbitValue<>(WabbitType.INT, sum));
        }
    }
}
