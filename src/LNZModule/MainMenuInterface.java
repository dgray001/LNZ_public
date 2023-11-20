package LNZModule;

import java.util.*;
import java.nio.file.*;
import processing.core.*;
import Button.*;
import DImg.DImg;
import FileSystem.FileSystem;
import Form.*;
import Misc.*;

class MainMenuInterface extends InterfaceLNZ {

  abstract class MainMenuGrowButton extends RippleRectangleButton {
    private LNZ p;

    protected double xf_grow;
    protected double ratio; // ratio when shrunk (can have it be > 1 to make it shrink when hovered)
    protected double grow_speed = 0.7; // pixels / ms
    protected PImage icon;
    protected boolean collapsing = false;

    MainMenuGrowButton(LNZ sketch, double xi, double yi, double xf, double yf, double ratio) {
      super(sketch, xi, yi, xf * ratio, yf);
      this.p = sketch;
      this.xf_grow = xf;
      this.ratio = ratio;
      this.max_ripple_distance = xf - xi;
      this.icon = this.getIcon();
      this.text_size = 24;
      this.noStroke();
      this.setColors(DImg.ccolor(170), DImg.ccolor(1, 0), DImg.ccolor(150, 90, 90, 150), DImg.ccolor(240, 180, 180), DImg.ccolor(255));
      this.refreshColor();
    }

    abstract PImage getIcon();

    @Override
    public void update(int millis) {
      int timeElapsed = millis - this.last_update_time;
      double pixelsMoved = timeElapsed * this.grow_speed;
      super.update(millis);
      double pixelsLeft = 0;
      if (this.collapsing) {
        if (this.hovered) {
          pixelsLeft = this.xf_grow - this.xf;
          if (pixelsLeft < pixelsMoved) {
            this.collapsing = false;
            this.refreshColor();
            pixelsMoved = pixelsLeft;
          }
          this.stretchButton(pixelsMoved, PConstants.RIGHT);
        }
        else {
          pixelsMoved *= -1;
          pixelsLeft = this.xf_grow * this.ratio - this.xf;
          if (pixelsLeft > pixelsMoved) {
            this.collapsing = false;
            this.refreshColor();
            pixelsMoved = pixelsLeft;
          }
          this.stretchButton(pixelsMoved, PConstants.RIGHT);
        }
      }
      if (!this.hovered && !this.collapsing) {
        p.imageMode(PConstants.CENTER);
        p.image(this.icon, this.xCenter(), this.yCenter(), this.buttonWidth(), this.buttonHeight());
      }
    }

    void reset() {
      this.stretchButton(this.xf_grow * this.ratio - this.xf, PConstants.RIGHT);
      this.collapsing = false;
      this.clicked = false;
      this.hovered = false;
      this.show_message = false;
      this.refreshColor();
    }

    @Override
    public int fillColor() {
      if (this.collapsing) {
        if (this.clicked) {
          return this.color_click;
        }
        else {
          return this.color_hover;
        }
      }
      return super.fillColor();
    }

    public void hover() {
      p.global.sounds.trigger_interface("interfaces/buttonOn4");
      this.collapsing = true;
      super.hover();
      this.show_message = true;
    }

    public void dehover() {
      this.collapsing = true;
      super.dehover();
      this.show_message = false;
      this.clicked = false;
      this.color_text = DImg.ccolor(255);
    }

    public void click() {
      p.global.sounds.trigger_interface("interfaces/buttonClick6");
      super.click();
      this.color_text = DImg.ccolor(0);
    }

    public void release() {
      super.release();
      this.color_text = DImg.ccolor(255);
      this.reset();
    }
  }


  class MainMenuGrowButton1 extends MainMenuGrowButton {
    MainMenuGrowButton1(LNZ sketch) {
      super(sketch, 0, sketch.height - 60, 200, sketch.height, 0.3);
      this.message = "Exit";
    }
    PImage getIcon() {
      return p.global.images.getImage("icons/power.png");
    }

    @Override
    public void release() {
      if (this.hovered) {
        p.global.exitDelay();
      }
      super.release();
    }
  }

  class MainMenuGrowButton2 extends MainMenuGrowButton {
    MainMenuGrowButton2(LNZ sketch) {
      super(sketch, 0, sketch.height - 160, 200, sketch.height - 100, 0.3);
      this.message = "Options";
    }
    PImage getIcon() {
      return p.global.images.getImage("icons/gear.png");
    }

