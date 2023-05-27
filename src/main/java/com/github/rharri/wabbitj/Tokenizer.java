package com.github.rharri.wabbitj;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class Tokenizer {
    private final String programText;
    private final char[] programTextChars;
    private int index;
    private final int lineNumber;
    private final int column;
    private final List<Token> tokens;
    private final int lastNewLineIndex;
    private final Map<String, TokenType> keywords;

    private final Predicate<Character> isDigit = Character::isDigit;

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
        if (endIndex > programText.length()) {
            endIndex = programText.length() - 1;
        }
        return programText.substring(startIndex, endIndex);
    }

    private FindEndResult findEnd(int start, String endToken) {
        int endTokenIndex = programText.indexOf(endToken, start);
        endTokenIndex += endToken.length();
        return new FindEndResult(start, endTokenIndex, slice(start, endTokenIndex));
    }

    private FindEndResult findEnd(int start,
                                  Predicate<Character> predicate,
                                  BiFunction<Integer, Predicate<Character>, Integer> finder) {

        int endTokenIndex = finder.apply(start, predicate);
        return new FindEndResult(start, endTokenIndex, slice(start, endTokenIndex));
    }

    private void addToken(TokenType type, int start, String representation) {
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

    public void tokenize() {
        while (index < programTextChars.length) {
            if (peek("/*")) {
                FindEndResult endToken = findEnd(index, "*/");
                addToken(TokenType.COMMENT, endToken.startIndex, endToken.found);
                index = endToken.endIndex;
            } else if (tryNext(isDigit)) {
                FindEndResult integer = findEnd(index, isDigit, this::find);
                addToken(TokenType.INTEGER, integer.startIndex, integer.found);
                index = integer.endIndex;
            } else if (peek(";")) {
                addToken(TokenType.SEMI, index, ";");
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
