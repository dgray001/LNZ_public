package Button;

import processing.core.*;
import LNZApplet.LNZApplet;
import DImg.DImg;

public abstract class RectangleButton extends Button {
  public double xi;
  public double yi;
  public double xf;
  public double yf;
  protected int roundness = 8;
  protected double x_center;
  protected double y_center;
  public boolean raised_border = false;
  public boolean raised_body = false;
  public boolean shadow = false;
  public double shadow_amount = 5;

  public RectangleButton(LNZApplet sketch) {
    this(sketch, 0, 0, 0, 0);
  }
  public RectangleButton(LNZApplet sketch, double xi, double yi, double xf, double yf) {
    super(sketch);
    this.setLocation(xi, yi, xf, yf);
  }

  @Override
  public String toString() {
    return "RectangleButton at " + this.xi + ", " +
      this.yi + ", " + this.xf + ", " + this.yf;
  }

  public double xCenter() {
    return this.x_center;
  }

  public double yCenter() {
    return this.y_center;
  }

  public double buttonWidth() {
    return this.xf - this.xi;
  }

  public double buttonHeight() {
    return this.yf - this.yi;
  }

  public void drawButton() {
    p.rectMode(PConstants.CORNERS);
    if (this.shadow) {
      p.fill(DImg.ccolor(0, 180));
      p.rect(this.xi + this.shadow_amount, this.yi + this.shadow_amount,
        this.xf + this.shadow_amount, this.yf + this.shadow_amount, this.roundness);
    }
    this.setFill();
    if (this.shadow && this.clicked && !this.disabled) {
      p.translate(this.shadow_amount, this.shadow_amount);
    }
    if (this.raised_body && !this.disabled) {
      p.fill(DImg.ccolor(255, 0));
      p.rect(this.xi, this.yi, this.xf, this.yf, this.roundness);
      p.stroke(DImg.ccolor(255, 0));
      if (this.clicked) {
        p.fill(DImg.darken(this.fillColor()));
        p.rect(this.xi, this.yi, this.xf, this.yCenter());
        p.fill(DImg.brighten(this.fillColor()));
        p.rect(this.xi, this.yCenter(), this.xf, this.yf);
      }
      else {
        p.fill(DImg.brighten(this.fillColor()));
        p.rect(this.xi, this.yi, this.xf, this.yCenter(), this.roundness);
        p.fill(DImg.darken(this.fillColor()));
        p.rect(this.xi, this.yCenter(), this.xf, this.yf, this.roundness);
      }
    }
    else {
      p.rect(this.xi, this.yi, this.xf, this.yf, this.roundness);
    }
    this.writeText();
    if (this.shadow && this.clicked && !this.disabled) {
      p.translate(-this.shadow_amount, -this.shadow_amount);
    }
    if (this.raised_border && !this.disabled) {
      p.strokeWeight(1);
      if (this.clicked) {
        p.stroke(DImg.ccolor(0));
        p.line(this.xi, this.yi, this.xf, this.yi);
        p.line(this.xi, this.yi, this.xi, this.yf);
        p.stroke(DImg.ccolor(255));
        p.line(this.xf, this.yf, this.xf, this.yi);
        p.line(this.xf, this.yf, this.xi, this.yf);
      }
      else {
        p.stroke(DImg.ccolor(255));
        p.line(this.xi, this.yi, this.xf, this.yi);
        p.line(this.xi, this.yi, this.xi, this.yf);
        p.stroke(DImg.ccolor(0));
        p.line(this.xf, this.yf, this.xf, this.yi);
        p.line(this.xf, this.yf, this.xi, this.yf);
      }
    }
    if (this.button_focused) {
      p.noFill();
      p.strokeWeight(this.stroke_weight);
      p.stroke(this.color_stroke);
      p.rect(this.xi + 0.1 * this.buttonWidth(), this.yi + 0.1 * this.buttonHeight(),
        this.xf - 0.1 * this.buttonWidth(), this.yf - 0.1 * this.buttonHeight(), this.roundness);
    }
  }

  public void setLocation(double xi, double yi, double xf, double yf) {
    this.xi = xi;
    this.yi = yi;
    this.xf = xf;
    this.yf = yf;
    this.x_center = this.xi + 0.5 * (this.xf - this.xi);
    this.y_center = this.yi + 0.5 * (this.yf - this.yi);
  }
  public void setXLocation(double xi, double xf) {
    this.setLocation(xi, this.yi, xf, this.yf);
  }
  public void setYLocation(double yi, double yf) {
    this.setLocation(this.xi, yi, this.xf, yf);
  }

  public void moveButton(double xMove, double yMove) {
    this.xi += xMove;
    this.yi += yMove;
    this.xf += xMove;
    this.yf += yMove;
    this.x_center = this.xi + 0.5 * (this.xf - this.xi);
    this.y_center = this.yi + 0.5 * (this.yf - this.yi);
  }

  public void stretchButton(double amount, int direction) {
    switch(direction) {
      case PConstants.UP:
        this.setLocation(this.xi, this.yi - amount, this.xf, this.yf);
        break;
      case PConstants.DOWN:
        this.setLocation(this.xi, this.yi, this.xf, this.yf + amount);
        break;
      case PConstants.LEFT:
        this.setLocation(this.xi - amount, this.yi, this.xf, this.yf);
        break;
      case PConstants.RIGHT:
        this.setLocation(this.xi, this.yi, this.xf + amount, this.yf);
        break;
      default:
        break;
    }
  }

  public boolean mouseOn(float mX, float mY) {
    if (mX >= this.xi && mY >= this.yi &&
      mX <= this.xf && mY <= this.yf) {
      return true;
    }
    return false;
  }
}