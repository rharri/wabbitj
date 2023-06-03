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

import com.github.rharri.wabbitj.tokenizer.Position;
import com.github.rharri.wabbitj.tokenizer.Token;
import com.github.rharri.wabbitj.tokenizer.TokenType;
import com.github.rharri.wabbitj.tokenizer.Tokenizer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenizerTest {

    @Test
    public void shouldTokenizePrintIntLiteral() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral = new Token(TokenType.INTEGER, "42", new Position(1, 7));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 9));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 10));

        List<Token> tokens = Tokenizer.tokenize("print 42;");

        var expected = List.of(print, intLiteral, semicolon, endOfFile);

        assertThat(tokens).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void shouldTokenizeMultilineComments() {
        var multilineCommentText = """
                /*
                    Comment
                
                    More comments
                */""";

        var multilineComment = new Token(TokenType.COMMENT, multilineCommentText, new Position(1, 1));
        var print = new Token(TokenType.PRINT, "print", new Position(7, 1));
        var intLiteral = new Token(TokenType.INTEGER, "42", new Position(7, 7));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(7, 9));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(8, 1));

        var programText = """
                /*
                    Comment
                
                    More comments
                */
                
                print 42;
                """;

        List<Token> tokens = Tokenizer.tokenize(programText);

        var expected = List.of(multilineComment, print, intLiteral, semicolon, endOfFile);

        assertThat(tokens).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void shouldTokenizeSingleLineComments() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral = new Token(TokenType.INTEGER, "42", new Position(1, 7));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 9));
        var comment1 = new Token(TokenType.COMMENT, "// comment 1", new Position(1, 11));
        var comment2 = new Token(TokenType.COMMENT, "// comment 2", new Position(3, 1));
        var print2 = new Token(TokenType.PRINT, "print", new Position(4, 1));
        var intLiteral2 = new Token(TokenType.INTEGER, "34", new Position(4, 7));
        var semicolon2 = new Token(TokenType.SEMI, ";", new Position(4, 9));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(5, 1));

        var programText = """
                print 42; // comment 1
                
                // comment 2
                print 34;
                """;

        List<Token> tokens = Tokenizer.tokenize(programText);

        var expected = List.of(print, intLiteral, semicolon, comment1, comment2, print2, intLiteral2, semicolon2, endOfFile);

        assertThat(tokens).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void shouldTokenizeNumbers() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral = new Token(TokenType.INTEGER, "42", new Position(1, 7));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 9));
        var print2 = new Token(TokenType.PRINT, "print", new Position(2, 1));
        var floatLiteral1 = new Token(TokenType.FLOAT, "2.3", new Position(2, 7));
        var semicolon2 = new Token(TokenType.SEMI, ";", new Position(2, 10));
        var print3 = new Token(TokenType.PRINT, "print", new Position(3, 1));
        var floatLiteral2 = new Token(TokenType.FLOAT, ".15", new Position(3, 7));
        var semicolon3 = new Token(TokenType.SEMI, ";", new Position(3, 10));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(4, 1));

        var programText = """
                print 42;
                print 2.3;
                print .15;
                """;

        List<Token> tokens = Tokenizer.tokenize(programText);

        var expected = List.of(
                print,
                intLiteral,
                semicolon,
                print2,
                floatLiteral1,
                semicolon2,
                print3,
                floatLiteral2,
                semicolon3,
                endOfFile
        );

        assertThat(tokens).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void shouldTokenizeSingleLineCommentWithNoNewline() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral = new Token(TokenType.INTEGER, "42", new Position(1, 7));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 9));
        var comment = new Token(TokenType.COMMENT, "// comment", new Position(1, 11));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 21));

        var programText = "print 42; // comment";

        List<Token> tokens = Tokenizer.tokenize(programText);

        var expected = List.of(
                print,
                intLiteral,
                semicolon,
                comment,
                endOfFile
        );

        assertThat(tokens).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void shouldTokenizeAddition() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral1 = new Token(TokenType.INTEGER, "2", new Position(1, 7));
        var plusOp = new Token(TokenType.PLUS, "+", new Position(1, 9));
        var intLiteral2 = new Token(TokenType.INTEGER, "3", new Position(1, 11));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 12));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 13));

        var programText = "print 2 + 3;";

        List<Token> tokens = Tokenizer.tokenize(programText);

        var expected = List.of(
                print,
                intLiteral1,
                plusOp,
                intLiteral2,
                semicolon,
                endOfFile
        );

        assertThat(tokens).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void shouldTokenizeSubtraction() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral1 = new Token(TokenType.INTEGER, "46", new Position(1, 7));
        var minusOp = new Token(TokenType.MINUS, "-", new Position(1, 10));
        var intLiteral2 = new Token(TokenType.INTEGER, "4", new Position(1, 12));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 13));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 14));

        var programText = "print 46 - 4;";

        List<Token> tokens = Tokenizer.tokenize(programText);

        var expected = List.of(
                print,
                intLiteral1,
                minusOp,
                intLiteral2,
                semicolon,
                endOfFile
        );

        assertThat(tokens).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void shouldTokenizeMultiplication() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral1 = new Token(TokenType.INTEGER, "2", new Position(1, 7));
        var multiplicationOp = new Token(TokenType.TIMES, "*", new Position(1, 9));
        var intLiteral2 = new Token(TokenType.INTEGER, "3", new Position(1, 11));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 12));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 13));

        var programText = "print 2 * 3;";

        List<Token> tokens = Tokenizer.tokenize(programText);

        var expected = List.of(
                print,
                intLiteral1,
                multiplicationOp,
                intLiteral2,
                semicolon,
                endOfFile
        );

        assertThat(tokens).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void shouldTokenizeDivision() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral1 = new Token(TokenType.INTEGER, "6", new Position(1, 7));
        var divisionOp = new Token(TokenType.DIVIDE, "/", new Position(1, 9));
        var intLiteral2 = new Token(TokenType.INTEGER, "2", new Position(1, 11));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 12));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 13));

        var programText = "print 6 / 2;";

        List<Token> tokens = Tokenizer.tokenize(programText);

        var expected = List.of(
                print,
                intLiteral1,
                divisionOp,
                intLiteral2,
                semicolon,
                endOfFile
        );

        assertThat(tokens).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void shouldTokenizeUnary() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var minusOp = new Token(TokenType.MINUS, "-", new Position(1, 7));
        var intLiteral = new Token(TokenType.INTEGER, "5", new Position(1, 8));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 9));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 10));

        var programText = "print -5;";

        List<Token> tokens = Tokenizer.tokenize(programText);

        var expected = List.of(
                print,
                minusOp,
                intLiteral,
                semicolon,
                endOfFile
        );

        assertThat(tokens).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void shouldTokenizeTrinomial() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral1 = new Token(TokenType.INTEGER, "2", new Position(1, 7));
        var plusOp = new Token(TokenType.PLUS, "+", new Position(1, 9));
        var intLiteral2 = new Token(TokenType.INTEGER, "3", new Position(1, 11));
        var multiplicationOp = new Token(TokenType.TIMES, "*", new Position(1, 13));
        var intLiteral3 = new Token(TokenType.INTEGER, "4", new Position(1, 15));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 16));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 17));

        var programText = "print 2 + 3 * 4;";

        List<Token> tokens = Tokenizer.tokenize(programText);

        var expected = List.of(
                print,
                intLiteral1,
                plusOp,
                intLiteral2,
                multiplicationOp,
                intLiteral3,
                semicolon,
                endOfFile
        );

        assertThat(tokens).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void shouldTokenizeGrouping() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var lparen = new Token(TokenType.LPAREN, "(", new Position(1, 7));
        var intLiteral1 = new Token(TokenType.INTEGER, "2", new Position(1, 8));
        var plusOp = new Token(TokenType.PLUS, "+", new Position(1, 10));
        var intLiteral2 = new Token(TokenType.INTEGER, "3", new Position(1, 12));
        var rparen = new Token(TokenType.RPAREN, ")", new Position(1, 13));
        var multiplicationOp = new Token(TokenType.TIMES, "*", new Position(1, 15));
        var intLiteral3 = new Token(TokenType.INTEGER, "4", new Position(1, 17));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 18));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 19));

        var programText = "print (2 + 3) * 4;";

        List<Token> tokens = Tokenizer.tokenize(programText);

        var expected = List.of(
                print,
                lparen,
                intLiteral1,
                plusOp,
                intLiteral2,
                rparen,
                multiplicationOp,
                intLiteral3,
                semicolon,
                endOfFile
        );

        assertThat(tokens).containsOnlyOnceElementsOf(expected);
    }

    @Test
    public void shouldTokenizeBinaryOpWithMixedTypes() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral = new Token(TokenType.INTEGER, "2", new Position(1, 7));
        var plusOp = new Token(TokenType.PLUS, "+", new Position(1, 9));
        var floatLiteral = new Token(TokenType.FLOAT, "3.5", new Position(1, 11));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 14));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 15));

        var programText = "print 2 + 3.5;";

        List<Token> tokens = Tokenizer.tokenize(programText);

        var expected = List.of(
                print,
                intLiteral,
                plusOp,
                floatLiteral,
                semicolon,
                endOfFile
        );

        assertThat(tokens).containsOnlyOnceElementsOf(expected);
    }
}
