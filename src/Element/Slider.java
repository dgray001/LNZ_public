package Element;

import processing.core.*;
import LNZApplet.LNZApplet;
import Button.*;
import DImg.DImg;

public class Slider  {
  public class SliderButton extends CircleButton {
    public boolean active = false;
    protected double active_grow_factor = 1.3;
    protected int active_color = DImg.ccolor(0, 50, 0);
    protected double lastX = 0;
    protected double change_factor = 1;
    public boolean show_value = false;
    public int round_value = 1;
    public boolean divide_round_value = false;

    SliderButton(LNZApplet sketch) {
      super(sketch, 0, 0, 0);
      this.setColors(DImg.ccolor(170), DImg.ccolor(255, 0), DImg.ccolor(255, 0), DImg.ccolor(255, 0), DImg.ccolor(0));
      p.strokeWeight(2);
    }

    @Override
    public double radius() {
      if (this.active) {
        return this.active_grow_factor * super.radius();
      }
      else {
        return super.radius();
      }
    }

    int lineColor() {
      if (this.disabled) {
        return this.color_disabled;
      }
      else if (this.active) {
        return this.active_color;
      }
      return this.color_stroke;
    }

    @Override
    public void drawButton() {
      p.ellipseMode(PConstants.RADIUS);
      if (this.disabled) {
        p.fill(this.color_disabled);
      }
      else if (this.active) {
        p.fill(this.active_color);
      }
      else {
        p.noFill();
      }
      p.stroke(this.lineColor());
      p.strokeWeight(Slider.this.line_thickness);
      p.circle(this.xc, this.yc, this.radius());
      if (this.show_value) {
        p.fill(this.color_text);
        p.textSize(this.text_size);
        p.textAlign(PConstants.CENTER, PConstants.BOTTOM);
        int display_num = (int)Math.round(Slider.this.value * this.round_value);
        if (this.divide_round_value) {
          display_num = Math.round(display_num / this.round_value);
        }
        p.text(Integer.toString(display_num), this.xc, this.yc - this.radius());
      }
    }

    public void mouseMove(float mX, float mY) {
      super.mouseMove(mX, mY);
      if (this.active && this.clicked) {
        this.moveButton(mX - this.lastX, 0);
        this.change_factor = 1; // how much value actually changed (accounting for step_size)
        Slider.this.refreshValue();
        this.lastX += this.change_factor * (mX - this.lastX);
      }
      else {
        this.lastX = mX;
      }
    }

    public void mousePress() {
      super.mousePress();
      if (!this.hovered) {
        this.active = false;
      }
    }

    void scroll(int amount) {
      if (!this.active) {
        return;
      }
      Slider.this.step(amount);
    }

    @Override
    public void keyPress(int key, int keyCode) {
      if (!this.active) {
        return;
      }
      if (key == PConstants.CODED) {
        switch(keyCode) {
          case PConstants.LEFT:
            Slider.this.step(-1);
            break;
          case PConstants.RIGHT:
            Slider.this.step(1);
            break;
          default:
            break;
        }
      }
    }

    public void hover() {}
    public void dehover() {}
    public void release() {}

    public void click() {
      this.active = true;
    }
  }
	
	protected LNZApplet p;

  public double xi;
  public double yi;
  public double xf;
  public double yf;
  protected double yCenter;

  protected double min_value = 0;
  protected double max_value = 0;
  protected double step_size = -1;
  protected boolean no_step = true;
  public double value = 0;

  public SliderButton button;
  protected double offset;
  protected double line_thickness = 3;

  protected boolean hovered = false;

  public String label = "";
  public boolean show_label = false;
  public boolean round_label = true;
  public boolean only_label_ends = false;
  public boolean show_label_in_middle = false;

  public Slider(LNZApplet sketch) {
    this(sketch, 0, 0, 0, 0);
  }
  public Slider(LNZApplet sketch, double xi, double yi, double xf, double yf) {
    this.p = sketch;
    this.button = new SliderButton(sketch);
    this.setLocation(xi, yi, xf, yf);
  }

  public void disable() {
    this.button.active = false;
    this.button.disabled = true;
  }

  public void enable() {
    this.button.disabled = false;
  }

  public void setLocation(double xi, double yi, double xf, double yf) {
    this.xi = xi;
    this.yi = yi;
    this.xf = xf;
    this.yf = yf;
    this.yCenter = yi + 0.5 * (yf - yi);
    this.button.setLocation(xi, this.yCenter, 0.5 * (yf - yi) / this.button.active_grow_factor);
    this.offset = this.button.radius() * this.button.active_grow_factor;
    this.refreshButton();
  }

  // called when slider changes value or size (this never changes value)
  void refreshButton() {
    if (this.min_value == this.max_value) {
      this.button.moveButton(this.xi + this.offset - this.button.xCenter(), 0);
      return;
    }
    double targetX = this.xi + this.offset + (this.xf - 2 * this.offset - this.xi) *
      (this.value - this.min_value) / (this.max_value - this.min_value);
    this.button.moveButton(targetX - this.button.xCenter(), 0);
  }

