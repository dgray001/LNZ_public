package Form;

import LNZApplet.LNZApplet;
import Misc.Misc;

public class IntegerFormField extends StringFormField {
  protected int min_value = 0;
  protected int max_value = 0;

  public IntegerFormField(LNZApplet sketch, String message) {
    this(sketch, message, "");
  }
  public IntegerFormField(LNZApplet sketch, String message, String hint) {
    this(sketch, message, hint, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1);
  }
  public IntegerFormField(LNZApplet sketch, String message, int min, int max) {
    this(sketch, message, "", min, max);
  }
  public IntegerFormField(LNZApplet sketch, String message, String hint, int min, int max) {
    super(sketch, message, hint);
    this.min_value = min;
    this.max_value = max;
  }

  int validateInt(int value) {
    if (this.min_value == this.max_value) {
      return value;
    }
    if (value < this.min_value) {
      value = this.min_value;
    }
    else if (value > this.max_value) {
      value = this.max_value;
    }
    return value;
  }

  @Override
  public String getValue() {
    int return_value = Misc.toInt(this.input.text);
    return_value = this.validateInt(return_value);
    return Integer.toString(return_value);
  }

  public void submit() {
    if (this.focused()) {
      return;
    }
    int value = this.validateInt(Misc.toInt(this.input.text));
    this.input.setText(Integer.toString(value));
  }
}