import java.util.Comparator;

public class EmployeeComparator implements Comparator {
  @Override
  public int compare(Object o1, Object o2) {
    if(o1 instanceof Employee && o2 instanceof Employee){
      return ((Employee) o2).hireDate().compareTo(((Employee) o1).hireDate());
    }
    return -1;
  }
}
