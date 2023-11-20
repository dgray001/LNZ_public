package Form;

import LNZApplet.LNZApplet;
import Button.RectangleButton;
import Misc.Misc;

public class SubmitFormField extends FormField {
  class SubmitButton extends RectangleButton {
    SubmitButton(LNZApplet sketch, float xi, float yi, float xf, float yf) {
      super(sketch, xi, yi, xf, yf);
      this.roundness = 0;
      this.raised_body = true;
      this.raised_border = true;
      this.adjust_for_text_descent = true;
    }
    public void hover() {
    }
    public void dehover() {
    }
    public void click() {
    }
    public void release() {
      if (this.hovered || this.button_focused) {
        SubmitFormField.this.submitted = true;
      }
    }
  }

  public RectangleButton button;
  protected boolean submitted = false;
  protected boolean submit_button = true;
  protected boolean extend_width = false;
  public boolean align_left = false;

  public SubmitFormField(LNZApplet sketch, String message) {
    this(sketch, message, true);
  }
  public SubmitFormField(LNZApplet sketch, String message, boolean submit_button) {
    super(sketch, message);
    this.button = new SubmitButton(sketch, 0, 0, 0, 30);
    this.button.message = message;
    this.button.show_message = true;
    this.submit_button = submit_button;
  }

  public void setButtonHeight(double new_height) {
    if (new_height < 0) {
      new_height = 0;
    }
    this.button.setYLocation(0, new_height);
  }

  public void disable() {
    this.button.disabled = true;
  }
  public void enable() {
    this.button.disabled = false;
  }

  public boolean focusable() {
    if (this.button.button_focused) {
      return false;
    }
    return true;
  }
  public void focus() {
    this.button.button_focused = true;
  }
  public void defocus() {
    this.button.button_focused = false;
  }
  public boolean focused() {
    return this.button.button_focused;
  }

  public void updateWidthDependencies() {
    p.textSize(this.button.text_size);
    double desiredWidth = p.textWidth(this.button.message) + p.textWidth("  ");
    if (desiredWidth > this.field_width || this.extend_width) {
      this.button.setXLocation(0, this.field_width);
    }
    else if (this.align_left) {
      this.button.setXLocation(4, desiredWidth + 4);
    }
    else {
      this.button.setXLocation(0.5 * (this.field_width - desiredWidth),
        0.5 * (this.field_width + desiredWidth));
    }
  }

  public double getHeight() {
    return this.button.yf - this.button.yi;
  }

  public String getValue() {
    return this.message;
  }
  @Override
  public void setValue(String newValue) {
    if (Misc.isBoolean(newValue)) {
      this.submit_button = Misc.toBoolean(newValue);
    }
  }

  public FormFieldSubmit updateField(int millis) {
    this.button.update(millis);
    if (this.submitted) {
      this.submitted = false;
      if (this.submit_button) {
        return FormFieldSubmit.SUBMIT;
      }
      else {
        return FormFieldSubmit.CANCEL;
      }
    }
    return FormFieldSubmit.NONE;
  }

  public void mouseMoveField(float mX, float mY) {
    this.button.mouseMove(mX, mY);
  }

  public void mousePressField() {
    this.button.mousePress();
  }

  public void mouseReleaseField(float mX, float mY) {
    this.button.mouseRelease(mX, mY);
  }

  public void scrollField(int amount) {
  }

  public void keyPressField(int key, int keyCode) {
    this.button.keyPress(key, keyCode);
  }
  public void keyReleaseField(int key, int keyCode) {
    this.button.keyRelease(key, keyCode);
  }
  public void submit() {}
}