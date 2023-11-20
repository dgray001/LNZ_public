package LNZModule;

import java.util.*;
import processing.core.*;
import Misc.Misc;

class Ability {
  private LNZ p;

  protected int ID = 0;
  protected double timer_cooldown = 0;
  protected double timer_other = 0;
  protected Unit target_unit = null;
  protected int target_key = -1;
  private int stacks = 0;
  private boolean toggle = false;
  private Set<Integer> currently_hit;

  Ability(LNZ sketch, int ID) {
    this.p = sketch;
    this.ID = ID;
    switch(this.ID) {
      // Ben Nelson
      case 101:
      case 102:
      case 104:
      case 105:
      case 106:
      case 107:
      case 109:
      case 110:
        break;
      case 103:
      case 108:
        this.currently_hit = new HashSet<Integer>();
        break;
      // Daniel Gray
      case 111:
      case 112:
      case 113:
      case 114:
      case 115:
      case 116:
      case 117:
      case 118:
      case 119:
      case 120:
        break;
      // Cathy Heck, zombie
      case 1001:
        this.currently_hit = new HashSet<Integer>();
        break;
      case 1002:
      case 1003:
        break;
      // Matt Schaefer, zombie
      case 1011:
        break;
      // Ben Kohring, zombie
      case 1021:
      case 1022:
        break;
      default:
        p.global.errorMessage("ERROR: Ability ID " + this.ID + " not found.");
        break;
    }
  }

  static String abilityName(int ID) {
    return (new Ability(null, ID)).displayName();
  }

  String displayName() {
    switch(this.ID) {
      // Ben Nelson
      case 101:
        return "Fearless Leader";
      case 102:
        return "Mighty Pen";
      case 103:
        return "Nelson Glare";
      case 104:
        return "Senseless Grit";
      case 105:
        return "Rage of the Ben";
      case 106:
        return "Fearless Leader II";
      case 107:
        return "Mighty Pen II";
      case 108:
        return "Nelson Glare II";
      case 109:
        return "Senseless Grit II";
      case 110:
        return "Rage of the Ben II";
      // Daniel Gray
      case 111:
        return "Aposematic Camouflage";
      case 112:
        return "Tongue Lash";
      case 113:
        return "Amphibious Leap";
      case 114:
        return "Alkaloid Secretion";
      case 115:
        return "Anuran Appetite";
      case 116:
        return "Aposematic Camouflage II";
      case 117:
        return "Tongue Lash II";
      case 118:
        return "Amphibious Leap II";
      case 119:
        return "Alkaloid Secretion II";
      case 120:
        return "Anuran Appetite II";
      // Cathy Heck, zombie
      case 1001:
        return "Blow Smoke";
      case 1002:
        return "Condom Throw";
      case 1003:
        return "Title IX Charge";
      // Matt Schaefer, zombie
      case 1011:
        return "";
      // Ben Kohring, zombie
      case 1021:
        return "Rage Run";
      case 1022:
        return "Rock Throw";
      default:
        return "ERROR";
    }
  }

