package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.AbstractSyntaxTree;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        int statusCode = new Main().execute(System.out, args);
        System.exit(statusCode);
    }

    public int execute(PrintStream out, String[] args) {
        if (args == null || args.length == 0 || args[0].isEmpty()) {
            out.println("Filename is required.");
            return 1;
        }

        Path p = Path.of(args[0]);
        if (Files.exists(p)) {
            try {
                String programText = Files.readString(p, StandardCharsets.UTF_8);
                Tokenizer tokenizer = Tokenizer.newInstance(programText);
                tokenizer.tokenize();
                List<Token> tokens = tokenizer.getTokens();
                Parser parser = Parser.newInstance(tokens);
                AbstractSyntaxTree ast = parser.parse();
                Interpreter interpreter = Interpreter.newInstance(JavaRuntime.newInstance(System.out));
                ast.accept(interpreter);
            } catch (IOException e) {
                out.println("File cannot be read.");
                return 1;
            }
        } else {
            out.printf("%s does not exist.%n", p);
            return 1;
        }
        return 0;
    }
}
