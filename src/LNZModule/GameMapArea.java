package LNZModule;

import java.util.*;
import java.util.concurrent.*;
import java.io.PrintWriter;
import processing.core.*;
import DImg.DImg;
import FileSystem.FileSystem;
import LNZApplet.LNZApplet;
import Misc.Misc;

class GameMapArea extends AbstractGameMap {

  class TerrainDimgThread extends AbstractTerrainDimgThread {

    class TerrainDimgPieceThread extends Thread {

      private Chunk chunk;

      TerrainDimgPieceThread(Chunk chunk) {
        super("TerrainDimgPieceThread");
        this.setDaemon(true);
        this.chunk = chunk;
      }

      @Override
      public String toString() {
        return "!!";
      }

      @Override
      public void run() {
        Coordinate chunk_grid_start = this.chunk.gridImageStart();
        Coordinate chunk_grid_end = this.chunk.gridImageEnd();
        if (chunk_grid_end.x < TerrainDimgThread.this.image_grid_start.x ||
          chunk_grid_end.y < TerrainDimgThread.this.image_grid_start.y ||
          chunk_grid_start.x > TerrainDimgThread.this.image_grid_end.x ||
          chunk_grid_start.y > TerrainDimgThread.this.image_grid_end.y) {
          this.complete();
          return;
        }
        DImg chunk_terrain_dimg = this.chunk.terrain_dimg;
        if (chunk_terrain_dimg == null) {
          this.complete();
          return;
        }
        chunk_grid_start = chunk_grid_start.maxR(TerrainDimgThread.this.image_grid_start);
        chunk_grid_end = chunk_grid_end.minR(TerrainDimgThread.this.image_grid_end);
        if ((this.chunk.coordinate.x + this.chunk.coordinate.y) % 2 != 0) {
          chunk_grid_start.add(LNZ.map_chunkWidth);
          chunk_grid_end.add(LNZ.map_chunkWidth);
        }
        double chunk_image_grid_xi = Misc.modulo(chunk_grid_start.x, 2 * LNZ.map_chunkWidth);
        double chunk_image_grid_yi = Misc.modulo(chunk_grid_start.y, 2 * LNZ.map_chunkWidth);
        double chunk_image_grid_xf = Misc.modulo(chunk_grid_end.x, 2 * LNZ.map_chunkWidth);
        double chunk_image_grid_yf = Misc.modulo(chunk_grid_end.y, 2 * LNZ.map_chunkWidth);
        if (chunk_image_grid_xf == 0) {
          chunk_image_grid_xf = 2 * LNZ.map_chunkWidth;
        }
        if (chunk_image_grid_yf == 0) {
          chunk_image_grid_yf = 2 * LNZ.map_chunkWidth;
        }
        PImage chunk_terrain_piece = chunk_terrain_dimg.getImagePiece(
          LNZApplet.round(chunk_image_grid_xi * TerrainDimgThread.this.terrain_resolution),
          LNZApplet.round(chunk_image_grid_yi * 0.5 * TerrainDimgThread.this.terrain_resolution),
          LNZApplet.round((chunk_image_grid_xf - chunk_image_grid_xi) * TerrainDimgThread.this.terrain_resolution),
          LNZApplet.round((chunk_image_grid_yf - chunk_image_grid_yi) * 0.5 * TerrainDimgThread.this.terrain_resolution));
        int piece_width = LNZApplet.round((chunk_image_grid_xf - chunk_image_grid_xi) * TerrainDimgThread.this.zoom);
        int piece_height = LNZApplet.round((chunk_image_grid_yf - chunk_image_grid_yi) * 0.5 * TerrainDimgThread.this.zoom);
        if (piece_width < 1 || piece_height < 1) {
          this.complete();
          return;
        }
        if ((this.chunk.coordinate.x + this.chunk.coordinate.y) % 2 != 0) {
          chunk_grid_start.subtract(LNZ.map_chunkWidth);
          chunk_grid_end.subtract(LNZ.map_chunkWidth);
        }
        chunk_terrain_piece.resize(piece_width, piece_height);
        // DImg::addScaledImage is synchronized
        TerrainDimgThread.this.new_terrain_display.addScaledImage(
          chunk_terrain_piece,
          LNZApplet.round((chunk_grid_start.x - TerrainDimgThread.this.
            image_grid_start.x) * TerrainDimgThread.this.zoom),
          LNZApplet.round((chunk_grid_start.y - TerrainDimgThread.this.
            image_grid_start.y) * 0.5 * TerrainDimgThread.this.zoom));
        this.complete();
      }

      void complete() {
        TerrainDimgThread.this.chunk_latch.countDown();
      }
    }

    private List<TerrainDimgPieceThread> threads = new ArrayList<TerrainDimgPieceThread>();
    private CountDownLatch chunk_latch;
    private DImg new_terrain_display;

    TerrainDimgThread() {}

    PImage updateTerrainDisplay() {
      this.new_terrain_display = new DImg(p,
        LNZApplet.round(this.xf_map - this.xi_map),
        LNZApplet.round(this.yf_map - this.yi_map));
      if (this.terrain_resolution == 0 || new_terrain_display.img == null) {
        return null;
      }
      Iterator<Map.Entry<IntegerCoordinate, Chunk>> it = GameMapArea.this.chunk_reference.entrySet().iterator();
      while(it.hasNext()) {
        Map.Entry<IntegerCoordinate, Chunk> entry = it.next();
        Chunk chunk = entry.getValue();
        if (chunk == null) {
          continue;
        }
        threads.add(new TerrainDimgPieceThread(chunk));
      }
      this.chunk_latch = new CountDownLatch(this.threads.size());
      for (TerrainDimgPieceThread thread : this.threads) {
        thread.start();
      }
      try {
        this.chunk_latch.await();
      } catch(InterruptedException e) {}
      this.threads.clear();
      return new_terrain_display.img;
    }

    PImage updateFogDisplay() {
      return p.createImage(1, 1, PConstants.ARGB);
    }
  }


  /*class HangingImage {
    private IntegerCoordinate chunk_coordinate;
    private PImage newImg;
    private int newImgX;
    private int newImgY;
    private int newImgW;
    private int newImgH;
    private int x;
    private int y;
    private int w;
    private int h;
    HangingImage(IntegerCoordinate chunk_coordinate, PImage newImg, int newImgX,
      int newImgY, int newImgW, int newImgH, int x, int y, int w, int h) {
      this.chunk_coordinate = chunk_coordinate;
      this.newImg = newImg;
      this.newImgX = newImgX;
      this.newImgY = newImgY;
      this.newImgW = newImgW;
      this.newImgH = newImgH;
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
    }
    boolean resolve() {
      Chunk chunk = GameMapArea.this.chunk_reference.get(this.chunk_coordinate);
      if (chunk == null) {
        return false;
      }
      chunk.terrain_dimg.addImageGrid(this.newImg, this.newImgX, this.newImgY,
        this.newImgW, this.newImgH, this.x, this.y, this.w, this.h);
      return true;
    }
  }*/


