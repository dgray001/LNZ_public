package LNZModule;

import java.util.*;
import processing.core.*;
import LNZApplet.LNZApplet;
import Misc.*;

class Item extends MapObject {
  protected boolean disappearing = false;
  protected int disappear_timer = 0;
  protected int map_key = -10;

  protected int stack = 1;

  protected double size = LNZ.item_defaultSize; // radius
  protected int tier = 1;

  protected double curr_health = 0;
  protected int hunger = 0;
  protected int thirst = 0;
  protected double money = 0;

  protected double health = 0;
  protected double attack = 0;
  protected double magic = 0;
  protected double defense = 0;
  protected double resistance = 0;
  protected double piercing = 0; // percentage from 0 - 1
  protected double penetration = 0; // percentage from 0 - 1
  protected double attackRange = 0;
  protected double attackCooldown = 0;
  protected double attackTime = 0;
  protected double sight = 0;
  protected double speed = 0;
  protected double tenacity = 0; // percentage from 0 - 1
  protected int agility = 0;
  protected double lifesteal = 0; // percentage
  protected boolean save_base_stats = false; // toggle true if manually changing stats

  protected int durability = 1; // when hits 0 item breaks
  protected int ammo = 0; // also used for other things (like key code)
  protected boolean toggled = false; // various uses
  protected Inventory inventory = null; // keyrings, item attachments, etc

  // graphics
  protected BounceInt bounce = new BounceInt(LNZ.item_bounceConstant);
  protected int recently_dropped = 0; // prevents magnetic hands picking up item too fast

