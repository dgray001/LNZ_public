package LNZModule;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import processing.core.*;
import Button.*;
import DImg.DImg;
import FileSystem.FileSystem;
import Form.*;
import Misc.Misc;

class Profile {
  class Options {
    protected double volume_master;
    protected boolean volume_master_muted;
    protected double volume_music;
    protected boolean volume_music_muted;
    protected double volume_interface;
    protected boolean volume_interface_muted;
    protected double volume_environment;
    protected boolean volume_environment_muted;
    protected double volume_units;
    protected boolean volume_units_muted;
    protected double volume_player;
    protected boolean volume_player_muted;

    protected double map_viewMoveSpeedFactor;
    protected double inventory_bar_size;
    protected boolean inventory_bar_hidden;
    protected int terrain_resolution;
    protected double fog_update_time;
    protected boolean lock_screen;
    protected boolean show_feature_interaction_tooltip;
    protected boolean show_healthbars;

    protected boolean player_pathfinding;
    protected boolean magnetic_hands;

    Options() {
      this.profileUpdated();
    }

    void profileUpdated() {
      this.defaults();
      this.read();
      this.change();
    }

    void defaults() {
      this.volume_master = LNZ.options_defaultVolume;
      this.volume_master_muted = false;
      this.volume_music = LNZ.options_defaultMusicVolume;
      this.volume_music_muted = false;
      this.volume_interface = LNZ.options_defaultVolume;
      this.volume_interface_muted = false;
      this.volume_environment = LNZ.options_defaultVolume;
      this.volume_environment_muted = false;
      this.volume_units = LNZ.options_defaultVolume;
      this.volume_units_muted = false;
      this.volume_player = LNZ.options_defaultVolume;
      this.volume_player_muted = false;
      this.map_viewMoveSpeedFactor = LNZ.map_defaultCameraSpeed;
      this.inventory_bar_size = LNZ.hero_defaultInventoryBarHeight;
      this.inventory_bar_hidden = false;
      this.terrain_resolution = LNZ.map_terrainResolutionDefault;
      this.fog_update_time = LNZ.map_timer_refresh_fog_default;
      this.lock_screen = true;
      this.show_feature_interaction_tooltip = true;
      this.show_healthbars = false;
      this.player_pathfinding = true;
      this.magnetic_hands = false;
    }

    void setVolumes() {
      double master_volume_multiplier = this.volume_master / (LNZ.options_volumeMax - LNZ.options_volumeMin);

      Profile.this.p.global.sounds.setBackgroundVolume(
        (float)(LNZ.options_volumeGainMultiplier *
        Math.log(master_volume_multiplier * this.volume_music / (LNZ.
        options_volumeMax - LNZ.options_volumeMin))),
        this.volume_master_muted || this.volume_music_muted);

      if (this.volume_master_muted || this.volume_interface_muted) {
        Profile.this.p.global.sounds.out_interface.mute();
      }
      else {
        Profile.this.p.global.sounds.out_interface.unmute();
      }
      Profile.this.p.global.sounds.out_interface.setGain(
        (float)(LNZ.options_volumeGainMultiplier *
        Math.log(master_volume_multiplier * this.volume_interface / (LNZ.
        options_volumeMax - LNZ.options_volumeMin))));

      if (this.volume_master_muted || this.volume_environment_muted) {
        Profile.this.p.global.sounds.out_environment.mute();
      }
      else {
        Profile.this.p.global.sounds.out_environment.unmute();
      }
      Profile.this.p.global.sounds.out_environment.setGain(
        (float)(LNZ.options_volumeGainMultiplier *
        Math.log(master_volume_multiplier * this.volume_environment / (LNZ.
        options_volumeMax - LNZ.options_volumeMin))));

      if (this.volume_master_muted || this.volume_units_muted) {
        Profile.this.p.global.sounds.out_units.mute();
      }
      else {
        Profile.this.p.global.sounds.out_units.unmute();
      }
      Profile.this.p.global.sounds.out_units.setGain(
        (float)(LNZ.options_volumeGainMultiplier *
        Math.log(master_volume_multiplier * this.volume_units / (LNZ.
        options_volumeMax - LNZ.options_volumeMin))));

      if (this.volume_master_muted || this.volume_player_muted) {
        Profile.this.p.global.sounds.out_player.mute();
      }
      else {
        Profile.this.p.global.sounds.out_player.unmute();
      }
      Profile.this.p.global.sounds.out_player.setGain(
        (float)(LNZ.options_volumeGainMultiplier *
        Math.log(master_volume_multiplier * this.volume_player / (LNZ.
        options_volumeMax - LNZ.options_volumeMin))));
    }

    void change() {
      if (Profile.this.invalidProfile()) {
        return;
      }

      this.setVolumes();

      Hero h = Profile.this.p.global.menu.getCurrentHeroIfExists();
      if (h != null) {
        h.inventory_bar.setHeight(this.inventory_bar_size);
      }
    }

    void read() {
      if (Profile.this.invalidProfile()) {
        return;
      }
      String[] lines = Profile.this.p.loadStrings(Profile.this.p.sketchPath("data/profiles/" + Profile.this.display_name.toLowerCase() + "/options.lnz"));
      if (lines == null) {
        this.save(); // save defaults if no options exists
        return;
      }
      for (String line : lines) {
        String[] data = PApplet.split(line, ':');
        if (data.length < 2) {
          continue;
        }
        switch(data[0]) {
          case "volume_master":
            this.volume_master = Misc.toDouble(PApplet.trim(data[1]));
            break;
          case "volume_master_muted":
            this.volume_master_muted = Misc.toBoolean(PApplet.trim(data[1]));
            break;
          case "volume_music":
            this.volume_music = Misc.toDouble(PApplet.trim(data[1]));
            break;
          case "volume_music_muted":
            this.volume_music_muted = Misc.toBoolean(PApplet.trim(data[1]));
            break;
          case "volume_interface":
            this.volume_interface = Misc.toDouble(PApplet.trim(data[1]));
            break;
          case "volume_interface_muted":
            this.volume_interface_muted = Misc.toBoolean(PApplet.trim(data[1]));
            break;
          case "volume_environment":
            this.volume_environment = Misc.toDouble(PApplet.trim(data[1]));
            break;
          case "volume_environment_muted":
            this.volume_environment_muted = Misc.toBoolean(PApplet.trim(data[1]));
            break;
          case "volume_units":
            this.volume_units = Misc.toDouble(PApplet.trim(data[1]));
            break;
          case "volume_units_muted":
            this.volume_units_muted = Misc.toBoolean(PApplet.trim(data[1]));
            break;
          case "volume_player":
            this.volume_player = Misc.toDouble(PApplet.trim(data[1]));
            break;
          case "volume_player_muted":
            this.volume_player_muted = Misc.toBoolean(PApplet.trim(data[1]));
            break;
          case "map_viewMoveSpeedFactor":
            this.map_viewMoveSpeedFactor = Misc.toDouble(PApplet.trim(data[1]));
            break;
          case "inventory_bar_size":
            this.inventory_bar_size = Misc.toDouble(PApplet.trim(data[1]));
            break;
          case "inventory_bar_hidden":
            this.inventory_bar_hidden = Misc.toBoolean(PApplet.trim(data[1]));
            break;
          case "lock_screen":
            this.lock_screen = Misc.toBoolean(PApplet.trim(data[1]));
            break;
          case "show_feature_interaction_tooltip":
            this.show_feature_interaction_tooltip = Misc.toBoolean(PApplet.trim(data[1]));
            break;
          case "show_healthbars":
            this.show_healthbars = Misc.toBoolean(PApplet.trim(data[1]));
            break;
          case "player_pathfinding":
            this.player_pathfinding = Misc.toBoolean(PApplet.trim(data[1]));
            break;
          case "magnetic_hands":
            this.magnetic_hands = Misc.toBoolean(PApplet.trim(data[1]));
            break;
          default:
            break;
        }
      }
    }

