package org.tcs.model.geometry;

import java.util.ArrayList;
import java.util.Collection;
import org.tcs.model.util.Pair;

public interface WorldMap {
  /**
   * @param ignoredCollisions A collection of OccupyReasons that will be ignored when checking if
   *     the point is occupied.
   * @return The distance between two points or Double.POSITIVE_INFINITY if there is no path between
   *     them or one/both of them are not part of the map.
   */
  double getDistance(Point start, Point target, Collection<OccupyReason> ignoredCollisions);

  /**
   * @return The distance between two points or Double.POSITIVE_INFINITY if there is no path between
   *     them or one/both of them are not part of the map.
   */
  default double getDistance(Point start, Point target) {
    return getDistance(start, target, new ArrayList<>());
  }

  /**
   * @param ignoredCollisions A collection of OccupyReasons that will be ignored when checking if
   *     the point is occupied.
   * @return A collection of distances between the starting point and those of target points that
   *     can be reached ordered ascendingly by the distance.
   */
  Collection<Pair<Point, Double>> getDistances(
      Point start, Collection<Point> targets, Collection<OccupyReason> ignoredCollisions);

  /**
   * @return A collection of distances between the starting point and those of target points that
   *     can be reached ordered ascendingly by the distance.
   */
  default Collection<Pair<Point, Double>> getDistances(Point start, Collection<Point> targets) {
    return getDistances(start, targets, new ArrayList<>());
  }

  /**
   * @param ignoredCollisions A collection of OccupyReasons that will be ignored when checking if
   *     the point is occupied.
   * @return The navigation map starting from the given point.
   */
  NavMap navMap(Point start, double budget, Collection<OccupyReason> ignoredCollisions);

  default NavMap navMap(Point start, double budget) {
    return navMap(start, budget, new ArrayList<>());
  }

  RealPoint pointToRealPoint(Point point);

  Point realPointToPoint(RealPoint realPoint);

  boolean checkInBounds(Point point);

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
    return getOccupyReason(point) != null;
  }

  /**
   * @return Reason if the given point is occupied and `null` otherwise.
   */
  OccupyReason getOccupyReason(Point point);

  boolean isGrid();
}
