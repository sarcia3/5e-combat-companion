package org.tcs.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.tcs.model.equipment.Weapon;
import org.tcs.model.equipment.WeaponsLibrary;
import org.tcs.model.geometry.*;

class StateTest {

  @Test
  void canCreateGameStateWithGivenCreatures() {
    Creature first = new Creature("Boring commoner", null, 4, 30);
    Creature second = new Creature("Cool commoner", null, 5, 30);
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

      Creature creature1 = new Creature("Creature", point1, 10, 2);
      Creature creature2 = new Creature("Creature", point2, 10, 2);
      Creature creature3 = new Creature("Creature", point3, 10, 2);
      state.addCreature(creature1);
      state.addCreature(creature2);
      state.addCreature(creature3);

      Collection<Creature> collection = state.getCreaturesWithinDistance(checkingPoint, 1.);
      assertEquals(2, collection.size());
      assertFalse(collection.contains(creature1));
      assertTrue(collection.contains(creature2));
      assertTrue(collection.contains(creature3));
    }
  }

  public static class GetPossibleAttacksTest {
    @Test
    public void simpleScenario() {
      // A day in London
      WeaponsLibrary.load();
      WorldMap map = new Finite2DGrid(10, 10);
      Weapon dagger = WeaponsLibrary.get("Dagger");
      List<Creature> list = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        Point point = map.realPointToPoint(new RealPoint(i, i));
        list.add(new Creature("Commoner " + i, point, 10, 10));
        list.getLast().inventory().addStoredWeapon(dagger);
        list.getLast().inventory().equipWeapon(dagger);
      }

      State state = new State(list, map);
      for (int i = 0; i < 5; i++) {
        int expected = dagger.generateAttacks(list.get(i)).size();
        if (i == 0 || i == 4) expected *= 3;
        else expected *= 4;
        assertEquals(expected, state.getPossibleAttacks(list.get(i), dagger).size());
      }
    }

    @Test
    public void wallScenario() {
      WeaponsLibrary.load();
      WorldMap map = new Finite2DGrid(3, 3);
      Point point1 = map.realPointToPoint(new RealPoint(0, 0));
      Point point2 = map.realPointToPoint(new RealPoint(2, 2));
      Weapon dagger = WeaponsLibrary.get("Dagger");
      Creature creature1 = new Creature("Commoner 1", point1, 10, 10);
      Creature creature2 = new Creature("Commoner 2", point2, 10, 10);
      State state = new State(List.of(creature1, creature2), map);
      creature1.inventory().addStoredWeapon(dagger);
      creature1.inventory().equipWeapon(dagger);
      creature2.inventory().addStoredWeapon(dagger);
      creature2.inventory().equipWeapon(dagger);
      for (int i = 0; i < 3; i++) {
        Point point = map.realPointToPoint(new RealPoint(1, i));
        map.occupyPoint(point, OccupyReason.Terrain);
      }
      assertEquals(0, state.getPossibleAttacks(creature1, dagger).size());
      assertEquals(0, state.getPossibleAttacks(creature2, dagger).size());
    }

    @Test
    public void storedButNotEquippedWeaponCannotBeUsedToAttack() {
      WeaponsLibrary.load();
      WorldMap map = new Finite2DGrid(3, 3);
      Weapon dagger = WeaponsLibrary.get("Dagger");
      Creature attacker =
          new Creature("Attacker", map.realPointToPoint(new RealPoint(0, 0)), 10, 10);
      Creature target = new Creature("Target", map.realPointToPoint(new RealPoint(1, 0)), 10, 10);
      State state = new State(List.of(attacker, target), map);

      attacker.inventory().addStoredWeapon(dagger); // stored only, never equipped

      assertThrows(
          IllegalArgumentException.class, () -> state.getPossibleAttacks(attacker, dagger));
    }

    @Test
    public void equippedWeaponCanBeUsedToAttack() {
      WeaponsLibrary.load();
      WorldMap map = new Finite2DGrid(3, 3);
      Weapon dagger = WeaponsLibrary.get("Dagger");
      Creature attacker =
          new Creature("Attacker", map.realPointToPoint(new RealPoint(0, 0)), 10, 10);
      Creature target = new Creature("Target", map.realPointToPoint(new RealPoint(1, 0)), 10, 10);
      State state = new State(List.of(attacker, target), map);

      attacker.inventory().addStoredWeapon(dagger);
      attacker.inventory().equipWeapon(dagger);

      assertFalse(state.getPossibleAttacks(attacker, dagger).isEmpty());
    }
  }
}
