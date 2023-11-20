package Element;

import LNZApplet.LNZApplet;
import Button.RectangleButton;
import DImg.DImg;

public class ScrollBar {
  public abstract class ScrollBarButton extends RectangleButton {
    protected int time_hold = 350;
    protected int time_click = 80;
    protected boolean held = false;

    public ScrollBarButton(LNZApplet sketch, double xi, double yi, double xf, double yf) {
      super(sketch, xi, yi, xf, yf);
      this.roundness = 0;
      this.raised_border = true;
    }

    @Override
    public void update(int millis) {
      super.update(millis);
      if (this.clicked) {
        if (this.held) {
          if (this.hold_timer > this.time_click) {
            this.hold_timer -= this.time_click;
            this.click();
          }
        }
        else {
          if (this.hold_timer > this.time_hold) {
            this.hold_timer -= this.time_hold;
            this.held = true;
            this.click();
          }
        }
      }
    }

    public void hover() {
    }
    public void dehover() {
    }
    public void release() {
      this.held = false;
    }
  }

  public class ScrollBarUpButton extends ScrollBarButton {
    double arrowWidth = 0;
    double arrowRatio = 0.1;
    double cushionRatio = 1.5;
    ScrollBarUpButton(LNZApplet sketch, double xi, double yi, double xf, double yf) {
      super(sketch, xi, yi, xf, yf);
      refreshArrowWidth();
      this.raised_border = true;
    }
    @Override
    public void setLocation(double xi, double yi, double xf, double yf) {
      super.setLocation(xi, yi, xf, yf);
      this.refreshArrowWidth();
    }
    void refreshArrowWidth() {
      if (ScrollBar.this.vertical) {
        this.arrowWidth = this.arrowRatio * this.buttonHeight();
      }
      else {
        this.arrowWidth = this.arrowRatio * this.buttonWidth();
      }
    }
    @Override
    public void drawButton() {
      super.drawButton();
      p.stroke(DImg.ccolor(0));
      p.strokeWeight(this.arrowWidth);
      if (ScrollBar.this.vertical) {
        p.line(this.xi + this.cushionRatio * this.arrowWidth, this.yf - this.cushionRatio * this.arrowWidth,
          this.xCenter(), this.yi + this.cushionRatio * this.arrowWidth);
        p.line(this.xf - this.cushionRatio * this.arrowWidth, this.yf - this.cushionRatio * this.arrowWidth,
          this.xCenter(), this.yi + this.cushionRatio * this.arrowWidth);
      }
      else {
        p.line(this.xf - this.cushionRatio * this.arrowWidth, this.yi + this.cushionRatio * this.arrowWidth,
          this.xi + this.cushionRatio * this.arrowWidth, this.yCenter());
        p.line(this.xf - this.cushionRatio * this.arrowWidth, this.yf - this.cushionRatio * this.arrowWidth,
          this.xi + this.cushionRatio * this.arrowWidth, this.yCenter());
      }
    }
    @Override
    public void dehover() {
      this.clicked = false;
    }
    public void click() {
      ScrollBar.this.decreaseValue(1);
    }
  }

  public class ScrollBarDownButton extends ScrollBarButton {
    double arrowWidth = 0;
    double arrowRatio = 0.1;
    double cushionRatio = 1.5;
    ScrollBarDownButton(LNZApplet sketch, double xi, double yi, double xf, double yf) {
      super(sketch, xi, yi, xf, yf);
      refreshArrowWidth();
      this.raised_border = true;
    }
    @Override
    public void setLocation(double xi, double yi, double xf, double yf) {
      super.setLocation(xi, yi, xf, yf);
      this.refreshArrowWidth();
    }
    void refreshArrowWidth() {
      if (ScrollBar.this.vertical) {
        this.arrowWidth = this.arrowRatio * this.buttonHeight();
      }
      else {
        this.arrowWidth = this.arrowRatio * this.buttonWidth();
      }
    }
    @Override
    public void drawButton() {
      super.drawButton();
      p.stroke(DImg.ccolor(0));
      p.strokeWeight(this.arrowWidth);
      if (ScrollBar.this.vertical) {
        p.line(this.xi + this.cushionRatio * this.arrowWidth, this.yi + this.cushionRatio * this.arrowWidth,
          this.xCenter(), this.yf - this.cushionRatio * this.arrowWidth);
        p.line(this.xf - this.cushionRatio * this.arrowWidth, this.yi + this.cushionRatio * this.arrowWidth,
          this.xCenter(), this.yf - this.cushionRatio * this.arrowWidth);
      }
      else {
        p.line(this.xi + this.cushionRatio * this.arrowWidth, this.yi + this.cushionRatio * this.arrowWidth,
          this.xf - this.cushionRatio * this.arrowWidth, this.yCenter());
        p.line(this.xi + this.cushionRatio * this.arrowWidth, this.yf - this.cushionRatio * this.arrowWidth,
          this.xf - this.cushionRatio * this.arrowWidth, this.yCenter());
      }
    }
    @Override
    public void dehover() {
      this.clicked = false;
    }
    public void click() {
      ScrollBar.this.increaseValue(1);
    }
  }

