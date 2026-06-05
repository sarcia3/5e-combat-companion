package org.tcs.model;

/**
 * An interface used in communication between State and GUI. Represents a process that can happen in
 * a state with additional information about it.
 */
public interface StateProcess extends Runnable {
  Creature getTarget();

  Creature getSource();
}
