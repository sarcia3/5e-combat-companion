package org.tcs.model;

import java.util.*;
import org.tcs.model.geometry.*;
import org.tcs.model.util.Pair;

@SuppressWarnings("unused")
public class State {
  InitiativeTracker initiative;
  List<Creature> creatures = new ArrayList<>();
  WorldMap worldMap;

  State(WorldMap worldMap) {
    this.worldMap = worldMap;
  }

  State(Collection<? extends Creature> creatures, WorldMap worldMap) {
    this.worldMap = worldMap;
    initiative = new InitiativeTracker(creatures);
    // TODO add positions and add creatures to positions
  }

  /**
   * Adds a creature to the state.
   *
   * @return False if given creature already exists in this state or the position is already
   *     occupied.
   */
  public boolean addCreature(Creature creature) {
    // TODO Change the InitiativeTracker so that we can add new creatures to it
    if (!worldMap.occupyPoint(creature.position())) return false;
    if (creatures.contains(creature)) return false;
    creatures.add(creature);
    return true;
  }

  public void setCreaturePosition(Creature creature, Point position) {
    if (!creatures.contains(creature)) throw new IllegalArgumentException();
    worldMap.freePoint(creature.position());
    if (!worldMap.occupyPoint(position)) throw new IllegalArgumentException();
    creature.setPosition(position);
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
    if (!worldMap.occupyPoint(target)) return false;

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
}
