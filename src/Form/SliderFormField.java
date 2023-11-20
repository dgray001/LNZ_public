package Form;

import processing.core.*;
import LNZApplet.LNZApplet;
import Button.CheckBox;
import Element.Slider;
import Misc.Misc;

public class SliderFormField extends MessageFormField {
  class DefaultCheckBox extends CheckBox {
    DefaultCheckBox(LNZApplet sketch) {
      super(sketch, 0, 0, 0, 0);
    }
    public void hover() {
    }
    public void dehover() {
    }
    public void release() {
    }
  }

  public Slider slider;
  protected CheckBox checkbox = null;
  protected double max_slider_height = 30;
  public double threshhold = 0.2;

  public SliderFormField(LNZApplet sketch, String message, double max) {
    this(sketch, message, 0, max, -1);
  }
  public SliderFormField(LNZApplet sketch, String message, double min, double max) {
    this(sketch, message, min, max, -1);
  }
  public SliderFormField(LNZApplet sketch, String message, double min, double max, double step) {
    super(sketch, message);
    this.text_align = PConstants.RIGHT;
    this.slider = new Slider(sketch);
    this.slider.bounds(min, max, step);
    this.slider.setValue(min);
    this.slider.button.show_value = true;
  }

  public void addCheckbox(String message) {
    this.checkbox = new DefaultCheckBox(p);
    this.checkbox.message = message;
    this.updateWidthDependencies();
  }

  public void addLabel(String label, boolean round_label) {
    this.addLabel(label, round_label, this.slider.only_label_ends);
  }
  public void addLabel(String label, boolean round_label, boolean only_label_ends) {
    this.slider.show_label = true;
    this.slider.label = label;
    this.slider.round_label = round_label;
    this.slider.only_label_ends = only_label_ends;
  }

  @Override
  public void disable() {
    this.slider.disable();
    if (this.checkbox != null) {
      this.checkbox.checked = true;
    }
  }
  @Override
  public void enable() {
    this.slider.enable();
    if (this.checkbox != null) {
      this.checkbox.checked = false;
    }
  }

  @Override
  public boolean focusable() {
    if (this.slider.button.active) {
      return false;
    }
    return true;
  }
  @Override
  public void focus() {
    this.slider.button.active = true;
  }
  @Override
  public void defocus() {
    this.slider.button.active = false;
  }
  @Override
  public boolean focused() {
    return this.slider.button.active;
  }

  @Override
  public void updateWidthDependencies() {
    double temp_field_width = this.field_width;
    this.field_width = this.threshhold * this.field_width;
    super.updateWidthDependencies();
    double buffer_width = 0.02 * this.field_width;
    this.field_width = temp_field_width;
    double sliderheight = Math.min(this.getHeight(), this.max_slider_height);
    if (this.checkbox != null) {
      this.checkbox.text_size = 0.75f * this.text_size;
      p.textSize(this.checkbox.text_size);
      double checkboxsize = 0.8 * (p.textAscent() + p.textDescent() + 2);
      buffer_width += p.textWidth(this.checkbox.message) + 0.02 * this.field_width;
      double xi = this.threshhold * this.field_width + buffer_width;
      double yi = 0.5 * (this.getHeight() - checkboxsize);
      this.checkbox.setLocation(xi, yi, xi + checkboxsize, yi + checkboxsize);
      buffer_width += checkboxsize + 0.02 * this.field_width;
    }
    double xi = this.threshhold * this.field_width + buffer_width;
    double yi = 0.5 * (this.getHeight() - sliderheight);
    this.slider.setLocation(xi, yi, this.field_width, yi + sliderheight);
  }

  @Override
  public String getValue() {
    if (this.checkbox != null && this.checkbox.checked) {
      return Double.toString(this.slider.value) + ":disabled";
    }
    return Double.toString(this.slider.value);
  }
  @Override
  public void setValue(String newValue) {
    if (Misc.isDouble(newValue)) {
      this.slider.setValue(Misc.toDouble(newValue));
    }
  }

  @Override
  public FormFieldSubmit updateField(int millis) {
    this.slider.update(millis);
    double temp_field_width = this.field_width;
    this.field_width = this.threshhold * this.field_width;
    super.updateField(millis);
    this.field_width = temp_field_width;
    if (this.checkbox != null) {
      p.textSize(this.checkbox.text_size);
      p.fill(this.checkbox.color_text);
      p.textAlign(PConstants.RIGHT, PConstants.CENTER);
      p.text(this.checkbox.message, this.checkbox.xi - 1, this.checkbox.yCenter());
      this.checkbox.update(millis);
    }
    return FormFieldSubmit.NONE;
  }

  @Override
  public void mouseMoveField(float mX, float mY) {
    this.slider.mouseMove(mX, mY);
    if (this.checkbox != null) {
      this.checkbox.mouseMove(mX, mY);
    }
  }

  @Override
  public void mousePressField() {
    this.slider.mousePress();
    if (this.checkbox != null) {
      this.checkbox.mousePress();
      if (this.checkbox.checked) {
        this.disable();
      }
      else {
        this.enable();
      }
    }
  }

  @Override
  public void mouseReleaseField(float mX, float mY) {
    this.slider.mouseRelease(mX, mY);
    if (this.checkbox != null) {
      this.checkbox.mouseRelease(mX, mY);
    }
  }

  @Override
  public void scrollField(int amount) {
    this.slider.scroll(amount);
  }

  @Override
  public void keyPressField(int key, int keyCode) {
    this.slider.keyPress(key, keyCode);
  }
}