package LNZModule;

import Form.*;
import Misc.Misc;

public class UnitEditForm extends EditMapObjectForm {
  protected Unit unit;

  UnitEditForm(LNZ sketch, Unit unit) {
    super(sketch, unit);
    this.unit = unit;
    this.addField(new FloatFormField(sketch, "Base Health: ", "base health", 0, Double.MAX_VALUE - 1));
    this.addField(new FloatFormField(sketch, "Base Attack: ", "base attack", 0, Double.MAX_VALUE - 1));
    this.addField(new FloatFormField(sketch, "Base Magic: ", "base magic", 0, Double.MAX_VALUE - 1));
    this.addField(new FloatFormField(sketch, "Base Defense: ", "base defense", 0, Double.MAX_VALUE - 1));
    this.addField(new FloatFormField(sketch, "Base Resistance: ", "base resistance", 0, Double.MAX_VALUE - 1));
    this.addField(new FloatFormField(sketch, "Base Piercing: ", "base piercing", 0, 1));
    this.addField(new FloatFormField(sketch, "Base Penetration: ", "base penetration", 0, 1));
    this.addField(new FloatFormField(sketch, "Base Attack Range: ", "base attack range", 0, Double.MAX_VALUE - 1));
    this.addField(new FloatFormField(sketch, "Base Sight: ", "base sight", 0, Double.MAX_VALUE - 1));
    this.addField(new FloatFormField(sketch, "Base Speed: ", "base speed", 0, Double.MAX_VALUE - 1));
    this.addField(new FloatFormField(sketch, "Base Tenacity: ", "base tenacity", 0, 1));
    this.addField(new IntegerFormField(sketch, "Base Agility: ", "base agility", 0, 10));
    this.addField(new CheckboxFormField(sketch, "AI Toggle:  "));
    this.addField(new SubmitFormField(sketch, "Finished", false));
    this.updateForm();
  }

  void updateObject() {
    this.unit.base_health = Misc.toDouble(this.fields.get(1).getValue());
    this.unit.base_attack = Misc.toDouble(this.fields.get(2).getValue());
    this.unit.base_magic = Misc.toDouble(this.fields.get(3).getValue());
    this.unit.base_defense = Misc.toDouble(this.fields.get(4).getValue());
    this.unit.base_resistance = Misc.toDouble(this.fields.get(5).getValue());
    this.unit.base_piercing = Misc.toDouble(this.fields.get(6).getValue());
    this.unit.base_penetration = Misc.toDouble(this.fields.get(7).getValue());
    this.unit.base_attackRange = Misc.toDouble(this.fields.get(8).getValue());
    this.unit.base_sight = Misc.toDouble(this.fields.get(9).getValue());
    this.unit.base_speed = Misc.toDouble(this.fields.get(10).getValue());
    this.unit.base_tenacity = Misc.toDouble(this.fields.get(11).getValue());
    this.unit.base_agility = Misc.toInt(this.fields.get(12).getValue());
    this.unit.ai_toggle = Misc.toBoolean(this.fields.get(13).getValue());
  }

  void updateForm() {
    this.fields.get(1).setValueIfNotFocused(Double.toString(this.unit.base_health));
    this.fields.get(2).setValueIfNotFocused(Double.toString(this.unit.base_attack));
    this.fields.get(3).setValueIfNotFocused(Double.toString(this.unit.base_magic));
    this.fields.get(4).setValueIfNotFocused(Double.toString(this.unit.base_defense));
    this.fields.get(5).setValueIfNotFocused(Double.toString(this.unit.base_resistance));
    this.fields.get(6).setValueIfNotFocused(Double.toString(this.unit.base_piercing));
    this.fields.get(7).setValueIfNotFocused(Double.toString(this.unit.base_penetration));
    this.fields.get(8).setValueIfNotFocused(Double.toString(this.unit.base_attackRange));
    this.fields.get(9).setValueIfNotFocused(Double.toString(this.unit.base_sight));
    this.fields.get(10).setValueIfNotFocused(Double.toString(this.unit.base_speed));
    this.fields.get(11).setValueIfNotFocused(Double.toString(this.unit.base_tenacity));
    this.fields.get(12).setValueIfNotFocused(Integer.toString(this.unit.base_agility));
    this.fields.get(13).setValueIfNotFocused(Boolean.toString(this.unit.ai_toggle));
  }
}