    void save() {
      if (Profile.this.invalidProfile()) {
        return;
      }
      PrintWriter file = Profile.this.p.createWriter(Profile.this.p.sketchPath(
        "data/profiles/" + Profile.this.display_name.toLowerCase() + "/options.lnz"));
      file.println("volume_master: " + this.volume_master);
      file.println("volume_master_muted: " + this.volume_master_muted);
      file.println("volume_music: " + this.volume_music);
      file.println("volume_music_muted: " + this.volume_music_muted);
      file.println("volume_interface: " + this.volume_interface);
      file.println("volume_interface_muted: " + this.volume_interface_muted);
      file.println("volume_environment: " + this.volume_environment);
      file.println("volume_environment_muted: " + this.volume_environment_muted);
      file.println("volume_units: " + this.volume_units);
      file.println("volume_units_muted: " + this.volume_units_muted);
      file.println("volume_player: " + this.volume_player);
      file.println("volume_player_muted: " + this.volume_player_muted);
      file.println("map_viewMoveSpeedFactor: " + this.map_viewMoveSpeedFactor);
      file.println("inventory_bar_size: " + this.inventory_bar_size);
      file.println("inventory_bar_hidden: " + this.inventory_bar_hidden);
      file.println("lock_screen: " + this.lock_screen);
      file.println("show_feature_interaction_tooltip: " + this.show_feature_interaction_tooltip);
      file.println("show_healthbars: " + this.show_healthbars);
      file.println("player_pathfinding: " + this.player_pathfinding);
      file.println("magnetic_hands: " + this.magnetic_hands);
      file.flush();
      file.close();
    }
  }



  class PlayerTree {
    class PlayerTreeNode {
      class PlayerTreeNodeButton1 extends RectangleButton {
        protected int start_time = 0;
        protected float last_mX = 0;
        protected float last_mY = 0;

        PlayerTreeNodeButton1(LNZ sketch, double xi, double yi, double button_height) {
          super(sketch, xi, yi, xi + 4 * button_height, yi + button_height);
          this.show_message = false;
          this.text_size = 18;
          this.message = PlayerTreeNode.this.code.display_name();
          this.color_disabled = DImg.ccolor(220);
        }

        @Override
        public int fillColor() {
          if (!this.show_message) {
            return this.color_disabled;
          }
          return super.fillColor();
        }

        @Override
        public void writeText() {
          if (!this.show_message) {
            return;
          }
          p.fill(this.color_text);
          p.textSize(this.text_size);
          if (this.hovered) {
            p.textAlign(PConstants.LEFT, PConstants.CENTER);
            p.text(this.message, this.xi + this.buttonHeight() + 3, this.yCenter());
          }
          else {
            p.textAlign(PConstants.CENTER, PConstants.CENTER);
            p.text(this.message, this.xCenter(), this.yCenter());
          }
        }

        @Override
        public void drawButton() {
          super.drawButton();
          if (this.show_message && this.hovered && p.millis() - this.start_time > 1000) {
            p.fill(PlayerTreeNode.this.p.global.color_nameDisplayed_background);
            p.noStroke();
            p.rectMode(PConstants.CORNER);
            p.textSize(16);
            String hover_message = "Click for details";
            double message_width = p.textWidth(hover_message);
            double message_height = p.textAscent() + p.textDescent() + 2;
            p.textAlign(PConstants.LEFT, PConstants.BOTTOM);
            if (this.last_mX < this.xi + 0.5 * this.buttonHeight()) {
              p.rect(this.last_mX - message_width - 2, this.last_mY - message_height - 2, message_width, message_height);
              p.fill(255);
              p.text(hover_message, this.last_mX - message_width - 1, this.last_mY - 1);
            }
            else {
              p.rect(this.last_mX + 2, this.last_mY - message_height - 2, message_width, message_height);
              p.fill(255);
              p.text(hover_message, this.last_mX + 1, this.last_mY - 1);
            }
          }
        }

        @Override
        public void mouseMove(float mX, float mY) {
          super.mouseMove(mX, mY);
          this.last_mX = mX;
          this.last_mY = mY;
        }

        public void hover() {
          if (PlayerTreeNode.this.visible) {
            this.start_time = p.millis();
            this.message = PlayerTreeNode.this.code.message();
          }
        }
        public void dehover() {
          if (PlayerTreeNode.this.visible) {
            this.message = PlayerTreeNode.this.code.display_name();
          }
        }
        public void click() {}
        public void release() {
          if (this.hovered) {
            if (PlayerTreeNode.this.unlocked || PlayerTreeNode.this.visible) {
              PlayerTreeNode.this.showDetails();
            }
          }
        }
      }

      class PlayerTreeNodeButton2 extends CircleButton {
        protected boolean hovered_cant_buy = false;

        PlayerTreeNodeButton2(LNZ sketch, double xi, double yi, double button_height) {
          super(sketch, xi + 0.5 * button_height, yi + 0.5 * button_height, 0.5 * button_height);
          this.show_message = false;
          this.text_size = 16;
          this.message = PlayerTreeNode.this.code.cost() + " 〶";
        }

        @Override
        public int fillColor() {
          if (!this.show_message || this.hovered_cant_buy) {
            return this.color_disabled;
          }
          return super.fillColor();
        }

        public void hover() {
          if (PlayerTreeNode.this.visible && !PlayerTreeNode.this.unlocked) {
            if (PlayerTree.this.achievementTokens() < PlayerTreeNode.this.code.cost()) {
              this.hovered_cant_buy = true;
            }
            this.message = PlayerTreeNode.this.code.cost() + " 〶\nBuy";
          }
        }
        public void dehover() {
          this.hovered_cant_buy = false;
          if (PlayerTreeNode.this.visible && !PlayerTreeNode.this.unlocked) {
            this.message = PlayerTreeNode.this.code.cost() + " 〶";
          }
        }
        public void click() {
          if (!PlayerTreeNode.this.visible || PlayerTreeNode.this.unlocked) {
            this.clicked = false;
          }
        }
        public void release() {
          if (this.hovered) {
            if (PlayerTreeNode.this.visible && !PlayerTreeNode.this.unlocked) {
              PlayerTreeNode.this.tryUnlock();
            }
          }
        }
      }

      private LNZ p;

      protected PlayerTreeCode code;
      protected ArrayList<PlayerTreeCode> dependencies = new ArrayList<PlayerTreeCode>();
      protected boolean in_view = false;
      protected boolean visible = false;
      protected boolean unlocked = false;
      protected PlayerTreeNodeButton1 button1;
      protected PlayerTreeNodeButton2 button2;

      PlayerTreeNode(LNZ sketch, PlayerTreeCode code, double xi, double yi, double button_height) {
        this.p = sketch;
        this.code = code;
        this.button1 = new PlayerTreeNodeButton1(sketch, xi, yi, button_height);
        this.button2 = new PlayerTreeNodeButton2(sketch, xi, yi, button_height);
        this.setDependencies();
      }

      void showDetails() {
        PlayerTree.this.showDetails(this.code);
      }

      void setDependencies() {
        switch(this.code) {
          case CAN_PLAY:
            break;
          case UNLOCK_DAN:
          case UNLOCK_JF:
          case UNLOCK_SPINNY:
          case UNLOCK_MATTUS:
          case UNLOCK_PATRICK:
            this.dependencies.add(PlayerTreeCode.CAN_PLAY);
            break;
          case XP_I:
            this.dependencies.add(PlayerTreeCode.CAN_PLAY);
            break;
          case XP_II:
            this.dependencies.add(PlayerTreeCode.XP_I);
            break;
          case XP_III:
            this.dependencies.add(PlayerTreeCode.XP_II);
            break;
          case XP_IV:
            this.dependencies.add(PlayerTreeCode.XP_III);
            break;
          case XP_V:
            this.dependencies.add(PlayerTreeCode.XP_IV);
            break;
          case MAGNETIC_WALLET:
            this.dependencies.add(PlayerTreeCode.CAN_PLAY);
            break;
          case MAGNETIC_HANDS:
            this.dependencies.add(PlayerTreeCode.MAGNETIC_WALLET);
            break;
          case HEALTHBARS:
            this.dependencies.add(PlayerTreeCode.CAN_PLAY);
            break;
          case ENEMY_INSIGHTI:
            this.dependencies.add(PlayerTreeCode.HEALTHBARS);
            break;
          case ENEMY_INSIGHTII:
            this.dependencies.add(PlayerTreeCode.ENEMY_INSIGHTI);
            break;
          case FARMING_INSIGHT:
            this.dependencies.add(PlayerTreeCode.HEALTHBARS);
            break;
          default:
            p.global.errorMessage("ERROR: PlayerTreeCode " + this.code + " not recognized.");
            break;
        }
      }

