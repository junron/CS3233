package models;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class User implements Serializable {
  private String username;
  private String password;
  private String name;
  private String nric;
  private Date dob;

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
  public byte[] serialize() {
    return new byte[0];
  }

  @Override
  public Serializable deserialize(byte[] serialized) {
    return null;
  }
}
