package org.tcs;

import javafx.application.Application;
import javafx.stage.Stage;
import org.tcs.model.Ability;
import org.tcs.model.Creature;
import org.tcs.model.Damage;
import org.tcs.model.State;
import org.tcs.model.equipment.Weapon;
import org.tcs.model.geometry.Finite2DGrid;
import org.tcs.model.geometry.RealPoint;
import org.tcs.ui.Assets;
import org.tcs.ui.PlayView;
import org.tcs.ui.ViewModel;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) {
    Assets.init();
    var map = new Finite2DGrid(100, 100);
    var state = new State(map);
    var creature = new Creature("Test 1", map.realPointToPoint(new RealPoint(1.0, 1.0)), 10, 20);
    creature.addWeapon(new Weapon("aaa", 1, Ability.CHA, Damage.Type.ACID));
    state.addCreature(creature);
    state.addCreature(
        new Creature("Test 2", map.realPointToPoint(new RealPoint(3.0, 3.0)), 10, 20));
    primaryStage.setScene(new PlayView(new ViewModel(state), primaryStage));
    primaryStage.setTitle("Drageons&Dungons 5e Combat Companion");
    primaryStage.show();
  }
}
