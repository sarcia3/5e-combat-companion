package org.tcs.model;

import java.util.*;

// should probably implement collections in the future.
public class InitiativeTracker {
  LinkedList<HasInitiative> combatQueue;

  /** Does nothing if the queue is empty */
  void advance() {
    if (!combatQueue.isEmpty()) {
      combatQueue.add(combatQueue.remove());
    }
  }

  HasInitiative getFirst() {
    return combatQueue.getFirst();
  }

  InitiativeTracker() {
    combatQueue = new LinkedList<>();
  }

  InitiativeTracker(Collection<? extends HasInitiative> elements) {
    ArrayList<HasInitiative> temporary = new ArrayList<>(elements);
    temporary.forEach(HasInitiative::generateInitiative);
    temporary.sort(Comparator.comparingInt(HasInitiative::getInitiative).reversed());
    combatQueue = new LinkedList<>(temporary);
  }

  /**
   * Generates initiative and adds an element to the queue at first available position after element
   * before it with greater initiative
   *
   * @param entry the entity to be added
   */
  void add(HasInitiative entry) {
    entry.generateInitiative();
    ListIterator<HasInitiative> thisQueue = combatQueue.listIterator(1);
    while (thisQueue.hasPrevious()) {
      if (thisQueue.previous().getInitiative() >= entry.getInitiative()) {
        thisQueue.add(entry);
        return;
      }
      thisQueue.next();
    }
    thisQueue.add(entry);
  }

  int getSize() {
    return combatQueue.size();
  }
}
