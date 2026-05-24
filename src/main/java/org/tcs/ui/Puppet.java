package org.tcs.ui;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.tcs.model.Creature;
import org.tcs.model.geometry.RealPoint;

public final class Puppet implements Drawable {
  public final Creature creature;
  private RealPoint position;
  private final Rectangle2D extent;
  private final Image image;

  public Puppet(Creature creature, Image image, double tileSize) {
    this.creature = creature;
    this.position = null;
    this.image = image;
    this.extent = new Rectangle2D(-tileSize / 2, -tileSize / 2, tileSize, tileSize);
  }

  public void setPosition(RealPoint position) {
    this.position = position;
  }

  @Override
  public RealPoint position() {
    return position;
  }

  @Override
  public Rectangle2D extent() {
    return extent;
  }

  @Override
  public void draw(GraphicsContext gc) {
    if (position == null) return;

    gc.drawImage(
        image,
        position.x() + extent.getMinX(),
        position.y() + extent.getMinY(),
        extent.getWidth(),
        extent.getHeight());

    // Healthbar
    double healthbarWidth = extent.getWidth() * 0.8;
    double healthbarHeight = 8;
    double healthbarX = position.x() + extent.getMinX() + (extent.getWidth() - healthbarWidth) / 2;
    double healthbarY = position.y() + extent.getMaxY() - healthbarHeight - 4;

    // Background
    gc.setFill(Color.RED);
    gc.fillRect(healthbarX, healthbarY, healthbarWidth, healthbarHeight);

    // Foreground (current health)
    double healthPercentage = (double) creature.hitPoints() / creature.hitPointMaximum();
    gc.setFill(Color.GREEN);
    gc.fillRect(healthbarX, healthbarY, healthbarWidth * healthPercentage, healthbarHeight);
  }
}
