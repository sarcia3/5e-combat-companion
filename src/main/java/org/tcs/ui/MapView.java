package org.tcs.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class MapView extends Canvas {
  private static final double TILE_SIZE = 150;
  private double cameraX = 0;
  private double cameraY = 0;
  private double lastMouseX = 0;
  private double lastMouseY = 0;
  private double mousePressX = 0;
  private double mousePressY = 0;
  private Integer selectedTileX = null;
  private Integer selectedTileY = null;

  public MapView() {
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        draw();
      }
    }.start();

    setOnMousePressed(
        event -> {
          lastMouseX = event.getX();
          lastMouseY = event.getY();
          mousePressX = event.getX();
          mousePressY = event.getY();
        });

    setOnMouseDragged(
        event -> {
          double deltaX = event.getX() - lastMouseX;
          double deltaY = event.getY() - lastMouseY;
          cameraX -= deltaX;
          cameraY -= deltaY;
          lastMouseX = event.getX();
          lastMouseY = event.getY();
        });

    setOnMouseClicked(
        event -> {
          double deltaX = Math.abs(event.getX() - mousePressX);
          double deltaY = Math.abs(event.getY() - mousePressY);

          if (deltaX <= 50 && deltaY <= 50) {
            double worldX = event.getX() + cameraX - getWidth() / 2;
            double worldY = event.getY() + cameraY - getHeight() / 2;
            int tileX = (int) Math.floor(worldX / TILE_SIZE);
            int tileY = (int) Math.floor(worldY / TILE_SIZE);
            selectedTileX = tileX;
            selectedTileY = tileY;
            System.out.println("Selected square: (" + tileX + ", " + tileY + ")");
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

    final double GRID_OFFSET_X = (width / 2 - cameraX) % TILE_SIZE;
    final double GRID_OFFSET_Y = (height / 2 - cameraY) % TILE_SIZE;

    // Draw grid
    gc.setStroke(Color.GRAY);
    gc.setLineWidth(1);

    // Draw vertical lines
    for (double x = GRID_OFFSET_X; x <= width; x += TILE_SIZE) {
      gc.strokeLine(x, 0, x, height);
    }

    // Draw horizontal lines
    for (double y = GRID_OFFSET_Y; y <= height; y += TILE_SIZE) {
      gc.strokeLine(0, y, width, y);
    }

    // Highlight selected square
    if (selectedTileX != null && selectedTileY != null) {
      double selectedScreenX = selectedTileX * TILE_SIZE - cameraX + width / 2;
      double selectedScreenY = selectedTileY * TILE_SIZE - cameraY + height / 2;
      gc.setFill(Color.rgb(255, 255, 255, 0.3));
      gc.fillRect(selectedScreenX, selectedScreenY, TILE_SIZE, TILE_SIZE);
    }
  }
}
