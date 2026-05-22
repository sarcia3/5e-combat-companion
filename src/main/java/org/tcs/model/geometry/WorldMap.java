package org.tcs.model.geometry;

import java.util.Collection;
import org.tcs.model.util.Pair;

public interface WorldMap {
  /**
   * @return The distance between two points or Double.POSITIVE_INFINITY if there is no path between
   *     them or one/both of them are not part of the map.
   */
  double getDistance(Point start, Point target);

  /**
   * @return A collection of distances between the starting point and those of target points that
   *     can be reached ordered ascendingly by the distance.
   */
  Collection<Pair<Point, Double>> getDistances(Point start, Collection<Point> targets);

  RealPoint pointToRealPoint(Point point);

  Point realPointToPoint(RealPoint realPoint);

  /**
   * Occupies a point if it was previously occupied.
   *
   * @return False if the given point is already occupied or is not part of the map.
   */
  boolean occupyPoint(Point point, OccupyReason reason);

  /**
   * Frees a point if it wasn't previously occupied.
   *
   * @return True if the point was occupied and false otherwise.
   */
  boolean freePoint(Point point);

  /**
   * @return True if the given point is occupied and false otherwise.
   */
  default boolean isPointOccupied(Point point) {
    return getPointOccupied(point) != null;
  }

  /**
   * @return Reason if the given point is occupied and `null` otherwise.
   */
  OccupyReason getPointOccupied(Point point);

  /**
   * @return Size of a point in feet.
   */
  double getPointSize();

  default boolean isGrid() {
    return getPointSize() != 0;
  }
}
