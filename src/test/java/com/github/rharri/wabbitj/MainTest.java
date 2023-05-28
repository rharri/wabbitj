package com.github.rharri.wabbitj;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    private OutputStream runWithoutStandardOut() {
        // An OutputStream that "writes" bytes to a byte[]
        return new ByteArrayOutputStream();
    }

    @Test
    public void shouldExitWithStatusCode1WhenArgsAreNull() {
        var out = new PrintStream(runWithoutStandardOut());
        int statusCode = new Main().execute(out, null);

        assertEquals(1, statusCode);
    }

    @Test
    public void shouldExitWithStatusCode1WhenArgsAreEmpty() {
        var out = new PrintStream(runWithoutStandardOut());
        int statusCode = new Main().execute(out, new String[0]);

        assertEquals(1, statusCode);
    }

    @Test
    public void shouldExitWithStatusCode1WhenFirstArgIsEmptyString() {
        var out = new PrintStream(runWithoutStandardOut());
        int statusCode = new Main().execute(out, new String[]{""});

        assertEquals(1, statusCode);
    }
}
