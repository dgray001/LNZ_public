package Form;

import LNZApplet.LNZApplet;
import Misc.Misc;

public class ButtonsFormField extends SubmitCancelFormField {
  protected int last_button_pressed = -1;

  public ButtonsFormField(LNZApplet sketch, String message1, String message2) {
    super(sketch, message1, message2);
  }

  @Override
  public String getValue() {
    return Integer.toString(this.last_button_pressed);
  }
  @Override
  public void setValue(String newValue) {
    if (Misc.isInt(newValue)) {
      this.last_button_pressed = Misc.toInt(newValue);
    }
    else {
      this.last_button_pressed = -1;
    }
  }

  @Override
  public FormFieldSubmit updateField(int millis) {
    this.button1.update(millis);
    this.button2.update(millis);
    if (this.submitted) {
      this.submitted = false;
      this.last_button_pressed = 0;
      return FormFieldSubmit.BUTTON;
    }
    else if (this.canceled) {
      this.canceled = false;
      this.last_button_pressed = 1;
      return FormFieldSubmit.BUTTON;
    }
    this.last_button_pressed = -1;
    return FormFieldSubmit.NONE;
  }
}