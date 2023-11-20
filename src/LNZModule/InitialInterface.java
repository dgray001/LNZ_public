package LNZModule;

import Button.*;
import DImg.DImg;
import Form.*;

class InitialInterface extends InterfaceLNZ {

  abstract class InitialInterfaceButton extends RectangleButton {
    protected LNZ p;

    InitialInterfaceButton(LNZ sketch, double yi, double yf) {
      super(sketch, LNZ.initialInterface_size - LNZ.initialInterface_buttonWidth -
        LNZ.initialInterface_buttonGap, yi, LNZ.initialInterface_size -
        LNZ.initialInterface_buttonGap, yf);
      this.p = sketch;
      this.setColors(DImg.ccolor(0, 100, 30, 200), DImg.ccolor(0, 129, 50, 150),
        DImg.ccolor(0, 129, 50, 190), DImg.ccolor(0, 129, 50, 230), DImg.ccolor(255));
      this.noStroke();
      this.show_message = true;
      this.text_size = 15;
    }

    public void hover() {
      p.global.sounds.trigger_interface("interfaces/buttonOn1");
      InitialInterface.this.logo.release();
    }
    public void dehover() {
      this.clicked = false;
    }
    public void click() {
      InitialInterface.this.logo.release();
    }
    public void release() {
      this.stayDehovered();
      InitialInterface.this.logo.release();
      InitialInterface.this.logo.release();
    }
  }

  class InitialInterfaceButton1 extends InitialInterfaceButton {
    InitialInterfaceButton1(LNZ sketch, double buttonHeight) {
      super(sketch, LNZ.initialInterface_buttonGap,
        LNZ.initialInterface_buttonGap + buttonHeight);
      this.message = "Launch";
    }

    @Override
    public void release() {
      super.release();
      p.global.sounds.trigger_interface("interfaces/buttonClick4");
      p.global.state = ProgramState.ENTERING_MAINMENU;
      p.background(p.global.color_background);
      p.surfaceSetSize(p.displayWidth, p.displayHeight);
      p.surfaceSetLocation(0, 0);
    }
  }

  class InitialInterfaceButton2 extends InitialInterfaceButton {
    InitialInterfaceButton2(LNZ sketch, double buttonHeight) {
      super(sketch, 2 * LNZ.initialInterface_buttonGap + buttonHeight,
        2 * LNZ.initialInterface_buttonGap + 2 * buttonHeight);
      this.message = "Uninstall";
    }

    @Override
    public void release() {
      super.release();
      p.global.sounds.trigger_interface("interfaces/buttonClick3");
      InitialInterface.this.form = new InitialInterfaceForm(p, "Uninstall Game", "Just delete it ya dip");
    }
  }

  class InitialInterfaceButton3 extends InitialInterfaceButton {
    InitialInterfaceButton3(LNZ sketch, double buttonHeight) {
      super(sketch, 3 * LNZ.initialInterface_buttonGap + 2 * buttonHeight,
        3 * LNZ.initialInterface_buttonGap + 3 * buttonHeight);
      this.message = "Reset\nGame";
    }

    @Override
    public void release() {
      super.release();
      p.global.sounds.trigger_interface("interfaces/buttonClick3");
      InitialInterface.this.form = new InitialInterfaceForm(p, "Reset Game", "Why would you want to reinstall a test version?");
    }
  }

  class InitialInterfaceButton4 extends InitialInterfaceButton {
    InitialInterfaceButton4(LNZ sketch, double buttonHeight) {
      super(sketch, 4 * LNZ.initialInterface_buttonGap + 3 * buttonHeight,
        4 * LNZ.initialInterface_buttonGap + 4 * buttonHeight);
      this.message = "Version\nHistory";
    }

    @Override
    public void release() {
      super.release();
      p.global.sounds.trigger_interface("interfaces/buttonClick3");
      InitialInterface.this.form = new InitialInterfaceForm(p, "Version History", LNZ.version_history, 180);
    }
  }

  class InitialInterfaceButton5 extends InitialInterfaceButton {
    InitialInterfaceButton5(LNZ sketch, double buttonHeight) {
      super(sketch, 5 * LNZ.initialInterface_buttonGap + 4 * buttonHeight,
        5 * LNZ.initialInterface_buttonGap + 5 * buttonHeight);
      this.message = "Exit";
    }

