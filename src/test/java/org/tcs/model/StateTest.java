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
    Creature first =
        new Creature.Builder()
            .name("Boring commoner")
            .proficiencyBonus(4)
            .movementSpeed(30)
            .build();
    Creature second =
        new Creature.Builder().name("Cool commoner").proficiencyBonus(5).movementSpeed(30).build();
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

      Creature creature1 =
          new Creature.Builder()
              .name("Creature")
              .position(point1)
              .hitPointMaximum(10)
              .movementSpeed(2)
              .build();
      Creature creature2 =
          new Creature.Builder()
              .name("Creature")
              .position(point2)
              .hitPointMaximum(10)
              .movementSpeed(2)
              .build();
      Creature creature3 =
          new Creature.Builder()
              .name("Creature")
              .position(point3)
              .hitPointMaximum(10)
              .movementSpeed(2)
              .build();
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
    public void wallScenario() {
      WorldMap map = new Finite2DGrid(3, 3);
      Point point1 = map.realPointToPoint(new RealPoint(0, 0));
      Point point2 = map.realPointToPoint(new RealPoint(2, 2));
      Weapon dagger = WeaponsLibrary.get("Dagger");
      Creature creature1 =
          new Creature.Builder()
              .name("Commoner 1")
              .position(point1)
              .hitPointMaximum(10)
              .movementSpeed(10)
              .build();
      Creature creature2 =
          new Creature.Builder()
              .name("Commoner 2")
              .position(point2)
              .hitPointMaximum(10)
              .movementSpeed(10)
              .build();
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
      WorldMap map = new Finite2DGrid(3, 3);
      Weapon dagger = WeaponsLibrary.get("Dagger");
      Creature attacker =
          new Creature.Builder()
              .name("Attacker")
              .position(map.realPointToPoint(new RealPoint(0, 0)))
              .hitPointMaximum(10)
              .movementSpeed(10)
              .build();
      Creature target =
          new Creature.Builder()
              .name("Target")
              .position(map.realPointToPoint(new RealPoint(1, 0)))
              .hitPointMaximum(10)
              .movementSpeed(10)
              .build();
      State state = new State(List.of(attacker, target), map);

      attacker.inventory().addStoredWeapon(dagger); // stored only, never equipped

      assertThrows(
          IllegalArgumentException.class, () -> state.getPossibleAttacks(attacker, dagger));
    }

    @Test
    public void equippedWeaponCanBeUsedToAttack() {
      WorldMap map = new Finite2DGrid(3, 3);
      Weapon dagger = WeaponsLibrary.get("Dagger");
      Creature attacker =
          new Creature.Builder()
              .name("Attacker")
              .position(map.realPointToPoint(new RealPoint(0, 0)))
              .hitPointMaximum(10)
              .movementSpeed(10)
              .build();
      Creature target =
          new Creature.Builder()
              .name("Target")
              .position(map.realPointToPoint(new RealPoint(1, 0)))
              .hitPointMaximum(10)
              .movementSpeed(10)
              .build();
      State state = new State(List.of(attacker, target), map);

      attacker.inventory().addStoredWeapon(dagger);
      attacker.inventory().equipWeapon(dagger);

      assertFalse(state.getPossibleAttacks(attacker, dagger).isEmpty());
    }
  }
}
