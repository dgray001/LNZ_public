package LNZModule;

import java.util.*;
import processing.core.*;
import DImg.DImg;
import LNZApplet.LNZApplet;
import Misc.Misc;

class Images {
  class ScaledImageData {
    private PImage img;
    private int scaled_width;
    ScaledImageData(PImage img, int scaled_width) {
      this.img = img;
      this.scaled_width = scaled_width;
    }
  }
  private LNZ p;

  private Map<String, PImage> imgs = new HashMap<String, PImage>();
  private Map<String, ScaledImageData> scaled_imgs = new HashMap<String, ScaledImageData>();
  private String base_path;
  protected boolean loaded_map_gifs = false;

  Images(LNZ sketch) {
    this.p = sketch;
    this.base_path = sketch.sketchPath("data/images/");
  }

  PImage getImage(String file_path) {
    if (this.imgs.containsKey(file_path)) {
      return this.imgs.get(file_path);
    }
    this.loadImageFile(file_path);
    return this.imgs.get(file_path);
  }

  PImage getScaledImage(String file_path, int scaled_width) {
    if (this.scaled_imgs.containsKey(file_path)) {
      ScaledImageData data = this.scaled_imgs.get(file_path);
      if (data.scaled_width == scaled_width) {
        return data.img;
      }
    }
    this.loadScaledImageFile(file_path, scaled_width);
    return this.scaled_imgs.get(file_path).img;
  }

  PImage getTransparentImage(String file_path) {
    if (this.imgs.containsKey(file_path + "***")) {
      return this.imgs.get(file_path + "***");
    }
    this.loadTransparentImageFile(file_path);
    return this.imgs.get(file_path + "***");
  }

  PImage getScaledTransparentImage(String file_path, int scaled_width) {
    if (this.scaled_imgs.containsKey(file_path + "***")) {
      ScaledImageData data = this.scaled_imgs.get(file_path + "***");
      if (data.scaled_width == scaled_width) {
        return data.img;
      }
    }
    this.loadScaledTransparentImageFile(file_path, scaled_width);
    return this.scaled_imgs.get(file_path + "***").img;
  }

  PGraphics getImageAsGraphic(String file_path) {
    PImage img = this.getImage(file_path);
    PGraphics graphic = p.createGraphics(img.width, img.height);
    graphic.beginDraw();
    graphic.image(img, 0, 0);
    graphic.endDraw();
    return graphic;
  }

  private void loadImageFiles(String ... file_paths) {
    for (String file_path : file_paths) {
      this.loadImageFile(file_path);
    }
  }
  private void loadImageFile(String file_path) {
    PImage img = p.loadImage(this.base_path + file_path);
    if (img == null) {
      p.global.log("Images: Missing image " + file_path + ".");
      this.imgs.put(file_path, Images.getBlackPixel());
      return;
    }
    this.imgs.put(file_path, img);
  }

  private void loadScaledImageFile(String file_path, int scaled_width) {
    PImage img = p.loadImage(this.base_path + file_path);
    if (img == null) {
      p.global.log("Images: Missing image " + file_path + ".");
      this.scaled_imgs.put(file_path, new ScaledImageData(Images.getBlackPixel(), 1));
      return;
    }
    img.resize(scaled_width, Math.round(img.height * (float)scaled_width / img.width));
    this.scaled_imgs.put(file_path, new ScaledImageData(img, scaled_width));
  }
  
  private void loadTransparentImageFile(String file_path) {
    PImage img = p.loadImage(this.base_path + file_path);
    if (img == null) {
      p.global.log("Images: Missing image " + file_path + ".");
      this.imgs.put(file_path + "***", Images.getBlackPixel());
      return;
    }
    DImg dimg = new DImg(p, img);
    dimg.makeTransparent(110);
    this.imgs.put(file_path + "***", dimg.img);
  }
  
  private void loadScaledTransparentImageFile(String file_path, int scaled_width) {
    PImage img = p.loadImage(this.base_path + file_path);
    if (img == null) {
      p.global.log("Images: Missing image " + file_path + ".");
      this.scaled_imgs.put(file_path + "***", new ScaledImageData(Images.getBlackPixel(), 1));
      return;
    }
    DImg dimg = new DImg(p, img);
    dimg.makeTransparent(110);
    img.resize(scaled_width, Math.round(img.height * (float)scaled_width / img.width));
    this.scaled_imgs.put(file_path + "***", new ScaledImageData(img, scaled_width));
  }

