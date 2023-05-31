package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public class UnaryOp implements Expression {

    private final Operator operator;
    private final Expression operand;

    private UnaryOp(Operator operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }

    public static UnaryOp newInstance(Operator operator, Expression operand) {
        return new UnaryOp(operator, operand);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitUnaryOp(this);
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getOperand() {
        return operand;
    }
}
