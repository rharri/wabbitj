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

package com.github.rharri.wabbitj.tokenizer;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class Tokenizer {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("print", TokenType.PRINT);
    }

    private static final Predicate<Character> isDigit = Character::isDigit;
    private static final Predicate<Character> isAlpha = Character::isAlphabetic;
    private static final Predicate<Character> isDecimalPoint = Tokenizer::isDecimalPoint;
    private static final Predicate<Character> isFloatingPoint = isDigit.or(isDecimalPoint);

    private static boolean isDecimalPoint(Character ch) {
        return ch == '.';
    }

    private final String programText;
    private final char[] programTextChars;
    private int index;
    private int lineNumber;
    private int column;
    private final List<Token> tokens;
    private int lastNewLineIndex;

    public Tokenizer(String programText) {
        Objects.requireNonNull(programText);

        if (programText.isEmpty() || programText.isBlank())
            throw new IllegalArgumentException("programText cannot be empty or blank.");

        this.programText = programText;
        this.programTextChars = programText.toCharArray();
        this.index = 0;
        this.lineNumber = 1;
        this.column = 1;
        this.tokens = new ArrayList<>();
        this.lastNewLineIndex = 0;
    }

    public static List<Token> tokenize(String programText) {
        Tokenizer tokenizer = new Tokenizer(programText);
        tokenizer.tokenize();
        return tokenizer.getTokens();
    }

    private boolean peek(String token) {
        assert token != null;
        assert !token.isEmpty();
        return slice(index, index + token.length()).equals(token);
    }

    private boolean tryNext(Predicate<Character> predicate) {
        assert predicate != null;
        return predicate.test(programText.charAt(index));
    }

    private String slice(int startIndex, int endIndex) {
        assert startIndex >= 0;
        assert endIndex >= 0;

        if (endIndex > programText.length())
            endIndex = programText.length() - 1;

        return programText.substring(startIndex, endIndex);
    }

    private FindEndResult findEnd(int start, String endToken) {
        assert start >= 0;
        assert !endToken.isEmpty();

        int endTokenIndex = programText.indexOf(endToken, start);

        if (endTokenIndex == -1) { // Handle endTokens that do not exist
            endTokenIndex = programText.length();
            return new FindEndResult(start, endTokenIndex, slice(start, endTokenIndex));
        } else {
            // If the endToken length is greater than 1, add it to the endTokenIndex
            // to get the index where the token actually ends

            // Example:
            //
            // 0123456
            // /* x */
            //      ^---- indexOf returns 5
            //
            // However, to properly capture the token and continue processing, the index needs to be 6
            //
            if (endToken.length() > 1)
                endTokenIndex += endToken.length();

            return new FindEndResult(start, endTokenIndex, slice(start, endTokenIndex));
        }
    }

    private FindEndResult findEnd(int start,
                                  Predicate<Character> predicate,
                                  BiFunction<Integer, Predicate<Character>, Integer> finder) {

        assert start >= 0;
        assert predicate != null;
        assert finder != null;

        int endTokenIndex = finder.apply(start, predicate);
        return new FindEndResult(start, endTokenIndex, slice(start, endTokenIndex));
    }

    private void addToken(TokenType type, int start, String representation) {
        assert start >= 0;
        assert !representation.isEmpty();

        if (lastNewLineIndex > 0)
            column = start - lastNewLineIndex;
        else
            column = start + 1;

        Token token = new Token(type, representation, new Position(lineNumber, column));
        tokens.add(token);
    }

    public List<Token> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    private int find(int start, Predicate<Character> predicate) {
        assert start >= 0;
        assert predicate != null;

        int endIndex = start;
        int index = start;

        while (index < programText.length()) {
            if (predicate.test(programText.charAt(index))) {
                endIndex += 1;
                index += 1;
            } else {
                break;
            }
        }
        return endIndex;
    }

    private boolean isDecimalInExpression(int start) {
        assert start >= 0;

        int decimalIndex = programText.indexOf(".", start);

        if (decimalIndex < 0)
            return false;

        int endOfNumberIndex = programText.indexOf(" ", start);

        if (endOfNumberIndex < 0)
            endOfNumberIndex = programText.indexOf(";", start);

        return decimalIndex < endOfNumberIndex;
    }

    public void tokenize() {
        while (index < programTextChars.length) {
            if (peek("/*")) {
                FindEndResult endToken = findEnd(index, "*/");
                addToken(TokenType.COMMENT, endToken.startIndex, endToken.found);
                index = endToken.endIndex;
                // Handle newlines within multiline comments
                lineNumber += (int) endToken.found.chars().filter(ch -> ch == '\n').count();
            } else if (peek("//")) {
                FindEndResult endToken = findEnd(index, "\n");
                addToken(TokenType.COMMENT, endToken.startIndex, endToken.found);
                index = endToken.endIndex;
            } else if (tryNext(isAlpha)) {
                FindEndResult nameOrKeyword = findEnd(index, isAlpha, this::find);
                TokenType type = keywords.getOrDefault(nameOrKeyword.found, TokenType.NAME);
                addToken(type, nameOrKeyword.startIndex, nameOrKeyword.found);
                index = nameOrKeyword.endIndex;
            } else if (tryNext(isDigit) || peek(".")) {
                boolean decimalInExpression = isDecimalInExpression(index);
                if (!decimalInExpression) {
                    FindEndResult integer = findEnd(index, isDigit, this::find);
                    addToken(TokenType.INTEGER, integer.startIndex, integer.found);
                    index = integer.endIndex;
                } else {
                    FindEndResult floatingPoint = findEnd(index, isFloatingPoint, this::find);
                    addToken(TokenType.FLOAT, floatingPoint.startIndex, floatingPoint.found);
                    index = floatingPoint.endIndex;
                }
            } else if (peek("+")) {
                addToken(TokenType.PLUS, index, "+");
                index += 1;
            } else if (peek("-")) {
                addToken(TokenType.MINUS, index, "-");
                index += 1;
            } else if (peek("*")) {
                addToken(TokenType.TIMES, index, "*");
                index += 1;
            } else if (peek("/")) {
                addToken(TokenType.DIVIDE, index, "/");
                index += 1;
            } else if (peek("(")) {
                addToken(TokenType.LPAREN, index, "(");
                index += 1;
            } else if (peek(")")) {
                addToken(TokenType.RPAREN, index, ")");
                index += 1;
            } else if (peek(";")) {
                addToken(TokenType.SEMI, index, ";");
                index += 1;
            } else if (peek("\n")) {
                lineNumber += 1;
                lastNewLineIndex = index;
                index += 1;
            } else {
                index += 1;
            }
        }
        addToken(TokenType.EOF, programText.length(), "EOF");
    }

    @Override
    public String toString() {
        return "Tokenizer{" +
                "index=" + index +
                ", lineNumber=" + lineNumber +
                ", column=" + column +
                '}';
    }

    private record FindEndResult(int startIndex, int endIndex, String found) {

        public FindEndResult {
            if (startIndex < 0)
                throw new IllegalArgumentException("startIndex must be >= 0.");

            if (endIndex < 0)
                throw new IllegalArgumentException("endIndex must be >= 0.");

            Objects.requireNonNull(found);

            if (found.isEmpty() || found.isBlank())
                throw new IllegalArgumentException("found cannot be empty or blank.");
        }
    }
}
