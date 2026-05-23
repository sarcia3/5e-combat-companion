package org.tcs;

import javafx.application.Application;
import javafx.stage.Stage;
import org.tcs.model.State;
import org.tcs.model.geometry.Finite2DGrid;
import org.tcs.ui.Assets;
import org.tcs.ui.PlayView;
import org.tcs.ui.ViewModel;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) {
    Assets.init();
    State state = new State(new Finite2DGrid(100, 100));
    primaryStage.setScene(PlayView.scene(new ViewModel(state)));
    primaryStage.setTitle("Drageons&Dungons 5e Combat Companion");
    primaryStage.show();
  }
}
