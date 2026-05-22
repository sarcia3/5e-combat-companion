package org.tcs.model;

import java.util.*;
import org.tcs.model.geometry.*;
import org.tcs.model.util.Pair;

@SuppressWarnings("unused")
public class State {
  InitiativeTracker initiative;
  Map<Creature, Point> creaturePositions;
  WorldMap worldMap;

  State(WorldMap worldMap) {
    creaturePositions = new HashMap<>();
    this.worldMap = worldMap;
  }

  State(Collection<? extends Creature> creatures, WorldMap worldMap) {
    this.worldMap = worldMap;
    creaturePositions = new HashMap<>();
    initiative = new InitiativeTracker(creatures);
    // TODO add positions and add creatures to positions
  }

  /**
   * Adds a creature to the state.
   *
   * @return False if given creature already exists in this state or the position is already
   *     occupied.
   */
 public boolean addCreature(Creature creature, Point position) {
    // TODO Change the InitiativeTracker so that we can add new creatures to it
    if (worldMap.isPointOccupied(position)) return false;
    boolean notExisted = creaturePositions.putIfAbsent(creature, position) == null;
    if (notExisted) worldMap.occupyPoint(position);
    return notExisted;
  }

  /**
   * Tries to move the actor from its current position to target position.
   *
   * @return True if the move was successful and false otherwise.
   * @throws IllegalArgumentException if actor is not part of the state.
   */
  // TODO add a flag that will allow passing through obstacles
  public boolean moveCreature(Creature actor, Point target) {
    Point start = creaturePositions.get(actor);
    if (start == null) throw new IllegalArgumentException();
    double distance = worldMap.getDistance(start, target);
    if (distance > actor.movementSpeed) return false;
    // We should never move to an occupied or non-existent point
    if (!worldMap.occupyPoint(target)) return false;
    worldMap.freePoint(start);
    creaturePositions.put(actor, target);
    // TODO change movementSpeed to double
    actor.movementSpeed -= (int) distance;
    return true;
  }

  /**
   * @return A list of all creatures registered in the map that are in the given distance of the
   *     given point.
   */
  // TODO add an option to ignore obstacles
  public Collection<Creature> getCreaturesWithinDistance(Point point, Double distance) {
    List<Point> pointsInDistance = new ArrayList<>();
    for (Pair<Point, Double> pair : worldMap.getDistance(point, creaturePositions.values()))
      if (pair.second() <= distance) pointsInDistance.add(pair.first());
    List<Creature> list = new ArrayList<>();
    for (Map.Entry<Creature, Point> entry : creaturePositions.entrySet())
      if (pointsInDistance.contains(entry.getValue())) list.add(entry.getKey());
    return list;
  }

}
