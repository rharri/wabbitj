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
        var intLiteral = IntLiteral.newInstance(42);
        var print = Print.newInstance(intLiteral);
        var statements = Statements.newInstance();
        statements.add(print);
        var program = Program.newInstance(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("42", streamTuple.out.toString().trim());
    }

    @Test
    public void shouldPrintAdditionExpression() {
        var intLiteral1 = IntLiteral.newInstance(2);
        var intLiteral2 = IntLiteral.newInstance(3);
        var binaryOp = BinaryOp.newInstance(Operator.PLUS, intLiteral1, intLiteral2);
        var print = Print.newInstance(binaryOp);
        var statements = Statements.newInstance();
        statements.add(print);
        var program = Program.newInstance(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("5", streamTuple.out.toString().trim());
    }

    @Test
    public void shouldPrintSubtractionExpression() {
        var intLiteral1 = IntLiteral.newInstance(46);
        var intLiteral2 = IntLiteral.newInstance(4);
        var binaryOp = BinaryOp.newInstance(Operator.MINUS, intLiteral1, intLiteral2);
        var print = Print.newInstance(binaryOp);
        var statements = Statements.newInstance();
        statements.add(print);
        var program = Program.newInstance(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("42", streamTuple.out.toString().trim());
    }

    @Test
    public void shouldPrintMultiplicationExpression() {
        var intLiteral1 = IntLiteral.newInstance(2);
        var intLiteral2 = IntLiteral.newInstance(3);
        var binaryOp = BinaryOp.newInstance(Operator.TIMES, intLiteral1, intLiteral2);
        var print = Print.newInstance(binaryOp);
        var statements = Statements.newInstance();
        statements.add(print);
        var program = Program.newInstance(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("6", streamTuple.out.toString().trim());
    }

    @Test
    public void shouldPrintDivideExpression() {
        var intLiteral1 = IntLiteral.newInstance(6);
        var intLiteral2 = IntLiteral.newInstance(2);
        var binaryOp = BinaryOp.newInstance(Operator.DIVIDE, intLiteral1, intLiteral2);
        var print = Print.newInstance(binaryOp);
        var statements = Statements.newInstance();
        statements.add(print);
        var program = Program.newInstance(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("3", streamTuple.out.toString().trim());
    }

    @Test
    public void shouldPrintUnaryExpression() {
        var intLiteral = IntLiteral.newInstance(5);
        var unaryOp = UnaryOp.newInstance(Operator.MINUS, intLiteral);
        var print = Print.newInstance(unaryOp);
        var statements = Statements.newInstance();
        statements.add(print);
        var program = Program.newInstance(statements);

        Tuple streamTuple = printableByteArrayStream();
        var runtime = JavaRuntime.newInstance(streamTuple.printableStream);
        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("-5", streamTuple.out.toString().trim());
    }
}
