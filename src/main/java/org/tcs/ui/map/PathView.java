package org.tcs.ui.map;

import static org.tcs.ui.map.MapView.PIXELS_PER_FOOT;

import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.tcs.model.geometry.Point;
import org.tcs.model.geometry.RealPoint;
import org.tcs.model.geometry.WorldMap;
import org.tcs.ui.viewmodel.CreatureViewModel;

public class PathView {
  private final CreatureViewModel creatureViewModel;
  private final WorldMap worldMap;
  private List<Point> currentPath;
  private Point target;
  private final ObjectProperty<Consumer<Point>> onSubmit = new SimpleObjectProperty<>(_ -> {});
  private final ObjectProperty<Runnable> onCancel = new SimpleObjectProperty<>(() -> {});

  public PathView(CreatureViewModel creatureViewModel, WorldMap worldMap) {
    this.creatureViewModel = creatureViewModel;
    this.worldMap = worldMap;
  }

  void setTarget(Point target) {
    this.target = target;
    currentPath = creatureViewModel.navMap().pathTo(target);
  }

  void onMouseClicked(MouseEvent event) {
    if (event.getButton().equals(MouseButton.PRIMARY)) {
      if (currentPath != null && !currentPath.isEmpty()) {
        // Copy just in case. Let's not leak mutable state
        onSubmit.get().accept(target);
        currentPath = null;
        target = null;
      }
    } else if (event.getButton().equals(MouseButton.SECONDARY)) {
      currentPath = null;
      target = null;
      onCancel.get().run();
    }
  }

  void draw(GraphicsContext gc) {
    if (currentPath == null || currentPath.isEmpty()) {
      return;
    }

    gc.setStroke(Color.BLUE);
    gc.setLineWidth(3);

    for (int i = 0; i < currentPath.size() - 1; i++) {
      Point abstractCurrent = currentPath.get(i);
      Point abstractNext = currentPath.get(i + 1);

      RealPoint current = worldMap.pointToRealPoint(abstractCurrent).multiply(PIXELS_PER_FOOT);
      RealPoint next = worldMap.pointToRealPoint(abstractNext).multiply(PIXELS_PER_FOOT);

      gc.strokeLine(current.x(), current.y(), next.x(), next.y());
    }

    gc.setFill(Color.BLUE);
    for (Point abstractPoint : currentPath) {
      RealPoint real = worldMap.pointToRealPoint(abstractPoint).multiply(PIXELS_PER_FOOT);
      gc.fillOval(real.x() - 3, real.y() - 3, 6, 6);
    }
  }

  ObjectProperty<Consumer<Point>> onSubmitProperty() {
    return onSubmit;
  }

  ObjectProperty<Runnable> onCancelProperty() {
    return onCancel;
  }
}
