package org.tcs.model;

import java.util.*;
import org.tcs.model.activity.WeaponAttack;
import org.tcs.model.geometry.*;
import org.tcs.model.util.Pair;

@SuppressWarnings("unused")
public class State {
  InitiativeTracker initiative;
  List<Creature> creatures = new ArrayList<>();
  WorldMap worldMap;
  TurnHandler turnHandler;

  public State(WorldMap worldMap) {
    this.worldMap = worldMap;
  }

  public State(Collection<? extends Creature> creatures, WorldMap worldMap) {
    this.worldMap = worldMap;
    initiative = new InitiativeTracker(creatures);
  }

  public WorldMap getMap() {
    return worldMap;
  }

  /**
   * Adds a creature to the state.
   *
   * @return False if given creature already exists in this state or the position is already
   *     occupied.
   */
  public boolean addCreature(Creature creature) {
    if (creatures.contains(creature)) return false;
    if (!worldMap.occupyPoint(creature.position(), OccupyReason.Creature)) return false;
    creatures.add(creature);
    initiative.add(creature);
    return true;
  }

  public List<Creature> getCreatures() {
    return creatures;
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

  public TurnHandler getTurnHandler() {
    if (turnHandler != null) return turnHandler;

    List<Runnable> list = new ArrayList<>();

    // I don't know how to resolve it in future if the state doesn't know what's hiding under this.
    // Maybe the hasInitiative interface is too much abstraction for our scope.
    Creature actor = (Creature) initiative.getFirst();

    for (WeaponAttack attack : actor.getPossibleAttacks()) {
      for (Creature target : getCreaturesWithinDistance(actor.position(), attack.getRange())) {
        list.add(
            new Runnable() {
              @Override
              public void run() {
                attack.resolve(State.this, target);
                initiative.advance();
                turnHandler = null;
                // There should be something here to notify the model?
                // Or maybe we can decorate it inside the TurnHandler.
              }

              @Override
              public String toString() {
                return attack.toString() + " targeting " + target.toString();
              }
            });
      }
    }

    turnHandler = new TurnHandler(list);
    return turnHandler;
  }
}
