package Form;

import LNZApplet.LNZApplet;
import Misc.Misc;

public class BooleanFormField extends StringFormField {
  public BooleanFormField(LNZApplet sketch, String message) {
    this(sketch, message, "");
  }
  public BooleanFormField(LNZApplet sketch, String message, String hint) {
    super(sketch, message, hint);
  }

  public void submit() {
    if (this.focused()) {
      return;
    }
    this.input.setText(Boolean.toString(Misc.toBoolean(this.input.text)));
  }
}