package org.tcs.ui;

import java.util.Random;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
  private final IntegerProperty numberOfDice = new SimpleIntegerProperty(1);
  private final ObservableList<IntegerProperty> rollResults =
      FXCollections.observableArrayList(prop -> new Observable[] {prop});
  private final VBox rolls = new VBox();
  private final BooleanBinding isValid =
      new BooleanBinding() {
        {
          super.bind(rollResults, numberOfSides); // register dependencies here
        }

        @Override
        protected boolean computeValue() {
          return rollResults.stream()
              .allMatch(roll -> roll.get() > 0 && roll.get() <= numberOfSides.get());
        }
      };
  private final ObjectProperty<RollInformation> info = new SimpleObjectProperty<>();
  private final Random random = new Random();
  private double layoutHeight;

  public WindowDiceRoller(Window owner) {
    super();
    initOwner(owner);
    setTitle("Roll the dice");
    initModality(Modality.APPLICATION_MODAL);

    Label sidesLabel = new Label();
    sidesLabel
        .textProperty()
        .bind(numberOfDice.asString("Rolling %d").concat(numberOfSides.asString("d%d")));

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

    Button rollAll = new Button("Roll all");
    rollAll.setPrefWidth(80);
    rollAll.setOnAction(
        _ -> {
          for (var val : rollResults) {
            val.set(random.nextInt(1, numberOfSides.get()));
          }
        });

    Button okButton = new Button("Submit");
    okButton.disableProperty().bind(isValid.not());
    okButton.setOnAction(_ -> hide());

    VBox layout = new VBox(15, sidesLabel, infoLabel, rollAll, rolls, okButton);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);

    layoutHeight = layout.heightProperty().get();

    setScene(new Scene(layout, 350, 180));
  }

  @Override
  public int roll(int numberOfSides, RollInformation information) {
    return roll(1, numberOfSides, information);
  }

  @Override
  public int roll(int numberOfDice, int numberOfSides, RollInformation information) {
    if (numberOfSides < 1 || numberOfDice < 1) throw new IllegalArgumentException();

    this.numberOfSides.set(numberOfSides);
    this.numberOfDice.set(numberOfDice);
    info.set(information);

    rollResults.clear();
    for (int i = 0; i < numberOfDice; i++) rollResults.add(new SimpleIntegerProperty(1));
    generateRolls();

    // values guessed by trial and error
    setHeight(numberOfDice * 28 + 220);
    showAndWait();

    // If the window simply closes, substitute a random number
    if (!isValid.get()) {
      int sum = 0;
      for (int i = 0; i < numberOfDice; i++) sum += random.nextInt(numberOfSides) + 1;
      return sum;
    }
    return rollResults.stream().mapToInt(ObservableIntegerValue::get).sum();
  }

  private void generateRolls() {
    rolls.getChildren().clear();

    Label instructionLabel = new Label();
    instructionLabel
        .textProperty()
        .bind(
            numberOfDice.map(
                n -> {
                  if (n.equals(1)) return "Enter dice roll result:";
                  else return "Enter dice roll results:";
                }));

    rolls.getChildren().add(instructionLabel);

    for (int i = 0; i < numberOfDice.get(); i++) rolls.getChildren().add(generateRoll(i));

    rolls.setAlignment(Pos.CENTER);
  }

  private Node generateRoll(int i) {
    var resultField = new BetterTextField();
    resultField.setPrefWidth(150);

    var rollResult = rollResults.get(i);

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

    BooleanBinding localIsValid =
        rollResult.greaterThan(0).and(rollResult.lessThanOrEqualTo(numberOfSides));

    var rollError = localIsValid.map(b -> b ? "" : "Invalid value");
    var rollErrorLabel = new Label();
    rollErrorLabel.setTextFill(Color.RED);
    rollErrorLabel.textProperty().bind(rollError);

    var visible = localIsValid.not().and(resultField.focusedProperty().not());
    rollErrorLabel.visibleProperty().bind(visible);
    rollErrorLabel.managedProperty().bind(visible);

    Button randomizeButton = new Button("Randomize");
    randomizeButton.setOnAction(_ -> rollResult.set(random.nextInt(numberOfSides.get()) + 1));

    HBox inputBox = new HBox(10, resultField, randomizeButton);
    inputBox.setAlignment(Pos.CENTER);

    VBox node = new VBox(inputBox, rollErrorLabel);
    node.setAlignment(Pos.CENTER);

    return node;
  }
}
