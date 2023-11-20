package Form;

import LNZApplet.LNZApplet;

public class SpacerFormField extends FormField {
  protected double spacer_height;

  public SpacerFormField(LNZApplet sketch, double spacer_height) {
    super(sketch, "");
    this.spacer_height = spacer_height;
  }

  public void enable() {}
  public void disable() {}
  public void updateWidthDependencies() {}

  public boolean focusable() {
    return false;
  }
  public void focus() {}
  public void defocus() {}
  public boolean focused() {
    return false;
  }

  public double getHeight() {
    return this.spacer_height;
  }

  public String getValue() {
    return this.message;
  }
  public void setValue(String new_value) {
    this.message = new_value;
  }

  public FormFieldSubmit updateField(int millis) {
    return FormFieldSubmit.NONE;
  }
  public void mouseMoveField(float mX, float mY) {}
  public void mousePressField() {}
  public void mouseReleaseField(float mX, float mY) {}
  public void scrollField(int amount) {}
  public void keyPressField(int key, int keyCode) {}
  public void keyReleaseField(int key, int keyCode) {}
  public void submit() {}
}