    @Override
    public void release() {
      if (this.hovered) {
        MainMenuInterface.this.form = new OptionsForm(p);
      }
      super.release();
    }
  }

  class MainMenuGrowButton3 extends MainMenuGrowButton {
    MainMenuGrowButton3(LNZ sketch) {
      super(sketch, 0, sketch.height - 260, 200, sketch.height - 200, 0.3);
      this.message = "Achievements";
    }
    PImage getIcon() {
      return p.global.images.getImage("icons/achievements.png");
    }

    @Override
    public void release() {
      if (this.hovered) {
        MainMenuInterface.this.form = new AchievementsForm(p);
      }
      super.release();
    }
  }

  class MainMenuGrowButton4 extends MainMenuGrowButton {
    MainMenuGrowButton4(LNZ sketch) {
      super(sketch, 0, sketch.height - 360, 200, sketch.height - 300, 0.3);
      this.message = "Map Editor";
    }
    PImage getIcon() {
      return p.global.images.getImage("icons/map.png");
    }

    @Override
    public void update(int millis) {
      super.update(millis);
    }

    @Override
    public void release() {
      if (this.hovered) {
        p.global.state = ProgramState.ENTERING_MAPEDITOR;
      }
      super.release();
    }
  }

  class MainMenuGrowButton5 extends MainMenuGrowButton {
    MainMenuGrowButton5(LNZ sketch) {
      super(sketch, 0, sketch.height - 460, 200, sketch.height - 400, 0.3);
      this.message = "Tutorial";
    }
    PImage getIcon() {
      return p.global.images.getImage("icons/tutorial.png");
    }

    @Override
    public void update(int millis) {
      super.update(millis);
    }

    @Override
    public void release() {
      if (this.hovered) {
        p.global.state = ProgramState.ENTERING_TUTORIAL;
      }
      super.release();
    }
  }

  class MainMenuGrowButton6 extends MainMenuGrowButton {
    MainMenuGrowButton6(LNZ sketch) {
      super(sketch, 0, sketch.height - 560, 200, sketch.height - 500, 0.3);
      this.message = "Minigames";
    }
    PImage getIcon() {
      return p.global.images.getImage("icons/minigame.png");
    }

    @Override
    public void update(int millis) {
      super.update(millis);
    }

    @Override
    public void release() {
      if (this.hovered) {
        p.global.state = ProgramState.ENTERING_MINIGAMES;
      }
      super.release();
    }
  }


  class BannerButton extends ImageButton {
    private LNZ p;
    private BounceDouble beta_button_size = new BounceDouble(160, 200, 160);

    BannerButton(LNZ sketch) {
      super(sketch, sketch.global.images.getImage("banner_default.png"),
        0, 0, 0, 0);
      this.p = sketch;
      double banner_width = Math.min(LNZ.banner_maxWidthRatio * sketch.width,
        LNZ.banner_maxHeightRatio * sketch.height * this.img.width / this.img.height);
      double banner_height = Math.min(LNZ.banner_maxHeightRatio * sketch.height,
        banner_width * this.img.height / this.img.width);
      banner_width = banner_height * this.img.width / this.img.height;
      double xi = 0.5 * (sketch.width - banner_width);
      double yi = -10;
      double xf = 0.5 * (sketch.width + banner_width) - 10;
      double yf = banner_height;
      this.setLocation(xi, yi, xf, yf);
    }

    @Override
    public void update(int millis) {
      if (this.use_time_elapsed) {
        this.beta_button_size.add(0.07 * millis);
      }
      else {
        this.beta_button_size.add(0.07 * (millis - this.last_update_time));
      }
      super.update(millis);
    }

    @Override
    public void drawButton() {
      p.imageMode(PConstants.CORNERS);
      p.image(this.img, this.xi, this.yi, this.xf, this.yf);
      p.imageMode(PConstants.CENTER);
      p.image(p.global.images.getImage("banner_beta.png"),
        this.xf - 0.08 * this.buttonWidth(),
        this.yf - 0.15 * this.buttonHeight(),
        this.beta_button_size.value(), this.beta_button_size.value());
    }

    public void hover() {
      this.setImg(p.global.images.getImage("banner_hovered.png"));
    }

    public void dehover() {
      this.setImg(p.global.images.getImage("banner_default.png"));
    }

    public void click() {
      p.global.sounds.trigger_interface("interfaces/buttonClick1");
      this.setImg(p.global.images.getImage("banner_clicked.png"));
    }

