package LNZModule;

import java.util.*;
import processing.core.*;
import Button.*;
import DImg.DImg;
import Element.TextBox;

class DeskInventory extends Inventory {
  class DrawerButton1 extends ImageButton {
    private LNZ p;
    protected boolean opened = false;
    DrawerButton1(LNZ sketch) {
      super(sketch, sketch.global.images.getImage("features/desk_drawer1_closed.png"),
        0, 0, 0, 0);
      this.p = sketch;
      this.force_left_button = false;
    }

    public void hover() {}
    public void click() {
      if (this.opened) {
        this.opened = false;
        DeskInventory.this.slots.get(0).deactivated = true;
        DeskInventory.this.slots.get(1).deactivated = true;
        this.moveButton(0, -DeskInventory.this.button_size);
        p.global.sounds.trigger_environment("features/desk_drawer1_close");
        this.img = p.global.images.getImage("features/desk_drawer1_closed.png");
      }
      else {
        this.opened = true;
        DeskInventory.this.slots.get(0).deactivated = false;
        DeskInventory.this.slots.get(1).deactivated = false;
        this.moveButton(0, DeskInventory.this.button_size);
        p.global.sounds.trigger_environment("features/desk_drawer1_open");
        this.img = p.global.images.getImage("features/desk_drawer1_opened.png");
      }
    }
    public void dehover() {}
    public void release() {}
  }

  class DrawerButton2 extends ImageButton {
    private LNZ p;
    protected boolean opened = false;
    DrawerButton2(LNZ sketch) {
      super(sketch, sketch.global.images.getImage("features/desk_drawer2_closed.png"),
        0, 0, 0, 0);
      this.p = sketch;
      this.force_left_button = false;
    }

    public void hover() {}
    public void click() {
      if (this.opened) {
        this.opened = false;
        DeskInventory.this.slots.get(2).deactivated = true;
        this.moveButton(-DeskInventory.this.button_size, -0.3 * DeskInventory.this.button_size);
        p.global.sounds.trigger_environment("features/desk_drawer2_close");
        this.img = p.global.images.getImage("features/desk_drawer2_closed.png");
      }
      else {
        this.opened = true;
        DeskInventory.this.slots.get(2).deactivated = false;
        this.moveButton(DeskInventory.this.button_size, 0.3 * DeskInventory.this.button_size);
        p.global.sounds.trigger_environment("features/desk_drawer2_open");
        this.img = p.global.images.getImage("features/desk_drawer2_opened.png");
      }
    }
    public void dehover() {}
    public void release() {}
  }

  class DrawerButton3 extends ImageButton {
    private LNZ p;
    protected boolean opened = false;
    DrawerButton3(LNZ sketch) {
      super(sketch, sketch.global.images.getImage("features/desk_drawer3_closed.png"),
        0, 0, 0, 0);
      this.p = sketch;
      this.force_left_button = false;
    }

    public void hover() {}
    public void click() {
      if (this.opened) {
        this.opened = false;
        DeskInventory.this.slots.get(5).deactivated = true;
        this.moveButton(-DeskInventory.this.button_size, -0.3 * DeskInventory.this.button_size);
        p.global.sounds.trigger_environment("features/desk_drawer2_close");
        this.img = p.global.images.getImage("features/desk_drawer3_closed.png");
      }
      else {
        this.opened = true;
        DeskInventory.this.slots.get(5).deactivated = false;
        this.moveButton(DeskInventory.this.button_size, 0.3 * DeskInventory.this.button_size);
        p.global.sounds.trigger_environment("features/desk_drawer2_open");
        this.img = p.global.images.getImage("features/desk_drawer2_opened.png");
      }
    }
    public void dehover() {}
    public void release() {}
  }

  class DrawerButton4 extends ImageButton {
    private LNZ p;
    protected boolean opened = false;
    DrawerButton4(LNZ sketch) {
      super(sketch, sketch.global.images.getImage("features/desk_drawer4_closed.png"),
        0, 0, 0, 0);
      this.p = sketch;
      this.force_left_button = false;
    }

    public void hover() {}
    public void click() {
      if (this.opened) {
        this.opened = false;
        DeskInventory.this.slots.get(8).deactivated = true;
        this.moveButton(-DeskInventory.this.button_size, -0.3 * DeskInventory.this.button_size);
        p.global.sounds.trigger_environment("features/desk_drawer2_close");
        this.img = p.global.images.getImage("features/desk_drawer4_closed.png");
      }
      else {
        this.opened = true;
        DeskInventory.this.slots.get(8).deactivated = false;
        this.moveButton(DeskInventory.this.button_size, 0.3 * DeskInventory.this.button_size);
        p.global.sounds.trigger_environment("features/desk_drawer2_open");
        this.img = p.global.images.getImage("features/desk_drawer2_opened.png");
      }
    }
    public void dehover() {}
    public void release() {}
  }

  protected DrawerButton1 top_drawer;
  protected DrawerButton2 mid_drawer;
  protected DrawerButton3 mid_drawer2;
  protected DrawerButton4 bottom_drawer;

  DeskInventory(LNZ sketch) {
    super(sketch, 3, 3, true);
    this.top_drawer = new DrawerButton1(sketch);
    this.mid_drawer = new DrawerButton2(sketch);
    this.mid_drawer2 = new DrawerButton3(sketch);
    this.bottom_drawer = new DrawerButton4(sketch);
    this.deactivateSlots();
    this.setButtonSize(this.button_size);
  }


  // stashes in closed drawers for specialized inventories
  void stashInDrawers(Item i) {
    this.stash(i, 0, 1, 2, 5, 8);
  }

  void closeDrawers() {
    this.top_drawer.opened = false;
    this.mid_drawer.opened = false;
    this.bottom_drawer.opened = false;
    this.top_drawer.img = p.global.images.getImage("features/desk_drawer1_closed.png");
    this.mid_drawer.img = p.global.images.getImage("features/desk_drawer2_closed.png");
    this.mid_drawer2.img = p.global.images.getImage("features/desk_drawer3_closed.png");
    this.bottom_drawer.img = p.global.images.getImage("features/desk_drawer4_closed.png");
    this.deactivateSlots();
  }

  @Override
  void setButtonSize(double button_size) {
    super.setButtonSize(button_size);
    this.top_drawer.setLocation(2, 2, 2 + 2 * button_size, 2 + this.button_size);
    if (this.top_drawer.opened) {
      this.top_drawer.moveButton(0, DeskInventory.this.button_size);
    }
    this.mid_drawer.setLocation(2 + 2 * button_size, 2, 2 + 3 * button_size, 2 + this.button_size);
    if (this.mid_drawer.opened) {
      this.mid_drawer.moveButton(DeskInventory.this.button_size, 0.3 * DeskInventory.this.button_size);
    }
    this.mid_drawer2.setLocation(2 + 2 * button_size, 2 + button_size, 2 + 3 * button_size, 2 + 2 * this.button_size);
    if (this.mid_drawer2.opened) {
      this.mid_drawer2.moveButton(DeskInventory.this.button_size, 0.3 * DeskInventory.this.button_size);
    }
    this.bottom_drawer.setLocation(2 + 2 * button_size, 2 + 2 * button_size, 2 + 3 * button_size, 2 + 3 * this.button_size);
    if (this.bottom_drawer.opened) {
      this.bottom_drawer.moveButton(DeskInventory.this.button_size, 0.3 * DeskInventory.this.button_size);
    }
  }

