package org.tcs.model;

import java.util.*;
import org.tcs.model.activity.WeaponAttack;
import org.tcs.model.equipment.Weapon;
import org.tcs.model.geometry.*;
import org.tcs.model.util.Pair;

@SuppressWarnings("unused")
public class State {
  InitiativeTracker initiative;
  List<Creature> creatures;
  WorldMap worldMap;

  public State(WorldMap worldMap) {
    creatures = new ArrayList<>();
    this.worldMap = worldMap;
    initiative = new InitiativeTracker();
  }

  public State(Collection<? extends Creature> creatures, WorldMap worldMap) {
    this.worldMap = worldMap;
    initiative = new InitiativeTracker(creatures);
    this.creatures = new ArrayList<>(creatures);
  }

  public WorldMap getMap() {
    return worldMap;
  }

  /**
   * Adds a creature to the state.
   *
   * @return False if the creature already exists in this state or the position is already occupied.
   */
  public boolean addCreature(Creature creature) {
    if (creatures.contains(creature)) return false;
    if (!worldMap.occupyPoint(creature.position(), OccupyReason.Creature)) return false;
    creatures.add(creature);
    initiative.add(creature);
    return true;
  }

  /**
   * Deletes a creature from the state.
   *
   * @return False if the creature was not in the state and true otherwise.
   */
  public boolean removeCreature(Creature creature) {
    if (!creatures.contains(creature)) return false;
    creatures.remove(creature);
    worldMap.freePoint(creature.position());
    initiative.remove(creature);
    return true;
  }

  public List<Creature> getCreatures() {
    return creatures;
  }

  public List<HasInitiative> getTurnOrder() {
    return initiative.getOrder();
  }

  public boolean setCreaturePosition(Creature creature, Point position) {
    if (!creatures.contains(creature))
      throw new IllegalArgumentException("Creature " + creature + " not in state");
    if (!worldMap.occupyPoint(position, OccupyReason.Creature)) return false;
    worldMap.freePoint(creature.position());
    creature.setPosition(position);
    return true;
  }

  /**
   * Tries to move the actor from its current position to target position.
   *
   * @return True if the move was successful and false otherwise.
   * @throws IllegalArgumentException if actor is not part of the state.
   */
  // TODO add a flag that will allow passing through obstacles
  public boolean moveCreature(Creature actor, Point target) {
    Point start = actor.position();
    if (!creatures.contains(actor)) throw new IllegalArgumentException();

    double distance = worldMap.getDistance(start, target);
    if (distance > actor.movementSpeed) return false;
    // We should never move to an occupied or non-existent point
    if (!worldMap.occupyPoint(target, OccupyReason.Creature)) return false;

    worldMap.freePoint(start);
    actor.setPosition(target);
    actor.movementSpeed -= distance;

    return true;
  }

  /**
   * @return A list of all creatures registered in the map that are in the given distance of the
   *     given point.
   */
  // TODO add an option to ignore obstacles
  public Collection<Creature> getCreaturesWithinDistance(Point point, double distance) {
    List<Point> pointsInDistance = new ArrayList<>();

    for (Pair<Point, Double> pair :
        worldMap.getDistances(point, creatures.stream().map(Creature::position).toList()))
      if (pair.second() <= distance) pointsInDistance.add(pair.first());

    return creatures.stream().filter(c -> pointsInDistance.contains(c.position())).toList();
  }

  public Collection<StateProcess> getPossibleAttacks(Creature actor, Weapon weapon) {
    // Maybe we should check here whether the actor has the weapon?
    // Ditto for actor being the current player (that is actor.equals(initiative.getFirst())

    Collection<WeaponAttack> attacks = weapon.generateAttacks(actor);
    List<StateProcess> list = new ArrayList<>();

    for (WeaponAttack attack : attacks) {
      for (Creature target : getCreaturesWithinDistance(actor.position(), attack.getRange()))
        if (target != actor) {
          list.add(
              new StateProcess() {

                @Override
                public Collection<Creature> getTargets() {
                  return List.of(target);
                }

                @Override
                public Collection<Creature> getSource() {
                  return List.of(actor);
                }

                @Override
                public void run() {
                  attack.resolve(State.this, target);
                }

                @Override
                public String toString() {
                  return attack.toString() + " targeting " + target.toString();
                }
              });
        }
    }
    return list;
  }

  public void nextTurn() {
    initiative.advance();
  }
}
