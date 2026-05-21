package org.tcs.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.tcs.model.geometry.RealPoint;

public class Decoration {
  private RealPoint position;
  private final Image image;

  public Decoration(RealPoint position, Image image) {
    this.position = position;
    this.image = image;
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

  public void draw(GraphicsContext gc) {
    gc.drawImage(image, position.x() - 60, position.y() - 60, 120, 120);
  }
}
