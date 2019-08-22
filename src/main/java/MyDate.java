// MyDate class.
// Fill in the parts that are marked "WRITE YOUR CODE HERE."

public class MyDate {

  /************** Data Members **********************/

  private int year;
  private int month;
  private int day;
  private static int[] numDaysInMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};


  /************** Constructors **********************/

  // Construct a MyDate instance from the input parameters year, month and day.
  // If year, month and day are not in the following valid ranges,
  // an InvalidDateException will be thrown:
  //   year >= 1.
  //   1 <= month <= 12.
  //   1 <= day <= X, where X is the number of days in that month.
  //
  // The InvalidDateException is thrown with the message "MyDate: Invalid date."
  public MyDate(int year, int month, int day) throws InvalidDateException {
    // =======================
    // WRITE YOUR CODE HERE.
    // =======================
    if (year < 1) throw new InvalidDateException("MyDate: Invalid date.");
    if (month < 1 || month > 12) throw new InvalidDateException("MyDate: Invalid date.");
    if (month == 2) {
      if (isLeapYear(year)) {
        if (day > 29) throw new InvalidDateException("MyDate: Invalid date.");
      } else {
        if (day > 28) throw new InvalidDateException("MyDate: Invalid date.");
      }
    }else{
      if (day > numDaysInMonth[month-1]) throw new InvalidDateException("MyDate: Invalid date.");
    }
    this.day = day;
    this.year = year;
    this.month = month;
  }


  /**************** Helper Methods ***********************/

  // Return true iff year is a leap year.
  // A year is a leap year if it is divisible by 400, or
  // it is divisible by 4 but not by 100.
  private static boolean isLeapYear(int year) {
    if (year < 1) return false;
    return (year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0));
  }


  /**************** Accessors ***********************/

  public int getYear() {
    return year;
  }

  public int getMonth() {
    return month;
  }

  public int getDay() {
    return day;
  }


  /***************** Overriding Methods ******************/

  public String toString() {
    return year + "-" + month + "-" + day;
  }
}
