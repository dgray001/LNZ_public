package LNZModule;

import java.util.*;
import processing.core.PConstants;
import Misc.Misc;

class Effect {
  private LNZ p;

  protected int ID = 0;
  protected String display_name = "Effect";

  protected String message = "";
  protected int number = 0;
  protected double decimal1 = 0;
  protected double decimal2 = 0;
  protected Rectangle rectangle;

  Effect(LNZ sketch) {
    this.p = sketch;
    this.rectangle = new Rectangle(sketch);
  }

  void setID(int ID) {
    this.ID = ID;
    switch(ID) {
      case 0: // nothing
      case 1: // console log
      case 2: // LNZ log
      case 3: // timestamp log
      case 4: // header messae
      case 5: // level chat
      case 6: // win level
      case 7: // activate trigger
      case 8: // deactivate trigger
      case 9: // add quest
      case 10: // remove quest
      case 11: // lose control
      case 12: // gain control
      case 13: // move view
      case 14: // tint map
      case 15: // stop tinting map
      case 16: // show blinking arrow
      case 17: // complete quest
      case 18: // add visual effect
      case 19: // move view to player
      case 20: // unit chats
      case 21: // stop unit
      case 22: // stop player
      case 23: // stop units in rectangle
      case 24: // move unit
      case 25: // move player
      case 26: // move units in rectangle
      case 27: // face unit
      case 28: // face player
      case 29: // face units in rectangle
      case 30: // teleport unit
      case 31: // teleport player
      case 32: // teleport units in rectangle
      case 33: // change player hunger
      case 34: // change player thirst
      case 35: // change player mana
      case 36: // set player hunger
      case 37: // set player thirst
      case 38: // set player mana
      case 39: // change unit health
      case 40: // set unit health
      case 41: // change time
      case 42: // set time
      case 43: // add item to player inventory
      case 44: // clear player inventory
      case 45: // create unit
      case 46: // clear level chat
      case 47: // grant hero tree upgrade
      case 48: // refresh cooldown on ability
      case 49: // change terrain in rectangle
      case 50: // add feature
      case 51: // remove features in rectangle
      case 52: // unlock achievement
      case 53: // give unit status effect
      case 54: // give player status effect
      case 55: // give units in rectangle status effect
      case 56: // explore rectangle
      case 57: // decision form
      case 58: // set background music
      case 59: // trigger sleeping
      case 60: // set background volume
      case 61: // trigger player sound
      case 62: // silence player sound
      case 63: // take bens eyes
      case 64: // equip item to unit
      case 65: // toggle unit ai_toggle
      case 66: // reset unit
        break;
      default:
        p.global.errorMessage("ERROR: Effect ID " + ID + " not recognized.");
        break;
    }
    this.setName();
  }

