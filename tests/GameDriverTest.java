import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import main.GameDriver;

class GameDriverTest {
    @Test
    void retriesInvalidInputAndCompletesValidPath() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        GameDriver.run(new Scanner(System.in));
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
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        GameDriver.run(new Scanner(System.in));
        assertTrue(bytes.toString(StandardCharsets.UTF_8)
                .contains("Unable to load progress."));
    }

    @Test
    void printsBracketAfterEveryBattle() {
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < 14; i++) {
            input.append("3\n");
        }
        input.append("4\n");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        GameDriver.run(new Scanner(System.in));
        String output = bytes.toString(StandardCharsets.UTF_8);
        assertEquals(14, countOccurrences(output, "TOURNAMENT BRACKET"));
    }

    private int countOccurrences(String text, String target) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(target, index)) >= 0) {
            count++;
            index += target.length();
        }
        return count;
    }
}
