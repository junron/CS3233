package models;

public class Admin extends User {
  public Admin() {
    this.username = "CS3323";
    this.password = "password";
  }

  @Override
  public String toString() {
    return "ADMIN ACCCOUNT!!";
  }
}
