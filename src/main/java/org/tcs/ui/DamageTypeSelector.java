package org.tcs.ui;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.tcs.model.Damage;

public class DamageTypeSelector {
  private final Stage popup = new Stage();
  private final List<BooleanProperty> content = new ArrayList<>();
  private boolean submitted;

  public DamageTypeSelector(Window owner) {
    popup.initOwner(owner);
    popup.initModality(Modality.APPLICATION_MODAL);
    popup.setTitle("Select types");

    ObservableList<HBox> list = FXCollections.observableArrayList();
    for (Damage.Type type : Damage.Type.values()) {
      BooleanProperty selected = new SimpleBooleanProperty(false);
      content.add(selected);

      var checkBox = new CheckBox();
      checkBox.selectedProperty().bindBidirectional(selected);

      var spacer = new Region();
      HBox.setHgrow(spacer, Priority.ALWAYS);

      var box = new HBox(new Label(type.toString()), spacer, checkBox);
      list.add(box);
    }

    var typesList = new ListView<HBox>();
    typesList.setItems(list);
    typesList.getStyleClass().add("type-selector-list");

    var okButton = new Button("OK");
    okButton.setOnAction(
        _ -> {
          submitted = true;
          popup.close();
        });

    var cancelButton = new Button("Cancel");
    cancelButton.setOnAction(_ -> popup.close());

    var buttonBox = new HBox(10, okButton, cancelButton);
    buttonBox.setPadding(new Insets(10));

    var root = new VBox(10, typesList, buttonBox);
    root.setPadding(new Insets(10));

    Scene scene = new Scene(root);
    scene
        .getStylesheets()
        .add(Objects.requireNonNull(getClass().getResource("/global.css")).toExternalForm());

    popup.setScene(scene);
  }

  public EnumSet<Damage.Type> showAndWait(ObservableSet<Damage.Type> set) {
    int i = 0;
    for (Damage.Type type : Damage.Type.values()) {
      content.get(i).set(set.contains(type));
      i++;
    }
    submitted = false;

    popup.showAndWait();

    if (submitted) {
      EnumSet<Damage.Type> selected = EnumSet.noneOf(Damage.Type.class);
      int j = 0;
      for (Damage.Type type : Damage.Type.values()) {
        if (content.get(j).get()) selected.add(type);
        j++;
      }
      return selected;
    }

    return null;
  }
}