  /*class HangingFeaturesThread extends Thread {
    HangingFeaturesThread() {
      super("HangingFeaturesThread");
      this.setDaemon(true);
    }
    @Override
    void run() {
      while(true) {
        delay(200);
        if (GameMapArea.this == null || GameMapArea.this.nullify) {
          return;
        }
        GameMapArea.this.checkHangingFeatures();
        GameMapArea.this.checkHangingImages();
      }
    }
  }*/


  class Chunk {
    class LoadChunkThread extends Thread {
      private Random random_object;

      LoadChunkThread(int mapSeed) {
        super("LoadChunkThread");
        this.setDaemon(true);
        this.random_object = new Random(mapSeed + Chunk.this.coordinate.hashCode());
        this.random_object.nextDouble();
        this.random_object.nextDouble();
      }

      @Override
      public String toString() {
        return "!!";
      }

      @Override
      public void run() {
        Chunk.this.initializeTerrain();
        if (FileSystem.fileExists(p, Chunk.this.fileName())) {
          Chunk.this.load();
        }
        else {
          Chunk.this.generate(this.random_object);
        }
        if (Chunk.this.refresh_player_location) {
          Chunk.this.refreshPlayerLocation();
        }
        Chunk.this.applyFogHandling();
      }
    }

    private LNZ p;

    private GameMapSquare[][] squares;
    private ConcurrentHashMap<Integer, Feature> features = new ConcurrentHashMap<Integer, Feature>();
    private DImg terrain_dimg;
    private DImg fog_dimg;
    private IntegerCoordinate coordinate;
    private Biome biome = Biome.NONE;
    private int timer_spawn_units = LNZ.map_timerChunkSpawnUnits;

    private LoadChunkThread thread;
    private boolean refresh_player_location = false;
    private boolean remove = false; // used to avoid concurrent modification error if trying to remove while generating chunk

    Chunk(LNZ sketch, IntegerCoordinate coordinate) {
      this.p = sketch;
      this.squares = new GameMapSquare[LNZ.map_chunkWidth][LNZ.map_chunkWidth];
      for (int i = 0; i < this.squares.length; i++) {
        for (int j = 0; j < this.squares[i].length; j++) {
          this.squares[i][j] = new GameMapSquare(p, i, j);
        }
      }
      this.coordinate = coordinate;
    }

    private void initializeTerrain() {
      this.terrain_dimg = new DImg(p,
        2 * LNZ.map_chunkWidth * GameMapArea.this.terrain_resolution,
        LNZ.map_chunkWidth * GameMapArea.this.terrain_resolution);
      this.terrain_dimg.setGrid(2 * LNZ.map_chunkWidth, 2 * LNZ.map_chunkWidth);
      for (int i = 0; i < this.squares.length; i++) {
        for (int j = 0; j < this.squares[i].length; j++) {
          GameMapSquare square = this.squares[i][j];
          PImage terrain_image = square.imageOverflows() ?
            square.defaultImage() : square.terrainImage();
          if (i == 0 && j == 0) {
            terrain_image = p.global.images.getImage("drafts/_1x1.png");
          }
          IntegerCoordinate image_coordinate = GameMapArea.this.mapToGridImageCoordinate(new IntegerCoordinate(i, j));
          this.terrain_dimg.addImageGrid(terrain_image, image_coordinate.x, image_coordinate.y, 2, 2);
        }
      }
      this.fog_dimg = new DImg(p,
        2 * LNZ.map_chunkWidth * LNZ.map_fogResolution,
        LNZ.map_chunkWidth * LNZ.map_fogResolution);
      this.fog_dimg.setGrid(2 * LNZ.map_chunkWidth, 2 * LNZ.map_chunkWidth);
    }

    void loadChunk() {
      this.thread = new LoadChunkThread(GameMapArea.this.seed);
      this.thread.start();
    }

    String fileName() {
      return (GameMapArea.this.map_folder + "/" + this.coordinate.x + this.coordinate.y + ".chunk.lnz");
    }

    int chunkXI() {
      return this.coordinate.x * LNZ.map_chunkWidth;
    }
    int chunkYI() {
      return this.coordinate.y * LNZ.map_chunkWidth;
    }
    int chunkXF() {
      return (this.coordinate.x + 1) * LNZ.map_chunkWidth;
    }
    int chunkYF() {
      return (this.coordinate.y + 1) * LNZ.map_chunkWidth;
    }

    Coordinate gridImageStart() {
      double x = LNZ.map_chunkWidth * (this.coordinate.x - this.coordinate.y);
      double y = LNZ.map_chunkWidth * (this.coordinate.x + this.coordinate.y);
      return new Coordinate(x, y);
    }
    Coordinate gridImageEnd() {
      double x = LNZ.map_chunkWidth * (2 + this.coordinate.x - this.coordinate.y);
      double y = LNZ.map_chunkWidth * (2 + this.coordinate.x + this.coordinate.y);
      return new Coordinate(x, y);
    }

    void refreshPlayerLocation() {
      this.refresh_player_location = false;
      Unit u = GameMapArea.this.units.get(0);
      if (u == null || u.remove) {
        return;
      }
      if (!GameMapArea.this.coordinateOf(u.coordinate).equals(this.coordinate)) {
        return;
      }
      u.resolveFloorHeight(GameMapArea.this);
      u.curr_height = u.floor_height;
      GameMapSquare square = GameMapArea.this.mapSquare(u.coordinate);
      if (square != null) {
        square.addUnit(u);
      }
    }

    void applyFogHandling() {
      for (int i = 0; i < this.squares.length; i++) {
        for (int j = 0; j < this.squares[i].length; j++) {
          GameMapSquare square = this.squares[i][j];
          if (square == null) {
            continue;
          }
          switch(GameMapArea.this.fog_handling) {
            case DEFAULT:
              if (square.mapEdge()) {
                this.colorFogGrid(LNZ.color_transparent, i, j);
              }
              else if (!square.explored) {
                this.colorFogGrid(LNZ.color_black, i, j);
              }
              else if (!square.visible) {
                this.colorFogGrid(GameMapArea.this.fog_color, i, j);
              }
              else {
                this.colorFogGrid(LNZ.color_transparent, i, j);
              }
              break;
            case NONE:
              square.explored = true;
              square.visible = true;
              this.colorFogGrid(LNZ.color_transparent, i, j);
              break;
            case NOFOG:
              square.visible = true;
              if (square.mapEdge()) {
                this.colorFogGrid(LNZ.color_transparent, i, j);
              }
              else if (!square.explored) {
                this.colorFogGrid(LNZ.color_black, i, j);
              }
              else {
                this.colorFogGrid(LNZ.color_transparent, i, j);
              }
              break;
            case EXPLORED:
              square.explored = true;
              if (square.mapEdge()) {
                this.colorFogGrid(LNZ.color_transparent, i, j);
              }
              else if (!square.visible) {
                this.colorFogGrid(GameMapArea.this.fog_color, i, j);
              }
              else {
                this.colorFogGrid(LNZ.color_transparent, i, j);
              }
              break;
          }
        }
      }
    }

    void colorFogGrid(int c, int i, int j) {
      GameMapArea.this.colorFogGrid(c, i, j);
    }

