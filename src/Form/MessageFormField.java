package Form;

import processing.core.*;
import LNZApplet.LNZApplet;
import DImg.DImg;

public class MessageFormField extends FormField {
  protected String display_message; // can be different if truncated
  protected float default_text_size = 22;
  protected float minimum_text_size = 8;
  protected float text_size = 0;
  public int text_color = DImg.ccolor(0);
  public int text_align = PConstants.LEFT;
  protected double left_edge = 1;

  public MessageFormField(LNZApplet sketch, String message) {
    this(sketch, message, PConstants.LEFT);
  }
  public MessageFormField(LNZApplet sketch, String message, int text_align) {
    super(sketch, message);
    this.display_message = message;
    this.text_align = text_align;
  }

  public void setTextSize(float new_text_size) {
    this.setTextSize(new_text_size, false);
  }
  public void setTextSize(float new_text_size, boolean force) {
    this.default_text_size = new_text_size;
    if (force) {
      this.minimum_text_size = new_text_size;
    }
    this.updateWidthDependencies();
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
    double max_width = this.field_width - 2;
    this.text_size = this.default_text_size;
    p.textSize(this.text_size);
    this.display_message = this.message;
    while(p.textWidth(this.display_message) > max_width) {
      this.text_size -= 0.2;
      p.textSize(this.text_size);
      if (this.text_size < this.minimum_text_size) {
        this.text_size = this.minimum_text_size;
        p.textSize(this.text_size);
        String truncated_string = "";
        for (int i = 0 ; i < this.display_message.length(); i++) {
          char c = this.display_message.charAt(i);
          if (p.textWidth(truncated_string + c) <= max_width) {
            truncated_string += c;
          }
          else {
            this.display_message = truncated_string;
            break;
          }
        }
        break;
      }
    }
  }

  public double getHeight() {
    p.textSize(this.text_size);
    return p.textAscent() + p.textDescent() + 2;
  }

  public String getValue() {
    return this.message;
  }
  public void setValue(String newValue) {
    this.message = newValue;
    this.updateWidthDependencies();
  }

  public FormFieldSubmit updateField(int millis) {
    p.textSize(this.text_size);
    p.textAlign(this.text_align, PConstants.TOP);
    p.fill(this.text_color);
    switch(this.text_align) {
      case PConstants.RIGHT:
        p.text(this.display_message, this.field_width - 1, 1);
        break;
      case PConstants.CENTER:
        p.text(this.display_message, 0.5 * this.field_width, 1);
        break;
      case PConstants.LEFT:
      default:
        p.text(this.display_message, this.left_edge, 1);
        break;
    }
    return FormFieldSubmit.NONE;
  }

  public void mouseMoveField(float mX, float mY) {
  }

  public void mousePressField() {}
  public void mouseReleaseField(float mX, float mY) {}
  public void scrollField(int amount) {}
  public void keyPressField(int key, int keyCode) {}
  public void keyReleaseField(int key, int keyCode) {}
  public void submit() {}
}