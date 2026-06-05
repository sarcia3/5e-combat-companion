package org.tcs.ui;

import java.util.Map;
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
import org.tcs.ui.viewmodel.CreatureEditViewModel;

public class CreatureEdit extends VBox {
  private final WeaponSelector weaponSelector;

  public CreatureEdit(
      Map<Creature, Image> creatureImages, CreatureEditViewModel model, Window owner) {
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

    getChildren().addAll(name, portrait, weapons(model));
    setMaxWidth(320.0);
    setAlignment(Pos.TOP_CENTER);
    setBackground(Background.fill(Color.WHITE));
    setPadding(new Insets(8.0));
  }

  private Node weapons(CreatureEditViewModel model) {
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

    var add = new Button("Add Weapon");
    add.setOnAction(
        _ -> {
          Weapon weapon = weaponSelector.showAndWait();

          if (weapon != null) {
            model.addWeapon(weapon);
          }
        });

    var weapons = new VBox();
    weapons.getChildren().addAll(owned, add);
    weapons.setAlignment(Pos.CENTER);

    return weapons;
  }
}
