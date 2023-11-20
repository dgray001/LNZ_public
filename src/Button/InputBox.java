package Button;

import java.awt.event.KeyEvent;
import processing.core.*;
import LNZApplet.LNZApplet;
import DImg.DImg;

public class InputBox extends RectangleButton {
  public String text = "";
  public String hint_text = "";
  protected int hint_color = DImg.ccolor(80);
  public boolean typing = false;
  protected String display_text = "";

  protected int location_display = 0;
  protected int location_cursor = 0;

  protected double cursor_weight = 1;
  protected int cursor_blink_time = 450;
  protected int cursor_blink_timer = 0;
  protected boolean cursor_blinking = true;

  protected float lastMouseX = 0;

  public InputBox(LNZApplet sketch, double xi, double yi, double xf, double yf) {
    super(sketch, xi, yi, xf, yf);
    this.roundness = 0;
    this.setColors(DImg.ccolor(170), DImg.ccolor(220),
      DImg.ccolor(220), DImg.ccolor(255), DImg.ccolor(0));
  }

  public void refreshText() {
    this.setText(this.text);
  }
  public void setText(String text) {
    if (text == null) {
      text = "";
    }
    this.text = text;
    this.updateDisplayText();
    if (this.location_cursor > this.text.length()) {
      this.location_cursor = this.text.length();
    }
    if (this.location_cursor > this.location_display + this.display_text.length()) {
      this.location_display = this.location_cursor - this.display_text.length();
      this.updateDisplayText();
    }
  }

  public void setTextSize(float text_size) {
    this.text_size = text_size;
    this.refreshText();
  }

  @Override
  public void setLocation(double xi, double yi, double xf, double yf) {
    super.setLocation(xi, yi, xf, yf);
    this.updateDisplayText();
  }

  @Override
  public void stretchButton(double amount, int direction) {
    super.stretchButton(amount, direction);
    this.updateDisplayText();
  }

  public void updateDisplayText() {
    if (this.text == null) {
      this.text = "";
    }
    this.display_text = "";
    p.textSize(this.text_size);
    double maxWidth = this.xf - this.xi - 2 - p.textWidth(' ');
    boolean decreaseDisplayLocation = true;
    for (int i = this.location_display; i < this.text.length(); i++ ) {
      if (p.textWidth(this.display_text + this.text.charAt(i)) > maxWidth) {
        decreaseDisplayLocation = false;
        break;
      }
      this.display_text += this.text.charAt(i);
    }
    if (decreaseDisplayLocation && this.location_display <= this.text.length()) {
      while(this.location_display > 0 && p.textWidth(this.text.charAt(
        this.location_display - 1) + this.display_text) <= maxWidth) {
        this.location_display--;
        this.display_text = this.text.charAt(this.location_display) + this.display_text;
      }
    }
    // if say increased text size
    if (this.location_cursor - this.location_display > this.display_text.length()) {
      int dif = this.location_cursor - this.location_display - this.display_text.length();
      this.location_display += dif;
      int end_index = this.location_display + this.display_text.length();
      if (end_index > this.text.length()) {
        end_index = this.text.length();
      }
      if (this.location_display > this.text.length()) {
        this.location_display = this.text.length();
      }
      this.display_text = this.text.substring(this.location_display, end_index);
    }
  }

  void resetBlink() {
    this.cursor_blinking = true;
    this.cursor_blink_timer = 0;
  }

  @Override
  public int fillColor() {
    if (this.disabled) {
      return this.color_disabled;
    }
    else if (this.typing) {
      return this.color_click;
    }
    else {
      return this.color_default;
    }
  }

