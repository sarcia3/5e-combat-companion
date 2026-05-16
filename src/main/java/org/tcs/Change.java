package org.tcs;

import org.tcs.model.State;

// Zalecam, żeby implementacje były wewnętrzne do `Change`
@SuppressWarnings("unused")
public interface Change {
  void apply(State state);
}
