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
import javafx.scene.control.Separator;
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

public class CreatureEdit extends VBox {
  private enum Nav {
    All,
    Weapons,
  }

  private final ObjectProperty<Nav> nav = new SimpleObjectProperty<>(Nav.All);
  private final WeaponSelector weaponSelector;
  private Runnable onDelete = () -> {};

  public CreatureEdit(Map<Creature, Image> creatureImages, CreatureViewModel model, Window owner) {
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

    getChildren().addAll(name, portrait, topLevel(), weapons(model));
    setMaxWidth(320.0);
    setAlignment(Pos.TOP_CENTER);
    setBackground(Background.fill(Color.WHITE));
    setPadding(new Insets(8.0));
  }

  private Node topLevel() {
    var delete = new Button("Delete this creature");
    delete.setOnAction(_ -> onDelete.run());

    var weapons = new Button("Weapons");
    weapons.setOnAction(_ -> nav.set(Nav.Weapons));

    var topLevel = new VBox();
    topLevel.getChildren().addAll(delete, weapons);
    topLevel.setAlignment(Pos.CENTER);
    topLevel.visibleProperty().bind(nav.isEqualTo(Nav.All));
    topLevel.managedProperty().bind(nav.isEqualTo(Nav.All));

    return topLevel;
  }

  private Node weapons(CreatureViewModel model) {
    var back = new Button("Back");
    back.setOnAction(_ -> nav.set(Nav.All));

    var owned = new VBox();
    model
        .storedWeaponsProperty()
        .addListener(
            (ListChangeListener<? super Weapon>)
                _ -> {
                  owned.getChildren().clear();
                  for (Weapon weapon : model.storedWeaponsProperty()) {
                    owned.getChildren().add(new WeaponEntry(weapon));
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

    var weapons = new VBox();
    weapons.visibleProperty().bind(nav.isEqualTo(Nav.Weapons));
    weapons.managedProperty().bind(nav.isEqualTo(Nav.Weapons));
    weapons.getChildren().addAll(back, new Separator(), owned, add);
    weapons.setAlignment(Pos.CENTER);

    return weapons;
  }

  public void setOnDelete(Runnable onDelete) {
    this.onDelete = onDelete;
  }
}
