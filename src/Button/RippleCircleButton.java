package Button;

import java.util.*;
import LNZApplet.LNZApplet;
import DImg.DImg;

public abstract class RippleCircleButton extends RippleRectangleButton {
  private ArrayList<Pixel> transparentPixels = new ArrayList<Pixel>();

  public RippleCircleButton(LNZApplet sketch, double xc, double yc, double r) {
    super(sketch, xc - r, yc - r, xc + r, yc + r);
    this.findTransparentPixels();
    this.refreshColor();
  }

  @Override
  public void setLocation(double xi, double yi, double xf, double yf) {
    super.setLocation(xi, yi, xf, yf);
    this.findTransparentPixels();
  }

  void findTransparentPixels() {
    this.transparentPixels = new ArrayList<Pixel>();
    double r = 0.5 * (this.xf - this.xi);
    for (int i = 0; i < this.img.height; i++) {
      for (int j = 0; j < this.img.width; j++) {
        double distance = Math.sqrt((r - i) * (r - i) + (r - j) * (r - j));
        if (distance > r) {
          this.transparentPixels.add(new Pixel(j, i, 0, 0));
        }
      }
    }
  }

  void colorTransparentPixels() {
    if (this.transparentPixels == null) {
      return;
    }
    this.img.loadPixels();
    for (Pixel px : this.transparentPixels) {
      int index = px.x + px.y * this.img.width;
      try {
        this.img.pixels[index] = DImg.ccolor(1, 0);
      } catch(ArrayIndexOutOfBoundsException e) {}
    }
    this.img.updatePixels();
  }

  @Override
  public void refreshColor() {
    super.refreshColor();
    this.colorTransparentPixels();
  }

  @Override
  void colorPixels() {
    super.colorPixels();
    this.colorTransparentPixels();
  }
}