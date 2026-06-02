package org.tcs.ui.map;

import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.tcs.ui.Decoration;
import org.tcs.ui.Drawable;

public class EditorContextMenu extends ContextMenu {
  private final ObjectProperty<Runnable> onAddCreature = new SimpleObjectProperty<>(() -> {});
  private final ObjectProperty<ContextTarget> target = new SimpleObjectProperty<>();
  private final ObjectProperty<Drawable> selected = new SimpleObjectProperty<>();

  public EditorContextMenu(ContextMenuHandler handler) {
    var addMenu = new Menu("Add...");

    var addDecoration = new MenuItem("...decoration");
    addDecoration.setOnAction(_ -> handler.addDecoration(target.get().real()));

    var addCreature = new MenuItem("...creature");
    addCreature.setOnAction(_ -> onAddCreature.get().run());

    addMenu.getItems().addAll(addCreature, addDecoration);

    var moveUp = new MenuItem("Move forward");
    moveUp.setOnAction(_ -> handler.moveDecorationForward((Decoration) selected.get()));
    var moveDown = new MenuItem("Move backward");
    moveDown.setOnAction(_ -> handler.moveDecorationBackward((Decoration) selected.get()));
    var moveToTop = new MenuItem("Move to front");
    moveToTop.setOnAction(_ -> handler.moveDecorationToFront((Decoration) selected.get()));
    var moveToBottom = new MenuItem("Move to back");
    moveToBottom.setOnAction(_ -> handler.moveDecorationToBack((Decoration) selected.get()));
    var delete = new MenuItem("Delete");
    delete.setOnAction(_ -> handler.removeDecoration((Decoration) selected.get()));

    var decorationOptions = List.of(moveUp, moveDown, moveToTop, moveToBottom, delete);
    for (MenuItem option : decorationOptions) {
      option.visibleProperty().bind(selected.map(t -> t instanceof Decoration));
      option.disableProperty().bind(selected.map(t -> !(t instanceof Decoration)));
    }

    getItems().addAll(addMenu, new SeparatorMenuItem());
    getItems().addAll(decorationOptions);
  }

  public ObjectProperty<ContextTarget> targetProperty() {
    return target;
  }

  public ObjectProperty<Drawable> selectedProperty() {
    return selected;
  }

  @Override
  public void hide() {
    super.hide();
    target.set(null);
  }
}
