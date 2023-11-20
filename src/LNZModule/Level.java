package LNZModule;

import java.util.*;
import java.io.PrintWriter;
import processing.core.*;
import Button.*;
import DImg.DImg;
import Element.*;
import FileSystem.FileSystem;
import Form.*;
import Misc.*;

class Linker {
  private LNZ p;
  protected Rectangle rect1;
  protected Rectangle rect2;

  Linker(LNZ sketch) {
    this(sketch, new Rectangle(sketch), new Rectangle(sketch));
  }
  Linker(LNZ sketch, Rectangle rect1, Rectangle rect2) {
    this.p = sketch;
    this.rect1 = rect1;
    this.rect2 = rect2;
  }

  @Override
  public String toString() {
    return "rect1: " + this.rect1.toString() + "\nrect2: " + this.rect2.toString();
  }

  boolean port(Unit u, String map_name) {
    if (this.rect1.contains(u, map_name)) {
      return true;
    }
    return false;
  }

  String fileString() {
    String fileString = "\nnew: Linker";
    fileString += "\nrect1: " + this.rect1.fileString();
    fileString += "\nrect2: " + this.rect2.fileString();
    fileString += "\nend: Linker\n";
    return fileString;
  }

  void addData(String datakey, String data) {
    switch(datakey) {
      case "rect1":
        this.rect1.addData(data);
        break;
      case "rect2":
        this.rect2.addData(data);
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not found for linker data.");
        break;
    }
  }
}



class Level {

  class CompletedButton extends RectangleButton {
    protected double target_text_size = 24;

    CompletedButton(LNZ sketch) {
      super(sketch, 0.5 * sketch.width - 150, 0.5 * sketch.height + 100, 0.5 * sketch.width + 150, 0.5 * sketch.height + 150);
      this.show_message = true;
      this.message = "Continue";
      this.text_size = 28;
      this.noStroke();
      this.stroke_weight = 2;
      this.use_time_elapsed = true; 
      this.setColors(LNZ.color_transparent, LNZ.color_transparent,
        LNZ.color_transparent, LNZ.color_transparent, DImg.ccolor(255));
    }

    @Override
    public void update(int time_elapsed) {
      super.update(time_elapsed);
      double text_size_change = 0.032 * time_elapsed;
      if (this.text_size > this.target_text_size) {
        this.text_size -= text_size_change;
        if (this.text_size < this.target_text_size) {
          this.text_size = this.target_text_size;
        }
      }
      else {
        this.text_size += text_size_change;
        if (this.text_size > this.target_text_size) {
          this.text_size = this.target_text_size;
        }
      }
    }

    public void hover() {
      this.target_text_size = 36;
    }
    public void dehover() {
      this.target_text_size = 28;
    }
    public void click() {
      this.underline_text = true;
    }
    public void release() {
      this.underline_text = false;
      if (this.hovered) {
        Level.this.completed = true;
      }
    }
  }

  private LNZ p;

  protected String folderPath; // to level folder
  protected String levelName = "error";
  protected Location location = Location.ERROR;
  protected boolean nullify = false;
  protected boolean completed = false;
  protected boolean completing = false;
  protected int completing_timer = 3000;
  protected int completion_code = 0;
  protected CompletedButton completed_button = null;

  protected LevelForm level_form = null;
  protected LevelQuestBox level_questbox = null;
  protected LevelChatBox level_chatbox = null;
  protected AbstractGameMap curr_map;
  protected String currMapName = null;
  protected ArrayList<String> mapNames = new ArrayList<String>();
  protected String album_name = null;

  protected double xi = 0;
  protected double yi = 0;
  protected double xf = 0;
  protected double yf = 0;

  protected ArrayList<Linker> linkers = new ArrayList<Linker>();
  protected int nextTriggerKey = 1;
  protected Map<Integer, Trigger> triggers = new HashMap<Integer, Trigger>();
  protected Map<Integer, Quest> quests = new HashMap<Integer, Quest>();
  protected ClockDouble time = new ClockDouble(24, 6.5); // day cycle
  protected ZombieSpawnParams zombie_spawn_params;

  protected Rectangle player_start_location = null;
  protected Rectangle player_spawn_location = null;
  protected Hero player;
  protected boolean was_viewing_hero_tree = false;
  protected boolean respawning = false;
  protected int respawn_timer = 0;
  protected boolean sleeping = false;
  protected int sleep_timer = 0;
  protected boolean in_control = true;

  protected int last_update_time = 0;
  protected boolean restart_timers = false;

  Level(LNZ sketch) {
    this.p = sketch;
    this.zombie_spawn_params = new ZombieSpawnParams(sketch);
  }
  Level(LNZ sketch, String folderPath, String levelName) {
    this.p = sketch;
    this.zombie_spawn_params = new ZombieSpawnParams(sketch);
    this.folderPath = folderPath;
    this.levelName = levelName;
    this.open();
  }
  Level(LNZ sketch, String folderPath, Location location) {
    this.p = sketch;
    this.zombie_spawn_params = new ZombieSpawnParams(sketch);
    this.folderPath = folderPath;
    this.location = location;
    this.levelName = location.displayName();
    this.open();
  }
  // test map
  Level(LNZ sketch, AbstractGameMap testMap) {
    this.p = sketch;
    this.zombie_spawn_params = new ZombieSpawnParams(sketch);
    this.folderPath = "";
    this.levelName = testMap.mapName;
    this.curr_map = testMap;
    this.currMapName = testMap.mapName;
  }


  // Should always be called before nullifying level reference
  void close() {
    if (this.curr_map != null) {
      this.curr_map.close();
    }
  }


  String timeString() {
    return this.timeString(false, false);
  }
  String timeString(boolean military_time, boolean show_seconds) {
    double minutes = 60.0 * (this.time.value() - Math.floor(this.time.value()));
    double seconds = 60.0 * (minutes - Math.floor(minutes));
    String hrs = Integer.toString((int)this.time.value());
    String mins = Integer.toString((int)minutes);
    while(mins.length() < 2) {
      mins = "0" + mins;
    }
    String secs = Integer.toString((int)seconds);
    while(secs.length() < 2) {
      secs = "0" + secs;
    }
    if (military_time) {
      if (show_seconds) {
        return hrs + ":" + mins + ":" + secs;
      }
      else {
        return hrs + ":" + mins;
      }
    }
    else {
      String suffix = " am";
      if (this.time.value() >= 12) {
        suffix = " pm";
        int hrs_int = (int)(this.time.value() - 12);
        if (hrs_int == 0) {
          hrs_int = 12;
        }
        hrs = Integer.toString(hrs_int);
      }
      if (show_seconds) {
        return hrs + ":" + mins + ":" + secs + suffix;
      }
      else {
        return hrs + ":" + mins + suffix;
      }
    }
  }


  void decisionForm(int i) {
    if (this.level_form != null) {
      p.global.log("WARNING: Decision form being created while level form " +
        this.level_form.getClass().toString() + " exists.");
    }
    this.level_form = new DecisionForm(p, i);
  }


  void gainControl() {
    this.in_control = true;
    p.global.player_blinks_left = 6;
    p.global.player_blinking = true;
    p.global.player_blink_time = LNZ.level_questBlinkTime;
    if (this.curr_map != null) {
      this.curr_map.in_control = true;
    }
    if (this.player != null) {
      this.player.in_control = true;
    }
  }

  void loseControl() {
    this.in_control = false;
    p.global.player_blinks_left = 6;
    p.global.player_blinking = false;
    p.global.player_blink_time = LNZ.level_questBlinkTime;
    if (this.curr_map != null) {
      this.curr_map.in_control = false;
    }
    if (this.player != null) {
      this.player.in_control = false;
    }
  }


  String getCurrMapNameDisplay() {
    if (this.currMapName == null) {
      return "No current map (see default)";
    }
    else {
      return "Current map: " + this.currMapName;
    }
  }


  String getPlayerStartLocationDisplay() {
    if (this.player_start_location == null) {
      return "No player start location";
    }
    else {
      return "Player starts on " + this.player_start_location.mapName + " at (" +
        this.player_start_location.centerX() + ", " + this.player_start_location.centerY() + ")";
    }
  }

  String getPlayerSpawnLocationDisplay() {
    if (this.player_spawn_location == null) {
      return "No player spawn location";
    }
    else {
      return "Player respawns at " + this.player_spawn_location.mapName + " at (" +
        this.player_spawn_location.centerX() + ", " + this.player_spawn_location.centerY() + ")";
    }
  }


  void addTestPlayer() {
    Hero h = new Hero(p, HeroCode.BEN);
    this.addPlayer(h);
  }

  void addPlayer(Hero h) {
    if (this.player != null) {
      p.global.errorMessage("ERROR: Trying to add player when player already exists.");
      return;
    }
    this.player = h;
    this.player.location = this.location;
    this.player.in_control = this.in_control;
    if (this.curr_map != null) {
      this.curr_map.addPlayer(this.player);
    }
  }

  void setPlayer(Hero player) {
    if (this.player != null) {
      p.global.errorMessage("ERROR: Trying to add player when player already exists.");
      return;
    }
    if (this.player_start_location == null || !this.hasMap(this.player_start_location.mapName)) {
      if (this.curr_map != null && GameMapArea.class.isInstance(this.curr_map)) {
        GameMapArea area_map = (GameMapArea)this.curr_map;
        player.setLocation(area_map.defaultSpawnX(), area_map.defaultSpawnY());
      }
      else if (this.mapNames.size() > 0) {
        this.openMap(this.mapNames.get(0));
        player.setLocation(0, 0);
      }
      else {
        player.setLocation(0, 0);
      }
    }
    else {
      this.openMap(this.player_start_location.mapName);
      if (this.curr_map != null && GameMapArea.class.isInstance(this.curr_map)) {
        GameMapArea area_map = (GameMapArea)this.curr_map;
        player.setLocation(area_map.defaultSpawnX(), area_map.defaultSpawnY());
      }
      else {
        player.setLocation(this.player_start_location.centerX(), this.player_start_location.centerY());
      }
    }
    if (this.curr_map == null || this.curr_map.nullify) {
      this.nullify = true;
      p.global.errorMessage("ERROR: Can't open default map.");
    }
    else {
      this.curr_map.addPlayer(player);
    }
    this.player = player;
    this.player.location = this.location;
    this.player.in_control = this.in_control;
  }

  void respawnPlayer() {
    if (this.player == null) {
      p.global.errorMessage("ERROR: Trying to respawn player when player doesn't exists.");
      return;
    }
    this.player.remove = false;
    this.player.curr_health = this.player.health();
    this.player.curr_mana = 0;
    this.player.hunger = LNZ.hero_maxHunger;
    this.player.thirst = LNZ.hero_maxThirst;
    this.player.experience *= LNZ.hero_experienceRespawnMultiplier;
    this.player.money *= LNZ.hero_moneyRespawnMultiplier;
    if (this.player_spawn_location != null && this.hasMap(this.player_spawn_location.mapName)) {
      this.openMap(this.player_spawn_location.mapName);
      if (this.curr_map != null && GameMapArea.class.isInstance(this.curr_map)) {
        GameMapArea area_map = (GameMapArea)this.curr_map;
        player.setLocation(area_map.defaultSpawnX(), area_map.defaultSpawnY());
      }
      else {
        player.setLocation(this.player_spawn_location.centerX(), this.player_spawn_location.centerY());
      }
    }
    else if (this.player_start_location != null && this.hasMap(this.player_start_location.mapName)) {
      this.openMap(this.player_start_location.mapName);
      player.setLocation(this.player_start_location.centerX(), this.player_start_location.centerY());
    }
    else {
      if (this.mapNames.size() > 0) {
        this.openMap(this.mapNames.get(0));
        player.setLocation(0, 0);
      }
      else {
        player.setLocation(0, 0);
      }
    }
    if (this.curr_map == null || this.curr_map.nullify) {
      this.nullify = true;
      p.global.errorMessage("ERROR: Can't open map with name " + this.currMapName + ".");
    }
    else {
      this.curr_map.addPlayer(player);
      this.curr_map.setViewLocation(player.coordinate);
    }
  }


  boolean hasMap(String queryMapName) {
    for (String mapName : this.mapNames) {
      if (queryMapName.equals(mapName)) {
        return true;
      }
    }
    return false;
  }

  void removeMap(String mapName) {
    for (int i = 0; i < this.mapNames.size(); i++) {
      if (mapName.equals(this.mapNames.get(i))) {
        this.mapNames.remove(i);
        return;
      }
    }
  }


  void movePlayerTo(Rectangle rect) {
    if (this.player == null || rect == null) {
      return;
    }
    if (!this.hasMap(rect.mapName)) {
      return;
    }
    this.openMap(rect.mapName);
    this.player.teleport(this.curr_map, rect.centerX(), rect.centerY());
    this.player.stopAction();
    this.curr_map.addPlayer(player);
    this.curr_map.addHeaderMessage(GameMapCode.displayName(this.curr_map.code));
  }

  void openCurrMap() {
    this.openMap(this.currMapName);
    if (this.curr_map == null || this.curr_map.nullify) {
      this.nullify = true;
      p.global.errorMessage("ERROR: Can't open curr map: " + this.currMapName + ".");
    }
  }

  String mapSuffix() {
    if (this.location.isArea()) {
      return "area";
    }
    return "map";
  }

  void openMap(String mapName) {
    if (mapName == null) {
      return;
    }
    if (!FileSystem.fileExists(p, this.finalFolderPath() + "/" + mapName + "." + this.mapSuffix() + ".lnz")) {
      p.global.errorMessage("ERROR: Level " + this.levelName + " has no map " +
        "with name " + mapName + " at location " + this.finalFolderPath() + ".");
      this.nullify = true;
      return;
    }
    if (mapName.equals(this.currMapName) && this.curr_map != null) {
      return;
    }
    this.closeMap();
    this.currMapName = mapName;
    if (this.location.isArea()) {
      this.curr_map = new GameMapArea(p, mapName, this.finalFolderPath());
      this.curr_map.open(this.finalFolderPath());
      ((GameMapArea)this.curr_map).initializeArea();
    }
    else {
      this.curr_map = new GameMap(p, mapName, this.finalFolderPath());
    }
    this.curr_map.setLocation(this.xi, this.yi, this.xf, this.yf);
    this.curr_map.in_control = this.in_control;
  }

  void closeMap() {
    if (this.curr_map != null) {
      this.curr_map.save(this.finalFolderPath());
      this.curr_map.close();
      this.curr_map = null;
    }
    this.currMapName = null;
  }


  void addLinker(Linker linker) {
    if (linker == null) {
      p.global.errorMessage("ERROR: Can't add null linker to linkers.");
      return;
    }
    this.linkers.add(linker);
  }
  void removeLinker(int index) {
    if (index < 0 || index >= this.linkers.size()) {
      p.global.errorMessage("ERROR: Linker index " + index + " out of range.");
      return;
    }
    this.linkers.remove(index);
  }

  void addTrigger(Trigger trigger) {
    if (trigger == null) {
      p.global.errorMessage("ERROR: Can't add null trigger to triggers.");
      return;
    }
    this.addTrigger(this.nextTriggerKey, trigger);
    this.nextTriggerKey++;
  }
  void addTrigger(int triggerCode, Trigger trigger) {
    if (trigger == null) {
      p.global.errorMessage("ERROR: Can't add null trigger to triggers.");
      return;
    }
    trigger.triggerID = triggerCode;
    this.triggers.put(triggerCode, trigger);
  }
  void removeTrigger(int triggerKey) {
    if (!this.triggers.containsKey(triggerKey)) {
      p.global.errorMessage("ERROR: No trigger with key " + triggerKey + ".");
      return;
    }
    this.triggers.remove(triggerKey);
  }

