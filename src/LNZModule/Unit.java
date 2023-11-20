package LNZModule;

import java.util.*;
import java.util.concurrent.*;
import processing.core.*;
import LNZApplet.*;
import Misc.Misc;

enum UnitAction {
  NONE, MOVING, TARGETING_FEATURE, TARGETING_UNIT, TARGETING_ITEM, ATTACKING,
  SHOOTING, AIMING, USING_ITEM, FEATURE_INTERACTION, FEATURE_INTERACTION_WITH_ITEM,
  HERO_INTERACTING_WITH_FEATURE, TARGETING_FEATURE_WITH_ITEM,
  HERO_INTERACTING_WITH_FEATURE_WITH_ITEM, MOVING_AND_USING_ITEM, CASTING,
  CAST_WHEN_IN_RANGE, TARGETING_TERRAIN, TERRAIN_INTERACTION,
  ;

  public static boolean walking(UnitAction action) {
    switch(action) {
      case MOVING:
      case TARGETING_FEATURE:
      case TARGETING_ITEM:
      case TARGETING_UNIT:
      case MOVING_AND_USING_ITEM:
      case TARGETING_TERRAIN:
      case CAST_WHEN_IN_RANGE:
        return true;
      default:
        return false;
    }
  }

  public static boolean aggressiveAction(UnitAction action) {
    switch(action) {
      case TARGETING_UNIT:
      case ATTACKING:
      case SHOOTING:
      case AIMING:
      case CASTING:
      case CAST_WHEN_IN_RANGE:
        return true;
      default:
        return false;
    }
  }
}

enum MoveModifier {
  NONE, SNEAK, RECOIL,
  AMPHIBIOUS_LEAP, ANURAN_APPETITE;
}

enum DamageType {
  PHYSICAL, MAGICAL, MIXED, TRUE;
}

enum GearSlot {
  ERROR("Error"), WEAPON("Weapon"), HEAD("Head"), CHEST("Chest"), LEGS("Legs"),
    FEET("Feet"), OFFHAND("Offhand"), BELT_LEFT("Belt (left)"), BELT_RIGHT(
    "Belt (right)"), HAND_THIRD("Third Hand"), HAND_FOURTH("Fourth Hand"),
    FEET_SECOND("Feet (second pair)"), FEET_THIRD("Feet (third pair)");

  private static final List<GearSlot> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  private String slot_name;
  private GearSlot(String slot_name) {
    this.slot_name = slot_name;
  }
  public String slot_name() {
    return this.slot_name;
  }
  public static String slot_name(GearSlot slot) {
    return slot.slot_name();
  }

  public static GearSlot gearSlot(String slot_name) {
    for (GearSlot slot : GearSlot.VALUES) {
      if (slot == GearSlot.ERROR) {
        continue;
      }
      if (slot.slot_name().equals(slot_name) || slot.toString().equals(slot_name)) {
        return slot;
      }
    }
    return GearSlot.ERROR;
  }
}

enum Alliance {
  NONE("None"), BEN("Ben"), ZOMBIE("Zombie");

  private static final List<Alliance> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  private String alliance_name;
  private Alliance(String alliance_name) {
    this.alliance_name = alliance_name;
  }
  public String alliance_name() {
    return this.alliance_name;
  }
  public static String alliance_name(Alliance alliance) {
    return alliance.alliance_name();
  }

  public static Alliance alliance(String alliance_name) {
    for (Alliance alliance : Alliance.VALUES) {
      if (alliance.alliance_name().equals(alliance_name)) {
        return alliance;
      }
    }
    return Alliance.NONE;
  }
}


class Unit extends MapObject {
  class PathFindingThread extends Thread {
    class CoordinateValues {
      private int source_height = 0;
      private boolean corner_square = false;
      private IntegerCoordinate source;
      private double distance = 0;
      CoordinateValues(int source_height, boolean corner_square, IntegerCoordinate source, double distance) {
        this.source_height = source_height;
        this.corner_square = corner_square;
        this.source = source;
        this.distance = distance;
      }
      CoordinateValues(CoordinateValues coordinate_values) {
        this.source_height = coordinate_values.source_height;
        this.corner_square = coordinate_values.corner_square;
        this.source = coordinate_values.source.copy();
        this.distance = coordinate_values.distance;
      }
    }

    private LNZ p;

    private Coordinate goal;
    private AbstractGameMap map = null;
    private Stack<Coordinate> move_stack = new Stack<Coordinate>();
    private boolean stop_thread = false;

    PathFindingThread(LNZ sketch, Coordinate goal, AbstractGameMap map) {
      super("PathFindingThread");
      this.setDaemon(true);
      this.p = sketch;
      this.goal = goal;
      this.map = map;
    }

    @Override
    public String toString() {
      return "!!";
    }

    Map<IntegerCoordinate, CoordinateValues> next_coordinates(Map<IntegerCoordinate, Double> last_coordinates, AbstractGameMap map) {
      Map<IntegerCoordinate, CoordinateValues> next_coordinates = new HashMap<IntegerCoordinate, CoordinateValues>();
      for (Map.Entry<IntegerCoordinate, Double> entry : last_coordinates.entrySet()) {
        int source_height = map.heightOfSquare(entry.getKey(), Unit.this.coordinate);
        for (IntegerCoordinate adjacent : entry.getKey().adjacentCoordinates()) {
          if (next_coordinates.containsKey(adjacent) && next_coordinates.get(
            adjacent).distance <= entry.getValue() + 1) {
            continue;
          }
          next_coordinates.put(adjacent, new CoordinateValues(source_height, false,
            entry.getKey(), entry.getValue() + 1));
        }
        for (IntegerCoordinate corner : entry.getKey().cornerCoordinates()) {
          if (next_coordinates.containsKey(corner) && next_coordinates.get(
            corner).distance <= entry.getValue() + LNZ.root_two) {
            continue;
          }
          next_coordinates.put(corner, new CoordinateValues(source_height, true,
            entry.getKey(), entry.getValue() + LNZ.root_two));
        }
      }
      return next_coordinates;
    }

    @Override
    public void run() {
      if (this.map == null) {
        return;
      }
      int effective_height = (int)Math.round(Unit.this.curr_height);
      int unit_max_height = effective_height + Unit.this.walkHeight();
      ArrayList<IntegerCoordinate> unit_squares_on = Unit.this.getSquaresOn();
      Coordinate unit_current = Unit.this.coordinate.copy();
      HashMap<IntegerCoordinate, CoordinateValues> coordinates = new HashMap<IntegerCoordinate, CoordinateValues>(); // value is distance
      IntegerCoordinate goal_square = new IntegerCoordinate(this.goal);
      if (!Unit.this.ai_controlled && !p.global.profile.options.player_pathfinding) {
        this.move_stack.push(this.goal);
        return;
      }
      IntegerCoordinate current_square = new IntegerCoordinate(Unit.this.coordinate);
      if (current_square.equals(goal_square)) {
        return;
      }
      coordinates.put(current_square, new CoordinateValues(effective_height, true, current_square, 0));
      Map<IntegerCoordinate, Double> last_coordinates = new HashMap<IntegerCoordinate, Double>();
      last_coordinates.put(current_square, 0.0);
      double last_distance = 0;
      maploop:
      while(true) {
        if (this.stop_thread) {
          return;
        }
        boolean break_map_loop = false;
        Map<IntegerCoordinate, CoordinateValues> current_coordinates = this.next_coordinates(last_coordinates, map);
        last_coordinates.clear();
        boolean all_dead_ends = true;
        ArrayList<IntegerCoordinate> current_coordinates_keys = new ArrayList<IntegerCoordinate>(current_coordinates.keySet());
        Collections.shuffle(current_coordinates_keys);
        for (IntegerCoordinate coordinate : current_coordinates_keys) {
          if (!map.containsMapSquare(coordinate)) {
            continue;
          }
          int max_height = current_coordinates.get(coordinate).source_height + Unit.this.walkHeight();
          int coordinate_height = map.heightOfSquare(coordinate, unit_current);
          if (coordinate_height > max_height) {
            continue;
          }
          if (current_coordinates.get(coordinate).corner_square) {
            coordinate_height = map.heightOfSquare(new IntegerCoordinate(
              current_coordinates.get(coordinate).source.x, coordinate.y), unit_current);
            if (coordinate_height > max_height) {
              continue;
            }
            coordinate_height = map.heightOfSquare(new IntegerCoordinate(
              coordinate.x, current_coordinates.get(coordinate).source.y), unit_current);
            if (coordinate_height > max_height) {
              continue;
            }
          }
          if (coordinates.containsKey(coordinate) && coordinates.get(
            coordinate).distance <= current_coordinates.get(coordinate).distance) {
            continue;
          }
          coordinates.put(coordinate, new CoordinateValues(current_coordinates.get(coordinate)));
          if (coordinate.equals(goal_square)) {
            last_distance = current_coordinates.get(coordinate).distance + 1;
            break_map_loop = true;
          }
          last_coordinates.put(coordinate, current_coordinates.get(coordinate).distance);
          all_dead_ends = false;
        }
        if (all_dead_ends) {
          return;
        }
        if (break_map_loop) {
          break maploop;
        }
      }
      boolean x_changed = false;
      boolean y_changed = false;
      boolean x_changed_last_turn = false;
      boolean y_changed_last_turn = false;
      boolean x_not_changed = false;
      boolean y_not_changed = false;
      boolean push_next_goal = false;
      boolean check_next_goal = false;
      int check_next_goal_x = 0;
      int check_next_goal_y = 0;
      if (!coordinates.containsKey(goal_square)) {
        p.global.errorMessage("ERROR: Coordinates missing original goal.");
        return;
      }
      pathloop:
      while(true) {
        if (this.stop_thread) {
          return;
        }
        x_not_changed = false;
        y_not_changed = false;
        if (!x_changed) {
          x_not_changed = true;
        }
        if (!y_changed) {
          y_not_changed = true;
        }
        IntegerCoordinate next_goal = null;
        List<IntegerCoordinate> adjacents;
        if (coordinates.get(goal_square).corner_square) {
          adjacents = Arrays.asList(goal_square.adjacentAndCornerCoordinates());
        }
        else {
          adjacents = Arrays.asList(goal_square.adjacentCoordinates());
        }
        Collections.shuffle(adjacents); // to allow random choosing of equivalent paths
        for (IntegerCoordinate adjacent : adjacents) {
          if (!coordinates.containsKey(adjacent)) {
            continue;
          }
          if (coordinates.get(adjacent).distance >= last_distance) {
            continue;
          }
          next_goal = adjacent;
          last_distance = coordinates.get(adjacent).distance;
        }
        if (next_goal == null) {
          p.global.errorMessage("ERROR: Found path but can't map it.");
          return;
        }
        if (check_next_goal) {
          check_next_goal = false;
          int max_height = coordinates.get(next_goal).source_height + Unit.this.walkHeight();
          if (next_goal.x != goal_square.x) {
            if (goal_square.y > check_next_goal_y) {
              int coordinate_height = map.heightOfSquare(next_goal.x, next_goal.y - 1, unit_current);
              if (coordinate_height > max_height) {
                y_changed = true;
              }
            }
            else {
              int coordinate_height = map.heightOfSquare(next_goal.x, next_goal.y + 1, unit_current);
              if (coordinate_height > max_height) {
                y_changed = true;
              }
            }
          }
          if (next_goal.y != goal_square.y) {
            if (goal_square.x > check_next_goal_x) {
              int coordinate_height = map.heightOfSquare(next_goal.x - 1, next_goal.y, unit_current);
              if (coordinate_height > max_height) {
                x_changed = true;
              }
            }
            else {
              int coordinate_height = map.heightOfSquare(next_goal.x + 1, next_goal.y, unit_current);
              if (coordinate_height > max_height) {
                x_changed = true;
              }
            }
          }
        }
        if (coordinates.get(goal_square).corner_square) {
          if (!coordinates.get(next_goal).corner_square) {
            check_next_goal = true;
            check_next_goal_x = goal_square.x;
            check_next_goal_y = goal_square.y;
          }
        }
        else {
          boolean keep_x_changed = false;
          boolean keep_y_changed = false;
          if (next_goal.x != goal_square.x) {
            x_changed = true;
            if (coordinates.get(next_goal).corner_square) {
              int max_height = coordinates.get(goal_square).source_height + Unit.this.walkHeight();
              if (next_goal.y > coordinates.get(next_goal).source.y) {
                int coordinate_height = map.heightOfSquare(goal_square.x, goal_square.y - 1, unit_current);
                if (coordinate_height > max_height) {
                  push_next_goal = true;
                }
              }
              else {
                int coordinate_height = map.heightOfSquare(goal_square.x, goal_square.y + 1, unit_current);
                if (coordinate_height > max_height) {
                  push_next_goal = true;
                }
              }
            }
            else if (y_changed_last_turn) {
              keep_x_changed = true; // zig zag
            }
          }
          if (next_goal.y != goal_square.y) {
            y_changed = true;
            if (coordinates.get(next_goal).corner_square) {
              int max_height = coordinates.get(goal_square).source_height + Unit.this.walkHeight();
              if (next_goal.x > coordinates.get(next_goal).source.x) {
                int coordinate_height = map.heightOfSquare(goal_square.x - 1, goal_square.y, unit_current);
                if (coordinate_height > max_height) {
                  push_next_goal = true;
                }
              }
              else {
                int coordinate_height = map.heightOfSquare(goal_square.x + 1, goal_square.y, unit_current);
                if (coordinate_height > max_height) {
                  push_next_goal = true;
                }
              }
            }
            else if (x_changed_last_turn) {
              keep_y_changed = true; // zig zag
            }
          }
          if (x_changed && x_not_changed) {
            x_changed_last_turn = true;
          }
          else {
            x_changed_last_turn = false;
          }
          if (y_changed && y_not_changed) {
            y_changed_last_turn = true;
          }
          else {
            y_changed_last_turn = false;
          }
          if (x_changed && y_changed) {
            if (!keep_x_changed) {
              x_changed = false;
            }
            if (!keep_y_changed) {
              y_changed = false;
            }
            this.move_stack.push(new Coordinate(goal_square.x + 0.5, goal_square.y + 0.5));
          }
        }
        if (next_goal.equals(current_square)) {
          break pathloop;
        }
        goal_square = next_goal.copy();
        if (push_next_goal) {
          push_next_goal = false;
          this.move_stack.push(new Coordinate(goal_square.x + 0.5, goal_square.y + 0.5));
        }
      }
      Coordinate initial_goal = this.goal.copy();
      if (!this.move_stack.empty()) {
        initial_goal = this.move_stack.peek().copy();
      }
      Coordinate dif = initial_goal.copy();
      dif.subtract(unit_current);
      double dif_distance = dif.distance();
      if (dif_distance < 1 || Unit.this.size == 0) {
        return; // what is this checking for?
      }
      dif.divide(dif_distance);
      HashSet<IntegerCoordinate> new_squares_on = new HashSet<IntegerCoordinate>();
      int number_sets_to_check = 2;
      for (int n = 0; n < number_sets_to_check; n++) {
        dif.multiply(Unit.this.size * 0.4);
        unit_current.add(dif);
        for (int i = (int)Math.floor(unit_current.x - Unit.this.size); i < (int)Math.ceil(unit_current.x + Unit.this.size); i++) {
          for (int j = (int)Math.floor(unit_current.y - Unit.this.size); j < (int)Math.ceil(unit_current.y + Unit.this.size); j++) {
            IntegerCoordinate coordinate = new IntegerCoordinate(i, j);
            if (unit_squares_on.contains(coordinate)) {
              continue;
            }
            new_squares_on.add(coordinate);
          }
        }
      }
      for (IntegerCoordinate coordinate : new_squares_on) {
        int coordinate_height = map.heightOfSquare(coordinate.x, coordinate.y, unit_current);
        if (coordinate_height > unit_max_height) {
          this.move_stack.push(new Coordinate(current_square.x + 0.5, current_square.y + 0.5));
          break;
        }
      }
    }
  }


  protected double size = LNZ.unit_defaultSize; // radius
  protected int sizeZ = LNZ.unit_defaultHeight;

  protected int level = 0;
  protected Alliance alliance = Alliance.NONE;
  protected Element element = Element.GRAY;

  protected Coordinate facing = new Coordinate(1, 0);
  protected double facingA = 0; // angle in radians

  protected HashMap<GearSlot, Item> gear = new HashMap<GearSlot, Item>();
  protected ArrayList<Ability> abilities = new ArrayList<Ability>();
  protected ConcurrentHashMap<StatusEffectCode, StatusEffect> statuses = new ConcurrentHashMap<StatusEffectCode, StatusEffect>();

  protected double base_health = 1;
  protected double base_attack = 0;
  protected double base_magic = 0;
  protected double base_defense = 0;
  protected double base_resistance = 0;
  protected double base_piercing = 0; // percentage from 0 - 1
  protected double base_penetration = 0; // percentage from 0 - 1
  protected double base_attackRange = LNZ.unit_defaultBaseAttackRange;
  protected double base_attackCooldown = LNZ.unit_defaultBaseAttackCooldown;
  protected double base_attackTime = LNZ.unit_defaultBaseAttackTime;
  protected double base_sight = LNZ.unit_defaultSight;
  protected double base_speed = 0;
  protected double base_tenacity = 0; // percentage from 0 - 1
  protected int base_agility = 1;
  protected double base_lifesteal = 0; // percentage
  protected boolean save_base_stats = false; // toggle on if base stats manually changed

  protected double curr_health = 1;
  protected double timer_attackCooldown = 0;
  protected double timer_actionTime = 0;
  protected double timer_last_damage = 0;

  protected UnitAction curr_action = UnitAction.NONE;
  protected int curr_action_id = 0;
  protected boolean curr_action_unhaltable = false;
  protected boolean curr_action_unstoppable = false;
  protected Coordinate curr_action_coordinate = new Coordinate(0, 0);
  protected Stack<Coordinate> move_stack = new Stack<Coordinate>();
  protected boolean using_current_move_stack = false;
  protected boolean waiting_for_pathfinding_thread = false;
  protected PathFindingThread pathfinding_thread = null;
  protected int timer_update_pathfinding = LNZ.unit_update_pathfinding_timer;

  protected int map_key = -10;
  protected MapObject object_targeting = null;
  protected MapObject last_damage_from = null;
  protected DamageSource last_damage_source = new DamageSource(0);
  protected double last_damage_amount = 0;
  protected double last_move_distance = 0;
  protected int buffer_cast = -1;
  protected boolean last_move_collision = false;
  protected boolean last_move_any_collision = false;
  protected double footgear_durability_distance = LNZ.unit_footgearDurabilityDistance;

  protected ArrayList<IntegerCoordinate> curr_squares_on = new ArrayList<IntegerCoordinate>(); // squares unit is on
  protected HashSet<IntegerCoordinate> curr_squares_sight = new HashSet<IntegerCoordinate>(); // squares unit can see
  protected double unit_height = 0; // height of unit you are standing on
  protected int floor_height = 0; // height of ground
  protected boolean falling = false;
  protected boolean jumping = false;
  protected double jump_amount = 0;
  protected double fall_amount = 0;
  protected int timer_resolve_floor_height = LNZ.unit_timer_resolve_floor_height_cooldown;

  protected boolean ai_controlled = true;
  protected int timer_ai_action1 = 0;
  protected int timer_ai_action2 = 0;
  protected int timer_ai_action3 = 0;
  protected boolean ai_toggle = false;

  // graphics
  protected double random_number = Math.random() * 100;
  protected int timer_talk = LNZ.unit_timer_talk + Misc.randomInt(LNZ.unit_timer_talk);
  protected int timer_target_sound = 0;
  protected int timer_walk = LNZ.unit_timer_walk;

  Unit(LNZ sketch, int ID) {
    this(sketch, ID, new Coordinate(0, 0));
  }
  Unit(LNZ sketch, int ID, Coordinate coordinate) {
    super(sketch, ID);
    this.setLocation(coordinate);
    this.setUnitID(ID);
  }

  static String unitName(int ID) {
    return (new Unit(null, ID)).displayName();
  }

