package org.tcs.model.prototypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;

public class PrototypeReference implements Prototype {
  String name;
  transient Prototype real;

  PrototypeReference(String name) {
    this.name = name;
    real = PrototypeLibrary.get(name);
  }

  Prototype getPrototype() {
    return real;
  }

  @Serial
  private void writeObject(ObjectOutputStream oos) throws IOException {
    name = real.getName();
    oos.defaultWriteObject();
  }

  @Serial
  private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
  }

  @Override
  public String getName() {
    return name;
  }
}
