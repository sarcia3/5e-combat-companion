package org.tcs.ui;

import java.util.Objects;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;
import org.tcs.model.dice.DiceRoller;
import org.tcs.ui.util.BetterTextField;

public class WindowDiceRoller extends Stage implements DiceRoller {
  private enum Nav {
    Manual,
    Normal
  }

  private final ObjectProperty<Nav> currentMode = new SimpleObjectProperty<>(Nav.Normal);
  private final IntegerProperty numberOfSides = new SimpleIntegerProperty(20);
  private final IntegerProperty numberOfDice = new SimpleIntegerProperty(1);
  private final ObservableList<IntegerProperty> rollResults =
      FXCollections.observableArrayList(prop -> new Observable[] {prop});
  private final VBox manualRolls = new VBox(5);
  private final VBox normalRolls = new VBox(5);
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
    infoLabel
        .textProperty()
        .addListener(
            (_, _, _) -> {
              setWidth(Math.max(350, infoLabel.textProperty().get().length() * 6 + 200));
            });

    var labelsBox = new VBox(5, sidesLabel, infoLabel);
    labelsBox.setAlignment(Pos.CENTER);
    labelsBox.getStyleClass().add("labels-view");

    Button okButton = new Button("Submit");
    okButton.disableProperty().bind(isValid.not());
    okButton.setOnAction(_ -> hide());

    Button rollAll = new Button("Roll all");
    rollAll.setPrefWidth(120);
    rollAll.setOnAction(
        _ -> {
          for (var val : rollResults) {
            val.set(random.nextInt(1, numberOfSides.get()));
          }
        });

    Button switchButton = new Button();
    switchButton.setOnAction(
        _ -> {
          if (currentMode.get() == Nav.Normal) currentMode.set(Nav.Manual);
          else currentMode.set(Nav.Normal);
        });
    switchButton
        .textProperty()
        .bind(
            currentMode.map(
                val -> {
                  if (val == Nav.Normal) return "Manual input";
                  else return "Normal input";
                }));

    var buttonsBox = new HBox(10, rollAll, switchButton);
    buttonsBox.setAlignment(Pos.CENTER);

    // Current mode shenanigans
    manualRolls.visibleProperty().bind(currentMode.isEqualTo(Nav.Manual));
    manualRolls.managedProperty().bind(currentMode.isEqualTo(Nav.Manual));

    normalRolls.visibleProperty().bind(currentMode.isEqualTo(Nav.Normal));
    normalRolls.managedProperty().bind(currentMode.isEqualTo(Nav.Normal));

    currentMode.addListener((_, _, _) -> updateHeight());
    numberOfDice.addListener((_, _, _) -> updateHeight());

    VBox layout = new VBox(15, labelsBox, buttonsBox, manualRolls, normalRolls, okButton);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);

    Scene scene = new Scene(layout, 350, 180);
    scene
        .getStylesheets()
        .add(Objects.requireNonNull(getClass().getResource("/global.css")).toExternalForm());
    setScene(scene);
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
    currentMode.set(Nav.Normal);

    rollResults.clear();
    for (int i = 0; i < numberOfDice; i++) rollResults.add(new SimpleIntegerProperty(0));
    generateManualRolls();
    generateNormalRolls();

    updateHeight();
    showAndWait();

    // If the window simply closes, substitute a random number
    if (!isValid.get()) {
      int sum = 0;
      for (int i = 0; i < numberOfDice; i++) sum += random.nextInt(numberOfSides) + 1;
      return sum;
    }
    return rollResults.stream().mapToInt(ObservableIntegerValue::get).sum();
  }

  private void generateManualRolls() {
    manualRolls.getChildren().clear();

    Label instructionLabel = new Label();
    instructionLabel
        .textProperty()
        .bind(
            numberOfDice.map(
                n -> {
                  if (n.equals(1)) return "Enter dice roll result:";
                  else return "Enter dice roll results:";
                }));

    manualRolls.getChildren().add(instructionLabel);

    for (int i = 0; i < numberOfDice.get(); i++)
      manualRolls.getChildren().add(generateManualRoll(i));

    manualRolls.setAlignment(Pos.CENTER);
  }

  private Node generateManualRoll(int i) {
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
    rollErrorLabel.textProperty().bind(rollError);
    rollErrorLabel.getStyleClass().add("error-text");

    var visible = localIsValid.not().and(resultField.focusedProperty().not());
    rollErrorLabel.visibleProperty().bind(visible);
    rollErrorLabel.managedProperty().bind(visible);

    Button randomizeButton = new Button("Randomize");
    randomizeButton.setOnAction(_ -> rollResult.set(random.nextInt(numberOfSides.get()) + 1));

    HBox inputBox = new HBox(10, resultField, randomizeButton);
    inputBox.setAlignment(Pos.CENTER);

    VBox node = new VBox(5, inputBox, rollErrorLabel);
    node.setAlignment(Pos.CENTER);

    return node;
  }

  private void generateNormalRolls() {
    normalRolls.getChildren().clear();

    int cols = (int) Math.ceil(Math.sqrt(numberOfDice.get()));
    int lastRow = numberOfDice.get() % cols;
    int fullRows = numberOfDice.get() / cols;

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setAlignment(Pos.CENTER);

    for (int i = 0; i < fullRows * cols; i++) {
      int row = i / cols;
      int col = i % cols;
      grid.add(generateNormalRoll(i), col, row);
    }

    if (lastRow != 0) {
      HBox row = new HBox(10);
      row.setAlignment(Pos.CENTER);
      for (int i = fullRows * cols; i < numberOfDice.get(); i++) {
        row.getChildren().add(generateNormalRoll(i));
      }
      GridPane.setColumnSpan(row, cols);
      grid.add(row, 0, fullRows);
    }

    normalRolls.getChildren().add(grid);
    normalRolls.setAlignment(Pos.CENTER);
  }

  // LLM generated
  private Node generateNormalRoll(int i) {
    var rollResult = rollResults.get(i);

    Button diceButton = new Button();
    diceButton.setPrefSize(80, 80);
    diceButton.getStyleClass().add("dice-button");

    diceButton.textProperty().bind(rollResult.asString());
    diceButton.setOnAction(_ -> rollResult.set(random.nextInt(numberOfSides.get()) + 1));

    // Highlight invalid values in red
    BooleanBinding localIsValid =
        rollResult.greaterThan(0).and(rollResult.lessThanOrEqualTo(numberOfSides));
    localIsValid.addListener(
        (_, _, valid) -> diceButton.setStyle(valid ? "" : "-fx-text-fill: -dnd-redLike;"));

    return diceButton;
  }

  // LLM generated
  private void updateHeight() {
    int baseHeight = 270;
    int cols = (int) Math.ceil(Math.sqrt(numberOfDice.get()));
    int rows = (int) Math.ceil((double) numberOfDice.get() / cols);

    int diceHeight =
        switch (currentMode.get()) {
          case Normal -> rows * 100;
          case Manual -> numberOfDice.get() * 45 + 60;
        };

    setHeight(baseHeight + diceHeight);
  }
}
