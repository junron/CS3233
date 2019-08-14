import java.util.Scanner;

public class ContactValidator {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Name: ");
    String name = scanner.nextLine();
    System.out.println("Phone number: ");
    String phone = scanner.nextLine();
    System.out.println("Email address: ");
    String email = scanner.nextLine();
    if (!name.matches("[a-zA-Z ]{3,}")) {
      System.out.println("Sorry, your name is invalid!");
    }
    if (!phone.matches("[689]\\d{7}")) {
      System.out.println("Sorry, your phone number is invalid!");
    }
    if (!email.matches("[a-zA-Z]\\w*@[a-zA-Z]*\\.(net|com)(\\.sg|\\.cn|\\.au)?")) {
      System.out.println("Sorry, your email is invalid!");
    }
  }
}