package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.*;

import java.util.ArrayDeque;
import java.util.Deque;

public class Interpreter implements NodeVisitor {

    private final JavaRuntime runtime;
    private final Deque<WabbitValue> stack = new ArrayDeque<>();

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
        WabbitValue wabbitValue = stack.pop();
        runtime.println(wabbitValue.javaObject());
    }

    @Override
    public void visitIntLiteral(IntLiteral intLiteral) {
        int value = intLiteral.getValue();
        stack.push(new WabbitValue(WabbitType.INT, value));
    }

    @Override
    public void visitFloatLiteral(FloatLiteral floatLiteral) {
        float value = floatLiteral.getValue();
        stack.push(new WabbitValue(WabbitType.FLOAT, value));
    }

    @Override
    public void visitBinaryOp(BinaryOp binaryOp) {
        binaryOp.getLhs().accept(this);
        binaryOp.getRhs().accept(this);

        WabbitValue lhs = stack.pop();
        WabbitValue rhs = stack.pop();

        Object result = runtime.binaryOp(binaryOp.getOperator(), lhs.javaObject(), rhs.javaObject());
        stack.add(new WabbitValue(WabbitType.ANY, result));
    }
}
