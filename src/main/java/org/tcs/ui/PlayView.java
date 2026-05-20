package org.tcs.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class PlayView {
  public static Scene scene(ViewModel model) {
    var canvas = new Canvas(800, 600);
    var gc = canvas.getGraphicsContext2D();

    new AnimationTimer() {
      @Override
      public void handle(long now) {
        var w = canvas.getWidth();
        var h = canvas.getHeight();
        gc.clearRect(0, 0, w, h);

        gc.setFill(Color.GREEN);
        gc.fillRect(0, 0, w, h / 2);
      }
    }.start();

    var pane = new Pane();
    pane.getChildren().add(canvas);
    canvas.widthProperty().bind(pane.widthProperty());
    canvas.heightProperty().bind(pane.heightProperty());

    return new Scene(pane, 800, 600);
  }
}
