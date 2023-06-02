/*
 * Copyright (c) 2023. Ryan Harri
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

    @Override
    public String toString() {
        return "Interpreter{" +
                "runtime=" + runtime +
                '}';
    }
}
