package org.tcs.ui;

import java.util.Map;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;
import org.tcs.model.Ability;
import org.tcs.model.Creature;
import org.tcs.model.StateProcess;
import org.tcs.model.equipment.Armor;
import org.tcs.model.equipment.Weapon;
import org.tcs.model.magic.Spell;
import org.tcs.ui.equipment.ArmorSelector;
import org.tcs.ui.equipment.WeaponSelector;
import org.tcs.ui.magic.SpellSelector;
import org.tcs.ui.viewmodel.CreatureViewModel;

public class CreatureView extends VBox {
  private enum Nav {
    All,
    Inventory,
    Process,
    Spells,
    Movement,
    Abilities
  }

  private final ObjectProperty<Nav> nav = new SimpleObjectProperty<>(Nav.All);
  private final BooleanProperty editMode = new SimpleBooleanProperty(false);
  private final BooleanBinding currentOrEdit;
  private final BooleanBinding playOnly;
  private Runnable onStartMoving = () -> {};
  private Runnable onStopMoving = () -> {};
  private Runnable onDelete = () -> {};
  private final WeaponSelector weaponSelector;
  private final ArmorSelector armorSelector;
  private final SpellSelector spellSelector;

  public CreatureView(Map<Creature, Image> creatureImages, CreatureViewModel model, Window owner) {
    currentOrEdit = model.isCurrentProperty().or(editMode);
    playOnly = model.isCurrentProperty().and(editMode.not());
    weaponSelector = new WeaponSelector(owner);
    armorSelector = new ArmorSelector(owner);
    spellSelector = new SpellSelector(owner);

    var name = new Label();
    name.textProperty().bind(model.creatureProperty().map(Creature::name));
    name.setTextAlignment(TextAlignment.CENTER);
    name.getStyleClass().add("title-view");

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
            inventoryView(model),
            spellsView(model),
            targetSelection(model),
            movement(model),
            abilityScores(model));
    setStyle("-fx-font-weight: bold;");
    setMaxWidth(320.0);
    setAlignment(Pos.TOP_CENTER);
    setBackground(Background.fill(Color.WHITE));
    setPadding(new Insets(8.0));