    public void release() {
      if (this.hovered) {
        this.setImg(p.global.images.getImage("banner_default.png"));
        MainMenuInterface.this.form = new CreditsForm(p);
        this.hovered = false;
        this.clicked = false;
      }
    }
  }


  class PlayButton extends LeagueButton {
    private LNZ p;

    PlayButton(LNZ sketch) {
      super(sketch, 0.5 * sketch.width, sketch.height - 10, 300 * LNZ.playButton_scaleFactor,
        400 * LNZ.playButton_scaleFactor, 0.2 * PConstants.PI, 40 * LNZ.playButton_scaleFactor,
        12 * LNZ.playButton_scaleFactor);
      this.p = sketch;
      this.setColors(DImg.ccolor(170), DImg.ccolor(120, 200, 120),
        DImg.ccolor(150, 250, 150), DImg.ccolor(30, 120, 30), DImg.ccolor(50, 10, 50));
      this.message = "Play Game";
      this.show_message = true;
      this.text_size = 22 * LNZ.playButton_scaleFactor;
    }
    public void hover() {
      p.global.sounds.trigger_interface("interfaces/buttonOn2");
    }
    public void dehover() {}
    public void click() {
      p.global.sounds.trigger_interface("interfaces/buttonClick2");
      this.color_text = DImg.ccolor(255, 190, 255);
    }
    public void release() {
      this.color_text = DImg.ccolor(50, 10, 50);
      if (this.hovered) {
        if (p.global.profile.upgraded(PlayerTreeCode.CAN_PLAY)) {
          p.global.state = ProgramState.ENTERING_PLAYING;
        }
        else if (p.global.profile.achievementUnlocked(AchievementCode.COMPLETED_TUTORIAL)) {
          MainMenuInterface.this.form = new UnlockPlayForm(p);
        }
        else {
          MainMenuInterface.this.form = new CompleteTutorialForm(p);
        }
      }
    }
  }


  class ProfileButton extends RippleCircleButton {
    private LNZ p;

    protected double grow_speed = 0.9; // pixels / ms
    protected PImage icon;
    protected boolean collapsing = false;
    protected boolean update_icon = true;
    protected int update_icon_timer = 100;

    ProfileButton(LNZ sketch) {
      super(sketch, sketch.width - LNZ.profileButton_offset,
      sketch.height - LNZ.profileButton_offset,
          2 * LNZ.profileButton_offset);
      this.p = sketch;
      this.icon = p.global.images.getImage("units/ben.png");
      this.message = "Profile";
      this.max_ripple_distance = (xf - xi) * LNZ.profileButton_growfactor;
      this.text_size = 32;
      this.noStroke();
      this.setColors(DImg.ccolor(170), DImg.ccolor(1, 0), DImg.ccolor(1, 0),
        DImg.ccolor(60, 60, 20, 200), DImg.ccolor(255));
      this.refreshColor();
    }

    String getImageString() {
      if (p.global.profile != null && p.global.profile.curr_hero != null &&
        p.global.profile.curr_hero != HeroCode.ERROR) {
        return p.global.profile.curr_hero.imagePathHeader();
      }
      return "ben";
    }

    @Override
    public void update(int millis) {
      int time_elapsed = millis - this.last_update_time;
      if (this.update_icon) {
        this.update_icon_timer -= time_elapsed;
        if (this.update_icon_timer < 0) {
          this.update_icon = false;
          this.icon = p.global.images.getImage("units/" + this.getImageString() + ".png");
        }
      }
      double pixelsMoved = time_elapsed * this.grow_speed;
      super.update(millis);
      double pixelsLeft = 0;
      if (this.collapsing) {
        if (this.hovered) {
          pixelsLeft = 4 * LNZ.profileButton_offset * LNZ.profileButton_growfactor - (this.xf - this.xi);
          if (pixelsLeft < pixelsMoved) {
            this.collapsing = false;
            this.refreshColor();
            pixelsMoved = pixelsLeft;
          }
          this.stretchButton(pixelsMoved, PConstants.LEFT);
          this.stretchButton(pixelsMoved, PConstants.UP);
        }
        else {
          pixelsMoved *= -1;
          pixelsLeft = 4 * LNZ.profileButton_offset - (this.xf - this.xi);
          if (pixelsLeft > pixelsMoved) {
            this.collapsing = false;
            this.refreshColor();
            pixelsMoved = pixelsLeft;
          }
          this.stretchButton(pixelsMoved, PConstants.LEFT);
          this.stretchButton(pixelsMoved, PConstants.UP);
        }
      }
      p.imageMode(PConstants.CENTER);
      p.image(this.icon, this.xCenter(), this.yCenter() - 0.2 * (this.yf - this.yi),
        0.4 * this.buttonWidth(), 0.4 * this.buttonHeight());
    }

