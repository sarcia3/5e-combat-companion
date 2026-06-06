package org.tcs.model.geometry;

import java.util.List;

public interface NavMap {
  /**
   * @return The path from the starting point to the target, including the starting point.
   */
  List<Point> pathTo(Point target);

  /**
   * @return The distance from the starting point to the target.
   *     <p>If the point is not reachable it returns Double.POSITIVE_INFINITY.
   */
  double distanceTo(Point target);
}
