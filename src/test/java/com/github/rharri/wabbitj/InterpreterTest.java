package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.IntLiteral;
import com.github.rharri.wabbitj.ast.Print;
import com.github.rharri.wabbitj.ast.Program;
import com.github.rharri.wabbitj.ast.Statements;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {

    @Test
    public void shouldPrintIntLiteral() {
        var intLiteral = IntLiteral.newInstance(42);
        var print = Print.newInstance(intLiteral);
        var statements = Statements.newInstance();
        statements.add(print);
        var program = Program.newInstance(statements);

        // An OutputStream that "writes" bytes to a byte[]
        var streamToByteArray = new ByteArrayOutputStream();

        // Add printing functionality to the byte[] output stream
        var printableByteArrayStream = new PrintStream(streamToByteArray);

        // Use a printable byte[] stream instead of "standard" output stream
        var runtime = JavaRuntime.newInstance(printableByteArrayStream);

        program.accept(Interpreter.newInstance(runtime));

        // Assert on the underlying byte[] stream
        assertEquals("42", streamToByteArray.toString().trim());
    }
}
