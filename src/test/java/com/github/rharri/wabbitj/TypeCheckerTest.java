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