      void visible() {
        this.visible = true;
        this.button1.show_message = true;
        this.button2.show_message = true;
        if (this.unlocked) {
          return;
        }
        this.button1.setColors(p.global.color_perkTreeLockedColor,
          p.global.color_perkTreeDarkColor, p.global.color_perkTreeBaseColor,
          p.global.color_perkTreeBrightColor, DImg.ccolor(255));
        this.button1.setStroke(p.global.color_perkTreeBrightColor, 2);
        this.button2.setColors(p.global.color_perkTreeLockedColor,
          p.global.color_perkTreeDarkColor, p.global.color_perkTreeBaseColor,
          p.global.color_perkTreeBrightColor, DImg.ccolor(255));
        this.button1.setStroke(p.global.color_perkTreeBrightColor, 1);
      }

      void unlock() {
        this.unlocked = true;
        this.button1.show_message = true;
        this.button2.show_message = true;
        this.button2.message = "Unlocked";
        this.button1.setColors(p.global.color_perkTreeLockedColor,
          p.global.color_perkTreeBrightColor, p.global.color_perkTreeBrightColor,
          p.global.color_perkTreeBrightColor, DImg.ccolor(255));
        this.button1.setStroke(p.global.color_perkTreeBrightColor, 3);
        this.button2.setColors(p.global.color_perkTreeLockedColor,
          p.global.color_perkTreeBrightColor, p.global.color_perkTreeBrightColor,
          p.global.color_perkTreeBrightColor, DImg.ccolor(255));
        this.button1.setStroke(p.global.color_perkTreeBrightColor, 1);
      }

      void tryUnlock() {
        PlayerTree.this.unlockNode(this.code);
      }


      void update(int millis) {
        this.button1.update(millis);
        this.button2.update(millis);
      }

      void mouseMove(float mX, float mY) {
        this.button1.mouseMove(mX, mY);
        this.button2.mouseMove(mX, mY);
        if (this.button2.hovered) {
          this.button1.hovered = false;
          this.button1.dehover();
        }
      }

      boolean hovered() {
        return this.button1.hovered || this.button2.hovered;
      }

      void mousePress() {
        this.button1.mousePress();
        this.button2.mousePress();
      }

      void mouseRelease(float mX, float mY) {
        this.button1.mouseRelease(mX, mY);
        this.button2.mouseRelease(mX, mY);
        if (this.button2.hovered) {
          this.button1.hovered = false;
          this.button1.dehover();
        }
      }
    }


    class NodeDetailsForm extends Form {
      protected boolean canceled = false;
      protected PlayerTreeNode node;
      protected double shadow_distance = 10;
      protected PImage img;

      NodeDetailsForm(LNZ sketch, PlayerTreeNode node) {
        super(sketch, 0.5 * (sketch.width - LNZ.profile_treeForm_width), 0.5 * (sketch.height - LNZ.profile_treeForm_height),
          0.5 * (sketch.width + LNZ.profile_treeForm_width), 0.5 * (sketch.height + LNZ.profile_treeForm_height));
        this.img = Images.getCurrImage(sketch);
        this.cancelButton();
        this.draggable = false;
        this.node = node;
        this.setTitleText(node.code.display_name());
        this.setTitleSize(20);
        this.setFieldCushion(0);
        this.color_background = sketch.global.color_perkTreeLockedColor;
        this.color_header = sketch.global.color_perkTreeBrightColor;
        this.color_stroke = sketch.global.color_perkTreeDarkColor;
        this.color_title = DImg.ccolor(255);

        this.addField(new SpacerFormField(sketch, 20));
        this.addField(new TextBoxFormField(sketch, node.code.description(), 200));
        this.addField(new SpacerFormField(sketch, 20));
        boolean has_enough = Profile.this.achievement_tokens >= node.code.cost();
        SubmitCancelFormField buttons = new SubmitCancelFormField(sketch,
          Profile.this.achievement_tokens + "/" + node.code.cost(), "Cancel");
        if (has_enough && node.visible && !node.unlocked) {
        }
        else {
          buttons.button1.disabled = true;
          if (node.unlocked) {
            buttons.button1.message = "Unlocked";
          }
        }
        this.addField(buttons);
      }

      @Override
      public void update(int millis) {
        p.rectMode(PConstants.CORNERS);
        p.fill(0);
        p.imageMode(PConstants.CORNER);
        p.image(this.img, 0, 0);
        p.fill(0, 150);
        p.stroke(0, 1);
        p.translate(shadow_distance, shadow_distance);
        p.rect(this.xi, this.yi, this.xf, this.yf);
        p.translate(-shadow_distance, -shadow_distance);
        super.update(millis);
      }

      public void cancel() {
        this.canceled = true;
      }

      public void submit() {
        PlayerTree.this.unlockNode(this.node.code);
        this.canceled = true;
      }

      public void buttonPress(int index) {}
    }


    class BackButton extends RectangleButton {
      BackButton(LNZ sketch) {
        super(sketch, 0, 0, 0, 0);
        this.setColors(DImg.ccolor(170), DImg.ccolor(1, 0), DImg.ccolor(40, 120), DImg.ccolor(20, 150), DImg.ccolor(255));
        this.noStroke();
        this.show_message = true;
        this.message = "Back";
        this.text_size = 18;
        this.adjust_for_text_descent = true;
      }

      public void hover() {}
      public void dehover() {}
      public void click() {}
      public void release() {
        if (this.hovered) {
          PlayerTree.this.curr_viewing = false;
        }
      }
    }


    protected double xi = 0;
    protected double yi = 0;
    protected double xf = 0;
    protected double yf = 0;
    protected double xCenter;
    protected double yCenter;

    protected double tree_xi = 0;
    protected double tree_yi = 0;
    protected double tree_xf = 0;
    protected double tree_yf = 0;
    protected double translateX = 0;
    protected double translateY = 0;

    protected double viewX = 0;
    protected double viewY = 0;
    protected double zoom = 1.0;
    protected double inverse_zoom = 1.0;
    protected boolean curr_viewing = false;

    protected boolean dragging = false;
    protected float last_mX = 0;
    protected float last_mY = 0;
    protected boolean hovered = false;

    protected double lowestX = 0;
    protected double lowestY = 0;
    protected double highestX = 0;
    protected double highestY = 0;

    protected int color_background = DImg.ccolor(30);
    protected int color_connectorStroke_locked;
    protected int color_connectorStroke_visible;
    protected int color_connectorStroke_unlocked;
    protected int color_connectorFill_locked;
    protected int color_connectorFill_visible;
    protected int color_connectorFill_unlocked;

    protected HashMap<PlayerTreeCode, PlayerTreeNode> nodes = new HashMap<PlayerTreeCode, PlayerTreeNode>();
    protected NodeDetailsForm node_details = null;
    protected BackButton back_button = null;


    PlayerTree(LNZ sketch) {
      this.color_connectorStroke_locked = sketch.global.color_perkTreeDarkColor;
      this.color_connectorStroke_visible = sketch.global.color_perkTreeBaseColor;
      this.color_connectorStroke_unlocked = sketch.global.color_perkTreeBrightColor;
      this.color_connectorFill_locked = sketch.global.color_perkTreeLockedColor;
      this.color_connectorFill_visible = sketch.global.color_perkTreeDarkColor;
      this.color_connectorFill_unlocked = sketch.global.color_perkTreeBaseColor;
      this.xCenter = 0.5 * sketch.width;
      this.yCenter = 0.5 * sketch.height;
      this.back_button = new BackButton(sketch);
      this.initializeNodes();
      this.updateDependencies();
      this.setView(0, 0);
    }


    void showDetails(PlayerTreeCode code) {
      if (!this.nodes.containsKey(code)) {
        return;
      }
      this.node_details = new NodeDetailsForm(p, this.nodes.get(code));
    }

    int achievementTokens() {
      return Profile.this.achievement_tokens;
    }

    void unlockNode(PlayerTreeCode code) {
      this.unlockNode(code, false);
    }
    void unlockNode(PlayerTreeCode code, boolean from_save) {
      if (!this.nodes.containsKey(code)) {
        return;
      }
      if (this.nodes.get(code).unlocked || (!this.nodes.get(code).visible && !from_save)) {
        return;
      }
      if (!from_save && this.achievementTokens() < code.cost()) {
        return;
      }
      if (!from_save) {
        Profile.this.achievement_tokens -= code.cost();
      }
      this.nodes.get(code).unlock();
      this.updateDependencies();
      Profile.this.upgrade(code, from_save);
    }

    ArrayList<PlayerTreeCode> unlockedCodes() {
      ArrayList<PlayerTreeCode> codes = new ArrayList<PlayerTreeCode>();
      for (Map.Entry<PlayerTreeCode, PlayerTreeNode> entry : this.nodes.entrySet()) {
        if (entry.getValue().unlocked) {
          codes.add(entry.getKey());
        }
      }
      return codes;
    }


