package org.tcs.model.magic.effect;

import java.util.Collection;
import org.tcs.model.Creature;
import org.tcs.model.State;
import org.tcs.model.magic.SpellEffect;
import org.tcs.model.magic.SpellLevel;

public class HealEffect implements SpellEffect {
  @Override
  public void resolve(
      State state, Creature caster, Collection<Creature> targets, SpellLevel slotLevel) {
    throw new UnsupportedOperationException("not implemented");
  }
}
