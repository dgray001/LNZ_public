package Form;

import LNZApplet.LNZApplet;

public class ButtonFormField extends SubmitFormField {
  public ButtonFormField(LNZApplet sketch, String message) {
    super(sketch, message, true);
  }

  @Override
  public FormFieldSubmit updateField(int millis) {
    if (super.updateField(millis) != FormFieldSubmit.NONE) {
      return FormFieldSubmit.BUTTON;
    }
    return FormFieldSubmit.NONE;
  }
}