  void setUnitID(int ID) {
    this.ID = ID;
    switch(ID) {
      // Other
      case 1001:
        this.setStrings("Test Dummy", "", "");
        this.addStatusEffect(StatusEffectCode.UNKILLABLE);
        break;
      case 1002:
        this.setStrings("Chicken", "Gaia", "");
        this.baseStats(2.5, 0, 0, 0, 1.4);
        this.base_agility = 2;
        this.base_sight = 4;
        this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(LNZ.ai_chickenTimer1);
        this.timer_ai_action2 = LNZ.ai_chickenTimer2 + Misc.randomInt(LNZ.ai_chickenTimer2);
        this.setLevel(1);
        this.size = LNZ.unit_defaultSize;
        this.sizeZ = 2;
        break;
      case 1003:
        this.setStrings("Chick", "Gaia", "");
        this.baseStats(1.5, 0, 0, 0, 1.1);
        this.base_sight = 3.5;
        this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(LNZ.ai_chickenTimer1);
        this.timer_ai_action2 = 2 * LNZ.ai_chickenTimer2 + 2 * Misc.randomInt(LNZ.ai_chickenTimer2);
        this.setLevel(0);
        this.size = 0.8 * LNZ.unit_defaultSize;
        this.sizeZ = 1;
        break;
      case 1004:
        this.setStrings("Rankin", "Human", "");
        this.baseStats(8, 3, 2, 0, 3);
        this.setLevel(5);
        this.size = 0.45;
        this.sizeZ = 6;
        break;
      case 1005:
        this.setStrings("Rooster", "Gaia", "");
        this.baseStats(3.1, 1.2, 0, 0, 1.4);
        this.base_agility = 2;
        this.base_sight = 4;
        this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(LNZ.ai_chickenTimer1);
        this.timer_ai_action2 = LNZ.ai_chickenTimer2 + Misc.randomInt(LNZ.ai_chickenTimer2);
        this.setLevel(2);
        this.size = LNZ.unit_defaultSize;
        this.sizeZ = 2;
        break;
      case 1006:
        this.setStrings("Father Dom", "Human", "");
        this.baseStats(3.5, 2, 0, 0, 2);
        this.magicStats(2, 4, 0.05);
        this.alliance = Alliance.BEN;
        this.setLevel(3);
        this.sizeZ = 4;
        this.gearSlots("Weapon");
        this.pickup(new Item(p, 2928));
        break;
      case 1007:
        this.setStrings("Michael Schmiesing", "Human", "The fat man himself!");
        this.baseStats(15, 4, 3, 0, 1.5);
        this.alliance = Alliance.BEN;
        this.setLevel(7);
        this.gearSlots("Weapon");
        this.size = 0.55;
        break;
      case 1008:
        this.setStrings("Molly Schmiesing", "Human", "");
        this.baseStats(9, 4, 1, 0, 1.7);
        this.alliance = Alliance.BEN;
        this.setLevel(6);
        break;
      case 1009:
        this.setStrings("Frog", "Gaia", "");
        this.baseStats(3.2, 0, 0, 0, 2.6);
        this.base_sight = 2.5;
        this.setLevel(2);
        this.base_agility = 3;
        this.size = 0.28;
        break;
      case 1010:
        this.setStrings("Quail", "Gaia", "");
        this.baseStats(4.5, 2, 0, 0, 1.3);
        this.base_sight = 4;
        this.setLevel(3);
        this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(LNZ.ai_chickenTimer1);
        this.timer_ai_action2 = 2 * LNZ.ai_chickenTimer2 + 2 * Misc.randomInt(LNZ.ai_chickenTimer2);
        break;
      case 1011:
        this.setStrings("Squirrel", "Gaia", "");
        this.baseStats(4, 2, 0.4, 0.1, 2);
        this.setLevel(4);
        this.base_agility = 5;
        this.size = 0.31;
        this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(LNZ.ai_chickenTimer1);
        break;
      case 1012:
        this.setStrings("Fawn", "Gaia", "");
        this.baseStats(8, 4, 1, 0, 2.1);
        this.setLevel(5);
        this.base_agility = 2;
        this.size = 0.5;
        this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(LNZ.ai_chickenTimer1);
        this.timer_ai_action2 = 7 * LNZ.ai_chickenTimer2 + 7 * Misc.randomInt(LNZ.ai_chickenTimer2);
        break;
      case 1013:
        this.setStrings("Raccoon", "Gaia", "");
        this.baseStats(12, 6.5, 1.2, 0.2, 1.4);
        this.setLevel(6);
        this.base_agility = 3;
        this.gearSlots("Weapon");
        this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(LNZ.ai_chickenTimer1);
        break;
      case 1014:
        this.setStrings("Doe", "Gaia", "");
        this.baseStats(15, 8, 2, 0.05, 2.4);
        this.setLevel(7);
        this.base_agility = 2;
        this.size = 0.65;
        this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(LNZ.ai_chickenTimer1);
        break;
      case 1015:
        this.setStrings("Buck", "Gaia", "");
        this.baseStats(18, 10, 2, 0.35, 2.3);
        this.setLevel(8);
        this.base_agility = 2;
        this.size = 0.7;
        this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(LNZ.ai_chickenTimer1);
        break;

      // Heroes
      case 1101:
        this.setStrings(HeroCode.displayName(HeroCode.BEN), "Hero", "");
        this.baseStats(4, 1, 0, 0, 1.7);
        this.setLevel(0);
        this.base_agility = 1;
        this.gearSlots("Weapon", "Head", "Chest", "Legs", "Feet");
        this.alliance = Alliance.BEN;
        this.element = Element.GRAY;
        break;
      case 1102:
        this.setStrings(HeroCode.displayName(HeroCode.DAN), "Hero", "");
        this.baseStats(4, 1, 0, 0, 1.8);
        this.setLevel(0);
        this.base_agility = 2;
        this.gearSlots("Weapon", "Head", "Chest", "Legs", "Feet");
        this.alliance = Alliance.BEN;
        this.element = Element.BROWN;
        break;
      case 1103:
        this.setStrings(HeroCode.displayName(HeroCode.JF), "Hero", "");
        this.baseStats(4, 1, 0, 0, 2);
        this.setLevel(0);
        this.gearSlots("Weapon", "Head", "Chest", "Legs", "Feet");
        this.alliance = Alliance.BEN;
        this.element = Element.CYAN;
        break;
      case 1104:
        this.setStrings(HeroCode.displayName(HeroCode.SPINNY), "Hero", "");
        this.baseStats(4, 1, 0, 0, 2);
        this.setLevel(0);
        this.gearSlots("Weapon", "Head", "Chest", "Legs", "Feet");
        this.alliance = Alliance.BEN;
        this.element = Element.RED;
        break;
      case 1105:
        this.setStrings(HeroCode.displayName(HeroCode.MATTUS), "Hero", "");
        this.baseStats(4, 1, 0, 0, 2);
        this.setLevel(0);
        this.gearSlots("Weapon", "Head", "Chest", "Legs", "Feet");
        this.alliance = Alliance.BEN;
        this.element = Element.PURPLE;
        break;
      case 1106:
        this.setStrings(HeroCode.displayName(HeroCode.PATRICK), "Hero", "");
        this.baseStats(4, 1, 0, 0, 2);
        this.setLevel(0);
        this.gearSlots("Weapon", "Head", "Chest", "Legs", "Feet");
        this.alliance = Alliance.BEN;
        break;
      case 1131:
        this.setStrings("Michael Fischer", "Hero", "");
        this.baseStats(4, 1, 0, 0, 2);
        this.setLevel(0);
        this.gearSlots("Weapon", "Head", "Chest", "Legs", "Feet");
        this.alliance = Alliance.BEN;
        break;

      // Zombies
      case 1201:
        this.setStrings("Broken Sick Zombie", "Zombie", "");
        this.baseStats(1, 1, 0, 0, 0.3);
        this.setLevel(1);
        this.alliance = Alliance.ZOMBIE;
        this.addStatusEffect(StatusEffectCode.SICK);
        break;
      case 1202:
        this.setStrings("Broken Zombie", "Zombie", "");
        this.baseStats(2.2, 1.3, 0, 0, 0.3);
        this.setLevel(2);
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1203:
        this.setStrings("Sick Zombie", "Zombie", "");
        this.baseStats(3.5, 2, 0, 0, 0.4);
        this.setLevel(3);
        this.gearSlots("Weapon");
        this.alliance = Alliance.ZOMBIE;
        this.addStatusEffect(StatusEffectCode.SICK);
        break;
      case 1204:
        this.setStrings("Lazy Hungry Zombie", "Zombie", "");
        this.baseStats(5, 3, 0.3, 0, 0.5);
        this.setLevel(4);
        this.alliance = Alliance.ZOMBIE;
        this.addStatusEffect(StatusEffectCode.HUNGRY);
        break;
      case 1205:
        this.setStrings("Hungry Zombie", "Zombie", "");
        this.baseStats(7, 3.2, 0.3, 0, 0.8);
        this.setLevel(5);
        this.alliance = Alliance.ZOMBIE;
        this.addStatusEffect(StatusEffectCode.HUNGRY);
        break;
      case 1206:
        this.setStrings("Lazy Zombie", "Zombie", "");
        this.baseStats(9, 3.5, 0.3, 0, 0.6);
        this.setLevel(6);
        this.gearSlots("Weapon");
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1207:
        this.setStrings("Confused Franny Zombie", "Zombie", "");
        this.baseStats(11, 4, 0.6, 0, 0.9);
        this.setLevel(7);
        this.gearSlots("Weapon");
        this.alliance = Alliance.ZOMBIE;
        this.addStatusEffect(StatusEffectCode.CONFUSED);
        break;
      case 1208:
        this.setStrings("Confused Zombie", "Zombie", "");
        this.baseStats(14, 4.3, 0.6, 0, 1);
        this.setLevel(8);
        this.gearSlots("Weapon");
        this.alliance = Alliance.ZOMBIE;
        this.addStatusEffect(StatusEffectCode.CONFUSED);
        break;
      case 1209:
        this.setStrings("Franny Zombie", "Zombie", "");
        this.baseStats(17, 4.7, 0.6, 0, 1.1);
        this.setLevel(9);
        this.gearSlots("Weapon");
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1210:
        this.setStrings("Intellectual Zombie", "Zombie", "");
        this.baseStats(20, 5, 1, 0, 1.2);
        this.setLevel(10);
        this.gearSlots("Weapon");
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1211:
        this.setStrings("Intellectual Franny Zombie", "Zombie", "");
        this.baseStats(24, 6, 1.1, 0, 1.2);
        this.setLevel(11);
        this.gearSlots("Weapon");
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1212:
        this.setStrings("Zombie Friar", "Zombie", "");
        this.baseStats(30, 0, 1, 1.3, 1.1);
        this.magicStats(12, 3, 0.05);
        this.setLevel(12);
        this.base_attackRange = 2.5;
        this.base_attackCooldown = 2600;
        this.base_attackTime = 800;
        this.gearSlots("Weapon");
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1213:
        this.setStrings("Zombie Professor", "Zombie", "");
        this.baseStats(33, 7.5, 1.3, 0.15, 1.2);
        this.setLevel(13);
        this.gearSlots("Weapon");
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1214:
        this.setStrings("Zombie Priest", "Zombie", "");
        this.baseStats(35, 0, 1.4, 0, 1.2);
        this.magicStats(16, 4, 0.1);
        this.setLevel(14);
        this.base_attackRange = 3;
        this.base_attackCooldown = 2200;
        this.base_attackTime = 600;
        this.gearSlots("Weapon");
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1215:
        this.setStrings("Zombie Jeffy", "Zombie", "");
        this.baseStats(45, 9, 1.6, 0, 1.1);
        this.setLevel(15);
        this.gearSlots("Weapon");
        Item jeffy_gun = new Item(p, 2312);
        jeffy_gun.ammo = 4 + Misc.randomInt(1, 4);
        this.pickup(jeffy_gun);
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1291:
        this.setStrings("Zombie", "Zombie", "");
        this.baseStats(3, 1, 0, 0, 0.8);
        this.gearSlots("Weapon");
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1292:
        this.setStrings("Running Zombie", "Zombie", "");
        this.baseStats(3, 1, 0, 0, 1);
        this.gearSlots("Weapon");
        this.addStatusEffect(StatusEffectCode.RUNNING);
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1293:
        this.setStrings("Armored Zombie", "Zombie", "");
        this.baseStats(3, 1, 2, 0, 0.8);
        this.gearSlots("Weapon");
        this.alliance = Alliance.ZOMBIE;
        break;

      // Named Zombies
      case 1301:
        this.setStrings("Duggy", "Zombie", "");
        this.baseStats(6, 2.5, 0.2, 0, 1);
        this.setLevel(1);
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1302:
        this.setStrings("Jacob Sanchez", "Zombie", "");
        this.baseStats(8, 5, 0, 0, 1.5);
        this.setLevel(2);
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1303:
        this.setStrings("Mike Olenchuk", "Zombie", "");
        this.baseStats(15, 4, 1, 0, 0.9);
        this.setLevel(3);
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1304:
        this.setStrings("Grady Stuckman", "Zombie", "");
        this.baseStats(12, 5.5, 0.4, 0.05, 1.7);
        this.setLevel(4);
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1305:
        this.setStrings("Ethan Pitney", "Zombie", "");
        this.baseStats(15, 7, 0.4, 0, 1.3);
        this.setLevel(5);
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1306:
        this.setStrings("James Sarlo", "Zombie", "");
        this.baseStats(20, 8, 0.4, 0.12, 1.4);
        this.setLevel(6);
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1307:
        this.setStrings("Matt Hair", "Zombie", "");
        this.baseStats(25, 6.5, 2.2, 0.03, 1.1);
        this.setLevel(7);
        this.alliance = Alliance.ZOMBIE;
        this.gearSlots("Weapon");
        break;
      case 1308:
        this.setStrings("Nick Belt", "Zombie", "");
        this.baseStats(22, 10, 0.8, 0.15, 1.4);
        this.setLevel(8);
        this.alliance = Alliance.ZOMBIE;
        this.base_attackRange = 1.2 * LNZ.unit_defaultBaseAttackRange;
        break;
      case 1309:
        this.setStrings("Alex Spieldenner", "Zombie", "");
        this.baseStats(18, 10, 0.9, 0.25, 1.6);
        this.setLevel(9);
        this.alliance = Alliance.ZOMBIE;
        this.base_attackCooldown = 0.8 * LNZ.unit_defaultBaseAttackCooldown;
        this.base_attackTime = 0.8 * LNZ.unit_defaultBaseAttackTime;
        break;
      case 1310:
        this.setStrings("Kyle Aubert", "Zombie", "");
        this.baseStats(28, 13, 1, 0.1, 1.1);
        this.setLevel(10);
        this.base_lifesteal = 0.05;
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1351:
        this.setStrings("Cathy Heck", "Boss Zombie", "");
        this.baseStats(75, 7.5, 1.6, 0.1, 0.6);
        this.magicStats(10, 1.2, 0.05);
        this.base_lifesteal = 0.08;
        this.abilities.add(new Ability(p, 1001));
        this.abilities.add(new Ability(p, 1002));
        this.abilities.add(new Ability(p, 1003));
        this.setLevel(11);
        this.alliance = Alliance.ZOMBIE;
        this.timer_ai_action1 = 3000 + Misc.randomInt(3000);
        this.timer_ai_action2 = 9000 + Misc.randomInt(9000);
        this.timer_ai_action3 = 15000 + Misc.randomInt(15000);
        break;
      case 1352:
        this.setStrings("Matt Schaefer", "Boss Zombie", "");
        this.baseStats(50, 7.5, 1.6, 0.1, 0.6);
        this.magicStats(10, 1.2, 0.05);
        this.setLevel(11);
        this.alliance = Alliance.ZOMBIE;
        break;
      case 1353:
        this.setStrings("Ben Kohring", "Boss Zombie", "");
        this.baseStats(45, 7.5, 1.6, 0.1, 0.6);
        this.magicStats(10, 1.2, 0.05);
        this.setLevel(8);
        this.abilities.add(new Ability(p, 1021));
        this.abilities.add(new Ability(p, 1022));
        this.timer_ai_action1 = 4000 + Misc.randomInt(4000);
        this.timer_ai_action2 = this.timer_ai_action1 + 4000;
        this.alliance = Alliance.ZOMBIE;
        break;

      default:
        p.global.errorMessage("ERROR: Unit ID " + ID + " not found.");
        break;
    }
  }

  String displayName() {
    return this.display_name;
  }
  @Override
  String displayNameEditor() {
    return this.displayName() + " (" + this.map_key + ")";
  }
  String type() {
    return this.type;
  }
  String description() {
    return this.description;
  }
  String selectedObjectTextboxText() {
    String text = "-- " + this.type();
    if (p.global.profile.upgraded(PlayerTreeCode.ENEMY_INSIGHTI)) {
      text += " (level " + this.level + ") --";
    }
    else {
      text += " --";
    }
    if (this.statuses.size() > 0 && p.global.profile.upgraded(PlayerTreeCode.ENEMY_INSIGHTII)) {
      text += "\n";
    }
    if (p.global.profile.upgraded(PlayerTreeCode.HEALTHBARS)) {
      text += "\n\nHealth: " + Math.round(Math.ceil(this.curr_health)) +
        "/" + Math.round(Math.ceil(this.health()));
    }
    if (p.global.profile.upgraded(PlayerTreeCode.ENEMY_INSIGHTI)) {
      double attack = this.attack();
      if (attack > 0) {
        text += "\nAttack: " + Math.round(attack * 10.0) / 10.0;
      }
    }
    if (p.global.profile.upgraded(PlayerTreeCode.ENEMY_INSIGHTII)) {
      double magic = this.magic();
      if (magic > 0) {
        text += "\nMagic: " + Math.round(magic * 10.0) / 10.0;
      }
    }
    if (p.global.profile.upgraded(PlayerTreeCode.ENEMY_INSIGHTI)) {
      double defense = this.defense();
      if (defense > 0) {
        text += "\nDefense: " + Math.round(defense * 10.0) / 10.0;
      }
    }
    if (p.global.profile.upgraded(PlayerTreeCode.ENEMY_INSIGHTII)) {
      double resistance = this.resistance();
      if (resistance > 0) {
        text += "\nResistance: " + Math.round(resistance * 10.0) / 10.0;
      }
    }
    if (p.global.profile.upgraded(PlayerTreeCode.ENEMY_INSIGHTI)) {
      double piercing = this.piercing();
      if (piercing > 0) {
        text += "\nPiercing: " + Math.round(piercing * 100) + "%";
      }
    }
    if (p.global.profile.upgraded(PlayerTreeCode.ENEMY_INSIGHTII)) {
      double penetration = this.penetration();
      if (penetration > 0) {
        text += "\nPenetration: " + Math.round(penetration * 100) + "%";
      }
    }
    if (p.global.profile.upgraded(PlayerTreeCode.ENEMY_INSIGHTI)) {
      text += "\nSpeed: " + Math.round(this.speed() * 10.0) / 10.0;
    }
    if (p.global.profile.upgraded(PlayerTreeCode.ENEMY_INSIGHTII)) {
      double tenacity = this.tenacity();
      if (tenacity > 0) {
        text += "\nTenacity: " + Math.round(tenacity * 100) + "%";
      }
      int agility = this.agility();
      if (agility > 0) {
        text += "\nAgility: " + agility;
      }
      double lifesteal = this.lifesteal();
      if (lifesteal > 0) {
        text += "\nLifesteal: " + Math.round(lifesteal * 100) + "%";
      }
    }
    return text + "\n\n" + this.description();
  }

  void baseStats(double health, double attack, double defense, double piercing, double speed) {
    this.base_health = health;
    this.curr_health = health;
    this.base_attack = attack;
    this.base_defense = defense;
    this.base_piercing = piercing;
    this.base_speed = speed;
  }

  void magicStats(double magic, double resistance, double penetration) {
    this.base_magic = magic;
    this.base_resistance = resistance;
    this.base_penetration = penetration;
  }

  void gearSlots(String ... strings) {
    for (String string : strings) {
      GearSlot slot = GearSlot.gearSlot(string);
      if (slot == GearSlot.ERROR) {
        p.global.errorMessage("ERROR: Invalid gear slot name: " + string + ".");
        continue;
      }
      this.gear.put(slot, null);
    }
  }

  void setLocation(double x, double y) {
    this.coordinate = new Coordinate(x, y);
  }
  void setLocation(Coordinate coordinate) {
    this.coordinate = coordinate.copy();
  }

  void teleport(AbstractGameMap map, double x, double y) {
    this.setLocation(x, y);
    this.curr_squares_on = this.getSquaresOn();
    this.resolveFloorHeight(map);
    this.curr_height = this.floor_height;
  }

  double xi() {
    return this.coordinate.x - this.size;
  }
  double yi() {
    return this.coordinate.y - this.size;
  }
  double xf() {
    return this.coordinate.x + this.size;
  }
  double yf() {
    return this.coordinate.y + this.size;
  }
  double xCenter() {
    return this.coordinate.x;
  }
  double yCenter() {
    return this.coordinate.y;
  }
  double width() {
    return 2 * this.size;
  }
  double height() {
    return 2 * this.size;
  }
  double xRadius() {
    return this.size;
  }
  double yRadius() {
    return this.size;
  }
  double zi() {
    return this.curr_height;
  }
  double zf() {
    return this.curr_height + this.sizeZ;
  }
  double zHalf() {
    return this.curr_height + 0.5 * this.sizeZ;
  }

  PImage getImage() {
    String path = "units/";
    switch(this.ID) {
      case 1001:
        path += "default.png";
        break;
      case 1002:
        path += "chicken.png";
        break;
      case 1003:
        path += "chick.png";
        break;
      case 1004:
        path += "john_rankin.png";
        break;
      case 1005:
        path += "rooster.png";
        break;
      case 1006:
        path += "father_dom.png";
        break;
      case 1007:
        path += "mike_schmiesing.png";
        break;
      case 1008:
        path += "molly_schmiesing.png";
        break;
      case 1009:
        path += "frog.png";
        break;
      case 1010:
        path += "quail.png";
        break;
      case 1011:
        path += "squirrel.png";
        break;
      case 1012:
        path += "deer_fawn.png";
        break;
      case 1013:
        path += "raccoon.png";
        break;
      case 1014:
        path += "deer_doe.png";
        break;
      case 1015:
        path += "deer_buck.png";
        break;
      case 1101:
        if (p.global.profile.ben_has_eyes) {
          path += "ben.png";
        }
        else {
          path += "ben_noeyes.png";
        }
        break;
      case 1102:
        path += "dan.png";
        break;
      case 1103:
        path += "jf.png";
        break;
      case 1104:
        path += "spinny.png";
        break;
      case 1105:
        path += "mattus.png";
        break;
      case 1106:
        path += "patrick.png";
        break;
      case 1131:
        path += "michael_fischer.png";
        break;
      case 1201:
      case 1202:
      case 1203:
      case 1204:
      case 1205:
      case 1206:
      case 1207:
      case 1208:
      case 1209:
      case 1210:
      case 1211:
        path += "zombie1.png";
        break;
      case 1212:
        path += "zombie_friar.png";
        break;
      case 1213:
        path += "zombie_professor.png";
        break;
      case 1214:
        path += "zombie_priest.png";
        break;
      case 1215:
        path += "zombie_jeffy.png";
        break;
      case 1291:
        path += "zombie1.png";
        break;
      case 1292:
        path += "zombie2.png";
        break;
      case 1293:
        path += "zombie3.png";
        break;
      case 1301:
        path += "duggy_zombie.png";
        break;
      case 1302:
        path += "jacob_sanchez_zombie.png";
        break;
      case 1303:
        path += "mike_olenchuk_zombie.png";
        break;
      case 1304:
        path += "grady_stuckman_zombie.png";
        break;
      case 1305:
        path += "ethan_pitney_zombie.png";
        break;
      case 1306:
        path += "james_sarlo_zombie.png";
        break;
      case 1307:
        path += "matt_hair_zombie.png";
        break;
      case 1308:
        path += "nick_belt_zombie.png";
        break;
      case 1309:
        path += "alex_spieldenner_zombie.png";
        break;
      case 1310:
        path += "kyle_aubert_zombie.png";
        break;
      case 1351:
        path += "cathy_heck_zombie.png";
        break;
      case 1352:
        path += "matt_schaefer_zombie.png";
        break;
      case 1353:
        path += "ben_kohring_zombie.png";
        break;
      default:
        p.global.errorMessage("ERROR: Unit ID " + ID + " not found.");
        path += "default.png";
        break;
    }
    return p.global.images.getImage(path);
  }


  boolean targetable(Unit u) {
    if (this.alliance != Alliance.NONE && this.alliance == u.alliance) {
      return false;
    }
    return true;
  }


  void setLevel(int level) {
    this.level = level;
    double level_constant = 0.5 * level * (level + 1);
    switch(this.ID) {
      case 1291: // Zombie
        this.base_health = 3 + 0.4 * level_constant;
        this.base_attack = 1 + 0.1 * level_constant;
        this.base_defense = 0.02 * level_constant;
        break;
      case 1292: // Running Zombie
        this.base_health = 3 + 0.25 * level_constant;
        this.base_attack = 1 + 0.1 * level_constant;
        this.base_defense = 0.02 * level_constant;
        break;
      case 1293: // Armored Zombie
        this.base_health = 3 + 0.4 * level_constant;
        this.base_attack = 1 + 0.075 * level_constant;
        this.base_defense = 2 + 0.045 * level_constant;
        break;
      default:
        break;
    }
    this.curr_health = this.health();
  }


  Item weapon() {
    if (this.gear.containsKey(GearSlot.WEAPON)) {
      return this.gear.get(GearSlot.WEAPON);
    }
    return null;
  }

  Item headgear() {
    if (this.gear.containsKey(GearSlot.HEAD)) {
      return this.gear.get(GearSlot.HEAD);
    }
    return null;
  }

  Item chestgear() {
    if (this.gear.containsKey(GearSlot.CHEST)) {
      return this.gear.get(GearSlot.CHEST);
    }
    return null;
  }

  Item leggear() {
    if (this.gear.containsKey(GearSlot.LEGS)) {
      return this.gear.get(GearSlot.LEGS);
    }
    return null;
  }

  Item footgear() {
    if (this.gear.containsKey(GearSlot.FEET)) {
      return this.gear.get(GearSlot.FEET);
    }
    return null;
  }

  Item offhand() {
    if (this.gear.containsKey(GearSlot.OFFHAND)) {
      return this.gear.get(GearSlot.OFFHAND);
    }
    return null;
  }


  boolean canEquip(GearSlot slot) {
    if (this.gear.containsKey(slot) && (this.gear.get(slot) == null || this.gear.get(slot).remove)) {
      return true;
    }
    return false;
  }

  boolean canPickup() {
    return this.canEquip(GearSlot.WEAPON);
  }

  void pickup(Item i) {
    this.gear.put(GearSlot.WEAPON, i);
  }

  // True if holding one of these items
  boolean holding(int ... item_ids) {
    if (this.weapon() == null) {
      return false;
    }
    for (int item_id : item_ids) {
      if (this.weapon().ID == item_id) {
        return true;
      }
    }
    return false;
  }


