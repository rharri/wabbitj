package com.github.rharri.wabbitj;

public class JavaRuntime {

    private JavaRuntime() {
    }

    public static JavaRuntime newInstance() {
        return new JavaRuntime();
    }

    public void println(Object object) {
        System.out.println(object);
    }
}
