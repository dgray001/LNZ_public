package LNZModule;

class Quest {
  private LNZ p;
  protected int ID = 0;
  protected boolean met = false;
  protected int blink_time_left = LNZ.level_questBlinkTime;
  protected int blinks_left = LNZ.level_questBlinks;
  protected boolean blinking = false;

  Quest(LNZ sketch, int id) {
    this.p = sketch;
    this.ID = id;
  }

  void meet(Hero h) {
    if (this.met) {
      return;
    }
    this.met = true;
    p.global.sounds.trigger_player("player/quest");
    this.blink_time_left = LNZ.level_questBlinkTime;
    this.blinks_left = LNZ.level_questBlinks;
    this.blinking = false;
    switch(this.ID) {
      // Francis Hall
      case 21: // find molly
        break;
      case 22: // find the thing
        break;
      case 23: // find bens coat
        break;
      case 24: // find bens backpack
        break;
      default:
        break;
    }
  }

  void update(Level level, int timeElapsed) {
    if (this.blink_time_left > 0) {
      this.blink_time_left -= timeElapsed;
      if (this.blink_time_left <= 0) {
        if (this.blinking) {
          this.blinking = false;
          this.blink_time_left = LNZ.level_questBlinkTime;
        }
        else if (this.blinks_left > 0) {
          this.blinking = true;
          this.blinks_left--;
          this.blink_time_left = LNZ.level_questBlinkTime;
        }
      }
    }
    if (this.met) {
      return;
    }
    switch(this.ID) {
      // Tutorial
      case 1: // select ben
        break;
      case 2: // move toward arrow
        break;
      case 3: // go to room
        break;
      case 4: // kill chicken
        break;
      case 5: // unlock inventory
        break;
      case 6: // use backpack
        if (level.player != null && level.player.inventory.slots.size() > 2) {
          this.meet(level.player);
        }
        break;
      case 7: // damage john rankin
        break;
      case 8: // drink water
        break;
      case 9: // eat food
        break;
      case 10: // sleep
        break;
      case 11: // die
        break;
      case 12: // enter common room
        break;
      // Francis Hall
      case 21: // find molly
        break;
      case 22: // find the thing
        if (level.player != null && level.player.holding(2211)) {
          this.meet(level.player);
        }
        break;
      case 23: // find bens coat
        if (level.player != null && level.player.holding(2512)) {
          this.meet(level.player);
        }
        break;
      case 24: // find bens backpack
        if (level.player != null && level.player.holding(2922)) {
          this.meet(level.player);
        }
        break;
      case 25: // go to room
        break;
      // Frontdoor
      case 31: // get off campus
        break;
      // Ahimdoor
      case 41: // get off campus
        break;
      case 42: // start dans car
        break;
      // Brothersdoor
      case 51:
        break;
      // Chapeldoor
      case 61:
        break;
      // Custodialdoor
      case 71:
        break;
      default:
        p.global.errorMessage("ERROR: Quest ID " + this.ID + " not recognized.");
        break;
    }
  }

  String name() {
    switch(this.ID) {
      // Tutorial
      case 1: // select ben
        return "Select Ben Nelson";
      case 2: // move toward arrow
        return "Move Ben Nelson toward the arrow";
      case 3: // go to room
        return "Go to your room";
      case 4: // kill chicken
        return "Kill the chicken";
      case 5: // unlock inventory
        return "Unlock your inventory";
      case 6: // use backpack
        return "Use your backpack";
      case 7: // damage john rankin
        return "Chase away John Rankin";
      case 8: // drink water
        return "Drink water";
      case 9: // eat food
        return "Eat food";
      case 10: // sleep
        return "Go to sleep";
      case 11: // die
        return "Die";
      case 12: // enter common room
        return "Enter the common room";
      // Francis Hall
      case 21: // find molly
        return "Find Molly";
      case 22: // find the thing
        return "Find The Thing";
      case 23: // find bens coat
        return "Find Ben's Coat";
      case 24: // find bens backpack
        return "Find Ben's Backpack";
      case 25: // go to room
        return "Go to your room";
      // Frontdoor
      case 31: // get off campus
        return "Get off campus";
      // Ahimdoor
      case 41:
        return "Get off campus";
      case 42:
        return "Get Dan's car started";
      // Brothersdoor
      case 51:
        return "";
      // Chapeldoor
      case 61:
        return "";
      // Custodialdoor
      case 71:
        return "";
      default:
        return "";
    }
  }

  String shortDescription() {
    switch(this.ID) {
      // Tutorial
      case 1: // select ben
        return "Select Ben Nelson by left-clicking him.";
      case 2: // move toward arrow
        return "Move Ben Nelson toward the arrow by right-clicking near the arrow.";
      case 3: // go to room
        return "Your room is on the second floor of Francis Hall.";
      case 4: // kill chicken
        return "Attack the chicken by right-clicking it.";
      case 5: // unlock inventory
        return "Unlock your inventory in the HeroTree, which you can open with 'ctrl-t'.";
      case 6: // use backpack
        return "Use your backpack by placing it in your primary hand and pressing 'r'.";
      case 7: // damage john rankin
        return "Chase away John Rankin by damaging him. Your base attack won't be enough.";
      case 8: // drink water
        return "Drink water by right-clicking the water fountain outside your room.";
      case 9: // eat food
        return "There's food in your fridge that will quell your hunger.";
      case 10: // sleep
        return "Go to sleep by right-clicking your bed at night-time.";
      case 11: // die
        return "Die by walking in lava; you will respawn at your bed.";
      case 12: // enter common room
        return "The lava won't damage you while your ultimate is activated.";
      // Francis Hall
      case 21: // find molly
        return "Find Molly and bring her back to Mike Schmiesing.";
      case 22: // find the thing
        return "Find The Thing before Connor Smith finds out you lost it.";
      case 23: // find bens coat
        return "Find Ben's Coat; it's got to be somewhere on Soldier's wing...";
      case 24: // find bens backpack
        return "Find Ben's Backpack; he remembers putting it in storage somewhere.";
      case 25: // go to room
        return "Somehow the door is locked again.";
      // Frontdoor
      case 31: // get off campus
        return "Find a place more remote, preferrably before night-fall.";
      // Ahimdoor
      case 41:
        return "Find a place more remote, preferrably before night-fall.";
      case 42:
        return "You need the car key and to make sure it has gas.";
      // Brothersdoor
      case 51:
        return "";
      // Chapeldoor
      case 61:
        return "";
      // Custodialdoor
      case 71:
        return "";
      default:
        return "";
    }
  }
}