    void initializeNodes() {
      for (PlayerTreeCode code : PlayerTreeCode.VALUES) {
        double xi = 0;
        double yi = 0;
        double h = LNZ.profile_tree_nodeHeight;
        switch(code) {
          case CAN_PLAY:
            h *= 1.2;
            break;
          case UNLOCK_DAN:
            xi = LNZ.profile_tree_nodeGap + 2 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = 0.1 * LNZ.profile_tree_nodeHeight - 2 * (LNZ.profile_tree_nodeGap + 1.5 * LNZ.profile_tree_nodeHeight);
            break;
          case UNLOCK_JF:
            xi = LNZ.profile_tree_nodeGap + 3 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = 0.1 * LNZ.profile_tree_nodeHeight - (LNZ.profile_tree_nodeGap + 1.5 * LNZ.profile_tree_nodeHeight);
            break;
          case UNLOCK_SPINNY:
            xi = LNZ.profile_tree_nodeGap + 4 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = 0.1 * LNZ.profile_tree_nodeHeight;
            break;
          case UNLOCK_MATTUS:
            xi = LNZ.profile_tree_nodeGap + 3 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = 0.1 * LNZ.profile_tree_nodeHeight + LNZ.profile_tree_nodeGap + 1.5 * LNZ.profile_tree_nodeHeight;
            break;
          case UNLOCK_PATRICK:
            xi = LNZ.profile_tree_nodeGap + 2 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = 0.1 * LNZ.profile_tree_nodeHeight + 2 * (LNZ.profile_tree_nodeGap + 1.5 * LNZ.profile_tree_nodeHeight);
            break;
          case XP_I:
            xi = -LNZ.profile_tree_nodeGap - 4 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = 0.1 * LNZ.profile_tree_nodeHeight;
            break;
          case XP_II:
            xi = -2 * LNZ.profile_tree_nodeGap - 8 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = 0.1 * LNZ.profile_tree_nodeHeight;
            break;
          case XP_III:
            xi = -3 * LNZ.profile_tree_nodeGap - 12 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = 0.1 * LNZ.profile_tree_nodeHeight;
            break;
          case XP_IV:
            xi = -4 * LNZ.profile_tree_nodeGap - 16 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = 0.1 * LNZ.profile_tree_nodeHeight;
            break;
          case XP_V:
            xi = -5 * LNZ.profile_tree_nodeGap - 20 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = 0.1 * LNZ.profile_tree_nodeHeight;
            break;
          case MAGNETIC_WALLET:
            xi = -LNZ.profile_tree_nodeGap - 2 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = 0.6 * LNZ.profile_tree_nodeHeight + LNZ.profile_tree_nodeGap + 1.5 * LNZ.profile_tree_nodeHeight;
            break;
          case MAGNETIC_HANDS:
            xi = -LNZ.profile_tree_nodeGap - 2 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = 0.6 * LNZ.profile_tree_nodeHeight + 2 * (LNZ.profile_tree_nodeGap + 1.5 * LNZ.profile_tree_nodeHeight);
            break;
          case HEALTHBARS:
            xi = -LNZ.profile_tree_nodeGap - 2 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = -0.4 * LNZ.profile_tree_nodeHeight - LNZ.profile_tree_nodeGap - 1.5 * LNZ.profile_tree_nodeHeight;
            break;
          case ENEMY_INSIGHTI:
            xi = -LNZ.profile_tree_nodeGap - 2 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = -0.4 * LNZ.profile_tree_nodeHeight - 2 * (LNZ.profile_tree_nodeGap + 1.5 * LNZ.profile_tree_nodeHeight);
            break;
          case ENEMY_INSIGHTII:
            xi = -LNZ.profile_tree_nodeGap - 2 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = -0.4 * LNZ.profile_tree_nodeHeight - 3 * (LNZ.profile_tree_nodeGap + 1.5 * LNZ.profile_tree_nodeHeight);
            break;
          case FARMING_INSIGHT:
            xi = -LNZ.profile_tree_nodeGap - 6 * 1.2 * LNZ.profile_tree_nodeHeight;
            yi = -0.4 * LNZ.profile_tree_nodeHeight - 2 * (LNZ.profile_tree_nodeGap + 1.5 * LNZ.profile_tree_nodeHeight);
            break;
          default:
            p.global.errorMessage("ERROR: PlayerTreeCode " + code + " not recognized.");
            break;
        }
        this.nodes.put(code, new PlayerTreeNode(p, code, xi, yi, h));
        if (xi < this.lowestX) {
          this.lowestX = xi;
        }
        else if (xi + 4 * h > this.highestX) {
          this.highestX = xi + 4 * h;
        }
        if (yi < this.lowestY) {
          this.lowestY = yi;
        }
        else if (yi + h > this.highestY) {
          this.highestY = yi + h;
        }
      }
    }


    void updateDependencies() {
      for (Map.Entry<PlayerTreeCode, PlayerTreeNode> entry : this.nodes.entrySet()) {
        if (entry.getValue().visible) {
          continue;
        }
        boolean visible = true;
        for (PlayerTreeCode code : entry.getValue().dependencies) {
          if (!this.nodes.get(code).unlocked) {
            visible = false;
            break;
          }
        }
        if (visible) {
          entry.getValue().visible();
        }
      }
    }


    void setLocation(double xi, double yi, double xf, double yf) {
      this.xi = xi;
      this.yi = yi;
      this.xf = xf;
      this.yf = yf;
      this.setView(this.viewX, this.viewY);
      this.back_button.setLocation(xf - 120, yf - 70, xf - 30, yf - 30);
    }

    void moveView(double moveX, double moveY) {
      this.setView(this.viewX + moveX, this.viewY + moveY);
    }
    void setView(double viewX, double viewY) {
      if (viewX < this.lowestX) {
        viewX = this.lowestX;
      }
      else if (viewX > this.highestX) {
        viewX = this.highestX;
      }
      if (viewY < this.lowestY) {
        viewY = this.lowestY;
      }
      else if (viewY > this.highestY) {
        viewY = this.highestY;
      }
      this.viewX = viewX;
      this.viewY = viewY;
      this.tree_xi = viewX - this.inverse_zoom * (this.xCenter - this.xi);
      this.tree_yi = viewY - this.inverse_zoom * (this.yCenter - this.yi);
      this.tree_xf = viewX - this.inverse_zoom * (this.xCenter - this.xf);
      this.tree_yf = viewY - this.inverse_zoom * (this.yCenter - this.yf);
      this.translateX = this.xCenter - this.zoom * viewX;
      this.translateY = this.yCenter - this.zoom * viewY;
      for (Map.Entry<PlayerTreeCode, PlayerTreeNode> entry : this.nodes.entrySet()) {
        if (entry.getValue().button1.xi > this.tree_xi && entry.getValue().button1.yi > this.tree_yi &&
          entry.getValue().button1.xf < this.tree_xf && entry.getValue().button1.yf < this.tree_yf) {
          entry.getValue().in_view = true;
        }
        else {
          entry.getValue().in_view = false;
        }
      }
    }


    void update(int millis) {
      if (this.node_details != null) {
        this.node_details.update(millis);
        if (this.node_details.canceled) {
          this.node_details = null;
        }
        return;
      }
      p.rectMode(PConstants.CORNERS);
      p.fill(this.color_background);
      p.noStroke();
      p.rect(this.xi, this.yi, this.xf, this.yf);
      p.translate(this.translateX, this.translateY);
      p.scale(this.zoom, this.zoom);
      for (Map.Entry<PlayerTreeCode, PlayerTreeNode> entry : this.nodes.entrySet()) {
        p.rectMode(PConstants.CORNERS);
        p.translate(entry.getValue().button1.xCenter(), entry.getValue().button1.yCenter());
        for (PlayerTreeCode dependency : entry.getValue().dependencies) {
          PlayerTreeNode dependent = this.nodes.get(dependency);
          p.strokeWeight(2);
          float connector_width = 6;
          if (entry.getValue().unlocked) {
            p.fill(this.color_connectorFill_unlocked);
            p.stroke(this.color_connectorStroke_unlocked);
            p.strokeWeight(4);
            connector_width = 10;
          }
          else if (entry.getValue().visible || dependent.unlocked) {
            p.fill(this.color_connectorFill_visible);
            p.stroke(this.color_connectorStroke_visible);
            p.strokeWeight(3);
            connector_width = 8;
          }
          else {
            p.fill(this.color_connectorFill_locked);
            p.stroke(this.color_connectorStroke_locked);
          }
          double xDif = dependent.button1.xCenter() - entry.getValue().button1.xCenter();
          double yDif = dependent.button1.yCenter() - entry.getValue().button1.yCenter();
          double rotation = Math.atan2(yDif, xDif);
          double distance = Math.sqrt(xDif * xDif + yDif * yDif);
          p.rotate(rotation);
          p.rect(0, -connector_width, distance, connector_width);
          p.rotate(-rotation);
        }
        p.translate(-entry.getValue().button1.xCenter(), -entry.getValue().button1.yCenter());
      }
      for (Map.Entry<PlayerTreeCode, PlayerTreeNode> entry : this.nodes.entrySet()) {
        if (entry.getValue().in_view) {
          entry.getValue().update(millis);
        }
      }
      p.scale(this.inverse_zoom, this.inverse_zoom);
      p.translate(-this.translateX, -this.translateY);
      this.back_button.update(millis);
      p.fill(255);
      p.textAlign(PConstants.CENTER, PConstants.TOP);
      p.textSize(30);
      p.text("Perk Tree", this.xCenter, this.yi + 5);
      p.text("Achievement Tokens: " + Profile.this.achievement_tokens + " 〶", this.xCenter, p.textAscent() + p.textDescent() + 10);
    }

