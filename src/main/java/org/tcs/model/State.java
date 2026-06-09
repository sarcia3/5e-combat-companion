package org.tcs.model;

import java.util.*;
import java.util.function.Consumer;
import org.tcs.model.activity.WeaponAttack;
import org.tcs.model.equipment.Weapon;
import org.tcs.model.geometry.*;
import org.tcs.model.magic.Ranged;
import org.tcs.model.magic.SingleCreature;
import org.tcs.model.magic.Spell;
import org.tcs.model.util.Pair;

@SuppressWarnings("unused")
public class State {
  InitiativeTracker initiative;
  List<Creature> creatures;
  WorldMap worldMap;
  Consumer<Creature> onRemoveCreature = (_) -> {};
  Consumer<Creature> onAddCreature = (_) -> {};

  public State(WorldMap worldMap) {
    creatures = new ArrayList<>();
    this.worldMap = worldMap;
    initiative = new InitiativeTracker();
  }

  public State(Collection<? extends Creature> creatures, WorldMap worldMap) {
    this.worldMap = worldMap;
    initiative = new InitiativeTracker(creatures);
    this.creatures = new ArrayList<>(creatures);
  }

  public WorldMap getMap() {
    return worldMap;
  }

  /**
   * Adds a creature to the state.
   *
   * @return False if the creature already exists in this state or the position is already occupied.
   */
  public boolean addCreature(Creature creature) {
    if (creatures.contains(creature)) return false;
    if (!worldMap.occupyPoint(creature.position(), OccupyReason.Creature)) return false;
    creatures.add(creature);
    initiative.add(creature);
    onAddCreature.accept(creature);
    return true;
  }

  /**
   * Deletes a creature from the state.
   *
   * @return False if the creature was not in the state and true otherwise.
   */
  public boolean removeCreature(Creature creature) {
    if (!creatures.contains(creature)) return false;
    creatures.remove(creature);
    worldMap.freePoint(creature.position());
    initiative.remove(creature);
    onRemoveCreature.accept(creature);
    return true;
  }

  public List<Creature> getCreatures() {
    return creatures;
  }

  public List<HasInitiative> getTurnOrder() {
    return initiative.getOrder();
  }

  public boolean setCreaturePosition(Creature creature, Point position) {
    if (!creatures.contains(creature))
      throw new IllegalArgumentException("Creature " + creature + " not in state");
    if (!worldMap.occupyPoint(position, OccupyReason.Creature)) return false;
    worldMap.freePoint(creature.position());
    creature.setPosition(position);
    return true;
  }

  /**
   * Tries to move the actor from its current position to target position.
   *
   * @return True if the move was successful and false otherwise.
   * @throws IllegalArgumentException if actor is not part of the state.
   */
  // TODO add a flag that will allow passing through obstacles
  public boolean moveCreature(Creature actor, Point target) {
    Point start = actor.position();
    if (!creatures.contains(actor)) throw new IllegalArgumentException();

    double distance = worldMap.getDistance(start, target);
    if (!actor.turnTracker.canMove(distance)) return false;
    // We should never move to an occupied or non-existent point
    if (!worldMap.occupyPoint(target, OccupyReason.Creature)) return false;

    worldMap.freePoint(start);
    actor.setPosition(target);
    actor.turnTracker.move(distance);

    return true;
  }

  /**
   * @return A list of all creatures registered in the map that are in the given distance of the
   *     given point.
   */
  // TODO add an option to ignore obstacles
  public Collection<Creature> getCreaturesWithinDistance(Point point, double distance) {
    List<Point> pointsInDistance = new ArrayList<>();

    for (Pair<Point, Double> pair :
        worldMap.getDistances(point, creatures.stream().map(Creature::position).toList()))
      if (pair.second() <= distance) pointsInDistance.add(pair.first());

    return creatures.stream().filter(c -> pointsInDistance.contains(c.position())).toList();
  }

