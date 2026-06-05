package org.tcs.ui;

import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.tcs.model.Creature;
import org.tcs.model.equipment.Weapon;

public class CreatureView extends VBox {
  private enum Nav {
    All,
    Weapons,
    Attacks
  }

  private final ObjectProperty<Nav> nav = new SimpleObjectProperty<>(Nav.All);

  public CreatureView(Map<Creature, Image> creatureImages, ViewModel model) {
    var portrait = new ImageView();
    portrait.setFitHeight(256.0);
    portrait.setPreserveRatio(true);
    portrait
        .imageProperty()
        .bind(
            model
                .selectedProperty()
                .map(key -> creatureImages.getOrDefault(key, Assets.PLACEHOLDER)));

    var attack = new Button("Attack");
    attack.setOnAction(_ -> nav.set(Nav.Weapons));

    var topLevel = new VBox();
    topLevel.getChildren().add(attack);
    topLevel.setAlignment(Pos.CENTER);
    topLevel.visibleProperty().bind(nav.isEqualTo(Nav.All));
    topLevel.managedProperty().bind(nav.isEqualTo(Nav.All));

    var attacks = new VBox();
    var weapons = new VBox();
    model
        .weaponsProperty()
        .addListener(
            (ListChangeListener<? super Weapon>)
                _ -> {
                  weapons.getChildren().clear();
                  for (Weapon weapon : model.weaponsProperty()) {
                    var btn = new Button(weapon.toString());
                    btn.setOnAction(
                        _ -> {
                          attacks.getChildren().clear();
                          for (Runnable a : model.getWeaponAttacks(weapon)) {
                            var attackBtn = new Button(a.toString());
                            attackBtn.setOnAction(_ -> a.run());
                            attacks.getChildren().add(attackBtn);
                            nav.set(Nav.Attacks);
                          }
                        });
                    weapons.getChildren().add(btn);
                  }
                });
    weapons.visibleProperty().bind(nav.isEqualTo(Nav.Weapons));
    weapons.managedProperty().bind(nav.isEqualTo(Nav.Weapons));

    attacks.visibleProperty().bind(nav.isEqualTo(Nav.Attacks));
    attacks.managedProperty().bind(nav.isEqualTo(Nav.Attacks));

    getChildren().addAll(portrait, topLevel, weapons, attacks);
    setMaxWidth(320.0);
    setAlignment(Pos.TOP_CENTER);
    setBackground(Background.fill(Color.WHITE));
  }
}
