package org.tcs.model.geometry;

public interface WorldMap {
  /**
   * @return The distance between two points or Double.POSITIVE_INFINITY if there is no path between
   *     them or one/both of them are not part of the map.
   */
  double getDistance(Point start, Point end);

  RealPoint pointToRealPoint(Point point);

  Point realPointToPoint(RealPoint realPoint);

  /**
   * Occupied a point if it was previously occupied.
   *
   * @return False it the given point is already occupied or is not part of the map.
   */
  boolean occupyPoint(Point point);

  /**
   * Frees a point if it wasn't previously occupied.
   *
   * @return True if the point was occupied and false otherwise.
   */
  boolean freePoint(Point point);

  /**
   * @return True if the given point is occupied and false otherwise.
   */
  boolean isPointOccupied(Point point);

  /**
   * @return Size of a point in feet.
   */
  double getPointSize();

  default boolean isGrid() {
    return getPointSize() != 0;
  }
}
