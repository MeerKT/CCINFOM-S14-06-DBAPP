package HelperClass;

import java.util.Scanner;

/**
 * Helper function for getting user input
 */
public class UserInput {
    private static final Scanner input = new Scanner(System.in);

    /**
     * Returns the static Scanner instance (input) for reading user input.
     *
     * @return scanner if system.in
     */
    public static Scanner getScanner() {
        return input;
    }

    /**
     * Closes the static Scanner instance (input) to release associated resources when input operations are complete.
     *
     */
    public static void closeScanner() {
        input.close();
    }

}

