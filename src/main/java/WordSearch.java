import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordSearch {

  public static void main(String[] args) {
    Pattern pattern = Pattern.compile(" (you|me|I|them|him|her|his|she) ");
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter a sentence: ");
    Matcher matcher = pattern.matcher(scanner.nextLine());
    while (matcher.find()) System.out.println("Found a " + matcher.group().trim());
  }
}
