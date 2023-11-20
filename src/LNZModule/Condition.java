package LNZModule;

import Misc.Misc;

class Condition {
  private LNZ p;

  protected int ID = 0;
  protected String display_name = "Condition";

  protected int number1 = 0;
  protected int number2 = 0;
  protected Rectangle rectangle;

  protected boolean not_condition = false;
  protected boolean met = false;

  Condition(LNZ sketch) {
    this.p = sketch;
    this.rectangle = new Rectangle(sketch);
  }

  void setID(int ID) {
    this.ID = ID;
    switch(ID) {
      case 0: // nothing
      case 1: // timer
      case 2: // selected specific unit
      case 3: // selected specific item
      case 4: // selected unit
      case 5: // selected item
      case 6: // player in rectangle
      case 7: // unit exists
      case 8: // has hero upgrade
      case 9: // unit in rectangle
      case 10: // unit health
      case 11: // holding item
      case 12: // player hunger
      case 13: // player thirst
      case 14: // player mana
      case 15: // time after
      case 16: // player respawning
      case 17: // item id in rectangle
        break;
      default:
        p.global.errorMessage("ERROR: Condition ID " + ID + " not recognized.");
        break;
    }
    this.setName();
  }

  void setName() {
    switch(this.ID) {
      case 1: // timer
        if (number1 < 1000) {
          this.display_name = "Timer (" + this.number1 + " ms)";
        }
        else {
          this.display_name = "Timer (" + Math.round(this.number1/100.0)/10.0 + " s)";
        }
        break;
      case 2: // selected specific unit
        this.display_name = "Select Specific Unit (" + this.number1 + ")";
        break;
      case 3: // selected specific item
        this.display_name = "Select Specific Item (" + this.number1 + ")";
        break;
      case 4: // selected unit id
        this.display_name = "Select Unit (" + this.number1 + ")";
        break;
      case 5: // selected item id
        this.display_name = "Select Item (" + this.number1 + ")";
        break;
      case 6: // player in rectangle
        this.display_name = "Player In: " + this.rectangle.fileString();
        break;
      case 7: // unit exists
        this.display_name = "Unit Exists (" + this.number1 + ")";
        break;
      case 8: // has hero upgrade
        this.display_name = "Has Hero Upgrade (" + HeroTreeCode.codeFromId(this.number1) + ")";
        break;
      case 9: // unit in rectangle
        this.display_name = "Unit (" + this.number1 + ") In: " + this.rectangle.fileString();
        break;
      case 10: // unit health
        this.display_name = "Unit (" + this.number1 + ") Health above (" + this.number2 + ")";
        break;
      case 11: // holding item
        this.display_name = "Player Holding Item (" + this.number1 + ")";
        break;
      case 12: // player hunger
        this.display_name = "Player Hunger Above (" + this.number1 + ")";
        break;
      case 13: // player thirst
        this.display_name = "Player Thirst Above (" + this.number1 + ")";
        break;
      case 14: // player mana
        this.display_name = "Player Mana Above (" + this.number1 + ")";
        break;
      case 15: // time after
        this.display_name = "Time after (" + this.number1 + ")";
        break;
      case 16: // player respawning
        this.display_name = "Player Respawning";
        break;
      case 17: // item id in rectangle
        this.display_name = "Item ID (" + this.number1 + ") In: " + this.rectangle.fileString();
        break;
      default:
        this.display_name = "Condition";
        break;
    }
    if (this.not_condition) {
      this.display_name = "NOT " + this.display_name;
    }
  }

