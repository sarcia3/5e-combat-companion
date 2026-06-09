package org.tcs.ui;

import java.util.EnumSet;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;
import org.tcs.model.Damage;
import org.tcs.ui.util.BetterTextField;
import org.tcs.ui.viewmodel.CreatureWizardViewModel;

public class CreatureWizard {

  private final DamageTypeSelector typeSelector;
  private final Stage popup;
  private boolean okClicked = false;

  public CreatureWizard(CreatureWizardViewModel model, Window owner) {
    popup = new Stage();
    popup.initOwner(owner);
    popup.setTitle("Create Creature");
    popup.initModality(Modality.APPLICATION_MODAL);

    typeSelector = new DamageTypeSelector(owner);

    // Name field
    var nameLabel = new Label("Name:");
    var nameField = new BetterTextField();
    nameField.textProperty().bindBidirectional(model.creatureNameProperty());
    HBox nameBox = new HBox(10, nameLabel, nameField);
    nameBox.setAlignment(Pos.CENTER_LEFT);

    // Max Hitpoints field
    var maxHitpointsLabel = new Label("Max Hitpoints:");
    var maxHitpointsField = new BetterTextField();
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
    maxHitpointsError.getStyleClass().add("error-text");
    maxHitpointsError.textProperty().bind(model.creatureHitpointsErrorProperty());
    HBox maxHitpointsBox = new HBox(10, maxHitpointsLabel, maxHitpointsField);
    maxHitpointsBox.setAlignment(Pos.CENTER_LEFT);

    // Movement Speed field
    var movementSpeedLabel = new Label("Movement Speed:");
    var movementSpeedField = new BetterTextField();
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
    movementSpeedError.getStyleClass().add("error-text");
    movementSpeedError.textProperty().bind(model.creatureMovementErrorProperty());
    HBox movementSpeedBox = new HBox(10, movementSpeedLabel, movementSpeedField);
    movementSpeedBox.setAlignment(Pos.CENTER_LEFT);

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

    form.getChildren().add(resistances(model));

    BorderPane root = new BorderPane();
    root.setCenter(form);
    root.setBottom(buttonBox);

    Scene scene = new Scene(root, 600, 600);
    scene
        .getStylesheets()
        .add(Objects.requireNonNull(getClass().getResource("/global.css")).toExternalForm());
    popup.setScene(scene);
  }

  public Node resistances(CreatureWizardViewModel model) {
    var resistancesButton = new Button("Edit resistances");
    resistancesButton.setOnAction(
        _ -> {
          EnumSet<Damage.Type> set = typeSelector.showAndWait(model.resistancesProperty());
          if (set != null) {
            model.resistancesProperty().clear();
            model.resistancesProperty().addAll(set);
          }
        });

    var vulnerabilitiesButton = new Button("Edit vulnerabilities");
    vulnerabilitiesButton.setOnAction(
        _ -> {
          EnumSet<Damage.Type> set = typeSelector.showAndWait(model.vulnerabilitiesProperty());
          if (set != null) {
            model.vulnerabilitiesProperty().clear();
            model.vulnerabilitiesProperty().addAll(set);
          }
        });

    var immunitiesButton = new Button("Edit immunities");
    immunitiesButton.setOnAction(
        _ -> {
          EnumSet<Damage.Type> set = typeSelector.showAndWait(model.immunitiesProperty());
          if (set != null) {
            model.immunitiesProperty().clear();
            model.immunitiesProperty().addAll(set);
          }
        });

    return new HBox(10, resistancesButton, vulnerabilitiesButton, immunitiesButton);
  }

  public boolean showAndWait() {
    okClicked = false;
    popup.showAndWait();
    return okClicked;
  }
}
