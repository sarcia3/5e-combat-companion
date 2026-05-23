package org.tcs.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import javafx.animation.AnimationTimer;
import javafx.collections.ListChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.tcs.model.Creature;
import org.tcs.model.geometry.Point;
import org.tcs.model.geometry.RealPoint;
import org.tcs.model.geometry.WorldMap;
import org.tcs.model.util.Pair;

public class MapView extends Canvas {
  private static final int CLICK_BUFFER = 10;
  public static final double PIXELS_PER_FOOT = 120;
  private final ViewModel model;
  private RealPoint camera = new RealPoint(0, 0);
  // Tracker for drag-based navigation.
  // TODO: Calling it a `RealPoint` is misleading. A dedicated record would be more fitting
  private RealPoint lastMouse = new RealPoint(0, 0);
  // Tracker for distinguishing clicks from drags
  // TODO: Calling it a `RealPoint` is misleading. A dedicated record would be more fitting
  private RealPoint mousePress = new RealPoint(0, 0);
  private Point selectedTile = null;
  private Drawable selectedDrawable = null;
  private Pair<RealPoint, Point> contextTarget = null;
  // This is a temporary solution. This won't be here (in that state) in the future
  private final List<Decoration> decorations = new ArrayList<>();
  private HashMap<Creature, Puppet> puppets = new HashMap<>();

  private final ContextMenu contextMenu;

  private Consumer<Pair<RealPoint, Point>> onAddDecoration;
  private Consumer<Pair<RealPoint, Point>> onAddCreature;

