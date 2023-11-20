package Form;

import LNZApplet.LNZApplet;
import Element.TextBox;
import DImg.DImg;

public class TextBoxFormField extends FormField {
  public TextBox textbox;

  public TextBoxFormField(LNZApplet sketch, String message, double box_height) {
    super(sketch, message);
    this.textbox = new TextBox(sketch);
    this.textbox.setText(message);
    this.textbox.setLocation(0, 0, 0, box_height);
    this.textbox.color_background = DImg.ccolor(255, 0);
    this.textbox.color_header = DImg.ccolor(255, 0);
    this.textbox.color_stroke = DImg.ccolor(255, 0);
  }

  public void enable() {}
  public void disable() {}

  public boolean focusable() {
    return false;
  }
  public void focus() {}
  public void defocus() {}
  public boolean focused() {
    return false;
  }

  public void updateWidthDependencies() {
    this.textbox.setLocation(0, 0, this.field_width, this.getHeight());
  }
  public double getHeight() {
    return this.textbox.yf - this.textbox.yi;
  }

  public String getValue() {
    return this.textbox.text_ref;
  }
  public void setValue(String newValue) {
    this.textbox.setText(newValue);
  }

  public FormFieldSubmit updateField(int millis) {
    this.textbox.update(millis);
    return FormFieldSubmit.NONE;
  }

  public void mouseMoveField(float mX, float mY) {
    this.textbox.mouseMove(mX, mY);
  }

  public void mousePressField() {
    this.textbox.mousePress();
  }

  public void mouseReleaseField(float mX, float mY) {
    this.textbox.mouseRelease(mX, mY);
  }

  public void scrollField(int amount) {
    this.textbox.scroll(amount);
  }

  public void keyPressField(int key, int keyCode) {
    this.textbox.keyPress(key, keyCode);
  }
  public void keyReleaseField(int key, int keyCode) {}
  public void submit() {}
}