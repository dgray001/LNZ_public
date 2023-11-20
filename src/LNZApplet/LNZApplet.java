package LNZApplet;

import processing.core.*;

public class LNZApplet extends PApplet {
  /*
   * Flag for mouse/keyboard event propagation
   * Elements that have functions tied to these events have choose to set or
   * check the flag. Generally used to prevent the event from applying to
   * multiple elements.
   */
  // TODO: Use for button (and other?) hover events, not necessary for click/release
  public boolean halt_event_propagation = true;

  public void arc(double a, double b, double c, double d, double start, double stop, int mode) {
    super.arc((float)a, (float)b, (float)c, (float)d, (float)start, (float)stop, mode);
  }

  public void circle(double x, double y, double extent) {
    super.circle((float)x, (float)y, (float)extent);
  }

  public float constrain(double amt, double low, double high) {
    return super.constrain((float)amt, (float)low, (float)high);
  }

  public void ellipse(double a, double b, double c, double d) {
    super.ellipse((float)a, (float)b, (float)c, (float)d);
  }
  
  public void line(double x1, double y1, double x2, double y2) {
    super.line((float)x1, (float)y1, (float)x2, (float)y2);
  }
  
  public void line(double x1, double y1, double z1, double x2, double y2, double z2) {
    super.line((float)x1, (float)y1, (float)z1, (float)x2, (float)y2, (float)z2);
  }

  public void image(PImage img, double a, double b, double c, double d) {
    super.image(img, (float)a, (float)b, (float)c, (float)d);
  }

  public void image(PImage img, double a, double b) {
    super.image(img, (float)a, (float)b);
  }

  public float noise(double x, double y) {
    return super.noise((float)x, (float)y);
  }

  public void noiseDetail(int lod, double falloff) {
    super.noiseDetail(lod, (float)falloff);
  }

  public void rect(double a, double b, double c, double d) {
    super.rect((float)a, (float)b, (float)c, (float)d);
  }

  public void rect(double a, double b, double c, double d, double r) {
    super.rect((float)a, (float)b, (float)c, (float)d, (float)r);
  }

  public void rotate(double angle) {
    super.rotate((float)angle);
  }

  public static int round(double n) {
    return PApplet.round((float)n);
  }

  public void scale(double x, double y) {
    super.scale((float)x, (float)y);
  }

  public void strokeWeight(double weight) {
    super.strokeWeight((float)weight);
  }

  public void surfaceSetLocation(int x, int y) {
    super.surface.setLocation(x, y);
  }

  public void surfaceSetSize(int w, int h) {
    super.surface.setSize(w, h);
  }

  public void text(String str, double x, double y) {
    super.text(str, (float)x, (float)y);
  }

  public void textSize(double size) {
    super.textSize((float)size);
  }

  public void translate(double x, double y) {
    super.translate((float)x, (float)y);
  }

  public void triangle(double x1, double y1, double x2, double y2, double x3, double y3) {
    super.triangle((float)x1, (float)y1, (float)x2, (float)y2, (float)x3, (float)y3);
  }

  public void vertex(double x, double y) {
    super.vertex((float)x, (float)y);
  }

  public PImage getCurrImage() {
    PImage img = createImage(width, height, ARGB);
    img.loadPixels();
    this.loadPixels();
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int index = i + j * width;
        img.pixels[index] = pixels[index];
      }
    }
    img.updatePixels();
    return img;
  }
  
  public PImage getCurrImage(int xi, int yi, int xf, int yf) {
    PImage img = createImage(xf - xi, yf - yi, ARGB);
    img.loadPixels();
    this.loadPixels();
    for (int i = xi; i <= xf; i++) {
      for (int j = yi; j <= yf; j++) {
        int index = i + j * width;
        if (index < 0 || index >= pixels.length) {
          continue;
        }
        int img_index = (i - xi) + (j - yi) * img.width;
        if (img_index >= img.pixels.length) {
          continue;
        }
        img.pixels[img_index] = pixels[index];
      }
    }
    img.updatePixels();
    return img;
  }
}