  String description() {
    switch(this.ID) {
      // Ben Nelson
      case 101:
        return "Ben is our fearless leader so never backs down from a fight.\n" +
          "Each time you damage, are damaged, or receive a negative status " +
          "effect, gain " + LNZ.ability_101_rageGain + "% rage, to a " +
          "maximum of 100. Also gain " + LNZ.ability_101_rageGainKill +
          "% rage for a kill. After not increasing rage for " + (LNZ.
          ability_101_cooldownTimer/1000) + "s your rage will decrease by 1% " +
          "every " + (LNZ.ability_101_tickTimer/1000) + "s.\nGain +" +
          (100*LNZ.ability_101_bonusAmount) + "% bonus tenacity and attack " +
          "speed for every rage you have.";
      case 102:
        return "Ben knows well the Pen is mightier than the Sword.\nThrow a " +
          "pen with " + LNZ.ability_102_powerBase + " + (" + Math.round(LNZ.
          ability_102_powerRatio*100) + "% attack power + " + Math.round(LNZ.ability_102_powerRatio*
          100) + "% magic power) mixed power " + LNZ.ability_102_distance + "m. If " +
          "Ben is holding a pen or pencil it has " + LNZ.ability_102_powerBasePen +
          " (" + Math.round(LNZ.ability_102_powerRatioPen*100) + "% attack power + " + Math.round(LNZ.
          ability_102_powerRatioPen*100) + "% magic power) mixed power.\nIf Mighty Pen hits " +
          "a target Ben will heal for " + (LNZ.ability_102_healRatio*100) +
          "% of damage dealt.";
      case 103:
        return "We all cower when Ben throws his glare our way.\nFace target " +
          "direction and glare at all enemies in a cone " + LNZ.
          ability_103_range + "m long, lowering their attack and movement speed " +
          "by " + Math.round((1-LNZ.ability_103_debuff)*100) + "% for " + (LNZ.
          ability_103_time/1000) + "s.\nEnemies in the center of the cone are " +
          "also silenced for the duration.";
      case 104:
        return "Ben's grit transcends all reason.\nPassive: Heal " + Math.round(
          LNZ.ability_104_passiveHealAmount*100) + "% missing health " +
          "every " + (LNZ.ability_104_passiveHealTimer/1000) + "s.\nActive: " +
          "Heal " + Math.round(LNZ.ability_104_activeHealAmount*100) + "% " +
          "missing health and gain " + Math.round((LNZ.ability_104_speedBuff-1)*100) +
          "% move speed when targeting enemies for " + (LNZ.
          ability_104_speedBuffTimer/1000) + "s.";
      case 105:
        return "Ben unleashes the totality of his rage.\nInstantly gain " +
          LNZ.ability_105_rageGain + " rage, become invulnerable and " +
          "gain +" + Math.round((LNZ.ability_105_buffAmount-1)*100) + "% attack " +
          "for " + (LNZ.ability_105_buffTime/1000) + "s.\nDuring Rage of " +
          "the Ben:\n - Your passive rage increases are increased by " + Math.round(
          (LNZ.ability_105_rageGainBonus-1)*100) + "%\n - You gain a " + Math.round(
          (LNZ.ability_105_fullRageBonus-1)*100) + "% bonus to tenacity and " +
          "attack speed if your rage is at 100%\n - You cannot cast Mighty Pen (a)";
      case 106:
        return "Ben is our fearless leader so never backs down from a fight.\n" +
          "Each time you damage, are damaged, or receive a negative status " +
          "effect, gain " + LNZ.ability_106_rageGain + "% rage, to a " +
          "maximum of 100. Also gain " + LNZ.ability_106_rageGainKill +
          "% rage for a kill. After not increasing rage for " + LNZ.
          ability_106_cooldownTimer/1000 + "s your rage will decrease by 1% " +
          "every " + LNZ.ability_106_tickTimer/1000 + "s.\nGain +" +
          LNZ.ability_106_bonusAmount + "% bonus tenacity and attack " +
          "speed for every rage you have.";
      case 107:
        return "Ben knows well the Pen is mightier than the Sword.\nThrow a " +
          "pen with " + LNZ.ability_107_powerBase + " + (" + Math.round(LNZ.
          ability_107_powerRatio*100) + "% attack power + " + Math.round(LNZ.ability_107_powerRatio*
          100) + "% magic power) mixed power " + LNZ.ability_107_distance + "m. If " +
          "Ben is holding a pen or pencil it has " + LNZ.ability_107_powerBasePen +
          " (" + Math.round(LNZ.ability_107_powerRatioPen*100) + "% attack power + " + Math.round(LNZ.
          ability_107_powerRatioPen*100) + "% magic power) mixed power.\nIf Mighty Pen hits " +
          "a target Ben will heal for " + Math.round(LNZ.ability_107_healRatio*100) +
          "% of damage dealt.";
      case 108:
        return "We all cower when Ben throws his glare our way.\nFace target " +
          "direction and glare at all enemies in a cone " + LNZ.
          ability_108_range + "m long, lowering their attack and movement speed " +
          "by " + Math.round((1-LNZ.ability_108_debuff)*100) + "% for " + (LNZ.
          ability_108_time/1000) + "s.\nEnemies in the center of the cone are " +
          "also silenced for the duration.";
      case 109:
        return "Ben's grit transcends all reason.\nPassive: Heal " + Math.round(
          LNZ.ability_109_passiveHealAmount*100) + "% missing health " +
          "every " + (LNZ.ability_109_passiveHealTimer/1000) + "s.\nActive: " +
          "Heal " + Math.round(LNZ.ability_109_activeHealAmount*100) + "% " +
          "missing health and gain " + Math.round((LNZ.ability_109_speedBuff-1)*100) +
          "% move speed when targeting enemies for " + (LNZ.
          ability_109_speedBuffTimer/1000) + "s.";
      case 110:
        return "Ben unleashes the totality of his rage.\nInstantly gain " +
          LNZ.ability_110_rageGain + " rage, become invulnerable and " +
          "gain +" + Math.round((LNZ.ability_110_buffAmount-1)*100) + "% attack " +
          "for " + (LNZ.ability_110_buffTime/1000) + "s.\nDuring Rage of " +
          "the Ben:\n - Your passive rage increases are increased by " + Math.round(
          (LNZ.ability_110_rageGainBonus-1)*100) + "%\n - You gain a " + Math.round(
          (LNZ.ability_110_fullRageBonus-1)*100) + "% bonus to tenacity and " +
          "attack speed if your rage is at 100%\n - You cannot cast Mighty Pen (a)";
      // Daniel Gray
      case 111:
        return "As a Frog-Human hybrid, Dan can express his inner frog enery.\n" +
          "If still for " + (int)(Math.round(LNZ.ability_111_stillTime)) +
          "s, enemies cannot see you without getting within " + LNZ.
          ability_111_distance + "m.\nWhile Dan is camouflaged, attacking or " +
          "casting \'Tongue Lash\' or \'Amphibious Leap\' will have " + (int)(
          100.0 * (LNZ.ability_111_powerBuff-1)) + "% more power.\n\nDan " +
          "also absorbs frog energy from his surroundings, regenerating a frog " +
          "energy every " + LNZ.ability_111_regenTime + "ms.";
      case 112:
        return "Dan lashes out his frog-like tongue.\nLash your tongue " + LNZ.
          ability_112_distance + "m and damage the first enemy it hits for " +
          LNZ.ability_112_basePower + " + (" + Math.round(LNZ.ability_112_physicalRatio
          *100) + "% attack power + " + Math.round(LNZ.ability_112_magicalRatio*100) +
          "% magic power) magical power.\nThe enemy hit will also be slowed " +
          (int)((1-LNZ.ability_112_slowAmount)*100) + "% for " + (LNZ.
          ability_112_slowTime/1000) + "s.";
      case 113:
        return "Dan leaps like the frog he believes he is.\nJump " + LNZ.
          ability_113_jumpDistance + "m and with " + LNZ.ability_113_basePower +
          " + (" + Math.round(LNZ.ability_113_physicalRatio*100) + "% attack power " +
          "+ " + Math.round(LNZ.ability_113_magicalRatio*100) + "% magic power) " +
          "magical power deal splash damage and stun for " + (LNZ.ability_113_stunTime/
          1000) + "s all enemies within a " + LNZ.ability_113_splashRadius +
          "m radius of where you land.\nKills decrease the remaining cooldown of " +
          "\'Tongue Lash\' by " + ((1-LNZ.ability_113_killCooldownReduction)*100) +
          "%.\nIf drenched, the jump distance is " + LNZ.ability_113_drenchedJumpDistance +
          "m and the splash radius is " + LNZ.ability_113_drenchedSplashRadius + "m.";
      case 114:
        return "Dan secretes poisonous alkaloids in all directions around him.\n" +
          "While this ability is active, Dan will deal " + Math.round(100*
          LNZ.ability_114_currHealth) + "% curr health and additional " +
          "damage with " + LNZ.ability_114_basePower + " + (0% attack " +
          "power + " + Math.round(LNZ.ability_114_magicRatio*100) + "% magic " +
          "power) magical power to all enemies within " + Math.round(10 * LNZ.
          ability_114_range)/10.0 + "m of him every " + LNZ.ability_114_tickTime +
          "ms.\nEnemies hit by \'Alkaloid Secretion\' will also be rotting for " +
          (LNZ.ability_114_rotTime/1000.0) + "s.\nThe mana cost is consumed " +
          "every time damage is dealt.";
      case 115:
        return "With a frog's appetite, Dan is sometimes more interested in " +
          "eating his enemies than actually killing them.\nDevour target enemy " +
          "within " + LNZ.ability_115_range + "m, making them suppressed, " +
          "untargetable, and decayed while devoured.\nRecast this ability to spit " +
          "them out " + LNZ.ability_115_regurgitateDistance + "m and deal " +
          "damage with " + LNZ.ability_115_basePower + " + (" + Math.round(100*LNZ.
          ability_115_physicalRatio) + "% attack power + " + Math.round(100*LNZ.
          ability_115_magicalRatio) + "% magic power) magic damage.\nIf this ability " +
          "is not recast within " + (LNZ.ability_115_maxTime/1000.0) + "s " +
          "it will be automatically recast.";
      case 116:
        return "As a Frog-Human hybrid, Dan can express his inner frog enery.\n" +
          "If still for " + (int)(Math.round(LNZ.ability_116_stillTime)) +
          "s, enemies cannot see you without getting within " + LNZ.
          ability_116_distance + "m.\nWhile Dan is camouflaged, attacking or " +
          "casting \'Tongue Lash\' or \'Amphibious Leap\' will have " + (int)(
          100.0 * (LNZ.ability_116_powerBuff-1)) + "% more power.\n\nDan " +
          "also absorbs frog energy from his surroundings, regenerating a frog " +
          "energy every " + LNZ.ability_116_regenTime + "ms.";
      case 117:
        return "Dan lashes out his frog-like tongue.\nLash your tongue " + LNZ.
          ability_117_distance + "m and damage the first enemy it hits for " +
          LNZ.ability_117_basePower + " + (" + Math.round(LNZ.ability_117_physicalRatio
          *100) + "% attack power + " + Math.round(LNZ.ability_117_magicalRatio*100) +
          "% magic power) magical power.\nThe enemy hit will also be slowed " +
          (int)((1-LNZ.ability_112_slowAmount)*100) + "% for " + (LNZ.
          ability_117_slowTime/1000) + "s.";
      case 118:
        return "Dan leaps like the frog he always wanted to be.\nJump " + LNZ.
          ability_118_jumpDistance + "m and with " + LNZ.ability_118_basePower +
          " + (" + Math.round(LNZ.ability_118_physicalRatio*100) + "% attack power " +
          "+ " + Math.round(LNZ.ability_118_magicalRatio*100) + "% magic power) " +
          "magical power deal splash damage and stun for " + (LNZ.ability_118_stunTime/
          1000) + "s all enemies within a " + LNZ.ability_118_splashRadius +
          "m radius of where you land.\nKills decrease the remaining cooldown of " +
          "\'Tongue Lash\' by " + ((1-LNZ.ability_118_killCooldownReduction)*100) +
          "%.\nIf drenched, the jump distance is " + LNZ.ability_118_drenchedJumpDistance +
          "m and the splash radius is " + LNZ.ability_118_drenchedSplashRadius + "m.";
      case 119:
        return "Dan secretes poisonous alkaloids in all directions around him.\n" +
          "While this ability is active, Dan will deal " + Math.round(100*
          LNZ.ability_119_currHealth) + "% curr health and additional " +
          "damage with " + LNZ.ability_119_basePower + " + (0% attack " +
          "power + " + Math.round(LNZ.ability_119_magicRatio*100) + "% magic " +
          "power) magical power to all enemies within " + Math.round(10 * LNZ.
          ability_119_range)/10.0 + " m of him every " + LNZ.ability_114_tickTime +
          "ms.\nEnemies hit by \'Alkaloid Secretion\' will also be rotting for " +
          (LNZ.ability_114_rotTime/1000.0) + "s.\nThe mana cost is consumed " +
          "every time damage is dealt.";
      case 120:
        return "With a frog's appetite, Dan is sometimes more interested in " +
          "eating his enemies than actually killing them.\nDevour target enemy " +
          "within " + LNZ.ability_115_range + "m, making them suppressed, " +
          "untargetable, and decayed while devoured.\nRecast this ability to spit " +
          "them out " + LNZ.ability_115_regurgitateDistance + "m and deal " +
          "damage with " + LNZ.ability_120_basePower + " + (" + Math.round(100*LNZ.
          ability_120_physicalRatio) + "% attack power + " + Math.round(100*LNZ.
          ability_120_magicalRatio) + "% magic power) magic damage.\nIf this ability " +
          "is not recast within " + (LNZ.ability_120_maxTime/1000.0) + "s " +
          "it will be automatically recast.";
      // Cathy Heck, zombie
      case 1001:
        return "Getting 2nd-hand smoke from Cathy Heck is particularly harmful" +
          ".\nBlow cigarette smoke in target direction in a cone " + LNZ.
          ability_1001_range + "m long, dealing " + LNZ.ability_1001_basePower +
          " + (0% attack power + " + Math.round(100*LNZ.ability_1001_magicRatio) +
          "% magic power) magic damage and making any enemies hit woozy for " +
          (LNZ.ability_1001_woozyTime/1000.0) + "s.";
      case 1002:
        return "";
      case 1003:
        return "";
      // Matt Schaefer, zombie
      case 1011:
        return "";
      // Ben Kohring, zombie
      case 1021:
        return "If any dare mess with Ben Kohring's luscious locks he will " +
          "fly into a fit of rage.\nFor " + (LNZ.ability_1021_time/
          1000.0) + "s you will have " + Math.round(100.0 * (LNZ.
          ability_1021_speed-1)) + "% increased speed, " + Math.round(100.0 * LNZ.
          ability_1021_lifesteal) + "% more lifesteal, " + Math.round(100.0 * LNZ.
          ability_1021_piercing) + "% higher piercing, and " + Math.round(100.0 * (1-
          LNZ.ability_1021_speed)) + "% decreased attack cooldown and time.";
      case 1022:
        return "Ben Kohring may not be privy to standard weaponry, but can " +
          "certainly throw rocks.\nThrow a rock with " + Math.round(LNZ.
          ability_1022_powerBase) + " + (" + Math.round(100*LNZ.
          ability_1022_powerRatio) + "% attack power + 0% magic power) physical " +
          "power.";
      default:
        return "-- error -- ";
    }
  }

