package Button;

import processing.core.*;
import LNZApplet.LNZApplet;
import DImg.DImg;

public abstract class IconButton extends RippleRectangleButton {
  protected int background_color = DImg.ccolor(255);
  protected PImage icon;
  public double icon_width = 0;

  public IconButton(LNZApplet sketch, double xi, double yi, double xf, double yf, PImage icon) {
    super(sketch, xi, yi, xf, yf);
    this.icon = icon;
    this.icon_width = yf - yi;
  }

  @Override
  public void setLocation(double xi, double yi, double xf, double yf) {
    super.setLocation(xi, yi, xf, yf);
    this.icon_width = yf - yi;
  }

  @Override
  public void update(int millis) {
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
    p.imageMode(PConstants.CORNER);
    p.image(this.icon, this.xi, this.yi, this.icon_width, this.icon_width);
    super.update(millis);
  }

  @Override
  public void writeText() {
    if (this.show_message) {
      p.fill(this.color_text);
      p.textAlign(PConstants.LEFT, PConstants.CENTER);
      p.textSize(this.text_size);
      if (this.adjust_for_text_descent) {
        p.text(this.message, this.xi + this.icon_width + 1, this.yCenter() - p.textDescent());
      }
      else {
        p.text(this.message, this.xi + this.icon_width + 1, this.yCenter());
      }
    }
  }
}