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

import com.github.rharri.wabbitj.interpreter.Interpreter;
import com.github.rharri.wabbitj.interpreter.JavaRuntime;
import com.github.rharri.wabbitj.tokenizer.Position;
import com.github.rharri.wabbitj.tokenizer.Token;
import com.github.rharri.wabbitj.tokenizer.TokenType;
import com.github.rharri.wabbitj.tokenizer.Tokenizer;
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
        return new JavaRuntime(printableByteArrayStream);
    }

    @Test
    public void shouldParsePrintIntLiteral() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral = new Token(TokenType.INTEGER, "42", new Position(1, 7));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 9));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 10));

        var tokens = List.of(print, intLiteral, semicolon, endOfFile);

        var parser = new Parser(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(new Interpreter(runtime));

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

        var parser = new Parser(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(new Interpreter(runtime));

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

        var parser = new Parser(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(new Interpreter(runtime));

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
        var tokenizer = new Tokenizer("print 46 - 4;");
        tokenizer.tokenize();
        var tokens = tokenizer.getTokens();

        var parser = new Parser(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(new Interpreter(runtime));

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
        var tokenizer = new Tokenizer("print 2 * 3;");
        tokenizer.tokenize();
        var tokens = tokenizer.getTokens();

        var parser = new Parser(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(new Interpreter(runtime));

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
        var tokenizer = new Tokenizer("print 6 / 2;");
        tokenizer.tokenize();
        var tokens = tokenizer.getTokens();

        var parser = new Parser(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(new Interpreter(runtime));

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
        var tokenizer = new Tokenizer("print -5;");
        tokenizer.tokenize();
        var tokens = tokenizer.getTokens();

        var parser = new Parser(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(new Interpreter(runtime));

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
        var tokenizer = new Tokenizer("print 2 + 3 * 4;");
        tokenizer.tokenize();
        var tokens = tokenizer.getTokens();

        var parser = new Parser(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(new Interpreter(runtime));

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
        var tokenizer = new Tokenizer("print (2 + 3) * 4;");
        tokenizer.tokenize();
        var tokens = tokenizer.getTokens();

        var parser = new Parser(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(new Interpreter(runtime));

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

    @Test
    public void shouldParsePolynomial() {
        var tokenizer = new Tokenizer("print 6 + 7 + 8 + 9 + 10;");
        tokenizer.tokenize();
        var tokens = tokenizer.getTokens();

        var parser = new Parser(tokens);
        var ast = parser.parse();

        var runtime = runtimeWithoutStandardOut();

        var interpreter = Mockito.spy(new Interpreter(runtime));

        ast.accept(interpreter);

        InOrder inOrder = inOrder(interpreter);
        inOrder.verify(interpreter).visitProgram(Mockito.any());
        inOrder.verify(interpreter).visitStatements(Mockito.any());
        inOrder.verify(interpreter).visitPrint(Mockito.any());
        inOrder.verify(interpreter, times(4)).visitBinaryOp(Mockito.any());
        inOrder.verify(interpreter, times(5)).visitIntLiteral(Mockito.any());
    }
}
