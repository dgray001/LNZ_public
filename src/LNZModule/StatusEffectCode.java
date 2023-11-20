package LNZModule;

import java.util.*;

enum StatusEffectCode {
  ERROR("Error"), HUNGRY("Hungry"), WEAK("Weak"), THIRSTY("Thirsty"), WOOZY("Woozy"),
  CONFUSED("Confused"), INVULNERABLE("Invulnerable"), UNKILLABLE("Unkillable"),
  BLEEDING("Bleeding"), HEMORRHAGING("Hemorrhaging"), WILTED("Wilted"), WITHERED("Withered"),
  VISIBLE("Visible"), SUPPRESSED("Suppressed"), UNTARGETABLE("Untargetable"),
  STUNNED("Stunned"), INVISIBLE("Invisible"), UNCOLLIDABLE("Uncollidable"),
  RUNNING("Running"), FERTILIZED("Fertilized"), SNEAKING("Sneaking"), RELAXED("Relaxed"),
  GHOSTING("Ghosting"), SILENCED("Silenced"), SLOWED("Slowed"),

  DRENCHED("Drenched"), DROWNING("Drowning"), BURNT("Burning"), CHARRED("Charred"),
  CHILLED("Chilled"), FROZEN("Frozen"), SICK("Sick"), DISEASED("Diseased"), ROTTING("Rotting"),
  DECAYED("Decayed"), SHAKEN("Shaken"), FALLEN("Fallen"), SHOCKED("Shocked"),
  PARALYZED("Paralyzed"), UNSTABLE("Unstable"), RADIOACTIVE("Radioactive"),

  NELSON_GLARE("Nelson Glared"), NELSON_GLAREII("Nelson Glared"), SENSELESS_GRIT(
  "Senseless Grit"), SENSELESS_GRITII("Senseless Grit"), RAGE_OF_THE_BEN(
  "Rage of the Ben"), RAGE_OF_THE_BENII("Rage of the Ben"),

  APOSEMATIC_CAMOUFLAGE("Camouflaged"), APOSEMATIC_CAMOUFLAGEII("Camouflaged"),
  TONGUE_LASH("Slowed"), ALKALOID_SECRETION("Secreting Alkaloids"), ALKALOID_SECRETIONII(
  "Secreting Alkaloids"),

  RAGE_RUN("Rage Run"),
  ;

  private static final List<StatusEffectCode> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  private String code_name;
  private StatusEffectCode(String code_name) {
    this.code_name = code_name;
  }
  public String codeName() {
    return this.code_name;
  }
  public static String codeName(StatusEffectCode code) {
    return code.codeName();
  }

  public static StatusEffectCode code(String code_name) {
    for (StatusEffectCode code : StatusEffectCode.VALUES) {
      if (code == StatusEffectCode.ERROR) {
        continue;
      }
      if (code.codeName().equals(code_name)) {
        return code;
      }
    }
    return StatusEffectCode.ERROR;
  }

  public boolean negative() {
    return StatusEffectCode.negative(this);
  }
  public static boolean negative(StatusEffectCode code) {
    switch(code) {
      case INVULNERABLE:
      case UNKILLABLE:
      case UNTARGETABLE:
      case INVISIBLE:
      case UNCOLLIDABLE:
      case RUNNING:
      case FERTILIZED:
      case SNEAKING:
      case RELAXED:
      case GHOSTING:
      case SENSELESS_GRIT:
      case SENSELESS_GRITII:
      case RAGE_OF_THE_BEN:
      case RAGE_OF_THE_BENII:
      case APOSEMATIC_CAMOUFLAGE:
      case APOSEMATIC_CAMOUFLAGEII:
      case ALKALOID_SECRETION:
      case ALKALOID_SECRETIONII:
      case RAGE_RUN:
        return false;
      default:
        return true;
    }
  }