    getStyleClass().add("creature-view");
    model.creatureProperty().addListener(_ -> nav.set(Nav.All));
  }

  private Node topLevelPlay(CreatureViewModel model) {
    var inventory = new Button("Inventory");
    inventory.setOnAction(_ -> nav.set(Nav.Inventory));

    var spells = new Button("Spells");
    spells.setOnAction(_ -> nav.set(Nav.Spells));

    var abilities = new Button("Abilities");
    abilities.setOnAction(_ -> nav.set(Nav.Abilities));

    var move = new Button("Move");
    move.setOnAction(
        _ -> {
          nav.set(Nav.Movement);
          onStartMoving.run();
        });

    var pass = new Button("Pass");
    pass.setOnAction(_ -> model.pass());

    var topLevel = new VBox(8);
    topLevel.getChildren().addAll(inventory, spells, abilities, move, pass);
    topLevel.setAlignment(Pos.CENTER);
    topLevel.visibleProperty().bind(nav.isEqualTo(Nav.All).and(playOnly));
    topLevel.managedProperty().bind(topLevel.visibleProperty());

    return topLevel;
  }

  private Node topLevelEdit() {
    var delete = new Button("Delete this creature");
    delete.setOnAction(_ -> onDelete.run());

    var inventory = new Button("Inventory");
    inventory.setOnAction(_ -> nav.set(Nav.Inventory));

    var spells = new Button("Spells");
    spells.setOnAction(_ -> nav.set(Nav.Spells));

    var abilities = new Button("Abilities");
    abilities.setOnAction(_ -> nav.set(Nav.Abilities));

    var topLevel = new VBox(8);
    topLevel.getChildren().addAll(delete, inventory, spells, abilities);
    topLevel.setAlignment(Pos.CENTER);
    topLevel.visibleProperty().bind(nav.isEqualTo(Nav.All).and(editMode));
    topLevel.managedProperty().bind(topLevel.visibleProperty());

    return topLevel;
  }

  private Node inventoryView(CreatureViewModel model) {
    var cancel = new Button("Cancel");
    cancel.setOnAction(_ -> nav.set(Nav.All));

    var message = new Label();
    message.getStyleClass().add("error-text");
    message.setVisible(false);
    message.managedProperty().bind(message.visibleProperty());

    var equippedLabel = new Label("Equipped weapons:");

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

    var addWeapon = new Button("Add a weapon");
    addWeapon.setOnAction(
        _ -> {
          Weapon weapon = weaponSelector.showAndWait();

          if (weapon != null) {
            model.addStoredWeapon(weapon);
          }
        });
    addWeapon.visibleProperty().bind(editMode);
    addWeapon.managedProperty().bind(addWeapon.visibleProperty());

    var selection = new VBox(8.0);
    selection.visibleProperty().bind(nav.isEqualTo(Nav.Inventory).and(currentOrEdit));
    selection.managedProperty().bind(nav.isEqualTo(Nav.Inventory).and(currentOrEdit));
    selection
        .getChildren()
        .addAll(
            cancel,
            message,
            new Separator(),
            armorView(model),
            new Separator(),
            equippedLabel,
            equippedWeapons,
            new Separator(),
            storedLabel,
            storedWeapons,
            new Separator(),
            addWeapon);
    return selection;
  }

  private Node armorView(CreatureViewModel model) {
    var armorLabel = new Label("Equipped armor:");
    var equippedArmor = new Label();
    equippedArmor.textProperty().bind(model.wornArmorProperty());

    var changeArmorButton = new Button("Change");
    changeArmorButton.setOnAction(
        _ -> {
          Armor armor = armorSelector.showAndWait();
          model.equip(armor);
        });
    changeArmorButton.visibleProperty().bind(editMode);
    changeArmorButton.managedProperty().bind(editMode);

    var spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    var armorBox = new HBox(equippedArmor, spacer, changeArmorButton);
    armorBox.setAlignment(Pos.CENTER_LEFT);

    var armorClass = new Label();
    armorClass.textProperty().bind(model.armorClassProperty().asString().map(s -> "AC: " + s));

    return new VBox(armorLabel, armorBox, armorClass);
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
          nav.set(Nav.Process);
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
    editMode.addListener(
        _ -> {
          if (editMode.get() && nav.isEqualTo(Nav.Process).get()) nav.set(Nav.All);
        });

    var cancel = new Button("Cancel");
    cancel.setOnAction(_ -> nav.set(Nav.All));

    var buttons = new VBox(8);
    model
        .processesProperty()
        .addListener(
            (ListChangeListener<? super Runnable>)
                _ -> {
                  buttons.getChildren().clear();
                  for (StateProcess a : model.processesProperty()) {
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
    selection.visibleProperty().bind(nav.isEqualTo(Nav.Process));
    selection.managedProperty().bind(nav.isEqualTo(Nav.Process));
    selection.getChildren().addAll(cancel, new Separator(), buttons);

    return selection;
  }

  private Node spellsView(CreatureViewModel model) {
    var cancel = new Button("Cancel");
    cancel.setOnAction(_ -> nav.set(Nav.All));

    var spellsList = new VBox(8, cancel);

    model
        .spellsProperty()
        .addListener(
            (ListChangeListener<? super Spell>)
                _ -> {
                  spellsList.getChildren().clear();
                  for (Spell spell : model.spellsProperty()) {
                    spellsList.getChildren().add(spellEntry(spell, model));
                  }
                });

    var add = new Button("Add a spell");
    add.setOnAction(_ -> model.addSpell(spellSelector.showAndWait()));
    add.visibleProperty().bind(nav.isEqualTo(Nav.Spells).and(editMode));
    add.managedProperty().bind(visibleProperty());

    var box = new VBox(10, cancel, spellsList, add);
    box.visibleProperty().bind(nav.isEqualTo(Nav.Spells).and(currentOrEdit));
    box.managedProperty().bind(box.visibleProperty());

    return box;
  }

  private Node spellEntry(Spell spell, CreatureViewModel model) {
    var label = new Label(spell.name());

    var castButton = new Button("Cast");
    castButton.setOnAction(
        _ -> {
          model.loadSpellProcesses(spell);
          nav.set(Nav.Process);
        });
    castButton.visibleProperty().bind(playOnly);
    castButton.managedProperty().bind(castButton.visibleProperty());

    var deleteButton = new Button("Delete");
    deleteButton.managedProperty().bind(editMode);
    deleteButton.visibleProperty().bind(editMode);
    deleteButton.setOnAction(_ -> model.removeSpell(spell));

    var buttonBox = new HBox(10, castButton, deleteButton);
    return new VBox(label, buttonBox);
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

  private Node abilityScores(CreatureViewModel model) {
    var cancel = new Button("Cancel");
    cancel.setOnAction(_ -> nav.set(Nav.All));

    var label = new Label("Abilities:");

    var box = new VBox(5, cancel, label, new Separator());
    box.visibleProperty().bind(nav.isEqualTo(Nav.Abilities));
    box.managedProperty().bind(box.visibleProperty());

    for (Ability ability : Ability.values()) {
      var name = new Label(ability.name() + ": ");

      var score = new Label();
      score.textProperty().bind(model.abilityScoreProperty(ability).asString());

      var modifier = new Label();
      modifier
          .textProperty()
          .bind(model.abilityModifierProperty(ability).asString().map(s -> "(" + s + ")"));
      box.getChildren().add(new HBox(3, name, score, modifier));
    }

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
