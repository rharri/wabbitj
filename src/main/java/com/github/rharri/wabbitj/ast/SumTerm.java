package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

public class SumTerm  implements  Expression {

    private final Expression lhs;
    private final Expression rhs;

    private SumTerm(Expression lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public static SumTerm newInstance(Expression lhs, Expression rhs) {
        return new SumTerm(lhs, rhs);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitSumTerm(this);
    }

    public Expression getLhs() {
        return lhs;
    }

    public Expression getRhs() {
        return rhs;
    }
}