  public Element element() {
    return StatusEffectCode.element(this);
  }
  public static Element element(StatusEffectCode code) {
    switch(code) {
      case DRENCHED:
      case DROWNING:
        return Element.BLUE;
      case BURNT:
      case CHARRED:
        return Element.RED;
      case CHILLED:
      case FROZEN:
        return Element.CYAN;
      case SICK:
      case DISEASED:
        return Element.ORANGE;
      case ROTTING:
      case DECAYED:
      case APOSEMATIC_CAMOUFLAGE:
      case APOSEMATIC_CAMOUFLAGEII:
      case TONGUE_LASH:
      case ALKALOID_SECRETION:
      case ALKALOID_SECRETIONII:
        return Element.BROWN;
      case SHAKEN:
      case FALLEN:
        return Element.PURPLE;
      case SHOCKED:
      case PARALYZED:
        return Element.YELLOW;
      case UNSTABLE:
      case RADIOACTIVE:
        return Element.MAGENTA;
      default:
        return Element.GRAY;
    }
  }

  public String getImageString() {
    String image_path = "statuses/";
    switch(this) {
      case INVULNERABLE:
        image_path += "invulnerable.png";
        break;
      case UNKILLABLE:
        image_path += "unkillable.png";
        break;
      case HUNGRY:
        image_path += "hungry.png";
        break;
      case WEAK:
        image_path += "weak.png";
        break;
      case THIRSTY:
        image_path += "thirsty.png";
        break;
      case WOOZY:
        image_path += "woozy.png";
        break;
      case CONFUSED:
        image_path += "confused.png";
        break;
      case BLEEDING:
        image_path += "bleeding.png";
        break;
      case HEMORRHAGING:
        image_path += "hemorrhaging.png";
        break;
      case WILTED:
        image_path += "wilted.png";
        break;
      case WITHERED:
        image_path += "withered.png";
        break;
      case VISIBLE:
        image_path += "visible.png";
        break;
      case SUPPRESSED:
        image_path += "suppressed.png";
        break;
      case UNTARGETABLE:
        image_path += "untargetable.png";
        break;
      case STUNNED:
        image_path += "stunned.png";
        break;
      case INVISIBLE:
        image_path += "invisible.png";
        break;
      case UNCOLLIDABLE:
        image_path += "uncollidable.png";
        break;
      case RUNNING:
        image_path += "running.png";
        break;
      case FERTILIZED:
        image_path += "fertilized.png";
        break;
      case SNEAKING:
        image_path += "sneaking.png";
        break;
      case RELAXED:
        image_path += "relaxed.png";
        break;
      case GHOSTING:
        image_path += "ghosting.png";
        break;
      case SILENCED:
        image_path += "silenced.png";
        break;
      case DRENCHED:
        image_path += "drenched.png";
        break;
      case DROWNING:
        image_path += "drowning.png";
        break;
      case BURNT:
        image_path += "burning.png";
        break;
      case CHARRED:
        image_path += "charred.png";
        break;
      case CHILLED:
        image_path += "chilled.png";
        break;
      case FROZEN:
        image_path += "frozen.jpg";
        break;
      case SICK:
        image_path += "sick.png";
        break;
      case DISEASED:
        image_path += "diseased.png";
        break;
      case ROTTING:
        image_path += "rotting.png";
        break;
      case DECAYED:
        image_path += "decayed.png";
        break;
      case SHAKEN:
        image_path += "shaken.png";
        break;
      case FALLEN:
        image_path += "fallen.png";
        break;
      case SHOCKED:
        image_path += "shocked.png";
        break;
      case PARALYZED:
        image_path += "paralyzed.png";
        break;
      case UNSTABLE:
        image_path += "unstable.png";
        break;
      case RADIOACTIVE:
        image_path += "radioactive.png";
        break;
      case NELSON_GLARE:
      case NELSON_GLAREII:
        image_path += "nelson_glare.png";
        break;
      case SENSELESS_GRIT:
      case SENSELESS_GRITII:
        image_path += "senseless_grit.png";
        break;
      case RAGE_OF_THE_BEN:
      case RAGE_OF_THE_BENII:
        image_path += "rage_of_the_ben.png";
        break;
      case APOSEMATIC_CAMOUFLAGE:
      case APOSEMATIC_CAMOUFLAGEII:
        image_path += "camouflaged.jpg";
        break;
      case SLOWED:
      case TONGUE_LASH:
        image_path += "slowed.png";
        break;
      case ALKALOID_SECRETION:
      case ALKALOID_SECRETIONII:
        image_path += "alkaloid_secretion.png";
        break;
      default:
        image_path += "default.png";
        break;
    }
    return image_path;
  }

