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
    Weapons
  }

  private final ObjectProperty<Creature> creature = new SimpleObjectProperty<>();
  private final ObjectProperty<Nav> nav = new SimpleObjectProperty<>(Nav.All);

  public CreatureView(Map<Creature, Image> creatureImages, ViewModel model) {
    creature.addListener(_ -> model.loadCreature(creature.get()));

    var portrait = new ImageView();
    portrait.setFitHeight(256.0);
    portrait.setPreserveRatio(true);
    portrait
        .imageProperty()
        .bind(creature.map(key -> creatureImages.getOrDefault(key, Assets.PLACEHOLDER)));

    var attack = new Button("Attack");
    attack.setOnAction(_ -> nav.set(Nav.Weapons));

    var topLevel = new VBox();
    topLevel.getChildren().add(attack);
    topLevel.setAlignment(Pos.CENTER);
    topLevel.visibleProperty().bind(nav.isEqualTo(Nav.All));
    topLevel.managedProperty().bind(nav.isEqualTo(Nav.All));

    var weapons = new VBox();
    model
        .weaponsProperty()
        .addListener(
            (ListChangeListener<? super Weapon>)
                _ -> {
                  weapons.getChildren().clear();
                  for (Weapon weapon : model.weaponsProperty()) {
                    var btn = new Button(weapon.toString());
                    weapons.getChildren().add(btn);
                  }
                });
    weapons.visibleProperty().bind(nav.isEqualTo(Nav.Weapons));
    weapons.managedProperty().bind(nav.isEqualTo(Nav.Weapons));

    getChildren().addAll(portrait, topLevel, weapons);
    setMaxWidth(320.0);
    setAlignment(Pos.TOP_CENTER);
    setBackground(Background.fill(Color.WHITE));
  }

  public ObjectProperty<Creature> creatureProperty() {
    return creature;
  }
}
