// TestMyDate class.
// Fill in the parts that are marked "WRITE YOUR CODE HERE."

import java.util.*;

public class TestMyDate {

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Enter the year, month and day (as integers): ");

    int year, month, day;

    // Read the integers year, month and day from the input using
    // Scanner's nextInt().
    // If nextInt() throws an InputMismatchException, then print an
    // error message "Invalid integer input." and terminate program.

    // =======================
    // WRITE YOUR CODE HERE.
    // =======================
    try{
      year = scanner.nextInt();
      month = scanner.nextInt();
      day = scanner.nextInt();
    } catch (InputMismatchException e) {
      System.out.println("Invalid integer input.");
      return;
    }


    MyDate date;

    // Create a MyDate instance with the input year, month and day.
    // If MyDate constructor throws an InvalidDateException, then print
    // the message in the exception and terminate program.

    // =======================
    // WRITE YOUR CODE HERE.
    // =======================
    try {
      date = new MyDate(year,month,day);
    } catch (InvalidDateException e) {
      System.out.println(e.getMessage());
      return;
    }


    // Print out the date input by the user.
    System.out.println("The date you entered is " + date + ".");
  }
}