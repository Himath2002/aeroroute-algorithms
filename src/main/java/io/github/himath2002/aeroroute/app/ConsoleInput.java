package io.github.himath2002.aeroroute.app;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/** Small input boundary that keeps validation out of the application workflow. */
final class ConsoleInput {
    private final Scanner scanner;
    private final PrintStream output;

    ConsoleInput(Scanner scanner, PrintStream output) {
        this.scanner = scanner;
        this.output = output;
    }

    int integer(String prompt, IntPredicate validator, String errorMessage) {
        while (true) {
            output.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                int number = Integer.parseInt(value);
                if (validator.test(number)) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
                // The shared message below keeps all invalid-number paths consistent.
            }
            output.println(errorMessage);
        }
    }

    String text(String prompt, Predicate<String> validator, String errorMessage) {
        while (true) {
            output.print(prompt);
            String value = scanner.nextLine().trim();
            if (validator.test(value)) {
                return value;
            }
            output.println(errorMessage);
        }
    }

    String airportCode(String prompt) {
        return text(
                prompt,
                value -> value.toUpperCase(Locale.ROOT).matches("[A-Z]{3}"),
                "Use a three-letter airport code, for example CMB."
        ).toUpperCase(Locale.ROOT);
    }
}
