package org.tcs.ui;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.tcs.model.geometry.RealPoint;

public final class Puppet implements Drawable {
  private RealPoint position;
  private final Rectangle2D extent;
  private final Image image;

  public Puppet(RealPoint position, Image image, double tileSize) {
    this.position = position;
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
    gc.drawImage(
        image,
        position.x() + extent.getMinX(),
        position.y() + extent.getMinY(),
        extent.getWidth(),
        extent.getHeight());
  }
}
