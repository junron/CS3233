package storage;

import models.Serializable;
import models.Transaction;

import java.io.IOException;

public class TransactionStorage extends GeneralStorage {
  public static TransactionStorage storage;

  public static void initialize() {
    try {
      TransactionStorage.storage = new TransactionStorage();
      storage.loadFromFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public TransactionStorage() throws IOException {
    super("transaction.txt");
  }

  public void addTransaction(Transaction transaction){
    this.objects.add(transaction);
    this.syncToFile();
  }

  @Override
  protected Serializable deserialize(String serialized) {
    Transaction transaction = new Transaction();
    transaction.deserialize(serialized);
    return transaction;
  }
}
