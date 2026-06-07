package org.tcs.ui;

import java.util.Random;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;
import org.tcs.model.dice.DiceRoller;
import org.tcs.ui.util.BetterTextField;

public class WindowDiceRoller extends Stage implements DiceRoller {
  private final IntegerProperty numberOfSides = new SimpleIntegerProperty(20);
  private final IntegerProperty rollResult = new SimpleIntegerProperty(1);
  private final BooleanProperty inputChanged = new SimpleBooleanProperty(false);
  private final ObjectProperty<RollInformation> info = new SimpleObjectProperty<>();
  private final Random random = new Random();

  public WindowDiceRoller(Window owner) {
    super();
    initOwner(owner);
    setTitle("Roll the dice");
    initModality(Modality.APPLICATION_MODAL);

    Label sidesLabel = new Label();
    sidesLabel.textProperty().bind(numberOfSides.asString("Rolling d%d"));

    Label infoLabel = new Label();
    infoLabel
        .textProperty()
        .bind(
            info.map(
                    v -> {
                      if (v.origin().isEmpty() || v.reason().isEmpty()) {
                        return "";
                      }

                      return v.origin() + " is rolling: " + v.reason();
                    })
                .orElse(""));

    Label instructionLabel = new Label("Enter dice roll result:");

    var resultField = new BetterTextField();
    resultField.setPrefWidth(150);

    resultField.setTextFormatter(
        new TextFormatter<>(
            change -> {
              if (change.getControlNewText().matches("\\d*")) {
                return change;
              }

              return null;
            }));
    Bindings.bindBidirectional(
        resultField.textProperty(), rollResult, new NumberStringConverter("0"));
    rollResult.addListener(_ -> inputChanged.set(true));

    var isValid =
        rollResult.greaterThanOrEqualTo(1).and(rollResult.lessThanOrEqualTo(numberOfSides));
    var rollError = isValid.map(b -> b ? "" : "Invalid value");
    var rollErrorLabel = new Label();
    rollErrorLabel.setTextFill(Color.RED);
    rollErrorLabel.textProperty().bind(rollError);
    var visible = isValid.not().and(inputChanged).and(resultField.focusedProperty().not());
    rollErrorLabel.visibleProperty().bind(visible);
    rollErrorLabel.managedProperty().bind(visible);

    Button randomizeButton = new Button("Randomize");
    randomizeButton.setOnAction(_ -> rollResult.set(random.nextInt(numberOfSides.get()) + 1));

    HBox inputBox = new HBox(10, resultField, randomizeButton);
    inputBox.setAlignment(Pos.CENTER);

    Button okButton = new Button("Submit");
    okButton.disableProperty().bind(isValid.not());
    okButton.setOnAction(_ -> hide());

    VBox layout =
        new VBox(15, sidesLabel, infoLabel, instructionLabel, inputBox, rollErrorLabel, okButton);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);

    setScene(new Scene(layout, 350, 180));
  }

  @Override
  public int roll(int numberOfSides, RollInformation information) {
    this.numberOfSides.set(numberOfSides);
    rollResult.set(0);
    info.set(information);
    inputChanged.set(false);

    showAndWait();
    return rollResult.get();
  }
}
