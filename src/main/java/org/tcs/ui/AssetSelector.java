package org.tcs.ui;

import java.util.Map;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

// Warning: LLM made a lot of this

public class AssetSelector {
  private final Stage popup;
  private String selectedImageKey = null;

  public AssetSelector(Window owner) {
    popup = new Stage();
    popup.initOwner(owner);
    popup.setTitle("Select Asset");
    popup.initModality(Modality.APPLICATION_MODAL);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(10));
    grid.getStyleClass().add("image-selector");

    int col = 0;
    int row = 0;
    int maxCols = 4;

    for (Map.Entry<String, Image> entry : Assets.images.entrySet()) {
      VBox cell = createImageCell(entry.getKey(), entry.getValue());
      grid.add(cell, col, row);

      col++;
      if (col >= maxCols) {
        col = 0;
        row++;
      }
    }

    ScrollPane scrollPane = new ScrollPane(grid);
    scrollPane.setFitToWidth(true);

    Button okButton = new Button("Ok");
    okButton.setOnAction(_ -> popup.hide());

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(
        _ -> {
          selectedImageKey = null;
          popup.hide();
        });

    HBox buttonBox = new HBox(10, okButton, cancelButton);
    buttonBox.setAlignment(Pos.CENTER_RIGHT);
    buttonBox.setPadding(new Insets(10));

    var root = new BorderPane();

    root.setCenter(scrollPane);
    root.setBottom(buttonBox);

    Scene scene = new Scene(root, 600, 600);
    scene
        .getStylesheets()
        .add(Objects.requireNonNull(getClass().getResource("/global.css")).toExternalForm());
    popup.setScene(scene);
  }

  private VBox createImageCell(String key, Image image) {
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(100);
    imageView.setFitHeight(100);
    imageView.setPreserveRatio(true);

    Label label = new Label(key);
    label.setWrapText(true);
    label.setMaxWidth(100);
    label.setAlignment(Pos.CENTER);

    VBox cell = new VBox(5, imageView, label);
    cell.setAlignment(Pos.CENTER);
    cell.setPadding(new Insets(5));
    cell.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");

    cell.setOnMouseClicked(
        event -> {
          selectedImageKey = key;
          GridPane parent = (GridPane) cell.getParent();
          parent
              .getChildren()
              .forEach(
                  node -> {
                    if (node instanceof VBox) {
                      node.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
                    }
                  });
          cell.setStyle("-fx-border-color: blue; -fx-border-width: 2;");

          if (event.getClickCount() == 2) {
            popup.hide();
          }
        });

    return cell;
  }

  public String showAndWait() {
    popup.showAndWait();
    return selectedImageKey;
  }
}