    @Override
    public void writeText() {
      if (this.show_message) {
        p.fill(this.color_text);
        p.textAlign(PConstants.CENTER, PConstants.TOP);
        p.textSize(this.text_size);
        if (this.adjust_for_text_descent) {
          p.text(this.message, this.xCenter(), this.yCenter() - p.textDescent());
        }
        else {
          p.text(this.message, this.xCenter(), this.yCenter());
        }
      }
    }

    void reset() {
      this.icon = p.global.images.getImage("units/" + this.getImageString() + ".png");
      this.color_text = DImg.ccolor(255);
      this.stretchButton(4 * LNZ.profileButton_offset - (this.xf - this.xi), PConstants.LEFT);
      this.stretchButton(4 * LNZ.profileButton_offset - (this.yf - this.yi), PConstants.UP);
      this.collapsing = false;
      this.clicked = false;
      this.hovered = false;
      this.show_message = false;
      this.refreshColor();
    }

    @Override
    public int fillColor() {
      if (this.collapsing) {
        if (this.clicked) {
          return this.color_click;
        }
        else {
          return this.color_hover;
        }
      }
      return super.fillColor();
    }

    public void hover() {
      p.global.sounds.trigger_interface("interfaces/buttonOn3");
      this.icon = p.global.images.getImage("units/" + this.getImageString() + "_whiteborder.png");
      this.collapsing = true;
      super.hover();
      this.show_message = true;
    }

    public void dehover() {
      this.icon = p.global.images.getImage("units/" + this.getImageString() + ".png");
      this.color_text = DImg.ccolor(255);
      this.collapsing = true;
      super.dehover();
      this.show_message = false;
      this.clicked = false;
    }

    public void click() {
      p.global.sounds.trigger_interface("interfaces/buttonClick5");
      this.icon = p.global.images.getImage("units/" + this.getImageString() + "_blueborder.png");
      this.color_text = DImg.ccolor(0, 0, 255);
      super.click();
    }

    public void release() {
      this.icon = p.global.images.getImage("units/" + this.getImageString() + ".png");
      this.color_text = DImg.ccolor(255);
      super.release();
      if (this.hovered) {
        MainMenuInterface.this.viewProfile();
      }
      this.reset();
    }
  }


  class ProfileForm extends FormLNZ {
    ProfileForm(LNZ sketch) {
      super(sketch, 0.5 * (sketch.width - LNZ.profileForm_width),
        0.5 * (sketch.height - LNZ.profileForm_height),
        0.5 * (sketch.width + LNZ.profileForm_width),
        0.5 * (sketch.height + LNZ.profileForm_height));
      this.setTitleText(p.global.profile.display_name);
      this.setTitleSize(18);
      this.setFieldCushion(0);
      this.color_background = DImg.ccolor(180, 180, 250);
      this.color_header = DImg.ccolor(90, 90, 200);

      SubmitFormField logout = new SubmitFormField(sketch, "Logout");
      logout.button.setColors(DImg.ccolor(180), DImg.ccolor(190, 190, 240),
        DImg.ccolor(140, 140, 190), DImg.ccolor(90, 90, 140), DImg.ccolor(0));

      this.addField(new SpacerFormField(sketch, 20));
      this.addField(logout);
    }

    public void submit() {
      p.global.profile.save();
      p.global.profile = null;
      MainMenuInterface.this.loadExistingProfile();
    }
  }


  class CreditsForm extends FormLNZ {
    CreditsForm(LNZ sketch) {
      super(sketch, 0.5 * (sketch.width - LNZ.creditsForm_width),
        0.5 * (sketch.height - LNZ.creditsForm_height),
        0.5 * (sketch.width + LNZ.creditsForm_width),
        0.5 * (sketch.height + LNZ.creditsForm_height));
      this.setTitleText("Credits");
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(250, 180, 250);
      this.color_header = DImg.ccolor(170, 30, 170);

      SubmitFormField submit = new SubmitFormField(sketch, "  Ok  ");
      submit.button.setColors(DImg.ccolor(220), DImg.ccolor(240, 190, 240),
        DImg.ccolor(190, 140, 190), DImg.ccolor(140, 90, 140), DImg.ccolor(0));
      this.addField(new SpacerFormField(sketch, 0));
      this.addField(new TextBoxFormField(sketch, LNZ.credits, 200));
      this.addField(submit);
    }
    public void submit() {
      this.canceled = true;
    }
  }


