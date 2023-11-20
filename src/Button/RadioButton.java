package Button;

import processing.core.*;
import LNZApplet.LNZApplet;
import DImg.DImg;

public abstract class RadioButton extends CircleButton {
  public boolean checked = false;
  protected int color_active = DImg.ccolor(0);

  public RadioButton(LNZApplet sketch, double xc, double yc, double r) {
    super(sketch, xc, yc, r);
    this.setColors(DImg.ccolor(170, 120), DImg.ccolor(170, 0),
      DImg.ccolor(170, 40), DImg.ccolor(170, 80), DImg.ccolor(0));
  }

  @Override
  public void drawButton() {
    super.drawButton();
    if (this.checked) {
      p.fill(this.color_active);
      p.ellipseMode(PConstants.RADIUS);
      p.circle(this.xCenter(), this.yCenter(), 0.6 * this.radius());
    }
    if (this.clicked) {
      p.fill(this.color_active, 135);
      p.ellipseMode(PConstants.RADIUS);
      p.circle(this.xCenter(), this.yCenter(), 1.4 * this.radius());
    }
  }

  public void click() {
    this.checked = !this.checked;
  }
}