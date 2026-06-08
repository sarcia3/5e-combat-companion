package org.tcs.model;

public class TurnTracker {
  double movement, maxMovement;
  int reactions, maxReactions;
  int actions, maxActions;
  int bonusActions, maxBonusActions;
  int attackActions;
  int spellSlots;
  int attacksPerAction;

  public TurnTracker(
      int maxActions,
      int maxBonusActions,
      int maxReactions,
      int attacksInAction,
      int spellSlots,
      double maxMovement) {
    this.maxReactions = maxReactions;
    this.maxActions = maxActions;
    this.maxBonusActions = maxBonusActions;
    this.attacksPerAction = attacksInAction;
    this.spellSlots = spellSlots;
    this.maxMovement = maxMovement;
  }

  void move(double dist) {
    movement -= dist;
  }

  boolean canMove(double dist) {
    return dist <= movement;
  }

  boolean hasAction() {
    return actions > 0;
  }

  void makeAction() {
    actions--;
  }

  boolean hasBonusAction() {
    return bonusActions > 0;
  }

  void makeBonusAction() {
    bonusActions--;
  }

  boolean hasAttackAction() {
    return (actions > 0 && attacksPerAction > 0) || attackActions > 0;
  }

  void makeAttackAction() {
    if (attackActions == 0) {
      attackActions = attacksPerAction;
      actions--;
    }
    attackActions--;
  }

  boolean hasReaction() {
    return reactions > 0;
  }

  void makeReaction() {
    reactions--;
  }

  boolean canCastSpell(int level) {
    if (level == 0) return true;
    return spellSlots > 0;
  }

  void castSpell(int level) {
    if (level == 0) return;
    spellSlots--;
  }

  void reset() {
    reactions = maxReactions;
    actions = maxActions;
    bonusActions = maxBonusActions;
    attackActions = 0;
    movement = maxMovement;
  }
}
