package org.tcs.ui;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import org.tcs.model.Creature;

public class PlayView {
  public static Scene scene(ViewModel model, Window owner) {
    var assetSelector = new AssetSelector(owner);

    var canvas = new MapView(model);
    canvas.setOnAddDecoration(
        target -> {
          var image = assetSelector.showAndWait();
          if (image == null) return;

          canvas.addDecoration(new Decoration(target, Assets.images.get(image)));
        });
    canvas.setOnAddCreature(
        target -> {
          var image = assetSelector.showAndWait();
          if (image == null) return;

          canvas.addCreature(new Creature("some name", target, 20, 5), Assets.images.get(image));
        });

    var pane = new StackPane();
    pane.getChildren().add(canvas);
    canvas.widthProperty().bind(pane.widthProperty());
    canvas.heightProperty().bind(pane.heightProperty());

    return new Scene(pane, 1200, 900);
  }
}
