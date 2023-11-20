package LNZModule;

import Form.*;
import Misc.Misc;

public class ItemEditForm extends EditMapObjectForm {
  protected Item item;

  ItemEditForm(LNZ sketch, Item item) {
    super(sketch, item);
    this.item = item;
    this.addField(new FloatFormField(p, "Heals: ", "curr health", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
    this.addField(new IntegerFormField(p, "Hunger: ", "hunger", -100, 100));
    this.addField(new IntegerFormField(p, "Thirst: ", "thirst", -100, 100));
    this.addField(new FloatFormField(p, "Money: ", "money", 0, Float.MAX_VALUE - 1));
    this.addField(new FloatFormField(p, "Health: ", "health", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
    this.addField(new FloatFormField(p, "Attack: ", "attack", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
    this.addField(new FloatFormField(p, "Magic: ", "magic", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
    this.addField(new FloatFormField(p, "Defense: ", "defense", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
    this.addField(new FloatFormField(p, "Resistance: ", "resistance", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
    this.addField(new FloatFormField(p, "Piercing: ", "piercing", -1, 1));
    this.addField(new FloatFormField(p, "Penetration: ", "penetration", -1, 1));
    this.addField(new FloatFormField(p, "Attack Range: ", "attack range", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
    this.addField(new FloatFormField(p, "Attack Cooldown: ", "attack cooldown", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
    this.addField(new FloatFormField(p, "Attack Time: ", "attack time", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
    this.addField(new FloatFormField(p, "Sight: ", "sight", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
    this.addField(new FloatFormField(p, "Speed: ", "speed", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
    this.addField(new FloatFormField(p, "Tenacity: ", "tenacity", -1, 1));
    this.addField(new IntegerFormField(p, "Agility: ", "agility", -10, 10));
    this.addField(new IntegerFormField(p, "Ammo: ", "ammo", 0, Integer.MAX_VALUE - 1));
    this.addField(new IntegerFormField(p, "Stack: ", "stack", 0, Integer.MAX_VALUE - 1));
    this.addField(new IntegerFormField(p, "Durability: ", "durability", 0, Integer.MAX_VALUE - 1));
    this.addField(new SubmitFormField(p, "Finished", false));
    this.updateForm();
  }

  void updateObject() {
    this.item.curr_health = Misc.toDouble(this.fields.get(1).getValue());
    this.item.hunger =  Misc.toInt(this.fields.get(2).getValue());
    this.item.thirst =  Misc.toInt(this.fields.get(3).getValue());
    this.item.money = Misc.toDouble(this.fields.get(4).getValue());
    this.item.health = Misc.toDouble(this.fields.get(5).getValue());
    this.item.attack = Misc.toDouble(this.fields.get(6).getValue());
    this.item.magic = Misc.toDouble(this.fields.get(7).getValue());
    this.item.defense = Misc.toDouble(this.fields.get(8).getValue());
    this.item.resistance = Misc.toDouble(this.fields.get(9).getValue());
    this.item.piercing = Misc.toDouble(this.fields.get(10).getValue());
    this.item.penetration = Misc.toDouble(this.fields.get(11).getValue());
    this.item.attackRange = Misc.toDouble(this.fields.get(12).getValue());
    this.item.attackCooldown = Misc.toDouble(this.fields.get(13).getValue());
    this.item.attackTime = Misc.toDouble(this.fields.get(14).getValue());
    this.item.sight = Misc.toDouble(this.fields.get(15).getValue());
    this.item.speed = Misc.toDouble(this.fields.get(16).getValue());
    this.item.tenacity = Misc.toDouble(this.fields.get(17).getValue());
    this.item.agility = Misc.toInt(this.fields.get(18).getValue());
    this.item.ammo = Misc.toInt(this.fields.get(19).getValue());
    this.item.stack = Misc.toInt(this.fields.get(20).getValue());
    this.item.durability = Misc.toInt(this.fields.get(21).getValue());
  }

  void updateForm() {
    this.fields.get(1).setValueIfNotFocused(Double.toString(this.item.curr_health));
    this.fields.get(2).setValueIfNotFocused(Integer.toString(this.item.hunger));
    this.fields.get(3).setValueIfNotFocused(Integer.toString(this.item.thirst));
    this.fields.get(4).setValueIfNotFocused(Double.toString(this.item.money));
    this.fields.get(5).setValueIfNotFocused(Double.toString(this.item.health));
    this.fields.get(6).setValueIfNotFocused(Double.toString(this.item.attack));
    this.fields.get(7).setValueIfNotFocused(Double.toString(this.item.magic));
    this.fields.get(8).setValueIfNotFocused(Double.toString(this.item.defense));
    this.fields.get(9).setValueIfNotFocused(Double.toString(this.item.resistance));
    this.fields.get(10).setValueIfNotFocused(Double.toString(this.item.piercing));
    this.fields.get(11).setValueIfNotFocused(Double.toString(this.item.penetration));
    this.fields.get(12).setValueIfNotFocused(Double.toString(this.item.attackRange));
    this.fields.get(13).setValueIfNotFocused(Double.toString(this.item.attackCooldown));
    this.fields.get(14).setValueIfNotFocused(Double.toString(this.item.attackTime));
    this.fields.get(15).setValueIfNotFocused(Double.toString(this.item.sight));
    this.fields.get(16).setValueIfNotFocused(Double.toString(this.item.speed));
    this.fields.get(17).setValueIfNotFocused(Double.toString(this.item.tenacity));
    this.fields.get(18).setValueIfNotFocused(Integer.toString(this.item.agility));
    this.fields.get(19).setValueIfNotFocused(Integer.toString(this.item.ammo));
    this.fields.get(20).setValueIfNotFocused(Integer.toString(this.item.stack));
    this.fields.get(21).setValueIfNotFocused(Integer.toString(this.item.durability));
  }
}