  Item(LNZ sketch, Item i) {
    this(sketch, i, i.coordinate);
  }
  Item(LNZ sketch, Item i, Coordinate coordinate) {
    super(sketch);
    if (i == null) {
      this.remove = true;
      return;
    }
    this.ID = i.ID;
    this.display_name = i.display_name;
    this.type = i.type;
    this.description = i.description;
    this.coordinate = coordinate.copy();
    this.curr_height = i.curr_height;
    this.remove = i.remove;
    this.stack = i.stack;
    this.size = i.size;
    this.tier = i.tier;
    this.curr_health = i.curr_health;
    this.hunger = i.hunger;
    this.thirst = i.thirst;
    this.money = i.money;
    this.health = i.health;
    this.attack = i.attack;
    this.magic = i.magic;
    this.defense = i.defense;
    this.resistance = i.resistance;
    this.piercing = i.piercing;
    this.penetration = i.penetration;
    this.attackRange = i.attackRange;
    this.attackCooldown = i.attackCooldown;
    this.attackTime = i.attackTime;
    this.sight = i.sight;
    this.speed = i.speed;
    this.tenacity = i.tenacity;
    this.agility = i.agility;
    this.lifesteal = i.lifesteal;
    this.ammo = i.ammo;
    this.toggled = i.toggled;
    this.durability = i.durability;
    this.inventory = i.inventory; // not a deep copy just a reference copy
    this.bounce = i.bounce;
  }
  Item(LNZ sketch, int ID) {
    super(sketch, ID);
    switch(ID) {
      // Farming
      case 2001:
        this.setStrings("Wapato Seed", "Seed", "Seed to the wapato plant.");
        this.size = 0.2;
        break;
      case 2002:
        this.setStrings("Wapato", "Food", "The edible tuber of the wapato " +
          "plant, traditionally a key part of many native american diets.");
        this.hunger = 15;
        break;
      case 2003:
        this.setStrings("Leek Seeds", "Seed", "Seeds to the leek plant.");
        break;
      case 2004:
        this.setStrings("Leek", "Food", "Rather than forming a true bulb " +
          "like its cousin the onion, a leek forms a bundle of leaf sheaths, " +
          "although it's just as edible.");
        this.hunger = 12;
        this.thirst = 5;
        break;
      case 2005:
        this.setStrings("Plantain Seeds", "Seed", "Seeds to the broadleaf plantain.");
        break;
      case 2006:
        this.setStrings("Plantain Leaves", "Food", "Edible leaves of the broadleaf plantain.");
        break;
      case 2007:
        this.setStrings("Ryegrass Seeds", "Seed", "Edible seeds to wild ryegrass.");
        this.hunger = 9;
        break;
      case 2008:
        this.setStrings("Barnyard Grass Seeds", "Seed", "Edible seeds to wild barnyard grass.");
        this.hunger = 7;
        break;
      case 2009:
        this.setStrings("Nettle Seeds", "Seed", "Seeds to the stinging nettle plant.");
        this.tier = 2;
        break;
      case 2010:
        this.setStrings("Nettle Leaves", "Food", "Used in cultures across " +
          "the world and throughout millenia in a wide variety of dishes.");
        this.tier = 2;
        this.hunger = 10;
        this.thirst = 5;
        this.curr_health = 2.1;
        break;
      case 2011:
        this.setStrings("Maple Seed", "Seed",
          "The classic example of a samara or winged dry fruit. " +
          "Also known as a helicopter or a wingnut.");
        this.tier = 2;
        break;
      case 2012:
        this.setStrings("Black Walnut", "Seed",
          "The black walnut is the walnut nut of a black walnut.");
        this.tier = 2;
        this.hunger = 12;
        break;
      case 2013:
        this.setStrings("Juniper Berries", "Seed", "");
        this.tier = 2;
        this.hunger = 8;
        this.thirst = 6;
        break;
      case 2014:
        this.setStrings("Acorn", "Seed", "Some people " +
          "think acorns should be called oaknuts. However, these people are " +
          "misguided by the stupid assertion that nomenclature should make sense.");
        this.tier = 2;
        this.hunger = 10;
        break;
      case 2015:
        this.setStrings("Pine Cone", "Seed", "");
        this.tier = 2;
        this.hunger = 10;
        break;
      case 2021:
        this.setStrings("Dandelion", "Seed", "");
        break;

      // Consumables
      case 2101:
        this.setStrings("Crumb", "Food", "A small fragment of leftover food.");
        this.hunger = 1;
        break;
      case 2102:
        this.setStrings("Unknown Food", "Food", "It looks edible, but what is it?");
        this.hunger = 3;
        this.thirst = 1;
        break;
      case 2103:
        this.setStrings("Unknown Food", "Food", "It looks edible, but what is it?");
        this.hunger = 3;
        this.thirst = 1;
        break;
      case 2104:
        this.setStrings("Unknown Food", "Food", "It looks edible, but what is it?");
        this.hunger = 3;
        this.thirst = 1;
        break;
      case 2105:
        this.setStrings("Unknown Food", "Food", "It looks edible, but what is it?");
        this.hunger = 4;
        break;
      case 2106:
        this.setStrings("Pickle", "Food", "And we shall sit at the table under the pickles.");
        this.hunger = 10;
        this.thirst = 5;
        break;
      case 2107:
        this.setStrings("Ketchup", "Food", "Why did the ketchup blush?\nHe saw the salad dressing.");
        this.hunger = 6;
        this.thirst = 3;
        break;
      case 2108:
        this.setStrings("Chicken Wing", "Food", "Crispy and delicious, but not as good as Drover's.");
        this.hunger = 25;
        this.thirst = 5;
        break;
      case 2109:
        this.setStrings("Steak", "Food", "Waiter waiter!\nYes sir?\nDo you have " +
          "frog legs?\nWhy yes.\nGood, now hop off and fetch me a steak.");
        this.hunger = 50;
        this.thirst = 20;
        break;
      case 2110:
        this.setStrings("Poptart", "Food", "Why are there poptarts but no momtarts?");
        this.hunger = 20;
        this.thirst = -5;
        break;
      case 2111:
        this.setStrings("Donut", "Food", "I do-nut think anyone can resist a good donut.");
        this.hunger = 20;
        break;
      case 2112:
        this.setStrings("Chocolate", "Food", "Life is like a box of chocolates, mostly disappointing");
        this.hunger = 18;
        break;
      case 2113:
        this.setStrings("Chips", "Food", "");
        this.hunger = 15;
        this.thirst = -5;
        break;
      case 2114:
        this.setStrings("Cheese", "Food", "Food made from the pressed curds of milk.");
        this.hunger = 12;
        this.thirst = 6;
        break;
      case 2115:
        this.setStrings("Peanuts", "Food", "");
        this.hunger = 15;
        this.thirst = -5;
        break;
      case 2116:
        this.setStrings("Raw Chicken", "Food", "Raw meat from a chicken. " +
          "You probably don't want to eat it before it's cooked.");
        this.hunger = 20;
        this.thirst = 10;
        break;
      case 2117:
        this.setStrings("Cooked Chicken", "Food", "Crispy and delicious.");
        this.hunger = 40;
        this.thirst = 10;
        break;
      case 2118:
        this.setStrings("Chicken Egg", "Food", "Can be eaten for its nutrition or broken to see what's inside.");
        this.hunger = 25;
        this.thirst = 10;
        break;
      case 2119:
        this.setStrings("Rotten Flesh", "Food", "If you haven't contracted the " +
          "zombie infection yet, what can a little rotting flesh do to ya?");
        this.hunger = 10;
        this.thirst = 10;
        break;
      case 2120:
        this.setStrings("Apple", "Food", "");
        this.hunger = 18;
        this.thirst = 10;
        break;
      case 2121:
        this.setStrings("Banana", "Food", "If a banana ate another banana, would that be canabananalism?");
        this.hunger = 16;
        this.thirst = 6;
        break;
      case 2122:
        this.setStrings("Pear", "Food", "This is the pear that was kicked out to make room for the partridge.");
        this.hunger = 18;
        this.thirst = 8;
        break;
      case 2123:
        this.setStrings("Bread", "Food", "");
        this.hunger = 25;
        break;
      case 2124:
        this.setStrings("Hot Pocket Box", "Package", "");
        break;
      case 2125:
        this.setStrings("Hot Pocket", "Food", "It freezes you, burns you, and " +
          "gives you constipation. But I bet you eat it anyway.");
        this.hunger = 25;
        this.thirst = 5;
        break;
      case 2126:
        this.setStrings("Raw Chicken Wing", "Food", "Raw meat from a chicken. " +
          "You probably don't want to eat it before it's cooked.");
        this.hunger = 12;
        this.thirst = 3;
        break;
      case 2127:
        this.setStrings("Quail Egg", "Food", "A small egg laid by a quail.");
        this.hunger = 15;
        this.thirst = 7;
        break;
      case 2128:
        this.setStrings("Raw Quail", "Food", "Raw meat from a quail. " +
          "You probably don't want to eat it before it's cooked.");
        this.hunger = 12;
        this.thirst = 5;
        break;
      case 2129:
        this.setStrings("Cooked Quail", "Food", "Crispy and delicious.");
        this.hunger = 25;
        this.thirst = 8;
        break;
      case 2130:
        this.setStrings("Raw Venison", "Food", "Raw meat from a deer. " +
          "You probably don't want to eat it before it's cooked.");
        this.hunger = 20;
        this.thirst = 7;
        break;
      case 5101:
        this.setStrings("Cooked Venison", "Food", "Juicy and delicious.");
        this.hunger = 45;
        this.thirst = 15;
        break;
      case 2131:
        this.setStrings("Water Cup", "Drink", "A disposable cup of water.");
        this.thirst = 12;
        break;
      case 2132:
        this.setStrings("Coke", "Drink", "");
        this.hunger = 6;
        this.thirst = 15;
        break;
      case 2133:
        this.setStrings("Wine", "Drink", "Top of the bottom shelf!");
        this.hunger = 6;
        this.thirst = 20;
        break;
      case 2134:
        this.setStrings("Beer", "Drink",
          "It might not be Yuengling, but it still does the job.");
        this.hunger = 8;
        this.thirst = 20;
        break;
      case 2141:
        this.setStrings("Holy Water", "Drink", "");
        this.tier = 2;
        this.curr_health = 10.3;
        this.thirst = 50;
        break;
      case 2142:
        this.setStrings("Golden Apple", "Food", "Culturally " +
          "important symbol of immortality, but also tastes pretty good.");
        this.tier = 3;
        this.curr_health = 30.5;
        this.hunger = 35;
        this.thirst = 10;
        break;
      case 2151:
        this.setStrings("One Dollar", "Money", "Good for a single poptart.");
        this.money = 1;
        break;
      case 2152:
        this.setStrings("Five Dollars", "Money", "Good for a small box of poptarts.");
        this.money = 5;
        break;
      case 2153:
        this.setStrings("Ten Dollars", "Money", "Good for a large box of poptarts.");
        this.money = 10;
        break;
      case 2154:
        this.setStrings("Fifty Dollars", "Money", "Good for a box of boxes of poptarts.");
        this.money = 50;
        break;
      case 2155:
        this.setStrings("Zucc Bucc", "Money",
          "Give me the zucc, give me the zucc, z-u-c-c zucc!");
        this.tier = 2;
        this.money = 101;
        this.size = 0.3;
        break;
      case 2156:
        this.setStrings("Wad of 5s", "Money", "Good for a crate of poptarts.");
        this.tier = 2;
        this.money = 500;
        this.size = 0.3;
        break;
      case 2157:
        this.setStrings("Wad of 10s", "Money", "Good for a pallet of poptarts.");
        this.tier = 2;
        this.money = 1000;
        this.size = 0.3;
        break;
      case 2158:
        this.setStrings("Wad of 50s", "Money", "Good for a truckload of poptarts.");
        this.tier = 2;
        this.money = 5000;
        this.size = 0.3;
        break;
      case 2159:
        this.setStrings("Wad of Zuccz", "Money",
          "Give me the zuccz, give me the zuccz, z-u-c-c (and z?) zuccz!");
        this.tier = 3;
        this.money = 10100;
        this.size = 0.3;
        break;
      case 2161:
        this.setStrings("Broken Candlestick", "Household Item", "Hanlon did it.");
        this.size = 0.4;
        break;
      case 2162:
        this.setStrings("Candlestick", "Household Item", "");
        this.size = 0.4;
        break;
      case 2163:
        this.setStrings("Candle", "Household Item", "");
        this.size = 0.21;
        this.ammo = 1200000;
        break;
      case 2164:
        this.setStrings("Lord's Day Candle", "Household Item", "");
        this.size = 0.55;
        this.tier = 2;
        this.ammo = 1200000;
        break;
      case 2165:
        this.setStrings("Lord's Day Papers", "Household Item", "");
        this.tier = 2;
        break;
      case 2166:
        this.setStrings("Wooden Horse", "Household Item",
          "Said to be carved by Belemjian himself.");
        this.tier = 3;
        break;
      case 2167:
        this.setStrings("Workbench", "Household Item", "Used for crafting items.");
        this.tier = 4;
        break;
      case 2168:
        this.setStrings("Bed", "Furniture", "");
        this.tier = 4;
        break;
      case 2169:
        this.setStrings("Wooden Box", "Furniture", "");
        this.tier = 2;
        break;
      case 2170:
        this.setStrings("Wooden Crate", "Furniture", "");
        this.tier = 3;
        break;
      case 2171:
        this.setStrings("Wooden Chest", "Furniture", "");
        this.tier = 4;
        break;
      case 2172:
        this.setStrings("Large Wooden Chest", "Furniture", "");
        this.tier = 5;
        break;

      // Melee Weapons
      case 2201:
        this.setStrings("Foam Sword", "Melee Weapon",
          "Weapon of choice for recruits.");
        this.attack = 0.8;
        this.attackRange = 0.12;
        this.size = 0.3;
        this.durability = 10;
        break;
      case 2202:
        this.setStrings("Pan", "Melee Weapon", "If Rapunzel " +
          "taught us anything, it's that a pan is a melee weapon.");
        this.attack = 1.6;
        this.attackRange = 0.02;
        this.durability = 30;
        break;
      case 2203:
        this.setStrings("Knife", "Melee Weapon", "");
        this.attack = 2.5;
        this.attackRange = 0.01;
        this.piercing = 0.04;
        this.size = 0.3;
        this.durability = 45;
        break;
      case 2204:
        this.setStrings("Decoy", "Melee Weapon", "");
        this.attack = 5;
        this.attackRange = 0.1;
        this.piercing = 0.06;
        this.size = 0.3;
        this.durability = 70;
        break;
      case 2205:
        this.setStrings("Wooden Sword", "Melee Weapon", "");
        this.attack = 2.2;
        this.attackRange = 0.15;
        this.piercing = 0.01;
        this.size = 0.3;
        this.durability = 30;
        break;
      case 2206:
        this.setStrings("Talc Sword", "Melee Weapon", "");
        this.attack = 2;
        this.attackRange = 0.15;
        this.size = 0.3;
        this.durability = 10;
        break;
      case 2207:
        this.setStrings("Wooden Spear", "Melee Weapon", "");
        this.attack = 1.9;
        this.attackRange = 0.35;
        this.piercing = 0.04;
        this.size = 0.4;
        this.durability = 20;
        break;
      case 2208:
        this.setStrings("Talc Spear", "Melee Weapon", "");
        this.attack = 1.7;
        this.attackRange = 0.35;
        this.piercing = 0.03;
        this.size = 0.4;
        this.durability = 6;
        break;
      case 2209:
        this.setStrings("Wooden Shovel", "Tool", "");
        this.attack = 1.1;
        this.attackRange = 0.12;
        this.size = 0.3;
        this.durability = 20;
        break;
      case 2210:
        this.setStrings("Talc Shovel", "Tool", "");
        this.attack = 0.9;
        this.attackRange = 0.12;
        this.size = 0.3;
        this.durability = 8;
        break;
      case 5201:
        this.setStrings("Wooden Hoe", "Tool", "Everyone knows a hoe is a tool.");
        this.attack = 1.;
        this.attackRange = 0.12;
        this.size = 0.3;
        this.durability = 20;
        break;
      case 5202:
        this.setStrings("Talc Hoe", "Tool", "Everyone knows a hoe is a tool.");
        this.attack = 0.9;
        this.attackRange = 0.12;
        this.size = 0.3;
        this.durability = 8;
        break;
      case 5203:
        this.setStrings("Wooden Ax", "Tool", "");
        this.attack = 1.5;
        this.attackRange = 0.14;
        this.piercing = 0.02;
        this.size = 0.3;
        this.durability = 25;
        break;
      case 5204:
        this.setStrings("Talc Ax", "Tool", "");
        this.attack = 1.2;
        this.attackRange = 0.14;
        this.piercing = 0.01;
        this.size = 0.3;
        this.durability = 12;
        break;
      case 2211:
        this.setStrings("The Thing", "Melee Weapon", "");
        this.attack = 9;
        this.tier = 7;
        this.attackRange = 0.15;
        this.piercing = 0.08;
        this.size = 0.32;
        this.durability = 100;
        break;
      case 2212:
        this.setStrings("Gypsum Sword", "Melee Weapon", "");
        this.tier = 2;
        this.attack = 8;
        this.attackRange = 0.15;
        this.piercing = 0.02;
        this.size = 0.3;
        this.durability = 20;
        break;
      case 2213:
        this.setStrings("Gypsum Spear", "Melee Weapon", "");
        this.tier = 2;
        this.attack = 7;
        this.attackRange = 0.35;
        this.piercing = 0.05;
        this.size = 0.4;
        this.durability = 14;
        break;
      case 2214:
        this.setStrings("Board with Nails", "Melee Weapon", "");
        this.tier = 2;
        this.attack = 7.5;
        this.attackRange = 0.2;
        this.piercing = 0.02;
        this.size = 0.4;
        this.speed = -0.2;
        this.durability = 15;
        break;
      case 2221:
        this.setStrings("Calcite Sword", "Melee Weapon", "");
        this.tier = 3;
        this.attack = 16;
        this.attackRange = 0.15;
        this.piercing = 0.03;
        this.size = 0.3;
        this.durability = 30;
        break;
      case 2222:
        this.setStrings("Calcite Spear", "Melee Weapon", "");
        this.tier = 3;
        this.attack = 14;
        this.attackRange = 0.35;
        this.piercing = 0.08;
        this.size = 0.4;
        this.durability = 21;
        break;
      case 2223:
        this.setStrings("Metal Pipe", "Melee Weapon", "");
        this.tier = 3;
        this.attack = 14;
        this.attackRange = 0.25;
        this.size = 0.4;
        this.durability = 60;
        break;
      case 2231:
        this.setStrings("Fluorite Sword", "Melee Weapon", "");
        this.tier = 4;
        this.attack = 35;
        this.attackRange = 0.15;
        this.piercing = 0.05;
        this.size = 0.3;
        this.durability = 50;
        break;
      case 2232:
        this.setStrings("Fluorite Spear", "Melee Weapon", "");
        this.tier = 4;
        this.attack = 31;
        this.attackRange = 0.35;
        this.piercing = 0.12;
        this.size = 0.4;
        this.durability = 35;
        break;
      case 2233:
        this.setStrings("Iron Sword", "Melee Weapon", "");
        this.tier = 4;
        this.attack = 50;
        this.attackRange = 0.15;
        this.piercing = 0.05;
        this.size = 0.3;
        this.durability = 75;
        break;
      case 2234:
        this.setStrings("Iron Spear", "Melee Weapon", "");
        this.tier = 4;
        this.attack = 45;
        this.attackRange = 0.35;
        this.piercing = 0.12;
        this.size = 0.4;
        this.durability = 55;
        break;
      case 2241:
        this.setStrings("Apatite Sword", "Melee Weapon", "");
        this.tier = 5;
        this.attack = 65;
        this.attackRange = 0.15;
        this.piercing = 0.06;
        this.size = 0.3;
        this.durability = 70;
        break;
      case 2242:
        this.setStrings("Apatite Spear", "Melee Weapon", "");
        this.tier = 5;
        this.attack = 58;
        this.attackRange = 0.35;
        this.piercing = 0.14;
        this.size = 0.4;
        this.durability = 50;
        break;
      case 2251:
        this.setStrings("Orthoclase Sword", "Melee Weapon", "");
        this.tier = 6;
        this.attack = 90;
        this.attackRange = 0.15;
        this.piercing = 0.07;
        this.size = 0.3;
        this.durability = 100;
        break;
      case 2252:
        this.setStrings("Orthoclase Spear", "Melee Weapon", "");
        this.tier = 6;
        this.attack = 80;
        this.attackRange = 0.35;
        this.piercing = 0.16;
        this.size = 0.4;
        this.durability = 70;
        break;
      case 2261:
        this.setStrings("Quartz Sword", "Melee Weapon", "");
        this.tier = 7;
        this.attack = 130;
        this.attackRange = 0.15;
        this.piercing = 0.08;
        this.size = 0.3;
        this.durability = 120;
        break;
      case 2262:
        this.setStrings("Quartz Spear", "Melee Weapon", "");
        this.tier = 7;
        this.attack = 110;
        this.attackRange = 0.35;
        this.piercing = 0.18;
        this.size = 0.4;
        this.durability = 85;
        break;
      case 2271:
        this.setStrings("Topaz Sword", "Melee Weapon", "");
        this.tier = 8;
        this.attack = 190;
        this.attackRange = 0.15;
        this.piercing = 0.09;
        this.size = 0.3;
        this.durability = 150;
        break;
      case 2272:
        this.setStrings("Topaz Spear", "Melee Weapon", "");
        this.tier = 8;
        this.attack = 160;
        this.attackRange = 0.35;
        this.piercing = 0.2;
        this.size = 0.4;
        this.durability = 105;
        break;
      case 2281:
        this.setStrings("Corundum Sword", "Melee Weapon", "");
        this.tier = 9;
        this.attack = 280;
        this.attackRange = 0.15;
        this.piercing = 0.1;
        this.size = 0.3;
        this.durability = 250;
        break;
      case 2282:
        this.setStrings("Corundum Spear", "Melee Weapon", "");
        this.tier = 9;
        this.attack = 240;
        this.attackRange = 0.35;
        this.piercing = 0.22;
        this.size = 0.4;
        this.durability = 175;
        break;
      case 2291:
        this.setStrings("Diamond Sword", "Melee Weapon", "");
        this.tier = 10;
        this.attack = 390;
        this.attackRange = 0.15;
        this.piercing = 0.11;
        this.size = 0.3;
        this.durability = 500;
        break;
      case 2292:
        this.setStrings("Diamond Spear", "Melee Weapon", "");
        this.tier = 10;
        this.attack = 340;
        this.attackRange = 0.35;
        this.piercing = 0.24;
        this.size = 0.4;
        this.durability = 350;
        break;

      // Ranged Weapons
      case 2301:
        this.setStrings("Slingshot", "Ranged Weapon", "");
        this.attack = 5;
        this.size = 0.3;
        this.durability = 30;
        break;
      case 2311:
        this.setStrings("Recurve Bow", "Ranged Weapon", "");
        this.tier = 2;
        this.attack = 8;
        this.attackRange = 0.2;
        this.piercing = 0.15;
        this.size = 0.3;
        this.durability = 50;
        break;
      case 2312:
        this.setStrings("M1911", "Ranged Weapon", "Semi-automatic with medium capacity and power. Effective at close range.");
        this.tier = 2;
        this.attack = 2;
        this.size = 0.3;
        this.durability = 120;
        break;
      case 2321:
        this.setStrings("War Machine", "Ranged Weapon", "6 round semi-automatic grenade launcher.");
        this.tier = 3;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2322:
        this.setStrings("Five-Seven", "Ranged Weapon", "Semi-automatic pistol. Versatile and strong overall with a large magazine.");
        this.tier = 3;
        this.attack = 2;
        this.size = 0.3;
        this.durability = 120;
        break;
      case 2323:
        this.setStrings("Type25", "Ranged Weapon", "Fully automatic assault rifle. High rate of fire with moderate recoil.");
        this.tier = 3;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2331:
        this.setStrings("Mustang and Sally", "Ranged Weapon", "");
        this.tier = 4;
        this.attack = 3;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2332:
        this.setStrings("FAL", "Ranged Weapon", "Fully automatic assault rifle with high damage. Effective at medium to long range.");
        this.tier = 4;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2333:
        this.setStrings("Python", "Ranged Weapon", "The Python .357 magnum revolver. No thank you, I have reproductive organs of my own.");
        this.tier = 4;
        this.attack = 3;
        this.size = 0.3;
        this.durability = 120;
        break;
      case 2341:
        this.setStrings("RPG", "Ranged Weapon", "Free-fire shoulder mounted rocket launcher.");
        this.tier = 5;
        this.attack = 3;
        this.attackRange = 0.04;
        this.speed = -1;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2342:
        this.setStrings("Dystopic Demolisher", "Ranged Weapon", "");
        this.tier = 5;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2343:
        this.setStrings("Ultra", "Ranged Weapon", "");
        this.tier = 5;
        this.attack = 3;
        this.size = 0.3;
        this.durability = 120;
        break;
      case 2344:
        this.setStrings("Strain25", "Ranged Weapon", "");
        this.tier = 5;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2345:
        this.setStrings("Executioner", "Ranged Weapon", "Double-action revolver pistol. Fires 28 gauge shotgun shells.");
        this.tier = 5;
        this.attack = 3;
        this.size = 0.3;
        this.size = 0.3;
        this.durability = 120;
        break;
      case 2351:
        this.setStrings("Galil", "Ranged Weapon", "Fully automatic assault rifle. Effective at medium to long range.");
        this.tier = 6;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2352:
        this.setStrings("WN", "Ranged Weapon", "");
        this.tier = 6;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2353:
        this.setStrings("Ballistic Knife", "Ranged Weapon", "Spring-action knife launcher. Increases melee speed and can fire the blade as a projectile.");
        this.tier = 6;
        this.attack = 3;
        this.attackRange = 0.04;
        this.speed = 1;
        this.lifesteal = 0.1;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2354:
        this.setStrings("Cobra", "Ranged Weapon", "");
        this.tier = 6;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2355:
        this.setStrings("MTAR", "Ranged Weapon", "Fully automatic assault rifle. Versatile and strong overall.");
        this.tier = 6;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2361:
        this.setStrings("RPD", "Ranged Weapon", "Fully automatic with good power and quick fire rate. Effective at medium to long range.");
        this.tier = 7;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2362:
        this.setStrings("Rocket-Propelled Grievance", "Ranged Weapon", "");
        this.tier = 7;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2363:
        this.setStrings("DSR-50", "Ranged Weapon", "");
        this.tier = 7;
        this.attack = 3;
        this.attackRange = 0.04;
        this.speed = -1;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2364:
        this.setStrings("Voice of Justice", "Ranged Weapon", "");
        this.tier = 7;
        this.attack = 3;
        this.size = 0.3;
        this.durability = 120;
        break;
      case 2371:
        this.setStrings("HAMR", "Ranged Weapon", "Fully automatic LMG. Reduces fire rate with less ammo, becoming more accurate.");
        this.tier = 8;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2372:
        this.setStrings("Ray Gun", "Ranged Weapon", "It's weird, but it works.");
        this.tier = 8;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2373:
        this.setStrings("Lamentation", "Ranged Weapon", "");
        this.tier = 8;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2374:
        this.setStrings("The Krauss Refibrillator", "Ranged Weapon", "");
        this.tier = 8;
        this.attack = 3;
        this.attackRange = 0.04;
        this.speed = 1.5;
        this.lifesteal = 0.15;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2375:
        this.setStrings("Malevolent Taxonomic Anodized Redeemer", "Ranged Weapon", "");
        this.tier = 8;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2381:
        this.setStrings("Relativistic Punishment Device", "Ranged Weapon", "");
        this.tier = 9;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2382:
        this.setStrings("Dead Specimen Reactor 5000", "Ranged Weapon", "");
        this.tier = 9;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2391:
        this.setStrings("SLDG HAMR", "Ranged Weapon", "");
        this.tier = 10;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;
      case 2392:
        this.setStrings("Porter's X2 Ray Gun", "Ranged Weapon", "");
        this.tier = 10;
        this.attack = 3;
        this.attackRange = 0.04;
        this.size = 0.35;
        this.durability = 120;
        break;

      // Headgear
      case 2401:
        this.setStrings("Talc Helmet", "Headgear", "");
        this.defense = 1;
        this.durability = 10;
        break;
      case 2402:
        this.setStrings("Cap", "Headgear", "");
        this.durability = 10;
        break;
      case 2403:
        this.setStrings("Bowl", "Headgear", "");
        this.defense = 1;
        this.durability = 25;
        break;
      case 2404:
        this.setStrings("Pot", "Headgear", "");
        this.defense = 2;
        this.speed = -0.5;
        this.durability = 60;
        break;
      case 2411:
        this.setStrings("Gypsum Helmet", "Headgear", "");
        this.tier = 2;
        this.defense = 2;
        this.durability = 20;
        break;
      case 2412:
        this.setStrings("Leather Helmet", "Headgear", "");
        this.tier = 2;
        this.defense = 2.5;
        this.durability = 45;
        break;
      case 2421:
        this.setStrings("Calcite Helmet", "Headgear", "");
        this.tier = 3;
        this.defense = 3.5;
        this.durability = 30;
        break;
      case 2431:
        this.setStrings("Fluorite Helmet", "Headgear", "");
        this.tier = 4;
        this.defense = 7;
        this.durability = 50;
        break;
      case 2432:
        this.setStrings("Iron Helmet", "Headgear", "");
        this.tier = 4;
        this.defense = 10;
        this.durability = 75;
        break;
      case 2441:
        this.setStrings("Apatite Helmet", "Headgear", "");
        this.tier = 5;
        this.defense = 12;
        this.durability = 70;
        break;
      case 2451:
        this.setStrings("Orthoclase Helmet", "Headgear", "");
        this.tier = 6;
        this.defense = 19;
        this.durability = 100;
        break;
      case 2461:
        this.setStrings("Quartz Helmet", "Headgear", "");
        this.tier = 7;
        this.defense = 30;
        this.durability = 150;
        break;
      case 2471:
        this.setStrings("Topaz Helmet", "Headgear", "");
        this.tier = 8;
        this.defense = 44;
        this.durability = 250;
        break;
      case 2481:
        this.setStrings("Corundum Helmet", "Headgear", "");
        this.tier = 9;
        this.defense = 60;
        this.durability = 450;
        break;
      case 2491:
        this.setStrings("Diamond Helmet", "Headgear", "");
        this.tier = 10;
        this.defense = 80;
        this.durability = 1000;
        break;

      // Chestgear
      case 2501:
        this.setStrings("Talc Chestplate", "Chestgear", "");
        this.defense = 1;
        this.durability = 10;
        break;
      case 2502:
        this.setStrings("T-Shirt", "Chestgear", "");
        this.attackRange = 0.04;
        this.durability = 10;
        break;
      case 2503:
        this.setStrings("Bra", "Chestgear", "");
        this.attackRange = 0.02;
        this.durability = 10;
        break;
      case 2504:
        this.setStrings("Coat", "Chestgear", "");
        this.attackRange = 0.04;
        this.defense = 1;
        this.durability = 15;
        break;
      case 2511:
        this.setStrings("Gypsum Chestplate", "Chestgear", "");
        this.tier = 2;
        this.defense = 2;
        this.durability = 20;
        break;
      case 2512:
        this.setStrings("Ben's Coat", "Chestgear", "");
        this.tier = 2;
        this.health = 3;
        this.attackRange = 0.05;
        this.defense = 2;
        this.durability = 35;
        break;
      case 2513:
        this.setStrings("Suit Jacket", "Chestgear", "");
        this.tier = 2;
        this.attackRange = 0.04;
        this.durability = 15;
        break;
      case 2514:
        this.setStrings("Leather Shirt", "Chestgear", "");
        this.tier = 2;
        this.defense = 2.5;
        this.durability = 45;
        break;
      case 2521:
        this.setStrings("Calcite Chestplate", "Chestgear", "");
        this.tier = 3;
        this.defense = 3.5;
        this.durability = 30;
        break;
      case 2531:
        this.setStrings("Fluorite Chestplate", "Chestgear", "");
        this.tier = 4;
        this.defense = 7;
        this.durability = 50;
        break;
      case 2532:
        this.setStrings("Iron Chestplate", "Chestgear", "");
        this.tier = 4;
        this.defense = 10;
        this.durability = 75;
        break;
      case 2541:
        this.setStrings("Apatite Chestplate", "Chestgear", "");
        this.tier = 5;
        this.defense = 12;
        this.durability = 70;
        break;
      case 2551:
        this.setStrings("Orthoclase Chestplate", "Chestgear", "");
        this.tier = 6;
        this.defense = 19;
        this.durability = 100;
        break;
      case 2561:
        this.setStrings("Quartz Chestplate", "Chestgear", "");
        this.tier = 7;
        this.defense = 30;
        this.durability = 150;
        break;
      case 2571:
        this.setStrings("Topaz Chestplate", "Chestgear", "");
        this.tier = 8;
        this.defense = 44;
        this.durability = 250;
        break;
      case 2581:
        this.setStrings("Corundum Chestplate", "Chestgear", "");
        this.tier = 9;
        this.defense = 60;
        this.durability = 450;
        break;
      case 2591:
        this.setStrings("Diamond Chestplate", "Chestgear", "");
        this.tier = 10;
        this.defense = 80;
        this.durability = 1000;
        break;

      // Leggear
      case 2601:
        this.setStrings("Talc Greaves", "Leggear", "");
        this.defense = 1;
        this.durability = 10;
        break;
      case 2602:
        this.setStrings("Boxers", "Leggear", "");
        this.attackRange = 0.02;
        this.durability = 10;
        break;
      case 2603:
        this.setStrings("Towel", "Leggear", "");
        this.attackRange = 0.06;
        this.durability = 8;
        break;
      case 2604:
        this.setStrings("Pants", "Leggear", "");
        this.attackRange = 0.08;
        this.defense = 1;
        this.durability = 15;
        break;
      case 2611:
        this.setStrings("Gypsum Greaves", "Leggear", "");
        this.tier = 2;
        this.defense = 2;
        this.durability = 20;
        break;
      case 2612:
        this.setStrings("Leather Pants", "Leggear", "");
        this.tier = 2;
        this.defense = 2.5;
        this.durability = 45;
        break;
      case 2622:
        this.setStrings("Calcite Greaves", "Leggear", "");
        this.tier = 3;
        this.defense = 3.5;
        this.durability = 30;
        break;
      case 2631:
        this.setStrings("Fluorite Greaves", "Leggear", "");
        this.tier = 4;
        this.defense = 7;
        this.durability = 50;
        break;
      case 2632:
        this.setStrings("Iron Greaves", "Leggear", "");
        this.tier = 4;
        this.defense = 10;
        this.durability = 75;
        break;
      case 2641:
        this.setStrings("Apatite Greaves", "Leggear", "");
        this.tier = 5;
        this.defense = 12;
        this.durability = 70;
        break;
      case 2651:
        this.setStrings("Orthoclase Greaves", "Leggear", "");
        this.tier = 6;
        this.defense = 19;
        this.durability = 100;
        break;
      case 2661:
        this.setStrings("Quartz Greaves", "Leggear", "");
        this.tier = 7;
        this.defense = 30;
        this.durability = 150;
        break;
      case 2671:
        this.setStrings("Topaz Greaves", "Leggear", "");
        this.tier = 8;
        this.defense = 44;
        this.durability = 250;
        break;
      case 2681:
        this.setStrings("Corundum Greaves", "Leggear", "");
        this.tier = 9;
        this.defense = 60;
        this.durability = 450;
        break;
      case 2691:
        this.setStrings("Diamond Greaves", "Leggear", "");
        this.tier = 10;
        this.defense = 80;
        this.durability = 1000;
        break;

      // Footgear
      case 2701:
        this.setStrings("Talc Boots", "Footgear", "");
        this.defense = 1;
        this.durability = 10;
        break;
      case 2702:
        this.setStrings("Socks", "Footgear", "");
        this.durability = 8;
        break;
      case 2703:
        this.setStrings("Sandals", "Footgear", "");
        this.speed = 0.2;
        this.durability = 12;
        break;
      case 2704:
        this.setStrings("Shoes", "Footgear", "");
        this.defense = 1;
        this.speed = 0.4;
        this.durability = 18;
        break;
      case 2705:
        this.setStrings("Boots", "Footgear", "");
        this.defense = 2;
        this.speed = 0.4;
        this.durability = 25;
        break;
      case 2711:
        this.setStrings("Gypsum Boots", "Footgear", "");
        this.tier = 2;
        this.defense = 2;
        this.durability = 20;
        break;
      case 2712:
        this.setStrings("Sneakers", "Footgear", "");
        this.tier = 2;
        this.defense = 1;
        this.speed = 0.6;
        this.durability = 40;
        break;
      case 2713:
        this.setStrings("Steel-Toed Boots", "Footgear", "");
        this.tier = 2;
        this.attack = 1;
        this.defense = 3;
        this.speed = 0.4;
        this.durability = 60;
        break;
      case 2714:
        this.setStrings("Cowboy Boots", "Footgear", "");
        this.tier = 2;
        this.defense = 2;
        this.speed = 0.6;
        this.durability = 60;
        break;
      case 2715:
        this.setStrings("Leather Boots", "Footgear", "");
        this.tier = 2;
        this.defense = 2.5;
        this.speed = 0.5;
        this.durability = 45;
        break;
      case 2721:
        this.setStrings("Calcite Boots", "Footgear", "");
        this.tier = 3;
        this.defense = 3.5;
        this.durability = 30;
        break;
      case 2731:
        this.setStrings("Fluorite Boots", "Footgear", "");
        this.tier = 4;
        this.defense = 7;
        this.durability = 50;
        break;
      case 2732:
        this.setStrings("Iron Boots", "Footgear", "");
        this.tier = 4;
        this.defense = 10;
        this.durability = 75;
        break;
      case 2741:
        this.setStrings("Apatite Boots", "Footgear", "");
        this.tier = 5;
        this.defense = 12;
        this.durability = 70;
        break;
      case 2751:
        this.setStrings("Orthoclase Boots", "Footgear", "");
        this.tier = 6;
        this.defense = 19;
        this.durability = 100;
        break;
      case 2761:
        this.setStrings("Quartz Boots", "Footgear", "");
        this.tier = 7;
        this.defense = 30;
        this.durability = 150;
        break;
      case 2771:
        this.setStrings("Topaz Boots", "Footgear", "");
        this.tier = 8;
        this.defense = 44;
        this.durability = 250;
        break;
      case 2781:
        this.setStrings("Corundum Boots", "Footgear", "");
        this.tier = 9;
        this.defense = 60;
        this.durability = 450;
        break;
      case 2791:
        this.setStrings("Diamond Boots", "Footgear", "");
        this.tier = 10;
        this.defense = 80;
        this.durability = 1000;
        break;

      // Material
      case 2801:
        this.setStrings("Talc Ore", "Material", "");
        break;
      case 2802:
        this.setStrings("Talc Crystal", "Material", "");
        break;
      case 2803:
        this.setStrings("Talc Powder", "Material", "");
        break;
      case 2804:
        this.setStrings("Soapstone", "Material", "");
        break;
      case 2805:
        this.setStrings("Broken Glass", "Material", "");
        break;
      case 2806:
        this.setStrings("Wire", "Material", "");
        break;
      case 2807:
        this.setStrings("Feather", "Material", "");
        break;
      case 2808:
        this.setStrings("Ashes", "Material", "");
        break;
      case 2809:
        this.setStrings("String", "Material", "");
        break;
      case 2810:
        this.setStrings("Wax", "Material", "");
        break;
      case 5801:
        this.setStrings("Ivy", "Material", "Strands of ivy which can be woven into string.");
        break;
      case 2811:
        this.setStrings("Gypsum Ore", "Material", "");
        this.tier = 2;
        break;
      case 2812:
        this.setStrings("Gypsum Crystal", "Material", "");
        this.tier = 2;
        break;
      case 2813:
        this.setStrings("Gypsum Powder", "Material", "");
        this.tier = 2;
        break;
      case 2814:
        this.setStrings("Selenite Crystal", "Material", "");
        this.tier = 2;
        break;
      case 2815:
        this.setStrings("Barbed Wire", "Material", "");
        this.tier = 2;
        break;
      case 2816:
        this.setStrings("Wooden Plank", "Material", "A large piece of cut " +
          "wood used to build various objects.");
        this.tier = 2;
        this.attack = 2.4;
        this.attackRange = 0.6;
        this.speed = -0.8;
        this.size = 0.48;
        break;
      case 2817:
        this.setStrings("Wooden Handle", "Material", "");
        this.tier = 2;
        this.attack = 2.8;
        this.attackRange = 0.4;
        this.size = 0.3;
        break;
      case 2818:
        this.setStrings("Wooden Piece", "Material", "A small piece of cut wood " +
          "used to build various objects.");
        this.tier = 2;
        this.attack = 0.5;
        this.attackRange = 0.02;
        this.size = 0.3;
        break;
      case 2819:
        this.setStrings("Cushion", "Material", "");
        this.tier = 2;
        this.size = 0.3;
        break;
      case 2820:
        this.setStrings("Rawhide", "Material", "");
        this.tier = 2;
        break;
      case 5811:
        this.setStrings("Leather", "Material", "");
        this.tier = 2;
        break;
      case 2821:
        this.setStrings("Calcite Ore", "Material", "");
        this.tier = 3;
        break;
      case 2822:
        this.setStrings("Calcite Crystal", "Material", "");
        this.tier = 3;
        break;
      case 2823:
        this.setStrings("Chalk", "Material", "");
        this.tier = 3;
        break;
      case 2824:
        this.setStrings("Iceland Spar", "Material", "");
        this.tier = 3;
        break;
      case 2825:
        this.setStrings("Star Piece", "Material", "");
        this.tier = 3;
        break;
      case 2826:
        this.setStrings("Antlers", "Material", "");
        this.tier = 3;
        break;
      case 2831:
        this.setStrings("Fluorite Ore", "Material", "");
        this.tier = 4;
        break;
      case 2832:
        this.setStrings("Fluorite Crystal", "Material", "");
        this.tier = 4;
        break;
      case 2833:
        this.setStrings("Iron Ore", "Material", "");
        this.tier = 4;
        break;
      case 2834:
        this.setStrings("Iron Chunk", "Material", "");
        this.tier = 4;
        break;
      case 2841:
        this.setStrings("Apatite Ore", "Material", "");
        this.tier = 5;
        break;
      case 2842:
        this.setStrings("Apatite Crystal", "Material", "");
        this.tier = 5;
        break;
      case 2843:
        this.setStrings("Iron Handle", "Material", "");
        this.tier = 5;
        this.attack = 12;
        this.attackRange = 0.4;
        this.size = 0.3;
        break;
      case 2851:
        this.setStrings("Orthoclase Ore", "Material", "");
        this.tier = 6;
        break;
      case 2852:
        this.setStrings("Orthoclase Chunk", "Material", "");
        this.tier = 6;
        break;
      case 2861:
        this.setStrings("Quartz Ore", "Material", "");
        this.tier = 7;
        break;
      case 2862:
        this.setStrings("Quartz Crystal", "Material", "");
        this.tier = 7;
        break;
      case 2863:
        this.setStrings("Amethyst", "Material", "");
        this.tier = 7;
        break;
      case 2864:
        this.setStrings("Glass", "Material", "");
        this.tier = 7;
        break;
      case 2871:
        this.setStrings("Topaz Ore", "Material", "");
        this.tier = 8;
        break;
      case 2872:
        this.setStrings("Topaz Chunk", "Material", "");
        this.tier = 8;
        break;
      case 2873:
        this.setStrings("Topaz Gem", "Material", "");
        this.tier = 8;
        break;
      case 2881:
        this.setStrings("Corundum Ore", "Material", "");
        this.tier = 9;
        break;
      case 2882:
        this.setStrings("Corundum Chunk", "Material", "");
        this.tier = 9;
        break;
      case 2883:
        this.setStrings("Sapphire", "Material", "");
        this.tier = 9;
        break;
      case 2891:
        this.setStrings("Diamond Ore", "Material", "");
        this.tier = 10;
        break;
      case 2892:
        this.setStrings("Diamond", "Material", "");
        this.tier = 10;
        break;

      // Other
      case 2901:
        this.setStrings("Key", "Key", "");
        break;
      case 2902:
        this.setStrings("Master Key", "Key", "");
        this.tier = 2;
        break;
      case 2903:
        this.setStrings("Skeleton Key", "Key", "");
        this.tier = 3;
        break;
      case 2904:
        this.setStrings("Small Key Ring", "Utility", "A small ring used to " +
          "hold keys. Holds up to 8 keys which can be used directly from the " +
          "keyring.");
        this.tier = 2;
        this.inventory = new SmallKeyringInventory(sketch);
        break;
      case 2905:
        this.setStrings("Large Key Ring", "Utility", "A large ring used to " +
          "hold keys. Holds up to 24 keys which can be used directly from the " +
          "keyring.");
        this.tier = 3;
        this.inventory = new LargeKeyringInventory(sketch);
        break;
      case 2906:
        this.setStrings("Car Key", "Key", "");
        this.tier = 3;
        break;
      case 2911:
        this.setStrings("Pen", "Office", "");
        this.attack = 0.6;
        this.durability = 6;
        break;
      case 2912:
        this.setStrings("Pencil", "Office", "");
        this.attack = 0.6;
        this.durability = 6;
        break;
      case 2913:
        this.setStrings("Paper", "Office", "");
        break;
      case 2914:
        this.setStrings("Document", "Office", "");
        break;
      case 2915:
        this.setStrings("Stapler", "Office", "");
        this.attack = 0.6;
        this.durability = 15;
        break;
      case 2916:
        this.setStrings("Crumpled Paper", "Office", "");
        break;
      case 2917:
        this.setStrings("Eraser", "Office", "");
        break;
      case 2918:
        this.setStrings("Scissors", "Office", "");
        this.attack = 1.5;
        this.durability = 12;
        break;
      case 2921:
        this.setStrings("Backpack", "Utility", "");
        this.attackRange = 0.04;
        break;
      case 2922:
        this.setStrings("Ben's Backpack", "Utility", "");
        this.attackRange = 0.04;
        break;
      case 2923:
        this.setStrings("Purse", "Utility", "");
        this.attackRange = 0.04;
        break;
      case 2924:
        this.setStrings("Glass Bottle", "Utility", "");
        this.attack = 0.8;
        this.piercing = 0.06;
        this.durability = 2;
        break;
      case 2925:
        this.setStrings("Water Bottle", "Utility", "");
        break;
      case 2926:
        this.setStrings("Canteen", "Utility", "");
        this.tier = 2;
        this.attack = 1;
        this.attackRange = 0.02;
        break;
      case 2927:
        this.setStrings("Water Jug", "Utility", "");
        this.tier = 3;
        this.attack = 1;
        this.attackRange = 0.02;
        break;
      case 2928:
        this.setStrings("Cigar", "Utility", "");
        this.ammo = LNZ.item_cigarLitTime;
        break;
      case 2929:
        this.setStrings("Gas Can", "Utility", "");
        this.size = 0.28;
        this.attack = 0.4;
        this.tier = 3;
        break;
      case 2930:
        this.setStrings("Waterskin", "Utility", "");
        break;
      case 5921:
        this.setStrings("Large Waterskin", "Utility", "");
        break;
      case 2931:
        this.setStrings("Rock", "Ammo", "A large pebble.");
        this.attack = 1;
        this.durability = 15;
        break;
      case 2932:
        this.setStrings("Arrow", "Ammo", "");
        this.attack = 1;
        this.piercing = 0.05;
        this.durability = 5;
        break;
      case 2933:
        this.setStrings("Pebble", "Ammo", "A small rock.");
        this.size = 0.22;
        this.durability = 15;
        break;
      case 2934:
        this.setStrings("Hammerstone", "Tool", "A primitive handtool used as a hammer.");
        this.size = 0.25;
        this.attack = 0.3;
        this.durability = 3;
        break;
      case 2941:
        this.setStrings(".45 ACP", "Ammo", "");
        this.size = 0.22;
        break;
      case 2942:
        this.setStrings("7.62x39mm", "Ammo", "");
        this.tier = 2;
        this.size = 0.22;
        break;
      case 2943:
        this.setStrings("5.56x45mm", "Ammo", "");
        this.tier = 2;
        this.size = 0.22;
        break;
      case 2944:
        this.setStrings("Grenade", "Ammo", "");
        this.tier = 2;
        this.attack = 2;
        break;
      case 2945:
        this.setStrings(".357 Magnum", "Ammo", "");
        this.tier = 2;
        this.size = 0.22;
        break;
      case 2946:
        this.setStrings(".50 BMG", "Ammo", "");
        this.tier = 2;
        this.size = 0.22;
        break;
      case 2947:
        this.setStrings("FN 5.7x28mm", "Ammo", "");
        this.tier = 2;
        this.size = 0.22;
        break;
      case 2948:
        this.setStrings("28 Gauge", "Ammo", "");
        this.tier = 2;
        this.size = 0.22;
        break;
      case 2961:
        this.setStrings("Dandelion", "Nature", "");
        this.hunger = 6;
        this.thirst = 2;
        break;
      case 2962:
        this.setStrings("Rose", "Nature", "");
        break;
      case 2963:
        this.setStrings("Stick", "Nature", "A thin piece of wood which can " +
          "be used for many purposed.");
        this.attack = 0.8;
        this.attackRange = 0.05;
        this.durability = 8;
        break;
      case 2964:
        this.setStrings("Kindling", "Nature", "Small bits of wood used to start fires.");
        this.attackRange = 0.04;
        break;
      case 2960:
      case 2965:
      case 2966:
      case 2967:
      case 2968:
        this.setStrings("Branch", "Nature", "");
        this.attack = 0.7;
        this.attackRange = 0.05;
        this.durability = 8;
        break;
      case 2969:
        this.setStrings("Wooden Log", "Nature", "Unprocessed wood from a tree.");
        this.size = 0.6;
        this.attack = 1.8;
        this.speed = -1.4;
        this.tier = 2;
        this.durability = 18;
        break;
      case 2970:
        this.setStrings("Wooden Peg", "Tool", "A primitive fastener used to build various wooden objects.");
        this.size = 0.22;
        this.durability = 1;
        break;
      case 2971:
        this.setStrings("Paintbrush", "Tool", "");
        this.durability = 100;
        break;
      case 2972:
        this.setStrings("Clamp", "Tool", "");
        this.attack = 1;
        this.attackRange = 0.02;
        this.durability = 100;
        break;
      case 2973:
        this.setStrings("Wrench", "Tool", "");
        this.attack = 1;
        this.attackRange = 0.02;
        this.durability = 100;
        break;
      case 2974:
        this.setStrings("Rope", "Tool", "");
        this.tier = 2;
        this.durability = 100;
        break;
      case 2975:
        this.setStrings("Hammer", "Tool", "");
        this.tier = 2;
        this.attack = 2;
        this.attackRange = 0.02;
        this.durability = 100;
        break;
      case 2976:
        this.setStrings("Window Breaker", "Tool", "A tool used to break glass.");
        this.tier = 2;
        this.attack = 1;
        this.durability = 100;
        break;
      case 2977:
        this.setStrings("Stone Hatchet", "Tool", "A primitive wood-cutting tool.");
        this.tier = 2;
        this.attack = 3;
        this.attackRange = 0.02;
        this.durability = 6;
        break;
      case 2978:
        this.setStrings("Wire Clippers", "Tool", "A tool used to cut wire.");
        this.tier = 3;
        this.attack = 2;
        this.attackRange = 0.1;
        this.size = 0.32;
        this.durability = 100;
        break;
      case 2979:
        this.setStrings("Saw", "Tool", "");
        this.tier = 3;
        this.attack = 2;
        this.attackRange = 0.05;
        this.piercing = 0.05;
        this.size = 0.32;
        this.durability = 100;
        break;
      case 2980:
        this.setStrings("Drill", "Tool", "");
        this.tier = 4;
        this.attack = 1;
        this.durability = 100;
        break;
      case 2981:
        this.setStrings("Roundsaw", "Tool", "");
        this.tier = 4;
        this.attack = 1;
        this.piercing = 0.05;
        this.size = 0.35;
        this.durability = 100;
        break;
      case 2982:
        this.setStrings("Beltsander", "Tool", "");
        this.tier = 4;
        this.attack = 1;
        this.size = 0.35;
        this.durability = 100;
        break;
      case 2983:
        this.setStrings("Chainsaw", "Tool", "");
        this.tier = 5;
        this.attack = 5;
        this.attackRange = 0.07;
        this.piercing = 0.2;
        this.size = 0.38;
        this.durability = 100;
        break;
      case 2984:
        this.setStrings("Woodglue", "Tool", "");
        this.tier = 2;
        this.durability = 16;
        break;
      case 2985:
        this.setStrings("Nails", "Tool", "");
        this.durability = 8;
        break;
      case 2986:
        this.setStrings("Screws", "Tool", "");
        this.durability = 8;
        this.tier = 2;
        break;
      case 2987:
        this.setStrings("Flint and Steel", "Tool", "");
        this.attack = 0.5;
        this.tier = 3;
        break;
      case 2988:
        this.setStrings("Lighter", "Tool", "");
        this.tier = 3;
        break;
      case 2991:
        this.setStrings("Rankin's Third Ball", "Rare Object", "A thing of legend.");
        this.tier = 1;
        this.size = 0.22;
        break;
      case 2992:
        this.setStrings("Soldier's Covenant", "Rare Object", "The only one Ben ever signed.");
        this.tier = 2;
        break;
      case 2993:
        this.setStrings("Jonah Plush Toy", "Rare Object", "Jonah was a prophet, OOH OOH !");
        this.tier = 3;
        break;
      case 2999:
        this.setStrings("Bens Eyes", "Rare Object", "Legend says Fischer stole " +
          "Ben's eyes right, well, um, before his eyes. I guess.");
        this.tier = 9;
        break;

      default:
        p.global.errorMessage("ERROR: Item ID " + ID + " not found.");
        break;
    }
  }
  Item(LNZ sketch, int ID, Coordinate coordinate) {
    this(sketch, ID);
    this.coordinate = coordinate.copy();
  }
  Item(LNZ sketch, int ID, double x, double y) {
    this(sketch, ID);
    this.coordinate = new Coordinate(x, y);
  }

