package org.tcs.ui;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;

public class Assets {
  public static Map<String, Image> images = new HashMap<>();
  public static final Image PLACEHOLDER = new Image("/Truck_Red_Front.png");
  public static final Image BACKGROUND = new Image("/background.png");

  public static void init() {
    images.put("truck", PLACEHOLDER);
    images.put("bishop", new Image("/w_Bishop.png"));
    images.put("knight", new Image("/w_Knight.png"));
    images.put("pawn", new Image("/w_Pawn.png"));
    images.put("queen", new Image("/w_Queen.png"));
    images.put("rook", new Image("/w_Rook.png"));
    images.put("king", new Image("/w_King.png"));
    images.put("cinema", new Image("/Building_Cinema.png"));
    images.put("trashcan", new Image("/Decor_TrashCan.png"));
    images.put("tree 1", new Image("/Tree1.png"));
    images.put("tree 2", new Image("/Tree2.png"));
    images.put("tree 3", new Image("/Tree3.png"));
    images.put("tree 4", new Image("/Tree4.png"));
    images.put("tree 5", new Image("/Tree5.png"));
  }
}
