package LNZModule;

import java.util.*;
import java.util.concurrent.*;
import java.io.PrintWriter;
import processing.core.*;
import DImg.DImg;
import LNZApplet.LNZApplet;
import Misc.Misc;

class GameMap extends AbstractGameMap {
  class TerrainDimgThread extends AbstractTerrainDimgThread {
    TerrainDimgThread(LNZ sketch) {}
    
    PImage updateTerrainDisplay() {
      if (GameMap.this.terrain_dimg == null) {
        return null;
      }
      PImage new_terrain_display = GameMap.this.terrain_dimg.getImagePiece(
        LNZApplet.round(this.image_grid_start.x * this.terrain_resolution),
        LNZApplet.round(this.image_grid_start.y * 0.5 * this.terrain_resolution),
        LNZApplet.round((this.image_grid_end.x - this.image_grid_start.x) * this.terrain_resolution),
        LNZApplet.round((this.image_grid_end.y - this.image_grid_start.y) * 0.5 * this.terrain_resolution));
      return DImg.resizeImage(p, new_terrain_display,
        LNZApplet.round(this.xf_map - this.xi_map),
        LNZApplet.round(this.yf_map - this.yi_map));
    }

    PImage updateFogDisplay() {
      if (GameMap.this.fog_dimg == null) {
        return null;
      }
      int new_width = LNZApplet.round(this.xf_map - this.xi_map);
      int new_height = LNZApplet.round(this.yf_map - this.yi_map);
      if (new_width < 1 || new_height < 1) {
        return null;
      }
      PImage new_fog_display = GameMap.this.fog_dimg.getImagePiece(
        LNZApplet.round(this.image_grid_start.x * LNZ.map_fogResolution),
        LNZApplet.round(this.image_grid_start.y * 0.5 * LNZ.map_fogResolution),
        LNZApplet.round((this.image_grid_end.x - this.image_grid_start.x) * LNZ.map_fogResolution),
        LNZApplet.round((this.image_grid_end.y - this.image_grid_start.y) * 0.5 * LNZ.map_fogResolution));
      DImg.resizeImage(p, new_fog_display, new_width, new_height);
      return new_fog_display;
    }
  }

  protected int mapWidth = 0;
  protected int mapHeight = 0;
  protected GameMapSquare[][] squares;

  protected DImg terrain_dimg;
  protected DImg fog_dimg;

  protected ConcurrentHashMap<Integer, Feature> features = new ConcurrentHashMap<Integer, Feature>();

  GameMap(LNZ sketch) {
    super(sketch);
  }
  GameMap(LNZ sketch, GameMapCode code, String folderPath) {
    super(sketch);
    this.code = code;
    this.mapName = GameMapCode.displayName(code);
    this.open(folderPath);
  }
  GameMap(LNZ sketch, String mapName, String folderPath) {
    super(sketch);
    this.mapName = mapName;
    this.open(folderPath);
  }
  GameMap(LNZ sketch, String mapName, int mapWidth, int mapHeight) {
    super(sketch);
    this.mapName = mapName;
    this.mapWidth = mapWidth;
    this.mapHeight = mapHeight;
    this.initializeSquares();
  }

  int mapXI() {
    return 0;
  }
  int mapYI() {
    return 0;
  }
  int mapXF() {
    return this.mapWidth;
  }
  int mapYF() {
    return this.mapHeight;
  }
  int currMapXI() {
    return 0;
  }
  int currMapYI() {
    return 0;
  }
  int currMapXF() {
    return this.mapWidth;
  }
  int currMapYF() {
    return this.mapHeight;
  }

