package LNZModule;

import java.util.*;
import processing.core.*;
import Button.*;
import DImg.DImg;
import Form.*;
import Misc.Misc;

abstract class InterfaceLNZ {

  abstract class ConfirmForm extends FormLNZ {
    ConfirmForm(LNZ sketch, String title, String message) {
      this(sketch, title, message, LNZ.mapEditor_formWidth_small, LNZ.mapEditor_formHeight_small);
    }
    ConfirmForm(LNZ sketch, String title, String message, boolean mediumForm) {
      this(sketch, title, message, LNZ.mapEditor_formWidth, LNZ.mapEditor_formHeight);
    }
    ConfirmForm(LNZ sketch, String title, String message, double formWidth, double formHeight) {
      super(sketch, 0.5 * (sketch.width - formWidth), 0.5 * (sketch.height - formHeight),
        0.5 * (sketch.width + formWidth), 0.5 * (sketch.height + formHeight));
      this.setTitleText(title);
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);

      SubmitCancelFormField submit = new SubmitCancelFormField(sketch, "  Ok  ", "Cancel");
      submit.button1.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      submit.button2.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      this.addField(new SpacerFormField(sketch, 0));
      this.addField(new TextBoxFormField(sketch, message, formHeight - 130));
      this.addField(submit);
    }
  }


  class EscForm extends FormLNZ {
    class EscButtonFormField extends ButtonFormField {
      EscButtonFormField(LNZ sketch, String message) {
        super(sketch, message);
        this.button.setColors(DImg.ccolor(170, 200), DImg.ccolor(1, 0),
          DImg.ccolor(40, 150), DImg.ccolor(20, 180), DImg.ccolor(255));
        this.button.raised_border = false;
        this.button.raised_body = false;
        this.button.noStroke();
        this.button.text_size = 20;
        this.extend_width = true;
      }
    }

    EscForm(LNZ sketch) {
      super(sketch, 0.5 * (sketch.width - LNZ.escFormWidth), 0.5 * (sketch.height - LNZ.escFormHeight),
        0.5 * (sketch.width + LNZ.escFormWidth), 0.5 * (sketch.height + LNZ.escFormHeight));
      this.cancel = null;
      this.scrollbar_width_multiplier = 0;
      this.draggable = false;
      this.color_shadow = DImg.ccolor(1, 0);
      this.setFieldCushion(20);
      this.setTitleText("Paused");
      this.setTitleSize(22);
      this.color_background = DImg.ccolor(60, 120);
      this.color_header = DImg.ccolor(1, 0);
      this.color_stroke = DImg.ccolor(1, 0);
      this.color_title = DImg.ccolor(255);

      this.addField(new SpacerFormField(sketch, 0));
      this.addField(new EscButtonFormField(sketch, "Continue"));
      this.addField(new EscButtonFormField(sketch, "Options"));
      this.addField(new EscButtonFormField(sketch, "Heroes"));
      this.addField(new EscButtonFormField(sketch, "Achievements"));
      this.addField(new EscButtonFormField(sketch, "Perk Tree"));
      this.addField(new EscButtonFormField(sketch, "Save and Exit to Main Menu"));
    }

    @Override
    public void update(int millis) {
      super.update(millis);
      p.stroke(255);
      p.strokeWeight(2);
      p.line(this.xi, this.yi, this.xf, this.yi);
      p.line(this.xi, this.yStart, this.xf, this.yStart);
      p.line(this.xi, this.yf, this.xf, this.yf);
      p.line(this.xi, this.yi, this.xi, this.yf);
      p.line(this.xf, this.yi, this.xf, this.yf);
    }

    public void submit() {}

    @Override
    public void buttonPress(int i) {
      switch(i) {
        case 1:
          this.cancel();
          break;
        case 2:
          InterfaceLNZ.this.return_to_esc_menu = true;
          InterfaceLNZ.this.esc_menu_img = this.img;
          InterfaceLNZ.this.optionsForm();
          break;
        case 3:
          InterfaceLNZ.this.return_to_esc_menu = true;
          InterfaceLNZ.this.esc_menu_img = this.img;
          InterfaceLNZ.this.heroesForm();
          break;
        case 4:
          InterfaceLNZ.this.return_to_esc_menu = true;
          InterfaceLNZ.this.esc_menu_img = this.img;
          InterfaceLNZ.this.achievementsForm();
          break;
        case 5:
          InterfaceLNZ.this.openPlayerTree();
          break;
        case 6:
          InterfaceLNZ.this.saveAndExitToMainMenu();
          break;
        default:
          break;
      }
    }
  }


  class HeroesForm extends FormLNZ {
    class HeroesFormField extends FormField {
      class HeroButton extends IconInverseButton {
        protected LNZ p;
        protected HeroCode code;
        protected Hero hero = null;
        HeroButton(LNZ sketch, float xi, float yi, float xf, float yf, HeroCode code) {
          super(sketch, xi, yi, xf, yf, sketch.global.images.getImage(code.getImagePath(sketch.global.profile.ben_has_eyes)));
          this.p = sketch;
          this.roundness = 4;
          this.adjust_for_text_descent = true;
          this.code = code;
          Element e = code.element();
          this.background_color = DImg.ccolor(170, 170);
          this.setColors(Element.colorLocked(sketch.global, e), Element.colorDark(sketch.global, e),
            Element.color(sketch.global, e), Element.colorLight(sketch.global, e), Element.colorText(sketch.global, e));
          this.show_message = true;
          this.message = code.displayName();
          this.text_size = 24;
          this.ripple_timer = 450;
          this.setStroke(DImg.ccolor(0), 1);
          if (sketch.global.profile.heroes.containsKey(this.code)) {
            this.hero = sketch.global.profile.heroes.get(this.code);
            if (sketch.global.profile.curr_hero == this.code) {
              this.setStroke(DImg.ccolor(255), 6);
            }
          }
        }
        public void hover() {
          super.hover();
          this.message = this.code.displayName() + "\n" + this.code.title() +
            "\nLocation: " + this.hero.location.displayName();
          this.text_size = 16;
        }
        public void dehover() {
          super.dehover();
          this.message = this.code.displayName();
          this.text_size = 24;
        }
        public void release() {
          super.release();
          if (this.hovered || this.button_focused) {
            HeroesFormField.this.last_code_clicked = this.code;
            HeroesFormField.this.clicked = true;
          }
        }
      }

      protected LNZ p;

      protected HeroButton[] heroes = new HeroButton[2];
      protected HeroCode last_code_clicked = HeroCode.ERROR;
      protected boolean clicked = false;

      HeroesFormField(LNZ sketch, int index) {
        super(sketch, "");
        this.p = sketch;
        switch(index) {
          case 0:
            this.heroes[0] = new HeroButton(sketch, 0, 0, 0, 100, HeroCode.BEN);
            this.heroes[1] = new HeroButton(sketch, 0, 0, 0, 100, HeroCode.DAN);
            break;
          case 1:
            this.heroes[0] = new HeroButton(sketch, 0, 0, 0, 100, HeroCode.JF);
            this.heroes[1] = new HeroButton(sketch, 0, 0, 0, 100, HeroCode.SPINNY);
            break;
          case 2:
            this.heroes[0] = new HeroButton(sketch, 0, 0, 0, 100, HeroCode.MATTUS);
            this.heroes[1] = new HeroButton(sketch, 0, 0, 0, 100, HeroCode.PATRICK);
            break;
        }
        this.enable();
      }

      public void disable() {
        for (HeroButton button : this.heroes) {
          if (button == null) {
            continue;
          }
          button.disabled = true;
        }
      }
      public void enable() {
        for (HeroButton button : this.heroes) {
          if (button == null) {
            continue;
          }
          button.disabled = !p.global.profile.heroes.containsKey(button.code);
        }
      }

      public boolean focusable() {
        for (int i = 0; i < this.heroes.length; i++) {
          if (this.heroes[this.heroes.length - 1 - i] == null) {
            continue;
          }
          if (this.heroes[this.heroes.length - 1 - i].button_focused) {
            return false;
          }
          return true;
        }
        return false;
      }
      public void focus() {
        boolean refocused = false;
        for (int i = 0; i < this.heroes.length; i++) {
          if (this.heroes[i] == null) {
            continue;
          }
          if (this.heroes[i].button_focused) {
            this.heroes[i].button_focused = false;
            if (i == this.heroes.length - 1) {
              this.heroes[0].button_focused = true;
            }
            else {
              this.heroes[i + 1].button_focused = true;
            }
            refocused = true;
            break;
          }
        }
        if (!refocused) {
          this.heroes[0].button_focused = true;
        }
      }
      public void defocus() {
        for (HeroButton button : this.heroes) {
          if (button == null) {
            continue;
          }
          button.button_focused = false;
        }
      }
      public boolean focused() {
        for (HeroButton button : this.heroes) {
          if (button == null) {
            continue;
          }
          if (button.button_focused) {
            return true;
          }
        }
        return false;
      }

      public void updateWidthDependencies() {
        double button_width = (this.field_width - 4) / this.heroes.length - (this.heroes.length - 1) * 10;
        for (int i = 0; i < this.heroes.length; i++) {
          if (this.heroes[i] == null) {
            continue;
          }
          this.heroes[i].setXLocation(2 + i * (button_width + 10), 2 + i * (button_width + 10) + button_width);
        }
      }

      public double getHeight() {
        for (HeroButton button : this.heroes) {
          if (button == null) {
            continue;
          }
          return button.buttonHeight();
        }
        return 0;
      }

      public String getValue() {
        return this.last_code_clicked.file_name();
      }
      public void setValue(String value) {
        this.message = value;
      }

      public FormFieldSubmit updateField(int millis) {
        for (HeroButton button : this.heroes) {
          if (button == null) {
            continue;
          }
          button.update(millis);
        }
        if (this.clicked) {
          this.clicked = false;
          return FormFieldSubmit.BUTTON;
        }
        return FormFieldSubmit.NONE;
      }

      public void mouseMoveField(float mX, float mY) {
        for (HeroButton button : this.heroes) {
          if (button == null) {
            continue;
          }
          button.mouseMove(mX, mY);
        }
      }

      public void mousePressField() {
        for (HeroButton button : this.heroes) {
          if (button == null) {
            continue;
          }
          button.mousePress();
        }
      }

      public void mouseReleaseField(float mX, float mY) {
        for (HeroButton button : this.heroes) {
          if (button == null) {
            continue;
          }
          button.mouseRelease(mX, mY);
        }
      }

      public void scrollField(int amount) {}

      public void keyPressField(int key, int keyCode) {
        for (HeroButton button : this.heroes) {
          if (button == null) {
            continue;
          }
          button.keyPress(key, keyCode);
        }
      }

      public void keyReleaseField(int key, int keyCode) {
        for (HeroButton button : this.heroes) {
          if (button == null) {
            continue;
          }
          button.keyRelease(key, keyCode);
        }
      }
      public void submit() {}
    }

    HeroesForm(LNZ sketch) {
      super(sketch, 0.5 * (sketch.width - LNZ.profile_heroesFormWidth),
        0.5 * (sketch.height - LNZ.profile_heroesFormHeight),
        0.5 * (sketch.width + LNZ.profile_heroesFormWidth),
        0.5 * (sketch.height + LNZ.profile_heroesFormHeight));
      this.setTitleText("Heroes");
      this.setTitleSize(22);
      this.color_shadow = DImg.ccolor(0, 180);
      this.color_background = DImg.ccolor(60);
      this.color_header = DImg.ccolor(90);
      this.color_stroke = DImg.ccolor(0);
      this.color_title = DImg.ccolor(255);
      this.setFieldCushion(15);

      this.addField(new SpacerFormField(sketch, 0));
      this.addField(new HeroesFormField(sketch, 0));
      this.addField(new HeroesFormField(sketch, 1));
      this.addField(new HeroesFormField(sketch, 2));
    }

    public void submit() {}

    public void buttonPress(int i) {
      HeroCode code_clicked = HeroCode.ERROR;
      try {
        code_clicked = ((HeroesFormField)this.fields.get(i)).last_code_clicked;
      } catch(Exception e) {}
      if (code_clicked != HeroCode.ERROR) {
        InterfaceLNZ.this.openHeroForm(code_clicked);
      }
    }

    public void keyPress(int key, int keyCode) {
      super.keyPress(key, keyCode);
      if (key == 'h' && p.global.holding_ctrl) {
        this.cancel();
      }
    }
  }


  class HeroForm extends FormLNZ {
    protected Hero hero;
    protected boolean switch_hero = true;
    HeroForm(LNZ sketch, Hero hero) {
      super(sketch, 0.5 * (sketch.width - LNZ.profile_heroFormWidth),
        0.5 * (sketch.height - LNZ.profile_heroFormHeight),
        0.5 * (sketch.width + LNZ.profile_heroFormWidth),
        0.5 * (sketch.height + LNZ.profile_heroFormHeight));
      this.hero = hero;
      this.setTitleText(hero.code.displayName());
      this.setTitleSize(22);
      Element e = hero.element;
      this.color_background = Element.colorLight(sketch.global, e);
      this.color_header = Element.color(sketch.global, e);
      this.color_stroke = Element.colorDark(sketch.global, e);
      this.color_title = Element.colorText(sketch.global, e);
      this.setFieldCushion(15);
      this.scrollbar.setButtonColors(Element.colorLocked(sketch.global, e),
        Element.color(sketch.global, e), Element.colorLight(sketch.global, e),
        Element.colorDark(sketch.global, e), Element.colorText(sketch.global, e));

      MessageFormField message1 = new MessageFormField(p, "Location: " + hero.location.displayName());
      message1.text_color = Element.colorText(sketch.global, e);
      MessageFormField message2 = new MessageFormField(p, hero.code.title());
      message2.text_color = Element.colorText(sketch.global, e);
      TextBoxFormField textbox = new TextBoxFormField(p, hero.code.description(), 150);
      textbox.textbox.color_text = Element.colorText(sketch.global, e);
      textbox.textbox.scrollbar.setButtonColors(Element.colorLocked(sketch.global, e),
        Element.color(sketch.global, e), Element.colorLight(sketch.global, e),
        Element.colorDark(sketch.global, e), Element.colorText(sketch.global, e));
      textbox.textbox.scrollbar.button_upspace.color_default = Element.colorLight(sketch.global, e);
      textbox.textbox.scrollbar.button_downspace.color_default = Element.colorLight(sketch.global, e);

      this.addField(new SpacerFormField(p, 80));
      this.addField(message1);
      if (PlayingInterface.class.isInstance(InterfaceLNZ.this)) {
        SubmitFormField submit = null;
        if (sketch.global.profile.curr_hero == hero.code) {
          submit = new SubmitFormField(sketch, "Continue Playing");
          this.switch_hero = false;
        }
        else {
          submit = new SubmitFormField(sketch, "Play Hero");
        }
        submit.button.text_size = 18;
        submit.button.setColors(Element.colorLocked(sketch.global, e),
          Element.color(sketch.global, hero.element), Element.colorLight(sketch.global, e),
          Element.colorDark(sketch.global, hero.element), Element.colorText(sketch.global, e));
        this.addField(submit);
      }
      this.addField(new SpacerFormField(sketch, 10));
      this.addField(message2);
      this.addField(textbox);
      this.addField(new SpacerFormField(sketch, 10));
    }

    @Override
    public void update(int millis) {
      super.update(millis);
      p.imageMode(PConstants.CENTER);
      p.image(p.global.images.getImage(this.hero.code.getImagePath(p.global.profile.ben_has_eyes)), this.xCenter(), this.yStart + 40, 75, 75);
    }

    public void submit() {
      if (this.switch_hero && PlayingInterface.class.isInstance(InterfaceLNZ.this)) {
        ((PlayingInterface)InterfaceLNZ.this).switchHero(this.hero.code, true);
      }
      this.canceled = true;
      InterfaceLNZ.this.return_to_heroes_menu = false;
    }

    public void buttonPress(int i) {}
  }


  class ErrorForm extends FormLNZ {
    ErrorForm(LNZ sketch, String errorMessage) {
      super(sketch, 0.5 * (sketch.width - LNZ.errorForm_width),
        0.5 * (sketch.height - LNZ.errorForm_height),
        0.5 * (sketch.width + LNZ.errorForm_width),
        0.5 * (sketch.height + LNZ.errorForm_height));
      this.setTitleText("Error Detected");
      this.setTitleSize(20);
      this.setFieldCushion(0);
      this.color_background = DImg.ccolor(250, 150, 150);
      this.color_header = DImg.ccolor(180, 50, 50);
      this.scrollbar.setButtonColors(DImg.ccolor(220), DImg.ccolor(240, 130, 130), DImg.ccolor(
        255, 155, 155), DImg.ccolor(200, 100, 100), DImg.ccolor(0));
      this.scrollbar.button_upspace.setColors(DImg.ccolor(170), DImg.ccolor(1, 0),
        DImg.ccolor(255, 80, 80, 75), DImg.ccolor(255, 80, 80, 150), DImg.ccolor(0));
      this.scrollbar.button_downspace.setColors(DImg.ccolor(170), DImg.ccolor(1, 0),
        DImg.ccolor(255, 80, 80, 75), DImg.ccolor(255, 80, 80, 150), DImg.ccolor(0));

      TextBoxFormField textbox = new TextBoxFormField(sketch, "Error message:\n" + errorMessage, 100);
      textbox.textbox.scrollbar.setButtonColors(DImg.ccolor(220), DImg.ccolor(240, 130, 130), DImg.ccolor(
        255, 155, 155), DImg.ccolor(200, 100, 100), DImg.ccolor(0));
      textbox.textbox.scrollbar.button_upspace.setColors(DImg.ccolor(170), DImg.ccolor(1, 0),
        DImg.ccolor(255, 80, 80, 75), DImg.ccolor(255, 80, 80, 150), DImg.ccolor(0));
      textbox.textbox.scrollbar.button_downspace.setColors(DImg.ccolor(170), DImg.ccolor(1, 0),
        DImg.ccolor(255, 80, 80, 75), DImg.ccolor(255, 80, 80, 150), DImg.ccolor(0));
      SubmitCancelFormField buttons = new SubmitCancelFormField(sketch, "Continue\n(may crash)", "Exit");
      p.textSize(buttons.button1.text_size);
      buttons.button1.setColors(DImg.ccolor(180), DImg.ccolor(240, 160, 160),
        DImg.ccolor(190, 110, 110), DImg.ccolor(140, 70, 70), DImg.ccolor(0));
      buttons.button2.setColors(DImg.ccolor(180), DImg.ccolor(240, 160, 160),
        DImg.ccolor(190, 110, 110), DImg.ccolor(140, 70, 70), DImg.ccolor(0));
      buttons.setButtonHeight(2 * (p.textAscent() + p.textDescent() + 2));

      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new MessageFormField(sketch, "Error detected on this frame."));
      this.addField(new SpacerFormField(sketch, 10));
      this.addField(textbox);
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new MessageFormField(sketch, "Check the data/error folder for logs and image."));
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new CheckboxFormField(sketch, "Send error report  "));
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(buttons);
      this.img.save("data/logs/screenshot.jpg");
    }

    public void submit() {
      if (this.fields.get(7).getValue().equals(Boolean.toString(true))) {
        this.sendEmail();
      }
      this.canceled = true;
    }

    @Override
    public void cancel() {
      if (this.fields.get(7).getValue().equals(Boolean.toString(true))) {
        this.sendEmail();
      }
      p.global.exitDelay();
    }

    void sendEmail() {
      p.global.log("Send email (not configured).");
    }

    void addErrorMessage(String message) {
      this.fields.get(3).setValue(this.fields.get(3).getValue() + "\n\nError Message:\n" + message);
    }
  }


  class OptionsForm extends TabbedFormLNZ {
    abstract class OptionsTab extends Form {
      OptionsTab(LNZ sketch) {
        super(sketch);
        this.setFieldCushion(5);
      }
      public void submit() {}
      public void cancel() {}
      public void buttonPress(int i) {}
    }

    class VolumeTab extends OptionsTab {
      VolumeTab(LNZ sketch) {
        super(sketch);
        SliderFormField volume_master = new SliderFormField(sketch, "Master Volume: ",
          LNZ.options_volumeMin, LNZ.options_volumeMax);
        volume_master.threshhold = LNZ.optionsForm_threshhold_master;
        volume_master.addCheckbox("mute: ");
        volume_master.addLabel("%", true);
        SliderFormField volume_music = new SliderFormField(sketch, "Music: ",
          LNZ.options_volumeMin, LNZ.options_volumeMax);
        volume_music.threshhold = LNZ.optionsForm_threshhold_other;
        volume_music.addCheckbox("mute: ");
        volume_music.addLabel("%", true);
        SliderFormField volume_interface = new SliderFormField(sketch, "Interface: ",
          LNZ.options_volumeMin, LNZ.options_volumeMax);
        volume_interface.threshhold = LNZ.optionsForm_threshhold_other;
        volume_interface.addCheckbox("mute: ");
        volume_interface.addLabel("%", true);
        SliderFormField volume_environment = new SliderFormField(sketch, "Environment: ",
          LNZ.options_volumeMin, LNZ.options_volumeMax);
        volume_environment.threshhold = LNZ.optionsForm_threshhold_other;
        volume_environment.addCheckbox("mute: ");
        volume_environment.addLabel("%", true);
        SliderFormField volume_units = new SliderFormField(sketch, "Units: ",
          LNZ.options_volumeMin, LNZ.options_volumeMax);
        volume_units.threshhold = LNZ.optionsForm_threshhold_other;
        volume_units.addCheckbox("mute: ");
        volume_units.addLabel("%", true);
        SliderFormField volume_player = new SliderFormField(sketch, "Player: ",
          LNZ.options_volumeMin, LNZ.options_volumeMax);
        volume_player.threshhold = LNZ.optionsForm_threshhold_other;
        volume_player.addCheckbox("mute: ");
        volume_player.addLabel("%", true);

        this.addField(new SpacerFormField(sketch, 15));
        this.addField(volume_master);
        this.addField(volume_music);
        this.addField(volume_interface);
        this.addField(volume_environment);
        this.addField(volume_units);
        this.addField(volume_player);
      }
    }

    class DisplayTab extends OptionsTab {
      DisplayTab(LNZ sketch) {
        super(sketch);
        SliderFormField map_move_speed = new SliderFormField(sketch, "Camera Speed: ",
          LNZ.map_minCameraSpeed, LNZ.map_maxCameraSpeed);
        map_move_speed.threshhold = LNZ.optionsForm_threshhold_other;
        map_move_speed.slider.button.round_value = 1000;
        map_move_speed.slider.button.divide_round_value = false;
        SliderFormField inventory_bar_size = new SliderFormField(sketch, "Inventory Bar Size: ", 80, 180);
        inventory_bar_size.threshhold = LNZ.optionsForm_threshhold_other;
        inventory_bar_size.addCheckbox("hide: ");
        SliderFormField map_resolution = new SliderFormField(sketch, "Terrain Resolution: ", 10, 110, 20);
        map_resolution.threshhold = LNZ.optionsForm_threshhold_other;
        map_resolution.addLabel(" pixels", true, false);
        SliderFormField fog_update_time = new SliderFormField(sketch, "Fog Update Time: ",
          LNZ.map_timer_refresh_fog_min, LNZ.map_timer_refresh_fog_max, 50);
        fog_update_time.threshhold = LNZ.optionsForm_threshhold_other;
        fog_update_time.addLabel(" ms", true, true);

        this.addField(new SpacerFormField(sketch, 15));
        this.addField(map_move_speed);
        this.addField(inventory_bar_size);
        this.addField(map_resolution);
        this.addField(fog_update_time);
        this.addField(new CheckboxFormField(sketch, "Display Feature Interaction Tooltips:  "));
        if (sketch.global.profile.upgraded(PlayerTreeCode.HEALTHBARS)) {
          CheckboxFormField healthbars = new CheckboxFormField(sketch, "Show Healthbars:  ");
          this.addField(healthbars);
        }
        else {
          this.addField(new SpacerFormField(sketch, -this.fieldCushion));
        }
      }
    }

    class PlayerTab extends OptionsTab {
      PlayerTab(LNZ sketch) {
        super(sketch);
        CheckboxFormField lock_screen = new CheckboxFormField(sketch, "Lock Screen:  ");
        CheckboxFormField pathfinding = new CheckboxFormField(sketch, "Use Pathfinding:  ");

        this.addField(new SpacerFormField(sketch, 15));
        this.addField(lock_screen);
        this.addField(pathfinding);
        if (sketch.global.profile.upgraded(PlayerTreeCode.MAGNETIC_HANDS)) {
          CheckboxFormField magnetic_hands = new CheckboxFormField(sketch, "Toggle Magnetic Hands:  ");
          this.addField(magnetic_hands);
        }
        else {
          this.addField(new SpacerFormField(sketch, -this.fieldCushion));
        }
      }
    }

    class TipsAndTricksButton extends RectangleButton {
      TipsAndTricksButton(LNZ sketch, int spacing) {
        super(sketch, OptionsForm.this.xi + spacing, OptionsForm.this.yf - OptionsForm.this.footer_space + spacing,
          OptionsForm.this.xi + spacing + 180, OptionsForm.this.yf - spacing);
        this.show_message = true;
        this.message = "Tips and Tricks";
        this.text_size = 18;
        this.setColors(DImg.ccolor(170), DImg.ccolor(1, 0), DImg.ccolor(250, 250, 180), DImg.ccolor(230, 230, 150), DImg.ccolor(0));
      }
      public void hover() {
        this.show_stroke = false;
      }
      public void dehover() {
        this.show_stroke = true;
      }
      public void click() {}
      public void release() {
        if (!this.hovered) {
          return;
        }
        OptionsForm.this.p.global.menu.form = new TipsAndTricksForm(OptionsForm.this.p);
      }
    }

    private TipsAndTricksButton tips_tricks_button;

    OptionsForm(LNZ sketch) {
      super(sketch, LNZ.optionsForm_widthOffset, LNZ.optionsForm_heightOffset,
        sketch.width - LNZ.optionsForm_widthOffset, sketch.height - LNZ.optionsForm_heightOffset);
      this.setTitleText("Options");
      this.setTitleSize(20);
      this.setFieldCushion(5);
      this.color_background = DImg.ccolor(250, 250, 180);
      this.color_header = DImg.ccolor(180, 180, 50);
      this.draggable = false;
      if (sketch.global.profile == null) {
        this.canceled = true;
        return;
      }

      this.footer_space = 150;
      this.tab_button_height = 55;
      this.tab_button_max_width = 140;
      this.tab_button_alignment = PConstants.LEFT;
      this.tips_tricks_button = new TipsAndTricksButton(sketch, 50);
      this.addTab(new VolumeTab(sketch), "Audio");
      this.addTab(new DisplayTab(sketch), "Display");
      this.addTab(new PlayerTab(sketch), "Player");

      TabConfig tab_config = new TabConfig();
      tab_config.tab_text_size = 18;
      tab_config.color_background = DImg.ccolor(240, 240, 200);
      tab_config.color_stroke = DImg.ccolor(240, 240, 200);
      tab_config.scrollbar_width_multiplier = 0.015;
      tab_config.scrollbar_min_width = 15;
      tab_config.scrollbar_max_width = 20;
      tab_config.scrollbar_color_default = DImg.ccolor(200, 200, 100);
      tab_config.scrollbar_color_hovered = DImg.ccolor(220, 220, 150);
      tab_config.scrollbar_color_clicked = DImg.ccolor(180, 180, 50);
      tab_config.scrollbar_color_space = DImg.ccolor(220, 220, 150);
      this.setTabConfig(tab_config);

      ButtonsFormField buttons = new ButtonsFormField(sketch, "Apply", "Defaults");
      buttons.button1.setColors(DImg.ccolor(220), DImg.ccolor(240, 240, 190),
        DImg.ccolor(190, 190, 140), DImg.ccolor(140, 140, 90), DImg.ccolor(0));
      buttons.button2.setColors(DImg.ccolor(220), DImg.ccolor(240, 240, 190),
        DImg.ccolor(190, 190, 140), DImg.ccolor(140, 140, 90), DImg.ccolor(0));
      SubmitCancelFormField submit = new SubmitCancelFormField(sketch, "Save", "Cancel");
      submit.button1.setColors(DImg.ccolor(220), DImg.ccolor(240, 240, 190),
        DImg.ccolor(190, 190, 140), DImg.ccolor(140, 140, 90), DImg.ccolor(0));
      submit.button2.setColors(DImg.ccolor(220), DImg.ccolor(240, 240, 190),
        DImg.ccolor(190, 190, 140), DImg.ccolor(140, 140, 90), DImg.ccolor(0));

      this.addField(new SpacerFormField(sketch, 10));
      this.addField(buttons);
      this.addField(new SpacerFormField(sketch, 10));
      this.addField(submit);

      this.setFormFieldValues();
    }

    @Override
    public void update(int millis) {
      super.update(millis);
      this.tips_tricks_button.update(millis);
    }

    @Override
    public void mouseMove(float mX, float mY) {
      super.mouseMove(mX, mY);
      this.tips_tricks_button.mouseMove(mX, mY);
    }

    @Override
    public void mousePress() {
      super.mousePress();
      this.tips_tricks_button.mousePress();
    }

    @Override
    public void mouseRelease(float mX, float mY) {
      super.mouseRelease(mX, mY);
      this.tips_tricks_button.mouseRelease(mX, mY);
    }

    void setFormFieldValues() {
      this.tabs.get(0).form.fields.get(1).setValue(p.global.profile.options.volume_master);
      if (p.global.profile.options.volume_master_muted) {
        this.tabs.get(0).form.fields.get(1).disable();
      }
      else {
        this.tabs.get(0).form.fields.get(1).enable();
      }

      this.tabs.get(0).form.fields.get(2).setValue(p.global.profile.options.volume_music);
      if (p.global.profile.options.volume_music_muted) {
        this.tabs.get(0).form.fields.get(2).disable();
      }
      else {
        this.tabs.get(0).form.fields.get(2).enable();
      }

      this.tabs.get(0).form.fields.get(3).setValue(p.global.profile.options.volume_interface);
      if (p.global.profile.options.volume_interface_muted) {
        this.tabs.get(0).form.fields.get(3).disable();
      }
      else {
        this.tabs.get(0).form.fields.get(3).enable();
      }

      this.tabs.get(0).form.fields.get(4).setValue(p.global.profile.options.volume_environment);
      if (p.global.profile.options.volume_environment_muted) {
        this.tabs.get(0).form.fields.get(4).disable();
      }
      else {
        this.tabs.get(0).form.fields.get(4).enable();
      }

      this.tabs.get(0).form.fields.get(5).setValue(p.global.profile.options.volume_units);
      if (p.global.profile.options.volume_units_muted) {
        this.tabs.get(0).form.fields.get(5).disable();
      }
      else {
        this.tabs.get(0).form.fields.get(5).enable();
      }

      this.tabs.get(0).form.fields.get(6).setValue(p.global.profile.options.volume_player);
      if (p.global.profile.options.volume_player_muted) {
        this.tabs.get(0).form.fields.get(6).disable();
      }
      else {
        this.tabs.get(0).form.fields.get(6).enable();
      }

      this.tabs.get(1).form.fields.get(1).setValue(p.global.profile.options.map_viewMoveSpeedFactor);

      this.tabs.get(1).form.fields.get(2).setValue(p.global.profile.options.inventory_bar_size);
      if (p.global.profile.options.inventory_bar_hidden) {
        this.tabs.get(1).form.fields.get(2).disable();
      }
      else {
        this.tabs.get(1).form.fields.get(2).enable();
      }

      this.tabs.get(1).form.fields.get(3).setValue(p.global.profile.options.terrain_resolution);

      this.tabs.get(1).form.fields.get(4).setValue(p.global.profile.options.fog_update_time);

      this.tabs.get(1).form.fields.get(5).setValue(p.global.profile.options.show_feature_interaction_tooltip);

      if (p.global.profile.upgraded(PlayerTreeCode.HEALTHBARS)) {
        this.tabs.get(1).form.fields.get(6).setValue(p.global.profile.options.show_healthbars);
      }

      this.tabs.get(2).form.fields.get(1).setValue(p.global.profile.options.lock_screen);

      this.tabs.get(2).form.fields.get(2).setValue(p.global.profile.options.player_pathfinding);

      if (p.global.profile.upgraded(PlayerTreeCode.MAGNETIC_HANDS)) {
        this.tabs.get(2).form.fields.get(3).setValue(p.global.profile.options.magnetic_hands);
      }
    }

    public void submit() {
      this.apply();
      p.global.profile.save();
      this.canceled = true;
    }

    void apply() {
      String vol_master = this.tabs.get(0).form.fields.get(1).getValue();
      if (vol_master.contains("disabled")) {
        p.global.profile.options.volume_master_muted = true;
      }
      else {
        p.global.profile.options.volume_master_muted = false;
      }
      p.global.profile.options.volume_master = Misc.toDouble(PApplet.split(vol_master, ':')[0]);

      String vol_music = this.tabs.get(0).form.fields.get(2).getValue();
      if (vol_music.contains("disabled")) {
        p.global.profile.options.volume_music_muted = true;
      }
      else {
        p.global.profile.options.volume_music_muted = false;
      }
      p.global.profile.options.volume_music = Misc.toDouble(PApplet.split(vol_music, ':')[0]);

      String vol_interface = this.tabs.get(0).form.fields.get(3).getValue();
      if (vol_interface.contains("disabled")) {
        p.global.profile.options.volume_interface_muted = true;
      }
      else {
        p.global.profile.options.volume_interface_muted = false;
      }
      p.global.profile.options.volume_interface = Misc.toDouble(PApplet.split(vol_interface, ':')[0]);

      String vol_environment = this.tabs.get(0).form.fields.get(4).getValue();
      if (vol_environment.contains("disabled")) {
        p.global.profile.options.volume_environment_muted = true;
      }
      else {
        p.global.profile.options.volume_environment_muted = false;
      }
      p.global.profile.options.volume_environment = Misc.toDouble(PApplet.split(vol_environment, ':')[0]);

      String vol_units = this.tabs.get(0).form.fields.get(5).getValue();
      if (vol_units.contains("disabled")) {
        p.global.profile.options.volume_units_muted = true;
      }
      else {
        p.global.profile.options.volume_units_muted = false;
      }
      p.global.profile.options.volume_units = Misc.toDouble(PApplet.split(vol_units, ':')[0]);

      String vol_player = this.tabs.get(0).form.fields.get(6).getValue();
      if (vol_player.contains("disabled")) {
        p.global.profile.options.volume_player_muted = true;
      }
      else {
        p.global.profile.options.volume_player_muted = false;
      }
      p.global.profile.options.volume_player = Misc.toDouble(PApplet.split(vol_player, ':')[0]);

      String camera_speed = this.tabs.get(1).form.fields.get(1).getValue();
      p.global.profile.options.map_viewMoveSpeedFactor = Misc.toDouble(PApplet.split(camera_speed, ':')[0]);

      String hud_size = this.tabs.get(1).form.fields.get(2).getValue();
      if (hud_size.contains("disabled")) {
        p.global.profile.options.inventory_bar_hidden = true;
      }
      else {
        p.global.profile.options.inventory_bar_hidden = false;
      }
      p.global.profile.options.inventory_bar_size = Misc.toDouble(PApplet.split(hud_size, ':')[0]);

      String terrain_resolution = this.tabs.get(1).form.fields.get(3).getValue();
      p.global.profile.options.terrain_resolution = (int)Math.round(Misc.toDouble(PApplet.split(terrain_resolution, ':')[0]));

      String fog_update_time = this.tabs.get(1).form.fields.get(4).getValue();
      p.global.profile.options.fog_update_time = Misc.toDouble(PApplet.split(fog_update_time, ':')[0]);
    
      String show_feature_tooltips = this.tabs.get(1).form.fields.get(5).getValue();
      p.global.profile.options.show_feature_interaction_tooltip = Misc.toBoolean(show_feature_tooltips);

      if (p.global.profile.upgraded(PlayerTreeCode.HEALTHBARS)) {
        String show_healthbars = this.tabs.get(1).form.fields.get(6).getValue();
        p.global.profile.options.show_healthbars = Misc.toBoolean(show_healthbars);
      }

      String lock_screen = this.tabs.get(2).form.fields.get(1).getValue();
      p.global.profile.options.lock_screen = Misc.toBoolean(lock_screen);

      String player_pathfinding = this.tabs.get(2).form.fields.get(2).getValue();
      p.global.profile.options.player_pathfinding = Misc.toBoolean(player_pathfinding);

      if (p.global.profile.upgraded(PlayerTreeCode.MAGNETIC_HANDS)) {
        String magnetic_hands = this.tabs.get(2).form.fields.get(3).getValue();
        p.global.profile.options.magnetic_hands = Misc.toBoolean(magnetic_hands);
      }

      p.global.profile.options.change();
    }

    public void buttonPress(int index) {
      switch(index) {
        case 1: // buttons
          if (this.fields.get(1).getValue().equals("0")) {
            this.apply();
          }
          else {
            p.global.profile.options.defaults();
            this.setFormFieldValues();
          }
          break;
        default:
          break;
      }
    }

    public void keyPress(int key, int keyCode) {
      super.keyPress(key, keyCode);
      if ((key == 'o' || key == 'O') && p.global.holding_ctrl) {
        this.cancel();
      }
    }
  }


  class TipsAndTricksForm extends FormLNZ {
    TipsAndTricksForm(LNZ sketch) {
      super(sketch, 0.5 * (sketch.width - LNZ.tipsForm_width), 0.5 * (sketch.height - LNZ.tipsForm_height),
        0.5 * (sketch.width + LNZ.tipsForm_width), 0.5 * (sketch.height + LNZ.tipsForm_height));
      this.setTitleText("Tips and Tricks");
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(250, 180, 250);
      this.color_header = DImg.ccolor(170, 30, 170);

      SubmitFormField submit = new SubmitFormField(sketch, "  Ok  ");
      submit.button.setColors(DImg.ccolor(220), DImg.ccolor(240, 190, 240),
        DImg.ccolor(190, 140, 190), DImg.ccolor(140, 90, 140), DImg.ccolor(0));
      this.addField(new SpacerFormField(sketch, 0));
      TextBoxFormField textbox = new TextBoxFormField(sketch, LNZ.tips_and_tricks, 480);
      textbox.textbox.scrollbar.setButtonColors(DImg.ccolor(220), DImg.ccolor(240, 130, 240),
        DImg.ccolor(255, 155, 255), DImg.ccolor(200, 100, 200), DImg.ccolor(0));
      textbox.textbox.scrollbarWidths(15, 10);
      this.addField(textbox);
      this.addField(submit);
    }
    public void submit() {
      this.canceled = true;
    }
  }


  class AchievementsForm extends TabbedFormLNZ {
    class OpenPerkTreeButton extends RippleRectangleButton {
      OpenPerkTreeButton(LNZ sketch, float xi, float yi, float xf, float yf) {
        super(sketch, xi, yi, xf, yf);
        this.roundness = 4;
        this.show_message = true;
        this.noStroke();
        this.message = "Open Perk Tree";
        this.setColors(DImg.ccolor(170, 170), DImg.ccolor(1, 0), DImg.ccolor(1, 0), DImg.ccolor(150, 150), DImg.ccolor(0));
        this.ripple_time = 700;
        this.text_size = 22;
      }
      @Override
      public void drawButton() {
        super.drawButton();
        if (this.hovered) {
          p.noFill();
          p.stroke(150, 40, 40);
          p.strokeWeight(1);
          p.rectMode(PConstants.CORNERS);
          p.rect(this.xi, this.yi, this.xf, this.yf, this.roundness);
        }
      }
      @Override
      public void hover() {
        super.hover();
        this.color_text = DImg.ccolor(150, 40, 40);
      }
      @Override
      public void dehover() {
        super.dehover();
        this.color_text = DImg.ccolor(0);
      }
      @Override
      public void release() {
        super.release();
        if (this.hovered || this.button_focused) {
          AchievementsForm.this.submit();
        }
      }
    }

    abstract class AchievementTab extends Form {
      AchievementTab(LNZ sketch) {
        super(sketch);
      }

      public void submit() {}
      public void cancel() {}
      public void buttonPress(int i) {}
    }

    class CompletionTab extends AchievementTab {
      CompletionTab(LNZ sketch) {
        super(sketch);
        ArrayList<MessageFormField> achievements_complete = new ArrayList<MessageFormField>();
        ArrayList<MessageFormField> achievements_incomplete = new ArrayList<MessageFormField>();
        for (AchievementCode code : AchievementCode.VALUES_COMPLETED()) {
          if (sketch.global.profile.achievements.get(code).equals(Boolean.TRUE)) {
            achievements_complete.add(new MessageFormField(sketch, code.displayName()));
          }
          else {
            achievements_incomplete.add(new MessageFormField(sketch, code.displayName()));
          }
        }
        this.addField(new MessageFormField(sketch, "Completion Achievements: " +
          achievements_complete.size() + "/" + (achievements_complete.size() +
          achievements_incomplete.size()) + " completed"));
        this.addField(new SpacerFormField(sketch, 10));
        for (MessageFormField field : achievements_complete) {
          field.text_color = DImg.ccolor(0);
          this.addField(field);
        }
        for (MessageFormField field : achievements_incomplete) {
          field.text_color = DImg.ccolor(170, 200);
          this.addField(field);
        }
      }
    }

    class ContinuousTab extends AchievementTab {
      ContinuousTab(LNZ sketch) {
        super(sketch);
        ArrayList<MessageFormField> achievements_complete = new ArrayList<MessageFormField>();
        ArrayList<MessageFormField> achievements_incomplete = new ArrayList<MessageFormField>();
        for (AchievementCode code : AchievementCode.VALUES_CONTINUOUS()) {
          if (sketch.global.profile.achievements.get(code).equals(Boolean.TRUE)) {
            achievements_complete.add(new MessageFormField(sketch, code.displayName()));
          }
          else {
            achievements_incomplete.add(new MessageFormField(sketch, code.display_progress(sketch.global.profile)));
          }
        }
        this.addField(new MessageFormField(sketch, "Continuous Achievements: " +
          achievements_complete.size() + "/" + (achievements_complete.size() +
          achievements_incomplete.size()) + " completed"));
        this.addField(new SpacerFormField(sketch, 10));
        for (AchievementCode code : AchievementCode.VALUES_CONTINUOUS()) {
          MessageFormField field;
          if (sketch.global.profile.achievements.get(code).equals(Boolean.TRUE)) {
            field = new MessageFormField(sketch, code.displayName());
            field.text_color = DImg.ccolor(0);
          }
          else {
            field = new MessageFormField(sketch, code.display_progress(sketch.global.profile));
            field.text_color = DImg.ccolor(170, 200);
          }
          this.addField(field);
        }
      }
    }

    class HiddenTab extends AchievementTab {
      HiddenTab(LNZ sketch) {
        super(sketch);
        ArrayList<MessageFormField> achievements_complete = new ArrayList<MessageFormField>();
        ArrayList<MessageFormField> achievements_incomplete = new ArrayList<MessageFormField>();
        for (AchievementCode code : AchievementCode.VALUES_HIDDEN()) {
          if (sketch.global.profile.achievements.get(code).equals(Boolean.TRUE)) {
            achievements_complete.add(new MessageFormField(sketch, code.displayName()));
          }
          else {
            achievements_incomplete.add(new MessageFormField(sketch, code.displayName()));
          }
        }
        this.addField(new MessageFormField(sketch, "Hidden Achievements: " +
          achievements_complete.size() + "/" + (achievements_complete.size() +
          achievements_incomplete.size()) + " completed"));
        this.addField(new SpacerFormField(sketch, 10));
        for (MessageFormField field : achievements_complete) {
          field.text_color = DImg.ccolor(0);
          this.addField(field);
        }
      }
    }

    class StatsTab extends AchievementTab {
      private boolean toggle_kills = false;
      private boolean toggle_deaths = false;
      private boolean toggle_quizmos = false;

      StatsTab(LNZ sketch) {
        super(sketch);
        this.setFieldCushion(0);
        this.addField(new MessageFormField(sketch, "   Profile (" + sketch.global.profile.display_name + ") Stats:"));
        this.addField(new SpacerFormField(sketch, 30));
        this.addField(new MessageFormField(sketch, "Levels Completed: " + sketch.global.profile.stats.levels_completed));
        this.addField(new SpacerFormField(sketch, 15));
        MessageFormField units_killed = new MessageFormField(sketch, " > Units Killed: " + sketch.global.profile.stats.units_killed);
        units_killed.button.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
          DImg.ccolor(150, 80), DImg.ccolor(150, 160), DImg.ccolor(1, 0));
        units_killed.release = (FormField field) -> {
          if (!field.button.hovered) {
            return null;
          }
          int amount = sketch.global.profile.stats.units_killed;
          this.toggleKills(amount);
          if (this.toggle_kills) {
            field.setValue(" v Units Killed: " + amount);
          }
          else {
            field.setValue(" > Units Killed: " + amount);
          }
          return null;
        };
        this.addField(units_killed);
        Iterator<Map.Entry<Integer, Integer>> killed_iterator =
          sketch.global.profile.stats.units_killed_details.entrySet().iterator();
        while(killed_iterator.hasNext()) {
          Map.Entry<Integer, Integer> kill = killed_iterator.next();
          MessageFormField field = new MessageFormField(sketch, "     " +
            (new Unit(sketch, kill.getKey())).displayName() + ": " + kill.getValue());
          field.addClass(1);
          field.hidden = true;
          field.setTextSize(18);
          this.addField(field);
        }
        this.addField(new SpacerFormField(sketch, 15));
        MessageFormField times_died = new MessageFormField(sketch, " > Times Died: " + sketch.global.profile.stats.times_died);
        times_died.button.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
          DImg.ccolor(150, 80), DImg.ccolor(150, 160), DImg.ccolor(1, 0));
        times_died.release = (FormField field) -> {
          if (!field.button.hovered) {
            return null;
          }
          int amount = sketch.global.profile.stats.times_died;
          this.toggleDeaths(amount);
          if (this.toggle_deaths) {
            field.setValue(" v Times Died: " + amount);
          }
          else {
            field.setValue(" > Times Died: " + amount);
          }
          return null;
        };
        this.addField(times_died);
        Iterator<Map.Entry<DamageSource, Integer>> death_iterator =
          sketch.global.profile.stats.times_died_details.entrySet().iterator();
        while(death_iterator.hasNext()) {
          Map.Entry<DamageSource, Integer> death = death_iterator.next();
          MessageFormField field = new MessageFormField(sketch, "     " +
            death.getKey().displayString() + ": " + death.getValue());
          field.addClass(2);
          field.hidden = true;
          field.setTextSize(18);
          this.addField(field);
        }
        this.addField(new SpacerFormField(sketch, 15));
        this.addField(new MessageFormField(sketch, "Distance Walked: " +
          Misc.round(sketch.global.profile.stats.distance_walked, 1) + " m"));
        this.addField(new SpacerFormField(sketch, 15));
        MessageFormField quizmo_answers = new MessageFormField(sketch,
          " > Star Pieces Gathered: " + sketch.global.profile.chuck_quizmo_answers.size());
        quizmo_answers.button.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
          DImg.ccolor(150, 80), DImg.ccolor(150, 160), DImg.ccolor(1, 0));
        quizmo_answers.release = (FormField field) -> {
          if (!field.button.hovered) {
            return null;
          }
          int amount = sketch.global.profile.chuck_quizmo_answers.size();
          this.toggleQuizmos(amount);
          if (this.toggle_quizmos) {
            field.setValue(" v Star Pieces Gathered: " + amount);
          }
          else {
            field.setValue(" > Star Pieces Gathered: " + amount);
          }
          return null;
        };
        this.addField(quizmo_answers);
        for (Integer i : sketch.global.profile.chuck_quizmo_answers) {
          MessageFormField field = new MessageFormField(sketch, "     " +
            Level.QuizmoForm.quizmoNumberToLocation(i).displayName());
          field.addClass(3);
          field.hidden = true;
          field.setTextSize(18);
          this.addField(field);
        }
      }

      void toggleKills(int amount) {
        this.toggle_kills = !this.toggle_kills;
        if (amount == 0) {
          this.toggle_kills = false;
        }
        for (FormField field : this.fieldsOfClass(1)) {
          field.hidden = !this.toggle_kills;
        }
      }

      void toggleDeaths(int amount) {
        this.toggle_deaths = !this.toggle_deaths;
        if (amount == 0) {
          this.toggle_deaths = false;
        }
        for (FormField field : this.fieldsOfClass(2)) {
          field.hidden = !this.toggle_deaths;
        }
      }

      void toggleQuizmos(int amount) {
        this.toggle_quizmos = !this.toggle_quizmos;
        if (amount == 0) {
          this.toggle_quizmos = false;
        }
        for (FormField field : this.fieldsOfClass(3)) {
          field.hidden = !this.toggle_quizmos;
        }
      }
    }

    AchievementsForm(LNZ sketch) {
      super(sketch, LNZ.achievementsForm_widthOffset, LNZ.achievementsForm_heightOffset,
      sketch.width - LNZ.achievementsForm_widthOffset, sketch.height - LNZ.achievementsForm_heightOffset);
      this.setTitleText("Achievements");
      this.setTitleSize(20);
      this.color_background = DImg.ccolor(100, 200, 200);
      this.color_header = DImg.ccolor(50, 180, 180);
      if (sketch.global.profile == null) {
        this.canceled = true;
        return;
      }

      this.footer_space = 150;
      this.tab_button_height = 55;
      this.tab_button_max_width = 140;
      this.tab_button_alignment = PConstants.CENTER;
      this.addTab(new CompletionTab(sketch), "Completion");
      this.addTab(new ContinuousTab(sketch), "Continuous");
      this.addTab(new HiddenTab(sketch), "Hidden");
      this.addTab(new StatsTab(sketch), "Stats");

      TabConfig tab_config = new TabConfig();
      tab_config.tab_text_size = 18;
      tab_config.color_background = DImg.ccolor(150, 220, 220);
      tab_config.color_stroke = DImg.ccolor(150, 220, 220);
      tab_config.scrollbar_width_multiplier = 0.015;
      tab_config.scrollbar_min_width = 15;
      tab_config.scrollbar_max_width = 20;
      tab_config.scrollbar_color_default = DImg.ccolor(100, 200, 200);
      tab_config.scrollbar_color_hovered = DImg.ccolor(150, 220, 220);
      tab_config.scrollbar_color_clicked = DImg.ccolor(50, 180, 180);
      tab_config.scrollbar_color_space = DImg.ccolor(150, 220, 220);
      this.setTabConfig(tab_config);

      SubmitFormField perk_tree = new SubmitFormField(sketch, "");
      perk_tree.button = new OpenPerkTreeButton(sketch, 0, 0, 0, 30);
      perk_tree.align_left = true;
      sketch.textSize(perk_tree.button.text_size);
      perk_tree.setButtonHeight((sketch.textAscent() + sketch.textDescent() + 4) * 1.2);

      this.addField(new SpacerFormField(sketch, 10));
      this.addField(new MessageFormField(sketch, " Achievement Tokens: " + sketch.global.profile.achievement_tokens + " 〶"));
      this.addField(perk_tree);
    }

    public void submit() {
      InterfaceLNZ.this.openPlayerTree();
    }

    public void keyPress(int key, int keyCode) {
      super.keyPress(key, keyCode);
      if ((key == 'i' || key == 'I') && p.global.holding_ctrl) {
        this.cancel();
      }
    }
  }

  protected LNZ p;

  protected Form form = null;
  protected boolean return_to_esc_menu = false;
  protected boolean return_to_heroes_menu = false;
  protected PImage esc_menu_img = null;
  protected PImage heroes_menu_img = null;
  protected boolean showing_nerd_stats = false;

  InterfaceLNZ(LNZ sketch) {
    this.p = sketch;
  }

  boolean escFormOpened() {
    if (this.form == null) {
      return false;
    }
    if (EscForm.class.isInstance(this.form)) {
      return true;
    }
    return false;
  }

  void throwError(String message) {
    if (this.form != null && ErrorForm.class.isInstance(this.form)) {
      ((ErrorForm)this.form).addErrorMessage(message);
    }
    else {
      this.form = new ErrorForm(p, message);
    }
  }

  void achievementsForm() {
    this.form = new AchievementsForm(p);
  }

  void heroesForm() {
    this.form = new HeroesForm(p);
  }

  void openHeroForm(HeroCode code) {
    if (!p.global.profile.heroes.containsKey(code)) {
      return;
    }
    this.return_to_heroes_menu = true;
    this.heroes_menu_img = FormLNZ.getFormLNZImage(p.global, this.form);
    this.form = new HeroForm(p, p.global.profile.heroes.get(code));
  }

  void optionsForm() {
    this.form = new OptionsForm(p);
  }

  void openPlayerTree() {
    if (p.global.profile != null && !p.global.profile.player_tree.curr_viewing) {
      p.global.profile.player_tree.curr_viewing = true;
      p.global.profile.player_tree.setLocation(0, 0, p.width, p.height);
      p.global.profile.player_tree.setView(0, 0);
    }
  }

  void LNZ_update(int millis) {
    if (p.global.profile != null && p.global.profile.player_tree.curr_viewing) {
      p.global.setCursor("icons/cursor_white.png");
      p.global.profile.player_tree.update(millis);
      return;
    }
    if (this.form == null) {
      p.global.defaultCursor("icons/cursor_white.png");
      this.update(millis);
    }
    else {
      p.global.defaultCursor("icons/cursor_white.png");
      this.form.update(millis);
      if (FormLNZ.formCanceled(p.global, this.form)) {
        this.form = null;
        if (this.return_to_heroes_menu) {
          this.return_to_heroes_menu = false;
          this.form = new HeroesForm(p);
          FormLNZ.setFormLNZImage(p.global, this.form, this.heroes_menu_img);
          this.heroes_menu_img = null;
        }
        else if (this.return_to_esc_menu) {
          this.return_to_esc_menu = false;
          this.form = new EscForm(p);
          FormLNZ.setFormLNZImage(p.global, this.form, this.esc_menu_img);
          this.esc_menu_img = null;
        }
        else {
          this.restartTimers();
        }
      }
    }
    if (this.showing_nerd_stats) {
      this.showNerdStats();
    }
  }

  void LNZ_mouseMove(float mX, float mY) {
    if (p.global.profile != null && p.global.profile.player_tree.curr_viewing) {
      p.global.profile.player_tree.mouseMove(mX, mY);
      return;
    }
    if (this.form == null) {
      this.mouseMove(mX, mY);
    }
    else {
      this.form.mouseMove(mX, mY);
    }
  }

  void LNZ_mousePress() {
    if (p.global.profile != null && p.global.profile.player_tree.curr_viewing) {
      p.global.profile.player_tree.mousePress();
      return;
    }
    if (this.form == null) {
      this.mousePress();
    }
    else {
      this.form.mousePress();
    }
  }

  void LNZ_mouseRelease(float mX, float mY) {
    if (p.global.profile != null && p.global.profile.player_tree.curr_viewing) {
      p.global.profile.player_tree.mouseRelease(mX, mY);
      return;
    }
    if (this.form == null) {
      this.mouseRelease(mX, mY);
    }
    else {
      this.form.mouseRelease(mX, mY);
    }
  }

  void LNZ_scroll(int amount) {
    if (p.global.profile != null && p.global.profile.player_tree.curr_viewing) {
      p.global.profile.player_tree.scroll(amount);
      return;
    } 
    if (this.form == null) {
      this.scroll(amount);
    }
    else {
      this.form.scroll(amount);
    }
  }

  void LNZ_keyPress(int key, int keyCode) {
    if (p.global.profile != null && p.global.profile.player_tree.curr_viewing) {
      p.global.profile.player_tree.keyPress(key, keyCode);
      return;
    } 
    if (this.form == null) {
      this.keyPress(key, keyCode);
      switch(key) {
        case PConstants.ESC:
          this.openEscForm();
          break;
        case 'i':
        case 'I':
          if (this.form == null && p.global.holding_ctrl && p.global.profile != null) {
            this.form = new AchievementsForm(p);
          }
          break;
        case 'o':
        case 'O':
          if (this.form == null && p.global.holding_ctrl && p.global.profile != null) {
            this.form = new OptionsForm(p);
          }
          break;
        case 'h':
        case 'H':
          if (this.form == null && p.global.holding_ctrl && p.global.profile != null) {
            this.form = new HeroesForm(p);
          }
          break;
        case 'p':
        case 'P':
          if (p.global.holding_ctrl && p.global.profile != null) {
            this.openPlayerTree();
          }
          break;
        case 'n':
        case 'N':
          if (p.global.holding_ctrl) {
            this.showing_nerd_stats = !this.showing_nerd_stats;
          }
          break;
        case '?':
          if (this.form == null) {
            this.form = new TipsAndTricksForm(p);
          }
          break;
      }
    }
    else {
      this.form.keyPress(key, keyCode);
    }
  }

  void LNZ_keyRelease(int key, int keyCode) {
    if (p.global.profile != null && p.global.profile.player_tree.curr_viewing) {
      p.global.profile.player_tree.keyRelease(key, keyCode);
      return;
    }
    if (this.form == null) {
      this.keyRelease(key, keyCode);
    }
    else {
      this.form.keyRelease(key, keyCode);
    }
  }


  abstract Hero getCurrentHeroIfExists();
  abstract void saveAndExitToMainMenu();
  abstract void loseFocus();
  abstract void gainFocus();
  abstract void restartTimers();
  abstract void update(int millis);
  abstract void showNerdStats();
  void showDefaultNerdStats() {
    this.showDefaultNerdStats(DImg.ccolor(0));
  }
  void showDefaultNerdStats(int c) {
    p.fill(c);
    p.textSize(14);
    p.textAlign(PConstants.LEFT, PConstants.TOP);
    float y_stats = 1;
    float line_height = p.textAscent() + p.textDescent() + 2;
    p.text("FPS: " + (int)p.global.lastFPS, 1, y_stats);
    Map<Thread, StackTraceElement[]> all_threads = Thread.getAllStackTraces();
    y_stats += line_height;
    p.text("Active Threads: " + all_threads.size(), 1, y_stats);
    Iterator<Map.Entry<Thread, StackTraceElement[]>> thread_iterator = all_threads.entrySet().iterator();
    while(thread_iterator.hasNext()) {
      Map.Entry<Thread, StackTraceElement[]> entry = thread_iterator.next();
      String thread_name = entry.getKey().getName();
      if (thread_name.equals("TerrainDimgThread") || thread_name.equals("MouseMoveThread") ||
        thread_name.equals("UpdateFogDisplayThread") || thread_name.equals("CopyTerrainDisplayThread") ||
        thread_name.equals("TerrainDimgPieceThread") || thread_name.equals("LoadChunkThread") ||
        thread_name.equals("HangingFeaturesThread") || thread_name.equals("PathFindingThread")) {
        y_stats += line_height;
        p.text("Hanging Thread (" + thread_name + "): " + entry.getKey().toString(), 1, y_stats);
      }
    }
  }
  abstract void mouseMove(float mX, float mY);
  abstract void mousePress();
  abstract void mouseRelease(float mX, float mY);
  abstract void scroll(int amount);
  abstract void keyPress(int key, int keyCode);
  abstract void openEscForm();
  abstract void keyRelease(int key, int keyCode);
}
