package org.tcs.ui.util;

import javafx.scene.control.TextField;

// No idea for a better name
public class BetterTextField extends TextField {
  public BetterTextField() {
    onMouseClickedProperty().addListener(_ -> selectAll());
  }
}
