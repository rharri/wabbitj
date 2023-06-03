/*
 * Copyright (c) 2023. Ryan Harri
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.rharri.wabbitj.ast;

import com.github.rharri.wabbitj.NodeVisitor;

import java.util.Objects;

public record BinaryOp(Operator operator, Expression lhs, Expression rhs, int line, int column)
        implements Expression, SourceLocation {

    public BinaryOp {
        Objects.requireNonNull(lhs);
        Objects.requireNonNull(rhs);

        if (line <= 0)
            throw new IllegalArgumentException("line must be >= 1");

        if (column <= 0)
            throw new IllegalArgumentException("column must be >= 1");
    }

    @Override
    public void accept(NodeVisitor visitor) {
        Objects.requireNonNull(visitor);
        visitor.visitBinaryOp(this);
    }
}
