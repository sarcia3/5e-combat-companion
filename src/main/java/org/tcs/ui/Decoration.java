package org.tcs.ui;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.tcs.model.geometry.RealPoint;

public class Decoration {
  private RealPoint position;
  private final Rectangle2D extent;
  private final Image image;

  public Decoration(RealPoint position, Image image) {
    this.position = position;
    this.image = image;
    this.extent =
        new Rectangle2D(
            -image.getWidth() / 2, -image.getHeight() / 2, image.getWidth(), image.getHeight());
  }

  public RealPoint getPosition() {
    return position;
  }

  public void setPosition(RealPoint position) {
    this.position = position;
  }

  public Image getImage() {
    return image;
  }

  public Rectangle2D getExtent() {
    return extent;
  }

  public boolean contains(RealPoint point) {
    return extent.contains(point.x() - position.x(), point.y() - position.y());
  }

  public void draw(GraphicsContext gc) {
    gc.drawImage(
        image,
        position.x() + extent.getMinX(),
        position.y() + extent.getMinY(),
        extent.getWidth(),
        extent.getHeight());
  }
}
