package org.tcs.model;

import java.util.Collection;

/**
 * An interface used in communication between State and GUI. Represents a process that can happen in
 * a state with additional information about it.
 */
public interface StateProcess extends Runnable {
  Collection<Creature> getTargets();

  Collection<Creature> getSource();
}
