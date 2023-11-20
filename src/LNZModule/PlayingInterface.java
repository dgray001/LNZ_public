package LNZModule;

import java.util.*;
import java.io.PrintWriter;
import processing.core.*;
import Button.*;
import DImg.DImg;
import Element.*;
import FileSystem.FileSystem;
import Form.*;

enum PlayingStatus {
  WORLD_MAP, STARTING_NEW, LOADING_SAVED, PLAYING;
}

class PlayingInterface extends InterfaceLNZ {

  abstract class PlayingButton extends RectangleButton {
    protected LNZ p;
    PlayingButton(LNZ sketch) {
      super(sketch, 0, 0.94 * sketch.height, 0, sketch.height - LNZ.mapEditor_buttonGapSize);
      this.p = sketch;
      this.raised_border = true;
      this.roundness = 0;
      this.setColors(DImg.ccolor(170), DImg.ccolor(222, 184, 135), DImg.ccolor(244, 164, 96), DImg.ccolor(205, 133, 63), DImg.ccolor(0));
      this.show_message = true;
    }
    public void hover() {
      p.global.sounds.trigger_interface("interfaces/buttonOn2");
    }
    public void dehover() {}
    public void click() {
      p.global.sounds.trigger_interface("interfaces/buttonClick1");
    }
  }

  class PlayingButton1 extends PlayingButton {
    PlayingButton1(LNZ sketch) {
      super(sketch);
      this.message = "Start\nPlaying";
    }
    public void release() {
      if (!this.hovered) {
        return;
      }
      this.stayDehovered();
      if (PlayingInterface.this.status == PlayingStatus.WORLD_MAP) {
        PlayingInterface.this.checkLevelSave();
        return;
      }
      Hero h = PlayingInterface.this.getCurrentHeroIfExists();
      if (h != null) {
        if (PlayingInterface.this.form != null || PlayingInterface.this.status != PlayingStatus.PLAYING) {
          return;
        }
        if (this.message.contains("Abandon")) {
          PlayingInterface.this.form = new AbandonLevelWhilePlayingForm(p, h);
        }
        else {
          //PlayingInterface.this.form = new EnterNewCampaignForm(h);
          PlayingInterface.this.saveAndReturnToInitialState();
        }
      }
    }
  }

  class PlayingButton2 extends PlayingButton {
    PlayingButton2(LNZ sketch) {
      super(sketch);
      this.message = "Options";
    }
    public void release() {
      if (!this.hovered) {
        return;
      }
      this.stayDehovered();
      PlayingInterface.this.form = new OptionsForm(p);
    }
  }

  class PlayingButton3 extends PlayingButton {
    PlayingButton3(LNZ sketch) {
      super(sketch);
      this.message = "Heroes";
    }
    public void release() {
      if (!this.hovered) {
        return;
      }
      this.stayDehovered();
      PlayingInterface.this.form = new HeroesForm(p);
    }
  }

  class PlayingButton4 extends PlayingButton {
    PlayingButton4(LNZ sketch) {
      super(sketch);
      this.message = "Main\nMenu";
    }
    public void release() {
      if (!this.hovered) {
        return;
      }
      this.stayDehovered();
      PlayingInterface.this.form = new GoToMainMenuForm(p);
    }
  }


  class GoToMainMenuForm extends ConfirmForm {
    GoToMainMenuForm(LNZ sketch) {
      super(sketch, "Main Menu", "Are you sure you want to save and exit to the main menu?");
    }
    public void submit() {
      this.canceled = true;
      PlayingInterface.this.saveAndExitToMainMenu();
    }
  }


