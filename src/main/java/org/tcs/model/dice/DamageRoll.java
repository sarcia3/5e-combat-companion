package org.tcs.model.dice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tcs.model.Damage;

/**
 * Class for describing complex rolls.
 *
 * <p>It's a utility which is meant to store things like 2d4 piercing + 1d8 bludgeoning
 */
public record DamageRoll(List<Component> components) {

  public record Component(int numberOfDice, int numberOfSides, int bonus, Damage.Type type) {}

  public DamageRoll(List<Component> components) {
    if (components == null) this.components = new ArrayList<>();
    else this.components = components;
  }

  public DamageRoll(Component component) {
    this(List.of(component));
  }

  /**
   * Creates a roll with bonus
   *
   * @param numberOfDice number of dice
   * @param numberOfSides number of sides on each dice
   * @param bonus bonus added to the roll
   * @param type damage type
   */
  public DamageRoll(int numberOfDice, int numberOfSides, int bonus, Damage.Type type) {
    this(new Component(numberOfDice, numberOfSides, bonus, type));
  }

  /** Creates a roll that is the concatenation of the rolls passed in the constructor */
  DamageRoll(Collection<? extends DamageRoll> rolls) {
    this(rolls.stream().flatMap(r -> r.components().stream()).toList());
  }

  /** Doubles the number of dice. */
  DamageRoll critical() {
    return new DamageRoll(
        components.stream()
            .map((r) -> new Component(2 * r.numberOfDice, r.numberOfSides, r.bonus, r.type))
            .toList());
  }

  public Damage resolve(DiceRoller diceRoller) {
    Damage result = new Damage();
    for (var component : components) {
      result.add(
          component.type,
          diceRoller.roll(
                  component.numberOfDice,
                  component.numberOfSides,
                  new DiceRoller.RollInformation(
                      "Attacker", "rolls for " + component.type + " damage"))
              + component.bonus);
    }
    return result;
  }

  // Warning: the following was LLM generated.

  // Matches one component: either "NdM" / "NdM+B" / just "B", followed by a damage-type word.
  private static final Pattern COMPONENT_PATTERN =
      Pattern.compile("^(?:(\\d+)d(\\d+)(?:\\+(\\d+))?|(\\d+))\\s+(\\w+)$");

  /**
   * Parses a damage expression like {@code "1d8+3 slashing + 2d6 fire"} into a {@link DamageRoll}.
   *
   * <p>Grammar: components are separated by " + " (whitespace on both sides). A component is either
   * {@code NdM}, {@code NdM+B}, or a bare flat number {@code B}, followed by a damage-type word
   * that must match one of {@link Damage.Type} (case-insensitive).
   *
   * @throws IllegalArgumentException if the input does not match the grammar or names an unknown
   *     damage type.
   */
  public static DamageRoll parse(String input) {
    String[] parts = input.trim().split("\\s+\\+\\s+");
    List<Component> parsed = new ArrayList<>();
    for (String part : parts) {
      Matcher matcher = COMPONENT_PATTERN.matcher(part.trim());
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Cannot parse component: '" + part + "'");
      }
      int numberOfDice;
      int numberOfSides;
      int bonus;
      if (matcher.group(1) != null) {
        numberOfDice = Integer.parseInt(matcher.group(1));
        numberOfSides = Integer.parseInt(matcher.group(2));
        bonus = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
      } else {
        numberOfDice = 0;
        numberOfSides = 0;
        bonus = Integer.parseInt(matcher.group(4));
      }
      Damage.Type type;
      try {
        type = Damage.Type.valueOf(matcher.group(5).toUpperCase());
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Unknown damage type: '" + matcher.group(5) + "'");
      }
      parsed.add(new Component(numberOfDice, numberOfSides, bonus, type));
    }
    return new DamageRoll(parsed);
  }
}
