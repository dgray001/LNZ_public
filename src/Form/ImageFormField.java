package Form;

import processing.core.*;
import LNZApplet.LNZApplet;

public class ImageFormField extends FormField {
  protected PImage img;
  protected double image_height;
  protected double image_w = 0;
  protected double image_h = 0;

  public ImageFormField(LNZApplet sketch, PImage img, double image_height) {
    super(sketch, "");
    this.img = img;
    this.image_height = image_height;
  }

  public void enable() {}
  public void disable() {}
  public void updateWidthDependencies() {
    double desired_width = this.img.width * this.image_height / this.img.height;
    if (this.field_width < desired_width) {
      this.image_w = this.field_width;
      this.image_h = this.img.height * this.image_w / this.img.width;
    }
    else {
      this.image_w = desired_width;
      this.image_h = this.image_height;
    }
  }

  public boolean focusable() {
    return false;
  }
  public void focus() {}
  public void defocus() {}
  public boolean focused() {
    return false;
  }

  public double getHeight() {
    return this.image_height;
  }

  public String getValue() {
    return this.message;
  }
  public void setValue(String newValue) {
    this.message = newValue;
  }

  public FormFieldSubmit updateField(int millis) {
    p.imageMode(PConstants.CENTER);
    p.image(this.img, 0.5 * this.field_width, 0.5 * this.image_height, this.image_w, this.image_h);
    return FormFieldSubmit.NONE;
  }
  public void mouseMoveField(float mX, float mY) {}
  public void mousePressField() {}
  public void mouseReleaseField(float mX, float mY) {}
  public void scrollField(int amount) {}
  public void keyPressField(int key, int keyCode) {}
  public void keyReleaseField(int key, int keyCode) {}
  public void submit() {}
}