    void mouseMove(float mX, float mY) {
      if (this.node_details != null) {
        this.node_details.mouseMove(mX, mY);
        return;
      }
      this.back_button.mouseMove(mX, mY);
      if (this.dragging) {
        this.moveView(this.inverse_zoom * (this.last_mX - mX), this.inverse_zoom * (this.last_mY - mY));
      }
      this.last_mX = mX;
      this.last_mY = mY;
      if (mX > this.xi && mY > this.yi && mX < this.xf && mY < this.yf) {
        this.hovered = true;
      }
      else {
        this.hovered = false;
      }
      mX -= this.translateX;
      mY -= this.translateY;
      mX *= this.inverse_zoom;
      mY *= this.inverse_zoom;
      for (Map.Entry<PlayerTreeCode, PlayerTreeNode> entry : this.nodes.entrySet()) {
        if (entry.getValue().in_view) {
          entry.getValue().mouseMove(mX, mY);
        }
      }
    }

    void mousePress() {
      if (this.node_details != null) {
        this.node_details.mousePress();
        return;
      }
      this.back_button.mousePress();
      boolean button_hovered = false;
      if (this.back_button.hovered) {
        button_hovered = true;
      }
      for (Map.Entry<PlayerTreeCode, PlayerTreeNode> entry : this.nodes.entrySet()) {
        if (entry.getValue().in_view) {
          entry.getValue().mousePress();
          if (entry.getValue().hovered()) {
            button_hovered = true;
          }
        }
      }
      if (!button_hovered && p.mouseButton == PConstants.LEFT && this.hovered) {
        this.dragging = true;
      }
    }

    void mouseRelease(float mX, float mY) {
      if (this.node_details != null) {
        this.node_details.mouseRelease(mX, mY);
        return;
      }
      this.back_button.mouseRelease(mX, mY);
      if (p.mouseButton == PConstants.LEFT) {
        this.dragging = false;
      }
      mX -= this.translateX;
      mY -= this.translateY;
      mX *= this.inverse_zoom;
      mY *= this.inverse_zoom;
      for (Map.Entry<PlayerTreeCode, PlayerTreeNode> entry : this.nodes.entrySet()) {
        if (entry.getValue().in_view) {
          entry.getValue().mouseRelease(mX, mY);
        }
      }
    }

    void scroll(int amount) {
      if (this.node_details != null) {
        this.node_details.scroll(amount);
        return;
      }
      this.zoom -= amount * 0.025;
      if (this.zoom < 0.5) {
        this.zoom = 0.5;
      }
      if (this.zoom > 1.5) {
        this.zoom = 1.5;
      }
      this.inverse_zoom = 1 / this.zoom;
      this.setView(this.viewX, this.viewY);
    }

    void keyPress(int key, int keyCode) {
      if (this.node_details != null) {
        this.node_details.keyPress(key, keyCode);
        return;
      }
      if (key == PConstants.ESC || (key == 'p' && p.global.holding_ctrl)) {
        this.curr_viewing = false;
      }
    }

    void keyRelease(int key, int keyCode) {
      if (this.node_details != null) {
        this.node_details.keyRelease(key, keyCode);
        return;
      }
    }
  }


  class ProfileStats {
    protected int levels_completed = 0;
    protected int units_killed = 0;
    Map<Integer, Integer> units_killed_details = new HashMap<Integer, Integer>();
    protected int times_died = 0;
    Map<DamageSource, Integer> times_died_details = new HashMap<DamageSource, Integer>();
    protected double distance_walked = 0;

    ProfileStats() {
    }

    void addUnitKilled(int unit_id) {
      this.units_killed++;
      if (this.units_killed_details.containsKey(unit_id)) {
        int i = this.units_killed_details.get(unit_id);
        this.units_killed_details.put(unit_id, ++i);
      }
      else {
        this.units_killed_details.put(unit_id, 1);
      }
    }

    void addTimeDied(DamageSource source) {
      this.times_died++;
      if (this.times_died_details.containsKey(source)) {
        int i = this.times_died_details.get(source);
        this.times_died_details.put(source, ++i);
      }
      else {
        this.times_died_details.put(source, 1);
      }
    }
  }


  private LNZ p;

  protected String display_name = "";

  protected HashSet<MinigameName> minigames = new HashSet<MinigameName>();
  protected HashMap<AchievementCode, Boolean> achievements = new HashMap<AchievementCode, Boolean>();
  protected int achievement_tokens = 0;
  protected boolean ben_has_eyes = true;
  protected boolean has_seen_tips_and_tricks = false;
  protected PlayerTree player_tree;
  protected Options options;
  protected ProfileStats stats = new ProfileStats();

  protected HashMap<HeroCode, Hero> heroes = new HashMap<HeroCode, Hero>();
  protected HeroCode curr_hero = HeroCode.ERROR; // hero the player is playing as
  protected HashMap<Location, Boolean> areas = new HashMap<Location, Boolean>();

  protected EnderChestInventory ender_chest;
  protected Set<Integer> chuck_quizmo_answers = new HashSet<Integer>();
  protected double money = 0;

  Profile(LNZ sketch) {
    this(sketch, "");
  }
  Profile(LNZ sketch, String s) {
    this.p = sketch;
    this.player_tree = new PlayerTree(sketch);
    this.ender_chest = new EnderChestInventory(sketch);
    this.display_name = s;
    for (AchievementCode code : AchievementCode.VALUES) {
      this.achievements.put(code, false);
    }
    for (Location location : Location.VALUES) {
      if (location.isArea()) {
        this.areas.put(location, false);
      }
    }
    this.options = new Options();
  }

  boolean invalidProfile() {
    String name = this.display_name.toLowerCase();
    return name == null || name.equals("") || !FileSystem.folderExists(p, "data/profiles/" + name);
  }

  void profileUpdated() {
    this.options.profileUpdated();
    this.save();
  }

  void addInitialHero() {
    if (this.heroes.size() > 0) {
      p.global.errorMessage("ERROR: Cannot add initial hero when heroes already in profile.");
      return;
    }
    this.addHero(HeroCode.BEN);
    this.curr_hero = HeroCode.BEN;
    this.saveProfileFile();
  }

  void addHero(HeroCode code) {
    if (this.heroes.containsKey(code)) {
      return;
    }
    Hero h = new Hero(p, code);
    switch(code) {
      case BEN:
        h.location = Location.FRANCISCAN_FRANCIS;
        break;
      case DAN:
        h.location = Location.DANS_HOUSE;
        break;
      case JF:
        h.location = Location.ERROR;
        break;
      case SPINNY:
        h.location = Location.ERROR;
        break;
      case MATTUS:
        h.location = Location.ERROR;
        break;
      case PATRICK:
        h.location = Location.ERROR;
        break;
      default:
        p.global.errorMessage("ERROR: HeroCode " + code.displayName() + " not " +
          "recognized when being added to profile.");
        break;
    }
    this.heroes.put(code, h);
    this.saveHeroesFile();
    p.global.notification.add(new HeroUnlockNotification(p, code));
    p.global.log("Added Hero: " + code.displayName());
  }

  Hero currHero() {
    if (this.curr_hero == null || this.curr_hero == HeroCode.ERROR) {
      return null;
    }
    return this.heroes.get(this.curr_hero);
  }

