package com.github.rharri.wabbitj;

import java.io.PrintStream;
import java.util.function.BinaryOperator;

public class JavaRuntime {

    private final PrintStream printStream;

    private JavaRuntime(PrintStream printStream) {
        this.printStream = printStream;
    }

    public static JavaRuntime newInstance(PrintStream printStream) {
        return new JavaRuntime(printStream);
    }

    public void println(Object object) {
        printStream.println(object);
    }

    public BinaryOperator<Integer> sumInteger = Integer::sum;

    public BinaryOperator<Float> sumFloat = Float::sum;
}