  public class ScrollBarUpSpaceButton extends ScrollBarButton {
    ScrollBarUpSpaceButton(LNZApplet sketch, double xi, double yi, double xf, double yf) {
      super(sketch, xi, yi, xf, yf);
      this.setColors(DImg.ccolor(180), DImg.ccolor(235),
        DImg.ccolor(235), DImg.ccolor(0), DImg.ccolor(0));
      this.raised_border = false;
    }
    public void click() {
      ScrollBar.this.decreaseValuePercent(0.1);
    }
    @Override
    public void release() {
      super.release();
      this.hovered = false;
    }
  }

  public class ScrollBarDownSpaceButton extends ScrollBarButton {
    ScrollBarDownSpaceButton(LNZApplet sketch, double xi, double yi, double xf, double yf) {
      super(sketch, xi, yi, xf, yf);
      this.setColors(DImg.ccolor(180), DImg.ccolor(235),
        DImg.ccolor(235), DImg.ccolor(0), DImg.ccolor(0));
      this.raised_border = false;
    }
    public void click() {
      ScrollBar.this.increaseValuePercent(0.1);
    }
    @Override
    public void release() {
      super.release();
      this.hovered = false;
    }
  }

  public class ScrollBarBarButton extends ScrollBarButton {
    protected double val = 0;
    protected double last_val = 0;
    ScrollBarBarButton(LNZApplet sketch, double xi, double yi, double xf, double yf) {
      super(sketch, xi, yi, xf, yf);
    }
    @Override
    public void update(int millis) {
      if (!this.hidden) {
        drawButton();
      }
      if (this.clicked && ScrollBar.this.value_size != 0) {
        this.hold_timer += millis - this.last_update_time;
      }
      this.last_update_time = millis;
    }
    @Override
    public void mouseMove(float mX, float mY) {
      super.mouseMove(mX, mY);
      if (ScrollBar.this.vertical) {
        this.last_val = mY;
      }
      else {
        this.last_val = mX;
      }
      if (this.clicked && ScrollBar.this.value_size != 0) {
        if (ScrollBar.this.vertical) {
          ScrollBar.this.increaseValue((mY - this.yi - this.val) / ScrollBar.this.value_size);
        }
        else {
          ScrollBar.this.increaseValue((mX - this.xi - this.val) / ScrollBar.this.value_size);
        }
      }
    }
    public void click() {
      if (ScrollBar.this.vertical) {
        this.val = this.last_val - this.yi;
      }
      else {
        this.val = this.last_val - this.xi;
      }
    }
  }
	
	protected LNZApplet p;

  public ScrollBarUpButton button_up;
  public ScrollBarDownButton button_down;
  public ScrollBarUpSpaceButton button_upspace;
  public ScrollBarDownSpaceButton button_downspace;
  public ScrollBarBarButton button_bar;

  public double minValue = 0;
  public double maxValue = 0;
  public double value = 0;

  protected double xi;
  protected double yi;
  protected double xf;
  protected double yf;
  protected boolean vertical;
  public double bar_size = 0;
  protected double min_size = 0;
  protected double value_size = 0;
  protected double step_size = 10; // constant

