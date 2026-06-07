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
    Wield,
    Unwield,
    Attacks,
    Movement
  }

  private final ObjectProperty<Nav> nav = new SimpleObjectProperty<>(Nav.All);
  private Runnable onStartMoving = () -> {};
  private Runnable onStopMoving = () -> {};

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
            name,
            portrait,
            nothingToDo,
            topLevel(model),
            wieldSelection(model),
            unwieldSelection(model),
            weaponSelection(model),
            targetSelection(model),
            movement(model));
    setMaxWidth(320.0);
    setAlignment(Pos.TOP_CENTER);
    setBackground(Background.fill(Color.WHITE));
    setPadding(new Insets(8.0));

    model.creatureProperty().addListener(_ -> nav.set(Nav.All));
  }

  private Node topLevel(CreatureViewModel model) {
    var attack = new Button("Attack");
    attack.setOnAction(_ -> nav.set(Nav.Weapons));

    var wield = new Button("Wield");
    wield.setOnAction(_ -> nav.set(Nav.Wield));

    var unwield = new Button("Unwield");
    unwield.setOnAction(_ -> nav.set(Nav.Unwield));

    var move = new Button("Move");
    move.setOnAction(
        _ -> {
          nav.set(Nav.Movement);
          onStartMoving.run();
        });

    var pass = new Button("Pass");
    pass.setOnAction(_ -> model.pass());

    var topLevel = new VBox();
    topLevel.getChildren().addAll(attack, wield, unwield, move, pass);
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
        .wieldedWeaponsProperty()
        .addListener(
            (ListChangeListener<? super Weapon>)
                _ -> {
                  buttons.getChildren().clear();
                  for (Weapon weapon : model.wieldedWeaponsProperty()) {
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

  private Node wieldSelection(CreatureViewModel model) {
    var cancel = new Button("Cancel");
    cancel.setOnAction(_ -> nav.set(Nav.All));

    var message = new Label();
    message.setStyle("-fx-text-fill: red;");
    message.setVisible(false);
    message.managedProperty().bind(message.visibleProperty());

    var buttons = new VBox();
    model
        .carriedWeaponsProperty()
        .addListener(
            (ListChangeListener<? super Weapon>)
                _ -> {
                  buttons.getChildren().clear();
                  for (Weapon weapon : model.carriedWeaponsProperty()) {
                    var btn = new Button(weapon.name());
                    btn.setOnAction(
                        _ -> {
                          if (model.wield(weapon)) {
                            message.setVisible(false);
                          } else {
                            message.setText("No free hand for " + weapon.name());
                            message.setVisible(true);
                          }
                        });
                    buttons.getChildren().add(btn);
                  }
                });

    var selection = new VBox();
    selection.visibleProperty().bind(nav.isEqualTo(Nav.Wield));
    selection.managedProperty().bind(nav.isEqualTo(Nav.Wield));
    selection.getChildren().addAll(cancel, new Separator(), message, buttons);

    return selection;
  }

  private Node unwieldSelection(CreatureViewModel model) {
    var cancel = new Button("Cancel");
    cancel.setOnAction(_ -> nav.set(Nav.All));

    var buttons = new VBox();
    model
        .wieldedWeaponsProperty()
        .addListener(
            (ListChangeListener<? super Weapon>)
                _ -> {
                  buttons.getChildren().clear();
                  for (Weapon weapon : model.wieldedWeaponsProperty()) {
                    var btn = new Button(weapon.name());
                    btn.setOnAction(_ -> model.unwield(weapon));
                    buttons.getChildren().add(btn);
                  }
                });

    var selection = new VBox();
    selection.visibleProperty().bind(nav.isEqualTo(Nav.Unwield));
    selection.managedProperty().bind(nav.isEqualTo(Nav.Unwield));
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
                    attackBtn.setOnAction(
                        _ -> {
                          a.run();
                          nav.set(Nav.All);
                        });
                    buttons.getChildren().add(attackBtn);
                  }
                });

    var selection = new VBox();
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
}