    void trySpawnUnits(Level level) {
      if (GameMapArea.this.units.size() > LNZ.map_maxUnits) {
        return;
      }
      BiomeSpawnReturn unit_spawn_return = BiomeSpawnReturn.unitSpawnReturn(
        this.biome, GameMapArea.this.units.size());
      if (!unit_spawn_return.spawn_unit) {
        return;
      }
      if (unit_spawn_return.only_spawn_night && !level.isNight()) {
        this.timer_spawn_units = (int)(0.1 * LNZ.map_timerChunkSpawnUnits);
        return;
      }
      double spawn_x = 0;
      double spawn_y = 0;
      if (unit_spawn_return.limit_by_terrain) {
        ArrayList<IntegerCoordinate> valid_squares = new ArrayList<IntegerCoordinate>();
        for (int i = 0; i < this.squares.length; i++) {
          for (int j = 0; j < this.squares[i].length; j++) {
            if (unit_spawn_return.terrain_ids.contains(this.squares[i][j].terrain_id)) {
              valid_squares.add(new IntegerCoordinate(i, j));
            }
          }
        }
        if (valid_squares.size() == 0) {
          this.timer_spawn_units = (int)(0.1 * LNZ.map_timerChunkSpawnUnits);
          return;
        }
        else {
          IntegerCoordinate spawn_square = valid_squares.get(Misc.randomInt(valid_squares.size()));
          spawn_x = this.chunkXI() + spawn_square.x + Math.random();
          spawn_y = this.chunkYI() + spawn_square.y + Math.random();
        }
      }
      else {
        spawn_x = Misc.randomDouble(this.chunkXI(), this.chunkXF());
        spawn_y = Misc.randomDouble(this.chunkYI(), this.chunkYF());
      }
      if (this.badSpawnSpace(spawn_x, spawn_y, level)) {
        this.timer_spawn_units = (int)(0.1 * LNZ.map_timerChunkSpawnUnits);
        return;
      }
      Unit u = new Unit(p, unit_spawn_return.unit_id);
      double x_facing = Misc.randomDouble(-1.0, 1.0);
      double y_facing = 1.0 - Math.abs(x_facing);
      if (Misc.randomChance(0.5)) {
        y_facing = -y_facing;
      }
      u.setFacing(x_facing, y_facing);
      GameMapArea.this.addUnit(u, spawn_x, spawn_y);
    }

    boolean badSpawnSpace(double spawn_x, double spawn_y, Level level) {
      GameMapSquare square = GameMapArea.this.mapSquare(spawn_x, spawn_y);
      if (square == null || square.isWall()) {
        return true;
      }
      if (level.player != null) {
        if (Math.abs(spawn_x - level.player.coordinate.x) < LNZ.map_unitSpawnMinDistance &&
          Math.abs(spawn_y - level.player.coordinate.y) < LNZ.map_unitSpawnMinDistance) {
          return true;
        }
      }
      return false;
    }

    void generate(Random random_object) {
      this.biome = GameMapArea.this.getBiome(this.coordinate);
      // Generate BiomeReturns from Perlin noise
      BiomeReturn[][] biome_return = new BiomeReturn[LNZ.map_chunkWidth][LNZ.map_chunkWidth];
      for (int i = 0; i < biome_return.length; i++) {
        for (int j = 0; j < biome_return[i].length; j++) {
          IntegerCoordinate square = new IntegerCoordinate(this.chunkXI() + i, this.chunkYI() + j);
          float noise_value = GameMapArea.this.perlinNoise(square, false);
          biome_return[i][j] = Biome.processPerlinNoise(this.biome, noise_value, random_object, new IntegerCoordinate(i, j));
        }
      }
      // Base terrain from perlin noise and biome
      for (int i = 0; i < this.squares.length; i++) {
        for (int j = 0; j < this.squares[i].length; j++) {
          this.squares[i][j].setTerrain(biome_return[i][j].terrain_code);
          IntegerCoordinate image_coordinate = GameMapArea.this.
            mapToGridImageCoordinate(new IntegerCoordinate(i, j));
          this.terrain_dimg.addImageGrid(this.squares[i][j].terrainImage(),
            image_coordinate.x, image_coordinate.y, 2, 2);
        }
      }
      // Base features from perlin noise and biome
      for (int i = 0; i < this.squares.length; i++) {
        for (int j = 0; j < this.squares[i].length; j++) {
          for (BiomeReturnFeature biome_return_feature : biome_return[i][j].features) {
            Feature f = new Feature(p, biome_return_feature.feature_id,
              this.chunkXI() + i + biome_return_feature.x_adjustment,
              this.chunkYI() + j + biome_return_feature.y_adjustment);
            if (biome_return_feature.specify_feature_toggle) {
              f.toggle = biome_return_feature.feature_toggle;
            }
            if (biome_return_feature.specify_feature_number) {
              f.number = biome_return_feature.feature_number;
            }
            boolean skip_feature = false;
            if (biome_return_feature.check_overlap) {
              for (int f_x = LNZApplet.round(f.xi()); f_x < LNZApplet.round(f.xf()); f_x++) {
                for (int f_y = LNZApplet.round(f.yi()); f_y < LNZApplet.round(f.yf()); f_y++) {
                  if (f_x >= this.chunkXF() || f_y >= this.chunkYF()) {
                    skip_feature = true;
                    break;
                  }
                  if (this.squares[f_x - this.chunkXI()][f_y - this.chunkYI()].features.size() > 0) {
                    skip_feature = true;
                    break;
                  }
                }
              }
            }
            if (skip_feature) { 
              break;
            }
            GameMapArea.this.addFeature(f, false);
          }
        }
      }
      this.save();
      GameMapArea.this.refreshTerrainImage();
    }

