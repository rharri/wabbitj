package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.IntLiteral;
import com.github.rharri.wabbitj.ast.Print;
import com.github.rharri.wabbitj.ast.Program;
import com.github.rharri.wabbitj.ast.Statements;

public class Main {

    public static void main(String[] args) {
        var intLiteral = IntLiteral.newInstance(42);
        var print = Print.newInstance(intLiteral);
        var statements = Statements.newInstance();
        statements.add(print);
        var program = Program.newInstance(statements);

        program.accept(Interpreter.newInstance(JavaRuntime.newInstance()));
    }
}
