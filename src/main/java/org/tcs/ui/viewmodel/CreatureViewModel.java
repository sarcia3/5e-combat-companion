package org.tcs.ui.viewmodel;

import java.util.EnumMap;
import java.util.Map;
import javafx.beans.property.*;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tcs.model.Ability;
import org.tcs.model.Creature;
import org.tcs.model.State;
import org.tcs.model.StateProcess;
import org.tcs.model.equipment.Armor;
import org.tcs.model.equipment.Weapon;
import org.tcs.model.geometry.NavMap;
import org.tcs.model.magic.Spell;

public class CreatureViewModel {
  private final State model;
  private final ObjectProperty<Creature> creature = new SimpleObjectProperty<>();
  private final BooleanProperty isCurrent = new SimpleBooleanProperty(false);
  private final ObservableList<Weapon> storedWeapons = FXCollections.observableArrayList();
  private final ObservableList<Weapon> equippedWeapons = FXCollections.observableArrayList();
  private final SimpleDoubleProperty movementLeft = new SimpleDoubleProperty(0.0);
  private final StringProperty wornArmor = new SimpleStringProperty("none");
  private final IntegerProperty armorClass = new SimpleIntegerProperty(0);
  private final ObservableList<Spell> spells = FXCollections.observableArrayList();
  private final ObservableList<StateProcess> processes = FXCollections.observableArrayList();
  private final Map<Ability, IntegerProperty> abilityScores = new EnumMap<>(Ability.class);
  private final Map<Ability, IntegerProperty> abilityModifiers = new EnumMap<>(Ability.class);

  private NavMap navMap;
  private Runnable onPass = () -> {};

  public CreatureViewModel(State model, ObservableObjectValue<Creature> current) {
    this.model = model;

    for (Ability ability : Ability.values()) {
      abilityScores.put(ability, new SimpleIntegerProperty(-1));
      abilityModifiers.put(ability, new SimpleIntegerProperty(-1));
    }

    creature.addListener(
        _ -> {
          if (creature.get() == null) return;
          reloadEquipment();
          reloadSpells();
          reloadAbilities();
          reloadNavMap();
        });
    isCurrent.bind(creature.isEqualTo(current));
  }

  public void loadAttacks(Weapon weapon) {
    processes.setAll(model.getPossibleAttacks(creature.get(), weapon));
  }

  public void addStoredWeapon(Weapon weapon) {
    creature.get().inventory().addStoredWeapon(weapon);
    reloadEquipment();
  }

  /** Tries to equip a stored weapon. Returns false if there are not enough free hands. */
  public boolean equip(Weapon weapon) {
    boolean equipped = creature.get().inventory().equipWeapon(weapon);
    if (equipped) reloadEquipment();
    return equipped;
  }

  public void equip(Armor armor) {
    creature.get().inventory().setWornArmor(armor);
    reloadEquipment();
  }

  /** Unequips an equipped weapon, freeing its hand(s). */
  public void unequip(Weapon weapon) {
    creature.get().inventory().unequipWeapon(weapon);
    reloadEquipment();
  }

  private void reloadEquipment() {
    storedWeapons.setAll(creature.get().inventory().getStoredWeapons());
    equippedWeapons.setAll(creature.get().inventory().getEquippedWeapons());
    Armor armor = creature.get().inventory().wornArmor();
    wornArmor.set(armor != null ? armor.name() : "none");
    armorClass.set(creature.get().armorClass());
  }

  public void loadSpellProcesses(Spell spell) {
    processes.setAll(model.getPossibleSpells(creature.get(), spell));
  }

  private void reloadSpells() {
    spells.setAll(creature.get().spellcasting().getSpells());
  }

  public void addSpell(Spell spell) {
    creature.get().spellcasting().addSpell(spell);
    reloadSpells();
  }

  public void removeSpell(Spell spell) {
    creature.get().spellcasting().removeSpell(spell);
    reloadSpells();
  }

  public void pass() {
    onPass.run();
  }

  public void setOnPass(Runnable onPass) {
    this.onPass = onPass;
  }

  public NavMap navMap() {
    return navMap;
  }

  void reloadNavMap() {
    if (creature.get() == null) return;
    movementLeft.set(creature.get().movementLeft());
    navMap = model.getMap().navMap(creature.get().position(), creature.get().movementLeft());
  }

  private void reloadAbilities() {
    for (Ability ability : Ability.values()) {
      abilityScores.get(ability).set(creature.get().abilityScore(ability));
      abilityModifiers.get(ability).set(creature.get().abilityModifier(ability));
    }
  }

  public DoubleProperty movementLeftProperty() {
    return movementLeft;
  }

  public ObjectProperty<Creature> creatureProperty() {
    return creature;
  }

  public ObservableList<Weapon> storedWeaponsProperty() {
    return storedWeapons;
  }

  public ObservableList<Weapon> equippedWeaponsProperty() {
    return equippedWeapons;
  }

  public StringProperty wornArmorProperty() {
    return wornArmor;
  }

  public IntegerProperty armorClassProperty() {
    return armorClass;
  }

  public ObservableList<Spell> spellsProperty() {
    return spells;
  }

  public ObservableList<StateProcess> processesProperty() {
    return processes;
  }

  public IntegerProperty abilityScoreProperty(Ability ability) {
    return abilityScores.get(ability);
  }

  public IntegerProperty abilityModifierProperty(Ability ability) {
    return abilityModifiers.get(ability);
  }

  public BooleanProperty isCurrentProperty() {
    return isCurrent;
  }
}
