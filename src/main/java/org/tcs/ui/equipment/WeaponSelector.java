package org.tcs.ui.equipment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.tcs.model.equipment.Weapon;
import org.tcs.model.equipment.WeaponsLibrary;

public class WeaponSelector {
  private final Stage popup = new Stage();
  private final ListView<WeaponEntry> weaponList;
  private Weapon selected = null;

  public WeaponSelector(Window owner) {
    popup.initOwner(owner);
    popup.initModality(Modality.APPLICATION_MODAL);
    popup.setTitle("Select Weapon");

    weaponList = new ListView<>();

    Runnable submit =
        () -> {
          var item = weaponList.getSelectionModel().getSelectedItem();
          if (item != null) {
            selected = item.weapon;
          }
          popup.close();
        };
    weaponList.setOnMouseClicked(
        event -> {
          if (event.getClickCount() == 2) {
            submit.run();
          }
        });

    weaponList.setPrefHeight(400);
    weaponList.setPrefWidth(300);
    ObservableList<WeaponEntry> entries = FXCollections.observableArrayList();
    weaponList.setItems(entries);

    for (Weapon weapon : WeaponsLibrary.getWeapons()) {
      entries.add(new WeaponEntry(weapon));
    }

    var okButton = new Button("OK");
    okButton.setOnAction(_ -> submit.run());

    var cancelButton = new Button("Cancel");
    cancelButton.setOnAction(_ -> popup.close());

    var buttonBox = new HBox(10, okButton, cancelButton);
    buttonBox.setPadding(new Insets(10));

    var root = new VBox(10, weaponList, buttonBox);
    root.setPadding(new Insets(10));

    popup.setScene(new Scene(root));
  }

  public Weapon showAndWait() {
    selected = null;
    popup.showAndWait();
    return selected;
  }
}
