package org.tcs.ui;

import org.tcs.model.geometry.Finite2DGrid;
import org.tcs.model.geometry.RealPoint;
import org.tcs.model.geometry.WorldMap;

public class ViewModel {
  private final WorldMap map = new Finite2DGrid(100, 100);
  private RealPoint target = null;

  public WorldMap getMap() {
    return map;
  }

  public void setAddTarget(RealPoint target) {
    this.target = target;
  }

  public RealPoint getAddTarget() {
    return target;
  }
}