  static String itemName(int ID) {
    return (new Item(null, ID)).displayName();
  }

  String displayName() {
    switch(this.ID) {
      case 2118: // Chicken Egg
        if (this.toggled) {
          return "Fertilized " + this.display_name;
        }
        return this.display_name;
      case 2901: // Key
        return this.display_name + " (" + this.ammo + ")";
      case 2902: // Master Key
        return this.display_name + " (" + this.ammo * 10 + " - " + (int)(this.ammo * 10 + 9) + ")";
      case 2903: // Skeleton Key
        return this.display_name + " (" + this.ammo * 100 + " - " + (int)(this.ammo * 100 + 99) + ")";
      case 2906: // Car Key
        switch(this.ammo) {
          case 1:
            return "Honda CRV key";
          case 2:
            return "Ford F150 key";
          case 3:
            return "VW Jetta key";
          case 4:
            return "VW Beetle key";
          case 5:
            return "Lamborghini key";
          default:
            return this.display_name;
        }
      case 2928: // Cigar
        if (this.toggled) {
          return "Lit " + this.display_name;
        }
        return this.display_name;
      case 2960: // Branch, oak
        if (p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "Oak " +  this.display_name;
        }
        break;
      case 2965: // Branch, maple
        if (p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "Maple " +  this.display_name;
        }
        break;
      case 2966: // Branch, walnut
        if (p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "Walnut " +  this.display_name;
        }
        break;
      case 2967: // Branch, cedar
        if (p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "Cedar " +  this.display_name;
        }
        break;
      case 2968: // Branch, pine
        if (p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "Pine " +  this.display_name;
        }
        break;
      default:
        return this.display_name;
    }
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
    String text = "-- " + this.type() + " --\n";
    if (this.curr_health != 0) {
      text += "\nHealth Regeneration: " + (int)(this.curr_health) + " + " +
        (100*(this.curr_health - (int)(this.curr_health))) + "% max health";
    }
    if (this.hunger != 0) {
      text += "\nFood: " + this.hunger;
    }
    if (this.thirst != 0) {
      text += "\nThirst: " + this.thirst;
    }
    if (this.money != 0) {
      text += "\nMoney: " + this.money;
    }
    if (this.health != 0) {
      text += "\nHealth: " + this.health;
    }
    if (this.breakable()) {
      switch(this.ID) {
        case 2984: // woodglue
        case 2985: // nails
        case 2986: // screws
          text += "\nAmount Left: " + this.durability;
          break;
        default:
          text += "\nDurability: " + this.durability;
          break;
      }
    }
    if (this.waterBottle()) {
      text += "\nWater: " + this.ammo + "/" + this.maximumAmmo();
    }
    else if (this.ID == 2929) {
      text += "\nGas: " + 0.1 * this.ammo + "/" + LNZApplet.round(0.1 * this.maximumAmmo()) + " gallons";
    }
    if (this.type.equals("Ranged Weapon")) {
      text += "\nAmmo: " + this.ammo + "/" + this.maximumAmmo();
      if (this.shootAttack() != 0) {
        text += "\nAttack: " + this.shootAttack();
      }
      if (this.shootMagic() != 0) {
        text += "\nMagic: " + this.shootMagic();
      }
      if (this.shootPiercing() != 0) {
        text += "\nPiercing: " + this.shootPiercing();
      }
      if (this.shootPenetration() != 0) {
        text += "\nPenetration: " + this.shootPenetration();
      }
      if (this.shootRange() != 0) {
        text += "\nRange: " + this.shootRange();
      }
      text += "\nInaccuracy: " + this.shootInaccuracy();
    }
    else {
      if (this.attack != 0) {
        text += "\nAttack: " + this.attack;
      }
      if (this.magic != 0) {
        text += "\nMagic: " + this.magic;
      }
      if (this.piercing != 0) {
        text += "\nPiercing: " + this.piercing;
      }
      if (this.penetration != 0) {
        text += "\nPenetration: " + this.penetration;
      }
      if (this.attackRange != 0) {
        text += "\nRange: " + this.attackRange;
      }
    }
    if (this.defense != 0) {
      text += "\nDefense: " + this.defense;
    }
    if (this.resistance != 0) {
      text += "\nResistance: " + this.resistance;
    }
    if (this.attackCooldown != 0) {
      text += "\nAttack Cooldown: " + this.attackCooldown;
    }
    if (this.attackTime != 0) {
      text += "\nAttack Time: " + this.attackTime;
    }
    if (this.sight != 0) {
      text += "\nSight: " + this.sight;
    }
    if (this.speed != 0) {
      text += "\nSpeed: " + this.speed;
    }
    if (this.tenacity != 0) {
      text += "\nTenacity: " + this.tenacity;
    }
    if (this.agility != 0) {
      text += "\nAgility: " + this.agility;
    }
    if (this.lifesteal != 0) {
      text += "\nLifesteal: " + this.lifesteal;
    }
    text += "\n\n" + this.description();
    if (p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
      switch(this.ID) {
        case 2001: // Wapato Seed
          text += "Growth time: 13.5 - 17.2 days." +
            "\nCan't grow if not watered." +
            "\nGrows twice as fast in shallow mud water.";
          break;
        case 2002: // Wapato
          text += "Growth time: 9.9 - 12.6 days." +
            "\nCan't grow if not watered." +
            "\nGrows twice as fast in shallow mud water.";
          break;
        case 2003: // Leek Seeds
          text += "Growth time: 6 - 7.5 days." +
            "\nGrows 80% slower if not watered.";
          break;
        case 2005: // Plantain Seeds
          text += "Growth time: 4.5 - 6 days." +
            "\nGrows 80% slower if not watered.";
          break;
        case 2007: // Ryegrass Seeds
          text += "Growth time: 3.9 - 5.1 days." +
            "\nGrows 80% slower if not watered.";
          break;
        case 2008: // Barnyard Grass Seeds
          text += "Growth time: 1.6 - 2.4 days." +
            "\nGrows 80% slower if not watered.";
          break;
        case 2009: // Nettle Seeds
          text += "Growth time: 3.6 - 4.6 days." +
            "\nGrows 80% slower if not watered.";
          break;
        default:
          break;
      }
    }
    return text;
  }

