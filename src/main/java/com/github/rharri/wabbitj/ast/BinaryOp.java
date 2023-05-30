package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public class BinaryOp implements Expression {

    private final Operator operator;
    private final Expression lhs;
    private final Expression rhs;

    private BinaryOp(Operator operator, Expression lhs, Expression rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public static BinaryOp newInstance(Operator operator, Expression lhs, Expression rhs) {
        return new BinaryOp(operator, lhs, rhs);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitBinaryOp(this);
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getLhs() {
        return lhs;
    }

    public Expression getRhs() {
        return rhs;
    }
}
