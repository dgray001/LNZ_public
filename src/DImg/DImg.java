package DImg;

import LNZApplet.LNZApplet;
import processing.core.*;

public class DImg {
	
	private PApplet p;

  public PImage img = null;
  private int img_mode = PConstants.CORNERS;
  public int gridX = 1;
  public int gridY = 1;

  // Constructor for blank image
  public DImg(PApplet sketch, int x, int y) {
    this.p = sketch;
    this.img = p.createImage(x, y, PConstants.ARGB);
  }
  // Constructor using predefined image
  public DImg(PApplet sketch, PImage img) {
    this.p = sketch;
    this.img = img;
  }

  public void mode(int imgMode) {
    switch(imgMode) {
      case PConstants.CORNERS:
      case PConstants.CORNER:
      case PConstants.CENTER:
        this.img_mode = imgMode;
        break;
      default:
        System.out.println("ERROR: imgMode invalid");
        break;
    }
  }

  public void setGrid(int x, int y) {
    if (x < 1 || y < 1) {
      return;
    }
    this.gridX = x;
    this.gridY = y;
  }

  public int gridWidth() { // pixels / grid unit
    return Math.round(this.img.width / this.gridX);
  }
  public int gridHeight() {
    return Math.round(this.img.height / this.gridY);
  }

  // Display functions
  public void display(float x, float y) {
    p.imageMode(this.img_mode);
    p.image(this.img, x, y);
  }
  public void display(float xi, float yi, float xf, float yf) {
    p.imageMode(this.img_mode);
    p.image(this.img, xi, yi, xf, yf);
  }

  // Return part of an image
  public PImage getImageSection(PImage img, int x, int y, int w, int h) {
    return img.get(x, y, w, h);
  }

  // Add image to part of this using width / height
  public void addImage(PImage newImg, int x, int y) {
    this.addImage(newImg, 0, 0, newImg.width, newImg.height, x, y, newImg.width, newImg.height);
  }
  public void addImage(PImage newImg, int x, int y, int w, int h) {
    this.addImage(newImg, 0, 0, newImg.width, newImg.height, x, y, w, h);
  }
  public synchronized void addImage(PImage newImg, int newImgX, int newImgY, int newImgW, int newImgH, int x, int y, int w, int h) {
    if (x < 0) {
      int scaled_dif = Math.round((-x * (float)newImgW) / w);
      w += x;
      newImgX += scaled_dif;
      newImgW -= scaled_dif;
      x = 0;
    }
    if (y < 0) {
      int scaled_dif = Math.round((-y * (float)newImgH) / h);
      h += y;
      newImgY += scaled_dif;
      newImgH -= scaled_dif;
      y = 0;
    }
    if (x + w > this.img.width) {
      int scaled_dif = Math.round(((x + w - this.img.width) * (float)newImg.width) / w);
      w = this.img.width - x;
      newImgW -= scaled_dif;
    }
    if (y + h > this.img.height) {
      int scaled_dif = Math.round(((y + h - this.img.height) * (float)newImg.height) / h);
      h = this.img.height - y;
      newImgH -= scaled_dif;
    }
    if (w < 1 || h < 1 || newImgW < 1 || newImgH < 1) {
      return;
    }
    if (newImgX < 0) {
      newImgW += newImgX;
      newImgX = 0;
    }
    if (newImgY < 0) {
      newImgH += newImgY;
      newImgY = 0;
    }
    if (newImgX + newImgW > newImg.width) {
      newImgW = newImg.width - newImgX;
    }
    if (newImgY + newImgH > newImg.height) {
      newImgH = newImg.height - newImgY;
    }
    // causes lines in isometric tiles so do manually
    //this.img.blend(newImg, newImgX, newImgY, newImgW, newImgH, x, y, w, h, PConstants.BLEND);
    this.img.loadPixels();
    newImg.loadPixels();
    float scaling_width = newImgW / (float)w;
    float scaling_height = newImgH / (float)h;
    for (int i = 0; i < h; i++) {
      int imgY = (int)(scaling_height * i + 0.5);
      for (int j = 0; j < w; j++) {
        int imgX = (int)(scaling_width * j + 0.5);
        int index = (y + i) * this.img.width + (x + j);
        int newImgIndex = (newImgY + imgY) * newImg.width + (newImgX + imgX);
        if (newImgIndex >= newImg.pixels.length) {
          continue;
        }
        int pixel_newImg = newImg.pixels[newImgIndex];
        int pixel_img = this.img.pixels[index];

        float r_source = pixel_newImg >> 16 & 0xFF;
        float g_source = pixel_newImg >> 8 & 0xFF;
        float b_source = pixel_newImg & 0xFF;
        float a_source = pixel_newImg >> 24 & 0xFF;
        float r_target = pixel_img >> 16 & 0xFF;
        float g_target = pixel_img >> 8 & 0xFF;
        float b_target = pixel_img & 0xFF;
        float a_target = pixel_img >> 24 & 0xFF;

        float factor_source = a_source / 255.0f;
        float factor_target = (1 - factor_source) * a_target / 255.0f;
        float r_final = PApplet.constrain(factor_source * r_source + factor_target * r_target, 0, 255);
        float g_final = PApplet.constrain(factor_source * g_source + factor_target * g_target, 0, 255);
        float b_final = PApplet.constrain(factor_source * b_source + factor_target * b_target, 0, 255);
        float a_final = PApplet.constrain(a_source + a_target, 0, 255);

        this.img.pixels[index] = DImg.ccolor(r_final, g_final, b_final, a_final);
      }
    }
    newImg.updatePixels();
    this.img.updatePixels();
  }

