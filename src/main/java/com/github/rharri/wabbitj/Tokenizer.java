package com.github.rharri.wabbitj;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class Tokenizer {
    private final String programText;
    private final char[] programTextChars;
    private int index;
    private int lineNumber;
    private int column;
    private final List<Token> tokens;
    private int lastNewLineIndex;
    private final Map<String, TokenType> keywords;

    private final Predicate<Character> isDigit = Character::isDigit;
    private final Predicate<Character> isAlpha = Character::isAlphabetic;
    private final Predicate<Character> isDecimalPoint = ch -> ch == '.';
    private final Predicate<Character> isFloatingPoint = isDigit.or(isDecimalPoint);

    private Tokenizer(String programText) {
        this.programText = programText;
        this.programTextChars = programText.toCharArray();
        this.index = 0;
        this.lineNumber = 1;
        this.column = 1;
        this.tokens = new ArrayList<>();
        this.lastNewLineIndex = 0;

        keywords = new HashMap<>();
        keywords.put("print", TokenType.PRINT);
    }

    public static Tokenizer newInstance(String programText) {
        return new Tokenizer(programText);
    }

    private boolean peek(String token) {
        return slice(index, index + token.length()).equals(token);
    }

    private boolean tryNext(Predicate<Character> predicate) {
        return predicate.test(programText.charAt(index));
    }

    private String slice(int startIndex, int endIndex) {
        if (endIndex > programText.length())
            endIndex = programText.length() - 1;

        return programText.substring(startIndex, endIndex);
    }

    private FindEndResult findEnd(int start, String endToken) {
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

        int endTokenIndex = finder.apply(start, predicate);
        return new FindEndResult(start, endTokenIndex, slice(start, endTokenIndex));
    }

    private void addToken(TokenType type, int start, String representation) {
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
                lineNumber += endToken.found.chars().filter(ch -> ch == '\n').count();
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

    private record FindEndResult(int startIndex, int endIndex, String found) {
    }
}