  @Override
  public void drawButton() {
    super.drawButton();
    p.textAlign(PConstants.LEFT, PConstants.TOP);
    if (this.text.equals("")) {
      p.textSize(this.text_size - 2);
      p.fill(this.hint_color);
      p.text(this.hint_text, this.xi + 2, this.yi + 1);
    }
    else {
      p.textSize(this.text_size);
      p.fill(this.color_text);
      p.text(this.display_text, this.xi + 2, this.yi + 1);
    }
    if (this.typing && this.cursor_blinking) {
      p.strokeWeight(this.cursor_weight);
      p.fill(this.color_stroke);
      double x_cursor = this.xi + 2 + p.textWidth(this.display_text.substring(
        0, this.location_cursor - this.location_display));
      p.line(x_cursor, this.yi + 2, x_cursor, this.yf - 2);
    }
  }

  @Override
  public void update(int millis) {
    int time_elapsed = millis - this.last_update_time;
    super.update(millis);
    if (this.typing) {
      this.cursor_blink_timer += time_elapsed;
      if (this.cursor_blink_timer > this.cursor_blink_time) {
        this.cursor_blink_timer -= this.cursor_blink_time;
        this.cursor_blinking = !this.cursor_blinking;
      }
    }
  }

  @Override
  public void mouseMove(float mX, float mY) {
    this.lastMouseX = mX;
    super.mouseMove(mX, mY);
  }

  public void dehover() {
  }

  public void hover() {
  }

  @Override
  public void mousePress() {
    this.typing = false;
    super.mousePress();
  }
  public void click() {
    this.typing = true;
    this.resetBlink();
    p.textSize(this.text_size);
    String display_text_copy = this.display_text;
    while (display_text_copy.length() > 0 && this.lastMouseX < this.xi + 2 + p.textWidth(display_text_copy)) {
      display_text_copy = display_text_copy.substring(0, display_text_copy.length() - 1);
    }
    this.location_cursor = location_display + display_text_copy.length();
  }

  public void release() {
  }

  public void keyPress(int key, int keyCode) {
    if (!this.typing) {
      return;
    }
    if (key == PConstants.CODED) {
      switch(keyCode) {
        case PConstants.LEFT:
          this.location_cursor--;
          if (this.location_cursor < 0) {
            this.location_cursor = 0;
          }
          else if (this.location_cursor < this.location_display) {
            this.location_display--;
            this.updateDisplayText();
          }
          break;
        case PConstants.RIGHT:
          this.location_cursor++;
          if (this.location_cursor > this.text.length()) {
            this.location_cursor = this.text.length();
          }
          else if (this.location_cursor > this.location_display + this.display_text.length()) {
            this.location_display++;
            this.updateDisplayText();
          }
          break;
        case KeyEvent.VK_HOME:
          this.location_cursor = 0;
          this.location_display = 0;
          this.updateDisplayText();
          break;
        case KeyEvent.VK_END:
          this.location_cursor = this.text.length();
          this.location_display = this.text.length();
          this.updateDisplayText();
          break;
        default:
          break;
      }
    }
    else {
      switch(key) {
        case PConstants.BACKSPACE:
          if (this.location_cursor > 0) {
            this.location_cursor--;
            if (this.location_cursor < this.location_display) {
              this.location_display--;
            }
            this.setText(this.text.substring(0, this.location_cursor) +
              this.text.substring(this.location_cursor + 1, this.text.length()));
          }
          break;
        case PConstants.TAB:
          break;
        case PConstants.ENTER:
        case PConstants.RETURN:
          break;
        case PConstants.ESC:
          this.typing = false;
          break;
        case PConstants.DELETE:
          break;
        default:
          this.location_cursor++;
          if (this.location_cursor > this.location_display + this.display_text.length()) {
            this.location_display++;
          }
          this.setText(this.text.substring(0, this.location_cursor - 1) +
            (char)key + this.text.substring(this.location_cursor - 1, this.text.length()));
          this.updateDisplayText();
          break;
      }
    }
    this.resetBlink();
  }

  void keyRelease() {
    if (!this.typing) {
      return;
    }
    this.resetBlink();
  }
}