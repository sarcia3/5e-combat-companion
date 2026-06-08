package org.tcs.ui;

import java.util.Map;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;
import org.tcs.model.Creature;
import org.tcs.model.StateProcess;
import org.tcs.model.equipment.Weapon;
import org.tcs.ui.viewmodel.CreatureViewModel;

public class CreatureView extends VBox {
  private enum Nav {
    All,
    Weapons,
    Attacks,
    Movement
  }

  private final ObjectProperty<Nav> nav = new SimpleObjectProperty<>(Nav.All);
  private final BooleanProperty editMode = new SimpleBooleanProperty(false);
  private final BooleanBinding currentOrEdit;
  private final BooleanBinding playOnly;
  private Runnable onStartMoving = () -> {};
  private Runnable onStopMoving = () -> {};
  private Runnable onDelete = () -> {};
  private final WeaponSelector weaponSelector;

  public CreatureView(Map<Creature, Image> creatureImages, CreatureViewModel model, Window owner) {
    currentOrEdit = model.isCurrentProperty().or(editMode);
    playOnly = model.isCurrentProperty().and(editMode.not());
    weaponSelector = new WeaponSelector(owner);

    var name = new Label();
    name.textProperty().bind(model.creatureProperty().map(Creature::name));
    name.setTextAlignment(TextAlignment.CENTER);
    name.setStyle("-fx-font-weight: bold; -fx-font-size: 24px;");

    var portrait = new ImageView();
    portrait.setFitHeight(256.0);
    portrait.setPreserveRatio(true);
    portrait
        .imageProperty()
        .bind(
            model
                .creatureProperty()
                .map(key -> creatureImages.getOrDefault(key, Assets.PLACEHOLDER)));

    var nothingToDo = new Label("Not this creature's turn");
    nothingToDo.visibleProperty().bind(currentOrEdit.not());
    nothingToDo.managedProperty().bind(nothingToDo.visibleProperty());
    getChildren()
        .addAll(
            name,
            portrait,
            nothingToDo,
            topLevelPlay(model),
            topLevelEdit(),
            weaponsView(model),
            targetSelection(model),
            movement(model));
    setMaxWidth(320.0);
    setAlignment(Pos.TOP_CENTER);
    setBackground(Background.fill(Color.WHITE));
    setPadding(new Insets(8.0));

    setStyle(getStyle() + "-fx-background-color: -dnd-bg-colour;");
    model.creatureProperty().addListener(_ -> nav.set(Nav.All));
  }

  private Node topLevelPlay(CreatureViewModel model) {
    var weapons = new Button("Weapons");
    weapons.setOnAction(_ -> nav.set(Nav.Weapons));

    var move = new Button("Move");
    move.setOnAction(
        _ -> {
          nav.set(Nav.Movement);
          onStartMoving.run();
        });

    var pass = new Button("Pass");
    pass.setOnAction(_ -> model.pass());

    var topLevel = new VBox(8);
    topLevel.getChildren().addAll(weapons, move, pass);
    topLevel.setAlignment(Pos.CENTER);
    topLevel.visibleProperty().bind(nav.isEqualTo(Nav.All).and(playOnly));
    topLevel.managedProperty().bind(topLevel.visibleProperty());

    return topLevel;
  }

  private Node topLevelEdit() {
    var delete = new Button("Delete this creature");
    delete.setOnAction(_ -> onDelete.run());

    var weapons = new Button("Weapons");
    weapons.setOnAction(_ -> nav.set(Nav.Weapons));

    var topLevel = new VBox(8);
    topLevel.getChildren().addAll(delete, weapons);
    topLevel.setAlignment(Pos.CENTER);
    topLevel.visibleProperty().bind(nav.isEqualTo(Nav.All).and(editMode));
    topLevel.managedProperty().bind(topLevel.visibleProperty());

    return topLevel;
  }