  void addQuest(int quest_id) {
    this.addQuest(new Quest(p, quest_id));
  }
  void addQuest(Quest quest) {
    if (this.quests.containsKey(quest.ID)) {
      return;
    }
    this.quests.put(quest.ID, quest);
    p.global.sounds.trigger_player("player/quest");
  }
  void removeQuest(int quest_id) {
    if (this.quests.containsKey(quest_id)) {
      this.quests.remove(quest_id);
    }
  }


  void setLocation(double xi, double yi, double xf, double yf) {
    this.xi = xi;
    this.yi = yi;
    this.xf = xf;
    this.yf = yf;
    if (this.curr_map != null) {
      this.curr_map.setLocation(xi, yi, xf, yf);
    }
    if (this.player != null) {
      this.player.hero_tree.setLocation(xi, yi, xf, yf);
    }
  }


  void drawLeftPanel(int millis) {
    if (this.curr_map != null) {
      this.curr_map.drawLeftPanel(millis);
    }
    if (this.player != null) {
      this.player.drawLeftPanel(millis, this.xi);
    }
  }

  boolean leftPanelElementsHovered() {
    if (this.curr_map != null) {
      return this.curr_map.leftPanelElementsHovered();
    }
    return false;
  }

  void drawRightPanel(int millis) {
    if (this.level_questbox == null) {
      this.level_questbox = new LevelQuestBox(p);
    }
    this.level_questbox.setXLocation(this.xf + LNZ.mapEditor_listBoxGap, p.width - LNZ.mapEditor_listBoxGap);
    this.level_questbox.update(millis);
    if (this.level_chatbox == null) {
      this.level_chatbox = new LevelChatBox(p);
    }
    this.level_chatbox.setXLocation(this.xf + LNZ.mapEditor_listBoxGap, p.width - LNZ.mapEditor_listBoxGap);
    this.level_chatbox.update(millis);
  }