    @Override
    public void release() {
      super.release();
      p.global.sounds.trigger_interface("interfaces/buttonClick3");
      p.global.exitDelay();
    }
  }

  class LogoImageButton extends ImageButton {
    LogoImageButton(LNZ sketch) {
      super(sketch, sketch.global.images.getImage("logo.png"), 0, 0, 400, 400);
    }

    public void hover() {
    }
    public void dehover() {
    }
    public void click() {
      this.color_tint = DImg.ccolor(255, 200, 200);
    }
    public void release() {
      this.color_tint = DImg.ccolor(255);
    }
  }

  class InitialInterfaceForm extends FormLNZ {
    InitialInterfaceForm(LNZ sketch, String title, String message) {
      this(sketch, title, message, 120);
    }
    InitialInterfaceForm(LNZ sketch, String title, String message, float offset) {
      super(sketch, 0.5 * LNZ.initialInterface_size - offset, 0.5 * LNZ.initialInterface_size - offset,
        0.5 * LNZ.initialInterface_size + offset, 0.5 * LNZ.initialInterface_size + offset);
      this.setTitleText(title);
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);
      this.scrollbar.setButtonColors(DImg.ccolor(170), DImg.ccolor(190, 255, 190),
        DImg.ccolor(220, 255, 220), DImg.ccolor(160, 220, 160), DImg.ccolor(0));

      SubmitFormField submit = new SubmitFormField(sketch, "  Ok  ");
      submit.button.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      this.addField(new SpacerFormField(sketch, 0));
      TextBoxFormField textbox = new TextBoxFormField(sketch, message, 2 * offset - 120);
      textbox.textbox.scrollbar.setButtonColors(DImg.ccolor(170), DImg.ccolor(190, 255, 190),
        DImg.ccolor(220, 255, 220), DImg.ccolor(160, 220, 160), DImg.ccolor(0));
      this.addField(textbox);
      this.addField(submit);
    }
    public void submit() {
      this.canceled = true;
    }
  }

  private InitialInterfaceButton[] buttons = new InitialInterfaceButton[5];
  private LogoImageButton logo;

  InitialInterface(LNZ sketch) {
    super(sketch);
    this.logo = new LogoImageButton(sketch);
    float buttonHeight = (LNZ.initialInterface_size - (this.buttons.length + 1) *
      LNZ.initialInterface_buttonGap) / this.buttons.length;
    this.buttons[0] = new InitialInterfaceButton1(sketch, buttonHeight);
    this.buttons[1] = new InitialInterfaceButton2(sketch, buttonHeight);
    this.buttons[2] = new InitialInterfaceButton3(sketch, buttonHeight);
    this.buttons[3] = new InitialInterfaceButton4(sketch, buttonHeight);
    this.buttons[4] = new InitialInterfaceButton5(sketch, buttonHeight);
  }

  void saveAndExitToMainMenu() {}

  Hero getCurrentHeroIfExists() {
    return null;
  }

  void update(int millis) {
    p.background(DImg.ccolor(200));
    this.logo.update(millis);
    for (InitialInterfaceButton button : this.buttons) {
      button.update(millis);
    }
  }

  void showNerdStats() {
    this.showDefaultNerdStats();
  }

  void mouseMove(float mX, float mY) {
    this.logo.mouseMove(mX, mY);
    for (InitialInterfaceButton button : this.buttons) {
      button.mouseMove(mX, mY);
    }
  }

  void mousePress() {
    this.logo.mousePress();
    for (InitialInterfaceButton button : this.buttons) {
      button.mousePress();
    }
  }

  void mouseRelease(float mX, float mY) {
    this.logo.mouseRelease(mX, mY);
    for (InitialInterfaceButton button : this.buttons) {
      button.mouseRelease(mX, mY);
    }
  }

  void scroll(int amount) {}
  void keyPress(int key, int keyCode) {}
  void openEscForm() {}
  void keyRelease(int key, int keyCode) {}
  void loseFocus() {}
  void gainFocus() {}
  void restartTimers() {}
}