  @Override
  void update(int millis) {
    this.drawBackground();
    p.imageMode(PConstants.CORNERS);
    p.image(p.global.images.getImage("features/desk_inventory.png"), 0, 0, this.display_width, this.display_height);
    if (top_drawer.opened) {
      this.top_drawer.update(millis);
    }
    if (mid_drawer.opened) {
      this.mid_drawer.update(millis);
    }
    if (mid_drawer2.opened) {
      this.mid_drawer2.update(millis);
    }
    if (bottom_drawer.opened) {
      this.bottom_drawer.update(millis);
    }
    super.update(millis, false);
    if (!top_drawer.opened) {
      this.top_drawer.update(millis);
    }
    if (!mid_drawer.opened) {
      this.mid_drawer.update(millis);
    }
    if (!mid_drawer2.opened) {
      this.mid_drawer2.update(millis);
    }
    if (!bottom_drawer.opened) {
      this.bottom_drawer.update(millis);
    }
  }

  @Override
  void mouseMove(float mX, float mY) {
    super.mouseMove(mX, mY);
    this.top_drawer.mouseMove(mX, mY);
    this.mid_drawer.mouseMove(mX, mY);
    this.mid_drawer2.mouseMove(mX, mY);
    this.bottom_drawer.mouseMove(mX, mY);
  }

  @Override
  void mousePress() {
    super.mousePress();
    this.top_drawer.mousePress();
    this.mid_drawer.mousePress();
    this.mid_drawer2.mousePress();
    this.bottom_drawer.mousePress();
  }

  @Override
  void mouseRelease(float mX, float mY) {
    super.mouseRelease(mX, mY);
    this.top_drawer.mouseRelease(mX, mY);
    this.mid_drawer.mouseRelease(mX, mY);
    this.mid_drawer2.mouseRelease(mX, mY);
    this.bottom_drawer.mouseRelease(mX, mY);
  }
}


class StoveInventory extends Inventory {
  class StoveDoor extends ImageButton {
    protected boolean opened = false;
    StoveDoor(LNZ sketch) {
      super(sketch, sketch.global.images.getImage("features/default.png"),
        0, 0, 0, 0);
      this.force_left_button = false;
    }

    public void hover() {}
    public void click() {
      if (this.opened) {
        return;
      }
      this.opened = true;
      StoveInventory.this.slots.get(26).deactivated = false;
      StoveInventory.this.slots.get(27).deactivated = false;
      StoveInventory.this.slots.get(28).deactivated = false;
      StoveInventory.this.slots.get(31).deactivated = false;
      StoveInventory.this.slots.get(32).deactivated = false;
      StoveInventory.this.slots.get(33).deactivated = false;
      this.img = Images.getTransparentPixel();
    }
    public void dehover() {}
    public void release() {}
  }

  abstract class KnobButton extends ImageButton {
    private LNZ p;
    protected int value = 0;
    protected int max_value = 0;
    KnobButton(LNZ sketch, PImage img, int max_value) {
      super(sketch, img, 0, 0, 0, 0);
      this.p = sketch;
      this.max_value = max_value;
      this.force_left_button = false;
    }

    @Override
    public void drawButton() {
      super.drawButton();
      p.imageMode(PConstants.CENTER);
      p.translate(this.xCenter(), this.yCenter());
      float curr_rotation = this.knobRotation();
      p.rotate(curr_rotation);
      p.image(p.global.images.getImage("features/stove_knob.png"), 0, 0,
        0.7 * this.buttonWidth(), 0.7 * this.buttonHeight());
      p.rotate(-curr_rotation);
      p.translate(-this.xCenter(), -this.yCenter());
    }

    @Override
    public void mouseMove(float mX, float mY) {
      super.mouseMove(mX, mY);
      if (this.clicked) {
        // p.rotate (??)
      }
    }

    abstract float knobRotation();

    public void hover() {}
    public void click() {}
    public void dehover() {}
    public void release() {}
  }

  abstract class KnobButtonBurner extends KnobButton {
    KnobButtonBurner(LNZ sketch) {
      super(sketch, sketch.global.images.getImage("features/stove_knob_burner.png"), 6);
    }

    float knobRotation() {
      switch(this.value) {
        case 0:
          return 0;
        case 1:
          return 0;
        case 2:
          return 0;
        case 3:
          return 0;
        case 4:
          return 0;
        case 5:
          return 0;
        case 6:
          return 0;
        default:
          p.global.errorMessage("ERROR: Knob value " + this.value +" invalid for BurnerKnob.");
          return 0;
      }
    }
  }

  class Burner1 extends KnobButtonBurner {
    Burner1(LNZ sketch) {
      super(sketch);
    }
  }

  class Burner2 extends KnobButtonBurner {
    Burner2(LNZ sketch) {
      super(sketch);
    }
  }

  class Burner3 extends KnobButtonBurner {
    Burner3(LNZ sketch) {
      super(sketch);
    }
  }

  class Burner4 extends KnobButtonBurner {
    Burner4(LNZ sketch) {
      super(sketch);
    }
  }

  class OvenKnob extends KnobButton {
    OvenKnob(LNZ sketch) {
      super(sketch, sketch.global.images.getImage("features/stove_knob_oven.png"), 9);
    }

    float knobRotation() {
      switch(this.value) {
        case 0:
          return 0;
        case 1:
          return 0;
        case 2:
          return 0;
        case 3:
          return 0;
        case 4:
          return 0;
        case 5:
          return 0;
        case 6:
          return 0;
        case 7:
          return 0;
        case 8:
          return 0;
        case 9:
          return 0;
        default:
          p.global.errorMessage("ERROR: Knob value " + this.value + " invalid for OvenKnob.");
          return 0;
      }
    }
  }

  protected StoveDoor door;
  protected KnobButton[] knobs = new KnobButton[5];

  StoveInventory(LNZ sketch) {
    super(sketch, 8, 5, true);
    this.deactivateSlots();
    this.door = new StoveDoor(sketch);
    this.slots.get(1).deactivated = false;
    this.slots.get(3).deactivated = false;
    this.slots.get(11).deactivated = false;
    this.slots.get(13).deactivated = false;
    this.knobs[0] = new Burner1(sketch);
    this.knobs[1] = new Burner2(sketch);
    this.knobs[2] = new OvenKnob(sketch);
    this.knobs[3] = new Burner3(sketch);
    this.knobs[4] = new Burner4(sketch);
    this.setButtonSize(this.button_size);
  }

