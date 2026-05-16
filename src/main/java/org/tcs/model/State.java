package org.tcs.model;

import java.util.Collection;
import java.util.Map;
import org.tcs.model.geometry.*;

@SuppressWarnings("unused")
public class State {
  InitiativeTracker initiative;
  Map<Creature, Position> creaturePositions;

  State() {
    initiative = new InitiativeTracker();
  }

  State(Collection<? extends Creature> creatures) {
    initiative = new InitiativeTracker(creatures);
    // TODO add positions and add creatures to positions
  }
}