  boolean update(int time_elapsed, Level level) {
    switch(this.ID) {
      case 0: // nothing
        break;
      case 1: // timer
        if (!this.met) {
          this.number2 -= time_elapsed;
          if (this.number2 < 0) {
            this.met = true;
          }
        }
        break;
      case 2: // selected specific unit
        if (this.met) {
          break;
        }
        if (level.curr_map == null) {
          break;
        }
        if (Unit.class.isInstance(level.curr_map.selected_object)) {
          Unit u = (Unit)level.curr_map.selected_object;
          if (u.map_key == this.number1) {
            this.met = true;
          }
        }
        break;
      case 3: // selected specific item
        if (this.met) {
          break;
        }
        if (level.curr_map == null) {
          break;
        }
        if (Item.class.isInstance(level.curr_map.selected_object)) {
          Item i = (Item)level.curr_map.selected_object;
          if (i.map_key == this.number1) {
            this.met = true;
          }
        }
        break;
      case 4: // selected unit
        if (this.met) {
          break;
        }
        if (level.curr_map == null) {
          break;
        }
        if (Unit.class.isInstance(level.curr_map.selected_object)) {
          Unit u = (Unit)level.curr_map.selected_object;
          if (u.ID == this.number1) {
            this.met = true;
          }
        }
        break;
      case 5: // selected item
        if (this.met) {
          break;
        }
        if (level.curr_map == null) {
          break;
        }
        if (Item.class.isInstance(level.curr_map.selected_object)) {
          Item i = (Item)level.curr_map.selected_object;
          if (i.ID == this.number1) {
            this.met = true;
          }
        }
        break;
      case 6: // player in rectangle
        if (this.met) {
          break;
        }
        if (level.respawning) {
          break;
        }
        if (level.player == null) {
          break;
        }
        if (level.currMapName == null) {
          break;
        }
        if (this.rectangle.contains(level.player, level.currMapName)) {
          this.met = true;
        }
        break;
      case 7: // unit exists
        if (this.met) {
          break;
        }
        if (level.curr_map == null) {
          break;
        }
        if (level.curr_map.units.containsKey(this.number1)) {
          this.met = true;
        }
        break;
      case 8: // has hero upgrade
        if (this.met) {
          break;
        }
        if (level.player == null) {
          break;
        }
        if (level.player.hero_tree.nodes.get(HeroTreeCode.codeFromId(this.number1)).unlocked) {
          this.met = true;
        }
        break;
      case 9: // unit in rectangle
        if (this.met) {
          break;
        }
        if (level.curr_map == null) {
          break;
        }
        if (!level.curr_map.units.containsKey(this.number1)) {
          break;
        }
        if (this.rectangle.contains(level.curr_map.units.get(this.number1), level.currMapName)) {
          this.met = true;
        }
        break;
      case 10: // unit health
        if (this.met) {
          break;
        }
        if (level.curr_map == null) {
          break;
        }
        if (!level.curr_map.units.containsKey(this.number1)) {
          break;
        }
        if (level.curr_map.units.get(this.number1).curr_health > this.number2) {
          this.met = true;
        }
        break;
      case 11: // holding item
        if (this.met) {
          break;
        }
        if (level.player == null) {
          break;
        }
        if (level.player.holding(this.number1)) {
          this.met = true;
        }
        break;
      case 12: // player hunger
        if (this.met) {
          break;
        }
        if (level.respawning) {
          break;
        }
        if (level.player == null) {
          break;
        }
        if (level.player.hunger > this.number1) {
          this.met = true;
        }
        break;
      case 13: // player thirst
        if (this.met) {
          break;
        }
        if (level.respawning) {
          break;
        }
        if (level.player == null) {
          break;
        }
        if (level.player.thirst > this.number1) {
          this.met = true;
        }
        break;
      case 14: // player mana
        if (this.met) {
          break;
        }
        if (level.respawning) {
          break;
        }
        if (level.player == null) {
          break;
        }
        if (level.player.curr_mana > this.number1) {
          this.met = true;
        }
        break;
      case 15: // time after
        if (this.met) {
          break;
        }
        if (level.time.value() >= this.number1) {
          this.met = true;
        }
        break;
      case 16: // player respawning
        if (this.met) {
          break;
        }
        this.met = level.respawning;
        break;
      case 17: // item id in rectangle
        if (this.met) {
          break;
        }
        if (level.curr_map == null) {
          break;
        }
        for (Item i : level.curr_map.items.values()) {
          if (i.remove || i.ID != this.number1) {
            continue;
          }
          if (this.rectangle.contains(i, level.currMapName)) {
            this.met = true;
          }
        }
        break;
      default:
        p.global.errorMessage("ERROR: Condition ID " + ID + " not recognized.");
        return false;
    }
    if (this.not_condition) {
      this.met = !this.met;
    }
    return this.met;
  }

  void reset() {
    this.met = false;
    switch(this.ID) {
      case 0: // nothing
        break;
      case 1: // timer
        this.number2 = this.number1;
        break;
      case 2: // selected specific unit
        break;
      case 3: // selected specific item
        break;
      case 4: // selected unit
        break;
      case 5: // selected item
        break;
      case 6: // player in rectangle
        break;
      case 7: // unit exists
        break;
      case 8: // has hero upgrade
        break;
      case 9: // unit in rectangle
        break;
      case 10: // unit health
        break;
      case 11: // holding item
        break;
      case 12: // player hunger
        break;
      case 13: // player thirst
        break;
      case 14: // player mana
        break;
      case 15: // time after
        break;
      case 16: // player respawning
        break;
      case 17: // item id in rectangle
        break;
      default:
        p.global.errorMessage("ERROR: Condition ID " + ID + " not recognized.");
        break;
    }
  }

  String fileString() {
    String fileString = "\nnew: Condition";
    fileString += "\nID: " + this.ID;
    fileString += "\nnumber1: " + this.number1;
    fileString += "\nnumber2: " + this.number2;
    fileString += "\nrectangle: " + this.rectangle.fileString();
    fileString += "\nmet: " + this.met;
    fileString += "\nnot_condition: " + this.not_condition;
    fileString += "\nend: Condition\n";
    return fileString;
  }

  void addData(String datakey, String data) {
    switch(datakey) {
      case "ID":
        this.setID(Misc.toInt(data));
        break;
      case "number1":
        this.number1 = Misc.toInt(data);
        break;
      case "number2":
        this.number2 = Misc.toInt(data);
        break;
      case "rectangle":
        this.rectangle.addData(data);
        break;
      case "met":
        this.met = Misc.toBoolean(data);
        break;
      case "not_condition":
        this.not_condition = Misc.toBoolean(data);
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not recognized for Condition object.");
        break;
    }
  }
}