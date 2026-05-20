package org.tcs.ui;

import org.tcs.model.geometry.Finite2DGrid;
import org.tcs.model.geometry.WorldMap;

public class ViewModel {
  private final WorldMap map = new Finite2DGrid(100, 100);

  public WorldMap getMap() {
    return map;
  }
}
