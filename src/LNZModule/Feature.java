package LNZModule;

import java.util.*;
import processing.core.*;
import LNZApplet.LNZApplet;
import Misc.Misc;

public class Feature extends MapObject {
  protected int sizeX = 0;
  protected int sizeY = 0;
  protected int sizeZ = 0;

  protected int number = 0;
  protected int number2 = 0;
  protected int timer = 0;
  protected boolean toggle = false;
  protected Inventory inventory = null;
  protected List<Item> items = null;
  protected AdjacentContext adjacent_context = null;

  protected int map_key = -10;
  protected int map_priority = 0; // if stacks of features
  protected boolean refresh_map_image = false; // TODO: can remove now (reinstate for when can switch to flat)
  protected boolean blocking_view = false; // true when showing transparent

  Feature(LNZ sketch, int ID) {
    super(sketch, ID);
    switch(ID) {
      // fog
      case 1:
        this.setStrings("Fog", "Fog", "");
        this.setSize(1, 1, 0);
        break;
      case 2:
        this.setStrings("Fog", "Fog", "");
        this.setSize(1, 1, 0);
        break;
      case 3:
        this.setStrings("Fog", "Fog", "");
        this.setSize(1, 1, 0);
        break;
      case 4:
        this.setStrings("Fog", "Fog", "");
        this.setSize(1, 1, 0);
        break;
      case 5:
        this.setStrings("Fog", "Fog", "");
        this.setSize(1, 1, 0);
        break;
      case 6:
        this.setStrings("Fog", "Fog", "");
        this.setSize(1, 1, 0);
        break;
      case 7:
        this.setStrings("Fog", "Fog", "");
        this.setSize(1, 1, 0);
        break;
      case 8:
        this.setStrings("Fog", "Fog", "");
        this.setSize(1, 1, 0);
        break;
      case 9:
        this.setStrings("Fog", "Fog", "");
        this.setSize(1, 1, 0);
        break;
      case 10:
        this.setStrings("Fog", "Fog", "");
        this.setSize(1, 1, 0);
        break;

      // Unique
      case 11:
        this.setStrings("Traveling Buddy", "NPC", "");
        this.setSize(1, 1, 3);
        break;
      case 12:
        this.setStrings("Chuck Quizmo", "NPC", "");
        this.setSize(1, 1, 5);
        break;
      case 21:
        this.setStrings("Workbench", "Tool", "");
        this.setSize(1, 1, 3);
        this.inventory = new WorkbenchInventory(sketch, this);
        this.items = new ArrayList<Item>();
        this.number = 2;
        break;
      case 22:
        this.setStrings("Ender Chest", "Tool", "");
        this.setSize(1, 1, 3);
        this.inventory = p.global.profile.ender_chest;
        break;
      case 23:
        this.setStrings("Wooden Box", "Furniture", "");
        this.setSize(1, 1, 2);
        this.inventory = new WoodenBoxInventory(sketch);
        break;
      case 24:
        this.setStrings("Wooden Crate", "Furniture", "");
        this.setSize(1, 1, 3);
        this.inventory = new WoodenCrateInventory(sketch);
        break;
      case 25:
        this.setStrings("Wooden Chest", "Furniture", "");
        this.setSize(1, 1, 3);
        this.inventory = new WoodenChestInventory(sketch);
        this.number = 2;
        break;
      case 26:
        this.setStrings("Large Wooden Chest", "Furniture", "");
        this.setSize(2, 1, 3);
        this.inventory = new LargeWoodenChestInventory(sketch);
        this.number = 2;
        break;
      case 27:
        this.setStrings("Large Wooden Chest", "Furniture", "");
        this.setSize(1, 2, 3);
        this.inventory = new LargeWoodenChestInventory(sketch);
        this.number = 2;
        break;

      // Furniture
      case 101:
        this.setStrings("Wooden Table", "Furniture", "");
        this.setSize(2, 2, 3);
        this.number = LNZ.feature_woodenTableHealth;
        break;
      case 102:
        this.setStrings("Wooden Desk", "Furniture", "");
        this.setSize(2, 1, 4);
        this.inventory = new DeskInventory(sketch);
        this.number = LNZ.feature_woodenDeskHealth;
        break;
      case 103:
        this.setStrings("Wooden Desk", "Furniture", "");
        this.setSize(1, 2, 4);
        this.inventory = new DeskInventory(sketch);
        this.number = LNZ.feature_woodenDeskHealth;
        break;
      case 104:
        this.setStrings("Wooden Desk", "Furniture", "");
        this.setSize(2, 1, 4);
        this.inventory = new DeskInventory(sketch);
        this.number = LNZ.feature_woodenDeskHealth;
        break;
      case 105:
        this.setStrings("Wooden Desk", "Furniture", "");
        this.setSize(1, 2, 4);
        this.inventory = new DeskInventory(sketch);
        this.number = LNZ.feature_woodenDeskHealth;
        break;
      case 106:
        this.setStrings("Wooden Table", "Furniture", "");
        this.setSize(2, 1, 4);
        this.number = LNZ.feature_woodenTableHealth;
        break;
      case 107:
        this.setStrings("Wooden Table", "Furniture", "");
        this.setSize(1, 2, 4);
        this.number = LNZ.feature_woodenTableHealth;
        break;
      case 108:
        this.setStrings("Ping Pong Table", "Furniture", "");
        this.setSize(2, 2, 3);
        this.number = LNZ.feature_woodenTableHealth;
        break;
      case 109:
        this.setStrings("Wooden Table", "Furniture", "");
        this.setSize(2, 2, 2);
        this.number = LNZ.feature_woodenTableHealth;
        break;
      case 110:
        this.setStrings("Wooden Table", "Furniture", "");
        this.setSize(1, 1, 3);
        this.number = LNZ.feature_woodenTableHealth;
        break;
      case 111:
      case 112:
      case 113:
      case 114:
        this.setStrings("Wooden Chair", "Furniture", "");
        this.setSize(1, 1, 2);
        this.number = LNZ.feature_woodenChairHealth;
        break;
      case 115:
        this.setStrings("Coordinator Chair", "Furniture", "");
        this.setSize(1, 1, 2);
        this.toggle = true;
        this.number = LNZ.feature_couchHealth;
        break;
      case 116:
        this.setStrings("Wooden Table", "Furniture", "");
        this.setSize(1, 1, 2);
        this.number = LNZ.feature_woodenTableHealth;
        break;
      case 117:
        this.setStrings("Green Chair", "Furniture", "");
        this.setSize(1, 1, 2);
        this.toggle = true;
        this.number = LNZ.feature_couchHealth;
        break;
      case 121:
      case 122:
        this.setStrings("Couch", "Furniture", "");
        this.setSize(3, 1, 2);
        this.toggle = true;
        this.number = LNZ.feature_couchHealth;
        break;
      case 123:
      case 124:
        this.setStrings("Couch", "Furniture", "");
        this.setSize(1, 3, 2);
        this.toggle = true;
        this.number = LNZ.feature_couchHealth;
        break;
      case 125:
        this.setStrings("Bench", "Furniture", "");
        this.setSize(2, 1, 2);
        this.number = LNZ.feature_woodenBenchSmallHealth;
        break;
      case 126:
        this.setStrings("Bench", "Furniture", "");
        this.setSize(1, 2, 2);
        this.number = LNZ.feature_woodenBenchSmallHealth;
        break;
      case 127:
        this.setStrings("Bench", "Furniture", "");
        this.setSize(2, 3, 3);
        this.number = LNZ.feature_woodenBenchLargeHealth;
        break;
      case 128:
        this.setStrings("Bench", "Furniture", "");
        this.setSize(3, 2, 3);
        this.number = LNZ.feature_woodenBenchLargeHealth;
        break;
      case 129:
        this.setStrings("Bench", "Furniture", "");
        this.setSize(2, 1, 2);
        this.number = LNZ.feature_woodenBenchSmallHealth;
        break;
      case 130:
        this.setStrings("Bench", "Furniture", "");
        this.setSize(1, 2, 2);
        this.number = LNZ.feature_woodenBenchSmallHealth;
        break;
      case 131:
      case 132:
        this.setStrings("Bed", "Furniture", "");
        this.setSize(1, 2, 3);
        this.toggle = true;
        this.number = LNZ.feature_bedHealth;
        break;
      case 133:
      case 134:
        this.setStrings("Bed", "Furniture", "");
        this.setSize(2, 1, 3);
        this.toggle = true;
        this.number = LNZ.feature_woodenTableHealth;
        break;
      case 141:
      case 142:
        this.setStrings("Wardrobe", "Furniture", "");
        this.setSize(2, 1, 9);
        this.toggle = true;
        this.number = LNZ.feature_wardrobeHealth;
        break;
      case 143:
      case 144:
        this.setStrings("Wardrobe", "Furniture", "");
        this.setSize(1, 2, 9);
        this.toggle = true;
        this.number = LNZ.feature_wardrobeHealth;
        break;
      case 151:
      case 152:
        this.setStrings("Sign", "Sign", LNZ.feature_signDescriptionDelimiter);
        this.setSize(1, 1, 3);
        break;
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
        this.setStrings("Sign", "Sign", LNZ.feature_signDescriptionDelimiter);
        this.setSize(1, 1, 0);
        break;
      case 160:
      case 161:
        this.setStrings("Water Fountain", "Furniture", "");
        this.setSize(1, 1, 4);
        break;
      case 162:
        this.setStrings("Sink", "Furniture", "");
        this.setSize(1, 1, 4);
        this.number2 = PConstants.RIGHT; // determines rotation
        break;
      case 163:
        this.setStrings("Shower Stall", "Furniture", "");
        this.setSize(1, 1, -1);
        break;
      case 164:
        this.setStrings("Urinal", "Furniture", "");
        this.setSize(1, 1, 5);
        break;
      case 165:
        this.setStrings("Toilet", "Furniture", "");
        this.setSize(1, 1, 2);
        break;
      case 171:
        this.setStrings("Stove", "Appliance", "");
        this.setSize(1, 1, 4);
        this.inventory = new StoveInventory(sketch);
        break;
      case 172:
        this.setStrings("Vending Machine", "Appliance", "");
        this.setSize(1, 1, 7);
        break;
      case 173:
        this.setStrings("Vending Machine", "Appliance", "");
        this.setSize(1, 1, 7);
        break;
      case 174:
        this.setStrings("Minifridge", "Appliance", "");
        this.setSize(1, 1, 3);
        this.inventory = new MinifridgeInventory(sketch);
        break;
      case 175:
        this.setStrings("Refridgerator", "Appliance", "");
        this.setSize(1, 1, 7);
        this.inventory = new RefridgeratorInventory(sketch);
        break;
      case 176:
        this.setStrings("Washer", "Appliance", "");
        this.setSize(1, 1, 3);
        this.inventory = new WasherInventory(sketch);
        break;
      case 177:
        this.setStrings("Dryer", "Appliance", "");
        this.setSize(1, 1, 3);
        this.inventory = new DryerInventory(sketch);
        break;
      case 178:
        this.setStrings("Microwave", "Appliance", "");
        this.setSize(1, 1, 1);
        this.inventory = new MicrowaveInventory(sketch);
        break;
      case 179:
        this.setStrings("TV", "Appliance", "");
        this.setSize(1, 1, 2);
        break;
      case 180:
        this.setStrings("Lamp", "Appliance", "");
        this.setSize(1, 1, 3);
        break;
      case 181:
        this.setStrings("Garbage Can", "Furniture", "");
        this.setSize(1, 1, 3);
        this.inventory = new GarbageInventory(sketch);
        break;
      case 182:
        this.setStrings("Recycle Can", "Furniture", "");
        this.setSize(1, 1, 3);
        this.inventory = new RecycleInventory(sketch);
        break;
      case 183:
        this.setStrings("Crate", "Furniture", "");
        this.setSize(1, 1, 2);
        this.inventory = new CrateInventory(sketch);
        break;
      case 184:
        this.setStrings("Cardboard Box", "Furniture", "");
        this.setSize(1, 1, 2);
        this.inventory = new CardboardBoxInventory(sketch);
        break;
      case 185:
        this.setStrings("Pickle Jar", "Furniture", "");
        this.setSize(1, 1, 1);
        break;
      case 186:
        this.setStrings("Outside Light Source", "", "");
        this.setSize(1, 1, 0);
        this.toggle = true;
        break;
      case 187:
        this.setStrings("Invisible Light Source", "", "");
        this.setSize(1, 1, 0);
        this.toggle = true;
        break;
      case 188:
        this.setStrings("Invisible Light Source", "", "");
        this.setSize(2, 1, 0);
        this.toggle = true;
        break;
      case 189:
        this.setStrings("Invisible Light Source", "", "");
        this.setSize(1, 2, 0);
        this.toggle = true;
        break;
      case 190:
        this.setStrings("Invisible Light Source", "", "");
        this.setSize(2, 2, 0);
        this.toggle = true;
        break;
      case 191:
      case 192:
      case 193:
      case 194:
        this.setStrings("Railing", "Furniture", "");
        this.setSize(1, 1, 5);
        break;
      case 195:
      case 196:
      case 197:
      case 198:
        this.setStrings("Light Switch", "Appliance", "");
        this.setSize(1, 1, 0);
        this.toggle = true;
        break;
      case 201:
        this.setStrings("Steel Cross", "Statue", "");
        this.setSize(2, 2, 100);
        break;
      case 202:
        this.setStrings("Statue", "Statue", "");
        this.setSize(1, 1, 6);
        break;
      case 211:
        this.setStrings("Wire Fence", "Fence", "");
        this.setSize(1, 1, 4);
        this.adjacent_context = new AdjacentContext();
        break;
      case 212:
        this.setStrings("Barbed Wire Fence", "Fence", "");
        this.setSize(1, 1, 5);
        this.adjacent_context = new AdjacentContext();
        break;
      case 251:
        this.setStrings("Parking Bumper", "Outdoors", "");
        this.setSize(1, 1, 1);
        break;
      case 252:
        this.setStrings("Parking Bumper", "Outdoors", "");
        this.setSize(1, 1, 1);
        break;
      case 253:
        this.setStrings("Gazebo", "Outdoors", "");
        this.setSize(4, 4, 7);
        break;
      case 254:
        this.setStrings("Parking Bumper", "Outdoors", "");
        this.setSize(1, 1, 1);
        break;
      case 255:
        this.setStrings("Parking Bumper", "Outdoors", "");
        this.setSize(1, 1, 1);
        break;
      case 256:
        this.setStrings("Tent", "Outdoors", "");
        this.setSize(1, 2, 3);
        break;
      case 257:
        this.setStrings("Tent", "Outdoors", "");
        this.setSize(2, 1, 3);
        break;
      case 258:
        this.setStrings("Tent", "Outdoors", "");
        this.setSize(1, 1, 2);
        break;
      case 259:
        this.setStrings("Tent", "Outdoors", "");
        this.setSize(2, 1, 3);
        break;
      case 260:
        this.setStrings("Tent", "Outdoors", "");
        this.setSize(1, 2, 3);
        break;
      case 261:
        this.setStrings("Campfire", "Outdoors", "");
        this.setSize(2, 2, 1);
        break;

      // Walls
      case 301:
      case 302:
      case 303:
      case 304:
      case 305:
      case 306:
      case 307:
        this.setStrings("Brick Wall", "Wall", "");
        this.setSize(1, 1, 100);
        break;
      case 311:
      case 312:
        this.setStrings("Pillar", "Wall", "");
        this.setSize(1, 1, 100);
        break;
      case 313:
      case 314:
      case 315:
      case 316:
      case 317:
      case 318:
      case 319:
      case 320:
        this.setStrings("Window Pane", "Window", "");
        this.setSize(1, 1, 1);
        break;
      case 321:
      case 322:
      case 323:
      case 324:
      case 325:
      case 326:
      case 327:
      case 328:
        this.setStrings("Window", "Window", "");
        this.setSize(1, 1, 100);
        break;
      case 331:
      case 332:
      case 333:
      case 334:
      case 335:
      case 336:
      case 337:
      case 338:
        this.setStrings("Wooden Door", "Door", "");
        this.setSize(1, 1, 0);
        break;
      case 339:
      case 340:
      case 341:
      case 342:
      case 343:
      case 344:
      case 345:
      case 346:
        this.setStrings("Wooden Door", "Door", "");
        this.setSize(1, 1, 100);
        break;
      case 351:
      case 352:
      case 353:
      case 354:
      case 355:
      case 356:
      case 357:
      case 358:
        this.setStrings("Steel Door", "Door", "");
        this.setSize(1, 1, 0);
        break;
      case 359:
      case 360:
      case 361:
      case 362:
      case 363:
      case 364:
      case 365:
      case 366:
        this.setStrings("Steel Door", "Door", "");
        this.setSize(1, 1, 100);
        break;

      // Nature
      case 401:
        this.setStrings("Dandelion", "Nature",
          "Common flower found worldwide. The word 'dandelion' " +
          "comes from the French, meaning 'lion's tooth.'");
        this.setSize(1, 1, 0);
        break;
      case 411:
      case 412:
        this.setStrings("Gravel", "Nature", "");
        this.setSize(1, 1, 0);
        this.number = Misc.randomInt(LNZ.feature_gravelMaxNumberRocks);
        break;
      case 413:
        this.setStrings("Ivy", "Nature", "");
        this.setSize(1, 1, 0);
        this.timer = LNZ.feature_ivyGrowTimer + Misc.randomInt(LNZ.feature_ivyGrowTimer);
        break;
      case 421:
      case 422:
      case 423:
      case 424:
      case 426:
        this.setStrings("Tree", "Nature", "");
        this.setSize(1, 1, 0);
        this.toggle = true;
        this.number = LNZ.feature_treeHealth;
        this.number2 = Misc.randomInt(1, 8);
        break;
      case 425:
        this.setStrings("Tree", "Nature", "");
        this.setSize(2, 2, 0);
        this.toggle = true;
        this.number = LNZ.feature_treeBigHealth;
        this.number2 = Misc.randomInt(1, 3);
        break;
      case 431:
        this.setStrings("Rock", "Nature", "");
        this.setSize(1, 1, 3);
        break;
      case 440:
        this.setStrings("Underbrush", "Nature", "");
        this.setSize(1, 1, 3);
        this.number = LNZ.feature_bushHealth;
        this.number2 = Misc.randomInt(1, 4);
        break;
      case 441:
      case 442:
      case 443:
        this.setStrings("Bush", "Nature", "");
        this.setSize(1, 1, 3);
        this.number = LNZ.feature_bushHealth;
        break;
      case 444:
      case 445:
      case 446:
      case 447:
      case 449:
        this.setStrings("Tree", "Nature", "");
        this.setSize(2, 2, 0);
        this.toggle = true;
        this.number = LNZ.feature_treeBigHealth;
        this.number2 = Misc.randomInt(1, 8);
        break;
      case 448:
        this.setStrings("Tree", "Nature", "");
        this.setSize(3, 3, 0);
        this.toggle = true;
        this.number = (int)Math.round(1.4 * LNZ.feature_treeBigHealth);
        this.number2 = Misc.randomInt(1, 3);
        break;
      case 451:
        this.setStrings("Wapato", "Nature", "Also known as an Indian potato, " +
          "as it was a key part of many native American diets.");
        this.setSize(1, 1, 0);
        break;
      case 452:
        this.setStrings("Leek", "Nature", "The broadleaf wild leek plant, " +
          "related to onion and garlic.");
        this.setSize(1, 1, 0);
        break;
      case 453:
        this.setStrings("Ryegrass", "Nature", "Grass of the genus Lolium, " +
          "not to be confused with the grain crop Rye.");
        this.setSize(1, 1, 0);
        break;
      case 454:
        this.setStrings("Barnyard Grass", "Nature", "Grass of the genus " +
          "Echinochloa; it grows like a weed.");
        this.setSize(1, 1, 0);
        break;
      case 455:
        this.setStrings("Broadleaf Plantain", "Nature", "Also known as white " +
          "man's footprint as it's native to Eurasia. Not to be confused with " +
          "bananas of the genus Musa.");
        this.setSize(1, 1, 0);
        break;
      case 456:
        this.setStrings("Stinging Nettle", "Nature", "Herbaceous plant with a " +
          "long history of use as food and medicine.");
        this.setSize(1, 1, 0);
        break;
      case 457:
        this.setStrings("Sapling", "Nature", "");
        this.setSize(1, 1, 0);
        this.timer = 100 * 60000 + Misc.randomInt(50 * 60000);
        break;
      case 458:
        this.setStrings("Sapling", "Nature", "");
        this.setSize(1, 1, 0);
        this.timer = 120 * 60000 + Misc.randomInt(60 * 60000);
        break;
      case 459:
        this.setStrings("Sapling", "Nature", "");
        this.setSize(1, 1, 0);
        this.timer = 120 * 60000 + Misc.randomInt(30 * 60000);
        break;
      case 460:
        this.setStrings("Sapling", "Nature", "");
        this.setSize(1, 1, 0);
        this.timer = 140 * 60000 + Misc.randomInt(50 * 60000);
        break;
      case 461:
        this.setStrings("Sapling", "Nature", "");
        this.setSize(1, 1, 0);
        this.timer = 120 * 60000 + Misc.randomInt(30 * 60000);
        break;
      case 462:
        this.setStrings("Sprout", "Nature", "");
        this.setSize(1, 1, 0);
        this.timer = 50 * 60000 + Misc.randomInt(25 * 60000);
        break;
      case 463:
        this.setStrings("Sprout", "Nature", "");
        this.setSize(1, 1, 0);
        this.timer = 60 * 60000 + Misc.randomInt(15 * 60000);
        break;
      case 464:
        this.setStrings("Sprout", "Nature", "");
        this.setSize(1, 1, 0);
        this.timer = 60 * 60000 + Misc.randomInt(15 * 60000);
        break;
      case 465:
        this.setStrings("Sprout", "Nature", "");
        this.setSize(1, 1, 0);
        this.timer = 70 * 60000 + Misc.randomInt(25 * 60000);
        break;
      case 466:
        this.setStrings("Sprout", "Nature", "");
        this.setSize(1, 1, 0);
        this.timer = 60 * 60000 + Misc.randomInt(15 * 60000);
        break;

      // Vehicles
      case 501:
        this.setStrings("Honda CR-V", "Car", "");
        this.setSize(2, 3, 5);
        break;
      case 502:
        this.setStrings("Ford F-150", "Car", "");
        this.setSize(2, 3, 5);
        break;
      case 503:
        this.setStrings("VW Jetta", "Car", "");
        this.setSize(2, 3, 5);
        break;
      case 504:
        this.setStrings("VW Bug", "Car", "");
        this.setSize(3, 2, 5);
        break;
      case 505:
        this.setStrings("Lamborghini", "Car", "");
        this.setSize(3, 2, 5);
        break;
      case 506:
        this.setStrings("Honda CR-V (broken down)", "Car", "");
        this.setSize(2, 3, 5);
        break;
      case 511:
        this.setStrings("Civilian Helicopter", "Helicopter", "");
        this.setSize(5, 5, 11);
        break;
      case 512:
        this.setStrings("Medical Helicopter", "Helicopter", "");
        this.setSize(5, 4, 11);
        break;
      case 513:
        this.setStrings("Military Helicopter", "Helicopter", "");
        this.setSize(5, 4, 11);
        break;

      default:
        p.global.errorMessage("ERROR: Feature ID " + ID + " not found.");
        break;
    }
  }
  Feature(LNZ sketch, int ID, double x, double y) {
    this(sketch, ID);
    this.setLocation(x, y);
  }
  Feature(LNZ sketch, int ID, Coordinate coordinate) {
    this(sketch, ID);
    this.setLocation(coordinate);
  }
  Feature(LNZ sketch, int ID, double x, double y, boolean toggle) {
    this(sketch, ID);
    this.setLocation(x, y);
    this.toggle = toggle;
  }
  Feature(LNZ sketch, int ID, Coordinate coordinate, boolean toggle) {
    this(sketch, ID);
    this.setLocation(coordinate);
    this.toggle = toggle;
  }

