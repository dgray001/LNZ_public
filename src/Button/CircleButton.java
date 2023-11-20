package Button;

import LNZApplet.LNZApplet;

public abstract class CircleButton extends EllipseButton {
  public CircleButton(LNZApplet sketch, double xc, double yc, double r) {
    super(sketch, xc, yc, r, r);
  }
  public double radius() {
    return this.xr;
  }

  public void setLocation(double xc, double yc, double radius) {
    super.setLocation(xc, yc, radius, radius);
  }
}