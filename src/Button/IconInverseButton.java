package Button;

import processing.core.*;
import LNZApplet.LNZApplet;

public abstract class IconInverseButton extends IconButton {
  public IconInverseButton(LNZApplet sketch, double xi, double yi, double xf, double yf, PImage icon) {
    super(sketch, xi, yi, xf, yf, icon);
  }

  @Override
  public void update(int millis) {
    super.update(millis);
    p.imageMode(PConstants.CORNER);
    p.image(this.icon, this.xi, this.yi, this.icon_width, this.icon_width);
    if (this.disabled) {
      p.rectMode(PConstants.CORNERS);
      if (this.show_stroke) {
        p.stroke(this.color_stroke);
        p.strokeWeight(this.stroke_weight);
      }
      else {
        p.noStroke();
      }
      p.fill(this.background_color);
      p.rect(this.xi, this.yi, this.xf, this.yf);
    }
  }
}