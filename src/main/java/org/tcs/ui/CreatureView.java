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
import org.tcs.model.Creature;
import org.tcs.model.StateProcess;
import org.tcs.model.equipment.Weapon;
import org.tcs.ui.viewmodel.CreatureViewModel;

public class CreatureView extends VBox {
  private enum Nav {
    All,
    Weapons,
    Attacks
  }

  private final ObjectProperty<Nav> nav = new SimpleObjectProperty<>(Nav.All);

  public CreatureView(Map<Creature, Image> creatureImages, CreatureViewModel model) {
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
    nothingToDo.visibleProperty().bind(model.isCurrentProperty().not());
    nothingToDo.managedProperty().bind(model.isCurrentProperty().not());
    getChildren()
        .addAll(
            name, portrait, nothingToDo, topLevel(model), weaponSelection(model), targetSelection(model));
    setMaxWidth(320.0);
    setAlignment(Pos.TOP_CENTER);
    setBackground(Background.fill(Color.WHITE));
    setPadding(new Insets(8.0));

    model.creatureProperty().addListener(_ -> nav.set(Nav.All));
  }

  private Node topLevel(CreatureViewModel model) {
    var attack = new Button("Attack");
    attack.setOnAction(_ -> nav.set(Nav.Weapons));

    var pass = new Button("Pass");
    pass.setOnAction(_ -> model.pass());

    var topLevel = new VBox();
    topLevel.getChildren().addAll(attack, pass);
    topLevel.setAlignment(Pos.CENTER);
    topLevel.visibleProperty().bind(nav.isEqualTo(Nav.All).and(model.isCurrentProperty()));
    topLevel.managedProperty().bind(nav.isEqualTo(Nav.All).and(model.isCurrentProperty()));

    return topLevel;
  }

  private Node weaponSelection(CreatureViewModel model) {
    var cancel = new Button("Cancel");
    cancel.setOnAction(_ -> nav.set(Nav.All));

    var buttons = new VBox();
    model
        .weaponsProperty()
        .addListener(
            (ListChangeListener<? super Weapon>)
                _ -> {
                  buttons.getChildren().clear();
                  for (Weapon weapon : model.weaponsProperty()) {
                    var btn = new Button(weapon.name());
                    btn.setOnAction(
                        _ -> {
                          model.loadAttacks(weapon);
                          nav.set(Nav.Attacks);
                        });
                    buttons.getChildren().add(btn);
                  }
                });

    var selection = new VBox();
    selection.visibleProperty().bind(nav.isEqualTo(Nav.Weapons));
    selection.managedProperty().bind(nav.isEqualTo(Nav.Weapons));
    selection.getChildren().addAll(cancel, new Separator(), buttons);

    return selection;
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
                    attackBtn.setOnAction(_ -> a.run());
                    buttons.getChildren().add(attackBtn);
                    nav.set(Nav.Attacks);
                  }
                });

    var selection = new VBox();
    selection.visibleProperty().bind(nav.isEqualTo(Nav.Attacks));
    selection.managedProperty().bind(nav.isEqualTo(Nav.Attacks));
    selection.getChildren().addAll(cancel, new Separator(), buttons);

    return selection;
  }
}
