package models;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class User implements Serializable {
  String username;
  String password;
  private String name;
  private String nric;
  private Date dob;

  public User() {
  }

  public User(String username, String password, String name, String nric, Date dob) throws Exception {
    if (username == null || username.length() == 0) throw new Exception("Username cannot be empty.");
    if (password == null || password.length() == 0) throw new Exception("Password cannot be empty.");
    if (name == null || name.length() == 0) throw new Exception("Name cannot be empty.");
    if (nric == null || nric.length() == 0) throw new Exception("NRIC cannot be empty.");
    nric = nric.toUpperCase();
    if (!nric.matches("^[STFG]\\d{7}[A-Z]$")) throw new Exception("Invalid NRIC.");
    if (dob == null) throw new Exception("DOB cannot be empty.");
    long dateDiffSeconds = (new Date().getTime() - dob.getTime()) / 1000;
    if (dateDiffSeconds < (18 * 365 * 24 * 60 * 60))
      throw new Exception("You must be at least 18 to drive a motor vehicle");
    this.username = username;
    this.password = password;
    this.name = name;
    this.nric = nric;
    this.dob = dob;
  }

  public User(String username, String password, String name, String nric, LocalDate dob) throws Exception {
    this(username, password, name, nric, dob == null ? null : Date
            .from(Instant.from(dob.atStartOfDay(ZoneId.systemDefault()))));
  }

  public String getUsername() {
    return username;
  }

  public String getName() {
    return name;
  }

  public boolean signIn(String password) {
    //    Timing safe password comparison
    if (password.length() != this.password.length()) return false;
    boolean res = true;
    for (int i = 0; i < password.length(); i++) {
      res &= (password.charAt(i) == this.password.charAt(i));
    }
    return res;
  }

  @Override
  public String serialize() {
    String time = String.valueOf(this.dob == null ? null : this.dob.getTime());
    return String.join("|", new String[]{this.username, this.password, this.name, this.nric, time});
  }

  @Override
  public void deserialize(String serialized) {
    String[] parts = serialized.split("\\|");
    this.username = parts[0];
    this.password = parts[1];
    this.name = parts[2];
    this.nric = parts[3];
    this.dob = parts[4].equals("null") ? null : new Date(Long.parseLong(parts[4]));
  }

  @Override
  public String toString() {
    return "User{" + "username='" + username + '\'' + ", password='" + password + '\'' + ", name='" + name + '\'' +
            ", nric='" + nric + '\'' + ", dob=" + dob + '}';
  }
}
