package org.tcs.model.magic;

public record SpellLevel(int value) {
  public SpellLevel{
    if(value > 9)
      throw new IllegalArgumentException("Spells of level 10 and higher forbidden by the Goddess of Magic");
    if(value < 0)
      throw new IllegalArgumentException("Spells cannot have a negative level");
  }

  public boolean isCantrip(){
    return value == 0;
  }
}
