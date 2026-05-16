package org.tcs.model;

import java.util.*;

// should probably implement collections in the future.
public class InitiativeTracker {
  private record InitiativeEntry(int initiative, HasInitiative actor)
      implements Comparable<InitiativeEntry> {

    InitiativeEntry(HasInitiative o) {
      this(o.generateInitiative(), o);
    }

    @Override
    public int compareTo(InitiativeEntry other) {
      return Integer.compare(other.initiative, initiative);
      // flipped on purpose, the greater the initiative the sooner an actor acts.
    }
  }

  LinkedList<InitiativeEntry> combatQueue;

  /** Does nothing if the queue is empty */
  void advance() {
    if (!combatQueue.isEmpty()) {
      combatQueue.add(combatQueue.remove());
    }
  }

  HasInitiative getFirst() {
    return combatQueue.getFirst().actor;
  }

  InitiativeTracker() {
    this(null);
  }

  InitiativeTracker(Collection<? extends HasInitiative> elements) {
    if (elements == null) {
      combatQueue = new LinkedList<>();
      return;
    }

    ArrayList<InitiativeEntry> temporary = new ArrayList<>();
    elements.forEach((element) -> temporary.add(new InitiativeEntry(element)));
    Collections.sort(temporary);
    combatQueue = new LinkedList<>(temporary);
  }

  /**
   * Generates initiative and adds an element to the queue at first available position after element
   * before it with greater initiative
   *
   * @param entry the entity to be added
   */
  void add(HasInitiative entry) {
    InitiativeEntry toAdd = new InitiativeEntry(entry);
    ListIterator<InitiativeEntry> thisQueue = combatQueue.listIterator(1);
    while (thisQueue.hasPrevious()) {
      if (thisQueue.previous().compareTo(toAdd) >= 0) {
        thisQueue.add(toAdd);
        return;
      }
      thisQueue.next(); // to offset `previous`
      if (!thisQueue.hasNext()) break;
      thisQueue.next();
    }
    thisQueue.add(toAdd);
  }

  int getSize() {
    return combatQueue.size();
  }
}
