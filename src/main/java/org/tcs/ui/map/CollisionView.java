package org.tcs.ui.map;

import static org.tcs.ui.map.MapView.PIXELS_PER_FOOT;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import org.tcs.model.geometry.OccupyReason;
import org.tcs.model.geometry.Point;
import org.tcs.model.geometry.RealPoint;
import org.tcs.model.geometry.WorldMap;

class CollisionView {
  public void onMousePressed(MouseButton button, RealPoint position, WorldMap worldMap) {
    if (button.equals(MouseButton.PRIMARY)) {
      var tile = worldMap.realPointToPoint(position.divide(PIXELS_PER_FOOT));

      if (worldMap.getOccupyReason(tile) == OccupyReason.Terrain) {
        worldMap.freePoint(tile);
      } else if (worldMap.getOccupyReason(tile) == null) {
        worldMap.occupyPoint(tile, OccupyReason.Terrain);
      }
    }
  }

  public void draw(
      WorldMap worldMap,
      RealPoint camera,
      double canvasWidth,
      double canvasHeight,
      GraphicsContext gc) {
    final double realTileSize = PIXELS_PER_FOOT;

    double minWorldX = camera.x() - canvasWidth / 2;
    double maxWorldX = camera.x() + canvasWidth / 2;
    double minWorldY = camera.y() - canvasHeight / 2;
    double maxWorldY = camera.y() + canvasHeight / 2;

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
}
