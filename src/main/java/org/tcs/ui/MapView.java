package org.tcs.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.tcs.model.geometry.Point;
import org.tcs.model.geometry.RealPoint;
import org.tcs.model.geometry.WorldMap;

public class MapView extends Canvas {
  public static final double PX_PER_FT = 120;
  private final ViewModel model;
  private RealPoint camera = new RealPoint(0, 0);
  // Tracker for drag-based navigation.
  // TODO: Calling it a `RealPoint` is misleading. A dedicated record would be more fitting
  private RealPoint lastMouse = new RealPoint(0, 0);
  // Tracker for distinguishing clicks from drags
  // TODO: Calling it a `RealPoint` is misleading. A dedicated record would be more fitting
  private RealPoint mousePress = new RealPoint(0, 0);
  private Point selectedTile = null;
  private Decoration selectedDecoration = null;
  private RealPoint contextTarget = new RealPoint(0, 0);
  // This is a temporary solution. This won't be here (in that state) in the future
  private final List<Decoration> decorations = new ArrayList<>();

  private final ContextMenu contextMenu;

  public MapView(ViewModel model) {
    this.model = model;
    // TODO: handle starting & stopping of the rendering
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        draw();
      }
    }.start();

    // Context menu
    this.contextMenu = new ContextMenu();

    var addMenu = new Menu("Add...");
    var addAsset = new MenuItem("...decoration");

    addAsset.setOnAction(_ -> decorations.add(new Decoration(contextTarget, Assets.truck)));

    addMenu.getItems().add(addAsset);
    contextMenu.getItems().add(addMenu);

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

    if (delta.x() <= 10 && delta.y() <= 10) {
      if (event.getButton().equals(MouseButton.SECONDARY)) {
        contextTarget = world;
        contextMenu.hide();
        contextMenu.show(this, event.getScreenX(), event.getScreenY());
      }

      WorldMap map = model.getMap();
      final double realTileSize = PX_PER_FT * map.getPointSize();
      selectedTile =
          map.realPointToPoint((new RealPoint(world.x() / realTileSize, world.y() / realTileSize)));
    }
  }

  private void onMouseDragged(MouseEvent event) {
    if (event.isSecondaryButtonDown()) {
      var delta = new RealPoint(event.getX() - lastMouse.x(), event.getY() - lastMouse.y());
      camera = new RealPoint(camera.x() - delta.x(), camera.y() - delta.y());
      lastMouse = new RealPoint(event.getX(), event.getY());
    }
  }

  private void onMousePressed(MouseEvent event) {
    lastMouse = new RealPoint(event.getX(), event.getY());
    mousePress = new RealPoint(event.getX(), event.getY());
    selectedDecoration = null;

    var world = screenToReal(event.getX(), event.getY());
    for (int i = decorations.size() - 1; i >= 0; i--) {
      Decoration decoration = decorations.get(i);
      if (decoration.contains(world)) {
        selectedDecoration = decoration;
        return;
      }
    }

    if (event.getButton().equals(MouseButton.PRIMARY)) {
      contextMenu.hide();
    }
  }

  private void draw() {
    WorldMap map = model.getMap();
    final double realTileSize = PX_PER_FT * map.getPointSize();

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

    // Highlight selected
    if (selectedDecoration != null) {
      gc.setStroke(Color.BLUE);
      RealPoint pos = selectedDecoration.getPosition();
      Rectangle2D extent = selectedDecoration.getExtent();
      gc.strokeRect(
          pos.x() + extent.getMinX(),
          pos.y() + extent.getMinY(),
          extent.getWidth(),
          extent.getHeight());
    }

    // Highlight selected square
    if (selectedTile != null) {
      var selectedReal = map.pointToRealPoint(selectedTile);
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
}
