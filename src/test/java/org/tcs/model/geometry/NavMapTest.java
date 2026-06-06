package org.tcs.model.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class NavMapTest {
  @Test
  public void simpleTest() {
    WorldMap grid = new Finite2DGrid(3, 3);
    Point point = grid.realPointToPoint(new RealPoint(1., 1.));
    grid.occupyPoint(point, OccupyReason.Creature);
    NavMap map = grid.navMap(point, 1.);
    for (int i = 0; i < 3; i++)
      for (int j = 0; j < 3; j++) {
        Point target = grid.realPointToPoint(new RealPoint(i, j));
        double dist = grid.getDistance(point, target);
        if (dist > 1.) dist = Double.POSITIVE_INFINITY;
        assertEquals(dist, map.distanceTo(target));
        if (target.equals(point)) {
          assertEquals(List.of(point), map.pathTo(target));
          continue;
        }
        if (dist == 1.) {
          assertEquals(List.of(point, target), map.pathTo(target));
          continue;
        }
        assertNull(map.pathTo(target));
      }
  }

  @Test
  public void simpleObstacle() {
    WorldMap grid = new Finite2DGrid(3, 3);
    Point point = grid.realPointToPoint(new RealPoint(0., 0.));
    grid.occupyPoint(point, OccupyReason.Creature);
    List<RealPoint> realPoints = new ArrayList<>();
    realPoints.add(new RealPoint(2., 1.));
    realPoints.add(new RealPoint(1., 2.));
    realPoints.add(new RealPoint(1., 1.));
    List<Point> points = realPoints.stream().map(grid::realPointToPoint).toList();
    for (Point point1 : points) grid.occupyPoint(point1, OccupyReason.Terrain);
    NavMap map1 = grid.navMap(point, 3.);
    NavMap map2 = grid.navMap(point, 3, List.of(OccupyReason.Terrain));
    for (int i = 0; i < 3; i++)
      for (int j = 0; j < 3; j++) {
        Point target = grid.realPointToPoint(new RealPoint(i, j));
        double dist = grid.getDistance(point, target, List.of(OccupyReason.Terrain));
        if (grid.isPointOccupied(target))
          assertEquals(Double.POSITIVE_INFINITY, map2.distanceTo(target));
        else assertEquals(dist, map2.distanceTo(target));
        if (Math.min(i, j) == 0 && Math.max(i, j) > 0) {
          assertEquals(dist, map1.distanceTo(target));
          assertEquals(Math.max(i, j) + 1, map1.pathTo(target).size());
        } else {
          assertEquals(Double.POSITIVE_INFINITY, map1.distanceTo(target));
          assertNull(map1.pathTo(target));
        }
      }
  }
}
