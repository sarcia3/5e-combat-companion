package org.tcs.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

public class PlayView {
  enum Mode {
    EDIT_PIECES,
    EDIT_COLLISION
  }

  public static Scene scene(ViewModel model, Window owner) {
    ObjectProperty<Mode> currentMode = new SimpleObjectProperty<>(Mode.EDIT_PIECES);
    var assetSelector = new AssetSelector(owner);
    var creatureWizard = new CreatureWizard(model, owner);

    var canvas = new MapView(model, currentMode);
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

    var editPiecesButton = modeTab(Mode.EDIT_PIECES, "Edit pieces", currentMode);
    var editCollisionButton = modeTab(Mode.EDIT_COLLISION, "Edit collision", currentMode);

    var buttonBox = new HBox(editPiecesButton, editCollisionButton);
    buttonBox.setMaxHeight(Region.USE_PREF_SIZE);
    buttonBox.setAlignment(Pos.CENTER);
    StackPane.setAlignment(buttonBox, Pos.TOP_CENTER);

    var pane = new StackPane();
    pane.getChildren().addAll(canvas, buttonBox);
    canvas.widthProperty().bind(pane.widthProperty());
    canvas.heightProperty().bind(pane.heightProperty());

    return new Scene(pane, 1200, 900);
  }

  private static Button modeTab(Mode mode, String text, ObjectProperty<Mode> currentMode) {
    var button = new Button(text);
    button.setOnAction(_ -> currentMode.set(mode));
    button.disableProperty().bind(currentMode.isEqualTo(mode));
    button
        .styleProperty()
        .bind(
            currentMode
                .isEqualTo(mode)
                .map(
                    disabled ->
                        disabled
                            ? "-fx-opacity: 1.0; -fx-background-color: #666;"
                            : "-fx-background-color: #fff"));
    return button;
  }
}
