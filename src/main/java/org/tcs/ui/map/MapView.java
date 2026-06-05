package org.tcs.ui.map;

import java.util.*;
import java.util.function.Consumer;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.tcs.model.Creature;
import org.tcs.model.geometry.Point;
import org.tcs.model.geometry.RealPoint;
import org.tcs.model.geometry.WorldMap;
import org.tcs.ui.*;
import org.tcs.ui.viewmodel.ViewModel;

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
  private final CollisionView collision = new CollisionView();
  private final DrawablesView drawables;

  private final EditorContextMenu contextMenu;
  private final ObservableObjectValue<PlayView.Mode> modeProperty;
  private Consumer<Point> onAddCreature = null;
  private Consumer<RealPoint> onAddDecoration = null;

  public MapView(
      ViewModel model,
      ObservableObjectValue<PlayView.Mode> modeProperty,
      Map<Creature, Image> creatureImages) {
    drawables = new DrawablesView(creatureImages, model);
    this.modeProperty = modeProperty;
    this.model = model;
    // TODO: handle starting & stopping of the rendering
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        draw();
      }
    }.start();

    // Context menu
    this.contextMenu =
        new EditorContextMenu(
            new ContextMenuHandler() {
              @Override
              public void addPuppet(Point position) {
                onAddCreature.accept(position);
              }

              @Override
              public void addDecoration(RealPoint position) {
                onAddDecoration.accept(position);
              }

              @Override
              public void removeDecoration(Decoration decoration) {
                drawables.removeDecoration(decoration);
              }

              @Override
              public void moveDecorationForward(Decoration decoration) {
                drawables.moveDecorationForward(decoration);
              }

              @Override
              public void moveDecorationBackward(Decoration decoration) {
                drawables.moveDecorationBackward(decoration);
              }

              @Override
              public void moveDecorationToFront(Decoration decoration) {
                drawables.moveDecorationToFront(decoration);
              }

              @Override
              public void moveDecorationToBack(Decoration decoration) {
                drawables.moveDecorationToBack(decoration);
              }
            });
    this.contextMenu.selectedProperty().bind(drawables.selectedProperty());

    modeProperty.addListener(
        (_, _, mode) -> {
          if (!mode.equals(PlayView.Mode.EDIT_PIECES)) {
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

  private RealPoint screenToReal(double x, double y) {
    return new RealPoint(x + camera.x() - getWidth() / 2, y + camera.y() - getHeight() / 2);
  }

  private void updateSelectedTile(MouseEvent event) {
    RealPoint world = screenToReal(event.getX(), event.getY());
    selectedTile = model.getMap().realPointToPoint(world.divide(PIXELS_PER_FOOT));
  }

  private void onMousePressed(MouseEvent event) {
    lastMouse = new RealPoint(event.getX(), event.getY());
    mousePress = new RealPoint(event.getX(), event.getY());
    var world = screenToReal(event.getX(), event.getY());

    if (event.getButton().equals(MouseButton.PRIMARY)) {
      contextMenu.hide();
    }

    PlayView.Mode mode = modeProperty.get();
    if (Objects.requireNonNull(mode) == PlayView.Mode.EDIT_COLLISION) {
      collision.onMousePressed(event.getButton(), world, model.getMap());
    } else {
      drawables.onMousePressed(world, event.getButton());
    }
  }

  private void onMouseDragged(MouseEvent event) {
    var delta = new RealPoint(event.getX() - lastMouse.x(), event.getY() - lastMouse.y());
    RealPoint world = screenToReal(event.getX(), event.getY());

    if (event.isSecondaryButtonDown()) {
      camera = new RealPoint(camera.x() - delta.x(), camera.y() - delta.y());
    }

    if (event.isPrimaryButtonDown()) {
      drawables.onMouseDragged(world);
    }

    updateSelectedTile(event);
    lastMouse = new RealPoint(event.getX(), event.getY());
  }

  private void onMouseReleased(MouseEvent event) {
    RealPoint delta =
        new RealPoint(
            Math.abs(event.getX() - mousePress.x()), Math.abs(event.getY() - mousePress.y()));
    RealPoint world = screenToReal(event.getX(), event.getY());

    // If the mouse drags for too much, we abort the click attempt
    if (delta.x() <= CLICK_BUFFER && delta.y() <= CLICK_BUFFER) {
      WorldMap map = model.getMap();
      Point tile = map.realPointToPoint(world.divide(PIXELS_PER_FOOT));

      if (event.getButton().equals(MouseButton.SECONDARY)
          && modeProperty.get().equals(PlayView.Mode.EDIT_PIECES)) {
        contextMenu.hide();
        contextMenu.show(this, event.getScreenX(), event.getScreenY());
        contextMenu.targetProperty().set(new ContextTarget(world, tile));
      }
    }
  }

  private void draw() {
    WorldMap worldMap = model.getMap();
    final double realTileSize = PIXELS_PER_FOOT;

    var gc = getGraphicsContext2D();
    gc.save();

    var width = getWidth();
    var height = getHeight();

    gc.clearRect(0, 0, width, height);
    gc.setFill(Color.BLACK);
    gc.fillRect(0, 0, width, height);

    // Draw background image, scaled to fill screen while maintaining aspect ratio
    Image background = Assets.BACKGROUND;
    double bgWidth = background.getWidth();
    double bgHeight = background.getHeight();
    double bgAspect = bgWidth / bgHeight;
    double screenAspect = width / height;

    double drawWidth, drawHeight, drawX, drawY;
    if (screenAspect > bgAspect) {
      // Screen is wider than background - fit to width
      drawWidth = width;
      drawHeight = width / bgAspect;
      drawX = 0;
      drawY = (height - drawHeight) / 2;
    } else {
      // Screen is taller than background - fit to height
      drawHeight = height;
      drawWidth = height * bgAspect;
      drawX = (width - drawWidth) / 2;
      drawY = 0;
    }
    gc.drawImage(background, drawX, drawY, drawWidth, drawHeight);

    gc.translate(width / 2 - camera.x(), height / 2 - camera.y());

    double opacity;
    if (modeProperty.get().equals(PlayView.Mode.EDIT_COLLISION)) {
      collision.draw(worldMap, camera, getWidth(), getHeight(), gc);
      opacity = 0.5;
    } else {
      opacity = 1.0;
    }

    gc.setGlobalAlpha(opacity);
    drawables.drawDecorations(gc);
    gc.setGlobalAlpha(1.0);

    drawGrid();

    gc.setGlobalAlpha(opacity);
    drawables.drawPuppets(gc);
    gc.setGlobalAlpha(1.0);

    drawables.drawSelection(gc);

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
    final double realTileSize = PIXELS_PER_FOOT;

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

  public void setOnAddCreature(Consumer<Point> onAddCreature) {
    this.onAddCreature = onAddCreature;
  }

  public void setOnAddDecoration(Consumer<RealPoint> onAddDecoration) {
    this.onAddDecoration = onAddDecoration;
  }

  public void addDecoration(Decoration decoration) {
    drawables.addDecoration(decoration);
  }

  public ObservableValue<Creature> selected() {
    return drawables
        .selectedProperty()
        .map(
            v -> {
              if (v instanceof Puppet p) {
                return p.creature;
              }

              return null;
            });
  }
}