  // newImg is already scaled
  public synchronized void addScaledImage(PImage newImg, int x, int y) {
    int max_pixel = this.img.pixels.length;
    this.img.loadPixels();
    int i_start = Math.max(0, -y);
    int i_end = Math.min(this.img.height - y, newImg.height);
    int j_start = Math.max(0, -x);
    int j_end = Math.min(this.img.width - x, newImg.width);
    for (int i = i_start; i < i_end; i++) {
      for (int j = j_start; j < j_end; j++) {
        int index = (y + i) * this.img.width + (x + j);
        int newImgIndex = i * newImg.width + j;
        if (index < 0 || index >= max_pixel) {
          continue;
        }
        int pixel_newImg = newImg.pixels[newImgIndex];
        int pixel_img = this.img.pixels[index];

        float r_source = pixel_newImg >> 16 & 0xFF;
        float g_source = pixel_newImg >> 8 & 0xFF;
        float b_source = pixel_newImg & 0xFF;
        float a_source = pixel_newImg >> 24 & 0xFF;
        float r_target = pixel_img >> 16 & 0xFF;
        float g_target = pixel_img >> 8 & 0xFF;
        float b_target = pixel_img & 0xFF;
        float a_target = pixel_img >> 24 & 0xFF;

        float factor_source = a_source / 255.0f;
        float factor_target = (1 - factor_source) * a_target / 255.0f;
        float r_final = PApplet.constrain(factor_source * r_source + factor_target * r_target, 0, 255);
        float g_final = PApplet.constrain(factor_source * g_source + factor_target * g_target, 0, 255);
        float b_final = PApplet.constrain(factor_source * b_source + factor_target * b_target, 0, 255);
        float a_final = PApplet.constrain(a_source + a_target, 0, 255);

        this.img.pixels[index] = DImg.ccolor(r_final, g_final, b_final, a_final);
      }
    }
    this.img.updatePixels();
  }

