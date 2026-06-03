package org.tcs.ui.map;

import static org.tcs.ui.map.MapView.PIXELS_PER_FOOT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import org.tcs.model.Creature;
import org.tcs.model.geometry.RealPoint;
import org.tcs.model.geometry.WorldMap;
import org.tcs.ui.*;

class DrawablesView {
  private final ObjectProperty<Drawable> selected = new SimpleObjectProperty<>();
  private final List<Decoration> decorations = new ArrayList<>();
  private Map<Creature, Puppet> puppets = new HashMap<>();
  private final Map<Creature, Image> creatureImages;
  private final ViewModel model;

  public DrawablesView(Map<Creature, Image> creatureImages, ViewModel model) {
    this.creatureImages = creatureImages;
    this.model = model;

    // Sync creatures
    syncCreatures(model.creaturesProperty());
    model
        .creaturesProperty()
        .addListener((ListChangeListener<Creature>) c -> syncCreatures(c.getList()));
  }

  private void syncCreatures(List<? extends Creature> creatures) {
    WorldMap worldMap = model.getMap();
    var newCreatures = new HashMap<Creature, Puppet>();
    for (Creature creature : creatures) {
      RealPoint position =
          worldMap
              .pointToRealPoint(creature.position())
              .multiply(worldMap.getPointSize() * PIXELS_PER_FOOT);
      Puppet puppet;
      if (puppets.containsKey(creature)) {
        puppet = puppets.get(creature);
      } else {
        puppet =
            new Puppet(
                creature,
                creatureImages.getOrDefault(creature, Assets.PLACEHOLDER),
                worldMap.getPointSize() * PIXELS_PER_FOOT);
      }

      puppet.setPosition(position);
      newCreatures.put(creature, puppet);
    }

    puppets = newCreatures;
  }

  public ObjectProperty<Drawable> selectedProperty() {
    return selected;
  }

  public void clearSelection() {
    selected.set(null);
  }

  public void onMousePressed(RealPoint world, MouseButton button) {
    if (button.equals(MouseButton.PRIMARY)) clearSelection();

    for (Puppet puppet : puppets.values()) {
      if (puppet.contains(world)) {
        selected.set(puppet);
        return;
      }
    }
    for (int i = decorations.size() - 1; i >= 0; i--) {
      Decoration decoration = decorations.get(i);
      if (decoration.contains(world)) {
        selected.set(decoration);
        return;
      }
    }
  }

  public void onMouseDragged(RealPoint position) {
    WorldMap map = model.getMap();

    if (selected.get() instanceof Puppet puppet) {
      // Since puppets snap to the grid, the position has to be rounded to nearest tile center.
      double tileSize = PIXELS_PER_FOOT * map.getPointSize();
      model.setCreaturePosition(puppet.creature, map.realPointToPoint(position.divide(tileSize)));
    } else if (selected.get() instanceof Decoration decoration) {
      decoration.setPosition(position);
    }
  }

  public void drawDecorations(GraphicsContext gc) {
    for (Decoration decoration : decorations) {
      decoration.draw(gc);
    }
  }

  public void drawPuppets(GraphicsContext gc) {
    for (Puppet puppet : puppets.values()) {
      puppet.draw(gc);
    }
  }

  public void drawSelection(GraphicsContext gc) {
    if (selected.get() != null) {
      gc.setStroke(Color.BLUE);
      RealPoint pos = selected.get().position();
      Rectangle2D extent = selected.get().extent();
      gc.strokeRect(
          pos.x() + extent.getMinX(),
          pos.y() + extent.getMinY(),
          extent.getWidth(),
          extent.getHeight());
    }
  }

  public void addDecoration(Decoration decoration) {
    decorations.add(decoration);
  }

  public void removeDecoration(Decoration decoration) {
    decorations.remove(decoration);
    if (selected.get() == decoration) selected.set(null);
  }

  public void moveDecorationForward(Decoration decoration) {
    var index = decorations.indexOf(decoration);
    if (index >= 0 && index < decorations.size() - 1) {
      var temp = decorations.get(index + 1);
      decorations.set(index + 1, decoration);
      decorations.set(index, temp);
    }
  }

  public void moveDecorationBackward(Decoration decoration) {
    var index = decorations.indexOf(decoration);
    if (index > 0) {
      var temp = decorations.get(index - 1);
      decorations.set(index - 1, decoration);
      decorations.set(index, temp);
    }
  }

  public void moveDecorationToFront(Decoration decoration) {
    removeDecoration(decoration);
    decorations.add(decoration);
  }

  public void moveDecorationToBack(Decoration decoration) {
    removeDecoration(decoration);
    decorations.addFirst(decoration);
  }
}