  void setName() {
    switch(this.ID) {
      case 1: // console log
        this.display_name = "Console Log";
        break;
      case 2: // LNZ log
        this.display_name = "LNZ Log";
        break;
      case 3: // timestamp log
        this.display_name = "Timestamp Log";
        break;
      case 4: // header messae
        this.display_name = "Header Message (" + this.number + ")";
        break;
      case 5: // level chat
        this.display_name = "Level Chat";
        break;
      case 6: // win level
        this.display_name = "Win Level (" + this.number + ")";
        break;
      case 7: // activate trigger
        this.display_name = "Activate Trigger (" + this.number + ")";
        break;
      case 8: // deactivate trigger
        this.display_name = "Deactivate Trigger (" + this.number + ")";
        break;
      case 9: // add quest
        this.display_name = "Add Quest (" + this.number + ")";
        break;
      case 10: // remove quest
        this.display_name = "Remove Quest (" + this.number + ")";
        break;
      case 11: // lose control
        this.display_name = "Lose Control";
        break;
      case 12: // gain control
        this.display_name = "Gain Control";
        break;
      case 13: // move view
        this.display_name = "Move View";
        break;
      case 14: // tint map
        this.display_name = "Tint Map (" + this.number + ")";
        break;
      case 15: // stop tinting map
        this.display_name = "Remove Map Tint";
        break;
      case 16: // show blinking arrow
        this.display_name = "Blinking Arrow (" + this.number + ")";
        break;
      case 17: // complete quest
        this.display_name = "Complete Quest (" + this.number + ")";
        break;
      case 18: // add visual effect
        this.display_name = "Add Visual Effect (" + this.number + ")";
        break;
      case 19: // move view to player
        this.display_name = "Move View to Player";
        break;
      case 20: // unit chats
        this.display_name = "Unit Chats (" + this.number + ")";
        break;
      case 21: // stop unit
        this.display_name = "Stop Unit (" + this.number + ")";
        break;
      case 22: // stop player
        this.display_name = "Stop Player";
        break;
      case 23: // stop units in rectangle
        this.display_name = "Stop Units in Rectangle";
        break;
      case 24: // move unit
        this.display_name = "Move Unit (" + this.number + ")";
        break;
      case 25: // move player
        this.display_name = "Move Player";
        break;
      case 26: // move units rectangle
        this.display_name = "Move Units in Rectangle";
        break;
      case 27: // face unit
        this.display_name = "Face Unit (" + this.number + ")";
        break;
      case 28: // face player
        this.display_name = "Face Player";
        break;
      case 29: // face units in rectangle
        this.display_name = "Face Units in Rectangle";
        break;
      case 30: // teleport unit
        this.display_name = "Teleport Unit (" + this.number + ")";
        break;
      case 31: // teleport player
        this.display_name = "Teleport Player";
        break;
      case 32: // teleport units in rectangle
        this.display_name = "Teleport Units in Rectangle";
        break;
      case 33: // change player hunger
        this.display_name = "Change Player Hunger (" + this.number + ")";
        break;
      case 34: // change player thirst
        this.display_name = "Change Player Thirst (" + this.number + ")";
        break;
      case 35: // change player mana
        this.display_name = "Change Player Mana (" + this.number + ")";
        break;
      case 36: // set player hunger
        this.display_name = "Set Player Hunger (" + this.number + ")";
        break;
      case 37: // set player thirst
        this.display_name = "Set Player Thirst (" + this.number + ")";
        break;
      case 38: // set player mana
        this.display_name = "Set Player Mana (" + this.number + ")";
        break;
      case 39: // change unit health
        this.display_name = "Change Unit (" + this.number + ") Health (" + this.decimal1 + ")";
        break;
      case 40: // set unit health
        this.display_name = "Set Unit (" + this.number + ") Health (" + this.decimal1 + ")";
        break;
      case 41: // change time
        this.display_name = "Change Time (" + this.decimal1 + ")";
        break;
      case 42: // set time
        this.display_name = "Set Time (" + this.decimal1 + ")";
        break;
      case 43: // add item to player inventory
        this.display_name = "Add Item to Inventory (" + this.number + ")";
        break;
      case 44: // clear player inventory
        this.display_name = "Clear Player Inventory";
        break;
      case 45: // create unit
        this.display_name = "Create Unit (" + this.number + ")";
        break;
      case 46: // clear level chat
        this.display_name = "Clear Level Chat";
        break;
      case 47: // grant hero tree upgrade
        this.display_name = "Give Player Upgrade (" + HeroTreeCode.codeFromId(this.number) + ")";
        break;
      case 48: // refresh cooldown on ability
        this.display_name = "Refresh Ability (" + this.number + ")";
        break;
      case 49: // change terrain in rectangle
        this.display_name = "Change Terrain to (" + this.number + ")";
        break;
      case 50: // add feature
        this.display_name = "Add Feature (" + this.number + ")";
        break;
      case 51: // remove features in rectangle
        this.display_name = "Remove Features from rectangle";
        break;
      case 52: // unlock achievement
        this.display_name = "Unlock Achievement (" + this.number + ")";
        break;
      case 53: // give unit status effect
        this.display_name = "Give Unit (" + this.number + ") Status Effect " + this.message;
        break;
      case 54: // give player status effect
        this.display_name = "Give Player Status Effect " + this.message;
        break;
      case 55: // give units in rectangle status effect
        this.display_name = "Give Units in Rectangle Status Effect " + this.message;
        break;
      case 56: // explore rectangle
        this.display_name = "Explore Rectangle: " + this.rectangle.fileString();
        break;
      case 57: // decision form
        this.display_name = "Decision Form (" + this.number + ")";
        break;
      case 58: // set background music
        this.display_name = "Play Background Music (" + this.message + ")";
        break;
      case 59: // trigger sleeping
        this.display_name = "Trigger Sleeping";
        break;
      case 60: // set background volume
        if (this.number > 0) {
          this.display_name = "Set Background Volume (" + this.decimal1 + ")";
        }
        else {
          this.display_name = "Mute Background Volume";
        }
        break;
      case 61: // trigger player sound
        this.display_name = "Trigger Player Sound (" + this.message + ")";
        break;
      case 62: // silence player sound
        this.display_name = "Silence Player Sound (" + this.message + ")";
        break;
      case 63: // take bens eyes
        this.display_name = "Remove Ben's Eyes";
        break;
      case 64: // equip item to unit
        this.display_name = "Equip Item (" + Math.round(this.decimal1) + ") to Unit (" + this.number + ")";
        break;
      case 65: // toggle ai toggle
        if (this.decimal1 > 0) {
          this.display_name = "AI Toggle (true) to Unit (" + this.number + ")";
        }
        else if (this.decimal1 < 0) {
          this.display_name = "AI Toggle (false) to Unit (" + this.number + ")";
        }
        else {
          this.display_name = "AI Toggle (toggle) to Unit (" + this.number + ")";
        }
        break;
      case 66: // reset unit
        this.display_name = "Reset Unit (" + this.number + ")";
        break;
      default:
        this.display_name = "Effect";
        break;
    }
  }