  // Add image to part of this using percent of width / height
  public void addImagePercent(PImage newImg, float xP, float yP, float wP, float hP) {
    if (xP < 0.0 || yP < 0.0 || wP < 0.0 || hP < 0.0 || xP > 1.0 || yP > 1.0 || wP > 1.0 || hP > 1.0) {
      //p.global.log("DImg: addImagePercent coordinates out of range");
      return;
    }
    this.addImage(newImg, 0, 0, newImg.width, newImg.height,
      Math.round(this.img.width * xP), Math.round(this.img.height * yP),
      Math.round(this.img.width * wP), Math.round(this.img.height * hP));
  }
  // Add image to grid squares
  public void addImageGrid(PImage newImg, int x, int y) {
    this.addImageGrid(newImg, x, y, 1, 1);
  }
  public void addImageGrid(PImage newImg, int x, int y, int w, int h) {
    this.addImageGrid(newImg, 0, 0, newImg.width, newImg.height, x, y, w, h);
  }
  public void addImageGrid(PImage newImg, int newImgX, int newImgY, int newImgW, int newImgH, int x, int y, int w, int h) {
    this.addImage(newImg, newImgX, newImgY, newImgW, newImgH,
      Math.round(this.img.width * ((float)x / this.gridX)),
      Math.round(this.img.height * ((float)y / this.gridY)),
      Math.round(w * ((float)this.img.width / this.gridX)),
      Math.round(h * ((float)this.img.height / this.gridY)));
  }

  // make grid a specific color
  public void colorGrid(int c, int x, int y) {
    this.colorGrid(c, x, y, 1, 1);
  }
  public void colorGrid(int c, int x, int y, int w, int h) {
    this.img.loadPixels();
    for (int i = 0; i < h * this.img.height / this.gridY; i++) {
      for (int j = 0; j < w * this.img.width / this.gridX; j++) {
        int index = (y * this.img.height / this.gridY + i) * this.img.width +
          (x * this.img.width / this.gridX + j);
        try {
          this.img.pixels[index] = c;
        } catch(IndexOutOfBoundsException e) {}
      }
    }
    this.img.updatePixels();
  }

  // make rectangular grid region a specific color if in iso region
  public void colorIsoGrid(PImage iso_image, int c, int x, int y, int w, int h) {
    w *= this.gridWidth();
    h *= this.gridHeight();
    iso_image = DImg.resizeImage(p, iso_image, w, h);
    this.img.loadPixels();
    for (int i = 0; i < h; i++) {
      for (int j = 0; j < w; j++) {
        int iso_index = i * iso_image.width + j;
        int iso_pixel = iso_image.pixels[iso_index];
        int iso_a = iso_pixel >> 24 & 0xFF;
        if (iso_a == 0) {
          continue;
        }
        int index = (y * this.gridHeight() + i) * this.img.width + (x * this.gridWidth() + j);
        if (index < 0 || index >= this.img.pixels.length) {
          continue;
        }
        this.img.pixels[index] = c;
      }
    }
    this.img.updatePixels();
  }

  // my own copy function which accounts for transparency
  public void copyImage(PImage newImg, float x, float y, float w, float h) {
    this.img.loadPixels();
    float scaling_width = newImg.width / w;
    float scaling_height = newImg.height / h;
    for (int i = 0; i < h; i++) {
      int imgY = (int)(scaling_height * i + 0.5);
      for (int j = 0; j < w; j++) {
        int imgX = (int)(scaling_width * j + 0.5);

        int index = (int)((i + y) * this.img.width + (j + x));
        int img_index = imgY * newImg.width + imgX;
        try {
          float r_source = newImg.pixels[img_index] >> 16 & 0xFF;
          float g_source = newImg.pixels[img_index] >> 8 & 0xFF;
          float b_source = newImg.pixels[img_index] & 0xFF;
          float a_source = p.alpha(newImg.pixels[img_index]);
          float r_target = this.img.pixels[index] >> 16 & 0xFF;
          float g_target = this.img.pixels[index] >> 8 & 0xFF;
          float b_target = this.img.pixels[index] & 0xFF;
          float a_target = p.alpha(this.img.pixels[index]);

          double factor_source = a_source / 255.0;
          double factor_target = (1 - factor_source) * a_target / 255.0;
          float r_final = PApplet.constrain((float)(factor_source * r_source + factor_target * r_target), 0, 255);
          float g_final = PApplet.constrain((float)(factor_source * g_source + factor_target * g_target), 0, 255);
          float b_final = PApplet.constrain((float)(factor_source * b_source + factor_target * b_target), 0, 255);
          float a_final = PApplet.constrain((float)(a_source + a_target), 0, 255);

          this.img.pixels[index] = DImg.ccolor(r_final, g_final, b_final, a_final);
        } catch(IndexOutOfBoundsException e) {}
      }
    }
    this.img.updatePixels();
  }