  void setLocation(double x, double y) {
    this.coordinate = new Coordinate(x, y);
  }
  void setLocation(Coordinate coordinate) {
    this.coordinate = coordinate.copy();
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
    return this.curr_height;
  }
  double zHalf() {
    return this.curr_height;
  }

  PImage getImage() {
    String path = "items/";
    switch(this.ID) {
      // Farming
      case 2001:
        path += "wapato_seed.png";
        break;
      case 2002:
        path += "wapato.png";
        break;
      case 2003:
        path += "leek_seeds.png";
        break;
      case 2004:
        path += "leek.png";
        break;
      case 2005:
        path += "plantain_seeds.png";
        break;
      case 2006:
        path += "plantain_leaves.png";
        break;
      case 2007:
        path += "ryegrass_seeds.png";
        break;
      case 2008:
        path += "barnyard_grass_seeds.png";
        break;
      case 2009:
        path += "nettle_seeds.png";
        break;
      case 2010:
        path += "nettle_leaves.png";
        break;
      case 2011:
        path += "maple_seed.png";
        break;
      case 2012:
        path += "black_walnut.png";
        break;
      case 2013:
        path += "juniper_berries.png";
        break;
      case 2014:
        path += "acorn.png";
        break;
      case 2015:
        path += "pine_cone.png";
        break;
      case 2021:
        path += "dandelion_seeds.png"; // TODO: Add image
        break;
      // Consumables
      case 2101:
        path += "crumb.png";
        break;
      case 2102:
        path += "wasabi.png";
        break;
      case 2103:
        path += "stuffing.jpg";
        break;
      case 2104:
        path += "catbarf.jpg";
        break;
      case 2105:
        path += "ramen.png";
        break;
      case 2106:
        path += "pickle.png";
        break;
      case 2107:
        path += "ketchup.png";
        break;
      case 2108:
        path += "chicken_wing.png";
        break;
      case 2109:
        path += "steak.png";
        break;
      case 2110:
        path += "poptart.png";
        break;
      case 2111:
        path += "donut.png";
        break;
      case 2112:
        path += "chocolate.png";
        break;
      case 2113:
        path += "chips.png";
        break;
      case 2114:
        path += "cheese.png";
        break;
      case 2115:
        path += "peanuts.png";
        break;
      case 2116:
        path += "raw_chicken.png";
        break;
      case 2117:
        path += "cooked_chicken.png";
        break;
      case 2118:
        path += "chicken_egg.png";
        break;
      case 2119:
        path += "rotten_flesh.png";
        break;
      case 2120:
        path += "apple.png";
        break;
      case 2121:
        path += "banana.png";
        break;
      case 2122:
        path += "pear.png";
        break;
      case 2123:
        path += "bread.png";
        break;
      case 2124:
        path += "hotpocket_box.png";
        break;
      case 2125:
        path += "hotpocket.png";
        break;
      case 2126:
        path += "raw_chicken_wing.png";
        break;
      case 2127:
        path += "quail_egg.png";
        break;
      case 2128:
        path += "raw_quail.png";
        break;
      case 2129:
        path += "cooked_quail.png";
        break;
      case 2130:
        path += "raw_venison.png";
        break;
      case 5101:
        path += "cooked_venison.png";
        break;
      case 2131:
        path += "water_cup.png";
        break;
      case 2132:
        path += "coke.png";
        break;
      case 2133:
        path += "wine.png";
        break;
      case 2134:
        path += "beer.png";
        break;
      case 2141:
        path += "holy_water.png";
        break;
      case 2142:
        path += "golden_apple.png";
        break;
      case 2151:
        path += "one_dollar.png";
        break;
      case 2152:
        path += "five_dollars.png";
        break;
      case 2153:
        path += "ten_dollars.png";
        break;
      case 2154:
        path += "fifty_dollars.png";
        break;
      case 2155:
        path += "zucc_bucc.png";
        break;
      case 2156:
        path += "wad_of_fives.png";
        break;
      case 2157:
        path += "wad_of_tens.png";
        break;
      case 2158:
        path += "wad_of_fifties.png";
        break;
      case 2159:
        path += "wad_of_zuccs.png";
        break;
      case 2161:
        path += "broken_candlestick.png";
        break;
      case 2162:
        path += "candlestick.png";
        break;
      case 2163:
        path += "candle.png";
        break;
      case 2164:
        path += "lords_day_candle.png";
        break;
      case 2165:
        path += "lords_day_papers.png";
        break;
      case 2166:
        path += "wooden_horse.png";
        break;
      case 2167:
        path = "features/workbench.jpg";
        break;
      case 2168:
        path = "features/bed_right.png";
        break;
      case 2169:
        path = "features/wooden_box.png";
        break;
      case 2170:
        path = "features/wooden_crate.png";
        break;
      case 2171:
        path = "features/wooden_chest.png";
        break;
      case 2172:
        path = "features/large_wooden_chest.png";
        break;
      // Melee Weapons
      case 2201:
        path += "foam_sword.png";
        break;
      case 2202:
        path += "pan.png";
        break;
      case 2203:
        path += "knife.png";
        break;
      case 2204:
        path += "decoy.png";
        break;
      case 2205:
        path += "wooden_sword.png";
        break;
      case 2206:
        path += "talc_sword.png";
        break;
      case 2207:
        path += "wooden_spear.png";
        break;
      case 2208:
        path += "talc_spear.png";
        break;
      case 2209:
        path += "wooden_shovel.png";
        break;
      case 2210:
        path += "talc_shovel.png";
        break;
      case 5201:
        path += "wooden_hoe.png";
        break;
      case 5202:
        path += "talc_hoe.png";
        break;
      case 5203:
        path += "wooden_ax.png";
        break;
      case 5204:
        path += "talc_ax.png";
        break;
      case 2211:
        path += "the_thing.png";
        break;
      case 2212:
        path += "gypsum_sword.png";
        break;
      case 2213:
        path += "gypsum_spear.png";
        break;
      case 2214:
        path += "board_with_nails.png";
        break;
      case 2221:
        path += "calcite_sword.png";
        break;
      case 2222:
        path += "calcite_spear.png";
        break;
      case 2223:
        path += "metal_pipe.png";
        break;
      case 2231:
        path += "fluorite_sword.png";
        break;
      case 2232:
        path += "fluorite_spear.png";
        break;
      case 2233:
        path += "iron_sword.png";
        break;
      case 2234:
        path += "iron_spear.png";
        break;
      case 2241:
        path += "apatite_sword.png";
        break;
      case 2242:
        path += "apatite_spear.png";
        break;
      case 2251:
        path += "orthoclase_sword.png";
        break;
      case 2252:
        path += "orthoclase_spear.png";
        break;
      case 2261:
        path += "quartz_sword.png";
        break;
      case 2262:
        path += "quartz_spear.png";
        break;
      case 2271:
        path += "topaz_sword.png";
        break;
      case 2272:
        path += "topaz_spear.png";
        break;
      case 2281:
        path += "corundum_sword.png";
        break;
      case 2282:
        path += "corundum_spear.png";
        break;
      case 2291:
        path += "diamond_sword.png";
        break;
      case 2292:
        path += "diamond_spear.png";
        break;
      // Ranged Weapons
      case 2301:
        if (this.ammo > 0) {
          path += "slingshot_loaded.png";
        }
        else {
          path += "slingshot_unloaded.png";
        }
        break;
      case 2311:
        if (this.ammo > 0) {
          path += "recurve_bow_loaded.png";
        }
        else {
          path += "recurve_bow_unloaded.png";
        }
        break;
      case 2312:
        path += "m1911.png";
        break;
      case 2321:
        path += "war_machine.png";
        break;
      case 2322:
        path += "five_seven.png";
        break;
      case 2323:
        path += "type25.png";
        break;
      case 2331:
        path += "mustang_and_sally.png";
        break;
      case 2332:
        path += "fal.png";
        break;
      case 2333:
        path += "python.png";
        break;
      case 2341:
        path += "rpg.png";
        break;
      case 2342:
        path += "dystopic_demolisher.png";
        break;
      case 2343:
        path += "ultra.png";
        break;
      case 2344:
        path += "strain25.png";
        break;
      case 2345:
        path += "executioner.png";
        break;
      case 2351:
        path += "galil.png";
        break;
      case 2352:
        path += "wn.png";
        break;
      case 2353:
        if (this.ammo > 0) {
          path += "ballistic_knife_loaded.png";
        }
        else {
          path += "ballistic_knife.png";
        }
        break;
      case 2354:
        path += "cobra.png";
        break;
      case 2355:
        path += "mtar.png";
        break;
      case 2361:
        path += "rpd.png";
        break;
      case 2362:
        path += "rocket_propelled_grievance.png";
        break;
      case 2363:
        path += "dsr-50.png";
        break;
      case 2364:
        path += "voice_of_justice.png";
        break;
      case 2371:
        path += "hamr.png";
        break;
      case 2372:
        path += "ray_gun.png";
        break;
      case 2373:
        path += "lamentation.png";
        break;
      case 2374:
        path += "the_krauss_refibrillator.png";
        break;
      case 2375:
        path += "malevolent_taxonomic_anodized_redeemer.png";
        break;
      case 2381:
        path += "relativistic_punishment_device.png";
        break;
      case 2382:
        path += "dead_specimen_reactor_5000.png";
        break;
      case 2391:
        path += "sldg_hamr.png";
        break;
      case 2392:
        path += "porters_x2_ray_gun.png";
        break;
      // Headgear
      case 2401:
        path += "talc_helmet.png";
        break;
      case 2402:
        path += "cap.png";
        break;
      case 2403:
        path += "bowl.png";
        break;
      case 2404:
        path += "pot.png";
        break;
      case 2411:
        path += "gypsum_helmet.png";
        break;
      case 2412:
        path += "leather_helmet.png";
        break;
      case 2421:
        path += "calcite_helmet.png";
        break;
      case 2431:
        path += "fluorite_helmet.png";
        break;
      case 2432:
        path += "iron_helmet.png";
        break;
      case 2441:
        path += "apatite_helmet.png";
        break;
      case 2451:
        path += "orthoclase_helmet.png";
        break;
      case 2461:
        path += "quartz_helmet.png";
        break;
      case 2471:
        path += "topaz_helmet.png";
        break;
      case 2481:
        path += "corundum_helmet.png";
        break;
      case 2491:
        path += "diamond_helmet.png";
        break;
      // Chestgear
      case 2501:
        path += "talc_chestplate.png";
        break;
      case 2502:
        path += "tshirt.png";
        break;
      case 2503:
        path += "bra.png";
        break;
      case 2504:
        path += "coat.png";
        break;
      case 2511:
        path += "gypsum_chestplate.png";
        break;
      case 2512:
        path += "bens_coat.png";
        break;
      case 2513:
        path += "suit_jacket.png";
        break;
      case 2514:
        path += "leather_shirt.png";
        break;
      case 2521:
        path += "calcite_chestplate.png";
        break;
      case 2531:
        path += "fluorite_chestplate.png";
        break;
      case 2532:
        path += "iron_chestplate.png";
        break;
      case 2541:
        path += "apatite_chestplate.png";
        break;
      case 2551:
        path += "orthoclase_chestplate.png";
        break;
      case 2561:
        path += "quartz_chestplate.png";
        break;
      case 2571:
        path += "topaz_chestplate.png";
        break;
      case 2581:
        path += "corundum_chestplate.png";
        break;
      case 2591:
        path += "diamond_chestplate.png";
        break;
      // Leggear
      case 2601:
        path += "talc_greaves.png";
        break;
      case 2602:
        path += "boxers.png";
        break;
      case 2603:
        path += "towel.png";
        break;
      case 2604:
        path += "pants.png";
        break;
      case 2611:
        path += "gypsum_greaves.png";
        break;
      case 2612:
        path += "leather_pants.png";
        break;
      case 2621:
        path += "calcite_greaves.png";
        break;
      case 2631:
        path += "fluorite_greaves.png";
        break;
      case 2632:
        path += "iron_greaves.png";
        break;
      case 2641:
        path += "apatite_greaves.png";
        break;
      case 2651:
        path += "orthoclase_greaves.png";
        break;
      case 2661:
        path += "quartz_greaves.png";
        break;
      case 2671:
        path += "topaz_greaves.png";
        break;
      case 2681:
        path += "corundum_greaves.png";
        break;
      case 2691:
        path += "diamond_greaves.png";
        break;
      // Footgear
      case 2701:
        path += "talc_boots.png";
        break;
      case 2702:
        path += "socks.png";
        break;
      case 2703:
        path += "sandals.png";
        break;
      case 2704:
        path += "shoes.png";
        break;
      case 2705:
        path += "boots.png";
        break;
      case 2711:
        path += "gypsum_boots.png";
        break;
      case 2712:
        path += "sneakers.png";
        break;
      case 2713:
        path += "steel-toed_boots.png";
        break;
      case 2714:
        path += "cowboy_boots.png";
        break;
      case 2715:
        path += "leather_boots.png";
        break;
      case 2721:
        path += "calcite_boots.png";
        break;
      case 2731:
        path += "fluorite_boots.png";
        break;
      case 2732:
        path += "iron_boots.png";
        break;
      case 2741:
        path += "apatite_boots.png";
        break;
      case 2751:
        path += "orthoclase_boots.png";
        break;
      case 2761:
        path += "quartz_boots.png";
        break;
      case 2771:
        path += "topaz_boots.png";
        break;
      case 2781:
        path += "corundum_boots.png";
        break;
      case 2791:
        path += "diamond_boots.png";
        break;
      // Materials
      case 2801:
        path += "talc_ore.png";
        break;
      case 2802:
        path += "talc_crystal.png";
        break;
      case 2803:
        path += "talc_powder.png";
        break;
      case 2804:
        path += "soapstone.png";
        break;
      case 2805:
        path += "broken_glass.png";
        break;
      case 2806:
        path += "wire.png";
        break;
      case 2807:
        path += "feather.png";
        break;
      case 2808:
        path += "ashes.png";
        break;
      case 2809:
        path += "string.png";
        break;
      case 2810:
        path += "wax.png";
        break;
      case 5801:
        path += "ivy.png";
        break;
      case 2811:
        path += "gypsum_ore.png";
        break;
      case 2812:
        path += "gypsum_crystal.png";
        break;
      case 2813:
        path += "gypsum_powder.png";
        break;
      case 2814:
        path += "selenite_crystal.png";
        break;
      case 2815:
        path += "barbed_wire.png";
        break;
      case 2816:
        path += "wooden_plank.png";
        break;
      case 2817:
        path += "wooden_handle.png";
        break;
      case 2818:
        path += "wooden_piece.png";
        break;
      case 2819:
        path += "cushion.png";
        break;
      case 2820:
        path += "rawhide.png";
        break;
      case 5811:
        path += "leather.png";
        break;
      case 2821:
        path += "calcite_ore.png";
        break;
      case 2822:
        path += "calcite_crystal.png";
        break;
      case 2823:
        path += "chalk.png";
        break;
      case 2824:
        path += "iceland_spar.png";
        break;
      case 2825:
        int frame = PApplet.constrain((int)(Math.floor(LNZ.item_starPieceFrames *
          (p.millis() % LNZ.item_starPieceAnimationTime) / LNZ.
          item_starPieceAnimationTime)), 0, LNZ.item_starPieceFrames - 1);
        path += "star_piece_" + frame + ".png";
        break;
      case 2826:
        path += "antlers.png";
        break;
      case 2831:
        path += "fluorite_ore.png";
        break;
      case 2832:
        path += "fluorite_crystal.png";
        break;
      case 2833:
        path += "iron_ore.png";
        break;
      case 2834:
        path += "iron_chunk.png";
        break;
      case 2841:
        path += "apatite_ore.png";
        break;
      case 2842:
        path += "apatite_crystal.png";
        break;
      case 2843:
        path += "iron_handle.png";
        break;
      case 2851:
        path += "orthoclase_ore.png";
        break;
      case 2852:
        path += "orthoclase_chunk.png";
        break;
      case 2861:
        path += "quartz_ore.png";
        break;
      case 2862:
        path += "quartz_crystal.png";
        break;
      case 2863:
        path += "amethyst.png";
        break;
      case 2864:
        path += "glass.png";
        break;
      case 2871:
        path += "topaz_ore.png";
        break;
      case 2872:
        path += "topaz_chunk.png";
        break;
      case 2873:
        path += "topaz_gem.png";
        break;
      case 2881:
        path += "corundum_ore.png";
        break;
      case 2882:
        path += "corundum_chunk.png";
        break;
      case 2883:
        path += "sapphire.png";
        break;
      case 2891:
        path += "diamond_ore.png";
        break;
      case 2892:
        path += "diamond.png";
        break;
      // Other
      case 2901:
        path += "key.png";
        break;
      case 2902:
        path += "master_key.png";
        break;
      case 2903:
        path += "skeleton_key.png";
        break;
      case 2904:
        path += "small_keyring.png";
        break;
      case 2905:
        path += "large_keyring.png";
        break;
      case 2906:
        path += "car_key.png";
        break;
      case 2911:
        path += "pen.png";
        break;
      case 2912:
        path += "pencil.png";
        break;
      case 2913:
        path += "paper.png";
        break;
      case 2914:
        path += "document.png";
        break;
      case 2915:
        path += "stapler.png";
        break;
      case 2916:
        path += "crumpled_paper.png";
        break;
      case 2917:
        path += "eraser.png";
        break;
      case 2918:
        path += "scissors.png";
        break;
      case 2921:
        path += "backpack.png";
        break;
      case 2922:
        path += "bens_backpack.png";
        break;
      case 2923:
        path += "purse.png";
        break;
      case 2924:
        double water_percent = (double)(this.ammo) / this.maximumAmmo();
        if (water_percent > 0.95) {
          path += "glass_bottle_water_full.png";
        }
        else if (water_percent > 0.8) {
          path += "glass_bottle_water_85.png";
        }
        else if (water_percent > 0.65) {
          path += "glass_bottle_water_70.png";
        }
        else if (water_percent > 0.5) {
          path += "glass_bottle_water_55.png";
        }
        else if (water_percent > 0.35) {
          path += "glass_bottle_water_40.png";
        }
        else if (water_percent > 0.2) {
          path += "glass_bottle_water_25.png";
        }
        else if (water_percent > 0) {
          path += "glass_bottle_water_10.png";
        }
        else {
          path += "glass_bottle.png";
        }
        break;
      case 2925:
        path += "water_bottle.png";
        break;
      case 2926:
        path += "canteen.png";
        break;
      case 2927:
        path += "water_jug.png";
        break;
      case 2928:
        if (this.toggled) {
          path += "cigar_lit.png";
        }
        else {
          path += "cigar.png";
        }
        break;
      case 2929:
        path += "gas_can.png";
        break;
      case 2930:
        path += "waterskin.png";
        break;
      case 5921:
        path += "large_waterskin.png";
        break;
      case 2931:
        path += "rock.png";
        break;
      case 2932:
        path += "arrow.png";
        break;
      case 2933:
        path += "pebble.png";
        break;
      case 2934:
        path += "hammerstone.png";
        break;
      case 2941:
        path += "45_acp.png";
        break;
      case 2942:
        path += "762_39mm.png";
        break;
      case 2943:
        path += "556_45mm.png";
        break;
      case 2944:
        path += "grenade.png";
        break;
      case 2945:
        path += "357_magnum.png";
        break;
      case 2946:
        path += "50_bmg.png";
        break;
      case 2947:
        path += "fn_57_28mm.png";
        break;
      case 2948:
        path += "28_gauge.png";
        break;
      case 2960:
        path += "branch_oak.png";
        break;
      case 2961:
        path += "dandelion.png";
        break;
      case 2962:
        path += "rose.png";
        break;
      case 2963:
        path += "stick.png";
        break;
      case 2964:
        path += "kindling.png";
        break;
      case 2965:
        path += "branch_maple.png";
        break;
      case 2966:
        path += "branch_walnut.png";
        break;
      case 2967:
        path += "branch_cedar.png";
        break;
      case 2968:
        path += "branch_pine.png";
        break;
      case 2969:
        path += "wooden_log.png";
        break;
      case 2970:
        path += "wooden_peg.png";
        break;
      case 2971:
        path += "paintbrush.png";
        break;
      case 2972:
        path += "clamp.png";
        break;
      case 2973:
        path += "wrench.png";
        break;
      case 2974:
        path += "rope.png";
        break;
      case 2975:
        path += "hammer.png";
        break;
      case 2976:
        path += "window_breaker.png";
        break;
      case 2977:
        path += "stone_hatchet.png";
        break;
      case 2978:
        path += "wire_clippers.png";
        break;
      case 2979:
        path += "saw.png";
        break;
      case 2980:
        path += "drill.png";
        break;
      case 2981:
        path += "roundsaw.png";
        break;
      case 2982:
        path += "beltsander.png";
        break;
      case 2983:
        path += "chainsaw.png";
        break;
      case 2984:
        path += "woodglue.png";
        break;
      case 2985:
        path += "nails.png";
        break;
      case 2986:
        path += "screws.png";
        break;
      case 2987:
        path += "flint_and_steel.png";
        break;
      case 2988:
        path += "lighter.png";
        break;
      case 2991:
        path += "rankins_third_ball.png";
        break;
      case 2992:
        path += "soldiers_covenant.png";
        break;
      case 2993:
        path += "jonah_plush_toy.png";
        break;
      case 2999:
        path += "bens_eyes.png";
        break;
      default:
        p.global.errorMessage("ERROR: Item ID " + ID + " not found.");
        path += "default.png";
        break;
    }
    return p.global.images.getImage(path);
  }