  void actuate(Level level) {
    StatusEffectCode code = null;
    switch(this.ID) {
      case 0: // nothing
        break;
      case 1: // console log
        System.out.println(this.message);
        break;
      case 2: // LNZ log
        p.global.log(this.message);
        break;
      case 3: // Timestamp log
        p.global.log(p.millis() + this.message);
        break;
      case 4: // header message
        if (level.curr_map != null) {
          level.curr_map.addHeaderMessage(this.message, this.number);
        }
        break;
      case 5: // level chat
        level.chat(this.message);
        break;
      case 6: // win level
        level.complete(this.number);
        break;
      case 7: // activate trigger
        if (level.triggers.containsKey(this.number)) {
          level.triggers.get(this.number).active = true;
        }
        break;
      case 8: // deactivate trigger
        if (level.triggers.containsKey(this.number)) {
          level.triggers.get(this.number).active = false;
        }
        break;
      case 9: // add quest
        level.addQuest(this.number);
        break;
      case 10: // remove quest
        level.removeQuest(this.number);
        break;
      case 11: // Lose control
        level.loseControl();
        break;
      case 12: // gain control
        level.gainControl();
        break;
      case 13: // move view
        if (level.curr_map != null) {
          level.curr_map.setViewLocation(this.rectangle.center());
        }
        break;
      case 14: // tint map
        if (level.curr_map != null) {
          level.curr_map.show_tint = true;
          if (this.number == 0) { // default to brown with transparency 100
            level.curr_map.color_tint = 1681075482;
          }
          else {
            level.curr_map.color_tint = this.number;
          }
        }
        break;
      case 15: // stop tinting map
        if (level.curr_map != null) {
          level.curr_map.show_tint = false;
        }
        break;
      case 16: // show blinking arrow
        int frame = (int)(LNZ.gif_arrow_frames * ((double)(p.millis() %
          LNZ.gif_arrow_time) / (1 + LNZ.gif_arrow_time)));
          double translate_x = 0;
        switch(this.number) {
          case 1: // toward buttons
            translate_x = level.xf - 80;
            p.translate(translate_x, 0.9 * p.height);
            p.rotate(0.1 * PConstants.PI);
            p.imageMode(PConstants.CENTER);
            p.image(p.global.images.getImage("gifs/arrow/" + frame + ".png"), 0, 0, 130, 130);
            p.rotate(-0.1 * PConstants.PI);
            p.translate(-translate_x, -0.9 * p.height);
            break;
          case 2: // panel collapse buttons
            translate_x = level.xf - 80;
            p.translate(translate_x, 0.05 * p.height);
            p.rotate(-0.2 * PConstants.PI);
            p.imageMode(PConstants.CENTER);
            p.image(p.global.images.getImage("gifs/arrow/" + frame + ".png"), 0, 0, 130, 130);
            p.rotate(0.2 * PConstants.PI);
            p.translate(-translate_x, -0.05 * p.height);
            translate_x = level.xi + 80;
            p.translate(translate_x, 0.05 * p.height);
            p.rotate(-0.8 * PConstants.PI);
            p.imageMode(PConstants.CENTER);
            p.image(p.global.images.getImage("gifs/arrow/" + frame + ".png"), 0, 0, 130, 130);
            p.rotate(0.8 * PConstants.PI);
            p.translate(-translate_x, -0.05 * p.height);
            break;
          case 3: // inventory bar
            translate_x = 0.5 * p.width;
            double translate_y = level.player.inventory_bar.yi - 70;
            p.translate(translate_x, translate_y);
            p.rotate(0.5 * PConstants.PI);
            p.imageMode(PConstants.CENTER);
            p.image(p.global.images.getImage("gifs/arrow/" + frame + ".png"), 0, 0, 130, 130);
            p.rotate(-0.5 * PConstants.PI);
            p.translate(-translate_x, -translate_y);
            break;
          case 4: // player left panel form
            translate_x = level.xi + 65;
            p.translate(translate_x, 0.55 * p.height);
            p.rotate(PConstants.PI);
            p.imageMode(PConstants.CENTER);
            p.image(p.global.images.getImage("gifs/arrow/" + frame + ".png"), 0, 0, 130, 130);
            p.rotate(-PConstants.PI);
            p.translate(-translate_x, -0.55 * p.height);
            break;
          case 5: // chatbox
            translate_x = level.xf - 65;
            p.translate(translate_x, 0.3 * p.height);
            p.imageMode(PConstants.CENTER);
            p.image(p.global.images.getImage("gifs/arrow/" + frame + ".png"), 0, 0, 130, 130);
            p.translate(-translate_x, -0.3 * p.height);
            break;
          case 6: // questbox
            translate_x = level.xf - 65;
            p.translate(translate_x, 0.08 * p.height);
            p.imageMode(PConstants.CENTER);
            p.image(p.global.images.getImage("gifs/arrow/" + frame + ".png"), 0, 0, 130, 130);
            p.translate(-translate_x, -0.08 * p.height);
            break;
          case 7: // selected object
            translate_x = level.xi + 65;
            p.translate(translate_x, 0.1 * p.height);
            p.rotate(PConstants.PI);
            p.imageMode(PConstants.CENTER);
            p.image(p.global.images.getImage("gifs/arrow/" + frame + ".png"), 0, 0, 130, 130);
            p.rotate(-PConstants.PI);
            p.translate(-translate_x, -0.1 * p.height);
            break;
          case 8: // move toward arrow
            translate_x = 0.5 * p.width + 200;
            translate_y = 0.5 * p.height + 200;
            p.translate(translate_x, translate_y);
            p.rotate(0.2 * PConstants.PI);
            p.imageMode(PConstants.CENTER);
            p.image(p.global.images.getImage("gifs/arrow/" + frame + ".png"), 0, 0, 130, 130);
            p.rotate(-0.2 * PConstants.PI);
            p.translate(-translate_x, -translate_y);
            break;
          case 9: // xp/level
            translate_x = level.xi + 65;
            p.translate(translate_x, 0.65 * p.height);
            p.rotate(PConstants.PI);
            p.imageMode(PConstants.CENTER);
            p.image(p.global.images.getImage("gifs/arrow/" + frame + ".png"), 0, 0, 130, 130);
            p.rotate(-PConstants.PI);
            p.translate(-translate_x, -0.65 * p.height);
            break;
          default:
            p.global.errorMessage("ERROR: Blinking arrow ID " + this.number + " not recognized.");
            break;
        }
        break;
      case 17: // complete quest
        if (level.player == null) {
          break;
        }
        if (level.quests.containsKey(this.number)) {
          level.quests.get(this.number).meet(level.player);
        }
        break;
      case 18: // add visual effect
        if (level.curr_map != null) {
          level.curr_map.addVisualEffect(this.number, this.rectangle.centerX(), this.rectangle.centerY());
        }
        break;
      case 19: // move view to player
        if (level.curr_map != null) {
          if (level.player != null) {
            level.curr_map.setViewLocation(level.player.coordinate.copy());
          }
        }
        break;
      case 20: // unit chats
        if (level.curr_map == null) {
          break;
        }
        Unit unit_chatting = level.curr_map.units.get(this.number);
        if (unit_chatting == null) {
          break;
        }
        level.chat(unit_chatting.displayName() + ": " + this.message);
        // TODO: Adjust position based on unit height
        level.curr_map.addVisualEffect(4009,
          unit_chatting.coordinate.x - 0.6,
          unit_chatting.coordinate.y - 1.2);
        level.curr_map.selected_object = unit_chatting;
        break;
      case 21: // stop unit
        if (level.curr_map == null) {
          break;
        }
        if (level.curr_map.units.containsKey(this.number)) {
          level.curr_map.units.get(this.number).stopAction();
        }
        break;
      case 22: // stop player
        if (level.player != null) {
          level.player.stopAction();
        }
        break;
      case 23: // stop units in rectangle
        if (level.curr_map == null) {
          break;
        }
        for (Map.Entry<Integer, Unit> entry : level.curr_map.units.entrySet()) {
          if (this.rectangle.contains(entry.getValue())) {
            entry.getValue().stopAction();
          }
        }
        break;
      case 24: // move unit
        if (level.curr_map == null) {
          break;
        }
        if (level.curr_map.units.containsKey(this.number)) {
          level.curr_map.units.get(this.number).moveTo(this.rectangle.centerX(), this.rectangle.centerY(), level.curr_map);
        }
        break;
      case 25: // move player
        if (level.curr_map == null) {
          break;
        }
        if (level.player != null) {
          level.player.moveTo(this.rectangle.centerX(), this.rectangle.centerY(), level.curr_map);
        }
        break;
      case 26: // move units in rectangle
        if (level.curr_map == null) {
          break;
        }
        for (Map.Entry<Integer, Unit> entry : level.curr_map.units.entrySet()) {
          if (this.rectangle.contains(entry.getValue())) {
            entry.getValue().moveTo(this.decimal1, this.decimal2, level.curr_map);
          }
        }
        break;
      case 27: // face unit
        if (level.curr_map == null) {
          break;
        }
        if (level.curr_map.units.containsKey(this.number)) {
          level.curr_map.units.get(this.number).turnTo(PConstants.PI * this.decimal1 / 180.0);
        }
        break;
      case 28: // face player
        if (level.player != null) {
          level.player.turnTo(PConstants.PI * this.decimal1 / 180.0);
        }
        break;
      case 29: // face units in rectangle
        if (level.curr_map == null) {
          break;
        }
        for (Map.Entry<Integer, Unit> entry : level.curr_map.units.entrySet()) {
          if (this.rectangle.contains(entry.getValue())) {
            entry.getValue().turnTo(PConstants.PI * this.decimal1 / 180.0);
          }
        }
        break;
      case 30: // teleport unit
        if (level.curr_map == null) {
          break;
        }
        if (level.curr_map.units.containsKey(this.number)) {
          level.curr_map.units.get(this.number).teleport(level.curr_map,
            this.rectangle.centerX(), this.rectangle.centerY());
        }
        break;
      case 31: // teleport player
        if (level.player != null) {
          level.player.teleport(level.curr_map,
            this.rectangle.centerX(), this.rectangle.centerY());
        }
        break;
      case 32: // teleport units in rectangle
        if (level.curr_map == null) {
          break;
        }
        for (Map.Entry<Integer, Unit> entry : level.curr_map.units.entrySet()) {
          if (this.rectangle.contains(entry.getValue())) {
            entry.getValue().setLocation(this.decimal1, this.decimal2);
          }
        }
        break;
      case 33: // change player hunger
        if (level.player != null) {
          level.player.changeHunger(this.number);
        }
        break;
      case 34: // change player thirst
        if (level.player != null) {
          level.player.changeThirst(this.number);
        }
        break;
      case 35: // change player mana
        if (level.player != null) {
          level.player.changeMana(this.number);
        }
        break;
      case 36: // set player hunger
        if (level.player != null) {
          level.player.setHunger(this.number);
        }
        break;
      case 37: // set player thirst
        if (level.player != null) {
          level.player.setThirst(this.number);
        }
        break;
      case 38: // set player mana
        if (level.player != null) {
          level.player.setMana(this.number);
        }
        break;
      case 39: // change unit health
        if (level.curr_map == null) {
          break;
        }
        if (level.curr_map.units.containsKey(this.number)) {
          level.curr_map.units.get(this.number).changeHealth(this.decimal1);
        }
        break;
      case 40: // set unit health
        if (level.curr_map == null) {
          break;
        }
        if (level.curr_map.units.containsKey(this.number)) {
          level.curr_map.units.get(this.number).setHealth(this.decimal1);
        }
        break;
      case 41: // change time
        level.time.add(this.decimal1);
        break;
      case 42: // set time
        level.time.set(this.decimal1);
        break;
      case 43: // add item to player inventory
        if (level.player != null) {
          Item leftover = level.player.inventory.stash(new Item(p, this.number));
          if (leftover != null && !leftover.remove && level.curr_map != null) {
            level.curr_map.addItem(leftover, level.player.frontX(), level.player.frontY());
          }
        }
        break;
      case 44: // clear player inventory
        if (level.player != null) {
          level.player.inventory.clear();
        }
        break;
      case 45: // create unit
        if (level.curr_map != null) {
          Unit u = new Unit(p, this.number, this.rectangle.center());
          if (this.decimal1 > 0) {
            level.curr_map.addUnit(u, (int)Math.round(this.decimal1));
          }
          else {
            level.curr_map.addUnit(u);
          }
        }
        break;
      case 46: // clear level chat
        if (level.level_chatbox != null) {
          level.level_chatbox.clearText();
        }
        break;
      case 47: // grant hero tree upgrade
        if (level.player != null) {
          level.player.upgrade(HeroTreeCode.codeFromId(this.number));
        }
        break;
      case 48: // refresh cooldown on ability
        if (level.player == null) {
          break;
        }
        if (this.number < 0 || this.number >= level.player.abilities.size()) {
          break;
        }
        if (level.player.abilities.get(this.number) == null) {
          break;
        }
        level.player.abilities.get(this.number).timer_cooldown = 0;
        break;
      case 49: // change terrain in rectangle
        if (level.curr_map == null) {
          break;
        }
        for (int i = (int)Math.round(this.rectangle.xi); i < (int)Math.round(this.rectangle.xf); i++) {
          for (int j = (int)Math.round(this.rectangle.yi); j < (int)Math.round(this.rectangle.yf); j++) {
            try {
              level.curr_map.setTerrain(this.number, i, j, false);
            } catch(Exception e) {}
          }
        }
        level.curr_map.refreshTerrainImage();
        break;
      case 50: // add feature
        if (level.curr_map != null) {
          Feature f = new Feature(p, this.number,
            Math.floor(this.rectangle.xi), Math.floor(this.rectangle.yi));
          f.number = (int)Math.round(this.decimal1);
          level.curr_map.addFeature(f);
        }
        break;
      case 51: // remove features in rectangle
        if (level.curr_map != null) {
          for (Feature f : level.curr_map.features()) {
            if (this.rectangle.contains(f)) {
              level.curr_map.removeFeature(f.map_key);
            }
          }
        }
        break;
      case 52: // unlock achievement
        p.global.profile.achievement(AchievementCode.achievementCode(this.number));
        break;
      case 53: // give unit status effect
        code = StatusEffectCode.code(this.message);
        if (code == null || code == StatusEffectCode.ERROR) {
          break;
        }
        if (level.curr_map == null) {
          break;
        }
        if (level.curr_map.units.containsKey(this.number)) {
          if (this.decimal1 > 0) {
            level.curr_map.units.get(this.number).refreshStatusEffect(code, this.decimal1);
          }
          else {
            level.curr_map.units.get(this.number).addStatusEffect(code);
          }
        }
        break;
      case 54: // give player status effect
        code = StatusEffectCode.code(this.message);
        if (code == null || code == StatusEffectCode.ERROR) {
          break;
        }
        if (level.player == null) {
          break;
        }
        if (this.decimal1 > 0) {
          level.player.refreshStatusEffect(code, this.decimal1);
        }
        else {
          level.player.addStatusEffect(code);
        }
        break;
      case 55: // give units in rectangle status effect
        code = StatusEffectCode.code(this.message);
        if (code == null || code == StatusEffectCode.ERROR) {
          break;
        }
        if (level.curr_map == null) {
          break;
        }
        for (Map.Entry<Integer, Unit> entry : level.curr_map.units.entrySet()) {
          if (this.rectangle.contains(entry.getValue())) {
            if (this.decimal1 > 0) {
              entry.getValue().refreshStatusEffect(code, this.decimal1);
            }
            else {
              entry.getValue().addStatusEffect(code);
            }
          }
        }
        break;
      case 56: // explore rectangle
        if (level.curr_map == null) {
          break;
        }
        if (!level.currMapName.equals(this.rectangle.mapName)) {
          break;
        }
        level.curr_map.exploreRectangle(this.rectangle);
        break;
      case 57: // decision form
        level.decisionForm(this.number);
        break;
      case 58: // set background music
        p.global.sounds.play_background(this.message);
        level.album_name = this.message;
        break;
      case 59: // trigger sleeping
        level.sleeping = true;
        level.sleep_timer = LNZ.feature_bedSleepTimer;
        level.loseControl();
        if (level.player != null) {
          level.player.stopAction();
        }
        break;
      case 60: // set background volume
        if (this.number > 0) {
          p.global.profile.options.volume_music = this.decimal1;
          p.global.profile.options.volume_music_muted = false;
        }
        else {
          p.global.profile.options.volume_music = this.decimal1;
          p.global.profile.options.volume_music_muted = true;
        }
        p.global.profile.options.change();
        break;
      case 61: // trigger player sound
        p.global.sounds.trigger_player(this.message);
        break;
      case 62: // silence player sound
        p.global.sounds.silence_player(this.message);
        break;
      case 63: // take bens eyes
        p.global.profile.ben_has_eyes = false;
        break;
      case 64: // equip item to unit
        if (level.curr_map == null) {
          break;
        }
        Item i = new Item(p, (int)Math.round(this.decimal1));
        i.ammo = (int)Math.round(this.decimal2);
        if (i == null || i.remove) {
          break;
        }
        if (level.curr_map.units.containsKey(this.number)) {
          level.curr_map.units.get(this.number).pickup(i);
        }
        break;
      case 65: // toggle ai toggle
        if (level.curr_map == null) {
          break;
        }
        if (!level.curr_map.units.containsKey(this.number)) {
          break;
        }
        if (this.decimal1 > 0) {
          level.curr_map.units.get(this.number).ai_toggle = true;
        }
        else if (this.decimal1 < 0) {
          level.curr_map.units.get(this.number).ai_toggle = false;
        }
        else {
          level.curr_map.units.get(this.number).ai_toggle = !level.curr_map.units.get(this.number).ai_toggle;
        }
        break;
      case 66: // reset unit
        if (level.curr_map == null) {
          break;
        }
        if (!level.curr_map.units.containsKey(this.number)) {
          break;
        }
        Unit u = level.curr_map.units.get(this.number);
        u.statuses.clear();
        u.curr_health = u.health();
        u.stopAction();
        break;
      default:
        p.global.errorMessage("ERROR: Effect ID " + ID + " not recognized.");
        break;
    }
  }

  String fileString() {
    String fileString = "\nnew: Effect";
    fileString += "\nID: " + this.ID;
    fileString += "\nmessage: " + this.message;
    fileString += "\nnumber: " + this.number;
    fileString += "\ndecimal1: " + this.decimal1;
    fileString += "\ndecimal2: " + this.decimal2;
    fileString += "\nrectangle: " + this.rectangle.fileString();
    fileString += "\nend: Effect\n";
    return fileString;
  }

  void addData(String datakey, String data) {
    switch(datakey) {
      case "ID":
        this.setID(Misc.toInt(data));
        break;
      case "message":
        this.message = data;
        break;
      case "number":
        this.number = Misc.toInt(data);
        break;
      case "decimal1":
        this.decimal1 = Misc.toDouble(data);
        break;
      case "decimal2":
        this.decimal2 = Misc.toDouble(data);
        break;
      case "rectangle":
        this.rectangle.addData(data);
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not recognized for Effect object.");
        break;
    }
  }
}