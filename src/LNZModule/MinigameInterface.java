package LNZModule;

import java.util.*;
import processing.core.*;
import Button.*;
import DImg.DImg;
import Element.*;
import LNZApplet.LNZApplet;

enum MinigameStatus {
  INITIAL, LAUNCHING, PLAYING;
}

class MinigameInterface extends InterfaceLNZ {

  abstract class MinigameButton extends RectangleButton {
    protected LNZ p;
    MinigameButton(LNZ sketch) {
      super(sketch, sketch.width - LNZ.mapEditor_buttonGapSize - LNZ.
        minigames_minigameButtonWidth, 0, sketch.width - LNZ.mapEditor_buttonGapSize, 0);
      this.p = sketch;
      this.raised_border = true;
      this.roundness = 0;
      this.setColors(DImg.ccolor(170), DImg.ccolor(90, 140, 155), DImg.ccolor(110, 170, 195), DImg.ccolor(80, 130, 150), DImg.ccolor(0));
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

  class MinigameButton1 extends MinigameButton {
    private boolean currently_playing = false;
    MinigameButton1(LNZ sketch) {
      super(sketch);
      this.message = "Main\nMenu";
    }
    void minigameStarted() {
      this.currently_playing = true;
      this.message = "Exit\nGame";
    }
    void minigameEnded() {
      this.currently_playing = false;
      this.message = "Main\nMenu";
    }
    public void release() {
      if (!this.hovered) {
        return;
      }
      this.stayDehovered();
      if (this.currently_playing) {
        MinigameInterface.this.form = new ExitMinigameForm(p);
      }
      else if (MinigameInterface.this.status == MinigameStatus.INITIAL) {
        MinigameInterface.this.saveAndExitToMainMenu();
      }
      else {
        MinigameInterface.this.form = new GoToMainMenuForm(p);
      }
    }
  }


  class GoToMainMenuForm extends ConfirmForm {
    GoToMainMenuForm(LNZ sketch) {
      super(sketch, "Main Menu", "Are you sure you want to exit to the main menu?");
    }
    public void submit() {
      this.canceled = true;
      MinigameInterface.this.saveAndExitToMainMenu();
    }
  }


  class ExitMinigameForm extends ConfirmForm {
    ExitMinigameForm(LNZ sketch) {
      super(sketch, "Exit Game", "Are you sure you want to exit the minigame?");
    }
    public void submit() {
      this.canceled = true;
      MinigameInterface.this.exitMinigame();
    }
  }


  class MinigameChooser {
    class MinigameChooserButton extends ImageButton {
      private MinigameName minigame;

      MinigameChooserButton(LNZ sketch, MinigameName minigame, boolean unlocked) {
        super(sketch, sketch.global.images.getImage(minigame.imagePath()), 0, 0, 0, 0);
        this.minigame = minigame;
        this.use_time_elapsed = true;
        this.show_message = true;
        this.message = minigame.displayName();
        this.text_size = 22;
        this.color_text = DImg.ccolor(255);
        if (!unlocked) {
          this.disabled = true;
        }
      }

      @Override
      public void drawButton() {
        super.drawButton();
        p.rectMode(PConstants.CORNERS);
        p.strokeWeight(0.01);
        if (this.clicked) {
          p.fill(200, 100);
          p.stroke(200, 100);
          p.rect(this.xi, this.yi, this.xf, this.yf);
        }
        else if (this.hovered) {
          p.fill(200, 200);
          p.stroke(200, 200);
          p.rect(this.xi, this.yi, this.xf, this.yf);
        }
      }

      public void hover() {
        MinigameChooser.this.hovered(this.minigame);
      }
      public void dehover() {
        MinigameChooser.this.dehovered(this.minigame);
      }
      public void click() {}
      public void release() {
        if (this.hovered) {
          MinigameChooser.this.chooseMinigame(this.minigame);
        }
      }
    }

    private ArrayList<MinigameChooserButton> buttons = new ArrayList<MinigameChooserButton>();
    private ScrollBar scrollbar;
    private MinigameName minigame_hovered = null;

    private double xi = 0;
    private double yi = 0;
    private double xf = 0;
    private double yf = 0;

    private boolean hovered = false;

    MinigameChooser(LNZ sketch) {
      this.scrollbar = new ScrollBar(sketch, false);
      this.scrollbar.setButtonColors(DImg.ccolor(170), DImg.ccolor(90, 140, 155),
        DImg.ccolor(110, 170, 195), DImg.ccolor(80, 130, 150), DImg.ccolor(0));
      this.scrollbar.useElapsedTime();
      for (MinigameName minigame : MinigameName.VALUES) {
        this.buttons.add(new MinigameChooserButton(sketch, minigame, p.global.profile.minigames.contains(minigame)));
      }
    }

    void hovered(MinigameName minigame) {
      this.minigame_hovered = minigame;
    }

    void dehovered(MinigameName minigame) {
      if (minigame == this.minigame_hovered) {
        this.minigame_hovered = null;
      }
    }

    void chooseMinigame(MinigameName minigame) {
      MinigameInterface.this.launchMinigame(minigame);
    }

    void setLocation(double xi, double yi, double xf, double yf) {
      this.xi = xi;
      this.yi = yi;
      this.xf = xf;
      this.yf = yf;
      double button_width = this.yf - this.yi - 3 * LNZ.minigames_edgeGap - LNZ.minigames_scrollbarWidth;
      for (MinigameChooserButton button : this.buttons) {
        button.setLocation(0, 0, button_width, button_width);
      }
      this.scrollbar.setLocation(this.xi + LNZ.minigames_edgeGap, this.yf -
        LNZ.minigames_edgeGap - LNZ.minigames_scrollbarWidth, this.xf -
        LNZ.minigames_edgeGap, this.yf - LNZ.minigames_edgeGap);
      double all_buttons_width = this.buttons.size() * (button_width + LNZ.
        minigames_buttonGap) - LNZ.minigames_buttonGap;
      double excess_width = Math.max(0, all_buttons_width - (this.xf - this.xi - 2 * LNZ.minigames_edgeGap));
      this.scrollbar.updateMaxValue(LNZApplet.round(Math.ceil(excess_width / (button_width + LNZ.minigames_edgeGap))));
    }

    void update(int time_elapsed) {
      this.scrollbar.update(time_elapsed);
      p.translate(0, this.yi + LNZ.minigames_edgeGap);
      double x_translate = this.xi + LNZ.minigames_edgeGap;
      for (int i = LNZApplet.round(this.scrollbar.value); i < this.buttons.size(); i++) {
        if (x_translate + this.buttons.get(i).buttonWidth() > this.xf) {
          break;
        }
        p.translate(x_translate, 0);
        this.buttons.get(i).update(time_elapsed);
        p.translate(-x_translate, 0);
        x_translate += this.buttons.get(i).buttonWidth() + LNZ.minigames_buttonGap;
      }
      p.translate(0, -this.yi - LNZ.minigames_edgeGap);
    }

    void mouseMove(float mX, float mY) {
      this.scrollbar.mouseMove(mX, mY);
      if (mX > this.xi && mX < this.xf && mY > this.yi && mY < this.yf) {
        this.hovered = true;
      }
      else {
        this.hovered = false;
      }
      double x_translate = this.xi + LNZ.minigames_edgeGap;
      for (int i = LNZApplet.round(this.scrollbar.value); i < this.buttons.size(); i++) {
        if (x_translate + this.buttons.get(i).buttonWidth() > this.xf) {
          break;
        }
        this.buttons.get(i).mouseMove(mX - (float)x_translate, (float)(mY - this.yi - LNZ.minigames_edgeGap));
        x_translate += this.buttons.get(i).buttonWidth() + LNZ.minigames_buttonGap;
      }
    }

    void mousePress() {
      this.scrollbar.mousePress();
      double x_translate = this.xi + LNZ.minigames_edgeGap;
      for (int i = LNZApplet.round(this.scrollbar.value); i < this.buttons.size(); i++) {
        if (x_translate + this.buttons.get(i).buttonWidth() > this.xf) {
          break;
        }
        this.buttons.get(i).mousePress();
        x_translate += this.buttons.get(i).buttonWidth() + LNZ.minigames_buttonGap;
      }
    }

    void mouseRelease(float mX, float mY) {
      this.scrollbar.mouseRelease(mX, mY);
      double x_translate = this.xi + LNZ.minigames_edgeGap;
      for (int i = LNZApplet.round(this.scrollbar.value); i < this.buttons.size(); i++) {
        if (x_translate + this.buttons.get(i).buttonWidth() > this.xf) {
          break;
        }
        this.buttons.get(i).mouseRelease(mX - (float)x_translate, (float)(mY - this.yi - LNZ.minigames_edgeGap));
        x_translate += this.buttons.get(i).buttonWidth() + LNZ.minigames_buttonGap;
      }
    }

    void scroll(int amount) {
      if (this.hovered) {
        this.scrollbar.increaseValue(amount);
      }
    }
  }


  class InitializeMinigameThread extends Thread {
    private MinigameName name;
    private Minigame minigame;
    InitializeMinigameThread(MinigameName name) {
      super("InitializeMinigameThread");
      this.name = name;
    }
    @Override
    public void run() {
      this.minigame = MinigameInterface.this.initializeMinigame(this.name);
      p.global.images.loadMinigameImages(this.name);
    }
  }


  class MouseMoveThread extends Thread {
    private float mX = 0;
    private float mY = 0;
    private boolean start_again = false;
    private float next_mX = 0;
    private float next_mY = 0;
    MouseMoveThread(float mX, float mY) {
      super("MouseMoveThread");
      this.setDaemon(true);
      this.mX = mX;
      this.mY = mY;
    }
    void startAgain(float mX, float mY) {
      this.start_again = true;
      this.next_mX = mX;
      this.next_mY = mY;
    }
    @Override
    public void run() {
      while(true) {
        boolean refreshMapLocation = false;
        if (MinigameInterface.this.status == MinigameStatus.INITIAL) {
          MinigameInterface.this.minigame_chooser.mouseMove(this.mX, this.mY);
        }
        // minigame mouse move
        if (MinigameInterface.this.minigame != null) {
          MinigameInterface.this.minigame.mouseMove(this.mX, this.mY);
          if (MinigameInterface.this.bottom_panel.clicked) {
            refreshMapLocation = true;
          }
        }
        // right panel mouse move
        MinigameInterface.this.bottom_panel.mouseMove(this.mX, this.mY);
        if (MinigameInterface.this.bottom_panel.open && !MinigameInterface.this.bottom_panel.collapsing) {
          for (MinigameButton button : MinigameInterface.this.buttons) {
            button.mouseMove(this.mX, this.mY);
          }
        }
        // refresh minigame location
        if (refreshMapLocation) {
          if (MinigameInterface.this.minigame != null) {
            MinigameInterface.this.minigame.setLocation(0, 0, p.width, p.height - MinigameInterface.this.bottom_panel.size);
          }
        }
        // cursor icon resolution
        if (MinigameInterface.this.bottom_panel.clicked) {
          MinigameInterface.this.resizeButtons();
          p.global.setCursor("icons/cursor_resizeh_white.png");
        }
        else if (MinigameInterface.this.bottom_panel.hovered) {
          p.global.setCursor("icons/cursor_resizeh.png");
        }
        else {
          p.global.defaultCursor("icons/cursor_resizeh_white.png", "icons/cursor_resizeh.png");
        }
        if (this.start_again) {
          this.mX = this.next_mX;
          this.mY = this.next_mY;
          this.start_again = false;
          continue;
        }
        break;
      }
    }
  }


  private MinigameButton[] buttons = new MinigameButton[1];
  private Panel bottom_panel;
  private MinigameChooser minigame_chooser;
  private InitializeMinigameThread initialize_minigame_thread = null;
  private MouseMoveThread mouse_move_thread = null;

  private Minigame minigame = null;
  private MinigameStatus status = MinigameStatus.INITIAL;
  private boolean return_to_playing = false;
  private int last_update_time = 0;


  MinigameInterface(LNZ sketch) {
    super(sketch);
    this.bottom_panel = new Panel(sketch, PConstants.DOWN, LNZ.minigames_panelWidth);
    this.minigame_chooser = new MinigameChooser(sketch);
    this.buttons[0] = new MinigameButton1(sketch);
    this.bottom_panel.removeButton();
    this.bottom_panel.cant_resize = true;
    this.bottom_panel.color_background = DImg.ccolor(50, 80, 100, 150);
    this.resizeButtons();
    if (p.global.auto_launch_minigame != null) {
      this.launchMinigame(p.global.auto_launch_minigame);
      this.return_to_playing = true;
    }
    p.global.auto_launch_minigame = null;
  }


  void resizeButtons() {
    double buttonSize = (this.bottom_panel.size_curr - 5 * LNZ.mapEditor_buttonGapSize) / 2.0;
    double yf = p.height - LNZ.mapEditor_buttonGapSize;
    this.buttons[0].setYLocation(yf - buttonSize, yf);
    yf -= buttonSize + LNZ.mapEditor_buttonGapSize;
    this.minigame_chooser.setLocation(5, p.height + 5 - this.bottom_panel.size, this.buttons[0].xi - 5, p.height - 5);
  }


  Minigame initializeMinigame(MinigameName code) {
    switch(code) {
      case ZAMBOS_CAR_BREAK_DOWN:
        return new Zambos(p, ZambosMap.CAR_BREAK_DOWN);
      case CHESS:
        return new Chess(p);
      default:
        return null;
    }
  }


  void launchMinigame(MinigameName name) {
    if (this.minigame != null || this.status != MinigameStatus.INITIAL) {
      p.global.errorMessage("ERROR: Can't launch minigame when playing one.");
      return;
    }
    this.status = MinigameStatus.LAUNCHING;
    this.initialize_minigame_thread = new InitializeMinigameThread(name);
    this.initialize_minigame_thread.start();
    ((MinigameButton1)this.buttons[0]).minigameStarted();
  }

  void completedMinigame() {
    if (this.minigame == null || this.status != MinigameStatus.PLAYING) {
      p.global.errorMessage("ERROR: Can't complete minigame when not playing one.");
      return;
    }
    p.global.log("Completed minigame " + this.minigame.displayName() + ".");
    this.exitMinigame();
  }

  void exitMinigame() {
    if (this.minigame == null || this.status == MinigameStatus.INITIAL) {
      p.global.errorMessage("ERROR: Can't exit minigame when not playing one.");
      return;
    }
    this.status = MinigameStatus.INITIAL;
    this.minigame = null;
    ((MinigameButton1)this.buttons[0]).minigameEnded();
    if (this.return_to_playing) {
      p.global.state = ProgramState.ENTERING_PLAYING;
      p.global.auto_start_playing = true;
    }
  }


  Hero getCurrentHeroIfExists() {
    return null;
  }

  void saveAndExitToMainMenu() {
    this.minigame = null;
    this.status = MinigameStatus.INITIAL;
    p.global.state = ProgramState.ENTERING_MAINMENU;
  }

  void loseFocus() {
    if (this.minigame != null) {
      this.minigame.loseFocus();
    }
  }

  void gainFocus() {
    if (this.minigame != null) {
      this.minigame.gainFocus();
    }
  }

  void restartTimers() {
    if (this.minigame != null) {
      this.minigame.restartTimers();
    }
  }

  void update(int millis) {
    int time_elapsed = millis - this.last_update_time;
    boolean refreshMinigameLocation = false;
    switch(this.status) {
      case INITIAL:
        p.rectMode(PConstants.CORNERS);
        p.stroke(60);
        p.fill(60);
        p.rect(0, 0, p.width, p.height - this.bottom_panel.size);
        this.minigame_chooser.update(time_elapsed);
        if (this.minigame_chooser.minigame_hovered != null) {
          p.imageMode(PConstants.CENTER);
          p.image(p.global.images.getImage(this.minigame_chooser.minigame_hovered.
            imagePath()), 0.5 * p.width, 0.5 * (p.height - this.bottom_panel.size),
            0.4 * (p.height - this.bottom_panel.size), 0.4 * (p.height - this.bottom_panel.size));
          p.fill(255);
          p.textSize(50);
          p.textAlign(PConstants.CENTER, PConstants.TOP);
          p.text(this.minigame_chooser.minigame_hovered.displayName(), 0.5 * p.width, 5);
        }
        break;
      case LAUNCHING:
        p.rectMode(PConstants.CORNERS);
        p.stroke(60);
        p.fill(60);
        p.rect(0, 0, p.width, p.height - this.bottom_panel.size);
        if (this.initialize_minigame_thread == null) {
          this.status = MinigameStatus.INITIAL;
          break;
        }
        if (this.initialize_minigame_thread.isAlive()) {
          p.imageMode(PConstants.CENTER);
          int frame = (int)Math.floor(LNZ.gif_loading_frames * ((float)(millis %
            LNZ.gif_loading_time) / (1 + LNZ.gif_loading_time)));
          p.image(p.global.images.getImage("gifs/loading/" + frame + ".png"), 0.5 * p.width, 0.5 * p.height, 250, 250);
          break;
        }
        if (this.initialize_minigame_thread.minigame == null) {
          this.status = MinigameStatus.INITIAL;
          this.initialize_minigame_thread = null;
          break;
        }
        this.minigame = this.initialize_minigame_thread.minigame;
        this.status = MinigameStatus.PLAYING;
        this.minigame.setLocation(0, 0, p.width, p.height - this.bottom_panel.size);
        this.initialize_minigame_thread = null;
        break;
      case PLAYING:
        if (this.minigame != null) {
          p.rectMode(PConstants.CORNERS);
          p.stroke(this.minigame.color_background);
          p.fill(this.minigame.color_background);
          p.rect(0, 0, p.width, p.height - this.bottom_panel.size);
          this.minigame.update(time_elapsed);
          if (this.bottom_panel.collapsing) {
            refreshMinigameLocation = true;
          }
          if (this.minigame.completed) {
            this.completedMinigame();
          }
        }
        else {
          p.rectMode(PConstants.CORNERS);
          p.stroke(60);
          p.fill(60);
          p.rect(0, 0, p.width, p.height - this.bottom_panel.size);
          p.global.errorMessage("ERROR: In playing status but no level to update.");
          this.status = MinigameStatus.INITIAL;
        }
        break;
      default:
        p.global.errorMessage("ERROR: Minigame status " + this.status + " not recognized.");
        break;
    }
    this.bottom_panel.update(millis);
    if (this.bottom_panel.open && !this.bottom_panel.collapsing) {
      for (MinigameButton button : this.buttons) {
        button.update(millis);
      }
      if (this.minigame != null) {
        this.minigame.drawBottomPanel(time_elapsed);
      }
    }
    if (refreshMinigameLocation) {
      if (this.minigame != null) {
        this.minigame.setLocation(0, 0, p.width, p.height - this.bottom_panel.size);
      }
    }
    this.last_update_time = millis;
  }

  void showNerdStats() {
    if (this.minigame != null) {
      this.minigame.displayNerdStats();
    }
    else {
      p.fill(255);
      p.textSize(14);
      p.textAlign(PConstants.LEFT, PConstants.TOP);
      float y_stats = 1;
      p.text("FPS: " + (int)p.global.lastFPS, 1, y_stats);
    }
  }

  void mouseMove(float mX, float mY) {
    if (this.mouse_move_thread != null && this.mouse_move_thread.isAlive()) {
      this.mouse_move_thread.startAgain(mX, mY);
    }
    this.mouse_move_thread = new MouseMoveThread(mX, mY);
    this.mouse_move_thread.start();
  }

  void mousePress() {
    if (this.minigame != null) {
      this.minigame.mousePress();
    }
    if (this.status == MinigameStatus.INITIAL) {
      this.minigame_chooser.mousePress();
    }
    this.bottom_panel.mousePress();
    if (this.bottom_panel.clicked) {
      p.global.setCursor("icons/cursor_resizeh_white.png");
    }
    else {
      p.global.defaultCursor("icons/cursor_resizeh_white.png");
    }
    if (this.bottom_panel.open && !this.bottom_panel.collapsing) {
      for (MinigameButton button : this.buttons) {
        button.mousePress();
      }
    }
  }

  void mouseRelease(float mX, float mY) {
    if (this.minigame != null) {
      this.minigame.mouseRelease(mX, mY);
    }
    if (this.status == MinigameStatus.INITIAL) {
      this.minigame_chooser.mouseRelease(mX, mY);
    }
    this.bottom_panel.mouseRelease(mX, mY);
    if (this.bottom_panel.hovered) {
      p.global.setCursor("icons/cursor_resizeh.png");
    }
    else {
      p.global.defaultCursor("icons/cursor_resizeh.png", "icons/cursor_resizeh_white.png");
    }
    if (this.bottom_panel.open && !this.bottom_panel.collapsing) {
      for (MinigameButton button : this.buttons) {
        button.mouseRelease(mX, mY);
      }
    }
  }

  void scroll(int amount) {
    if (this.minigame != null) {
      this.minigame.scroll(amount);
    }
    if (this.status == MinigameStatus.INITIAL) {
      this.minigame_chooser.scroll(amount);
    }
  }

  void keyPress(int key, int keyCode) {
    if (this.minigame != null) {
      this.minigame.keyPress(key, keyCode);
    }
  }

  void openEscForm() {
    if (this.minigame != null) {
      this.form = this.minigame.getEscForm();
    }
  }

  void keyRelease(int key, int keyCode) {
    if (this.minigame != null) {
      this.minigame.keyRelease(key, keyCode);
    }
  }
}