package Button;

import processing.core.*;
import LNZApplet.LNZApplet;

public abstract class EllipseButton extends Button {
  public double xc;
  public double yc;
  protected double xr;
  protected double yr;

  public EllipseButton(LNZApplet sketch, double xc, double yc, double xr, double yr) {
    super(sketch);
    this.xc = xc;
    this.yc = yc;
    this.xr = xr;
    this.yr = yr;
  }

  public double xCenter() {
    return this.xc;
  }

  public double yCenter() {
    return this.yc;
  }

  public double buttonWidth() {
    return 2 * this.xr;
  }

  public double buttonHeight() {
    return 2 * this.yr;
  }

  public void drawButton() {
    this.setFill();
    p.ellipseMode(PConstants.RADIUS);
    p.ellipse(this.xc, this.yc, this.xr, this.yr);
    this.writeText();
    if (this.button_focused) {
      p.noFill();
      p.strokeWeight(this.stroke_weight);
      p.stroke(this.color_stroke);
      p.ellipse(this.xc + 0.1 * this.xr, this.yc + 0.1 * this.yr,
        this.xr - 0.1 * this.xr, this.yr - 0.1 * this.yr);
    }
  }

  public void setLocation(double xc, double yc, double xr, double yr) {
    this.xc = xc;
    this.yc = yc;
    this.xr = xr;
    this.yr = yr;
  }

  public void moveButton(double xMove, double yMove) {
    this.xc += xMove;
    this.yc += yMove;
  }

  public boolean mouseOn(float mX, float mY) {
    if (this.xr == 0 || this.yr == 0) {
      return false;
    }
    double xRatio = (mX - this.xc) / this.xr;
    double yRatio = (mY - this.yc) / this.yr;
    if (xRatio * xRatio + yRatio * yRatio <= 1) {
      return true;
    }
    return false;
  }
}