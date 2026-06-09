package org.tcs.model.magic;

import java.util.Collection;
import org.tcs.model.Creature;
import org.tcs.model.State;

public interface SpellEffect {
  void resolve(State state, Creature caster, Collection<Creature> Targets, SpellLevel slotLevel);
}