  void closeDrawers() {
    this.door.opened = false;
    this.slots.get(26).deactivated = true;
    this.slots.get(27).deactivated = true;
    this.slots.get(28).deactivated = true;
    this.slots.get(31).deactivated = true;
    this.slots.get(32).deactivated = true;
    this.slots.get(33).deactivated = true;
    this.door.img = p.global.images.getImage("features/default.png");
  }

  @Override
  void setButtonSize(double button_size) {
    super.setButtonSize(button_size);
    this.door.setLocation(2 + button_size, 2 + 5 * button_size,
      2 + 4 * button_size, 2 + 7 * this.button_size);
    for (int i = 0; i < this.knobs.length; i++) {
      this.knobs[i].setLocation(2 + i * button_size, 2 + 3.5 * button_size,
        2 + (i + 1) * button_size, 2 + 4.5 * button_size);
    }
  }

  @Override
  void update(int millis) {
    super.update(millis);
    this.door.update(millis);
    for (KnobButton knob : this.knobs) {
      knob.update(millis);
    }
  }

  @Override
  void mouseMove(float mX, float mY) {
    super.mouseMove(mX, mY);
    this.door.mouseMove(mX, mY);
    for (KnobButton knob : this.knobs) {
      knob.mouseMove(mX, mY);
    }
  }

  @Override
  void mousePress() {
    super.mousePress();
    this.door.mousePress();
    for (KnobButton knob : this.knobs) {
      knob.mousePress();
    }
  }

  @Override
  void mouseRelease(float mX, float mY) {
    super.mouseRelease(mX, mY);
    this.door.mouseRelease(mX, mY);
    for (KnobButton knob : this.knobs) {
      knob.mouseRelease(mX, mY);
    }
  }
}


class MinifridgeInventory extends Inventory {
  class MinifridgeButton extends RectangleButton {
    private LNZ p;
    protected boolean opened = false;
    MinifridgeButton(LNZ sketch) {
      super(sketch, 0, 0, 0, 0);
      this.p = sketch;
      this.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
        DImg.ccolor(120, 30), DImg.ccolor(120, 30), DImg.ccolor(1, 0));
      this.noStroke();
      this.force_left_button = false;
    }

    public void hover() {}
    public void click() {
      if (this.opened) {
        this.opened = false;
        MinifridgeInventory.this.deactivateSlots();
        this.moveButton(-2 * MinifridgeInventory.this.button_size, 0);
        p.global.sounds.trigger_environment("features/minifridge_close");
      }
      else {
        this.opened = true;
        MinifridgeInventory.this.activateSlots();
        this.moveButton(2 * MinifridgeInventory.this.button_size, 0);
        p.global.sounds.trigger_environment("features/minifridge_open");
      }
      this.hovered = false;
      this.clicked = false;
    }
    public void dehover() {}
    public void release() {}
  }

  protected MinifridgeButton button;

  MinifridgeInventory(LNZ sketch) {
    super(sketch, 2, 2, true);
    this.button = new MinifridgeButton(sketch);
    this.deactivateSlots();
    this.setButtonSize(this.button_size);
  }

  @Override
  void setButtonSize(double button_size) {
    super.setButtonSize(button_size);
    this.button.setLocation(2, 2, 2 + 2 * button_size, 2 + 2 * button_size);
    if (this.button.opened) {
      this.button.moveButton(2 * button_size, 0);
    }
  }

  @Override
  void update(int millis) {
    PImage closed_img = p.global.images.getImage("features/minifridge_closed.png");
    double display_w = this.display_height * closed_img.width / closed_img.height;
    if (button.opened) {
      p.imageMode(PConstants.CORNER);
      PImage open_img = p.global.images.getImage("features/minifridge_opened.png");
      double left_side = 0.5 * (this.display_width - display_w);
      double open_display_w = this.display_height * open_img.width / open_img.height;
      p.image(open_img, left_side, 0, open_display_w, this.display_height);
    }
    super.update(millis, false);
    if (!button.opened) {
      p.imageMode(PConstants.CENTER);
      p.image(closed_img, 0.5 * this.display_width, 0.5 * this.display_height, display_w, this.display_height);
    }
    this.button.update(millis);
  }

  @Override
  void mouseMove(float mX, float mY) {
    super.mouseMove(mX, mY);
    this.button.mouseMove(mX, mY);
  }

  @Override
  void mousePress() {
    super.mousePress();
    this.button.mousePress();
  }

  @Override
  void mouseRelease(float mX, float mY) {
    super.mouseRelease(mX, mY);
    this.button.mouseRelease(mX, mY);
  }
}


