package com.github.rharri.wabbitj;

import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        int statusCode = new CommandLine(new WabbitJ()).execute(args);
        System.exit(statusCode);
    }
}