  class LoadProfileForm extends FormLNZ {
    private ArrayList<Path> profiles;

    LoadProfileForm(LNZ sketch) {
      super(sketch, 0.5 * (sketch.width - LNZ.newProfileForm_width),
        0.5 * (sketch.height - LNZ.newProfileForm_height),
        0.5 * (sketch.width + LNZ.newProfileForm_width),
        0.5 * (sketch.height + LNZ.newProfileForm_height));
      this.setTitleText("Load Profile");
      this.setTitleSize(18);
      this.setFieldCushion(0);
      this.color_background = DImg.ccolor(250, 180, 180);
      this.color_header = DImg.ccolor(180, 50, 50);

      RadiosFormField radios = new RadiosFormField(sketch, "Choose a profile:");
      this.profiles = FileSystem.listFolders(sketch, "data/profiles");
      if (this.profiles.size() == 0) {
        MainMenuInterface.this.createNewProfile();
        return;
      }
      for (Path p : this.profiles) {
        radios.addRadio(p.getFileName().toString() + "  ");
      }
      MessageFormField error = new MessageFormField(sketch, "");
      error.text_color = DImg.ccolor(150, 20, 20);
      error.setTextSize(18);
      CheckboxFormField checkbox = new CheckboxFormField(sketch, "Save as default profile  ");
      checkbox.setTextSize(16);
      checkbox.checkbox.checked = true;
      SubmitFormField submit = new SubmitFormField(sketch, "Play Profile");
      submit.button.setColors(DImg.ccolor(180), DImg.ccolor(240, 190, 190),
        DImg.ccolor(190, 140, 140), DImg.ccolor(140, 90, 90), DImg.ccolor(0));
      ButtonFormField newProfileButton = new ButtonFormField(sketch, "Create New Profile");
      newProfileButton.button.setColors(DImg.ccolor(180), DImg.ccolor(240, 190, 190),
        DImg.ccolor(190, 140, 140), DImg.ccolor(140, 90, 90), DImg.ccolor(0));

      this.addField(new SpacerFormField(sketch, 20));
      this.addField(radios);
      this.addField(error);
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(checkbox);
      this.addField(new SpacerFormField(sketch, 8));
      this.addField(submit);
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(newProfileButton);
    }

    public void submit() {
      String profileIndex = this.fields.get(1).getValue();
      if (!Misc.isInt(profileIndex)) {
        this.fields.get(2).setValue("Select a profile to play");
        return;
      }
      int index = Misc.toInt(profileIndex);
      if (index < 0 || index >= this.profiles.size()) {
        this.fields.get(2).setValue("Select a profile to play");
        return;
      }
      String profileName = this.profiles.get(index).getFileName().toString();
      if (MainMenuInterface.this.loadProfile(profileName)) {
        this.canceled = true;
        if (this.fields.get(4).getValue().equals("true")) {
          p.global.configuration.default_profile_name = profileName;
          p.global.configuration.save();
        }
        p.global.log("Opened profile: " + profileName);
      }
      else {
        this.fields.get(2).setValue("There was an error opening the profile");
      }
    }

    @Override
    public void cancel() {
      this.fields.get(2).setValue("You must select a profile");
    }

    @Override
    public void buttonPress(int i) {
      MainMenuInterface.this.createNewProfile();
    }
  }


