package org.tcs.ui.viewmodel;

import javafx.beans.property.*;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tcs.model.Creature;
import org.tcs.model.State;
import org.tcs.model.StateProcess;
import org.tcs.model.equipment.Weapon;
import org.tcs.model.geometry.NavMap;

public class CreatureViewModel {
  private final State model;
  private final ObjectProperty<Creature> creature = new SimpleObjectProperty<>();
  private final BooleanProperty isCurrent = new SimpleBooleanProperty(false);
  private final ObservableList<Weapon> storedWeapons = FXCollections.observableArrayList();
  private final ObservableList<Weapon> equippedWeapons = FXCollections.observableArrayList();
  private final ObservableList<StateProcess> attacks = FXCollections.observableArrayList();
  private final SimpleDoubleProperty movementLeft = new SimpleDoubleProperty(0.0);
  private NavMap navMap;
  private Runnable onPass = () -> {};

  public CreatureViewModel(State model, ObservableObjectValue<Creature> current) {
    this.model = model;

    creature.addListener(
        _ -> {
          if (creature.get() == null) return;
          reloadWeapons();
          reloadNavMap();
        });
    isCurrent.bind(creature.isEqualTo(current));
  }

  public void loadAttacks(Weapon weapon) {
    attacks.setAll(model.getPossibleAttacks(creature.get(), weapon));
  }

  public boolean addStoredWeapon(Weapon weapon) {
    return creature.get().inventory().addStoredWeapon(weapon);
  }

  /** Tries to equip a stored weapon. Returns false if there are not enough free hands. */
  public boolean equip(Weapon weapon) {
    boolean equipped = creature.get().inventory().equipWeapon(weapon);
    if (equipped) reloadWeapons();
    return equipped;
  }

  /** Unequips an equipped weapon, freeing its hand(s). */
  public void unequip(Weapon weapon) {
    creature.get().inventory().unequipWeapon(weapon);
    reloadWeapons();
  }

  private void reloadWeapons() {
    storedWeapons.setAll(creature.get().inventory().getStoredWeapons());
    equippedWeapons.setAll(creature.get().inventory().getEquippedWeapons());
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

  public ObservableList<StateProcess> attacksProperty() {
    return attacks;
  }

  public BooleanProperty isCurrentProperty() {
    return isCurrent;
  }
}
