package Button;

import processing.core.*;
import LNZApplet.LNZApplet;

public abstract class ArcButton extends Button {
  class TestButton extends TriangleButton {
    TestButton(LNZApplet sketch, double x1, double y1, double x2, double y2, double x3, double y3) {
      super(sketch, x1, y1, x2, y2, x3, y3);
    }
    public void hover() {}
    public void dehover() {}
    public void click() {}
    public void release() {}
  }

  protected double xc;
  protected double yc;
  protected double xr;
  protected double yr;
  protected double start;
  protected double stop;
  protected boolean pie = true; // false for open arc
  protected double xStart;
  protected double xStop;
  protected double yStart;
  protected double yStop;

  public ArcButton(LNZApplet sketch, double xc, double yc, double xr, double yr, double start, double stop) {
    super(sketch);
    this.setLocation(xc, yc, xr, yr, start, stop);
  }

  @Override
  public String toString() {
    return this.xc + " " + this.yc + " " + this.xr + " " + this.yr + " " +
      this.start + " " + this.stop;
  }

  void setLocation(double xc, double yc, double xr, double yr, double start, double stop) {
    this.xc = xc;
    this.yc = yc;
    this.xr = xr;
    this.yr = yr;
    this.start = start;
    this.stop = stop;
    // fix angles if not in range [0, TWO_PI]
    this.xStart = Math.cos(this.start);
    this.xStop = Math.cos(this.stop);
    this.yStart = Math.sin(this.start);
    this.yStop = Math.sin(this.stop);
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
    if (this.pie) {
      p.arc(this.xc, this.yc, this.xr, this.yr, this.start, this.stop, PConstants.PIE);
    }
    else {
      p.arc(this.xc, this.yc, this.xr, this.yr, this.start, this.stop, PConstants.CHORD);
    }
    this.writeText();
  }

  public void moveButton(double xMove, double yMove) {
    this.xc += xMove;
    this.yc += yMove;
  }

  public boolean mouseOn(float mX, float mY) {
    if (this.xr == 0 || this.yr == 0) {
      return false;
    }
    // in ellipse
    double xRatio = (mX - this.xc) / this.xr;
    double yRatio = (mY - this.yc) / this.yr;
    double hypotenuse = xRatio * xRatio + yRatio * yRatio;
    if (hypotenuse > 1) {
      return false;
    }
    hypotenuse = Math.sqrt(hypotenuse);
    // in arc
    double angle = Math.asin(yRatio / hypotenuse);
    if (xRatio < 0) { // Q2 or Q3
      angle = Math.PI - angle;
    }
    else if (yRatio < 0) { // Q4
      angle += PApplet.TWO_PI;
    }
    if (angle > this.start && angle < this.stop) {
      if (this.pie) {
        return true;
      }
      else {
        TestButton excludedArea = new TestButton(p, 0, 0, this.xStart, this.yStart, this.xStop, this.yStop);
        if (!excludedArea.mouseOn((float)xRatio, (float)yRatio)) {
          return true;
        }
      }
    }
    return false;
  }
}