package com.github.rharri.wabbitj;

public record Token(TokenType type, String representation, Position position) {
}
