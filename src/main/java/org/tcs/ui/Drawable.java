package org.tcs.ui;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import org.tcs.model.geometry.RealPoint;

public sealed interface Drawable permits Decoration, Puppet {
  RealPoint position();

  Rectangle2D extent();

  default boolean contains(RealPoint point) {
    if (position() == null) return false;
    return extent().contains(point.x() - position().x(), point.y() - position().y());
  }

  void draw(GraphicsContext gc);
}
