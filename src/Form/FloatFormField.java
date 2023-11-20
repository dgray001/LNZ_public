package Form;

import LNZApplet.LNZApplet;
import Misc.Misc;

public class FloatFormField extends StringFormField {
  protected double min_value = 0;
  protected double max_value = 0;

  public FloatFormField(LNZApplet sketch, String message) {
    this(sketch, message, "");
  }
  public FloatFormField(LNZApplet sketch, String message, String hint) {
    this(sketch, message, hint, 0, 0);
  }
  public FloatFormField(LNZApplet sketch, String message, double min, double max) {
    this(sketch, message, "", min, max);
  }
  public FloatFormField(LNZApplet sketch, String message, String hint, double min, double max) {
    super(sketch, message, hint);
    this.min_value = min;
    this.max_value = max;
  }

  @Override
  public String getValue() {
    double return_value = Misc.toDouble(this.input.text);
    if (return_value < this.min_value) {
      return_value = this.min_value;
    }
    else if (return_value > this.max_value) {
      return_value = this.max_value;
    }
    return Double.toString(return_value);
  }

  public void submit() {
    if (this.focused()) {
      return;
    }
    double value = Misc.toDouble(this.input.text);
    if (value > this.max_value) {
      value = this.max_value;
    }
    else if (value < this.min_value) {
      value = this.min_value;
    }
    this.input.setText(Double.toString(value));
  }
}