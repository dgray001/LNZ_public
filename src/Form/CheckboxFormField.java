package Form;

import LNZApplet.LNZApplet;
import Button.CheckBox;
import Misc.Misc;

public class CheckboxFormField extends MessageFormField {
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

  public CheckBox checkbox;

  public CheckboxFormField(LNZApplet sketch, String message) {
    super(sketch, message);
    this.checkbox = new DefaultCheckBox(sketch);
  }

  @Override
  public void updateWidthDependencies() {
    double temp_field_width = this.field_width;
    this.field_width = 0.75 * this.field_width;
    super.updateWidthDependencies();
    this.field_width = temp_field_width;
    p.textSize(this.text_size);
    double checkboxsize = Math.min(0.8 * this.getHeight(), this.field_width - p.textWidth(this.message));
    double xi = p.textWidth(this.message);
    double yi = 0.5 * (this.getHeight() - checkboxsize);
    this.checkbox.setLocation(xi, yi, xi + checkboxsize, yi + checkboxsize);
  }

  @Override
  public String getValue() {
    return Boolean.toString(this.checkbox.checked);
  }
  @Override
  public void setValue(String newValue) {
    if (Misc.isBoolean(newValue)) {
      this.checkbox.checked = Misc.toBoolean(newValue);
    }
  }

  @Override
  public FormFieldSubmit updateField(int millis) {
    this.checkbox.update(millis);
    return super.updateField(millis);
  }

  @Override
  public void mouseMoveField(float mX, float mY) {
    this.checkbox.mouseMove(mX, mY);
  }

  @Override
  public void mousePressField() {
    this.checkbox.mousePress();
  }

  @Override
  public void mouseReleaseField(float mX, float mY) {
    this.checkbox.mouseRelease(mX, mY);
  }
}