    void load() {
      String[] lines = p.loadStrings(this.fileName());
      if (lines == null) {
        p.global.errorMessage("ERROR: Reading chunk at path " + this.fileName() + " but no file exists.");
        return;
      }

      Stack<ReadFileObject> object_queue = new Stack<ReadFileObject>();

      Feature curr_feature = null;
      int next_feature_key = 0;
      Item curr_item = null;
      Item curr_item_internal = null; // for item inventories

      for (String line : lines) {
        String[] line_split = PApplet.split(line, ':');
        if (line_split.length < 2) {
          continue;
        }
        String datakey = PApplet.trim(line_split[0]);
        String data = PApplet.trim(line_split[1]);
        for (int i = 2; i < line_split.length; i++) {
          data += ":" + line_split[i];
        }
        if (datakey.equals("new")) {
          ReadFileObject type = ReadFileObject.objectType(PApplet.trim(line_split[1]));
          switch(type) {
            case FEATURE:
              if (line_split.length < 3) {
                p.global.errorMessage("ERROR: Feature ID missing in Feature constructor.");
                break;
              }
              object_queue.push(type);
              curr_feature = new Feature(p, Misc.toInt(PApplet.trim(line_split[2])));
              break;
            case ITEM:
              if (line_split.length < 3) {
                p.global.errorMessage("ERROR: Item ID missing in Item constructor.");
                break;
              }
              object_queue.push(type);
              if (curr_item == null) {
                curr_item = new Item(p, Misc.toInt(PApplet.trim(line_split[2])));
              }
              else {
                if (curr_item_internal != null) {
                  p.global.errorMessage("ERROR: Can't create an internal item inside an internal item.");
                  break;
                }
                if (curr_item.inventory == null) {
                  p.global.errorMessage("ERROR: Can't create an internal item " +
                    "inside an item with no inventory.");
                  break;
                }
                curr_item_internal = new Item(p, Misc.toInt(PApplet.trim(line_split[2])));
              }
              break;
            default:
              p.global.errorMessage("ERROR: Can't add a " + type + " type to Chunk data.");
              break;
          }
        }
        else if (datakey.equals("end")) {
          ReadFileObject type = ReadFileObject.objectType(PApplet.trim(line_split[1]));
          if (object_queue.empty()) {
            p.global.errorMessage("ERROR: Tring to end a " + type.name + " object but not inside any object.");
          }
          else if (type.name.equals(object_queue.peek().name)) {
            switch(object_queue.pop()) {
              case FEATURE:
                if (curr_feature == null) {
                  p.global.errorMessage("ERROR: Trying to end a null feature.");
                  break;
                }
                GameMapArea.this.addFeature(curr_feature, false, next_feature_key);
                curr_feature = null;
                break;
              case ITEM:
                if (curr_item == null) {
                  p.global.errorMessage("ERROR: Trying to end a null item.");
                  break;
                }
                if (object_queue.empty()) {
                  p.global.errorMessage("ERROR: Trying to end an item not inside any other object.");
                  break;
                }
                switch(object_queue.peek()) {
                  case FEATURE:
                    if (line_split.length < 3) {
                      p.global.errorMessage("ERROR: Ending item in feature inventory " +
                        "but no slot information given.");
                      break;
                    }
                    if (curr_feature == null) {
                      p.global.errorMessage("ERROR: Trying to add item to null feature.");
                      break;
                    }
                    if (curr_feature.inventory == null) {
                      p.global.errorMessage("ERROR: Trying to add item to feature " +
                        "inventory but curr_feature has no inventory.");
                      break;
                    }
                    if (PApplet.trim(line_split[2]).equals("item_array")) {
                      if (curr_feature.items == null) {
                        p.global.errorMessage("ERROR: Trying to add item to feature " +
                          "item array but curr_feature has no item array.");
                        break;
                      }
                      curr_feature.items.add(curr_item);
                      break;
                    }
                    if (!Misc.isInt(PApplet.trim(line_split[2]))) {
                      p.global.errorMessage("ERROR: Ending item in feature inventory " +
                        "but no slot information given.");
                      break;
                    }
                    int slot_number = Misc.toInt(PApplet.trim(line_split[2]));
                    if (slot_number < 0 || slot_number >= curr_feature.inventory.slots.size()) {
                      p.global.errorMessage("ERROR: Trying to add item to feature " +
                        "inventory but slot number " + slot_number + " out of range.");
                      break;
                    }
                    curr_feature.inventory.slots.get(slot_number).item = curr_item;
                    break;
                  case ITEM:
                    if (curr_item_internal == null) {
                      p.global.errorMessage("ERROR: Trying to end a null internal item.");
                      break;
                    }
                    if (line_split.length < 3 || !Misc.isInt(PApplet.trim(line_split[2]))) {
                      p.global.errorMessage("ERROR: Ending item in item inventory " +
                        "but no slot number given.");
                      break;
                    }
                    if (curr_item == null) {
                      p.global.errorMessage("ERROR: Trying to add item to null item.");
                      break;
                    }
                    if (curr_item.inventory == null) {
                      p.global.errorMessage("ERROR: Trying to add item to item " +
                        "inventory but curr_item has no inventory.");
                      break;
                    }
                    int item_slot_number = Misc.toInt(PApplet.trim(line_split[2]));
                    if (item_slot_number < 0 || item_slot_number >= curr_item.inventory.slots.size()) {
                      p.global.errorMessage("ERROR: Trying to add item to feature " +
                        "inventory but slot number " + item_slot_number + " out of range.");
                      break;
                    }
                    curr_item.inventory.slots.get(item_slot_number).item = curr_item_internal;
                    break;
                  default:
                    p.global.errorMessage("ERROR: Trying to end an item inside a " +
                      object_queue.peek().name + " in Chunk data.");
                    break;
                }
                if (curr_item_internal == null) {
                  curr_item = null;
                }
                else {
                  curr_item_internal = null;
                }
                break;
              default:
                p.global.errorMessage("ERROR: Trying to end a " + type.name + " which is not known.");
                break;
            }
          }
          else {
            p.global.errorMessage("ERROR: Tring to end a " + type.name + " object " +
              "but current object is a " + object_queue.peek().name + ".");
          }
        }
        else if (!object_queue.empty()) {
          switch(object_queue.peek()) {
            case FEATURE:
              if (curr_feature == null) {
                p.global.errorMessage("ERROR: Trying to add feature data to a null feature.");
                break;
              }
              curr_feature.addData(datakey, data);
              break;
            case ITEM:
              if (curr_item == null) {
                p.global.errorMessage("ERROR: Trying to add item data to a null item.");
                break;
              }
              if (curr_item_internal != null) {
                curr_item_internal.addData(datakey, data);
              }
              else {
                curr_item.addData(datakey, data);
              }
              break;
            default:
              break;
          }
        }
        else {
          switch(datakey) {
            case "biome":
              this.biome = Biome.biome(data);
              break;
            case "nextFeatureKey":
              next_feature_key = Misc.toInt(data);
              break;
            case "terrain":
              String[] data_split = PApplet.split(data, ':');
              if (data_split.length < 2) {
                p.global.errorMessage("ERROR: Terrain missing dimension in data: " + data + ".");
                break;
              }
              String[] terrain_dimensions = PApplet.split(data_split[0], ',');
              if (terrain_dimensions.length < 2) {
                p.global.errorMessage("ERROR: Terrain dimensions missing dimension in data: " + data + ".");
                break;
              }
              int terrain_x = Misc.toInt(PApplet.trim(terrain_dimensions[0]));
              int terrain_y = Misc.toInt(PApplet.trim(terrain_dimensions[1]));
              String[] terrain_values = PApplet.split(data_split[1], ',');
              if (terrain_values.length < 3) {
                p.global.errorMessage("ERROR: Terrain values missing dimension in data: " + data + ".");
                break;
              }
              int terrain_id = Misc.toInt(PApplet.trim(terrain_values[0]));
              int terrain_height = Misc.toInt(PApplet.trim(terrain_values[1]));
              try { // TODO: clean up
                GameMapSquare square = this.squares[terrain_x][terrain_y];
                square.setTerrain(terrain_id);
                square.base_elevation = terrain_height;
                IntegerCoordinate image_coordinate = GameMapArea.this.
                  mapToGridImageCoordinate(new IntegerCoordinate(terrain_x, terrain_y));
                this.terrain_dimg.addImageGrid(square.terrainImage(),
                  image_coordinate.x, image_coordinate.y, 2, 2);
                if (Misc.toBoolean(PApplet.trim(terrain_values[2]))) {
                  square.explored = true;
                }
                try {
                  int terrain_timer = Misc.toInt(PApplet.trim(terrain_values[3]));
                  this.squares[terrain_x][terrain_y].timer_square = terrain_timer;
                } catch(Exception e) {}
              }
              catch(ArrayIndexOutOfBoundsException e) {}
              break;
            default:
              p.global.errorMessage("ERROR: Datakey " + datakey + " not recognized.");
              break;
          }
        }
      }
      GameMapArea.this.refreshTerrainImage();
    }

