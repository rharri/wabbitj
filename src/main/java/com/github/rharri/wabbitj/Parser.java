package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.*;

import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int index;

    private Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.index = 0;
    }

    private Token expect(TokenType type) {
        Token token = tokens.get(index);

        if (token.type().equals(type)) {
            index += 1;
            return token;
        } else {
            throw new IllegalArgumentException("Expected " + type + "." + " Got " + token.type() + ".");
        }
    }

    private boolean peek(TokenType type) {
        return tokens.get(index).type().equals(type);
    }

    public static Parser newInstance(List<Token> tokens) {
        return new Parser(tokens);
    }

    public AbstractSyntaxTree parse() {
        var statements = Statements.newInstance();

        while (!peek(TokenType.EOF)) {
            if (peek(TokenType.PRINT))
                statements.add(parsePrintStatement());
            else if (peek(TokenType.COMMENT))
                expect(TokenType.COMMENT);
        }

        return Program.newInstance(statements);
    }

    private Statement parsePrintStatement() {
        expect(TokenType.PRINT);
        Expression expression = parseFactor();
        expect(TokenType.SEMI);
        return Print.newInstance(expression);
    }

    private Expression parseFactor() {
        if (peek(TokenType.INTEGER))
            return parseIntLiteral();
        return null;
    }

    private Expression parseIntLiteral() {
        Token token = expect(TokenType.INTEGER);
        int digits = Integer.parseInt(token.representation());
        return IntLiteral.newInstance(digits);
    }
}
