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
        String input = "\nletters\n-1\n999999999999999999999\n1\n2\n"
            + "5\n1\n3\n4\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        runWithConsole(input, bytes);
        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Invalid option"));
        assertTrue(output.contains("Goodbye."));
        assertTrue(output.contains("Match winner:"));
        assertTrue(output.contains("TOURNAMENT BRACKET"));
        assertTrue(output.contains("Winners Bracket"));
    }

    @Test
    void buysFromCategoryAndItemSubmenus() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        runWithConsole("2\n5\n1\n4\n", bytes);
        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Choose an equipment category:"));
        assertTrue(output.contains("Weapon items:"));
        assertTrue(output.contains("Item equipped."));
        assertTrue(output.contains("Goodbye."));
    }

    @Test
    void leadingBlankLineDoesNotDiscardTheFirstMenuChoice() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        runWithConsole("\n2\n0\n4\n", bytes);
        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Choose an equipment category:"));
        assertTrue(output.indexOf("Choose an equipment category:")
            < output.lastIndexOf("Goodbye."));
    }

    @Test
    void invalidCategoryAndItemChoicesRepromptInTheirSubmenus() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        runWithConsole("2\n9\n5\n9\n0\n0\n4\n", bytes);
        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Invalid category."));
        assertTrue(output.contains("Invalid item."));
        assertTrue(output.contains("Goodbye."));
    }

    @Test
    void zeroExitsShopFromCategoryAndItemMenus() {
        ByteArrayOutputStream categoryOutput = new ByteArrayOutputStream();
        runWithConsole("2\n0\n4\n", categoryOutput);
        assertTrue(categoryOutput.toString(StandardCharsets.UTF_8)
                .contains("Goodbye."));

        ByteArrayOutputStream itemOutput = new ByteArrayOutputStream();
        runWithConsole("2\n1\n0\n4\n", itemOutput);
        assertTrue(itemOutput.toString(StandardCharsets.UTF_8)
                .contains("Goodbye."));
        assertTrue(itemOutput.toString(StandardCharsets.UTF_8)
                .contains("0. Return to main menu"));
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
    void ignoresAnInitialEmptyLineBeforeTheFirstChoice() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        runWithConsole("\n4\n", bytes);
        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Goodbye."));
        assertTrue(!output.contains("Invalid option"));
    }

    @Test
    void reportsMissingSaveWithoutCrashing() {
        String input = "6\n4\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        runWithConsole(input, bytes);
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
        runWithConsole(input.toString(), bytes);
        String output = bytes.toString(StandardCharsets.UTF_8);
        assertEquals(14, countOccurrences(output, "TOURNAMENT BRACKET"));
    }

    @Test
    void returnsToTheMenuAfterTheBracketWithoutAnExtraInput() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        runWithConsole("3\n4\n", bytes);
        String output = bytes.toString(StandardCharsets.UTF_8);
        int bracket = output.indexOf("TOURNAMENT BRACKET");
        int prompt = output.indexOf("Press Enter to continue.");
        int nextMenu = output.indexOf("\n1. View Status", bracket);
        assertTrue(bracket >= 0);
        assertTrue(prompt < 0);
        assertTrue(nextMenu > bracket);
    }

    @Test
    void displaysPlayerProgressionCounters() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        runWithConsole("1\n4\n", bytes);
        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Tournament Wins: 0/5"));
        assertTrue(output.contains("Tournament Losses: 0/10"));
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

    private void runWithConsole(String input, ByteArrayOutputStream output) {
        java.io.InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        try {
            System.setIn(new ByteArrayInputStream(
                input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output));
            GameDriver.run(new Scanner(System.in));
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
