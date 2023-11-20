package LNZModule;

import java.util.*;
import java.io.*;
import processing.core.*;
import DImg.DImg;
import FileSystem.FileSystem;
import Misc.Misc;

public class Global {
  private LNZ p;
  // Program
  protected InterfaceLNZ menu;
  protected Queue<NotificationLNZ> notification = new ArrayDeque<NotificationLNZ>();
  protected ProgramState state = ProgramState.INITIAL_INTERFACE;
  protected MinigameName auto_launch_minigame = null;
  protected boolean auto_start_playing = false;
  protected int timer_exiting = LNZ.exit_delay;
  protected Images images;
  protected Sounds sounds;
  protected Configuration configuration;
  protected PImage cursor;
  protected String last_cursor_string = "";
  protected boolean holding_shift = false;
  protected boolean holding_ctrl = false;
  protected boolean holding_alt = false;
  protected boolean holding_space = false;
  protected boolean holding_left = false;
  protected boolean holding_right = false;
  protected boolean holding_up = false;
  protected boolean holding_down = false;
  protected boolean holding_rightclick = false;
  protected boolean holding_leftclick = false;
  protected Deque<String> error_messages = new ArrayDeque<String>();
  protected PrintWriter log;
  protected boolean focused_last_frame = true;
  protected boolean viewing_ender_chest = false;
  protected HashMap<Integer, CraftingRecipe> crafting_recipes = CraftingRecipes.getAllCraftingRecipes();
  // FPS
  protected int last_frame_time = 0;
  protected double lastFPS = LNZ.maxFPS;
  protected int timer_FPS = LNZ.frameUpdateTime;
  protected int frame_counter = 0;
  // Colors
  protected int color_background = DImg.ccolor(180);
  protected int color_nameDisplayed_background = DImg.ccolor(100, 180);
  protected int color_nameDisplayed_text = DImg.ccolor(255);
  protected int color_panelBackground = DImg.ccolor(160, 82, 45);
  protected int color_loadingScreenBackground = DImg.ccolor(222, 185, 140);
  protected int color_mapBorder = DImg.ccolor(60);
  protected int color_mapBackground = DImg.ccolor(20);
  protected int color_inventoryBackground = DImg.ccolor(210, 153, 108);
  protected int color_perkTreeBaseColor = DImg.ccolor(160, 120, 80);
  protected int color_perkTreeLockedColor = DImg.ccolor(150);
  protected int color_perkTreeBrightColor = DImg.ccolor(170, 160, 100);
  protected int color_perkTreeDarkColor = DImg.ccolor(150, 70, 50);
  // Profile
  protected Profile profile;
  // Graphics
  protected boolean player_blinking = true;
  protected int player_blinks_left = 0;
  protected int player_blink_time = LNZ.level_questBlinkTime;

  Global(LNZ sketch) {
    this.p = sketch;
  }

  void initializeGlobal() {
    this.log = p.createWriter(p.sketchPath("data/logs/curr_log.lnz"));
    this.configuration = new Configuration(p);
    this.images = new Images(p);
    this.sounds = new Sounds(p);
    this.cursor = this.images.getImage("icons/cursor_default.png");
    p.cursor(this.cursor);
    if (!FileSystem.folderExists(p, "data/logs")) {
      FileSystem.mkdir(p, "data/logs");
    }
  }

  int frame(int millis) {
    int elapsed = millis - this.last_frame_time;
    this.last_frame_time = millis;
    return elapsed;
  }

  void loseFocus() {
    this.holding_shift = false;
    this.holding_ctrl = false;
    this.holding_alt = false;
    this.holding_space = false;
    this.holding_left = false;
    this.holding_right = false;
    this.holding_up = false;
    this.holding_down = false;
    this.holding_rightclick = false;
    if (this.menu != null) {
      this.menu.loseFocus();
    }
  }

  void gainFocus() {
    if (this.menu != null) {
      this.menu.gainFocus();
    }
  }

  void setCursor(String cursor_path) {
    if (this.last_cursor_string.equals(cursor_path)) {
      return;
    }
    this.cursor = this.images.getImage(cursor_path);
    this.last_cursor_string = cursor_path;
    p.cursor(this.cursor);
  }

  // The api calling default cursor has to provide the other cursors it can cause
  void defaultCursor(String ... possible_cursors) {
    for (String s : possible_cursors) {
      if (s.equals(this.last_cursor_string)) {
        this.setCursor("icons/cursor_default.png");
      }
    }
  }
  void defaultCursor() {
    this.setCursor("icons/cursor_default.png");
  }

  void viewingEnderChest() {
    if (this.viewing_ender_chest) {
      this.errorMessage("ERROR: Already an ender chest open. Will cause concurrent modification errors.");
    }
    this.viewing_ender_chest = true;
  }

  void notViewingEnderChest() {
    if (!this.viewing_ender_chest) {
      this.errorMessage("ERROR: Ender chest not open.");
    }
    this.viewing_ender_chest = false;
  }

  synchronized void log(String message) {
    this.log.println(message);
    System.out.println("LNZ log: " + message);
  }

  String lastErrorMessage() {
    if (this.error_messages.peek() == null) {
      return "None";
    }
    return this.error_messages.peek();
  }

  synchronized void errorMessage(String message) {
    this.error_messages.push(message);
    this.log(message + "\n" + Arrays.toString(new Throwable().getStackTrace()));
  }

  void checkErrorMessge() {
    if (this.menu == null) {
      return;
    }
    if (this.error_messages.peek() == null) {
      return;
    }
    this.menu.throwError(this.error_messages.pop());
  }

