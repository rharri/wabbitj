package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.*;

import java.util.Optional;

public interface NodeVisitor {
    Optional<Object> visitProgram(Program program);
    Optional<Object> visitStatements(Statements statements);
    Optional<Object> visitPrint(Print print);
    Optional<Object> visitIntLiteral(IntLiteral intLiteral);
    Optional<Object> visitFloatLiteral(FloatLiteral floatLiteral);
}