  private Node weaponsView(CreatureViewModel model) {
    var cancel = new Button("Cancel");
    cancel.setOnAction(_ -> nav.set(Nav.All));

    var message = new Label();
    message.setStyle("-fx-text-fill: -dnd-redLike;");
    message.setVisible(false);
    message.managedProperty().bind(message.visibleProperty());

    var equippedLabel = new Label("Equipped:");
    equippedLabel.setStyle("-fx-font-weight: bold;");

    var equippedWeapons = new VBox(4.0);
    model
        .equippedWeaponsProperty()
        .addListener(
            (ListChangeListener<? super Weapon>)
                _ -> {
                  equippedWeapons.getChildren().clear();
                  for (Weapon weapon : model.equippedWeaponsProperty()) {
                    var weaponBox = equippedWeaponEntry(model, weapon);
                    equippedWeapons.getChildren().add(weaponBox);
                  }
                });

    var storedLabel = new Label("Stored:");
    storedLabel.setStyle("-fx-font-weight: bold;");

    var storedWeapons = new VBox();
    storedWeapons.setSpacing(4.0);
    model
        .storedWeaponsProperty()
        .addListener(
            (ListChangeListener<? super Weapon>)
                _ -> {
                  storedWeapons.getChildren().clear();
                  for (Weapon weapon : model.storedWeaponsProperty()) {
                    var weaponBox = storedWeaponEntry(model, weapon, message);
                    storedWeapons.getChildren().add(weaponBox);
                  }
                });

    var add = new Button("Add a weapon");
    add.setOnAction(
        _ -> {
          Weapon weapon = weaponSelector.showAndWait();

          if (weapon != null) {
            model.addStoredWeapon(weapon);
          }
        });
    add.visibleProperty().bind(editMode);
    add.managedProperty().bind(add.visibleProperty());

    var selection = new VBox(8.0);
    selection.visibleProperty().bind(nav.isEqualTo(Nav.Weapons));
    selection.managedProperty().bind(nav.isEqualTo(Nav.Weapons));
    selection
        .getChildren()
        .addAll(
            cancel,
            message,
            new Separator(),
            equippedLabel,
            equippedWeapons,
            new Separator(),
            storedLabel,
            storedWeapons,
            new Separator(),
            add);

    return selection;
  }

  private HBox storedWeaponEntry(CreatureViewModel model, Weapon weapon, Label message) {
    var weaponBox = new HBox(4.0);
    weaponBox.setAlignment(Pos.CENTER_LEFT);

    var nameLabel = new Label(weapon.name());

    var equipBtn = new Button("Equip");
    equipBtn.setOnAction(
        _ -> {
          if (model.equip(weapon)) {
            message.setVisible(false);
          } else {
            message.setText("No free hand for " + weapon.name());
            message.setVisible(true);
          }
        });
    equipBtn.visibleProperty().bind(currentOrEdit);

    weaponBox.getChildren().addAll(nameLabel, equipBtn);
    return weaponBox;
  }

  private VBox equippedWeaponEntry(CreatureViewModel model, Weapon weapon) {
    var weaponBox = new VBox(2.0);

    var nameLabel = new Label(weapon.name());

    var buttonBox = new HBox(4.0);

    var attackBtn = new Button("Attack");
    attackBtn.setOnAction(
        _ -> {
          model.loadAttacks(weapon);
          nav.set(Nav.Attacks);
        });
    attackBtn.visibleProperty().bind(playOnly);
    attackBtn.managedProperty().bind(attackBtn.visibleProperty());

    var unequipBtn = new Button("Unequip");
    unequipBtn.setOnAction(_ -> model.unequip(weapon));
    unequipBtn.visibleProperty().bind(currentOrEdit);
    unequipBtn.managedProperty().bind(unequipBtn.visibleProperty());

    buttonBox.getChildren().addAll(attackBtn, unequipBtn);
    weaponBox.getChildren().addAll(nameLabel, buttonBox);
    return weaponBox;
  }

  private Node targetSelection(CreatureViewModel model) {
    var cancel = new Button("Cancel");
    cancel.setOnAction(_ -> nav.set(Nav.Weapons));

    var buttons = new VBox();
    model
        .attacksProperty()
        .addListener(
            (ListChangeListener<? super Runnable>)
                _ -> {
                  buttons.getChildren().clear();
                  for (StateProcess a : model.attacksProperty()) {
                    var attackBtn = new Button(a.toString());
                    attackBtn.setOnAction(
                        _ -> {
                          a.run();
                          nav.set(Nav.All);
                        });
                    buttons.getChildren().add(attackBtn);
                  }
                });

    var selection = new VBox(8);
    selection.visibleProperty().bind(nav.isEqualTo(Nav.Attacks));
    selection.managedProperty().bind(nav.isEqualTo(Nav.Attacks));
    selection.getChildren().addAll(cancel, new Separator(), buttons);

    return selection;
  }

  private Node movement(CreatureViewModel model) {
    var left = new Label();
    left.textProperty()
        .bind(model.movementLeftProperty().asString("%.1f").map(s -> "Movement left: " + s));

    var cancel = new Button("Cancel");
    cancel.setOnAction(_ -> stopMoving());

    var box = new VBox();
    box.setAlignment(Pos.CENTER);
    box.visibleProperty().bind(nav.isEqualTo(Nav.Movement));
    box.managedProperty().bind(nav.isEqualTo(Nav.Movement));
    box.getChildren().addAll(left, cancel);
    return box;
  }

  public void stopMoving() {
    nav.set(Nav.All);
    onStopMoving.run();
  }

  public void setOnStopMoving(Runnable onStopMoving) {
    this.onStopMoving = onStopMoving;
  }

  public void setOnStartMoving(Runnable onStartMoving) {
    this.onStartMoving = onStartMoving;
  }

  public void setOnDelete(Runnable onDelete) {
    this.onDelete = onDelete;
  }

  public BooleanProperty editModeProperty() {
    return editMode;
  }
}
