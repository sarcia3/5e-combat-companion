package org.tcs.ui;

import java.util.Map;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

// Warning: LLM made this

public class AssetSelector extends BorderPane {
  private String selectedImageKey = null;
  private Consumer<String> onOk = null;
  private Runnable onCancel = null;

  public AssetSelector() {
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(10));

    int col = 0;
    int row = 0;
    int maxCols = 5;

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
    okButton.setOnAction(
        _ -> {
          if (onOk != null) {
            onOk.accept(selectedImageKey);
          }
        });

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(
        _ -> {
          if (onCancel != null) {
            onCancel.run();
          }
        });

    HBox buttonBox = new HBox(10, okButton, cancelButton);
    buttonBox.setBackground(Background.fill(Color.WHITE));
    buttonBox.setAlignment(Pos.CENTER_RIGHT);
    buttonBox.setPadding(new Insets(10));

    setCenter(scrollPane);
    setBottom(buttonBox);
    // Honestly not sure why both are needed
    setPrefSize(Region.USE_COMPUTED_SIZE, 600);
    setMaxSize(Region.USE_PREF_SIZE, 600);
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
        _ -> {
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
        });

    return cell;
  }

  public void setOnOk(Consumer<String> onOk) {
    this.onOk = onOk;
  }

  public void setOnCancel(Runnable onCancel) {
    this.onCancel = onCancel;
  }
}
