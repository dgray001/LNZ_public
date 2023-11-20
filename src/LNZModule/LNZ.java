package LNZModule;

import processing.core.*;
import processing.event.MouseEvent;
import LNZApplet.LNZApplet;
import LNZApplet.Constants;

public class LNZ extends LNZApplet implements Constants {
  public Global global; // global object

  public static void main(String[] args) {
    // prepare program
    System.setProperty("quantum.multithreaded", "false");
    String[] processing_args = {"LNZ"};
    LNZ lnz_sketch = new LNZ();
    // run sketch
    PApplet.runSketch(processing_args, lnz_sketch);
  }

  @Override
  public void settings() {
    this.fullScreen(FX2D);
    smooth(2);
  }

  @Override
  public void setup() {
    // pixelDensity(displayDensity()); // doesn't seem to do anything on Windows
    surface.setSize(Constants.initialInterface_size, Constants.initialInterface_size);
    surface.setLocation((int) (0.5 * (displayWidth - Constants.initialInterface_size)),
        (int) (0.5 * (displayHeight - Constants.initialInterface_size)));
    frameRate(Constants.maxFPS);
    this.global = new Global(this);
    this.global.initializeGlobal();
    surface.setIcon(this.global.images.getImage("icon.png"));
    Profile p = new Profile(this);
    p.options.setVolumes(); // sets default volumes for initial interface
    background(global.color_background);
    this.global.menu = new InitialInterface(this);
    mouseX = -50;
    mouseY = -50;
  }

  public void draw() {
    int time_elapsed = millis() - global.last_frame_time;
    if (time_elapsed < 5) { // hard throttle to keep framerate < 200
      return;
    }
    actuallyDraw();
  }

  void actuallyDraw() {
    this.halt_event_propagation = false;
    int timeElapsed = global.frame(millis());
    // FPS counter
    global.timer_FPS -= timeElapsed;
    if (global.timer_FPS < 0) {
      global.timer_FPS = Constants.frameUpdateTime;
      global.lastFPS = (Constants.frameAverageCache * global.lastFPS
          + (frameCount - global.frame_counter) * (1000.0 / Constants.frameUpdateTime))
          / (Constants.frameAverageCache + 1);
      global.frame_counter = frameCount + 1;
    }
    // Player unit ellipse
    if (global.player_blinks_left > 0) {
      global.player_blink_time -= timeElapsed;
      if (global.player_blink_time < 0) {
        global.player_blink_time += Constants.level_questBlinkTime;
        global.player_blinks_left--;
        global.player_blinking = !global.player_blinking;
      }
    }
    // Program
    if (global.menu != null) {
      global.menu.LNZ_update(millis());
    }
    switch (global.state) {
    case INITIAL_INTERFACE:
      break;
    case ENTERING_MAINMENU:
      global.timer_exiting -= timeElapsed;
      background(30, 0, 0);
      if (global.timer_exiting < 0) {
        global.timer_exiting = Constants.exit_delay;
        global.state = ProgramState.MAINMENU_INTERFACE;
        global.menu = new MainMenuInterface(this);
        global.sounds.play_background("main");
        global.defaultCursor();
      }
      break;
    case MAINMENU_INTERFACE:
      break;
    case ENTERING_MAPEDITOR:
      background(60);
      global.state = ProgramState.MAPEDITOR_INTERFACE;
      global.menu = new MapEditorInterface(this);
      global.sounds.play_background("aoc");
      global.defaultCursor();
      break;
    case MAPEDITOR_INTERFACE:
      break;
    case ENTERING_TUTORIAL:
      background(60);
      global.state = ProgramState.TUTORIAL;
      global.menu = new TutorialInterface(this);
      global.sounds.play_background("none");
      global.defaultCursor();
      break;
    case TUTORIAL:
      break;
    case ENTERING_PLAYING:
      background(60);
      global.state = ProgramState.PLAYING;
      global.menu = new PlayingInterface(this);
      global.defaultCursor();
      break;
    case PLAYING:
      break;
    case ENTERING_MINIGAMES:
      background(60);
      global.state = ProgramState.MINIGAMES;
      global.menu = new MinigameInterface(this);
      global.sounds.play_background("none");
      global.defaultCursor();
      break;
    case MINIGAMES:
      break;
    case EXITING:
      global.timer_exiting -= timeElapsed;
      if (global.timer_exiting < 0) {
        System.exit(0);
      }
      break;
    default:
      break;
    }
    // global notification
    if (global.notification.peek() != null) {
      global.notification.peek().update(timeElapsed);
      if (global.notification.peek().finished) {
        global.notification.remove();
      }
    }
    // background music go to next track
    global.sounds.update();
    // check focused
    if (!focused && global.focused_last_frame) {
      global.loseFocus();
    }
    else if (focused && !global.focused_last_frame) {
      global.gainFocus();
    }
    global.focused_last_frame = focused;
    // check error message
    global.checkErrorMessge();
  }

  public void mouseDragged() {
    this.mouseMoved();
  }

  public void mouseMoved() {
    this.halt_event_propagation = false;
    if (global.menu != null) {
      global.menu.LNZ_mouseMove(mouseX, mouseY);
    }
    if (global.notification.peek() != null) {
      global.notification.peek().mouseMove(mouseX, mouseY);
    }
  }

  public void mousePressed() {
    this.halt_event_propagation = false;
    if (mouseButton == LEFT) {
      global.holding_leftclick = true;
    }
    else if (mouseButton == RIGHT) {
      global.holding_rightclick = true;
    }
    if (global.menu != null) {
      global.menu.LNZ_mousePress();
    }
    if (global.notification.peek() != null) {
      global.notification.peek().mousePress();
    }
  }

  public void mouseReleased() {
    this.halt_event_propagation = false;
    if (mouseButton == LEFT) {
      global.holding_leftclick = false;
    }
    else if (mouseButton == RIGHT) {
      global.holding_rightclick = false;
    }
    if (global.menu != null) {
      global.menu.LNZ_mouseRelease(mouseX, mouseY);
    }
  }

  public void mouseWheel(MouseEvent e) {
    this.halt_event_propagation = false;
    if (global.menu != null) {
      global.menu.LNZ_scroll(e.getCount());
    }
  }

  public void keyPressed() {
    this.halt_event_propagation = false;
    key = global.keyPressFX2D(key, keyCode);
    if (global.menu != null) {
      global.menu.LNZ_keyPress(key, keyCode);
    }
    // Prevent sketch from exiting on ESC
    if (key == ESC) {
      key = 0;
    }
  }

  public void keyReleased() {
    this.halt_event_propagation = false;
    key = global.keyReleaseFX2D(key, keyCode);
    if (global.menu != null) {
      global.menu.LNZ_keyRelease(key, keyCode);
    }
  }
}
