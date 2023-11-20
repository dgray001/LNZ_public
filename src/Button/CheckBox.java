package Button;

import LNZApplet.LNZApplet;
import DImg.DImg;

public abstract class CheckBox extends RectangleButton {
  public boolean checked = false;
  protected int color_check = DImg.ccolor(0);
  protected double offset = 0;

  public CheckBox(LNZApplet sketch, double xi, double yi, double size) {
    this(sketch, xi, yi, xi + size, xi + size);
  }
  public CheckBox(LNZApplet sketch, double xi, double yi, double xf, double yf) {
    super(sketch, xi, yi, xf, yf);
    this.setColors(DImg.ccolor(170, 170), DImg.ccolor(170, 0),
      DImg.ccolor(170, 50), DImg.ccolor(170, 120), DImg.ccolor(0));
    this.roundness = 0;
    this.stroke_weight = 2;
  }

  @Override
  public void setLocation(double xi, double yi, double xf, double yf) {
    super.setLocation(xi, yi, xf, yf);
    this.offset = 0.1 * (xf  - xi);
  }

  @Override
  public void drawButton() {
    super.drawButton();
    if (this.checked) {
      p.strokeWeight(this.stroke_weight);
      p.stroke(this.color_stroke);
      p.line(this.xi + offset, this.yi + offset, this.xf - offset, this.yf - offset);
      p.line(this.xi + offset, this.yf - offset, this.xf - offset, this.yi + offset);
    }
  }

  public void click() {
    this.checked = !this.checked;
  }
}