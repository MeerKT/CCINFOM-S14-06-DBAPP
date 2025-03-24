package View;

import HelperClass.UserInput;

/**
 * The HRSView class represents the graphical user interface for the Hotel
 * Reservation System.
 * It extends the JFrame class and provides methods to initialize and set up the
 * different screens of the system.
 */
public class BankWindow {
    public static int chooseWindow(){
        System.out.println("1 - Customer Window\n2- Employee Window\n3 - Exit");
        System.out.print("Choose Window: ");
        int option = Integer.parseInt(UserInput.getScanner().nextLine());

        while (option < 1 || option > 3) {
            System.out.println("\nInvalid option Choose again");
            System.out.println("1 - Customer Window\n2- Employee Window\n3 - Exit");
            System.out.print("Choose Window: ");
            option = Integer.parseInt(UserInput.getScanner().nextLine());
        }
        return option;
    }
}
