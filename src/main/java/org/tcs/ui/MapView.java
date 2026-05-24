package org.tcs.ui;

import java.util.*;
import java.util.function.Consumer;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.tcs.model.Creature;
import org.tcs.model.geometry.OccupyReason;
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

  private Consumer<RealPoint> onAddDecoration;
  private Consumer<Point> onAddCreature;
  private final SimpleBooleanProperty showDecorationOptions = new SimpleBooleanProperty(false);
  private final ObservableObjectValue<PlayView.Mode> modeProperty;
  private final Map<Creature, Image> creatureImages;

  MapView(
      ViewModel model,
      ObservableObjectValue<PlayView.Mode> modeProperty,
      Map<Creature, Image> creatureImages) {
    this.creatureImages = creatureImages;
    this.modeProperty = modeProperty;
    this.model = model;
    // TODO: handle starting & stopping of the rendering
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        draw();
      }
    }.start();

    // Sync creatures
    syncCreatures(model.creaturesProperty());
    model
        .creaturesProperty()
        .addListener(
            (ListChangeListener<Creature>)
                c -> {
                  syncCreatures(c.getList());
                });

    // Context menu
    this.contextMenu = new ContextMenu();

    var addMenu = new Menu("Add...");

    var addDecoration = new MenuItem("...decoration");
    addDecoration.setOnAction(
        _ -> {
          if (onAddDecoration != null) onAddDecoration.accept(contextTarget.first());
        });

    var addCreature = new MenuItem("...creature");
    addCreature.setOnAction(
        _ -> {
          if (onAddCreature != null) onAddCreature.accept(contextTarget.second());
        });

    addMenu.getItems().addAll(addCreature, addDecoration);

    var moveUp = new MenuItem("Move forward");
    moveUp.setOnAction(_ -> moveDecorationForward((Decoration) selectedDrawable));
    var moveDown = new MenuItem("Move backward");
    moveDown.setOnAction(_ -> moveDecorationBackward((Decoration) selectedDrawable));
    var moveToTop = new MenuItem("Move to front");
    moveToTop.setOnAction(_ -> moveDecorationToFront((Decoration) selectedDrawable));
    var moveToBottom = new MenuItem("Move to back");
    moveToBottom.setOnAction(_ -> moveDecorationToBack((Decoration) selectedDrawable));
    var delete = new MenuItem("Delete");
    delete.setOnAction(_ -> removeDecoration((Decoration) selectedDrawable));

    var decorationOptions = List.of(moveUp, moveDown, moveToTop, moveToBottom, delete);
    for (MenuItem option : decorationOptions) {
      option.visibleProperty().bind(showDecorationOptions);
      option.disableProperty().bind(showDecorationOptions.not());
    }

    contextMenu.getItems().addAll(addMenu, new SeparatorMenuItem());
    contextMenu.getItems().addAll(decorationOptions);

    modeProperty.addListener(
        (_, _, mode) -> {
          if (!mode.equals(PlayView.Mode.EDIT_PIECES)) {
            selectedDrawable = null;
            contextMenu.hide();
          }
        });

    // Input handling
    setOnMouseExited(_ -> selectedTile = null);
    setOnMouseMoved(this::updateSelectedTile);
    setOnMousePressed(this::onMousePressed);
    setOnMouseDragged(this::onMouseDragged);
    setOnMouseReleased(this::onMouseReleased);
  }

  private void syncCreatures(List<? extends Creature> creatures) {
    WorldMap map = model.getMap();
    var newCreatures = new HashMap<Creature, Puppet>();
    for (Creature creature : creatures) {
      RealPoint position =
          map.pointToRealPoint(creature.position()).multiply(map.getPointSize() * PIXELS_PER_FOOT);
      if (puppets.containsKey(creature)) {
        Puppet puppet = puppets.get(creature);
        puppet.setPosition(position);
        newCreatures.put(creature, puppet);
      } else {
        var randomImage =
            new ArrayList<>(Assets.images.values()).get(new Random().nextInt(Assets.images.size()));
        creatureImages.put(creature, randomImage);
        System.out.println("Adding creature " + creature + " at " + creature.position());
        Puppet puppet = new Puppet(creature, randomImage, map.getPointSize() * PIXELS_PER_FOOT);
        puppet.setPosition(position);
        newCreatures.put(creature, puppet);
      }
    }

    puppets = newCreatures;
  }

  private RealPoint screenToReal(double x, double y) {
    return new RealPoint(x + camera.x() - getWidth() / 2, y + camera.y() - getHeight() / 2);
  }

  private void updateSelectedTile(MouseEvent event) {
    RealPoint world = screenToReal(event.getX(), event.getY());
    var realTileSize = PIXELS_PER_FOOT * model.getMap().getPointSize();
    selectedTile = model.getMap().realPointToPoint(world.divide(realTileSize));
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
      Point tile = map.realPointToPoint(world.divide(realTileSize));

      if (event.getButton().equals(MouseButton.SECONDARY)
          && modeProperty.get().equals(PlayView.Mode.EDIT_PIECES)) {
        contextTarget = new Pair<>(world, tile);
        contextMenu.hide();
        contextMenu.show(this, event.getScreenX(), event.getScreenY());
      }
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

    updateSelectedTile(event);
    lastMouse = new RealPoint(event.getX(), event.getY());
  }

  private void onMousePressed(MouseEvent event) {
    lastMouse = new RealPoint(event.getX(), event.getY());
    mousePress = new RealPoint(event.getX(), event.getY());
    selectedDrawable = null;
    showDecorationOptions.set(false);
    var world = screenToReal(event.getX(), event.getY());

    if (modeProperty.get().equals(PlayView.Mode.EDIT_COLLISION)) {
      if (event.getButton().equals(MouseButton.PRIMARY)) {
        WorldMap worldMap = model.getMap();
        var tile =
            worldMap.realPointToPoint(world.divide(worldMap.getPointSize() * PIXELS_PER_FOOT));

        if (worldMap.getOccupyReason(tile) == OccupyReason.Terrain) {
          worldMap.freePoint(tile);
        } else if (worldMap.getOccupyReason(tile) == null) {
          worldMap.occupyPoint(tile, OccupyReason.Terrain);
        }
      }

      return;
    }

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
        showDecorationOptions.set(true);
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

    double opacity;
    if (modeProperty.get().equals(PlayView.Mode.EDIT_COLLISION)) {
      drawCollision();
      opacity = 0.5;
    } else {
      opacity = 1.0;
    }

    gc.setGlobalAlpha(opacity);
    for (Decoration decoration : decorations) {
      decoration.draw(gc);
    }
    gc.setGlobalAlpha(1.0);

    drawGrid();

    gc.setGlobalAlpha(opacity);
    for (Puppet puppet : puppets.values()) {
      puppet.draw(gc);
    }
    gc.setGlobalAlpha(1.0);

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

  private void drawGrid() {
    double width = getWidth();
    double height = getHeight();
    var gc = getGraphicsContext2D();
    final double realTileSize = PIXELS_PER_FOOT * model.getMap().getPointSize();

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
  }

  // I don't like this whole method. It works exclusively with a grid while trying to not admit
  // to work exclusively with a grid.
  private void drawCollision() {
    WorldMap worldMap = model.getMap();
    final double realTileSize = PIXELS_PER_FOOT * worldMap.getPointSize();

    var gc = getGraphicsContext2D();

    double minWorldX = camera.x() - getWidth() / 2;
    double maxWorldX = camera.x() + getWidth() / 2;
    double minWorldY = camera.y() - getHeight() / 2;
    double maxWorldY = camera.y() + getHeight() / 2;

    double minTileX = Math.floor(minWorldX / realTileSize);
    double maxTileX = Math.ceil(maxWorldX / realTileSize);
    double minTileY = Math.floor(minWorldY / realTileSize);
    double maxTileY = Math.ceil(maxWorldY / realTileSize);

    for (double tileX = minTileX; tileX <= maxTileX; tileX += 1.0) {
      for (double tileY = minTileY; tileY <= maxTileY; tileY += 1.0) {
        Point point = worldMap.realPointToPoint(new RealPoint(tileX + 0.5, tileY + 0.5));
        if (!worldMap.checkInBounds(point)) continue;
        OccupyReason occupyReason = worldMap.getOccupyReason(point);

        Color fillColor;
        if (occupyReason != null && occupyReason.equals(OccupyReason.Terrain)) {
          fillColor = Color.rgb(255, 0, 0, 0.5); // Red for occupied
        } else {
          fillColor = Color.rgb(0, 255, 0, 0.3); // Green for free
        }

        gc.setFill(fillColor);
        double screenX = tileX * realTileSize;
        double screenY = tileY * realTileSize;
        gc.fillRect(screenX, screenY, realTileSize, realTileSize);
      }
    }
  }

  public void setOnAddCreature(Consumer<Point> onAddCreature) {
    this.onAddCreature = onAddCreature;
  }

  public void addCreature(Creature creature, Image image) {
    puppets.put(
        creature, new Puppet(creature, image, model.getMap().getPointSize() * PIXELS_PER_FOOT));
    model.addCreature(creature);
  }

  public void setOnAddDecoration(Consumer<RealPoint> onAddDecoration) {
    this.onAddDecoration = onAddDecoration;
  }

  public void addDecoration(Decoration decoration) {
    decorations.add(decoration);
  }

  public void removeDecoration(Decoration decoration) {
    decorations.remove(decoration);
    if (selectedDrawable == decoration) selectedDrawable = null;
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
