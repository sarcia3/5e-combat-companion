package org.tcs.model.geometry;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Finite2DGridTest {
  public static class CorrectDistanceCalculationTests {
    @Test
    void straightPath() {
      Finite2DGrid grid = new Finite2DGrid(10, 10);
      List<Point> list = new ArrayList<>();
      for (int i = 0; i < 10; i++) list.add(grid.realPointToPoint(new RealPoint(0, i)));
      for (int i = 0; i < list.size(); i++)
        for (int j = 0; j < list.size(); j++)
          assertEquals(Math.abs(j - i), grid.getDistance(list.get(i), list.get(j)));
    }

    @Test
    void simpleObstacle() {
      Finite2DGrid grid = new Finite2DGrid(10, 10);
      Point obstacle = grid.realPointToPoint(new RealPoint(1., 1.));
      grid.occupyPoint(obstacle);
      Point point1 = grid.realPointToPoint(new RealPoint(0., 0.));
      Point point2 = grid.realPointToPoint(new RealPoint(2., 2.));
      Point point3 = grid.realPointToPoint(new RealPoint(3., 3.));
      assertEquals(3.5, grid.getDistance(point1, point2));
      assertEquals(5., grid.getDistance(point1, point3));
      assertEquals(1.5, grid.getDistance(point2, point3));
    }

    @Test
    void impenetrableWall() {
      Finite2DGrid grid = new Finite2DGrid(3, 3);
      List<Point> list = new ArrayList<>();
      for (int i = 0; i < 3; i++) list.add(grid.realPointToPoint(new RealPoint(1., i)));
      for (Point point : list) grid.occupyPoint(point);
      Point point1 = grid.realPointToPoint(new RealPoint(0., 0.));
      Point point2 = grid.realPointToPoint(new RealPoint(2., 2.));
      assertEquals(Double.POSITIVE_INFINITY, grid.getDistance(point1, point2));
    }
  }

  public static class OccupyingTests {
    @Test
    void addAndDelete() {
      Finite2DGrid grid = new Finite2DGrid(10, 10);
      Point point = grid.realPointToPoint(new RealPoint(0., 0.));
      assertTrue(grid.occupyPoint(point));
      assertFalse(grid.occupyPoint(point));
      assertTrue(grid.freePoint(point));
      assertFalse(grid.freePoint(point));
    }
  }
}
