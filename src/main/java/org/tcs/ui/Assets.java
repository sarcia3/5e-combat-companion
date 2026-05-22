package org.tcs.ui;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;

public class Assets {
  public static Map<String, Image> images = new HashMap<>();

  public static void init() {
    images.put("truck", new Image("/Truck_Red_Front.png"));
    images.put("bishop", new Image("/w_Bishop.png"));
    images.put("knight", new Image("/w_Knight.png"));
    images.put("pawn", new Image("/w_Pawn.png"));
    images.put("queen", new Image("/w_Queen.png"));
    images.put("rook", new Image("/w_Rook.png"));
    images.put("king", new Image("/w_King.png"));
  }
}