  int manaCost() {
    switch(this.ID) {
      // Ben Nelson
      case 102:
        return 25;
      case 103:
        return 0;
      case 104:
        return 0;
      case 105:
        return -40;
      case 107:
        return 25;
      case 108:
        return 0;
      case 109:
        return 0;
      case 110:
        return -60;
      // Daniel Gray
      case 112:
        return 10;
      case 113:
        return 20;
      case 114:
        return 2;
      case 115:
        return 60;
      case 117:
        return 15;
      case 118:
        return 30;
      case 119:
        return 3;
      case 120:
        return 90;
      default:
        return 0;
    }
  }

  double timer_cooldown() {
    switch(this.ID) {
      // Ben Nelson
      case 102:
        return 6000;
      case 103:
        return 18000;
      case 104:
        return 15000;
      case 105:
        return 120000;
      case 107:
        return 5000;
      case 108:
        return 12000;
      case 109:
        return 10000;
      case 110:
        return 100000;
      // Daniel Gray
      case 111:
        return LNZ.ability_111_stillTime;
      case 112:
        return 60000;
      case 113:
        return 18000;
      case 114:
        return 2000;
      case 115:
        return 120000;
      case 116:
        return LNZ.ability_116_stillTime;
      case 117:
        return 5000;
      case 118:
        return 16000;
      case 119:
        return 1200;
      case 120:
        return 90000;
      default:
        return 0;
    }
  }

  boolean turnsCaster() {
    switch(this.ID) {
      case 102:
      case 103:
      case 107:
      case 108:
      case 112:
      case 113:
      case 117:
      case 118:
      case 1001:
      case 1022:
        return true;
      case 115:
      case 120:
        if (this.toggle) {
          return true;
        }
        else {
          return false;
        }
      default:
        return false;
    }
  }

  boolean castsOnTarget() { // point and click ability
    switch(this.ID) {
      case 115: // Anuran Appetite
      case 120: // Anuran Appetite II
        if (this.toggle) {
          return false;
        }
        else {
          return true;
        }
      default:
        return false;
    }
  }

  double castsOnTargetRange() {
    switch(this.ID) {
      case 115: // Anuran Appetite
      case 120: // Anuran Appetite II
        return LNZ.ability_115_range;
      default:
        return 0;
    }
  }

  boolean checkMana() {
    switch(this.ID) {
      case 101:
      case 102:
      case 103:
      case 106:
      case 107:
      case 108:
        return false;
      case 115:
      case 120:
        if (this.toggle) {
          return false;
        }
        else {
          return true;
        }
      case 1001:
      case 1002:
      case 1003:
        return false;
      default:
        return true;
    }
  }


  PImage getImage() {
    int image_id = 0;
    switch(this.ID) {
      // Ben Nelson
      case 101:
      case 106:
        image_id = 101;
        break;
      case 102:
      case 107:
        image_id = 102;
        break;
      case 103:
      case 108:
        image_id = 103;
        break;
      case 104:
      case 109:
        image_id = 104;
        break;
      case 105:
      case 110:
        image_id = 105;
        break;
      // Daniel Gray
      case 111:
      case 116:
        image_id = 111;
        break;
      case 112:
      case 117:
        image_id = 112;
        break;
      case 113:
      case 118:
        image_id = 113;
        break;
      case 114:
      case 119:
        image_id = 114;
        break;
      case 115:
      case 120:
        image_id = 115;
        break;
      default:
        return p.global.images.getImage("transparent.png");
    }
    return p.global.images.getImage("abilities/" + image_id + ".png");
  }