  double health() {
    double health = this.base_health;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      health += gear_entry.getValue().health;
    }
    if (this.weapon() != null && this.weapon().weapon()) {
      health += this.weapon().health;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        default:
          break;
      }
    }
    return health;
  }

  // To make abilities array in Unit instead of Hero
  double currMana() {
    return 0;
  }
  double mana() {
    return 0;
  }
  void increaseMana(double amount) {}
  void decreaseMana(double amount) {}

  double attack() {
    double attack = this.base_attack;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      attack += gear_entry.getValue().attack;
    }
    if (this.weapon() != null) {
      if (this.weapon().shootable()) {
        attack += this.weapon().shootAttack();
      }
      else {
        attack += this.weapon().attack;
      }
    }
    if (this.weak()) {
      attack *= LNZ.status_weak_multiplier;
    }
    if (this.wilted()) {
      attack *= LNZ.status_wilted_multiplier;
    }
    if (this.withered()) {
      attack *= LNZ.status_withered_multiplier;
    }
    if (this.relaxed()) {
      attack *= LNZ.status_relaxed_multiplier;
    }
    if (this.nelsonGlare()) {
      attack *= LNZ.ability_103_debuff;
    }
    if (this.nelsonGlareII()) {
      attack *= LNZ.ability_108_debuff;
    }
    if (this.rageOfTheBen()) {
      attack *= LNZ.ability_105_buffAmount;
    }
    if (this.rageOfTheBenII()) {
      attack *= LNZ.ability_110_buffAmount;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        default:
          break;
      }
    }
    return attack;
  }

  double magic() {
    double magic = this.base_magic;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      magic += gear_entry.getValue().magic;
    }
    if (this.weapon() != null) {
      if (this.weapon().shootable()) {
        magic += this.weapon().shootMagic();
      }
      else {
        magic += this.weapon().magic;
      }
    }
    if (this.weak()) {
      magic *= LNZ.status_weak_multiplier;
    }
    if (this.wilted()) {
      magic *= LNZ.status_wilted_multiplier;
    }
    if (this.withered()) {
      magic *= LNZ.status_withered_multiplier;
    }
    if (this.relaxed()) {
      magic *= LNZ.status_relaxed_multiplier;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        default:
          break;
      }
    }
    return magic;
  }

  double power(double attack_ratio, double magic_ratio) {
    return this.attack() * attack_ratio + this.magic() * magic_ratio;
  }

  double defense() {
    double defense = this.base_defense;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      defense += gear_entry.getValue().defense;
    }
    if (this.weapon() != null && this.weapon().weapon()) {
      defense += this.weapon().defense;
    }
    if (this.weak()) {
      defense *= LNZ.status_weak_multiplier;
    }
    if (this.wilted()) {
      defense *= LNZ.status_wilted_multiplier;
    }
    if (this.withered()) {
      defense *= LNZ.status_withered_multiplier;
    }
    if (this.relaxed()) {
      defense *= LNZ.status_relaxed_multiplier;
    }
    if (this.sick()) {
      defense *= LNZ.status_sick_defenseMultiplier;
    }
    if (this.diseased()) {
      defense *= LNZ.status_diseased_defenseMultiplier;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        default:
          break;
      }
    }
    return defense;
  }

  double resistance() {
    double resistance = this.base_resistance;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      resistance += gear_entry.getValue().resistance;
    }
    if (this.weapon() != null && this.weapon().weapon()) {
      resistance += this.weapon().resistance;
    }
    if (this.weak()) {
      resistance *= LNZ.status_weak_multiplier;
    }
    if (this.wilted()) {
      resistance *= LNZ.status_wilted_multiplier;
    }
    if (this.withered()) {
      resistance *= LNZ.status_withered_multiplier;
    }
    if (this.relaxed()) {
      resistance *= LNZ.status_relaxed_multiplier;
    }
    if (this.sick()) {
      resistance *= LNZ.status_sick_defenseMultiplier;
    }
    if (this.diseased()) {
      resistance *= LNZ.status_diseased_defenseMultiplier;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        default:
          break;
      }
    }
    return resistance;
  }

  double piercing() {
    double piercing = this.base_piercing;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      piercing += gear_entry.getValue().piercing;
    }
    if (this.weapon() != null) {
      if (this.weapon().shootable()) {
        piercing += this.weapon().shootPiercing();
      }
      else {
        piercing += this.weapon().piercing;
      }
    }
    if (this.weak()) {
      piercing *= LNZ.status_weak_multiplier;
    }
    if (this.wilted()) {
      piercing *= LNZ.status_wilted_multiplier;
    }
    if (this.withered()) {
      piercing *= LNZ.status_withered_multiplier;
    }
    if (this.relaxed()) {
      piercing *= LNZ.status_relaxed_multiplier;
    }
    if (this.rageRun()) {
      piercing *= LNZ.ability_1021_piercing;
    }
    if (piercing > 1) {
      piercing = 1;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        default:
          break;
      }
    }
    return piercing;
  }

  double penetration() {
    double penetration = this.base_penetration;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      penetration += gear_entry.getValue().penetration;
    }
    if (this.weapon() != null) {
      if (this.weapon().shootable()) {
        penetration += this.weapon().shootPenetration();
      }
      else {
        penetration += this.weapon().penetration;
      }
    }
    if (this.weak()) {
      penetration *= LNZ.status_weak_multiplier;
    }
    if (this.wilted()) {
      penetration *= LNZ.status_wilted_multiplier;
    }
    if (this.withered()) {
      penetration *= LNZ.status_withered_multiplier;
    }
    if (this.relaxed()) {
      penetration *= LNZ.status_relaxed_multiplier;
    }
    if (penetration > 1) {
      penetration = 1;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        default:
          break;
      }
    }
    return penetration;
  }

  double attackRange() {
    return this.attackRange(false);
  }
  double attackRange(boolean forceMelee) {
    double attackRange = this.base_attackRange;
    if (this.weapon() != null && this.weapon().weapon()) {
      if (!forceMelee && this.weapon().shootable()) {
        attackRange = this.weapon().shootRange();
      }
      else {
        attackRange += this.weapon().attackRange;
      }
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        default:
          break;
      }
    }
    return attackRange;
  }

  double attackCooldown() {
    return this.attackCooldown(false);
  }
  double attackCooldown(boolean forceMelee) {
    double attackCooldown = this.base_attackCooldown;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      attackCooldown += gear_entry.getValue().attackCooldown;
    }
    if (this.weapon() != null && this.weapon().weapon()) {
      if (!forceMelee && this.weapon().shootable()) {
        attackCooldown = this.weapon().shootCooldown();
      }
      else {
        attackCooldown += this.weapon().attackCooldown;
      }
    }
    if (this.rageRun()) {
      attackCooldown *= LNZ.ability_1021_attackspeed;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        case 101: // Fearless Leader I
          if (this.rageOfTheBen() && this.currMana() == this.mana()) {
            attackCooldown *= (1 - LNZ.ability_101_bonusAmount * this.currMana() * LNZ.ability_105_fullRageBonus);
          }
          if (this.rageOfTheBenII() && this.currMana() == this.mana()) {
            attackCooldown *= (1 - LNZ.ability_101_bonusAmount * this.currMana() * LNZ.ability_110_fullRageBonus);
          }
          else {
            attackCooldown *= (1 - LNZ.ability_101_bonusAmount * this.currMana());
          }
          break;
        case 106: // Fearless Leader II
          if (this.rageOfTheBen() && this.currMana() == this.mana()) {
            attackCooldown *= (1 - LNZ.ability_106_bonusAmount * this.currMana() * LNZ.ability_105_fullRageBonus);
          }
          if (this.rageOfTheBenII() && this.currMana() == this.mana()) {
            attackCooldown *= (1 - LNZ.ability_106_bonusAmount * this.currMana() * LNZ.ability_110_fullRageBonus);
          }
          else {
            attackCooldown *= (1 - LNZ.ability_106_bonusAmount * this.currMana());
          }
          break;
        default:
          break;
      }
    }
    return attackCooldown;
  }

  double attackTime() {
    return this.attackTime(false);
  }
  double attackTime(boolean forceMelee) {
    double attackTime = this.base_attackTime;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      attackTime += gear_entry.getValue().attackTime;
    }
    if (this.weapon() != null && this.weapon().weapon()) {
      if (!forceMelee && this.weapon().shootable()) {
        attackTime = this.weapon().shootTime();
      }
      else {
        attackTime += this.weapon().attackTime;
      }
    }
    if (this.rageRun()) {
      attackTime *= LNZ.ability_1021_attackspeed;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        case 101: // Fearless Leader I
          if (this.rageOfTheBen() && this.currMana() == this.mana()) {
            attackTime *= (1 - LNZ.ability_101_bonusAmount * this.currMana() * LNZ.ability_105_fullRageBonus);
          }
          if (this.rageOfTheBenII() && this.currMana() == this.mana()) {
            attackTime *= (1 - LNZ.ability_101_bonusAmount * this.currMana() * LNZ.ability_110_fullRageBonus);
          }
          else {
            attackTime *= (1 - LNZ.ability_101_bonusAmount * this.currMana());
          }
          break;
        case 106: // Fearless Leader II
          if (this.rageOfTheBen() && this.currMana() == this.mana()) {
            attackTime *= (1 - LNZ.ability_106_bonusAmount * this.currMana() * LNZ.ability_105_fullRageBonus);
          }
          if (this.rageOfTheBenII() && this.currMana() == this.mana()) {
            attackTime *= (1 - LNZ.ability_106_bonusAmount * this.currMana() * LNZ.ability_110_fullRageBonus);
          }
          else {
            attackTime *= (1 - LNZ.ability_106_bonusAmount * this.currMana());
          }
          break;
        default:
          break;
      }
    }
    return attackTime;
  }

  double sight() {
    double sight = this.base_sight;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      sight += gear_entry.getValue().sight;
    }
    if (this.weapon() != null) {
      sight += this.weapon().sight;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        default:
          break;
      }
    }
    return sight;
  }

  double speed() {
    double speed = this.base_speed;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      speed += gear_entry.getValue().speed;
    }
    if (this.weapon() != null) {
      speed += this.weapon().speedWhenHolding();
    }
    if (this.chilled()) {
      if (this.element == Element.CYAN) {
        speed *= LNZ.status_chilled_speedMultiplierCyan;
      }
      else {
        speed *= LNZ.status_chilled_speedMultiplier;
      }
    }
    if (this.frozen()) {
      return 0;
    }
    if (this.nelsonGlare()) {
      speed *= LNZ.ability_103_debuff;
    }
    if (this.nelsonGlareII()) {
      speed *= LNZ.ability_108_debuff;
    }
    if (this.senselessGrit()) {
      if (this.curr_action == UnitAction.TARGETING_UNIT) {
        speed *= LNZ.ability_104_speedBuff;
      }
    }
    if (this.senselessGritII()) {
      if (this.curr_action == UnitAction.TARGETING_UNIT) {
        speed *= LNZ.ability_109_speedBuff;
      }
    }
    if (this.tongueLash()) {
      speed *= LNZ.ability_112_slowAmount;
    }
    if (this.running()) {
      speed *= LNZ.status_running_multiplier;
    }
    if (this.slowed()) {
      speed *= LNZ.status_slowed_multiplier;
    }
    if (this.relaxed()) {
      speed *= LNZ.status_relaxed_multiplier;
    }
    if (this.rageRun()) {
      speed *= LNZ.ability_1021_speed;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        default:
          break;
      }
    }
    return speed;
  }

  double tenacity() {
    double tenacity = this.base_tenacity;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      tenacity += gear_entry.getValue().tenacity;
    }
    if (this.weapon() != null && this.weapon().weapon()) {
      tenacity += this.weapon().tenacity;
    }
    if (this.weak()) {
      tenacity *= LNZ.status_weak_multiplier;
    }
    if (this.wilted()) {
      tenacity *= LNZ.status_wilted_multiplier;
    }
    if (this.withered()) {
      tenacity *= LNZ.status_withered_multiplier;
    }
    if (this.relaxed()) {
      tenacity *= LNZ.status_relaxed_multiplier;
    }
    if (this.sick()) {
      tenacity *= LNZ.status_sick_defenseMultiplier;
    }
    if (this.diseased()) {
      tenacity *= LNZ.status_diseased_defenseMultiplier;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        case 101: // Fearless Leader I
          if (this.rageOfTheBen() && this.currMana() == this.mana()) {
            tenacity += LNZ.ability_101_bonusAmount * this.currMana() * LNZ.ability_105_fullRageBonus;
          }
          if (this.rageOfTheBenII() && this.currMana() == this.mana()) {
            tenacity += LNZ.ability_101_bonusAmount * this.currMana() * LNZ.ability_110_fullRageBonus;
          }
          else {
            tenacity += LNZ.ability_101_bonusAmount * this.currMana();
          }
          break;
        case 106: // Fearless Leader II
          if (this.rageOfTheBen() && this.currMana() == this.mana()) {
            tenacity += LNZ.ability_106_bonusAmount * this.currMana() * LNZ.ability_105_fullRageBonus;
          }
          if (this.rageOfTheBenII() && this.currMana() == this.mana()) {
            tenacity += LNZ.ability_106_bonusAmount * this.currMana() * LNZ.ability_110_fullRageBonus;
          }
          else {
            tenacity += LNZ.ability_106_bonusAmount * this.currMana();
          }
          break;
        default:
          break;
      }
    }
    if (tenacity > 1) {
      tenacity = 1;
    }
    return tenacity;
  }

  int agility() {
    int agility = this.base_agility;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      agility += gear_entry.getValue().agility;
    }
    if (this.weapon() != null && this.weapon().weapon()) {
      agility += this.weapon().agility;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        default:
          break;
      }
    }
    if (agility < 0) {
      agility = 0;
    }
    if (agility > LNZ.unit_maxAgility) {
      agility = LNZ.unit_maxAgility;
    }
    return agility;
  }

  double lifesteal() {
    double lifesteal = this.base_lifesteal;
    for (Map.Entry<GearSlot, Item> gear_entry : this.gear.entrySet()) {
      if (gear_entry.getKey() == GearSlot.WEAPON) {
        continue;
      }
      if (gear_entry.getValue() == null) {
        continue;
      }
      lifesteal += gear_entry.getValue().lifesteal;
    }
    if (this.weapon() != null && this.weapon().weapon()) {
      lifesteal += this.weapon().lifesteal;
    }
    if (this.weak()) {
      lifesteal *= LNZ.status_weak_multiplier;
    }
    if (this.wilted()) {
      lifesteal *= LNZ.status_wilted_multiplier;
    }
    if (this.withered()) {
      lifesteal *= LNZ.status_withered_multiplier;
    }
    if (this.rageRun()) {
      lifesteal *= LNZ.ability_1021_lifesteal;
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        default:
          break;
      }
    }
    return lifesteal;
  }

  int swimNumber() {
    int swimNumber = 0;
    switch(this.ID) {
      case 1009: // Frog
      case 1010: // Quail
      case 1012: // Fawn
      case 1013: // Raccoon
      case 1014: // Doe
      case 1015: // Buck
        swimNumber = 1;
        break;
      default:
        break;
    }
    if (this.element == Element.BLUE) {
      swimNumber += 1;
    }
    return swimNumber;
  }

  double passiveHeal() {
    double passive_heal = 0;
    if (Hero.class.isInstance(this)) {
      passive_heal += LNZ.hero_passiveHealPercent;
    }
    if (this.relaxed()) {
      passive_heal += LNZ.status_relaxed_healMultiplier;
    }
    return passive_heal;
  }


  void removeStatusEffect(StatusEffectCode code) {
    this.statuses.remove(code);
  }

  void addStatusEffect(StatusEffectCode code) {
    this.addStatusEffect(code, null);
  }
  void addStatusEffect(StatusEffectCode code, DamageSource damage_source) {
    if (this.statuses.containsKey(code)) {
      StatusEffect status_effect = this.statuses.get(code);
      status_effect.permanent = true;
      if (damage_source != null) {
        status_effect.damage_source = damage_source;
      }
    }
    else {
      StatusEffect status_effect = new StatusEffect(p, code, true);
      if (damage_source != null) {
        status_effect.damage_source = damage_source;
      }
      this.statuses.put(code, status_effect);
    }
  }

  double calculateTimerFrom(StatusEffectCode code, double timer) {
    if (code == StatusEffectCode.SUPPRESSED) {
      return timer;
    }
    if (code.negative()) {
      timer *= this.element.resistanceFactorTo(code.element());
      timer *= 1 - this.tenacity();
    }
    return timer;
  }
  
  void addStatusEffect(StatusEffectCode code, double timer) {
    this.addStatusEffect(code, timer, null);
  }
  void addStatusEffect(StatusEffectCode code, double timer, DamageSource damage_source) {
    timer = this.calculateTimerFrom(code, timer);
    if (this.statuses.containsKey(code)) {
      StatusEffect status_effect = this.statuses.get(code);
      if (!status_effect.permanent) {
        status_effect.addTime(timer);
        if (damage_source != null) {
          status_effect.damage_source = damage_source;
        }
      }
    }
    else {
      StatusEffect status_effect = new StatusEffect(p, code, timer);
      if (damage_source != null) {
        status_effect.damage_source = damage_source;
      }
      this.statuses.put(code, status_effect);
      if (code.negative()) {
        this.effectFromNewNegativeStatusEffect();
      }
    }
  }

  void refreshStatusEffect(StatusEffectCode code, double timer) {
    this.refreshStatusEffect(code, timer, null);
  }
  void refreshStatusEffect(StatusEffectCode code, double timer, DamageSource damage_source) {
    timer = this.calculateTimerFrom(code, timer);
    if (this.statuses.containsKey(code)) {
      StatusEffect status_effect = this.statuses.get(code);
      if (!status_effect.permanent) {
        status_effect.refreshTime(timer);
        if (damage_source != null) {
          status_effect.damage_source = damage_source;
        }
      }
    }
    else {
      StatusEffect status_effect = new StatusEffect(p, code, timer);
      if (damage_source != null) {
        status_effect.damage_source = damage_source;
      }
      this.statuses.put(code, status_effect);
      if (code.negative()) {
        this.effectFromNewNegativeStatusEffect();
      }
    }
  }
  void effectFromNewNegativeStatusEffect() {
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        case 101: // Fearless Leader I
          if (this.rageOfTheBenII()) {
            this.increaseMana((int)(LNZ.ability_101_rageGain * LNZ.ability_110_rageGainBonus));
          }
          else if (this.rageOfTheBen()) {
            this.increaseMana((int)(LNZ.ability_101_rageGain * LNZ.ability_105_rageGainBonus));
          }
          else {
            this.increaseMana(LNZ.ability_101_rageGain);
          }
          a.timer_other = LNZ.ability_101_cooldownTimer;
          break;
        case 106: // Fearless Leader II
          if (this.rageOfTheBenII()) {
            this.increaseMana((int)(LNZ.ability_106_rageGain * LNZ.ability_110_rageGainBonus));
          }
          else if (this.rageOfTheBen()) {
            this.increaseMana((int)(LNZ.ability_106_rageGain * LNZ.ability_105_rageGainBonus));
          }
          else {
            this.increaseMana(LNZ.ability_106_rageGain);
          }
          a.timer_other = LNZ.ability_106_cooldownTimer;
          break;
        default:
          break;
      }
    }
  }

  boolean hasStatusEffect(StatusEffectCode code) {
    return this.statuses.containsKey(code);
  }
  boolean invulnerable() {
    return this.hasStatusEffect(StatusEffectCode.INVULNERABLE);
  }
  boolean unkillable() {
    return this.hasStatusEffect(StatusEffectCode.UNKILLABLE);
  }
  boolean hungry() {
    return this.hasStatusEffect(StatusEffectCode.HUNGRY);
  }
  boolean weak() {
    return this.hasStatusEffect(StatusEffectCode.WEAK);
  }
  boolean thirsty() {
    return this.hasStatusEffect(StatusEffectCode.THIRSTY);
  }
  boolean woozy() {
    return this.hasStatusEffect(StatusEffectCode.WOOZY);
  }
  boolean confused() {
    return this.hasStatusEffect(StatusEffectCode.CONFUSED);
  }
  boolean bleeding() {
    return this.hasStatusEffect(StatusEffectCode.BLEEDING);
  }
  boolean hemorrhaging() {
    return this.hasStatusEffect(StatusEffectCode.HEMORRHAGING);
  }
  boolean wilted() {
    return this.hasStatusEffect(StatusEffectCode.WILTED);
  }
  boolean withered() {
    return this.hasStatusEffect(StatusEffectCode.WITHERED);
  }
  boolean visible() {
    return this.hasStatusEffect(StatusEffectCode.VISIBLE);
  }
  boolean suppressed() {
    return this.hasStatusEffect(StatusEffectCode.SUPPRESSED);
  }
  boolean untargetable() {
    return this.hasStatusEffect(StatusEffectCode.UNTARGETABLE);
  }
  boolean stunned() {
    return this.hasStatusEffect(StatusEffectCode.STUNNED);
  }
  boolean invisible() {
    return this.hasStatusEffect(StatusEffectCode.INVISIBLE);
  }
  boolean uncollidable() {
    return this.hasStatusEffect(StatusEffectCode.UNCOLLIDABLE);
  }
  boolean running() {
    return this.hasStatusEffect(StatusEffectCode.RUNNING);
  }
  boolean fertilized() {
    return this.hasStatusEffect(StatusEffectCode.FERTILIZED);
  }
  boolean sneaking() {
    return this.hasStatusEffect(StatusEffectCode.SNEAKING);
  }
  boolean relaxed() {
    return this.hasStatusEffect(StatusEffectCode.RELAXED);
  }
  boolean ghosting() {
    return this.hasStatusEffect(StatusEffectCode.GHOSTING);
  }
  boolean silenced() {
    return this.hasStatusEffect(StatusEffectCode.SILENCED);
  }
  boolean slowed() {
    return this.hasStatusEffect(StatusEffectCode.SLOWED);
  }
  boolean drenched() {
    return this.hasStatusEffect(StatusEffectCode.DRENCHED);
  }
  boolean drowning() {
    return this.hasStatusEffect(StatusEffectCode.DROWNING);
  }
  boolean burnt() {
    return this.hasStatusEffect(StatusEffectCode.BURNT);
  }
  boolean charred() {
    return this.hasStatusEffect(StatusEffectCode.CHARRED);
  }
  boolean chilled() {
    return this.hasStatusEffect(StatusEffectCode.CHILLED);
  }
  boolean frozen() {
    return this.hasStatusEffect(StatusEffectCode.FROZEN);
  }
  boolean sick() {
    return this.hasStatusEffect(StatusEffectCode.SICK);
  }
  boolean diseased() {
    return this.hasStatusEffect(StatusEffectCode.DISEASED);
  }
  boolean rotting() {
    return this.hasStatusEffect(StatusEffectCode.ROTTING);
  }
  boolean decayed() {
    return this.hasStatusEffect(StatusEffectCode.DECAYED);
  }
  boolean shaken() {
    return this.hasStatusEffect(StatusEffectCode.SHAKEN);
  }
  boolean fallen() {
    return this.hasStatusEffect(StatusEffectCode.FALLEN);
  }
  boolean shocked() {
    return this.hasStatusEffect(StatusEffectCode.SHOCKED);
  }
  boolean paralyzed() {
    return this.hasStatusEffect(StatusEffectCode.PARALYZED);
  }
  boolean unstable() {
    return this.hasStatusEffect(StatusEffectCode.UNSTABLE);
  }
  boolean radioactive() {
    return this.hasStatusEffect(StatusEffectCode.RADIOACTIVE);
  }
  boolean nelsonGlare() {
    return this.hasStatusEffect(StatusEffectCode.NELSON_GLARE);
  }
  boolean nelsonGlareII() {
    return this.hasStatusEffect(StatusEffectCode.NELSON_GLAREII);
  }
  boolean senselessGrit() {
    return this.hasStatusEffect(StatusEffectCode.SENSELESS_GRIT);
  }
  boolean senselessGritII() {
    return this.hasStatusEffect(StatusEffectCode.SENSELESS_GRITII);
  }
  boolean rageOfTheBen() {
    return this.hasStatusEffect(StatusEffectCode.RAGE_OF_THE_BEN);
  }
  boolean rageOfTheBenII() {
    return this.hasStatusEffect(StatusEffectCode.RAGE_OF_THE_BENII);
  }
  boolean aposematicCamouflage() {
    return this.hasStatusEffect(StatusEffectCode.APOSEMATIC_CAMOUFLAGE);
  }
  boolean aposematicCamouflageII() {
    return this.hasStatusEffect(StatusEffectCode.APOSEMATIC_CAMOUFLAGEII);
  }
  boolean tongueLash() {
    return this.hasStatusEffect(StatusEffectCode.TONGUE_LASH);
  }
  boolean alkaloidSecretion() {
    return this.hasStatusEffect(StatusEffectCode.ALKALOID_SECRETION);
  }
  boolean alkaloidSecretionII() {
    return this.hasStatusEffect(StatusEffectCode.ALKALOID_SECRETIONII);
  }
  boolean rageRun() {
    return this.hasStatusEffect(StatusEffectCode.RAGE_RUN);
  }

  StatusEffectCode priorityStatusEffect() {
    return null;
  }


  synchronized void refreshPlayerSight(AbstractGameMap map) {
    HashSet<IntegerCoordinate> last_squares_sight = new HashSet<IntegerCoordinate>();
    for (IntegerCoordinate coordinate : this.curr_squares_sight) {
      last_squares_sight.add(coordinate.copy());
    }
    this.curr_squares_sight = this.getSquaresSight(map);
    for (IntegerCoordinate coordinate : this.curr_squares_sight) {
      if (!last_squares_sight.contains(coordinate)) {
        map.exploreTerrainAndVisible(coordinate.x, coordinate.y);
      }
    }
    for (IntegerCoordinate coordinate : last_squares_sight) {
      if (!this.curr_squares_sight.contains(coordinate)) {
        map.setTerrainVisible(false, coordinate.x, coordinate.y);
      }
    }
  }


  void startPathfindingThread(AbstractGameMap map) {
    switch(this.curr_action) {
      case MOVING:
      case MOVING_AND_USING_ITEM:
        this.startPathfindingThread(this.curr_action_coordinate, map);
        break;
      case TARGETING_FEATURE:
      case TARGETING_FEATURE_WITH_ITEM:
      case TARGETING_UNIT:
      case TARGETING_ITEM:
        if (this.object_targeting == null) {
          break;
        }
        this.startPathfindingThread(this.object_targeting.xCenter(), this.object_targeting.yCenter(), map);
        break;
      default:
        break;
    }
  }
  void startPathfindingThread(double x, double y, AbstractGameMap map) {
    this.startPathfindingThread(new Coordinate(x, y), map);
  }
  void startPathfindingThread(Coordinate target, AbstractGameMap map) {
    this.waiting_for_pathfinding_thread = true;
    if (this.pathfinding_thread != null && this.pathfinding_thread.isAlive()) {
      this.pathfinding_thread.stop_thread = true;
    }
    this.pathfinding_thread = new PathFindingThread(p, target, map);
    this.pathfinding_thread.start();
  }

  // If unit choosing to move somewhere
  void moveLogic(int time_elapsed, AbstractGameMap map) {
    if (this.waiting_for_pathfinding_thread) {
      if (this.pathfinding_thread == null) {
        this.waiting_for_pathfinding_thread = false;
      }
      else if (this.pathfinding_thread.isAlive()) {
        if (this.last_move_any_collision) { // only wait for thead when you actually collide
          return;
        }
      }
      else {
        this.move_stack = this.pathfinding_thread.move_stack;
        if (!this.move_stack.empty()) {
          this.using_current_move_stack = true;
        }
        this.pathfinding_thread = null;
        this.waiting_for_pathfinding_thread = false;
        return; // have to return so next loop the unit faces properly
      }
    }
    boolean collision_last_move = this.last_move_collision;
    if (this.sneaking()) {
      this.move(time_elapsed, map, MoveModifier.SNEAK);
    }
    else {
      this.move(time_elapsed, map, MoveModifier.NONE);
    }
    if (this.using_current_move_stack) {
      if (this.move_stack.empty()) {
        this.using_current_move_stack = false;
      }
      else if (this.distanceFromPoint(this.move_stack.peek().x, this.move_stack.peek().y) < this.last_move_distance) {
        this.move_stack.pop();
      }
      if (this.curr_action == UnitAction.TARGETING_UNIT) {
        this.timer_update_pathfinding -= time_elapsed;
        if (this.timer_update_pathfinding < 0) {
          this.timer_update_pathfinding += LNZ.unit_update_pathfinding_timer;
          this.startPathfindingThread(map);
        }
      }
    }
    if (this.last_move_collision) {
      if (collision_last_move) {
        this.timer_actionTime -= time_elapsed;
      }
      else {
        this.timer_actionTime = LNZ.unit_moveCollisionStopActionTime;
      }
      switch(this.ID) {
        case 1002: // Chicken
        case 1003: // Chick
        case 1005: // Rooster
          if (!this.falling) {
            this.jump(map);
          }
          break;
        default:
          break;
      }
      if (this.timer_actionTime < 0) { // colliding over and over
        this.stopAction(true);
      }
    }
    this.timer_walk -= time_elapsed;
    if (this.timer_walk < 0) {
      this.timer_walk += LNZ.unit_timer_walk;
      GameMapSquare square = map.mapSquare(this.coordinate);
      if (square != null) {
        this.walkSound(square.terrain_id);
      }
    }
  }


  void update(int time_elapsed, AbstractGameMap map) {
    // timers
    this.update(time_elapsed);
    // ai logic for ai units
    if (this.ai_controlled) {
      this.aiLogic(time_elapsed, map);
    }
    if ((this.suppressed() || this.stunned()) && !this.curr_action_unstoppable) {
      this.stopAction();
    }
    // unit action
    switch(this.curr_action) {
      case MOVING:
        switch(this.curr_action_id) {
          case 1: // Anuran Appetite regurgitate
            this.move(time_elapsed, map, MoveModifier.ANURAN_APPETITE);
            if (this.last_move_any_collision) {
              this.stopAction(true);
            }
            break;
          default:
            if (this.using_current_move_stack) {
              if (this.move_stack.empty()) {
                this.using_current_move_stack = false;
                this.face(this.curr_action_coordinate);
              }
              else {
                this.face(this.move_stack.peek().x, this.move_stack.peek().y);
              }
            }
            else {
              this.face(this.curr_action_coordinate);
            }
            this.moveLogic(time_elapsed, map);
            break;
        }
        if (this.coordinate.distance(this.curr_action_coordinate)
          < this.last_move_distance + LNZ.small_number) {
          this.stopAction(true);
        }
        break;
      case CAST_WHEN_IN_RANGE:
        if (this.curr_action_id < 0 || this.curr_action_id >= this.abilities.size()) {
          this.stopAction();
          break;
        }
        Ability a = this.abilities.get(this.curr_action_id);
        if (a == null) {
          this.stopAction();
          break;
        }
        if (this.object_targeting == null || this.object_targeting.remove) {
          this.stopAction();
          break;
        }
        Unit target_unit = (Unit)this.object_targeting;
        if (target_unit.untargetable()) {
          this.stopAction();
        }
        if (!target_unit.targetable(this)) {
          this.stopAction();
          break;
        }
        if (this.distance(target_unit) > a.castsOnTargetRange()) {
          this.face(target_unit);
          this.moveLogic(time_elapsed, map);
        }
        else {
          a.activate(this, map, target_unit);
        }
        break;
      case CASTING:
        if (this.curr_action_id < 0 || this.curr_action_id >= this.abilities.size()) {
          this.stopAction();
          break;
        }
        Ability a_casting = this.abilities.get(this.curr_action_id);
        if (a_casting == null) {
          this.stopAction();
          break;
        }
        switch(a_casting.ID) {
          case 113: // Amphibious Leap
          case 118: // Amphibious Leap II
            this.move(time_elapsed, map, MoveModifier.AMPHIBIOUS_LEAP);
            if (this.last_move_any_collision) {
              a_casting.timer_other = 0;
            }
            break;
          default:
            break;
        }
        break;
      case TARGETING_TERRAIN:
        GameMapSquare targeted_square = map.mapSquare(this.curr_action_coordinate);
        if (targeted_square == null || targeted_square.interactionTooltip(this).equals("")) {
          this.stopAction();
          break;
        }
        double distance_to_terrain = this.distance(this.curr_action_coordinate);
        if (distance_to_terrain - 0.5 > LNZ.feature_defaultInteractionDistance) {
          if (this.using_current_move_stack) {
            if (this.move_stack.empty()) {
              this.using_current_move_stack = false;
              this.face(this.curr_action_coordinate);
            }
            else {
              this.face(this.move_stack.peek().x, this.move_stack.peek().y);
            }
          }
          else {
            this.face(this.curr_action_coordinate);
          }
          this.moveLogic(time_elapsed, map);
          break;
        }
        this.face(this.curr_action_coordinate);
        this.curr_action = UnitAction.TERRAIN_INTERACTION;
        this.timer_actionTime = targeted_square.interactionTime(map, this, true);
        break;
      case TERRAIN_INTERACTION:
        GameMapSquare interacting_square = map.mapSquare(this.curr_action_coordinate);
        if (interacting_square == null || interacting_square.interactionTooltip(this).equals("")) {
          this.stopAction();
          break;
        }
        this.timer_actionTime -= time_elapsed;
        if (timer_actionTime < 0) {
          IntegerCoordinate square = new IntegerCoordinate(this.curr_action_coordinate);
          this.stopAction();
          interacting_square.interact(map, this, square);
        }
        break;
      case TARGETING_FEATURE:
      case TARGETING_FEATURE_WITH_ITEM:
        if (this.object_targeting == null || this.object_targeting.remove) {
          this.stopAction();
          break;
        }
        Feature f = (Feature)this.object_targeting;
        if (!f.targetable(this)) {
          this.stopAction();
          break;
        }
        if (this.distance(f) > f.interactionDistance()) {
          if (this.using_current_move_stack) {
            if (this.move_stack.empty()) {
              this.using_current_move_stack = false;
              this.face(f);
            }
            else {
              this.face(this.move_stack.peek().x, this.move_stack.peek().y);
            }
          }
          else {
            this.face(f);
          }
          this.moveLogic(time_elapsed, map);
          break;
        }
        this.face(f);
        if (f.onInteractionCooldown()) {
          break;
        }
        if (this.curr_action == UnitAction.TARGETING_FEATURE_WITH_ITEM) {
          this.curr_action = UnitAction.FEATURE_INTERACTION_WITH_ITEM;
        }
        else {
          this.curr_action = UnitAction.FEATURE_INTERACTION;
        }
        this.timer_actionTime = f.interactionTime(map, this);
        break;
      case FEATURE_INTERACTION:
      case FEATURE_INTERACTION_WITH_ITEM:
        if (this.object_targeting == null || this.object_targeting.remove) {
          this.stopAction();
          break;
        }
        this.timer_actionTime -= time_elapsed;
        if (this.timer_actionTime < 0) {
          boolean use_item = this.curr_action == UnitAction.FEATURE_INTERACTION_WITH_ITEM;
          this.curr_action = UnitAction.NONE; // must be before since interact() can set HERO_INTERACTING_WITH_FEATURE
          ((Feature)this.object_targeting).interact(this, map, use_item);
          if (this.curr_action == UnitAction.NONE) {
            this.stopAction();
          }
        }
        break;
      case HERO_INTERACTING_WITH_FEATURE:
      case HERO_INTERACTING_WITH_FEATURE_WITH_ITEM:
        if (this.object_targeting == null || this.object_targeting.remove) {
          this.stopAction();
          break;
        }
        break;
      case TARGETING_UNIT:
        if (this.object_targeting == null || this.object_targeting.remove) {
          this.stopAction();
          break;
        }
        Unit u = (Unit)this.object_targeting;
        if (u.untargetable()) {
          this.stopAction();
        }
        if (!u.targetable(this)) {
          this.stopAction();
          break;
        }
        double distance = this.distance(u);
        if (distance > this.attackRange()) {
          if (this.using_current_move_stack) {
            if (this.move_stack.empty()) {
              this.using_current_move_stack = false;
              this.face(u);
            }
            else {
              this.face(this.move_stack.peek().x, this.move_stack.peek().y);
            }
          }
          else {
            this.face(u);
          }
          this.moveLogic(time_elapsed, map);
        }
        else if (this.timer_attackCooldown <= 0) {
          this.face(u);
          if (this.weapon() != null && this.weapon().shootable()) {
            if (this.weapon().meleeAttackable() && distance < this.attackRange(true)) {
              this.curr_action = UnitAction.ATTACKING;
              this.timer_actionTime = this.attackTime(true);
            }
            else {
              this.curr_action = UnitAction.SHOOTING;
              this.timer_actionTime = this.attackTime();
            }
          }
          else {
            this.curr_action = UnitAction.ATTACKING;
            this.timer_actionTime = this.attackTime();
          }
        }
        break;
      case AIMING:
        if (this.weapon() == null || !this.weapon().shootable()) {
          this.stopAction();
          break;
        }
        this.face(this.curr_action_coordinate);
        if (this.timer_attackCooldown <= 0) {
          this.curr_action = UnitAction.SHOOTING;
          this.timer_actionTime = this.attackTime();
        }
        break;
      case TARGETING_ITEM:
        if (this.object_targeting == null || this.object_targeting.remove) {
          this.stopAction();
          break;
        }
        Item i = (Item)this.object_targeting;
        if (!i.targetable(this)) {
          this.stopAction();
          break;
        }
        if (this.distance(i) > i.interactionDistance()) {
          if (this.using_current_move_stack) {
            if (this.move_stack.empty()) {
              this.using_current_move_stack = false;
              this.face(i);
            }
            else {
              this.face(this.move_stack.peek().x, this.move_stack.peek().y);
            }
          }
          else {
            this.face(i);
          }
          this.moveLogic(time_elapsed, map);
        }
        else {
          this.face(i);
          if (this.gear.containsKey(GearSlot.WEAPON)) {
            if (this.weapon() == null) {
              this.pickup(new Item(p, i));
              i.remove = true;
              if (!this.ai_controlled) {
                i.pickupSound();
              }
              if (this.map_key == 0) {
                map.selected_object = this.weapon();
              }
            }
            else if (this.weapon().ID == i.ID && this.weapon().maxStack() > this.weapon().stack) {
              int stack_to_pickup = Math.min(this.weapon().maxStack() - this.weapon().stack, i.stack);
              i.removeStack(stack_to_pickup);
              this.weapon().addStack(stack_to_pickup);
              if (!this.ai_controlled) {
                i.pickupSound();
              }
              if (this.map_key == 0) {
                map.selected_object = this.weapon();
              }
            }
          }
          this.stopAction();
        }
        break;
      case ATTACKING:
        if (this.object_targeting == null || this.object_targeting.remove) {
          this.stopAction();
          break;
        }
        Unit unit_attacking = (Unit)this.object_targeting;
        if (unit_attacking.untargetable()) {
          this.stopAction();
        }
        if (this.frozen()) {
          this.stopAction();
          break;
        }
        else if (this.chilled()) {
          if (this.element == Element.CYAN) {
            this.timer_actionTime -= time_elapsed * LNZ.status_chilled_cooldownMultiplierCyan;
          }
          else {
            this.timer_actionTime -= time_elapsed * LNZ.status_chilled_cooldownMultiplier;
          }
        }
        else {
          this.timer_actionTime -= time_elapsed;
        }
        if (this.timer_actionTime < 0) {
          this.curr_action = UnitAction.TARGETING_UNIT;
          this.attack(unit_attacking, map);
        }
        break;
      case SHOOTING:
        if (this.weapon() == null || !this.weapon().shootable()) {
          this.stopAction();
          break;
        }
        if (this.object_targeting != null) {
          this.face(this.object_targeting);
        }
        this.timer_actionTime -= time_elapsed;
        if (this.timer_actionTime < 0) {
          this.shoot(map);
          if (this.weapon().shootable() && this.weapon().automatic() && p.global.holding_rightclick) {
            if (this.map_key == 0 && p.global.holding_ctrl) {
              this.curr_action = UnitAction.AIMING;
            }
            else {
              this.curr_action = UnitAction.TARGETING_UNIT;
            }
          }
          else {
            this.stopAction();
          }
        }
        break;
      case USING_ITEM:
        if (this.weapon() == null || !this.weapon().usable()) {
          this.stopAction();
          break;
        }
        this.timer_actionTime -= time_elapsed;
        if (this.timer_actionTime < 0) {
          this.useItem(map);
          this.stopAction();
        }
        break;
      case MOVING_AND_USING_ITEM:
        if (this.weapon() == null || !this.weapon().usable()) {
          this.curr_action = UnitAction.MOVING;
          break;
        }
        this.timer_actionTime -= time_elapsed;
        if (this.timer_actionTime < 0) {
          this.useItem(map);
          this.curr_action = UnitAction.MOVING;
        }
        if (this.using_current_move_stack) {
          if (this.move_stack.empty()) {
            this.using_current_move_stack = false;
            this.face(this.curr_action_coordinate);
          }
          else {
            this.face(this.move_stack.peek().x, this.move_stack.peek().y);
          }
        }
        else {
          this.face(this.curr_action_coordinate);
        }
        this.moveLogic(time_elapsed, map);
        if (this.coordinate.distance(this.curr_action_coordinate)
          < this.last_move_distance + LNZ.small_number) {
            this.curr_action = UnitAction.USING_ITEM;
        }
        break;
      case NONE:
        break;
    }
    // update gear items
    for (Map.Entry<GearSlot, Item> slot : this.gear.entrySet()) {
      if (slot.getValue() != null && slot.getValue().remove) {
        this.gear.put(slot.getKey(), null);
      }
    }
    // Update status effects
    Iterator<Map.Entry<StatusEffectCode, StatusEffect>> status_iterator = this.statuses.entrySet().iterator();
    while(status_iterator.hasNext()) {
      Map.Entry<StatusEffectCode, StatusEffect> entry = status_iterator.next();
      StatusEffect se = entry.getValue();
      if (!se.permanent) {
        se.timer_gone -= time_elapsed;
        if (se.timer_gone < 0) {
          status_iterator.remove();
          continue;
        }
      }
      switch(entry.getKey()) {
        case HUNGRY:
          se.number -= time_elapsed;
          if (se.number < 0) {
            se.number += LNZ.status_hunger_tickTimer;
            this.calculateDotDamage(LNZ.status_hunger_dot, false,
              LNZ.status_hunger_damageLimit, se.damage_source);
            if (Misc.randomChance(LNZ.status_hunger_weakPercentage)) {
              this.refreshStatusEffect(StatusEffectCode.WEAK, 8000,
                se.damage_source.copy());
            }
          }
          break;
        case THIRSTY:
          se.number -= time_elapsed;
          if (se.number < 0) {
            se.number += LNZ.status_thirst_tickTimer;
            this.calculateDotDamage(LNZ.status_thirst_dot, false,
              LNZ.status_thirst_damageLimit, se.damage_source);
            if (Misc.randomChance(LNZ.status_thirst_woozyPercentage)) {
              this.refreshStatusEffect(StatusEffectCode.WOOZY, 10000,
                se.damage_source.copy());
            }
            if (Misc.randomChance(LNZ.status_thirst_confusedPercentage)) {
              this.refreshStatusEffect(StatusEffectCode.CONFUSED, 10000,
                se.damage_source.copy());
            }
          }
          break;
        case WOOZY:
          se.number -= time_elapsed;
          if (se.number < 0) {
            se.number += Misc.randomInt(LNZ.status_woozy_tickMaxTimer);
            this.turn(LNZ.status_woozy_maxAmount - 2 * Misc.randomDouble(LNZ.status_woozy_maxAmount));
            this.stopAction();
          }
          break;
        case CONFUSED:
          se.number -= time_elapsed;
          if (se.number < 0) {
            se.number += Misc.randomInt(LNZ.status_confused_tickMaxTimer);
            this.moveTo(this.coordinate.x + LNZ.status_confused_maxAmount -
              2 * Misc.randomDouble(LNZ.status_confused_maxAmount), this.coordinate.y +
              LNZ.status_confused_maxAmount - 2 * Misc.randomDouble(LNZ.status_confused_maxAmount), null);
          }
          break;
        case BLEEDING:
          se.number -= time_elapsed;
          if (se.number < 0) {
            se.number += LNZ.status_bleed_tickTimer;
            this.calculateDotDamage(LNZ.status_bleed_dot, true,
              LNZ.status_bleed_damageLimit, se.damage_source);
            if (Misc.randomChance(LNZ.status_bleed_hemorrhagePercentage)) {
              this.refreshStatusEffect(StatusEffectCode.HEMORRHAGING, 5000,
                se.damage_source.copy());
            }
          }
          break;
        case HEMORRHAGING:
          se.number -= time_elapsed;
          if (se.number < 0) {
            se.number += LNZ.status_hemorrhage_tickTimer;
            this.calculateDotDamage(LNZ.status_hemorrhage_dot, true,
              LNZ.status_hemorrhage_damageLimit, se.damage_source);
            if (Misc.randomChance(LNZ.status_hemorrhage_bleedPercentage)) {
              this.refreshStatusEffect(StatusEffectCode.BLEEDING, 5000,
                se.damage_source.copy());
            }
          }
          break;
        case DRENCHED:
          se.number -= time_elapsed;
          if (se.number < 0) {
            se.number += LNZ.status_drenched_tickTimer;
            if (this.element == Element.RED) {
              this.calculateDotDamage(LNZ.status_drenched_dot, true,
                LNZ.status_drenched_damageLimit, se.damage_source);
            }
          }
          break;
        case DROWNING:
          se.number -= time_elapsed;
          if (se.number < 0) {
            se.number += LNZ.status_drowning_tickTimer;
            if (this.element == Element.BLUE) {
              this.calculateDotDamage(LNZ.status_drowning_dot, true,
                LNZ.status_drowning_damageLimitBlue, se.damage_source);
            }
            else {
              this.calculateDotDamage(LNZ.status_drowning_dot, true,
                LNZ.status_drowning_damageLimit, se.damage_source);
            }
            if (Misc.randomChance(LNZ.status_drowning_drenchedPercentage)) {
              this.refreshStatusEffect(StatusEffectCode.DRENCHED, 5000,
                se.damage_source.copy());
            }
          }
          break;
        case BURNT:
          se.number -= time_elapsed;
          if (se.number < 0) {
            se.number += LNZ.status_burnt_tickTimer;
            if (this.element == Element.RED) {
              this.calculateDotDamage(LNZ.status_burnt_dot, true,
                LNZ.status_burnt_damageLimitRed, se.damage_source);
            }
            else {
              this.calculateDotDamage(LNZ.status_burnt_dot, true,
                LNZ.status_burnt_damageLimit, se.damage_source);
            }
            if (Misc.randomChance(LNZ.status_burnt_charredPercentage)) {
              this.refreshStatusEffect(StatusEffectCode.CHARRED, 5000,
                se.damage_source.copy());
            }
          }
          break;
        case CHARRED:
          se.number -= time_elapsed;
          if (se.number < 0) {
            se.number += LNZ.status_charred_tickTimer;
            if (this.element == Element.RED) {
              this.calculateDotDamage(LNZ.status_charred_dot, true,
                LNZ.status_charred_damageLimitRed, se.damage_source);
            }
            else {
              this.calculateDotDamage(LNZ.status_charred_dot, true,
                LNZ.status_charred_damageLimit, se.damage_source);
            }
          }
          break;
        case FROZEN:
          se.number -= time_elapsed;
          if (se.number < 0) {
            se.number += LNZ.status_frozen_tickTimer;
            if (this.element == Element.ORANGE) {
              this.calculateDotDamage(LNZ.status_frozen_dot, true,
                LNZ.status_frozen_damageLimit, se.damage_source);
            }
          }
          break;
        case ROTTING:
          se.number -= time_elapsed;
          if (se.number < 0) {
            se.number += LNZ.status_rotting_tickTimer;
            if (this.element == Element.BROWN) {
              this.calculateDotDamage(LNZ.status_rotting_dot, true,
                LNZ.status_rotting_damageLimitBrown, se.damage_source);
            }
            else if (this.element == Element.BLUE) {
              this.calculateDotDamage(LNZ.status_rotting_dot, true,
                LNZ.status_rotting_damageLimitBlue, se.damage_source);
            }
            else {
              this.calculateDotDamage(LNZ.status_rotting_dot, true,
                LNZ.status_rotting_damageLimit, se.damage_source);
            }
            if (Misc.randomChance(LNZ.status_rotting_decayedPercentage)) {
              this.refreshStatusEffect(StatusEffectCode.DECAYED, 5000,
                se.damage_source.copy());
            }
          }
          break;
        case DECAYED:
          se.number -= time_elapsed;
          if (se.number < 0) {
            se.number += LNZ.status_decayed_tickTimer;
            if (this.element == Element.BROWN) {
              this.calculateDotDamage(LNZ.status_decayed_dot, true,
                LNZ.status_decayed_damageLimitBrown, se.damage_source);
            }
            else {
              this.calculateDotDamage(LNZ.status_decayed_dot, true,
                LNZ.status_decayed_damageLimit, se.damage_source);
            }
          }
          break;
        case APOSEMATIC_CAMOUFLAGE:
          if (this.visible()) {
            status_iterator.remove();
            continue;
          }
          for (Map.Entry<Integer, Unit> entryI : map.units.entrySet()) {
            if (entryI.getValue().alliance == this.alliance) {
              continue;
            }
            double distance = this.distance(entryI.getValue());
            if (distance < LNZ.ability_111_distance) {
              status_iterator.remove();
              this.addStatusEffect(StatusEffectCode.VISIBLE, 1000,
                se.damage_source.copy());
              continue;
            }
          }
          break;
        case APOSEMATIC_CAMOUFLAGEII:
          if (this.visible()) {
            status_iterator.remove();
            continue;
          }
          for (Map.Entry<Integer, Unit> entryII : map.units.entrySet()) {
            if (entryII.getValue().alliance == this.alliance) {
              continue;
            }
            double distance = this.distance(entryII.getValue());
            if (distance < LNZ.ability_116_distance) {
              status_iterator.remove();
              this.addStatusEffect(StatusEffectCode.VISIBLE, 1000,
                se.damage_source.copy());
              continue;
            }
          }
          break;
        default:
          break;
      }
    }
    // Update abilities
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      a.update(time_elapsed, this, map);
    }
    // Cast ability
    if (this.buffer_cast > -1) {
      this.cast(this.buffer_cast, map);
      this.buffer_cast = -1;
    }
    // Resolve location logic
    if (this.jumping) {
      double curr_jump_amount = time_elapsed / LNZ.unit_fallTimer;
      if (curr_jump_amount > this.jump_amount) {
        this.jumping = false;
        this.falling = true;
        this.fall_amount = 0;
        curr_jump_amount = this.jump_amount;
        this.resolveFloorHeight(map);
      }
      this.curr_height += curr_jump_amount;
      this.jump_amount -= curr_jump_amount;
    }
    else if (this.falling) {
      double curr_fall_amount = time_elapsed / LNZ.unit_fallTimer;
      if (this.fallsGracefully()) {
        curr_fall_amount *= 0.6;
      }
      this.curr_height -= curr_fall_amount;
      this.fall_amount += curr_fall_amount;
      if (this.curr_height <= this.floor_height) {
        this.curr_height = this.floor_height;
        this.fall_amount -= this.floor_height - this.curr_height;
        this.falling = false;
        this.fall_amount = Misc.round(this.fall_amount, 3);
        int no_damage_fall_amount = LNZ.unit_noDamageFallHeight + this.agility();
        if (this.fall_amount > no_damage_fall_amount && !this.fallsGracefully()) {
          this.calculateDotDamage(LNZ.unit_fallDamageMultiplier *
            (this.fall_amount - no_damage_fall_amount), true,
            new DamageSource(1));
          p.global.sounds.trigger_units("player/fall", this.coordinate.subtractR(map.view));
        }
        this.fall_amount = 0;
      }
    }
    else if (this.floor_height < this.curr_height) {
      this.falling = true;
      this.fall_amount = 0;
    }
    else {
      for (IntegerCoordinate coordinate : this.curr_squares_on) {
        GameMapSquare square = map.mapSquare(coordinate.x, coordinate.y);
        if (square == null || square.elevation(this.coordinate.subtractR(coordinate)) < this.curr_height) {
          continue;
        }
        switch(square.terrain_id) {
          case 181: // Very Shallow Water
          case 182:
            break;
          case 183: // Shallow Water
            this.refreshStatusEffect(StatusEffectCode.DRENCHED, 1000,
              new DamageSource(5));
            break;
          case 184: // Medium Water
            this.refreshStatusEffect(StatusEffectCode.DRENCHED, 2000,
              new DamageSource(5));
            if (this.swimNumber() < 1) {
              this.refreshStatusEffect(StatusEffectCode.DROWNING, 100,
                new DamageSource(5));
            }
            break;
          case 185: // Deep Water
            this.refreshStatusEffect(StatusEffectCode.DRENCHED, 3000,
              new DamageSource(5));
            if (this.swimNumber() < 2) {
              this.refreshStatusEffect(StatusEffectCode.DROWNING, 100,
                new DamageSource(5));
            }
            break;
          case 191: // Lava
            DamageSource damage_source = new DamageSource(3);
            this.refreshStatusEffect(StatusEffectCode.BURNT, 4000, damage_source);
            this.refreshStatusEffect(StatusEffectCode.CHARRED, 1000, damage_source);
            this.calculateDotDamage(time_elapsed * 0.00015, true, damage_source);
            this.damage(null, time_elapsed * 0.01, damage_source);
            break;
          default:
            break;
        }
      }
      if (this.timer_resolve_floor_height < 0) {
        this.timer_resolve_floor_height += LNZ.unit_timer_resolve_floor_height_cooldown;
        this.resolveFloorHeight(map);
      }
    }
  }

  // timers independent of curr action
  void update(int time_elapsed) {
    if (this.timer_last_damage > 0) {
      this.timer_last_damage -= time_elapsed;
    }
    if (this.timer_attackCooldown > 0) {
      if (this.frozen()) {
        this.timer_attackCooldown = this.attackCooldown();
      }
      else if (this.chilled()) {
        if (this.element == Element.CYAN) {
          this.timer_attackCooldown -= time_elapsed * LNZ.status_chilled_cooldownMultiplierCyan;
        }
        else {
          this.timer_attackCooldown -= time_elapsed * LNZ.status_chilled_cooldownMultiplier;
        }
      }
      else {
        this.timer_attackCooldown -= time_elapsed;
      }
    }
    this.timer_resolve_floor_height -= time_elapsed;
    this.timer_target_sound -= time_elapsed;
    this.timer_talk -= time_elapsed;
    if (this.timer_talk < 0) {
      this.timer_talk += LNZ.unit_timer_talk + Misc.randomInt(LNZ.unit_timer_talk);
      this.talkSound();
    }
    this.healPercent(this.passiveHeal() * time_elapsed * 0.001, true);
    this.updateItems();
  }

  void updateItems() {
    for (Map.Entry<GearSlot, Item> entry : this.gear.entrySet()) {
      if (entry.getValue() == null) {
        continue;
      }
      switch(entry.getKey()) {
        case WEAPON:
          switch(entry.getValue().ID) {
            case 2928: // cigar
              if (entry.getValue().toggled) {
                this.refreshStatusEffect(StatusEffectCode.RELAXED, 2500);
              }
              break;
            default:
              break;
          }
          break;
        default:
          break;
      }
    }
  }


  void useItem(AbstractGameMap map) {
    p.global.errorMessage("ERROR: Units cannot use Items, only Heroes can.");
  }


  void dropWeapon(AbstractGameMap map) {
    if (this.weapon() == null) {
      return;
    }
    map.addItem(new Item(p, this.weapon(), this.front()));
    this.gear.put(GearSlot.WEAPON, null);
    if (!this.ai_controlled) {
      p.global.sounds.trigger_player("player/drop", this.coordinate.subtractR(map.view));
    }
  }

  double frontX() {
    return this.coordinate.x + this.facing.x * this.xRadius() - 0.5 * this.facing.y * this.xRadius();
  }
  double frontY() {
    return this.coordinate.y + 0.5 * this.facing.x * this.yRadius() + this.facing.y * this.yRadius();
  }
  Coordinate front() {
    return new Coordinate(this.frontX(), this.frontY());
  }

  void plantSeed(AbstractGameMap map, int i, int j) {
    GameMapSquare square = map.mapSquare(i, j);
    double distance = this.distance(i + 0.5, j + 0.5);
    if (distance - 0.5 - LNZ.small_number > LNZ.feature_defaultInteractionDistance) {
      return;
    }
    Item it = this.weapon();
    if (it == null || it.remove || !it.plantable()) {
      return;
    }
    switch(it.ID) {
      case 2001: // Wapato Seed
      case 2002: // Wapato
        if (square.terrain_id != 164 && square.terrain_id != 165 && square.terrain_id != 182) {
          return;
        }
        if (square.features.size() > 0) {
          return;
        }
        Feature f = new Feature(p, it.plantFeatureId(), i, j);
        f.number = 1;
        f.timer = 90 * 60000 + Misc.randomInt(25 * 60000);
        if (it.ID == 2002) {
          f.timer = (int)(f.timer * 0.2);
        }
        it.removeStack();
        map.addFeature(f);
        break;
      case 2003: // Leek Seeds
      case 2005: // Plantain Seeds
      case 2007: // Ryegrass Seeds
      case 2008: // Barnyard Grass Seeds
      case 2009: // Nettle Seeds
        if (square.terrain_id != 164 && square.terrain_id != 165) {
          return;
        }
        if (square.features.size() > 0) {
          return;
        }
        it.removeStack();
        map.addFeature(it.plantFeatureId(), i, j);
        break;
      case 2011: // Maple Seed
      case 2012: // Black Walnut
      case 2013: // Juniper Berries
      case 2014: // Acorn
      case 2015: // Pine Cone
      case 2021: // Dandelion Seeds
      case 2960: // Branch, oak
      case 2965: // Branch, maple
      case 2966: // Branch, walnut
      case 2967: // Branch, cedar
      case 2968: // Branch, pine
        if (square.canPlantSomething()) {
          it.removeStack();
          map.addFeature(it.plantFeatureId(), i, j);
        }
        break;
      default:
        p.global.errorMessage("ERROR: Trying to plant item with code " + it.ID +
          " but no logic to plant it exists.");
        break;
    }
  }


  void target(MapObject object, AbstractGameMap map) {
    this.target(object, map, false);
  }
  void target(MapObject object, AbstractGameMap map, boolean use_item) {
    if (object == null) {
      return;
    }
    if (this.object_targeting == object) {
      return;
    }
    this.object_targeting = object;
    if (Feature.class.isInstance(object)) {
      if (use_item) {
        this.curr_action = UnitAction.TARGETING_FEATURE_WITH_ITEM;
      }
      else {
        this.curr_action = UnitAction.TARGETING_FEATURE;
      }
    }
    else if (Unit.class.isInstance(object) && !((Unit)object).untargetable()) {
      this.curr_action = UnitAction.TARGETING_UNIT;
      this.targetSound();
    }
    else if (Item.class.isInstance(object)) {
      this.curr_action = UnitAction.TARGETING_ITEM;
    }
    else {
      this.curr_action = UnitAction.NONE;
      this.object_targeting = null;
      return;
    }
    if (map != null) {
      this.startPathfindingThread(object.xCenter(), object.yCenter(), map);
    }
  }
  void target(AbstractGameMap map) { // target terrain
    if (map == null) {
      return;
    }
    if (this.curr_action == UnitAction.TARGETING_TERRAIN || this.curr_action == UnitAction.TERRAIN_INTERACTION) {
      if ((int)this.curr_action_coordinate.x == (int)map.mc.x &&
        (int)this.curr_action_coordinate.y == (int)map.mc.y) {
        return;
      }
    }
    this.curr_action = UnitAction.TARGETING_TERRAIN;
    this.curr_action_coordinate = map.mc.addR(0.5);
    this.startPathfindingThread(map.mc.addR(0.5), map);
  }

  // Aim at mouse
  void aim(Coordinate target) {
    this.curr_action = UnitAction.AIMING;
    this.object_targeting = null;
    this.curr_action_coordinate = target.copy();
  }


  void restartAbilityTimers() {
    for (Ability a : this.abilities) {
      if (a != null) {
        a.timer_cooldown = 0;
      }
    }
  }

  // Cast ability
  void bufferCast(int index) {
    if (index < 0 || index >= this.abilities.size()) {
      return;
    }
    Ability a = this.abilities.get(index);
    if (a == null) {
      return;
    }
    this.buffer_cast = index;
  }

  void cast(int index, AbstractGameMap map) {
    this.cast(index, map, null);
  }
  void cast(int index , AbstractGameMap map, MapObject secondary_target) {
    this.cast(index, map, secondary_target, false);
  }
  void cast(int index , AbstractGameMap map, MapObject secondary_target, boolean player_casting) {
    if (this.suppressed() || this.stunned() || this.silenced()) {
      return;
    }
    if (index < 0 || index >= this.abilities.size()) {
      return;
    }
    Ability a = this.abilities.get(index);
    if (a == null) {
      return;
    }
    if (a.timer_cooldown > 0) {
      return;
    }
    if (a.checkMana()) {
      if (a.manaCost() > this.currMana()) {
        if (player_casting) {
          map.addHeaderMessage("Not enough mana");
        }
        return;
      }
    }
    if (a.castsOnTarget()) {
      if (this.object_targeting == null) {
        if (secondary_target == null) {
          return;
        }
        else {
          this.object_targeting = secondary_target;
        }
      }
      if (!Unit.class.isInstance(this.object_targeting)) {
        return;
      }
      Unit u = (Unit)this.object_targeting;
      if (this.distance(u) > a.castsOnTargetRange()) {
        this.curr_action = UnitAction.CAST_WHEN_IN_RANGE;
        this.curr_action_id = index;
        return;
      }
      this.curr_action_id = index;
      if (this.map_key == 0) {
        this.curr_action_coordinate = map.mc.copy();
      }
      if (a.turnsCaster()) {
        this.face(u);
      }
      a.activate(this, map, u);
    }
    else {
      this.curr_action_id = index;
      if (this.map_key == 0) {
        this.curr_action_coordinate = map.mc.copy();
      }
      if (a.turnsCaster()) {
        if (this.object_targeting != null && this.ai_controlled) {
          this.face(this.object_targeting);
        }
        else if (!this.ai_controlled && this.map_key == 0) {
          this.face(map.mc);
        }
      }
      a.activate(this, map);
    }
  }


  // Shoot projectile
  void shoot(AbstractGameMap map) {
    if (this.weapon() == null || !this.weapon().shootable()) {
      return;
    }
    map.addProjectile(new Projectile(p, this.weapon().ID + 1000, this, this.weapon().shootInaccuracy()));
    switch(this.weapon().ID) {
      case 2118: // Chicken Egg
        p.global.sounds.trigger_units("items/throw", this.coordinate.subtractR(map.view));
        break;
      case 2301: // Slingshot
        p.global.sounds.trigger_units("items/slingshot", this.coordinate.subtractR(map.view));
        break;
      case 2311: // Recurve Bow
        p.global.sounds.trigger_units("items/recurve_bow", this.coordinate.subtractR(map.view));
        break;
      case 2312: // M1911
        p.global.sounds.trigger_units("items/m1911", this.coordinate.subtractR(map.view));
        break;
      case 2321: // War Machine
      case 2342:
      case 2331:
        p.global.sounds.trigger_units("items/war_machine", this.coordinate.subtractR(map.view));
        break;
      case 2322: // Five-Seven
      case 2343:
        p.global.sounds.trigger_units("items/five_seven", this.coordinate.subtractR(map.view));
        break;
      case 2323: // Type25
      case 2344:
        p.global.sounds.trigger_units("items/type25", this.coordinate.subtractR(map.view));
        break;
      case 2332: // FAL
        p.global.sounds.trigger_units("items/FAL", this.coordinate.subtractR(map.view));
        break;
      case 2333: // Python
      case 2354:
        p.global.sounds.trigger_units("items/python", this.coordinate.subtractR(map.view));
      case 2341: // RPG
      case 2362:
        p.global.sounds.trigger_units("items/RPG", this.coordinate.subtractR(map.view));
        break;
      case 2345: // Executioner
      case 2364:
        p.global.sounds.trigger_units("items/executioner", this.coordinate.subtractR(map.view));
        break;
      case 2351: // Galil
      case 2373:
        p.global.sounds.trigger_units("items/galil", this.coordinate.subtractR(map.view));
        break;
      case 2353: // Ballistic Knife
      case 2374:
        p.global.sounds.trigger_units("items/ballistic_knife", this.coordinate.subtractR(map.view));
        break;
      case 2352: // WN
        Projectile burst1 = new Projectile(p, this.weapon().ID + 1000, this, this.weapon().shootInaccuracy());
        Projectile burst2 = new Projectile(p, this.weapon().ID + 1000, this, this.weapon().shootInaccuracy());
        burst1.coordinate.subtract(this.facing.multiplyR(0.05));
        burst2.coordinate.subtract(this.facing.multiplyR(0.1));
        map.addProjectile(burst1);
        map.addProjectile(burst2);
        p.global.sounds.trigger_units("items/FAL", this.coordinate.subtractR(map.view));
        break;
      case 2355: // MTAR
      case 2375:
        p.global.sounds.trigger_units("items/MTAR",
          this.coordinate.subtractR(map.view));
        break;
      case 2361: // RPD
      case 2381: // Relativistic Punishment Device
        p.global.sounds.trigger_units("items/RPD",
          this.coordinate.subtractR(map.view));
        break;
      case 2363: // DSR-50
      case 2382:
        p.global.sounds.trigger_units("items/DSR50",
          this.coordinate.subtractR(map.view));
        break;
      case 2371: // HAMR
      case 2391:
        p.global.sounds.trigger_units("items/HAMR",
          this.coordinate.subtractR(map.view));
        break;
      case 2372: // Ray Gun
        p.global.sounds.trigger_units("items/ray_gun",
        this.coordinate.subtractR(map.view));
        break;
      case 2392: // Porter's X2 Ray Gun
        p.global.sounds.trigger_units("items/porters_x2_ray_gun",
          this.coordinate.subtractR(map.view));
        break;
      case 2924: // Glass Bottle
      case 2931: // Rock
      case 2932: // Arrow
      case 2933: // Pebble
        p.global.sounds.trigger_units("items/throw",
          this.coordinate.subtractR(map.view));
        break;
      case 2944: // Grenade
        p.global.sounds.trigger_units("items/grenade_throw", this.coordinate.subtractR(map.view));
        break;
      default:
        break;
    }
    this.weapon().shot();
    this.move(this.weapon().shootRecoil(), map, MoveModifier.RECOIL);
    this.timer_attackCooldown = this.attackCooldown();
  }


  // Auto attack
  void attack(Unit u, AbstractGameMap map) {
    if (this.autoAttackRanged()) {
      Projectile proj = new Projectile(p, 3005, this);
      proj.target_key = u.map_key; // makes it homing
      map.addProjectile(proj);
      p.global.sounds.trigger_units("units/attack/auto_attack_throw");
    }
    else {
      double power = this.autoAttackPower();
      DamageType damage_type = this.autoAttackDamageType();
      DamageSource damage_source = this.weapon() == null ?
        new DamageSource(11, this.ID) :
        new DamageSource(12, this.ID, this.weapon().ID);
      u.damage(this, u.calculateDamageFrom(this, power, damage_type, this.element), damage_source);
    }
    this.timer_attackCooldown = this.attackCooldown(true);
    this.attackSound();
    if (this.weapon() != null) {
      this.weapon().attacked();
    }
    switch(this.ID) {
      case 1005: // Rooster
        if (Misc.randomChance(0.2)) {
          this.stopAction();
          this.turnAround();
          this.moveForward(2, map);
        }
        break;
      case 1010: // Quail
        if (Misc.randomChance(0.3)) {
          this.stopAction();
          this.turnAround();
          this.moveForward(2, map);
        }
        break;
      case 1011: // Squirrel
        if (Misc.randomChance(0.5)) {
          this.stopAction();
          this.turnAround();
          this.moveForward(2, map);
        }
        break;
      default:
        break;
    }
  }

  boolean autoAttackRanged() {
    switch(this.ID) {
      case 1212: // zombie friar
      case 1214: // zombie priest
        return true;
      default:
        return false;
    }
  }

  double autoAttackPower() {
    double power = 0;
    switch(this.ID) {
      case 1212: // zombie friar
      case 1214: // zombie priest
        power = this.magic();
        break;
      default:
        power = this.attack();
        break;
    }
    if (this.aposematicCamouflage()) {
      power *= LNZ.ability_111_powerBuff;
      this.removeStatusEffect(StatusEffectCode.APOSEMATIC_CAMOUFLAGE);
    }
    if (this.aposematicCamouflageII()) {
      power *= LNZ.ability_116_powerBuff;
      this.removeStatusEffect(StatusEffectCode.APOSEMATIC_CAMOUFLAGEII);
    }
    return power;
  }

  DamageType autoAttackDamageType() {
    switch(this.ID) {
      case 1212: // zombie friar
      case 1214: // zombie priest
        return DamageType.MAGICAL;
      default:
        return DamageType.PHYSICAL;
    }
  }


  double calculateDamageFrom(Unit source, double power, DamageType damageType, Element element) {
    return this.calculateDamageFrom(power, damageType, element, source.piercing(), source.penetration());
  }
  double calculateDamageFrom(double power, DamageType damageType, Element element, double piercing, double penetration) {
    double effectiveDefense = 0;
    switch(damageType) {
      case PHYSICAL:
        effectiveDefense = this.defense() * (1 - piercing);
        break;
      case MAGICAL:
        effectiveDefense = this.resistance() * (1 - penetration);
        break;
      case MIXED:
        effectiveDefense = this.defense() * (1 - piercing) + this.resistance() * (1 - penetration);
        break;
      case TRUE:
        effectiveDefense = 0;
        break;
    }
    double subtotal = Math.max(0, power - effectiveDefense) * this.element.resistanceFactorTo(element);
    if (element == Element.BLUE && this.drenched()) {
      subtotal *= LNZ.status_drenched_multiplier;
    }
    if (this.sick()) {
      subtotal *= LNZ.status_sick_damageMultiplier;
    }
    if (this.diseased()) {
      subtotal *= LNZ.status_diseased_damageMultiplier;
    }
    return subtotal;
  }


  void calculateDotDamage(double percent, boolean max_health, DamageSource damage_source) {
    this.calculateDotDamage(percent, max_health, 0, damage_source);
  }
  void calculateDotDamage(double percent, boolean max_health, double damage_limit, DamageSource damage_source) {
    double damage = 0;
    if (max_health) {
      damage = percent * this.health();
    }
    else {
      damage = percent * this.curr_health;
    }
    if (damage < 0) {
      return;
    }
    double min_health = this.health() * damage_limit;
    if (this.curr_health - damage < min_health) {
      damage = this.curr_health - min_health;
    }
    this.damage(null, damage, damage_source);
  }


  void damage(Unit source, double amount, DamageSource damage_source) {
    double last_health = this.curr_health;
    if (this.remove || amount <= 0) {
      this.aiResponseToAttack(source, amount);
      return;
    }
    this.last_damage_source = damage_source;
    if (this.invulnerable()) {
      this.aiResponseToAttack(source, amount);
      return;
    }
    this.curr_health -= amount;
    if (this.unkillable()) {
      if (this.curr_health < this.statuses.get(StatusEffectCode.UNKILLABLE).number) {
        this.curr_health = this.statuses.get(StatusEffectCode.UNKILLABLE).number;
      }
    }
    if (this.curr_health <= 0) {
      this.curr_health = 0;
      this.remove = true;
      this.deathSound();
      if (this.map_key == 0) {
        p.global.profile.timeDied(damage_source);
      }
    }
    else if (source != null) {
      this.damagedSound();
    }
    this.last_damage_from = source;
    if (source != null) {
      source.damaged(this, amount);
      if (this.remove) {
        source.killed(this);
      }
    }
    if (this.headgear() != null) {
      this.headgear().lowerDurability();
    }
    if (this.chestgear() != null) {
      this.chestgear().lowerDurability();
    }
    if (this.leggear() != null) {
      this.leggear().lowerDurability();
    }
    if (this.footgear() != null) {
      this.footgear().lowerDurability();
    }
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        case 101: // Fearless Leader I
          if (this.rageOfTheBenII()) {
            this.increaseMana(LNZ.ability_101_rageGain * LNZ.ability_110_rageGainBonus);
          }
          else if (this.rageOfTheBen()) {
            this.increaseMana(LNZ.ability_101_rageGain * LNZ.ability_105_rageGainBonus);
          }
          else {
            this.increaseMana(LNZ.ability_101_rageGain);
          }
          a.timer_other = LNZ.ability_101_cooldownTimer;
          break;
        case 106: // Fearless Leader II
          if (this.rageOfTheBenII()) {
            this.increaseMana(LNZ.ability_106_rageGain * LNZ.ability_110_rageGainBonus);
          }
          else if (this.rageOfTheBen()) {
            this.increaseMana(LNZ.ability_106_rageGain * LNZ.ability_105_rageGainBonus);
          }
          else {
            this.increaseMana(LNZ.ability_106_rageGain);
          }
          a.timer_other = LNZ.ability_106_cooldownTimer;
          break;
        default:
          break;
      }
    }
    this.aiResponseToAttack(source, amount);
    this.timer_last_damage = LNZ.unit_healthbarDamageAnimationTime;
    this.last_damage_amount = last_health - this.curr_health;
  }

  void aiResponseToAttack(Unit source, double amount) {
    if (!this.ai_controlled) {
      return;
    }
    switch(this.ID) {
      case 1001: // Target Dummy
        if (source == null) {
          this.description += "\n" + amount + " damage.";
        }
        else {
          this.description += "\n" + amount + " damage from " + source.displayName() + ".";
        }
        break;
      case 1002: // Chicken
      case 1003: // Chick
        if (source == null) {
          this.faceRandom();
        }
        else {
          this.faceAway(source);
        }
        this.addStatusEffect(StatusEffectCode.RUNNING, 3000);
        this.moveForward(4, null);
        this.ai_toggle = true;
        this.timer_ai_action3 = 300;
        break;
      case 1005: // Rooster
        if (source != null && (!UnitAction.aggressiveAction(this.curr_action) || this.last_move_collision)) {
          this.target(source, null);
          this.addStatusEffect(StatusEffectCode.RUNNING, 3000);
        }
        break;
      case 1009: // Frog
        this.moveForward(3, null);
        break;
      case 1010: // Quail
        if (source != null && (!UnitAction.aggressiveAction(this.curr_action) ||
          this.last_move_collision) && Misc.randomChance(0.2)) {
          this.target(source, null);
          this.addStatusEffect(StatusEffectCode.RUNNING, 3000);
          break;
        }
        if (source == null) {
          this.faceRandom();
        }
        else {
          this.faceAway(source);
        }
        this.addStatusEffect(StatusEffectCode.RUNNING, 3000);
        this.moveForward(4, null);
        break;
      case 1011: // Squirrel
        if (source != null && (!UnitAction.aggressiveAction(this.curr_action) ||
          this.last_move_collision) && Misc.randomChance(0.8)) {
          this.target(source, null);
          this.addStatusEffect(StatusEffectCode.RUNNING, 3000);
          break;
        }
        if (source == null && (!UnitAction.aggressiveAction(this.curr_action) || this.last_move_collision)) {
          this.faceRandom();
        }
        else {
          this.faceAway(source);
        }
        this.addStatusEffect(StatusEffectCode.RUNNING, 3000);
        this.moveForward(4, null);
        break;
      case 1012: // Fawn
        if (source == null) {
          this.faceRandom();
        }
        else {
          this.faceAway(source);
        }
        this.addStatusEffect(StatusEffectCode.RUNNING, 4000);
        this.moveForward(6, null);
        this.ai_toggle = true;
        this.timer_ai_action3 = 4000;
        break;
      case 1013: // Raccoon
        if (source != null && (!UnitAction.aggressiveAction(this.curr_action) || this.last_move_collision)) {
          this.target(source, null);
          this.timer_ai_action3 = 700;
        }
        break;
      case 1014: // Doe
        this.addStatusEffect(StatusEffectCode.RUNNING, 4000);
        this.timer_ai_action2 = 4000;
        this.timer_ai_action3 = 4000;
        if (!UnitAction.aggressiveAction(this.curr_action) || this.last_move_collision) {
          if (source == null) {
            this.faceRandom();
            this.moveForward(6, null);
            break;
          }
          if (this.ai_toggle) {
            this.target(source, null);
          }
          else {
            this.faceAway(source);
            moveForward(6, null);
          }
        }
        break;
      case 1015: // Buck
        this.addStatusEffect(StatusEffectCode.RUNNING, 4000);
        this.timer_ai_action2 = 4000;
        if (source != null && (!UnitAction.aggressiveAction(this.curr_action) || this.last_move_collision)) {
          this.target(source, null);
        }
        break;
      case 1201: // Tier I Zombie
      case 1202:
      case 1203:
      case 1204:
      case 1205:
      case 1206:
      case 1207:
      case 1208:
      case 1209:
      case 1210:
      case 1291: // Auto-spawned Zombies
      case 1292:
      case 1293:
      case 1301: // Tier I named zombies
      case 1302:
      case 1303:
      case 1304:
      case 1305:
      case 1306:
      case 1307:
        if (source != null && (!UnitAction.aggressiveAction(this.curr_action) || this.last_move_collision)) {
          this.target(source, null);
        }
        break;
      default:
        break;
    }
  }


  void damaged(Unit u, double damage) {
    this.heal(Math.max(0, this.lifesteal() * damage), false);
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        case 101: // Fearless Leader I
          if (this.rageOfTheBenII()) {
            this.increaseMana(LNZ.ability_101_rageGain * LNZ.ability_110_rageGainBonus);
          }
          else if (this.rageOfTheBen()) {
            this.increaseMana(LNZ.ability_101_rageGain * LNZ.ability_105_rageGainBonus);
          }
          else {
            this.increaseMana(LNZ.ability_101_rageGain);
          }
          a.timer_other = LNZ.ability_101_cooldownTimer;
          break;
        case 106: // Fearless Leader II
          if (this.rageOfTheBenII()) {
            this.increaseMana(LNZ.ability_106_rageGain * LNZ.ability_110_rageGainBonus);
          }
          else if (this.rageOfTheBen()) {
            this.increaseMana(LNZ.ability_106_rageGain * LNZ.ability_105_rageGainBonus);
          }
          else {
            this.increaseMana(LNZ.ability_106_rageGain);
          }
          a.timer_other = LNZ.ability_106_cooldownTimer;
          break;
        default:
          break;
      }
    }
  }


  void killed(Unit u) {
    for (Ability a : this.abilities) {
      if (a == null) {
        continue;
      }
      switch(a.ID) {
        case 101: // Fearless Leader I
          if (this.rageOfTheBenII()) {
            this.increaseMana(LNZ.ability_101_rageGainKill * LNZ.ability_110_rageGainBonus);
          }
          else if (this.rageOfTheBen()) {
            this.increaseMana(LNZ.ability_101_rageGainKill * LNZ.ability_105_rageGainBonus);
          }
          else {
            this.increaseMana(LNZ.ability_101_rageGainKill);
          }
          a.timer_other = LNZ.ability_101_cooldownTimer;
          break;
        case 106: // Fearless Leader II
          if (this.rageOfTheBenII()) {
            this.increaseMana(LNZ.ability_106_rageGainKill * LNZ.ability_110_rageGainBonus);
          }
          else if (this.rageOfTheBen()) {
            this.increaseMana(LNZ.ability_106_rageGainKill * LNZ.ability_105_rageGainBonus);
          }
          else {
            this.increaseMana(LNZ.ability_106_rageGainKill);
          }
          a.timer_other = LNZ.ability_106_cooldownTimer;
          break;
        case 113: // Amphibious Leap I
          a.timer_cooldown *= LNZ.ability_113_killCooldownReduction;
          break;
        case 118: // Amphibious Leap II
          a.timer_cooldown *= LNZ.ability_118_killCooldownReduction;
          break;
        default:
          break;
      }
    }
    this.killSound();
    if (this.map_key == 0) {
      p.global.profile.unitKilled(u.ID);
    }
  }


  void destroy(AbstractGameMap map) {
    for (Item i : this.drops()) {
      map.addItem(i, this.coordinate.x - this.size + 2 * Misc.randomDouble(this.size),
        this.coordinate.y - this.size + 2 * Misc.randomDouble(this.size));
    }
    for (Map.Entry<GearSlot, Item> entry : this.gear.entrySet()) {
      this.gear.put(entry.getKey(), null);
    }
    this.statuses.clear();
    this.stopAction();
    this.restartAbilityTimers();
  }


  ArrayList<Item> drops() {
    ArrayList<Item> drops = new ArrayList<Item>();
    Item i;
    switch(this.ID) {
      case 1002: // Chicken
        if (Misc.randomChance(0.5)) {
          drops.add(new Item(p, 2116));
        }
        if (Misc.randomChance(0.5)) {
          drops.add(new Item(p, 2807));
        }
        i = new Item(p, 2126);
        i.setStack(Misc.randomInt(0, 2));
        drops.add(i);
        break;
      case 1003: // Chick
        if (Misc.randomChance(0.3)) {
          drops.add(new Item(p, 2807));
        }
        break;
      case 1004: // John Rankin
        drops.add(new Item(p, 2991));
        break;
      case 1005: // Rooster
        if (Misc.randomChance(0.7)) {
          drops.add(new Item(p, 2116));
        }
        if (Misc.randomChance(0.7)) {
          drops.add(new Item(p, 2807));
        }
        if (Misc.randomChance(0.25)) {
          drops.add(new Item(p, 2807));
        }
        i = new Item(p, 2126);
        i.setStack(Misc.randomInt(0, 2));
        drops.add(i);
        break;
      case 1006: // Father Dom
        drops.add(new Item(p, 2988));
        drops.add(new Item(p, 2928));
        break;
      case 1010: // Quail
        if (Misc.randomChance(0.3)) {
          drops.add(new Item(p, 2807));
        }
        if (Misc.randomChance(0.5)) {
          drops.add(new Item(p, 2128));
        }
        break;
      case 1011: // Sqirrel
        if (Misc.randomChance(0.1)) {
          drops.add(new Item(p, 2820));
        }
        if (Misc.randomChance(0.35)) {
          drops.add(new Item(p, 2014));
        }
        else if (Misc.randomChance(0.25)) {
          drops.add(new Item(p, 2012));
        }
        break;
      case 1012: // Fawn
        if (Misc.randomChance(0.5)) {
          drops.add(new Item(p, 2820));
        }
        if (Misc.randomChance(0.5)) {
          drops.add(new Item(p, 2130));
        }
        break;
      case 1013: // Raccoon
        if (Misc.randomChance(0.3)) {
          drops.add(new Item(p, 2820));
        }
        break;
      case 1014: // Doe
        if (Misc.randomChance(0.75)) {
          drops.add(new Item(p, 2820));
        }
        if (Misc.randomChance(0.25)) {
          drops.add(new Item(p, 2820));
        }
        drops.add(new Item(p, 2130));
        if (Misc.randomChance(0.7)) {
          drops.add(new Item(p, 2130));
        }
        if (Misc.randomChance(0.4)) {
          drops.add(new Item(p, 2130));
        }
        break;
      case 1015: // Buck
        if (Misc.randomChance(0.85)) {
          drops.add(new Item(p, 2820));
        }
        if (Misc.randomChance(0.35)) {
          drops.add(new Item(p, 2820));
        }
        drops.add(new Item(p, 2130));
        if (Misc.randomChance(0.8)) {
          drops.add(new Item(p, 2130));
        }
        if (Misc.randomChance(0.5)) {
          drops.add(new Item(p, 2130));
        }
        if (Misc.randomChance(0.5)) {
          drops.add(new Item(p, 2826));
        }
        break;
      case 1201: // Base Zombies
      case 1202:
      case 1203:
      case 1204:
      case 1205:
      case 1206:
      case 1207:
      case 1208:
      case 1209:
      case 1291:
        if (Misc.randomChance(0.2)) {
          drops.add(new Item(p, 2119));
        }
        break;
      case 1210: // Intellectual Zombie
      case 1211: // Intellectual Franny Zombie
        if (Misc.randomChance(0.2)) {
          drops.add(new Item(p, 2119));
        }
        if (Misc.randomChance(0.15)) {
          drops.add(new Item(p, 2912));
        }
        if (Misc.randomChance(0.15)) {
          drops.add(new Item(p, 2913));
        }
        break;
      case 1212: // Zombie Friar
        if (Misc.randomChance(0.2)) {
          drops.add(new Item(p, 2119));
        }
        if (Misc.randomChance(0.25)) {
          drops.add(new Item(p, 2141));
        }
        break;
      case 1213: // Zombie Professor
        if (Misc.randomChance(0.2)) {
          drops.add(new Item(p, 2119));
        }
        if (Misc.randomChance(0.25)) {
          drops.add(new Item(p, 2911));
        }
        if (Misc.randomChance(0.25)) {
          drops.add(new Item(p, 2913));
        }
        break;
      case 1214: // Zombie Priest
        if (Misc.randomChance(0.2)) {
          drops.add(new Item(p, 2119));
        }
        if (Misc.randomChance(0.35)) {
          drops.add(new Item(p, 2141));
        }
        break;
      case 1215: // Zombie Jeffy
        if (Misc.randomChance(0.2)) {
          drops.add(new Item(p, 2119));
        }
        i = new Item(p, 2941);
        i.stack = Misc.randomInt(1, 12);
        drops.add(i);
        break;
      case 1292: // Running Zombie
        if (Misc.randomChance(0.2)) {
          drops.add(new Item(p, 2119));
        }
        if (Misc.randomChance(0.1)) {
          drops.add(new Item(p, 2712));
        }
        break;
      case 1293: // Armored Zombie
        if (Misc.randomChance(0.2)) {
          drops.add(new Item(p, 2119));
        }
        if (Misc.randomChance(0.1)) {
          int ore_id = 0;
          if (this.tier() >= 9) {
            ore_id = 2872;
          }
          else if (this.tier() >= 7) {
            ore_id = 2862;
          }
          else if (this.tier() == 6) {
            ore_id = 2852;
          }
          else if (this.tier() == 5) {
            ore_id = 2842;
          }
          else if (this.tier() == 4) {
            ore_id = 2832;
          }
          else if (this.tier() == 3) {
            ore_id = 2822;
          }
          else if (this.tier() == 2) {
            ore_id = 2812;
          }
          else {
            ore_id = 2802;
          }
          drops.add(new Item(p, ore_id));
        }
        break;
      case 1309: // Alex Spieldenner
        if (this.ai_toggle) {
          i = new Item(p, 2901);
          i.ammo = 41;
          drops.add(i);
        }
        else {
          drops.add(new Item(p, 2925));
        }
        break;
      case 1351: // Cathy Heck
        i = new Item(p, 2904);
        //i.inventory.stash(new Item(p, 2903));
        drops.add(i);
        break;
      case 1353: // Ben Kohring
        i = new Item(p, 2906);
        i.ammo = 1;
        drops.add(i);
        break;
      default:
        break;
    }
    for (Map.Entry<GearSlot, Item> entry : this.gear.entrySet()) {
      if (entry.getValue() != null) {
        drops.add(entry.getValue());
      }
    }
    return drops;
  }


  void healPercent(double amount, boolean max_heath) {
    if (max_heath) {
      this.heal(amount * this.health());
    }
    else {
      this.heal(amount * (this.health() - this.curr_health));
    }
  }
  void heal(double amount) {
    this.heal(amount, false);
  }
  void heal(double amount, boolean overheal) {
    this.curr_health += amount;
    if (this.curr_health < 0) {
      this.curr_health = 0;
    }
    if (!overheal && this.curr_health > this.health()) {
      this.curr_health = this.health();
    }
  }

  void changeHealth(double amount) {
    this.setHealth(this.curr_health + amount);
  }
  void setHealth(double amount) {
    this.curr_health = amount;
    if (this.curr_health <= 0) {
      this.curr_health = 0;
      this.remove = true;
    }
    if (this.curr_health > this.health()) {
      this.curr_health = this.health();
    }
  }


  void stopAction() {
    this.stopAction(false);
  }
  void stopAction(boolean forceStop) {
    if (!forceStop && this.curr_action_unstoppable && this.curr_action != UnitAction.NONE) {
      return;
    }
    this.curr_action = UnitAction.NONE;
    this.curr_action_coordinate = this.coordinate.copy();
    this.object_targeting = null;
    this.last_move_collision = false;
    this.last_move_any_collision = false;
    this.curr_action_unhaltable = false;
    this.curr_action_unstoppable = false;
    this.curr_action_id = 0;
  }


  void turnDirection(int direction) {
    switch(direction) {
      case PConstants.LEFT:
        this.turn(-PConstants.HALF_PI);
        break;
      case PConstants.RIGHT:
        this.turn(PConstants.HALF_PI);
        break;
      default:
        p.global.errorMessage("ERROR: turn direction " + direction + " not recognized.");
        break;
    }
  }
  void turnAround() {
    this.setFacing(-this.facing.x, -this.facing.y);
  }
  void turn(double angle_change) {
    this.turnTo(this.facingA + angle_change);
  }
  void turnTo(double facingA) {
    this.facingA = facingA;
    this.facing.x = Math.cos(this.facingA);
    this.facing.y = Math.sin(this.facingA);
  }

  void faceRandom() {
    this.setFacing(1 - Misc.randomDouble(2), 1 - Misc.randomDouble(2));
  }
  void face(MapObject object) {
    this.face(object.xCenter(), object.yCenter());
  }
  void faceAway(MapObject object) {
    this.faceAway(object.xCenter(), object.yCenter());
  }
  void face(double faceX, double faceY) {
    this.setFacing(faceX - this.coordinate.x, faceY - this.coordinate.y);
  }
  void face(Coordinate coordinate) {
    this.setFacing(coordinate.subtractR(this.coordinate));
  }
  void faceAway(double faceX, double faceY) {
    this.setFacing(this.coordinate.x - faceX, this.coordinate.y - faceY);
  }
  void face(int direction) {
    switch(direction) {
      case PConstants.UP:
        this.setFacing(0, -1);
        break;
      case PConstants.DOWN:
        this.setFacing(0, 1);
        break;
      case PConstants.LEFT:
        this.setFacing(-1, 0);
        break;
      case PConstants.RIGHT:
        this.setFacing(1, 0);
        break;
      default:
        p.global.errorMessage("ERROR: face direction " + direction + " not recognized.");
        break;
    }
  }
  void setFacing(double facingX, double facingY) {
    this.setFacing(new Coordinate(facingX, facingY));
  }
  void setFacing(Coordinate new_facing) {
    double normConstant = new_facing.distance();
    if (normConstant == 0.0) {
      return; // happens when exactly on target location
    }
    this.facing = new_facing.divideR(normConstant);
    this.facingA = Math.atan2(this.facing.y, this.facing.x);
  }

  double facingAngleModifier() {
    switch(this.curr_action) {
      case ATTACKING:
        return Constants.unit_attackAnimationAngle(1 - this.timer_actionTime / this.attackTime(true));
      case CASTING:
        if (this.curr_action_id < 0 || this.curr_action_id >= this.abilities.size()) {
          break;
        }
        Ability a_casting = this.abilities.get(this.curr_action_id);
        if (a_casting == null) {
          break;
        }
        switch(a_casting.ID) {
          case 1002: // Condom Throw
            return 2 * PConstants.PI * (1.0 - a_casting.timer_other / LNZ.ability_1002_castTime);
          default:
            break;
        }
        break;
      default:
        return 0;
    }
    return 0;
  }

  double facingDif(MapObject object) { // returns dif angle between facing and object
    return this.facingDif(object.xCenter(), object.yCenter());
  }
  double facingDif(double target_x, double target_y) {
    double faceX = target_x - this.xCenter();
    double faceY = target_y - this.yCenter();
    double normConstant = Math.sqrt(faceX * faceX + faceY * faceY);
    if (normConstant == 0.0) {
      return 0; // happens when exactly on target location
    }
    faceX /= normConstant;
    faceY /= normConstant;
    return this.facingDif((double)Math.atan2(faceY, faceX));
  }
  double facingDif(double target_facingA) {
    double initial_dif = target_facingA - this.facingA;
    if (Math.abs(initial_dif) > PConstants.PI) {
      initial_dif = (PConstants.PI - Math.abs(initial_dif)) * initial_dif / initial_dif;
    }
    return initial_dif;
  }


  void jump(AbstractGameMap map) {
    this.resolveFloorHeight(map);
    if (!this.falling && LNZApplet.round(this.curr_height) == this.floor_height) {
      this.jump_amount = this.jumpHeight();
      this.jumping = true;
    }
    this.resolveFloorHeight(map);
  }

  int jumpHeight() {
    switch(this.agility()) {
      case 0:
        return 0;
      case 1:
        return 2;
      case 2:
        return 3;
      case 3:
        return 4;
      case 4:
        return 5;
      case 5:
        return 6;
    }
    return 0;
  }

  // Unit falls slowly and takes no fall damage
  boolean fallsGracefully() {
    switch(this.ID) {
      case 1002: // chicken
      case 1005: // rooster
        return true;
      default:
        return false;
    }
  }

  int walkHeight() {
    if (this.falling || this.jumping) {
      return 0;
    }
    switch(this.agility()) {
      case 0:
        return 0;
      case 1:
        return 1;
      case 2:
        return 1;
      case 3:
        return 2;
      case 4:
        return 2;
      case 5:
        return 2;
    }
    return 0;
  }


  void walkSound(int terrain_id) {
    if (!this.in_view || this.last_move_collision || this.sneaking()) {
      return;
    }
    // custom walk sounds
    switch(this.ID) {
      case 1001: // Target Dummy
      case 1009: // Frog
      case 1011: // Squirrel
        return;
      case 1002: // Chicken
      case 1003: // Chick
      case 1005: // Rooster
      case 1010: // Quail
      case 1013: // Raccoon
        p.global.sounds.trigger_units("units/walk/chicken");
        return;
      default:
        break;
    }
    // default walk sounds
    switch(terrain_id) {
      case 111:
      case 112:
      case 113:
        p.global.sounds.trigger_units("player/walk_wood");
        break;
      case 131:
      case 132:
      case 133:
      case 135:
      case 171:
      case 172:
      case 173:
      case 174:
      case 175:
      case 176:
      case 177:
      case 178:
      case 179:
      case 313:
      case 314:
      case 315:
      case 316:
      case 317:
      case 318:
      case 319:
      case 320:
      case 321:
      case 322:
      case 323:
      case 401:
      case 501:
      case 601:
      case 701:
      case 801:
        p.global.sounds.trigger_units("player/walk_hard");
        break;
      case 134:
        p.global.sounds.trigger_units("player/walk_gravel");
        break;
      case 141:
      case 142:
      case 143:
      case 144:
      case 145:
        p.global.sounds.trigger_units("player/walk_sand");
        break;
      case 151:
      case 152:
      case 153:
      case 154:
      case 155:
      case 156:
        p.global.sounds.trigger_units("player/walk_grass");
        break;
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 402:
      case 502:
      case 602:
      case 702:
      case 802:
        p.global.sounds.trigger_units("player/walk_dirt");
        break;
      case 181:
      case 182:
        p.global.sounds.trigger_units("player/walk_water_very_shallow");
        break;
      case 183:
        p.global.sounds.trigger_units("player/walk_water_shallow");
        break;
      case 184:
        p.global.sounds.trigger_units("player/walk_water_medium");
        break;
      case 185:
        p.global.sounds.trigger_units("player/walk_water_deep");
        break;
      default:
        p.global.sounds.trigger_units("player/walk_default");
        break;
    }
  }

  void talkSound() {
    if (!this.in_view) {
      return;
    }
    String sound_name = "units/talk/";
    switch(this.ID) {
      case 1002: // Chicken
      case 1003: // Chick
      case 1005: // Rooster
        sound_name += "chicken" + Misc.randomInt(1, 3);
        break;
      case 1009: // Frog
        sound_name += "frog" + Misc.randomInt(1, 3);
        break;
      case 1010: // Quail
        sound_name += "quail" + Misc.randomInt(1, 3);
        break;
      case 1011: // Squirrel
        sound_name += "squirrel" + Misc.randomInt(1, 3);
        break;
      case 1013: // Raccoon
        sound_name += "raccoon" + Misc.randomInt(1, 4);
        break;
      case 1201: // Zombies
      case 1202:
      case 1203:
      case 1204:
      case 1205:
      case 1206:
      case 1207:
      case 1208:
      case 1209:
      case 1210:
      case 1291: // Auto-spawned Zombies
      case 1292:
      case 1293:
      case 1301: // Named Zombies
      case 1302:
      case 1303:
      case 1304:
      case 1305:
      case 1306:
      case 1307:
      case 1308:
        sound_name += "zombie" + Misc.randomInt(0, 5);
        break;
      default:
        return;
    }
    p.global.sounds.trigger_units(sound_name);
  }

  void targetSound() {
    if (!this.in_view) {
      return;
    }
    if (this.timer_target_sound > 0) {
      return;
    }
    if (this.ai_controlled) {
      this.timer_target_sound = 2 * LNZ.unit_timer_target_sound +
        2 * Misc.randomInt(LNZ.unit_timer_target_sound);
    }
    else {
      this.timer_target_sound = LNZ.unit_timer_target_sound +
        Misc.randomInt(LNZ.unit_timer_target_sound);
    }
    String sound_name = "units/target/";
    switch(this.ID) {
      case 1005: // Rooster
        sound_name += "rooster";
        break;
      case 1011: // Squirrel
        sound_name += "squirrel";
        break;
      case 1013: // Raccoon
        sound_name += "raccoon";
        break;
      case 1201: // Zombies
      case 1202:
      case 1203:
      case 1204:
      case 1205:
      case 1206:
      case 1207:
      case 1208:
      case 1209:
      case 1210:
      case 1291: // Auto-spawned Zombies
      case 1292:
      case 1293:
      case 1301: // Named Zombies
      case 1302:
      case 1303:
      case 1304:
      case 1305:
      case 1306:
      case 1307:
      case 1308:
        sound_name += "zombie" + Misc.randomInt(0, 8);
        break;
      default:
        return;
    }
    p.global.sounds.trigger_units(sound_name);
  }

  void attackSound() {
    if (!this.in_view) {
      return;
    }
    if (this.weapon() != null) {
      this.weapon().attackSound();
      return;
    }
    String sound_name = "units/attack/";
    switch(this.ID) {
      case 1005: // Rooster
        sound_name += "rooster";
        break;
      case 1010: // Quail
        sound_name += "quail";
        break;
      case 1011: // Squirrel
        sound_name += "squirrel";
        break;
      case 1013: // Raccoon
        sound_name += "raccoon";
        break;
      case 1201: // Campaign Zombies
      case 1202:
      case 1203:
      case 1204:
      case 1205:
      case 1206:
      case 1207:
      case 1208:
      case 1209:
      case 1210:
      case 1211:
      case 1212:
      case 1213:
      case 1214:
      case 1215:
      case 1291: // Auto-spawned Zombies
      case 1292:
      case 1293:
      case 1301: // Named Zombies
      case 1302:
      case 1303:
      case 1304:
      case 1305:
      case 1306:
      case 1307:
      case 1308:
        sound_name += "zombie" + Misc.randomInt(0, 15);
        break;
      case 1351:
        sound_name += "heck" + Misc.randomInt(1, 5);
        break;
      default:
        sound_name += "default";
        break;
    }
    p.global.sounds.trigger_units(sound_name);
  }

  void damagedSound() {
    if (!this.in_view) {
      return;
    }
    String sound_name = "units/damaged/";
    switch(this.ID) {
      case 1002: // Chicken
      case 1003: // Chick
      case 1005: // Rooster
        sound_name += "chicken" + Misc.randomInt(1, 2);
        break;
      case 1009: // Frog
        sound_name += "frog";
        break;
      case 1010: // Quail
        sound_name += "quail";
        break;
      case 1011: // Squirrel
        sound_name += "squirrel";
        break;
      case 1013: // Raccoon
        sound_name += "raccoon" + Misc.randomInt(1, 2);
        break;
      case 1201: // Zombies
      case 1202:
      case 1203:
      case 1204:
      case 1205:
      case 1206:
      case 1207:
      case 1208:
      case 1209:
      case 1210:
      case 1291: // Auto-spawned Zombies
      case 1292:
      case 1293:
      case 1301: // Named Zombies
      case 1302:
      case 1303:
      case 1304:
      case 1305:
      case 1306:
      case 1307:
      case 1308:
        sound_name += "zombie" + Misc.randomInt(0, 11);
        break;
      default:
        return;
    }
    p.global.sounds.trigger_units(sound_name);
  }

  void deathSound() {
    if (!this.in_view) {
      return;
    }
    String sound_name = "units/death/";
    switch(this.ID) {
      case 1002: // Chicken
      case 1003: // Chick
      case 1005: // Rooster
        sound_name += "chicken" + Misc.randomInt(1, 2);
        break;
      case 1009: // Frog
        sound_name += "frog";
        break;
      case 1010: // Quail
        sound_name += "quail";
        break;
      case 1011: // Squirrel
        sound_name += "squirrel";
        break;
      case 1013: // Raccoon
        sound_name += "raccoon";
        break;
      case 1201: // Zombies
      case 1202:
      case 1203:
      case 1204:
      case 1205:
      case 1206:
      case 1207:
      case 1208:
      case 1209:
      case 1210:
      case 1291: // Auto-spawned Zombies
      case 1292:
      case 1293:
      case 1301: // Named Zombies
      case 1302:
      case 1303:
      case 1304:
      case 1305:
      case 1306:
      case 1307:
      case 1308:
        sound_name += "zombie" + Misc.randomInt(0, 10);
        break;
      default:
        return;
    }
    p.global.sounds.trigger_units(sound_name);
  }

  void killSound() {
    if (!this.in_view) {
      return;
    }
    String sound_name = "units/kill/";
    switch(this.ID) {
      case 1201: // Zombies
      case 1202:
      case 1203:
      case 1204:
      case 1205:
      case 1206:
      case 1207:
      case 1208:
      case 1209:
      case 1210:
      case 1291: // Auto-spawned Zombies
      case 1292:
      case 1293:
      case 1301: // Named Zombies
      case 1302:
      case 1303:
      case 1304:
      case 1305:
      case 1306:
      case 1307:
      case 1308:
        sound_name += "zombie" + Misc.randomInt(0, 6);
        break;
      default:
        return;
    }
    p.global.sounds.trigger_units(sound_name);
  }

  void tauntSound() {
    if (!this.in_view) {
      return;
    }
    String sound_name = "units/taunt/";
    switch(this.ID) {
      case 1101: // Ben Nelson
        break;
      case 1102: // Dan Gray
        break;
      default:
        return;
    }
    p.global.sounds.trigger_units(sound_name);
  }

  void moveForward(double distance, AbstractGameMap map) {
    Coordinate target = this.coordinate.addR(this.facing.multiplyR(distance));
    this.moveTo(target, map);
  }

  void moveTo(double x, double y, AbstractGameMap map) {
    this.moveTo(new Coordinate(x, y), map);
  }
  void moveTo(Coordinate target, AbstractGameMap map) {
    if (this.curr_action == UnitAction.USING_ITEM || this.curr_action == UnitAction.MOVING_AND_USING_ITEM) {
      this.curr_action = UnitAction.MOVING_AND_USING_ITEM;
    }
    else {
      this.curr_action = UnitAction.MOVING;
    }
    if (map != null && (this.ai_controlled || !p.global.holding_ctrl)) {
      this.startPathfindingThread(target, map);
    }
    this.object_targeting = null;
    this.last_move_collision = false;
    this.last_move_any_collision = false;
    this.curr_action_coordinate = target.copy();
    this.curr_action_id = 0;
  }

  void move(double time_elapsed, AbstractGameMap map, MoveModifier modifier) {
    // remove camouflage
    if (this.aposematicCamouflage()) {
      this.removeStatusEffect(StatusEffectCode.APOSEMATIC_CAMOUFLAGE);
    }
    if (this.aposematicCamouflageII()) {
      this.removeStatusEffect(StatusEffectCode.APOSEMATIC_CAMOUFLAGEII);
    }
    // calculate attempted move distances
    double seconds = time_elapsed / 1000.0;
    double effectiveDistance = 0;
    switch(modifier) {
      case NONE:
        effectiveDistance = this.speed() * this.terrainSpeedMultiplier(map) * seconds;
        break;
      case SNEAK:
        effectiveDistance = LNZ.unit_sneakSpeed * seconds;
        break;
      case RECOIL:
        effectiveDistance = -time_elapsed; // just use time_elapsed as the distance
        break;
      case AMPHIBIOUS_LEAP:
        effectiveDistance = LNZ.ability_113_jumpSpeed * seconds;
        this.curr_height += LNZ.ability_113_jumpHeight;
        break;
      case ANURAN_APPETITE:
        effectiveDistance = LNZ.ability_115_regurgitateSpeed * seconds;
        break;
    }
    Coordinate tryMove = this.facing.multiplyR(effectiveDistance);
    Coordinate start = this.coordinate.copy();
    this.last_move_collision = false;
    this.last_move_any_collision = false;
    // move in x direction
    this.moveX(tryMove, map);
    // move in y direction
    this.moveY(tryMove, map);
    // calculates squares_on and height
    this.curr_squares_on = this.getSquaresOn();
    this.resolveFloorHeight(map);
    this.timer_resolve_floor_height = LNZ.unit_timer_resolve_floor_height_cooldown;
    // move stat
    Coordinate moved = this.coordinate.subtractR(start);
    IntegerCoordinate start_grid = new IntegerCoordinate(start);
    IntegerCoordinate curr_grid = new IntegerCoordinate(this.coordinate);
    if (!start_grid.equals(curr_grid)) {
      GameMapSquare start_square = map.mapSquare(start_grid);
      if (start_square != null) {
        start_square.removeUnit(this);
      }
      GameMapSquare curr_square = map.mapSquare(curr_grid);
      if (curr_square != null) {
        curr_square.addUnit(this);
      }
    }
    this.last_move_distance = moved.distance();
    this.footgear_durability_distance -= this.last_move_distance;
    if (this.footgear_durability_distance < 0) {
      this.footgear_durability_distance += LNZ.unit_footgearDurabilityDistance;
      if (this.footgear() != null) {
        this.footgear().lowerDurability();
      }
    }
    if (this.map_key == 0) {
      p.global.profile.walkedDistance(this.last_move_distance);
    }
  }

  double terrainSpeedMultiplier(AbstractGameMap map) {
    try {
      return map.mapSquare(this.coordinate).speedMultiplier(this);
    } catch(NullPointerException e) {
      return 1;
    }
  }

  void moveX(Coordinate tryMove, AbstractGameMap map) {
    if (tryMove.x == 0) {
      return;
    }
    double originalTryMoveX = tryMove.x;
    double moveCapRatio = LNZ.map_moveLogicCap / tryMove.x;
    double equivalentMoveY = moveCapRatio * tryMove.y;
    while(Math.abs(tryMove.x) > LNZ.map_moveLogicCap) {
      if (tryMove.x > 0) {
        if (this.collisionLogicX(LNZ.map_moveLogicCap, equivalentMoveY, map)) {
          this.last_move_collision = true;
          this.last_move_any_collision = true;
          return;
        }
        tryMove.x -= LNZ.map_moveLogicCap;
      }
      else {
        if (this.collisionLogicX(-LNZ.map_moveLogicCap, -equivalentMoveY, map)) {
          this.last_move_collision = true;
          this.last_move_any_collision = true;
          return;
        }
        tryMove.x += LNZ.map_moveLogicCap;
      }
    }
    moveCapRatio = tryMove.x / originalTryMoveX;
    equivalentMoveY = moveCapRatio * tryMove.y;
    if (this.collisionLogicX(tryMove.x, equivalentMoveY, map)) {
      this.last_move_collision = true;
      this.last_move_any_collision = true;
      return;
    }
    if (Math.abs(this.facing.x) < LNZ.unit_small_facing_threshhold) {
      this.last_move_collision = true;
      this.last_move_any_collision = true;
    }
  }

  void moveY(Coordinate tryMove, AbstractGameMap map) {
    if (tryMove.y == 0) {
      return;
    }
    double originalTryMoveY = tryMove.y;
    double moveCapRatio = LNZ.map_moveLogicCap / tryMove.y;
    double equivalentMoveX = moveCapRatio * tryMove.x;
    while(Math.abs(tryMove.y) > LNZ.map_moveLogicCap) {
      if (tryMove.y > 0) {
        if (this.collisionLogicY(LNZ.map_moveLogicCap, equivalentMoveX, map)) {
          this.last_move_any_collision = true;
          return;
        }
        tryMove.y -= LNZ.map_moveLogicCap;
      }
      else {
        if (this.collisionLogicY(-LNZ.map_moveLogicCap, equivalentMoveX, map)) {
          this.last_move_any_collision = true;
          return;
        }
        tryMove.y += LNZ.map_moveLogicCap;
      }
    }
    moveCapRatio = tryMove.y / originalTryMoveY;
    equivalentMoveX = moveCapRatio * tryMove.x;
    if (this.collisionLogicY(tryMove.y, equivalentMoveX, map)) {
      this.last_move_any_collision = true;
      return;
    }
    if (Math.abs(this.facing.y) > LNZ.unit_small_facing_threshhold) {
      this.last_move_collision = false;
    }
  }

  // returns true if collision occurs
  boolean collisionLogicX(double tryMoveX, double equivalentMoveY, AbstractGameMap map) {
    double startX = this.coordinate.x;
    this.coordinate.x += tryMoveX;
    // map collisions
    if (!this.inMapX(map.mapXI(), map.mapXF())) {
      this.coordinate.x = startX;
      return true;
    }
    if (this.ghosting()) {
      return false; // TODO: Need a way to increase height when ghosting maybe ?
    }
    // terrain collisions
    List<IntegerCoordinate> squares_moving_on = this.getSquaresOn();
    double new_height = this.curr_height;
    int max_height = (int)Math.round(Unit.this.curr_height) + this.walkHeight();
    for (IntegerCoordinate coordinate : squares_moving_on) {
      int coordinate_height = map.heightOfSquare(coordinate, this.coordinate);
      if (coordinate_height <= max_height) {
        if (coordinate_height > new_height) {
          new_height = coordinate_height;
        }
        continue;
      }
      if (!this.currentlyOn(coordinate)) {
        this.coordinate.x = startX;
        return true;
      }
      double s_x = coordinate.x + 0.5;
      if ( (this.coordinate.x > s_x && this.facing.x > 0) ||
        (this.coordinate.x < s_x && this.facing.x < 0) ) {
        continue;
      }
      double s_y = coordinate.y + 0.5;
      double original_xDif = s_x - startX;
      double original_yDif = s_y - this.coordinate.y;
      double original_distance = Math.sqrt(original_xDif * original_xDif + original_yDif * original_yDif);
      double new_xDif = s_x - this.coordinate.x;
      double new_yDif = s_y - this.coordinate.y - equivalentMoveY;
      double new_distance = Math.sqrt(new_xDif * new_xDif + new_yDif * new_yDif);
      if (new_distance > original_distance) {
        continue;
      }
      this.coordinate.x = startX;
      return true;
    }
    // unit collisions
    if (!this.uncollidable()) {
      for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
        if (entry.getKey() == this.map_key) {
          continue;
        }
        Unit u = entry.getValue();
        if (u.uncollidable()) {
          continue;
        }
        if (this.zf() <= u.zi() || u.zf() <= this.zi()) {
          continue;
        }
        double distance_to = this.collisionDistance(u);
        if (distance_to > 0) {
          continue;
        }
        if ( (this.coordinate.x > u.coordinate.x && this.facing.x > 0) ||
          (this.coordinate.x < u.coordinate.x && this.facing.x < 0) ) {
          continue;
        }
        this.coordinate.x = startX;
        return true;
      }
    }
    this.curr_height = new_height;
    return false;
  }

  // returns true if collision occurs
  boolean collisionLogicY(double tryMoveY, double equivalentMoveX, AbstractGameMap map) {
    double startY = this.coordinate.y;
    this.coordinate.y += tryMoveY;
    // map collisions
    if (!this.inMapY(map.mapYI(), map.mapYF())) {
      this.coordinate.y = startY;
      return true;
    }
    if (this.ghosting()) {
      return false;
    }
    // terrain collisions
    List<IntegerCoordinate> squares_moving_on = this.getSquaresOn();
    double new_height = this.curr_height;
    int max_height = (int)Math.round(Unit.this.curr_height) + this.walkHeight();
    for (IntegerCoordinate coordinate : squares_moving_on) {
      int coordinate_height = map.heightOfSquare(coordinate, this.coordinate);
      if (coordinate_height <= max_height) {
        if (coordinate_height > new_height) {
          new_height = coordinate_height;
        }
        continue;
      }
      if (!this.currentlyOn(coordinate)) {
        this.coordinate.y = startY;
        return true;
      }
      double s_y = coordinate.y + 0.5;
      if ( (this.coordinate.y > s_y && this.facing.y > 0) ||
        (this.coordinate.y < s_y && this.facing.y < 0) ) {
        continue;
      }
      double s_x = coordinate.x + 0.5;
      double original_xDif = s_x - this.coordinate.x;
      double original_yDif = s_y - startY;
      double original_distance = Math.sqrt(original_xDif * original_xDif + original_yDif * original_yDif);
      double new_xDif = s_x - this.coordinate.x - equivalentMoveX;
      double new_yDif = s_y - this.coordinate.y;
      double new_distance = Math.sqrt(new_xDif * new_xDif + new_yDif * new_yDif);
      if (new_distance > original_distance) {
        continue;
      }
      this.coordinate.y = startY;
      return true;
    }
    // unit collisions
    if (!this.uncollidable()) {
      for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
        if (entry.getKey() == this.map_key) {
          continue;
        }
        Unit u = entry.getValue();
        if (u.uncollidable()) {
          continue;
        }
        if (this.zf() <= u.zi() || u.zf() <= this.zi()) {
          continue;
        }
        double distance_to = this.collisionDistance(u);
        if (distance_to > 0) {
          continue;
        }
        if ( (this.coordinate.y > u.coordinate.y && this.facing.y > 0) ||
          (this.coordinate.y < u.coordinate.y && this.facing.y < 0) ) {
          continue;
        }
        this.coordinate.y = startY;
        return true;
      }
    }
    this.curr_height = new_height;
    return false;
  }

  double collisionDistance(Unit u) {
    if (this.alliance == u.alliance || (this.type.equals("Gaia") && u.type.equals("Gaia"))) {
      double xDistance = Math.max(0, Math.abs(this.coordinate.x - u.coordinate.x) - 0.2 * (this.size + u.size));
      double yDistance = Math.max(0, Math.abs(this.coordinate.y - u.coordinate.y) - 0.2 * (this.size + u.size));
      return Math.sqrt(xDistance * xDistance + yDistance * yDistance);
    }
    return this.distance(u);
  }


  void resolveFloorHeight(AbstractGameMap map) {
    this.floor_height = map.maxHeightOfSquares(this.curr_squares_on, this.coordinate);
    this.unit_height = -100;
    for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
      if (entry.getKey() == this.map_key) {
        continue;
      }
      Unit u = entry.getValue();
      double distance_to = this.distance(u);
      if (distance_to > LNZ.small_number) {
        continue;
      }
      if (u.zf() > this.zi()) {
        continue;
      }
      if (u.zf() > this.unit_height) {
        this.unit_height = u.zf();
      }
    }
    if (this.unit_height > this.floor_height) {
      this.floor_height = (int)Math.round(this.unit_height);
    }
    if (this.unit_height > map.max_height) {
      this.unit_height = map.max_height;
    }
    if (this.floor_height > map.max_height) {
      this.floor_height = map.max_height;
    }
    if (this.curr_height > map.max_height) {
      this.curr_height = map.max_height;
    }
  }


  HashSet<IntegerCoordinate> getSquaresSight(AbstractGameMap map) {
    HashSet<IntegerCoordinate> squares_sight = new HashSet<IntegerCoordinate>();
    double unit_sight = this.sight();
    double inner_square_distance = LNZ.inverse_root_two * unit_sight;
    boolean walls_dont_block = false; // feature flag to make walls not block
    double see_around_cutoff = LNZ.inverse_root_two + LNZ.small_number;
    double see_around_blocked_cutoff = 2 * see_around_cutoff;
    double see_around_distance = see_around_cutoff - this.size;
    for (int i = (int)Math.floor(this.coordinate.x - unit_sight) - 1; i <= (int)Math.ceil(this.coordinate.x + unit_sight); i++) {
      for (int j = (int)Math.floor(this.coordinate.y - unit_sight) - 1; j <= (int)Math.ceil(this.coordinate.y + unit_sight); j++) {
        double distanceX = Math.abs(i + 0.5 - this.coordinate.x);
        double distanceY = Math.abs(j + 0.5 - this.coordinate.y);
        if ( (distanceX < inner_square_distance && distanceY < inner_square_distance) ||
          (Math.sqrt(distanceX * distanceX + distanceY * distanceY) < unit_sight) ) {
          if (walls_dont_block) {
            squares_sight.add(new IntegerCoordinate(i, j));
            continue;
          }
          boolean add_square = true;
          int xi = LNZApplet.round(Math.min(Math.floor(this.xi() + LNZ.small_number), i));
          int yi = LNZApplet.round(Math.min(Math.floor(this.yi() + LNZ.small_number), j));
          int xf = LNZApplet.round(Math.max(Math.floor(this.xf() - LNZ.small_number), i));
          int yf = LNZApplet.round(Math.max(Math.floor(this.yf() - LNZ.small_number), j));
          int my_x = (int)Math.floor(this.coordinate.x);
          int my_y = (int)Math.floor(this.coordinate.y);
          double left_blocked = 2 * LNZ.inverse_root_two;
          double right_blocked = 2 * LNZ.inverse_root_two;
          for (int a = xi; a <= xf; a++) {
            for (int b = yi; b <= yf; b++) {
              if (a == i && b == j) {
                continue;
              }
              if (a == my_x && b == my_y) {
                continue;
              }
              try {
                if (map.mapSquare(a, b).passesVision()) {
                  continue;
                }
                double p_x = a + 0.5;
                double p_y = b + 0.5;
                double x_dif = i + 0.5 - this.coordinate.x;
                double y_dif = j + 0.5 - this.coordinate.y;
                double area_parrallelogram = x_dif * (p_y - this.coordinate.y) - y_dif * (p_x - this.coordinate.x);
                boolean left_side = true;
                if (area_parrallelogram < 0) {
                  left_side = false;
                  area_parrallelogram = Math.abs(area_parrallelogram);
                }
                double line_segment_distance = Math.sqrt(x_dif * x_dif + y_dif * y_dif);
                double distance_from_line = area_parrallelogram / line_segment_distance;
                if (distance_from_line < see_around_distance) {
                  add_square = false;
                  break;
                }
                else if (distance_from_line < see_around_blocked_cutoff) {
                  if (left_side) {
                    if (distance_from_line < left_blocked) {
                      left_blocked = distance_from_line;
                    }
                  }
                  else {
                    if (distance_from_line < right_blocked) {
                      right_blocked = distance_from_line;
                    }
                  }
                  if (left_blocked + right_blocked < see_around_blocked_cutoff) {
                    add_square = false;
                    break;
                  }
                }
              } catch(Exception e) {}
            }
            if (!add_square) {
              break;
            }
          }
          if (add_square) {
            squares_sight.add(new IntegerCoordinate(i, j));
          }
        }
      }
    }
    return squares_sight;
  }

  // Only checks terrain collisions, not currently used
  boolean willCollideMovingTo(double target_x, double target_y, AbstractGameMap map) {
    int xi = (int)Math.round(Math.min(Math.floor(this.xi() + LNZ.small_number), target_x));
    int yi = (int)Math.round(Math.min(Math.floor(this.yi() + LNZ.small_number), target_y));
    int xf = (int)Math.round(Math.max(Math.floor(this.xf() - LNZ.small_number), target_x));
    int yf = (int)Math.round(Math.max(Math.floor(this.yf() - LNZ.small_number), target_y));
    for (int a = xi; a <= xf; a++) {
      for (int b = yi; b <= yf; b++) {
      }
    }
    return false;
  }


  boolean canMoveUp(int height_difference) {
    if (height_difference > this.walkHeight()) {
      return false;
    }
    return true;
  }

  boolean currentlyOn(IntegerCoordinate coordinate) {
    for (IntegerCoordinate coordinate_on : this.curr_squares_on) {
      if (coordinate_on.equals(coordinate)) {
        return true;
      }
    }
    return false;
  }


  void aiLogic(int time_elapsed, AbstractGameMap map) {
    switch(this.ID) {
      case 1002: // Chicken
        if (this.curr_action == UnitAction.NONE || this.last_move_any_collision) {
          this.curr_action = UnitAction.NONE;
          this.timer_ai_action1 -= time_elapsed;
          if (this.timer_ai_action3 > 0) {
            this.timer_ai_action3 -= time_elapsed;
            if (this.timer_ai_action3 <= 0) {
              this.ai_toggle = false;
            }
          }
          if (this.timer_ai_action1 < 0) {
            double other_chicken_face_x = 0;
            double other_chicken_face_y = 0;
            int other_chickens_moved_from = 0;
            if (!this.ai_toggle) {
              this.timer_ai_action3 = 0;
              for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
                if (entry.getKey() == this.map_key) {
                  continue;
                }
                Unit u = entry.getValue();
                if (u.ID != 1002 && u.ID != 1003) {
                  continue;
                }
                if (!u.ai_toggle) {
                  continue;
                }
                if (Misc.randomChance(0.3)) {
                  continue;
                }
                if (this.distance(u) > this.sight()) {
                  continue;
                }
                this.ai_toggle = true;
                this.timer_ai_action3 = (int)(0.5 * (this.timer_ai_action3 + u.timer_ai_action3));
                other_chickens_moved_from++;
                other_chicken_face_x += u.facing.x;
                other_chicken_face_y += u.facing.y;
              }
            }
            this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(LNZ.ai_chickenTimer1);
            if (other_chickens_moved_from > 0) {
              other_chicken_face_x /= other_chickens_moved_from;
              other_chicken_face_y /= other_chickens_moved_from;
              this.refreshStatusEffect(StatusEffectCode.RUNNING, 0.8 * LNZ.ai_chickenTimer1);
              this.moveTo(this.coordinate.x + other_chicken_face_x * LNZ.ai_chickenMoveDistance +
                Misc.randomDouble(-1, 1), other_chicken_face_y * LNZ.ai_chickenMoveDistance +
                Misc.randomDouble(-1, 1), map);
            }
            else {
              this.moveTo(this.coordinate.x + LNZ.ai_chickenMoveDistance - 2 * Misc.randomDouble(LNZ.ai_chickenMoveDistance),
                this.coordinate.y + LNZ.ai_chickenMoveDistance - 2 * Misc.randomDouble(LNZ.ai_chickenMoveDistance), map);
            }
          }
        }
        this.timer_ai_action2 -= time_elapsed;
        if (this.timer_ai_action2 < 0) {
          this.timer_ai_action2 = (int)(LNZ.ai_chickenTimer2 + Misc.randomDouble(LNZ.ai_chickenTimer2));
          Item egg_item = new Item(p, 2118, this.coordinate.x, this.coordinate.y);
          if (this.fertilized() || Misc.randomChance(0.2)) {
            egg_item.toggled = true;
          }
          map.addItem(egg_item);
          if (this.in_view) {
            p.global.sounds.trigger_units("units/other/chicken_lay_egg");
          }
        }
        break;
      case 1003: // Chick
        if (this.curr_action == UnitAction.NONE || this.last_move_any_collision) {
          this.timer_ai_action1 -= time_elapsed;
          if (this.timer_ai_action3 > 0) {
            this.timer_ai_action3 -= time_elapsed;
            if (this.timer_ai_action3 <= 0) {
              this.ai_toggle = false;
            }
          }
          if (this.timer_ai_action1 < 0) {
            double other_chicken_face_x = 0;
            double other_chicken_face_y = 0;
            int other_chickens_moved_from = 0;
            if (!this.ai_toggle) {
              this.timer_ai_action3 = 0;
              for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
                if (entry.getKey() == this.map_key) {
                  continue;
                }
                Unit u = entry.getValue();
                if (u.ID != 1002 && u.ID != 1003) {
                  continue;
                }
                if (!u.ai_toggle) {
                  continue;
                }
                if (Misc.randomChance(0.1)) {
                  continue;
                }
                if (this.distance(u) > this.sight()) {
                  continue;
                }
                this.ai_toggle = true;
                this.timer_ai_action3 = (int)(0.5 * (this.timer_ai_action3 + u.timer_ai_action3));
                other_chickens_moved_from++;
                other_chicken_face_x += u.facing.x;
                other_chicken_face_y += u.facing.y;
              }
            }
            this.timer_ai_action1 = (int)(LNZ.ai_chickenTimer1 + Misc.randomDouble(LNZ.ai_chickenTimer1));
            if (other_chickens_moved_from > 0) {
              other_chicken_face_x /= other_chickens_moved_from;
              other_chicken_face_y /= other_chickens_moved_from;
              this.refreshStatusEffect(StatusEffectCode.RUNNING, 0.8 * LNZ.ai_chickenTimer1);
              this.moveTo(this.coordinate.x + other_chicken_face_x * LNZ.ai_chickenMoveDistance +
                Misc.randomDouble(-1, 1), other_chicken_face_y * LNZ.ai_chickenMoveDistance +
                Misc.randomDouble(-1, 1), map);
            }
            else {
              this.moveTo(this.coordinate.x + LNZ.ai_chickenMoveDistance - 2 * Misc.randomDouble(LNZ.ai_chickenMoveDistance),
                this.coordinate.y + LNZ.ai_chickenMoveDistance - 2 * Misc.randomDouble(LNZ.ai_chickenMoveDistance), map);
            }
          }
        }
        this.timer_ai_action2 -= time_elapsed;
        if (this.timer_ai_action2 < 0) {
          this.timer_ai_action2 = (int)(LNZ.ai_chickenTimer2 + Misc.randomDouble(LNZ.ai_chickenTimer2));
          if (Misc.randomChance(0.5)) {
            this.setUnitID(1002);
          }
          else {
            this.setUnitID(1005);
          }
          this.size = LNZ.unit_defaultSize;
        }
        break;
      case 1005: // Rooster
        if (this.curr_action == UnitAction.NONE || (this.last_move_any_collision && this.object_targeting == null)) {
          this.timer_ai_action1 -= time_elapsed;
          this.timer_ai_action2 -= time_elapsed;
          boolean random_walk = true;
          for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
            if (entry.getKey() == this.map_key) {
              continue;
            }
            Unit u = entry.getValue();
            if (u.ID != 1002 && u.ID != 1003) {
              continue;
            }
            if (!u.ai_toggle) {
              continue;
            }
            if (u.timer_ai_action3 <= 0) {
              continue;
            }
            if (this.distance(u) > this.sight()) {
              continue;
            }
            if (u.last_damage_from == null || u.last_damage_from.remove) {
              continue;
            }
            if (this.distance(u.last_damage_from) > this.sight()) {
              continue;
            }
            try {
              this.target(u.last_damage_from, map);
              random_walk = false;
            } catch(Exception e) {}
            break;
          }
          if (this.timer_ai_action1 < 0) {
            if (random_walk) {
              this.timer_ai_action1 = (int)(LNZ.ai_chickenTimer1 + Misc.randomDouble(LNZ.ai_chickenTimer1));
              this.faceRandom();
              this.moveForward(LNZ.ai_chickenMoveDistance, map);
            }
          }
          if (this.timer_ai_action2 < 0) {
            this.timer_ai_action2 = (int)(0.4 * LNZ.ai_chickenTimer2 + 0.4 * Misc.randomDouble(LNZ.ai_chickenTimer2));
            if (this.in_view) {
              p.global.sounds.trigger_units("units/other/rooster_crow" + Misc.randomInt(1, 3));
            }
          }
        }
        break;
      case 1009: // Frog
        int frog_terrain_id = 0;
        try {
          frog_terrain_id = map.mapSquare(this.coordinate).terrain_id;
        } catch(NullPointerException e) {}
        this.timer_ai_action1 -= time_elapsed;
        if (this.timer_ai_action1 < 0) {
          this.timer_ai_action1 = 2500;
          for (int i = LNZApplet.round(Math.floor(this.coordinate.x)) - 2; i <= LNZApplet.round(Math.floor(this.coordinate.x)) + 2; i++) {
            for (int j = LNZApplet.round(Math.floor(this.coordinate.y)) - 2; j <= LNZApplet.round(Math.floor(this.coordinate.y)) + 2; j++) {
              int terrain_id = 0;
              try {
                terrain_id = map.mapSquare(i, j).terrain_id;
              } catch(NullPointerException e) {}
              if (terrain_id >= 181 && terrain_id < 190) {
                this.face(i + 0.5, j + 0.5);
                break;
              }
            }
          }
        }
        if (frog_terrain_id >= 181 && frog_terrain_id < 190) { // in water
          this.remove = true;
          this.last_damage_from = null;
          p.global.sounds.trigger_units("units/other/frog_splash");
          map.addVisualEffect(4018, this.coordinate.x, this.coordinate.y);
        }
        if (this.curr_action == UnitAction.NONE || this.last_move_any_collision) {
          for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
            if (entry.getKey() == this.map_key) {
              continue;
            }
            Unit u = entry.getValue();
            boolean ignore_unit = false;
            switch(u.ID) {
              case 1001: // Test Dummy
              case 1003: // Chick
              case 1009: // Frog
              case 1011: // Squirrel
                ignore_unit = true;
                break;
              default:
                break;
            }
            if (ignore_unit) {
              continue;
            }
            if (u.sneaking() && Math.abs(this.facingDif(u)) > PConstants.HALF_PI) {
              continue;
            }
            if (this.distance(u) > this.sight()) {
              continue;
            }
            this.moveForward(2, map);
          }
        }
        break;
      case 1010: // Quail
        if (this.curr_action == UnitAction.NONE || (this.last_move_any_collision && this.object_targeting == null)) {
          boolean random_walk = true;
          for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
            if (entry.getKey() == this.map_key) {
              continue;
            }
            Unit u = entry.getValue();
            boolean ignore_unit = false;
            switch(u.ID) {
              case 1001: // Test Dummy
              case 1002: // Chicken
              case 1003: // Chick
              case 1005: // Rooster
              case 1009: // Frog
              case 1010: // Quail
              case 1011: // Squirrel
              case 1012: // Fawn
                ignore_unit = true;
                break;
              default:
                break;
            }
            if (ignore_unit) {
              continue;
            }
            if (u.sneaking()) {
              continue;
            }
            if (this.distance(u) > this.sight()) {
              continue;
            }
            random_walk = false;
            this.faceAway(u);
            this.moveForward(0.2 * LNZ.ai_chickenMoveDistance, map);
          }
          this.timer_ai_action1 -= time_elapsed;
          if (random_walk && this.timer_ai_action1 < 0) {
            this.timer_ai_action1 = (int)(LNZ.ai_chickenTimer1 + Misc.randomDouble(LNZ.ai_chickenTimer1));
            this.faceRandom();
            this.moveForward(LNZ.ai_chickenMoveDistance, map);
          }
        }
        this.timer_ai_action2 -= time_elapsed;
        if (this.timer_ai_action2 < 0) {
          this.timer_ai_action2 = (int)(LNZ.ai_chickenTimer2 + Misc.randomDouble(LNZ.ai_chickenTimer2));
          Item egg_item = new Item(p, 2127, this.coordinate.x, this.coordinate.y);
          if (this.fertilized() || Misc.randomChance(0.2)) {
            egg_item.toggled = true; // quail egg doesn't hatch yet
          }
          map.addItem(egg_item);
          if (this.in_view) {
            p.global.sounds.trigger_units("units/other/chicken_lay_egg");
          }
        }
        break;
      case 1011: // Squirrel
        if (this.curr_action == UnitAction.NONE || (this.last_move_any_collision && this.object_targeting == null)) {
          boolean random_walk = true;
          for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
            if (entry.getKey() == this.map_key) {
              continue;
            }
            Unit u = entry.getValue();
            boolean ignore_unit = false;
            switch(u.ID) {
              case 1001: // Test Dummy
              case 1002: // Chicken
              case 1003: // Chick
              case 1005: // Rooster
              case 1009: // Frog
              case 1010: // Quail
              case 1011: // Squirrel
              case 1012: // Fawn
                ignore_unit = true;
                break;
              default:
                break;
            }
            if (ignore_unit) {
              continue;
            }
            if (u.sneaking()) {
              continue;
            }
            if (this.distance(u) > this.sight()) {
              continue;
            }
            random_walk = false;
            if (Misc.randomChance(0.004) || (this.last_move_collision && Misc.randomChance(0.08))) {
              this.target(u, map);
            }
            else {
              this.faceAway(u);
              this.moveForward(0.2 * LNZ.ai_chickenMoveDistance, map);
            }
            break;
          }
          this.timer_ai_action1 -= time_elapsed;
          if (random_walk && this.timer_ai_action1 < 0) {
            this.timer_ai_action1 = (int)(0.6 * (LNZ.ai_chickenTimer1 + Misc.randomDouble(LNZ.ai_chickenTimer1)));
            this.faceRandom();
            if (Misc.randomChance(0.3)) {
              this.moveForward(0.5 * (LNZ.ai_chickenMoveDistance + Misc.randomDouble(LNZ.ai_chickenMoveDistance)), map);
            }
            else {
              this.timer_ai_action1 = (int)(this.timer_ai_action1 * 0.3);
            }
          }
        }
        break;
      case 1012: // Fawn
        this.timer_ai_action3 -= time_elapsed;
        if (this.timer_ai_action3 < 0) {
          this.timer_ai_action3 = 1100;
          this.ai_toggle = false;
          double other_deer_face_x = 0;
          double other_deer_face_y = 0;
          int other_deer_distressed = 0;
          int longest_distressed = 0;
          for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
            if (entry.getKey() == this.map_key) {
              continue;
            }
            Unit u = entry.getValue();
            switch(u.ID) {
              case 1012: // Fawn
                if (!u.ai_toggle) {
                  break;
                }
                if (this.distance(u) >  this.sight()) {
                  break;
                }
                other_deer_distressed++;
                other_deer_face_x += u.facing.x;
                other_deer_face_y += u.facing.y;
                longest_distressed = Math.max(longest_distressed, u.timer_ai_action3);
                break;
              case 1014: // Doe
                if (u.timer_ai_action2 <= 0) {
                  break;
                }
                if (this.distance(u) >  this.sight()) {
                  break;
                }
                other_deer_distressed += 2;
                if (u.ai_toggle) {
                  other_deer_face_x -= 2 * u.facing.x;
                  other_deer_face_y -= 2 * u.facing.y;
                }
                else {
                  other_deer_face_x += 2 * u.facing.x;
                  other_deer_face_y += 2 * u.facing.y;
                }
                longest_distressed = Math.max(longest_distressed, u.timer_ai_action2);
                break;
              case 1015: // Buck
                if (!u.ai_toggle) {
                  break;
                }
                if (this.distance(u) >  this.sight()) {
                  break;
                }
                other_deer_distressed++;
                other_deer_face_x -= u.facing.x;
                other_deer_face_y -= u.facing.y;
                longest_distressed = Math.max(longest_distressed, u.timer_ai_action3);
                break;
              case 1001:
              case 1002:
              case 1003:
              case 1004:
              case 1005:
              case 1009:
              case 1010:
              case 1011:
              case 1013: // ignore smaller animals
                break;
              default:
                double distance = this.distance(u);
                if (distance > this.sight()) {
                  break;
                }
                if (distance > 0.5 * this.sight() && u.sneaking()) {
                  break;
                }
                if (distance > 0.5 * this.sight() || u.sneaking()) {
                  this.face(u);
                  this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(1, LNZ.ai_chickenTimer1);
                  break;
                }
                this.faceAway(u);
                other_deer_distressed += 3;
                other_deer_face_x += 3 * this.facing.x;
                other_deer_face_y += 3 * this.facing.y;
                longest_distressed = Math.max(longest_distressed, 4000);
                break;
            }
          }
          if (other_deer_distressed > 0 && longest_distressed > 50) {
            other_deer_face_x /= other_deer_distressed;
            other_deer_face_y /= other_deer_distressed;
            this.ai_toggle = true;
            this.timer_ai_action3 = longest_distressed - 50;
            this.refreshStatusEffect(StatusEffectCode.RUNNING, longest_distressed);
            this.setFacing(other_deer_face_x, other_deer_face_y);
            this.moveForward(4, map);
          }
        }
        if (!this.ai_toggle && (this.curr_action == UnitAction.NONE || this.last_move_any_collision)) {
          this.timer_ai_action1 -= time_elapsed;
          if (this.timer_ai_action1 < 0) {
            this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(1, LNZ.ai_chickenTimer1);
            int attempts_to_find_grass = 0;
            while(true) {
              attempts_to_find_grass++;
              int try_x = (int)this.coordinate.x + Misc.randomInt(-4, 4);
              int try_y = (int)this.coordinate.y + Misc.randomInt(-4, 4);
              if (attempts_to_find_grass > 4) {
                this.moveTo(try_x + Math.random(), try_y + Math.random(), map);
                break;
              }
              GameMapSquare square = map.mapSquare(try_x, try_y);
              if (square == null) {
                continue;
              }
              if (square.terrain_id > 150 && square.terrain_id < 161) {
                this.moveTo(try_x + 0.5, try_y + 0.5, map);
                break;
              }
            }
          }
        }
        if (this.timer_ai_action2 < 0) {
          if (Misc.randomChance(0.5)) {
            this.setUnitID(1014);
          }
          else {
            this.setUnitID(1015);
          }
        }
        else {
          this.timer_ai_action2 -= time_elapsed;
        }
        break;
      case 1013: // Raccoon
        if (this.timer_ai_action2 >= 0) {
          this.timer_ai_action2 -= time_elapsed;
        }
        if (this.timer_ai_action3 >= 0) {
          this.timer_ai_action3 -= time_elapsed;
        }
        boolean currently_moving = false;
        switch(this.curr_action) {
          case TARGETING_ITEM:
          case MOVING:
          case MOVING_AND_USING_ITEM:
            if (this.last_move_any_collision) {
              this.stopAction();
            }
            currently_moving = true;
          case NONE:
            boolean random_walk = true;
            this.ai_toggle = false;
            for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
              if (entry.getKey() == this.map_key) {
                continue;
              }
              Unit u = entry.getValue();
              boolean ignore_unit = false;
              switch(u.ID) {
                case 1001: // Test Dummy
                case 1005: // Rooster
                case 1011: // Squirrel
                case 1012: // Fawn
                case 1013: // Raccoon
                  ignore_unit = true;
                  break;
                default:
                  break;
              }
              if (ignore_unit) {
                continue;
              }
              if (u.sneaking() && this.distance(u) > 0.6 * this.sight()) {
                continue;
              }
              if (this.distance(u) > this.sight()) {
                continue;
              }
              boolean attack_unit = false;
              switch(u.ID) {
                case 1002: // Chicken
                case 1003: // Chick
                case 1009: // Frog
                case 1010: // Quail
                  attack_unit = true;
                  break;
                default:
                  break;
              }
              if (attack_unit || this.distance(u) < 1.5) {
                this.target(u, map);
                this.timer_ai_action3 = 400;
                random_walk = false;
                break;
              }
              else if (this.timer_ai_action2 < 0) {
                this.face(u);
                this.timer_ai_action2 = 100;
                this.ai_toggle = true;
              }
              else {
                this.ai_toggle = true;
              }
            }
            if (this.ai_toggle) {
              this.addStatusEffect(StatusEffectCode.SNEAKING);
            }
            else {
              this.removeStatusEffect(StatusEffectCode.SNEAKING);
            }
            if (currently_moving) {
              break;
            }
            if (random_walk && this.canPickup()) {
              for (Map.Entry<Integer, Item> entry : map.items.entrySet()) {
                Item i = entry.getValue();
                if (i == null || i.remove) {
                  continue;
                }
                boolean ignore_item = true;
                switch(i.ID) {
                  case 2901: // Key
                  case 2902: // Master Key
                  case 2903: // Skeleton Key
                  case 2904: // Small Keyring
                  case 2905: // Large Keyring
                    ignore_item = false;
                    break;
                  default:
                    break;
                }
                if (ignore_item) {
                  continue;
                }
                if (this.distance(i) > this.sight()) {
                  continue;
                }
                this.target(i, map);
              }
            }
            this.timer_ai_action1 -= time_elapsed;
            if (random_walk && this.timer_ai_action1 < 0) {
              this.timer_ai_action1 = (int)(LNZ.ai_chickenTimer1 + Misc.randomDouble(LNZ.ai_chickenTimer1));
              this.faceRandom();
              this.moveForward(LNZ.ai_chickenMoveDistance, map);
            }
            break;
          case TARGETING_UNIT:
            if (this.object_targeting == null || (this.distance(this.object_targeting) > 2 && this.timer_ai_action3 < 0)) {
              this.stopAction();
            }
            break;
          default:
            break;
        }
        break;
      case 1014: // Doe
        if (this.timer_ai_action2 >= 0) {
          this.timer_ai_action2 -= time_elapsed;
        }
        this.timer_ai_action3 -= time_elapsed;
        Unit potential_target = null;
        if (this.timer_ai_action3 < 0) {
          this.timer_ai_action3 = 1100;
          this.ai_toggle = false;
          double other_deer_face_x = 0;
          double other_deer_face_y = 0;
          int other_deer_distressed = 0;
          int longest_distressed = this.timer_ai_action2;
          for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
            if (entry.getKey() == this.map_key) {
              continue;
            }
            Unit u = entry.getValue();
            switch(u.ID) {
              case 1012: // Fawn
                if (!u.ai_toggle) {
                  break;
                }
                if (this.distance(u) >  this.sight()) {
                  break;
                }
                other_deer_distressed++;
                other_deer_face_x += u.facing.x;
                other_deer_face_y += u.facing.y;
                longest_distressed = Math.max(longest_distressed, u.timer_ai_action3);
                this.ai_toggle = true;
                break;
              case 1014: // Doe
                if (u.timer_ai_action2 <= 0) {
                  break;
                }
                if (this.distance(u) >  this.sight()) {
                  break;
                }
                other_deer_distressed++;
                if (this.ai_toggle == u.ai_toggle) {
                  other_deer_face_x += u.facing.x;
                  other_deer_face_y += u.facing.y;
                }
                else {
                  other_deer_face_x -= u.facing.x;
                  other_deer_face_y -= u.facing.y;
                }
                longest_distressed = Math.max(longest_distressed, u.timer_ai_action2);
                break;
              case 1015: // Buck
                if (!u.ai_toggle) {
                  break;
                }
                if (this.distance(u) >  this.sight()) {
                  break;
                }
                other_deer_distressed++;
                other_deer_face_x -= u.facing.x;
                other_deer_face_y -= u.facing.y;
                longest_distressed = Math.max(longest_distressed, u.timer_ai_action3);
                break;
              case 1001:
              case 1002:
              case 1003:
              case 1004:
              case 1005:
              case 1009:
              case 1010:
              case 1011:
              case 1013: // ignore smaller animals
                break;
              default:
                double distance = this.distance(u);
                if (distance > this.sight() || (distance > 0.5 * this.sight() && u.sneaking())) {
                  break;
                }
                if (distance > 0.5 * this.sight() || (distance > 1 && u.sneaking())) {
                  this.face(u);
                  this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(1, LNZ.ai_chickenTimer1);
                  break;
                }
                this.faceAway(u);
                other_deer_distressed += 3;
                other_deer_face_x += 3 * this.facing.x;
                other_deer_face_y += 3 * this.facing.y;
                longest_distressed = Math.max(longest_distressed, 4000);
                potential_target = u;
                break;
            }
          }
          if (other_deer_distressed > 0 && longest_distressed > 50) {
            other_deer_face_x /= other_deer_distressed;
            other_deer_face_y /= other_deer_distressed;
            this.timer_ai_action2 = longest_distressed;
            this.timer_ai_action3 = longest_distressed - 50;
            this.refreshStatusEffect(StatusEffectCode.RUNNING, longest_distressed);
            if (this.ai_toggle && potential_target != null && !potential_target.remove) {
              this.target(potential_target, map);
            }
            else {
              this.setFacing(other_deer_face_x, other_deer_face_y);
              this.moveForward(4, map);
            }
          }
        }
        if (this.timer_ai_action2 < 0 && (this.curr_action == UnitAction.NONE || this.last_move_any_collision)) {
          this.timer_ai_action1 -= time_elapsed;
          if (this.timer_ai_action1 < 0) {
            this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(1, LNZ.ai_chickenTimer1);
            int attempts_to_find_grass = 0;
            while(true) {
              attempts_to_find_grass++;
              int try_x = (int)this.coordinate.x + Misc.randomInt(-4, 4);
              int try_y = (int)this.coordinate.y + Misc.randomInt(-4, 4);
              if (attempts_to_find_grass > 4) {
                this.moveTo(try_x + Math.random(), try_y + Math.random(), map);
                break;
              }
              GameMapSquare square = map.mapSquare(try_x, try_y);
              if (square == null) {
                continue;
              }
              if (square.terrain_id > 150 && square.terrain_id < 161) {
                this.moveTo(try_x + 0.5, try_y + 0.5, map);
                break;
              }
            }
          }
        }
        break;
      case 1015: // Buck
        this.timer_ai_action3 -= time_elapsed;
        if (this.timer_ai_action3 < 0) {
          this.timer_ai_action3 = 1100;
          this.ai_toggle = false;
          double other_deer_face_x = 0;
          double other_deer_face_y = 0;
          int other_deer_distressed = 0;
          int longest_distressed = 0;
          for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
            if (entry.getKey() == this.map_key) {
              continue;
            }
            Unit u = entry.getValue();
            switch(u.ID) {
              case 1012: // Fawn
                if (!u.ai_toggle) {
                  break;
                }
                if (this.distance(u) >  this.sight()) {
                  break;
                }
                other_deer_distressed++;
                other_deer_face_x += u.facing.x;
                other_deer_face_y += u.facing.y;
                longest_distressed = Math.max(longest_distressed, u.timer_ai_action3);
                break;
              case 1014: // Doe
                if (u.timer_ai_action2 <= 0) {
                  break;
                }
                if (this.distance(u) >  this.sight()) {
                  break;
                }
                other_deer_distressed++;
                other_deer_face_x += u.facing.x;
                other_deer_face_y += u.facing.y;
                longest_distressed = Math.max(longest_distressed, u.timer_ai_action2);
                break;
              case 1015: // Buck
                if (!u.ai_toggle) {
                  break;
                }
                if (this.distance(u) >  this.sight()) {
                  break;
                }
                other_deer_distressed++;
                other_deer_face_x += u.facing.x;
                other_deer_face_y += u.facing.y;
                longest_distressed = Math.max(longest_distressed, u.timer_ai_action3);
                break;
              case 1001:
              case 1002:
              case 1003:
              case 1004:
              case 1005:
              case 1009:
              case 1010:
              case 1011:
              case 1013: // ignore smaller animals
                break;
              default:
                double distance = this.distance(u);
                if (distance > this.sight() || (distance > 0.5 * this.sight() && u.sneaking())) {
                  break;
                }
                if (distance > 0.5 * this.sight() || (distance > 1 && u.sneaking())) {
                  this.face(u);
                  this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(1, LNZ.ai_chickenTimer1);
                  break;
                }
                this.face(u);
                other_deer_distressed += 3;
                other_deer_face_x += 3 * this.facing.x;
                other_deer_face_y += 3 * this.facing.y;
                longest_distressed = Math.max(longest_distressed, 4000);
                if (!UnitAction.aggressiveAction(this.curr_action)) {
                  this.target(u, map);
                }
                break;
            }
          }
          if (other_deer_distressed > 0 && longest_distressed > 50) {
            other_deer_face_x /= other_deer_distressed;
            other_deer_face_y /= other_deer_distressed;
            this.ai_toggle = true;
            this.timer_ai_action3 = longest_distressed - 50;
            this.refreshStatusEffect(StatusEffectCode.RUNNING, longest_distressed);
            if (!UnitAction.aggressiveAction(this.curr_action)) {
              this.setFacing(other_deer_face_x, other_deer_face_y);
              this.moveForward(4, map);
            }
          }
        }
        if (this.timer_ai_action2 < 0 && (this.curr_action == UnitAction.NONE || this.last_move_any_collision)) {
          this.timer_ai_action1 -= time_elapsed;
          if (this.timer_ai_action1 < 0) {
            this.timer_ai_action1 = LNZ.ai_chickenTimer1 + Misc.randomInt(1, LNZ.ai_chickenTimer1);
            int attempts_to_find_grass = 0;
            while(true) {
              attempts_to_find_grass++;
              int try_x = (int)this.coordinate.x + Misc.randomInt(-4, 4);
              int try_y = (int)this.coordinate.y + Misc.randomInt(-4, 4);
              if (attempts_to_find_grass > 4) {
                this.moveTo(try_x + Math.random(), try_y + Math.random(), map);
                break;
              }
              GameMapSquare square = map.mapSquare(try_x, try_y);
              if (square == null) {
                continue;
              }
              if (square.terrain_id > 150 && square.terrain_id < 161) {
                this.moveTo(try_x + 0.5, try_y + 0.5, map);
                break;
              }
            }
          }
        }
        break;
      case 1201: // Campaign Zombies
      case 1202:
      case 1203:
      case 1204:
      case 1205:
      case 1206:
      case 1207:
      case 1208:
      case 1209:
      case 1210:
      case 1211:
      case 1212:
      case 1213:
      case 1214:
      case 1215:
      case 1291: // Auto-spawned Zombies
      case 1292:
      case 1293:
      case 1302: // Named Zombies
      case 1303:
      case 1304:
      case 1305:
      case 1306:
      case 1307:
      case 1308:
      case 1309:
      case 1310:
        if (this.curr_action == UnitAction.NONE || this.last_move_any_collision) {
          this.timer_ai_action1 -= time_elapsed;
          if (this.timer_ai_action1 < 0) {
            this.timer_ai_action1 = 400;
            boolean no_target = true;
            for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
              if (entry.getKey() == this.map_key) {
                continue;
              }
              Unit u = entry.getValue();
              if (u.alliance == this.alliance) {
                continue;
              }
              double distance = this.distance(u);
              if (distance > this.sight()) {
                continue;
              }
              if (no_target) {
                no_target = false;
                this.target(u, map);
              }
              else if (!u.ai_controlled) {
                this.target(u, map);
              }
            }
            if (no_target && Misc.randomChance(0.1)) {
              this.moveTo(this.coordinate.x + 3 - Misc.randomDouble(6), this.coordinate.y+ 3 - Misc.randomDouble(6), map);
            }
          }
        }
        break;
      case 1351: // Cathy Heck
        if (!this.ai_toggle) {
          break;
        }
        this.timer_ai_action1 -= time_elapsed;
        this.timer_ai_action2 -= time_elapsed;
        this.timer_ai_action3 -= time_elapsed;
        switch(this.curr_action) {
          case NONE:
            if (map.units.containsKey(0)) {
              this.target(map.units.get(0), map);
            }
            break;
          case TARGETING_UNIT:
            if (this.timer_ai_action3 < 0) {
              this.timer_ai_action3 = 9000 + Misc.randomInt(9000);
              if (this.object_targeting == null || this.object_targeting.remove) {
                break;
              }
              if (this.distance(this.object_targeting) > LNZ.ability_1003_size_w * 1.3) {
                break;
              }
              this.cast(2, map);
            }
            else if (this.timer_ai_action2 < 0) {
              this.timer_ai_action2 = 9000 + Misc.randomInt(9000);
              this.cast(1, map);
            }
            else if (this.timer_ai_action1 < 0) {
              this.timer_ai_action1 = 9000 + Misc.randomInt(9000);
              this.cast(0, map);
            }
            break;
          default:
            break;
        }
        break;
      case 1353: // Ben Kohring
        if (!this.ai_toggle) {
          break;
        }
        switch(this.curr_action) {
          case NONE:
            if (map.units.containsKey(0)) {
              this.target(map.units.get(0), map);
            }
            break;
          default:
            break;
        }
        this.timer_ai_action1 -= time_elapsed;
        this.timer_ai_action2 -= time_elapsed;
        if (this.timer_ai_action1 < 0) {
          this.timer_ai_action1 = 6000 + Misc.randomInt(3000);
          this.cast(0, map);
        }
        else if (this.timer_ai_action2 < 0) {
          this.timer_ai_action2 = this.timer_ai_action1 + 4000;
          if (this.object_targeting != null) {
            this.cast(1, map);
          }
        }
        break;
      default:
        break;
    }
  }


  String fileString() {
    return this.fileString(true);
  }
  String fileString(boolean include_headers) {
    String fileString = "";
    if (include_headers) {
      fileString += "\nnew: Unit: " + this.ID;
    }
    fileString += this.objectFileString();
    fileString += "\nsize: " + this.size;
    fileString += "\nlevel: " + this.level;
    fileString += "\nalliance: " + this.alliance.alliance_name();
    fileString += "\nelement: " + this.element.element_name();
    for (Map.Entry<GearSlot, Item> slot : this.gear.entrySet()) {
      if (slot.getKey() == null || slot.getKey() == GearSlot.ERROR) {
        continue;
      }
      fileString += "\ngearSlot: " + slot.getKey();
      if (slot.getValue() != null) {
        fileString += slot.getValue().fileString(slot.getKey());
      }
    }
    for (Map.Entry<StatusEffectCode, StatusEffect> entry : this.statuses.entrySet()) {
      fileString += "\nnext_status_code: " + entry.getKey().codeName();
      fileString += entry.getValue().fileString();
    }
    fileString += "\nfacingX: " + this.facing.x;
    fileString += "\nfacingY: " + this.facing.y;
    fileString += "\nfacingA: " + this.facingA;
    if (this.save_base_stats) {
      fileString += "\nsave_base_stats: " + this.save_base_stats;
      fileString += "\nbase_health: " + this.base_health;
      fileString += "\nbase_attack: " + this.base_attack;
      fileString += "\nbase_magic: " + this.base_magic;
      fileString += "\nbase_defense: " + this.base_defense;
      fileString += "\nbase_resistance: " + this.base_resistance;
      fileString += "\nbase_piercing: " + this.base_piercing;
      fileString += "\nbase_penetration: " + this.base_penetration;
      fileString += "\nbase_attackRange: " + this.base_attackRange;
      fileString += "\nbase_attackCooldown: " + this.base_attackCooldown;
      fileString += "\nbase_attackTime: " + this.base_attackTime;
      fileString += "\nbase_sight: " + this.base_sight;
      fileString += "\nbase_speed: " + this.base_speed;
      fileString += "\nbase_tenacity: " + this.base_tenacity;
      fileString += "\nbase_agility: " + this.base_agility;
      fileString += "\nbase_lifesteal: " + this.base_lifesteal;
    }
    if (this.save_base_stats || Math.abs(this.curr_health - this.health()) > LNZ.small_number) {
      fileString += "\ncurr_health: " + this.curr_health;
    }
    fileString += "\nfootgear_durability_distance: " + this.footgear_durability_distance;
    fileString += "\ntimer_attackCooldown: " + this.timer_attackCooldown;
    fileString += "\ntimer_actionTime: " + this.timer_actionTime;
    fileString += "\nai_toggle: " + this.ai_toggle;
    if (include_headers) {
      fileString += "\nend: Unit\n";
    }
    return fileString;
  }

  void addData(String datakey, String data) {
    if (this.addObjectData(datakey, data)) {
      return;
    }
    switch(datakey) {
      case "size":
        this.size = Misc.toDouble(data);
        break;
      case "level":
        this.setLevel(Misc.toInt(data));
        break;
      case "alliance":
        this.alliance = Alliance.alliance(data);
        break;
      case "element":
        this.element = Element.element(data);
        break;
      case "gearSlot":
        GearSlot slot = GearSlot.gearSlot(data);
        if (slot == GearSlot.ERROR) {
          p.global.errorMessage("ERROR: Error gear slot from unit data: " + data + ".");
        }
        this.gear.put(slot, null);
        break;
      case "facingX":
        this.facing.x = Misc.toDouble(data);
        break;
      case "facingY":
        this.facing.y = Misc.toDouble(data);
        break;
      case "facingA":
        this.facingA = Misc.toDouble(data);
        break;
      case "addNullAbility":
        this.abilities.add(null);
        break;
      case "save_base_stats":
        this.save_base_stats = Misc.toBoolean(data);
        break;
      case "base_health":
        this.base_health = Misc.toDouble(data);
        break;
      case "base_attack":
        this.base_attack = Misc.toDouble(data);
        break;
      case "base_magic":
        this.base_magic = Misc.toDouble(data);
        break;
      case "base_defense":
        this.base_defense = Misc.toDouble(data);
        break;
      case "base_resistance":
        this.base_resistance = Misc.toDouble(data);
        break;
      case "base_piercing":
        this.base_piercing = Misc.toDouble(data);
        break;
      case "base_penetration":
        this.base_penetration = Misc.toDouble(data);
        break;
      case "base_attackRange":
        this.base_attackRange = Misc.toDouble(data);
        break;
      case "base_attackCooldown":
        this.base_attackCooldown = Misc.toDouble(data);
        break;
      case "base_attackTime":
        this.base_attackTime = Misc.toDouble(data);
        break;
      case "base_sight":
        this.base_sight = Misc.toDouble(data);
        break;
      case "base_speed":
        this.base_speed = Misc.toDouble(data);
        break;
      case "base_tenacity":
        this.base_tenacity = Misc.toDouble(data);
        break;
      case "base_agility":
        this.base_agility = Misc.toInt(data);
        break;
      case "base_lifesteal":
        this.base_lifesteal = Misc.toDouble(data);
        break;
      case "curr_health":
        this.curr_health = Misc.toDouble(data);
        break;
      case "footgear_durability_distance":
        this.footgear_durability_distance = Misc.toDouble(data);
        break;
      case "timer_attackCooldown":
        this.timer_attackCooldown = Misc.toDouble(data);
        break;
      case "timer_actionTime":
        this.timer_actionTime = Misc.toDouble(data);
        break;
      case "ai_toggle":
        this.ai_toggle = Misc.toBoolean(data);
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not found for unit data.");
        break;
    }
  }


  int tier() {
    return 1 + this.level / 10;
  }
}