  GameMapSquare mapSquare(int i, int j) {
    try {
      return this.squares[i][j];
    } catch(ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }

  void initializeSquares() {
    this.squares = new GameMapSquare[this.mapWidth][this.mapHeight];
    for (int i = 0; i < this.squares.length; i++) {
      for (int j = 0; j < this.squares[i].length; j++) {
        this.squares[i][j] = new GameMapSquare(p, i, j);
      }
    }
  }

  void initializeBackgroundImage() {
    this.terrain_dimg = new DImg(p,
      (this.mapWidth + this.mapHeight) * this.terrain_resolution,
      (this.mapWidth + this.mapHeight) * LNZApplet.round(0.5 * this.terrain_resolution));
    this.terrain_dimg.setGrid(this.mapWidth + this.mapHeight, this.mapWidth + this.mapHeight);
    for (int i = 0; i < this.mapWidth; i++) {
      for (int j = 0; j < this.mapHeight; j++) {
        GameMapSquare square = this.mapSquare(i, j);
        if (square == null) {
          continue;
        }
        PImage terrain_image = square.imageOverflows() ?
          square.defaultImage() : square.terrainImage();
        IntegerCoordinate image_coordinate = this.mapToGridImageCoordinate(new IntegerCoordinate(i, j));
        this.terrain_dimg.addImageGrid(terrain_image, image_coordinate.x, image_coordinate.y, 2, 2);
      }
    }
    for (Feature f : this.features.values()) {
      if (f == null || f.remove || f.terrainImageHeightOverflow() != 0 || !f.displaysImage()) {
        continue;
      }
      IntegerCoordinate image_coordinate = this.mapToGridImageCoordinate(new IntegerCoordinate(f.coordinate));
      this.terrain_dimg.addImageGrid(f.getImage(),
        image_coordinate.x, image_coordinate.y,
        f.sizeX + f.sizeY, f.sizeX + f.sizeY);
    }
    this.fog_dimg = new DImg(p,
      (this.mapWidth + this.mapHeight) * LNZ.map_fogResolution,
      (this.mapWidth + this.mapHeight) * LNZApplet.round(0.5 * LNZ.map_fogResolution));
    this.fog_dimg.setGrid(this.mapWidth + this.mapHeight, this.mapWidth + this.mapHeight);
  }

  void colorFogGrid(int c, IntegerCoordinate grid) {
    IntegerCoordinate image_coordinate = this.mapToGridImageCoordinate(grid);
    this.fog_dimg.colorIsoGrid(p.global.images.getImage("terrain/fog.png"),
      c, image_coordinate.x, image_coordinate.y, 2, 2);
  }

  void terrainImageGrid(PImage img, IntegerCoordinate grid, int w, int h) {
    if (this.terrain_dimg == null) {
      return;
    }
    IntegerCoordinate image_grid = this.mapToGridImageCoordinate(grid);
    image_grid.x -= h - 1;
    this.terrain_dimg.addImageGrid(img, image_grid.x, image_grid.y, 2 * w, 2 * h);
  }

  void colorTerrainGrid(int c, IntegerCoordinate grid, int w, int h) {
    if (this.terrain_dimg == null) {
      return;
    }
    IntegerCoordinate image_grid = this.mapToGridImageCoordinate(grid);
    this.terrain_dimg.colorIsoGrid(p.global.images.getImage("terrain/default.png"),
      c, image_grid.x, image_grid.y, 2 * w, 2 * h);
  }

  synchronized void startTerrainDimgThread() {
    if (this.terrain_dimg_thread != null && this.terrain_dimg_thread.isAlive()) {
      this.terrain_dimg_thread.update_terrain_display = true;
      return;
    }
    this.terrain_dimg_thread = new TerrainDimgThread(p);
    this.terrain_dimg_thread.start();
  }

  void actuallyAddFeature(int code, Feature f) {
    this.features.put(code, f);
  }

  @Override
  void addItem(Item i, int code, boolean auto_disappear) {
    super.addItem(i, code, auto_disappear);
    i.disappearing = false; // items don't disappear on prebuilt maps
  }

  void featureAddedMapSquareNotFound(IntegerCoordinate coordinate, Feature f) {
    p.global.errorMessage("ERROR: Couldn't find square with coordinates " +
      coordinate.x + ", " + coordinate.y + " where feature with code " + f.map_key + " was added.");
  }

  void featureRemovedMapSquareNotFound(IntegerCoordinate coordinate, Feature f) {
    p.global.errorMessage("ERROR: Couldn't find square with coordinates " +
      coordinate.x + ", " + coordinate.y + " where feature with code " + f.map_key + " was removed.");
  }

  Feature getFeature(int code) {
    return this.features.get(code);
  }

  Collection<Feature> features() {
    return this.features.values();
  }

  void updateFeatures(int time_elapsed) {
    Iterator<Map.Entry<Integer,Feature>> feature_iterator = this.features.entrySet().iterator();
    while(feature_iterator.hasNext()) {
      Map.Entry<Integer, Feature> entry = (Map.Entry<Integer, Feature>)feature_iterator.next();
      this.updateFeature(entry.getValue(), feature_iterator, time_elapsed);
    }
  }

  void updateFeaturesCheckRemovalOnly() {
    Iterator<Map.Entry<Integer,Feature>> feature_iterator = this.features.entrySet().iterator();
    while(feature_iterator.hasNext()) {
      Map.Entry<Integer, Feature> entry = (Map.Entry<Integer, Feature>)feature_iterator.next();
      if (entry.getValue().remove) {
        this.removeFeature(entry.getKey());
        feature_iterator.remove();
      }
    }
  }


  Coordinate mapToGridImageCoordinate(Coordinate map) {
    double x = map.x - map.y + this.mapHeight;
    double y = map.x + map.y;
    return new Coordinate(x, y);
  }
  IntegerCoordinate mapToGridImageCoordinate(IntegerCoordinate map) {
    int x = map.x - map.y + this.mapHeight - 1;
    int y = map.x + map.y;
    return new IntegerCoordinate(x, y);
  }

  Coordinate gridImageToMapCoordinate(Coordinate image) {
    double x = 0.5 * (image.y + image.x - this.mapHeight);
    double y = 0.5 * (image.y - image.x + this.mapHeight);
    return new Coordinate(x, y);
  }

  void saveTerrain(PrintWriter file) {
    file.println("dimensions: " + this.mapWidth + ", " + this.mapHeight);
    for (int i = this.mapXI(); i < this.mapXF(); i++) {
      for (int j = this.mapYI(); j < this.mapYF(); j++) {
        GameMapSquare square = this.mapSquare(i, j);
        switch(square.terrain_id) {
          case 151: // Grass, light
          case 152: // Grass, green
            square.timer_square = Integer.MAX_VALUE - 1; // prevent grass maturing/spreading on campaigns
            break;
          default:
            break;
        }
        file.println("terrain: " + i + ", " + j + ": " + square.terrain_id +
          ", " + square.base_elevation + ", " + square.explored +
          ", " + square.timer_square);
      }
    }
    // add feature data
    for (Map.Entry<Integer, Feature> entry : this.features.entrySet()) {
      file.println("nextFeatureKey: " + entry.getKey());
      file.println(entry.getValue().fileString());
    }
  }

  void addImplementationSpecificData(String datakey, String data) {
    switch(datakey) {
      case "dimensions":
        String[] dimensions = PApplet.split(data, ',');
        if (dimensions.length < 2) {
          p.global.errorMessage("ERROR: Map missing dimensions in data: " + data + ".");
          this.mapWidth = 1;
          this.mapHeight = 1;
        }
        else {
          this.mapWidth = Misc.toInt(PApplet.trim(dimensions[0]));
          this.mapHeight = Misc.toInt(PApplet.trim(dimensions[1]));
        }
        this.initializeSquares();
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not recognized for GameMap object.");
        break;
    }
  }
}