  void achievement(AchievementCode code) {
    if (code == null) {
      return;
    }
    if (this.achievements.get(code).equals(Boolean.FALSE)) {
      this.achievements.put(code, Boolean.TRUE);
      this.achievement_tokens += code.tokens();
      this.saveProfileFile();
      p.global.notification.add(new AchievementNotification(p, code));
      p.global.log("Completed achievement: " + code.displayName());
    }
  }

  int achievementsCompleted() {
    int achievements_completed = 0;
    for (AchievementCode code : AchievementCode.VALUES) {
      if (this.achievements.get(code).equals(Boolean.TRUE)) {
        achievements_completed++;
      }
    }
    return achievements_completed;
  }

  boolean answeredChuckQuizmo(int code) {
    return this.chuck_quizmo_answers.add(code);
  }

  boolean achievementUnlocked(AchievementCode code) {
    if (code == null || !this.achievements.containsKey(code)) {
      return false;
    }
    return this.achievements.get(code).equals(Boolean.TRUE);
  }

  void unlockArea(Location location) {
    if (location == null || !this.areas.containsKey(location)) {
      p.global.errorMessage("ERROR: Trying to unlock area that doesn't exist.");
      return;
    }
    if (this.areas.get(location).equals(Boolean.FALSE)) {
      this.areas.put(location, Boolean.TRUE);
      this.saveProfileFile();
      p.global.notification.add(new AreaUnlockNotification(p, location));
      p.global.log("Unlocked area: " + location.displayName());
    }
  }

  void upgrade(PlayerTreeCode code, boolean from_save) {
    switch(code) {
      case CAN_PLAY:
        if (!from_save) {
          this.addInitialHero();
        }
        break;
      case UNLOCK_DAN:
        if (!from_save) {
          this.addHero(HeroCode.DAN);
        }
        break;
      case UNLOCK_JF:
        if (!from_save) {
          this.addHero(HeroCode.JF);
        }
        break;
      case UNLOCK_SPINNY:
        if (!from_save) {
          this.addHero(HeroCode.SPINNY);
        }
        break;
      case UNLOCK_MATTUS:
        if (!from_save) {
          this.addHero(HeroCode.MATTUS);
        }
        break;
      case UNLOCK_PATRICK:
        if (!from_save) {
          this.addHero(HeroCode.PATRICK);
        }
        break;
      default:
        break;
    }
    if (!from_save) {
      this.saveProfileFile();
      p.global.log("Unlocked perk: " + code.display_name());
    }
  }

  boolean upgraded(PlayerTreeCode code) {
    if (code == null || !this.player_tree.nodes.containsKey(code)) {
      return false;
    }
    return this.player_tree.nodes.get(code).unlocked;
  }

  boolean unlockedMinigame(MinigameName name) {
    if (name == null) {
      return false;
    }
    return this.minigames.contains(name);
  }

  void unlockMinigame(MinigameName name) {
    if (name == null || this.minigames.contains(name)) {
      return;
    }
    this.minigames.add(name);
    this.saveProfileFile();
    p.global.notification.add(new MinigameUnlockNotification(p, name));
    p.global.log("Unlocked minigame: " + name.displayName());
  }

  void levelCompleted() {
    this.stats.levels_completed++;
    this.saveProfileFile();
  }

