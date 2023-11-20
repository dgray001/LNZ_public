package LNZModule;

import java.util.Objects;
import processing.core.PApplet;
import Misc.Misc;

class DamageSource {
  protected int damage_source_key = 0;
  protected int unit_key = 0; // if killed by a unit
  protected int weapon_key = 0; // if killed by a projectile or item or ability
  protected StatusEffectCode status_effect = StatusEffectCode.ERROR;
  private int hash_code;

  DamageSource(int damage_source_key) {
    this(damage_source_key, 0, 0, StatusEffectCode.ERROR);
  }
  DamageSource(int damage_source_key, StatusEffectCode status_effect) {
    this(damage_source_key, 0, 0, status_effect);
  }
  DamageSource(int damage_source_key, int unit_key) {
    this(damage_source_key, unit_key, 0, StatusEffectCode.ERROR);
  }
  DamageSource(int damage_source_key, int unit_key, StatusEffectCode status_effect) {
    this(damage_source_key, unit_key, 0, status_effect);
  }
  DamageSource(int damage_source_key, int unit_key, int weapon_key) {
    this(damage_source_key, unit_key, weapon_key, StatusEffectCode.ERROR);
  }
  DamageSource(int damage_source_key, int unit_key, int weapon_key, StatusEffectCode status_effect) {
    this.damage_source_key = damage_source_key;
    this.unit_key = unit_key;
    this.weapon_key = weapon_key;
    this.status_effect = status_effect;
    this.hash_code = Objects.hash(damage_source_key, unit_key, weapon_key, status_effect);
  }

  String fileString() {
    return Integer.toString(this.damage_source_key) + ", " +
      Integer.toString(this.unit_key) + ", " + 
      Integer.toString(this.weapon_key) + ", " +
      this.status_effect.codeName();
  }
  static DamageSource toDamageSource(String file_string) {
    String[] data = PApplet.split(file_string, ',');
    int damage_source_key = Misc.toInt(PApplet.trim(data[0]));
    int unit_key = Misc.toInt(PApplet.trim(data[1]));
    int weapon_key = Misc.toInt(PApplet.trim(data[2]));
    StatusEffectCode status_effect = StatusEffectCode.code(PApplet.trim(data[3]));
    return new DamageSource(damage_source_key, unit_key, weapon_key, status_effect);
  }

  @Override
  public String toString() {
    return this.displayString();
  }
  DamageSource copy() {
    return new DamageSource(this.damage_source_key, this.unit_key, this.weapon_key, this.status_effect);
  }

  @Override
  public boolean equals(Object damage_source_object) {
    if (this == damage_source_object) {
      return true;
    }
    if (damage_source_object == null || this.getClass() != damage_source_object.getClass()) {
      return false;
    }
    DamageSource damage_source = (DamageSource)damage_source_object;
    if (this.damage_source_key == damage_source.damage_source_key &&
      this.unit_key == damage_source.unit_key &&
      this.weapon_key == damage_source.weapon_key &&
      this.status_effect == damage_source.status_effect) {
      return true;
    }
    return false;
  }
  @Override
  public int hashCode() {
    return this.hash_code;
  }

  String displayString() {
    switch(this.damage_source_key) {
      case 1: // fall
        return "Falling";
      case 2: // stray projectile
        return Projectile.projectileName(this.unit_key);
      case 3: // lava
        return "Lava";
      case 4: // status effect (unknown source)
        return StatusEffectCode.codeName(this.status_effect);
      case 5: // Water
        return "Drowning in Water";
      case 11: // auto attack (no weapon or projectile)
        return Unit.unitName(this.unit_key);
      case 12: // auto attack (with weapon)
        return Unit.unitName(this.unit_key) + " wielding " + Item.itemName(this.weapon_key);
      case 13: // projectile (from unit)
        return Projectile.projectileName(this.weapon_key) + " shot by " + Unit.unitName(this.unit_key);
      case 14: // ability (from unit)
        return Ability.abilityName(this.weapon_key) + " from " + Unit.unitName(this.unit_key);
      case 15: // status effect (from unit)
        return StatusEffectCode.codeName(this.status_effect) + " from " + Unit.unitName(this.unit_key);
      case 16: // status effect (from item)
        return StatusEffectCode.codeName(this.status_effect) + " from " + Item.itemName(this.unit_key);
      case 21: // status effect (from climbing fence)
        return StatusEffectCode.codeName(this.status_effect) + " from climbing fence";
      default:
        return "Unknown Causes";
    }
  }
}