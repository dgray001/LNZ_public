package LNZModule;

import java.io.PrintWriter;
import processing.core.*;
import Button.*;
import DImg.DImg;
import Element.*;
import FileSystem.FileSystem;

enum TutorialStatus {
  INITIAL, STARTING_NEW, LOADING_SAVED, PLAYING;
}

class TutorialInterface extends InterfaceLNZ {

  abstract class TutorialButton extends RectangleButton {
    private LNZ p;
    TutorialButton(LNZ sketch) {
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

  class TutorialButton1 extends TutorialButton {
    TutorialButton1(LNZ sketch) {
      super(sketch);
      this.message = "Restart\nTutorial";
    }
    public void release() {
      if (!this.hovered) {
        return;
      }
      this.stayDehovered();
      TutorialInterface.this.form = new RestartTutorialForm(p);
    }
  }

  class TutorialButton2 extends TutorialButton {
    TutorialButton2(LNZ sketch) {
      super(sketch);
      this.message = "Options";
    }
    public void release() {
      if (!this.hovered) {
        return;
      }
      this.stayDehovered();
      TutorialInterface.this.form = new OptionsForm(p);
    }
  }

  class TutorialButton3 extends TutorialButton {
    TutorialButton3(LNZ sketch) {
      super(sketch);
      this.message = "Heroes";
    }
    public void release() {
      if (!this.hovered) {
        return;
      }
      this.stayDehovered();
      TutorialInterface.this.form = new HeroesForm(p);
    }
  }

  class TutorialButton4 extends TutorialButton {
    TutorialButton4(LNZ sketch) {
      super(sketch);
      this.message = "Main\nMenu";
    }
    public void release() {
      if (!this.hovered) {
        return;
      }
      this.stayDehovered();
      TutorialInterface.this.form = new GoToMainMenuForm(p);
    }
  }


  class GoToMainMenuForm extends ConfirmForm {
    GoToMainMenuForm(LNZ sketch) {
      super(sketch, "Main Menu", "Are you sure you want to save and exit to the main menu?");
    }
    public void submit() {
      this.canceled = true;
      TutorialInterface.this.saveAndExitToMainMenu();
    }
  }

  class RestartTutorialForm extends ConfirmForm {
    RestartTutorialForm(LNZ sketch) {
      super(sketch, "Restart Tutorial", "Are you sure you want to restart the tutorial? " +
        "Any current progress will be lost.");
    }
    public void submit() {
      this.canceled = true;
      TutorialInterface.this.startNewTutorial();
    }
  }


  class OpenNewTutorialThread extends Thread {
    private LNZ p;
    private Level level;
    private String curr_status = "";

    OpenNewTutorialThread(LNZ sketch) {
      super("OpenNewTutorialThread");
      this.setDaemon(true);
      this.p = sketch;
    }

    @Override
    public void run() {
      this.curr_status += "Creating New Tutorial";
      this.level = new Level(p, "data/locations", Location.TUTORIAL);
      if (this.level.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      this.curr_status += "\nCopying Data";
      String destination_folder = "data/profiles/" + p.global.profile.display_name.toLowerCase() + "/locations/";
      if (!FileSystem.folderExists(p, destination_folder)) {
        FileSystem.mkdir(p, destination_folder);
      }
      FileSystem.deleteFolder(p, destination_folder + Location.TUTORIAL.fileName());
      FileSystem.copyFolder(p, "data/locations/" + Location.TUTORIAL.fileName(),
        destination_folder + Location.TUTORIAL.fileName());
      this.level.folderPath = destination_folder;
      this.level.save();
      if (this.level.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      this.curr_status += "\nOpening Map";
      this.level.setPlayer(new Hero(p, HeroCode.BEN));
      if (this.level.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      if (!p.global.images.loaded_map_gifs) {
        this.curr_status += "\nLoading Animations";
        p.global.images.loadMapGifs();
      }
    }
  }


  class OpenSavedTutorialThread extends Thread {
    private Level level;
    private String curr_status = "";

    OpenSavedTutorialThread() {
      super("OpenSavedTutorialThread");
    }

    @Override
    public void run() {
      this.curr_status += "Opening Saved Tutorial";
      String destination_folder = "data/profiles/" + p.global.profile.display_name.toLowerCase() + "/locations/";
      this.level = new Level(p, destination_folder, Location.TUTORIAL);
      if (this.level.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      this.curr_status += "\nOpening Hero";
      Hero hero = Hero.readHeroFile(p, destination_folder + "tutorial/hero.lnz");
      if (!p.global.lastErrorMessage().equals("None")) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      this.curr_status += "\nOpening Map";
      this.level.openCurrMap();
      this.level.addPlayer(hero);
      if (this.level.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      if (!p.global.images.loaded_map_gifs) {
        this.curr_status += "\nLoading Animations";
        p.global.images.loadMapGifs();
      }
    }
  }


  private TutorialButton[] buttons = new TutorialButton[4];
  private Panel leftPanel = new Panel(p, PConstants.LEFT, LNZ.mapEditor_panelMinWidth,
    LNZ.mapEditor_panelMaxWidth, LNZ.mapEditor_panelStartWidth);
  private Panel rightPanel = new Panel(p, PConstants.RIGHT, LNZ.mapEditor_panelMinWidth,
    LNZ.mapEditor_panelMaxWidth, LNZ.mapEditor_panelStartWidth);

  private TutorialStatus status = TutorialStatus.INITIAL;
  private Level tutorial = null;

  private OpenNewTutorialThread newTutorialThread = null;
  private OpenSavedTutorialThread savedTutorialThread = null;


  TutorialInterface(LNZ sketch) {
    super(sketch);
    this.buttons[0] = new TutorialButton1(sketch);
    this.buttons[1] = new TutorialButton2(sketch);
    this.buttons[2] = new TutorialButton3(sketch);
    this.buttons[3] = new TutorialButton4(sketch);
    this.leftPanel.addIcon(p.global.images.getImage("icons/triangle_gray.png"));
    this.rightPanel.addIcon(p.global.images.getImage("icons/triangle_gray.png"));
    this.leftPanel.color_background = p.global.color_panelBackground;
    this.rightPanel.color_background = p.global.color_panelBackground;
    this.resizeButtons();
    this.checkTutorialSave();
  }


  void resizeButtons() {
    double buttonSize = (this.rightPanel.size_curr - 5 * LNZ.mapEditor_buttonGapSize) / 4.0;
    double xi =p.width - this.rightPanel.size_curr + LNZ.mapEditor_buttonGapSize;
    this.buttons[0].setXLocation(xi, xi + buttonSize);
    xi += buttonSize + LNZ.mapEditor_buttonGapSize;
    this.buttons[1].setXLocation(xi, xi + buttonSize);
    xi += buttonSize + LNZ.mapEditor_buttonGapSize;
    this.buttons[2].setXLocation(xi, xi + buttonSize);
    xi += buttonSize + LNZ.mapEditor_buttonGapSize;
    this.buttons[3].setXLocation(xi, xi + buttonSize);
  }


  void checkTutorialSave() {
    if (FileSystem.folderExists(p, "data/profiles/" + p.global.profile.display_name.toLowerCase() +
      "/locations/" + Location.TUTORIAL.fileName())) {
      this.status = TutorialStatus.LOADING_SAVED;
      this.tutorial = null;
      this.savedTutorialThread = new OpenSavedTutorialThread();
      this.savedTutorialThread.start();
      return;
    }
    this.startNewTutorial();
  }

  void startNewTutorial() {
    this.status = TutorialStatus.STARTING_NEW;
    this.tutorial = null;
    this.newTutorialThread = new OpenNewTutorialThread(p);
    this.newTutorialThread.start();
  }

  void completedTutorial(int completion_code) {
    p.global.log("Completed tutorial with code " + completion_code + ".");
    switch(completion_code) {
      case 0: // default
        this.tutorial = null;
        FileSystem.deleteFolder(p, this.destination_folder() + Location.TUTORIAL.fileName());
        p.global.profile.achievement(AchievementCode.COMPLETED_TUTORIAL);
        this.saveAndExitToMainMenu();
        break;
      default:
        p.global.errorMessage("ERROR: Completion code " + completion_code + " not recognized for tutorial.");
        break;
    }
  }


  Hero getCurrentHeroIfExists() {
    if (this.tutorial != null) {
      return this.tutorial.player;
    }
    return null;
  }

  String destination_folder() {
    return ("data/profiles/" + p.global.profile.display_name.toLowerCase() + "/locations/");
  }

  void saveTutorial() {
    if (this.tutorial == null) {
      return;
    }
    this.tutorial.save();
    if (this.tutorial.player == null) {
      return;
    }
    PrintWriter file = p.createWriter(this.destination_folder() + Location.TUTORIAL.fileName() + "/hero.lnz");
    file.println(this.tutorial.player.fileString());
    file.flush();
    file.close();
  }

  void saveAndExitToMainMenu() {
    this.saveTutorial();
    this.tutorial = null;
    p.global.state = ProgramState.ENTERING_MAINMENU;
  }

  void loseFocus() {
    if (this.tutorial != null) {
      this.tutorial.loseFocus();
    }
  }

  void gainFocus() {
    if (this.tutorial != null) {
      this.tutorial.gainFocus();
    }
  }

  void restartTimers() {
    if (this.tutorial != null) {
      this.tutorial.restartTimers();
    }
  }

  void update(int millis) {
    boolean refreshLevelLocation = false;
    switch(this.status) {
      case INITIAL:
        p.rectMode(PConstants.CORNERS);
        p.noStroke();
        p.fill(60);
        p.rect(this.leftPanel.size, 0,p.width - this.rightPanel.size,p.height);
        break;
      case STARTING_NEW:
        if (this.newTutorialThread.isAlive()) {
          p.fill(p.global.color_mapBorder);
          p.noStroke();
          p.rectMode(PConstants.CORNERS);
          p.rect(this.leftPanel.size, 0,p.width - this.rightPanel.size,p.height);
          p.fill(p.global.color_loadingScreenBackground);
          p.rect(this.leftPanel.size + LNZ.map_borderSize, LNZ.map_borderSize,
             p.width - this.rightPanel.size - LNZ.map_borderSize,p.height - LNZ.map_borderSize);
          p.fill(0);
          p.textSize(24);
          p.textAlign(PConstants.LEFT, PConstants.TOP);
          p.text(this.newTutorialThread.curr_status + " ...", this.leftPanel.size +
            LNZ.map_borderSize + 30, LNZ.map_borderSize + 30);
          p.imageMode(PConstants.CENTER);
          int frame = (int)(LNZ.gif_loading_frames * (double)(millis %
            LNZ.gif_loading_time) / (1d + LNZ.gif_loading_time));
          p.image(p.global.images.getImage("gifs/loading/" + frame + ".png"), 0.5 *p.width, 0.5 *p.height, 250, 250);
        }
        else {
          if (this.newTutorialThread.level == null || this.newTutorialThread.level.nullify) {
            this.tutorial = null;
            this.status = TutorialStatus.INITIAL;
          }
          else {
            this.tutorial = this.newTutorialThread.level;
            this.tutorial.setLocation(this.leftPanel.size, 0,p.width - this.rightPanel.size,p.height);
            this.tutorial.restartTimers();
            this.status = TutorialStatus.PLAYING;
            this.saveTutorial();
          }
          this.newTutorialThread = null;
          return;
        }
        break;
      case LOADING_SAVED:
        if (this.savedTutorialThread.isAlive()) {
          p.fill(p.global.color_mapBorder);
          p.noStroke();
          p.rectMode(PConstants.CORNERS);
          p.rect(this.leftPanel.size, 0,p.width - this.rightPanel.size,p.height);
          p.fill(p.global.color_loadingScreenBackground);
          p.rect(this.leftPanel.size + LNZ.map_borderSize, LNZ.map_borderSize,
             p.width - this.rightPanel.size - LNZ.map_borderSize,p.height - LNZ.map_borderSize);
          p.fill(0);
          p.textSize(24);
          p.textAlign(PConstants.LEFT, PConstants.TOP);
          p.text(this.savedTutorialThread.curr_status + " ...", this.leftPanel.size +
            LNZ.map_borderSize + 30, LNZ.map_borderSize + 30);
          p.imageMode(PConstants.CENTER);
          int frame = (int)(LNZ.gif_loading_frames * (double)(millis %
            LNZ.gif_loading_time) / (1d + LNZ.gif_loading_time));
          p.image(p.global.images.getImage("gifs/loading/" + frame + ".png"), 0.5 *p.width, 0.5 *p.height, 250, 250);
        }
        else {
          if (this.savedTutorialThread.level == null || this.savedTutorialThread.level.nullify) {
            this.tutorial = null;
            this.status = TutorialStatus.INITIAL;
          }
          else {
            this.tutorial = this.savedTutorialThread.level;
            this.tutorial.setLocation(this.leftPanel.size, 0,p.width - this.rightPanel.size,p.height);
            this.status = TutorialStatus.PLAYING;
          }
          this.savedTutorialThread = null;
          return;
        }
        break;
      case PLAYING:
        if (this.tutorial != null) {
          this.tutorial.update(millis);
          if (this.leftPanel.collapsing || this.rightPanel.collapsing) {
            refreshLevelLocation = true;
          }
          if (this.tutorial.completed) {
            this.completedTutorial(this.tutorial.completion_code);
          }
        }
        break;
      default:
        p.global.errorMessage("Tutorial status " + this.status + " not recognized.");
        break;
    }
    this.leftPanel.update(millis);
    this.rightPanel.update(millis);
    if (this.rightPanel.open && !this.rightPanel.collapsing) {
      for (TutorialButton button : this.buttons) {
        button.update(millis);
      }
      if (this.tutorial != null) {
        this.tutorial.drawRightPanel(millis);
      }
    }
    if (this.leftPanel.open && !this.leftPanel.collapsing) {
      if (this.tutorial != null) {
        this.tutorial.drawLeftPanel(millis);
      }
    }
    if (refreshLevelLocation) {
      if (this.tutorial != null) {
        this.tutorial.setLocation(this.leftPanel.size, 0,p.width - this.rightPanel.size,p.height);
      }
    }
  }

  void showNerdStats() {
    if (this.tutorial != null) {
      this.tutorial.displayNerdStats();
    }
    else {
      this.showDefaultNerdStats();
    }
  }

  void mouseMove(float mX, float mY) {
    boolean refreshMapLocation = false;
    // level mouse move
    if (this.tutorial != null) {
      this.tutorial.mouseMove(mX, mY);
      if (this.leftPanel.clicked || this.rightPanel.clicked) {
        refreshMapLocation = true;
      }
    }
    // left panel mouse move
    this.leftPanel.mouseMove(mX, mY);
    if (this.leftPanel.open && !this.leftPanel.collapsing) {
      if (this.tutorial != null) {
        if (this.tutorial.leftPanelElementsHovered()) {
          this.leftPanel.hovered = false;
        }
      }
      else if (this.tutorial != null) {
        if (this.tutorial.leftPanelElementsHovered()) {
          this.leftPanel.hovered = false;
        }
      }
    }
    // right panel mouse move
    this.rightPanel.mouseMove(mX, mY);
    if (this.rightPanel.open && !this.rightPanel.collapsing) {
      for (TutorialButton button : this.buttons) {
        button.mouseMove(mX, mY);
      }
    }
    // refresh map location
    if (refreshMapLocation) {
      if (this.tutorial != null) {
        this.tutorial.setLocation(this.leftPanel.size, 0,p.width - this.rightPanel.size,p.height);
      }
    }
    // cursor icon resolution
    if (this.leftPanel.clicked || this.rightPanel.clicked) {
      this.resizeButtons();
      p.global.setCursor("icons/cursor_resizeh_white.png");
    }
    else if (this.leftPanel.hovered || this.rightPanel.hovered) {
      p.global.setCursor("icons/cursor_resizeh.png");
    }
    else {
      p.global.defaultCursor("icons/cursor_resizeh_white.png", "icons/cursor_resizeh.png");
    }
  }

  void mousePress() {
    if (this.tutorial != null) {
      this.tutorial.mousePress();
    }
    this.leftPanel.mousePress();
    this.rightPanel.mousePress();
    if (this.leftPanel.clicked || this.rightPanel.clicked) {
      p.global.setCursor("icons/cursor_resizeh_white.png");
    }
    else {
      p.global.defaultCursor("icons/cursor_resizeh_white.png");
    }
    if (this.rightPanel.open && !this.rightPanel.collapsing) {
      for (TutorialButton button : this.buttons) {
        button.mousePress();
      }
    }
  }

  void mouseRelease(float mX, float mY) {
    if (this.tutorial != null) {
      this.tutorial.mouseRelease(mX, mY);
    }
    this.leftPanel.mouseRelease(mX, mY);
    this.rightPanel.mouseRelease(mX, mY);
    if (this.leftPanel.hovered || this.rightPanel.hovered) {
      p.global.setCursor("icons/cursor_resizeh.png");
    }
    else {
      p.global.defaultCursor("icons/cursor_resizeh.png", "icons/cursor_resizeh_white.png");
    }
    if (this.rightPanel.open && !this.rightPanel.collapsing) {
      for (TutorialButton button : this.buttons) {
        button.mouseRelease(mX, mY);
      }
    }
  }

  void scroll(int amount) {
    if (this.tutorial != null) {
      this.tutorial.scroll(amount);
    }
  }

  void keyPress(int key, int keyCode) {
    if (this.tutorial != null) {
      this.tutorial.keyPress(key, keyCode);
    }
  }

  void openEscForm() {
    this.form = new EscForm(p);
  }

  void keyRelease(int key, int keyCode) {
    if (this.tutorial != null) {
      this.tutorial.keyRelease(key, keyCode);
    }
  }
}
