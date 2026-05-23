package org.tcs.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tcs.model.Creature;
import org.tcs.model.State;
import org.tcs.model.geometry.Point;
import org.tcs.model.geometry.RealPoint;
import org.tcs.model.geometry.WorldMap;
import org.tcs.model.util.Pair;

public class ViewModel {
  private final State model;
  private Pair<RealPoint, Point> target = null;
  private Drawable.Type addType = null;
  private final ObservableList<Creature> creatures = FXCollections.observableArrayList();

  public ViewModel(State model) {
    this.model = model;
  }

  public WorldMap getMap() {
    return model.getMap();
  }

  public void setAddParams(Pair<RealPoint, Point> target, Drawable.Type addType) {
    this.target = target;
    this.addType = addType;
  }

  public Pair<RealPoint, Point> getAddTarget() {
    return target;
  }

  public Drawable.Type getAddType() {
    return addType;
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