    void save() {
      PrintWriter file = p.createWriter(this.fileName());
      file.println("biome: " + this.biome.fileName());
      for (int i = 0; i < this.squares.length; i++) {
        for (int j = 0; j < this.squares[i].length; j++) {
          file.println("terrain: " + i + ", " + j + ": " + this.squares[i][j].terrain_id +
            ", " + this.squares[i][j].base_elevation + ", " + this.squares[i][j].explored +
            ", " + this.squares[i][j].timer_square);
        }
      }
      // save features
      for (Map.Entry<Integer, Feature> entry : this.features.entrySet()) {
        file.println("nextFeatureKey: " + entry.getKey());
        file.println(entry.getValue().fileString());
      }
      file.flush();
      file.close();
    }
  }


  protected AreaLocation area_location = AreaLocation.NONE;

  protected ConcurrentHashMap<IntegerCoordinate, Chunk> chunk_reference = new ConcurrentHashMap<IntegerCoordinate, Chunk>();
  protected IntegerCoordinate current_chunk = new IntegerCoordinate(0, 0);
  protected int timer_refresh_chunks = LNZ.map_refreshChunkTimer;

  protected String map_folder;
  protected int mapEdgeXi = 0;
  protected int mapEdgeYi = 0;
  protected int mapEdgeXf = 1;
  protected int mapEdgeYf = 1;
  protected IntegerCoordinate default_spawn_chunk = new IntegerCoordinate(0, 0);
  protected int chunk_view_radius = 1;
  protected int seed = (int)Misc.map(Math.random(), 0, 1, 0, Integer.MAX_VALUE - 1); // will get set in open() if seed saved

  // prevents nullptr on perlinNoise() since noise/noiseDetail/noiseSeed not thread-safe (maybe??)
  protected boolean waiting_for_noise_initialization = true;

  // keeps track of features that are hanging over unloaded chunks when added / removed
 // protected ConcurrentHashMap<IntegerCoordinate, ConcurrentHashMap<Integer, Feature>> hanging_features =
    //new ConcurrentHashMap<IntegerCoordinate, ConcurrentHashMap<Integer, Feature>>();
  //protected Queue<HangingImage> hanging_images = new ConcurrentLinkedQueue<HangingImage>();
  //protected HangingFeaturesThread hanging_features_thread;


  GameMapArea(LNZ p, String mapName, String map_folder) {
    super(p);
    this.mapName = mapName;
    this.map_folder = map_folder;
  }

  void initializeArea() {
    p.noiseSeed(this.seed);
    p.noiseDetail(LNZ.map_noiseOctaves, 0.55);
    this.waiting_for_noise_initialization = false;
    this.refreshChunks();
    this.startTerrainDimgThread();
    //this.hanging_features_thread = new HangingFeaturesThread();
    //this.hanging_features_thread.start(); // runs continuously
  }


  synchronized float perlinNoise(IntegerCoordinate coordinate, boolean chunk_noise) {
    if (coordinate == null) {
      return 0;
    }
    try {
      if (chunk_noise) {
        return p.noise(coordinate.x * LNZ.map_chunkPerlinMultiplier + LNZ.map_noiseOffsetX,
          coordinate.y * LNZ.map_chunkPerlinMultiplier + LNZ.map_noiseOffsetY);
      }
      else {
        return p.noise(coordinate.x * LNZ.map_mapPerlinMultiplier + LNZ.map_noiseOffsetX,
          coordinate.y * LNZ.map_mapPerlinMultiplier + LNZ.map_noiseOffsetY);
      }
    } catch(NullPointerException e) {
      return 0;
    }
  }

  Biome getBiome(IntegerCoordinate coordinate) {
    float noise_value = this.perlinNoise(coordinate, true);
    return AreaLocation.getBiomeFromPerlinNoise(this.area_location, coordinate, noise_value, this.seed);
  }


  int mapXI() {
    return LNZ.map_chunkWidth * this.mapEdgeXi;
  }
  int mapYI() {
    return LNZ.map_chunkWidth * this.mapEdgeYi;
  }
  int mapXF() {
    return LNZ.map_chunkWidth * this.mapEdgeXf;
  }
  int mapYF() {
    return LNZ.map_chunkWidth * this.mapEdgeYf;
  }
  int currMapXI() {
    return LNZ.map_chunkWidth * Math.max(this.current_chunk.x - this.chunk_view_radius, this.mapEdgeXi);
  }
  int currMapYI() {
    return LNZ.map_chunkWidth * Math.max(this.current_chunk.y - this.chunk_view_radius, this.mapEdgeYi);
  }
  int currMapXF() {
    return LNZ.map_chunkWidth * Math.min(this.current_chunk.x + this.chunk_view_radius + 1, this.mapEdgeXf);
  }
  int currMapYF() {
    return LNZ.map_chunkWidth * Math.min(this.current_chunk.y + this.chunk_view_radius + 1, this.mapEdgeYf);
  }


  double defaultSpawnX() {
    return PApplet.constrain(this.default_spawn_chunk.x * LNZ.map_chunkWidth,
      this.mapXI(), this.mapXF()) + 0.5 * LNZ.map_chunkWidth;
  }
  double defaultSpawnY() {
    return PApplet.constrain(this.default_spawn_chunk.y * LNZ.map_chunkWidth,
      this.mapYI(), this.mapYF()) + 0.5 * LNZ.map_chunkWidth;
  }
  void viewDefaultChunk() {
    this.current_chunk = this.default_spawn_chunk;
    this.setViewLocation(this.defaultSpawnX(), this.defaultSpawnY());
  }


