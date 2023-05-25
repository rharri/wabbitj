package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.IntLiteral;
import com.github.rharri.wabbitj.ast.Print;
import com.github.rharri.wabbitj.ast.Program;
import com.github.rharri.wabbitj.ast.Statements;

import java.util.Optional;

public interface NodeVisitor {
    Optional<Object> visitProgram(Program program);
    Optional<Object> visitStatements(Statements statements);
    Optional<Object> visitPrint(Print print);
    Optional<Object> visitIntLiteral(IntLiteral intLiteral);
}
