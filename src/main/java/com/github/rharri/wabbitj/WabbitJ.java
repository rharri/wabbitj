package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.AbstractSyntaxTree;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "wabbitj", mixinStandardHelpOptions = true, versionProvider = WabbitJ.PackageVersionProvider.class,
    description = "Wabbit is a statically typed programming language similar to Go. Wabbit was created by David Beazley." +
            " Please see https://www.dabeaz.com/compiler.html for more information.")
public class WabbitJ implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The Wabbit file to execute.")
    private File file;

    @Override
    public Integer call() {
        if (file.exists()) {
            try {
                String programText = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                Tokenizer tokenizer = Tokenizer.newInstance(programText);
                tokenizer.tokenize();
                List<Token> tokens = tokenizer.getTokens();
                Parser parser = Parser.newInstance(tokens);
                AbstractSyntaxTree ast = parser.parse();
                Interpreter interpreter = Interpreter.newInstance(JavaRuntime.newInstance(System.out));
                ast.accept(interpreter);
            } catch (IOException e) {
                System.out.println("File cannot be read.");
                return 1;
            }
        } else {
            System.out.printf("%s does not exist.%n", file.toPath());
            return 1;
        }
        return 0;
    }

    // Credit: https://github.com/remkop/picocli/issues/236
    // Credit: https://docs.oracle.com/javase/tutorial/deployment/jar/packageman.html
    // Credit: https://docs.oracle.com/javase/8/docs/technotes/guides/versioning/spec/versioning2.html
    // Credit: https://stackoverflow.com/a/921753
    static class PackageVersionProvider implements CommandLine.IVersionProvider {

        @Override
        public String[] getVersion() {
            Package pkg = WabbitJ.class.getPackage();
            return new String[] { String.format("%s %s",
                    pkg.getImplementationTitle(),
                    pkg.getImplementationVersion()) };
        }
    }
}
