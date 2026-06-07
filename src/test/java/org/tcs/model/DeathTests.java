package org.tcs.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.tcs.model.dice.DiceRoller;
import org.tcs.model.equipment.Weapon;
import org.tcs.model.equipment.WeaponsLibrary;
import org.tcs.model.geometry.Finite2DGrid;
import org.tcs.model.geometry.Point;
import org.tcs.model.geometry.RealPoint;
import org.tcs.model.geometry.WorldMap;

public class DeathTests {
  @Test
  public void normalSlowDeath() {
    WorldMap map = new Finite2DGrid(1, 2);
    State state = new State(map);
    Point point1 = map.realPointToPoint(new RealPoint(0, 0));
    Point point2 = map.realPointToPoint(new RealPoint(0, 1));
    DiceRoller dice1 =
        new DiceRoller() {
          @Override
          public int roll(int numberOfSides, RollInformation information) {
            return 19 % numberOfSides;
          }
        };
    DiceRoller dice2 =
        new DiceRoller() {
          @Override
          public int roll(int numberOfSides, RollInformation information) {
            return 2 % numberOfSides;
          }
        };
    Creature creature1 = new Creature("X", point1, 10, 10., 0, dice1);
    Creature creature2 = new Creature("Y", point2, 10, 10., 0, dice2);
    state.addCreature(creature1);
    state.addCreature(creature2);
    WeaponsLibrary.load();
    Weapon dag = WeaponsLibrary.getWeaponByName("Dagger");
    creature1.inventory().addCarriedWeapon(dag);
    creature1.inventory().wieldWeapon(dag);
    StateProcess attack = state.getPossibleAttacks(creature1, dag).stream().toList().getFirst();
    while (creature2.hitPoints != 0) attack.run();
    for (int i = 0; i < 6; i++) state.nextTurn();
    assertFalse(state.getCreatures().contains(creature2));
  }

  @Test
  public void recovery() {
    WorldMap map = new Finite2DGrid(1, 2);
    State state = new State(map);
    Point point1 = map.realPointToPoint(new RealPoint(0, 0));
    Point point2 = map.realPointToPoint(new RealPoint(0, 1));
    DiceRoller dice1 =
        new DiceRoller() {
          @Override
          public int roll(int numberOfSides, RollInformation information) {
            return 19 % numberOfSides;
          }
        };
    DiceRoller dice2 =
        new DiceRoller() {
          @Override
          public int roll(int numberOfSides, RollInformation information) {
            return 19 % numberOfSides + 1;
          }
        };
    Creature creature1 = new Creature("X", point1, 10, 10., 0, dice1);
    Creature creature2 = new Creature("Y", point2, 10, 10., 0, dice2);
    state.addCreature(creature1);
    state.addCreature(creature2);
    WeaponsLibrary.load();
    Weapon dag = WeaponsLibrary.getWeaponByName("Dagger");
    creature1.inventory().addCarriedWeapon(dag);
    creature1.inventory().wieldWeapon(dag);
    StateProcess attack = state.getPossibleAttacks(creature1, dag).stream().toList().getFirst();
    while (creature2.hitPoints != 0) attack.run();
    for (int i = 0; i < 2; i++) state.nextTurn();
    assertEquals(1, creature2.hitPoints);
    assertTrue(state.getCreatures().contains(creature2));
  }

  @Test
  public void suddenDeath() {
    WorldMap map = new Finite2DGrid(1, 2);
    State state = new State(map);
    Point point1 = map.realPointToPoint(new RealPoint(0, 0));
    Point point2 = map.realPointToPoint(new RealPoint(0, 1));
    DiceRoller dice1 =
        new DiceRoller() {
          @Override
          public int roll(int numberOfSides, RollInformation information) {
            return 19 % numberOfSides;
          }
        };
    Creature creature1 = new Creature("X", point1, 10, 10., 0, dice1);
    Creature creature2 = new Creature("Y", point2, 1, 10.);
    state.addCreature(creature1);
    state.addCreature(creature2);
    WeaponsLibrary.load();
    Weapon dag = WeaponsLibrary.getWeaponByName("Dagger");
    creature1.inventory().addCarriedWeapon(dag);
    creature1.inventory().wieldWeapon(dag);
    StateProcess attack = state.getPossibleAttacks(creature1, dag).stream().toList().getFirst();
    attack.run();
    assertFalse(state.getCreatures().contains(creature2));
  }

  @Test
  public void damageSlowDeath() {
    WorldMap map = new Finite2DGrid(1, 2);
    State state = new State(map);
    Point point1 = map.realPointToPoint(new RealPoint(0, 0));
    Point point2 = map.realPointToPoint(new RealPoint(0, 1));
    DiceRoller dice1 =
        new DiceRoller() {
          @Override
          public int roll(int numberOfSides, RollInformation information) {
            return 19 % numberOfSides;
          }
        };
    DiceRoller dice2 =
        new DiceRoller() {
          @Override
          public int roll(int numberOfSides, RollInformation information) {
            return 2 % numberOfSides;
          }
        };
    Creature creature1 = new Creature("X", point1, 10, 10., 0, dice1);
    Creature creature2 = new Creature("Y", point2, 10, 10., 0, dice2);
    state.addCreature(creature1);
    state.addCreature(creature2);
    WeaponsLibrary.load();
    Weapon dag = WeaponsLibrary.getWeaponByName("Dagger");
    creature1.inventory().addCarriedWeapon(dag);
    creature1.inventory().wieldWeapon(dag);
    StateProcess attack = state.getPossibleAttacks(creature1, dag).stream().toList().getFirst();
    while (creature2.hitPoints != 0) attack.run();
    attack.run();
    attack.run();
    attack.run();
    assertFalse(state.getCreatures().contains(creature2));
  }
}
