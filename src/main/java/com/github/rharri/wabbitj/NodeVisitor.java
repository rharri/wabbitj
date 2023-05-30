package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.*;

import java.util.Optional;

public interface NodeVisitor {
    void visitProgram(Program program);
    void visitStatements(Statements statements);
    void visitPrint(Print print);
    void visitIntLiteral(IntLiteral intLiteral);
    void visitFloatLiteral(FloatLiteral floatLiteral);
    void visitSumTerm(SumTerm sumTerm);
}
