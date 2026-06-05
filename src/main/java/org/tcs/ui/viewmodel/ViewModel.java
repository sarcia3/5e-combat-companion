package org.tcs.ui.viewmodel;

import java.util.ArrayList;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tcs.model.Creature;
import org.tcs.model.HasInitiative;
import org.tcs.model.State;
import org.tcs.model.geometry.Point;
import org.tcs.model.geometry.WorldMap;

public class ViewModel {
  private final State model;
  public final CreatureWizardViewModel creatureWizard = new CreatureWizardViewModel();
  public final CreatureViewModel creature;
  public final CreatureEditViewModel creatureEdit = new CreatureEditViewModel();
  private final ObservableList<Creature> creatures = FXCollections.observableArrayList();
  private final ObservableList<Creature> initiativeQueue = FXCollections.observableArrayList();
  private final ObjectProperty<Creature> currentCreature = new SimpleObjectProperty<>();

  public ViewModel(State model) {
    this.model = model;
    creature = new CreatureViewModel(model, currentCreature);
    creature.setOnPass(
        () -> {
          model.nextTurn();
          update();
        });
    creatures.setAll(model.getCreatures());

    update();
  }

  public WorldMap getMap() {
    return model.getMap();
  }

  private void update() {
    var list = new ArrayList<Creature>();

    for (HasInitiative creature : model.getTurnOrder()) {
      if (creature instanceof Creature c) {
        list.add(c);
      }
    }

    if (list.isEmpty()) {
      currentCreature.set(null);
    } else {
      currentCreature.set(list.getFirst());
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

  public void setCreatureName(Creature creature, String name) {
    String uniqueName = name;
    int counter = 0;

    while (true) {
      String testName = uniqueName;
      boolean isDuplicate =
          creatures.stream().filter(c -> c != creature).anyMatch(c -> c.name().equals(testName));

      if (!isDuplicate) {
        creature.setName(uniqueName);
        creatures.setAll(model.getCreatures());
        break;
      }

      counter++;
      uniqueName = name + " #" + counter;
    }
  }

  public ObservableList<Creature> creaturesProperty() {
    return creatures;
  }

  public ObservableList<Creature> initiativeQueueProperty() {
    return initiativeQueue;
  }
}
