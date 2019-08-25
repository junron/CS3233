package storage;

import models.Serializable;
import models.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

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

  public void addTransaction(Transaction transaction) {
    this.objects.add(transaction);
    this.syncToFile();
  }

  public ArrayList<Transaction> filter(Function<Transaction, Boolean> predicate) {
    ArrayList<Transaction> result = new ArrayList<>();
    for (Serializable transaction : getObjects()) {
      if (transaction instanceof Transaction) {
        if (predicate.apply((Transaction) transaction)) {
          result.add((Transaction) transaction);
        }
      }
    }
    return result;
  }

  public ArrayList<Transaction> getTransactionByCarPlate(String carPlate) {
    return filter(transaction -> transaction.getCar().getRegistrationNum().equals(carPlate));
  }

  public void removeTransaction(int serialNo) {
    objects.removeIf(serializable -> serializable instanceof Transaction && ((Transaction) serializable)
            .getSerialNumber() == serialNo);
    syncToFile();
  }

  @Override
  protected Serializable deserialize(String serialized) {
    Transaction transaction = new Transaction();
    transaction.deserialize(serialized);
    return transaction;
  }
}
