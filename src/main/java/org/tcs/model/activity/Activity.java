package org.tcs.model.activity;

import org.tcs.model.State;

public sealed interface Activity permits Action, BonusAction, Reaction {
  void resolve(State state);
}