  public ScrollBar(LNZApplet sketch, boolean vertical) {
    this(sketch, 0, 0, 0, 0, vertical);
  }
  public ScrollBar(LNZApplet sketch, double xi, double yi, double xf, double yf, boolean vertical) {
    this.p = sketch;
    this.vertical = vertical;
    this.button_up = new ScrollBarUpButton(sketch, 0, 0, 0, 0);
    this.button_down = new ScrollBarDownButton(sketch, 0, 0, 0, 0);
    this.button_upspace = new ScrollBarUpSpaceButton(sketch, 0, 0, 0, 0);
    this.button_downspace = new ScrollBarDownSpaceButton(sketch, 0, 0, 0, 0);
    this.button_bar = new ScrollBarBarButton(sketch, 0, 0, 0, 0);
    this.setLocation(xi, yi, xf, yf);
  }

  public void setButtonColors(int c_dis, int c_def, int c_hov, int c_cli, int c_tex) {
    this.button_up.setColors(c_dis, c_def, c_hov, c_cli, c_tex);
    this.button_down.setColors(c_dis, c_def, c_hov, c_cli, c_tex);
    this.button_bar.setColors(c_dis, c_def, c_hov, c_cli, c_tex);
  }

  public void useElapsedTime() {
    this.button_up.use_time_elapsed = true;
    this.button_down.use_time_elapsed = true;
    this.button_upspace.use_time_elapsed = true;
    this.button_downspace.use_time_elapsed = true;
    this.button_bar.use_time_elapsed = true;
  }

  public void move(double xMove, double yMove) {
    this.xi += xMove;
    this.yi += yMove;
    this.xf += xMove;
    this.yf += yMove;
    this.button_up.moveButton(xMove, yMove);
    this.button_down.moveButton(xMove, yMove);
    this.button_upspace.moveButton(xMove, yMove);
    this.button_downspace.moveButton(xMove, yMove);
    this.button_bar.moveButton(xMove, yMove);
  }

  public void setLocation(double xi, double yi, double xf, double yf) {
    this.xi = xi;
    this.yi = yi;
    this.xf = xf;
    this.yf = yf;
    if (this.vertical) {
      this.bar_size = this.xf - this.xi;
      if (3 * this.bar_size > this.yf - this.yi) {
        this.bar_size = (this.yf - this.yi) / 3.0;
        this.min_size = 0.5 * this.bar_size;
      }
      else {
        this.min_size = Math.min(this.bar_size, (this.yf - this.yi) / 9.0);
      }
      this.button_up.setLocation(this.xi, this.yi, this.xf, this.yi + this.bar_size);
      this.button_down.setLocation(this.xi, this.yf - this.bar_size, this.xf, this.yf);
    }
    else {
      this.bar_size = this.yf - this.yi;
      if (3 * this.bar_size > this.xf - this.xi) {
        this.bar_size = (this.xf - this.xi) / 3.0;
        this.min_size = 0.5 * this.bar_size;
      }
      else {
        this.min_size = Math.min(this.bar_size, (this.xf - this.xi) / 9.0);
      }
      this.button_up.setLocation(this.xi, this.yi, this.xi + this.bar_size, this.yf);
      this.button_down.setLocation(this.xf - this.bar_size, this.yi, this.xf, this.yf);
    }
    this.refreshBarButtonSizes();
  }

  void refreshBarButtonSizes() {
    double bar_height = 0;
    if (this.vertical) {
      bar_height = this.yf - this.yi - 2 * this.bar_size;
    }
    else {
      bar_height = this.xf - this.xi - 2 * this.bar_size;
    }
    double bar_button_size = Math.max(this.min_size, bar_height - this.step_size * (this.maxValue - this.minValue));
    if (this.maxValue == this.minValue) {
      this.value_size = 0;
    }
    else {
      this.value_size = (bar_height - bar_button_size) / (this.maxValue - this.minValue);
    }
    this.refreshBarButtons();
  }

