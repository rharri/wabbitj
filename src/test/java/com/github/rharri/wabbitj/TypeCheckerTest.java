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

package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeCheckerTest {

    @Test
    public void shouldReturnTypeErrorUnsupportedOperandsForPlusOperator() {
        var intLiteral = new IntLiteral(2, 1, 7);
        var floatLiteral = new FloatLiteral(3.5f, 1, 11);
        var binaryOp = new BinaryOp(Operator.PLUS, intLiteral, floatLiteral, 1, 7);
        var print = new Print(binaryOp);
        var statements = new Statements();
        statements.add(print);
        var program = new Program(statements);

        var programText = "print 2 + 3.5;";

        var typeChecker = new TypeChecker("test.wb", programText);
        program.accept(typeChecker);

        List<String> typeErrors = typeChecker.getErrors();

        assertEquals(1, typeErrors.size());
    }
}
