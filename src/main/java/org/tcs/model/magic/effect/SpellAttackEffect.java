package org.tcs.model.magic.effect;

import java.util.Collection;
import org.tcs.model.Creature;
import org.tcs.model.Damage;
import org.tcs.model.State;
import org.tcs.model.dice.DamageRoll;
import org.tcs.model.magic.SpellEffect;
import org.tcs.model.magic.SpellLevel;

public class SpellAttackEffect implements SpellEffect {

  private final DamageRoll damage;

  public SpellAttackEffect(DamageRoll damage) {
    this.damage = damage;
  }

  @Override
  public void resolve(
      State state, Creature caster, Collection<Creature> targets, SpellLevel slotLevel) {
    // todo if we are fancy. use slotLeve to have the spells scale with spell slot level increase.
    // What would mean having two distinct DamageRoll variables
    for (var target : targets) {
      int rolledDie = caster.diceRoller().roll(20);

      if (rolledDie == 1
          || rolledDie != 20
              && rolledDie + caster.spellcasting().spellAttackBonus() < target.armorClass()) {
        continue;
      }

      Damage total;
      if (rolledDie == 20) {
        total = damage.critical().resolve(caster.diceRoller(), caster);
      } else {
        // caster.diceRoller(), caster feels redudnant todo rethink
        total = damage.resolve(caster.diceRoller(), caster);
      }

      target.takeDamage(total);
    }
  }
}
