package org.tcs.ui.magic;

import java.util.Objects;
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
import org.tcs.model.magic.Spell;
import org.tcs.model.magic.SpellLibrary;

public class SpellSelector {
  private final Stage popup = new Stage();
  private final ListView<SpellEntry> spellList;
  private Spell selected = null;

  public SpellSelector(Window owner) {
    popup.initOwner(owner);
    popup.initModality(Modality.APPLICATION_MODAL);
    popup.setTitle("Select Spell");

    spellList = new ListView<>();

    Runnable submit =
        () -> {
          var item = spellList.getSelectionModel().getSelectedItem();
          if (item != null) {
            selected = item.spell;
          }
          popup.close();
        };
    spellList.setOnMouseClicked(
        event -> {
          if (event.getClickCount() == 2) {
            submit.run();
          }
        });

    spellList.setPrefHeight(400);
    spellList.setPrefWidth(300);
    ObservableList<SpellEntry> entries = FXCollections.observableArrayList();
    spellList.setItems(entries);

    for (Spell Spell : SpellLibrary.getSpells()) {
      entries.add(new SpellEntry(Spell));
    }

    var okButton = new Button("OK");
    okButton.setOnAction(_ -> submit.run());

    var cancelButton = new Button("Cancel");
    cancelButton.setOnAction(_ -> popup.close());

    var buttonBox = new HBox(10, okButton, cancelButton);
    buttonBox.setPadding(new Insets(10));

    var root = new VBox(10, spellList, buttonBox);
    root.setPadding(new Insets(10));

    Scene scene = new Scene(root);
    scene
        .getStylesheets()
        .add(Objects.requireNonNull(getClass().getResource("/global.css")).toExternalForm());

    popup.setScene(scene);
  }

  public Spell showAndWait() {
    selected = null;
    popup.showAndWait();
    return selected;
  }
}