  void activate(Unit u, AbstractGameMap map) {
    this.activate(u, map, null);
  }
  void activate(Unit u, AbstractGameMap map, Unit target_unit) {
    int ability_index = u.curr_action_id;
    u.curr_action_id = 0;
    switch(this.ID) {
      case 102: // Mighty Pen
      case 107: // Mighty Pen II
        if (u.rageOfTheBen()) {
          return;
        }
        break;
      default:
        break;
    }
    if (this.castsOnTarget()) {
      this.target_unit = target_unit;
      if (target_unit != null) {
        this.target_key = target_unit.map_key;
      }
      else {
        this.target_key = -1;
      }
      if (this.target_unit == null || this.target_unit.remove) {
        return;
      }
      if (u.distance(this.target_unit) > this.castsOnTargetRange()) {
        return;
      }
    }
    u.decreaseMana(this.manaCost());
    this.timer_cooldown = this.timer_cooldown();
    switch(this.ID) {
      case 102: // Mighty Pen
        map.addProjectile(new Projectile(p, 3001, u));
        p.global.sounds.trigger_units("items/throw", u.coordinate.subtractR(map.view));
        break;
      case 103: // Nelson Glare
        this.timer_other = LNZ.ability_103_castTime;
        this.toggle = true;
        u.curr_action = UnitAction.CASTING;
        u.curr_action_id = ability_index;
        this.currently_hit.clear();
        p.global.sounds.trigger_units("units/ability/103", u.coordinate.subtractR(map.view));
        break;
      case 104: // Senseless Grit
        u.healPercent(LNZ.ability_104_activeHealAmount, false);
        u.addStatusEffect(StatusEffectCode.SENSELESS_GRIT, LNZ.ability_104_speedBuffTimer,
          new DamageSource(15, u.ID, StatusEffectCode.SENSELESS_GRIT));
        p.global.sounds.trigger_units("units/ability/104", u.coordinate.subtractR(map.view));
        break;
      case 105: // Rage of the Ben
        u.increaseMana(LNZ.ability_105_rageGain);
        u.addStatusEffect(StatusEffectCode.INVULNERABLE, LNZ.ability_105_buffTime,
          new DamageSource(15, u.ID, StatusEffectCode.INVULNERABLE));
        u.addStatusEffect(StatusEffectCode.RAGE_OF_THE_BEN, LNZ.ability_105_buffTime,
          new DamageSource(15, u.ID, StatusEffectCode.RAGE_OF_THE_BEN));
        p.global.sounds.trigger_units("units/ability/105", u.coordinate.subtractR(map.view));
        break;
      case 107: // Mighty Pen II
        map.addProjectile(new Projectile(p, 3002, u));
        p.global.sounds.trigger_units("items/throw", u.coordinate.subtractR(map.view));
        break;
      case 108: // Nelson Glare II
        this.timer_other = LNZ.ability_108_castTime;
        this.toggle = true;
        u.curr_action = UnitAction.CASTING;
        u.curr_action_id = ability_index;
        this.currently_hit.clear();
        p.global.sounds.trigger_units("units/ability/103", u.coordinate.subtractR(map.view));
        break;
      case 109: // Senseless Grit II
        u.healPercent(LNZ.ability_109_activeHealAmount, false);
        u.addStatusEffect(StatusEffectCode.SENSELESS_GRITII, LNZ.ability_109_speedBuffTimer,
          new DamageSource(15, u.ID, StatusEffectCode.SENSELESS_GRITII));
        p.global.sounds.trigger_units("units/ability/104", u.coordinate.subtractR(map.view));
        break;
      case 110: // Rage of the Ben II
        u.increaseMana(LNZ.ability_110_rageGain);
        u.addStatusEffect(StatusEffectCode.INVULNERABLE, LNZ.ability_110_buffTime,
          new DamageSource(15, u.ID, StatusEffectCode.INVULNERABLE));
        u.addStatusEffect(StatusEffectCode.RAGE_OF_THE_BENII, LNZ.ability_110_buffTime,
          new DamageSource(15, u.ID, StatusEffectCode.RAGE_OF_THE_BENII));
        p.global.sounds.trigger_units("units/ability/110", u.coordinate.subtractR(map.view));
        break;
      case 112: // Tongue Lash
        this.timer_other = LNZ.ability_112_castTime;
        this.toggle = true;
        u.curr_action = UnitAction.CASTING;
        u.curr_action_id = ability_index;
        p.global.sounds.trigger_units("units/ability/112", u.coordinate.subtractR(map.view));
        break;
      case 113: // Amphibious Leap
        if (u.drenched()) {
          this.timer_other = LNZ.ability_113_drenchedJumpDistance;
        }
        else {
          this.timer_other = LNZ.ability_113_jumpDistance;
        }
        this.toggle = true;
        u.curr_action_unhaltable = true;
        u.curr_action = UnitAction.CASTING;
        u.curr_action_id = ability_index;
        p.global.sounds.trigger_units("units/ability/113", u.coordinate.subtractR(map.view));
        break;
      case 114: // Alkaloid Secretion
        if (u.alkaloidSecretion()) {
          u.removeStatusEffect(StatusEffectCode.ALKALOID_SECRETION);
          p.global.sounds.silence_units("units/ability/114");
        }
        else {
          u.addStatusEffect(StatusEffectCode.ALKALOID_SECRETION,
            new DamageSource(15, u.ID, StatusEffectCode.ALKALOID_SECRETION));
          p.global.sounds.trigger_units("units/ability/114_start", u.coordinate.subtractR(map.view));
          p.global.sounds.trigger_units("units/ability/114", u.coordinate.subtractR(map.view));
          u.increaseMana(this.manaCost());
          this.timer_cooldown = 0;
        }
        break;
      case 115: // Anuran Appetite
        if (this.toggle) {
          this.timer_other = 0;
          u.increaseMana(this.manaCost());
        }
        else {
          this.toggle = true;
          this.timer_cooldown = 0;
          this.timer_other = LNZ.ability_115_maxTime;
          this.target_unit.addStatusEffect(StatusEffectCode.SUPPRESSED, LNZ.ability_115_maxTime,
            new DamageSource(15, u.ID, StatusEffectCode.SUPPRESSED));
          this.target_unit.addStatusEffect(StatusEffectCode.UNTARGETABLE, LNZ.ability_115_maxTime,
            new DamageSource(15, u.ID, StatusEffectCode.UNTARGETABLE));
          this.target_unit.addStatusEffect(StatusEffectCode.INVULNERABLE, LNZ.ability_115_maxTime,
            new DamageSource(15, u.ID, StatusEffectCode.INVULNERABLE));
          this.target_unit.addStatusEffect(StatusEffectCode.INVISIBLE, LNZ.ability_115_maxTime,
            new DamageSource(15, u.ID, StatusEffectCode.INVISIBLE));
          this.target_unit.addStatusEffect(StatusEffectCode.UNCOLLIDABLE, LNZ.ability_115_maxTime,
            new DamageSource(15, u.ID, StatusEffectCode.UNCOLLIDABLE));
          this.target_unit.addStatusEffect(StatusEffectCode.DECAYED, LNZ.ability_115_maxTime,
            new DamageSource(15, u.ID, StatusEffectCode.DECAYED));
          p.global.sounds.trigger_units("units/ability/115", u.coordinate.subtractR(map.view));
        }
        break;
      case 117: // Tongue Last II
        this.timer_other = LNZ.ability_112_castTime;
        this.toggle = true;
        u.curr_action = UnitAction.CASTING;
        u.curr_action_id = ability_index;
        p.global.sounds.trigger_units("units/ability/112", u.coordinate.subtractR(map.view));
        break;
      case 118: // Amphibious Leap II
        if (u.drenched()) {
          this.timer_other = LNZ.ability_118_drenchedJumpDistance;
        }
        else {
          this.timer_other = LNZ.ability_118_jumpDistance;
        }
        this.toggle = true;
        u.curr_action_unhaltable = true;
        u.curr_action = UnitAction.CASTING;
        p.global.sounds.trigger_units("units/ability/113", u.coordinate.subtractR(map.view));
        break;
      case 119: // Alkaloid Secretion II
        if (u.alkaloidSecretionII()) {
          u.removeStatusEffect(StatusEffectCode.ALKALOID_SECRETIONII);
          p.global.sounds.silence_units("units/ability/114");
        }
        else {
          u.addStatusEffect(StatusEffectCode.ALKALOID_SECRETIONII,
            new DamageSource(15, u.ID, StatusEffectCode.ALKALOID_SECRETIONII));
          p.global.sounds.trigger_units("units/ability/114_start", u.coordinate.subtractR(map.view));
          p.global.sounds.trigger_units("units/ability/114", u.coordinate.subtractR(map.view));
          u.increaseMana(this.manaCost());
          this.timer_cooldown = 0;
        }
        break;
      case 120: // Anuran Appetite II
        if (this.toggle) {
          this.timer_other = 0;
          u.increaseMana(this.manaCost());
        }
        else {
          this.toggle = true;
          this.timer_cooldown = 0;
          this.timer_other = LNZ.ability_120_maxTime;
          this.target_unit.addStatusEffect(StatusEffectCode.SUPPRESSED, LNZ.ability_120_maxTime,
            new DamageSource(15, u.ID, StatusEffectCode.SUPPRESSED));
          this.target_unit.addStatusEffect(StatusEffectCode.UNTARGETABLE, LNZ.ability_120_maxTime,
            new DamageSource(15, u.ID, StatusEffectCode.UNTARGETABLE));
          this.target_unit.addStatusEffect(StatusEffectCode.INVULNERABLE, LNZ.ability_120_maxTime,
            new DamageSource(15, u.ID, StatusEffectCode.INVULNERABLE));
          this.target_unit.addStatusEffect(StatusEffectCode.INVISIBLE, LNZ.ability_120_maxTime,
            new DamageSource(15, u.ID, StatusEffectCode.INVISIBLE));
          this.target_unit.addStatusEffect(StatusEffectCode.UNCOLLIDABLE, LNZ.ability_120_maxTime,
            new DamageSource(15, u.ID, StatusEffectCode.UNCOLLIDABLE));
          this.target_unit.addStatusEffect(StatusEffectCode.DECAYED, LNZ.ability_120_maxTime,
            new DamageSource(15, u.ID, StatusEffectCode.DECAYED));
          p.global.sounds.trigger_units("units/ability/115", u.coordinate.subtractR(map.view));
        }
        break;
      case 1001: // Blow Smoke
        this.timer_other = LNZ.ability_1001_castTime;
        this.toggle = true;
        u.curr_action = UnitAction.CASTING;
        u.curr_action_id = ability_index;
        this.currently_hit.clear();
        p.global.sounds.trigger_units("units/ability/1001", u.coordinate.subtractR(map.view));
        break;
      case 1002: // Condom Throw
        this.timer_other = LNZ.ability_1002_castTime;
        this.toggle = true;
        u.curr_action = UnitAction.CASTING;
        u.curr_action_id = ability_index;
        p.global.sounds.trigger_units("units/ability/1002", u.coordinate.subtractR(map.view));
        break;
      case 1003: // Title IX Charge
        this.timer_other = LNZ.ability_1003_castTime;
        this.toggle = true;
        u.curr_action = UnitAction.CASTING;
        u.curr_action_id = ability_index;
        //global.sounds.trigger_units("units/ability/1003_cast", u.coordinate.subtractR(map.view));
        break;
      case 1021: // Rage Run
        u.addStatusEffect(StatusEffectCode.RAGE_RUN, LNZ.ability_1021_time,
          new DamageSource(15, u.ID, StatusEffectCode.RAGE_RUN));
        //global.sounds.trigger_units("units/ability/1021", u.coordinate.subtractR(map.view));
        break;
      case 1022: // Rock Throw
        map.addProjectile(new Projectile(p, 3004, u));
        p.global.sounds.trigger_units("items/throw", u.coordinate.subtractR(map.view));
        break;
      default:
        p.global.errorMessage("ERROR: Can't activate ability with ID " + this.ID + ".");
        break;
    }
  }


