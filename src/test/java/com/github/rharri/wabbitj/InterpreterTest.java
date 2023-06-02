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
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {

    private record Tuple(OutputStream out, PrintStream printableStream) {
    }

    private Tuple printableByteArrayStream() {
        // An OutputStream that "writes" bytes to a byte[]
        var streamToByteArray = new ByteArrayOutputStream();

        // Add printing functionality to the byte[] output stream
        var printStream =  new PrintStream(streamToByteArray);

        return new Tuple(streamToByteArray, printStream);
    }

    @Test
    public void shouldPrintIntLiteral() {
        var intLiteral = new IntLiteral(42, 0, 0);
        var print = new Print(intLiteral);
        var statements = new Statements();
        statements.add(print);
        var program = new Program(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("42", streamTuple.out.toString().trim());
    }

    @Test
    public void shouldPrintAdditionExpression() {
        var intLiteral1 = new IntLiteral(2, 0, 0);
        var intLiteral2 = new IntLiteral(3, 0, 0);
        var binaryOp = new BinaryOp(Operator.PLUS, intLiteral1, intLiteral2, 0, 0);
        var print = new Print(binaryOp);
        var statements = new Statements();
        statements.add(print);
        var program = new Program(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("5", streamTuple.out.toString().trim());
    }

    @Test
    public void shouldPrintSubtractionExpression() {
        var intLiteral1 = new IntLiteral(46, 0, 0);
        var intLiteral2 = new IntLiteral(4, 0, 0);
        var binaryOp = new BinaryOp(Operator.MINUS, intLiteral1, intLiteral2, 0, 0);
        var print = new Print(binaryOp);
        var statements = new Statements();
        statements.add(print);
        var program = new Program(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("42", streamTuple.out.toString().trim());
    }

    @Test
    public void shouldPrintMultiplicationExpression() {
        var intLiteral1 = new IntLiteral(2, 0, 0);
        var intLiteral2 = new IntLiteral(3, 0, 0);
        var binaryOp = new BinaryOp(Operator.TIMES, intLiteral1, intLiteral2, 0, 0);
        var print = new Print(binaryOp);
        var statements = new Statements();
        statements.add(print);
        var program = new Program(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("6", streamTuple.out.toString().trim());
    }

    @Test
    public void shouldPrintDivideExpression() {
        var intLiteral1 = new IntLiteral(6, 0, 0);
        var intLiteral2 = new IntLiteral(2, 0, 0);
        var binaryOp = new BinaryOp(Operator.DIVIDE, intLiteral1, intLiteral2, 0, 0);
        var print = new Print(binaryOp);
        var statements = new Statements();
        statements.add(print);
        var program = new Program(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("3", streamTuple.out.toString().trim());
    }

    @Test
    public void shouldPrintUnaryExpression() {
        var intLiteral = new IntLiteral(5, 0, 0);
        var unaryOp = new UnaryOp(Operator.MINUS, intLiteral);
        var print = new Print(unaryOp);
        var statements = new Statements();
        statements.add(print);
        var program = new Program(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("-5", streamTuple.out.toString().trim());
    }

    @Test
    public void shouldPrintTrinomialExpression() {
        var intLiteral1 = new IntLiteral(3, 0, 0);
        var intLiteral2 = new IntLiteral(4, 0, 0);
        var binaryOp1 = new BinaryOp(Operator.TIMES, intLiteral1, intLiteral2, 0, 0);
        var intLiteral3 = new IntLiteral(2, 0, 0);
        var binaryOp2 = new BinaryOp(Operator.PLUS, intLiteral3, binaryOp1, 0, 0);
        var print = new Print(binaryOp2);
        var statements = new Statements();
        statements.add(print);
        var program = new Program(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("14", streamTuple.out.toString().trim());
    }

    @Test
    public void shouldPrintGroupingExpression() {
        var intLiteral1 = new IntLiteral(2, 0, 0);
        var intLiteral2 = new IntLiteral(3, 0, 0);
        var binaryOp1 = new BinaryOp(Operator.PLUS, intLiteral1, intLiteral2, 0, 0);
        var grouping = new Grouping(binaryOp1);
        var intLiteral3 = new IntLiteral(4, 0, 0);
        var binaryOp2 = new BinaryOp(Operator.TIMES, grouping, intLiteral3, 0, 0);
        var print = new Print(binaryOp2);
        var statements = new Statements();
        statements.add(print);
        var program = new Program(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("20", streamTuple.out.toString().trim());
    }
}
