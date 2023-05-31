package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.Operator;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class JavaRuntime {

    private final PrintStream out;
    private final Map<Operator, BiFunction<Object, Object, Object>> binaryOperations = new HashMap<>();
    private final Map<Operator, UnaryOperator<Object>> unaryOperations = new HashMap<>();

    private JavaRuntime(PrintStream out) {
        this.out = out;

        binaryOperations.put(Operator.PLUS, JavaRuntime::add);
        binaryOperations.put(Operator.MINUS, JavaRuntime::subtract);
        binaryOperations.put(Operator.TIMES, JavaRuntime::multiply);
        binaryOperations.put(Operator.DIVIDE, JavaRuntime::divide);

        unaryOperations.put(Operator.MINUS, JavaRuntime::minus);
        unaryOperations.put(Operator.PLUS, JavaRuntime::plus);
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

    public Object unaryOp(Operator operator, Object operand) {
        return unaryOperations.get(operator).apply(operand);
    }

    private static Object add(Object lhs, Object rhs) {
        // Assume right hand side is of the same type
        // Wabbit does not do implicit conversions
        // This is guaranteed by the type checker?

        if (lhs instanceof Integer)
            return (int) lhs + (int) rhs;

        if (lhs instanceof Float)
            return (float) lhs + (float) rhs;

        throw new IllegalArgumentException("Cannot perform operation with provided operands.");
    }

    private static Object subtract(Object lhs, Object rhs) {
        // Assume right hand side is of the same type
        // Wabbit does not do implicit conversions
        // This is guaranteed by the type checker?

        if (lhs instanceof Integer)
            return (int) lhs - (int) rhs;

        if (lhs instanceof Float)
            return (float) lhs - (float) rhs;

        throw new IllegalArgumentException("Cannot perform operation with provided operands.");
    }

    private static Object multiply(Object lhs, Object rhs) {
        // Assume right hand side is of the same type
        // Wabbit does not do implicit conversions
        // This is guaranteed by the type checker?

        if (lhs instanceof Integer)
            return (int) lhs * (int) rhs;

        if (lhs instanceof Float)
            return (float) lhs * (float) rhs;

        throw new IllegalArgumentException("Cannot perform operation with provided operands.");
    }

    private static Object divide(Object lhs, Object rhs) {
        // Assume right hand side is of the same type
        // Wabbit does not do implicit conversions
        // This is guaranteed by the type checker?

        if (lhs instanceof Integer)
            return (int) lhs / (int) rhs;

        if (lhs instanceof Float)
            return (float) lhs / (float) rhs;

        throw new IllegalArgumentException("Cannot perform operation with provided operands.");
    }

    private static Object minus(Object operand) {
        // Assume right hand side is of the same type
        // Wabbit does not do implicit conversions
        // This is guaranteed by the type checker?

        if (operand instanceof Integer)
            return -1 * (int) operand;

        if (operand instanceof Float)
            return -1 * (float) operand;

        throw new IllegalArgumentException("Cannot perform operation with provided operands.");
    }

    private static Object plus(Object operand) {
        // Assume right hand side is of the same type
        // Wabbit does not do implicit conversions
        // This is guaranteed by the type checker?

        if (operand instanceof Integer) {
            int op = (int) operand;
            return op >= 0 ? op : -1 * op;
        }

        if (operand instanceof Float) {
            float op = (float) operand;
            return op >= 0 ? op : -1.0f * op;
        }

        throw new IllegalArgumentException("Cannot perform operation with provided operand.");
    }
}
