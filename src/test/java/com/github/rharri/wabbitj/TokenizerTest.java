package com.github.rharri.wabbitj;

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

        var tokenizer = Tokenizer.newInstance("print 42;");
        tokenizer.tokenize();
        List<Token> tokens = tokenizer.getTokens();

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

        var tokenizer = Tokenizer.newInstance(programText);
        tokenizer.tokenize();
        List<Token> tokens = tokenizer.getTokens();

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

        var tokenizer = Tokenizer.newInstance(programText);
        tokenizer.tokenize();
        List<Token> tokens = tokenizer.getTokens();

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

        var tokenizer = Tokenizer.newInstance(programText);
        tokenizer.tokenize();
        List<Token> tokens = tokenizer.getTokens();

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

        var tokenizer = Tokenizer.newInstance(programText);
        tokenizer.tokenize();
        List<Token> tokens = tokenizer.getTokens();

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

        var tokenizer = Tokenizer.newInstance(programText);
        tokenizer.tokenize();
        List<Token> tokens = tokenizer.getTokens();

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

        var tokenizer = Tokenizer.newInstance(programText);
        tokenizer.tokenize();
        List<Token> tokens = tokenizer.getTokens();

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
        var minusOp = new Token(TokenType.TIMES, "*", new Position(1, 9));
        var intLiteral2 = new Token(TokenType.INTEGER, "3", new Position(1, 11));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 12));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 13));

        var programText = "print 2 * 3;";

        var tokenizer = Tokenizer.newInstance(programText);
        tokenizer.tokenize();
        List<Token> tokens = tokenizer.getTokens();

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
    public void shouldTokenizeDivision() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral1 = new Token(TokenType.INTEGER, "6", new Position(1, 7));
        var minusOp = new Token(TokenType.DIVIDE, "/", new Position(1, 9));
        var intLiteral2 = new Token(TokenType.INTEGER, "2", new Position(1, 11));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 12));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 13));

        var programText = "print 6 / 2;";

        var tokenizer = Tokenizer.newInstance(programText);
        tokenizer.tokenize();
        List<Token> tokens = tokenizer.getTokens();

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
}