  class NewProfileForm extends FormLNZ {
    NewProfileForm(LNZ sketch) {
      super(sketch, 0.5 * (sketch.width - LNZ.newProfileForm_width),
        0.5 * (sketch.height - LNZ.newProfileForm_height),
        0.5 * (sketch.width + LNZ.newProfileForm_width),
        0.5 * (sketch.height + LNZ.newProfileForm_height));
      this.setTitleText("New Profile");
      this.setTitleSize(18);
      this.setFieldCushion(0);
      this.color_background = DImg.ccolor(250, 180, 180);
      this.color_header = DImg.ccolor(180, 50, 50);

      StringFormField input = new StringFormField(sketch, "  ", "Enter profile name");
      input.input.typing = true;
      MessageFormField error = new MessageFormField(sketch, " ");
      error.text_color = DImg.ccolor(150, 20, 20);
      error.setTextSize(18);
      CheckboxFormField checkbox = new CheckboxFormField(sketch, " Save as default profile  ");
      checkbox.setTextSize(16);
      checkbox.checkbox.checked = true;
      SubmitFormField submit = new SubmitFormField(sketch, "Create New Profile");
      submit.button.setColors(DImg.ccolor(180), DImg.ccolor(240, 190, 190),
        DImg.ccolor(190, 140, 140), DImg.ccolor(140, 90, 90), DImg.ccolor(0));
      ButtonFormField loadProfileButton = new ButtonFormField(sketch, "Load Existing Profile");
      loadProfileButton.button.setColors(DImg.ccolor(180), DImg.ccolor(240, 190, 190),
        DImg.ccolor(190, 140, 140), DImg.ccolor(140, 90, 90), DImg.ccolor(0));
      ArrayList<Path> profiles = FileSystem.listFolders(sketch, "data/profiles");
      if (profiles.size() == 0) {
        loadProfileButton.button.disabled = true;
      }

      this.addField(new SpacerFormField(sketch, 20));
      this.addField(input);
      this.addField(error);
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(checkbox);
      this.addField(new SpacerFormField(sketch, 8));
      this.addField(submit);
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(loadProfileButton);
    }

    public void submit() {
      String possible_profile_name = this.fields.get(1).getValue();
      int errorcode = Profile.isValidProfileName(p, possible_profile_name);
      switch(errorcode) {
        case 0:
          Profile prof = new Profile(p, possible_profile_name);
          prof.save();
          p.global.profile = prof;
          this.canceled = true;
          if (this.fields.get(4).getValue().equals("true")) {
            p.global.configuration.default_profile_name = possible_profile_name;
            p.global.configuration.save();
          }
          p.global.log("Creating new profile: " + possible_profile_name);
          break;
        case 1:
          this.fields.get(2).setValue("Enter a profile name.");
          break;
        case 2:
          this.fields.get(2).setValue("Profile name must start with a letter.");
          break;
        case 3:
          this.fields.get(2).setValue("Profile name must be alphanumeric.");
          break;
        case 4:
          this.fields.get(2).setValue("That profile already exists.");
          break;
        default:
          this.fields.get(2).setValue("An unknown error occured.");
          break;
      }
    }

    @Override
    public void cancel() {
      this.fields.get(2).setValue("You must create a profile");
    }

    @Override
    public void buttonPress(int i) {
      MainMenuInterface.this.loadExistingProfile();
    }
  }


  class CompleteTutorialForm extends FormLNZ {
    protected double arrow_x = 0;
    protected double arrow_y = 0;
    CompleteTutorialForm(LNZ sketch) {
      super(sketch, 0.5 * sketch.width - 120, 0.5 * sketch.height - 120,
        0.5 * sketch.width + 120, 0.5 * sketch.height + 120);
      this.setTitleText("Play Game");
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);
      this.scrollbar.setButtonColors(DImg.ccolor(170), DImg.ccolor(190, 255, 190),
        DImg.ccolor(220, 255, 220), DImg.ccolor(160, 220, 160), DImg.ccolor(0));

      SubmitFormField submit = new SubmitFormField(sketch, "  Ok  ");
      submit.button.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      this.addField(new SpacerFormField(sketch, 0));
      TextBoxFormField textbox = new TextBoxFormField(sketch, "Please complete the tutorial " +
        "before launching the game.\nThe tutorial can be found here and normally takes " +
        "about 15 minutes to complete.", 120);
      textbox.textbox.scrollbar.setButtonColors(DImg.ccolor(170), DImg.ccolor(190, 255, 190),
        DImg.ccolor(220, 255, 220), DImg.ccolor(160, 220, 160), DImg.ccolor(0));
      this.addField(textbox);
      this.addField(submit);

      MainMenuGrowButton button = null;
      try {
        button = MainMenuInterface.this.growButtons[4];
        this.arrow_x = button.xf + 70;
        this.arrow_y = button.yCenter();
      } catch(ArrayIndexOutOfBoundsException e) {}
    }