  GameMapSquare mapSquare(int i, int j) {
    try {
      IntegerCoordinate coordinate = this.coordinateOf(i, j);
      Chunk chunk = this.chunk_reference.get(coordinate);
      if (chunk == null) {
        return null;
      }
      return chunk.squares[Math.floorMod(i, LNZ.map_chunkWidth)][Math.floorMod(j, LNZ.map_chunkWidth)];
    } catch(ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }
  IntegerCoordinate coordinateOf(IntegerCoordinate coordinate) {
    return this.coordinateOf(coordinate.x, coordinate.y);
  }
  IntegerCoordinate coordinateOf(Coordinate coordinate) {
    return this.coordinateOf((int)coordinate.x, (int)coordinate.y);
  }
  IntegerCoordinate coordinateOf(int i, int j) { // chunk coordinate of
    return new IntegerCoordinate(
      Math.floorDiv(i, LNZ.map_chunkWidth),
      Math.floorDiv(j, LNZ.map_chunkWidth));
  }

  void initializeSquares() {}
  synchronized void refreshChunks() {
    boolean refresh_view = false;
    this.current_chunk = this.getCurrentChunk();
    IntegerCoordinate player_chunk = this.getPlayerChunk();
    // remove unnecessary chunks from memory
    Iterator<Map.Entry<IntegerCoordinate, Chunk>> it = this.chunk_reference.entrySet().iterator();
    while(it.hasNext()) {
      Map.Entry<IntegerCoordinate, Chunk> entry = it.next();
      IntegerCoordinate coordinate = entry.getKey();
      if (entry.getValue().remove || coordinate.x > this.current_chunk.x + this.chunk_view_radius ||
        coordinate.x < this.current_chunk.x - this.chunk_view_radius ||
        coordinate.y > this.current_chunk.y + this.chunk_view_radius ||
        coordinate.y < this.current_chunk.y - this.chunk_view_radius) {
        entry.getValue().save();
        it.remove();
      }
    }
    // add needed new chunks
    for (int i = this.current_chunk.x - this.chunk_view_radius; i <= this.current_chunk.x + this.chunk_view_radius; i++) {
      for (int j = this.current_chunk.y - this.chunk_view_radius; j <= this.current_chunk.y + this.chunk_view_radius; j++) {
        IntegerCoordinate coordinate = new IntegerCoordinate(i, j);
        if (!this.coordinateInMap(coordinate)) {
          continue;
        }
        if (this.chunk_reference.containsKey(coordinate)) {
          continue;
        }
        Chunk new_chunk = new Chunk(p, coordinate);
        this.chunk_reference.put(coordinate, new_chunk);
        if (coordinate.equals(player_chunk)) {
          new_chunk.refresh_player_location = true;
        }
        new_chunk.loadChunk();
        if (this.current_chunk.equals(coordinate)) {
          refresh_view = true;
        }
      }
    }
    this.timer_refresh_chunks = LNZ.map_refreshChunkTimer;
    if (refresh_view) {
      this.refreshViewLocation();
    }
  }
  boolean coordinateInMap(IntegerCoordinate coordinate) {
    if (coordinate.x < this.mapEdgeXi || coordinate.x >= this.mapEdgeXf ||
      coordinate.y < this.mapEdgeYi || coordinate.y >= this.mapEdgeYf) {
      return false;
    }
    return true;
  }

  // prevents generation of chunks too far from player (if player unit exists)
  IntegerCoordinate getCurrentChunk() {
    if (this.units.containsKey(0)) {
      return this.coordinateOf(this.units.get(0).coordinate);
    }
    return new IntegerCoordinate(this.view.divideR(LNZ.map_chunkWidth));
  }

  // returns chunk player is in or null if no player exists
  IntegerCoordinate getPlayerChunk() {
    if (this.units.containsKey(0)) {
      return this.coordinateOf(this.units.get(0).coordinate);
    }
    return null;
  }

  @Override
  Biome getBiomeAt(double x, double y) {
    IntegerCoordinate chunk_coordinate = new IntegerCoordinate(
      LNZApplet.round(Math.floor(x / LNZ.map_chunkWidth)),
      LNZApplet.round(Math.floor(y / LNZ.map_chunkWidth)));
    Chunk chunk = this.chunk_reference.get(chunk_coordinate);
    try {
      return chunk.biome;
    } catch(NullPointerException e) {}
    return Biome.NONE;
  }

  void initializeBackgroundImage() {}

  void colorFogGrid(int c, IntegerCoordinate grid) {
    Chunk chunk = this.chunk_reference.get(this.coordinateOf(grid));
    if (chunk == null || chunk.fog_dimg == null) {
      return;
    }
    IntegerCoordinate image_grid = this.mapToGridImageCoordinate(grid);
    image_grid.x = Misc.modulo(image_grid.x, 2 * LNZ.map_chunkWidth);
    image_grid.y = Misc.modulo(image_grid.y, 2 * LNZ.map_chunkWidth);
    chunk.fog_dimg.colorIsoGrid(p.global.images.getImage("terrain/fog.png"), c,
      Math.floorMod(image_grid.x, LNZ.map_chunkWidth),
      Math.floorMod(image_grid.y, LNZ.map_chunkWidth), 4, 4);
  }

  void terrainImageGrid(PImage img, IntegerCoordinate grid, int w, int h) {
    Chunk chunk = this.chunk_reference.get(this.coordinateOf(grid));
    if (chunk == null || w < 1 || h < 1 || chunk.terrain_dimg == null) {
      // data corruption
      return;
    }
    IntegerCoordinate image_grid = this.mapToGridImageCoordinate(grid);
    int relative_x = Misc.modulo(image_grid.x, 2 * LNZ.map_chunkWidth);
    int relative_y = Misc.modulo(image_grid.y, 2 * LNZ.map_chunkWidth);
    relative_x -= h - 1;
    chunk.terrain_dimg.addImageGrid(img, relative_x, relative_y, 2 * w, 2 * h);
    // now check for hanging
    // TODO: rest of this shit isn't updated for isometric (hanging feature)
    boolean x_hanging = false;
    boolean y_hanging = false;
    if (relative_x + w > LNZ.map_chunkWidth) {
      x_hanging = true;
    }
    if (relative_y + h > LNZ.map_chunkWidth) {
      y_hanging = true;
    }
    if (x_hanging && y_hanging) {
      int remaining_width = relative_x + w - LNZ.map_chunkWidth;
      int img_width = LNZApplet.round(img.width * (float)remaining_width / w);
      int remaining_height = relative_y + h - LNZ.map_chunkWidth;
      int img_height = LNZApplet.round(img.height * (float)remaining_height / h);
      IntegerCoordinate x_edge = this.coordinateOf(grid.x + w, grid.y);
      Chunk x_chunk = this.chunk_reference.get(x_edge);
      if (x_chunk == null) {
      }
      else {
        x_chunk.terrain_dimg.addImageGrid(img, img.width - img_width, 0, img_width,
          img.height - img_height, 0, relative_y, remaining_width, LNZ.map_chunkWidth - relative_y);
      }
      IntegerCoordinate y_edge = this.coordinateOf(grid.x, grid.y + h);
      Chunk y_chunk = this.chunk_reference.get(y_edge);
      if (y_chunk == null) {
      }
      else {
        y_chunk.terrain_dimg.addImageGrid(img, 0, img.height - img_height, img.width
          - img_width, img_height, relative_x, 0, LNZ.map_chunkWidth - relative_x, remaining_height);
      }
      IntegerCoordinate xy_edge = this.coordinateOf(grid.x + w, grid.y + h);
      Chunk xy_chunk = this.chunk_reference.get(xy_edge);
      if (xy_chunk == null) {
      }
      else {
        xy_chunk.terrain_dimg.addImageGrid(img, img.width - img_width, img.height - img_height,
          img_width, img_height, 0, 0, remaining_width, remaining_height);
      }
    }
    else if (x_hanging) {
      IntegerCoordinate x_edge = this.coordinateOf(grid.x + w, grid.y);
      Chunk x_chunk = this.chunk_reference.get(x_edge);
      int remaining_width = relative_x + w - LNZ.map_chunkWidth;
      int img_width = LNZApplet.round(img.width * (double)remaining_width / w);
      if (x_chunk == null) {
      }
      else {
        x_chunk.terrain_dimg.addImageGrid(img, img.width - img_width, 0, img_width,
          img.height, 0, relative_y, remaining_width, h);
      }
    }
    else if (y_hanging) {
      IntegerCoordinate y_edge = this.coordinateOf(grid.x, grid.y + h);
      Chunk y_chunk = this.chunk_reference.get(y_edge);
      int remaining_height = relative_y + h - LNZ.map_chunkWidth;
      int img_height = LNZApplet.round(img.height * (double)remaining_height / h);
      if (y_chunk == null) {
      }
      else {
        y_chunk.terrain_dimg.addImageGrid(img, 0, img.height - img_height, img.width,
          img_height, relative_x, 0, w, remaining_height);
      }
    }
  }

  void colorTerrainGrid(int c, IntegerCoordinate grid, int w, int h) {
    Chunk chunk = this.chunk_reference.get(this.coordinateOf(grid));
    if (chunk == null || chunk.terrain_dimg == null) {
      return;
    }
    IntegerCoordinate image_grid = this.mapToGridImageCoordinate(grid);
    image_grid.x = Misc.modulo(image_grid.x, 2 * LNZ.map_chunkWidth);
    image_grid.y = Misc.modulo(image_grid.y, 2 * LNZ.map_chunkWidth);
    chunk.terrain_dimg.colorIsoGrid(p.global.images.getImage("terrain/default.png"), c,
      Math.floorMod(image_grid.x, LNZ.map_chunkWidth),
      Math.floorMod(image_grid.y, LNZ.map_chunkWidth), 2 * w, 2 * h);
  }

  synchronized void startTerrainDimgThread() {
    if (this.terrain_dimg_thread != null && this.terrain_dimg_thread.isAlive()) {
      this.terrain_dimg_thread.update_terrain_display = true;
      return;
    }
    if (this.waiting_for_noise_initialization) {
      return;
    }
    this.terrain_dimg_thread = new TerrainDimgThread();
    this.terrain_dimg_thread.start();
  }
  

  Coordinate mapToGridImageCoordinate(Coordinate map) {
    int x_chunk = Math.floorDiv((int)Math.floor(map.x), LNZ.map_chunkWidth);
    int y_chunk = Math.floorDiv((int)Math.floor(map.y), LNZ.map_chunkWidth);
    map.x = Misc.modulo(map.x, LNZ.map_chunkWidth);
    map.y = Misc.modulo(map.y, LNZ.map_chunkWidth);
    double x = map.x - map.y + LNZ.map_chunkWidth;
    double y = map.x + map.y;
    x += LNZ.map_chunkWidth * (x_chunk - y_chunk);
    y += LNZ.map_chunkWidth * (x_chunk + y_chunk);
    return new Coordinate(x, y);
  }
  IntegerCoordinate mapToGridImageCoordinate(IntegerCoordinate map) {
    int x_chunk = Math.floorDiv(map.x, LNZ.map_chunkWidth);
    int y_chunk = Math.floorDiv(map.y, LNZ.map_chunkWidth);
    map.x = Misc.modulo(map.x, LNZ.map_chunkWidth);
    map.y = Misc.modulo(map.y, LNZ.map_chunkWidth);
    int x = map.x - map.y + LNZ.map_chunkWidth - 1;
    int y = map.x + map.y;
    x += LNZ.map_chunkWidth * (x_chunk - y_chunk);
    y += LNZ.map_chunkWidth * (x_chunk + y_chunk);
    return new IntegerCoordinate(x, y);
  }

  Coordinate gridImageToMapCoordinate(Coordinate image) {
    int x_chunk = Math.floorDiv((int)Math.floor(image.x), 2 * LNZ.map_chunkWidth);
    int y_chunk = Math.floorDiv((int)Math.floor(image.y), 2 * LNZ.map_chunkWidth);
    image.x = Misc.modulo(image.x, 2 * LNZ.map_chunkWidth);
    image.y = Misc.modulo(image.y, 2 * LNZ.map_chunkWidth);
    double x = 0.5 * (image.y + image.x - LNZ.map_chunkWidth);
    double y = 0.5 * (image.y - image.x + LNZ.map_chunkWidth);
    x += LNZ.map_chunkWidth * x_chunk;
    y += LNZ.map_chunkWidth * y_chunk;
    return new Coordinate(x, y);
  }


  @Override
  void addPlayer(Hero player) {
    super.addPlayer(player);
    this.refreshChunks();
    this.setViewLocation(player.coordinate.copy());
  }

  void actuallyAddFeature(int code, Feature f) {
    IntegerCoordinate coordinate = this.coordinateOf(f.coordinate);
    Chunk chunk = this.chunk_reference.get(coordinate);
    if (chunk == null) {
      p.global.log("WARNING: Can't find chunk with coordinates " + coordinate.x +
        ", " + coordinate.y + " to add feature to.");
      return;
    }
    chunk.features.put(code, f);
  }

  void featureAddedMapSquareNotFound(IntegerCoordinate coordinate, Feature f) {
    p.global.log("WARNING: featureAddedMapSquareNotFound");
    // feature hanging over edge of unloaded chunk when added
    if (f == null || f.remove) {
      return;
    }
   // if (!this.hanging_features.containsKey(coordinate)) {
      //this.hanging_features.put(coordinate, new ConcurrentHashMap<Integer, Feature>());
    //}
    //this.hanging_features.get(coordinate).putIfAbsent(f.map_key, f);
  }

  void featureRemovedMapSquareNotFound(IntegerCoordinate coordinate, Feature f) {
    // feature hanging over edge of unloaded chunk when removed
    if (f == null || f.remove) {
      return;
    }
    /*if (!this.hanging_features.containsKey(coordinate)) {
      return;
    }
    this.hanging_features.get(coordinate).remove(f.map_key);
    if (this.hanging_features.get(coordinate).isEmpty()) {
      this.hanging_features.remove(coordinate);
    }*/
  }

  /*synchronized void checkHangingFeatures() {
    Iterator hanging_features_iterator = this.hanging_features.entrySet().iterator();
    while(hanging_features_iterator.hasNext()) {
      Map.Entry<IntegerCoordinate, ConcurrentHashMap<Integer, Feature>> entry =
        (Map.Entry<IntegerCoordinate, ConcurrentHashMap<Integer, Feature>>)hanging_features_iterator.next();
      GameMapSquare square = this.mapSquare(entry.getKey().x, entry.getKey().y);
      if (square == null) {
        continue;
      }
      Iterator coordinate_iterator = entry.getValue().entrySet().iterator();
      while(coordinate_iterator.hasNext()) {
        Map.Entry<Integer, Feature> feature_entry = (Map.Entry<Integer, Feature>)coordinate_iterator.next();
        if (feature_entry.getValue() == null || feature_entry.getValue().remove) {
          coordinate_iterator.remove();
          continue;
        }
        if (this.resolveHangingFeature(feature_entry.getValue(), entry.getKey())) {
          coordinate_iterator.remove();
        }
      }
      if (entry.getValue().isEmpty()) {
        hanging_features_iterator.remove();
      }
    }
  }*/

  /*synchronized void checkHangingImages() {
    Iterator<HangingImage> hanging_image_iterator = this.hanging_images.iterator();
    while(hanging_image_iterator.hasNext()) {
      HangingImage hanging_image = hanging_image_iterator.next();
      if (hanging_image.resolve()) {
        hanging_image_iterator.remove();
      }
    }
  }*/

  boolean featureHanging(Feature f) {
    if (f == null || f.remove) {
      return false;
    }
    IntegerCoordinate x_edge = this.coordinateOf(
      LNZApplet.round(f.coordinate.x + f.sizeX), LNZApplet.round(f.coordinate.y));
    IntegerCoordinate y_edge = this.coordinateOf(
      LNZApplet.round(f.coordinate.x), LNZApplet.round(f.coordinate.y + f.sizeY));
    IntegerCoordinate xy_edge = this.coordinateOf(
      LNZApplet.round(f.coordinate.x + f.sizeX), LNZApplet.round(f.coordinate.y + f.sizeY));
    Chunk x_chunk = this.chunk_reference.get(x_edge);
    Chunk y_chunk = this.chunk_reference.get(y_edge);
    Chunk xy_chunk = this.chunk_reference.get(xy_edge);
    return (x_chunk == null || y_chunk == null || xy_chunk == null);
  }

  boolean resolveHangingFeature(Feature f, IntegerCoordinate coordinate) {
    if (f == null || f.remove) {
      return true;
    }
    GameMapSquare square = this.mapSquare(coordinate.x, coordinate.y);
    Chunk chunk = this.chunk_reference.get(this.coordinateOf(coordinate.x, coordinate.y));
    if (square == null || chunk == null || chunk.terrain_dimg == null) { // still hanging
      return false;
    }
    square.addedFeature(f, coordinate.x, coordinate.y);
    int x_chunk = Math.floorMod(coordinate.x, LNZ.map_chunkWidth);
    int y_chunk = Math.floorMod(coordinate.y, LNZ.map_chunkWidth);
    DImg dimg = new DImg(p, f.getImage());
    dimg.setGrid(f.sizeX, f.sizeY);
    PImage image_piece = dimg.getImageGridPiece(
      coordinate.x - LNZApplet.round(f.coordinate.x),
      coordinate.y - LNZApplet.round(f.coordinate.y));
    // TODO: Need to update coordinates for isometric (hanging feature)
    chunk.terrain_dimg.addImageGrid(image_piece, x_chunk, y_chunk);
    return true;
  }

  Feature getFeature(int code) {
    for (Chunk chunk : this.chunk_reference.values()) {
      if (chunk.features.containsKey(code)) {
        return chunk.features.get(code);
      }
    }
    return null;
  }

  Collection<Feature> features() { // remove this, make logic more specific
    ArrayList<Feature> feature_list = new ArrayList<Feature>();
    for (Chunk chunk : this.chunk_reference.values()) {
      feature_list.addAll(chunk.features.values());
    }
    return feature_list;
  }

  void updateFeatures(int time_elapsed) {
    for (Chunk chunk : this.chunk_reference.values()) {
      Iterator<Map.Entry<Integer, Feature>> feature_iterator = chunk.features.entrySet().iterator();
      while(feature_iterator.hasNext()) {
        Map.Entry<Integer, Feature> entry = feature_iterator.next();
        updateFeature(entry.getValue(), feature_iterator, time_elapsed);
      }
    }
  }

  void updateFeaturesCheckRemovalOnly() {
    for (Chunk chunk : this.chunk_reference.values()) {
      Iterator<Map.Entry<Integer, Feature>> feature_iterator = chunk.features.entrySet().iterator();
      while(feature_iterator.hasNext()) {
        Map.Entry<Integer, Feature> entry = feature_iterator.next();
        if (entry.getValue().remove) {
          this.removeFeature(entry.getKey());
          feature_iterator.remove();
        }
      }
    }
  }


  @Override
  void updateView(int time_elapsed) {
    super.updateView(time_elapsed);
    this.timer_refresh_chunks -= time_elapsed;
    if (this.timer_refresh_chunks < 0) {
      this.refreshChunks();
    }
  }

  void trySpawnUnits(Level level, int time_elapsed) {
    for (Chunk chunk : this.chunk_reference.values()) {
      chunk.timer_spawn_units -= time_elapsed;
      if (chunk.timer_spawn_units < 0) {
        chunk.timer_spawn_units = LNZ.map_timerChunkSpawnUnits;
        chunk.trySpawnUnits(level);
      }
    }
  }


  @Override
  void displayNerdStats() {
    p.fill(255);
    p.textSize(14);
    p.textAlign(PConstants.LEFT, PConstants.TOP);
    double y_stats = this.yi + 31;
    double line_height = p.textAscent() + p.textDescent() + 2;
    p.text("Map Location: " + this.code.displayName(), this.xi + 1, y_stats);
    y_stats += line_height;
    p.text("FPS: " + (int)p.global.lastFPS + " (" + (int)this.lastFPS + ")", this.xi + 1, y_stats);
    y_stats += line_height;
    try {
      p.text("Area Location: " + this.area_location.displayName(), this.xi + 1, y_stats);
      y_stats += line_height;
      p.text("Biome: " + this.chunk_reference.get(this.current_chunk).biome.displayName(), this.xi + 1, y_stats);
      y_stats += line_height;
    } catch(NullPointerException e) {}
    this.displayAbstractNerdStats(y_stats, line_height);
  }


  void saveTerrain(PrintWriter file) {
    file.println("area_location: " + this.area_location.fileName());
    file.println("mapEdgeXi: " + this.mapEdgeXi);
    file.println("mapEdgeYi: " + this.mapEdgeYi);
    file.println("mapEdgeXf: " + this.mapEdgeXf);
    file.println("mapEdgeYf: " + this.mapEdgeYf);
    file.println("seed: " + this.seed);
    file.println("nextFeatureKey: " + this.next_feature_key);
    file.println("default_spawn_chunk: " + this.default_spawn_chunk.x + ", " + this.default_spawn_chunk.y);
    Iterator<Map.Entry<IntegerCoordinate, Chunk>> it = this.chunk_reference.entrySet().iterator();
    while(it.hasNext()) {
      Map.Entry<IntegerCoordinate, Chunk> entry = it.next();
      entry.getValue().save();
    }
  }
  @Override
  String fileType() {
    return "area";
  }

  void addImplementationSpecificData(String datakey, String data) {
    switch(datakey) {
      case "area_location":
        this.area_location = AreaLocation.areaLocation(data);
        break;
      case "mapEdgeXi":
        this.mapEdgeXi = Misc.toInt(data);
        break;
      case "mapEdgeYi":
        this.mapEdgeYi = Misc.toInt(data);
        break;
      case "mapEdgeXf":
        this.mapEdgeXf = Misc.toInt(data);
        break;
      case "mapEdgeYf":
        this.mapEdgeYf = Misc.toInt(data);
        break;
      case "default_spawn_chunk":
        String[] data_split = PApplet.split(data, ',');
        if (data_split.length < 2) {
          p.global.errorMessage("ERROR: default_spawn_chunk data corrupted.");
          break;
        }
        this.default_spawn_chunk = new IntegerCoordinate(
          Misc.toInt(PApplet.trim(data_split[0])), Misc.toInt(PApplet.trim(data_split[1])));
        break;
      case "seed":
        this.seed = Misc.toInt(data);
        p.noiseSeed(this.seed);
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not recognized for GameMap object.");
        break;
    }
  }
}