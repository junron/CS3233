package storage;

import models.Admin;
import models.Serializable;
import models.User;

import java.io.IOException;

public class UserStorage extends GeneralStorage {
  public static UserStorage storage;

  public static void initialize() {
    try {
      UserStorage.storage = new UserStorage();
      storage.loadFromFile();
      if (storage.objects.size() == 0) {
        storage.addUser(new Admin());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public UserStorage() throws IOException {
    super("users.txt");
  }

  public void addUser(User u) {
    this.objects.add(u);
    this.syncToFile();
  }

  public User getUserByUsername(String username) {
    return (User) this.objects.stream()
            .filter(user -> user instanceof User && ((User) user).getUsername().equals(username)).findFirst()
            .orElse(null);
  }

  @Override
  protected Serializable deserialize(String serialized) {
    if (serialized.length() > 6 && serialized.substring(0, 6).equals("CS3323"))
      return new Admin();
    Serializable serializable = new User();
    serializable.deserialize(serialized);
    return serializable;
  }
}