  // image piece
  public PImage getImagePiece(int xi, int yi, int w, int h) {
    if (xi < 0) {
      w += xi;
      xi = 0;
    }
    if (yi < 0) {
      h += yi;
      yi = 0;
    }
    if (xi + w > this.img.width) {
      w = this.img.width - xi;
    }
    if (yi + h > this.img.height) {
      h = this.img.height - yi;
    }
    if (w <= 0 || h <= 0) {
      return p.createImage(1, 1, PConstants.ARGB);
    }
    PImage return_image = p.createImage(w, h, PConstants.ARGB);
    return_image.loadPixels();
    for (int i = 0; i < h; i++) {
      for (int j = 0; j < w; j++) {
        int index = (yi + i) * this.img.width + (xi + j);
        if (index < 0 || index >= this.img.pixels.length) {
          continue;
        }
        int return_index = i * w + j;
        return_image.pixels[return_index] = this.img.pixels[index];
      }
    }
    return_image.updatePixels();
    return return_image;
  }
  public PImage getImagePercent(float xp, float yp, float wp, float hp) {
    return this.getImagePiece(
      Math.round(xp * this.img.width), Math.round(yp * this.img.height),
      Math.round(wp * this.img.width), Math.round(hp * this.img.height));
  }
  public PImage getImageGridPiece(int x, int y) {
    return this.getImageGridPiece(x, y, 1, 1);
  }
  public PImage getImageGridPiece(int x, int y, int w, int h) {
    if (x < 0 || y < 0 || x >= this.gridX || y >= this.gridY) {
      //p.global.log("DImg: getImageGridPiece coordinate out of range");
      return p.createImage(1, 1, PConstants.ARGB);
    }
    if (w < 1 || h < 1 || x + w > this.gridX || y + h > this.gridY) {
      //p.global.log("DImg: getImageGridPiece coordinate out of range");
      return p.createImage(1, 1, PConstants.ARGB);
    }
    return this.getImagePiece(x * this.img.width / this.gridX, y * this.img.height / this.gridY,
      w * this.img.width / this.gridX, h * this.img.height / this.gridY);
  }

  // convolution
  public void convolution(float[][] matrix) {
    if (matrix.length % 2 != 1 || matrix[0].length % 2 != 1) {
      //p.global.log("DImg: convolution matrix invalid size.");
      return;
    }
    this.img.loadPixels();
    for (int i = 0; i < this.img.height; i++) {
      for (int j = 0; j < this.img.width; j++) {
        int index = i * this.img.width + j;
        float r_total = 0;
        float g_total = 0;
        float b_total = 0;
        for (int i_offset = 0; i_offset < matrix[0].length; i_offset++) {
          for (int j_offset = 0; j_offset < matrix.length; j_offset++) {
            int i_corrected = PApplet.constrain(i + i_offset - matrix[0].length / 2, 0, this.img.height);
            int j_corrected = PApplet.constrain(j + j_offset - matrix.length / 2, 0, this.img.width);
            int index_offset = PApplet.constrain(i_corrected * this.img.width + j_corrected, 0, this.img.pixels.length - 1);
            float factor = matrix[i_offset][j_offset];
            r_total += factor * (this.img.pixels[index_offset] >> 16 & 0xFF);
            g_total += factor * (this.img.pixels[index_offset] >> 8 & 0xFF);
            b_total += factor * (this.img.pixels[index_offset] & 0xFF);
          }
        }
        r_total = PApplet.constrain(r_total, 0, 255);
        g_total = PApplet.constrain(g_total, 0, 255);
        b_total = PApplet.constrain(b_total, 0, 255);
        this.img.pixels[index] = ccolor(r_total, g_total, b_total);
      }
    }
    this.img.updatePixels();
  }
  public void blur() {
    this.convolution(new float[][]{
      {(float)(1.0/9), (float)(1.0/9), (float)(1.0/9)},
      {(float)(1.0/9), (float)(1.0/9), (float)(1.0/9)},
      {(float)(1.0/9), (float)(1.0/9), (float)(1.0/9)}});
  }
  public void sharpen() {
    this.convolution(new float[][]{{-1, -1, -1}, {-1, 9, -1}, {-1, -1, -1}});
  }

