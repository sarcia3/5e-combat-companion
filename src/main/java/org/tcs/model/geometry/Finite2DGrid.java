package org.tcs.model.geometry;

import java.util.*;
import javafx.util.Pair;

public class Finite2DGrid implements WorldMap {
  private final int width, height;
  private final Set<GridPoint2D> occupiedPoints;

  public Finite2DGrid(int width, int height) {
    this.width = width;
    this.height = height;
    occupiedPoints = new HashSet<>();
  }

  @Override
  public double getDistance(Point start, Point end) {
    GridPoint2D start2D = (GridPoint2D) start;
    GridPoint2D end2D = (GridPoint2D) end;
    // For now, it runs Dijkstra, but we should replace it with A* later.
    Map<GridPoint2D, Double> distances = new HashMap<>();
    Queue<Pair<GridPoint2D, Double>> queue = new PriorityQueue<>(Comparator.comparing(Pair::getValue));
    queue.add(new Pair<GridPoint2D, Double>(start2D, 0.));
    while (!queue.isEmpty() && !distances.containsKey(end2D)) {
      Pair<GridPoint2D, Double> current = queue.remove();
      if (distances.containsKey(current.getKey())) continue;
      else distances.put(current.getKey(), current.getValue());
      for (int i = -1; i <= 1; i++)
        for (int j = -1; j <= 1; j++)
          if (i != 0 || j != 0) {
            GridPoint2D next = new GridPoint2D(current.getKey().x + i, current.getKey().y + j);
            if (!distances.containsKey(next) && checkIfAccessible(next)) {
              queue.add(
                  new Pair<GridPoint2D, Double>(next, current.getValue() + (i * j > 0 ? 1.5 : 1.)));
            }
          }
    }

    return distances.getOrDefault(end2D, Double.POSITIVE_INFINITY);
  }

  @Override
  public RealPoint pointToRealPoint(Point point) {
    if (!(point instanceof GridPoint2D(int x, int y))) throw new ClassCastException();
    return new RealPoint(x + 0.5, y + 0.5);
  }

  @Override
  public Point realPointToPoint(RealPoint realPoint) {
    return new GridPoint2D((int) Math.floor(realPoint.x()), (int) Math.floor(realPoint.y()));
  }

  @Override
  public boolean occupyPoint(Point point) {
    return occupiedPoints.add((GridPoint2D) point);
  }

  @Override
  public boolean freePoint(Point point) {
    return occupiedPoints.remove((GridPoint2D) point);
  }

  @Override
  public double getPointSize() {
    return 1.;
  }

  private record GridPoint2D(int x, int y) implements Point {

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof GridPoint2D(int x1, int y1))) return false;
      return x1 == x && y1 == y;
    }
  }

  private boolean checkIfAccessible(GridPoint2D gridPoint2D) {
    boolean xInBounds = gridPoint2D.x < width && gridPoint2D.x >= 0;
    boolean yInBounds = gridPoint2D.y < height && gridPoint2D.y >= 0;
    boolean occupied = occupiedPoints.contains(gridPoint2D);
    return xInBounds && yInBounds && (!occupied);
  }
}
