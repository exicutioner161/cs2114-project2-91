import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import main.GameDriver;

class GameDriverTest {
    @Test
    void retriesInvalidInputAndCompletesValidPath() {
        String input = "\nletters\n-1\n999999999999999999999\n1\n2\n3\n4\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        GameDriver.run(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
            new PrintStream(bytes));
        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Invalid option"));
        assertTrue(output.contains("Goodbye."));
        assertTrue(output.contains("Match winner:"));
        assertTrue(output.contains("TOURNAMENT BRACKET"));
        assertTrue(output.contains("Winners Bracket"));
    }

    @Test
    void mainRunsTheExitPath() {
        java.io.InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("4\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(bytes));
            GameDriver.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("Goodbye."));
    }

    @Test
    void reportsMissingSaveWithoutCrashing() {
        String input = "6\n4\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        GameDriver.run(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
            new PrintStream(bytes));
        assertTrue(bytes.toString(StandardCharsets.UTF_8)
            .contains("Unable to load progress."));
    }
}
