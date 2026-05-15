package org.tcs;

// Zalecam, żeby implementacje były wewnętrzne do `Change`
@SuppressWarnings("unused")
public interface Change {
  void apply(State state);
}
