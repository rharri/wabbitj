package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.Operator;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class JavaRuntime {

    private final PrintStream printStream;
    private final Map<Operator, BiFunction<Object, Object, Object>> binaryOperations = new HashMap<>();

    private JavaRuntime(PrintStream printStream) {
        this.printStream = printStream;

        binaryOperations.put(Operator.PLUS, JavaRuntime::add);
    }

    public static JavaRuntime newInstance(PrintStream printStream) {
        return new JavaRuntime(printStream);
    }

    public void println(Object object) {
        printStream.println(object);
    }

    public Object binaryOp(Operator operator, Object lhs, Object rhs) {
        return binaryOperations.get(operator).apply(lhs, rhs);
    }

    private static Object add(Object lhs, Object rhs) {
        // Assume right hand side is of the same type
        // Wabbit does not do implicit conversions
        // This is guaranteed by the type checker?

        if (lhs instanceof Integer)
            return (int)lhs + (int)rhs;

        if (lhs instanceof Float)
            return (float)lhs + (float)rhs;

        throw new IllegalArgumentException("Cannot perform calculation with provided operands.");
    }
}
