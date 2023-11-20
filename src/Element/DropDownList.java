package Element;

import processing.core.*;
import LNZApplet.LNZApplet;

public class DropDownList extends ListTextBox {
  protected boolean active = false;
  public boolean show_highlight = false;
  public String hint_text = "";

  public DropDownList(LNZApplet sketch) {
    this(sketch, 0, 0, 0, 0);
  }
  public DropDownList(LNZApplet sketch, double xi, double yi, double xf, double yf) {
    super(sketch, xi, yi, xf, yf);
  }

  @Override
  public void update(int millis) {
    if (this.active) {
      super.update(millis);
    }
    else {
      p.textAlign(PConstants.LEFT, PConstants.TOP);
      p.textSize(this.text_size);
      float text_height = p.textAscent() + p.textDescent();
      p.rectMode(PConstants.CORNERS);
      p.fill(this.color_background);
      p.stroke(this.color_stroke);
      p.strokeWeight(1);
      p.rect(this.xi, this.yi, this.xf - 1 - this.scrollbar.bar_size, this.yi + 3 + text_height);
      if (this.line_clicked >= 0) {
        p.fill(this.color_text);
        p.text(this.text_lines.get(this.line_clicked), this.xi + 2, this.yi + 1);
      }
      else {
        p.fill(this.color_text, 150);
        p.text(this.hint_text, this.xi + 2, this.yi + 1);
      }
      if (this.show_highlight) {
        p.fill(this.highlight_color);
        p.strokeWeight(0.0001);
        p.stroke(this.highlight_color);
        p.rect(this.xi + 1, this.yi + 1, this.xf - 2 - this.scrollbar.bar_size, this.yi + 1 + text_height);
      }
    }
  }

  @Override
  public void mousePress() {
    if (this.active) {
      if (this.hovered) {
        int last_line_clicked = this.line_clicked;
        super.mousePress();
        this.line_clicked = last_line_clicked;
      }
      else {
        this.show_highlight = false;
        this.active = false;
      }
    }
    else {
      int last_line_clicked = this.line_clicked;
      super.mousePress();
      if (this.line_clicked == (int)this.scrollbar.value) {
        if (this.show_highlight) {
          this.active = true;
          this.line_clicked = last_line_clicked;
          this.jump_to_line(true);
        }
        else {
          this.line_clicked = last_line_clicked;
          this.show_highlight = true;
        }
      }
      else {
        this.line_clicked = last_line_clicked;
        this.show_highlight = false;
      }
    }
  }

  @Override
  public void mouseRelease(float mX, float mY) {
    int last_line_clicked = this.line_clicked;
    super.mouseRelease(mX, mY);
    this.line_clicked = last_line_clicked;
  }

  @Override
  public void keyPress(int key, int keyCode) {
    if (!this.show_highlight && !this.active) {
      return;
    }
    if (key == PConstants.CODED) {
      switch(keyCode) {
        case PConstants.UP:
          if (this.line_clicked > 0) {
            this.line_clicked--;
            this.jump_to_line(false);
          }
          break;
        case PConstants.DOWN:
          if (this.line_clicked < this.text_lines_ref.size() - 1) {
            this.line_clicked++;
            this.jump_to_line(false);
          }
          break;
        default:
          break;
      }
    }
    else {
      switch(key) {
        case PConstants.ENTER:
        case PConstants.RETURN:
          if (this.active) {
            this.doubleclick();
          }
          else {
            this.active = true;
            this.jump_to_line(true);
          }
          break;
        case PConstants.ESC:
          this.doubleclick();
          this.show_highlight = false;
          break;
        default:
          break;
      }
    }
  }

  public void click() {}

  public void doubleclick() {
    if (this.active) {
      this.active = false;
      this.show_highlight = true;
    }
  }
}