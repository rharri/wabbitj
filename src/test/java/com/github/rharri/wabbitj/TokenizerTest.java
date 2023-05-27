package com.github.rharri.wabbitj;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TokenizerTest {

    @Test
    public void shouldTokenizeBasicProgram() {
        var print = new Token(TokenType.PRINT, "print", new Position(1, 1));
        var intLiteral = new Token(TokenType.INTEGER, "42", new Position(1, 7));
        var semicolon = new Token(TokenType.SEMI, ";", new Position(1, 9));
        var endOfFile = new Token(TokenType.EOF, "EOF", new Position(1, 9));

        var tokenizer = Tokenizer.newInstance("print 42;");
        tokenizer.tokenize();
        List<Token> tokens = tokenizer.getTokens();

        var expected = List.of(print, intLiteral, semicolon, endOfFile);

        assertTrue(expected.containsAll(tokens));
    }
}
