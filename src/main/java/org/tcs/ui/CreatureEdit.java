package org.tcs.ui;

import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;
import org.tcs.model.Creature;
import org.tcs.model.equipment.Weapon;
import org.tcs.ui.viewmodel.CreatureViewModel;
import org.tcs.ui.viewmodel.ViewModel;

public class CreatureEdit extends VBox {
  private final WeaponSelector weaponSelector;

  private enum Nav {
    Choice,
    Weapons
  }

  private final ObjectProperty<Nav> nav = new SimpleObjectProperty<>(Nav.Choice);

  public CreatureEdit(
      Map<Creature, Image> creatureImages,
      CreatureViewModel creatureViewModel,
      Window owner,
      ViewModel viewModel) {
    weaponSelector = new WeaponSelector(owner);

    creatureViewModel
        .creatureProperty()
        .addListener(
            (_, _, new_c) -> {
              if (new_c != null) nav.set(Nav.Choice);
            });

    var name = new Label();
    name.textProperty().bind(creatureViewModel.creatureProperty().map(Creature::name));
    name.setTextAlignment(TextAlignment.CENTER);
    name.setStyle("-fx-font-weight: bold; -fx-font-size: 24px;");

    var portrait = new ImageView();
    portrait.setFitHeight(256.0);
    portrait.setPreserveRatio(true);
    portrait
        .imageProperty()
        .bind(
            creatureViewModel
                .creatureProperty()
                .map(key -> creatureImages.getOrDefault(key, Assets.PLACEHOLDER)));

    getChildren()
        .addAll(name, portrait, weapons(creatureViewModel), choices(viewModel, creatureViewModel));
    setMaxWidth(320.0);
    setAlignment(Pos.TOP_CENTER);
    setBackground(Background.fill(Color.WHITE));
    setPadding(new Insets(8.0));
  }

  private Node choices(ViewModel viewModel, CreatureViewModel creature) {
    var weapons = new Button("Edit weapons");
    weapons.setOnAction(
        _ -> {
          nav.set(Nav.Weapons);
        });

    var deletion = new Button("Delete the creature");
    deletion.setOnAction(
        _ -> {
          viewModel.removeCreature(creature.creatureProperty().get());
        });

    var choices = new VBox();
    choices.getChildren().addAll(weapons, deletion);
    choices.setAlignment(Pos.CENTER);

    choices.visibleProperty().bind(nav.isEqualTo(Nav.Choice));
    choices.managedProperty().bind(nav.isEqualTo(Nav.Choice));

    return choices;
  }

  private Node weapons(CreatureViewModel model) {
    var owned = new VBox();
    model
        .weaponsProperty()
        .addListener(
            (ListChangeListener<? super Weapon>)
                _ -> {
                  owned.getChildren().clear();
                  for (Weapon weapon : model.weaponsProperty()) {
                    owned.getChildren().add(new WeaponEntry(weapon));
                  }
                });

    var add = new Button("Add a weapon");
    add.setOnAction(
        _ -> {
          Weapon weapon = weaponSelector.showAndWait();

          if (weapon != null) {
            model.addWeapon(weapon);
          }
        });

    var back = new Button("Go back");
    back.setOnAction(
        _ -> {
          nav.set(Nav.Choice);
        });

    var weapons = new VBox();
    weapons.getChildren().addAll(owned, add, back);
    weapons.setAlignment(Pos.CENTER);

    weapons.visibleProperty().bind(nav.isEqualTo(Nav.Weapons));
    weapons.managedProperty().bind(nav.isEqualTo(Nav.Weapons));

    return weapons;
  }
}
