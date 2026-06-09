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
import org.tcs.model.equipment.Armor;
import org.tcs.model.equipment.ArmorLibrary;

public class ArmorSelector {
  private final Stage popup = new Stage();
  private final ListView<ArmorEntry> armorList;
  private Armor selected = null;

  public ArmorSelector(Window owner) {
    popup.initOwner(owner);
    popup.initModality(Modality.APPLICATION_MODAL);
    popup.setTitle("Select Armor");

    armorList = new ListView<>();

    Runnable submit =
        () -> {
          var item = armorList.getSelectionModel().getSelectedItem();
          if (item != null) {
            selected = item.armor;
          }
          popup.close();
        };
    armorList.setOnMouseClicked(
        event -> {
          if (event.getClickCount() == 2) {
            submit.run();
          }
        });

    armorList.setPrefHeight(400);
    armorList.setPrefWidth(300);
    ObservableList<ArmorEntry> entries = FXCollections.observableArrayList();
    armorList.setItems(entries);

    entries.add(new ArmorEntry(null));
    for (Armor armor : ArmorLibrary.getArmors()) {
      entries.add(new ArmorEntry(armor));
    }

    var okButton = new Button("OK");
    okButton.setOnAction(_ -> submit.run());

    var cancelButton = new Button("Cancel");
    cancelButton.setOnAction(_ -> popup.close());

    var buttonBox = new HBox(10, okButton, cancelButton);
    buttonBox.setPadding(new Insets(10));

    var root = new VBox(10, armorList, buttonBox);
    root.setPadding(new Insets(10));

    popup.setScene(new Scene(root));
  }

  public Armor showAndWait() {
    selected = null;
    popup.showAndWait();
    return selected;
  }
}
