package org.tcs.model.geometry;

import java.util.*;
import org.tcs.model.util.Pair;

public class Finite2DGrid implements WorldMap {
  private final int width, height;
  private final Map<GridPoint2D, OccupyReason> occupiedPoints;

  /**
   * @throws IllegalArgumentException if either width or height is negative.
   */
  public Finite2DGrid(int width, int height) {
    if (width < 0 || height < 0) throw new IllegalArgumentException();
    this.width = width;
    this.height = height;
    occupiedPoints = new HashMap<>();
  }

  /**
   * In this implementation we allow going diagonally with cost being 1.42, approximation of
   * sqrt(2).
   */
  @Override
  public double getDistance(Point start, Point target) {
    GridPoint2D start2D = (GridPoint2D) start;
    GridPoint2D target2D = (GridPoint2D) target;
    // For now, it runs Dijkstra, but we should replace it with A* later.
    Map<GridPoint2D, Double> distances = new HashMap<>();
    Queue<Pair<GridPoint2D, Double>> queue =
        new PriorityQueue<>(Comparator.comparing(Pair::second));
    queue.add(new Pair<GridPoint2D, Double>(start2D, 0.));
    while (!queue.isEmpty() && !distances.containsKey(target2D)) {
      Pair<GridPoint2D, Double> current = queue.remove();
      // If we've already been in this point do nothing
      if (distances.containsKey(current.first())) continue;
      distances.put(current.first(), current.second());
      // If this point is occupied or outside the map do nothing
      if (!checkAccessible(current.first())) continue;
      // Otherwise go to neighbours
      for (Pair<GridPoint2D, Double> next : getNeighbours(current.first())) {
        if (!distances.containsKey(next.first())) {
          queue.add(new Pair<GridPoint2D, Double>(next.first(), next.second() + current.second()));
        }
      }
    }

    return distances.getOrDefault(target2D, Double.POSITIVE_INFINITY);
  }

  @Override
  public Collection<Pair<Point, Double>> getDistances(Point start, Collection<Point> targets) {
    List<Pair<Point, Double>> list = new ArrayList<>();
    GridPoint2D start2D = (GridPoint2D) start;
    Map<GridPoint2D, Double> distances = new HashMap<>();
    Queue<Pair<GridPoint2D, Double>> queue =
        new PriorityQueue<>(Comparator.comparing(Pair::second));
    queue.add(new Pair<GridPoint2D, Double>(start2D, 0.));
    while (!queue.isEmpty() && list.size() < targets.size()) {
      Pair<GridPoint2D, Double> current = queue.remove();
      // If we've already been in this point do nothing
      if (distances.containsKey(current.first())) continue;
      distances.put(current.first(), current.second());
      if (targets.contains(current.first()))
        list.add(new Pair<Point, Double>(current.first(), current.second()));
      // If this point is occupied or outside the map do nothing
      if (!checkAccessible(current.first())) continue;
      // Otherwise go to neighbours
      for (Pair<GridPoint2D, Double> next : getNeighbours(current.first())) {
        if (!distances.containsKey(next.first())) {
          queue.add(new Pair<GridPoint2D, Double>(next.first(), next.second() + current.second()));
        }
      }
    }
    return list;
  }

  @Override
  public RealPoint pointToRealPoint(Point point) {
    if (point == null) return null;
    if (!(point instanceof GridPoint2D(int x, int y))) throw new ClassCastException();
    return new RealPoint(x + 0.5, y + 0.5);
  }

  @Override
  public Point realPointToPoint(RealPoint realPoint) {
    if (realPoint == null) return null;
    return new GridPoint2D((int) Math.floor(realPoint.x()), (int) Math.floor(realPoint.y()));
  }

  @Override
  public boolean checkInBounds(Point point) {
    return checkInBounds((GridPoint2D) point);
  }

  @Override
  public boolean occupyPoint(Point point, OccupyReason reason) {
    if (reason == null) throw new IllegalArgumentException();
    GridPoint2D gridPoint2D = (GridPoint2D) point;
    if (!checkInBounds(gridPoint2D)) return false;
    return occupiedPoints.putIfAbsent(gridPoint2D, reason) == null;
  }

  @Override
  public boolean freePoint(Point point) {
    return occupiedPoints.remove((GridPoint2D) point) != null;
  }

  @Override
  public OccupyReason getOccupyReason(Point point) {
    return occupiedPoints.get((GridPoint2D) point);
  }

  @Override
  public double getPointSize() {
    return 1.;
  }

  private record GridPoint2D(int x, int y) implements Point {}

  private boolean checkInBounds(GridPoint2D gridPoint2D) {
    boolean xInBounds = gridPoint2D.x < width && gridPoint2D.x >= 0;
    boolean yInBounds = gridPoint2D.y < height && gridPoint2D.y >= 0;
    return xInBounds && yInBounds;
  }

  private boolean checkAccessible(GridPoint2D gridPoint2D) {
    return checkInBounds(gridPoint2D) && !isPointOccupied(gridPoint2D);
  }

  /**
   * @return A list of neighbours of the given point with the distance to them. The distance is 1 if
   *     the Points are side connected and 1.42 if they are diagonally connected.
   */
  private Collection<Pair<GridPoint2D, Double>> getNeighbours(GridPoint2D gridPoint2D) {
    List<Pair<GridPoint2D, Double>> list = new ArrayList<>();
    for (int i = -1; i <= 1; i++)
      for (int j = -1; j <= 1; j++)
        if (i != 0 || j != 0) {
          GridPoint2D prospect = new GridPoint2D(gridPoint2D.x + i, gridPoint2D.y + j);
          if (checkInBounds(prospect)) list.add(new Pair<>(prospect, i * j > 0 ? 1.42 : 1));
        }
    return list;
  }
}
