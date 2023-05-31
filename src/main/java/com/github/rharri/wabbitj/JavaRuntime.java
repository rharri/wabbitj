package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.Operator;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class JavaRuntime {

    private final PrintStream out;
    private final Map<Operator, BiFunction<Object, Object, Object>> binaryOperations = new HashMap<>();

    private JavaRuntime(PrintStream out) {
        this.out = out;

        binaryOperations.put(Operator.PLUS, JavaRuntime::add);
        binaryOperations.put(Operator.MINUS, JavaRuntime::subtract);
        binaryOperations.put(Operator.TIMES, JavaRuntime::multiply);
        binaryOperations.put(Operator.DIVIDE, JavaRuntime::divide);
    }

    public static JavaRuntime newInstance(PrintStream printStream) {
        return new JavaRuntime(printStream);
    }

    public void println(Object object) {
        out.println(object);
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

    private static Object subtract(Object lhs, Object rhs) {
        // Assume right hand side is of the same type
        // Wabbit does not do implicit conversions
        // This is guaranteed by the type checker?

        if (lhs instanceof Integer)
            return (int)lhs - (int)rhs;

        if (lhs instanceof Float)
            return (float)lhs - (float)rhs;

        throw new IllegalArgumentException("Cannot perform calculation with provided operands.");
    }

    private static Object multiply(Object lhs, Object rhs) {
        // Assume right hand side is of the same type
        // Wabbit does not do implicit conversions
        // This is guaranteed by the type checker?

        if (lhs instanceof Integer)
            return (int)lhs * (int)rhs;

        if (lhs instanceof Float)
            return (float)lhs * (float)rhs;

        throw new IllegalArgumentException("Cannot perform calculation with provided operands.");
    }

    private static Object divide(Object lhs, Object rhs) {
        // Assume right hand side is of the same type
        // Wabbit does not do implicit conversions
        // This is guaranteed by the type checker?

        if (lhs instanceof Integer)
            return (int)lhs / (int)rhs;

        if (lhs instanceof Float)
            return (float)lhs / (float)rhs;

        throw new IllegalArgumentException("Cannot perform calculation with provided operands.");
    }
}