  boolean targetable(Unit u) {
    return true;
  }


  boolean equippable(GearSlot slot) {
    switch(slot) {
      case WEAPON:
        return true;
      case HEAD:
        if (this.type.equals("Headgear")) {
          return true;
        }
        return false;
      case CHEST:
        if (this.type.equals("Chestgear")) {
          return true;
        }
        return false;
      case LEGS:
        if (this.type.equals("Leggear")) {
          return true;
        }
        return false;
      case FEET:
        if (this.type.equals("Footgear")) {
          return true;
        }
        return false;
      case OFFHAND:
        if (this.type.equals("Offhand")) {
          return true;
        }
        return false;
      case BELT_LEFT:
        if (this.type.equals("Belt")) {
          return true;
        }
        return false;
      case BELT_RIGHT:
        if (this.type.equals("Belt")) {
          return true;
        }
        return false;
      default:
        return false;
    }
  }

  double speedWhenHolding() {
    if (this.weapon()) {
      return this.speed;
    }
    switch(this.ID) {
      case 2816: // woden plank
      case 2969: // wooden log
        return this.speed;
      default:
        return 0;
    }
  }

  boolean weapon() {
    if (this.type.contains("Weapon") || this.throwable()) {
      return true;
    }
    return false;
  }

  boolean armor() {
    switch(this.type) {
      case "Headgear":
      case "Chestgear":
      case "Leggear":
      case "Footgear":
        return true;
      default:
        return false;
    }
  }

