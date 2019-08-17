package storage;

import models.Serializable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class GeneralStorage {
  ArrayList<Serializable> objects;
  private File storageFile;

  GeneralStorage(File storageFile) throws IOException {
    this.objects = new ArrayList<>();
    if (!storageFile.exists()) storageFile.createNewFile();
    this.storageFile = storageFile;
  }

  GeneralStorage(String filename) throws IOException {
    this(new File(filename));
  }

  public ArrayList<Serializable> getObjects() {
    return objects;
  }

  void syncToFile() {
    if (!storageFile.canWrite()) return;
    PrintWriter printWriter;
    try {
      printWriter = new PrintWriter(storageFile);
    } catch (FileNotFoundException e) {
      return;
    }
    for (Serializable object : objects) {
      printWriter.println(object.serialize());
    }
    printWriter.close();
  }

  void loadFromFile() {
    if (!storageFile.canRead()) return;
    Scanner s;
    try {
      s = new Scanner(storageFile);
    } catch (FileNotFoundException e) {
      return;
    }
    while (s.hasNextLine()) {
      Serializable object = deserialize(s.nextLine());
      this.objects.add(object);
    }
    s.close();
  }

  protected abstract Serializable deserialize(String serialized);
}
