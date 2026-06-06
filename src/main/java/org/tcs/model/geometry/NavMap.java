package org.tcs.model.geometry;

import java.util.List;

public interface NavMap {
  /**
   * @return The path from the starting point to the target, including the starting point.
   */
  List<Point> pathTo(Point target);
}
