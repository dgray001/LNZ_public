package Form;

import java.util.*;
import processing.core.*;
import LNZApplet.LNZApplet;
import Button.ToggleButton;
import DImg.DImg;
import Misc.Misc;

public class ToggleFormField extends MessageFormField {
  class FormFieldToggleButton extends ToggleButton {
    FormFieldToggleButton(LNZApplet sketch, PImage[] images) {
      super(sketch, images, 0, 0, 0, 0);
      this.use_time_elapsed = true;
      this.overshadow_colors = true;
      this.setColors(DImg.ccolor(170, 170), DImg.ccolor(1, 0), DImg.ccolor(100, 80), DImg.ccolor(200, 160), DImg.ccolor(0));
    }
    @Override
    public void toggle() {
      super.toggle();
      ToggleFormField.this.toggle();
    }
    public void hover() {
    }
    public void dehover() {
    }
  }

  protected FormFieldToggleButton toggle;
  protected ArrayList<String> messages = new ArrayList<String>();

  public ToggleFormField(LNZApplet sketch, ArrayList<ToggleFormFieldInput> message_to_images) {
    super(sketch, "");
    PImage[] imgs = new PImage[message_to_images.size()];
    for (int i = 0; i < message_to_images.size(); i++) {
      this.messages.add(message_to_images.get(i).message);
      imgs[i] = message_to_images.get(i).img;
    }
    this.toggle = new FormFieldToggleButton(sketch, imgs);
    this.toggle();
  }

  void toggle() {
    super.setValue(this.messages.get(this.toggle.toggle_index));
    this.updateWidthDependencies();
  }

  @Override
  public void updateWidthDependencies() {
    double temp_field_width = this.field_width;
    this.field_width = 0.75 * this.field_width;
    super.updateWidthDependencies();
    this.field_width = temp_field_width;
    p.textSize(this.text_size);
    double togglesize = Math.min(0.95 * this.getHeight(), this.field_width - p.textWidth(this.message) - 2);
    double yi = 0.5 * (this.getHeight() - togglesize);
    this.toggle.setLocation(1, yi, togglesize, yi + togglesize);
    this.left_edge = 2 + togglesize;
  }

  @Override
  public String getValue() {
    return Integer.toString(this.toggle.toggle_index);
  }
  @Override
  public void setValue(String newValue) {
    if (Misc.isInt(newValue)) {
      this.toggle.setToggle(Misc.toInt(newValue));
      this.toggle();
    }
  }

  @Override
  public FormFieldSubmit updateField(int millis) {
    this.toggle.update(millis);
    return super.updateField(millis);
  }

  @Override
  public void mouseMoveField(float mX, float mY) {
    this.toggle.mouseMove(mX, mY);
  }

  @Override
  public void mousePressField() {
    this.toggle.mousePress();
  }

  @Override
  public void mouseReleaseField(float mX, float mY) {
    this.toggle.mouseRelease(mX, mY);
  }
}