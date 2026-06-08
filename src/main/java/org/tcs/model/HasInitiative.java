package org.tcs.model;

/**
 * Characters/Monsters/etc. in D&D take turns based on initiative. Initiative is a randomly
 * generated number assigned to a character before entering combat. After initiative for each combat
 * member gets rolled they get transformed into a queue of taking actions. The higher the
 * initiative, the sooner the given combat member goes. After each character has taken their turn,
 * the round ends and initiative repeats in the same order as before. For more details see the<a
 * href="https://media.wizards.com/2016/downloads/DND/SRD-OGL_V5.1.pdf">System Reference Document
 * p.50.</a>
 *
 * <p>Implementing this interface, means that an instance of an implementing class should get its
 * initiative rolled and be put in the InitiativeTracker queue during combat.
 */
public interface HasInitiative {
  int generateInitiative();

  void onTurnReset();
}
