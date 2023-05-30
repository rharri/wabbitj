package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public interface AbstractSyntaxTree {
    void accept(NodeVisitor visitor);
}
