package Button;

import processing.core.*;
import LNZApplet.LNZApplet;

public abstract class ToggleButton extends ImageButton {
  public int toggle_index = 0;
  protected boolean click_toggle = true;
  protected PImage[] images;

  public ToggleButton(LNZApplet sketch, PImage[] images, double xi, double yi, double xf, double yf) {
    super(sketch, images[0], xi, yi, xf, yf);
    this.images = images;
  }

  public void setToggle(int toggle_index) {
    this.toggle_index = toggle_index;
    if (this.toggle_index < 0) {
      this.toggle_index = 0;
    }
    if (this.toggle_index >= this.images.length) {
      this.toggle_index = this.images.length - 1;
    }
  }

  public void toggle() {
    this.toggle_index++;
    if (this.toggle_index >= this.images.length) {
      this.toggle_index = 0;
    }
    this.setImg(this.images[this.toggle_index]);
  }

  public void click() {
    if (this.click_toggle) {
      this.toggle();
    }
  }

  public void release() {
    if (!this.hovered || this.click_toggle) {
      return;
    }
    this.toggle();
  }
}