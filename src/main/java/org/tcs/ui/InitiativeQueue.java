package org.tcs.ui;

import java.util.Map;
import java.util.function.Consumer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.tcs.model.Creature;
import org.tcs.ui.viewmodel.ViewModel;

public class InitiativeQueue extends VBox {
  private final Map<Creature, Image> images;
  private Consumer<Creature> onEntryClicked = _ -> {};

  public InitiativeQueue(ViewModel model, Map<Creature, Image> images) {
    super(10);
    setAlignment(Pos.TOP_LEFT);
    setPadding(new Insets(10));
    this.images = images;

    var entries = model.initiativeQueueProperty();
    var firstName = new SimpleStringProperty();
    var label = new Label();
    label.textProperty().bind(firstName.map(v -> "Current: " + v).orElse("No creatures"));
    label.setStyle("-fx-font-weight: bold;");

    entries.addListener(
        (ListChangeListener<Creature>)
            _ -> {
              if (entries.isEmpty()) {
                firstName.set(null);
              } else {
                firstName.set(entries.getFirst().name());
              }
            });
    if (entries.isEmpty()) {
      firstName.set(null);
    } else {
      firstName.set(entries.getFirst().name());
    }

    var entriesBox = new VBox(10);
    entriesBox.setMaxWidth(Region.USE_PREF_SIZE);
    entries.addListener((ListChangeListener<Creature>) _ -> rebuildEntries(entries, entriesBox));
    rebuildEntries(entries, entriesBox);

    setPickOnBounds(false);
    getChildren().addAll(label, entriesBox);
  }

  private void rebuildEntries(ObservableList<Creature> creatures, VBox entriesBox) {
    entriesBox.getChildren().clear();

    // We render 10 entries
    if (creatures.isEmpty()) return;

    var first = creatures.getFirst();
    entriesBox.getChildren().add(createEntryNode(first));
    entriesBox.getChildren().add(new Separator());

    for (int i = 1; i < 7; i++) {
      var creature = creatures.get(i % creatures.size());
      entriesBox.getChildren().add(createEntryNode(creature));
    }
  }

  private StackPane createEntryNode(Creature creature) {
    ImageView imageView = new ImageView(images.getOrDefault(creature, Assets.PLACEHOLDER));
    imageView.setFitWidth(60);
    imageView.setFitHeight(60);
    imageView.setPreserveRatio(true);

    StackPane entryPane = new StackPane(imageView);
    entryPane.getStyleClass().add("queue-view");
    entryPane.setPadding(new Insets(5));
    entryPane.setOnMouseClicked(_ -> onEntryClicked.accept(creature));

    Tooltip tooltip = new Tooltip(creature.name());
    Tooltip.install(entryPane, tooltip);

    return entryPane;
  }

  public void setOnEntryClicked(Consumer<Creature> onEntryClicked) {
    this.onEntryClicked = onEntryClicked;
  }
}