class RefridgeratorInventory extends Inventory {
  class FridgeButton extends RectangleButton {
    private LNZ p;
    protected boolean opened = false;
    FridgeButton(LNZ sketch) {
      super(sketch, 0, 0, 0, 0);
      this.p = sketch;
      this.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0), DImg.ccolor(120, 30), DImg.ccolor(120, 30), DImg.ccolor(1, 0));
      this.noStroke();
      this.force_left_button = false;
    }

    public void hover() {}
    public void click() {
      if (this.opened) {
        this.opened = false;
        RefridgeratorInventory.this.slots.get(4).deactivated = true;
        RefridgeratorInventory.this.slots.get(5).deactivated = true;
        RefridgeratorInventory.this.slots.get(6).deactivated = true;
        RefridgeratorInventory.this.slots.get(7).deactivated = true;
        RefridgeratorInventory.this.slots.get(8).deactivated = true;
        RefridgeratorInventory.this.slots.get(9).deactivated = true;
        this.moveButton(-2 * RefridgeratorInventory.this.button_size, 0);
        p.global.sounds.trigger_environment("features/fridge_close");
      }
      else {
        this.opened = true;
        RefridgeratorInventory.this.slots.get(4).deactivated = false;
        RefridgeratorInventory.this.slots.get(5).deactivated = false;
        RefridgeratorInventory.this.slots.get(6).deactivated = false;
        RefridgeratorInventory.this.slots.get(7).deactivated = false;
        RefridgeratorInventory.this.slots.get(8).deactivated = false;
        RefridgeratorInventory.this.slots.get(9).deactivated = false;
        this.moveButton(2 * RefridgeratorInventory.this.button_size, 0);
        p.global.sounds.trigger_environment("features/fridge_open");
      }
      this.hovered = false;
      this.clicked = false;
    }
    public void dehover() {}
    public void release() {}
  }


  class FreezerButton extends RectangleButton {
    private LNZ p;
    protected boolean opened = false;
    FreezerButton(LNZ sketch) {
      super(sketch, 0, 0, 0, 0);
      this.p = sketch;
      this.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0), DImg.ccolor(120, 30), DImg.ccolor(120, 30), DImg.ccolor(1, 0));
      this.noStroke();
      this.force_left_button = false;
    }

    public void hover() {}
    public void click() {
      if (this.opened) {
        this.opened = false;
        RefridgeratorInventory.this.slots.get(0).deactivated = true;
        RefridgeratorInventory.this.slots.get(1).deactivated = true;
        RefridgeratorInventory.this.slots.get(2).deactivated = true;
        RefridgeratorInventory.this.slots.get(3).deactivated = true;
        this.moveButton(-2 * RefridgeratorInventory.this.button_size, 0);
        p.global.sounds.trigger_environment("features/freezer_close");
      }
      else {
        this.opened = true;
        RefridgeratorInventory.this.slots.get(0).deactivated = false;
        RefridgeratorInventory.this.slots.get(1).deactivated = false;
        RefridgeratorInventory.this.slots.get(2).deactivated = false;
        RefridgeratorInventory.this.slots.get(3).deactivated = false;
        this.moveButton(2 * RefridgeratorInventory.this.button_size, 0);
        p.global.sounds.trigger_environment("features/freezer_open");
      }
      this.hovered = false;
      this.clicked = false;
    }
    public void dehover() {}
    public void release() {}
  }

  protected FridgeButton fridge;
  protected FreezerButton freezer;
  protected int freezer_sound_left = 0;
  protected double offset = 0;

  RefridgeratorInventory(LNZ sketch) {
    super(sketch, 5, 2, true);
    this.fridge = new FridgeButton(sketch);
    this.freezer = new FreezerButton(sketch);
    this.deactivateSlots();
    this.setButtonSize(this.button_size);
  }

  @Override
  void setButtonSize(double button_size) {
    super.setButtonSize(button_size);
    this.fridge.setLocation(2, 2 + 2 * button_size, 2 + 2 * button_size, 2 + 5 * button_size);
    this.freezer.setLocation(2, 2, 2 + 2 * button_size, 2 + 2 * button_size);
    if (this.fridge.opened) {
      this.fridge.moveButton(2 * button_size, 0);
    }
    if (this.freezer.opened) {
      this.freezer.moveButton(2 * button_size, 0);
    }
  }

  @Override
  void update(int time_elapsed) {
    PImage freezer_closed_img = p.global.images.getImage("features/freezer_closed.png");
    double display_w = 0.4 * this.display_height * freezer_closed_img.width / freezer_closed_img.height;
    PImage fridge_closed_img = p.global.images.getImage("features/fridge_closed.png");
    double fridge_display_h = display_w * fridge_closed_img.height / fridge_closed_img.width;
    this.offset = Math.min(0, 0.6 * this.display_height - fridge_display_h);
    p.translate(0, this.offset);
    if (fridge.opened) {
      p.imageMode(PConstants.CORNER);
      double left_side = 0.5 * (this.display_width - display_w);
      PImage open_img = p.global.images.getImage("features/fridge_opened.png");
      double open_display_w = 1.1285 * fridge_display_h * open_img.width / open_img.height;
      p.image(open_img, left_side, 0.4 * this.display_height, open_display_w, 1.1285 * fridge_display_h);
    }
    if (freezer.opened) {
      p.imageMode(PConstants.CORNER);
      double left_side = 0.5 * (this.display_width - display_w);
      PImage open_img = p.global.images.getImage("features/freezer_opened.png");
      double open_display_w = 1.14935 * 0.4 * this.display_height * open_img.width / open_img.height;
      p.image(open_img, left_side, - 0.14935 * 0.4 * this.display_height, open_display_w, 1.14935 * 0.4 * this.display_height);
      this.freezer_sound_left -= time_elapsed;
      if (this.freezer_sound_left < 10) {
        p.global.sounds.trigger_environment("features/freezer_ambience");
        this.freezer_sound_left = 3250;
      }
    }
    super.update(time_elapsed, false);
    if (!fridge.opened) {
      p.imageMode(PConstants.CORNER);
      p.image(fridge_closed_img, 0.5 * (this.display_width - display_w), 0.4 * this.display_height, display_w, fridge_display_h);
    }
    if (!freezer.opened) {
      p.imageMode(PConstants.CORNER);
      p.image(freezer_closed_img, 0.5 * (this.display_width - display_w), 0, display_w, 0.4 * this.display_height);
      p.global.sounds.silence_environment("features/freezer_ambience");
      this.freezer_sound_left = 0;
    }
    this.fridge.update(time_elapsed);
    this.freezer.update(time_elapsed);
    p.translate(0, -this.offset);
  }

  @Override
  void mouseMove(float mX, float mY) {
    mY -= this.offset;
    super.mouseMove(mX, mY);
    this.fridge.mouseMove(mX, mY);
    this.freezer.mouseMove(mX, mY);
  }

  @Override
  void mousePress() {
    super.mousePress();
    this.fridge.mousePress();
    this.freezer.mousePress();
  }

  @Override
  void mouseRelease(float mX, float mY) {
    super.mouseRelease(mX, mY);
    this.fridge.mouseRelease(mX, mY);
    this.freezer.mouseRelease(mX, mY);
  }
}


class WasherInventory extends Inventory {
  class WasherButton extends RectangleButton {
    private LNZ p;
    protected boolean opened = false;
    WasherButton(LNZ sketch) {
      super(sketch, 0, 0, 0, 0);
      this.p = sketch;
      this.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
        DImg.ccolor(120, 30), DImg.ccolor(120, 30), DImg.ccolor(1, 0));
      this.noStroke();
      this.force_left_button = false;
    }

