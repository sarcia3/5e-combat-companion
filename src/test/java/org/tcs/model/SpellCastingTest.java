package org.tcs.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.junit.jupiter.api.Test;
import org.tcs.model.dice.DamageRoll;
import org.tcs.model.dice.DiceRoller;
import org.tcs.model.geometry.Finite2DGrid;
import org.tcs.model.geometry.Point;
import org.tcs.model.geometry.RealPoint;
import org.tcs.model.geometry.WorldMap;
import org.tcs.model.magic.CastingTime;
import org.tcs.model.magic.Ranged;
import org.tcs.model.magic.SingleCreature;
import org.tcs.model.magic.Spell;
import org.tcs.model.magic.SpellLevel;
import org.tcs.model.magic.Spellcasting;
import org.tcs.model.magic.effect.SpellAttackEffect;

class SpellCastingTest {

  /** A DiceRoller that returns a predetermined sequence of values, ignoring requested dice size. */
  private static final class QueuedDiceRoller implements DiceRoller {
    private final Queue<Integer> values;

    QueuedDiceRoller(int... rolls) {
      this.values = new ArrayDeque<>();
      for (int r : rolls) values.add(r);
    }

    @Override
    public int roll(int numberOfSides, RollInformation information) {
      if (numberOfSides < 1) throw new IllegalArgumentException();
      return values.poll();
    }
  }

  private static Spell fireBolt() {
    return new Spell(
        "Fire Bolt",
        "A mote of fire streaks toward a creature.",
        new SpellLevel(0),
        new Ranged(120),
        new SingleCreature(),
        CastingTime.ACTION,
        new SpellAttackEffect(DamageRoll.parse("1d10 fire")));
  }

  @Test
  void castingFireBoltOnAHitDealsDamageAndConsumesTheAction() {
    WorldMap map = new Finite2DGrid(10, 10);
    Point casterPoint = map.realPointToPoint(new RealPoint(0, 0));
    Point targetPoint = map.realPointToPoint(new RealPoint(1, 0));

    // Caster's rolls, consumed in order: 12 = initiative (rolled when State is built),
    // 15 = d20 attack roll, 7 = d10 fire damage.
    Creature caster =
        new Creature.Builder()
            .name("Wizard")
            .position(casterPoint)
            .hitPointMaximum(20)
            .proficiencyBonus(2)
            .movementSpeed(30)
            .diceRoller(new QueuedDiceRoller(12, 15, 7))
            .build();
    Creature target =
        new Creature.Builder()
            .name("Goblin")
            .position(targetPoint)
            .hitPointMaximum(10)
            .movementSpeed(30)
            .build();

    Spell fireBolt = fireBolt();
    // Caster has INT-based spellcasting and knows Fire Bolt; no slots needed (cantrip).
    caster.spellcasting = new Spellcasting(List.of(fireBolt), Map.of(), Ability.INT, caster);

    State state = new State(List.of(caster, target), map);

    // Spell attack bonus = prof(2) + INT mod(0) = 2; 15 + 2 = 17 >= target AC 10 -> hit.
    StateProcess cast =
        state.getPossibleSpells(caster, fireBolt).stream()
            .filter(process -> process.getTarget() == target)
            .findFirst()
            .orElseThrow();
    cast.run();

    assertEquals(3, target.hitPoints()); // 10 - 7 fire damage
    assertFalse(caster.turnTracker.hasAction()); // the action was spent
  }
}
