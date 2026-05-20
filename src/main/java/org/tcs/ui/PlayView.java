package org.tcs.ui;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class PlayView {
  public static Scene scene(ViewModel model) {
    var canvas = new MapView(model);

    var pane = new Pane();
    pane.getChildren().add(canvas);
    canvas.widthProperty().bind(pane.widthProperty());
    canvas.heightProperty().bind(pane.heightProperty());

    return new Scene(pane, 1200, 900);
  }
}
