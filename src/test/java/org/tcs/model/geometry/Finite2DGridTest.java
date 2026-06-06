package org.tcs.model.geometry;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class Finite2DGridTest {
  public static class CorrectDistanceCalculationTests {
    @Test
    void straightPath() {
      WorldMap grid = new Finite2DGrid(10, 10);
      List<Point> list = new ArrayList<>();
      for (int i = 0; i < 10; i++) list.add(grid.realPointToPoint(new RealPoint(0, i)));
      for (int i = 0; i < list.size(); i++)
        for (int j = 0; j < list.size(); j++)
          assertEquals(Math.abs(j - i), grid.getDistance(list.get(i), list.get(j)));
    }

    @Test
    void simpleObstacle() {
      WorldMap grid = new Finite2DGrid(10, 10);
      Point obstacle = grid.realPointToPoint(new RealPoint(1., 1.));
      grid.occupyPoint(obstacle, OccupyReason.Terrain);
      Point point1 = grid.realPointToPoint(new RealPoint(0., 0.));
      Point point2 = grid.realPointToPoint(new RealPoint(2., 2.));
      Point point3 = grid.realPointToPoint(new RealPoint(3., 3.));
      assertEquals(3.42, grid.getDistance(point1, point2));
      assertEquals(4.84, grid.getDistance(point1, point3));
      assertEquals(1.42, grid.getDistance(point2, point3));
    }

    @Test
    void impenetrableWall() {
      WorldMap grid = new Finite2DGrid(3, 3);
      List<Point> list = new ArrayList<>();
      for (int i = 0; i < 3; i++) list.add(grid.realPointToPoint(new RealPoint(1., i)));
      for (Point point : list) grid.occupyPoint(point, OccupyReason.Terrain);
      Point point1 = grid.realPointToPoint(new RealPoint(0., 0.));
      Point point2 = grid.realPointToPoint(new RealPoint(2., 2.));
      assertEquals(Double.POSITIVE_INFINITY, grid.getDistance(point1, point2));
    }

    @Test
    @Timeout(3)
    void hugeEmptyTest() {
      int size = 100000;
      WorldMap grid = new Finite2DGrid(size, size);
      Point point1 = grid.realPointToPoint(new RealPoint(0., 0.));
      Point point2 = grid.realPointToPoint(new RealPoint(size - 1., size - 1.));
      assertEquals((size - 1) * 1.42, grid.getDistance(point1, point2));
    }

    @Test
    @Timeout(3)
    void bigWithDiagonal() {
      int size = 500;
      WorldMap grid = new Finite2DGrid(size, size);
      Point point1 = grid.realPointToPoint(new RealPoint(0., 0.));
      Point point2 = grid.realPointToPoint(new RealPoint(size - 1., size - 1.));
      for (int i = 1; i < size; i++) {
        Point point = grid.realPointToPoint(new RealPoint(i, size - i - 1));
        grid.occupyPoint(point, OccupyReason.Terrain);
        if (i > 1) {
          point = grid.realPointToPoint(new RealPoint(i, size - i));
          grid.occupyPoint(point, OccupyReason.Terrain);
        }
      }
      double answer = size * 2. - 4. + 1.42;
      assertTrue(Math.abs(answer - grid.getDistance(point1, point2)) < 0.0001);
    }
  }

  public static class OccupyingTests {
    @Test
    void addAndDelete() {
      WorldMap grid = new Finite2DGrid(10, 10);
      Point point = grid.realPointToPoint(new RealPoint(0., 0.));
      assertTrue(grid.occupyPoint(point, OccupyReason.Terrain));
      assertFalse(grid.occupyPoint(point, OccupyReason.Terrain));
      assertTrue(grid.freePoint(point));
      assertFalse(grid.freePoint(point));
    }

    @Test
    void ignoreStartingObstacle() {
      WorldMap grid = new Finite2DGrid(3, 3);
      Point point1 = grid.realPointToPoint(new RealPoint(0., 0.));
      Point point2 = grid.realPointToPoint(new RealPoint(2., 2.));
      grid.occupyPoint(point1, OccupyReason.Creature);
      assertEquals(2 * 1.42, grid.getDistance(point1, point2));
    }

    @Test
    void ignoreTargetObstacle() {
      WorldMap grid = new Finite2DGrid(3, 3);
      Point point1 = grid.realPointToPoint(new RealPoint(0., 0.));
      Point point2 = grid.realPointToPoint(new RealPoint(2., 2.));
      grid.occupyPoint(point2, OccupyReason.Creature);
      assertEquals(2 * 1.42, grid.getDistance(point1, point2));
    }
  }
}
