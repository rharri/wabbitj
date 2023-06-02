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

import java.util.*;

public class TypeChecker implements NodeVisitor {

    private final List<String> errors = new ArrayList<>();
    private final String filename;
    private final String programText;
    private final Deque<TypeInfo> stack = new ArrayDeque<>();

    public TypeChecker(String filename, String programText) {
        this.filename = filename;
        this.programText = programText;
    }

    @Override
    public void visitProgram(Program program) {
        program.statements().accept(this);
    }

    @Override
    public void visitStatements(Statements statements) {
        for (Statement statement : statements.statements())
            statement.accept(this);
    }

    @Override
    public void visitPrint(Print print) {
        print.expression().accept(this);
    }

    @Override
    public void visitIntLiteral(IntLiteral intLiteral) {
        stack.push(new TypeInfo(WabbitType.INT, intLiteral.line(), intLiteral.column()));
    }

    @Override
    public void visitFloatLiteral(FloatLiteral floatLiteral) {
        stack.push(new TypeInfo(WabbitType.FLOAT, floatLiteral.line(), floatLiteral.column()));
    }

    @Override
    public void visitBinaryOp(BinaryOp binaryOp) {
        binaryOp.lhs().accept(this);
        binaryOp.rhs().accept(this);

        TypeInfo rhs = stack.pop();
        TypeInfo lhs = stack.pop();

        if (lhs.type != rhs.type) {
            String message = String.format("Type Error: unsupported operand type(s) for %s: '%s' and '%s'",
                    binaryOp.operator(),
                    lhs.type,
                    rhs.type);
            errors.add(formatErrorMessage(message, binaryOp.line(), binaryOp.column()));
        }

        // Reduce the binary op to a type; use the type of the LHS
        // If the types are the same then it doesn't matter which type we use and if the types are different then
        // another type error *could* be raised for the same line
        stack.push(lhs);
    }

    @Override
    public void visitUnaryOp(UnaryOp unaryOp) {
        // Not handled yet
    }

    @Override
    public void visitGrouping(Grouping grouping) {
        // Not handled yet
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    private String formatErrorMessage(String message, int line, int column) {
        String[] lines = programText.split("\n");

        // File 'file.wb', line 1, col 1
        String header = String.format("File '%s', line %s, col %s", filename, line, column);

        // The line with the error
        String subject = lines[line - 1];

        // An arrow to highlight the error with the line
        String highlighter = String.format("\t " + " ".repeat(column - 2) + "%s", "^------");

        return header + "\n\t" + subject + "\n" + highlighter + "\n" + message + "\n";
    }

    @Override
    public String toString() {
        return "TypeChecker{" +
                "filename='" + filename + '\'' +
                '}';
    }

    private record TypeInfo(WabbitType type, int line, int column) {
    }
}
