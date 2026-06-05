package org.tcs.ui;

import java.util.ArrayList;
import java.util.Collection;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tcs.model.Creature;
import org.tcs.model.HasInitiative;
import org.tcs.model.State;
import org.tcs.model.equipment.Weapon;
import org.tcs.model.geometry.Point;
import org.tcs.model.geometry.WorldMap;

public class ViewModel {
  private final State model;
  private final ObservableList<Creature> creatures = FXCollections.observableArrayList();
  private final SimpleStringProperty creatureName = new SimpleStringProperty("");
  private final SimpleIntegerProperty creatureHitpoints = new SimpleIntegerProperty(0);
  private final SimpleStringProperty creatureHitpointsError = new SimpleStringProperty("");
  private final SimpleDoubleProperty creatureMovement = new SimpleDoubleProperty(0.0);
  private final SimpleStringProperty creatureMovementError = new SimpleStringProperty("");
  private final ObservableList<Creature> initiativeQueue = FXCollections.observableArrayList();
  private final ObjectProperty<Creature> selected = new SimpleObjectProperty<>();
  private final ObservableList<Weapon> weapons = FXCollections.observableArrayList();

  public ViewModel(State model) {
    this.model = model;
    creatures.setAll(model.getCreatures());

    creatureHitpointsError.bind(
        Bindings.createStringBinding(
            () -> {
              int hitpoints = creatureHitpoints.get();
              if (hitpoints <= 0) {
                return "Hitpoints must be positive";
              }
              return "";
            },
            creatureHitpoints));

    creatureMovementError.bind(
        Bindings.createStringBinding(
            () -> {
              double movement = creatureMovement.get();
              if (movement <= 0) {
                return "Movement speed must be positive";
              }
              return "";
            },
            creatureMovement));

    update();

    selected.addListener(_ -> updateSelected());
  }

  public WorldMap getMap() {
    return model.getMap();
  }

  private void updateSelected() {
    if (selected.get() == null) return;
    weapons.setAll(selected.get().getWeapons());
  }

  private void update() {
    var list = new ArrayList<Creature>();

    for (HasInitiative creature : model.getTurnOrder()) {
      if (creature instanceof Creature c) {
        list.add(c);
      }
    }

    initiativeQueue.setAll(list);
  }

  public void addCreature(Creature creature) {
    if (model.addCreature(creature)) {
      creatures.add(creature);
    }

    update();
  }

  public void setCreaturePosition(Creature creature, Point position) {
    // Simply ignore failures to move
    try {
      model.setCreaturePosition(creature, position);
      creatures.setAll(model.getCreatures());
    } catch (IllegalArgumentException _) {
    }
  }

  public ObservableList<Creature> creaturesProperty() {
    return creatures;
  }

  public Property<String> creatureNameProperty() {
    return creatureName;
  }

  public IntegerProperty creatureHitpointsProperty() {
    return creatureHitpoints;
  }

  public String getCreatureHitpointsError() {
    return creatureHitpointsError.get();
  }

  public ObservableStringValue creatureHitpointsErrorProperty() {
    return creatureHitpointsError;
  }

  public DoubleProperty creatureMovementProperty() {
    return creatureMovement;
  }

  public String getCreatureMovementError() {
    return creatureMovementError.get();
  }

  public ObservableStringValue creatureMovementErrorProperty() {
    return creatureMovementError;
  }

  public void resetCreatureData() {
    creatureName.set("");
    creatureHitpoints.set(0);
    creatureMovement.set(0.0);
  }

  public Creature makeCreature(Point position) {
    try {
      String name = creatureName.get();
      int hitpoints = creatureHitpoints.get();
      double movement = creatureMovement.get();

      if (hitpoints <= 0 || movement <= 0) {
        throw new IllegalStateException("Invalid creature data");
      }

      resetCreatureData();

      return new Creature(name, position, hitpoints, movement);
    } catch (NumberFormatException e) {
      throw new IllegalStateException("Invalid creature data");
    }
  }

  public Collection<Runnable> getWeaponAttacks(Weapon weapon) {
    return model.getPossibleAttacks(selected.get(), weapon);
  }

  public ObservableList<Creature> initiativeQueueProperty() {
    return initiativeQueue;
  }

  public ObjectProperty<Creature> selectedProperty() {
    return selected;
  }

  public ObservableList<Weapon> weaponsProperty() {
    return weapons;
  }
}