  void update(int timeElapsed, Unit u, AbstractGameMap map) {
    this.update(timeElapsed);
    double max_distance = 0;
    double box_width = 0;
    double box_height = 0;
    switch(this.ID) {
      case 101: // Fearless Leader I
        if (this.timer_other <= 0) {
          u.decreaseMana(1);
          this.timer_other = LNZ.ability_101_tickTimer;
        }
        break;
      case 103: // Nelson Glare
        if (!this.toggle) {
          break;
        }
        if (u.curr_action != UnitAction.CASTING) {
          this.toggle = false;
          break;
        }
        max_distance = LNZ.ability_103_range * (1 - this.timer_other
          / LNZ.ability_103_castTime);
        for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
          Unit target = entry.getValue();
          if (target.alliance == u.alliance) {
            continue;
          }
          // already hit
          if (this.currently_hit.contains(target.map_key)) {
            continue;
          }
          double distance = u.centerDistance(target);
          if (distance > max_distance + target.size) {
            continue;
          }
          boolean silenced = true;
          if (distance > 0) {
            double angle = Math.abs((float)Math.atan2(
              target.coordinate.y - u.coordinate.y,
              target.coordinate.x - u.coordinate.x) - u.facingA);
            double unit_angle = 2 * Math.asin(0.5 * target.size / distance);
            if (angle > unit_angle + LNZ.ability_103_coneAngle) {
              continue;
            }
            if (angle > unit_angle + 0.3 * LNZ.ability_103_coneAngle) {
              silenced = false;
            }
          }
          this.currently_hit.add(target.map_key);
          target.addStatusEffect(StatusEffectCode.NELSON_GLARE, LNZ.ability_103_time,
            new DamageSource(15, u.ID, StatusEffectCode.NELSON_GLARE));
          if (silenced) {
            target.addStatusEffect(StatusEffectCode.SILENCED, LNZ.ability_103_time,
              new DamageSource(15, u.ID, StatusEffectCode.SILENCED));
          }
        }
        if (this.timer_other <= 0) {
          this.toggle = false;
          u.stopAction();
        }
        break;
      case 104: // Senseless Grit
        if (this.timer_other <= 0) {
          u.healPercent(LNZ.ability_104_passiveHealAmount, false);
          this.timer_other = LNZ.ability_104_passiveHealTimer;
        }
        break;
      case 106: // Fearless Leader II
        if (this.timer_other <= 0) {
          u.decreaseMana(1);
          this.timer_other = LNZ.ability_106_tickTimer;
        }
        break;
      case 108: // Nelson Glare II
        if (!this.toggle) {
          break;
        }
        if (u.curr_action != UnitAction.CASTING) {
          this.toggle = false;
          break;
        }
        max_distance = LNZ.ability_108_range * (1 - this.timer_other
          / LNZ.ability_108_castTime);
        for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
          Unit target = entry.getValue();
          if (target.alliance == u.alliance) {
            continue;
          }
          // already hit
          if (this.currently_hit.contains(target.map_key)) {
            continue;
          }
          double distance = u.centerDistance(target);
          if (distance > max_distance + target.size) {
            continue;
          }
          boolean silenced = true;
          if (distance > 0) {
            double angle = Math.abs(Math.atan2(
              target.coordinate.y - u.coordinate.y,
              target.coordinate.x - u.coordinate.x) - u.facingA);
            double unit_angle = 2 * Math.asin(0.5 * target.size / distance);
            if (angle > unit_angle + LNZ.ability_108_coneAngle) {
              continue;
            }
            if (angle > unit_angle + 0.3 * LNZ.ability_108_coneAngle) {
              silenced = false;
            }
          }
          this.currently_hit.add(target.map_key);
          target.addStatusEffect(StatusEffectCode.NELSON_GLAREII, LNZ.ability_108_time,
            new DamageSource(15, u.ID, StatusEffectCode.NELSON_GLAREII));
          if (silenced) {
            target.addStatusEffect(StatusEffectCode.SILENCED, LNZ.ability_108_time,
              new DamageSource(15, u.ID, StatusEffectCode.SILENCED));
          }
        }
        if (this.timer_other <= 0) {
          this.toggle = false;
          u.stopAction();
        }
        break;
      case 109: // Senseless Grit II
        if (this.timer_other <= 0) {
          u.healPercent(LNZ.ability_109_passiveHealAmount, false);
          this.timer_other = LNZ.ability_109_passiveHealTimer;
        }
        break;
      case 111: // Aposematic Camouflage
        if (this.timer_other <= 0) {
          u.increaseMana(1);
          this.timer_other = LNZ.ability_111_regenTime;
        }
        if (u.curr_action == UnitAction.NONE) {
          if (this.timer_cooldown <= 0 && !u.aposematicCamouflage() && !u.visible()) {
            u.addStatusEffect(StatusEffectCode.APOSEMATIC_CAMOUFLAGE,
              new DamageSource(15, u.ID, StatusEffectCode.APOSEMATIC_CAMOUFLAGE));
            p.global.sounds.trigger_units("units/ability/111", u.coordinate.subtractR(map.view));
          }
        }
        else {
          this.timer_cooldown = this.timer_cooldown();
        }
        break;
      case 112: // Tongue Lash
        if (!this.toggle) {
          break;
        }
        if (u.curr_action != UnitAction.CASTING) {
          this.toggle = false;
          break;
        }
        box_width = LNZ.ability_112_distance * (1 - this.timer_other / LNZ.ability_112_castTime);
        box_height = u.size;
        for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
          Unit target = entry.getValue();
          if (target.alliance == u.alliance) {
            continue;
          }
          PVector distance = new PVector((int)(target.coordinate.x - u.coordinate.x), (int)(target.coordinate.y - u.coordinate.y));
          distance.rotate(-(int)u.facingA);
          if (distance.x + target.size > 0 && distance.y + target.size > -0.5 *
            box_height && distance.x - target.size < box_width && distance.y -
            target.size < 0.5 * box_height) {
            // collision
            double power = LNZ.ability_112_basePower + u.power(LNZ.
              ability_112_physicalRatio, LNZ.ability_112_magicalRatio);
            if (u.aposematicCamouflageII()) {
              power *= LNZ.ability_116_powerBuff;
            }
            else if (u.aposematicCamouflage()) {
              power *= LNZ.ability_111_powerBuff;
            }
            double damage = target.calculateDamageFrom(power, DamageType.MAGICAL,
              Element.BROWN, u.piercing(), u.penetration());
            target.damage(u, damage, new DamageSource(14, u.ID, this.ID));
            target.refreshStatusEffect(StatusEffectCode.TONGUE_LASH, LNZ.ability_112_slowTime,
              new DamageSource(15, u.ID, StatusEffectCode.TONGUE_LASH));
            this.toggle = false;
            u.stopAction();
            p.global.sounds.trigger_units("units/ability/112_hit", u.coordinate.subtractR(map.view));
            break;
          }
        }
        if (this.timer_other <= 0) {
          this.toggle = false;
          u.stopAction();
        }
        break;
      case 113: // Amphibious Leap
        if (!this.toggle) {
          break;
        }
        if (u.curr_action != UnitAction.CASTING) {
          this.toggle = false;
          break;
        }
        this.timer_other -= u.last_move_distance;
        if (u.coordinate.distance(u.curr_action_coordinate) < u.last_move_distance) {
          this.timer_other = 0;
        }
        if (this.timer_other <= 0) {
          this.toggle = false;
          u.stopAction();
          double splash_radius = 0;
          if (u.drenched()) {
            map.addVisualEffect(4004, u.coordinate);
            splash_radius = LNZ.ability_113_drenchedSplashRadius;
          }
          else {
            map.addVisualEffect(4003, u.coordinate);
            splash_radius = LNZ.ability_113_splashRadius;
          }
          double power = LNZ.ability_113_basePower + u.power(LNZ.
            ability_113_physicalRatio, LNZ.ability_113_magicalRatio);
          for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
            Unit target = entry.getValue();
            if (target.alliance == u.alliance) {
              continue;
            }
            if (u.distance(target) > splash_radius) {
              continue;
            }
            target.damage(u, target.calculateDamageFrom(power, DamageType.MAGICAL,
              Element.BROWN, u.piercing(), u.penetration()), new DamageSource(14, u.ID, this.ID));
            target.refreshStatusEffect(StatusEffectCode.STUNNED, LNZ.ability_113_stunTime,
              new DamageSource(15, u.ID, StatusEffectCode.STUNNED));
          }
          p.global.sounds.trigger_units("units/ability/113_land", u.coordinate.subtractR(map.view));
        }
        break;
      case 114: // Alkaloid Secretion
        if (!u.alkaloidSecretion()) {
          break;
        }
        if (this.timer_other <= 0) {
          if (u.currMana() < this.manaCost()) {
            u.removeStatusEffect(StatusEffectCode.ALKALOID_SECRETION);
            break;
          }
          else {
            u.decreaseMana(this.manaCost());
          }
          this.timer_other = LNZ.ability_114_tickTime;
          double power = LNZ.ability_114_basePower + u.power(0, LNZ.ability_114_magicRatio);
          for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
            Unit target = entry.getValue();
            if (target.alliance == u.alliance) {
              continue;
            }
            if (u.distance(target) > LNZ.ability_114_range) {
              continue;
            }
            target.damage(u, LNZ.ability_114_currHealth * target.curr_health, new DamageSource(14, u.ID, this.ID));
            target.damage(u, target.calculateDamageFrom(power, DamageType.MAGICAL,
              Element.BROWN, u.piercing(), u.penetration()), new DamageSource(14, u.ID, this.ID));
            target.refreshStatusEffect(StatusEffectCode.ROTTING, LNZ.ability_114_rotTime,
              new DamageSource(15, u.ID, StatusEffectCode.ROTTING));
          }
          map.addVisualEffect(4007, u.coordinate);
          p.global.sounds.trigger_units("units/ability/114", u.coordinate.subtractR(map.view));
        }
        break;
      case 115: // Anuran Appetite
        if (!this.toggle) {
          break;
        }
        if (this.target_unit == null || this.target_unit.remove || u.remove) {
          this.toggle = false;
          break;
        }
        if (this.timer_other <= 0) {
          this.toggle = false;
          this.target_unit.removeStatusEffect(StatusEffectCode.SUPPRESSED);
          this.target_unit.removeStatusEffect(StatusEffectCode.UNTARGETABLE);
          this.target_unit.removeStatusEffect(StatusEffectCode.INVULNERABLE);
          this.target_unit.removeStatusEffect(StatusEffectCode.INVISIBLE);
          this.target_unit.removeStatusEffect(StatusEffectCode.UNCOLLIDABLE);
          this.target_unit.setLocation(u.front());
          this.target_unit.setFacing(u.facing.copy());
          this.target_unit.curr_action = UnitAction.MOVING;
          this.target_unit.curr_action_id = 1;
          this.target_unit.curr_action_unstoppable = true;
          this.target_unit.curr_action_unhaltable = true;
          this.target_unit.curr_action_coordinate = this.target_unit.coordinate.addR(
            u.facing.multiplyR(LNZ.ability_115_regurgitateDistance));
          double power = LNZ.ability_115_basePower + u.power(LNZ.
            ability_115_physicalRatio, LNZ.ability_115_magicalRatio);
          this.target_unit.damage(u, this.target_unit.calculateDamageFrom(power,
            DamageType.MAGICAL, Element.BROWN, u.piercing(), u.penetration()), new DamageSource(14, u.ID, this.ID));
          p.global.sounds.trigger_units("units/ability/115_spit", u.coordinate.subtractR(map.view));
          p.global.sounds.silence_units("units/ability/115");
        }
        break;
      case 116: // Aposematic Camouflage II
        if (this.timer_other <= 0) {
          u.increaseMana(1);
          this.timer_other = LNZ.ability_116_regenTime;
        }
        if (u.curr_action == UnitAction.NONE) {
          if (this.timer_cooldown <= 0 && !u.aposematicCamouflageII() && !u.visible()) {
            u.addStatusEffect(StatusEffectCode.APOSEMATIC_CAMOUFLAGEII,
              new DamageSource(15, u.ID, StatusEffectCode.APOSEMATIC_CAMOUFLAGEII));
            p.global.sounds.trigger_units("units/ability/111", u.coordinate.subtractR(map.view));
          }
        }
        else {
          this.timer_cooldown = this.timer_cooldown();
        }
        break;
      case 117: // Tongue Lash II
        if (!this.toggle) {
          break;
        }
        if (u.curr_action != UnitAction.CASTING) {
          this.toggle = false;
          break;
        }
        box_width = LNZ.ability_117_distance * (1 - this.timer_other / LNZ.ability_112_castTime);
        box_height = u.size;
        for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
          Unit target = entry.getValue();
          if (target.alliance == u.alliance) {
            continue;
          }
          PVector distance = new PVector(
            (int)(target.coordinate.x - u.coordinate.x),
            (int)(target.coordinate.y - u.coordinate.y));
          distance.rotate(-(int)u.facingA);
          if (distance.x + target.size > 0 && distance.y + target.size > -0.5 *
            box_height && distance.x - target.size < box_width && distance.y -
            target.size < 0.5 * box_height) {
            // collision
            double power = LNZ.ability_117_basePower + u.power(LNZ.
              ability_117_physicalRatio, LNZ.ability_117_magicalRatio);
              double damage = target.calculateDamageFrom(power, DamageType.MAGICAL,
              Element.BROWN, u.piercing(), u.penetration());
            if (u.aposematicCamouflageII()) {
              power *= LNZ.ability_116_powerBuff;
            }
            else if (u.aposematicCamouflage()) {
              power *= LNZ.ability_111_powerBuff;
            }
            target.damage(u, damage, new DamageSource(14, u.ID, this.ID));
            target.refreshStatusEffect(StatusEffectCode.TONGUE_LASH, LNZ.ability_117_slowTime,
              new DamageSource(15, u.ID, StatusEffectCode.TONGUE_LASH));
            this.toggle = false;
            u.stopAction();
            p.global.sounds.trigger_units("units/ability/112_hit", u.coordinate.subtractR(map.view));
            break;
          }
        }
        if (this.timer_other <= 0) {
          this.toggle = false;
          u.stopAction();
        }
        break;
      case 118: // Amphibious Leap II
        if (!this.toggle) {
          break;
        }
        if (u.curr_action != UnitAction.CASTING) {
          this.toggle = false;
          break;
        }
        this.timer_other -= u.last_move_distance;
        if (u.coordinate.distance(u.curr_action_coordinate) < u.last_move_distance) {
          this.timer_other = 0;
        }
        if (this.timer_other <= 0) {
          this.toggle = false;
          u.stopAction();
          double splash_radius = 0;
          if (u.drenched()) {
            map.addVisualEffect(4006, u.coordinate);
            splash_radius = LNZ.ability_118_drenchedSplashRadius;
          }
          else {
            map.addVisualEffect(4005, u.coordinate);
            splash_radius = LNZ.ability_118_splashRadius;
          }
          double power = LNZ.ability_118_basePower + u.power(LNZ.
            ability_118_physicalRatio, LNZ.ability_118_magicalRatio);
          for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
            Unit target = entry.getValue();
            if (target.alliance == u.alliance) {
              continue;
            }
            if (u.distance(target) > splash_radius) {
              continue;
            }
            target.damage(u, target.calculateDamageFrom(power, DamageType.MAGICAL,
              Element.BROWN, u.piercing(), u.penetration()), new DamageSource(14, u.ID, this.ID));
            target.refreshStatusEffect(StatusEffectCode.STUNNED, LNZ.ability_118_stunTime,
              new DamageSource(15, u.ID, StatusEffectCode.STUNNED));
          }
          p.global.sounds.trigger_units("units/ability/113_land", u.coordinate.subtractR(map.view));
        }
        break;
      case 119: // Alkaloid Secretion II
        if (!u.alkaloidSecretionII()) {
          break;
        }
        if (this.timer_other <= 0) {
          if (u.currMana() < this.manaCost()) {
            u.removeStatusEffect(StatusEffectCode.ALKALOID_SECRETIONII);
            break;
          }
          else {
            u.decreaseMana(this.manaCost());
          }
          this.timer_other = LNZ.ability_114_tickTime;
          double power = LNZ.ability_119_basePower + u.power(0, LNZ.ability_119_magicRatio);
          for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
            Unit target = entry.getValue();
            if (target.alliance == u.alliance) {
              continue;
            }
            if (u.distance(target) > LNZ.ability_119_range) {
              continue;
            }
            target.damage(u, LNZ.ability_119_currHealth * target.curr_health, new DamageSource(14, u.ID, this.ID));
            target.damage(u, target.calculateDamageFrom(power, DamageType.MAGICAL,
              Element.BROWN, u.piercing(), u.penetration()), new DamageSource(14, u.ID, this.ID));
            target.refreshStatusEffect(StatusEffectCode.ROTTING, LNZ.ability_114_rotTime,
              new DamageSource(15, u.ID, StatusEffectCode.ROTTING));
          }
          map.addVisualEffect(4008, u.coordinate);
          p.global.sounds.trigger_units("units/ability/114", u.coordinate.subtractR(map.view));
        }
        break;
      case 120: // Anuran Appetite II
        if (!this.toggle) {
          break;
        }
        if (this.target_unit == null || this.target_unit.remove || u.remove) {
          this.toggle = false;
          break;
        }
        if (this.timer_other <= 0) {
          this.toggle = false;
          this.target_unit.removeStatusEffect(StatusEffectCode.SUPPRESSED);
          this.target_unit.removeStatusEffect(StatusEffectCode.UNTARGETABLE);
          this.target_unit.removeStatusEffect(StatusEffectCode.INVULNERABLE);
          this.target_unit.removeStatusEffect(StatusEffectCode.INVISIBLE);
          this.target_unit.removeStatusEffect(StatusEffectCode.UNCOLLIDABLE);
          this.target_unit.setLocation(u.front());
          this.target_unit.setFacing(u.facing.copy());
          this.target_unit.curr_action = UnitAction.MOVING;
          this.target_unit.curr_action_id = 1;
          this.target_unit.curr_action_unstoppable = true;
          this.target_unit.curr_action_unhaltable = true;
          this.target_unit.curr_action_coordinate = this.target_unit.coordinate.addR(
            u.facing.multiplyR(LNZ.ability_115_regurgitateDistance));
          double power = LNZ.ability_120_basePower + u.power(LNZ.
            ability_120_physicalRatio, LNZ.ability_120_magicalRatio);
          this.target_unit.damage(u, this.target_unit.calculateDamageFrom(power,
            DamageType.MAGICAL, Element.BROWN, u.piercing(), u.penetration()), new DamageSource(14, u.ID, this.ID));
          p.global.sounds.trigger_units("units/ability/115_spit", u.coordinate.subtractR(map.view));
          p.global.sounds.silence_units("units/ability/115");
        }
        break;
      case 1001: // Blow Smoke
        if (!this.toggle) {
          break;
        }
        if (u.curr_action != UnitAction.CASTING) {
          this.toggle = false;
          break;
        }
        max_distance = LNZ.ability_1001_range * (1 - this.timer_other
          / LNZ.ability_1001_castTime);
        for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
          Unit target = entry.getValue();
          if (target.alliance == u.alliance) {
            continue;
          }
          // already hit
          if (this.currently_hit.contains(target.map_key)) {
            continue;
          }
          double distance = u.centerDistance(target);
          if (distance > max_distance + target.size) {
            continue;
          }
          if (distance > 0) {
            double angle = Math.abs(Math.atan2(target.coordinate.y - u.coordinate.y, target.coordinate.x - u.coordinate.x) - u.facingA);
            double unit_angle = 2 * Math.asin(0.5 * target.size / distance);
            if (angle > unit_angle + LNZ.ability_1001_coneAngle) {
              continue;
            }
          }
          this.currently_hit.add(target.map_key);
          double power = LNZ.ability_1001_basePower + u.power(0, LNZ.ability_1001_magicRatio);
          target.damage(u, target.calculateDamageFrom(power,
            DamageType.MAGICAL, Element.GRAY, u.piercing(), u.penetration()), new DamageSource(14, u.ID, this.ID));
          target.addStatusEffect(StatusEffectCode.WOOZY, LNZ.ability_1001_woozyTime,
            new DamageSource(15, u.ID, StatusEffectCode.WOOZY));
        }
        if (this.timer_other <= 0) {
          this.toggle = false;
          u.stopAction();
        }
        break;
      case 1002: // Condom Throw
        if (!this.toggle) {
          break;
        }
        if (u.curr_action != UnitAction.CASTING) {
          this.toggle = false;
          break;
        }
        if (this.timer_other <= 0) {
          this.toggle = false;
          u.stopAction();
          Projectile proj = new Projectile(p, 3003, u);
          map.addProjectile(proj);
          proj = new Projectile(p, 3003, u);
          proj.turn(0.25 * Math.PI);
          map.addProjectile(proj);
          proj = new Projectile(p, 3003, u);
          proj.turn(0.5 * Math.PI);
          map.addProjectile(proj);
          proj = new Projectile(p, 3003, u);
          proj.turn(0.75 * Math.PI);
          map.addProjectile(proj);
          proj = new Projectile(p, 3003, u);
          proj.turn(Math.PI);
          map.addProjectile(proj);
          proj = new Projectile(p, 3003, u);
          proj.turn(-0.25 * Math.PI);
          map.addProjectile(proj);
          proj = new Projectile(p, 3003, u);
          proj.turn(-0.5 * Math.PI);
          map.addProjectile(proj);
          proj = new Projectile(p, 3003, u);
          proj.turn(-0.75 * Math.PI);
          map.addProjectile(proj);
        }
        break;
      case 1003: // Title IX Charge
        if (!this.toggle) {
          break;
        }
        if (u.curr_action != UnitAction.CASTING) {
          this.toggle = false;
          break;
        }
        if (this.timer_other <= 0) {
          this.toggle = false;
          u.stopAction();
          //global.sounds.trigger_units("units/ability/1003_slap", u.x - map.viewX, u.y - map.viewY);
          for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
            Unit target = entry.getValue();
            if (target.alliance == u.alliance) {
              continue;
            }
            PVector distance = new PVector(
              (float)(target.coordinate.x - u.coordinate.x),
              (float)(target.coordinate.y - u.coordinate.y));
            distance.rotate(-(float)u.facingA);
            if (distance.x + target.size > 0 && distance.y + target.size > -0.5 *
              LNZ.ability_1003_size_h && distance.x - target.size < LNZ.
              ability_1003_size_w && distance.y - target.size < 0.5 * LNZ.ability_1003_size_h) {
              // collision
              double power = LNZ.ability_1003_basePower + u.power(0, LNZ.
                ability_1003_magicRatio) + target.health() * LNZ.ability_1003_maxHealth;
              double damage = target.calculateDamageFrom(power, DamageType.MAGICAL,
                Element.GRAY, u.piercing(), u.penetration());
              target.damage(u, damage, new DamageSource(14, u.ID, this.ID));
              target.refreshStatusEffect(StatusEffectCode.STUNNED, 0.3 * LNZ.ability_1003_statusTime,
                new DamageSource(15, u.ID, StatusEffectCode.STUNNED));
              target.refreshStatusEffect(StatusEffectCode.WILTED, LNZ.ability_1003_statusTime,
                new DamageSource(15, u.ID, StatusEffectCode.WILTED));
              target.refreshStatusEffect(StatusEffectCode.WOOZY, LNZ.ability_1003_statusTime,
                new DamageSource(15, u.ID, StatusEffectCode.WOOZY));
              target.refreshStatusEffect(StatusEffectCode.SLOWED, LNZ.ability_1003_statusTime,
                new DamageSource(15, u.ID, StatusEffectCode.SLOWED));
              this.toggle = false;
              break;
            }
          }
        }
        break;

      default:
        break;
    }
  }


  void update(int timeElapsed) {
    if (this.timer_cooldown > 0) {
      this.timer_cooldown -= timeElapsed;
      if (this.timer_cooldown < 0) {
        this.timer_cooldown = 0;
      }
    }
    switch(this.ID) {
      case 113: // Amphibious Leap
      case 118: // Amphiibous Leap II
        break;
      default:
        if (this.timer_other > 0) {
          this.timer_other -= timeElapsed;
          if (this.timer_other < 0) {
            this.timer_other = 0;
          }
        }
        break;
    }
  }


  void addStack() {
    this.stacks++;
    switch(this.ID) {
      default:
        p.global.errorMessage("ERROR: Ability ID " + this.ID + " can't add stack.");
        break;
    }
  }


  String fileString() {
    String fileString = "\nnew: Ability: " + this.ID;
    fileString += "\ntimer_cooldown: " + this.timer_cooldown;
    fileString += "\ntimer_other: " + this.timer_other;
    fileString += "\ntarget_key: " + this.target_key;
    fileString += "\nstacks: " + this.stacks;
    fileString += "\ntoggle: " + this.toggle;
    return fileString;
  }

  void addData(String datakey, String data) {
    switch(datakey) {
      case "timer_cooldown":
        this.timer_cooldown = Misc.toDouble(data);
        break;
      case "timer_other":
        this.timer_other = Misc.toDouble(data);
        break;
      case "target_key":
        this.target_key = Misc.toInt(data);
        break;
      case "stacks":
        this.stacks = Misc.toInt(data);
        break;
      case "toggle":
        this.toggle = Misc.toBoolean(data);
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not found for Ability data.");
        break;
    }
  }
}