package org.tcs.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class StateTest {

  @Test
  void canCreateEmptyGameState() {
    new State();
  }

  @Test
  void canCreateGameStateWithGivenCreatures() {
    Creature first = new Creature("Boring commoner", 4, 30);
    Creature second = new Creature("Cool commoner", 5, 30);
    ArrayList<Creature> commoners = new ArrayList<>();
    commoners.add(first);
    commoners.add(second);
    State state = new State(commoners);
    assertEquals(2, state.initiative.getSize());
  }
}
