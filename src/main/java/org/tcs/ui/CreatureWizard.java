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
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class CreatureWizard {
  private final Stage popup;

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
    maxHitpointsField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0));
    maxHitpointsField.textProperty().bindBidirectional(model.creatureHitpointsProperty());
    var maxHitpointsError = new Label();
    maxHitpointsError.setTextFill(Color.RED);
    maxHitpointsError.textProperty().bind(model.creatureHitpointsErrorProperty());
    HBox maxHitpointsBox = new HBox(10, maxHitpointsLabel, maxHitpointsField);

    // Movement Speed field
    var movementSpeedLabel = new Label("Movement Speed:");
    var movementSpeedField = new TextField();
    movementSpeedField.setTextFormatter(new TextFormatter<>(new FloatStringConverter(), 0.0f));
    movementSpeedField.textProperty().bindBidirectional(model.creatureMovementProperty());
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
    okButton.setOnAction(_ -> popup.hide());

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(
        _ -> {
          model.creatureHitpointsProperty().setValue("0");
          model.creatureMovementProperty().setValue("0.0");
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

  public void showAndWait() {
    popup.showAndWait();
  }
}
