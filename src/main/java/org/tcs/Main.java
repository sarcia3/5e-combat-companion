package org.tcs;

import javafx.application.Application;
import javafx.stage.Stage;
import org.tcs.model.Creature;
import org.tcs.model.State;
import org.tcs.model.equipment.WeaponsLibrary;
import org.tcs.model.geometry.Finite2DGrid;
import org.tcs.model.geometry.RealPoint;
import org.tcs.ui.Assets;
import org.tcs.ui.PlayView;
import org.tcs.ui.WindowDiceRoller;
import org.tcs.ui.viewmodel.ViewModel;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) {
    Assets.init();
    var map = new Finite2DGrid(100, 100);
    var state = new State(map);
    var creature =
        new Creature.Builder()
            .name("Test 1")
            .position(map.realPointToPoint(new RealPoint(1.0, 1.0)))
            .diceRoller(new WindowDiceRoller(primaryStage))
            .build();

    creature.inventory().addStoredWeapon(WeaponsLibrary.get("Dagger"));
    creature.inventory().equipWeapon(WeaponsLibrary.get("Dagger"));
    state.addCreature(creature);
    state.addCreature(
        new Creature.Builder()
            .name("Test 2")
            .position(map.realPointToPoint(new RealPoint(3.0, 3.0)))
            .diceRoller(new WindowDiceRoller(primaryStage))
            .build());
    primaryStage.setScene(new PlayView(new ViewModel(state), primaryStage));
    primaryStage.setTitle("Drageons&Dungons 5e Combat Companion");
    primaryStage.show();
  }

  @SuppressWarnings("unused")
  static void main(String[] args) {
    launch(args);
  }
}
