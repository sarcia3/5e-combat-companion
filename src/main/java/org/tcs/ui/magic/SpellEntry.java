package org.tcs.ui.magic;

import javafx.scene.control.Label;
import org.tcs.model.magic.Spell;

public class SpellEntry extends Label {
  public final Spell spell;

  public SpellEntry(Spell spell) {
    this.spell = spell;
    setText(spell.name());
  }
}
