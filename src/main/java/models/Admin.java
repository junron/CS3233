package models;

public class Admin extends User {
  public Admin() {
    this.username = "admin";
    this.password = "a";
  }

  @Override
  public String toString() {
    return "ADMIN ACCCOUNT!!";
  }
}
