package org.tcs.ui.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableStringValue;
import org.tcs.model.Creature;
import org.tcs.model.geometry.Point;

public class CreatureWizardViewModel {
  private final SimpleStringProperty creatureName = new SimpleStringProperty("");
  private final SimpleIntegerProperty creatureHitpoints = new SimpleIntegerProperty(0);
  private final SimpleStringProperty creatureHitpointsError = new SimpleStringProperty("");
  private final SimpleDoubleProperty creatureMovement = new SimpleDoubleProperty(0.0);
  private final SimpleStringProperty creatureMovementError = new SimpleStringProperty("");

  public CreatureWizardViewModel() {
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
      int hitPoints = creatureHitpoints.get();
      double movement = creatureMovement.get();

      if (hitPoints <= 0 || movement <= 0) {
        throw new IllegalStateException("Invalid creature data");
      }

      resetCreatureData();

      return new Creature(name, position, hitPoints, movement);
    } catch (NumberFormatException e) {
      throw new IllegalStateException("Invalid creature data");
    }
  }
}
