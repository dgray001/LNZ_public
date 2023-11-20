package Button;

import processing.core.*;
import LNZApplet.LNZApplet;
import DImg.DImg;

public abstract class Button {
	
	protected LNZApplet p;

  // state
  protected boolean hidden = false;
  public boolean disabled = false;
  public boolean hovered = false;
  public boolean clicked = false;
  public boolean button_focused = false;
  // colors
  public int color_disabled = DImg.ccolor(220, 180);
  public int color_default = DImg.ccolor(220);
  public int color_hover = DImg.ccolor(170);
  public int color_click = DImg.ccolor(120);
  public int color_text = DImg.ccolor(0);
  public int color_stroke = DImg.ccolor(0);
  public int color_focused = DImg.ccolor(170, 180);
  // config
  public String message = "";
  public boolean show_message = false;
  public double text_size = 14;
  protected boolean show_stroke = true;
  protected double stroke_weight = 0.5f;
  public boolean stay_dehovered = false;
  public boolean adjust_for_text_descent = false;
  public boolean hover_check_after_release = true;
  public boolean use_time_elapsed = false;
  public boolean force_left_button = true;
  public boolean underline_text = false;
  // timer
  protected int hold_timer = 0;
  protected int last_update_time = 0;

  public Button(LNZApplet sketch) {
    this.p = sketch;
  }

  public void disable() {
    this.disabled = true;
    this.hovered = false;
    this.clicked = false;
    this.button_focused = false;
  }

  public void setMessage(String new_message) {
    if (new_message != null) {
      this.message = new_message;
    }
  }

  public void setColors(int c_dis, int c_def, int c_hov, int c_cli, int c_tex) {
    this.color_disabled = c_dis;
    this.color_default = c_def;
    this.color_hover = c_hov;
    this.color_click = c_cli;
    this.color_text = c_tex;
  }

  public void setStroke(int c_str, double stroke_weight) {
    this.color_stroke = c_str;
    this.stroke_weight = stroke_weight;
    this.show_stroke = true;
  }
  public void noStroke() {
    this.show_stroke = false;
  }

  public int fillColor() {
    if (this.disabled) {
      return this.color_disabled;
    }
    else if (this.clicked) {
      return this.color_click;
    }
    else if (this.hovered) {
      return this.color_hover;
    }
    else {
      return this.color_default;
    }
  }

  void setFill() {
    p.fill(this.fillColor());
    p.stroke(this.color_stroke);
    if (this.show_stroke) {
      if (this.button_focused) {
        p.strokeWeight(2 * this.stroke_weight);
      }
      else {
        p.strokeWeight(this.stroke_weight);
      }
    }
    else {
      if (this.button_focused) {
        p.strokeWeight(0.8 * this.stroke_weight);
      }
      else {
        p.strokeWeight(0.0001);
        p.noStroke();
      }
    }
  }

  public void writeText() {
    if (!this.show_message || this.message == null) {
      return;
    }
    p.fill(this.color_text);
    p.textAlign(PConstants.CENTER, PConstants.CENTER);
    p.textSize(this.text_size);
    if (this.adjust_for_text_descent) {
      p.text(this.message, this.xCenter(), this.yCenter() - p.textDescent());
      if (this.underline_text) {
        double text_width = p.textWidth(this.message);
        double text_start_x = this.xCenter() - 0.5 * text_width;
        double text_height = p.textDescent() + p.textAscent();
        double text_end_y = this.yCenter() - p.textDescent() + 0.5 * text_height + this.stroke_weight;
        p.strokeWeight(this.stroke_weight);
        p.stroke(this.color_text);
        p.line(text_start_x, text_end_y, text_start_x + text_width, text_end_y);
      }
    }
    else {
      p.text(this.message, this.xCenter(), this.yCenter());
      if (this.underline_text) {
        double text_width = p.textWidth(this.message);
        double text_start_x = this.xCenter() - 0.5 * text_width;
        double text_height = p.textDescent() + p.textAscent();
        double text_end_y = this.yCenter() + 0.5 * text_height + this.stroke_weight;
        p.strokeWeight(this.stroke_weight);
        p.stroke(this.color_text);
        p.line(text_start_x, text_end_y, text_start_x + text_width, text_end_y);
      }
    }
  }

  public void stayDehovered() {
    this.stay_dehovered = true;
    this.hovered = false;
  }

  public void update(int millis) {
    if (!this.hidden) {
      drawButton();
      if (this.clicked) {
        if (this.use_time_elapsed) {
          this.hold_timer += millis;
        }
        else {
          this.hold_timer += millis - this.last_update_time;
        }
      }
    }
    if (!this.use_time_elapsed) {
      this.last_update_time = millis;
    }
  }

  public void mouseMove(float mX, float mY) {
    if (this.disabled) {
      return;
    }
    boolean prev_hover = this.hovered;
    this.hovered = this.mouseOn(mX, mY);
    if (this.stay_dehovered) {
      if (this.hovered) {
        this.hovered = false;
      }
      else {
        this.stay_dehovered = false;
      }
    }
    if (prev_hover && !this.hovered) {
      this.dehover();
    }
    else if (!prev_hover && this.hovered) {
      this.hover();
    }
  }

  public void mousePress() {
    if (this.disabled) {
      return;
    }
    if (this.force_left_button && p.mouseButton != PConstants.LEFT) {
      return;
    }
    if (this.hovered) {
      this.clicked = true;
      this.click();
    }
    else {
      this.clicked = false;
    }
  }

  public void mouseRelease(float mX, float mY) {
    if (this.disabled) {
      return;
    }
    if (this.force_left_button && p.mouseButton != PConstants.LEFT) {
      return;
    }
    if (this.clicked) {
      this.clicked = false;
      this.hold_timer = 0;
      this.release();
    }
    this.clicked = false;
    if (this.hover_check_after_release) {
      this.mouseMove(mX, mY);
    }
  }

  public void keyPress(int key, int keyCode) {
    if (this.disabled) {
      return;
    }
    if (key == PConstants.CODED) {
    }
    else {
      switch(key) {
        case PConstants.RETURN:
        case PConstants.ENTER:
          if (this.button_focused) {
            this.clicked = true;
            this.click();
          }
          break;
      }
    }
  }

  public void keyRelease(int key, int keyCode) {
    if (this.disabled) {
      return;
    }
    if (key == PConstants.CODED) {
    }
    else {
      switch(key) {
        case PConstants.RETURN:
        case PConstants.ENTER:
          if (this.button_focused) {
            if (this.clicked) {
              this.clicked = false;
              this.hold_timer = 0;
              this.release();
            }
          }
          break;
      }
    }
  }

  public abstract double xCenter();
  public abstract double yCenter();
  public abstract double buttonWidth();
  public abstract double buttonHeight();
  public abstract void drawButton();
  public abstract void moveButton(double xMove, double yMove);
  public abstract boolean mouseOn(float mX, float mY);
  public abstract void hover();
  public abstract void dehover();
  public abstract void click();
  public abstract void release();
}