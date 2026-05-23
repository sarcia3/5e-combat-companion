package org.tcs.ui;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

public class PlayView {
  public static Scene scene(ViewModel model, Window owner) {
    var assetSelector = new AssetSelector(owner);
    var creatureWizard = new CreatureWizard(model, owner);

    var canvas = new MapView(model);
    canvas.setOnAddDecoration(
        target -> {
          var image = assetSelector.showAndWait();
          if (image == null) return;

          canvas.addDecoration(new Decoration(target, Assets.images.get(image)));
        });
    canvas.setOnAddCreature(
        target -> {
          if (!creatureWizard.showAndWait()) return;
          var image = assetSelector.showAndWait();
          if (image == null) return;

          canvas.addCreature(model.makeCreature(target), Assets.images.get(image));
        });

    var pane = new StackPane();
    pane.getChildren().add(canvas);
    canvas.widthProperty().bind(pane.widthProperty());
    canvas.heightProperty().bind(pane.heightProperty());

    return new Scene(pane, 1200, 900);
  }
}
