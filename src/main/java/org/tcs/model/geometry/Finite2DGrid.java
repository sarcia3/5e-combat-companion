package org.tcs.model.geometry;

import java.util.*;
import javafx.util.Pair;

public class Finite2DGrid implements WorldMap {
  private final int width, height;
  private final Set<Point2D> occupiedPoints;

  public Finite2DGrid(int width, int height) {
    this.width = width;
    this.height = height;
    occupiedPoints = new HashSet<>();
  }

  @Override
  public double getDistance(Point start, Point end) {
    Point2D start2D = (Point2D) start;
    Point2D end2D = (Point2D) end;
    // For now, it runs Dijkstra, but we should replace it with A* later.
    Map<Point2D, Double> distances = new HashMap<>();
    Queue<Pair<Point2D, Double>> queue = new PriorityQueue<>(Comparator.comparing(Pair::getValue));
    queue.add(new Pair<Point2D, Double>(start2D, 0.));
    while (!queue.isEmpty() && !distances.containsKey(end2D)) {
      Pair<Point2D, Double> current = queue.remove();
      if (distances.containsKey(current.getKey())) continue;
      else distances.put(current.getKey(), current.getValue());
      for (int i = -1; i <= 1; i++)
        for (int j = -1; j <= 1; j++)
          if (i != 0 || j != 0) {
            Point2D next = new Point2D(current.getKey().x + i, current.getKey().y + j);
            if (!distances.containsKey(next) && checkIfAccessible(next)) {
              queue.add(
                  new Pair<Point2D, Double>(next, current.getValue() + (i * j > 0 ? 1.5 : 1.)));
            }
          }
    }

    return distances.getOrDefault(end2D, Double.POSITIVE_INFINITY);
  }

  @Override
  public RealPoint pointToRealPoint(Point point) {
    if (!(point instanceof Point2D(int x, int y))) throw new ClassCastException();
    return new RealPoint(x + 0.5, y + 0.5);
  }

  @Override
  public Point realPointToPoint(RealPoint realPoint) {
    return new Point2D((int) Math.floor(realPoint.x()), (int) Math.floor(realPoint.y()));
  }

  @Override
  public boolean occupyPoint(Point point) {
    return occupiedPoints.add((Point2D) point);
  }

  @Override
  public boolean freePoint(Point point) {
    return occupiedPoints.remove((Point2D) point);
  }

  @Override
  public double getPointSize() {
    return 1.;
  }

  private record Point2D(int x, int y) implements Point {

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Point2D(int x1, int y1))) return false;
      return x1 == x && y1 == y;
    }
  }

  private boolean checkIfAccessible(Point2D point2D) {
    boolean xInBounds = point2D.x < width && point2D.x >= 0;
    boolean yInBounds = point2D.y < height && point2D.y >= 0;
    boolean occupied = occupiedPoints.contains(point2D);
    return xInBounds && yInBounds && (!occupied);
  }
}
