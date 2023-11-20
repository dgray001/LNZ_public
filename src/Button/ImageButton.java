package Button;

import processing.core.*;
import LNZApplet.LNZApplet;
import DImg.DImg;

public abstract class ImageButton extends RectangleButton {
  public PImage img;
  protected int color_tint = DImg.ccolor(255);
  protected boolean overshadow_colors = false;

  public ImageButton(LNZApplet sketch, PImage img, double xi, double yi, double xf, double yf) {
    super(sketch, xi, yi, xf, yf);
    this.img = img;
  }

  @Override
  public void drawButton() {
    p.tint(this.color_tint);
    p.imageMode(PConstants.CORNERS);
    p.image(this.img, this.xi, this.yi, this.xf, this.yf);
    p.noTint();
    this.writeText();
    if (this.overshadow_colors) {
      p.fill(this.fillColor());
      p.stroke(this.fillColor());
      p.rectMode(PConstants.CORNERS);
      p.rect(this.xi, this.yi, this.xf, this.yf);
    }
  }

  public void setImg(PImage img) {
    this.img = img;
    this.img.resize(LNZApplet.round(this.buttonWidth()), LNZApplet.round(this.buttonHeight()));
  }
}