    public void hover() {}
    public void click() {
      if (this.opened) {
        this.opened = false;
        WasherInventory.this.slots.get(5).deactivated = true;
        WasherInventory.this.slots.get(6).deactivated = true;
        WasherInventory.this.slots.get(9).deactivated = true;
        WasherInventory.this.slots.get(10).deactivated = true;
        this.moveButton(2 * WasherInventory.this.button_size, 0);
        p.global.sounds.trigger_environment("features/washer_close");
      }
      else {
        this.opened = true;
        WasherInventory.this.slots.get(5).deactivated = false;
        WasherInventory.this.slots.get(6).deactivated = false;
        WasherInventory.this.slots.get(9).deactivated = false;
        WasherInventory.this.slots.get(10).deactivated = false;
        this.moveButton(-2 * WasherInventory.this.button_size, 0);
        p.global.sounds.trigger_environment("features/washer_open");
      }
      this.hovered = false;
      this.clicked = false;
    }
    public void dehover() {}
    public void release() {}
  }

  protected WasherButton button;

  WasherInventory(LNZ sketch) {
    super(sketch, 4, 4, true);
    this.button = new WasherButton(sketch);
    this.deactivateSlots();
    this.setButtonSize(this.button_size);
  }

  @Override
  void setButtonSize(double button_size) {
    super.setButtonSize(button_size);
    this.button.setLocation(2 + button_size, 2 + button_size, 2 + 3 * button_size, 2 + 3 * button_size);
    if (this.button.opened) {
      this.button.moveButton(-2 * button_size, 0);
    }
  }

  @Override
  void update(int millis) {
    if (button.opened) {
      p.imageMode(PConstants.CORNER);
      PImage open_img = p.global.images.getImage("features/washer_opened.png");
      PImage closed_img = p.global.images.getImage("features/washer_closed.png");
      double right_side = 0.5 * this.display_height * (1 + closed_img.width / closed_img.height);
      double display_w = this.display_width * open_img.width / open_img.height;
      p.image(open_img, right_side - display_w, 0, display_w, this.display_height);
    }
    super.update(millis, false);
    if (!button.opened) {
      p.imageMode(PConstants.CENTER);
      PImage closed_img = p.global.images.getImage("features/washer_closed.png");
      double display_w = this.display_height * closed_img.width / closed_img.height;
      p.image(closed_img, 0.5 * this.display_width, 0.5 * this.display_height, display_w, this.display_height);
    }
    this.button.update(millis);
  }

  @Override
  void mouseMove(float mX, float mY) {
    super.mouseMove(mX, mY);
    this.button.mouseMove(mX, mY);
  }

  @Override
  void mousePress() {
    super.mousePress();
    this.button.mousePress();
  }

  @Override
  void mouseRelease(float mX, float mY) {
    super.mouseRelease(mX, mY);
    this.button.mouseRelease(mX, mY);
  }
}


class DryerInventory extends Inventory {
  class DryerButton extends RectangleButton {
    private LNZ p;
    protected boolean opened = false;
    DryerButton(LNZ sketch) {
      super(sketch, 0, 0, 0, 0);
      this.p = sketch;
      this.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0), DImg.ccolor(120, 30), DImg.ccolor(120, 30), DImg.ccolor(1, 0));
      this.noStroke();
      this.force_left_button = false;
    }

    public void hover() {}
    public void click() {
      if (this.opened) {
        this.opened = false;
        DryerInventory.this.slots.get(5).deactivated = true;
        DryerInventory.this.slots.get(6).deactivated = true;
        DryerInventory.this.slots.get(9).deactivated = true;
        DryerInventory.this.slots.get(10).deactivated = true;
        this.moveButton(-2 * DryerInventory.this.button_size, 0);
        p.global.sounds.trigger_environment("features/dryer_close");
      }
      else {
        this.opened = true;
        DryerInventory.this.slots.get(5).deactivated = false;
        DryerInventory.this.slots.get(6).deactivated = false;
        DryerInventory.this.slots.get(9).deactivated = false;
        DryerInventory.this.slots.get(10).deactivated = false;
        this.moveButton(2 * DryerInventory.this.button_size, 0);
        p.global.sounds.trigger_environment("features/dryer_open");
      }
      this.hovered = false;
      this.clicked = false;
    }
    public void dehover() {}
    public void release() {}
  }

  protected DryerButton button;

  DryerInventory(LNZ sketch) {
    super(sketch, 4, 4, true);
    this.button = new DryerButton(sketch);
    this.deactivateSlots();
    this.setButtonSize(this.button_size);
  }

  @Override
  void setButtonSize(double button_size) {
    super.setButtonSize(button_size);
    this.button.setLocation(2 + button_size, 2 + button_size, 2 + 3 * button_size, 2 + 3 * button_size);
    if (this.button.opened) {
      this.button.moveButton(2 * button_size, 0);
    }
  }

  @Override
  void update(int millis) {
    PImage closed_img = p.global.images.getImage("features/dryer_closed.png");
    double display_w = this.display_height * closed_img.width / closed_img.height;
    if (button.opened) {
      p.imageMode(PConstants.CORNER);
      PImage open_img = p.global.images.getImage("features/dryer_opened.png");
      double left_side = 0.5 * (this.display_width - display_w);
      double open_display_w = this.display_width * open_img.width / open_img.height;
      p.image(open_img, left_side, 0, open_display_w, this.display_height);
    }
    super.update(millis, false);
    if (!button.opened) {
      p.imageMode(PConstants.CENTER);
      p.image(closed_img, 0.5 * this.display_width, 0.5 * this.display_height, display_w, this.display_height);
    }
    this.button.update(millis);
  }

  @Override
  void mouseMove(float mX, float mY) {
    super.mouseMove(mX, mY);
    this.button.mouseMove(mX, mY);
  }

  @Override
  void mousePress() {
    super.mousePress();
    this.button.mousePress();
  }

  @Override
  void mouseRelease(float mX, float mY) {
    super.mouseRelease(mX, mY);
    this.button.mouseRelease(mX, mY);
  }
}


class MicrowaveInventory extends Inventory {
  class MicrowaveButton extends RectangleButton {
    private LNZ p;
    protected boolean opened = false;
    MicrowaveButton(LNZ sketch) {
      super(sketch, 0, 0, 0, 0);
      this.p = sketch;
      this.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
        DImg.ccolor(120, 30), DImg.ccolor(120, 30), DImg.ccolor(1, 0));
      this.noStroke();
      this.force_left_button = false;
    }