  int maxStack() {
    if (this.toggled) {
      return 1;
    }
    if (this.ID > 2000 && this.ID < 2101) { // Farming
      return 60;
    }
    switch(this.ID) {
      case 2101: // crumb
      case 2102: // unknown food
      case 2103: // unknown food
      case 2104: // unknown food
      case 2105: // unknown food
      case 2106: // pickle
      case 2107: // ketchup
      case 2108: // chicken wing
      case 2109: // steak
      case 2110: // poptart
      case 2111: // donut
      case 2112: // chocolate
      case 2113: // chips
      case 2114: // cheese
      case 2115: // peanuts
      case 2116: // raw chicken
      case 2117: // cooked chicken
      case 2118: // chicken egg
      case 2119: // rotten flesh
      case 2120: // apple
      case 2121: // banana
      case 2122: // pear
      case 2123: // bread
      case 2125: // hot pocket
      case 2126: // raw chicken wing
      case 2127: // quail egg
      case 2128: // raw quail
      case 2129: // cooked quail
      case 2130: // Raw Venison
      case 5101: // Cooked Venison
        return 8;
      case 2131: // water cup
      case 2132: // coke
      case 2133: // wine
      case 2134: // beer
      case 2141: // holy water
      case 2142: // golden apple
        return 4;
      case 2151: // one dollar
      case 2152: // five dollars
      case 2153: // ten dollars
      case 2154: // fifty dollars
      case 2155: // zucc bucc
      case 2156: // wad of 5s
      case 2157: // wad of 10s
      case 2158: // wad of 50s
      case 2159: // wad of zuccs
        return 100;
      case 2167: // Workbench
      case 2168: // Bed
      case 2169: // Wooden Box
      case 2170: // Wooden Crate
      case 2171: // Wooden Chest
      case 2172: // Large Wooden
        return 2;
      case 2801: // talc ore
      case 2802: // talc crystal
      case 2803: // talc powder
      case 2804: // soapstone
      case 2805: // broken glass
      case 2806: // wire
      case 2807: // feather
      case 2808: // ashes
      case 2809: // string
      case 5801: // ivy
      case 2811: // gypsum ore
      case 2812: // gypsum crystal
      case 2813: // gypsum powder
      case 2814: // selenite crystal
      case 2815: // barbed wire
      case 2820: // Rawhide
      case 5811: // Leather
      case 2821: // calcite ore
      case 2822: // calcite crystal
      case 2823: // chalk
      case 2824: // iceland spar
      case 2825: // star piece
      case 2831: // fluorite ore
      case 2832: // fluorite crystal
      case 2833: // iron ore
      case 2834: // iron chunk
      case 2841: // apatite ore
      case 2842: // apatite crystal
      case 2851: // orthoclase ore
      case 2852: // orthoclase chunk
      case 2853: // moonstone
      case 2861: // quartz ore
      case 2862: // quartz crystal
      case 2863: // amethyst
      case 2864: // glass
      case 2871: // topaz ore
      case 2872: // topaz chunk
      case 2873: // topaz gem
      case 2881: // corundum ore
      case 2882: // corundum chunk
      case 2883: // sapphire
      case 2891: // diamond ore
      case 2892: // diamond
      case 2970: // Wooden Peg
        return 60;
      case 2816: // wooden plank
      case 2819: // cushion
        return 4;
      case 2817: // wooden handle
      case 2818: // wooden piece
      case 2843: // iron handle
        return 16;
      case 2911: // pen
      case 2912: // pencil
      case 2913: // paper
      case 2914: // document
      case 2916: // crumpled paper
      case 2917: // eraser
        return 12;
      case 2931: // rock
      case 2932: // arrow
      case 2933: // pebble
        return 20;
      case 2941: // .45 ACP
      case 2942: // 7.62
      case 2943: // 5.56
      case 2945: // .357 magnum
      case 2946: // .50 BMG
      case 2947: // FN 4.7
      case 2948: // 28 gauge
        return 100;
      case 2944: // grenade
        return 4;
      case 2961: // dandelion
      case 2962: // rose
      case 2963: // stick
      case 2964: // kindling
      case 2965: // branch (maple)
      case 2966: // branch (walnut)
      case 2967: // branch (cedar)
      case 2968: // branch (pine)
        return 12;
      default:
        return 1;
    }
  }

  void setStack(int amount) {
    this.stack = amount;
    if (this.stack <= 0) {
      this.remove = true;
    }
    if (this.stack > this.maxStack()) {
      p.global.errorMessage("ERROR: Stack of " + this.displayName() + " too big.");
    }
  }

  void addStack() {
    this.addStack(1);
  }
  void addStack(int amount) {
    this.stack += amount;
    if (this.stack <= 0) {
      this.remove = true;
    }
    if (this.stack > this.maxStack()) {
      p.global.errorMessage("ERROR: Stack of " + this.displayName() + " too big.");
    }
  }

  void removeStack() {
    this.removeStack(1);
  }
  void removeStack(int amount) {
    this.stack -= amount;
    if (this.stack <= 0) {
      this.remove = true;
    }
  }

  boolean usable() {
    return this.consumable() || this.reloadable() || this.money() || this.utility();
  }

  boolean waterBottle() {
    switch(this.ID) {
      case 2924: // Glass Bottle
      case 2925: // Water Bottle
      case 2926: // Canteen
      case 2927: // Water Jug
      case 2930: // Waterskin
      case 5921: // Large Waterskin
        return true;
      default:
        return false;
    }
  }

  boolean shovel() {
    switch(this.ID) {
      case 2209: // Wooden Shovel
      case 2210: // Talc Shovel
        return true;
      default:
        return false;
    }
  }

  boolean hoe() {
    switch(this.ID) {
      case 5201: // Wooden Hoe
      case 5202: // Talc Hoe
        return true;
      default:
        return false;
    }
  }

  boolean ax() {
    switch(this.ID) {
      case 2977: // Stone Hatchet
      case 2979: // Saw
      case 2981: // Roundsaw
      case 2983: // Chainsaw
        return true;
      default:
        return false;
    }
  }

  boolean placeable() {
    switch(this.ID) {
      case 2167: // Workbench
      case 2168: // Bed
      case 2169: // Wooden Box
      case 2170: // Wooden Crate
      case 2171: // Wooden Chest
      case 2172: // Large Wooden Chest
        return true;
      default:
        return false;
    }
  }

  int placeableFeatureId(Unit u) {
    switch(this.ID) {
      case 2167: // Workbench
        return 21;
      case 2168: // Bed
        if (u.facingA > 0.25 * Math.PI && u.facingA < 0.75 * Math.PI) {
          return 132;
        }
        else if (u.facingA > -0.25 * Math.PI && u.facingA < 0.25 * Math.PI) {
          return 134;
        }
        else if (u.facingA > -0.75 * Math.PI && u.facingA < -0.25 * Math.PI) {
          return 131;
        }
        else {
          return 133;
        }
      case 2169: // Wooden Box
        return 23;
      case 2170: // Wooden Crate
        return 24;
      case 2171: // Wooden Chest
        return 25;
      case 2172: // Large Wooden Chest
        return 26;
      default:
        p.global.errorMessage("ERROR: Placeable feature ID not found for " + this.displayName() + ".");
        return 0;
    }
  }

  boolean utility() {
    return this.type.equals("Utility");
  }

  boolean key() {
    return this.type.equals("Key");
  }

  boolean money() {
    return this.type.equals("Money");
  }

  boolean plantable() { // plantable on tilled dirt
    if (this.type.equals("Seed")) {
      return true;
    }
    switch(this.ID) {
      case 2002: // Wapato
      case 2960: // Branch, oak
      case 2965: // Branch, maple
      case 2966: // Branch, walnut
      case 2967: // Branch, cedar
      case 2968: // Branch, pine
        return true;
      default:
        return false;
    }
  }

  boolean sapling() {
    switch(this.ID) {
      case 2960: // Branch, oak
      case 2965: // Branch, maple
      case 2966: // Branch, walnut
      case 2967: // Branch, cedar
      case 2968: // Branch, pine
        return true;
      default:
        return false;
    }
  }

  boolean plantableAnwhere() {
    switch(this.ID) {
      case 2011: // Maple Seed
      case 2012: // Black Walnut
      case 2013: // Juniper Berries
      case 2014: // Acorn
      case 2015: // Pine Cone
      case 2021: // Dandelion Seeds
        return true;
      default:
        return false;
    }
  }

  int plantFeatureId() { // returns ID of plant feature the item plants
    if (!this.plantable()) {
      p.global.errorMessage("ERROR: Trying to retrieve plantFeatureId of item " +
        "with ID " + this.ID + " that is not plantable.");
      return 0;
    }
    switch(this.ID) {
      case 2001: // Wapato Seed
      case 2002: // Wapato
        return 451;
      case 2003: // Leek Seeds
        return 452;
      case 2005: // Plantain Seeds
        return 455;
      case 2007: // Ryegrass Seeds
        return 453;
      case 2008: // Barnyard Grass Seeds
        return 454;
      case 2009: // Nettle Seeds
        return 456;
      case 2011: // Maple Seed
        return 462;
      case 2012: // Black Walnut
        return 463;
      case 2013: // Juniper Berries
        return 464;
      case 2014: // Acorn
        return 465;
      case 2015: // Pine Cone
        return 466;
      case 2021: // Dandelion
        return 401;
      case 2960: // Branch, oak
        return 460;
      case 2965: // Branch, maple
        return 457;
      case 2966: // Branch, walnut
        return 458;
      case 2967: // Branch, cedar
        return 459;
      case 2968: // Branch, pine
        return 461;
      default:
        p.global.errorMessage("ERROR: Plantable item with ID " + this.ID + " has " +
          "no plantFeatureId set.");
        return 0;
    }
  }

  boolean consumable() {
    if (this.type.equals("Food") || this.type.equals("Drink")) {
      return true;
    }
    switch(this.ID) {
      case 2007: // Ryegrass Seeds
      case 2008: // Barnyard Grass Seeds
      case 2012: // Black Walnut
      case 2013: // Juniper Berries
      case 2014: // Acorn
      case 2961: // Dandelion
        return true;
      default:
        return false;
    }
  }

  void consumed() {
    this.removeStack();
  }

  boolean unlocks(int lock_code) {
    switch(this.ID) {
      case 2901: // key
        return this.ammo == lock_code;
      case 2902: // master key
        return this.ammo == lock_code / 10;
      case 2903: // skeleton key
        return this.ammo == lock_code / 100;
      case 2904: // small keyring
      case 2905: // large keyring
        for (Item i : this.inventory.items()) {
          if (i == null || i.remove) {
            continue;
          }
          if (i.unlocks(lock_code)) {
            return true;
          }
        }
        return false;
      case 2906: // car key
        switch(lock_code) {
          case 501: // Honda CRV
            return this.ammo == 1;
          case 502: // Ford F150
            return this.ammo == 2;
          case 503: // VS Jetta
            return this.ammo == 3;
          case 504: // VS Beetle
            return this.ammo == 4;
          case 505: // Lamborghini
            return this.ammo == 5;
          default:
            return false;
        }
      default:
        return false;
    }
  }

  boolean reloadable() {
    if (this.type.equals("Ranged Weapon") && this.availableAmmo() < this.maximumAmmo()) {
      return true;
    }
    return false;
  }

  ArrayList<Integer> possibleAmmo() {
    ArrayList<Integer> possible_ammo = new ArrayList<Integer>();
    switch(this.ID) {
      case 2301: // Slingshot
        possible_ammo.add(2931);
        possible_ammo.add(2933);
        break;
      case 2311: // Recurve Bow
        possible_ammo.add(2932);
        break;
      case 2312: // M1911
        possible_ammo.add(2941);
        break;
      case 2321: // War Machine
        possible_ammo.add(2944);
        break;
      case 2322: // Five-Seven
        possible_ammo.add(2947);
        break;
      case 2323: // Type25
        possible_ammo.add(2943);
        break;
      case 2331: // Mustang and Sally
        possible_ammo.add(2944);
        break;
      case 2332: // FAL
        possible_ammo.add(2942);
        break;
      case 2333: // Python
        possible_ammo.add(2945);
        break;
      case 2341: // RPG
        possible_ammo.add(2944);
        break;
      case 2342: // Dystopic Demolisher
        possible_ammo.add(2944);
        break;
      case 2343: // Ultra
        possible_ammo.add(2947);
        break;
      case 2344: // Strain25
        possible_ammo.add(2943);
        break;
      case 2345: // Executioner
        possible_ammo.add(2948);
        break;
      case 2351: // Galil
        possible_ammo.add(2943);
        break;
      case 2352: // WN
        possible_ammo.add(2942);
        break;
      case 2353: // Ballistic Knife
        possible_ammo.add(2203);
        break;
      case 2354: // Cobra
        possible_ammo.add(2945);
        break;
      case 2355: // MTAR
        possible_ammo.add(2943);
        break;
      case 2361: // RPD
        possible_ammo.add(2942);
        break;
      case 2362: // Rocket-Propelled Grievance
        possible_ammo.add(2944);
        break;
      case 2363: // DSR-50
        possible_ammo.add(2946);
        break;
      case 2364: // Voice of Justice
        possible_ammo.add(2948);
        break;
      case 2371: // HAMR
        possible_ammo.add(2942);
        break;
      case 2372: // Ray Gun
        break;
      case 2373: // Lamentation
        possible_ammo.add(2942);
        break;
      case 2374: // The Krauss Refibrillator
        possible_ammo.add(2203);
        break;
      case 2375: // Malevolent Taxonomic Anodized Redeemer
        possible_ammo.add(2943);
        break;
      case 2381: // Relativistic Punishment Device
        possible_ammo.add(2942);
        break;
      case 2382: // Dead Specimen Reactor 5000
        possible_ammo.add(2946);
        break;
      case 2391: // SLDG HAMR
        possible_ammo.add(2942);
        break;
      case 2392: // Porter's X2 Ray Gun
        break;
      default:
        break;
    }
    return possible_ammo;
  }

