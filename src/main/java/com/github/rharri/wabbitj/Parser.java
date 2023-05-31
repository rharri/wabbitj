package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Parser {

    private final List<Token> tokens = new ArrayList<>();
    private int index;

    private Parser(List<Token> tokens) {
        this.tokens.addAll(tokens);
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

    private Optional<Token> tryExpect(TokenType type) {
        if (peek(type))
            return Optional.of(expect(type));
        else
            return Optional.empty();
    }

    private Optional<Token> tryExpect(TokenType... types) {
        for (TokenType type : types) {
            Optional<Token> token = tryExpect(type);
            if (token.isPresent())
                return token;
        }
        return Optional.empty();
    }

    private boolean peek(TokenType type) {
        return tokens.get(index).type().equals(type);
    }

    public static Parser newInstance(List<Token> tokens) {
        return new Parser(tokens);
    }

    public AbstractSyntaxTree parse() {
        var statements = new Statements();

        while (!peek(TokenType.EOF)) {
            if (peek(TokenType.PRINT))
                statements.add(parsePrintStatement());
            else if (peek(TokenType.COMMENT))
                expect(TokenType.COMMENT);
        }

        return new Program(statements);
    }

    private Statement parsePrintStatement() {
        expect(TokenType.PRINT);
        Expression expression = parseExpression();
        expect(TokenType.SEMI);
        return new Print(expression);
    }

    private Expression parseFactor() {
        if (peek(TokenType.INTEGER))
            return parseIntLiteral();

        if (peek(TokenType.FLOAT))
            return parseFloatLiteral();

        if (peek(TokenType.MINUS) || peek(TokenType.PLUS))
            return parseUnary();

        if (peek(TokenType.LPAREN))
            return parseGrouping();

        Token token = tokens.get(index);
        throw new IllegalArgumentException("Parser error: Unexpected token " + token.type());
    }

    private Expression parseIntLiteral() {
        Token token = expect(TokenType.INTEGER);
        int value = Integer.parseInt(token.representation());
        return new IntLiteral(value);
    }

    private Expression parseFloatLiteral() {
        Token token = expect(TokenType.FLOAT);
        float value = Float.parseFloat(token.representation());
        return new FloatLiteral(value);
    }

    private Expression parseExpression() {
        return parseSumTerm();
    }

    private Expression parseSumTerm() {
        Expression lhs = parseMulTerm();
        Optional<Token> token = tryExpect(TokenType.PLUS, TokenType.MINUS);

        if (token.isPresent()) {
            Expression rhs = parseMulTerm();

            switch (token.get().type()) {
                case PLUS -> lhs = new BinaryOp(Operator.PLUS, lhs, rhs);
                case MINUS -> lhs = new BinaryOp(Operator.MINUS, lhs, rhs);
            }
        }
        return lhs;
    }

    private Expression parseMulTerm() {
        Expression lhs = parseFactor();
        Optional<Token> token = tryExpect(TokenType.TIMES, TokenType.DIVIDE);

        if (token.isPresent()) {
            Expression rhs = parseFactor();

            switch (token.get().type()) {
                case TIMES -> lhs = new BinaryOp(Operator.TIMES, lhs, rhs);
                case DIVIDE -> lhs = new BinaryOp(Operator.DIVIDE, lhs, rhs);
            }
        }
        return lhs;
    }

    private Expression parseUnary() {
        Optional<Token> token = tryExpect(TokenType.MINUS, TokenType.PLUS);

        Expression operand = parseExpression();

        if (token.isPresent()) {
            return switch (token.get().type()) {
                case MINUS -> new UnaryOp(Operator.MINUS, operand);
                case PLUS -> new UnaryOp(Operator.PLUS, operand);
                default -> throw new IllegalArgumentException("Unary operation not supported.");
            };
        }

        throw new IllegalArgumentException("Invalid token type.");
    }

    private Expression parseGrouping() {
        expect(TokenType.LPAREN);
        Expression expression = parseExpression();
        expect(TokenType.RPAREN);
        return new Grouping(expression);
    }
}