    public void hover() {}
    public void click() {
      if (this.opened) {
        this.opened = false;
        MicrowaveInventory.this.slots.get(4).deactivated = true;
        this.setXLocation(-MicrowaveInventory.this.button_size + MicrowaveInventory.
          this.image_offset, 2 + 2 * MicrowaveInventory.this.button_size +
          MicrowaveInventory.this.image_offset);
        p.global.sounds.trigger_environment("features/microwave_close");
      }
      else {
        this.opened = true;
        MicrowaveInventory.this.slots.get(4).deactivated = false;
        this.setXLocation(-1.5 * MicrowaveInventory.this.button_size +
          MicrowaveInventory.this.image_offset, -0.5 * MicrowaveInventory.
          this.button_size + MicrowaveInventory.this.image_offset);
        p.global.sounds.trigger_environment("features/microwave_open");
      }
      this.hovered = false;
      this.clicked = false;
    }
    public void dehover() {}
    public void release() {}
  }

  protected MicrowaveButton button;
  protected double image_offset = 0;

  MicrowaveInventory(LNZ sketch) {
    super(sketch, 3, 3, true);
    this.button = new MicrowaveButton(sketch);
    this.deactivateSlots();
    this.setButtonSize(this.button_size);
  }

  @Override
  void setButtonSize(double button_size) {
    super.setButtonSize(button_size);
    this.image_offset = 0.5 * button_size;
    this.button.setLocation(-button_size + this.image_offset, 2, 2 + 2 *
      button_size + this.image_offset, 2 + 3 * button_size);
    if (this.button.opened) {
      this.button.setLocation(-1.5 * button_size + this.image_offset, 2, -0.5 *
        button_size + this.image_offset, 2 + 3 * button_size);
    }
  }

  @Override
  void update(int millis) {
    PImage opened_img = p.global.images.getImage("features/microwave_opened.png");
    PImage closed_img = p.global.images.getImage("features/microwave_closed.png");
    double closed_display_w = this.display_height * closed_img.width / closed_img.height;
    double opened_display_w = closed_display_w * opened_img.width / closed_img.width;
    double right_side = 0.5 * (this.display_width + closed_display_w);
    if (button.opened) {
      p.imageMode(PConstants.CORNERS);
      double display_h = this.display_height * opened_img.height / closed_img.height;
      p.image(opened_img, right_side - opened_display_w + this.image_offset, 0,
        right_side + this.image_offset, display_h);
    }
    super.update(millis, false);
    if (!button.opened) {
      p.imageMode(PConstants.CORNERS);
      p.image(closed_img, right_side - closed_display_w + this.image_offset, 0,
        right_side + this.image_offset, this.display_height);
    }
    this.button.update(millis);
  }

  @Override
  void mouseMove(float mX, float mY) {
    super.mouseMove(mX, mY);
    this.button.mouseMove(mX, mY);
  }

  @Override
  void mousePress() {
    super.mousePress();
    this.button.mousePress();
  }

  @Override
  void mouseRelease(float mX, float mY) {
    super.mouseRelease(mX, mY);
    this.button.mouseRelease(mX, mY);
  }
}


class GarbageInventory extends Inventory {
  protected int drop_time = 100;
  GarbageInventory(LNZ sketch) {
    super(sketch, 4, 1, true);
  }

  @Override
  void update(int timeElapsed) {
    super.update(timeElapsed);
    this.drop_time -= timeElapsed;
    if (this.drop_time < 0) {
      this.drop_time += 100;
      this.makeItemsFall();
    }
  }

  @Override
  void mouseMove(float mX, float mY) {
    super.mouseMove(mX, mY);
    this.dehoverBottomItems();
  }

  @Override
  void mouseRelease(float mX, float mY) {
    super.mouseRelease(mX, mY);
    this.dehoverBottomItems();
  }

  void makeItemsFall() {
    for (int i = 0; i < this.slots.size() - 1; i++) {
      if (this.slots.get(i).item != null && this.slots.get(i+1).item == null) {
        this.slots.get(i+1).item = this.slots.get(i).item;
        this.slots.get(i).item = null;
        i++;
      }
    }
  }

  void dehoverBottomItems() {
    boolean found_item = false;
    for (int i = 0; i < this.slots.size(); i++) {
      if (found_item) {
        this.slots.get(i).button.hovered = false;
      }
      else if (this.slots.get(i).item != null) {
        found_item = true;
      }
    }
  }
}


class RecycleInventory extends GarbageInventory {
  RecycleInventory(LNZ sketch) {
    super(sketch);
  }
}


class CrateInventory extends Inventory {
  CrateInventory(LNZ sketch) {
    super(sketch, 2, 2, true);
  }
}


class CardboardBoxInventory extends Inventory {
  CardboardBoxInventory(LNZ sketch) {
    super(sketch, 2, 2, true);
  }
}


class WoodenBoxInventory extends Inventory {
  WoodenBoxInventory(LNZ sketch) {
    super(sketch, 2, 2, true);
  }
}


class WoodenCrateInventory extends Inventory {
  WoodenCrateInventory(LNZ sketch) {
    super(sketch, 3, 3, true);
  }
}


class WoodenChestInventory extends Inventory {
  WoodenChestInventory(LNZ sketch) {
    super(sketch, 3, 8, true);
  }
}


class LargeWoodenChestInventory extends Inventory {
  LargeWoodenChestInventory(LNZ sketch) {
    super(sketch, 4, 12, true);
  }
}


class WorkbenchInventory extends Inventory {
  class CraftButton extends RectangleButton {
    protected int craft_timer = 0;

    CraftButton(LNZ sketch) {
      super(sketch, 0, 0, 0, 0);
      this.disabled = true;
      this.show_message = true;
      this.force_left_button = false;
      this.message = "Craft";
      this.use_time_elapsed = true;
      this.noStroke();
      this.setColors(DImg.ccolor(170, 170), DImg.ccolor(1, 0), DImg.ccolor(200, 100), DImg.ccolor(200,
        200), DImg.ccolor(0));
    }

    @Override
    public void update(int timeElapsed) {
      super.update(timeElapsed);
      if (this.clicked && p.mouseButton == PConstants.RIGHT) {
        this.craft_timer -= timeElapsed;
        if (this.craft_timer < 0) {
          this.craft_timer = LNZ.hero_multicraftTimer;
          this.release();
        }
      }
    }

    public void dehover() {}
    public void hover() {}
    public void click() {
      this.craft_timer = LNZ.hero_multicraftTimer;
    }
    public void release() {
      if (!this.hovered) {
        return;
      }
      if (WorkbenchInventory.this.craftable_item == null) {
        return;
      }
      WorkbenchInventory.this.craft();
    }
  }


