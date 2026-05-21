package org.tcs;

import javafx.application.Application;
import javafx.stage.Stage;
import org.tcs.ui.Assets;
import org.tcs.ui.PlayView;
import org.tcs.ui.ViewModel;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) {
    Assets.init();
    primaryStage.setScene(PlayView.scene(new ViewModel()));
    primaryStage.setTitle("Drageons&Dungons 5e Combat Companion");
    primaryStage.show();
  }
}
