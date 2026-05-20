package org.tcs.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.tcs.model.geometry.RealPoint;

public class MapView extends Canvas {
  private static final double TILE_SIZE = 150;
  private RealPoint camera = new RealPoint(0, 0);
  private RealPoint lastMouse = new RealPoint(0, 0);
  private RealPoint mousePress = new RealPoint(0, 0);
  private RealPoint selectedTile = null;

  public MapView() {
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        draw();
      }
    }.start();

    setOnMousePressed(
        event -> {
          lastMouse = new RealPoint(event.getX(), event.getY());
          mousePress = new RealPoint(event.getX(), event.getY());
        });

    setOnMouseDragged(
        event -> {
          RealPoint delta =
              new RealPoint(event.getX() - lastMouse.x(), event.getY() - lastMouse.y());
          camera = new RealPoint(camera.x() - delta.x(), camera.y() - delta.y());
          lastMouse = new RealPoint(event.getX(), event.getY());
        });

    setOnMouseClicked(
        event -> {
          RealPoint delta =
              new RealPoint(
                  Math.abs(event.getX() - mousePress.x()), Math.abs(event.getY() - mousePress.y()));

          if (delta.x() <= 50 && delta.y() <= 50) {
            RealPoint world =
                new RealPoint(
                    event.getX() + camera.x() - getWidth() / 2,
                    event.getY() + camera.y() - getHeight() / 2);
            RealPoint tile =
                new RealPoint(Math.floor(world.x() / TILE_SIZE), Math.floor(world.y() / TILE_SIZE));
            selectedTile = tile;
            System.out.println("Selected square: (" + (int) tile.x() + ", " + (int) tile.y() + ")");
          }
        });
  }

  void draw() {
    var gc = getGraphicsContext2D();
    var width = getWidth();
    var height = getHeight();

    gc.setFill(Color.BLACK);
    gc.clearRect(0, 0, width, height);
    gc.fillRect(0, 0, width, height);

    final RealPoint GRID_OFFSET =
        new RealPoint((width / 2 - camera.x()) % TILE_SIZE, (height / 2 - camera.y()) % TILE_SIZE);

    // Draw grid
    gc.setStroke(Color.GRAY);
    gc.setLineWidth(1);

    // Draw vertical lines
    for (double x = GRID_OFFSET.x(); x <= width; x += TILE_SIZE) {
      gc.strokeLine(x, 0, x, height);
    }

    // Draw horizontal lines
    for (double y = GRID_OFFSET.y(); y <= height; y += TILE_SIZE) {
      gc.strokeLine(0, y, width, y);
    }

    // Highlight selected square
    if (selectedTile != null) {
      RealPoint selectedScreen =
          new RealPoint(
              selectedTile.x() * TILE_SIZE - camera.x() + width / 2,
              selectedTile.y() * TILE_SIZE - camera.y() + height / 2);
      gc.setFill(Color.rgb(255, 255, 255, 0.3));
      gc.fillRect(selectedScreen.x(), selectedScreen.y(), TILE_SIZE, TILE_SIZE);
    }
  }
}
