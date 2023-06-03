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
import com.github.rharri.wabbitj.tokenizer.Token;
import com.github.rharri.wabbitj.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Parser {

    private final List<Token> tokens = new ArrayList<>();
    private int index;

    public Parser(List<Token> tokens) {
        Objects.requireNonNull(tokens);

        this.tokens.addAll(tokens);
        this.index = 0;
    }

    private Token expect(TokenType type) {
        Token token = tokens.get(index);

        if (token.type().equals(type)) {
            index += 1;
            return token;
        }

        throw new IllegalArgumentException("Expected " + type + "." + " Got " + token.type() + ".");
    }

    private Token tryExpect(TokenType type) {
        return peek(type) ? expect(type) : Token.NO_SUCH_TOKEN;
    }

    private Token tryExpect(TokenType type1, TokenType type2) {
        assert type1 != type2;

        Token token = tryExpect(type1);
        return !Objects.equals(token, Token.NO_SUCH_TOKEN) ? token : tryExpect(type2);
    }

    private boolean peek(TokenType type) {
        return tokens.get(index).type().equals(type);
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
        throw new IllegalArgumentException("Parser error: Unexpected token " + token.type() + ".");
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
            Token token = tryExpect(TokenType.PLUS, TokenType.MINUS);

            if (Objects.equals(token, Token.NO_SUCH_TOKEN))
                break;

            Expression rhs = parseMulTerm();

            lhs = switch (token.type()) {
                case PLUS -> new BinaryOp(Operator.PLUS,
                        lhs,
                        rhs,
                        token.position().line(),
                        token.position().column());
                case MINUS -> new BinaryOp(Operator.MINUS,
                        lhs,
                        rhs,
                        token.position().line(),
                        token.position().column());
                default -> throw new IllegalArgumentException("Binary operation not supported.");
            };
        }
        return lhs;
    }

    private Expression parseMulTerm() {
        Expression lhs = parseFactor();

        while (true) {
            Token token = tryExpect(TokenType.TIMES, TokenType.DIVIDE);

            if (Objects.equals(token, Token.NO_SUCH_TOKEN))
                break;

            Expression rhs = parseFactor();

            lhs = switch (token.type()) {
                case TIMES -> new BinaryOp(Operator.TIMES,
                        lhs,
                        rhs,
                        token.position().line(),
                        token.position().column());
                case DIVIDE -> new BinaryOp(Operator.DIVIDE,
                        lhs,
                        rhs,
                        token.position().line(),
                        token.position().column());
                default -> throw new IllegalArgumentException("Binary operation not supported.");
            };
        }
        return lhs;
    }

    private Expression parseUnary() {
        Token token = tryExpect(TokenType.MINUS, TokenType.PLUS);

        if (Objects.equals(token, Token.NO_SUCH_TOKEN))
            throw new IllegalArgumentException("Expected MINUS or PLUS.");

        Expression operand = parseExpression();

        return switch (token.type()) {
            case MINUS -> new UnaryOp(Operator.MINUS, operand);
            case PLUS -> new UnaryOp(Operator.PLUS, operand);
            default -> throw new IllegalArgumentException("Unary operation not supported.");
        };
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