  abstract class PlayingForm extends FormLNZ {
    PlayingForm(LNZ sketch, String title, float formWidth, float formHeight) {
      super(sketch, 0.5 * (sketch.width - formWidth), 0.5 * (sketch.height - formHeight),
        0.5 * (sketch.width + formWidth), 0.5 * (sketch.height + formHeight));
      this.setTitleText(title);
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);
    }
  }


  class ConfirmStartLevelForm extends PlayingForm {
    protected Hero hero = null;
    ConfirmStartLevelForm(LNZ sketch, Hero hero) {
      super(sketch, "Start Level: " + hero.location.displayName(), 550, 390);
      this.hero = hero;

      MessageFormField message1 = new MessageFormField(sketch, "Begin the following level?");
      if (hero.location.isArea()) {
        message1.setValue("Begin playing in the following area?");
      }
      MessageFormField message2 = new MessageFormField(sketch, "Hero: " + hero.displayName());
      message2.text_color = DImg.ccolor(120, 30, 120);
      message2.setTextSize(18);
      MessageFormField message3 = new MessageFormField(sketch, "Location: " + hero.location.displayName());
      message3.text_color = DImg.ccolor(120, 30, 120);
      message3.setTextSize(18);
      MessageFormField message4 = new MessageFormField(sketch, "");
      message4.text_color = DImg.ccolor(150, 20, 20);
      message4.setTextSize(18);
      ButtonsFormField submit = new ButtonsFormField(sketch, "Begin Level", "Abandon Level");
      submit.button1.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      submit.button2.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      if (hero.location.isArea()) {
        submit.button1.message = "Enter Area";
        submit.button2.message = "";
        submit.button2.disabled = true;
      }
      ButtonFormField switch_hero = new ButtonFormField(sketch, "Switch Hero");
      switch_hero.button.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));

      this.addField(new SpacerFormField(sketch, 10));
      this.addField(message1);
      this.addField(message2);
      this.addField(message3);
      this.addField(new SpacerFormField(sketch, 10));
      this.addField(message4);
      this.addField(new SpacerFormField(sketch, 10));
      this.addField(submit);
      this.addField(switch_hero);
    }

    void abandonLevel() {
      if (this.hero.location.isArea()) {
        this.fields.get(5).setValue("You can't abandon an area.");
        return;
      }
      Location area_location = this.hero.location.areaLocation();
      if (p.global.profile.areas.containsKey(area_location) && p.global.profile.areas.get(area_location) == Boolean.TRUE) {
        PlayingInterface.this.form = new ConfirmAbandonForm(p);
        this.canceled = true;
      }
      else {
        this.fields.get(5).setValue("You can't abandon this level as you haven't explored the surrounding area.");
      }
    }

    public void submit() {
      PlayingInterface.this.startLevel();
      this.canceled = true;
    }

    @Override
    public void buttonPress(int i) {
      switch(i) {
        case 7: // buttons
          if (this.fields.get(7).getValue().equals("0")) {
            this.submit();
          }
          else {
            this.abandonLevel();
          }
          break;
        case 8: // switch hero
          PlayingInterface.this.heroesForm();
          break;
        default:
          p.global.errorMessage("ERROR: Button press code " + i + " not recognized in ConfirmStartLevelForm.");
          break;
      }
    }
  }


  class ConfirmContinueLevelForm extends PlayingForm {
    protected Hero hero = null;
    ConfirmContinueLevelForm(LNZ sketch, Hero hero) {
      super(sketch, "Continue " + hero.location.levelVsAreaStringLabel() + ": " +
        hero.location.displayName(), 550, 440);
      this.hero = hero;

      MessageFormField message1 = new MessageFormField(sketch, "Continue the following level?");
      if (hero.location.isArea()) {
        message1.setValue("Continue playing in the following area?");
      }
      MessageFormField message2 = new MessageFormField(sketch, "Hero: " + hero.displayName());
      message2.text_color = DImg.ccolor(120, 30, 120);
      message2.setTextSize(18);
      MessageFormField message3 = new MessageFormField(sketch, "Location: " + hero.location.displayName());
      message3.text_color = DImg.ccolor(120, 30, 120);
      message3.setTextSize(18);
      MessageFormField message4 = new MessageFormField(sketch, "");
      message4.text_color = DImg.ccolor(150, 20, 20);
      message4.setTextSize(18);
      ButtonsFormField submit = new ButtonsFormField(sketch, "Continue Level", "Abandon Level");
      submit.button1.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      submit.button2.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      if (hero.location.isArea()) {
        submit.button1.message = "Continue";
        submit.button2.message = "";
        submit.button2.disabled = true;
      }
      ButtonFormField switch_hero = new ButtonFormField(sketch, "Switch Hero");
      switch_hero.button.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      ButtonFormField restart_level = new ButtonFormField(sketch, "Restart Level");
      restart_level.button.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));

      this.addField(new SpacerFormField(sketch, 10));
      this.addField(message1);
      this.addField(message2);
      this.addField(message3);
      this.addField(new SpacerFormField(sketch, 10));
      this.addField(message4);
      this.addField(new SpacerFormField(sketch, 10));
      this.addField(submit);
      this.addField(switch_hero);
      if (!hero.location.isArea()) {
        this.addField(restart_level);
      }
    }

    void abandonLevel() {
      if (this.hero.location.isArea()) {
        this.fields.get(5).setValue("You can't abandon an area.");
        return;
      }
      Location area_location = this.hero.location.areaLocation();
      if (p.global.profile.areas.containsKey(area_location) && p.global.profile.areas.get(area_location) == Boolean.TRUE) {
        PlayingInterface.this.form = new ConfirmAbandonForm(p);
        this.canceled = true;
      }
      else {
        this.fields.get(5).setValue("You can't abandon this level as you haven't explored the surrounding area.");
      }
    }

    public void submit() {
      PlayingInterface.this.continueLevel();
      this.canceled = true;
    }

    @Override
    public void buttonPress(int i) {
      switch(i) {
        case 7: // buttons
          if (this.fields.get(7).getValue().equals("0")) {
            this.submit();
          }
          else {
            this.abandonLevel();
          }
          break;
        case 8: // switch hero
          PlayingInterface.this.heroesForm();
          break;
        case 9: // restart level
          if (this.hero.location.isArea()) {
            this.fields.get(5).setValue("You can't restart an area.");
            return;
          }
          PlayingInterface.this.form = new ConfirmRestartForm(p);
          this.canceled = true;
          break;
        default:
          p.global.errorMessage("ERROR: Button press code " + i + " not recognized in ConfirmContinueLevelForm.");
          break;
      }
    }
  }


  class AbandonLevelWhilePlayingForm extends PlayingForm {
    protected Hero hero = null;
    AbandonLevelWhilePlayingForm(LNZ sketch, Hero hero) {
      super(sketch, "Abandon " + hero.location.levelVsAreaStringLabel() + ": " +
        hero.location.displayName(), 550, 440);
      this.hero = hero;
      this.cancel = null;

      MessageFormField message1 = new MessageFormField(sketch, "Abandon the following level?");
      MessageFormField message2 = new MessageFormField(sketch, "Hero: " + hero.displayName());
      message2.text_color = DImg.ccolor(120, 30, 120);
      message2.setTextSize(18);
      MessageFormField message3 = new MessageFormField(sketch, "Location: " + hero.location.displayName());
      message3.text_color = DImg.ccolor(120, 30, 120);
      if (hero.location.isArea()) {
        message1.setValue("You can't abandon an area."); // basic instruction on how to start next level
      }
      message3.setTextSize(18);
      MessageFormField message4 = new MessageFormField(sketch, "");
      message4.text_color = DImg.ccolor(150, 20, 20);
      message4.setTextSize(18);
      ButtonsFormField submit = new ButtonsFormField(sketch, "Abandon Level", "Restart Level");
      submit.button1.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      submit.button2.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      if (hero.location.isArea()) {
        submit.button1.message = "Continue";
        submit.button2.message = "";
        submit.button2.disabled = true;
      }
      ButtonFormField switch_hero = new ButtonFormField(sketch, "Switch Hero");
      switch_hero.button.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      ButtonFormField continue_level = new ButtonFormField(sketch, "Continue Level");
      continue_level.button.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));

      this.addField(new SpacerFormField(sketch, 10));
      this.addField(message1);
      this.addField(message2);
      this.addField(message3);
      this.addField(new SpacerFormField(sketch, 10));
      this.addField(message4);
      this.addField(new SpacerFormField(sketch, 10));
      this.addField(submit);
      this.addField(switch_hero);
      this.addField(continue_level);
    }

    void restartLevel() {
      if (this.hero.location.isArea()) {
        this.fields.get(5).setValue("You can't restart an area.");
        return;
      }
      PlayingInterface.this.form = new ConfirmRestartForm(p);
      this.canceled = true;
    }

    public void submit() {
      if (this.hero.location.isArea()) {
        this.fields.get(5).setValue("You can't abandon an area.");
        return;
      }
      Location area_location = this.hero.location.areaLocation();
      if (p.global.profile.areas.containsKey(area_location) && p.global.profile.areas.get(area_location) == Boolean.TRUE) {
        PlayingInterface.this.form = new ConfirmAbandonForm(p);
        this.canceled = true;
      }
      else {
        this.fields.get(5).setValue("You can't abandon this level as you haven't explored the surrounding area.");
      }
    }

    @Override
    public void buttonPress(int i) {
      switch(i) {
        case 7: // buttons
          if (this.fields.get(7).getValue().equals("0")) {
            this.submit();
          }
          else {
            this.restartLevel();
          }
          break;
        case 8: // switch hero
          PlayingInterface.this.heroesForm();
          break;
        case 9: // continue level
          this.canceled = true;
          break;
        default:
          p.global.errorMessage("ERROR: Button press code " + i + " not recognized in AbandonLevelWhilePlayingForm.");
          break;
      }
    }
  }


  abstract class ConfirmActionForm extends FormLNZ {
    ConfirmActionForm(LNZ sketch, String title, String message) {
      super(sketch, 0.5 * sketch.width - 120, 0.5 * sketch.height - 120,
        0.5 * sketch.width + 120, 0.5 * sketch.height + 120);
      this.setTitleText(title);
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);
      this.scrollbar.setButtonColors(DImg.ccolor(170), DImg.ccolor(190, 255, 190),
        DImg.ccolor(220, 255, 220), DImg.ccolor(160, 220, 160), DImg.ccolor(0));

      SubmitCancelFormField submit = new SubmitCancelFormField(sketch, "  Ok  ", "Cancel");
      submit.button1.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      submit.button2.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      TextBoxFormField textbox = new TextBoxFormField(sketch, message, 120);
      textbox.textbox.scrollbar.setButtonColors(DImg.ccolor(170), DImg.ccolor(190, 255, 190),
        DImg.ccolor(220, 255, 220), DImg.ccolor(160, 220, 160), DImg.ccolor(0));

      this.addField(new SpacerFormField(sketch, 0));
      this.addField(textbox);
      this.addField(submit);
    }

    public void submit() {
      this.canceled = true;
      this.doAction();
    }

    abstract void doAction();
  }


  class ConfirmAbandonForm extends ConfirmActionForm {
    ConfirmAbandonForm(LNZ sketch) {
      super(sketch, "Abandon Level", "Are you sure you want to abandon the level?");
    }
    void doAction() {
      if (PlayingInterface.this.status == PlayingStatus.PLAYING) {
        PlayingInterface.this.saveAndReturnToInitialState();
      }
      PlayingInterface.this.abandonLevel();
    }
  }


  class ConfirmRestartForm extends ConfirmActionForm {
    ConfirmRestartForm(LNZ sketch) {
      super(sketch, "Restart Level", "Are you sure you want to restart the level?");
    }
    void doAction() {
      if (PlayingInterface.this.status == PlayingStatus.PLAYING) {
        PlayingInterface.this.saveAndReturnToInitialState();
      }
      PlayingInterface.this.restartLevel();
    }
  }


  class ConfirmLaunchCampaign extends ConfirmActionForm {
    protected Location location = Location.ERROR;
    ConfirmLaunchCampaign(LNZ sketch, Location location) {
      super(sketch, "Launch Campaign", "Are you sure you want to launch a new campaign?");
      this.location = location;
    }
    void doAction() {
      PlayingInterface.this.saveAndReturnToInitialState();
      PlayingInterface.this.launchCampaign(this.location);
    }
  }


  class ConfirmLaunchMinigameForm extends ConfirmActionForm {
    private MinigameName minigame;
    ConfirmLaunchMinigameForm(LNZ sketch, MinigameName minigame) {
      super(sketch, minigame.displayName(),
        minigame.launchAfterLevelDescription() + "\n\nYou have already " +
        "completed this minigame, do you wish to play it again?");
      this.minigame = minigame;
    }
    void doAction() {
      PlayingInterface.this.launchMinigame(minigame);
    }
  }


  // Forces doAction no matter what => is simply informative
  abstract class InformActionForm extends FormLNZ {
    InformActionForm(LNZ sketch, String title, String message) {
      this(sketch, title, message, "  Ok  ");
    }
    InformActionForm(LNZ sketch, String title, String message, String button_text) {
      super(sketch, 0.5 * sketch.width - 120, 0.5 * sketch.height - 120,
        0.5 * sketch.width + 120, 0.5 * sketch.height + 120);
      this.setTitleText(title);
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);
      this.scrollbar.setButtonColors(DImg.ccolor(170), DImg.ccolor(190, 255, 190),
        DImg.ccolor(220, 255, 220), DImg.ccolor(160, 220, 160), DImg.ccolor(0));

      SubmitFormField submit = new SubmitFormField(sketch, button_text);
      submit.button.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      this.addField(new SpacerFormField(sketch, 0));
      TextBoxFormField textbox = new TextBoxFormField(sketch, message, 120);
      textbox.textbox.scrollbar.setButtonColors(DImg.ccolor(170), DImg.ccolor(190, 255, 190),
        DImg.ccolor(220, 255, 220), DImg.ccolor(160, 220, 160), DImg.ccolor(0));
      this.addField(textbox);
      this.addField(submit);
    }
    public void submit() {
      this.cancel();
    }
    @Override
    public void cancel() {
      super.cancel();
      this.doAction();
    }
    abstract void doAction();
  }


  class InformLaunchMinigameForm extends InformActionForm {
    private MinigameName minigame;
    InformLaunchMinigameForm(LNZ sketch, MinigameName minigame) {
      super(sketch, minigame.displayName(),
        minigame.launchAfterLevelDescription() + "\n\nThe newly-unlocked " +
        "minigame will now launch.");
      this.minigame = minigame;
    }
    void doAction() {
      PlayingInterface.this.launchMinigame(this.minigame);
    }
  }


  class EnterNewCampaignForm extends FormLNZ {
    protected Hero hero = null;
    protected DropDownList list = null;

    EnterNewCampaignForm(LNZ sketch, Hero hero) {
      super(sketch, 0.5 * (sketch.width - 300), 0.5 * (sketch.height - 400),
        0.5 * (sketch.width + 300), 0.5 * (sketch.height + 400));
      this.setTitleText("Enter New Campaign");
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);
      this.hero = hero;

      TextBoxFormField list_field = new TextBoxFormField(sketch, "", 100);
      this.list = new DropDownList(sketch);
      this.list.setLocation(0, 0, 0, 100);
      this.list.hint_text = "Select Campaign to Launch";
      boolean first = true;
      for (Location a : hero.location.locationsFromArea()) {
        if (first) {
          first = false;
          this.list.setText(a.displayName());
        }
        else {
          this.list.addLine(a.displayName());
        }
      }
      list_field.textbox = this.list;
      SubmitCancelFormField submit = new SubmitCancelFormField(sketch, "Launch Campaign", "Cancel");
      submit.button1.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      submit.button2.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));

      this.addField(new SpacerFormField(sketch, 0));
      this.addField(list_field);
      this.addField(submit);
    }

    public void submit() {
      String location_name = this.list.highlightedLine();
      if (location_name == null) {
        return;
      }
      Location new_location = Location.location(location_name);
      if (new_location == null || new_location == Location.ERROR) {
        p.global.errorMessage("ERROR: The location name " + location_name +
          " gave an invalid location.");
        return;
      }
      if (!new_location.isCampaignStart()) {
        p.global.errorMessage("ERROR: The location " + new_location.displayName() +
          " is not the start of any campaign.");
        return;
      }
      for (Map.Entry<HeroCode, Hero> entry : p.global.profile.heroes.entrySet()) {
        // can't enter campaign another hero is in
        if (entry.getValue().location.getCampaignStart() == new_location) {
          return;
        }
      }
      PlayingInterface.this.form = new ConfirmLaunchCampaign(p, new_location);
      this.canceled = true;
    }
  }


  class OpenNewLevelThread extends Thread {
    private Level level = null;
    private Hero hero = null;
    private String curr_status = "";
    private boolean running = true;

    OpenNewLevelThread(Hero hero) {
      super("OpenNewLevelThread");
      this.setDaemon(true);
      this.hero = hero;
    }

    void stopThread() {
      this.running = false;
      FileSystem.deleteFolder(p, PlayingInterface.this.savePath + this.hero.location.fileName());
    }

    @Override
    public void run() {
      while(this.running) {
        this.curr_status += "Gathering Level Data";
        if (this.hero == null) {
          this.curr_status += " -> No hero found.";
          p.delay(2500);
          return;
        }
        if (this.hero.location == null || this.hero.location == Location.ERROR) {
          this.curr_status += " -> No hero location found.";
          p.delay(2500);
          return;
        }
        this.level = new Level(p, "data/locations", this.hero.location);
        if (this.level.nullify) {
          this.curr_status += " -> " + p.global.lastErrorMessage();
          p.delay(2500);
          return;
        }
        this.curr_status += "\nCopying Data";
        FileSystem.mkdir(p, PlayingInterface.this.savePath);
        String destination_folder = PlayingInterface.this.savePath + this.hero.location.fileName();
        FileSystem.deleteFolder(p, destination_folder);
        FileSystem.copyFolder(p, "data/locations/" + this.hero.location.fileName(), destination_folder);
        this.level.folderPath = PlayingInterface.this.savePath;
        this.level.save();
        if (!this.hero.location.isArea()) {
          PrintWriter hero_file = p.createWriter(destination_folder + "/old_hero.lnz");
          hero_file.println(this.hero.fileString());
          hero_file.flush();
          hero_file.close();
        }
        if (this.level.nullify) {
          this.curr_status += " -> " + p.global.lastErrorMessage();
          p.delay(2500);
          return;
        }
        this.curr_status += "\nOpening Map";
        this.level.setPlayer(this.hero);
        if (this.level.nullify) {
          this.curr_status += " -> " + p.global.lastErrorMessage();
          p.delay(2500);
          return;
        }
        if (!p.global.images.loaded_map_gifs) {
          this.curr_status += "\nLoading Animations";
          p.global.images.loadMapGifs();
        }
        break;
      }
    }
  }


  class OpenSavedLevelThread extends Thread {
    private Level level = null;
    private Hero hero = null;
    private String curr_status = "";
    private boolean running = true;

    OpenSavedLevelThread(Hero hero) {
      super("OpenSavedLevelThread");
      this.setDaemon(true);
      this.hero = hero;
    }

    void stopThread() {
      this.running = false;
    }

    @Override
    public void run() {
      while(this.running) {
        this.curr_status += "Opening Saved Level";
        if (this.hero == null) {
          this.curr_status += " -> No hero found.";
          p.delay(2500);
          return;
        }
        if (this.hero.location == null || this.hero.location == Location.ERROR) {
          this.curr_status += " -> No hero location found.";
          p.delay(2500);
          return;
        }
        String destination_folder = "data/profiles/" + p.global.profile.display_name.toLowerCase() + "/locations/";
        this.level = new Level(p, destination_folder, this.hero.location);
        if (this.level.nullify) {
          this.curr_status += " -> " + p.global.lastErrorMessage();
          p.delay(2500);
          return;
        }
        curr_status += "\nOpening Map";
        this.level.openCurrMap();
        this.level.addPlayer(this.hero);
        if (this.level.nullify) {
          this.curr_status += " -> " + p.global.lastErrorMessage();
          p.delay(2500);
          return;
        }
        if (!p.global.images.loaded_map_gifs) {
          this.curr_status += "\nLoading Animations";
          p.global.images.loadMapGifs();
        }
        break;
      }
    }
  }


  class LoadWorldMapThread extends Thread {
    LoadWorldMapThread() {
      super("LoadWorldMapThread");
      this.setDaemon(true);
    }
    @Override
    public void run() {
      WorldMap map = new WorldMap(p, PlayingInterface.this);
      map.setLocation(PlayingInterface.this.left_panel.size, 0, p.width - PlayingInterface.this.right_panel.size, p.height);
      PlayingInterface.this.world_map = map;
    }
  }


  private PlayingButton[] buttons = new PlayingButton[4];
  private Panel left_panel;
  private Panel right_panel;

  private String savePath = "data/profiles/" + p.global.profile.display_name.toLowerCase() + "/locations/";
  private WorldMap world_map = null;
  private Level level = null;
  private PlayingStatus status = PlayingStatus.WORLD_MAP;
  private boolean check_level_save = false;

  private OpenNewLevelThread newLevelThread = null;
  private OpenSavedLevelThread savedLevelThread = null;
  private LoadWorldMapThread worldMapThread = null;

  // TODO: Figure out what these are and what to do with them
  private boolean return_to_confirmStartLevelForm = false;
  private boolean return_to_confirmContinueLevelForm = false;
  private boolean return_to_confirmAbandonLevelForm = false;


  PlayingInterface(LNZ sketch) {
    super(sketch);
    this.left_panel = new Panel(sketch, PConstants.LEFT, LNZ.mapEditor_panelMinWidth,
    LNZ.mapEditor_panelMaxWidth, LNZ.mapEditor_panelStartWidth);
    this.right_panel = new Panel(sketch, PConstants.RIGHT, LNZ.mapEditor_panelMinWidth,
    LNZ.mapEditor_panelMaxWidth, LNZ.mapEditor_panelStartWidth);
    this.buttons[0] = new PlayingButton1(sketch);
    this.buttons[1] = new PlayingButton2(sketch);
    this.buttons[2] = new PlayingButton3(sketch);
    this.buttons[3] = new PlayingButton4(sketch);
    this.left_panel.addIcon(p.global.images.getImage("icons/triangle_gray.png"));
    this.right_panel.addIcon(p.global.images.getImage("icons/triangle_gray.png"));
    this.left_panel.color_background = p.global.color_panelBackground;
    this.right_panel.color_background = p.global.color_panelBackground;
    this.resizeButtons();
    this.worldMapThread = new LoadWorldMapThread();
    this.worldMapThread.start();
  }


  void resizeButtons() {
    double buttonSize = (this.right_panel.size_curr - 5 * LNZ.mapEditor_buttonGapSize) / 4.0;
    double xi = p.width - this.right_panel.size_curr + LNZ.mapEditor_buttonGapSize;
    this.buttons[0].setXLocation(xi, xi + buttonSize);
    xi += buttonSize + LNZ.mapEditor_buttonGapSize;
    this.buttons[1].setXLocation(xi, xi + buttonSize);
    xi += buttonSize + LNZ.mapEditor_buttonGapSize;
    this.buttons[2].setXLocation(xi, xi + buttonSize);
    xi += buttonSize + LNZ.mapEditor_buttonGapSize;
    this.buttons[3].setXLocation(xi, xi + buttonSize);
  }


  void returnToWorldMap() {
    this.status = PlayingStatus.WORLD_MAP;
    this.buttons[0].message = "Start\nPlaying";
    this.worldMapThread = new LoadWorldMapThread();
    this.worldMapThread.start();
  }

  void checkLevelSave() {
    if (p.global.profile.curr_hero == null || p.global.profile.curr_hero == HeroCode.ERROR) {
      p.global.errorMessage("ERROR: Profile has no current hero.");
      return;
    }
    Hero curr_hero = p.global.profile.heroes.get(p.global.profile.curr_hero);
    if (curr_hero == null) {
      p.global.errorMessage("ERROR: Profile missing curr hero " + p.global.profile.curr_hero + ".");
      return;
    }
    if (curr_hero.location == null || curr_hero.location == Location.ERROR) {
      p.global.errorMessage("ERROR: Hero " + curr_hero.displayName() + " missing location data.");
      return;
    }
    if (FileSystem.folderExists(p, this.savePath + curr_hero.location.fileName())) {
      this.form = new ConfirmContinueLevelForm(p, curr_hero);
    }
    else {
      this.form = new ConfirmStartLevelForm(p, curr_hero);
    }
  }

  void startLevel() {
    if (this.level != null) {
      p.global.errorMessage("ERROR: Trying to open level save when current level not null.");
      return;
    }
    if (this.status != PlayingStatus.WORLD_MAP) {
      p.global.errorMessage("ERROR: Trying to open level save when current status is " + this.status + ".");
      return;
    }
    if (p.global.profile.curr_hero == null || p.global.profile.curr_hero == HeroCode.ERROR) {
      p.global.errorMessage("ERROR: Profile has no current hero.");
      return;
    }
    Hero curr_hero = p.global.profile.heroes.get(p.global.profile.curr_hero);
    if (curr_hero == null) {
      p.global.errorMessage("ERROR: Profile missing curr hero " + p.global.profile.curr_hero + ".");
      return;
    }
    if (curr_hero.location == null || curr_hero.location == Location.ERROR) {
      p.global.errorMessage("ERROR: Hero " + curr_hero.displayName() + " missing location data.");
      return;
    }
    this.status = PlayingStatus.STARTING_NEW;
    this.newLevelThread = new OpenNewLevelThread(curr_hero);
    this.newLevelThread.start();
  }

  void continueLevel() {
    if (this.level != null) {
      p.global.errorMessage("ERROR: Trying to open level save when current level not null.");
      return;
    }
    if (this.status != PlayingStatus.WORLD_MAP) {
      p.global.errorMessage("ERROR: Trying to open level save when current status is " + this.status + ".");
      return;
    }
    if (p.global.profile.curr_hero == null || p.global.profile.curr_hero == HeroCode.ERROR) {
      p.global.errorMessage("ERROR: Profile has no current hero.");
      return;
    }
    Hero curr_hero = p.global.profile.heroes.get(p.global.profile.curr_hero);
    if (curr_hero == null) {
      p.global.errorMessage("ERROR: Profile missing curr hero " + p.global.profile.curr_hero + ".");
      return;
    }
    if (curr_hero.location == null || curr_hero.location == Location.ERROR) {
      p.global.errorMessage("ERROR: Hero " + curr_hero.displayName() + " missing location data.");
      return;
    }
    if (!FileSystem.folderExists(p, this.savePath + curr_hero.location.fileName())) {
      p.global.errorMessage("ERROR: No save folder at " + (this.savePath + curr_hero.location.fileName()) + ".");
      return;
    }
    this.status = PlayingStatus.LOADING_SAVED;
    this.savedLevelThread = new OpenSavedLevelThread(curr_hero);
    this.savedLevelThread.start();
  }

  void restartLevel() {
    if (this.level != null) {
      p.global.errorMessage("ERROR: Trying to restart level when current level not null.");
      return;
    }
    if (this.status != PlayingStatus.WORLD_MAP) {
      p.global.errorMessage("ERROR: Trying to restart level save when current status is " + this.status + ".");
      return;
    }
    if (p.global.profile.curr_hero == null || p.global.profile.curr_hero == HeroCode.ERROR) {
      p.global.errorMessage("ERROR: Profile has no current hero.");
      return;
    }
    Hero curr_hero = p.global.profile.heroes.get(p.global.profile.curr_hero);
    if (curr_hero == null) {
      p.global.errorMessage("ERROR: Profile missing curr hero " + p.global.profile.curr_hero + ".");
      return;
    }
    if (curr_hero.location == null || curr_hero.location == Location.ERROR) {
      p.global.errorMessage("ERROR: Hero " + curr_hero.displayName() + " missing location data.");
      return;
    }
    if (!FileSystem.folderExists(p, this.savePath + curr_hero.location.fileName())) {
      p.global.errorMessage("ERROR: No save folder at " + (this.savePath + curr_hero.location.fileName()) + ".");
      return;
    }
    if (curr_hero.location.isArea()) {
      p.global.errorMessage("ERROR: Can't restart " + curr_hero.location.displayName() + " since it's an area.");
      return;
    }
    Hero hero = Hero.readHeroFile(p, this.savePath + curr_hero.location.fileName() + "/old_hero.lnz");
    if (hero == null || hero.code == HeroCode.ERROR || hero.code != curr_hero.code || hero.location != curr_hero.location) {
      p.global.errorMessage("ERROR: Can't restart " + curr_hero.location.displayName() + " since old hero data corrupted.");
      return;
    }
    FileSystem.deleteFolder(p, this.savePath + curr_hero.location.fileName());
    p.global.profile.heroes.put(hero.code, hero);
    this.status = PlayingStatus.STARTING_NEW;
    this.newLevelThread = new OpenNewLevelThread(hero);
    this.newLevelThread.start();
  }

  void abandonLevel() {
    if (this.level != null) {
      p.global.errorMessage("ERROR: Trying to restart level when current level not null.");
      return;
    }
    if (this.status != PlayingStatus.WORLD_MAP) {
      p.global.errorMessage("ERROR: Trying to restart level save when current status is " + this.status + ".");
      return;
    }
    if (p.global.profile.curr_hero == null || p.global.profile.curr_hero == HeroCode.ERROR) {
      p.global.errorMessage("ERROR: Profile has no current hero.");
      return;
    }
    Hero curr_hero = p.global.profile.heroes.get(p.global.profile.curr_hero);
    if (curr_hero == null) {
      p.global.errorMessage("ERROR: Profile missing curr hero " + p.global.profile.curr_hero + ".");
      return;
    }
    if (curr_hero.location == null || curr_hero.location == Location.ERROR) {
      p.global.errorMessage("ERROR: Hero " + curr_hero.displayName() + " missing location data.");
      return;
    }
    if (curr_hero.location.isArea()) {
      p.global.errorMessage("ERROR: Can't abandon " + curr_hero.location.displayName() + " since it's an area.");
      return;
    }
    Location area_location = curr_hero.location.areaLocation();
    if (!p.global.profile.areas.containsKey(area_location) || p.global.profile.areas.get(area_location) == Boolean.FALSE) {
      p.global.errorMessage("ERROR: Can't abandon " + curr_hero.location.displayName() + " since its area isn't unlocked.");
      return;
    }
    if (FileSystem.folderExists(p, this.savePath + curr_hero.location.fileName())) {
      Hero hero = Hero.readHeroFile(p, this.savePath + curr_hero.location.fileName() + "/old_hero.lnz");
      if (hero == null || hero.code == HeroCode.ERROR || hero.code != curr_hero.code || hero.location != curr_hero.location) {
        p.global.errorMessage("ERROR: Can't restart " + curr_hero.location.displayName() + " since old hero data corrupted.");
        return;
      }
      FileSystem.deleteFolder(p, this.savePath + curr_hero.location.fileName());
      hero.location = area_location;
      p.global.profile.heroes.put(hero.code, hero);
      this.status = PlayingStatus.LOADING_SAVED;
      this.savedLevelThread = new OpenSavedLevelThread(hero);
      this.savedLevelThread.start();
    }
    else {
      curr_hero.location = area_location;
      this.status = PlayingStatus.STARTING_NEW;
      this.newLevelThread = new OpenNewLevelThread(curr_hero);
      this.newLevelThread.start();
    }
  }

  void launchCampaign(Location new_location) {
    if (this.level != null) {
      p.global.errorMessage("ERROR: Trying to launch new campaign when current level not null.");
      return;
    }
    if (this.status != PlayingStatus.WORLD_MAP) {
      p.global.errorMessage("ERROR: Trying to launch new campaign save when current status is " + this.status + ".");
      return;
    }
    if (p.global.profile.curr_hero == null || p.global.profile.curr_hero == HeroCode.ERROR) {
      p.global.errorMessage("ERROR: Profile has no current hero.");
      return;
    }
    Hero curr_hero = p.global.profile.heroes.get(p.global.profile.curr_hero);
    if (curr_hero == null) {
      p.global.errorMessage("ERROR: Profile missing curr hero " + p.global.profile.curr_hero + ".");
      return;
    }
    if (curr_hero.location == null || curr_hero.location == Location.ERROR) {
      p.global.errorMessage("ERROR: Hero " + curr_hero.displayName() + " missing location data.");
      return;
    }
    if (!curr_hero.location.isArea()) {
      p.global.errorMessage("ERROR: Can't launch new campaign since hero in " +
        curr_hero.location.displayName() + " which is not an area.");
      return;
    }
    if (new_location == null || new_location == Location.ERROR) {
      p.global.errorMessage("ERROR: New location does not exist.");
      return;
    }
    ArrayList<Location> campaign_locations = curr_hero.location.locationsFromArea();
    boolean valid_new_location = false;
    for (Location a : campaign_locations) {
      if (a == new_location) {
        valid_new_location = true;
        break;
      }
    }
    if (!valid_new_location) {
      p.global.errorMessage("ERROR: New location " + new_location.displayName() +
        " is not accessible from hero's current location " + curr_hero.location.displayName());
      return;
    }
    if (!new_location.isCampaignStart()) {
      p.global.errorMessage("ERROR: New location " + new_location.displayName() +
        " is not a campaign start point.");
      return;
    }
    for (Map.Entry<HeroCode, Hero> entry : p.global.profile.heroes.entrySet()) {
      // can't enter campaign another hero is in
      if (entry.getValue().location.getCampaignStart() == new_location) {
        p.global.errorMessage("ERROR: New location " + new_location.displayName() +
          " is being played by " + entry.getValue().displayName() + " already.");
        return;
      }
    }
    curr_hero.location = new_location;
    this.status = PlayingStatus.STARTING_NEW;
    this.newLevelThread = new OpenNewLevelThread(curr_hero);
    this.newLevelThread.start();
  }

  void switchHero(HeroCode code, boolean start_playing) {
    if (code == null || code == HeroCode.ERROR) {
      p.global.errorMessage("ERROR: Can't switch to a hero code that doesn't exist.");
      return;
    }
    if (!p.global.profile.heroes.containsKey(code)) {
      p.global.errorMessage("ERROR: Can't switch to a hero that hasn't beens unlocked.");
      return;
    }
    if (p.global.profile.curr_hero == code) {
      if (start_playing && this.status == PlayingStatus.WORLD_MAP) {
        this.checkLevelSave();
      }
      return;
    }
    switch(this.status) {
      case WORLD_MAP:
        break;
      case STARTING_NEW:
        if (this.newLevelThread == null) {
          p.global.errorMessage("ERROR: No thread in new level status.");
          break;
        }
        this.newLevelThread.stopThread();
        this.newLevelThread = null;
        break;
      case LOADING_SAVED:
        if (this.savedLevelThread == null) {
          p.global.errorMessage("ERROR: No thread in open level status.");
          break;
        }
        this.savedLevelThread.stopThread();
        this.savedLevelThread = null;
        break;
      case PLAYING:
        if (this.level == null) {
          p.global.errorMessage("ERROR: No level in playing status.");
          break;
        }
        this.level.save();
        this.level = null;
        break;
      default:
        p.global.errorMessage("ERROR: Playing status " + this.status + " not recognized.");
        break;
    }
    p.global.profile.curr_hero = code;
    p.global.profile.saveHeroesFile();
    this.returnToWorldMap();
    if (start_playing) {
      this.checkLevelSave();
    }
  }


  void completedLevel(int completion_code) {
    if (this.level == null || this.status != PlayingStatus.PLAYING) {
      p.global.errorMessage("ERROR: Can't complete level when not playing one.");
      return;
    }
    if (this.level.player == null || this.level.player.code == null || this.level.player.code == HeroCode.ERROR) {
      p.global.errorMessage("ERROR: Can't complete level without a player object.");
      return;
    }
    p.global.log("Completed level " + this.level.location.displayName() + " with code " + completion_code + ".");
    MinigameName potential_minigame = Location.minigameAfterCompletion(this.level.location, completion_code);
    if (potential_minigame != null) {
      if (p.global.profile.unlockedMinigame(potential_minigame)) { // option to play
        this.form = new ConfirmLaunchMinigameForm(p, potential_minigame);
      }
      else { // forced to play minigame
        p.global.profile.unlockMinigame(potential_minigame);
        this.form = new InformLaunchMinigameForm(p, potential_minigame);
      }
    }
    Location next_location = Location.nextLocation(this.level.location, completion_code);
    if (next_location == Location.ERROR) {
      p.global.errorMessage("ERROR: Completion code " + completion_code +
        " not recognized for location " + this.level.location.displayName() + ".");
    }
    this.level.player.stopAction();
    this.level.player.statuses.clear();
    this.level.player.restartAbilityTimers();
    FileSystem.deleteFolder(p, this.savePath + this.level.player.location.fileName());
    this.level.player.location = next_location;
    p.global.profile.saveHeroesFile();
    if (next_location.isArea()) {
      p.global.profile.unlockArea(next_location);
      switch(this.level.location.getCampaignStart()) {
        case FRANCISCAN_FRANCIS:
          p.global.profile.achievement(AchievementCode.COMPLETED_FRANCISCAN);
          break;
        case DANS_HOUSE:
          p.global.profile.achievement(AchievementCode.COMPLETED_DANSHOUSE);
          break;
        default:
          break;
      }
    }
    p.global.profile.levelCompleted();
    this.returnToWorldMap();
    p.global.auto_start_playing = true;
    this.level = null;
  }


  Hero getCurrentHeroIfExists() {
    if (this.level != null) {
      return this.level.player;
    }
    return null;
  }

  void saveLevel() {
    if (this.level == null) {
      return;
    }
    this.level.save();
    p.global.profile.saveHeroesFile();
  }

  void launchMinigame(MinigameName minigame) {
    if (minigame == null) {
      p.global.errorMessage("ERROR: Can't launch null minigame.");
      return;
    }
    if (!p.global.profile.unlockedMinigame(minigame)) {
      p.global.errorMessage("ERROR: Can't launch minigame that isn't unlocked.");
      return;
    }
    p.global.state = ProgramState.ENTERING_MINIGAMES;
    p.global.auto_launch_minigame = minigame;
  }

  void saveAndExitToMainMenu() {
    this.saveAndReturnToInitialState();
    p.global.state = ProgramState.ENTERING_MAINMENU;
  }

  void saveAndReturnToInitialState() {
    this.saveLevel();
    this.returnToWorldMap();
    this.level = null;
  }

  void loseFocus() {
    if (this.level != null) {
      this.level.loseFocus();
    }
  }

  void gainFocus() {
    if (this.level != null) {
      this.level.gainFocus();
    }
  }

  void restartTimers() {
    if (this.level != null) {
      this.level.restartTimers();
    }
  }

  void update(int millis) {
    boolean refreshLevelLocation = false;
    switch(this.status) {
      case WORLD_MAP:
        if (this.world_map != null) {
          this.world_map.update(millis);
          if (p.global.auto_start_playing) {
            p.global.auto_start_playing = false;
            this.check_level_save = true;
          }
        }
        else {
          p.rectMode(PConstants.CORNERS);
          p.noStroke();
          p.fill(DImg.ccolor(60));
          p.rect(this.left_panel.size, 0, p.width - this.right_panel.size, p.height);
          p.imageMode(PConstants.CENTER);
          int frame = (int)Math.floor(LNZ.gif_loading_frames * ((float)(millis %
            LNZ.gif_loading_time) / (1 + LNZ.gif_loading_time)));
          p.image(p.global.images.getImage("gifs/loading/" + frame + ".png"), 0.5 * p.width, 0.5 * p.height, 250, 250);
        }
        break;
      case STARTING_NEW:
        if (this.newLevelThread.isAlive()) {
          p.fill(p.global.color_mapBorder);
          p.noStroke();
          p.rectMode(PConstants.CORNERS);
          p.rect(this.left_panel.size, 0, p.width - this.right_panel.size, p.height);
          p.fill(p.global.color_loadingScreenBackground);
          p.rect(this.left_panel.size + LNZ.map_borderSize, LNZ.map_borderSize,
              p.width - this.right_panel.size - LNZ.map_borderSize, p.height - LNZ.map_borderSize);
          p.fill(DImg.ccolor(0));
          p.textSize(24);
          p.textAlign(PConstants.LEFT, PConstants.TOP);
          p.text(this.newLevelThread.curr_status + " ...", this.left_panel.size +
            LNZ.map_borderSize + 30, LNZ.map_borderSize + 30);
          p.imageMode(PConstants.CENTER);
          int frame = (int)Math.floor(LNZ.gif_loading_frames * ((float)(millis %
            LNZ.gif_loading_time) / (1 + LNZ.gif_loading_time)));
          p.image(p.global.images.getImage("gifs/loading/" + frame + ".png"), 0.5 * p.width, 0.5 * p.height, 250, 250);
        }
        else {
          if (this.newLevelThread.level == null || this.newLevelThread.level.nullify) {
            this.level = null;
            this.returnToWorldMap();
          }
          else {
            this.level = this.newLevelThread.level;
            this.level.setLocation(this.left_panel.size, 0, p.width - this.right_panel.size, p.height);
            this.level.restartTimers();
            if (this.level.album_name != null) {
              p.global.sounds.play_background(this.level.album_name);
            }
            this.status = PlayingStatus.PLAYING;
            if (this.level.location.isArea()) {
              this.buttons[0].message = "World\nMap";
            }
            else {
              this.buttons[0].message = "Abandon\nLevel";
            }
          }
          this.newLevelThread = null;
          return;
        }
        break;
      case LOADING_SAVED:
        if (this.savedLevelThread.isAlive()) {
          p.fill(p.global.color_mapBorder);
          p.noStroke();
          p.rectMode(PConstants.CORNERS);
          p.rect(this.left_panel.size, 0, p.width - this.right_panel.size, p.height);
          p.fill(p.global.color_loadingScreenBackground);
          p.rect(this.left_panel.size + LNZ.map_borderSize, LNZ.map_borderSize,
              p.width - this.right_panel.size - LNZ.map_borderSize, p.height - LNZ.map_borderSize);
          p.fill(DImg.ccolor(0));
          p.textSize(24);
          p.textAlign(PConstants.LEFT, PConstants.TOP);
          p.text(this.savedLevelThread.curr_status + " ...", this.left_panel.size +
            LNZ.map_borderSize + 30, LNZ.map_borderSize + 30);
          p.imageMode(PConstants.CENTER);
          int frame = (int)Math.floor(LNZ.gif_loading_frames * ((float)(millis %
            LNZ.gif_loading_time) / (1 + LNZ.gif_loading_time)));
          p.image(p.global.images.getImage("gifs/loading/" + frame + ".png"), 0.5 * p.width, 0.5 * p.height, 250, 250);
        }
        else {
          if (this.savedLevelThread.level == null || this.savedLevelThread.level.nullify ||
            this.savedLevelThread.level.curr_map == null || this.savedLevelThread.level.curr_map.nullify) {
            this.level = null;
            this.returnToWorldMap();
          }
          else {
            this.level = this.savedLevelThread.level;
            this.level.setLocation(this.left_panel.size, 0, p.width - this.right_panel.size, p.height);
            this.level.restartTimers();
            if (this.level.album_name != null) {
              p.global.sounds.play_background(this.level.album_name);
            }
            this.level.curr_map.addHeaderMessage(GameMapCode.displayName(this.level.curr_map.code));
            this.status = PlayingStatus.PLAYING;
            if (this.level.location.isArea()) {
              this.buttons[0].message = "World\nMap";
            }
            else {
              this.buttons[0].message = "Abandon\nLevel";
            }
          }
          this.savedLevelThread = null;
          return;
        }
        break;
      case PLAYING:
        if (this.level != null) {
          this.level.update(millis);
          if (this.level.completed) {
            this.completedLevel(this.level.completion_code);
          }
        }
        else {
          p.global.errorMessage("ERROR: In playing status but no level to update.");
          this.returnToWorldMap();
        }
        break;
      default:
        p.global.errorMessage("ERROR: Playing status " + this.status + " not recognized.");
        break;
    }
    if (this.left_panel.collapsing || this.right_panel.collapsing) {
      refreshLevelLocation = true;
    }
    this.left_panel.update(millis);
    this.right_panel.update(millis);
    if (this.right_panel.open && !this.right_panel.collapsing) {
      for (PlayingButton button : this.buttons) {
        button.update(millis);
      }
      if (this.level != null) {
        this.level.drawRightPanel(millis);
      }
    }
    if (this.left_panel.open && !this.left_panel.collapsing) {
      if (this.level != null) {
        this.level.drawLeftPanel(millis);
      }
      else if (this.world_map != null) {
        this.world_map.drawLeftPanel(millis);
      }
    }
    if (refreshLevelLocation) {
      if (this.level != null) {
        this.level.setLocation(this.left_panel.size, 0, p.width - this.right_panel.size, p.height);
      }
      else if (this.world_map != null) {
        this.world_map.setLocation(this.left_panel.size, 0, p.width - this.right_panel.size, p.height);
      }
    }
    if (this.check_level_save && this.status == PlayingStatus.WORLD_MAP) {
      this.checkLevelSave();
      this.check_level_save = false;
    }
  }

  void showNerdStats() {
    if (this.level != null) {
      this.level.displayNerdStats();
    }
    else {
      p.fill(DImg.ccolor(255));
      p.textSize(14);
      p.textAlign(PConstants.LEFT, PConstants.TOP);
      float y_stats = 1;
      p.text("FPS: " + (int)p.global.lastFPS, 1, y_stats);
    }
  }

  void mouseMove(float mX, float mY) {
    boolean refreshMapLocation = false;
    if (this.left_panel.clicked || this.right_panel.clicked) {
      refreshMapLocation = true;
    }
    if (this.status == PlayingStatus.WORLD_MAP && this.world_map != null) {
      this.world_map.mouseMove(mX, mY);
    }
    // level mouse move
    if (this.level != null) {
      this.level.mouseMove(mX, mY);
    }
    // left panel mouse move
    this.left_panel.mouseMove(mX, mY);
    if (this.left_panel.open && !this.left_panel.collapsing) {
      if (this.level != null) {
        if (this.level.leftPanelElementsHovered()) {
          this.left_panel.hovered = false;
        }
      }
    }
    // right panel mouse move
    this.right_panel.mouseMove(mX, mY);
    if (this.right_panel.open && !this.right_panel.collapsing) {
      for (PlayingButton button : this.buttons) {
        button.mouseMove(mX, mY);
      }
    }
    // refresh map location
    if (refreshMapLocation) {
      if (this.level != null) {
        this.level.setLocation(this.left_panel.size, 0, p.width - this.right_panel.size, p.height);
      }
      else if (this.world_map != null) {
        this.world_map.setLocation(this.left_panel.size, 0, p.width - this.right_panel.size, p.height);
      }
    }
    // cursor icon resolution
    if (this.left_panel.clicked || this.right_panel.clicked) {
      this.resizeButtons();
      p.global.setCursor("icons/cursor_resizeh_white.png");
    }
    else if (this.left_panel.hovered || this.right_panel.hovered) {
      p.global.setCursor("icons/cursor_resizeh.png");
    }
    else {
      p.global.defaultCursor("icons/cursor_resizeh_white.png", "icons/cursor_resizeh.png");
    }
  }

  void mousePress() {
    if (this.level != null) {
      this.level.mousePress();
    }
    this.left_panel.mousePress();
    this.right_panel.mousePress();
    if (this.status == PlayingStatus.WORLD_MAP && this.world_map != null) {
      this.world_map.mousePress();
      if (this.left_panel.clicked || this.right_panel.clicked) {
        this.world_map.dragging = false;
      }
    }
    if (this.left_panel.clicked || this.right_panel.clicked) {
      p.global.setCursor("icons/cursor_resizeh_white.png");
    }
    else {
      p.global.defaultCursor("icons/cursor_resizeh_white.png");
    }
    if (this.right_panel.open && !this.right_panel.collapsing) {
      for (PlayingButton button : this.buttons) {
        button.mousePress();
      }
    }
  }

  void mouseRelease(float mX, float mY) {
    if (this.status == PlayingStatus.WORLD_MAP && this.world_map != null) {
      this.world_map.mouseRelease(mX, mY);
    }
    if (this.level != null) {
      this.level.mouseRelease(mX, mY);
    }
    this.left_panel.mouseRelease(mX, mY);
    this.right_panel.mouseRelease(mX, mY);
    if (this.left_panel.hovered || this.right_panel.hovered) {
      p.global.setCursor("icons/cursor_resizeh.png");
    }
    else {
      p.global.defaultCursor("icons/cursor_resizeh.png", "icons/cursor_resizeh_white.png");
    }
    if (this.right_panel.open && !this.right_panel.collapsing) {
      for (PlayingButton button : this.buttons) {
        button.mouseRelease(mX, mY);
      }
    }
  }

  void scroll(int amount) {
    if (this.status == PlayingStatus.WORLD_MAP && this.world_map != null) {
      this.world_map.scroll(amount);
    }
    if (this.level != null) {
      this.level.scroll(amount);
    }
  }

  void keyPress(int key, int keyCode) {
    if (this.level != null) {
      this.level.keyPress(key, keyCode);
    }
  }

  void openEscForm() {
    if (this.level != null) {
      if (this.level.was_viewing_hero_tree) {
        return; // don't open esc menu if viewing hero tree
      }
    }
    this.form = new EscForm(p);
  }

  void keyRelease(int key, int keyCode) {
    if (this.level != null) {
      this.level.keyRelease(key, keyCode);
    }
  }
}