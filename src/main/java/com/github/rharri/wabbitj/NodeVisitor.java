package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.*;

public interface NodeVisitor {
    void visitProgram(Program program);
    void visitStatements(Statements statements);
    void visitPrint(Print print);
    void visitIntLiteral(IntLiteral intLiteral);
    void visitFloatLiteral(FloatLiteral floatLiteral);
    void visitBinaryOp(BinaryOp binaryOp);
}
