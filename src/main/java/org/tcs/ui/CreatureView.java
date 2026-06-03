package org.tcs.ui;

import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.tcs.model.Creature;

public class CreatureView extends VBox {
  private final ObjectProperty<Creature> creature = new SimpleObjectProperty<>();

  public CreatureView(Map<Creature, Image> creatureImages) {
    var portrait = new ImageView();
    portrait.setFitHeight(256.0);
    portrait.setPreserveRatio(true);
    portrait
        .imageProperty()
        .bind(creature.map(key -> creatureImages.getOrDefault(key, Assets.PLACEHOLDER)));
    getChildren().add(portrait);

    setMaxWidth(320.0);
    setAlignment(Pos.TOP_CENTER);
    setBackground(Background.fill(Color.WHITE));
  }

  public ObjectProperty<Creature> creatureProperty() {
    return creature;
  }
}
