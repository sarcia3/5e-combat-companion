package org.tcs.ui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import org.tcs.model.Creature;

public class PlayView {
  public static Scene scene(ViewModel model) {
    var selectorVisible = new SimpleBooleanProperty(false);

    var canvas = new MapView(model);
    canvas.setOnAddDecoration(
        target -> {
          selectorVisible.set(true);
          model.setAddParams(target, Drawable.Type.Decoration);
        });
    canvas.setOnAddCreature(
        target -> {
          selectorVisible.set(true);
          model.setAddParams(target, Drawable.Type.Puppet);
        });
    canvas.disableProperty().bind(selectorVisible);

    var assetSelector = new AssetSelector();
    assetSelector.setOnOk(
        image -> {
          selectorVisible.set(false);
          Drawable.Type type = model.getAddType();

          if (type == Drawable.Type.Puppet) {
            canvas.addCreature(
                new Creature("some name", model.getAddTarget().second(), 20, 5),
                Assets.images.get(image));
          } else if (type == Drawable.Type.Decoration) {
            canvas.addDecoration(
                new Decoration(model.getAddTarget().first(), Assets.images.get(image)));
          }
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