  public MapView(ViewModel model) {
    this.model = model;
    // TODO: handle starting & stopping of the rendering
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        draw();
      }
    }.start();

    // Sync creatures
    model
        .creaturesProperty()
        .addListener(
            (ListChangeListener<Creature>)
                c -> {
                  WorldMap map = model.getMap();
                  var newCreatures = new HashMap<Creature, Puppet>();
                  for (Creature creature : c.getList()) {
                    RealPoint position =
                        map.pointToRealPoint(creature.position())
                            .multiply(map.getPointSize() * PIXELS_PER_FOOT);
                    if (puppets.containsKey(creature)) {
                      Puppet puppet = puppets.get(creature);
                      puppet.setPosition(position);
                      newCreatures.put(creature, puppet);
                    } else {
                      newCreatures.put(
                          creature,
                          new Puppet(
                              creature, Assets.PLACEHOLDER, map.getPointSize() * PIXELS_PER_FOOT));
                    }
                  }

                  puppets = newCreatures;
                });

    // Context menu
    this.contextMenu = new ContextMenu();

    var addMenu = new Menu("Add...");

    var addDecoration = new MenuItem("...decoration");
    addDecoration.setOnAction(
        _ -> {
          if (onAddDecoration != null) onAddDecoration.accept(contextTarget);
        });

    var addCreature = new MenuItem("...creature");
    addCreature.setOnAction(
        _ -> {
          if (onAddCreature != null) onAddCreature.accept(contextTarget);
        });

    addMenu.getItems().addAll(addCreature, addDecoration);
    contextMenu.getItems().add(addMenu);

    // Input handling
    setOnMousePressed(this::onMousePressed);
    setOnMouseDragged(this::onMouseDragged);
    setOnMouseReleased(this::onMouseReleased);
  }

  private RealPoint screenToReal(double x, double y) {
    return new RealPoint(x + camera.x() - getWidth() / 2, y + camera.y() - getHeight() / 2);
  }

  private void onMouseReleased(MouseEvent event) {
    RealPoint delta =
        new RealPoint(
            Math.abs(event.getX() - mousePress.x()), Math.abs(event.getY() - mousePress.y()));
    RealPoint world = screenToReal(event.getX(), event.getY());

    // If the mouse drags for too much, we abort the click attempt
    if (delta.x() <= CLICK_BUFFER && delta.y() <= CLICK_BUFFER) {
      WorldMap map = model.getMap();
      final double realTileSize = PIXELS_PER_FOOT * map.getPointSize();

      if (event.getButton().equals(MouseButton.SECONDARY)) {
        contextTarget = new Pair<>(world, map.realPointToPoint(world.divide(realTileSize)));
        contextMenu.hide();
        contextMenu.show(this, event.getScreenX(), event.getScreenY());
      }

      selectedTile = map.realPointToPoint(world.divide(realTileSize));
    }
  }

  private void onMouseDragged(MouseEvent event) {
    var delta = new RealPoint(event.getX() - lastMouse.x(), event.getY() - lastMouse.y());

    if (event.isSecondaryButtonDown()) {
      camera = new RealPoint(camera.x() - delta.x(), camera.y() - delta.y());
    } else if (event.isPrimaryButtonDown() && selectedDrawable != null) {
      RealPoint world = screenToReal(event.getX(), event.getY());
      if (selectedDrawable instanceof Puppet puppet) {
        // Since puppets snap to the grid, the position has to be rounded to nearest tile center.
        WorldMap map = model.getMap();
        double tileSize = PIXELS_PER_FOOT * map.getPointSize();
        model.setCreaturePosition(puppet.creature, map.realPointToPoint(world.divide(tileSize)));
      } else if (selectedDrawable instanceof Decoration decoration) {
        decoration.setPosition(world);
      }
    }

    lastMouse = new RealPoint(event.getX(), event.getY());
  }

  private void onMousePressed(MouseEvent event) {
    lastMouse = new RealPoint(event.getX(), event.getY());
    mousePress = new RealPoint(event.getX(), event.getY());
    selectedDrawable = null;

    var world = screenToReal(event.getX(), event.getY());
    for (Puppet puppet : puppets.values()) {
      if (puppet.contains(world)) {
        selectedDrawable = puppet;
        return;
      }
    }
    for (int i = decorations.size() - 1; i >= 0; i--) {
      Decoration decoration = decorations.get(i);
      if (decoration.contains(world)) {
        selectedDrawable = decoration;
        return;
      }
    }

    if (event.getButton().equals(MouseButton.PRIMARY)) {
      contextMenu.hide();
    }
  }

  private void draw() {
    WorldMap worldMap = model.getMap();
    final double realTileSize = PIXELS_PER_FOOT * worldMap.getPointSize();

    var gc = getGraphicsContext2D();
    gc.save();

    var width = getWidth();
    var height = getHeight();

    gc.clearRect(0, 0, width, height);
    gc.setFill(Color.BLACK);
    gc.fillRect(0, 0, width, height);

    gc.translate(width / 2 - camera.x(), height / 2 - camera.y());

    // Draw grid
    double minWorldX = camera.x() - width / 2;
    double maxWorldX = camera.x() + width / 2;
    double minWorldY = camera.y() - height / 2;
    double maxWorldY = camera.y() + height / 2;

    double firstGridX = Math.floor(minWorldX / realTileSize) * realTileSize;
    double firstGridY = Math.floor(minWorldY / realTileSize) * realTileSize;

    gc.setStroke(Color.GRAY);
    gc.setLineWidth(1);

    // Draw vertical lines
    for (double x = firstGridX; x <= maxWorldX; x += realTileSize) {
      gc.strokeLine(x, minWorldY, x, maxWorldY);
    }

    // Draw horizontal lines
    for (double y = firstGridY; y <= maxWorldY; y += realTileSize) {
      gc.strokeLine(minWorldX, y, maxWorldX, y);
    }

    for (Decoration decoration : decorations) {
      decoration.draw(gc);
    }

    for (Puppet puppet : puppets.values()) {
      puppet.draw(gc);
    }

    // Highlight selected
    if (selectedDrawable != null) {
      gc.setStroke(Color.BLUE);
      RealPoint pos = selectedDrawable.position();
      Rectangle2D extent = selectedDrawable.extent();
      gc.strokeRect(
          pos.x() + extent.getMinX(),
          pos.y() + extent.getMinY(),
          extent.getWidth(),
          extent.getHeight());
    }

    // Highlight selected square
    if (selectedTile != null) {
      var selectedReal = worldMap.pointToRealPoint(selectedTile);
      var selectedScreen =
          new RealPoint(selectedReal.x() * realTileSize, selectedReal.y() * realTileSize);
      gc.setFill(Color.rgb(255, 255, 255, 0.3));
      gc.fillRect(
          selectedScreen.x() - realTileSize / 2,
          selectedScreen.y() - realTileSize / 2,
          realTileSize,
          realTileSize);
    }

    gc.restore();
  }

  public void setOnAddCreature(Consumer<Pair<RealPoint, Point>> onAddCreature) {
    this.onAddCreature = onAddCreature;
  }

  public void addCreature(Creature creature, Image image) {
    puppets.put(
        creature, new Puppet(creature, image, model.getMap().getPointSize() * PIXELS_PER_FOOT));
    model.addCreature(creature);
  }

  public void setOnAddDecoration(Consumer<Pair<RealPoint, Point>> onAddDecoration) {
    this.onAddDecoration = onAddDecoration;
  }

  public void addDecoration(Decoration decoration) {
    decorations.add(decoration);
  }
}