  boolean shootable() {
    if (this.remove) {
      return false;
    }
    if (this.throwable()) {
      return true;
    }
    else if (this.type.equals("Ranged Weapon") && this.availableAmmo() > 0 && !this.toggled) {
      return true;
    }
    return false;
  }

  boolean throwable() {
    if (this.remove) {
      return false;
    }
    switch(this.ID) {
      case 2118: // chicken egg
      case 2924: // glass bottle
      case 2931: // rock
      case 2932: // arrow
      case 2933: // pebble
      case 2944: // grenade
        return true;
      default:
        return false;
    }
  }

  boolean meleeAttackable() {
    if (!this.shootable()) {
      return true;
    }
    if (this.throwable()) {
      return true;
    }
    switch(this.ID) {
      case 2353: // Ballistic Knife
      case 2374: // The Krauss Refibrillator
        return true;
      default:
        return false;
    }
  }

  void shot() {
    this.lowerDurability();
    if (this.throwable()) {
      this.removeStack();
    }
    else {
      this.ammo--;
    }
  }

  void attacked() {
    this.lowerDurability();
  }


  boolean automatic() {
    switch(this.ID) {
      case 2323: // Type25
      case 2344: // Strain25
      case 2351: // Galil
      case 2355: // MTAR
      case 2361: // RPD
      case 2362: // Rocket-Propelled Grievance
      case 2371: // HAMR
      case 2372: // Ray Gun
      case 2373: // Lamentation
      case 2375: // MAlevolent Taxonomic Anodized Redeemer
      case 2381: // Relativistic Punishment Device
      case 2391: // SLDG HAMR
      case 2392: // Porter's X2 Ray Gun
        return true;
      default:
        return false;
    }
  }


  double shootAttack() {
    switch(this.ID) {
      case 2118: // Chicken Egg (thrown)
        return 2;
      case 2301: // Slingshot
        return 5;
      case 2311: // Recurve Bow
        return 8;
      case 2312: // M1911
        return 20;
      case 2321: // War Machine
        return 8;
      case 2322: // Five-Seven
        return 160;
      case 2323: // Type25
        return 110;
      case 2331: // Mustang and Sally
        return 1000;
      case 2332: // FAL
        return 160;
      case 2333: // Python
        return 1000;
      case 2341: // RPG
        return 600;
      case 2342: // Dystopic Demolisher
        return 600;
      case 2343: // Ultra
        return 300;
      case 2344: // Strain25
        return 160;
      case 2345: // Executioner
        return 1040;
      case 2351: // Galil
        return 150;
      case 2352: // WN
        return 240;
      case 2353: // Ballistic Knife
        return 500;
      case 2354: // Cobra
        return 1000;
      case 2355: // MTAR
        return 140;
      case 2361: // RPD
        return 140;
      case 2362: // Rocket-Propelled Grievance
        return 1200;
      case 2363: // DSR-50
        return 800;
      case 2364: // Voice of Justice
        return 4200;
      case 2371: // HAMR
        return 190;
      case 2372: // Ray Gun
        return 500;
      case 2373: // Lamentation
        return 220;
      case 2374: // The Krauss Refibrillator
        return 1000;
      case 2375: // Malevolent Taxonomic Anodized Redeemer
        return 210;
      case 2381: // Relativistic Punishment Device
        return 180;
      case 2382: // Dead Specimen Reactor 5000
        return 1000;
      case 2391: // SLDG HAMR
        return 250;
      case 2392: // Porter's X2 Ray Gun
        return 600;
      case 2924: // Glass Bottle (thrown)
        return 1;
      case 2931: // Rock (thrown)
        return 2;
      case 2932: // Arrow (thrown)
        return 2;
      case 2933: // Pebble (thrown)
        return 1;
      case 2944: // Grenade (thrown)
        return 3;
      default:
        return 0;
    }
  }

  double shootMagic() {
    switch(this.ID) {
      case 2372: // Ray Gun
        return 500;
      case 2392: // Porter's X2 Ray Gun
        return 600;
      default:
        return 0;
    }
  }

  double shootPiercing() {
    switch(this.ID) {
      case 2311: // Recurve Bow
        return 0.15;
      case 2312: // M1911
        return 0.12;
      case 2321: // War Machine
        return 0.05;
      case 2322: // Five-Seven
        return 0.1;
      case 2323: // Type25
        return 0.18;
      case 2331: // Mustang and Sally
        return 0.05;
      case 2332: // FAL
        return 0.15;
      case 2333: // Python
        return 0.15;
      case 2341: // RPG
        return 0.06;
      case 2342: // Dystopic Demolisher
        return 0.15;
      case 2343: // Ultra
        return 0.15;
      case 2344: // Strain25
        return 0.24;
      case 2345: // Executioner
        return 0.1;
      case 2351: // Galil
        return 0.16;
      case 2352: // WN
        return 0.2;
      case 2353: // Ballistic Knife
        return 0.25;
      case 2354: // Cobra
        return 0.15;
      case 2355: // MTAR
        return 0.16;
      case 2361: // RPD
        return 0.15;
      case 2362: // Rocket-Propelled Grievance
        return 0.08;
      case 2363: // DSR-50
        return 0.3;
      case 2364: // Voice of Justice
        return 0.12;
      case 2371: // HAMR
        return 0.2;
      case 2372: // Ray Gun
        return 0;
      case 2373: // Lamentation
        return 0.22;
      case 2374: // The Krauss Refibrillator
        return 0.35;
      case 2375: // Malevolent Taxonomic Anodized Redeemer
        return 0.2;
      case 2381: // Relativistic Punishment Device
        return 0.2;
      case 2382: // Dead Specimen Reactor 5000
        return 0.45;
      case 2391: // SLDG HAMR
        return 0.25;
      case 2392: // Porter's X2 Ray Gun
        return 0;
      case 2924: // Glass Bottle (thrown)
        return 0.06;
      case 2932: // Arrow (thrown)
        return 0.06;
      default:
        return 0;
    }
  }

  double shootPenetration() {
    switch(this.ID) {
      case 2372: // Ray Gun
        return 0.08;
      case 2392: // Porter's X2 Ray Gun
        return 0.12;
      default:
        return 0;
    }
  }

  double shootRange() {
    switch(this.ID) {
      case 2118: // Chicken Egg (thrown)
        return 3;
      case 2301: // Slingshot
        return 5.5;
      case 2311: // Recurve Bow
        return 6.5;
      case 2312: // M1911
        return 6;
      case 2321: // War Machine
        return 10;
      case 2322: // Five-Seven
        return 7;
      case 2323: // Type25
        return 9;
      case 2331: // Mustang and Sally
        return 12;
      case 2332: // FAL
        return 10;
      case 2333: // Python
        return 7;
      case 2341: // RPG
        return 10;
      case 2342: // Dystopic Demolisher
        return 12;
      case 2343: // Ultra
        return 8;
      case 2344: // Strain25
        return 10;
      case 2345: // Executioner
        return 4;
      case 2351: // Galil
        return 10;
      case 2352: // WN
        return 11;
      case 2353: // Ballistic Knife
        return 7;
      case 2354: // Cobra
        return 7;
      case 2355: // MTAR
        return 11;
      case 2361: // RPD
        return 11;
      case 2362: // Rocket-Propelled Grievance
        return 12;
      case 2363: // DSR-50
        return 16;
      case 2364: // Voice of Justice
        return 5;
      case 2371: // HAMR
        return 10;
      case 2372: // Ray Gun
        return 9;
      case 2373: // Lamentation
        return 11;
      case 2374: // The Krauss Refibrillator
        return 8;
      case 2375: // Malevolent Taxonomic Anodized Redeemer
        return 12;
      case 2381: // Relativistic Punishment Device
        return 12;
      case 2382: // Dead Specimen Reactor 5000
        return 18;
      case 2391: // SLDG HAMR
        return 11;
      case 2392: // Porter's X2 Ray Gun
        return 9;
      case 2924: // Glass Bottle (thrown)
        return 4;
      case 2931: // Rock (thrown)
        return 4.5;
      case 2932: // Arrow (thrown)
        return 3;
      case 2933: // Pebble (thrown)
        return 3.5;
      case 2944: // Grenade (thrown)
        return 5.5;
      default:
        return 0;
    }
  }

  double shootCooldown() {
    double ammo_ratio = 0;
    switch(this.ID) {
      case 2301: // Slingshot
        return 1300;
      case 2311: // Recurve Bow
        return 1500;
      case 2312: // M1911
        return 96;
      case 2321: // War Machine
        return 250;
      case 2322: // Five-Seven
        return 80;
      case 2323: // Type25
        return 64;
      case 2331: // Mustang and Sally
        return 200;
      case 2332: // FAL
        return 112;
      case 2333: // Python
        return 96;
      case 2341: // RPG
        return 320;
      case 2342: // Dystopic Demolisher
        return 250;
      case 2343: // Ultra
        return 80;
      case 2344: // Strain25
        return 64;
      case 2345: // Executioner
        return 128;
      case 2351: // Galil
        return 80;
      case 2352: // WN
        return 112;
      case 2353: // Ballistic Knife
        return 200;
      case 2354: // Cobra
        return 96;
      case 2355: // MTAR
        return 80;
      case 2361: // RPD
        return 80;
      case 2362: // Rocket-Propelled Grievance
        return 320;
      case 2363: // DSR-50
        return 1200;
      case 2364: // Voice of Justice
        return 128;
      case 2371: // HAMR
        ammo_ratio = (float)(this.ammo) / this.maximumAmmo();
        return 200 - 120 * ammo_ratio;
      case 2372: // Ray Gun
        return 331;
      case 2373: // Lamentation
        return 80;
      case 2374: // The Krauss Refibrillator
        return 200;
      case 2375: // Malevolent Taxonomic Anodized Redeemer
        return 80;
      case 2381: // Relativistic Punishment Device
        return 80;
      case 2382: // Dead Specimen Reactor 5000
        return 1200;
      case 2391: // SLDG HAMR
        ammo_ratio = (float)(this.ammo) / this.maximumAmmo();
        return 200 - 120 * ammo_ratio;
      case 2392: // Porter's X2 Ray Gun
        return 331;
      default:
        return 300;
    }
  }

  double shootTime() {
    double ammo_ratio = 0;
    switch(this.ID) {
      case 2301: // Slingshot
        return 350;
      case 2311: // Recurve Bow
        return 300;
      case 2312: // M1911
        return 10;
      case 2321: // War Machine
        return 25;
      case 2322: // Five-Seven
        return 8;
      case 2323: // Type25
        return 6;
      case 2331: // Mustang and Sally
        return 20;
      case 2332: // FAL
        return 11;
      case 2333: // Python
        return 10;
      case 2341: // RPG
        return 32;
      case 2342: // Dystopic Demolisher
        return 25;
      case 2343: // Ultra
        return 8;
      case 2344: // Strain25
        return 6;
      case 2345: // Executioner
        return 13;
      case 2351: // Galil
        return 8;
      case 2352: // WN
        return 11;
      case 2353: // Ballistic Knife
        return 5;
      case 2354: // Cobra
        return 10;
      case 2355: // MTAR
        return 8;
      case 2361: // RPD
        return 8;
      case 2362: // Rocket-Propelled Grievance
        return 32;
      case 2363: // DSR-50
        return 120;
      case 2364: // Voice of Justice
        return 13;
      case 2371: // HAMR
        ammo_ratio = (float)(this.ammo) / this.maximumAmmo();
        return 20 - 12 * ammo_ratio;
      case 2372: // Ray Gun
        return 33;
      case 2373: // Lamentation
        return 8;
      case 2374: // The Krauss Refibrillator
        return 5;
      case 2375: // Malevolent Taxonomic Anodized Redeemer
        return 8;
      case 2381: // Relativistic Punishment Device
        return 8;
      case 2382: // Dead Specimen Reactor 5000
        return 120;
      case 2391: // SLDG HAMR
        ammo_ratio = (float)(this.ammo) / this.maximumAmmo();
        return 20 - 12 * ammo_ratio;
      case 2392: // Porter's X2 Ray Gun
        return 33;
      default:
        return 60;
    }
  }

  double shootRecoil() {
    switch(this.ID) {
      case 2312: // M1911
        return 0.005;
      case 2321: // War Machine
        return 0.1;
      case 2322: // Five-Seven
        return 0.002;
      case 2323: // Type25
        return 0.02;
      case 2331: // Mustang and Sally
        return 0.08;
      case 2332: // FAL
        return 0.02;
      case 2333: // Python
        return 0.015;
      case 2341: // RPG
        return 0.12;
      case 2342: // Dystopic Demolisher
        return 0.1;
      case 2343: // Ultra
        return 0.002;
      case 2344: // Strain25
        return 0.015;
      case 2345: // Executioner
        return 0.02;
      case 2351: // Galil
        return 0.01;
      case 2352: // WN
        return 0.02;
      case 2353: // Ballistic Knife
        return 0;
      case 2354: // Cobra
        return 0.015;
      case 2355: // MTAR
        return 0.015;
      case 2361: // RPD
        return 0.02;
      case 2362: // Rocket-Propelled Grievance
        return 0.12;
      case 2363: // DSR-50
        return 0.25;
      case 2364: // Voice of Justice
        return 0.02;
      case 2371: // HAMR
        return 0.02;
      case 2372: // Ray Gun
        return 0;
      case 2373: // Lamentation
        return 0.01;
      case 2374: // The Krauss Refibrillator
        return 0;
      case 2375: // Malevolent Taxonomic Anodized Redeemer
        return 0.015;
      case 2381: // Relativistic Punishment Device
        return 0.02;
      case 2382: // Dead Specimen Reactor 5000
        return 0.2;
      case 2391: // SLDG HAMR
        return 0.02;
      case 2392: // Porter's X2 Ray Gun
        return 0;
      default:
        return 0;
    }
  }

  double shootInaccuracy() {
    double ammo_ratio = 0;
    switch(this.ID) {
      case 2118: // Chicken Egg (thrown)
        return 0.1;
      case 2301: // Slingshot
        return 0.12;
      case 2311: // Recurve Bow
        return 0.12;
      case 2312: // M1911
        return 0.12;
      case 2321: // War Machine
        return 0.05;
      case 2322: // Five-Seven
        return 0.08;
      case 2323: // Type25
        return 0.15;
      case 2331: // Mustang and Sally
        return 0.08;
      case 2332: // FAL
        return 0.1;
      case 2333: // Python
        return 0.08;
      case 2341: // RPG
        return 0.08;
      case 2342: // Dystopic Demolisher
        return 0.05;
      case 2343: // Ultra
        return 0.06;
      case 2344: // Strain25
        return 0.1;
      case 2345: // Executioner
        return 0.05;
      case 2351: // Galil
        return 0.06;
      case 2352: // WN
        return 0.08;
      case 2353: // Ballistic Knife
        return 0.05;
      case 2354: // Cobra
        return 0.07;
      case 2355: // MTAR
        return 0.08;
      case 2361: // RPD
        return 0.1;
      case 2362: // Rocket-Propelled Grievance
        return 0.08;
      case 2363: // DSR-50
        return 0.04;
      case 2364: // Voice of Justice
        return 0.05;
      case 2371: // HAMR
        ammo_ratio = (float)(this.ammo) / this.maximumAmmo();
        return 0.05 + 0.1 * ammo_ratio;
      case 2372: // Ray Gun
        return 0.05;
      case 2373: // Lamentation
        return 0.06;
      case 2374: // The Krauss Refibrillator
        return 0.03;
      case 2375: // Malevolent Taxonomic Anodized Redeemer
        return 0.08;
      case 2381: // Relativistic Punishment Device
        return 0.08;
      case 2382: // Dead Specimen Reactor 5000
        return 0.02;
      case 2391: // SLDG HAMR
        ammo_ratio = (float)(this.ammo) / this.maximumAmmo();
        return 0.05 + 0.1 * ammo_ratio;
      case 2392: // Porter's X2 Ray Gun
        return 0.05;
      case 2924: // Glass Bottle
        return 0.15;
      case 2931: // Rock (throw)
        return 0.1;
      case 2932: // Arrow (throw)
        return 0.15;
      case 2933: // Pebble (throw)
        return 0.1;
      case 2944: // Grenade (throw)
        return 0.1;
      default:
        return 0;
    }
  }


