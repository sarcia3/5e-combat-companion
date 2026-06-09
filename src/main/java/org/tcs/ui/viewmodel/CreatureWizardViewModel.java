package org.tcs.ui.viewmodel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.*;
import org.tcs.model.Creature;
import org.tcs.model.Damage;
import org.tcs.model.dice.DiceRoller;
import org.tcs.model.geometry.Point;

public class CreatureWizardViewModel {
  private final Creature.Builder builder = new Creature.Builder();

  private final SimpleStringProperty creatureName = new SimpleStringProperty("");
  private final SimpleIntegerProperty creatureHitpoints = new SimpleIntegerProperty(20);
  private final SimpleStringProperty creatureHitpointsError = new SimpleStringProperty("");
  private final SimpleDoubleProperty creatureMovement = new SimpleDoubleProperty(10.0);
  private final SimpleStringProperty creatureMovementError = new SimpleStringProperty("");

  private final ObservableSet<Damage.Type> resistances =
      FXCollections.observableSet(EnumSet.noneOf(Damage.Type.class));
  private final ObservableSet<Damage.Type> vulnerabilities =
      FXCollections.observableSet(EnumSet.noneOf(Damage.Type.class));
  private final ObservableSet<Damage.Type> immunities =
      FXCollections.observableSet(EnumSet.noneOf(Damage.Type.class));

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

    resistances.addListener(onSetChange(l -> builder.resistances(new ArrayList<>(l))));
    vulnerabilities.addListener(onSetChange(l -> builder.vulnerabilities(new ArrayList<>(l))));
    immunities.addListener(onSetChange(l -> builder.immunities(new ArrayList<>(l))));

    resetCreatureData();
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

  public ObservableSet<Damage.Type> resistancesProperty() {
    return resistances;
  }

  public ObservableSet<Damage.Type> vulnerabilitiesProperty() {
    return vulnerabilities;
  }

  public ObservableSet<Damage.Type> immunitiesProperty() {
    return immunities;
  }

  public ObservableStringValue creatureMovementErrorProperty() {
    return creatureMovementError;
  }

  public void resetCreatureData() {
    creatureName.set("");
    creatureHitpoints.set(0);
    creatureMovement.set(0.0);

    resistances.clear();
    vulnerabilities.clear();
    immunities.clear();
  }

  public Creature makeCreature(Point position, DiceRoller diceRoller) {
    try {
      int hitPoints = creatureHitpoints.get();
      double movement = creatureMovement.get();

      if (hitPoints <= 0 || movement <= 0) {
        throw new IllegalStateException("Invalid creature data");
      }

      Creature creature = builder.position(position).diceRoller(diceRoller).build();

      resetCreatureData();

      return creature;
    } catch (NumberFormatException e) {
      throw new IllegalStateException("Invalid creature data");
    }
  }

  private static <T> ChangeListener<T> onChange(Consumer<T> action) {
    return (_, _, v) -> action.accept(v);
  }

  private static <T> SetChangeListener<T> onSetChange(Consumer<ObservableSet<? extends T>> action) {
    return c -> action.accept(c.getSet());
  }
}