  // called when button changes value (this changes value so calls refreshButton)
  void refreshValue() {
    double target_value = this.min_value + (this.button.xCenter() - this.xi - this.offset)
      * (this.max_value - this.min_value) / (this.xf - 2 * this.offset - this.xi);
    boolean hitbound = false;
    if (target_value >= this.max_value) {
      double change = target_value - this.value;
      if (change > 0) {
        this.button.change_factor = (this.max_value - this.value) / change;
      }
      target_value = this.max_value;
      hitbound = true;
    }
    else if (target_value <= this.min_value) {
      double change = target_value - this.value;
      if (change < 0) {
        this.button.change_factor = (this.min_value - this.value) / change;
      }
      target_value = this.min_value;
      hitbound = true;
    }
    double change = target_value - this.value;
    if (!this.no_step && !hitbound && this.step_size != 0 && change != 0) {
      double new_change = this.step_size * (Math.round(change / this.step_size));
      this.button.change_factor = new_change/change;
      change = new_change;
    }
    this.value += change;
    this.refreshButton();
  }

  public void bounds(double min, double max, double step) {
    if (min > max) {
      min = max;
    }
    this.min_value = min;
    this.max_value = max;
    this.step_size = step;
    if (this.value < min) {
      this.value = min;
    }
    else if (this.value > max) {
      this.value = max;
    }
    if (step > 0) {
      this.no_step = false;
    }
    else {
      this.no_step = true;
    }
  }

  void step(int amount) {
    if (this.no_step) {
      this.value += 0.1 * (this.max_value - this.min_value) * amount;
    }
    else {
      this.value += this.step_size * amount;
    }
    if (this.value > this.max_value) {
      this.value = this.max_value;
    }
    else if (this.value < this.min_value) {
      this.value = this.min_value;
    }
    this.refreshButton();
  }

  public void setValue(double value) {
    this.value = value;
    if (this.value > this.max_value) {
      this.value = this.max_value;
    }
    else if (this.value < this.min_value) {
      this.value = this.min_value;
    }
    this.refreshButton();
  }

  public void update(int millis) {
    if (this.show_label) {
      p.textSize((float)this.button.radius());
      p.textAlign(PConstants.CENTER, PConstants.BOTTOM);
    }
    if (!this.no_step && this.max_value != this.min_value) {
      p.strokeWeight(0.5 * this.line_thickness);
      p.stroke(this.button.active_color);
      p.fill(this.button.active_color);
      boolean not_switched_color = true;
      boolean on_end = true;
      for (double i = this.min_value; i <= this.max_value; i += this.step_size) {
        if (i + this.step_size > this.max_value) {
          on_end = true;
        }
        double targetX = this.xi + this.offset + (this.xf - 2 * this.offset - this.xi) *
          (i - this.min_value) / (this.max_value - this.min_value);
        if (not_switched_color && targetX > this.button.xCenter()) {
          p.stroke(this.button.color_stroke);
          p.fill(this.button.color_stroke);
          not_switched_color = false;
        }
        if (this.show_label && (!this.only_label_ends || on_end)) {
          p.line(targetX, this.button.yc - 3, targetX, this.button.yc + this.button.radius() - 1);
          String label_text = "";
          if (this.round_label) {
            if (on_end || this.show_label_in_middle) {
              label_text = Math.round(i) + this.label;
            }
            else {
              label_text = Long.toString(Math.round(i));
            }
          }
          else {
            if (on_end || this.show_label_in_middle) {
              label_text = i + this.label;
            }
            else {
              label_text = Double.toString(i);
            }
          }
          p.text(label_text, targetX, this.button.yc);
        }
        else {
          p.line(targetX, this.button.yc - this.button.radius() + 1, targetX,
            this.button.yc + this.button.radius() - 1);
        }
        on_end = false;
      }
    }
    else if (this.show_label) {
      String label_min = "";
      String label_max = "";
      if (this.round_label) {
        label_min = Math.round(this.min_value) + this.label;
        label_max = Math.round(this.max_value) + this.label;
      }
      else {
        label_min = this.min_value + this.label;
        label_max = this.max_value + this.label;
      }
      p.fill(this.button.active_color);
      p.text(label_min, this.xi + this.offset, this.button.yc);
      p.fill(this.button.color_stroke);
      p.text(label_max, this.xf - this.offset, this.button.yc);
    }
    p.strokeWeight(this.line_thickness);
    p.stroke(this.button.active_color);
    p.line(Math.min(this.xi + this.offset, this.button.xc - this.button.radius()),
      this.yCenter, this.button.xc - this.button.radius(), this.yCenter);
    p.stroke(this.button.color_stroke);
    p.line(this.button.xc + this.button.radius(), this.yCenter,
      Math.max(this.xf - this.offset, this.button.xc + this.button.radius()), this.yCenter);
    this.button.update(millis);
  }

  public void mouseMove(float mX, float mY) {
    this.button.mouseMove(mX, mY);
    if (mX > this.xi && mY > this.yi && mX < this.xf && mY < this.yf) {
      this.hovered = true;
    }
    else {
      this.hovered = false;
    }
  }

  public void mousePress() {
    this.button.mousePress();
    if (this.hovered && !this.button.disabled) {
      this.button.active = true;
      this.button.clicked = true;
      this.button.moveButton(this.button.lastX - this.button.xCenter(), 0);
      Slider.this.refreshValue();
    }
  }

  public void mouseRelease(float mX, float mY) {
    this.button.mouseRelease(mX, mY);
  }

  public void scroll(int amount) {
    this.button.scroll(amount);
  }

  public void keyPress(int key, int keyCode) {
    this.button.keyPress(key, keyCode);
  }
}