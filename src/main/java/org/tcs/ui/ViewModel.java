package org.tcs.ui;

import org.tcs.model.geometry.Finite2DGrid;
import org.tcs.model.geometry.RealPoint;
import org.tcs.model.geometry.WorldMap;

public class ViewModel {
  private final WorldMap map = new Finite2DGrid(100, 100);
  private RealPoint target = null;
  private Drawable.Type addType = null;

  public WorldMap getMap() {
    return map;
  }

  public void setAddParams(RealPoint target, Drawable.Type addType) {
    this.target = target;
    this.addType = addType;
  }

  public RealPoint getAddTarget() {
    return target;
  }

  public Drawable.Type getAddType() {
    return addType;
  }
}
