package org.tcs.model.geometry;

public interface WorldMap {
  /**
   * @return The distance between two points or Double.POSITIVE_INFINITY if there is no path between
   *     them.
   */
  double getDistance(Point start, Point end);

  RealPoint pointToRealPoint(Point point);

  Point realPointToPoint(RealPoint realPoint);

  /**
   * Frees a point if it was previously occupied.
   *
   * @return True if the point was not occupied yet and false otherwise.
   */
  boolean occupyPoint(Point point);

  /**
   * Occupies a point if it wasn't previously occupied.
   *
   * @return True if the point was occupied and false otherwise.
   */
  boolean freePoint(Point point);

  /**
   * @return Size of a point in feet.
   */
  double getPointSize();

  default boolean isGrid() {
    return getPointSize() != 0;
  }
}
