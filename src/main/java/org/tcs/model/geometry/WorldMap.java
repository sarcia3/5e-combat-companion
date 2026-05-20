package org.tcs.model.geometry;

public interface WorldMap {
  float getDistance(Point start, Point end);

  RealPoint pointToRealPoint(Point point);

  Point realPointToPoint(RealPoint realPoint);

  /**
   * @return True if the point was not occupied yet and false otherwise.
   */
  boolean occupyPoint(Point point);

  /**
   * @return True if the point was occupied and false otherwise.
   */
  boolean freePoint(Point point);
}
