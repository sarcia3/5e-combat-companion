package org.tcs.ui;

import java.util.HashMap;
import java.util.Map;
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
import org.tcs.ui.map.MapView;
import org.tcs.ui.viewmodel.ViewModel;

public class PlayView extends Scene {
  public enum Mode {
    PLAY,
    EDIT_PIECES,
    EDIT_COLLISION,
    PATHING
  }

  private final Map<Creature, Image> creatureImages = new HashMap<>();

  public PlayView(ViewModel model, Window owner) {
    super(new StackPane(), 1200, 900);

    ObjectProperty<Mode> currentMode = new SimpleObjectProperty<>(Mode.PLAY);
    var mapView = getMapView(model, owner, currentMode);

    var creatureEdit = new CreatureEdit(creatureImages, model.creature, owner);
    model.creature.creatureProperty().bind(mapView.selected());
    creatureEdit
        .visibleProperty()
        .bind(
            currentMode
                .isEqualTo(Mode.EDIT_PIECES)
                .and(model.creature.creatureProperty().isNotNull()));
    creatureEdit
        .managedProperty()
        .bind(
            currentMode
                .isEqualTo(Mode.EDIT_PIECES)
                .and(model.creature.creatureProperty().isNotNull()));
    StackPane.setAlignment(creatureEdit, Pos.TOP_RIGHT);

    var playButton = modeTab(Mode.PLAY, "Play", currentMode);
    var editPiecesButton = modeTab(Mode.EDIT_PIECES, "Edit pieces", currentMode);
    var editCollisionButton = modeTab(Mode.EDIT_COLLISION, "Edit collision", currentMode);

    var buttonBox = new HBox(playButton, editPiecesButton, editCollisionButton);
    buttonBox.setMaxHeight(Region.USE_PREF_SIZE);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.visibleProperty().bind(currentMode.isNotEqualTo(Mode.PATHING));
    buttonBox.managedProperty().bind(currentMode.isNotEqualTo(Mode.PATHING));
    StackPane.setAlignment(buttonBox, Pos.TOP_CENTER);

    var initiativeQueue = new InitiativeQueue(model, creatureImages);
    initiativeQueue.visibleProperty().bind(currentMode.isEqualTo(Mode.PLAY));
    initiativeQueue.managedProperty().bind(currentMode.isEqualTo(Mode.PLAY));
    initiativeQueue.setOnEntryClicked(
        creature -> {
          mapView.moveCameraTo(creature);
          mapView.selectCreature(creature);
        });
    StackPane.setAlignment(initiativeQueue, Pos.TOP_LEFT);

    var creatureView = new CreatureView(creatureImages, model.creature);
    creatureView.setOnStartMoving(() -> currentMode.set(Mode.PATHING));
    creatureView.setOnStopMoving(() -> currentMode.set(Mode.PLAY));
    model.creature.creatureProperty().bind(mapView.selected());
    var visible =
        currentMode
            .isEqualTo(Mode.PLAY)
            .or(currentMode.isEqualTo(Mode.PATHING))
            .and(model.creature.creatureProperty().isNotNull());
    creatureView.visibleProperty().bind(visible);
    creatureView.managedProperty().bind(visible);
    StackPane.setAlignment(creatureView, Pos.TOP_RIGHT);

    var pane = new StackPane();
    pane.getChildren().addAll(mapView, buttonBox, creatureEdit, initiativeQueue, creatureView);
    mapView.widthProperty().bind(pane.widthProperty());
    mapView.heightProperty().bind(pane.heightProperty());

    setRoot(pane);
  }

  private MapView getMapView(ViewModel model, Window owner, ObjectProperty<Mode> currentMode) {
    var assetSelector = new AssetSelector(owner);
    var creatureWizard = new CreatureWizard(model.creatureWizard, owner);

    var canvas = new MapView(model, currentMode, creatureImages);
    canvas.setOnAddDecoration(
        target -> {
          var image = assetSelector.showAndWait();
          if (image == null) return;

          canvas.addDecoration(new Decoration(target, Assets.images.get(image)));
        });
    // Ideally, the asset selector should be a part of the creature wizard. This is just
    // faster to write like this, and there isn't much time.
    canvas.setOnAddCreature(
        target -> {
          if (!creatureWizard.showAndWait()) return;
          var imageName = assetSelector.showAndWait();
          if (imageName == null) return;

          // Listeners will be notified soon after, so we can safely mutate here
          var creature = model.creatureWizard.makeCreature(target);
          var image = Assets.images.get(imageName);
          creatureImages.put(creature, image);
          model.setCreatureName(creature, creature.name());
          model.addCreature(creature);
        });

    return canvas;
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
