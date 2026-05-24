package org.tcs.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;

public class CreatureWizard {
  private final Stage popup;
  private boolean okClicked = false;

  public CreatureWizard(ViewModel model, Window owner) {
    popup = new Stage();
    popup.initOwner(owner);
    popup.setTitle("Create Creature");
    popup.initModality(Modality.APPLICATION_MODAL);

    // Name field
    var nameLabel = new Label("Name:");
    var nameField = new TextField();
    nameField.textProperty().bindBidirectional(model.creatureNameProperty());
    HBox nameBox = new HBox(10, nameLabel, nameField);

    // Max Hitpoints field
    var maxHitpointsLabel = new Label("Max Hitpoints:");
    var maxHitpointsField = new TextField();
    maxHitpointsField.setTextFormatter(
        new TextFormatter<>(
            change1 -> {
              if (change1.getControlNewText().matches("\\d*")) {
                return change1;
              }

              return null;
            }));
    Bindings.bindBidirectional(
        maxHitpointsField.textProperty(),
        model.creatureHitpointsProperty(),
        new NumberStringConverter("0"));
    var maxHitpointsError = new Label();
    maxHitpointsError.setTextFill(Color.RED);
    maxHitpointsError.textProperty().bind(model.creatureHitpointsErrorProperty());
    HBox maxHitpointsBox = new HBox(10, maxHitpointsLabel, maxHitpointsField);

    // Movement Speed field
    var movementSpeedLabel = new Label("Movement Speed:");
    var movementSpeedField = new TextField();
    movementSpeedField.setTextFormatter(
        new TextFormatter<>(
            change -> {
              if (change.getControlNewText().matches("\\d*.?\\d*")) {
                return change;
              }

              return null;
            }));
    Bindings.bindBidirectional(
        movementSpeedField.textProperty(),
        model.creatureMovementProperty(),
        new NumberStringConverter("0.0"));
    var movementSpeedError = new Label();
    movementSpeedError.setTextFill(Color.RED);
    movementSpeedError.textProperty().bind(model.creatureMovementErrorProperty());
    HBox movementSpeedBox = new HBox(10, movementSpeedLabel, movementSpeedField);

    // Form layout
    VBox form = new VBox(10);
    form.setPadding(new Insets(20));
    form.getChildren()
        .addAll(nameBox, maxHitpointsBox, maxHitpointsError, movementSpeedBox, movementSpeedError);

    // Buttons
    Button okButton = new Button("Ok");
    okButton
        .disableProperty()
        .bind(
            Bindings.createBooleanBinding(
                () ->
                    !model.getCreatureHitpointsError().isEmpty()
                        || !model.getCreatureMovementError().isEmpty(),
                model.creatureHitpointsErrorProperty(),
                model.creatureMovementErrorProperty()));
    okButton.setOnAction(
        _ -> {
          okClicked = true;
          popup.hide();
        });

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(
        _ -> {
          model.resetCreatureData();
          popup.hide();
        });

    HBox buttonBox = new HBox(10, okButton, cancelButton);
    buttonBox.setAlignment(Pos.CENTER_RIGHT);
    buttonBox.setPadding(new Insets(10));

    BorderPane root = new BorderPane();
    root.setCenter(form);
    root.setBottom(buttonBox);

    popup.setScene(new Scene(root, 600, 600));
  }

  public boolean showAndWait() {
    okClicked = false;
    popup.showAndWait();
    return okClicked;
  }
}
