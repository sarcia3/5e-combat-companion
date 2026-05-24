package org.tcs.ui;

import java.util.HashMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import org.tcs.model.Creature;

public class PlayView {
  enum Mode {
    EDIT_PIECES,
    EDIT_COLLISION
  }

  public static Scene scene(ViewModel model, Window owner) {
    ObjectProperty<Mode> currentMode = new SimpleObjectProperty<>(Mode.EDIT_PIECES);
    var creatureImages = new HashMap<Creature, Image>();
    var assetSelector = new AssetSelector(owner);
    var creatureWizard = new CreatureWizard(model, owner);

    var canvas = new MapView(model, currentMode, creatureImages);
    canvas.setOnAddDecoration(
        target -> {
          var image = assetSelector.showAndWait();
          if (image == null) return;

          canvas.addDecoration(new Decoration(target, Assets.images.get(image)));
        });
    canvas.setOnAddCreature(
        target -> {
          if (!creatureWizard.showAndWait()) return;
          var imageName = assetSelector.showAndWait();
          if (imageName == null) return;

          // Listeners will be notified soon after, so we can safely mutate here
          var creature = model.makeCreature(target);
          var image = Assets.images.get(imageName);
          creatureImages.put(creature, image);
          canvas.addCreature(creature, image);
        });

    var editPiecesButton = modeTab(Mode.EDIT_PIECES, "Edit pieces", currentMode);
    var editCollisionButton = modeTab(Mode.EDIT_COLLISION, "Edit collision", currentMode);

    var buttonBox = new HBox(editPiecesButton, editCollisionButton);
    buttonBox.setMaxHeight(Region.USE_PREF_SIZE);
    buttonBox.setAlignment(Pos.CENTER);
    StackPane.setAlignment(buttonBox, Pos.TOP_CENTER);

    var initiativeQueue = new InitiativeQueue(model, creatureImages);
    StackPane.setAlignment(initiativeQueue, Pos.TOP_LEFT);

    var pane = new StackPane();
    pane.getChildren().addAll(canvas, buttonBox, initiativeQueue);
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