  class ToolsButton extends RectangleButton {
    class ToolsButtonTextBox extends TextBox {
      ToolsButtonTextBox(LNZ sketch) {
        super(sketch, 0, 0, 0, 0);
        this.setTitleSize(19);
        this.setTextSize(17);
        this.color_background = sketch.global.color_nameDisplayed_background;
        this.color_header = sketch.global.color_nameDisplayed_background;
        this.color_stroke = DImg.ccolor(1, 0);
        this.color_text = DImg.ccolor(255);
        this.color_title = DImg.ccolor(255);
        this.scrollbar.setButtonColors(DImg.ccolor(170),
          DImg.adjustColorBrightness(sketch.global.color_nameDisplayed_background, 1.1),
          DImg.adjustColorBrightness(sketch.global.color_nameDisplayed_background, 1.2),
          DImg.adjustColorBrightness(sketch.global.color_nameDisplayed_background, 0.95), DImg.ccolor(0));
        this.scrollbar.button_upspace.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
          DImg.ccolor(1, 0), DImg.ccolor(0), DImg.ccolor(0));
        this.scrollbar.button_downspace.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
          DImg.ccolor(1, 0), DImg.ccolor(0), DImg.ccolor(0));
        this.useElapsedTime();
      }
    }

    private LNZ p;

    protected ToolsButtonTextBox description;

    ToolsButton(LNZ sketch) {
      super(sketch, 0, 0, 0, 0);
      this.p = sketch;
      this.description = new ToolsButtonTextBox(sketch);
      this.show_message = true;
      this.force_left_button = false;
      this.message = "Tools";
      this.use_time_elapsed = true;
      this.noStroke();
      this.setColors(DImg.ccolor(170, 170), DImg.ccolor(1, 0),
        DImg.ccolor(200, 100), DImg.ccolor(200, 200), DImg.ccolor(0));
    }

    public void update(int time_elapsed) {
      super.update(time_elapsed);
      if (this.hovered) {
        String message_text = "";
        if (WorkbenchInventory.this.slots.get(4).item != null) {
          message_text += WorkbenchInventory.this.slots.get(4).item.displayName() + " Will Add:";
          for (Map.Entry<ToolCode, Integer> entry : ToolCode.toolCodesFrom(WorkbenchInventory.this.slots.get(4).item).entrySet()) {
            message_text += "\n - " + entry.getKey().displayName() + " (" + entry.getValue() + ")";
          }
          message_text += "\n\n";
        }
        if (WorkbenchInventory.this.crafting_recipe != null) {
          message_text += "Tools Need:";
          for (ToolCode code : WorkbenchInventory.this.crafting_recipe.tools) {
            message_text += "\n - " + code.displayName();
          }
          message_text += "\n\n";
        }
        message_text += "Tools Have:";
        for (Map.Entry<ToolCode, Integer> entry : WorkbenchInventory.this.currentTools().entrySet()) {
          message_text += "\n - " + entry.getKey().displayName() + " (" + entry.getValue() + ")";
        }
        String title_text = "Workbench";
        if (WorkbenchInventory.this.craftable_item != null) {
          title_text = WorkbenchInventory.this.craftable_item.displayName();
        }
        p.textSize(this.description.text_size);
        double description_width = Math.max(LNZ.feature_workbenchMinimumToolsButtonWidth, p.textWidth(title_text) + 2);
        this.description.setXLocation(this.xf - description_width, this.xf);
        double description_height = p.textAscent() + p.textDescent() + 6;
        this.description.setTitleText(title_text);
        this.description.setText(message_text);
        description_height += (2 + this.description.text_lines.size()) *
          (p.textAscent() + p.textDescent() + this.description.text_leading);
        this.description.setYLocation(this.yi - description_height, this.yi);
        this.description.update(time_elapsed);
      }
    }

    public void dehover() {
      this.message = "Tools";
    }
    public void hover() {
      if (WorkbenchInventory.this.slots.get(4).item != null) {
        this.message = "Add\nTool";
      }
    }
    public void click() {}
    public void release() {
      if (!this.hovered) {
        return;
      }
      Item i = WorkbenchInventory.this.slots.get(4).item;
      if (i == null || i.remove) {
        return;
      }
      if (ToolCode.toolCodesFrom(i).size() == 0) {
        return; // not a tool
      }
      WorkbenchInventory.this.f.items.add(new Item(p, i));
      WorkbenchInventory.this.slots.get(4).item = null;
      i.remove = true;
    }
  }


  private int curr_crafting_hash_code = 0;
  private Item craftable_item = null;
  private CraftingRecipe crafting_recipe = null;
  private boolean craftable_item_hovered = false;
  protected float last_mX = 0;
  protected float last_mY = 0;
  private CraftButton craft;
  private ToolsButton tools;
  private Feature f;

  WorkbenchInventory(LNZ sketch, Feature f) {
    super(sketch, 3, 6, true);
    this.craft = new CraftButton(sketch);
    this.tools = new ToolsButton(sketch);
    this.f = f;
    this.deactivateSlots();
    this.slots.get(0).deactivated = false;
    this.slots.get(1).deactivated = false;
    this.slots.get(2).deactivated = false;
    this.slots.get(4).deactivated = false;
    this.slots.get(6).deactivated = false;
    this.slots.get(7).deactivated = false;
    this.slots.get(8).deactivated = false;
    this.slots.get(12).deactivated = false;
    this.slots.get(13).deactivated = false;
    this.slots.get(14).deactivated = false;
  }


  HashMap<ToolCode, Integer> currentTools() {
    return ToolCode.toolCodesFrom(this.currentToolItems().toArray(new Item[0]));
  }

  List<Item> currentToolItems() {
    if (this.f.items == null) {
      return new ArrayList<Item>();
    }
    return this.f.items;
  }


  void craft() {
    if (this.craftable_item == null) {
      return;
    }
    if (this.slots.get(11).item != null && (this.slots.get(11).item.ID !=
      this.craftable_item.ID || this.slots.get(11).item.maxStack() - this.
      slots.get(11).item.stack < this.craftable_item.stack)) {
      return;
    }
    if (!this.crafting_recipe.hasTools(this.currentTools())) {
      return;
    }
    this.crafting_recipe.useTools(p, this.currentToolItems());
    this.slots.get(0).removeStack();
    this.slots.get(1).removeStack();
    this.slots.get(2).removeStack();
    this.slots.get(6).removeStack();
    this.slots.get(7).removeStack();
    this.slots.get(8).removeStack();
    this.slots.get(12).removeStack();
    this.slots.get(13).removeStack();
    this.slots.get(14).removeStack();
    if (this.slots.get(11).item == null) {
      this.slots.get(11).item = new Item(p, this.craftable_item);
    }
    else {
      this.slots.get(11).item.addStack(this.craftable_item.stack);
    }
    this.slots.get(11).deactivated = false;
    this.craftable_item = null;
  }


  @Override
  void setButtonSize(double button_size) {
    super.setButtonSize(button_size);
    this.slots.get(4).button.moveButton(0.5 * button_size, 0);
    this.craft.setLocation(2 + 3.5 * button_size, 2 + 2.1 * button_size,
      2 + 5 * button_size, 2 + 2.9 * button_size);
    this.craft.text_size = button_size * 0.35;
    this.tools.setLocation(2 + 3.5 * button_size, 2, 2 + 4.5 * button_size, 2 + button_size);
    this.tools.text_size = button_size * 0.35;
  }

  @Override
  void update(int timeElapsed) {
    if (!this.slots.get(11).deactivated && this.slots.get(11).item == null) {
      this.slots.get(11).deactivated = true;
      this.slots.get(11).button.hovered = false;
      this.slots.get(11).button.clicked = false;
    }
    super.update(timeElapsed);
    this.curr_crafting_hash_code = this.getCraftingHashCode();
    p.imageMode(PConstants.CORNER);
    if (this.slots.get(4).item == null) {
      p.image(p.global.images.getImage("icons/tool.png"), 2 + 4.5 * this.button_size,
        2, this.button_size, this.button_size);
    }
    if (p.global.crafting_recipes.containsKey(this.curr_crafting_hash_code)) {
      this.crafting_recipe = p.global.crafting_recipes.get(this.curr_crafting_hash_code);
      this.craftable_item = new Item(p, this.crafting_recipe.output);
      this.craftable_item.stack = this.crafting_recipe.amount;
      this.craft.disabled = !this.crafting_recipe.hasTools(this.currentTools());
      boolean draw_craftable_item = true;
      if (this.slots.get(11).item != null && (this.slots.get(11).item.ID !=
        this.craftable_item.ID || this.slots.get(11).item.maxStack() - this.
        slots.get(11).item.stack < this.craftable_item.stack)) {
        this.craft.disabled = true;
        draw_craftable_item = false;
      }
      if (this.craft.disabled) {
        p.image(p.global.images.getImage("icons/crafting_arrow_red.png"), 2 + 3.5 * this.button_size,
          2 + 1 * this.button_size, this.button_size, this.button_size);
      }
      else {
        p.image(p.global.images.getImage("icons/crafting_arrow_green.png"), 2 + 3.5 * this.button_size,
          2 + 1 * this.button_size, this.button_size, this.button_size);
      }
      if (draw_craftable_item) {
        p.image(this.craftable_item.getImage(), 2 + 5 * this.button_size,
          2 + 1 * this.button_size, this.button_size, this.button_size);
        if (this.craftable_item.stack > 1) {
          p.fill(255);
          p.textSize(14);
          p.textAlign(PConstants.RIGHT, PConstants.BOTTOM);
          p.text(Integer.toString(this.craftable_item.stack), 6 * this.button_size, 2 * this.button_size);
        }
        if (this.craftable_item_hovered) {
          p.textSize(20);
          float rect_height = p.textAscent() + p.textDescent() + 2;
          float rect_width = p.textWidth(this.craftable_item.displayName()) + 2;
          p.rectMode(PConstants.CORNER);
          p.fill(p.global.color_nameDisplayed_background);
          p.stroke(1, 0);
          p.strokeWeight(0.1);
          p.rect(this.last_mX - rect_width - 1, this.last_mY - rect_height - 1, rect_width, rect_height);
          p.fill(255);
          p.textAlign(PConstants.LEFT, PConstants.TOP);
          p.text(this.craftable_item.displayName(), this.last_mX - rect_width - 1, this.last_mY - rect_height - 1);
        }
      }
    }
    else {
      this.craftable_item = null;
      this.crafting_recipe = null;
      this.craft.disable();
      p.image(p.global.images.getImage("icons/crafting_arrow.png"), 2 + 3.5 * this.button_size,
        2 + 1 * this.button_size, this.button_size, this.button_size);
    }
    this.craft.update(timeElapsed);
    this.tools.update(timeElapsed);
  }

  @Override
  void mouseMove(float mX, float mY) {
    this.last_mX = mX;
    this.last_mY = mY;
    super.mouseMove(mX, mY);
    this.craft.mouseMove(mX, mY);
    this.tools.mouseMove(mX, mY);
    if (this.craftable_item == null) {
      this.craftable_item_hovered = false;
    }
    else {
      if (mX > 2 + 5 * this.button_size && mY > 2 + 1 * this.button_size &&
        mX < 2 + 6 * this.button_size && mY < 2 + 2 * this.button_size) {
        this.craftable_item_hovered = true;
      }
      else {
        this.craftable_item_hovered = false;
      }
    }
  }

  @Override
  void mousePress() {
    super.mousePress();
    this.craft.mousePress();
    this.tools.mousePress();
  }

  @Override
  void mouseRelease(float mX, float mY) {
    super.mouseRelease(mX, mY);
    this.craft.mouseRelease(mX, mY);
    this.tools.mouseRelease(mX, mY);
  }

  @Override
  int getCraftingHashCode() {
    int[][] item_grid = new int[3][3];
    if (this.slots.get(0).item != null) {
      item_grid[0][0] = this.slots.get(0).item.ID;
    }
    if (this.slots.get(1).item != null) {
      item_grid[0][1] = this.slots.get(1).item.ID;
    }
    if (this.slots.get(2).item != null) {
      item_grid[0][2] = this.slots.get(2).item.ID;
    }
    if (this.slots.get(6).item != null) {
      item_grid[1][0] = this.slots.get(6).item.ID;
    }
    if (this.slots.get(7).item != null) {
      item_grid[1][1] = this.slots.get(7).item.ID;
    }
    if (this.slots.get(8).item != null) {
      item_grid[1][2] = this.slots.get(8).item.ID;
    }
    if (this.slots.get(12).item != null) {
      item_grid[2][0] = this.slots.get(12).item.ID;
    }
    if (this.slots.get(13).item != null) {
      item_grid[2][1] = this.slots.get(13).item.ID;
    }
    if (this.slots.get(14).item != null) {
      item_grid[2][2] = this.slots.get(14).item.ID;
    }
    return Arrays.deepHashCode(CraftingRecipe.reduceItemGrid(p, item_grid));
  }
}