  // Brighten
  public void brighten(float factor) {
    this.img.loadPixels();
    for (int i = 0; i < this.img.height; i++) {
      for (int j = 0; j < this.img.width; j++) {
        int index = i * this.img.width + j;
        if (index == 0) {
          continue;
        }
        int c = this.img.pixels[index];
        float r = PApplet.constrain((c >> 16 & 0xFF) * factor, 0, 255);
        float g = PApplet.constrain((c >> 8 & 0xFF) * factor, 0, 255);
        float b = PApplet.constrain((c & 0xFF) * factor, 0, 255);
        float a = p.alpha(c);
        this.img.pixels[index] = ccolor(r, g, b, a);
      }
    }
    this.img.updatePixels();
  }

  public void brightenGradient(float factor, float gradientDistance, float x, float y) {
    this.img.loadPixels();
    for (int i = 0; i < this.img.height; i++) {
      for (int j = 0; j < this.img.width; j++) {
        int index = i * this.img.width + j;
        double distance = Math.sqrt((i - y) * (i - y) + (j - x) * (j - x));
        double curr_factor = factor;
        if (distance < gradientDistance) {
          curr_factor = 1 + (factor - 1) * distance / gradientDistance;
        }
        int c = this.img.pixels[index];
        float r = PApplet.constrain((float)((c >> 16 & 0xFF) * curr_factor), 0, 255);
        float g = PApplet.constrain((float)((c >> 8 & 0xFF) * curr_factor), 0, 255);
        float b = PApplet.constrain((float)((c & 0xFF) * curr_factor), 0, 255);
        int col = ccolor(r, g, b, 255);
        this.img.pixels[index] = col;
      }
    }
    this.img.updatePixels();
  }

  // transparent
  public void makeTransparent() {
    this.makeTransparent(1);
  }
  public void makeTransparent(int alpha) {
    this.img.loadPixels();
    for (int i = 0; i < this.img.height; i++) {
      for (int j = 0; j < this.img.width; j++) {
        int index = i * this.img.width + j;
        if (index == 0) {
          continue;
        }
        float r = this.img.pixels[index] >> 16 & 0xFF;
        float g = this.img.pixels[index] >> 8 & 0xFF;
        float b = this.img.pixels[index] & 0xFF;
        float a = this.img.pixels[index] >> 24 & 0xFF;
        this.img.pixels[index] = ccolor(r, g, b, Math.min(a, alpha));
      }
    }
    this.img.updatePixels();
  }
  public void transparencyGradientFromPoint(double x, double y, double distance) {
    this.img.loadPixels();
    for (int i = 0; i < this.img.height; i++) {
      for (int j = 0; j < this.img.width; j++) {
        int index = i * this.img.width + j;
        if (index == 0) {
          continue;
        }
        float r = this.img.pixels[index] >> 16 & 0xFF;
        float g = this.img.pixels[index] >> 8 & 0xFF;
        float b = this.img.pixels[index] & 0xFF;
        double curr_distance = Math.sqrt((i - y) * (i - y) + (j - x) * (j - x));
        double alpha = 255;
        if (curr_distance < distance) {
          alpha = 255 * curr_distance / distance;
        }
        this.img.pixels[index] = ccolor(r, g, b, (float)alpha);
      }
    }
    this.img.updatePixels();
  }

  // color pixels
  public void colorPixels(int c) {
    this.img.loadPixels();
    for (int i = 0; i < this.img.height; i++) {
      for (int j = 0; j < this.img.width; j++) {
        int index = i * this.img.width + j;
        this.img.pixels[index] = c;
      }
    }
    this.img.updatePixels();
  }

  public void colorPixel(int x, int y, int c) {
    this.img.loadPixels();
    int index = x + y * this.img.width;
    if (index < 1 || index >= this.img.pixels.length) {
      return;
    }
    this.img.pixels[index] = c;
    this.img.updatePixels();
  }