  String displayName() {
    switch(this.ID) {
      case 456: // Stinging Nettle
        if (this.number == 4) {
          return "Flowering " + this.display_name;
        }
        return this.display_name;
      case 421: // Tree, maple
      case 444:
      case 457: // Sapling, maple
      case 462: // Sprout, maple
        if (p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "Maple " +  this.display_name;
        }
        break;
      case 422: // Tree, walnut
      case 445:
      case 458: // Sapling, walnut
      case 463: // Sprout, walnut
        if (p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "Walnut " +  this.display_name;
        }
        break;
      case 423: // Tree, cedar
      case 446:
      case 459: // Sapling, cedar
      case 464: // Sprout, cedar
        if (p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "Cedar " +  this.display_name;
        }
        break;
      case 424: // Tree, dead
      case 447:
        if (p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "Dead " +  this.display_name;
        }
        break;
      case 425: // Tree, oak
      case 448:
      case 460: // Sapling, oak
      case 465: // Sprout, oak
        if (p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "Oak " +  this.display_name;
        }
        break;
      case 426: // Tree, pine
      case 449:
      case 461: // Sapling, pine
      case 466: // Sprout, pine
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
    switch(this.ID) {
      case 21: // workbench
        String workbench_description = "Tools Available:";
        for (Item i : this.items) {
          if (i == null || i.remove) {
            continue;
          }
          switch(i.ID) {
            case 2809: // String
            case 2970: // Wooden Peg
              workbench_description += "\n" + i.displayName() + " (" + i.stack + ")";
              break;
            default:
              workbench_description += "\n" + i.displayName() + " (" + i.durability + ")";
              break;
          }
        }
        return workbench_description;
      case 151: // sign
      case 152:
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
        return PApplet.trim(PApplet.split(this.description, LNZ.feature_signDescriptionDelimiter)[0]);
      default:
        return this.description;
    }
  }
  String selectedObjectTextboxText() {
    String text = "-- " + this.type() + " --\n";
    if (this.car()) {
      text += "\nGas: " + 0.1 * this.number + "/" + Math.round(0.1 * this.gasTankSize()) + " gallons";
    }
    text += "\n\n" + this.description();
    if (p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
      switch(this.ID) {
        case 401: // Dandelion
          String stage_name = "";
          switch(this.number) {
            case 1:
              stage_name = "Sprout";
              break;
            case 2:
              stage_name = "Budding";
              break;
            case 3:
              stage_name = "Flowering";
              break;
            case 4:
              stage_name = "Seeding";
              break;
            default:
              break;
          }
          text += "\n\nCurrent stage: " + stage_name +
            "\nTime to next stage: " + (Math.round(this.timer * 0.000008333) / 10.0) + " days";
          break;
        case 451: // Wapato
          if (this.number == 4) {
            text += "\n\nCurrent stage: " + this.number +
              "\nTime to next stage: ready to harvest" +
              "\nCan't grow if not watered." +
              "\nGrows twice as fast in shallow mud water.";
            break;
          }
          text += "\n\nCurrent stage: " + this.number +
            "\nTime to next stage: " + (Math.round(this.timer * 0.000008333) / 10.0) + " days" +
            "\nCan't grow if not watered." +
            "\nGrows twice as fast in shallow mud water.";
          break;
        case 452: // Leek
        case 453: // Ryegrass
        case 454: // Barnyard Grass
        case 455: // Broadleaf Plantain
        case 456: // Stinging Nettle
          if (this.number == 4) {
            text += "\n\nCurrent stage: " + this.number +
              "\nTime to next stage: ready to harvest" +
              "\nGrows 80% slower if not watered.";
            break;
          }
          text += "\n\nCurrent stage: " + this.number +
            "\nTime to next stage: " + (Math.round(this.timer * 0.000008333) / 10.0) + " days" +
            "\nGrows 80% slower if not watered.";
          break;
        case 457: // Sapling, maple
        case 458: // Sapling, walnut
        case 459: // Sapling, cedar
        case 460: // Sapling, oak
        case 461: // Sapling, pine
          text += "\n\nTime to maturity: " + (Math.round(this.timer * 0.000008333) / 10.0) + " days";
          break;
        case 462: // Sprout, maple
        case 463: // Sprout, walnut
        case 464: // Sprout, cedar
        case 465: // Sprout, oak
        case 466: // Sprout, pine
          text += "\n\nTime to sapling stage: " + (Math.round(this.timer * 0.000008333) / 10.0) + " days";
          break;
        default:
          break;
      }
    }
    return text;
  }

  void setLocation(double x, double y) {
    this.coordinate = new Coordinate((int)x, (int)y);
  }
  void setLocation(Coordinate coordinate) {
    this.coordinate = coordinate.floorR();
  }
  IntegerCoordinate gridLocation() {
    return new IntegerCoordinate(this.coordinate);
  }
  IntegerCoordinate endGridLocation() {
    return new IntegerCoordinate(
      (int)Math.round(this.coordinate.x + this.sizeX - 1),
      (int)Math.round(this.coordinate.y + this.sizeY - 1));
  }

  // TODO: Remove this function
  // This function serves as a temporary workaround to not make image pieces
  // for all the features that need them. Should be removed eventually.
  static List<FeatureDrawGridPiece> drawGridLocationsOverride(int ID,
    Map<IntegerCoordinate, List<FeatureDrawGridPiece>> locations_map,
    IntegerCoordinate size) {
    List<FeatureDrawGridPiece> locations = new ArrayList<FeatureDrawGridPiece>();
    switch(ID) {
      case 448: // 3x3
        locations.add(new FeatureDrawGridPiece(new IntegerCoordinate(-1, 2),
          new IntegerCoordinate(2, 2), true, true));
        return locations;
      case 501: // 2x3
      case 502:
      case 503:
      case 506:
        locations.add(new FeatureDrawGridPiece(new IntegerCoordinate(-1, 2),
          new IntegerCoordinate(0, 1), true, true));
        return locations;
      case 504: // 3x2
      case 505:
        locations.add(new FeatureDrawGridPiece(new IntegerCoordinate(-1, 1),
          new IntegerCoordinate(1, 0), true, true));
        return locations;
      default:
        return Feature.drawGridLocations(locations_map, size);
    }
  }

  // Returns list of coordinates where feature is drawn
  // since can only draw 2x1 or 1x2 piece at a time
  // this function is based entirely on size of feature, so is static
  // and updates a map (in AbstractGameMap) to avoid repeat calculations
  public static List<FeatureDrawGridPiece> drawGridLocations(
    Map<IntegerCoordinate, List<FeatureDrawGridPiece>> locations_map,
    IntegerCoordinate size) {
    if (locations_map.containsKey(size)) {
      return locations_map.get(size);
    }
    List<FeatureDrawGridPiece> locations = new ArrayList<FeatureDrawGridPiece>();
    if (size.x == 2 && size.y == 2) { // special case
      locations.add(new FeatureDrawGridPiece(new IntegerCoordinate(-1, 1),
        new IntegerCoordinate(0, 0), true, true));
      locations_map.put(size, locations);
      return locations;
    }
    boolean can_add_y = true;
    for (int y = 0; y < size.y; y++) {
      for (int x = 0; x < size.x; x += 2) {
        if (x + 1 < size.x) { // space to insert a 2x1 piece
          locations.add(new FeatureDrawGridPiece(
            new IntegerCoordinate(x + 1, y), new IntegerCoordinate(x, y),
            true, false));
          continue;
        }
        if (y + 1 < size.y) { // space to insert a 1x2 piece
          if (can_add_y) { // to avoid overlapping 1x2 pieces
            locations.add(new FeatureDrawGridPiece(
              new IntegerCoordinate(x - 1, y + 1), new IntegerCoordinate(x, y),
              false, true));
            can_add_y = false;
          }
          else {
            can_add_y = true;
          }
          continue;
        }
        if (can_add_y) {
          locations.add(new FeatureDrawGridPiece(
            new IntegerCoordinate(x, y)));
        }
        else {
          can_add_y = true;
        }
      }
    }
    locations_map.put(size, locations);
    return locations;
  }

  boolean drawGridLocationOutsideFeature() {
    if (this.sizeY == 1) {
      return false;
    }
    return true;
  }

  Coordinate randomLocationUnderFeature() {
    return new Coordinate(
      this.coordinate.x + 0.2 + Misc.randomDouble(0.6 + this.sizeX - 1),
      this.coordinate.y + 0.2 + Misc.randomDouble(0.6 + this.sizeY - 1));
  }

  void setSize(int sizeX, int sizeY, int sizeZ) {
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.sizeZ = sizeZ;
  }

  double xi() {
    return this.coordinate.x;
  }
  double yi() {
    return this.coordinate.y;
  }
  double xf() {
    return this.coordinate.x + this.sizeX;
  }
  double yf() {
    return this.coordinate.y + this.sizeY;
  }
  double xCenter() {
    return this.coordinate.x + 0.5 * this.sizeX;
  }
  double yCenter() {
    return this.coordinate.y + 0.5 * this.sizeY;
  }
  double width() {
    return this.sizeX;
  }
  double height() {
    return this.sizeY;
  }
  double xRadius() {
    return 0.5 * this.sizeX;
  }
  double yRadius() {
    return 0.5 * this.sizeY;
  }
  double zi() {
    return this.curr_height;
  }
  double zf() {
    return this.curr_height + this.sizeZ;
  }
  double zHalf() {
    return this.curr_height + 0.5 * this.sizeZ;
  }

  boolean displaysImage() {
    switch(this.ID) {
      case 186:
      case 187:
      case 188:
      case 189:
      case 190:
        return false;
      default:
        return true;
    }
  }

  private String getImagePath() {
    return this.getImagePath(-1);
  }
  private String getImagePath(int piece_index) {
    String path = "features/";
    switch(this.ID) {
      case 1:
        path += "fog0";
        break;
      case 2:
        path += "fog1";
        break;
      case 3:
        path += "fog2";
        break;
      case 4:
        path += "fog3";
        break;
      case 5:
        path += "fog4";
        break;
      case 6:
        path += "fog5";
        break;
      case 7:
        path += "fog6";
        break;
      case 8:
        path += "fog7";
        break;
      case 9:
        path += "fog8";
        break;
      case 10:
        path += "fog9";
        break;
      case 11:
        path += "khalil";
        break;
      case 12:
        path += "chuck_quizmo";
        break;
      case 21:
        path += "workbench";
        break;
      case 22:
        path += "ender_chest";
        break;
      case 23:
        path += "wooden_box";
        break;
      case 24:
        path += "wooden_crate";
        break;
      case 25: // TODO: Chest can have other orientation based on how it's placed (timer?)
        path += "wooden_chest";
        break;
      case 26:
        path += "large_wooden_chest_down";
        break;
      case 27:
        path += "large_wooden_chest_right";
        break;
      case 101:
        path += "table";
        break;
      case 102:
        path += "desk_up";
        break;
      case 103:
        path += "desk_left";
        break;
      case 104:
        path += "desk_down";
        break;
      case 105:
        path += "desk_right";
        break;
      case 106:
        path += "table_small_up";
        break;
      case 107:
        path += "table_small_left";
        break;
      case 108:
        if (this.toggle) {
          path += "pingpong_table1";
        }
        else {
          path += "pingpong_table2";
        }
        break;
      case 109:
        path += "table_low";
        break;
      case 110:
        path += "table_end";
        break;
      case 111:
        path += "chair_up";
        break;
      case 112:
        path += "chair_down";
        break;
      case 113:
        path += "chair_left";
        break;
      case 114:
        path += "chair_right";
        break;
      case 115:
        path += "chair_coordinator";
        break;
      case 116:
        path += "table_end_low";
        break;
      case 117:
        path += "chair_green";
        break;
      case 121:
        path += "couch_up";
        break;
      case 122:
        path += "couch_down";
        break;
      case 123:
        path += "couch_left";
        break;
      case 124:
        path += "couch_right";
        break;
      case 125:
        path += "bench_small_up";
        break;
      case 126:
        path += "bench_small_left";
        break;
      case 127:
        path += "bench_large_up";
        break;
      case 128:
        path += "bench_large_left";
        break;
      case 129:
        path += "bench_small_down";
        break;
      case 130:
        path += "bench_small_right";
        break;
      case 131:
        path += "bed_up";
        break;
      case 132:
        path += "bed_down";
        break;
      case 133:
        path += "bed_left";
        break;
      case 134:
        path += "bed_right";
        break;
      case 141:
        path += "wardrobe_up";
        break;
      case 142:
        path += "wardrobe_down";
        break;
      case 143:
        path += "wardrobe_left";
        break;
      case 144:
        path += "wardrobe_right";
        break;
      case 151:
        path += "sign_wooden_up";
        break;
      case 152:
        path += "sign_wooden_left";
        break;
      case 153:
        path += "sign_green_left";
        break;
      case 154:
        path += "sign_green_right";
        break;
      case 155:
        path += "sign_gray_up";
        break;
      case 156:
        path += "sign_gray_down";
        break;
      case 157:
        path += "sign_gray_left";
        break;
      case 158:
        path += "sign_gray_right";
        break;
      case 160:
        if (this.timer > 0) {
          path += "water_fountain_right"; // TODO: _on";
        }
        else {
          path += "water_fountain_right";
        }
        break;
      case 161:
        if (this.timer > 0) {
          path += "water_fountain_down"; // TODO: _on";
        }
        else {
          path += "water_fountain_down";
        }
        break;
      case 162:
        path += "sink";
        switch(this.number2) {
          case PConstants.UP:
            path += "_up";
            break;
          case PConstants.DOWN:
            path += "_down";
            break;
          case PConstants.LEFT:
            path += "_left";
            break;
          case PConstants.RIGHT:
            path += "_right";
            break;
          default:
            p.global.log("WARNING: Sink rotation not valid.");
            break;
        }
        break;
      case 163:
        if (this.timer > 0) {
          path += "shower_stall_on";
        }
        else {
          path += "shower_stall";
        }
        break;
      case 164:
        path += "urinal";
        break;
      case 165:
        path += "toilet";
        break;
      case 171:
        path += "stove";
        break;
      case 172:
        path += "vending_machine_food";
        break;
      case 173:
        path += "vending_machine_drink";
        break;
      case 174:
        if (this.toggle) {
          path += "minifridge_right";
        }
        else {
          path += "minifridge_down";
        }
        break;
      case 175:
        path += "fridge";
        break;
      case 176:
        path += "washer";
        break;
      case 177:
        path += "dryer";
        break;
      case 178:
        path += "microwave";
        break;
      case 179:
        path += "tv";
        break;
      case 180:
        if (this.toggle) {
          path += "lamp_on";
        }
        else {
          path += "lamp";
        }
        break;
      case 181:
        if (this.toggle) {
          path += "garbage_can_aluminum";
        }
        else {
          path += "garbage_can";
        }
        break;
      case 182:
        path += "recycle_can";
        break;
      case 183:
        path += "crate";
        break;
      case 184:
        path += "cardboard_box";
        break;
      case 185:
        path += "pickle_jar";
        break;
      case 186:
      case 187:
      case 188:
      case 189:
      case 190:
        path = "transparent";
        break;
      case 191:
        path += "railing_green_up";
        break;
      case 192:
        path += "railing_green_left";
        break;
      case 193:
        path += "railing_red_up";
        break;
      case 194:
        path += "railing_red_left";
        break;
      case 195:
        if (this.toggle) {
          path += "switch_up_on";
        }
        else {
          path += "switch_up_off";
        }
        break;
      case 196:
        if (this.toggle) {
          path += "switch_down_on";
        }
        else {
          path += "switch_down_off";
        }
        break;
      case 197:
        if (this.toggle) {
          path += "switch_left_on";
        }
        else {
          path += "switch_left_off";
        }
        break;
      case 198:
        if (this.toggle) {
          path += "switch_right_on";
        }
        else {
          path += "switch_right_off";
        }
        break;
      case 201:
        path += "steel_cross";
        break;
      case 202:
        path += "mary_statue";
        break;
      case 211:
        path += "wire_fence_";
        break;
      case 212:
        path += "barbed_wire_fence_";
        break;
      case 251:
        path += "parking_bumper_up";
        break;
      case 252:
        path += "parking_bumper_left";
        break;
      case 253:
        path += "gazebo";
        break;
      case 254:
        path += "parking_bumper_down";
        break;
      case 255:
        path += "parking_bumper_right";
        break;
      case 256:
        path += "tent_green_down";
        break;
      case 257:
        path += "tent_green_right";
        break;
      case 258:
        if (this.toggle) {
          path += "tent_brown_down";
        }
        else {
          path += "tent_brown_right";
        }
        break;
      case 259:
        path += "tent_gray_right";
        break;
      case 260:
        path += "tent_gray_down";
        break;
      case 261:
        path += "campfire";
        break;
      case 301:
        path = "terrain/brickWall_blue";
        break;
      case 302:
        path = "terrain/brickWall_gray";
        break;
      case 303:
        path = "terrain/brickWall_green";
        break;
      case 304:
        path = "terrain/brickWall_pink";
        break;
      case 305:
        path = "terrain/brickWall_red";
        break;
      case 306:
        path = "terrain/brickWall_yellow";
        break;
      case 307:
        path = "terrain/brickWall_white";
        break;
      case 311:
        path += "pillar_gray";
        break;
      case 312:
        path += "pillar_red";
        break;
      case 313:
        path += "windowpane_brick_white";
        break;
      case 314:
        path += "windowpane_brick_blue";
        break;
      case 315:
        path += "windowpane_brick_gray";
        break;
      case 316:
        path += "windowpane_brick_green";
        break;
      case 317:
        path += "windowpane_brick_pink";
        break;
      case 318:
        path += "windowpane_brick_red";
        break;
      case 319:
        path += "windowpane_brick_yellow";
        break;
      case 320:
        path += "windowpane_brick_brown";
        break;
      case 321:
        path += "window_brick_white";
        break;
      case 322:
        path += "window_brick_blue";
        break;
      case 323:
        path += "window_brick_gray";
        break;
      case 324:
        path += "window_brick_green";
        break;
      case 325:
        path += "window_brick_pink";
        break;
      case 326:
        path += "window_brick_red";
        break;
      case 327:
        path += "window_brick_yellow";
        break;
      case 328:
        path += "window_brick_brown";
        break;
      case 331:
        path += "door_open_up_lefthinges";
        break;
      case 332:
        path += "door_open_up_righthinges";
        break;
      case 333:
        path += "door_open_left_uphinges";
        break;
      case 334:
        path += "door_open_left_downhinges";
        break;
      case 335:
        path += "door_open_diagonalleft_uphinges";
        break;
      case 336:
        path += "door_open_diagonalleft_downhinges";
        break;
      case 337:
        path += "door_open_diagonalright_uphinges";
        break;
      case 338:
        path += "door_open_diagonalright_downhinges";
        break;
      case 339:
        if (this.toggle) {
          path += "door_closed_up";
        }
        else {
          path += "door_closed_up2";
        }
        break;
      case 340:
        if (this.toggle) {
          path += "door_closed_left";
        }
        else {
          path += "door_closed_left2";
        }
        break;
      case 341:
        path += "door_closed_diagonalleft";
        break;
      case 342:
        path += "door_closed_diagonalright";
        break;
      case 343:
        if (this.toggle) {
          path += "door_locked_up";
        }
        else {
          path += "door_locked_up2";
        }
        break;
      case 344:
        if (this.toggle) {
          path += "door_locked_left";
        }
        else {
          path += "door_locked_left2";
        }
        break;
      case 345:
        path += "door_locked_diagonalleft";
        break;
      case 346:
        path += "door_locked_diagonalright";
        break;
      case 351:
        path += "steeldoor_open_up_lefthinges";
        break;
      case 352:
        path += "steeldoor_open_up_righthinges";
        break;
      case 353:
        path += "steeldoor_open_left_uphinges";
        break;
      case 354:
        path += "steeldoor_open_left_downhinges";
        break;
      case 355:
        path += "steeldoor_open_diagonalleft_uphinges";
        break;
      case 356:
        path += "steeldoor_open_diagonalleft_downhinges";
        break;
      case 357:
        path += "steeldoor_open_diagonalright_uphinges";
        break;
      case 358:
        path += "steeldoor_open_diagonalright_downhinges";
        break;
      case 359:
        if (this.toggle) {
          path += "steeldoor_closed_up";
        }
        else {
          path += "steeldoor_closed_up2";
        }
        break;
      case 360:
        if (this.toggle) {
          path += "steeldoor_closed_left";
        }
        else {
          path += "steeldoor_closed_left2";
        }
        break;
      case 361:
        path += "steeldoor_closed_diagonalleft";
        break;
      case 362:
        path += "steeldoor_closed_diagonalright";
        break;
      case 363:
        if (this.toggle) {
          path += "steeldoor_locked_up";
        }
        else {
          path += "steeldoor_locked_up2";
        }
        break;
      case 364:
        if (this.toggle) {
          path += "steeldoor_locked_left";
        }
        else {
          path += "steeldoor_locked_left2";
        }
        break;
      case 365:
        path += "steeldoor_locked_diagonalleft";
        break;
      case 366:
        path += "steeldoor_locked_diagonalright";
        break;
      case 401:
        if (this.number == 0) {
          path = "transparent";
        }
        else {
          path += "dandelion" + this.number + "";
        }
        break;
      case 411:
        path += "gravel_pebbles";
        break;
      case 412:
        path += "gravel_rocks";
        break;
      case 413:
        path += "ivy";
        break;
      case 421:
        path += "tree_maple_small" + this.number2 + "";
        break;
      case 422:
        path += "tree_walnut_small" + this.number2 + "";
        break;
      case 423:
        path += "tree_cedar_small" + this.number2 + "";
        break;
      case 424:
        path += "tree_dead_small" + this.number2 + "";
        break;
      case 425:
        path += "tree_oak_small" + this.number2 + "";
        break;
      case 426:
        path += "tree_pine_small" + this.number2 + "";
        break;
      case 444:
        path += "tree_maple" + this.number2 + "";
        break;
      case 445:
        path += "tree_walnut" + this.number2 + "";
        break;
      case 446:
        path += "tree_cedar" + this.number2 + "";
        break;
      case 447:
        path += "tree_dead" + this.number2 + "";
        break;
      case 448:
        path += "tree_oak" + this.number2 + "";
        break;
      case 449:
        path += "tree_pine" + this.number2 + "";
        break;
      case 431:
        path += "rock";
        break;
      case 440:
        path += "underbrush" + this.number2 + "";
        break;
      case 441:
        path += "bush_light";
        break;
      case 442:
        path += "bush_dark";
        break;
      case 443:
        path += "bush_green";
        break;
      case 451:
        if (this.number == 0) {
          path = "transparent";
        }
        else {
          path += "wapato" + this.number + "";
        }
        break;
      case 452:
        if (this.number == 0) {
          path = "transparent";
        }
        else {
          path += "leek" + this.number + "";
        }
        break;
      case 453:
        if (this.number == 0) {
          path = "transparent";
        }
        else {
          path += "ryegrass" + this.number + "";
        }
        break;
      case 454:
        if (this.number == 0) {
          path = "transparent";
        }
        else {
          path += "barnyard_grass" + this.number + "";
        }
        break;
      case 455:
        if (this.number == 0) {
          path = "transparent";
        }
        else {
          path += "broadleaf_plantain" + this.number + "";
        }
        break;
      case 456:
        if (this.number == 0) {
          path = "transparent";
        }
        else {
          path += "stinging_nettle" + this.number + "";
        }
        break;
      case 457:
        path += "sapling_maple";
        break;
      case 458:
        path += "sapling_walnut";
        break;
      case 459:
        path += "sapling_cedar";
        break;
      case 460:
        path += "sapling_oak";
        break;
      case 461:
        path += "sapling_pine";
        break;
      case 462:
        path += "sprout_maple";
        break;
      case 463:
        path += "sprout_walnut";
        break;
      case 464:
        path += "sprout_cedar";
        break;
      case 465:
        path += "sprout_oak";
        break;
      case 466:
        path += "sprout_pine";
        break;
      case 501:
        path += "car_hondacrv";
        break;
      case 502:
        path += "car_fordf150";
        break;
      case 503:
        path += "car_vwjetta";
        break;
      case 504:
        path += "car_vwbug";
        break;
      case 505:
        path += "car_lamborghini";
        break;
      case 506:
        path += "car_hondacrv";
        break;
      case 511:
        path += "helicopter_civilian";
        break;
      case 512:
        path += "helicopter_medical";
        break;
      case 513:
        path += "helicopter_military";
        break;
      default:
        p.global.errorMessage("ERROR: Feature ID " + ID + " not found.");
        path += "default";
        break;
    }
    if (this.isFence()) {
      path += this.adjacent_context.filestring;
    }
    if (piece_index > -1) {
      path += "_piece" + piece_index;
    }
    path += ".png";
    return path;
  }

  PImage getImage() {
    return this.getImage(false);
  }
  PImage getImage(boolean blocks_player_view) {
    this.blocking_view = blocks_player_view;
    String path = this.getImagePath();
    if (blocks_player_view) {
      return p.global.images.getTransparentImage(path);
    }
    return p.global.images.getImage(path);
  }
  PImage getScaledImage(int scaled_width) {
    return this.getScaledImage(false, scaled_width);
  }
  PImage getScaledImage(boolean blocks_player_view, int scaled_width) {
    return this.getScaledImage(blocks_player_view, scaled_width, -1);
  }
  PImage getScaledImage(boolean blocks_player_view, int scaled_width, int piece_index) {
    this.blocking_view = blocks_player_view;
    String path = this.getImagePath(piece_index);
    if (blocks_player_view) {
      return p.global.images.getScaledTransparentImage(path, scaled_width);
    }
    return p.global.images.getScaledImage(path, scaled_width);
  }

  // TODO: This breaks images and what-not so need to remove and/or refactor
  boolean ignoreSquare(int i, int j) { // squares not part of feature
    return false;
  }

  boolean isFence() {
    switch(this.ID) {
      case 211: // Wire Fence
      case 212: // Barbed Wire Fence
        return true;
      default:
        return false;
    }
  }


  static int featurePlacingOffsetX(int feature_id) {
    switch(feature_id) {
      case 133: // bed, left
        return -2;
      default:
        return 0;
    }
  }
  
  static int featurePlacingOffsetY(int feature_id) {
    switch(feature_id) {
      case 131: // bed, up
        return -2;
      default:
        return 0;
    }
  }

  // TODO: This inputs a float coordinate not 2 ints
  // TODO: This affects offset function below
  double featureHeight(int i, int j) {
    switch(this.ID) {
      case 425: // Tree, oak
      case 444: // Tree large, maple
      case 445: // Tree large, walnut
      case 446: // Tree large, cedar
      case 447: // Tree large, dead
      case 449: // Tree large, pine
        if (i != 1 || j != 1) {
          return 0;
        }
        break;
      case 448: // Tree oak, large
        if (i == 0 || i == 2 || j == 0 || j == 2) {
          return 0;
        }
        break;
      default:
        break;
    }
    return this.sizeZ;
  }

  boolean blocksPlayerView(IntegerCoordinate player_grid,
    Coordinate player_coordinate, double player_height) {
    IntegerCoordinate dif_coordinate = this.gridLocation().subtractR(player_grid);
    Coordinate relative_coordinate = player_coordinate.subtractR(this.gridLocation());
    if (dif_coordinate.x < 1 - this.sizeX || dif_coordinate.y < 1 - this.sizeY) {
      return false;
    }
    if (Math.abs(dif_coordinate.x - dif_coordinate.y) > 2) {
      return false;
    }
    if (player_height >= this.terrainImageHeightOverflow(relative_coordinate) - 1) {
      return false;
    }
    if (Math.max(dif_coordinate.x, dif_coordinate.y) - 1 >
      Math.ceil(this.terrainImageHeightOverflow(relative_coordinate) * 0.5)) {
      return false;
    }
    return true;
  }

  int terrainImageHeightOverflow() {
    return this.terrainImageHeightOverflow(null);
  }
  // TODO: Add an adjust height function like in terrain (for both collision logic and mouse on logic)
  int terrainImageHeightOverflow(Coordinate relative_coordinate) {
    switch(this.ID) {
      case 11: // Khalil
        return 3;
      case 12: // Chuck Quizmo
        return 2;
      case 21: // Workbench
        return 3;
      case 22: // Ender Chest
        return 3;
      case 23: // Wooden Box
        return 2;
      case 24: // Wooden Crate
      case 25: // Wooden Chest
      case 26: // Large Wooden Chest
      case 27:
        return 3;
      case 101: // Table
        return 3;
      case 102: // Desk
      case 103:
      case 104:
      case 105:
        return 4;
      case 106:
      case 107: // Table, small
        return 3;
      case 108: // Ping Pong Table
        return 3;
      case 109: // Table, low
        return 2;
      case 110: // Table, end
        return 3;
      case 111: // Wooden Chair
      case 112:
      case 113:
      case 114:
        return 3;
      case 115: // Coordinator Chair
        return 3;
      case 116: // Table, end, low
        return 2;
      case 117: // Green Chair
        return 3;
      case 125: // Bench, small
      case 126:
      case 129:
      case 130:
        return 5;
      case 121: // Couch
      case 122:
      case 123:
      case 124:
        return 4;
      case 131: // Bed
      case 132:
      case 133:
      case 134:
        return 3;
      case 141: // Wardrobe
      case 142:
      case 143:
      case 144:
        return 5;
      case 151: // Sign, wooden
      case 152:
        return 4;
      case 160: // Water Fountain
      case 161:
        return 5;
      case 162: // Sink
        return 5;
      case 164: // Urinal
        return 3;
      case 165: // Toilet
        return 2;
      case 171: // Stove
        return 4;
      case 172: // Vending Machine
      case 173:
        return 6;
      case 174: // Minifridge
        return 3;
      case 175: // Fridge
        return 7;
      case 176: // Washer
      case 177: // Dryer
        return 3;
      case 178: // Microwave
        return 1;
      case 179: // TV
        return 2;
      case 180: // Lamp
        return 3;
      case 181: // Garbage Can
      case 182: // Recycle Can
        return 3;
      case 183: // Crate
      case 184: // Cardboard Box
        return 2;
      case 185: // Pickle Jar
        return 1;
      case 195: // Light switch
      case 196:
      case 197:
      case 198:
        return 3;
      case 211: // Wire Fence
        return 4;
      case 212: // Barbed Wire Fence
        return 5;
      case 256: // Tent, green, down
      case 257: // Tent, green, right
        return 5;
      case 258: // Tent, brown
        return 2;
      case 259: // Tent, gray, down
      case 260: // Tent, gray, right
        return 5;
      case 301: // Movable Brick Wall
      case 302:
      case 303:
      case 304:
      case 305:
      case 306:
      case 307:
        return 6;
      case 311: // Pillar
      case 312:
        return 6;
      case 313: // Window Pane, brick
      case 314:
      case 315:
      case 316:
      case 317:
      case 318:
      case 319:
      case 320:
        return 6;
      case 321: // Window, brick
      case 322:
      case 323:
      case 324:
      case 325:
      case 326:
      case 327:
      case 328:
        return 6;
      case 331: // Wooden Door
      case 332:
      case 333:
      case 334:
      case 335:
      case 336:
      case 337:
      case 338:
      case 339:
      case 340:
      case 341:
      case 342:
      case 343:
      case 344:
      case 345:
      case 346:
      case 351: // Steel Door
      case 352:
      case 353:
      case 354:
      case 355:
      case 356:
      case 357:
      case 358:
      case 359:
      case 360:
      case 361:
      case 362:
      case 363:
      case 364:
      case 365:
      case 366:
        return 5;
      case 401: // Dandelion
        return 1;
      case 413: // Ivy
        return 3;
      case 421: // Tree, small, maple
      case 422: // Tree, small, walnut
      case 423: // Tree, small, cedar
      case 424: // Tree, small, dead
      case 426: // Tree, small, pine
        return 6;
      case 425: // Tree, small, oak
        return 8;
      case 431: // Rock 1x1
        return 3;
      case 440: // Underbrush
      case 441: // Bush
      case 442:
      case 443:
        return 3;
      case 444: // Tree, large, maple
      case 445: // Tree, large, walnut
      case 446: // Tree, large, cedar
      case 447: // Tree, large, dead
      case 449: // Tree, large, pine
        return 8;
      case 448: // Tree, large, oak
        return 12;
      case 451: // Wapato
        switch(this.number) {
          case 1:
            return 0;
          case 2:
            return 2;
          default:
            return 3;
        }
      case 452: // Leek
        switch(this.number) {
          case 1:
            return 1;
          default:
            return 3;
        }
      case 455: // Broadleaf Plantain
        switch(this.number) {
          case 1:
            return 1;
          default:
            return 3;
        }
      case 456: // Stinging Nettle
        switch(this.number) {
          case 1:
            return 0;
          case 2:
            return 1;
          default:
            return 3;
        }
      case 457: // Saplings
      case 458:
      case 459:
      case 460:
      case 461:
        return 3;
      case 462: // Sprouts
      case 463:
      case 464:
      case 465:
      case 466:
        return 1;
      case 501: // Honda CRV
      case 502: // Ford F150
      case 503: // VW Jetta
      case 504: // VW Bug
      case 505: // Lamborghini
      case 506: // Honda CRV (broken down)
        return 5;
      default:
        return 0;
    }
  }

  double terrainSpeedSlow(int i, int j) {
    switch(this.ID) {
      case 421: // Trees
      case 422:
      case 423:
      case 424:
      case 425:
      case 426:
      case 444:
      case 445:
      case 446:
      case 447:
      case 448:
      case 449:
        return -0.4;
      case 440: // Underbrush
      case 441: // Bushes
      case 442:
      case 443:
        return -0.6;
      default:
        break;
    }
    return 0;
  }

  // True if when feature is added to map it won't stack on other features
  boolean ignoresOtherFeatureHeights() {
    switch(this.ID) {
      case 413: // Ivy
        return true;
      default:
        return false;
    }
  }


  @Override
  void mouseMove(double mX, double mY) {
    super.mouseMove(mX, mY);
    if (!this.hovered) {
      return;
    }
    int i = LNZApplet.round(Math.floor(mX - this.xi()));
    int j = LNZApplet.round(Math.floor(mY - this.yi()));
    if (this.ignoreSquare(i, j)) {
      this.hovered = false;
    }
  }


  boolean car() {
    return this.type.equals("Car");
  }

  boolean tilledPlant() {
    switch(this.ID) {
      case 401: // Dandelion
      case 451: // Wapato
      case 452: // Leek
      case 453: // Ryegrass
      case 454: // Barnyard Grass
      case 455: // Broadleaf Plantain
      case 456: // Stinging Nettle
      case 457: // Sapling, maple
      case 458: // Sapling, walnut
      case 459: // Sapling, cedar
      case 460: // Sapling, oak
      case 461: // Sapling, pine
      case 462: // Sprout, maple
      case 463: // Sprout, walnut
      case 464: // Sprout, cedar
      case 465: // Sprout, oak
      case 466: // Sprout, pine
        return true;
      default:
        return false;
    }
  }

  int gasTankSize() {
    switch(this.ID) {
      case 501: // Honda CRV
      case 502: // Ford F150
      case 503: // VW Jetta
      case 504: // VW Beetle
      case 505: // Lamborghini
        return 150;
      default:
        return 0;
    }
  }


  boolean targetable(Unit u) {
    if (this.targetableByUnit()) {
      return true;
    }
    else if (this.targetableByHeroOnly() && Hero.class.isInstance(u)) {
      return true;
    }
    return false;
  }

  boolean targetableByUnit() {
    switch(this.ID) {
      case 101: // wooden table
      case 102: // desk
      case 103:
      case 104:
      case 105:
      case 106: // small wooden table
      case 107:
      case 108: // ping pong table
      case 109: // wooden table, low
      case 110: // wooden table, end
      case 111: // wooden chair
      case 112:
      case 113:
      case 114:
      case 115: // Coordinator Chair
      case 116: // Wooden Table, end, low
      case 117: // Green Chair
      case 121: // Couch
      case 122:
      case 123:
      case 124:
      case 125: // Wooden Bench
      case 126:
      case 127:
      case 128:
      case 129:
      case 130:
      case 131: // Bed
      case 132:
      case 133:
      case 134:
      case 141: // wardrobe
      case 142:
      case 143:
      case 144:
      case 160: // water fountain
      case 161:
      case 162: // sink
      case 163: // shower stall
      case 164: // urinal
      case 165: // toilet
      case 185: // Pickle Jar
      case 211: // Fence
      case 212:
      case 321: // Window, brick
      case 322:
      case 323:
      case 324:
      case 325:
      case 326:
      case 327:
      case 328:
      case 331: // wooden door (open)
      case 332:
      case 333:
      case 334:
      case 335:
      case 336:
      case 337:
      case 338:
      case 339: // wooden door (closed)
      case 340:
      case 341:
      case 342:
      case 343: // wooden door (locked)
      case 344:
      case 345:
      case 346:
      case 351: // steel door (open)
      case 352:
      case 353:
      case 354:
      case 355:
      case 356:
      case 357:
      case 358:
      case 359: // steel door (closed)
      case 360:
      case 361:
      case 362:
      case 363: // steel door (locked)
      case 364:
      case 365:
      case 366:
      case 401: // dandelion
      case 411: // gravel
      case 412:
      case 413: // Ivy
      case 421: // Tree, small
      case 422:
      case 423:
      case 424:
      case 425:
      case 426:
      case 440: // Underbrush
      case 441: // Bush
      case 442:
      case 443:
      case 444: // Tree, large
      case 445:
      case 446:
      case 447:
      case 448:
      case 449:
      case 451: // Growable Plants
      case 452:
      case 453:
      case 454:
      case 455:
      case 456:
      case 457:
      case 458:
      case 459:
      case 460:
      case 461:
      case 462:
      case 463:
      case 464:
      case 465:
      case 466:
        return true;
      default:
        return false;
    }
  }

  boolean targetableByHeroOnly() {
    switch(this.ID) {
      case 11: // khalil
      case 12: // chuck quizmo
      case 21: // workbench
      case 22: // ender chest
      case 23: // wooden box
      case 24: // wooden crate
      case 25: // wooden chest
      case 26: // large wooden chest
      case 27:
      case 151: // sign
      case 152:
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
      case 171: // stove
      case 172: // vending machine
      case 173:
      case 174: // minifridge
      case 175: // refridgerator
      case 176: // washer
      case 177: // dryer
      case 178: // microwave
      case 180: // lamp
      case 181: // garbage can
      case 182: // recycle can
      case 183: // crate
      case 184: // cardboard box
      case 195: // light switch
      case 196:
      case 197:
      case 198:
      case 301: // movable brick wall
      case 302:
      case 303:
      case 304:
      case 305:
      case 306:
      case 307:
      case 501: // cars
      case 502:
      case 503:
      case 504:
      case 505:
        return true;
      default:
        return false;
    }
  }


  double interactionDistance() {
    switch(this.ID) {
      default:
        return LNZ.feature_defaultInteractionDistance;
    }
  }


  boolean onInteractionCooldown() {
    switch(this.ID) {
      case 151: // sign
      case 152:
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
      case 163: // shower stall
      case 164: // urinal
      case 165: // toilet
      case 185: // pickle jar
        if (this.number > 0) {
          return true;
        }
        break;
      default:
        return false;
    }
    return false;
  }


  double interactionTime(AbstractGameMap map, Unit u) {
    if (u == null) {
      return 0;
    }
    switch(this.ID) {
      case 101: // Wooden Table
      case 106: // Small Wooden Table
      case 107:
      case 108: // Ping Pong Table
      case 109: // Wooden Table, low
      case 110: // Wooden Table, end
      case 111: // Wooden Chair
      case 112:
      case 113:
      case 114:
      case 115: // Coordinator Chair
      case 116: // Wooden Table, end, low
      case 117: // Green Chair
      case 121: // Couch
      case 122:
      case 123:
      case 124:
      case 131: // Bed
      case 132:
      case 133:
      case 134:
      case 141: // Wardrobe
      case 142:
      case 143:
      case 144:
        return LNZ.feature_furnitureInteractionTime;
      case 211: // Wire Fence
      case 212:
        return LNZ.feature_wireFenceInteractionTime;
      case 301: // Movable Brick Wall
      case 302:
      case 303:
      case 304:
      case 305:
      case 306:
      case 307:
        return LNZ.feature_movableBrickWallInteractionTime;
      case 411: // Gravel
      case 412:
        return LNZ.feature_gravelInteractionTime;
      case 421: // Tree, small
      case 422:
      case 423:
      case 424:
      case 425:
      case 426:
      case 444: // Tree, large
      case 445:
      case 446:
      case 447:
      case 448:
      case 449:
        return LNZ.feature_treeInteractionTime;
      case 440: // Underbrush
      case 441: // Bush
      case 442:
      case 443:
        return LNZ.feature_bushInteractionTime;
      case 451: // Plantable
      case 452:
      case 453:
      case 454:
      case 455:
      case 456:
        if (u.weapon() != null && u.weapon().waterBottle()) {
          p.global.sounds.trigger_units("items/water_plant",
            u.xCenter() - map.view.x, u.yCenter() - map.view.y);
          return 500;
        }
        return 0;
      default:
        return 0;
    }
  }


  ArrayList<Integer> drops() {
    ArrayList<Integer> id_list = new ArrayList<Integer>();
    switch(this.ID) {
      case 21: // workbench
        id_list.add(2167);
        break;
      case 23: // Wooden Box
        id_list.add(2169);
        break;
      case 24: // Wooden Crate
        id_list.add(2170);
        break;
      case 25: // Wooden Chest
        id_list.add(2171);
        break;
      case 26: // Large Wooden Chest
      case 27:
        id_list.add(2172);
        break;
      case 101: // wooden table
      case 102: // desk
      case 103:
      case 104:
      case 105:
      case 108: // ping pong table
      case 109: // wooden table, low
        id_list.add(2816);
        id_list.add(2816);
        id_list.add(2818);
        id_list.add(2818);
        id_list.add(2214);
        id_list.add(2985);
        break;
      case 106: // small wooden table
      case 107:
        id_list.add(2816);
        id_list.add(2818);
        id_list.add(2818);
        id_list.add(2214);
        id_list.add(2985);
        break;
      case 110: // wooden table, end
      case 111: // wooden chair
      case 112:
      case 113:
      case 114:
      case 116: // wooden table, end, low
        id_list.add(2818);
        id_list.add(2818);
        id_list.add(2985);
        break;
      case 125: // wooden bench, small
      case 126:
      case 129:
      case 130:
        id_list.add(2816);
        id_list.add(2816);
        id_list.add(2816);
        id_list.add(2214);
        id_list.add(2985);
        break;
      case 127: // wooden bench, large
      case 128:
        id_list.add(2816);
        id_list.add(2816);
        id_list.add(2816);
        id_list.add(2816);
        id_list.add(2214);
        id_list.add(2214);
        id_list.add(2985);
        id_list.add(2985);
        break;
      case 115: // Coordinator Chair
      case 117: // Green Chair
        id_list.add(2818);
        id_list.add(2818);
        id_list.add(2986);
        id_list.add(2819);
        break;
      case 121: // couch
      case 122:
      case 123:
      case 124:
        id_list.add(2816);
        id_list.add(2818);
        id_list.add(2986);
        id_list.add(2819);
        id_list.add(2819);
        break;
      case 131: // bed
      case 132:
      case 133:
      case 134:
        id_list.add(2168);
        break;
      case 141: // wardrobe
      case 142:
      case 143:
      case 144:
        id_list.add(2816);
        id_list.add(2816);
        id_list.add(2818);
        id_list.add(2818);
        id_list.add(2986);
        break;
      case 185: // pickle jar
        id_list.add(2805);
        break;
      case 211: // wire fence
      case 212:
        id_list.add(2806);
        break;
      case 321: // Window, brick
      case 322:
      case 323:
      case 324:
      case 325:
      case 326:
      case 327:
      case 328:
        id_list.add(2805);
        id_list.add(2805);
        break;
      case 331: // wooden door
      case 332:
      case 333:
      case 334:
      case 335:
      case 336:
      case 337:
      case 338:
      case 339:
      case 340:
      case 341:
      case 342:
      case 343:
      case 344:
      case 345:
      case 346:
        id_list.add(2816);
        id_list.add(2816);
        id_list.add(2816);
        id_list.add(2816);
        id_list.add(2214);
        id_list.add(2214);
        id_list.add(2985);
        id_list.add(2985);
        break;
      case 413: // Ivy
        id_list.add(5801);
        break;
      case 421: // Trees
      case 422:
      case 423:
      case 424:
      case 426:
        id_list.add(2969);
        id_list.add(2969);
        for (int i = 0; i < Misc.randomInt(0, 2); i++) {
          id_list.add(Tree.branchId(this.ID));
        }
        if (Tree.species(this.ID) != Tree.DEAD) {
          for (int i = 0; i < Misc.randomInt(0, 2); i++) {
            id_list.add(Tree.fruitId(this.ID));
          }
        }
        break;
      case 425: // Tree, Oak
      case 444: // Large Trees
      case 445:
      case 446:
      case 447:
      case 449:
        id_list.add(2969);
        id_list.add(2969);
        id_list.add(2969);
        for (int i = 0; i < Misc.randomInt(1, 3); i++) {
          id_list.add(Tree.branchId(this.ID));
        }
        if (Tree.species(this.ID) != Tree.DEAD) {
          for (int i = 0; i < Misc.randomInt(0, 2); i++) {
            id_list.add(Tree.fruitId(this.ID));
          }
        }
        break;
      case 448: // Large Tree, Oak
        id_list.add(2969);
        id_list.add(2969);
        id_list.add(2969);
        id_list.add(2969);
        for (int i = 0; i < Misc.randomInt(1, 3); i++) {
          id_list.add(Tree.branchId(this.ID));
        }
        if (Tree.species(this.ID) != Tree.DEAD) {
          for (int i = 0; i < Misc.randomInt(0, 2); i++) {
            id_list.add(Tree.fruitId(this.ID));
          }
        }
        break;
      case 451: // Wapato
        if (this.number == 4) {
          id_list.add(2002);
          id_list.add(2001);
          id_list.add(2001);
        }
        else if (this.number == 3) {
          if (Misc.randomChance(0.8)) {
            id_list.add(2002);
          }
        }
        break;
      case 452: // Leek
        if (this.number == 4) {
          id_list.add(2004);
          id_list.add(2003);
          if (Misc.randomChance(0.8)) {
            id_list.add(2003);
          }
        }
        else if (this.number == 3) {
          id_list.add(2004);
        }
        break;
      case 453: // Ryegrass
        if (this.number == 4) {
          id_list.add(2007);
          if (Misc.randomChance(0.7)) {
            id_list.add(2007);
          }
          if (Misc.randomChance(0.3)) {
            id_list.add(2007);
          }
        }
        else if (this.number == 3) {
          if (Misc.randomChance(0.5)) {
            id_list.add(2007);
          }
        }
        break;
      case 454: // Barnyard Grass
        if (this.number == 4) {
          id_list.add(2008);
          if (Misc.randomChance(0.7)) {
            id_list.add(2008);
          }
          if (Misc.randomChance(0.3)) {
            id_list.add(2008);
          }
        }
        else if (this.number == 3) {
          if (Misc.randomChance(0.5)) {
            id_list.add(2008);
          }
        }
        break;
      case 455: // Broadleaf Plantain
        if (this.number == 4) {
          id_list.add(2006);
          if (Misc.randomChance(0.5)) {
            id_list.add(2006);
          }
          id_list.add(2005);
          if (Misc.randomChance(0.7)) {
            id_list.add(2005);
          }
          if (Misc.randomChance(0.3)) {
            id_list.add(2005);
          }
        }
        else if (this.number == 3) {
          id_list.add(2006);
          if (Misc.randomChance(0.5)) {
            id_list.add(2006);
          }
          if (Misc.randomChance(0.3)) {
            id_list.add(2005);
          }
        }
        else if (this.number == 2) {
          if (Misc.randomChance(0.4)) {
            id_list.add(2006);
          }
        }
        break;
      case 456: // Stinging Nettle
        if (this.number == 4) {
          id_list.add(2010);
          if (Misc.randomChance(0.5)) {
            id_list.add(2010);
          }
          id_list.add(2009);
          if (Misc.randomChance(0.7)) {
            id_list.add(2009);
          }
          if (Misc.randomChance(0.3)) {
            id_list.add(2009);
          }
        }
        else if (this.number == 3) {
          id_list.add(2010);
          if (Misc.randomChance(0.5)) {
            id_list.add(2010);
          }
          if (Misc.randomChance(0.3)) {
            id_list.add(2009);
          }
        }
        else if (this.number == 2) {
          if (Misc.randomChance(0.5)) {
            id_list.add(2010);
          }
        }
        break;
      case 457: // Sapling, maple
        if (Misc.randomChance(0.5)) {
          id_list.add(2965);
        }
        break;
      case 458: // Sapling, walnut
        if (Misc.randomChance(0.5)) {
          id_list.add(2966);
        }
        break;
      case 459: // Sapling, cedar
        if (Misc.randomChance(0.5)) {
          id_list.add(2967);
        }
        break;
      case 460: // Sapling, oak
        if (Misc.randomChance(0.5)) {
          id_list.add(2960);
        }
        break;
      case 461: // Sapling, pine
        if (Misc.randomChance(0.5)) {
          id_list.add(2968);
        }
        break;
      default:
        break;
    }
    return id_list;
  }


  double lightPercentageBlocked() {
    switch(this.ID) {
      case 172: // vending machine
      case 173: // vending machine
      case 175: // fridge
      case 201: // steel cross
      case 301: // movable brick wall
      case 302:
      case 303:
      case 304:
      case 305:
      case 306:
      case 307:
      case 311: // pillar
      case 312: // pillar
      case 359: // steel door (closed)
      case 360:
      case 361:
      case 362:
      case 363: // steel door (locked)
      case 364:
      case 365:
      case 366:
        return 1;
      case 339: // wooden door (closed)
      case 340:
      case 341:
      case 342:
      case 343: // wooden door (locked)
      case 344:
      case 345:
      case 346:
        return 0.9;
      case 321: // Window, brick
      case 322:
      case 323:
      case 324:
      case 325:
      case 326:
      case 327:
      case 328:
        return 0.05;
      case 413: // Ivy
        return 0.15;
      case 421: // Tree
      case 422:
      case 423:
      case 424:
      case 425:
      case 426:
        return 0.3;
      case 431: // Rock
        return 0.7;
      case 440: // Underbrush
      case 441: // Bush
      case 442:
      case 443:
        return 0.5;
      case 444: // Large Tree
      case 445:
      case 446:
      case 447:
      case 448:
      case 449:
        return 0.3;
      default:
        return 0;
    }
  }


  void destroy(AbstractGameMap map) {
    map.removeFeature(this.map_key);
    for (int id : this.drops()) {
      map.addItem(new Item(p, id, this.randomLocationUnderFeature()));
    }
    if (this.inventory != null) {
      for (Item i : this.inventory.items()) {
        map.addItem(new Item(p, i, this.randomLocationUnderFeature()));
      }
    }
    if (this.items != null) {
      for (Item i : this.items) {
        map.addItem(new Item(p, i, this.randomLocationUnderFeature()));
      }
    }
    // id-specific logic
    switch(this.ID) {
      case 12: // chuck quizmo
        map.addVisualEffect(4002, this.xCenter(), this.yCenter());
        break;
      case 421: // Tree (maple)
      case 422: // Tree (walnut)
      case 423: // Tree (cedar)
      case 424: // Tree (dead)
      case 425: // Tree (oak)
      case 426: // Tree (pine)
      case 440: // Underbrush
      case 441: // Bushes
      case 442:
      case 443:
      case 444: // large trees
      case 445:
      case 446:
      case 447:
      case 448:
      case 449:
        for (int i = LNZApplet.round(this.coordinate.x); i < LNZApplet.round(this.coordinate.x) + this.sizeX; i++) {
          for (int j = LNZApplet.round(this.coordinate.y); j < LNZApplet.round(this.coordinate.y) + this.sizeY; j++) {
            GameMapSquare square = map.mapSquare(i, j);
            if (square == null) {
              continue;
            }
            for (Feature f : square.features) {
              if (f == null || f.ID != 413) {
                continue;
              }
              f.destroy(map);
            }
          }
        }
        break;
    }
  }


  void interact(Unit u, AbstractGameMap map) {
    this.interact(u, map, false);
  }
  void interact(Unit u, AbstractGameMap map, boolean use_item) {
    if (Hero.class.isInstance(u)) {
      if (use_item) {
        u.curr_action = UnitAction.HERO_INTERACTING_WITH_FEATURE_WITH_ITEM;
      }
      else {
        u.curr_action = UnitAction.HERO_INTERACTING_WITH_FEATURE;
      }
      return;
    }
    Feature new_f;
    Item weapon = u.weapon();
    Item new_i;
    // Non-hero interaction with feature
    switch(this.ID) {
      case 21: // workbench
      case 101: // wooden table
      case 102: // wooden desk
      case 103:
      case 104:
      case 105:
      case 106: // small wooden table
      case 107:
      case 108: // ping pong table
      case 109: // wooden table, low
      case 110: // wooden table, end
      case 111: // wooden chair
      case 112:
      case 113:
      case 114:
      case 115: // coordinator chair
      case 116: // wooden table, end, low
      case 117: // Green Chair
      case 121: // couch
      case 122:
      case 123:
      case 124:
      case 125: // wooden bench
      case 126:
      case 127:
      case 128:
      case 129:
      case 130:
      case 131: // Bed
      case 132:
      case 141: // Wardrobe
      case 142:
      case 143:
      case 144:
        if (!u.holding(2977, 2979, 2980, 2981, 2983)) {
          break;
        }
        switch(weapon.ID) {
          case 2977: // Stone Hatchet
            this.number -= 1;
            p.global.sounds.trigger_units("items/melee/ax",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 2979: // Saw
            this.number -= 1;
            p.global.sounds.trigger_units("items/saw_cut_wood",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 2980: // Drill
            this.number -= 1;
            p.global.sounds.trigger_units("items/melee/drill" + Misc.randomInt(1, 3),
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 2981: // Roundsaw
            this.number -= 2;
            p.global.sounds.trigger_units("items/roundsaw_cut_wood",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 2983: // Chainsaw
            this.number -= 2;
            p.global.sounds.trigger_units("items/chainsaw_long",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
        }
        weapon.lowerDurability();
        if (this.number < 1) {
          this.destroy(map);
        }
        break;
      case 160: // Water Fountain
      case 161:
        p.global.sounds.trigger_environment("features/water_fountain",
          this.xCenter() - map.view.x, this.yCenter() - map.view.y);
        this.timer = 3000;
        this.refresh_map_image = true;
        break;
      case 162: // Sink
        p.global.sounds.trigger_environment("features/sink",
          this.xCenter() - map.view.x, this.yCenter() - map.view.y);
        break;
      case 163: // Shower Stall
        this.number = LNZ.feature_showerStallCooldown;
        this.timer = 3500;
        this.refresh_map_image = true;
        p.global.sounds.trigger_environment("features/shower_stall",
          this.xCenter() - map.view.x, this.yCenter() - map.view.y);
        break;
      case 164: // Urinal
        this.number = LNZ.feature_urinalCooldown;
        p.global.sounds.trigger_environment("features/urinal",
          this.xCenter() - map.view.x, this.yCenter() - map.view.y);
        break;
      case 165: // Toilet
        this.number = LNZ.feature_toiletCooldown;
        p.global.sounds.trigger_environment("features/toilet",
          this.xCenter() - map.view.x, this.yCenter() - map.view.y);
        break;
      case 185: // Pickle Jar
        if (use_item && u.holding(2975)) {
          this.destroy(map);
          p.global.sounds.trigger_environment("items/glass_bottle_hit",
            this.xCenter() - map.view.x, this.yCenter() - map.view.y);
          break;
        }
        if (u.canPickup()) {
          this.number = LNZ.feature_pickleJarCooldown;
          new_i = new Item(p, 2106);
          u.pickup(new_i);
          new_i.pickupSound();
        }
        break;
      case 211: // Wire Fence
        if (use_item && u.holding(2978)) {
          this.destroy(map);
          weapon.lowerDurability();
          p.global.sounds.trigger_environment("items/wire_clipper",
            this.xCenter() - map.view.x, this.yCenter() - map.view.y);
          break;
        }
        else if (u.agility() >= 2) {
          u.setLocation(this.xCenter(), this.yCenter());
          u.curr_height = this.curr_height + this.sizeZ;
          p.global.sounds.trigger_units("features/climb_fence",
            this.xCenter() - map.view.x, this.yCenter() - map.view.y);
          if (Misc.randomChance(0.3)) {
            u.addStatusEffect(StatusEffectCode.BLEEDING, 2000,
              new DamageSource(21, StatusEffectCode.BLEEDING));
          }
        }
        break;
      case 212: // Barbed Wire Fence
        if (u.agility() >= 3) {
          u.setLocation(this.xCenter(), this.yCenter());
          u.curr_height = this.curr_height + this.sizeZ;
          p.global.sounds.trigger_units("features/climb_fence",
            this.xCenter() - map.view.x, this.yCenter() - map.view.y);
          if (Misc.randomChance(0.8)) {
            u.addStatusEffect(StatusEffectCode.BLEEDING, 2500,
              new DamageSource(21, StatusEffectCode.BLEEDING));
          }
        }
        break;
      case 321: // Window, brick
      case 322:
      case 323:
      case 324:
      case 325:
      case 326:
      case 327:
      case 328:
        if (!u.holding(2976)) {
          break;
        }
        this.destroy(map);
        weapon.lowerDurability();
        new_f = new Feature(p, this.ID - 8, this.coordinate.copy(), false);
        map.addFeature(new_f);
        new_f.curr_height = this.curr_height;
        p.global.sounds.trigger_environment("items/window_break",
            this.xCenter() - map.view.x, this.yCenter() - map.view.y);
        break;
      case 331: // wooden door (open)
      case 332:
      case 333:
      case 334:
      case 335:
      case 336:
      case 337:
      case 338:
      case 339: // wooden door (closed)
      case 340:
      case 341:
      case 342:
      case 343: // wooden door (locked)
      case 344:
      case 345:
      case 346:
        if (use_item && u.holding(2977, 2979, 2983)) {
          this.destroy(map);
          weapon.lowerDurability();
          break;
        }
        switch(this.ID) {
          case 331: // door open (up)
            this.remove = true;
            new_f = new Feature(p, 339, this.coordinate.copy(), false);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 332:
            this.remove = true;
            new_f = new Feature(p, 339, this.coordinate.copy(), true);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 333: // door open (left)
            this.remove = true;
            new_f = new Feature(p, 340, this.coordinate.copy(), false);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 334:
            this.remove = true;
            new_f = new Feature(p, 340, this.coordinate.copy(), true);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 335: // door open (diagonal left)
            this.remove = true;
            new_f = new Feature(p, 341, this.coordinate.copy(), false);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 336:
            this.remove = true;
            new_f = new Feature(p, 341, this.coordinate.copy(), true);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 337: // door open (diagonal right)
            this.remove = true;
            new_f = new Feature(p, 342, this.coordinate.copy(), false);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 338:
            this.remove = true;
            new_f = new Feature(p, 342, this.coordinate.copy(), true);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 339: // door closed (up)
            this.remove = true;
            if (this.toggle) {
              new_f = new Feature(p, 332, this.coordinate.copy());
            }
            else {
              new_f = new Feature(p, 331, this.coordinate.copy());
            }
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_open",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 340: // door closed (left)
            this.remove = true;
            if (this.toggle) {
              new_f = new Feature(p, 334, this.coordinate.copy());
            }
            else {
              new_f = new Feature(p, 333, this.coordinate.copy());
            }
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_open",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 341: // door closed (diagonal left)
            this.remove = true;
            if (this.toggle) {
              new_f = new Feature(p, 336, this.coordinate.copy());
            }
            else {
              new_f = new Feature(p, 335, this.coordinate.copy());
            }
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_open",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 342: // door closed (diagonal right)
            this.remove = true;
            if (this.toggle) {
              new_f = new Feature(p, 338, this.coordinate.copy());
            }
            else {
              new_f = new Feature(p, 337, this.coordinate.copy());
            }
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_open",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 343: // door locked (up)
            if (weapon == null || !weapon.unlocks(this.number)) {
              break;
            }
            this.remove = true;
            new_f = new Feature(p, 339, this.coordinate.copy(), this.toggle);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_unlock",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 344: // door locked (left)
            if (weapon == null || !weapon.unlocks(this.number)) {
              break;
            }
            this.remove = true;
            new_f = new Feature(p, 340, this.coordinate.copy(), this.toggle);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_unlock",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 345: // door locked (diagonal left)
            if (weapon == null || !weapon.unlocks(this.number)) {
              break;
            }
            this.remove = true;
            new_f = new Feature(p, 341, this.coordinate.copy(), this.toggle);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_unlock",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 346: // door locked (diagonal right)
            if (weapon == null || !weapon.unlocks(this.number)) {
              break;
            }
            this.remove = true;
            new_f = new Feature(p, 342, this.coordinate.copy(), this.toggle);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_unlock",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
        }
        break;
      case 351: // steel door (open)
      case 352:
      case 353:
      case 354:
      case 355:
      case 356:
      case 357:
      case 358:
      case 359: // steel door (closed)
      case 360:
      case 361:
      case 362:
      case 363: // steel door (locked)
      case 364:
      case 365:
      case 366:
        switch(this.ID) {
          case 351: // door open (up)
            this.remove = true;
            new_f = new Feature(p, 359, this.coordinate.copy(), false);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 352:
            this.remove = true;
            new_f = new Feature(p, 359, this.coordinate.copy(), true);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 353: // door open (left)
            this.remove = true;
            new_f = new Feature(p, 360, this.coordinate.copy(), false);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 354:
            this.remove = true;
            new_f = new Feature(p, 360, this.coordinate.copy(), true);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 355: // door open (diagonal left)
            this.remove = true;
            new_f = new Feature(p, 361, this.coordinate.copy(), false);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 356:
            this.remove = true;
            new_f = new Feature(p, 361, this.coordinate.copy(), true);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 357: // door open (diagonal right)
            this.remove = true;
            new_f = new Feature(p, 362, this.coordinate.copy(), false);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 358:
            this.remove = true;
            new_f = new Feature(p, 362, this.coordinate.copy(), true);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 359: // door closed (up)
            this.remove = true;
            if (this.toggle) {
              new_f = new Feature(p, 352, this.coordinate.copy());
            }
            else {
              new_f = new Feature(p, 351, this.coordinate.copy());
            }
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_open",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 360: // door closed (left)
            this.remove = true;
            if (this.toggle) {
              new_f = new Feature(p, 354, this.coordinate.copy());
            }
            else {
              new_f = new Feature(p, 353, this.coordinate.copy());
            }
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_open",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 361: // door closed (diagonal left)
            this.remove = true;
            if (this.toggle) {
              new_f = new Feature(p, 356, this.coordinate.copy());
            }
            else {
              new_f = new Feature(p, 355, this.coordinate.copy());
            }
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_open",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 362: // door closed (diagonal right)
            this.remove = true;
            if (this.toggle) {
              new_f = new Feature(p, 358, this.coordinate.copy());
            }
            else {
              new_f = new Feature(p, 357, this.coordinate.copy());
            }
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_open",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 363: // door locked (up)
            if (weapon == null || !weapon.unlocks(this.number)) {
              break;
            }
            this.remove = true;
            new_f = new Feature(p, 359, this.coordinate.copy(), this.toggle);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_unlock",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 364: // door locked (left)
            if (weapon == null || !weapon.unlocks(this.number)) {
              break;
            }
            this.remove = true;
            new_f = new Feature(p, 360, this.coordinate.copy(), this.toggle);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_unlock",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 365: // door locked (diagonal left)
            if (weapon == null || !weapon.unlocks(this.number)) {
              break;
            }
            this.remove = true;
            new_f = new Feature(p, 361, this.coordinate.copy(), this.toggle);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_unlock",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
          case 366: // door locked (diagonal right)
            if (weapon == null || !weapon.unlocks(this.number)) {
              break;
            }
            this.remove = true;
            new_f = new Feature(p, 362, this.coordinate.copy(), this.toggle);
            map.addFeature(new_f);
            new_f.curr_height = this.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_unlock",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            break;
        }
        break;
      case 401: // dandelion
        this.remove = true;
        new_i = null;
        if (this.number == 3) {
          new_i = new Item(p, 2961);
        }
        else if (this.number == 4) {
          new_i = new Item(p, 2021);
        }
        if (new_i != null) {
          if (u.canPickup()) {
            u.pickup(new_i);
          }
          else {
            map.addItem(new_i);
          }
          new_i.pickupSound();
        }
        break;
      case 411: // gravel (pebbles)
        if (u.canPickup()) {
          new_i = new Item(p, 2933);
          u.pickup(new_i);
          new_i.pickupSound();
          this.number--;
          if (this.number < 1) {
            this.remove = true;
            map.setTerrain(134, this.gridLocation());
          }
        }
        break;
      case 412: // gravel (rocks)
        if (u.canPickup()) {
          new_i = new Item(p, 2931);
          u.pickup(new_i);
          new_i.pickupSound();
          this.number--;
          if (this.number < 1) {
            this.remove = true;
            map.addFeature(new Feature(p, 411, this.coordinate.copy()));
          }
        }
        break;
      case 413: // Ivy
        this.destroy(map);
        p.global.sounds.trigger_units("features/ivy",
          this.xCenter() - map.view.x, this.yCenter() - map.view.y);
        break;
      case 421: // Tree (maple)
      case 422: // Tree (walnut)
      case 423: // Tree (cedar)
      case 424: // Tree (dead)
      case 425: // Tree (oak)
      case 426: // Tree (pine)
      case 444: // large trees
      case 445:
      case 446:
      case 447:
      case 448:
      case 449:
        int branch_id = Tree.branchId(this.ID);
        if (!use_item || !u.holding(2977, 2979, 2981, 2983)) {
          if (this.toggle) {
            map.addItem(new Item(p, branch_id, u.front()));
            if (Misc.randomChance(LNZ.feature_treeChanceEndBranches)) {
              this.toggle = false;
            }
          }
        }
        else {
          switch(weapon.ID) {
            case 2977: // Stone Hatchet
              this.number -= 1;
              p.global.sounds.trigger_units("items/melee/ax",
                this.xCenter() - map.view.x, this.yCenter() - map.view.y);
              break;
            case 2979: // Saw
              this.number -= 1;
              p.global.sounds.trigger_units("items/saw_cut_wood",
                this.xCenter() - map.view.x, this.yCenter() - map.view.y);
              break;
            case 2981: // Roundsaw
              this.number -= 2;
              p.global.sounds.trigger_units("items/roundsaw_cut_wood",
                this.xCenter() - map.view.x, this.yCenter() - map.view.y);
              break;
            case 2983: // Chainsaw
              this.number -= 4;
              p.global.sounds.trigger_units("items/chainsaw_long",
                this.xCenter() - map.view.x, this.yCenter() - map.view.y);
              break;
          }
          weapon.lowerDurability();
          if (Misc.randomChance(LNZ.feature_treeDropChance)) {
            map.addItem(new Item(p, branch_id, u.front()));
          }
          if (this.number < 1) {
            this.destroy(map);
          }
        }
        break;
      case 440: // Underbrush
        if (use_item) {
          if (u.holding(2204, 2211) || (weapon != null && weapon.ax())) {
            this.number--;
            if (Misc.randomChance(LNZ.feature_bushDropChance)) {
              map.addItem(new Item(p, 2964, this.randomLocationUnderFeature()));
            }
            weapon.lowerDurability();
            if (this.number < 1) {
              this.remove = true;
            }
            if (u.holding(2204, 2211)) {
              p.global.sounds.trigger_units("features/sword_bush",
                this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            }
            else {
              p.global.sounds.trigger_units("items/melee/ax",
                this.xCenter() - map.view.x, this.yCenter() - map.view.y);
            }
          }
        }
        else {
          if (this.toggle) {
            new_i = new Item(p, 2964, u.front());
            map.addItem(new_i);
            new_i.curr_height = this.curr_height;
            p.global.sounds.trigger_units("features/break_branch" + Misc.randomInt(1, 6),
              this.center().subtractR(map.view));
            this.toggle = false;
          }
          else {
            map.addHeaderMessage("No more kindling to gather");
          }
        }
        break;
      case 441: // Bush
      case 442:
      case 443:
        if (use_item) {
          if (u.holding(2204, 2211)) {
            this.number--;
            if (Misc.randomChance(LNZ.feature_bushDropChance)) {
              map.addItem(new Item(p, 2964, this.randomLocationUnderFeature()));
            }
            weapon.lowerDurability();
            if (this.number < 1) {
              this.remove = true;
            }
            p.global.sounds.trigger_units("features/sword_bush",
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
          }
        }
        else {
          if (this.toggle) {
            new_i = new Item(p, 2964, u.front());
            map.addItem(new_i);
            new_i.curr_height = this.curr_height;
            p.global.sounds.trigger_units("features/break_branch" + Misc.randomInt(1, 6),
              this.center().subtractR(map.view));
            this.toggle = false;
          }
          else {
            map.addHeaderMessage("No more kindling to gather");
          }
        }
        break;
      case 451: // Wapato
      case 452: // Leek
      case 453: // Ryegrass
      case 454: // Barnyard Grass
      case 455: // Broadleaf Plantain
        if (weapon != null && weapon.waterBottle()) {
          if (weapon.ammo >= 20) {
            map.waterGround(this.gridLocation());
            weapon.ammo -= 20;
          }
          break;
        }
        if (use_item) {
          this.destroy(map);
          p.global.sounds.trigger_units("features/destroy_plant" + Misc.randomInt(1, 2),
            this.xCenter() - map.view.x, this.yCenter() - map.view.y);
        }
        break;
      case 456: // Stinging Nettle
        if (weapon != null && weapon.waterBottle()) {
          if (weapon.ammo >= 20) {
            map.waterGround(this.gridLocation());
            weapon.ammo -= 20;
          }
          break;
        }
        if (use_item) {
          if (weapon != null && weapon.hoe()) {
            this.destroy(map);
            weapon.lowerDurability();
            p.global.sounds.trigger_units("features/destroy_plant" + Misc.randomInt(1, 2),
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
          }
        }
        break;
      case 457: // Sapling, maple
      case 458: // Sapling, walnut
      case 459: // Sapling, cedar
      case 460: // Sapling, oak
      case 461: // Sapling, pine
      case 462: // Sprout, maple
      case 463: // Sprout, walnut
      case 464: // Sprout, cedar
      case 465: // Sprout, oak
      case 466: // Sprout, pine
        if (use_item) {
          if (weapon != null && (weapon.hoe() || weapon.ax())) {
            this.destroy(map);
            weapon.lowerDurability();
            p.global.sounds.trigger_units("features/destroy_plant" + Misc.randomInt(1, 2),
              this.xCenter() - map.view.x, this.yCenter() - map.view.y);
          }
        }
        break;
      default:
        p.global.errorMessage("ERROR: Unit " + u.displayName() + " trying to " +
          "interact with feature " + this.displayName() + " but no interaction logic found.");
        break;
    }
  }


  String interactionTooltip(Unit u, boolean use_item) {
    switch(this.ID) {
      case 11: // Khalil
        return "Talk";
      case 12: // Chuck Quizmo
        return "Talk";
      case 21: // Workbench
        if (use_item) {
          return "Pickup";
        }
        return "Craft";
      case 22: // Ender Chest
        return "Open";
      case 23: // Wooden Box
      case 24: // Wooden Crate
      case 25: // Wooden Chest
      case 26: // Large Wooden Chest
      case 27:
        if (use_item) {
          return "Pickup";
        }
        return "Open";
      case 101: // Wooden Table
      case 106: // Small Wooden Table
      case 107:
      case 108: // Ping Pong Table
      case 109: // Wooden Table, low
      case 110: // Wooden Table, end
      case 111: // Wooden Chair
      case 112:
      case 113:
      case 114:
      case 116: // Wooden Table, end, low
      case 125: // Wooden Bench
      case 126:
      case 127:
      case 128:
      case 129:
      case 130:
        return "Destroy";
      case 102: // Wooden Desk
      case 103:
      case 104:
      case 105:
        if (use_item) {
          return "Destroy";
        }
        return "Open";
      case 115: // Coordinator Chair
      case 117: // Green Chair
      case 121: // Couch
      case 122:
      case 123:
      case 124:
      case 141: // Wardrobe
      case 142:
      case 143:
      case 144:
        if (use_item) {
          return "Destroy";
        }
        return "Rummage";
      case 131: // Bed
      case 132:
      case 133:
      case 134:
        if (use_item) {
          return "Pickup";
        }
        return "Sleep";
      case 151: // Sign
      case 152:
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
        return "Read";
      case 160: // Water Fountain
      case 161:
      case 162: // Sink
        if (use_item) {
          return "Fill";
        }
        return "Drink";
      case 163: // Shower Stall
        return "Drink";
      case 164: // Urinal
      case 165: // Toilet
        return "Drink (if thirsty)";
      case 171: // Stove
      case 174: // Minifridge
      case 175: // Refridgerator
      case 176: // Washer
      case 177: // Dryer
      case 178: // Microwave
      case 181: // Garbage Can
      case 182: // Recycle Can
      case 183: // Crate
      case 184: // Cardboard Box
        return "Open";
      case 172: // Vending Machine
      case 173:
        return "Purchase";
      case 180: // lamp
        return "Switch";
      case 185: // Pickle Jar
        if (use_item) {
          return "Destroy";
        }
        return "Take pickle";
      case 195: // Light Switch
      case 196:
      case 197:
      case 198:
        return "Switch";
      case 211: // Wire Fence
        if (use_item) {
          return "Destroy";
        }
        return "Climb (agility 2+)";
      case 212: // Barbed Wire Fence
        if (use_item) {
          return "Destroy";
        }
        return "Climb (agility 3+)";
      case 301: // Movable Brick Wall
      case 302:
      case 303:
      case 304:
      case 305:
      case 306:
      case 307:
        return "Push";
      case 321: // Window, brick
      case 322:
      case 323:
      case 324:
      case 325:
      case 326:
      case 327:
      case 328:
        return "Destroy";
      case 331: // wooden door (open)
      case 332:
      case 333:
      case 334:
      case 335:
      case 336:
      case 337:
      case 338:
      case 351: // steel door (open)
      case 352:
      case 353:
      case 354:
      case 355:
      case 356:
      case 357:
      case 358:
        if (use_item) {
          return "Destroy";
        }
        return "Close";
      case 339: // wooden door (closed)
      case 340:
      case 341:
      case 342:
      case 359: // steel door (closed)
      case 360:
      case 361:
      case 362:
        if (use_item) {
          return "Destroy";
        }
        return "Open";
      case 343: // wooden door (locked)
      case 344:
      case 345:
      case 346:
      case 363: // steel door (locked)
      case 364:
      case 365:
      case 366:
        if (use_item) {
          return "Destroy";
        }
        return "Unlock";
      case 401: // dandelion
        if (number == 4) {
          return "Collect Seeds";
        }
        else if (number == 3) {
          return "Pick Flower";
        }
        return "Pick";
      case 411: // gravel (pebbles)
      case 412: // gravel (rocks)
        return "Pickup";
      case 413: // Ivy
        return "Break";
      case 421: // Tree (maple)
      case 422: // Tree (unknown)
      case 423: // Tree (cedar)
      case 424: // Tree (dead)
      case 425: // Tree (large)
      case 426: // Tree (pine)
      case 444: // large trees
      case 445:
      case 446:
      case 447:
      case 448:
      case 449:
        if (use_item) {
          return "Cut Down";
        }
        return "Take Branch";
      case 440: // Underbrush
      case 441: // bush
      case 442:
      case 443:
        if (use_item) {
          return "Cut Down";
        }
        return "Take Kindling";
      case 451: // Wapato
      case 452: // Leek
      case 453: // Ryegrass
      case 454: // Barnyard Grass
      case 455: // Broadleaf Plantain
      case 456: // Stinging Nettle
        if (u != null && !u.remove && u.weapon() != null && u.weapon().waterBottle()) {
          return "Water Plant";
        }
        else if (use_item) {
          return "Harvest Plant";
        }
        return "";
      case 457: // Sapling, maple
      case 458: // Sapling, walnut
      case 459: // Sapling, cedar
      case 460: // Sapling, oak
      case 461: // Sapling, pine
        if (use_item) {
          return "Uproot Sapling";
        }
        return "";
      case 462: // Sprout, maple
      case 463: // Sprout, walnut
      case 464: // Sprout, cedar
      case 465: // Sprout, oak
      case 466: // Sprout, pine
        if (use_item) {
          return "Uproot Sprout";
        }
        return "";
      case 501: // car
      case 502:
      case 503:
      case 504:
      case 505:
        if (use_item) {
          return "Start Car";
        }
        return "";
      default:
        return "";
    }
  }


  boolean switchable() { // can be toggled with light switch
    switch(this.ID) {
      case 180: // lamp
      case 186: // outside light source
      case 187: // invisible light source
      case 188:
      case 189:
      case 190:
        return true;
      default:
        return false;
    }
  }


  void update(int time_elapsed, AbstractGameMap map) {
    this.update(time_elapsed);
    GameMapSquare square = map.mapSquare(this.coordinate);
    IntegerCoordinate feature_loc = this.gridLocation();
    switch(this.ID) {
      case 21: // workbench
        for (int i = 0; i < this.items.size(); i++) {
          if (this.items.get(i) == null || this.items.get(i).remove) {
            this.items.remove(i);
            i--;
          }
        }
        break;
      case 180: // lamp
        if (!toggle) {
          break;
        }
        try {
          square.light_level = 9;
          square.light_source = true;
          GameMapSquare square_up = map.mapSquare(this.coordinate.x, this.coordinate.y + 1);
          square_up.light_level = 9;
          square_up.light_source = true;
        } catch(NullPointerException e) {}
        break;
      case 186: // outside light source
        try {
          square.light_level = map.base_light_level;
          square.light_source = true;
        } catch(NullPointerException e) {}
        break;
      case 187: // invisible light source
      case 188:
      case 189:
      case 190:
        if (!toggle) {
          break;
        }
        for (int i = feature_loc.x; i < feature_loc.x + this.sizeX; i++) {
          for (int j = feature_loc.y; j < feature_loc.y + this.sizeY; j++) {
            GameMapSquare feature_square = map.mapSquare(i, j);
            if (feature_square == null) {
              continue;
            }
            feature_square.light_level = 10;
            feature_square.light_source = true;
          }
        }
        break;
      case 195: // light switch
      case 196:
      case 197:
      case 198:
        Feature light = map.getFeature(this.number);
        if (light == null || light.remove || !light.switchable()) {
          break;
        }
        if (this.toggle && !light.toggle) {
          light.toggle = true;
          light.refresh_map_image = true;
        }
        else if (!this.toggle && light.toggle) {
          light.toggle = false;
          light.refresh_map_image = true;
        }
        break;
      case 211: // Fences
      case 212:
        if (this.timer < 0) {
          this.timer = LNZ.feature_fenceRefreshTime;
          IntegerCoordinate[] adjacents = this.gridLocation().adjacentCoordinates();
          int[] adjacent_order = IntegerCoordinate.adjacentCoordinateOrder();
          for (int i = 0; i < adjacents.length; i++) {
            GameMapSquare adjacent = map.mapSquare(adjacents[i]);
            if (adjacent == null) {
              this.adjacent_context.set(adjacent_order[i], false);
              continue;
            }
            boolean b = false;
            for (Feature f : adjacent.features) {
              if (!f.isFence()) {
                continue;
              }
              if (this.zi() > f.zf() || f.zi() > this.zf()) {
                continue;
              }
              b = true;
              break;
            }
            this.adjacent_context.set(adjacent_order[i], b);
          }
          this.adjacent_context.setFileString();
        }
        break;
      case 413: // Ivy
        if (this.timer < 0) {
          this.timer = LNZ.feature_ivyGrowTimer + Misc.randomInt(LNZ.feature_ivyGrowTimer);
          int square_x = feature_loc.x - 1 + Misc.randomInt(3);
          int square_y = feature_loc.y - 1 + Misc.randomInt(3);
          if (square_x != feature_loc.x && square_y != feature_loc.y) {
            break; // no diagonal growth
          }
          GameMapSquare ivy_square = map.mapSquare(square_x, square_y);
          if (ivy_square == null) {
            break;
          }
          if (!ivy_square.can_grow_ivy) {
            break;
          }
          map.addFeature(413, square_x, square_y);
        }
        break;
      case 421: // Trees
      case 422:
      case 423:
      case 424:
      case 425:
      case 426:
      case 444:
      case 445:
      case 446:
      case 447:
      case 448:
      case 449:
        if (this.timer < 0) {
          this.timer = LNZ.feature_treeTimer + Misc.randomInt(LNZ.feature_treeTimer);
          this.toggle = true;
        }
        break;
      case 440: // Underbrush
        if (this.timer < 0) {
          this.timer = LNZ.feature_bushTimer + Misc.randomInt(LNZ.feature_bushTimer);
          this.toggle = true;
        }
        break;
      case 441: // Bushes
      case 442:
      case 443:
        if (this.timer < 0) {
          this.timer = 2 * LNZ.feature_bushTimer + Misc.randomInt(2 * LNZ.feature_bushTimer);
          this.toggle = true;
        }
        break;
      case 451: // Wapato
        if (square != null) {
          if (square.terrain_id == 164) {
            this.timer += time_elapsed;
          }
          else if (square.terrain_id == 182) {
            this.timer -= time_elapsed;
          }
          else if (square.terrain_id != 165 && square.terrain_id != 183) {
            this.destroy(map);
          }
        }
        if (this.number >= 4) {
          break;
        }
        if (this.timer < 0) {
          this.number++;
          this.timer = 90 * 60000 + Misc.randomInt(25 * 60000);
          this.refresh_map_image = true;
        }
        break;
      case 401: // Dandelion
        if (square != null && !square.canGrowSomething()) {
          this.destroy(map);
        }
        if (this.timer < 0) {
          this.number++;
          if (this.number == 5) {
            this.number = 2;
          }
          this.timer = 15 * 60000 + Misc.randomInt(10 * 60000);
          this.refresh_map_image = true;
        }
        break;
      case 452: // Leek
      case 453: // Ryegrass
      case 454: // Barnyard Grass
      case 455: // Broadleaf Plantain
      case 456: // Stinging Nettle
        if (square != null) {
          if (square.terrain_id == 164) {
            this.timer += LNZApplet.round(0.8 * time_elapsed);
          }
          else if (square.terrain_id != 165) {
            this.destroy(map);
          }
        }
        if (this.number >= 4) {
          break;
        }
        if (this.timer < 0) {
          this.number++;
          switch(this.ID) {
            case 452: // Leek
              this.timer = 40 * 60000 + Misc.randomInt(10 * 60000);
              break;
            case 453: // Ryegrass
              this.timer = 26 * 60000 + Misc.randomInt(8 * 60000);
              break;
            case 454: // Barnyard Grass
              this.timer = 11 * 60000 + Misc.randomInt(5 * 60000);
              break;
            case 455: // Broadleaf Plantain
              this.timer = 30 * 60000 + Misc.randomInt(10 * 60000);
              break;
            case 456: // Stinging Nettle
              this.timer = 24 * 60000 + Misc.randomInt(7 * 60000);
              break;
          }
          this.refresh_map_image = true;
        }
        break;
      case 457: // Sapling, maple
      case 458: // Sapling, walnut
      case 459: // Sapling, cedar
      case 460: // Sapling, oak
      case 461: // Sapling, pine
        if (square != null && !square.canGrowSomething()) {
          this.destroy(map);
          break;
        }
        if (this.timer < 0) {
          this.timer = 6000;
          Feature tree = new Feature(p, this.treeId(Misc.randomChance(0.5)));
          tree.coordinate.x = this.coordinate.x - Misc.randomInt(0, tree.sizeX - 1);
          tree.coordinate.y = this.coordinate.y - Misc.randomInt(0, tree.sizeY - 1);
          boolean grow_tree = true;
          IntegerCoordinate tree_loc = tree.gridLocation();
          for (int i = tree_loc.x; i < tree_loc.x + tree.sizeX; i++) {
            for (int j = tree_loc.y; j < tree_loc.y + tree.sizeY; j++) {
              GameMapSquare tree_square = map.mapSquare(i, j);
              if (tree_square == null) {
                grow_tree = false;
                break;
              }
              if (tree_square.terrain_id < 151 || tree_square.terrain_id > 170) {
                grow_tree = false;
                break;
              }
              if (tree_square.features.size() > 1 || (tree_square.features.size() == 1 &&
                tree_square.features.get(0).map_key != this.map_key)) {
                grow_tree = false;
                break;
              }
            }
          }
          if (grow_tree) {
            this.remove = true;
            map.addFeature(tree);
          }
        }
        break;
      case 462: // Sprout, maple
      case 463: // Sprout, walnut
      case 464: // Sprout, cedar
      case 465: // Sprout, oak
      case 466: // Sprout, pine
        if (square != null && !square.canGrowSomething()) {
          this.destroy(map);
          break;
        }
        if (this.timer < 0) {
          this.remove = true;
          map.addFeature(this.treeId(true), this.gridLocation());
        }
        break;
      default:
        break;
    }
  }

  int treeId(boolean large_tree) {
    switch(this.ID) {
      case 457: // Sapling, maple
        if (large_tree) {
          return 444;
        }
        return 421;
      case 458: // Sapling, walnut
        if (large_tree) {
          return 445;
        }
        return 422;
      case 459: // Sapling, cedar
        if (large_tree) {
          return 446;
        }
        return 423;
      case 460: // Sapling, oak
        if (large_tree) {
          return 448;
        }
        return 425;
      case 461: // Sapling, pine
        if (large_tree) {
          return 449;
        }
        return 426;
      case 462: // Sprout, maple
        return 457;
      case 463: // Sprout, walnut
        return 458;
      case 464: // Sprout, cedar
        return 459;
      case 465: // Sprout, oak
        return 460;
      case 466: // Sprout, pine
        return 461;
      default:
        p.global.errorMessage("ERROR: Feature ID " + this.ID + " has not treeId.");
        return 0;
    }
  }

  void update(int time_elapsed) {
    switch(this.ID) {
      case 151: // Sign
      case 152:
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
      case 164: // Urinal
      case 165: // Toilet
      case 185: // Pickle Jar
        if (this.number < 0) {
          break;
        }
        this.number -= time_elapsed;
        break;
      case 160: // Water Fountain
      case 161:
        if (this.timer >= 0) {
          this.timer -= time_elapsed;
          if (this.timer < 0) {
            this.refresh_map_image = true;
          }
        }
        break;
      case 163: // Shower Stall
        if (this.timer >= 0) {
          this.timer -= time_elapsed;
          if (this.timer < 0) {
            this.refresh_map_image = true;
          }
        }
        if (this.number < 0) {
          break;
        }
        this.number -= time_elapsed;
        break;
      case 211: // Fence
      case 212:
      case 401: // Dandelion
      case 413: // Ivy
      case 421: // Trees
      case 422:
      case 423:
      case 424:
      case 425:
      case 426:
      case 444:
      case 445:
      case 446:
      case 447:
      case 448:
      case 449:
      case 440: // Underbrush
      case 441: // Bushes
      case 442:
      case 443:
      case 451: // Growing plants
      case 452:
      case 453:
      case 454:
      case 455:
      case 456:
      case 457:
      case 458:
      case 459:
      case 460:
      case 461:
      case 462:
      case 463:
      case 464:
      case 465:
      case 466:
        if (this.timer >= 0) {
          this.timer -= time_elapsed;
        }
        break;
      default:
        break;
    }
  }


  void createKhalilInventory() {
    if (this.ID != 11 || this.inventory != null) {
      return;
    }
    this.inventory = Inventory.getKhalilInventory(p, this.number);
  }


  String fileString() {
    String fileString = "\nnew: Feature: " + this.ID;
    fileString += this.objectFileString();
    fileString += "\nnumber: " + this.number;
    fileString += "\nnumber2: " + this.number2;
    fileString += "\ntimer: " + this.timer;
    fileString += "\ntoggle: " + this.toggle;
    if (this.inventory != null && this.ID != 22) {
      fileString += this.inventory.internalFileString();
    }
    if (this.items != null) {
      for (Item i : this.items) {
        fileString += i.fileString() + ": item_array";
      }
    }
    switch(this.ID) {
      case 151: // Sign, green
      case 152:
      case 153:
      case 154:
      case 155: // Sign, gray
      case 156:
      case 157:
      case 158:
        fileString += "\ndescription: " + this.description;
        break;
      default:
        break;
    }
    fileString += "\nend: Feature\n";
    return fileString;
  }

  void addData(String datakey, String data) {
    if (this.addObjectData(datakey, data)) {
      return;
    }
    switch(datakey) {
      case "number":
        this.number = Misc.toInt(data);
        break;
      case "number2":
        this.number2 = Misc.toInt(data);
        break;
      case "timer":
        this.timer = Misc.toInt(data);
        break;
      case "toggle":
        this.toggle = Misc.toBoolean(data);
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not found for feature data.");
        break;
    }
  }

  boolean isFog() {
    switch(this.ID) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
        return true;
      default:
        return false;
    }
  }
}