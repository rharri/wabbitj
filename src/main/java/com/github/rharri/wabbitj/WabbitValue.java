package com.github.rharri.wabbitj;

public record WabbitValue<T>(WabbitType wabbitType, T value) {
}