  void refreshBarButtons() {
    if (this.vertical) {
      double cut_one = this.yi + this.bar_size + this.value_size * (this.value - this.minValue);
      double cut_two = this.yf - this.bar_size - this.value_size * (this.maxValue - this.value);
      this.button_upspace.setLocation(this.xi, this.yi + this.bar_size, this.xf, cut_one);
      this.button_downspace.setLocation(this.xi, cut_two, this.xf, this.yf - this.bar_size);
      this.button_bar.setLocation(this.xi, cut_one, this.xf, cut_two);
    }
    else {
      double cut_one = this.xi + this.bar_size + this.value_size * (this.value - this.minValue);
      double cut_two = this.xf - this.bar_size - this.value_size * (this.maxValue - this.value);
      this.button_upspace.setLocation(this.xi + this.bar_size, this.yi, cut_one, this.yf);
      this.button_downspace.setLocation(cut_two, this.yi, this.xf - this.bar_size, this.yf);
      this.button_bar.setLocation(cut_one, this.yi, cut_two, this.yf);
    }
  }

  public void updateMinValue(double minValue) {
    this.minValue = minValue;
    if (this.minValue > this.maxValue) {
      this.minValue = this.maxValue;
    }
    if (this.value < this.minValue) {
      this.value = this.minValue;
    }
    this.refreshBarButtonSizes();
  }
  public void increaseMinValue(double amount) {
    this.updateMinValue(this.minValue + amount);
  }
  public void decreaseMinValue(double amount) {
    this.updateMinValue(this.minValue - amount);
  }

  public void updateMaxValue(double maxValue) {
    this.maxValue = maxValue;
    if (this.maxValue < this.minValue) {
      this.maxValue = this.minValue;
    }
    if (this.value > this.maxValue) {
      this.value = this.maxValue;
    }
    this.refreshBarButtonSizes();
  }
  public void increaseMaxValue(double amount) {
    this.updateMaxValue(this.maxValue + amount);
  }
  public void decreaseMaxValue(double amount) {
    this.updateMaxValue(this.maxValue - amount);
  }

  public void updateValue(double value) {
    if (this.p.halt_event_propagation || this.value == value) {
      return;
    }
    this.p.halt_event_propagation = true;
    this.value = value;
    if (this.value < this.minValue) {
      this.value = this.minValue;
    }
    else if (this.value > this.maxValue) {
      this.value = this.maxValue;
    }
    this.refreshBarButtons();
  }
  public void scrollMax() {
    this.value = this.maxValue;
    this.refreshBarButtons();
  }
  public void scrollMin() {
    this.value = this.minValue;
    this.refreshBarButtons();
  }

  public void increaseValue(double amount) {
    this.updateValue(this.value + amount);
  }
  public void decreaseValue(double amount) {
    this.updateValue(this.value - amount);
  }
  public void increaseValuePercent(double percent) {
    this.updateValue(this.value + percent * (this.maxValue - this.minValue));
  }
  public void decreaseValuePercent(double percent) {
    this.updateValue(this.value - percent * (this.maxValue - this.minValue));
  }

  public void update(int millis) {
    this.button_up.update(millis);
    this.button_down.update(millis);
    this.button_upspace.update(millis);
    this.button_downspace.update(millis);
    this.button_bar.update(millis);
  }

  public void mouseMove(float mX, float mY) {
    this.button_up.mouseMove(mX, mY);
    this.button_down.mouseMove(mX, mY);
    this.button_upspace.mouseMove(mX, mY);
    this.button_downspace.mouseMove(mX, mY);
    this.button_bar.mouseMove(mX, mY);
  }

  public void mousePress() {
    this.button_up.mousePress();
    this.button_down.mousePress();
    this.button_upspace.mousePress();
    this.button_downspace.mousePress();
    this.button_bar.mousePress();
  }

  boolean clicked() {
    return (this.button_up.clicked || this.button_down.clicked ||
      this.button_upspace.clicked || this.button_downspace.clicked ||
      this.button_bar.clicked);
  }

  public void mouseRelease(float mX, float mY) {
    this.button_up.mouseRelease(mX, mY);
    this.button_down.mouseRelease(mX, mY);
    this.button_upspace.mouseRelease(mX, mY);
    this.button_downspace.mouseRelease(mX, mY);
    this.button_bar.mouseRelease(mX, mY);
  }
}

