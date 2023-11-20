package Form;

import LNZApplet.LNZApplet;
import Button.RectangleButton;

public class SubmitCancelFormField extends FormField {
  public class SubmitCancelButton extends RectangleButton {
    protected boolean submit;

    SubmitCancelButton(LNZApplet sketch, float xi, float yi, float xf, float yf, boolean submit) {
      super(sketch, xi, yi, xf, yf);
      this.submit = submit;
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
        if (this.submit) {
          SubmitCancelFormField.this.submitted = true;
        }
        else {
          SubmitCancelFormField.this.canceled = true;
        }
      }
    }
  }

  public SubmitCancelButton button1;
  public SubmitCancelButton button2;
  protected boolean submitted = false;
  protected boolean canceled = false;
  protected float gapSize = 10;

  public SubmitCancelFormField(LNZApplet sketch, String message1, String message2) {
    super(sketch, message1);
    this.button1 = new SubmitCancelButton(sketch, 0, 0, 0, 30, true);
    this.button1.message = message1;
    this.button1.show_message = true;
    this.button2 = new SubmitCancelButton(sketch, 0, 0, 0, 30, false);
    this.button2.message = message2;
    this.button2.show_message = true;
  }

  public void setButtonHeight(float new_height) {
    if (new_height < 0) {
      new_height = 0;
    }
    this.button1.setYLocation(0, new_height);
    this.button2.setYLocation(0, new_height);
  }

  public void disable() {
    this.button1.disabled = true;
    this.button2.disabled = true;
  }
  public void enable() {
    this.button1.disabled = false;
    this.button2.disabled = false;
  }

  public boolean focusable() {
    if (this.button2.button_focused) {
      return false;
    }
    return true;
  }
  public void focus() {
    if (this.button1.button_focused) {
      this.button1.button_focused = false;
      this.button2.button_focused = true;
    }
    else {
      this.button1.button_focused = true;
      this.button2.button_focused = false;
    }
  }
  public void defocus() {
    this.button1.button_focused = false;
    this.button2.button_focused = false;
  }
  public boolean focused() {
    if (this.button1.button_focused || this.button2.button_focused) {
      return true;
    }
    return false;
  }

  public void updateWidthDependencies() {
    p.textSize(this.button1.text_size);
    double desiredWidth1 = p.textWidth(this.button1.message) + p.textWidth("  ");
    p.textSize(this.button2.text_size);
    double desiredWidth2 = p.textWidth(this.button2.message) + p.textWidth("  ");
    if (this.gapSize > this.field_width) {
      this.button1.setXLocation(0, 0);
      this.button2.setXLocation(0, 0);
    }
    else if (desiredWidth1 + this.gapSize + desiredWidth2 > this.field_width) {
      this.button1.setXLocation(0, 0.5 * (this.field_width - this.gapSize));
      this.button2.setXLocation(0.5 * (this.field_width + gapSize), this.field_width);
    }
    else if (2 * Math.max(desiredWidth1, desiredWidth2) + this.gapSize > this.field_width) {
      this.button1.setXLocation(0.5 * (this.field_width - this.gapSize) - desiredWidth1, 0.5 * (this.field_width - this.gapSize));
      this.button2.setXLocation(0.5 * (this.field_width + this.gapSize), 0.5 * (this.field_width + this.gapSize) + desiredWidth2);
    }
    else {
      this.button1.setXLocation(0.5 * (this.field_width - this.gapSize) -
        Math.max(desiredWidth1, desiredWidth2), 0.5 * (this.field_width - this.gapSize));
      this.button2.setXLocation(0.5 * (this.field_width + this.gapSize),
        0.5 * (this.field_width + this.gapSize) + Math.max(desiredWidth1, desiredWidth2));
    }
  }

  public double getHeight() {
    return Math.max(this.button1.yf - this.button1.yi, this.button2.yf - this.button2.yi);
  }

  public String getValue() {
    return this.message;
  }
  @Override
  public void setValue(String newValue) {
    this.message = newValue;
  }

  public FormFieldSubmit updateField(int millis) {
    this.button1.update(millis);
    this.button2.update(millis);
    if (this.submitted) {
      this.submitted = false;
      return FormFieldSubmit.SUBMIT;
    }
    else if (this.canceled) {
      this.canceled = false;
      return FormFieldSubmit.CANCEL;
    }
    return FormFieldSubmit.NONE;
  }

  public void mouseMoveField(float mX, float mY) {
    this.button1.mouseMove(mX, mY);
    this.button2.mouseMove(mX, mY);
  }

  public void mousePressField() {
    this.button1.mousePress();
    this.button2.mousePress();
  }

  public void mouseReleaseField(float mX, float mY) {
    this.button1.mouseRelease(mX, mY);
    this.button2.mouseRelease(mX, mY);
  }

  public void scrollField(int amount) {
  }

  public void keyPressField(int key, int keyCode) {
    this.button1.keyPress(key, keyCode);
    this.button2.keyPress(key, keyCode);
  }
  public void keyReleaseField(int key, int keyCode) {
    this.button1.keyRelease(key, keyCode);
    this.button2.keyRelease(key, keyCode);
  }
  public void submit() {}
}