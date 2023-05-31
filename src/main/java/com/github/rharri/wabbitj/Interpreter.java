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
        for (AbstractSyntaxTree statement : statements.statements()) {
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
        int value = intLiteral.value();
        stack.push(new WabbitValue(WabbitType.INT, value));
    }

    @Override
    public void visitFloatLiteral(FloatLiteral floatLiteral) {
        float value = floatLiteral.value();
        stack.push(new WabbitValue(WabbitType.FLOAT, value));
    }

    @Override
    public void visitBinaryOp(BinaryOp binaryOp) {
        binaryOp.lhs().accept(this);
        binaryOp.rhs().accept(this);

        WabbitValue rhs = stack.pop();
        WabbitValue lhs = stack.pop();

        Object result = runtime.binaryOp(binaryOp.operator(), lhs.javaObject(), rhs.javaObject());
        stack.add(new WabbitValue(WabbitType.ANY, result));
    }

    @Override
    public void visitUnaryOp(UnaryOp unaryOp) {
        unaryOp.operand().accept(this);

        WabbitValue operand = stack.pop();

        Object result = runtime.unaryOp(unaryOp.operator(), operand.javaObject());
        stack.add(new WabbitValue(WabbitType.ANY, result));
    }

    @Override
    public void visitGrouping(Grouping grouping) {
        grouping.expression().accept(this);
    }
}
