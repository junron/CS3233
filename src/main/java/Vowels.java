import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vowels {

  public static void main(String[] args) {
    Scanner s = new Scanner(System.in);
    System.out.println("Enter text: ");
    String input = s.nextLine();
    final String patternStr = "[aeiou]";
    Pattern pattern = Pattern.compile(patternStr);
    Matcher matcher = pattern.matcher(input);
    int count=0;
    while(matcher.find()) count++;
    System.out.println("Number of vowels in text = "+count);
  }

}
