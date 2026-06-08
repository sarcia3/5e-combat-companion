package org.tcs.model;

public class TurnTracker {
  double movement, maxMovement;
  int reactions, maxReactions;
  int actions, maxActions;
  int bonusActions, maxBonusActions;
  int attackActions;
  // To how many attack actions does the action translate
  int attacksInAction;

  public TurnTracker(
      int maxActions,
      int maxBonusActions,
      int maxReactions,
      int attacksInAction,
      double maxMovement) {
    this.maxReactions = maxReactions;
    this.maxActions = maxActions;
    this.maxBonusActions = maxBonusActions;
    this.attacksInAction = attacksInAction;
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
    return (actions > 0 && attacksInAction > 0) || attackActions > 0;
  }

  void makeAttackAction() {
    if (attackActions == 0) {
      attackActions = attacksInAction;
      actions--;
    }
    attackActions--;
  }

  void reset() {
    reactions = maxReactions;
    actions = maxActions;
    bonusActions = maxBonusActions;
    attackActions = 0;
    movement = maxMovement;
    System.out.println(actions);
  }
}
