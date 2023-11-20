package Button;

import LNZApplet.LNZApplet;

public abstract class TriangleButton extends Button {
  protected double x1;
  protected double y1;
  protected double x2;
  protected double y2;
  protected double x3;
  protected double y3;
  protected double dotvv;
  protected double dotuu;
  protected double dotvu;
  protected double constant;
  protected double x_center;
  protected double y_center;

  public TriangleButton(LNZApplet sketch, double x1, double y1, double x2, double y2, double x3, double y3) {
    super(sketch);
    this.setLocation(x1, y1, x2, y2, x3, y3);
  }

  @Override
  public String toString() {
    return this.x1 + " " + this.y1 + " " + this.x2 + " " + this.y2 + " " + this.x3 + " " + this.y3;
  }

  void setLocation(double x1, double y1, double x2, double y2, double x3, double y3) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.x3 = x3;
    this.y3 = y3;
    this.dotvv = (x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1);
    this.dotuu = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
    this.dotvu = (x3 - x1) * (x2 - x1) + (y3 - y1) * (y2 - y1);
    this.constant = this.dotvv * this.dotuu - this.dotvu * this.dotvu;
    this.x_center = (x1 + x2 + x3) / 3.0; // centroid
    this.y_center = (y1 + y2 + y3) / 3.0;
  }

  public double xCenter() {
    return this.x_center;
  }

  public double yCenter() {
    return this.y_center;
  }

  public double buttonWidth() {
    return Math.max(this.x1, Math.max(this.x2, this.x3)) -
      Math.min(this.x1, Math.min(this.x2, this.x3));
  }

  public double buttonHeight() {
    return Math.max(this.y1, Math.max(this.y2, this.y3)) -
      Math.min(this.y1, Math.min(this.y2, this.y3));
  }

  public void drawButton() {
    this.setFill();
    p.triangle(this.x1, this.y1, this.x2, this.y2, this.x3, this.y3);
    this.writeText();
  }

  public void moveButton(double x_move, double y_move) {
    this.setLocation(this.x1 + x_move, this.y1 + y_move, this.x2 + x_move,
      this.y2 + y_move, this.x3 + x_move, this.y3 + y_move);
  }

  public boolean mouseOn(float mX, float mY) {
    double dotvp = (this.x3 - this.x1) * (mX - this.x1) + (this.y3 - this.y1) * (mY - this.y1);
    double dotup = (this.x2 - this.y1) * (mX - this.x1) + (this.y2 - this.y1) * (mY - this.y1);
    if (this.constant == 0) {
      return false;
    }
    double t1 = (this.dotuu * dotvp - this.dotvu * dotup) / this.constant;
    double t2 = (this.dotvv * dotup - this.dotvu * dotvp) / this.constant;
    if (t1 >= 0 && t2 >= 0 && t1 + t2 < 1) {
      return true;
    }
    return false;
  }
}