class EnderChestInventory extends Inventory {
  EnderChestInventory(LNZ sketch) {
    super(sketch, 3, 4, true);
  }
}


class SmallKeyringInventory extends Inventory {
  SmallKeyringInventory(LNZ sketch) {
    super(sketch, 3, 3, true);
    this.slots.get(4).deactivated = true;
  }

  @Override
  Item stash(Item i) {
    if (!i.key()) {
      return i;
    }
    return super.stash(i);
  }

  @Override
  Item placeAt(Item i, int index, boolean replace, boolean ignore_deactivate) {
    if (!i.key()) {
      return i;
    }
    return super.placeAt(i, index, replace, ignore_deactivate);
  }

  @Override
  void drawBackground() {
    super.drawBackground();
    p.imageMode(PConstants.CORNER);
    p.image(p.global.images.getImage("items/small_keyring.png"), 2, 2, 3 * this.button_size, 3 * this.button_size);
  }
}


class LargeKeyringInventory extends Inventory {
  LargeKeyringInventory(LNZ sketch) {
    super(sketch, 6, 6, true);
    this.slots.get(0).deactivated = true;
    this.slots.get(5).deactivated = true;
    this.slots.get(9).deactivated = true;
    this.slots.get(10).deactivated = true;
    this.slots.get(14).deactivated = true;
    this.slots.get(15).deactivated = true;
    this.slots.get(16).deactivated = true;
    this.slots.get(20).deactivated = true;
    this.slots.get(21).deactivated = true;
    this.slots.get(22).deactivated = true;
    this.slots.get(30).deactivated = true;
    this.slots.get(35).deactivated = true;
  }

  @Override
  Item stash(Item i) {
    if (!i.key()) {
      return i;
    }
    return super.stash(i);
  }

  @Override
  Item placeAt(Item i, int index, boolean replace, boolean ignore_deactivate) {
    if (!i.key()) {
      return i;
    }
    return super.placeAt(i, index, replace, ignore_deactivate);
  }

  @Override
  void drawBackground() {
    super.drawBackground();
    p.imageMode(PConstants.CORNER);
    p.image(p.global.images.getImage("items/large_keyring.png"), 2, 2, 6 * this.button_size, 6 * this.button_size);
  }
}

