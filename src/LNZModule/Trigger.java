package LNZModule;

import java.util.*;
import Misc.Misc;

class Trigger {
  private LNZ p;

  protected String triggerName = "";
  protected int triggerID = -1; // level id

  protected boolean active = false;
  protected boolean looping = false;
  protected boolean amalgam = true; // all conditions must be met (&& vs || condition list)

  protected ArrayList<Condition> conditions = new ArrayList<Condition>();
  protected ArrayList<Effect> effects = new ArrayList<Effect>();

  Trigger(LNZ sketch) {
    this.p = sketch;
  }
  Trigger(LNZ sketch, String triggerName) {
    this.p = sketch;
    this.triggerName = triggerName;
  }

  void update(int timeElapsed, Level level) {
    if (!this.active) {
      return;
    }
    boolean actuate = false;
    if (amalgam) {
      actuate = true;
    }
    for (Condition condition : this.conditions) {
      if (condition.update(timeElapsed, level)) {
        if (!amalgam) {
          actuate = true;
        }
      }
      else if (amalgam) {
        actuate = false;
      }
    }
    if (this.conditions.size() == 0 && !this.amalgam) {
      actuate = true;
    }
    if (actuate) {
      this.actuate(level);
    }
    else if (this.amalgam) {
      for (Condition condition : this.conditions) {
        condition.met = false;
      }
    }
  }

  void actuate(Level level) {
    for (Effect effect : this.effects) {
      effect.actuate(level);
    }
    for (Condition condition : this.conditions) {
      condition.reset();
    }
    if (!this.looping) {
      this.active = false;
    }
  }

  String fileString() {
    String fileString = "\nnew: Trigger";
    fileString += "\ntriggerName: " + this.triggerName;
    fileString += "\nactive: " + this.active;
    fileString += "\nlooping: " + this.looping;
    fileString += "\namalgam: " + this.amalgam;
    for (Condition condition : this.conditions) {
      fileString += condition.fileString();
    }
    for (Effect effect : this.effects) {
      fileString += effect.fileString();
    }
    fileString += "\nend: Trigger\n";
    return fileString;
  }

  void addData(String datakey, String data) {
    switch(datakey) {
      case "triggerName":
        this.triggerName = data;
        break;
      case "active":
        this.active = Misc.toBoolean(data);
        break;
      case "looping":
        this.looping = Misc.toBoolean(data);
        break;
      case "amalgam":
        this.amalgam = Misc.toBoolean(data);
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not recognized for Trigger object.");
        break;
    }
  }
}