  public String description() {
    switch(this) {
      case INVULNERABLE:
        return "This unit does not take damage from any source.";
      case UNKILLABLE:
        return "This unit cannot be killed.";
      case HUNGRY:
        return "This unit is hungry and will slowly take damage to " + Math.round(100.0 *
          LNZ.status_hunger_damageLimit) + "% max health.\nHunger can " +
          "also lead to weakness.";
      case WEAK:
        return "This unit is weak and has " + Math.round(100.0 * LNZ.
          status_weak_multiplier) + "% combat stats (attack, defense, etc.).";
      case THIRSTY:
        return "This unit is thirsty and will slowly take damage to " + Math.round(100.0 *
          LNZ.status_thirst_damageLimit) + "% max health.\nThirst can " +
          "also lead to becoming woozy or confused.";
      case WOOZY:
        return "This unit is woozy and will randomly stop what they are " +
          "doing and turn another direction.";
      case CONFUSED:
        return "This unit is confused and will randomly stop what they are " +
          "doing and move in a random direction.";
      case BLEEDING:
        return "This unit is bleeding and will take damage to " + Math.round(100.0 *
          LNZ.status_bleed_damageLimit) + "% max health.\nBleeding can " +
          "also lead to hemorrhaging";
      case HEMORRHAGING:
        return "This unit is hemorraghing and will quickly die if it is not stopped.";
      case WILTED:
        return "This unit is wilted and has " + Math.round(100.0 * LNZ.
          status_wilted_multiplier) + "% combat stats (attack, defense, etc.).";
      case WITHERED:
        return "This unit is withered and has " + Math.round(100.0 * LNZ.
          status_withered_multiplier) + "% combat stats (attack, defense, etc.).";
      case VISIBLE:
        return "This unit is visible and can be seen by enemies.";
      case SUPPRESSED:
        return "This unit is suppressed and cannot perform any action";
      case UNTARGETABLE:
        return "This unit is untargetable and cannot be targeted by attacks, abilities, or spells.";
      case STUNNED:
        return "This unit is stunned and cannot move, attack, use abilities, or cast spells.";
      case INVISIBLE:
        return "This unit is invisible and cannot be seen.";
      case UNCOLLIDABLE:
        return "This unit is uncollidable and cannot be collided with.";
      case RUNNING:
        return "This unit is running and moves " + Math.round(100.0 * (LNZ.
          status_running_multiplier - 1.0)) + "% faster.";
      case FERTILIZED:
        return "This unit is fertilized.";
      case SNEAKING:
        return "This unit is sneaking and will move slowly and not make walking sound.";
      case RELAXED:
        return "This unit is relaxed and has " + Math.round(100.0 * LNZ.
          status_relaxed_multiplier) + "% combat stats (attack, defense, etc.), " +
          " move speed, and tenacity, but " + Math.round(1000.0 * LNZ.
          status_relaxed_healMultiplier) / 10.0 + "% increased passive healing.";
      case GHOSTING:
        return "This unit is ghosting and can move through walls and other units.";
      case SILENCED:
        return "This unit is silenced and cannot cast spells.";
      case SLOWED:
        return "This unit is slowed and moves " + Math.round(100.0 * (1.0 - LNZ.
          status_slowed_multiplier)) + "% slower.";
      case DRENCHED:
        return "This unit is drenched so will take more damage from blue sources." +
          "\nIf this unit is red it will also slowly take damage to " + Math.round(100.0 *
          LNZ.status_drenched_damageLimit) + "% max health.";
      case DROWNING:
        return "This unit is drowning and will quickly take damage to their death." +
          "\nDrowning will also make the unit drenched." +
          "\nIf this unit is blue it will only take damage to " + Math.round(100.0 *
          LNZ.status_drowning_damageLimitBlue) + "% max health.";
      case BURNT:
        return "This unit is burning and will take damage to its death." +
          "\nBurning also has chance to make this unit charred." +
          "\nIf this unit is red it will only take damage to " + Math.round(100.0 *
          LNZ.status_burnt_damageLimitRed) + "% max health.";
      case CHARRED:
        return "This unit is charred and will quickly take damage to its death." +
          "\nIf this unit is red it will only take damage to " + Math.round(100.0 *
          LNZ.status_charred_damageLimitRed) + "% max health.";
      case CHILLED:
        return "This unit is chilled and has " + Math.round(100.0 * LNZ.
          status_chilled_speedMultiplier) + "% movement and attack speed." +
          "\nIf this unit is cyan it has " + Math.round(100.0 * LNZ.
            status_chilled_speedMultiplierCyan) + "% movement and attack speed.";
      case FROZEN:
        return "This unit is frozen and cannot move or attack." +
          "\nIf this unit is orange they will take small damage to " + Math.round(100.0 *
          LNZ.status_frozen_damageLimit) + "% max health.";
      case SICK:
        return "This unit is sick and cannot defend themselves. They will take " +
          Math.round(100.0 * (LNZ.status_sick_damageMultiplier - 1)) + "% more " +
          "damage from all sources and have their defensive stats reduced by " +
          Math.round(100.0 * (1 - LNZ.status_sick_defenseMultiplier)) + "%.";
      case DISEASED:
        return "This unit is diseased and cannot defend themselves. They will take " +
          Math.round(100.0 * (LNZ.status_diseased_damageMultiplier - 1)) + "% more " +
          "damage from all sources and have their defensive stats reduced by " +
          Math.round(100.0 * (1 - LNZ.status_diseased_defenseMultiplier)) + "%.";
      case ROTTING:
        return "This unit is rotting and will take damage to " + Math.round(100.0 *
          LNZ.status_rotting_damageLimit) + "% max health.\nIf this " +
          "unit is blue they can die from rotting; if this unit is brown they " +
          "will only take damage to " + Math.round(100.0 * LNZ.
          status_rotting_damageLimitBrown) + "% max health.";
      case DECAYED:
        return "This unit is decayed and will take damage to their death.\nIf " +
        "this unit is brown they will only take damage to " + Math.round(100.0 *
        LNZ.status_decayed_damageLimitBrown) + "% max health.";
      case SHAKEN:
        return "This unit is shaken.";
      case FALLEN:
        return "This unit is fallen.";
      case SHOCKED:
        return "This unit is shocked.";
      case PARALYZED:
        return "This unit is paralyzed.";
      case UNSTABLE:
        return "This unit is unstable.";
      case RADIOACTIVE:
        return "This unit is radioactive.";
      case NELSON_GLARE:
        return "This unit is Nelson glared and has " + Math.round(100.0 * (1 - LNZ.
          ability_103_debuff)) + "% reduced attack and speed.";
      case NELSON_GLAREII:
        return "This unit is Nelson glared and has " + Math.round(100.0 * (1 - LNZ.
          ability_108_debuff)) + "% reduced attack and speed.";
      case SENSELESS_GRIT:
        return "This unit has senseless grit and has " + Math.round(100.0 * (LNZ.
          ability_104_speedBuff - 1)) + "% more move speed when targeting enemies.";
      case SENSELESS_GRITII:
        return "This unit has senseless grit and has " + Math.round(100.0 * (LNZ.
          ability_109_speedBuff - 1)) + "% more move speed when targeting enemies.";
      case RAGE_OF_THE_BEN:
        return "This unit has the rage of the Ben and has " + Math.round(100.0 * (LNZ.
          ability_105_buffAmount - 1)) + "% increased attack and incrased rage gains.";
      case RAGE_OF_THE_BENII:
        return "This unit has the rage of the Ben and has " + Math.round(100.0 * (LNZ.
          ability_110_buffAmount - 1)) + "% increased attack and incrased rage gains.";
      case APOSEMATIC_CAMOUFLAGE:
        return "This unit is camouflaged and cannot be seen by enemies.\nThis " +
        "unit will also have a " + Math.round(100.0 * (LNZ.ability_111_powerBuff -
        1)) + "% bonus power the first attack they deliver while camouflaged.";
      case APOSEMATIC_CAMOUFLAGEII:
        return "This unit is camouflaged and cannot be seen by enemies.\nThis " +
        "unit will also have a " + Math.round(100.0 * (LNZ.ability_116_powerBuff -
        1)) + "% bonus power the first attack they deliver while camouflaged.";
      case TONGUE_LASH:
        return "This unit has been tongue lashed and is slowed by " + Math.round(100.0 *
          (1 - LNZ.ability_112_slowAmount)) + "%.";
      case ALKALOID_SECRETION:
      case ALKALOID_SECRETIONII:
        return "This unit is secreting alkaloids and damaging nearby enemy units " +
        "every " + LNZ.ability_114_tickTime + "ms while also making them rot.";
      default:
        return "";
    }
  }
}