  void loadMapGifs() {
    if (this.loaded_map_gifs) {
      return;
    }
    this.loaded_map_gifs = true;
    // move gif
    for (int i = 0; i <= LNZ.gif_move_frames; i++) {
      this.loadImageFile("gifs/move/" + i + ".png");
    }
    // ability gifs
    for (int i = 0; i <= LNZ.gif_poof_frames; i++) {
      this.loadImageFile("gifs/poof/" + i + ".png");
    }
    for (int i = 0; i <= LNZ.gif_amphibiousLeap_frames; i++) {
      this.loadImageFile("gifs/amphibious_leap/" + i + ".png");
    }
    // explosion gifs
    for (int i = 0; i <= LNZ.gif_explosionBig_frames; i++) {
      this.loadImageFile("gifs/explosion_big/" + i + ".png");
    }
    for (int i = 0; i <= LNZ.gif_explosionCrackel_frames; i++) {
      this.loadImageFile("gifs/explosion_crackel/" + i + ".png");
    }
    for (int i = 0; i <= LNZ.gif_explosionFire_frames; i++) {
      this.loadImageFile("gifs/explosion_fire/" + i + ".png");
    }
    for (int i = 0; i <= LNZ.gif_explosionGreen_frames; i++) {
      this.loadImageFile("gifs/explosion_green/" + i + ".png");
    }
    for (int i = 0; i <= LNZ.gif_explosionNormal_frames; i++) {
      this.loadImageFile("gifs/explosion_normal/" + i + ".png");
    }
    // map gifs
    for (int i = 0; i <= LNZ.gif_fire_frames; i++) {
      this.loadImageFile("gifs/fire/" + i + ".png");
    }
    for (int i = 0; i <= LNZ.gif_lava_frames; i++) {
      this.loadImageFile("gifs/lava/" + i + ".png");
    }
    for (int i = 0; i <= LNZ.gif_drenched_frames; i++) {
      this.loadImageFile("gifs/drenched/" + i + ".png");
    }
    // other images
    for (int i = 1; i <= 11; i++) {
      this.loadImageFile("icons/tier_" + i + ".png");
    }
  }

  void loadMinigameImages(MinigameName name) {
    switch(name) {
      case CHESS:
        String p = "minigames/chess/";
        this.loadImageFiles(p + "black_bishop.png", p + "black_king.png",
        p + "black_knight.png", p + "black_pawn.png", p + "black_queen.png",
        p + "black_rook.png", p + "white_bishop.png", p + "white_king.png",
        p + "white_knight.png", p + "white_pawn.png", p + "white_queen.png",
        p + "white_rook.png");
        break;
      default:
        break;
    }
  }

  static PImage getBlackPixel() {
    PImage img = new PImage(1, 1, PConstants.RGB);
    img.loadPixels();
    img.pixels[0] = DImg.ccolor(0);
    img.updatePixels();
    return img;
  }

  static PImage getTransparentPixel() {
    PImage img = new PImage(1, 1, PConstants.ARGB);
    img.loadPixels();
    img.pixels[0] = DImg.ccolor(255, 0);
    img.updatePixels();
    return img;
  }

  static PImage getRandomPixel() {
    PImage img = new PImage(1, 1, PConstants.ARGB);
    img.loadPixels();
    img.pixels[0] = DImg.ccolor(Misc.randomInt(255), Misc.randomInt(255), Misc.randomInt(255));
    img.updatePixels();
    return img;
  }

  static PImage getColoredPixel(int c) {
    PImage img = new PImage(1, 1, PConstants.ARGB);
    img.loadPixels();
    img.pixels[0] = c;
    img.updatePixels();
    return img;
  }

  static PImage getCurrImage(LNZApplet sketch) {
    PImage img = sketch.createImage(sketch.width, sketch.height, PConstants.ARGB);
    img.loadPixels();
    sketch.loadPixels();
    for (int i = 0; i < sketch.width; i++) {
      for (int j = 0; j < sketch.height; j++) {
        int index = i + j * sketch.width;
        img.pixels[index] = sketch.pixels[index];
      }
    }
    img.updatePixels();
    return img;
  }

  static PImage getCurrImage(LNZApplet sketch, int xi, int yi, int xf, int yf) {
    PImage img = sketch.createImage(xf - xi, yf - yi, PConstants.ARGB);
    img.loadPixels();
    sketch.loadPixels();
    for (int i = xi; i <= xf; i++) {
      for (int j = yi; j <= yf; j++) {
        int index = i + j * sketch.width;
        if (index < 0 || index >= sketch.pixels.length) {
          continue;
        }
        int img_index = (i - xi) + (j - yi) * img.width;
        if (img_index >= img.pixels.length) {
          continue;
        }
        img.pixels[img_index] = sketch.pixels[index];
      }
    }
    img.updatePixels();
    return img;
  }
}