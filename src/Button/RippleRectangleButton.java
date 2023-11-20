package Button;

import java.util.*;
import processing.core.*;
import LNZApplet.LNZApplet;
import DImg.DImg;

public abstract class RippleRectangleButton extends ImageButton {
  class Pixel {
    protected int x;
    protected int y;
    protected double x_pixel;
    protected double y_pixel;
    Pixel(int x, int y, double x_pixel, double y_pixel) {
      this.x = x;
      this.y = y;
      this.x_pixel = x_pixel;
      this.y_pixel = y_pixel;
    }
    double distance(float mX, float mY) {
      return Math.sqrt((mX - this.x_pixel) * (mX - this.x_pixel) +
        (mY - this.y_pixel) * (mY - this.y_pixel));
    }
  }

  protected int ripple_time = 250;
  protected int ripple_timer = 0;
  protected int number_buckets = 50;
  protected HashMap<Integer, ArrayList<Pixel>> buckets;
  protected float last_mX = 0;
  protected float last_mY = 0;
  protected float clickX = 0;
  protected float clickY = 0;
  protected double max_ripple_distance = 0;

  public RippleRectangleButton(LNZApplet sketch) {
    super(sketch, sketch.createImage(1, 1, PConstants.ARGB), 0, 0, 0, 0);
  }
  public RippleRectangleButton(LNZApplet sketch, double xi, double yi, double xf, double yf) {
    super(sketch, sketch.createImage((int)(xf - xi), (int)(yf - yi), PConstants.ARGB), xi, yi, xf, yf);
    this.refreshColor();
    this.max_ripple_distance = Math.max(this.buttonWidth(), this.buttonHeight());
  }

  @Override
  public void setLocation(double xi, double yi, double xf, double yf) {
    super.setLocation(xi, yi, xf, yf);
    this.max_ripple_distance = Math.max(this.buttonWidth(), this.buttonHeight());
    if (this.buttonWidth() > 0 && this.buttonHeight() > 0) {
      this.setImg(p.createImage((int)(xf - xi), (int)(yf - yi), PConstants.ARGB));
      this.refreshColor();
    }
  }

  @Override
  public void update(int millis) {
    int time_elapsed = millis - this.last_update_time;
    if (this.use_time_elapsed) {
      time_elapsed = millis;
    }
    super.update(millis);
    if (this.ripple_timer > 0) {
      this.ripple_timer -= time_elapsed;
      if (this.ripple_timer <= 0) {
        this.refreshColor();
      }
      else {
        this.colorPixels();
      }
    }
  }

  public void refreshColor() {
    DImg dimg = new DImg(this.p, this.img);
    dimg.colorPixels(this.fillColor());
    this.img = dimg.img;
    this.ripple_timer = 0;
  }

  void initializeRipple() {
    this.buckets = new HashMap<Integer, ArrayList<Pixel>>();
    for (int i = 0; i < this.number_buckets; i++) {
      this.buckets.put(i, new ArrayList<Pixel>());
    }
    double key_multiplier = this.ripple_time / this.number_buckets;
    for (int i = 0; i < this.img.height; i++) {
      for (int j = 0; j < this.img.width; j++) {
        double x = this.xi + this.buttonWidth() * j / this.img.width;
        double y = this.yi + this.buttonHeight() * i / this.img.height;
        Pixel p = new Pixel(j, i, x, y);
        double distance = p.distance(this.clickX, this.clickY);
        int timer = (int)(this.ripple_time * (1 - distance / this.max_ripple_distance) / key_multiplier);
        if (this.buckets.containsKey(timer)) {
          this.buckets.get(timer).add(p);
        }
      }
    }
    this.ripple_timer = this.ripple_time;
  }

  void colorPixels() {
    DImg dimg = new DImg(p, this.img);
    //float curr_distance = this.max_ripple_distance * (this.ripple_time - this.ripple_timer) / this.ripple_time;
    double key_multiplier = this.ripple_time / this.number_buckets;
    for (Map.Entry<Integer, ArrayList<Pixel>> entry : this.buckets.entrySet()) {
      if (entry.getKey() * key_multiplier > this.ripple_timer) {
        for (Pixel px : entry.getValue()) {
          dimg.colorPixel(px.x, px.y, this.color_click);
        }
        entry.getValue().clear();
      }
    }
  }

  @Override
  public void mouseMove(float mX, float mY) {
    this.last_mX = mX;
    this.last_mY = mY;
    super.mouseMove(mX, mY);
  }

  public void hover() {
    this.refreshColor();
  }

  public void dehover() {
    this.refreshColor();
  }

  public void click() {
    this.clickX = this.last_mX;
    this.clickY = this.last_mY;
    this.initializeRipple();
  }

  public void release() {
    this.refreshColor();
  }
}