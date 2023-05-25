package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

import java.util.Optional;

public interface AbstractSyntaxTree {
    Optional<Object> accept(NodeVisitor visitor);
}