  char keyPressFX2D(char key, int keyCode) {
    if (key == PConstants.CODED) {
      switch(keyCode) {
        case PConstants.ALT:
          this.holding_alt = true;
          break;
        case PConstants.CONTROL:
          this.holding_ctrl = true;
          break;
        case PConstants.SHIFT:
          this.holding_shift = true;
          break;
        case PConstants.LEFT:
          this.holding_left = true;
          break;
        case PConstants.RIGHT:
          this.holding_right = true;
          break;
        case PConstants.UP:
          this.holding_up = true;
          break;
        case PConstants.DOWN:
          this.holding_down = true;
          break;
      }
    }
    else {
      key = this.fixKeyFX2D(key, keyCode);
      if (key == ' ') {
        this.holding_space = true;
      }
    }
    return key;
  }
  char keyReleaseFX2D(char key, int keyCode) {
    if (key == PConstants.CODED) {
      switch(keyCode) {
        case PConstants.ALT:
          this.holding_alt = false;
          break;
        case PConstants.CONTROL:
          this.holding_ctrl = false;
          break;
        case PConstants.SHIFT:
          this.holding_shift = false;
          break;
        case PConstants.LEFT:
          this.holding_left = false;
          break;
        case PConstants.RIGHT:
          this.holding_right = false;
          break;
        case PConstants.UP:
          this.holding_up = false;
          break;
        case PConstants.DOWN:
          this.holding_down = false;
          break;
      }
    }
    else {
      key = this.fixKeyFX2D(key, keyCode);
      if (key == ' ') {
        this.holding_space = false;
      }
    }
    return key;
  }
  char fixKeyFX2D(char key, int keyCode) {
    if (!this.holding_shift) {
      if (key >= 'A' && key <= 'Z') {
        key += 32;
      }
      else {
        switch(key) {
          case 192:
            key = '`';
            break;
          case 222:
            key = '\'';
            break;
        }
      }
    }
    else {
      switch(key) {
        case '1':
          key = '!';
          break;
        case '2':
          key = '@';
          break;
        case '3':
          key = '#';
          break;
        case '4':
          key = '$';
          break;
        case '5':
          key = '%';
          break;
        case '6':
          key = '^';
          break;
        case '7':
          key = '&';
          break;
        case '8':
          key = '*';
          break;
        case '9':
          key = '(';
          break;
        case '0':
          key = ')';
          break;
        case '-':
          key = '_';
          break;
        case '=':
          key = '+';
          break;
        case '[':
          key = '{';
          break;
        case ']':
          key = '}';
          break;
        case '\\':
          key = '|';
          break;
        case ';':
          key = ':';
          break;
        case '\'':
          key = '\"';
          break;
        case ',':
          key = '<';
          break;
        case '.':
          key = '>';
          break;
        case '/':
          key = '?';
          break;
        case 192:
          key = '~';
          break;
        case 222:
          key = '\"';
          break;
      }
    }
    return key;
  }

  void exitDelay() {
    this.log("Exiting normally.");
    this.log.flush();
    this.log.close();
    if (!FileSystem.folderExists(p, "data/logs/past")) {
      FileSystem.mkdir(p, "data/logs/past");
    }
    FileSystem.copyFile(p, "data/logs/curr_log.lnz", "data/logs/past/" + PApplet.year() + this.monthString() +
      this.dayString() + "-" + this.hourString() + this.minuteString() + this.secondString() + ".lnz");
    this.sounds.pauseAll();
    this.state = ProgramState.EXITING;
  }

  void exitImmediately() {
    this.exitDelay();
    System.exit(0);
  }

  String monthString() {
    int month = PApplet.month();
    if (month < 10) {
      return "0" + Integer.toString(month);
    }
    else {
      return Integer.toString(month);
    }
  }

  String dayString() {
    int day = PApplet.day();
    if (day < 10) {
      return "0" + Integer.toString(day);
    }
    else {
      return Integer.toString(day);
    }
  }

  String hourString() {
    int hour = PApplet.hour();
    if (hour < 10) {
      return "0" + Integer.toString(hour);
    }
    else {
      return Integer.toString(hour);
    }
  }

  String minuteString() {
    int minute = PApplet.minute();
    if (minute < 10) {
      return "0" + Integer.toString(minute);
    }
    else {
      return Integer.toString(minute);
    }
  }

  String secondString() {
    int second = PApplet.second();
    if (second < 10) {
      return "0" + Integer.toString(second);
    }
    else {
      return Integer.toString(second);
    }
  }
}


// global options (profile independent)
class Configuration {
  private LNZ p;

  protected String default_profile_name = "";
  protected double cursor_size = LNZ.default_cursor_size;

  Configuration(LNZ sketch) {
    this.p = sketch;
    this.loadConfiguration();
  }

  void loadConfiguration() {
    String[] lines = p.loadStrings(p.sketchPath("data/configuration.lnz"));
    if (lines == null) {
      this.save(); // save defaults if no configuration exists
      return;
    }
    for (String line : lines) {
      String[] data = PApplet.split(line, ':');
      if (data.length < 2) {
        continue;
      }
      switch(data[0]) {
        case "default_profile_name":
          this.default_profile_name = PApplet.trim(data[1]);
          break;
        case "cursor_size":
          if (Misc.isFloat(PApplet.trim(data[1]))) {
            this.cursor_size = Misc.toFloat(PApplet.trim(data[1]));
          }
          break;
        default:
          break;
      }
    }
  }

  void defaultConfiguration() {
    this.cursor_size = LNZ.default_cursor_size;
  }

  void save() {
    this.saveConfiguration();
  }
  void saveConfiguration() {
    PrintWriter file = p.createWriter(p.sketchPath("data/configuration.lnz"));
    file.println("default_profile_name: " + this.default_profile_name);
    file.println("cursor_size: " + this.cursor_size);
    file.flush();
    file.close();
  }
}
