package com.github.rharri.wabbitj;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.mockito.Mockito.*;

public class ParserTest {

    private JavaRuntime runtimeWithoutStandardOut() {
        // An OutputStream that "writes" bytes to a byte[]
        var streamToByteArray = new ByteArrayOutputStream();

        // Add printing functionality to the byte[] output stream
        var printableByteArrayStream = new PrintStream(streamToByteArray);

        // Use a printable byte[] stream instead of "standard" output stream
        return JavaRuntime.newInstance(printableByteArrayStream);
    }

    @Test
    public void shouldParsePrintIntLiteral() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral = new Token(TokenType.INTEGER, "42", new Position(1, 7));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 9));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 10));

        var tokens = List.of(print, intLiteral, semicolon, endOfFile);

        var parser = Parser.newInstance(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(Interpreter.newInstance(runtime));

        ast.accept(interpreter);

        InOrder inOrder = inOrder(interpreter);
        inOrder.verify(interpreter).visitProgram(Mockito.any());
        inOrder.verify(interpreter).visitStatements(Mockito.any());
        inOrder.verify(interpreter).visitPrint(Mockito.any());
        inOrder.verify(interpreter).visitIntLiteral(Mockito.any());
    }

    @Test
    public void shouldParsePrintFloatLiteral() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var floatLiteral = new Token(TokenType.FLOAT, "1.5", new Position(1, 7));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 10));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 11));

        var tokens = List.of(print, floatLiteral, semicolon, endOfFile);

        var parser = Parser.newInstance(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(Interpreter.newInstance(runtime));

        ast.accept(interpreter);

        InOrder inOrder = inOrder(interpreter);
        inOrder.verify(interpreter).visitProgram(Mockito.any());
        inOrder.verify(interpreter).visitStatements(Mockito.any());
        inOrder.verify(interpreter).visitPrint(Mockito.any());
        inOrder.verify(interpreter).visitFloatLiteral(Mockito.any());
    }

    @Test
    public void shouldParsePrintAdditionExpression() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral1 = new Token(TokenType.INTEGER, "2", new Position(1, 7));
        var plusOp = new Token(TokenType.PLUS, "+", new Position(1, 9));
        var intLiteral2 = new Token(TokenType.INTEGER, "3", new Position(1, 11));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 12));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 13));

        var tokens = List.of(
                print,
                intLiteral1,
                plusOp,
                intLiteral2,
                semicolon,
                endOfFile
        );

        var parser = Parser.newInstance(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(Interpreter.newInstance(runtime));

        ast.accept(interpreter);

        InOrder inOrder = inOrder(interpreter);
        inOrder.verify(interpreter).visitProgram(Mockito.any());
        inOrder.verify(interpreter).visitStatements(Mockito.any());
        inOrder.verify(interpreter).visitPrint(Mockito.any());
        inOrder.verify(interpreter).visitBinaryOp(Mockito.any());
        inOrder.verify(interpreter, times(2)).visitIntLiteral(Mockito.any());
    }

    @Test
    public void shouldParsePrintSubtractionExpression() {
        var tokenizer = Tokenizer.newInstance("print 46 - 4;");
        tokenizer.tokenize();
        var tokens = tokenizer.getTokens();

        var parser = Parser.newInstance(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(Interpreter.newInstance(runtime));

        ast.accept(interpreter);

        InOrder inOrder = inOrder(interpreter);
        inOrder.verify(interpreter).visitProgram(Mockito.any());
        inOrder.verify(interpreter).visitStatements(Mockito.any());
        inOrder.verify(interpreter).visitPrint(Mockito.any());
        inOrder.verify(interpreter).visitBinaryOp(Mockito.any());
        inOrder.verify(interpreter, times(2)).visitIntLiteral(Mockito.any());
    }

    @Test
    public void shouldParsePrintMultiplicationExpression() {
        var tokenizer = Tokenizer.newInstance("print 2 * 3;");
        tokenizer.tokenize();
        var tokens = tokenizer.getTokens();

        var parser = Parser.newInstance(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(Interpreter.newInstance(runtime));

        ast.accept(interpreter);

        InOrder inOrder = inOrder(interpreter);
        inOrder.verify(interpreter).visitProgram(Mockito.any());
        inOrder.verify(interpreter).visitStatements(Mockito.any());
        inOrder.verify(interpreter).visitPrint(Mockito.any());
        inOrder.verify(interpreter).visitBinaryOp(Mockito.any());
        inOrder.verify(interpreter, times(2)).visitIntLiteral(Mockito.any());
    }

    @Test
    public void shouldParsePrintDivisionExpression() {
        var tokenizer = Tokenizer.newInstance("print 6 / 2;");
        tokenizer.tokenize();
        var tokens = tokenizer.getTokens();

        var parser = Parser.newInstance(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(Interpreter.newInstance(runtime));

        ast.accept(interpreter);

        InOrder inOrder = inOrder(interpreter);
        inOrder.verify(interpreter).visitProgram(Mockito.any());
        inOrder.verify(interpreter).visitStatements(Mockito.any());
        inOrder.verify(interpreter).visitPrint(Mockito.any());
        inOrder.verify(interpreter).visitBinaryOp(Mockito.any());
        inOrder.verify(interpreter, times(2)).visitIntLiteral(Mockito.any());
    }

    @Test
    public void shouldParsePrintUnaryExpression() {
        var tokenizer = Tokenizer.newInstance("print -5;");
        tokenizer.tokenize();
        var tokens = tokenizer.getTokens();

        var parser = Parser.newInstance(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(Interpreter.newInstance(runtime));

        ast.accept(interpreter);

        InOrder inOrder = inOrder(interpreter);
        inOrder.verify(interpreter).visitProgram(Mockito.any());
        inOrder.verify(interpreter).visitStatements(Mockito.any());
        inOrder.verify(interpreter).visitPrint(Mockito.any());
        inOrder.verify(interpreter).visitUnaryOp(Mockito.any());
        inOrder.verify(interpreter).visitIntLiteral(Mockito.any());
    }

    @Test
    public void shouldParsePrintTrinomialExpression() {
        var tokenizer = Tokenizer.newInstance("print 2 + 3 * 4;");
        tokenizer.tokenize();
        var tokens = tokenizer.getTokens();

        var parser = Parser.newInstance(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(Interpreter.newInstance(runtime));

        ast.accept(interpreter);

        InOrder inOrder = inOrder(interpreter);
        inOrder.verify(interpreter).visitProgram(Mockito.any());
        inOrder.verify(interpreter).visitStatements(Mockito.any());
        inOrder.verify(interpreter).visitPrint(Mockito.any());
        inOrder.verify(interpreter).visitBinaryOp(Mockito.any());
        inOrder.verify(interpreter).visitIntLiteral(Mockito.any());
        inOrder.verify(interpreter).visitBinaryOp(Mockito.any());
        inOrder.verify(interpreter, times(2)).visitIntLiteral(Mockito.any());
    }

    @Test
    public void shouldParsePrintGrouping() {
        var tokenizer = Tokenizer.newInstance("print (2 + 3) * 4;");
        tokenizer.tokenize();
        var tokens = tokenizer.getTokens();

        var parser = Parser.newInstance(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(Interpreter.newInstance(runtime));

        ast.accept(interpreter);

        InOrder inOrder = inOrder(interpreter);
        inOrder.verify(interpreter).visitProgram(Mockito.any());
        inOrder.verify(interpreter).visitStatements(Mockito.any());
        inOrder.verify(interpreter).visitPrint(Mockito.any());
        inOrder.verify(interpreter).visitBinaryOp(Mockito.any());
        inOrder.verify(interpreter).visitGrouping(Mockito.any());
        inOrder.verify(interpreter).visitBinaryOp(Mockito.any());
        inOrder.verify(interpreter, times(3)).visitIntLiteral(Mockito.any());
    }
}
