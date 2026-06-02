package org.tcs.ui.map;

import org.tcs.model.geometry.Point;
import org.tcs.model.geometry.RealPoint;
import org.tcs.ui.Decoration;

public interface ContextMenuHandler {
  void addPuppet(Point position);

  void addDecoration(RealPoint position);

  void removeDecoration(Decoration decoration);

  void moveDecorationForward(Decoration decoration);

  void moveDecorationBackward(Decoration decoration);

  void moveDecorationToFront(Decoration decoration);

  void moveDecorationToBack(Decoration decoration);
}
