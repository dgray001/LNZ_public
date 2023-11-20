package Form;

import LNZApplet.LNZApplet;
import Button.InputBox;

public class StringFormField extends MessageFormField {
  public InputBox input;

  public StringFormField(LNZApplet sketch, String message) {
    this(sketch, message, "");
  }
  public StringFormField(LNZApplet sketch, String message, String hint) {
    super(sketch, message);
    this.input = new InputBox(sketch, 0, 0, 0, 0);
    if (hint != null) {
      this.input.hint_text = hint;
    }
  }

  @Override
  public boolean focusable() {
    if (this.input.typing) {
      return false;
    }
    return true;
  }
  @Override
  public void focus() {
    this.input.typing = true;
  }
  @Override
  public void defocus() {
    this.input.typing = false;
  }
  @Override
  public boolean focused() {
    return this.input.typing;
  }

  public void updateWidthDependencies() {
    double temp_field_width = this.field_width;
    this.field_width = 0.5 * this.field_width;
    super.updateWidthDependencies();
    this.field_width = temp_field_width;
    this.input.setTextSize(this.text_size);
    p.textSize(this.text_size);
    this.input.setLocation(p.textWidth(this.message), 0, this.field_width, p.textAscent() + p.textDescent() + 2);
  }

  @Override
  public String getValue() {
    return this.input.text;
  }
  @Override
  public void setValue(String newValue) {
    this.input.setText(newValue);
  }

  @Override
  public FormFieldSubmit updateField(int millis) {
    this.input.update(millis);
    return super.updateField(millis);
  }

  @Override
  public void mouseMoveField(float mX, float mY) {
    this.input.mouseMove(mX, mY);
  }

  @Override
  public void mousePressField() {
    this.input.mousePress();
  }

  @Override
  public void mouseReleaseField(float mX, float mY) {
    this.input.mouseRelease(mX, mY);
  }

  @Override
  public void keyPressField(int key, int keyCode) {
    this.input.keyPress(key, keyCode);
  }
  @Override
  public void keyReleaseField(int key, int keyCode) {
    this.input.keyRelease(key, keyCode);
  }
}