  void heroFeatureInteraction(Hero h, boolean use_item) {
    if (this.curr_map == null || h == null || h.object_targeting == null || h.object_targeting.remove) {
      return;
    }
    if (!Feature.class.isInstance(h.object_targeting)) {
      p.global.errorMessage("ERROR: Hero " + h.displayName() + " trying to " +
        "interact with feature " + h.displayName() + " but it's not a feature.");
      return;
    }
    Feature f = (Feature)h.object_targeting;
    Feature new_f;
    int item_id = 0;
    Item new_i;
    double random_number = Math.random();
    switch(f.ID) {
      case 11: // Khalil
        if (f.toggle) {
          this.level_form = new KhalilForm(p, f, h);
          p.global.defaultCursor();
          f.toggle = false;
        }
        else {
          f.toggle = true;
          if (Misc.randomChance(0.5)) {
            this.chat("Traveling Buddy: I am a caterpillar. Well, that's not " +
              "entirely true. My mother was a caterpillar, my father was a worm, " +
              "but I'm okay with that now.");
          }
          else {
            this.chat("Traveling Buddy: I am a small business operator; a " +
              "traveling salesman. I sell Persian rugs door-to-door!");
          }
          this.curr_map.addVisualEffect(4009, f.coordinate.x + 0.6, f.coordinate.y - 0.4);
        }
        break;
      case 12: // Chuck Quizmo
        if (f.toggle) {
          this.level_form = new QuizmoForm(p, f, h);
          p.global.defaultCursor();
          f.toggle = false;
        }
        else {
          f.toggle = true;
          this.chat("Chuck Quizmo: Chuck Quizmo's the name, and quizzes are my " +
            "game! You want quizzes, I got 'em! If you can manage to answer my " +
            "brain-busting questions correctly, then... Y... Yaa... Yaaaaaahooo! " +
            "I'll give you a Star Piece!");
          this.curr_map.addVisualEffect(4009, f.coordinate.x + 0.7, f.coordinate.y - 0.4);
        }
        break;
      case 21: // Workbench
        if (use_item) {
          if (!h.holding(2977, 2979, 2980, 2981, 2983)) {
            this.curr_map.addHeaderMessage("Not holding correct tool to destroy desk.");
            break;
          }
          switch(h.weapon().ID) {
            case 2977: // Stone Hatchet
              f.number -= 1;
              p.global.sounds.trigger_units("items/melee/ax",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2979: // Saw
              f.number -= 1;
              p.global.sounds.trigger_units("items/saw_cut_wood",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2980: // Drill
              f.number -= 1;
              p.global.sounds.trigger_units("items/melee/drill" + Misc.randomInt(1, 3),
                f.center().subtractR(this.curr_map.view));
              break;
            case 2981: // Roundsaw
              f.number -= 2;
              p.global.sounds.trigger_units("items/roundsaw_cut_wood",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2983: // Chainsaw
              f.number -= 2;
              p.global.sounds.trigger_units("items/chainsaw_long",
                f.center().subtractR(this.curr_map.view));
              break;
          }
          h.weapon().lowerDurability();
          if (f.number < 1) {
            f.destroy(this.curr_map);
          }
          break;
        }
        if (h.inventory.viewing) {
          break;
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        break;
      case 22: // Ender Chest
        p.global.viewingEnderChest();
        if (p.global.state == ProgramState.MAPEDITOR_INTERFACE) {
          f.inventory = new EnderChestInventory(p);
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        p.global.sounds.trigger_units("features/chest",
          f.center().subtractR(this.curr_map.view));
        break;
      case 23: // Wooden Box
      case 24: // Wooden Crate
      case 25: // Wooden Chest
      case 26: // Large Wooden Chest
        if (use_item) {
          if (!h.holding(2977, 2979, 2980, 2981, 2983)) {
            this.curr_map.addHeaderMessage("Not holding correct tool to [pickup] " + f.displayName().toLowerCase() + ".");
            break;
          }
          switch(h.weapon().ID) {
            case 2977: // Stone Hatchet
              f.number -= 1;
              p.global.sounds.trigger_units("items/melee/ax",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2979: // Saw
              f.number -= 1;
              p.global.sounds.trigger_units("items/saw_cut_wood",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2980: // Drill
              f.number -= 1;
              p.global.sounds.trigger_units("items/melee/drill" + Misc.randomInt(1, 3),
                f.center().subtractR(this.curr_map.view));
              break;
            case 2981: // Roundsaw
              f.number -= 2;
              p.global.sounds.trigger_units("items/roundsaw_cut_wood",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2983: // Chainsaw
              f.number -= 2;
              p.global.sounds.trigger_units("items/chainsaw_long",
                f.center().subtractR(this.curr_map.view));
              break;
          }
          h.weapon().lowerDurability();
          if (f.number < 1) {
            f.destroy(this.curr_map);
          }
          break;
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        break;
      case 101: // Wooden Table
      case 106: // Small Wooden Table
      case 107:
      case 108: // Ping Pong Table
      case 109: // Wooden Table, low
      case 110: // Wooden Table, end
      case 116:
      case 111: // Wooden Chair
      case 112:
      case 113:
      case 114:
      case 125: // Wooden Bench
      case 126:
      case 127:
      case 128:
      case 129:
      case 130:
        if (!h.holding(2977, 2979, 2980, 2981, 2983)) {
          this.curr_map.addHeaderMessage("Not holding correct tool to destroy " + f.displayName().toLowerCase() + ".");
          break;
        }
        switch(h.weapon().ID) {
          case 2977: // stone hatchet
            f.number -= 1;
            p.global.sounds.trigger_units("items/melee/ax",
              f.center().subtractR(this.curr_map.view));
            break;
          case 2979: // saw
            f.number -= 1;
            p.global.sounds.trigger_units("items/saw_cut_wood",
              f.center().subtractR(this.curr_map.view));
            break;
          case 2980: // drill
            f.number -= 1;
            p.global.sounds.trigger_units("items/melee/drill" + Misc.randomInt(1, 3),
              f.center().subtractR(this.curr_map.view));
            break;
          case 2981: // roundsaw
            f.number -= 2;
            p.global.sounds.trigger_units("items/roundsaw_cut_wood",
              f.center().subtractR(this.curr_map.view));
            break;
          case 2983: // chainsaw
            f.number -= 2;
            p.global.sounds.trigger_units("items/chainsaw_long",
              f.center().subtractR(this.curr_map.view));
            break;
        }
        h.weapon().lowerDurability();
        if (f.number < 1) {
          f.destroy(this.curr_map);
        }
        break;
      case 102: // Wooden Desk
      case 103:
      case 104:
      case 105:
        if (use_item) {
          if (!h.holding(2977, 2979, 2980, 2981, 2983)) {
            this.curr_map.addHeaderMessage("Not holding correct tool to destroy desk.");
            break;
          }
          switch(h.weapon().ID) {
            case 2977: // stone hatchet
              f.number -= 1;
              p.global.sounds.trigger_units("items/melee/ax",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2979: // saw
              f.number -= 1;
              p.global.sounds.trigger_units("items/saw_cut_wood",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2980: // drill
              f.number -= 1;
              p.global.sounds.trigger_units("items/melee/drill" + Misc.randomInt(1, 3),
                f.center().subtractR(this.curr_map.view));
              break;
            case 2981: // roundsaw
              f.number -= 2;
              p.global.sounds.trigger_units("items/roundsaw_cut_wood",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2983: // chainsaw
              f.number -= 2;
              p.global.sounds.trigger_units("items/chainsaw_long",
                f.center().subtractR(this.curr_map.view));
              break;
          }
          h.weapon().lowerDurability();
          if (f.number < 1) {
            f.destroy(this.curr_map);
          }
          break;
        }
        if (h.inventory.viewing) {
          break;
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        break;
      case 115: // Coordinator Chair
      case 117: // Green Chair
      case 121: // Couch
      case 122:
      case 123:
      case 124:
        if (use_item) {
          if (!h.holding(2977, 2979, 2980, 2981, 2983)) {
            this.curr_map.addHeaderMessage("Not holding correct tool to destroy " + f.displayName().toLowerCase() + ".");
            break;
          }
          switch(h.weapon().ID) {
            case 2977: // stone hatchet
              f.number -= 1;
              p.global.sounds.trigger_units("items/melee/ax",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2979: // saw
              f.number -= 1;
              p.global.sounds.trigger_units("items/saw_cut_wood",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2980: // drill
              f.number -= 1;
              p.global.sounds.trigger_units("items/melee/drill" + Misc.randomInt(1, 3),
                f.center().subtractR(this.curr_map.view));
              break;
            case 2981: // roundsaw
              f.number -= 2;
              p.global.sounds.trigger_units("items/roundsaw_cut_wood",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2983: // chainsaw
              f.number -= 2;
              p.global.sounds.trigger_units("items/chainsaw_long",
                f.center().subtractR(this.curr_map.view));
              break;
          }
          h.weapon().lowerDurability();
          if (f.number < 1) {
            f.destroy(this.curr_map);
          }
          break;
        }
        p.global.sounds.trigger_environment("features/couch_shuffle",
          f.center().subtractR(this.curr_map.view));
        if (!f.toggle) {
          this.curr_map.addHeaderMessage("This " + f.displayName() + " has nothing in it.");
          break;
        }
        if (random_number > 0.99) {
          item_id = 2154;
        }
        else if (random_number > 0.96) {
          item_id = 2153;
        }
        else if (random_number > 0.9) {
          item_id = 2152;
        }
        else if (random_number > 0.75) {
          item_id = 2151;
        }
        else if (random_number > 0.72) {
          item_id = 2101;
        }
        else if (random_number > 0.69) {
          item_id = 2102;
        }
        else if (random_number > 0.66) {
          item_id = 2103;
        }
        else if (random_number > 0.63) {
          item_id = 2104;
        }
        else if (random_number > 0.6) {
          item_id = 2105;
        }
        else if (random_number > 0.58) {
          item_id = 2107;
        }
        else if (random_number > 0.57) {
          item_id = 2110;
        }
        else if (random_number > 0.56) {
          item_id = 2112;
        }
        else if (random_number > 0.55) {
          item_id = 2113;
        }
        else if (random_number > 0.54) {
          item_id = 2132;
        }
        else if (random_number > 0.53) {
          item_id = 2134;
        }
        else if (random_number > 0.52) {
          item_id = 2402;
        }
        else if (random_number > 0.51) {
          item_id = 2502;
        }
        else if (random_number > 0.5) {
          item_id = 2602;
        }
        else if (random_number > 0.49) {
          item_id = 2603;
        }
        else if (random_number > 0.48) {
          item_id = 2604;
        }
        else if (random_number > 0.45) {
          item_id = 2702;
        }
        else if (random_number > 0.44) {
          item_id = 2703;
        }
        else if (random_number > 0.43) {
          item_id = 2911;
        }
        else if (random_number > 0.42) {
          item_id = 2912;
        }
        else if (random_number > 0.41) {
          item_id = 2913;
        }
        else if (random_number > 0.39) {
          item_id = 2916;
        }
        else if (random_number > 0.38) {
          item_id = 2917;
        }
        else if (random_number > 0.35) {
          item_id = 2924;
        }
        else if (random_number > 0.33) {
          item_id = 2931;
        }
        else if (random_number > 0.31) {
          item_id = 2933;
        }
        else {
          f.toggle = false;
          this.curr_map.addHeaderMessage("This " + f.displayName() + " has nothing left in it.");
          break;
        }
        new_i = new Item(p, item_id, h.frontX(), h.frontY());
        if (h.canPickup()) {
          h.pickup(new_i);
          new_i.pickupSound();
        }
        else {
          this.curr_map.addItem(new_i);
        }
        this.curr_map.addHeaderMessage("You found a " + new_i.displayName() + ".");
        break;
      case 131: // bed
      case 132:
      case 133:
      case 134:
        if (use_item) {
          if (!h.holding(2977, 2979, 2980, 2981, 2983)) {
            this.curr_map.addHeaderMessage("Not holding correct tool to destroy bed.");
            break;
          }
          switch(h.weapon().ID) {
            case 2977: // stone hatchet
              f.number -= 1;
              p.global.sounds.trigger_units("items/melee/ax",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2979: // saw
              f.number -= 1;
              p.global.sounds.trigger_units("items/saw_cut_wood",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2980: // drill
              f.number -= 1;
              p.global.sounds.trigger_units("items/melee/drill" + Misc.randomInt(1, 3),
                f.center().subtractR(this.curr_map.view));
              break;
            case 2981: // roundsaw
              f.number -= 2;
              p.global.sounds.trigger_units("items/roundsaw_cut_wood",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2983: // chainsaw
              f.number -= 2;
              p.global.sounds.trigger_units("items/chainsaw_long",
                f.center().subtractR(this.curr_map.view));
              break;
          }
          h.weapon().lowerDurability();
          if (f.number < 1) {
            f.destroy(this.curr_map);
          }
          break;
        }
        if (this.isNight()) {
          this.sleeping = true;
          this.sleep_timer = LNZ.feature_bedSleepTimer;
          this.loseControl();
          h.stopAction();
        }
        else {
          this.curr_map.addHeaderMessage("You can only sleep at night.");
        }
        break;
      case 141: // wardrobe
      case 142:
        if (use_item) {
          if (!h.holding(2977, 2979, 2980, 2981, 2983)) {
            this.curr_map.addHeaderMessage("Not holding correct tool to destroy wardrobe.");
            break;
          }
          switch(h.weapon().ID) {
            case 2977: // stone hatchet
              f.number -= 1;
              p.global.sounds.trigger_units("items/melee/ax",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2979: // saw
              f.number -= 1;
              p.global.sounds.trigger_units("items/saw_cut_wood",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2980: // drill
              f.number -= 1;
              p.global.sounds.trigger_units("items/melee/drill" + Misc.randomInt(1, 3),
                f.center().subtractR(this.curr_map.view));
              break;
            case 2981: // roundsaw
              f.number -= 2;
              p.global.sounds.trigger_units("items/roundsaw_cut_wood",
                f.center().subtractR(this.curr_map.view));
              break;
            case 2983: // chainsaw
              f.number -= 2;
              p.global.sounds.trigger_units("items/chainsaw_long",
                f.center().subtractR(this.curr_map.view));
              break;
          }
          h.weapon().lowerDurability();
          if (f.number < 1) {
            f.destroy(this.curr_map);
          }
          break;
        }
        p.global.sounds.trigger_environment("features/wardrobe_shuffle",
          f.center().subtractR(this.curr_map.view));
        if (!f.toggle) {
          this.curr_map.addHeaderMessage("This " + f.displayName() + " has nothing in it.");
          break;
        }
        if (random_number > 0.99) {
          item_id = 2154;
        }
        else if (random_number > 0.98) {
          item_id = 2153;
        }
        else if (random_number > 0.95) {
          item_id = 2152;
        }
        else if (random_number > 0.9) {
          item_id = 2151;
        }
        else if (random_number > 0.88) {
          item_id = 2101;
        }
        else if (random_number > 0.87) {
          item_id = 2102;
        }
        else if (random_number > 0.86) {
          item_id = 2103;
        }
        else if (random_number > 0.85) {
          item_id = 2104;
        }
        else if (random_number > 0.84) {
          item_id = 2105;
        }
        else if (random_number > 0.83) {
          item_id = 2107;
        }
        else if (random_number > 0.82) {
          item_id = 2110;
        }
        else if (random_number > 0.81) {
          item_id = 2111;
        }
        else if (random_number > 0.8) {
          item_id = 2112;
        }
        else if (random_number > 0.785) {
          item_id = 2113;
        }
        else if (random_number > 0.77) {
          item_id = 2132;
        }
        else if (random_number > 0.755) {
          item_id = 2133;
        }
        else if (random_number > 0.74) {
          item_id = 2134;
        }
        else if (random_number > 0.73) {
          item_id = 2141;
        }
        else if (random_number > 0.71) {
          item_id = 2203;
        }
        else if (random_number > 0.68) {
          item_id = 2402;
        }
        else if (random_number > 0.65) {
          item_id = 2502;
        }
        else if (random_number > 0.63) {
          item_id = 2504;
        }
        else if (random_number > 0.62) {
          item_id = 2513;
        }
        else if (random_number > 0.6) {
          item_id = 2602;
        }
        else if (random_number > 0.58) {
          item_id = 2603;
        }
        else if (random_number > 0.56) {
          item_id = 2604;
        }
        else if (random_number > 0.53) {
          item_id = 2702;
        }
        else if (random_number > 0.51) {
          item_id = 2703;
        }
        else if (random_number > 0.5) {
          item_id = 2704;
        }
        else if (random_number > 0.49) {
          item_id = 2705;
        }
        else if (random_number > 0.48) {
          item_id = 2712;
        }
        else if (random_number > 0.47) {
          item_id = 2713;
        }
        else if (random_number > 0.46) {
          item_id = 2714;
        }
        else if (random_number > 0.45) {
          item_id = 2911;
        }
        else if (random_number > 0.44) {
          item_id = 2912;
        }
        else if (random_number > 0.43) {
          item_id = 2913;
        }
        else if (random_number > 0.42) {
          item_id = 2914;
        }
        else if (random_number > 0.41) {
          item_id = 2916;
        }
        else if (random_number > 0.4) {
          item_id = 2917;
        }
        else if (random_number > 0.37) {
          item_id = 2924;
        }
        else if (random_number > 0.36) {
          item_id = 2925;
        }
        else {
          f.toggle = false;
          this.curr_map.addHeaderMessage("This " + f.displayName() + " has nothing left in it.");
          break;
        }
        new_i = new Item(p, item_id, h.frontX(), h.frontY());
        if (h.canPickup()) {
          h.pickup(new_i);
          new_i.pickupSound();
        }
        else {
          this.curr_map.addItem(new_i);
        }
        this.curr_map.addHeaderMessage("You found a " + new_i.displayName() + ".");
        break;
      case 151: // sign
      case 152:
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
        try {
          this.curr_map.addHeaderMessage(PApplet.trim(PApplet.split(f.description,
            LNZ.feature_signDescriptionDelimiter)[1]));
        } catch(ArrayIndexOutOfBoundsException e) {
          this.curr_map.addHeaderMessage("-- the sign has nothing written on it --");
        }
        f.number = LNZ.feature_signCooldown;
        break;
      case 160: // Water Fountain
      case 161:
        p.global.sounds.trigger_environment("features/water_fountain",
          f.center().subtractR(this.curr_map.view));
        f.timer = 3000;
        f.refresh_map_image = true;
        if (use_item) {
          if (h.holding(2924, 2925, 2926, 2927)) {
            h.weapon().changeAmmo(3);
          }
          // if holding a dirty item clean it (?)
          else {
            this.curr_map.addHeaderMessage("Not holding anything to p.fill");
          }
        }
        h.increaseThirst(3);
        p.global.sounds.trigger_environment("features/water_fountain_drink",
          f.center().subtractR(this.curr_map.view));
        break;
      case 162: // sink
        p.global.sounds.trigger_environment("features/sink",
          f.center().subtractR(this.curr_map.view));
        if (use_item) {
          if (h.holding(2924, 2925, 2926, 2927)) {
            h.weapon().changeAmmo(4);
          }
          // if holding a dirty item clean it (?)
          else {
            this.curr_map.addHeaderMessage("Not holding anything to p.fill");
          }
          break;
        }
        h.increaseThirst(2);
        p.global.sounds.trigger_environment("features/water_fountain_drink",
          f.center().subtractR(this.curr_map.view));
        break;
      case 163: // shower stall
        f.number = LNZ.feature_showerStallCooldown;
        f.timer = 3500;
        f.refresh_map_image = true;
        // if holding a dirty item clean it (?)
        // if you are dirty clean yourself
        h.increaseThirst(1);
        p.global.sounds.trigger_environment("features/shower_stall",
          f.center().subtractR(this.curr_map.view));
        break;
      case 164: // urinal
        f.number = LNZ.feature_urinalCooldown;
        if (h.thirst < LNZ.hero_thirstThreshhold) {
          h.increaseThirst(3);
        }
        p.global.sounds.trigger_environment("features/urinal",
          f.center().subtractR(this.curr_map.view));
        break;
      case 165: // toilet
        f.number = LNZ.feature_toiletCooldown;
        if (h.thirst < LNZ.hero_thirstThreshhold) {
          h.increaseThirst(3);
        }
        p.global.sounds.trigger_environment("features/toilet",
          f.center().subtractR(this.curr_map.view));
        break;
      case 171: // stove
        if (h.inventory.viewing) {
          break;
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        p.global.sounds.trigger_environment("features/stove_open",
          f.center().subtractR(this.curr_map.view));
        break;
      case 172: // vending machine
      case 173:
        this.level_form = new VendingForm(p, f, h);
        p.global.defaultCursor();
        break;
      case 174: // minifridge
        if (h.inventory.viewing) {
          break;
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        break;
      case 175: // refridgerator
        if (h.inventory.viewing) {
          break;
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        break;
      case 176: // washer
        if (h.inventory.viewing) {
          break;
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        break;
      case 177: // dryer
        if (h.inventory.viewing) {
          break;
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        break;
      case 178: // microwave
        if (h.inventory.viewing) {
          break;
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        break;
      case 180: // lamp
        f.toggle = !f.toggle;
        f.refresh_map_image = true;
        if (f.toggle) {
          p.global.sounds.trigger_environment("features/switch_on",
            f.center().subtractR(this.curr_map.view));
          this.curr_map.timer_refresh_fog = 0;
        }
        else {
          p.global.sounds.trigger_environment("features/switch_off",
            f.center().subtractR(this.curr_map.view));
          this.curr_map.timer_refresh_fog = 0;
        }
        break;
      case 181: // garbage can
        if (h.inventory.viewing) {
          break;
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        p.global.sounds.trigger_environment("features/trash_can",
          f.center().subtractR(this.curr_map.view));
        break;
      case 182: // recycle can
        if (h.inventory.viewing) {
          break;
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        p.global.sounds.trigger_environment("features/trash_can",
          f.center().subtractR(this.curr_map.view));
        break;
      case 183: // crate
        if (h.inventory.viewing) {
          break;
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        p.global.sounds.trigger_environment("features/crate",
          f.center().subtractR(this.curr_map.view));
        break;
      case 184: // Cardboard Box
        if (h.inventory.viewing) {
          break;
        }
        h.inventory.featureInventory(f.inventory);
        h.inventory.viewing = true;
        p.global.sounds.trigger_environment("features/cardboard_box",
          f.center().subtractR(this.curr_map.view));
        break;
      case 185: // Pickle Jar
        if (use_item) {
          if (h.holding(2975)) {
            f.destroy(this.curr_map);
            p.global.sounds.trigger_environment("items/glass_bottle_hit",
              f.center().subtractR(this.curr_map.view));
          }
          else {
            this.curr_map.addHeaderMessage("Not correct tool to destroy jar");
          }
          break;
        }
        if (h.canPickup()) {
          f.number = LNZ.feature_pickleJarCooldown;
          new_i = new Item(p, 2106);
          h.pickup(new_i);
          new_i.pickupSound();
        }
        break;
      case 195: // Light Switch
      case 196:
      case 197:
      case 198:
        f.toggle = !f.toggle;
        f.refresh_map_image = true;
        if (f.toggle) {
          p.global.sounds.trigger_environment("features/switch_on",
            f.center().subtractR(this.curr_map.view));
          this.curr_map.timer_refresh_fog = 0;
        }
        else {
          p.global.sounds.trigger_environment("features/switch_off",
            f.center().subtractR(this.curr_map.view));
          this.curr_map.timer_refresh_fog = 0;
        }
        break;
      case 211: // Wire Fence
        if (use_item) {
          if (h.holding(2978)) {
            f.destroy(this.curr_map);
            h.weapon().lowerDurability();
            p.global.sounds.trigger_environment("items/wire_clipper",
              f.center().subtractR(this.curr_map.view));
          }
          else {
            this.curr_map.addHeaderMessage("Not correct tool to destroy wire");
          }
          break;
        }
        if (h.agility() >= 2) {
          h.setLocation(f.xCenter(), f.yCenter());
          h.curr_height = f.curr_height + f.sizeZ;
          p.global.sounds.trigger_units("features/climb_fence",
            f.center().subtractR(this.curr_map.view));
          if (Misc.randomChance(0.3)) {
            h.addStatusEffect(StatusEffectCode.BLEEDING, 2000,
              new DamageSource(21, StatusEffectCode.BLEEDING));
          }
        }
        else {
          this.curr_map.addHeaderMessage("Not enough agility to climb fence");
        }
        break;
      case 212: // Barbed Wire Fence
        if (use_item) {
          this.curr_map.addHeaderMessage("Not correct tool to destroy barbed wire");
        }
        if (h.agility() >= 3) {
          h.setLocation(f.xCenter(), f.yCenter());
          h.curr_height = f.curr_height + f.sizeZ;
          p.global.sounds.trigger_units("features/climb_fence",
            f.center().subtractR(this.curr_map.view));
          if (Misc.randomChance(0.8)) {
            h.addStatusEffect(StatusEffectCode.BLEEDING, 2500,
              new DamageSource(21, StatusEffectCode.BLEEDING));
          }
        }
        else {
          this.curr_map.addHeaderMessage("Not enough agility to climb fence");
        }
        break;
      case 301: // Movable Brick Wall
      case 302:
      case 303:
      case 304:
      case 305:
      case 306:
      case 307: // TODO: Add visual effect of wall moving
        f.remove = true;
        p.global.sounds.trigger_units("features/movable_wall",
          f.center().subtractR(this.curr_map.view));
        this.curr_map.addHeaderMessage("The wall easily slid over");
        break;
      case 321: // Window, brick
      case 322:
      case 323:
      case 324:
      case 325:
      case 326:
      case 327:
      case 328:
        if (!h.holding(2976)) {
          this.curr_map.addHeaderMessage("Not correct tool to break window");
          break;
        }
        f.destroy(this.curr_map);
        h.weapon().lowerDurability();
        new_f = new Feature(p, f.ID - 8, f.coordinate.copy(), false);
        this.curr_map.addFeature(new_f);
        new_f.curr_height = f.curr_height;
        p.global.sounds.trigger_environment("items/window_break",
          f.center().subtractR(this.curr_map.view));
        break;
      case 331: // Wooden Door (open)
      case 332:
      case 333:
      case 334:
      case 335:
      case 336:
      case 337:
      case 338:
      case 339: // Wooden Door (closed)
      case 340:
      case 341:
      case 342:
      case 343: // Wooden Door (locked)
      case 344:
      case 345:
      case 346:
        if (use_item) {
          if (h.holding(2977, 2979, 2983)) {
            f.destroy(this.curr_map);
            h.weapon().lowerDurability();
          }
          else {
            this.curr_map.addHeaderMessage("Not correct tool to destroy door");
          }
          break;
        }
        switch(f.ID) {
          case 331: // Door, open (up)
            f.remove = true;
            new_f = new Feature(p, 339, f.coordinate, false);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 332:
            f.remove = true;
            new_f = new Feature(p, 339, f.coordinate, true);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 333: // Door, open (left)
            f.remove = true;
            new_f = new Feature(p, 340, f.coordinate, false);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 334:
            f.remove = true;
            new_f = new Feature(p, 340, f.coordinate, true);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 335: // door open (diagonal left)
            f.remove = true;
            new_f = new Feature(p, 341, f.coordinate, false);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 336:
            f.remove = true;
            new_f = new Feature(p, 341, f.coordinate, true);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 337: // door open (diagonal right)
            f.remove = true;
            new_f = new Feature(p, 342, f.coordinate, false);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 338:
            f.remove = true;
            new_f = new Feature(p, 342, f.coordinate, true);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 339: // door closed (up)
            f.remove = true;
            if (f.toggle) {
              new_f = new Feature(p, 332, f.coordinate);
            }
            else {
              new_f = new Feature(p, 331, f.coordinate);
            }
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_open",
              f.center().subtractR(this.curr_map.view));
            break;
          case 340: // door closed (left)
            f.remove = true;
            if (f.toggle) {
              new_f = new Feature(p, 334, f.coordinate);
            }
            else {
              new_f = new Feature(p, 333, f.coordinate);
            }
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_open",
              f.center().subtractR(this.curr_map.view));
            break;
          case 341: // door closed (diagonal left)
            f.remove = true;
            if (f.toggle) {
              new_f = new Feature(p, 336, f.coordinate);
            }
            else {
              new_f = new Feature(p, 335, f.coordinate);
            }
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_open",
              f.center().subtractR(this.curr_map.view));
            break;
          case 342: // door closed (diagonal right)
            f.remove = true;
            if (f.toggle) {
              new_f = new Feature(p, 338, f.coordinate);
            }
            else {
              new_f = new Feature(p, 337, f.coordinate);
            }
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_open",
              f.center().subtractR(this.curr_map.view));
            break;
          case 343: // door locked (up)
            if (h.weapon() == null || !h.weapon().unlocks(f.number)) {
              if (h.weapon() != null && h.weapon().type.equals("Key")) {
                this.curr_map.addHeaderMessage("The key doesn't unlock this door");
              }
              else if (h.holding(2904, 2905)) {
                this.curr_map.addHeaderMessage("No key on this ring unlocks the door");
              }
              else {
                this.curr_map.addHeaderMessage("The door is locked");
              }
              break;
            }
            f.remove = true;
            new_f = new Feature(p, 339, f.coordinate, f.toggle);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_unlock",
              f.center().subtractR(this.curr_map.view));
            break;
          case 344: // door locked (left)
            if (h.weapon() == null || !h.weapon().unlocks(f.number)) {
              if (h.weapon() != null && h.weapon().type.equals("Key")) {
                this.curr_map.addHeaderMessage("The key doesn't unlock this door");
              }
              else if (h.holding(2904, 2905)) {
                this.curr_map.addHeaderMessage("No key on this ring unlocks the door");
              }
              else {
                this.curr_map.addHeaderMessage("The door is locked");
              }
              break;
            }
            f.remove = true;
            new_f = new Feature(p, 340, f.coordinate, f.toggle);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_unlock",
              f.center().subtractR(this.curr_map.view));
            break;
          case 345: // door locked (diagonal left)
            if (h.weapon() == null || !h.weapon().unlocks(f.number)) {
              if (h.weapon() != null && h.weapon().type.equals("Key")) {
                this.curr_map.addHeaderMessage("The key doesn't unlock this door");
              }
              else if (h.holding(2904, 2905)) {
                this.curr_map.addHeaderMessage("No key on this ring unlocks the door");
              }
              else {
                this.curr_map.addHeaderMessage("The door is locked");
              }
              break;
            }
            f.remove = true;
            new_f = new Feature(p, 341, f.coordinate, f.toggle);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/wooden_door_unlock",
              f.center().subtractR(this.curr_map.view));
            break;
          case 346: // door locked (diagonal right)
            if (h.weapon() == null || !h.weapon().unlocks(f.number)) {
              if (h.weapon() != null && h.weapon().type.equals("Key")) {
                this.curr_map.addHeaderMessage("The key doesn't unlock this door");
              }
              else if (h.holding(2904, 2905)) {
                this.curr_map.addHeaderMessage("No key on this ring unlocks the door");
              }
              else {
                this.curr_map.addHeaderMessage("The door is locked");
              }
              break;
            }
            f.remove = true;
            new_f = new Feature(p, 342, f.coordinate, f.toggle);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/stee_door_unlock",
              f.center().subtractR(this.curr_map.view));
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
        switch(f.ID) {
          case 351: // door open (up)
            f.remove = true;
            new_f = new Feature(p, 359, f.coordinate, false);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 352:
            f.remove = true;
            new_f = new Feature(p, 359, f.coordinate, true);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 353: // door open (left)
            f.remove = true;
            new_f = new Feature(p, 360, f.coordinate, false);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 354:
            f.remove = true;
            new_f = new Feature(p, 360, f.coordinate, true);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 355: // door open (diagonal left)
            f.remove = true;
            new_f = new Feature(p, 361, f.coordinate, false);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 356:
            f.remove = true;
            new_f = new Feature(p, 361, f.coordinate, true);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 357: // door open (diagonal right)
            f.remove = true;
            new_f = new Feature(p, 362, f.coordinate, false);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 358:
            f.remove = true;
            new_f = new Feature(p, 362, f.coordinate, true);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_close",
              f.center().subtractR(this.curr_map.view));
            break;
          case 359: // door closed (up)
            f.remove = true;
            if (f.toggle) {
              new_f = new Feature(p, 352, f.coordinate);
            }
            else {
              new_f = new Feature(p, 351, f.coordinate);
            }
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_open",
              f.center().subtractR(this.curr_map.view));
            break;
          case 360: // door closed (left)
            f.remove = true;
            if (f.toggle) {
              new_f = new Feature(p, 354, f.coordinate);
            }
            else {
              new_f = new Feature(p, 353, f.coordinate);
            }
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_open",
              f.center().subtractR(this.curr_map.view));
            break;
          case 361: // door closed (diagonal left)
            f.remove = true;
            if (f.toggle) {
              new_f = new Feature(p, 356, f.coordinate);
            }
            else {
              new_f = new Feature(p, 355, f.coordinate);
            }
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_open",
              f.center().subtractR(this.curr_map.view));
            break;
          case 362: // door closed (diagonal right)
            f.remove = true;
            if (f.toggle) {
              new_f = new Feature(p, 358, f.coordinate);
            }
            else {
              new_f = new Feature(p, 357, f.coordinate);
            }
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_open",
              f.center().subtractR(this.curr_map.view));
            break;
          case 363: // door locked (up)
            if (h.weapon() == null || !h.weapon().unlocks(f.number)) {
              if (h.weapon() != null && h.weapon().type.equals("Key")) {
                this.curr_map.addHeaderMessage("The key doesn't unlock this door");
              }
              else {
                this.curr_map.addHeaderMessage("The door is locked");
              }
              break;
            }
            f.remove = true;
            new_f = new Feature(p, 359, f.coordinate, f.toggle);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_unlock",
              f.center().subtractR(this.curr_map.view));
            break;
          case 364: // door locked (left)
            if (h.weapon() == null || !h.weapon().unlocks(f.number)) {
              if (h.weapon() != null && h.weapon().type.equals("Key")) {
                this.curr_map.addHeaderMessage("The key doesn't unlock this door");
              }
              else {
                this.curr_map.addHeaderMessage("The door is locked");
              }
              break;
            }
            f.remove = true;
            new_f = new Feature(p, 360, f.coordinate, f.toggle);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_unlock",
              f.center().subtractR(this.curr_map.view));
            break;
          case 365: // door locked (diagonal left)
            if (h.weapon() == null || !h.weapon().unlocks(f.number)) {
              if (h.weapon() != null && h.weapon().type.equals("Key")) {
                this.curr_map.addHeaderMessage("The key doesn't unlock this door");
              }
              else {
                this.curr_map.addHeaderMessage("The door is locked");
              }
              break;
            }
            f.remove = true;
            new_f = new Feature(p, 361, f.coordinate, f.toggle);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_unlock",
              f.center().subtractR(this.curr_map.view));
            break;
          case 366: // door locked (diagonal right)
            if (h.weapon() == null || !h.weapon().unlocks(f.number)) {
              if (h.weapon() != null && h.weapon().type.equals("Key")) {
                this.curr_map.addHeaderMessage("The key doesn't unlock this door");
              }
              else {
                this.curr_map.addHeaderMessage("The door is locked");
              }
              break;
            }
            f.remove = true;
            new_f = new Feature(p, 362, f.coordinate, f.toggle);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
            new_f.curr_height = f.curr_height;
            p.global.sounds.trigger_environment("features/steel_door_unlock",
              f.center().subtractR(this.curr_map.view));
            break;
        }
        break;
      case 401: // dandelion
        f.remove = true;
        new_i = null;
        if (f.number == 3) {
          new_i = new Item(p, 2961);
        }
        else if (f.number == 4) {
          new_i = new Item(p, 2021);
        }
        if (new_i != null) {
          if (h.canPickup()) {
            h.pickup(new_i);
          }
          else {
            this.curr_map.addItem(new_i);
          }
          new_i.pickupSound();
        }
        break;
      case 411: // gravel (pebbles)
        if (h.canPickup()) {
          new_i = new Item(p, 2933);
          h.pickup(new_i);
          new_i.pickupSound();
          f.number--;
          if (f.number < 1) {
            f.remove = true;
          }
        }
        break;
      case 412: // Gravel (rocks)
        if (h.canPickup()) {
          new_i = new Item(p, 2931);
          h.pickup(new_i);
          new_i.pickupSound();
          f.number--;
          if (f.number < 1) {
            f.remove = true;
            new_f = new Feature(p, 411, f.coordinate);
            this.curr_map.addFeature(new_f);
            new_f.hovered = true;
            this.curr_map.hovered_object = new_f;
          }
        }
        break;
      case 413: // Ivy
        f.destroy(this.curr_map);
        p.global.sounds.trigger_units("features/ivy",
          f.center().subtractR(this.curr_map.view));
        break;
      case 421: // Tree, maple
      case 422: // Tree, walnut
      case 423: // Tree, cedar
      case 424: // Tree, dead
      case 425: // Tree, oak
      case 426: // Tree, pine
      case 444: // large Tree, maple
      case 445: // large Tree, walnut
      case 446: // large Tree, cedar
      case 447: // large Tree, dead
      case 448: // large Tree, oak
      case 449: // large Tree, pine
        int branch_id = Tree.branchId(f.ID);
        if (use_item) {
          if (h.holding(2977, 2979, 2981, 2983)) {
            switch(h.weapon().ID) {
              case 2977: // stone hatchet
                f.number -= 1;
                p.global.sounds.trigger_units("items/melee/ax",
                  f.center().subtractR(this.curr_map.view));
                break;
              case 2979: // saw
                f.number -= 1;
                p.global.sounds.trigger_units("items/saw_cut_wood",
                  f.center().subtractR(this.curr_map.view));
                break;
              case 2981: // roundsaw
                f.number -= 1;
                p.global.sounds.trigger_units("items/roundsaw_cut_wood",
                  f.center().subtractR(this.curr_map.view));
                break;
              case 2983: // chainsaw
                f.number -= 4;
                p.global.sounds.trigger_units("items/chainsaw_long",
                  f.center().subtractR(this.curr_map.view));
                break;
            }
            h.weapon().lowerDurability();
            if (Misc.randomChance(LNZ.feature_treeDropChance)) {
              this.curr_map.addItem(new Item(p, branch_id, h.front()));
            }
            if (f.number < 1) {
              f.destroy(this.curr_map);
            }
          }
          else {
            this.curr_map.addHeaderMessage("Not correct tool to cut tree");
          }
        }
        else {
          if (f.toggle) {
            this.curr_map.addItem(new Item(p, branch_id, h.front()));
            p.global.sounds.trigger_units("features/break_branch" + Misc.randomInt(1, 6),
              f.center().subtractR(this.curr_map.view));
            if (Misc.randomChance(LNZ.feature_treeChanceEndBranches)) {
              f.toggle = false;
            }
          }
          else {
            this.curr_map.addHeaderMessage("No more branches in reach");
          }
        }
        break;
      case 440: // Underbrush
        if (use_item) {
          if (h.holding(2204, 2211) || (h.weapon() != null && h.weapon().ax())) {
            f.number--;
            if (Misc.randomChance(LNZ.feature_bushDropChance)) {
              this.curr_map.addItem(new Item(p, 2964, f.randomLocationUnderFeature()));
            }
            h.weapon().lowerDurability();
            if (f.number < 1) {
              f.remove = true;
            }
            if (h.holding(2204, 2211)) {
              p.global.sounds.trigger_units("features/sword_bush",
                f.center().subtractR(this.curr_map.view));
            }
            else {
              p.global.sounds.trigger_units("items/melee/ax",
                f.center().subtractR(this.curr_map.view));
            }
          }
          else {
            this.curr_map.addHeaderMessage("Not correct tool to cut bush");
          }
        }
        else {
          if (f.toggle) {
            new_i = new Item(p, 2964, h.front());
            this.curr_map.addItem(new_i);
            new_i.curr_height = f.curr_height;
            p.global.sounds.trigger_units("features/break_branch" + Misc.randomInt(1, 6),
              f.center().subtractR(this.curr_map.view));
            f.toggle = false;
          }
          else {
            this.curr_map.addHeaderMessage("No more kindling to gather");
          }
        }
        break;
      case 441: // Bush
      case 442:
      case 443:
        if (use_item) {
          if (h.holding(2204, 2211)) {
            f.number--;
            if (Misc.randomChance(LNZ.feature_bushDropChance)) {
              this.curr_map.addItem(new Item(p, 2964, f.randomLocationUnderFeature()));
            }
            h.weapon().lowerDurability();
            if (f.number < 1) {
              f.remove = true;
            }
            p.global.sounds.trigger_units("features/sword_bush",
              f.center().subtractR(this.curr_map.view));
          }
          else {
            this.curr_map.addHeaderMessage("Not correct tool to cut bush");
          }
        }
        else {
          if (f.toggle) {
            new_i = new Item(p, 2964, h.front());
            this.curr_map.addItem(new_i);
            new_i.curr_height = f.curr_height;
            p.global.sounds.trigger_units("features/break_branch" + Misc.randomInt(1, 6),
              f.center().subtractR(this.curr_map.view));
            f.toggle = false;
          }
          else {
            this.curr_map.addHeaderMessage("No more kindling to gather");
          }
        }
        break;
      case 451: // Wapato
      case 452: // Leek
      case 453: // Ryegrass
      case 454: // Barnyard Grass
      case 455: // Broadleaf Plantain
        if (h.weapon() != null && h.weapon().waterBottle()) {
          if (h.weapon().ammo >= 20) {
            this.curr_map.waterGround(f.gridLocation());
            h.weapon().ammo -= 20;
          }
          else {
            this.curr_map.addHeaderMessage("Not enough water to water plant.");
          }
          break;
        }
        if (use_item) {
          f.destroy(this.curr_map);
          p.global.sounds.trigger_units("features/destroy_plant" + Misc.randomInt(1, 2),
            f.center().subtractR(this.curr_map.view));
        }
        break;
      case 456: // Stinging Nettle
        if (h.weapon() != null && h.weapon().waterBottle()) {
          if (h.weapon().ammo >= 20) {
            this.curr_map.waterGround(f.gridLocation());
            h.weapon().ammo -= 20;
          }
          else {
            this.curr_map.addHeaderMessage("Not enough water to water plant.");
          }
          break;
        }
        if (use_item) {
          if (h.weapon() != null && h.weapon().hoe()) {
            f.destroy(this.curr_map);
            h.weapon().lowerDurability();
            p.global.sounds.trigger_units("features/destroy_plant" + Misc.randomInt(1, 2),
              f.center().subtractR(this.curr_map.view));
          }
          else {
            this.curr_map.addHeaderMessage("Need a hoe to harvest stinging nettle.");
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
          if (h.weapon() != null && (h.weapon().hoe() || h.weapon().ax())) {
            f.destroy(this.curr_map);
            h.weapon().lowerDurability();
            p.global.sounds.trigger_units("features/destroy_plant" + Misc.randomInt(1, 2),
              f.center().subtractR(this.curr_map.view));
          }
          else {
            this.curr_map.addHeaderMessage("Need a hoe or ax to uproot saplings.");
          }
        }
        break;
      case 501: // car
      case 502:
      case 503:
      case 504:
      case 505:
        if (use_item) {
          if (h.holding(2929)) {
            if (h.weapon().ammo < 1) {
              this.curr_map.addHeaderMessage("The gas can is empty");
            }
            else if (f.number >= f.gasTankSize()) {
              this.curr_map.addHeaderMessage("The car's gas tank is full");
            }
            else {
              int amount_to_fill = Math.min(8, Math.min(h.weapon().ammo, f.gasTankSize() - f.number));
              h.weapon().ammo -= amount_to_fill;
              f.number += amount_to_fill;
              p.global.sounds.trigger_units("player/pour_fuel",
                f.center().subtractR(this.curr_map.view));
            }
          }
          else if (h.holding(2906)) {
            if (h.weapon().unlocks(f.ID)) {
              if (f.number > 0) {
                this.level_form = new VehicleForm(p, f, h);
                p.global.defaultCursor();
              }
              else {
                this.curr_map.addHeaderMessage("The car is out of gas");
              }
            }
            else {
              this.curr_map.addHeaderMessage("This key doesn't operate that car");
            }
          }
          else {
            this.curr_map.addHeaderMessage("You need a key to operate this car");
          }
        }
        break;
      default:
        p.global.errorMessage("ERROR: Hero " + h.displayName() + " trying to " +
          "interact with feature " + f.displayName() + " but no interaction logic found.");
        break;
    }
  }


  boolean isNight() {
    return DayCycle.dayTime(this.time.value()) == DayCycle.NIGHT;
  }
  boolean isDay() {
    return DayCycle.dayTime(this.time.value()) == DayCycle.DAY;
  }
  boolean isDawn() {
    return DayCycle.dayTime(this.time.value()) == DayCycle.DAWN;
  }
  boolean isDusk() {
    return DayCycle.dayTime(this.time.value()) == DayCycle.DUSK;
  }


  void complete() {
    this.complete(0);
  }
  void complete(int completion_code) {
    if (this.completing || this.completed || this.location.isArea()) {
      return;
    }
    this.completion_code = completion_code;
    this.completing = true;
    this.save();
    if (this.curr_map != null) {
      this.curr_map.draw_fog = false;
    }
    p.global.sounds.play_background("victory");
  }


  void restartTimers() {
    this.restart_timers = true;
  }
  void restartTimers(int millis) {
    this.last_update_time = millis;
    if (this.curr_map != null) {
      this.curr_map.last_update_time = millis;
    }
  }


  void update(int millis) {
    if (this.restart_timers) {
      this.restart_timers = false;
      this.restartTimers(millis);
    }
    int time_elapsed = millis - this.last_update_time;
    if (this.completing || this.completed) {
      if (this.curr_map != null) {
        this.curr_map.updateView(time_elapsed);
        this.curr_map.drawMap(false);
      }
      else {
        p.rectMode(PConstants.CORNERS);
        p.noStroke();
        p.fill(DImg.ccolor(60));
        p.rect(this.xi, this.yi, this.xf, this.yf);
      }
      this.completing_timer -= time_elapsed;
      if (this.completing_timer < 0 && this.completed_button == null) {
        this.completed_button = new CompletedButton(p);
      }
      p.rectMode(PConstants.CORNERS);
      p.fill(100, 100);
      p.noStroke();
      p.rect(this.xi, this.yi, this.xf, this.yf);
      p.fill(255);
      p.textSize(90);
      p.textAlign(PConstants.CENTER, PConstants.CENTER);
      p.text("You are Victorious!", 0.5 * p.width, 0.5 * p.height);
      if (this.completed_button != null) {
        this.completed_button.update(time_elapsed);
      }
      this.last_update_time = millis;
      // draw map
      return;
    }
    if (this.player != null) {
      if (this.player.hero_tree.curr_viewing) {
        this.was_viewing_hero_tree = true;
        if (this.player.hero_tree.set_screen_location) {
          this.player.hero_tree.set_screen_location = false;
          p.global.defaultCursor();
          this.player.hero_tree.setLocation(this.xi, this.yi, this.xf, this.yf);
        }
        this.player.hero_tree.update(time_elapsed);
        this.last_update_time = millis;
        return;
      }
      else if (this.was_viewing_hero_tree) {
        this.was_viewing_hero_tree = false;
        this.restartTimers();
        p.global.defaultCursor();
      }
    }
    if (this.level_form != null) {
      this.level_form.update(millis);
      if (this.level_form.canceled) {
        this.level_form = null;
        this.restartTimers(millis);
      }
      this.last_update_time = millis;
      return;
    }
    this.time.add(time_elapsed * LNZ.level_timeConstants);
    if (this.curr_map != null) {
      this.curr_map.base_light_level = DayCycle.lightFraction(this.time.value());
      if (this.respawning) {
        this.curr_map.grayscale_image = true;
      }
      else {
        this.curr_map.grayscale_image = false;
      }
      this.curr_map.update(millis);
      this.zombie_spawn_params.update(time_elapsed, this);
      for (Map.Entry<Integer, Trigger> entry : this.triggers.entrySet()) {
        entry.getValue().update(time_elapsed, this);
      }
      if (this.location.isArea()) {
        ((GameMapArea)this.curr_map).trySpawnUnits(this, time_elapsed);
      }
      if (this.player != null) {
        if (this.player.curr_action == UnitAction.HERO_INTERACTING_WITH_FEATURE) {
          this.heroFeatureInteraction(this.player, false);
          this.player.stopAction();
        }
        else if (this.player.curr_action == UnitAction.HERO_INTERACTING_WITH_FEATURE_WITH_ITEM) {
          this.heroFeatureInteraction(this.player, true);
          this.player.stopAction();
        }
        for (Linker linker : this.linkers) {
          if (linker.rect1.contains(this.player, this.currMapName)) {
            this.movePlayerTo(linker.rect2);
          }
        }
        if (this.player.inventory.item_dropping != null) {
          this.curr_map.addItem(new Item(p, this.player.inventory.item_dropping,
            this.player.front()));
          this.player.inventory.item_dropping = null;
        }
        for (Item i : this.player.inventory.more_items_dropping) {
          this.curr_map.addItem(new Item(p, i, this.player.front()));
        }
        this.player.inventory.more_items_dropping.clear();
        if (this.player.inventory.viewing) {
          if (this.player.inventory.item_holding != null) {
            this.curr_map.selected_object = this.player.inventory.item_holding;
          }
        }
        if (this.player.messages.peek() != null) {
          this.curr_map.addHeaderMessage(this.player.messages.poll());
        }
      }
    }
    else {
      p.rectMode(PConstants.CORNERS);
      p.noStroke();
      p.fill(DImg.ccolor(60));
      p.rect(this.xi, this.yi, this.xf, this.yf);
    }
    if (this.player != null) {
      if (this.respawning) {
        this.respawn_timer -= time_elapsed;
        if (this.respawn_timer < 0) {
          this.respawnPlayer();
          this.respawning = false;
        }
      }
      else if (this.sleeping) {
        this.sleep_timer -= time_elapsed;
        if (this.sleep_timer < 0) {
          this.gainControl();
          this.player.healPercent(0.5, false);
          this.sleeping = false;
          this.player_spawn_location = new Rectangle(this.currMapName, this.player);
          this.time.set(6);
          if (this.curr_map != null) {
            this.curr_map.addHeaderMessage("Spawn Point Reset");
          }
        }
      }
      else {
        this.player.update_hero(time_elapsed);
        for (Map.Entry<Integer, Quest> entry : this.quests.entrySet()) {
          entry.getValue().update(this, time_elapsed);
        }
        if (this.player.seesTime()) {
          p.fill(255);
          p.textSize(14);
          p.textAlign(PConstants.LEFT, PConstants.TOP);
          String time_line = this.timeString();
          p.text(time_line, this.xf - 40 - p.textWidth(time_line), 1);
        }
        if (this.player.remove && !this.respawning) {
          this.respawning = true;
          this.respawn_timer = LNZ.level_defaultRespawnTimer;
        }
      }
    }
    if (this.respawning) {
      p.fill(255);
      p.textSize(90);
      p.textAlign(PConstants.CENTER, PConstants.BOTTOM);
      p.text("You Died", 0.5 * p.width, 0.5 * p.height);
      p.textSize(25);
      p.textAlign(PConstants.CENTER, PConstants.TOP);
      p.text("from: " + this.player.last_damage_source.toString(), 0.5 * p.width, 0.5 * p.height);
      p.textSize(45);
      p.text("Respawning in " + (int)Math.ceil(this.respawn_timer * 0.001) + " s",
        0.5 * p.width, 0.5 * p.height + 30);
    }
    if (this.sleeping) {
      p.rectMode(PConstants.CORNERS);
      p.noStroke();
      int alpha_amount = (int)(255d * ((1d - this.sleep_timer) / LNZ.feature_bedSleepTimer));
      p.fill(DImg.ccolor(0, alpha_amount));
      p.rect(this.xi, this.yi, this.xf, this.yf);
    }
    this.last_update_time = millis;
  }

  void displayNerdStats() {
    if (this.curr_map != null) {
      this.curr_map.displayNerdStats();
    }
    else {
      p.fill(255);
      p.textSize(14);
      p.textAlign(PConstants.LEFT, PConstants.TOP);
      double y_stats = 1;
      double line_height = p.textAscent() + p.textDescent() + 2;
      p.text("FPS: " + (int)p.global.lastFPS, this.xi + 1, y_stats);
      y_stats += line_height;
      p.text("Active Threads: " + Thread.getAllStackTraces().size(), this.xi + 1, y_stats);
    }
  }

  void mouseMove(float mX, float mY) {
    if (this.completing || this.completed) {
      if (this.completed_button != null) {
        this.completed_button.mouseMove(mX, mY);
        if (this.curr_map != null) {
          this.curr_map.mouseMove(mX, mY);
        }
      }
      return;
    }
    if (this.level_questbox != null) {
      this.level_questbox.mouseMove(mX, mY);
    }
    if (this.level_chatbox != null) {
      this.level_chatbox.mouseMove(mX, mY);
    }
    if (this.player != null) {
      if (this.player.hero_tree.curr_viewing) {
        this.player.hero_tree.mouseMove(mX, mY);
        if (this.player.left_panel_menu != null) {
          this.player.left_panel_menu.mouseMove(mX, mY);
        }
        return;
      }
      else {
        this.player.mouseMove_hero(mX, mY);
      }
    }
    if (this.level_form != null) {
      this.level_form.mouseMove(mX, mY);
      return;
    }
    if (this.curr_map != null) {
      this.curr_map.mouseMove(mX, mY);
      if (this.player != null && this.player.inventory_bar.hovered) {
        this.curr_map.hovered_object = null;
        p.global.defaultCursor("icons/cursor_interact.png", "icons/cursor_attack.png", "icons/cursor_pickup.png");
      }
    }
  }

  void mousePress() {
    if (this.level_questbox != null) {
      this.level_questbox.mousePress();
    }
    if (this.level_chatbox != null) {
      this.level_chatbox.mousePress();
    }
    if (this.completing || this.completed) {
      if (this.completed_button != null) {
        this.completed_button.mousePress();
      }
      if (this.curr_map != null && p.mouseButton == PConstants.LEFT) {
        this.curr_map.mousePress();
      }
      return;
    }
    if (this.player != null) {
      if (this.player.hero_tree.curr_viewing) {
        this.player.hero_tree.mousePress();
        if (this.player.left_panel_menu != null) {
          this.player.left_panel_menu.mousePress();
        }
        return;
      }
      else {
        this.player.mousePress_hero();
      }
    }
    if (this.level_form != null) {
      this.level_form.mousePress();
      return;
    }
    if (this.curr_map != null) {
      this.curr_map.mousePress();
    }
  }

  void mouseRelease(float mX, float mY) {
    if (this.level_questbox != null) {
      this.level_questbox.mouseRelease(mX, mY);
    }
    if (this.level_chatbox != null) {
      this.level_chatbox.mouseRelease(mX, mY);
    }
    if (this.completing || this.completed) {
      if (this.completed_button != null) {
        this.completed_button.mouseRelease(mX, mY);
      }
      if (this.curr_map != null && p.mouseButton == PConstants.LEFT) {
        this.curr_map.mouseRelease(mX, mY);
      }
      return;
    }
    if (this.player != null) {
      if (this.player.hero_tree.curr_viewing) {
        this.player.hero_tree.mouseRelease(mX, mY);
        if (this.player.left_panel_menu != null) {
          this.player.left_panel_menu.mouseRelease(mX, mY);
        }
        if (!this.player.hero_tree.curr_viewing) {
          this.was_viewing_hero_tree = false;
          this.restartTimers();
          p.global.defaultCursor();
        }
        return;
      }
      else {
        this.player.mouseRelease_hero(mX, mY);
      }
    }
    if (this.level_form != null) {
      this.level_form.mouseRelease(mX, mY);
      return;
    }
    if (this.curr_map != null) {
      this.curr_map.mouseRelease(mX, mY);
    }
  }

  void scroll(int amount) {
    if (this.level_questbox != null) {
      this.level_questbox.scroll(amount);
    }
    if (this.level_chatbox != null) {
      this.level_chatbox.scroll(amount);
    }
    if (this.completing || this.completed) {
      if (this.curr_map != null && p.mouseButton == PConstants.LEFT) {
        this.curr_map.scroll(amount);
      }
      return;
    }
    if (this.player != null) {
      if (this.player.hero_tree.curr_viewing) {
        this.player.hero_tree.scroll(amount);
        return;
      }
      else {
        this.player.scroll_hero(amount);
      }
    }
    if (this.level_form != null) {
      this.level_form.scroll(amount);
      return;
    }
    if (this.curr_map != null) {
      this.curr_map.scroll(amount);
    }
  }

  void keyPress(int key, int keyCode) {
    if (this.player != null) {
      if (this.player.hero_tree.curr_viewing) {
        this.player.hero_tree.keyPress(key, keyCode);
        if (key == PConstants.CODED) {
          switch(keyCode) {
          }
        }
        else {
          switch(key) {
            case PConstants.ESC:
              this.player.hero_tree.curr_viewing = false;
              this.restartTimers();
              p.global.defaultCursor();
              break;
            case 't':
            case 'T':
              if (p.global.holding_ctrl) {
                this.player.hero_tree.curr_viewing = false;
                this.restartTimers();
                p.global.defaultCursor();
              }
              break;
          }
        }
        if (!this.player.hero_tree.curr_viewing) {
          this.restartTimers();
          p.global.defaultCursor();
        }
        return;
      }
      else {
        this.player.keyPress_hero(key, keyCode);
      }
    }
    if (this.level_form != null) {
      this.level_form.keyPress(key, keyCode);
      return;
    }
    if (this.curr_map != null) {
      this.curr_map.keyPress(key, keyCode);
    }
    if (key == PConstants.CODED) {
      switch(keyCode) {
      }
    }
    else {
      switch(key) {
        case 't':
        case 'T':
          if (p.global.holding_ctrl && this.player != null) {
            this.player.hero_tree.curr_viewing = true;
            p.global.defaultCursor();
            this.player.hero_tree.setLocation(this.xi, this.yi, this.xf, this.yf);
          }
          break;
        case 'c':
        case 'C':
          if (p.global.holding_ctrl && this.player != null) {
            this.complete();
          }
          break;
      }
    }
  }

  void keyRelease(int key, int keyCode) {
    if (this.player != null) {
      if (this.player.hero_tree.curr_viewing) {
        this.player.hero_tree.keyRelease(key, keyCode);
        return;
      }
      else {
        this.player.keyRelease_hero(key, keyCode);
      }
    }
    if (this.level_form != null) {
      this.level_form.keyRelease(key, keyCode);
      return;
    }
    if (this.curr_map != null) {
      this.curr_map.keyRelease(key, keyCode);
    }
  }


  void loseFocus() {
    if (this.curr_map != null) {
      this.curr_map.loseFocus();
    }
  }

  void gainFocus() {
    if (this.curr_map != null) {
      this.curr_map.gainFocus();
    }
  }


  String finalFolderPath() {
    return this.folderPath + "/" + this.folderName();
  }
  String folderName() {
    if (this.location == Location.ERROR) {
      return this.levelName;
    }
    else {
      return this.location.fileName();
    }
  }


  void save() {
    this.save(true);
  }
  void save(boolean saveMap) {
    String finalFolderPath = this.finalFolderPath();
    if (!FileSystem.folderExists(p, finalFolderPath)) {
      FileSystem.mkdir(p, finalFolderPath);
    }
    PrintWriter file = p.createWriter(finalFolderPath + "/level.lnz");
    file.println("new: Level");
    file.println("levelName: " + this.levelName);
    file.println("location: " + this.location.fileName());
    file.println("completed: " + this.completed);
    file.println("completing: " + this.completing);
    file.println("completion_code: " + this.completion_code);
    file.println("time: " + this.time.value());
    file.println("respawning: " + this.respawning);
    file.println("respawn_timer: " + this.respawn_timer);
    file.println("sleeping: " + this.sleeping);
    file.println("sleep_timer: " + this.sleep_timer);
    file.println("in_control: " + this.in_control);
    if (this.zombie_spawn_params.save_params) {
      file.println("max_zombies_per_square: " + this.zombie_spawn_params.max_zombies_per_square);
      file.println("min_level: " + this.zombie_spawn_params.min_level);
      file.println("max_level: " + this.zombie_spawn_params.max_level);
      file.println("del_level: " + this.zombie_spawn_params.del_level);
      file.println("group_size: " + this.zombie_spawn_params.group_size);
      file.println("del_group_size: " + this.zombie_spawn_params.del_group_size);
      file.println("group_radius: " + this.zombie_spawn_params.group_radius);
      file.println("min_distance: " + this.zombie_spawn_params.min_distance);
      file.println("max_distance: " + this.zombie_spawn_params.max_distance);
    }
    if (this.currMapName != null) {
      file.println("currMapName: " + this.currMapName);
    }
    String mapNameList = "";
    for (int i = 0; i < this.mapNames.size(); i++) {
      if (i > 0) {
        mapNameList += ", ";
      }
      mapNameList += this.mapNames.get(i);
    }
    file.println("mapNames: " + mapNameList);
    for (Linker linker : this.linkers) {
      file.println(linker.fileString());
    }
    for (Map.Entry<Integer, Trigger> entry : this.triggers.entrySet()) {
      file.println("nextTriggerKey: " + entry.getKey());
      file.println(entry.getValue().fileString());
    }
    if (this.album_name != null) {
      file.println("album_name: " + this.album_name);
    }
    if (this.player_start_location != null) {
      file.println("player_start_location: " + this.player_start_location.fileString());
    }
    if (this.player_spawn_location != null) {
      file.println("player_spawn_location: " + this.player_spawn_location.fileString());
    }
    file.println("end: Level");
    file.flush();
    file.close();
    if (saveMap && this.curr_map != null) {
      this.curr_map.save(finalFolderPath);
    }
    p.global.profile.save(); // needed for ender chest to work properly
  }


  void open() {
    this.open2Data(this.open1File());
  }


  String[] open1File() {
    String finalFolderPath = this.finalFolderPath();
    String[] lines;
    lines = p.loadStrings(finalFolderPath + "/level.lnz");
    if (lines == null) {
      p.global.errorMessage("ERROR: Reading level at path " + finalFolderPath + " but no level file exists.");
      this.nullify = true;
    }
    return lines;
  }


  void open2Data(String[] lines) {
    if (lines == null) {
      this.nullify = true;
      p.global.errorMessage("ERROR: Null file data for level; possibly missing " +
        "file at " + this.finalFolderPath() + ".");
      return;
    }
    Stack<ReadFileObject> object_queue = new Stack<ReadFileObject>();

    Linker curr_linker = null;
    int max_trigger_key = 0;
    Trigger curr_trigger = null;
    Condition curr_condition = null;
    Effect curr_effect = null;

    for (String line : lines) {
      String[] parameters = PApplet.split(line, ':');
      if (parameters.length < 2) {
        continue;
      }

      String datakey = PApplet.trim(parameters[0]);
      String data = PApplet.trim(parameters[1]);
      for (int i = 2; i < parameters.length; i++) {
        data += ":" + parameters[i];
      }
      if (datakey.equals("new")) {
        ReadFileObject type = ReadFileObject.objectType(PApplet.trim(parameters[1]));
        switch(type) {
          case LEVEL:
            object_queue.push(type);
            break;
          case LINKER:
            object_queue.push(type);
            curr_linker = new Linker(p);
            break;
          case TRIGGER:
            object_queue.push(type);
            curr_trigger = new Trigger(p);
            break;
          case CONDITION:
            object_queue.push(type);
            curr_condition = new Condition(p);
            break;
          case EFFECT:
            object_queue.push(type);
            curr_effect = new Effect(p);
            break;
          default:
            p.global.errorMessage("ERROR: Can't add a " + type + " type to Level data.");
            break;
        }
      }
      else if (datakey.equals("end")) {
        ReadFileObject type = ReadFileObject.objectType(data);
        if (object_queue.empty()) {
          p.global.errorMessage("ERROR: Tring to end a " + type.name + " object but not inside any object.");
        }
        else if (type.name.equals(object_queue.peek().name)) {
          switch(object_queue.pop()) {
            case LEVEL:
              return;
            case LINKER:
              if (curr_linker == null) {
                p.global.errorMessage("ERROR: Trying to end a null linker.");
              }
              this.addLinker(curr_linker);
              curr_linker = null;
              break;
            case TRIGGER:
              if (curr_trigger == null) {
                p.global.errorMessage("ERROR: Trying to end a null trigger.");
              }
              if (this.nextTriggerKey > max_trigger_key) {
                max_trigger_key = this.nextTriggerKey;
              }
              this.addTrigger(curr_trigger);
              curr_trigger = null;
              break;
            case CONDITION:
              if (curr_condition == null) {
                p.global.errorMessage("ERROR: Trying to end a null condition.");
              }
              if (object_queue.peek() != ReadFileObject.TRIGGER) {
                p.global.errorMessage("ERROR: Trying to end a condition while not in a TRIGGER.");
              }
              if (curr_trigger == null) {
                p.global.errorMessage("ERROR: Trying to end an condition but curr_trigger is null.");
              }
              curr_condition.setName();
              curr_trigger.conditions.add(curr_condition);
              curr_condition = null;
              break;
            case EFFECT:
              if (curr_effect == null) {
                p.global.errorMessage("ERROR: Trying to end a null effect.");
              }
              if (object_queue.peek() != ReadFileObject.TRIGGER) {
                p.global.errorMessage("ERROR: Trying to end a effect while not in a TRIGGER.");
              }
              if (curr_trigger == null) {
                p.global.errorMessage("ERROR: Trying to end an effect but curr_trigger is null.");
              }
              curr_effect.setName();
              curr_trigger.effects.add(curr_effect);
              curr_effect = null;
              break;
            default:
              break;
          }
        }
        else {
          p.global.errorMessage("ERROR: Tring to end a " + type.name + " object while inside a " + object_queue.peek().name + " object.");
        }
      }
      else {
        switch(object_queue.peek()) {
          case LEVEL:
            this.addData(datakey, data);
            break;
          case LINKER:
            if (curr_linker == null) {
              p.global.errorMessage("ERROR: Trying to add linker data to null linker.");
            }
            curr_linker.addData(datakey, data);
            break;
          case TRIGGER:
            if (curr_trigger == null) {
              p.global.errorMessage("ERROR: Trying to add trigger data to null trigger.");
            }
            curr_trigger.addData(datakey, data);
            break;
          case CONDITION:
            if (curr_condition == null) {
              p.global.errorMessage("ERROR: Trying to add condition data to null trigger.");
            }
            curr_condition.addData(datakey, data);
            break;
          case EFFECT:
            if (curr_effect == null) {
              p.global.errorMessage("ERROR: Trying to add effect data to null trigger.");
            }
            curr_effect.addData(datakey, data);
            break;
          default:
            // before or after actual file data
            break;
        }
      }
    }

    this.nextTriggerKey = max_trigger_key + 1;
  }


  void addData(String datakey, String data) {
    switch(datakey) {
      case "levelName":
        this.levelName = data;
        break;
      case "location":
        this.location = Location.location(data);
        break;
      case "completed":
        this.completed = Misc.toBoolean(data);
        break;
      case "completing":
        this.completing = Misc.toBoolean(data);
        break;
      case "completion_code":
        this.completion_code = Misc.toInt(data);
        break;
      case "time":
        this.time.set(Misc.toDouble(data));
        break;
      case "respawning":
        this.respawning = Misc.toBoolean(data);
        break;
      case "respawn_timer":
        this.respawn_timer = Misc.toInt(data);
        break;
      case "sleeping":
        this.sleeping = Misc.toBoolean(data);
        break;
      case "sleep_timer":
        this.sleep_timer = Misc.toInt(data);
        break;
      case "max_zombies_per_square":
        this.zombie_spawn_params.max_zombies_per_square = Misc.toDouble(data);
        break;
      case "min_level":
        this.zombie_spawn_params.min_level = Misc.toInt(data);
        break;
      case "max_level":
        this.zombie_spawn_params.max_level = Misc.toInt(data);
        break;
      case "del_level":
        this.zombie_spawn_params.del_level = Misc.toInt(data);
        break;
      case "group_size":
        this.zombie_spawn_params.group_size = Misc.toInt(data);
        break;
      case "del_group_size":
        this.zombie_spawn_params.del_group_size = Misc.toInt(data);
        break;
      case "group_radius":
        this.zombie_spawn_params.group_radius = Misc.toDouble(data);
        break;
      case "min_distance":
        this.zombie_spawn_params.min_distance = Misc.toDouble(data);
        break;
      case "max_distance":
        this.zombie_spawn_params.max_distance = Misc.toDouble(data);
        break;
      case "in_control":
        if (Misc.toBoolean(data)) {
          this.gainControl();
        }
        else {
          this.loseControl();
        }
        break;
      case "currMapName":
        this.currMapName = data;
        break;
      case "album_name":
        this.album_name = data;
        break;
      case "mapNames":
        String[] map_names = PApplet.split(data, ',');
        for (String map_name : map_names) {
          if (!map_name.equals("")) {
            this.mapNames.add(PApplet.trim(map_name));
          }
        }
        break;
      case "nextTriggerKey":
        this.nextTriggerKey = Misc.toInt(data);
        break;
      case "player_start_location":
        this.player_start_location = new Rectangle(p);
        this.player_start_location.addData(data);
        break;
      case "player_spawn_location":
        this.player_spawn_location = new Rectangle(p);
        this.player_spawn_location.addData(data);
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not recognized for Level object.");
        break;
    }
  }



  class LevelQuestBox extends ListTextBox {
    private LNZ p;
    LevelQuestBox(LNZ sketch) {
      super(sketch, sketch.width, LNZ.mapEditor_listBoxGap, sketch.width, LNZ.
        level_questBoxHeightRatio * sketch.height - LNZ.mapEditor_listBoxGap);
      this.p = sketch;
      this.color_background = DImg.ccolor(250, 190, 140);
      this.color_header = DImg.ccolor(220, 180, 130);
      this.scrollbar.setButtonColors(DImg.ccolor(220), DImg.ccolor(220, 160, 110), DImg.ccolor(
        240, 180, 130), DImg.ccolor(200, 140, 90), DImg.ccolor(0));
      this.scrollbar.button_upspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      this.scrollbar.button_downspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      this.setTitleText("Quests");
    }

    @Override
    public void update(int millis) {
      this.clearText();
      boolean first = true;
      ArrayList<Quest> quests = new ArrayList<Quest>();
      for (Map.Entry<Integer, Quest> entry : Level.this.quests.entrySet()) {
        quests.add(entry.getValue());
        if (first) {
          this.setText(entry.getValue().name());
          first = false;
        }
        else {
          this.addLine(entry.getValue().name());
        }
      }
      if (this.line_hovered >= this.text_lines_ref.size()) {
        this.line_hovered = -1;
      }
      if (this.line_clicked >= this.text_lines_ref.size()) {
        this.line_clicked = this.text_lines_ref.size() - 1;
      }
      int timeElapsed = millis - this.last_update_time;
      p.rectMode(PConstants.CORNERS);
      p.fill(this.color_background);
      p.stroke(this.color_stroke);
      p.strokeWeight(1);
      p.rect(this.xi, this.yi, this.xf, this.yf);
      double currY = this.yi + 1;
      if (this.text_title_ref != null) {
        p.fill(this.color_header);
        p.textSize(this.title_size);
        p.rect(this.xi, this.yi, this.xf, this.yi + p.textAscent() + p.textDescent() + 1);
        p.fill(this.color_title);
        p.textAlign(PConstants.CENTER, PConstants.TOP);
        p.text(this.text_title, this.xi + 0.5 * (this.xf - this.xi), currY);
        currY += p.textAscent() + p.textDescent() + 2;
      }
      p.textAlign(PConstants.LEFT, PConstants.TOP);
      p.textSize(this.text_size);
      double text_height = p.textAscent() + p.textDescent();
      for (int i = (int)Math.floor(this.scrollbar.value); i < this.text_lines.size(); i++, currY += text_height + this.text_leading) {
        if (currY + text_height + 1 > this.yf) {
          break;
        }
        if (i < 0 || i >= quests.size()) {
          continue;
        }
        p.fill(this.color_text);
        if (quests.get(i).blinking) {
          p.fill(DImg.ccolor(170));
        }
        if (this.word_wrap) {
          p.text(this.text_lines.get(i), this.xi + 2, currY);
        }
        else {
          p.text(this.truncateLine(this.text_lines.get(i)), this.xi + 2, currY);
        }
        if (quests.get(i).met) {
          p.fill(0);
          p.line(this.xi + 1, currY + 0.5 * text_height, this.xf - 1, currY + 0.5 * text_height);
        }
      }
      if (this.scrollbar.maxValue != this.scrollbar.minValue) {
        this.scrollbar.update(millis);
      }
      if (!this.word_wrap) {
        if (this.scrollbar_horizontal.maxValue != this.scrollbar_horizontal.minValue) {
          this.scrollbar_horizontal.update(millis);
        }
      }
      this.last_update_time = millis;
      if (this.doubleclick_timer > 0) {
        this.doubleclick_timer -= timeElapsed;
      }
      currY = this.yi + 1;
      if (this.text_title_ref != null) {
        p.textSize(this.title_size);
        currY += p.textAscent() + p.textDescent() + 2;
      }
      p.textSize(this.text_size);
      text_height = p.textAscent() + p.textDescent();
      if (this.line_hovered >= Math.floor(this.scrollbar.value)) {
        double hovered_yi = currY + (this.line_hovered - Math.floor(this.scrollbar.value)) * (text_height + this.text_leading);
        if (hovered_yi + text_height + 1 < this.yf) {
          p.rectMode(PConstants.CORNERS);
          p.fill(this.hover_color);
          p.strokeWeight(0.001);
          p.stroke(this.hover_color);
          p.rect(this.xi + 1, hovered_yi, this.xf - 2 - this.scrollbar.bar_size, hovered_yi + text_height);
        }
      }
      if (this.line_clicked >= Math.floor(this.scrollbar.value)) {
        double clicked_yi = currY + (this.line_clicked - Math.floor(this.scrollbar.value)) * (text_height + this.text_leading);
        if (clicked_yi + text_height + 1 < this.yf) {
          p.rectMode(PConstants.CORNERS);
          p.fill(this.highlight_color);
          p.strokeWeight(0.001);
          p.stroke(this.highlight_color);
          p.rect(this.xi + 1, clicked_yi, this.xf - 2 - this.scrollbar.bar_size, clicked_yi + text_height);
          if (this.line_hovered == this.line_clicked) {
            try {
              String tooltip = quests.get(this.line_clicked).shortDescription();
              double tooltip_width = p.textWidth(tooltip) + 2;
              p.noStroke();
              p.fill(p.global.color_nameDisplayed_background);
              p.rect(p.mouseX - tooltip_width - 2, p.mouseY + 2, p.mouseX - 2, p.mouseY + 2 + text_height + 2);
              p.fill(p.global.color_nameDisplayed_text);
              p.textAlign(PConstants.LEFT, PConstants.TOP);
              p.text(tooltip, p.mouseX - tooltip_width - 1, p.mouseY + 1);
            } catch(Exception e) {}
          }
        }
      }
    }

    public void click() {
    }

    public void doubleclick() {
    }
  }


  void chat(String message) {
    if (this.level_chatbox == null) {
      return;
    }
    this.level_chatbox.chat(message);
  }

  class LevelChatBox extends TextBox {
    private LNZ p;
    protected boolean first_message = true;

    LevelChatBox(LNZ sketch) {
      super(sketch, sketch.width, LNZ.level_questBoxHeightRatio * sketch.height,
        sketch.width, 0.9 * sketch.height - LNZ.mapEditor_listBoxGap);
      this.p = sketch;
      this.color_background = DImg.ccolor(250, 190, 140);
      this.color_header = DImg.ccolor(220, 180, 130);
      this.scrollbar.setButtonColors(DImg.ccolor(220), DImg.ccolor(220, 160, 110), DImg.ccolor(
        240, 180, 130), DImg.ccolor(200, 140, 90), DImg.ccolor(0));
      this.scrollbar.button_upspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      this.scrollbar.button_downspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      this.setTitleText("Chat Log");
    }

    void chat(String message) {
      p.global.sounds.trigger_player("player/chat");
      if (this.first_message) {
        this.addText(message);
        this.first_message = false;
      }
      else {
        this.addText("\n\n" + message);
      }
      this.scrollbar.scrollMax();
    }
  }



  abstract class LevelForm extends FormLNZ {
    LevelForm(LNZ sketch, double xi, double yi, double xf, double yf) {
      super(sketch, xi, yi, xf, yf);
      this.scrollbar_max_width = 35;
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(211, 188, 141);
      this.color_header = DImg.ccolor(220, 200, 150);
      p.global.defaultCursor();
      this.min_x = Level.this.xi;
      this.min_y = Level.this.yi;
      this.max_x = Level.this.xf;
      this.max_y = Level.this.yf;
    }
  }



  class DecisionForm extends LevelForm {
    protected int ID = 0;

    DecisionForm(LNZ sketch, int ID) {
      super(sketch,
        0.5 * (sketch.width - LNZ.level_decisionFormWidth),
        0.5 * (sketch.height - LNZ.level_decisionFormHeight),
        0.5 * (sketch.width + LNZ.level_decisionFormWidth),
        0.5 * (sketch.height + LNZ.level_decisionFormHeight));
      this.ID = ID;
      this.cancel = null;
      this.setFieldCushion(0);
      this.addField(new SpacerFormField(p, 20));

      switch(ID) {
        case 1: // francis hall initial cut scene
          this.addField(new MessageFormField(p, "Ben puts in earbuds to drown out the stupid conversation."));
          this.addField(new SpacerFormField(p, 20));
          RadiosFormField radios = new RadiosFormField(p, "What music does he play?");
          radios.addRadio("Thompson Twins");
          radios.addRadio("Now 2");
          this.addField(radios);
          this.addField(new SpacerFormField(p, 20));
          this.addField(new SubmitFormField(p, "Listen"));
          break;
        default:
          p.global.errorMessage("ERROR: Decision form ID " + ID + " not recognized.");
          break;
      }
    }

    public void cancel() {}

    public void submit() {
      switch(ID) {
        case 1: // francis hall initial cut scene
          switch(this.fields.get(3).getValue()) {
            case "0":
              p.global.sounds.play_background("thompson");
              break;
            case "1":
              p.global.sounds.play_background("now2");
              break;
            default:
              p.global.sounds.play_background("starset");
              break;
          }
          try {
            p.global.profile.options.volume_music = 100;
            p.global.profile.options.volume_music_muted = false;
            p.global.profile.options.change();
          } catch(Exception e) {}
          break;
        default:
          p.global.errorMessage("ERROR: Decision form ID " + ID + " not recognized.");
          break;
      }
      this.canceled = true;
    }
  }



  class VendingForm extends LevelForm {
    protected Feature vending_machine;
    protected Hero hero_looking;

    VendingForm(LNZ sketch, Feature f, Hero h) {
      super(sketch,
        0.5 * (sketch.width - LNZ.level_vendingFormWidth),
        0.5 * (sketch.height - LNZ.level_vendingFormHeight),
        0.5 * (sketch.width + LNZ.level_vendingFormWidth),
        0.5 * (sketch.height + LNZ.level_vendingFormHeight));
      this.vending_machine = f;
      this.hero_looking = h;
      this.setTitleText(this.vending_machine.displayName());

      this.addField(new SpacerFormField(p, 20));
      ButtonsFormField insert_money = new ButtonsFormField(p, "Insert $1", "Insert $5");
      insert_money.button1.setColors(DImg.ccolor(170), DImg.ccolor(236, 213, 166), DImg.ccolor(211,
        188, 141), DImg.ccolor(190, 165, 120), DImg.ccolor(0));
      insert_money.button2.setColors(DImg.ccolor(170), DImg.ccolor(236, 213, 166), DImg.ccolor(211,
        188, 141), DImg.ccolor(190, 165, 120), DImg.ccolor(0));
      this.addField(insert_money);
      this.addField(new MessageFormField(p, "$" + this.vending_machine.number));
      this.addField(new MessageFormField(p, ""));
      RadiosFormField choices = new RadiosFormField(p, "Choices");
      switch(this.vending_machine.ID) {
        case 172: // food
          choices.addRadio("Chips, $1");
          choices.addRadio("Pretzels, $1");
          choices.addRadio("Chocolate, $2");
          choices.addRadio("Donut, $2");
          choices.addRadio("Poptart, $2");
          choices.addRadio("Peanuts, $1");
          break;
        case 173: // drink
          choices.addRadio("Water, $1");
          choices.addRadio("Coke, $1");
          choices.addRadio("Diet Coke, $1");
          choices.addRadio("Juice, $1");
          choices.addRadio("Energy Drink, $2");
          break;
        default:
          p.global.errorMessage("ERROR: Can't create VendingForm with feature of " +
            "ID " + this.vending_machine.ID + ".");
          break;
      }
      this.addField(choices);
      SubmitFormField vend = new SubmitFormField(p, " Vend ");
      vend.button.setColors(DImg.ccolor(170), DImg.ccolor(236, 213, 166), DImg.ccolor(211,
        188, 141), DImg.ccolor(190, 165, 120), DImg.ccolor(0));
      this.addField(vend);
    }

    @Override
    public void buttonPress(int i) {
      if (i != 1) {
        p.global.errorMessage("ERROR: Pressed button other than insert on VendingForm.");
        return;
      }
      int money_inserted = 1 + 4 * Misc.toInt(this.fields.get(1).getValue());
      if (this.hero_looking.money < money_inserted) {
        this.fields.get(3).setValue("You don't have $" + money_inserted + " to insert.");
        return;
      }
      this.hero_looking.money -= money_inserted;
      p.global.sounds.trigger_environment("features/vending_machine_coin",
        this.vending_machine.center().subtractR(Level.this.curr_map.view));
      if (Misc.randomChance(LNZ.feature_vendingEatMoneyChance)) {
        this.fields.get(3).setValue("The vending machine ate your money.");
        return;
      }
      this.vending_machine.number += money_inserted;
      this.fields.get(2).setValue("$" + this.vending_machine.number);
      this.fields.get(3).setValue("Please make your selection.");
    }

    public void submit() {
      int selection = Misc.toInt(this.fields.get(4).getValue());
      if (selection < 0) {
        this.fields.get(3).setValue("Please make a selection.");
        return;
      }
      int cost = 0;
      int item_id = 0;
      switch(this.vending_machine.ID) {
        case 172: // food
          switch(selection) {
            case 0:
              cost = 1;
              item_id = 2113;
              break;
            case 1:
              this.fields.get(3).setValue("Out of stock.");
              return;
            case 2:
              cost = 2;
              item_id = 2112;
              break;
            case 3:
              cost = 2;
              item_id = 2111;
              break;
            case 4:
              cost = 2;
              item_id = 2110;
              break;
            case 5:
              cost = 1;
              item_id = 2115;
              break;
          }
          break;
        case 173: // drink
          switch(selection) {
            case 0:
              cost = 1;
              item_id = 2924;
              break;
            case 1:
              cost = 1;
              item_id = 2132;
              break;
            case 2:
              this.fields.get(3).setValue("Out of stock.");
              return;
            case 3:
              cost = 1;
              item_id = 2133;
              break;
            case 4:
              this.fields.get(3).setValue("Out of stock.");
              return;
          }
          break;
      }
      if (this.vending_machine.number < cost) {
        this.fields.get(3).setValue("Please insert more money to purchase.");
        return;
      }
      this.vending_machine.number -= cost;
      Item new_item = new Item(p, item_id, this.vending_machine.coordinate.x +
        0.2 + Misc.randomDouble(0.4), this.vending_machine.coordinate.y + 0.85);
      if (item_id == 2924) {
        new_item.ammo = new_item.maximumAmmo();
      }
      Level.this.curr_map.addItem(new_item);
      p.global.sounds.trigger_environment("features/vending_machine_vend",
        this.vending_machine.center().subtractR(Level.this.curr_map.view));
      this.fields.get(2).setValue("$" + this.vending_machine.number);
      this.fields.get(3).setValue("Thank you for your purchase.");
    }
  }



  class QuizmoForm extends LevelForm {
    protected Feature chuck_quizmo;
    protected Hero hero;
    protected double last_update_time = 0;
    protected double time_before_cancel = LNZ.level_quizmoTimeDelay;
    protected boolean canceling = false;
    protected boolean correct_guess = false;
    protected boolean guessed = false;

    QuizmoForm(LNZ sketch, Feature f, Hero h) {
      super(sketch,
        0.5 * (sketch.width - LNZ.level_quizmoFormWidth),
        0.5 * (sketch.height - LNZ.level_quizmoFormHeight),
        0.5 * (sketch.width + LNZ.level_quizmoFormWidth),
        0.5 * (sketch.height + LNZ.level_quizmoFormHeight));
      this.chuck_quizmo = f;
      this.hero = h;
      this.setTitleText(this.chuck_quizmo.displayName());

      RadiosFormField question = new RadiosFormField(p, "");
      switch(this.chuck_quizmo.number) {
        case 0:
          question.setMessage("Test Question.");
          question.addRadio("Answer 1");
          question.addRadio("Answer 2");
          question.addRadio("Answer 3");
          question.addRadio("Answer 4");
          break;
        case 1: // Tutorial
          question.setMessage("Which of these is not part of Ben's penance?");
          question.addRadio("The Golden Rule");
          question.addRadio("Praying to Mary");
          question.addRadio("Telling everyone how much he hates them");
          question.addRadio("Being kinder to those around him");
          break;
        case 2: // Francis Hall
          question.setMessage("Which of these were options for Ben to listen to?");
          question.addRadio("Kalin Twins");
          question.addRadio("Now3");
          question.addRadio("Thompson Twins");
          question.addRadio("Joe Fagin");
          break;
        default:
          p.global.errorMessage("ERROR: Chuck Quizmo ID " + this.chuck_quizmo.number +
            " not found.");
          break;
      }

      this.addField(new SpacerFormField(p, 120));
      this.addField(question);
      this.addField(new SpacerFormField(p, 20));
      this.addField(new SubmitCancelFormField(p, "Guess!", "Leave"));
    }

    public static Location quizmoNumberToLocation(int number) {
      switch(number) {
        case 1:
          return Location.TUTORIAL;
        case 2:
          return Location.FRANCISCAN_FRANCIS;
        default:
          return Location.ERROR;
      }
    }

    @Override
    public void update(int millis) {
      super.update(millis);
      p.imageMode(PConstants.CORNER);
      p.image(this.hero.getImage(), this.xi + 20, this.yi + 40, 100, 100);
      p.image(p.global.images.getImage("features/chuck_quizmo.png"), this.xi + 160, this.yi + 40, 179.2, 134.4);
      int frame = PApplet.constrain((int)Math.floor(
        LNZ.gif_quizmoQuestion_frames * (p.millis() %
        LNZ.gif_quizmoQuestion_time) / LNZ.gif_quizmoQuestion_time),
        0, LNZ.gif_quizmoQuestion_frames);
      if (this.canceling) {
        this.time_before_cancel -= millis - this.last_update_time;
        if (this.time_before_cancel < 0) {
          this.canceled = true;
        }
        if (this.correct_guess) {
          p.image(p.global.images.getImage("features/vanna_t_smiling.png"), this.xi + 300, this.yi + 60, 80, 80);
          p.image(p.global.images.getImage("gifs/quizmo_correct/" + frame + ".png"),
            this.xi + 140, this.yi + 80, 60, 60);
        }
        else if (this.guessed) {
          p.image(p.global.images.getImage("features/vanna_t.png"), this.xi + 300, this.yi + 60, 80, 80);
          p.image(p.global.images.getImage("gifs/quizmo_wrong/" + frame + ".png"),
            this.xi + 140, this.yi + 80, 60, 60);
        }
        else {
          p.image(p.global.images.getImage("features/vanna_t.png"), this.xi + 300, this.yi + 60, 80, 80);
          p.image(p.global.images.getImage("gifs/quizmo_question/" + frame + ".png"),
            this.xi + 140, this.yi + 80, 60, 60);
        }
      }
      else {
        p.image(p.global.images.getImage("features/vanna_t.png"), this.xi + 300, this.yi + 60, 80, 80);
        p.image(p.global.images.getImage("gifs/quizmo_question/" + frame + ".png"),
          this.xi + 140, this.yi + 80, 60, 60);
      }
      this.last_update_time = millis;
    }

    @Override
    public void cancel() {
      this.canceling = true;
      Level.this.chat("Chuck Quizmo: Well, well... so long, farewell. 'Til we meet again!");
      Level.this.curr_map.addVisualEffect(4009, this.chuck_quizmo.coordinate.x + 0.7, this.chuck_quizmo.coordinate.y - 0.4);
    }

    public void submit() {
      int guess = Misc.toInt(this.fields.get(1).getValue());
      if (guess < 0) {
        Level.this.chat("Chuck Quizmo: You haven't made a guess!");
        Level.this.curr_map.addVisualEffect(4009, this.chuck_quizmo.coordinate.x + 0.7, this.chuck_quizmo.coordinate.y - 0.4);
        return;
      }
      if (this.guessed) {
        return;
      }
      this.guessed = true;
      this.fields.get(3).disable();
      int correct_answer = -1;
      switch(this.chuck_quizmo.number) {
        case 0:
          correct_answer = 1;
          break;
        case 1: // Tutorial
          correct_answer = 3;
          break;
        case 2: // Francis Hall
          correct_answer = 2;
          break;
        default:
          p.global.errorMessage("ERROR: Chuck Quizmo ID " + this.chuck_quizmo.number +
            " not found.");
          break;
      }
      if (guess == correct_answer) {
        if (this.chuck_quizmo.number == 0) {
          Level.this.chat("Chuck Quizmo: No star piece for test question!");
        }
        else if (p.global.profile.answeredChuckQuizmo(this.chuck_quizmo.number)) {
          if (hero.canPickup()) {
            hero.pickup(new Item(p, 2825));
          }
          else {
            Level.this.curr_map.addItem(new Item(p, 2825, this.hero.front()));
          }
          Level.this.chat("Chuck Quizmo: Congratulations! Here's your Star Piece!");
        }
        else {
          Level.this.chat("Chuck Quizmo: You've already won this Star Piece!");
        }
        hero.addExperience(5 + Math.pow(Math.min(this.chuck_quizmo.number, 10), 8));
        Level.this.curr_map.addVisualEffect(4009, this.chuck_quizmo.coordinate.x + 0.7, this.chuck_quizmo.coordinate.y - 0.4);
        this.correct_guess = true;
      }
      else {
        Level.this.chat("Chuck Quizmo: Too bad!");
        Level.this.curr_map.addVisualEffect(4009, this.chuck_quizmo.coordinate.x + 0.7, this.chuck_quizmo.coordinate.y - 0.4);
        this.correct_guess = false;
      }
      this.chuck_quizmo.destroy(Level.this.curr_map);
      this.canceling = true;
    }
  }



  class KhalilForm extends LevelForm {
    protected Inventory original_stock;
    protected Feature khalil;
    protected Hero hero;
    protected List<Double> costs;

    KhalilForm(LNZ sketch, Feature f, Hero h) {
      super(sketch,
        0.5 * (sketch.width - LNZ.level_khalilFormWidth),
        0.5 * (sketch.height - LNZ.level_khalilFormHeight),
        0.5 * (sketch.width + LNZ.level_khalilFormWidth),
        0.5 * (sketch.height + LNZ.level_khalilFormHeight));
      this.color_background = DImg.ccolor(102, 153, 204);
      this.color_header = DImg.ccolor(72, 120, 170);
      this.original_stock = Inventory.getKhalilInventory(sketch, f.number);
      this.costs = Inventory.getKhalilInventoryCosts(sketch, f.number);
      if (this.original_stock == null || this.costs == null) {
        this.canceled = true;
        return;
      }
      this.khalil = f;
      this.hero = h;
      this.setTitleText(this.khalil.displayName());
      if (this.khalil.inventory == null) {
        this.khalil.createKhalilInventory();
      }

      MessageFormField khalilMessageField = new MessageFormField(p, "Please take a look at my inventory of goods.");
      khalilMessageField.setTextSize(18, true);
      RadiosFormField radiosField = new RadiosFormField(p, "Inventory");
      for (int i = 0; i < this.original_stock.slots.size(); i++) {
        String item_name = this.original_stock.slots.get(i).item.displayName();
        Item stock_item = this.khalil.inventory.slots.get(i).item;
        int stock_amount = 0;
        if (stock_item != null && !stock_item.remove) {
          stock_amount = stock_item.stack;
        }
        if (stock_amount > 0) {
          radiosField.addRadio(item_name + " (" + stock_amount + " left): $" + this.costs.get(i));
        }
        else {
          radiosField.addDisabledRadio(item_name + " (out of stock): $" + this.costs.get(i));
        }
      }
      SubmitCancelFormField buttons = new SubmitCancelFormField(p, "Purchase", "Leave");
      buttons.button1.setColors(DImg.ccolor(170), DImg.ccolor(127, 178, 229), DImg.ccolor(102,
        153, 204), DImg.ccolor(80, 128, 179), DImg.ccolor(0));
      buttons.button2.setColors(DImg.ccolor(170), DImg.ccolor(127, 178, 229), DImg.ccolor(102,
        153, 204), DImg.ccolor(80, 128, 179), DImg.ccolor(0));

      this.addField(new SpacerFormField(p, 110));
      this.addField(khalilMessageField);
      this.addField(new SpacerFormField(p, 5));
      this.addField(radiosField);
      this.addField(new SpacerFormField(p, 20));
      this.addField(buttons);
    }


    @Override
    public void update(int millis) {
      super.update(millis);
      if (this.khalil == null) {
        return;
      }
      p.imageMode(PConstants.CENTER);
      p.image(this.khalil.getImage(), this.xCenter(), this.yStart + 65, 128, 112);
    }


    @Override
    public void cancel() {
      this.canceled = true;
      Level.this.chat("Traveling Buddy: Sooo loooong traveling buddyyyy");
      Level.this.curr_map.addVisualEffect(4009, this.khalil.coordinate.x + 0.6, this.khalil.coordinate.y - 0.4);
    }

    public void submit() {
      int selection = Misc.toInt(this.fields.get(3).getValue());
      if (selection < 0) {
        this.fields.get(1).setValue("Please make a selection.");
        return;
      }
      double cost = this.costs.get(selection);
      if (this.hero.money < cost) {
        this.fields.get(1).setValue("It seems you don't have enough money to afford that.");
        return;
      }
      Item i = this.khalil.inventory.slots.get(selection).item;
      if (i == null || i.remove) {
        this.fields.get(1).setValue("That item is out of stock.");
        return;
      }
      this.fields.get(1).setValue("Thank you for your purchase.");
      Item bought_item = new Item(p, i);
      i.removeStack();
      bought_item.stack = 1;
      this.hero.money -= cost;
      if (this.hero.canPickup()) {
        this.hero.pickup(bought_item);
      }
      else {
        Item leftover = this.hero.inventory.stash(bought_item);
        if (leftover != null && !leftover.remove) {
          Level.this.curr_map.addItem(leftover, this.khalil.coordinate.x + 1, this.khalil.coordinate.y + 1);
        }
      }
      RadioButton button = ((RadiosFormField)this.fields.get(3)).radios.get(selection);
      if (i.remove) {
        button.message = i.displayName() + " (out of stock): $" + this.costs.get(selection);
        button.disabled = true;
        button.color_text = DImg.ccolor(80);
      }
      else {
        button.message = i.displayName() + " (" + i.stack + " left): $" + this.costs.get(selection);
      }
    }
  }



  class VehicleForm extends LevelForm {
    private Feature car = null;
    private int timer_idle_sound = 0;
    private int last_update_time = 0;
    private boolean first_update = true;

    VehicleForm(LNZ sketch, Feature f, Hero h) {
      super(sketch,
        0.5 * (sketch.width - LNZ.level_vehicleFormWidth),
        0.5 * (sketch.height - LNZ.level_vehicleFormHeight),
        0.5 * (sketch.width + LNZ.level_vehicleFormWidth),
        0.5 * (sketch.height + LNZ.level_vehicleFormHeight));
      if (f == null || f.remove || h == null || h.remove) {
        p.global.errorMessage("ERROR: Null parameter passed into VehicleForm.");
        return;
      }
      this.car = f;
      this.setTitleText(f.displayName());
      this.setFieldCushion(10);

      String message_body = this.getMessageBody(); // from level location / car
      MessageFormField message = new MessageFormField(p, message_body);
      SubmitCancelFormField submit = new SubmitCancelFormField(p, "Drive Away", "Exit Vehicle");
      submit.button1.setColors(DImg.ccolor(170), DImg.ccolor(236, 213, 166), DImg.ccolor(211,
        188, 141), DImg.ccolor(190, 165, 120), DImg.ccolor(0));
      submit.button2.setColors(DImg.ccolor(170), DImg.ccolor(236, 213, 166), DImg.ccolor(211,
        188, 141), DImg.ccolor(190, 165, 120), DImg.ccolor(0));

      this.addField(new SpacerFormField(p, 210));
      this.addField(new MessageFormField(p, "You started the car"));
      this.addField(message);
      this.addField(submit);

      p.global.sounds.trigger_player("player/car_start");
    }

    String getMessageBody() {
      switch(Level.this.location) {
        case FRANCISCAN_LEV2_FRONTDOOR:
          switch(this.car.ID) {
            case 501: // ahimdoor
            case 503: // behindcaf
            case 504: // lower lot
              return "Drive off-campus to find a more remote area?";
            default:
              break;
          }
          break;
        case FRANCISCAN_LEV3_KILLEDHECK:
          switch(this.car.ID) {
            case 502: // outside egan
              return "Drive off-campus to find a more remote area?";
            default:
              break;
          }
          break;
        case FRANCISCAN_LEV2_AHIMDOOR:
          switch(this.car.ID) {
            case 501: // ahimdoor
              return "Drive off-campus to find a more remote area?";
            default:
              break;
          }
        default:
          break;
      }
      return "Error";
    }

    @Override
    public void update(int millis) {
      if (this.first_update) {
        this.first_update = false;
        this.last_update_time = millis;
      }
      int time_elapsed = millis - this.last_update_time;
      this.timer_idle_sound -= time_elapsed;
      if (this.timer_idle_sound < 0) {
        this.timer_idle_sound = 6900;
        p.global.sounds.trigger_player("player/car_idle");
      }
      super.update(millis);
      p.imageMode(PConstants.CENTER);
      double image_width = this.car.width() * 200.0 / this.car.height();
      p.image(this.car.getImage(), this.xCenter(), this.yStart + 105, image_width, 200);
      this.last_update_time = millis;
    }

    @Override
    public void cancel() {
      super.cancel();
      p.global.sounds.trigger_player("player/car_off");
      p.global.sounds.silence_player("player/car_idle");
      this.timer_idle_sound = 500;
    }


    public void submit() {
      boolean found_action = false;
      switch(Level.this.location) {
        case FRANCISCAN_LEV2_FRONTDOOR:
          switch(this.car.ID) {
            case 501: // ahimdoor
              found_action = true;
              Level.this.complete(1);
              break;
            case 503: // behindcaf
              found_action = true;
              Level.this.complete(5);
              break;
            case 504: // lower lot
              found_action = true;
              Level.this.complete(2);
              break;
            default:
              break;
          }
          break;
        case FRANCISCAN_LEV3_KILLEDHECK:
          switch(this.car.ID) {
            case 502: // outside egan
              found_action = true;
              Level.this.complete(2);
              break;
          }
          break;
        case FRANCISCAN_LEV2_AHIMDOOR:
          switch(this.car.ID) {
            case 501: // ahimdoor
              found_action = true;
              Level.this.complete(1);
            default:
              break;
          }
          break;
        default:
          break;
      }
      if (!found_action) {
        p.global.errorMessage("ERROR: Location " + Level.this.location.fileName() +
          " with vehicle " + this.car.ID + " not found in VehicleForm::submit().");
      }
      this.canceled = true;
    }
  }
}



class LevelEditor extends Level {
  private LNZ p;
  protected Rectangle last_rectangle = null;

  LevelEditor(LNZ sketch) {
    super(sketch);
    this.p = sketch;
  }
  LevelEditor(LNZ sketch, String folderPath, String levelName) {
    super(sketch);
    this.p = sketch;
    this.folderPath = folderPath;
    this.levelName = levelName;
    this.open();
  }

  @Override
  void openMap(String mapName) {
    if (mapName == null) {
      return;
    }
    if (!FileSystem.fileExists(p, this.finalFolderPath() + "/" + mapName + "." + this.mapSuffix() + ".lnz")) {
      p.global.errorMessage("ERROR: Level " + this.folderName() + " has no map " +
        "with name " + mapName + " at location " + this.finalFolderPath() + ".");
      return;
    }
    if (mapName.equals(this.currMapName)) {
      return;
    }
    this.closeMap();
    this.currMapName = mapName;
    if (this.location.isArea()) {
      this.curr_map = new GameMapAreaEditor(p, mapName, this.finalFolderPath());
    }
    else {
      this.curr_map = new GameMapLevelEditor(p, mapName, this.finalFolderPath());
    }
    this.curr_map.setLocation(this.xi, this.yi, this.xf, this.yf);
  }


  void newLinker() {
    if (this.last_rectangle == null) {
      return;
    }
    if (this.curr_map == null) {
      return;
    }
    if (!GameMapLevelEditor.class.isInstance(this.curr_map)) {
      return;
    }
    if (((GameMapLevelEditor)this.curr_map).rectangle_dropping == null) {
      return;
    }
    Linker linker = new Linker(p, this.last_rectangle, ((GameMapLevelEditor)this.curr_map).rectangle_dropping);
    this.addLinker(linker);
  }


  Rectangle getCurrentRectangle() {
    if (this.curr_map == null) {
      return null;
    }
    if (!GameMapLevelEditor.class.isInstance(this.curr_map)) {
      return null;
    }
    return ((GameMapLevelEditor)this.curr_map).rectangle_dropping;
  }


  void newTrigger() {
    this.addTrigger(new Trigger(p, "Trigger " + this.nextTriggerKey));
  }


  @Override
  void update(int millis) {
    if (this.curr_map != null) {
      this.curr_map.update(millis);
    }
    else {
      p.rectMode(PConstants.CORNERS);
      p.noStroke();
      p.fill(DImg.ccolor(60));
      p.rect(this.xi, this.yi, this.xf, this.yf);
    }
    if (this.curr_map != null && this.last_rectangle != null && this.last_rectangle.mapName.equals(this.currMapName)) {
      ((GameMapEditor)this.curr_map).drawRectangle(this.last_rectangle);
    }
  }

  @Override
  void keyPress(int key, int keyCode) {
    super.keyPress(key, keyCode);
    if (key == PConstants.CODED) {
      switch(keyCode) {}
    }
    else {
      switch(key) {
        case 's':
          if (this.curr_map != null && GameMapLevelEditor.class.isInstance(this.curr_map)) {
            this.last_rectangle = ((GameMapLevelEditor)this.curr_map).rectangle_dropping;
            ((GameMapLevelEditor)this.curr_map).rectangle_dropping = null;
            this.curr_map.addHeaderMessage("Rectangle saved");
          }
          break;
        case 'S':
          if (this.curr_map != null && GameMapLevelEditor.class.isInstance(this.curr_map)) {
            this.last_rectangle = ((GameMapLevelEditor)this.curr_map).rectangle_dropping;
            ((GameMapLevelEditor)this.curr_map).rectangle_dropping = null;
            this.curr_map.addHeaderMessage("Rectangle saved");
            if (this.last_rectangle != null) {
              if (p.global.holding_ctrl) {
                this.player_spawn_location = this.last_rectangle;
                this.curr_map.addHeaderMessage("Player respawn location set");
              }
              else {
                this.player_start_location = this.last_rectangle;
                this.curr_map.addHeaderMessage("Player start location set");
              }
              this.last_rectangle = null;
            }
          }
          break;
      }
    }
  }

  @Override
  void open() {
    super.open();
    this.currMapName = null;
  }
}