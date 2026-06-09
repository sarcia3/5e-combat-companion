package org.tcs.ui.viewmodel;

import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import org.tcs.model.Creature;
import org.tcs.model.dice.DiceRoller;
import org.tcs.model.geometry.Point;

public class CreatureWizardViewModel {
  private final Creature.Builder builder = new Creature.Builder();

  private final SimpleStringProperty creatureName = new SimpleStringProperty("");
  private final SimpleIntegerProperty creatureHitpoints = new SimpleIntegerProperty(20);
  private final SimpleStringProperty creatureHitpointsError = new SimpleStringProperty("");
  private final SimpleDoubleProperty creatureMovement = new SimpleDoubleProperty(10.0);
  private final SimpleStringProperty creatureMovementError = new SimpleStringProperty("");

  public CreatureWizardViewModel() {
    creatureName.addListener(onChange(builder::name));

    creatureHitpoints.addListener(onChange(v -> builder.hitPointMaximum(v.intValue())));

    creatureHitpointsError.bind(
        Bindings.createStringBinding(
            () -> {
              int hitPoints = creatureHitpoints.get();
              if (hitPoints <= 0) {
                return "Hitpoints must be positive";
              }
              return "";
            },
            creatureHitpoints));

    creatureMovement.addListener(onChange(v -> builder.movementSpeed(v.intValue())));

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

  public Creature makeCreature(Point position, DiceRoller diceRoller) {
    try {
      int hitPoints = creatureHitpoints.get();
      double movement = creatureMovement.get();

      if (hitPoints <= 0 || movement <= 0) {
        throw new IllegalStateException("Invalid creature data");
      }

      resetCreatureData();

      return new Creature.Builder().position(position).diceRoller(diceRoller).build();
    } catch (NumberFormatException e) {
      throw new IllegalStateException("Invalid creature data");
    }
  }

  static <T> ChangeListener<T> onChange(Consumer<T> action) {
    return (_, _, v) -> action.accept(v);
  }
}