  public static PImage createPImage(PApplet sketch, int c, int w, int h) {
    DImg dimg = new DImg(sketch, w, h);
    dimg.colorPixels(c);
    return dimg.img;
  }


  public static int ccolor(int gray) {
    return ccolor(gray, gray, gray, 255);
  }
  public static int ccolor(int gray, int a) {
    return ccolor(gray, gray, gray, a);
  }
  public static int ccolor(int r, int g, int b) {
    return ccolor(r, g, b, 255);
  }
  public static int ccolor(float r, float g, float b) {
    return ccolor(r, g, b, 255);
  }
  public static int ccolor(double r, double g, double b) {
    return ccolor(r, g, b, 255);
  }
  public static int ccolor(float r, float g, float b, float a) {
    return ccolor(Math.round(r), Math.round(g), Math.round(b), Math.round(a));
  }
  public static int ccolor(double r, double g, double b, double a) {
    return ccolor(Math.round(r), Math.round(g), Math.round(b), Math.round(a));
  }
  public static int ccolor(int r, int g, int b, int a) {
    return (a << 24) | (r << 16) | (g << 8) | b;
  }


  // color functions
  public static int brighten(int c) {
    return adjustColorBrightness(c, 1.05);
  }
  public static int darken(int c) {
    return adjustColorBrightness(c, 0.95);
  }
  public static int adjustColorBrightness(int c, double factor) {
    double r = Math.min(Math.max(factor * (c >> 16 & 0xFF), 0), 255);
    double g = Math.min(Math.max(factor * (c >> 8 & 0xFF), 0), 255);
    double b = Math.min(Math.max(factor * (c & 0xFF), 0), 255);
    double a = Math.min(Math.max(factor * (c >> 24 & 0xFF), 0), 255);
    return ccolor(r, g, b, a);
  }


  // resize image using nearest-neighbor interpolation
  public static PImage resizeImage(PApplet sketch, PImage img, int w, int h) {
    if (w <= 0 || h <= 0) {
      return sketch.createImage(1, 1, PConstants.ARGB);
    }
    float scaling_width = img.width / (float)w;
    float scaling_height = img.height / (float)h;
    PImage return_image = sketch.createImage(w, h, PConstants.ARGB);
    return_image.loadPixels();
    for (int i = 0; i < h; i++) {
      long imgY = (int)Math.floor(scaling_height * i);
      for (int j = 0; j < w; j++) {
        long imgX = (int)Math.floor(scaling_width * j); // must floor to avoid artifacts
        int index = i * w + j;
        int img_index = (int)(imgY * img.width + imgX);
        if (img_index < 0 || img_index >= img.pixels.length) {
          continue;
        }
        return_image.pixels[index] = img.pixels[img_index];
      }
    }
    return_image.updatePixels();
    return return_image;
  }


  // copy image pixel for pixel
  public static PImage copy(PApplet sketch, PImage img) {
    PImage return_image = sketch.createImage(img.width, img.height, PConstants.ARGB);
    img.loadPixels();
    return_image.loadPixels();
    for (int i = 0; i < img.height; i++) {
      for (int j = 0; j < img.width; j++) {
        int index = i * img.width + j;
        return_image.pixels[index] = img.pixels[index];
      }
    }
    return_image.updatePixels();
    img.updatePixels();
    return return_image;
  }


  // tint image
  public static void tint(PImage img, int c) {
    float c_a = (c >> 24 & 0xFF) / 255f;
    float c_r = (c >> 16 & 0xFF) / 255f;
    float c_g = (c >> 8 & 0xFF) / 255f;
    float c_b = (c & 0xFF) / 255f;
    img.loadPixels();
    for (int i = 0; i < img.height; i++) {
      for (int j = 0; j < img.width; j++) {
        int index = i * img.width + j;
        int color = img.pixels[index];
        int a = Math.round((color >> 24 & 0xFF) * c_a);
        int r = Math.round((color >> 16 & 0xFF) * c_r);
        int g = Math.round((color >> 8 & 0xFF) * c_g);
        int b = Math.round((color & 0xFF) * c_b);
        img.pixels[index] = DImg.ccolor(r, g, b, a);
      }
    }
    img.updatePixels();
  }
}