    public void update(int millis) {
      super.update(millis);
      int frame = (int)LNZ.gif_arrow_frames * (millis %
        LNZ.gif_arrow_time) / (1 + LNZ.gif_arrow_time);
      p.translate(this.arrow_x, this.arrow_y);
      p.rotate(PConstants.PI);
      p.imageMode(PConstants.CENTER);
      p.image(p.global.images.getImage("gifs/arrow/" + frame + ".png"), 0, 0, 130, 130);
      p.rotate(-PConstants.PI);
      p.translate(-this.arrow_x, -this.arrow_y);
    }
    public void submit() {
      this.canceled = true;
    }
  }


  class UnlockPlayForm extends FormLNZ {
    protected double arrow_x = 0;
    protected double arrow_y = 0;
    UnlockPlayForm(LNZ sketch) {
      super(sketch, 0.5 * sketch.width - 120, 0.5 * sketch.height - 120,
        0.5 * sketch.width + 120, 0.5 * sketch.height + 120);
      this.setTitleText("Play Game");
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);
      this.scrollbar.setButtonColors(DImg.ccolor(170), DImg.ccolor(190, 255, 190),
        DImg.ccolor(220, 255, 220), DImg.ccolor(160, 220, 160), DImg.ccolor(0));

      SubmitFormField submit = new SubmitFormField(sketch, "  Ok  ");
      submit.button.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      this.addField(new SpacerFormField(sketch, 0));
      TextBoxFormField textbox = new TextBoxFormField(sketch, "You must unlock the " +
        "ability to play the game in your perk tree. You can open your perk " +
        "tree with 'ctrl-p' or from the Achievements view.", 120);
      textbox.textbox.scrollbar.setButtonColors(DImg.ccolor(170), DImg.ccolor(190, 255, 190),
        DImg.ccolor(220, 255, 220), DImg.ccolor(160, 220, 160), DImg.ccolor(0));
      this.addField(textbox);
      this.addField(submit);

      MainMenuGrowButton button = null;
      try {
        button = MainMenuInterface.this.growButtons[2];
        this.arrow_x = button.xf + 70;
        this.arrow_y = button.yCenter();
      } catch(ArrayIndexOutOfBoundsException e) {}
    }

    public void update(int millis) {
      super.update(millis);
      int frame = (int)LNZ.gif_arrow_frames * (millis %
        LNZ.gif_arrow_time) / (1 + LNZ.gif_arrow_time);
      p.translate(this.arrow_x, this.arrow_y);
      p.rotate(PConstants.PI);
      p.imageMode(PConstants.CENTER);
      p.image(p.global.images.getImage("gifs/arrow/" + frame + ".png"), 0, 0, 130, 130);
      p.rotate(-PConstants.PI);
      p.translate(-this.arrow_x, -this.arrow_y);
    }
    public void submit() {
      this.canceled = true;
    }
  }


  class BackgroundImageThread extends Thread {
    private LNZ p;
    private PImage img;
    private double distance_threshhold = 20;
    private double mX = 0;
    private double mY = 0;

    BackgroundImageThread(LNZ sketch) {
      super("BackgroundImageThread");
      this.setDaemon(true);
      this.p = sketch;
      this.img = sketch.createImage(sketch.width, sketch.height, PConstants.ARGB);
      this.mX = sketch.mouseX;
      this.mY = sketch.mouseY;
      if (sketch.global.profile != null) {
        this.distance_threshhold = 20 + 10 * p.global.profile.achievementsCompleted();
      }
    }

    @Override
    public void run() {
      DImg dimg = new DImg(p, this.img);
      dimg.transparencyGradientFromPoint(this.mX, this.mY, this.distance_threshhold);
      this.img = dimg.img;
    }
  }


  private MainMenuGrowButton[] growButtons = new MainMenuGrowButton[6];
  private BannerButton banner;
  private PlayButton play;
  private ProfileButton profile;
  private PImage backgroundImagePicture;
  private PImage backgroundImage;
  private BackgroundImageThread thread;

  MainMenuInterface(LNZ sketch) {
    super(sketch);
    this.banner = new BannerButton(sketch);
    this.play = new PlayButton(sketch);
    this.profile = new ProfileButton(sketch);
    this.thread = new BackgroundImageThread(sketch);
    this.backgroundImagePicture = DImg.resizeImage(sketch, sketch.global.images.getImage("hillary.png"),
      sketch.width, sketch.height);
    this.backgroundImage = DImg.createPImage(sketch, DImg.ccolor(0), sketch.width, sketch.height);
    this.growButtons[0] = new MainMenuGrowButton1(sketch);
    this.growButtons[1] = new MainMenuGrowButton2(sketch);
    this.growButtons[2] = new MainMenuGrowButton3(sketch);
    this.growButtons[3] = new MainMenuGrowButton4(sketch);
    this.growButtons[4] = new MainMenuGrowButton5(sketch);
    this.growButtons[5] = new MainMenuGrowButton6(sketch);
    if (sketch.global.profile == null) {
      this.loadProfile();
    }
    this.thread.start();
  }

  void loadProfile() {
    ArrayList<Path> profiles = FileSystem.listFolders(p, "data/profiles");
    for (Path path : profiles) {
      if (p.global.configuration.default_profile_name.toLowerCase().equals(path.getFileName().toString().toLowerCase())) {
        if (!loadProfile(p.global.configuration.default_profile_name)) {
          p.global.log("Failed to load default profile: " + p.global.configuration.default_profile_name);
          break;
        }
        p.global.log("Loading default profile: " + p.global.configuration.default_profile_name);
        return;
      }
    }
    if (profiles.size() > 0) {
      this.form = new LoadProfileForm(p);
    }
    else {
      this.createNewProfile();
    }
  }

  // returns true if profile loaded
  boolean loadProfile(String profile_name) {
    FileSystem.mkdir(p, "data/profiles", false, true);
    if (!FileSystem.folderExists(p, "data/profiles/" + profile_name.toLowerCase())) {
      p.global.log("Profile: No profile folder exists with name " + profile_name + ".");
      return false;
    }
    if (!FileSystem.fileExists(p, "data/profiles/" + profile_name.toLowerCase() + "/profile.lnz")) {
      p.global.errorMessage("ERROR: Profile file missing for " + profile_name + ".");
      return false;
    }
    if (!FileSystem.fileExists(p, "data/profiles/" + profile_name.toLowerCase() + "/heroes.lnz")) {
      p.global.errorMessage("ERROR: Heroes file missing for " + profile_name + ".");
      return false;
    }
    if (!FileSystem.fileExists(p, "data/profiles/" + profile_name.toLowerCase() + "/options.lnz")) {
      p.global.errorMessage("ERROR: Options file missing for " + profile_name + ".");
      return false;
    }
    p.global.profile = Profile.readProfile(p, p.sketchPath("data/profiles/" + profile_name.toLowerCase()));
    return true;
  }
  // create new profile
  void createNewProfile() {
    this.form = new NewProfileForm(p);
  }
  // load profile
  void loadExistingProfile() {
    this.form = new LoadProfileForm(p);
  }
  // Open profile
  void viewProfile() {
    this.form = new ProfileForm(p);
  }

  Hero getCurrentHeroIfExists() {
    return null;
  }

  void update(int millis) {
    // draw background
    p.imageMode(PConstants.CORNER);
    p.image(this.backgroundImagePicture, 0, 0);
    p.image(this.backgroundImage, 0, 0);
    // update elements
    for (MainMenuGrowButton button : this.growButtons) {
      button.update(millis);
    }
    this.banner.update(millis);
    this.play.update(millis);
    this.profile.update(millis);
    // restart thread
    if (!this.thread.isAlive()) {
      this.backgroundImage = this.thread.img;
      this.thread = new BackgroundImageThread(p);
      this.thread.start();
    }
    // check for tips and tricks
    if (p.global.profile != null && !p.global.profile.has_seen_tips_and_tricks &&
      p.global.profile.achievementUnlocked(AchievementCode.COMPLETED_TUTORIAL)) {
      this.form = new TipsAndTricksForm(p);
      p.global.profile.has_seen_tips_and_tricks = true;
    }
  }

  void showNerdStats() {
    showDefaultNerdStats(DImg.ccolor(255));
  }

  void mouseMove(float mX, float mY) {
    for (MainMenuGrowButton button : this.growButtons) {
      button.mouseMove(mX, mY);
    }
    this.banner.mouseMove(mX, mY);
    this.play.mouseMove(mX, mY);
    this.profile.mouseMove(mX, mY);
  }

  void mousePress() {
    for (MainMenuGrowButton button : this.growButtons) {
      button.mousePress();
    }
    this.banner.mousePress();
    this.play.mousePress();
    this.profile.mousePress();
  }

  void mouseRelease(float mX, float mY) {
    for (MainMenuGrowButton button : this.growButtons) {
      button.mouseRelease(mX, mY);
    }
    this.banner.mouseRelease(mX, mY);
    this.play.mouseRelease(mX, mY);
    this.profile.mouseRelease(mX, mY);
  }

  void scroll(int amount) {}
  void keyPress(int key, int keyCode) {}
  void openEscForm() {}
  void keyRelease(int key, int keyCode) {}


  void loseFocus() {}
  void gainFocus() {}
  void restartTimers() {}
  void saveAndExitToMainMenu() {}
}
