import java.util.*;
import java.text.*;

//No changes required
public class EmployeeSort {

  public static void main(String[] args) {
    DateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
    ArrayList<Employee> list = new ArrayList<Employee>();

    try
    {
      list.add(new Employee("Michael", "T", 45,(Date)formatter.parse("13-FEB-1998") ));
      list.add(new Employee("Anthony", "Venison", 30,(Date)formatter.parse("29-JAN-2010")));
      list.add(new Employee("Jennifer", "Anton", 25,(Date)formatter.parse("29-MAR-2001")));
    }
    catch(ParseException ex){}

    System.out.println("Sorting by ages using comparable");
    Collections.sort(list);
    System.out.println(list);
    System.out.println("Sorting by hiredate using comparator");
    Collections.sort(list, new EmployeeComparator());
    System.out.println(list);

  }
}
