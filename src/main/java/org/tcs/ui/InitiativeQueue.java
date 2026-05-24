package org.tcs.ui;

import java.util.Map;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.tcs.model.Creature;

public class InitiativeQueue extends VBox {
  private final Map<Creature, Image> images;

  public InitiativeQueue(ViewModel model, Map<Creature, Image> images) {
    super(10);
    setAlignment(Pos.TOP_LEFT);
    setPadding(new Insets(10));
    this.images = images;

    var entries = model.initiativeQueueProperty();
    entries.addListener((ListChangeListener<Creature>) _ -> rebuildEntries(entries));
    rebuildEntries(entries);

    setMaxWidth(Region.USE_PREF_SIZE);
  }

  private void rebuildEntries(ObservableList<Creature> creatures) {
    getChildren().clear();

    // We render 10 entries
    if (creatures.isEmpty()) return;
    for (int i = 0; i < 10; i++) {
      var creature = creatures.get(i % creatures.size());
      getChildren().add(createEntryNode(creature));
    }
  }

  private StackPane createEntryNode(Creature creature) {
    ImageView imageView = new ImageView(images.get(creature));
    imageView.setFitWidth(60);
    imageView.setFitHeight(60);
    imageView.setPreserveRatio(true);

    StackPane entryPane = new StackPane(imageView);
    entryPane.setStyle("-fx-background-color: #444; -fx-background-radius: 5;");
    entryPane.setPadding(new Insets(5));

    Tooltip tooltip = new Tooltip(creature.name());
    Tooltip.install(entryPane, tooltip);

    return entryPane;
  }
}
