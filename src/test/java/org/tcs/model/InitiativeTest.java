package org.tcs.model;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.junit.jupiter.api.Test;

class InitiativeTest {

  private static HasInitiative withInitiative(int value) {
    return new HasInitiative() {
      @Override
      public int generateInitiative() {
        return value;
      }

      @Override
      public void onTurnReset() {}
    };
  }

  @Test
  void constructorSortsByInitiativeDescending() {
    HasInitiative low = withInitiative(5);
    HasInitiative mid = withInitiative(12);
    HasInitiative high = withInitiative(20);

    InitiativeTracker tracker = new InitiativeTracker(List.of(low, high, mid));

    assertSame(high, tracker.getFirst());
    tracker.advance();
    assertSame(mid, tracker.getFirst());
    tracker.advance();
    assertSame(low, tracker.getFirst());
  }

  @Test
  void addInsertsInDescendingOrder() {
    HasInitiative high = withInitiative(20);
    HasInitiative low = withInitiative(5);
    InitiativeTracker tracker = new InitiativeTracker(List.of(high, low));

    HasInitiative middle = withInitiative(12);
    tracker.add(middle);

    assertSame(high, tracker.getFirst());
    tracker.advance();
    assertSame(middle, tracker.getFirst());
    tracker.advance();
    assertSame(low, tracker.getFirst());
  }
}
