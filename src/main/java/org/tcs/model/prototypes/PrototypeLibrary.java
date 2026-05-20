package org.tcs.model.prototypes;

import java.util.HashMap;
import java.util.Map;

public class PrototypeLibrary {
  PrototypeLibrary() {
    throw new UnsupportedOperationException();
  }

  private static Map<String, Prototype> library;

  static void loadAll() {
    library = new HashMap<>();
    // TODO actually load prototypes
    // This is a placeholder
    // We should be reading Prototypes from some file here
    Prototype t =
        new Prototype() {
          @Override
          public String getName() {
            return "Name";
          }
        };
    add(t);
  }

  static Prototype get(String name) {
    return library.get(name);
  }

  static void add(Prototype prototype) {
    library.put(prototype.getName(), prototype);
  }
}
