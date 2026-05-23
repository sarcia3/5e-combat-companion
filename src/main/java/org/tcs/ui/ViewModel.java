package org.tcs.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tcs.model.Creature;
import org.tcs.model.State;
import org.tcs.model.geometry.Point;
import org.tcs.model.geometry.WorldMap;

public class ViewModel {
  private final State model;
  private final ObservableList<Creature> creatures = FXCollections.observableArrayList();

  public ViewModel(State model) {
    this.model = model;
  }

  public WorldMap getMap() {
    return model.getMap();
  }

  public void addCreature(Creature creature) {
    if (model.addCreature(creature)) {
      creatures.add(creature);
    } else {
      System.err.println("Failed to add creature");
    }
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
}
