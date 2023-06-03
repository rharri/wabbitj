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

import com.github.rharri.wabbitj.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Parser {

    private final List<Token> tokens = new ArrayList<>();
    private int index;

    private Parser(List<Token> tokens) {
        assert tokens != null;

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
        Objects.requireNonNull(tokens);
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
        return new IntLiteral(value, token.position().line(), token.position().column());
    }

    private Expression parseFloatLiteral() {
        Token token = expect(TokenType.FLOAT);
        float value = Float.parseFloat(token.representation());
        return new FloatLiteral(value, token.position().line(), token.position().column());
    }

    private Expression parseExpression() {
        return parseSumTerm();
    }

    private Expression parseSumTerm() {
        Expression lhs = parseMulTerm();

        while (true) {
            Optional<Token> token = tryExpect(TokenType.PLUS, TokenType.MINUS);

            if (token.isEmpty())
                break;

            Expression rhs = parseMulTerm();

            switch (token.get().type()) {
                case PLUS -> lhs = new BinaryOp(Operator.PLUS,
                        lhs,
                        rhs,
                        token.get().position().line(),
                        token.get().position().column());
                case MINUS -> lhs = new BinaryOp(Operator.MINUS,
                        lhs,
                        rhs,
                        token.get().position().line(),
                        token.get().position().column());
            }
        }
        return lhs;
    }

    private Expression parseMulTerm() {
        Expression lhs = parseFactor();

        while (true) {
            Optional<Token> token = tryExpect(TokenType.TIMES, TokenType.DIVIDE);

            if (token.isEmpty())
                break;

            Expression rhs = parseFactor();

            switch (token.get().type()) {
                case TIMES -> lhs = new BinaryOp(Operator.TIMES,
                        lhs,
                        rhs,
                        token.get().position().line(),
                        token.get().position().column());
                case DIVIDE -> lhs = new BinaryOp(Operator.DIVIDE,
                        lhs,
                        rhs,
                        token.get().position().line(),
                        token.get().position().column());
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

    @Override
    public String toString() {
        return "Parser{" +
                "index=" + index +
                '}';
    }
}
