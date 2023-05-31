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
        var intLiteral = new IntLiteral(42);
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
        var intLiteral1 = new IntLiteral(2);
        var intLiteral2 = new IntLiteral(3);
        var binaryOp = new BinaryOp(Operator.PLUS, intLiteral1, intLiteral2);
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
        var intLiteral1 = new IntLiteral(46);
        var intLiteral2 = new IntLiteral(4);
        var binaryOp = new BinaryOp(Operator.MINUS, intLiteral1, intLiteral2);
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
        var intLiteral1 = new IntLiteral(2);
        var intLiteral2 = new IntLiteral(3);
        var binaryOp = new BinaryOp(Operator.TIMES, intLiteral1, intLiteral2);
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
        var intLiteral1 = new IntLiteral(6);
        var intLiteral2 = new IntLiteral(2);
        var binaryOp = new BinaryOp(Operator.DIVIDE, intLiteral1, intLiteral2);
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
        var intLiteral = new IntLiteral(5);
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
        var intLiteral1 = new IntLiteral(3);
        var intLiteral2 = new IntLiteral(4);
        var binaryOp1 = new BinaryOp(Operator.TIMES, intLiteral1, intLiteral2);
        var intLiteral3 = new IntLiteral(2);
        var binaryOp2 = new BinaryOp(Operator.PLUS, intLiteral3, binaryOp1);
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
        var intLiteral1 = new IntLiteral(2);
        var intLiteral2 = new IntLiteral(3);
        var binaryOp1 = new BinaryOp(Operator.PLUS, intLiteral1, intLiteral2);
        var grouping = new Grouping(binaryOp1);
        var intLiteral3 = new IntLiteral(4);
        var binaryOp2 = new BinaryOp(Operator.TIMES, grouping, intLiteral3);
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
