package org.tcs.ui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class PlayView {
  public static Scene scene(ViewModel model) {
    var selectorVisible = new SimpleBooleanProperty(false);

    var canvas = new MapView(model);
    canvas.setOnAddDecoration(
        target -> {
          selectorVisible.set(true);
          model.setAddTarget(target);
        });
    canvas.disableProperty().bind(selectorVisible);

    var assetSelector = new AssetSelector();
    assetSelector.setOnOk(
        image -> {
          selectorVisible.set(false);
          canvas.addDecoration(new Decoration(model.getAddTarget(), Assets.images.get(image)));
        });
    assetSelector.setOnCancel(() -> selectorVisible.set(false));

    var pane = new StackPane();
    pane.getChildren().add(canvas);
    canvas.widthProperty().bind(pane.widthProperty());
    canvas.heightProperty().bind(pane.heightProperty());

    pane.getChildren().add(assetSelector);
    StackPane.setAlignment(assetSelector, Pos.CENTER);
    assetSelector.visibleProperty().bind(selectorVisible);
    assetSelector.managedProperty().bind(selectorVisible);

    return new Scene(pane, 1200, 900);
  }
}