  public Collection<StateProcess> getPossibleAttacks(Creature actor, Weapon weapon) {
    // Maybe we should check here whether the actor has the weapon?
    // I think yes ~ Sara
    if (!actor.inventory().getEquippedWeapons().contains(weapon))
      throw new IllegalArgumentException();

    // Ditto for actor being the current player (that is actor.equals(initiative.getFirst())

    Collection<WeaponAttack> attacks = weapon.generateAttacks(actor);
    List<StateProcess> list = new ArrayList<>();

    if (actor.isUnconscious() || !actor.turnTracker.hasAttackAction()) return list;

    for (WeaponAttack attack : attacks) {
      for (Creature target : getCreaturesWithinDistance(actor.position(), attack.getRange()))
        if (target != actor) {
          list.add(
              new StateProcess() {

                @Override
                public Creature getTarget() {
                  return target;
                }

                @Override
                public Creature getSource() {
                  return actor;
                }

                @Override
                public void run() {
                  attack.resolve(State.this, target);
                  actor.turnTracker.makeAttackAction();
                  if (target.isDead()) removeCreature(target);
                }

                @Override
                public String toString() {
                  return "Attack " + target + attack;
                }
              });
        }
    }
    return list;
  }

  public Collection<StateProcess> getPossibleSpells(Creature actor, Spell spell) {
    // in my opinion this a bad way of handling this. I think just a method in activity called
    // castSpell would be cleaner. That way you choose a spell, and then choose target(s).
    // this version is especially problematic for stuff like choose any amount of creature within
    // range
    // this grows exponentially. I implemented a bare-bones version of this to have some
    // consistancy,
    // but I do not agree with it. This is the reason why I implemented only simple spell attacks
    // ---
    // for us to be able to change this quickly.
    List<StateProcess> list = new ArrayList<>();
    if (actor.spellcasting == null) {
      return list;
    }

    if (actor.isUnconscious()) return list;

    if (!actor.spellcasting().getSpells().contains(spell)) throw new IllegalArgumentException();

    if (actor.turnTracker.leveledSpells() > 0 && spell.level().value() > 0) return list;

    // leveled spells need an available slot; cantrips are always castable
    if (spell.level().value() > 0 && !actor.spellcasting().hasSlot(spell.level())) return list;

    boolean hasActivity =
        switch (spell.castingTime()) {
          case ACTION -> actor.turnTracker.hasAction();
          case BONUS_ACTION -> actor.turnTracker.hasBonusAction();
          default -> false;
        };
    if (!hasActivity) return list;

    if (!(spell.range() instanceof Ranged(int range)
        && spell.targeting() instanceof SingleCreature)) throw new UnsupportedOperationException();
    // todo ignore obstacles
    for (Creature target : getCreaturesWithinDistance(actor.position(), range)) {
      if (target == actor) continue;
      list.add(
          new StateProcess() {
            @Override
            public Creature getTarget() {
              return target;
            }

            @Override
            public Creature getSource() {
              return actor;
            }

            @Override
            public void run() {
              spell.effect().resolve(State.this, actor, List.of(target), spell.level());
              // consume the activity matching the casting time
              switch (spell.castingTime()) {
                case ACTION -> actor.turnTracker.makeAction();
                case BONUS_ACTION -> actor.turnTracker.makeBonusAction();
                default -> {}
              }
              actor.turnTracker.castSpell(spell.level().value()); // mark leveled-spell-this-turn
              actor.spellcasting().castSpell(spell.level()); // spend the slot (no-op for cantrips)
              if (target.isDead()) removeCreature(target);
            }

            @Override
            public String toString() {
              return spell.name() + " targeting " + target;
            }
          });
    }

    return list;
  }

  public void nextTurn() {
    if (initiative.getFirst() == null) return;
    initiative.advance();
    if (initiative.getFirst() instanceof Creature creature) {
      if (creature.isUnconscious()) {
        creature.deathSavingThrow();
        if (creature.isDead()) removeCreature(creature);
      }
    }
    initiative.getFirst().onTurnReset();
  }

  public void setOnRemoveCreature(Consumer<Creature> onRemoveCreature) {
    this.onRemoveCreature = onRemoveCreature;
  }

  public void setOnAddCreature(Consumer<Creature> onAddCreature) {
    this.onAddCreature = onAddCreature;
  }
}