  void lowerDurability() {
    this.lowerDurability(1, false);
  }
  void lowerDurability(int amount) {
    this.lowerDurability(amount, false);
  }
  void lowerDurability(int amount, boolean force) {
    if (!force && !this.breakable()) {
      return;
    }
    this.durability -= amount;
    if (this.durability < 1) {
      switch(this.ID) {
        case 2312: // M1911
        case 2321: // War Machine
        case 2322: // Five-Seven
        case 2323: // Type25
        case 2331: // Mustang and Sally
        case 2332: // FAL
        case 2333: // Python
        case 2341: // RPG
        case 2342: // Dystopic Demolisher
        case 2343: // Ultra
        case 2344: // Strain25
        case 2345: // Executioner
        case 2351: // Galil
        case 2352: // WN
        case 2353: // Ballistic Knife
        case 2354: // Cobra
        case 2355: // MTAR
        case 2361: // RPD
        case 2362: // Rocket-Propelled Grievance
        case 2363: // DSR-50
        case 2364: // Voice of Justice
        case 2371: // HAMR
        case 2372: // Ray Gun
        case 2373: // Lamentation
        case 2374: // The Krauss Refibrillator
        case 2375: // Malevolent Taxonomic Anodized Redeemer
        case 2381: // Relativistic Punishment Device
        case 2382: // Dead Specimen Reactor 5000
        case 2391: // SLDG HAMR
        case 2392: // Porter's X2 Ray Gun
          //this.toggled = true; // needs cleaned
          break;
        case 2809: // String
        case 2970: // Wooden Peg
          this.durability = 1;
          this.removeStack();
          break;
        default:
          this.remove = true;
          break;
      }
    }
  }
  boolean breakable() { // if item uses durability
    if (this.ID > 2200 && this.ID < 2801) {
      return true;
    }
    if (this.ID > 5200 && this.ID < 5801) {
      return true;
    }
    switch(this.ID) {
      case 2911: // Pen
      case 2912: // Pencil
      case 2915: // Stapler
      case 2918: // Scissors
      case 2924: // Glass bottle
      case 2931: // Rock
      case 2932: // Arrow
      case 2933: // Pebble
      case 2934: // Hammerstone
      case 2963: // stick
      case 2965: // branch
      case 2966: // branch
      case 2967: // branch
      case 2968: // branch
      case 2969: // wooden log
      case 2970: // Wooden Peg
      case 2971: // paintbrush
      case 2972: // clamp
      case 2973: // wrench
      case 2974: // rope
      case 2975: // hammer
      case 2976: // window breaker
      case 2977: // stone hatchet
      case 2978: // wire clippers
      case 2979: // saw
      case 2980: // drill
      case 2981: // roundsaw
      case 2982: // beltsander
      case 2983: // chainsaw
      case 2984: // woodglue
      case 2985: // Nails
      case 2986: // Screws
        return true;
      default:
        return false;
    }
  }


  void changeAmmo(int amount) {
    this.ammo += amount;
    if (this.ammo < 0) {
      this.ammo = 0;
    }
    if (this.ammo > this.maximumAmmo()) {
      this.ammo = this.maximumAmmo();
    }
  }
  int availableAmmo() {
    return this.ammo;
  }
  int maximumAmmo() {
    switch(this.ID) {
      case 2301: // Slingshot
        return 1;
      case 2311: // Recurve Bow
        return 1;
      case 2312: // M1911
        return 8;
      case 2321: // War Machine
        return 6;
      case 2322: // Five-Seven
        return 20;
      case 2323: // Type25
        return 30;
      case 2331: // Mustang and Sally
        return 6;
      case 2332: // FAL
        return 20;
      case 2333: // Python
        return 6;
      case 2341: // RPG
        return 1;
      case 2342: // Dystopic Demolisher
        return 6;
      case 2343: // Ultra
        return 20;
      case 2344: // Strain25
        return 30;
      case 2345: // Executioner
        return 5;
      case 2351: // Galil
        return 35;
      case 2352: // WN
        return 30;
      case 2353: // Ballistic Knife
        return 1;
      case 2354: // Cobra
        return 12;
      case 2355: // MTAR
        return 30;
      case 2361: // RPD
        return 100;
      case 2362: // Rocket-Propelled Grievance
        return 8;
      case 2363: // DSR-50
        return 4;
      case 2364: // Voice of Justice
        return 5;
      case 2371: // HAMR
        return 125;
      case 2372: // Ray Gun
        return 20;
      case 2373: // Lamentation
        return 35;
      case 2374: // The Krauss Refibrillator
        return 1;
      case 2375: // Malevolent Taxonomic Anodized Redeemer
        return 30;
      case 2381: // Relativistic Punishment Device
        return 125;
      case 2382: // Dead Specimen Reactor 5000
        return 8;
      case 2391: // SLDG HAMR
        return 125;
      case 2392: // Porter's X2 Ray Gun
        return 40;
      case 2924: // Glass Bottle
        return 30;
      case 2925: // Water Bottle
        return 100;
      case 2926: // Canteen
        return 250;
      case 2927: // Water Jug
        return 600;
      case 2929: // Gas Can
        return 50;
      case 2930: // Waterskin
        return 120;
      case 5921: // Large Waterskin
        return 300;
      default:
        return 0;
    }
  }

  double useTime() {
    if (this.consumable()) {
      return 1300;
    }
    switch(this.ID) {
      case 2131: // water cup
      case 2132: // coke
      case 2133: // wine
      case 2134: // beer
      case 2141: // holy water
      case 2924: // Glass Bottle
      case 2925: // Water Bottle
      case 2926: // Canteen
      case 2927: // Water Jug
      case 2930: // Waterskin
      case 5921: // Large Waterskin
        return 1650;
      case 2301: // Slingshot
        return 800;
      case 2311: // Recurve Bow
        return 1100;
      case 2312: // M1911
        return 1000;
      case 2321: // War Machine
        return 6400;
      case 2322: // Five-Seven
      case 2343: // Ultra
        return 1100;
      case 2323: // Type25
      case 2344: // Strain25
        return 860;
      case 2331: // Mustang and Sally
        return 1850;
      case 2332: // FAL
      case 2352: // WN
        return 1300;
      case 2333: // Python
      case 2354: // Cobra
        return 2650;
      case 2341: // RPG
      case 2362: // Rocket-Propelled Grievance
        return 550;
      case 2342: // Dystopic Demolisher
        return 1950;
      case 2345: // Executioner
      case 2364: // Voice of Justice
        return 3770;
      case 2351: // Galil
      case 2373: // Lamentation
        return 1600;
      case 2353: // Ballistic Knife
      case 2374: // The Krauss Refibrillator
        return 180;
      case 2355: // MTAR
      case 2375: // Malevolent Taxonomic Anodized Redeemer
        return 1000;
      case 2361: // RPD
      case 2381: // Relativistic Punishment Device
        return 6300;
      case 2363: // DSR-50
      case 2382: // Dead Specimen Reactor 5000
        return 1950;
      case 2371: // HAMR
      case 2391: // SLDG HAMR
        return 2200;
      case 2372: // Ray Gun
      case 2392: // Porter's X2 Ray Gun
        return 2600;
      default:
        return 0;
    }
  }


  // pickup
  void pickupSound() {
    String sound_name = "items/pickup/";
    switch(this.ID) {
      case 2204: // Decoy
      case 2211: // The Thing
        sound_name += "sword";
        break;
      case 2980: // Drill
        sound_name = "items/melee/drill" + Misc.randomInt(1, 3);
        break;
      case 2981: // Roundsaw
        sound_name += "roundsaw";
        break;
      case 2983: // Chainsaw
        sound_name += "chainsaw";
        break;
      default:
        sound_name += "default";
        break;
    }
    p.global.sounds.trigger_units(sound_name);
  }

  // equip
  void equipSound() {
    String sound_name = "player/";
    switch(this.ID) {
      case 2402: // Cap
      case 2502: // T-shirt
      case 2503: // Bra
      case 2504: // Coat
      case 2512: // Ben's Coat
      case 2513: // Suit Jacket
      case 2602: // Boxers
      case 2603: // Towel
      case 2604: // Pants
      case 2702: // Socks
      case 2703: // Sandals
      case 2704: // Shoes
      case 2705: // Boots
      case 2712: // Sneakers
        sound_name += "armor_cloth";
        break;
      default:
        sound_name += "armor_metal";
        break;
    }
    p.global.sounds.trigger_player(sound_name);
  }

  // melee attack
  void attackSound() {
    String sound_name = "items/melee/";
    switch(this.ID) {
      case 2203: // knife
      case 2353: // ballistic knife
      case 2374: // the krauss refibrillator
        sound_name += "knife";
        break;
      case 2204: // decoy
      case 2211: // the thing
        sound_name += "sword_swing";
        break;
      case 2205: // wooden sword
      case 2206: // talc sword
      case 2212: // gypsum sword
      case 2221: // calcite sword
      case 2231: // fluorite sword
      case 2241: // apatite sword
      case 2251: // orthoclase sword
      case 2261: // quartz sword
      case 2271: // topaz sword
      case 2281: // corundum sword
      case 2291: // diamond sword
        sound_name += "sword";
        break;
      case 2207: // wooden spear
      case 2208: // talc spear
      case 2213: // gypsum spear
      case 2222: // calcite spear
      case 2232: // fluorite spear
      case 2242: // apatite spear
      case 2252: // orthoclase spear
      case 2262: // quartz spear
      case 2272: // topaz spear
      case 2282: // corundum spear
      case 2292: // diamond spear
        sound_name += "spear";
        break;
      case 5203: // wooden ax
      case 5204: // talc ax
      case 2977: // stone hatchet
        sound_name += "ax";
        break;
      case 2979: // saw
        sound_name += "saw";
        break;
      case 2980: // drill
        sound_name += "drill" + Misc.randomInt(1, 3);
        break;
      case 2983: // chainsaw
        sound_name += "chainsaw";
        break;
      default:
        sound_name += "default";
        break;
    }
    p.global.sounds.trigger_units(sound_name);
  }


  double interactionDistance() {
    switch(this.ID) {
      default:
        return LNZ.item_defaultInteractionDistance;
    }
  }


  void update(int time_elapsed) {
    this.bounce.add(time_elapsed);
    if (this.recently_dropped >= 0) {
      this.recently_dropped -= time_elapsed;
    }
    if (this.disappearing) {
      this.disappear_timer -= time_elapsed;
      if (disappear_timer < 0) {
        this.remove = true;
      }
    }
    switch(this.ID) {
      case 2163: // candle
        if (this.toggled) { // lit
          this.ammo -= time_elapsed;
          if (this.ammo < 0) {
            this.toggled = false;
            //this.remove = true;
          }
        }
        break;
      case 2164: // lords day candle
        if (this.toggled) { // lit
          this.ammo -= time_elapsed;
          if (this.ammo < 0) {
            this.toggled = false;
            //this.remove = true;
          }
        }
        break;
      case 2928: // cigar
        if (this.toggled) { // lit
          this.ammo -= time_elapsed;
          if (this.ammo < 0) {
            this.remove = true;
          }
        }
        break;
      default:
        break;
    }
  }

  String fileString() {
    return this.fileString(null);
  }
  String fileString(GearSlot slot) {
    String fileString = "\nnew: Item: " + this.ID;
    fileString += this.objectFileString();
    fileString += "\ndisappearing: " + this.disappearing;
    fileString += "\ndisappear_timer: " + this.disappear_timer;
    fileString += "\nstack: " + this.stack;
    if (this.save_base_stats) {
      fileString += "\nsave_base_stats: " + this.save_base_stats;
      fileString += "\nsize: " + this.size;
      fileString += "\ncurr_health: " + this.curr_health;
      fileString += "\nhunger: " + this.hunger;
      fileString += "\nthirst: " + this.thirst;
      fileString += "\nmoney: " + this.money;
      fileString += "\nhealth: " + this.health;
      fileString += "\nattack: " + this.attack;
      fileString += "\nmagic: " + this.magic;
      fileString += "\ndefense: " + this.defense;
      fileString += "\nresistance: " + this.resistance;
      fileString += "\npiercing: " + this.piercing;
      fileString += "\npenetration: " + this.penetration;
      fileString += "\nattackRange: " + this.attackRange;
      fileString += "\nattackCooldown: " + this.attackCooldown;
      fileString += "\nattackTime: " + this.attackTime;
      fileString += "\nsight: " + this.sight;
      fileString += "\nspeed: " + this.speed;
      fileString += "\ntenacity: " + this.tenacity;
      fileString += "\nagility: " + this.agility;
      fileString += "\nlifesteal: " + this.lifesteal;
    }
    fileString += "\ndurability: " + this.durability;
    fileString += "\nammo: " + this.ammo;
    fileString += "\ntoggled: " + this.toggled;
    if (this.inventory != null) {
      fileString += this.inventory.internalFileString();
    }
    fileString += "\nend: Item";
    if (slot != null) {
      fileString += ": " + slot.slot_name();
    }
    return fileString;
  }

  void addData(String datakey, String data) {
    if (this.addObjectData(datakey, data)) {
      return;
    }
    switch(datakey) {
      case "disappearing":
        this.disappearing = Misc.toBoolean(data);
        break;
      case "disappear_timer":
        this.disappear_timer = Misc.toInt(data);
        break;
      case "stack":
        this.stack = Misc.toInt(data);
        break;
      case "size":
        this.size = Misc.toDouble(data);
        break;
      case "save_base_stats":
        this.save_base_stats = Misc.toBoolean(data);
        break;
      case "curr_health":
        this.curr_health = Misc.toDouble(data);
        break;
      case "hunger":
        this.hunger = Misc.toInt(data);
        break;
      case "thirst":
        this.thirst = Misc.toInt(data);
        break;
      case "money":
        this.money = Misc.toDouble(data);
        break;
      case "health":
        this.health = Misc.toDouble(data);
        break;
      case "attack":
        this.attack = Misc.toDouble(data);
        break;
      case "magic":
        this.magic = Misc.toDouble(data);
        break;
      case "defense":
        this.defense = Misc.toDouble(data);
        break;
      case "resistance":
        this.resistance = Misc.toDouble(data);
        break;
      case "piercing":
        this.piercing = Misc.toDouble(data);
        break;
      case "penetration":
        this.penetration = Misc.toDouble(data);
        break;
      case "attackRange":
        this.attackRange = Misc.toDouble(data);
        break;
      case "attackCooldown":
        this.attackCooldown = Misc.toDouble(data);
        break;
      case "attackTime":
        this.attackTime = Misc.toDouble(data);
        break;
      case "sight":
        this.sight = Misc.toDouble(data);
        break;
      case "speed":
        this.speed = Misc.toDouble(data);
        break;
      case "tenacity":
        this.tenacity = Misc.toDouble(data);
        break;
      case "agility":
        this.agility = Misc.toInt(data);
        break;
      case "lifesteal":
        this.lifesteal = Misc.toDouble(data);
        break;
      case "durability":
        this.durability = Misc.toInt(data);
        break;
      case "ammo":
        this.ammo = Misc.toInt(data);
        break;
      case "toggled":
        this.toggled = Misc.toBoolean(data);
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not found for Item data.");
        break;
    }
  }
}