  void unitKilled(Integer unit_id) {
    this.stats.addUnitKilled(unit_id);
    this.saveProfileFile();
    if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_KILLSI)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_KILLSI, this)) {
        this.achievement(AchievementCode.CONTINUOUS_KILLSI);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_KILLSII)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_KILLSII, this)) {
        this.achievement(AchievementCode.CONTINUOUS_KILLSII);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_KILLSIII)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_KILLSIII, this)) {
        this.achievement(AchievementCode.CONTINUOUS_KILLSIII);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_KILLSIV)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_KILLSIV, this)) {
        this.achievement(AchievementCode.CONTINUOUS_KILLSIV);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_KILLSV)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_KILLSV, this)) {
        this.achievement(AchievementCode.CONTINUOUS_KILLSV);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_KILLSVI)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_KILLSVI, this)) {
        this.achievement(AchievementCode.CONTINUOUS_KILLSVI);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_KILLSVII)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_KILLSVII, this)) {
        this.achievement(AchievementCode.CONTINUOUS_KILLSVII);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_KILLSVIII)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_KILLSVIII, this)) {
        this.achievement(AchievementCode.CONTINUOUS_KILLSVIII);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_KILLSIX)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_KILLSIX, this)) {
        this.achievement(AchievementCode.CONTINUOUS_KILLSIX);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_KILLSX)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_KILLSX, this)) {
        this.achievement(AchievementCode.CONTINUOUS_KILLSX);
      }
    }
  }

  void timeDied(DamageSource source) {
    this.stats.addTimeDied(source);
    this.saveProfileFile();
    if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_DEATHSI)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_DEATHSI, this)) {
        this.achievement(AchievementCode.CONTINUOUS_DEATHSI);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_DEATHSII)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_DEATHSII, this)) {
        this.achievement(AchievementCode.CONTINUOUS_DEATHSII);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_DEATHSIII)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_DEATHSIII, this)) {
        this.achievement(AchievementCode.CONTINUOUS_DEATHSIII);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_DEATHSIV)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_DEATHSIV, this)) {
        this.achievement(AchievementCode.CONTINUOUS_DEATHSIV);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_DEATHSV)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_DEATHSV, this)) {
        this.achievement(AchievementCode.CONTINUOUS_DEATHSV);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_DEATHSVI)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_DEATHSVI, this)) {
        this.achievement(AchievementCode.CONTINUOUS_DEATHSVI);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_DEATHSVII)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_DEATHSVII, this)) {
        this.achievement(AchievementCode.CONTINUOUS_DEATHSVII);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_DEATHSVIII)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_DEATHSVIII, this)) {
        this.achievement(AchievementCode.CONTINUOUS_DEATHSVIII);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_DEATHSIX)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_DEATHSIX, this)) {
        this.achievement(AchievementCode.CONTINUOUS_DEATHSIX);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_DEATHSX)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_DEATHSX, this)) {
        this.achievement(AchievementCode.CONTINUOUS_DEATHSX);
      }
    }
  }

  void walkedDistance(double distance) {
    this.stats.distance_walked += distance;
    this.saveProfileFile();
    if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_WALKI)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_WALKI, this)) {
        this.achievement(AchievementCode.CONTINUOUS_WALKI);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_WALKII)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_WALKII, this)) {
        this.achievement(AchievementCode.CONTINUOUS_WALKII);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_WALKIII)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_WALKIII, this)) {
        this.achievement(AchievementCode.CONTINUOUS_WALKIII);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_WALKIV)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_WALKIV, this)) {
        this.achievement(AchievementCode.CONTINUOUS_WALKIV);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_WALKV)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_WALKV, this)) {
        this.achievement(AchievementCode.CONTINUOUS_WALKV);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_WALKVI)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_WALKVI, this)) {
        this.achievement(AchievementCode.CONTINUOUS_WALKVI);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_WALKVII)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_WALKVII, this)) {
        this.achievement(AchievementCode.CONTINUOUS_WALKVII);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_WALKVIII)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_WALKVIII, this)) {
        this.achievement(AchievementCode.CONTINUOUS_WALKVIII);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_WALKIX)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_WALKIX, this)) {
        this.achievement(AchievementCode.CONTINUOUS_WALKIX);
      }
    }
    else if (!this.achievementUnlocked(AchievementCode.CONTINUOUS_WALKX)) {
      if (AchievementCode.achievement_met(AchievementCode.CONTINUOUS_WALKX, this)) {
        this.achievement(AchievementCode.CONTINUOUS_WALKX);
      }
    }
  }

  void saveProfileFile() {
    if (this.display_name.equals("")) {
      return;
    }
    PrintWriter file = p.createWriter(p.sketchPath("data/profiles/" +
      this.display_name.toLowerCase() + "/profile.lnz"));
    file.println("display_name: " + this.display_name);
    file.println("stat_levelsCompleted: " + this.stats.levels_completed);
    file.println("stat_unitsKilled: " + this.stats.units_killed);
    Iterator<Map.Entry<Integer, Integer>> kill_iterator = this.stats.units_killed_details.entrySet().iterator();
    while(kill_iterator.hasNext()) {
      Map.Entry<Integer, Integer> kill = kill_iterator.next();
      file.println("stat_unitsKilledDetails: " + kill.getKey() + ": " + kill.getValue());
    }
    file.println("stat_timesDied: " + this.stats.times_died);
    Iterator<Map.Entry<DamageSource, Integer>> death_iterator = this.stats.times_died_details.entrySet().iterator();
    while(death_iterator.hasNext()) {
      Map.Entry<DamageSource, Integer> death = death_iterator.next();
      file.println("stat_timesDiedDetails: " + death.getKey().fileString() + ": " + death.getValue());
    }
    file.println("stat_distanceWalked: " + this.stats.distance_walked);
    file.println("ben_has_eyes: " + this.ben_has_eyes);
    file.println("has_seen_tips_and_tricks: " + this.has_seen_tips_and_tricks);
    for (AchievementCode code : AchievementCode.VALUES) {
      if (this.achievements.get(code).equals(Boolean.TRUE)) {
        file.println("achievement: " + code.file_name());
      }
    }
    for (MinigameName minigame : this.minigames) {
      file.println("minigame: " + minigame.fileName());
    }
    for (Map.Entry<Location, Boolean> entry : this.areas.entrySet()) {
      if (entry.getValue().equals(Boolean.TRUE)) {
        file.println("areaUnlocked: " + entry.getKey().fileName());
      }
    }
    file.println("achievement_tokens: " + this.achievement_tokens);
    for (PlayerTreeCode code : this.player_tree.unlockedCodes()) {
      file.println("perk: " + code.file_name());
    }
    for (Integer i : this.chuck_quizmo_answers) {
      file.println("chuck_quizmo_answer: " + i);
    }
    file.println("curr_hero: " + this.curr_hero.file_name());
    file.println("money: " + this.money);
    file.println(this.ender_chest.internalFileString());
    file.flush();
    file.close();
  }

  void saveHeroesFile() {
    if (this.display_name.equals("")) {
      return;
    }
    PrintWriter heroes_file = p.createWriter(p.sketchPath("data/profiles/" + this.display_name.toLowerCase() + "/heroes.lnz"));
    for (Map.Entry<HeroCode, Hero> entry : this.heroes.entrySet()) {
      heroes_file.println(entry.getValue().fileString());
      heroes_file.println("");
    }
    heroes_file.flush();
    heroes_file.close();
  }

  void save() {
    if (this.display_name.equals("")) {
      return;
    }
    this.saveProfileFile();
    this.saveHeroesFile();
    this.options.save();
  }

  static Profile readProfile(LNZ sketch, String folder_path) {
    String[] lines = sketch.loadStrings(folder_path + "/profile.lnz");
    Profile p = new Profile(sketch);
    Item curr_item = null;
    boolean in_item = false;
    if (lines == null) {
      sketch.global.errorMessage("ERROR: Reading profile file but path " + (folder_path + "/profile.lnz") + " doesn't exist.");
      return p;
    }
    for (String line : lines) {
      String[] data = PApplet.split(line, ':');
      if (data.length < 2) {
        continue;
      }
      switch(PApplet.trim(data[0])) {
        case "display_name":
          p.display_name = PApplet.trim(data[1]);
          break;
        case "achievement":
          AchievementCode code = AchievementCode.achievementCode(PApplet.trim(data[1]));
          if (code != null && p.achievements.containsKey(code)) {
            p.achievements.put(code, Boolean.TRUE);
          }
          else if (code == null) {
            sketch.global.errorMessage("ERROR: Unknown achievement " + PApplet.trim(data[1]) + " in profile data.");
          }
          else {
            sketch.global.errorMessage("ERROR: Unknown achievement " + code.displayName() + " in profile data.");
          }
          break;
        case "minigame":
          MinigameName name = MinigameName.minigameName(PApplet.trim(data[1]));
          if (name != null) {
            p.minigames.add(name);
          }
          else {
            sketch.global.errorMessage("ERROR: Unknown minigame " + PApplet.trim(data[1]) + " in profile data.");
          }
          break;
        case "areaUnlocked":
          Location location = Location.location(PApplet.trim(data[1]));
          if (location != null && p.areas.containsKey(location)) {
            p.areas.put(location, Boolean.TRUE);
          }
          else {
            sketch.global.errorMessage("ERROR: Unknown location " + location.displayName() + " in profile data.");
          }
          break;
        case "stat_levelsCompleted":
          p.stats.levels_completed = Misc.toInt(PApplet.trim(data[1]));
          break;
        case "stat_unitsKilled":
          p.stats.units_killed = Misc.toInt(PApplet.trim(data[1]));
          break;
        case "stat_unitsKilledDetails":
          p.stats.units_killed_details.put(
            Misc.toInt(PApplet.trim(data[1])),
            Misc.toInt(PApplet.trim(data[2])));
          break;
        case "stat_timesDied":
          p.stats.times_died = Misc.toInt(PApplet.trim(data[1]));
          break;
        case "stat_timesDiedDetails":
          p.stats.times_died_details.put(
            DamageSource.toDamageSource(PApplet.trim(data[1])),
            Misc.toInt(PApplet.trim(data[2])));
          break;
        case "stat_distanceWalked":
          p.stats.distance_walked = Misc.toDouble(PApplet.trim(data[1]));
          break;
        case "ben_has_eyes":
          p.ben_has_eyes = Misc.toBoolean(PApplet.trim(data[1]));
          break;
        case "has_seen_tips_and_tricks":
          p.has_seen_tips_and_tricks = Misc.toBoolean(PApplet.trim(data[1]));
          break;
        case "chuck_quizmo_answer":
          p.answeredChuckQuizmo(Misc.toInt(PApplet.trim(data[1])));
          break;
        case "perk":
          PlayerTreeCode tree_code = PlayerTreeCode.code(PApplet.trim(data[1]));
          if (tree_code != null) {
            p.player_tree.unlockNode(tree_code, true);
          }
          break;
        case "achievement_tokens":
          if (Misc.isInt(PApplet.trim(data[1]))) {
            p.achievement_tokens = Misc.toInt(PApplet.trim(data[1]));
          }
          break;
        case "curr_hero":
          p.curr_hero = HeroCode.heroCode(PApplet.trim(data[1]));
          break;
        case "money":
          if (Misc.isDouble(PApplet.trim(data[1]))) {
            p.money = Misc.toDouble(PApplet.trim(data[1]));
          }
          break;
        case "new":
          switch(PApplet.trim(data[1])) {
            case "Item":
              if (data.length < 3) {
                sketch.global.errorMessage("ERROR: Item ID missing in Item constructor.");
                break;
              }
              if (curr_item != null) {
                sketch.global.errorMessage("ERROR: Can't create a new Item inside an Item.");
                break;
              }
              curr_item = new Item(sketch, Misc.toInt(PApplet.trim(data[2])));
              in_item = true;
              break;
            default:
              sketch.global.errorMessage("ERROR: Trying to create a new " + PApplet.trim(data[1]) +
                " which is invalid for profile data.");
              break;
          }
          break;
        case "end":
          switch(PApplet.trim(data[1])) {
            case "Item":
              if (curr_item == null) {
                sketch.global.errorMessage("ERROR: Can't end a null Item.");
                break;
              }
              if (data.length < 3) {
                sketch.global.errorMessage("ERROR: No positional information for ender chest item.");
                break;
              }
              int index = Misc.toInt(PApplet.trim(data[2]));
              Item i = p.ender_chest.placeAt(curr_item, index, true);
              if (i != null) {
                sketch.global.errorMessage("ERROR: Item already exists at position " + index + ".");
                break;
              }
              curr_item = null;
              in_item = false;
              break;
            default:
              sketch.global.errorMessage("ERROR: Trying to create a new " + PApplet.trim(data[1]) +
                " which is invalid for profile data.");
              break;
          }
          break;
        default:
          if (in_item) {
            curr_item.addData(PApplet.trim(data[0]), PApplet.trim(data[1]));
            continue;
          }
          break;
      }
    }
    lines = sketch.loadStrings(folder_path + "/heroes.lnz");
    if (lines == null) {
      sketch.global.errorMessage("ERROR: Reading heroes file but file " + (folder_path + "/heroes.lnz") + " doesn't exist.");
      return p;
    }
    Stack<ReadFileObject> object_queue = new Stack<ReadFileObject>();
    Hero curr_hero = null;
    StatusEffectCode curr_status_code = StatusEffectCode.ERROR;
    StatusEffect curr_status = null;
    Ability curr_ability = null;
    for (String line : lines) {
      String[] parameters = PApplet.split(line, ':');
      if (parameters.length < 2) {
        continue;
      }
      String dataname = PApplet.trim(parameters[0]);
      String data = PApplet.trim(parameters[1]);
      for (int i = 2; i < parameters.length; i++) {
        data += ":" + parameters[i];
      }
      if (dataname.equals("new")) {
        ReadFileObject type = ReadFileObject.objectType(PApplet.trim(parameters[1]));
        switch(type) {
          case HERO:
            if (parameters.length < 3) {
              sketch.global.errorMessage("ERROR: Unit ID missing in Hero constructor.");
              break;
            }
            object_queue.push(type);
            curr_hero = new Hero(sketch, Misc.toInt(PApplet.trim(parameters[2])));
            curr_hero.abilities.clear();
            break;
          case INVENTORY:
            if (curr_hero == null) {
              sketch.global.errorMessage("ERROR: Trying to start an inventory in a null hero.");
            }
            object_queue.push(type);
            break;
          case ITEM:
            if (curr_hero == null) {
              sketch.global.errorMessage("ERROR: Trying to start an item in a null hero.");
            }
            if (parameters.length < 3) {
              sketch.global.errorMessage("ERROR: Item ID missing in Item constructor.");
              break;
            }
            object_queue.push(type);
            curr_item = new Item(sketch, Misc.toInt(PApplet.trim(parameters[2])));
            break;
          case STATUS_EFFECT:
            if (curr_hero == null) {
              sketch.global.errorMessage("ERROR: Trying to start a status effect in a null hero.");
            }
            object_queue.push(type);
            curr_status = new StatusEffect(sketch);
            break;
          case ABILITY:
            if (curr_hero == null) {
              sketch.global.errorMessage("ERROR: Trying to start an ability in a null hero.");
            }
            if (parameters.length < 3) {
              sketch.global.errorMessage("ERROR: Ability ID missing in Projectile constructor.");
              break;
            }
            object_queue.push(type);
            curr_ability = new Ability(sketch, Misc.toInt(PApplet.trim(parameters[2])));
            break;
          default:
            sketch.global.errorMessage("ERROR: Can't add a " + type + " type to Heroes data.");
            break;
        }
      }
      else if (dataname.equals("end")) {
        ReadFileObject type = ReadFileObject.objectType(PApplet.trim(parameters[1]));
        if (object_queue.empty()) {
          sketch.global.errorMessage("ERROR: Tring to end a " + type.name + " object but not inside any object.");
        }
        else if (type.name.equals(object_queue.peek().name)) {
          switch(object_queue.pop()) {
            case HERO:
              if (curr_hero == null) {
                sketch.global.errorMessage("ERROR: Trying to end a null hero.");
                break;
              }
              if (!object_queue.empty()) {
                sketch.global.errorMessage("ERROR: Trying to end a hero but inside another object.");
                break;
              }
              if (p.heroes.containsKey(curr_hero.code)) {
                sketch.global.errorMessage("ERROR: Trying to end hero " + curr_hero.code + " this profile already has.");
                break;
              }
              if (curr_hero.code == HeroCode.ERROR) {
                sketch.global.errorMessage("ERROR: Trying to end hero with errored code.");
                break;
              }
              p.heroes.put(curr_hero.code, curr_hero);
              curr_hero = null;
              break;
            case INVENTORY:
              if (curr_hero == null) {
                sketch.global.errorMessage("ERROR: Trying to end an inventory in a null hero.");
                break;
              }
              break;
            case ITEM:
              if (curr_item == null) {
                sketch.global.errorMessage("ERROR: Trying to end a null item.");
                break;
              }
              if (object_queue.empty()) {
                sketch.global.errorMessage("ERROR: Trying to end an item not inside any other object.");
                break;
              }
              switch(object_queue.peek()) {
                case HERO:
                  if (parameters.length < 3) {
                    sketch.global.errorMessage("ERROR: GearSlot code missing in Item constructor.");
                    break;
                  }
                  GearSlot code = GearSlot.gearSlot(PApplet.trim(parameters[2]));
                  if (curr_hero == null) {
                    sketch.global.errorMessage("ERROR: Trying to add gear to null hero.");
                    break;
                  }
                  curr_hero.gear.put(code, curr_item);
                  break;
                case INVENTORY:
                  if (parameters.length < 3) {
                    sketch.global.errorMessage("ERROR: No positional information for inventory item.");
                    break;
                  }
                  int index = Misc.toInt(PApplet.trim(parameters[2]));
                  if (curr_hero == null) {
                    sketch.global.errorMessage("ERROR: Trying to add inventory item to null hero.");
                    break;
                  }
                  Item i = curr_hero.inventory.placeAt(curr_item, index, true);
                  if (i != null) {
                    sketch.global.errorMessage("ERROR: Item already exists at position " + index + ".");
                    break;
                  }
                  break;
                default:
                  sketch.global.errorMessage("ERROR: Trying to end an item inside a " + object_queue.peek().name + ".");
                  break;
              }
              curr_item = null;
              break;
            case STATUS_EFFECT:
              if (curr_status == null) {
                sketch.global.errorMessage("ERROR: Trying to end a null status effect.");
                break;
              }
              if (object_queue.empty()) {
                sketch.global.errorMessage("ERROR: Trying to end a status effect not inside any other object.");
                break;
              }
              if (object_queue.peek() != ReadFileObject.HERO) {
                sketch.global.errorMessage("ERROR: Trying to end a status effect not inside a hero.");
                break;
              }
              if (curr_hero == null) {
                sketch.global.errorMessage("ERROR: Trying to end a status effect inside a null hero.");
                break;
              }
              curr_hero.statuses.put(curr_status_code, curr_status);
              curr_status = null;
              break;
            case ABILITY:
              if (curr_ability == null) {
                sketch.global.errorMessage("ERROR: Trying to end a null ability.");
                break;
              }
              if (object_queue.empty()) {
                sketch.global.errorMessage("ERROR: Trying to end an ability not inside any other object.");
                break;
              }
              if (object_queue.peek() != ReadFileObject.HERO) {
                sketch.global.errorMessage("ERROR: Trying to end an ability not inside a hero.");
                break;
              }
              if (curr_hero == null) {
                sketch.global.errorMessage("ERROR: Trying to end an ability inside a null hero.");
                break;
              }
              curr_hero.abilities.add(curr_ability);
              curr_ability = null;
              break;
            default:
              sketch.global.errorMessage("ERROR: Invalid ReadFile type in Profile::readProfile.");
              break;
          }
        }
        else {
          sketch.global.errorMessage("ERROR: Tring to end a " + type.name + " object but current object is a " + object_queue.peek().name + ".");
        }
      }
      else {
        switch(object_queue.peek()) {
          case HERO:
            if (curr_hero == null) {
              sketch.global.errorMessage("ERROR: Trying to add unit data to a null hero.");
              break;
            }
            if (dataname.equals("next_status_code")) {
              curr_status_code = StatusEffectCode.code(data);
            }
            else {
              curr_hero.addData(dataname, data);
            }
            break;
          case INVENTORY:
            if (curr_hero == null) {
              sketch.global.errorMessage("ERROR: Trying to add hero inventory data to a null hero.");
              break;
            }
            curr_hero.inventory.addData(dataname, data);
            break;
          case ITEM:
            if (curr_item == null) {
              sketch.global.errorMessage("ERROR: Trying to add item data to a null item.");
              break;
            }
            curr_item.addData(dataname, data);
            break;
          case STATUS_EFFECT:
            if (curr_status == null) {
              sketch.global.errorMessage("ERROR: Trying to add status effect data to a null status effect.");
              break;
            }
            curr_status.addData(dataname, data);
            break;
          case ABILITY:
            if (curr_ability == null) {
              sketch.global.errorMessage("ERROR: Trying to add ability data to a null ability.");
              break;
            }
            curr_ability.addData(dataname, data);
            break;
          default:
            break;
        }
      }
    }
    p.profileUpdated();
    return p;
  }


  static int isValidProfileName(LNZ sketch, String s) {
    if (s == null) {
      return 1;
    }
    else if (s.equals("")) {
      return 1;
    }
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (i == 0 && !Character.isLetter(c)) {
        return 2;
      }
      else if (!Character.isLetterOrDigit(c)) {
        return 3;
      }
    }
    for (Path p : FileSystem.listEntries(sketch, sketch.sketchPath("data/profiles/"))) {
      String filename = p.getFileName().toString().toLowerCase();
      if (filename.equals(s.toLowerCase())) {
        return 4;
      }
    }
    return 0;
  }
}