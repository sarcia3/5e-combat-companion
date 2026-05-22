package org.tcs.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.tcs.model.geometry.Finite2DGrid;
import org.tcs.model.geometry.Point;
import org.tcs.model.geometry.RealPoint;
import org.tcs.model.geometry.WorldMap;

class StateTest {

  @Test
  void canCreateGameStateWithGivenCreatures() {
    Creature first = new Creature("Boring commoner", 4, 30);
    Creature second = new Creature("Cool commoner", 5, 30);
    ArrayList<Creature> commoners = new ArrayList<>();
    commoners.add(first);
    commoners.add(second);
    State state = new State(commoners, new Finite2DGrid(1, 1));
    assertEquals(2, state.initiative.size());
  }

  static class getCreaturesWithinDistanceTests {
    @Test
    void emptyMap() {
      WorldMap worldMap = new Finite2DGrid(1, 1);
      State state = new State(worldMap);
      Point point = worldMap.realPointToPoint(new RealPoint(0, 0));
      assertEquals(0, state.getCreaturesWithinDistance(point, 1.).size());
    }

    @Test
    void simpleTest() {
      WorldMap worldMap = new Finite2DGrid(3, 3);
      State state = new State(worldMap);
      Point point1 = worldMap.realPointToPoint(new RealPoint(0, 0));
      Point point2 = worldMap.realPointToPoint(new RealPoint(1, 0));
      Point point3 = worldMap.realPointToPoint(new RealPoint(0, 1));
      Point checkingPoint = worldMap.realPointToPoint(new RealPoint(1, 1));

      Creature creature1 = new Creature("Creature", 10, 2);
      Creature creature2 = new Creature("Creature", 10, 2);
      Creature creature3 = new Creature("Creature", 10, 2);
      assertTrue(state.addCreature(creature1, point1));
      assertTrue(state.addCreature(creature2, point2));
      assertTrue(state.addCreature(creature3, point3));
      Collection<Creature> collection = state.getCreaturesWithinDistance(checkingPoint, 1.);
      assertEquals(2, collection.size());
      assertFalse(collection.contains(creature1));
      assertTrue(collection.contains(creature2));
      assertTrue(collection.contains(creature3));
    }
  }
}
