/*
 * Copyright (c) 2023. Ryan Harri
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.rharri.wabbitj;

import com.github.rharri.wabbitj.ast.AbstractSyntaxTree;
import com.github.rharri.wabbitj.interpreter.Interpreter;
import com.github.rharri.wabbitj.interpreter.JavaRuntime;
import com.github.rharri.wabbitj.tokenizer.Token;
import com.github.rharri.wabbitj.tokenizer.Tokenizer;
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

                Tokenizer tokenizer = new Tokenizer(programText);
                tokenizer.tokenize();
                List<Token> tokens = tokenizer.getTokens();

                Parser parser = new Parser(tokens);
                AbstractSyntaxTree ast = parser.parse();

                TypeChecker typeChecker = new TypeChecker(file.getName(), programText);
                ast.accept(typeChecker);
                List<String> errors = typeChecker.getErrors();

                if (!errors.isEmpty()) {
                    for (String error : errors)
                        System.out.println(error);

                    return 1;
                }

                Interpreter interpreter = new Interpreter(new JavaRuntime(System.out));
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

    @Override
    public String toString() {
        return "WabbitJ{" +
                "file=" + file +
                '}';
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
