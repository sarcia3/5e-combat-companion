package org.tcs.ui;

import javafx.scene.image.Image;

public class Assets {
  public static Image truck;

  public static void init() {
    truck = new Image("/Truck_Red_Front.png", MapView.PX_PER_FT, MapView.PX_PER_FT, true, true);
  }
}
