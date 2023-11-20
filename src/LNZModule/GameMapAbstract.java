package LNZModule;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.io.PrintWriter;
import processing.core.*;
import DImg.DImg;
import Element.TextBox;
import Form.*;
import LNZApplet.LNZApplet;
import Misc.Misc;

abstract class AbstractGameMap {
  class HeaderMessage {
    private LNZ p;

    private String message;
    private int text_align;
    private double text_size;
    private boolean fading = true;
    private boolean showing = true;
    private int fade_time = LNZ.map_headerMessageFadeTime;
    private int show_time = LNZ.map_headerMessageShowTime;
    private int color_background = DImg.ccolor(110, 90, 70, 150);
    private int color_text = DImg.ccolor(255);
    private boolean clickable = true;
    private boolean centered = false;

    private double xi = 0;
    private double yi = 0;
    private double xf = 0;
    private double yf = 0;
    private double centerX = 0;
    private int index = 0;

    private int alpha = 255;
    private boolean hovered = false;
    private boolean remove = false;

    HeaderMessage(LNZ sketch, String message) {
      this(sketch, message, PConstants.CENTER, LNZ.map_defaultHeaderMessageTextSize);
    }
    HeaderMessage(LNZ sketch, String message, int text_align) {
      this(sketch, message, text_align, LNZ.map_defaultHeaderMessageTextSize);
    }
    HeaderMessage(LNZ sketch, String message, int text_align, double text_size) {
      this.p = sketch;
      this.message = message;
      this.text_align = text_align;
      this.text_size = text_size;
      this.evaluateSize();
    }

    void setTextSize(double text_size) {
      this.text_size = text_size;
      this.evaluateSize();
    }

    void evaluateSize() {
      p.textSize(this.text_size);
      double size_width = p.textWidth(this.message) + 4;
      double size_height = p.textAscent() + p.textDescent() + 2;
      switch(this.text_align) {
        case PConstants.LEFT:
          this.xi = AbstractGameMap.this.xi + 5;
          this.yi = AbstractGameMap.this.yi + LNZ.map_borderSize + 1;
          this.xf = this.xi + size_width;
          this.yf = this.yi + size_height;
          break;
        case PConstants.RIGHT:
          this.xi = AbstractGameMap.this.xf - 5 - size_width;
          this.yi = AbstractGameMap.this.yi + LNZ.map_borderSize + 1;
          this.xf = AbstractGameMap.this.xf - 5;
          this.yf = this.yi + size_height;
          break;
        case PConstants.CENTER:
        default:
          this.xi = 0.5 * (p.width - size_width);
          this.yi = AbstractGameMap.this.yi + LNZ.map_borderSize + 1;
          this.xf = 0.5 * (p.width + size_width);
          this.yf = this.yi + size_height;
          break;
      }
      this.centerX = this.xi + 0.5 * (this.xf - this.xi);
    }

    void placeCenter() {
      this.placeCenter(34);
    }
    void placeCenter(double text_size) {
      this.centered = true;
      this.text_size = text_size;
      p.textSize(this.text_size);
      double size_width = p.textWidth(this.message) + 4;
      double size_height = p.textAscent() + p.textDescent() + 2;
      this.xi = 0.5 * (p.width - size_width);
      this.yi = 0.5 * (p.height - size_height);
      this.xf = 0.5 * (p.width + size_width);
      this.yf = 0.5 * (p.height + size_height);
      this.centerX = this.xi + 0.5 * (this.xf - this.xi);
    }

    void updateView(int timeElapsed, int index) {
      if (this.remove) {
        return;
      }
      this.index = index;
      if (this.fading) {
        this.fade_time -= timeElapsed;
        if (this.fade_time <= 0) {
          if (this.showing) {
            this.fading = false;
          }
          else {
            this.remove = true;
          }
        }
        if (this.showing) {
          this.alpha = (int)Math.round(255 * (LNZ.map_headerMessageFadeTime -
            this.fade_time) / LNZ.map_headerMessageFadeTime);
        }
        else {
          this.alpha = (int)Math.round(255 * this.fade_time / LNZ.map_headerMessageFadeTime);
        }
      }
      else {
        this.alpha = 255;
        this.show_time -= timeElapsed;
        if (this.show_time <= 0) {
          this.fading = true;
          this.fade_time = LNZ.map_headerMessageFadeTime;
          this.showing = false;
        }
      }
    }

    void drawMessage() {
      if (this.remove) {
        return;
      }
      double translate_amount = this.index * (this.yf - this.yi + 4);
      p.translate(0, translate_amount);
      p.rectMode(PConstants.CORNERS);
      p.fill(this.color_background, alpha);
      p.rect(this.xi, this.yi, this.xf, this.yf);
      p.textAlign(PConstants.CENTER, PConstants.BOTTOM);
      p.textSize(this.text_size);
      p.fill(this.color_text, alpha);
      p.text(this.message, this.centerX, this.yf - 2);
      p.translate(0, -translate_amount);
    }

    void mouseMove(double mX, double mY) {
      if (mX > this.xi && mY > this.yi && mX < this.xf && mY < this.yf) {
        this.hovered = true;
      }
      else {
        this.hovered = false;
      }
    }

    void mousePress() {
      if (this.hovered && this.clickable) {
        this.fading = false;
        this.showing = true;
        this.show_time = LNZ.map_headerMessageShowTime;
      }
    }
  }


  // This thread prepares the map (and fog) displays to be drawn on screen
  abstract class AbstractTerrainDimgThread extends Thread {

    class UpdateFogDisplayThread extends Thread {
      private boolean kill_thread = false;
      UpdateFogDisplayThread() {
        super("UpdateFogDisplayThread");
        this.setDaemon(true);
      }
      @Override
      public String toString() {
        return "!!";
      }
      @Override
      public void run() {
        while(!this.kill_thread) {
          try {
            update_fog_display_start.await();
          } catch(InterruptedException e) {}
          if (this.kill_thread) {
            break;
          }
          AbstractTerrainDimgThread.this.fog_display =
            AbstractTerrainDimgThread.this.updateFogDisplay();
          update_fog_display_start = new CountDownLatch(1);
          update_fog_display_finished.countDown();
        }
      }
    }

    class CopyTerrainDisplayThread extends Thread {
      private PImage terrain_display_copy;
      private boolean kill_thread = false;
      CopyTerrainDisplayThread() {
        super("CopyTerrainDisplayThread");
        this.setDaemon(true);
      }
      @Override
      public String toString() {
        return "!!";
      }
      @Override
      public void run() {
        while(!this.kill_thread) {
          PImage terrain_display = AbstractTerrainDimgThread.this.terrain_display;
          if (terrain_display == null) {
            return; // new thread is made for each new terrain_display
          }
          this.terrain_display_copy = DImg.copy(p, terrain_display);
          terrain_display_copy_taken = new CountDownLatch(1);
          terrain_display_copy_ready.countDown();
          if (this.kill_thread) {
            break;
          }
          try {
            terrain_display_copy_taken.await();
          } catch(InterruptedException e) {}
        }
      }
    }

    void beginTerrainDisplayCopyThread() {
      if (this.copy_terrain_display_thread != null) {
        this.copy_terrain_display_thread.kill_thread = true;
        this.copy_terrain_display_thread.interrupt();
      }
      this.copy_terrain_display_thread = new CopyTerrainDisplayThread();
      this.terrain_display_copy_ready = new CountDownLatch(1);
      this.terrain_display_copy_taken = new CountDownLatch(1);
      this.copy_terrain_display_thread.start();
    }
    
    PImage terrainDisplayCopy() {
      try {
        this.terrain_display_copy_ready.await();
      } catch(InterruptedException e) {}
      PImage terrain_display = this.copy_terrain_display_thread.terrain_display_copy;
      this.terrain_display_copy_ready = new CountDownLatch(1);
      this.terrain_display_copy_taken.countDown();
      return terrain_display;
    }

    private boolean kill_thread = false;

    protected boolean update_terrain_display = false;

    protected Coordinate image_grid_start = new Coordinate(0, 0);
    protected Coordinate image_grid_end = new Coordinate(0, 0);
    protected double terrain_resolution = 0;
    protected double xi_map = 0;
    protected double yi_map = 0;
    protected double xf_map = 0;
    protected double yf_map = 0;
    protected double zoom = 0;
    protected Coordinate view = new Coordinate(0, 0);

    protected double last_FPS = 0;
    protected int last_FPS_time = 0;
    protected int last_frame_time = 0;
    protected int timer_FPS = 0;
    protected int frame_counter = 0;

    protected int frame_time = 0;
    protected int update_terrain_time = 0;
    protected int updated_terrain_count = 0;
    protected int create_new_map_time = 0;
    protected int draw_squares_time = 0;
    protected int draw_square_time = 0;
    protected int draw_features_time = 0;
    protected int draw_units_time = 0;
    protected int draw_items_time = 0;
    protected int draw_fog_time = 0;
    protected int assignment_time = 0;

    protected PImage terrain_display;
    protected CopyTerrainDisplayThread copy_terrain_display_thread;
    private CountDownLatch terrain_display_copy_ready;
    private CountDownLatch terrain_display_copy_taken;

    protected PImage fog_display;
    protected UpdateFogDisplayThread update_fog_display_thread;
    private CountDownLatch update_fog_display_start;
    private CountDownLatch update_fog_display_finished;

    AbstractTerrainDimgThread() {
      super("TerrainDimgThread");
      this.setDaemon(true);
      this.update_fog_display_start = new CountDownLatch(1);
      this.update_fog_display_thread = new UpdateFogDisplayThread();
    }

    void killThread() {
      this.kill_thread = true;
      if (this.copy_terrain_display_thread != null) {
        this.copy_terrain_display_thread.kill_thread = true;
        this.copy_terrain_display_thread.interrupt();
      }
      if (this.update_fog_display_thread != null) {
        this.update_fog_display_thread.kill_thread = true;
        this.update_fog_display_thread.interrupt();
      }
      this.interrupt();
    }

    @Override
    public String toString() {
      return "!!";
    }

    @Override
    public void run() {
      this.update_fog_display_thread.start();
      while(!this.kill_thread) {
        int start_time = AbstractGameMap.this.last_update_time;
        int timer_start_time = p.millis();
        boolean draw_fog = AbstractGameMap.this.draw_fog;
        if (draw_fog) {
          this.update_fog_display_finished = new CountDownLatch(1);
          this.update_fog_display_start.countDown();
        }
        if (this.timer_FPS > 6 * LNZ.frameUpdateTime) {
          this.last_FPS_time = AbstractGameMap.this.last_update_time;
          this.last_FPS = (1000.0 * this.frame_counter) / (this.timer_FPS + 1);
          if (LNZ.DEV) {
            PApplet.println(
              "\nframe logic: " + this.frame_time / this.frame_counter,
              "\nupdate terrain_display: " + this.update_terrain_time / this.frame_counter,
              "\nupdate terrain_display count: " + this.updated_terrain_count + " / " + this.frame_counter,
              "\ncopy terrain display: " + this.create_new_map_time / this.frame_counter,
              "\ndraw squares: " + this.draw_squares_time / this.frame_counter,
              "(squares: " + this.draw_square_time / this.frame_counter + ",",
              "features: " + this.draw_features_time / this.frame_counter + ",",
              "units: " + this.draw_units_time / this.frame_counter + ",",
              "items: " + this.draw_items_time / this.frame_counter + ")",
              "\nfog display: " + this.draw_fog_time / this.frame_counter,
              "\nassignment logic: " + this.assignment_time / this.frame_counter);
          }
          AbstractGameMap.this.lastFPS = this.last_FPS;
          this.timer_FPS = 0;
          this.frame_counter = 0;

          this.frame_time = 0;
          this.update_terrain_time = 0;
          this.updated_terrain_count = 0;
          this.create_new_map_time = 0;
          this.draw_squares_time = 0;
          this.draw_square_time = 0;
          this.draw_features_time = 0;
          this.draw_units_time = 0;
          this.draw_items_time = 0;
          this.draw_fog_time = 0;
          this.assignment_time = 0;
        }
        this.frame_time += p.millis() - timer_start_time;
        timer_start_time = p.millis();
        boolean terrain_display_updated = false;
        if (this.update_terrain_display) {
          this.update_terrain_display = false;
          terrain_display_updated = true;
          this.image_grid_start = AbstractGameMap.this.image_grid_start.copy();
          this.image_grid_end = AbstractGameMap.this.image_grid_end.copy();
          this.terrain_resolution = AbstractGameMap.this.terrain_resolution;
          this.xi_map = AbstractGameMap.this.xi_map;
          this.yi_map = AbstractGameMap.this.yi_map;
          this.xf_map = AbstractGameMap.this.xf_map;
          this.yf_map = AbstractGameMap.this.yf_map;
          this.zoom = AbstractGameMap.this.zoom;
          this.view = AbstractGameMap.this.view.copy();

          this.terrain_display = this.updateTerrainDisplay();
          this.beginTerrainDisplayCopyThread();
          this.updated_terrain_count++;
        }

        if (this.terrain_display == null) {
          this.update_terrain_display = true;
          continue; // happens on first frame or when moving across many chunks
        }
        this.update_terrain_time += p.millis() - timer_start_time;
        timer_start_time = p.millis();

        DImg new_map_display = new DImg(p, this.terrainDisplayCopy());
        this.create_new_map_time += p.millis() - timer_start_time;
        timer_start_time = p.millis();
        this.drawSquares(new_map_display);
        if (AbstractGameMap.this.grayscale_image) {
          new_map_display.img.filter(PConstants.GRAY);
        }
        this.draw_squares_time += p.millis() - timer_start_time;
        timer_start_time = p.millis();

        if (draw_fog) {
          try {
            this.update_fog_display_finished.await();
          } catch(InterruptedException e) {}
          AbstractGameMap.this.fog_display = this.fog_display;
        }
        this.draw_fog_time += p.millis() - timer_start_time;
        timer_start_time = p.millis();

        AbstractGameMap.this.map_display = new_map_display;

        if (terrain_display_updated) {
          AbstractGameMap.this.image_grid_start_old = this.image_grid_start.copy();
          AbstractGameMap.this.image_grid_end_old = this.image_grid_end.copy();
          AbstractGameMap.this.xi_map_old = this.xi_map;
          AbstractGameMap.this.yi_map_old = this.yi_map;
          AbstractGameMap.this.xf_map_old = this.xf_map;
          AbstractGameMap.this.yf_map_old = this.yf_map;
          AbstractGameMap.this.zoom_old = this.zoom;
          AbstractGameMap.this.view_old = this.view;
        }
        this.assignment_time += p.millis() - timer_start_time;
        timer_start_time = p.millis();
        this.timer_FPS += AbstractGameMap.this.last_update_time - start_time;
        this.frame_counter++;
      }
    }

    abstract PImage updateTerrainDisplay();
    abstract PImage updateFogDisplay();

    private Coordinate player_coordinate;
    private IntegerCoordinate player_grid;
    private double player_height;
    private Unit display_player = null;
    private Coordinate player_image_coordinate = null;

    private class DrawFeatureData {
      Feature feature;
      FeatureDrawGridPiece piece;
      int index;
      DrawFeatureData(Feature feature, FeatureDrawGridPiece piece, int index) {
        this.feature = feature;
        this.piece = piece;
        this.index = index;
      }
    }
    
    private Map<IntegerCoordinate, List<DrawFeatureData>> extra_features_to_draw =
      new HashMap<IntegerCoordinate, List<DrawFeatureData>>();
    private Map<IntegerCoordinate, List<Unit>> extra_units_to_draw =
      new HashMap<IntegerCoordinate, List<Unit>>();
    private Map<IntegerCoordinate, List<Item>> extra_items_to_draw =
      new HashMap<IntegerCoordinate, List<Item>>();

    void drawSquares(DImg new_map_display) {
      // TODO: Switch to PGraphic for whole fucking thing => draw negative overflow squares first then terrain dimg
      this.player_coordinate = AbstractGameMap.this.units.containsKey(0) ?
        AbstractGameMap.this.units.get(0).coordinate : AbstractGameMap.this.mc;
      this.player_grid = new IntegerCoordinate(
        AbstractGameMap.this.units.containsKey(0) ?
        AbstractGameMap.this.units.get(0).coordinate : AbstractGameMap.this.mc);
      this.player_height = AbstractGameMap.this.units.containsKey(0) ?
        AbstractGameMap.this.units.get(0).curr_height : 0;
      // iterate tiles diagonally to match what is shown on screen
      for (int k = 0; k <= AbstractGameMap.this.currHeight() + AbstractGameMap.this.currWidth() - 2; k++) {
        for (int j = 0; j <= k && j < AbstractGameMap.this.currHeight(); j++) {
      //for (int i = AbstractGameMap.this.currMapXI(); i < AbstractGameMap.this.currMapXF(); i++) {
        //for (int j = AbstractGameMap.this.currMapYI(); j < AbstractGameMap.this.currMapYF(); j++) {
          int y = j + AbstractGameMap.this.currMapYI();
          int x = k + AbstractGameMap.this.currMapXI() - j;
          GameMapSquare square = AbstractGameMap.this.mapSquare(x, y);
          if (square == null) {
            continue;
          }
          this.drawSquare(new_map_display, square, new IntegerCoordinate(x, y));
        }
      }
    }

    void drawSquare(DImg new_map_display, GameMapSquare square, IntegerCoordinate coordinate) {
      if (!square.explored && AbstractGameMap.this.draw_fog) {
        square.in_view = false;
        return;
      }
      int timer_start_time = p.millis();
      if (square.terrain_id == 191) { // lava
        Coordinate image_coordinate = AbstractGameMap.this.mapToDisplayImageCoordinate(
          new Coordinate(coordinate), this.xi_map, this.yi_map, this.zoom, this.view);
            new_map_display.addImage(square.terrainImage(false),
          LNZApplet.round(image_coordinate.x), LNZApplet.round(image_coordinate.y),
          LNZApplet.round(2 * this.zoom),
          LNZApplet.round(this.zoom));
      }
      // draw overflow
      int overflow = square.terrainImageHeightOverflow();
      Coordinate image_coordinate = AbstractGameMap.this.mapToScreenImageCoordinate(
        new Coordinate(coordinate), this.zoom, this.view);
        image_coordinate.y -= overflow * 0.25 * this.zoom;
      Coordinate imageFarCoordinate = image_coordinate.copy();
      imageFarCoordinate.x += 2 * this.zoom;
      imageFarCoordinate.y += (1 + 0.25 * overflow) * this.zoom;
      if (!AbstractGameMap.this.screenCoordinateInView(image_coordinate) &&
        !AbstractGameMap.this.screenCoordinateInView(imageFarCoordinate)) {
        square.in_view = false;
        return;
      }
      square.in_view = true;
      if (square.imageOverflows()) {
        boolean blocks_player_view = square.blocksPlayerView(coordinate,
          player_grid, player_coordinate, player_height);
        imageFarCoordinate.y -= 0.25 * this.zoom * square.base_elevation;
        int scaled_width = LNZApplet.round(2 * this.zoom);
        PImage slab_image = square.slabImage(blocks_player_view, scaled_width);
        if (slab_image != null) {
          Coordinate slab_coordinate = AbstractGameMap.this.mapToDisplayImageCoordinate(
            new Coordinate(coordinate), this.xi_map, this.yi_map, this.zoom, this.view);
          slab_coordinate.y -= square.base_elevation * 0.25 * this.zoom;
          new_map_display.addScaledImage(slab_image,
            LNZApplet.round(slab_coordinate.x),
            LNZApplet.round(slab_coordinate.y));
        }
        image_coordinate = AbstractGameMap.this.screenImageToDisplayImageCoordinate(
          image_coordinate, this.xi_map, this.yi_map);
        PImage terrain_image = square.terrainImageScaled(blocks_player_view, scaled_width);
        new_map_display.addScaledImage(terrain_image,
          LNZApplet.round(image_coordinate.x),
          LNZApplet.round(image_coordinate.y));
      }
      this.draw_square_time += p.millis() - timer_start_time;
      timer_start_time = p.millis();
      // assign overflow feature pieces to their draw squares
      IntegerCoordinate draw_square = null;
      for (Feature f : square.features) { // first ones should be the ones on bottom (generally?)
        if (f == null || f.remove || !f.displaysImage() ||
          (f.terrainImageHeightOverflow() + f.curr_height) == 0) {
          continue;
        }
        // TODO: Real solution is cut every picture (at runtime) into 1x1 pieces
        // Current solution doesn't work when a 2x1 or 1x2 are stacked
        if (coordinate.equals(f.gridLocation())) {
          List<FeatureDrawGridPiece> pieces = Feature.drawGridLocationsOverride(
            f.ID,
            AbstractGameMap.this.feature_draw_grid_locations_map,
            new IntegerCoordinate(f.sizeX, f.sizeY));
          for (int i = 0; i < pieces.size(); i++) {
            boolean place_first = false;
            if (f.sizeX == 1 && f.sizeY == 1 && square.features.size() == 1) {
              place_first = true;
            }
            FeatureDrawGridPiece piece = pieces.get(i);
            int index = pieces.size() == 1 ? -1 : i;
            IntegerCoordinate draw_coordinate = f.gridLocation().addR(piece.draw_location);
            if (draw_square == null || draw_coordinate.diagonallyAfter(draw_square)) {
              draw_square = draw_coordinate.copy();
            }
            if (this.extra_features_to_draw.containsKey(draw_square)) {
              List<DrawFeatureData> list = this.extra_features_to_draw.get(draw_square);
              if (place_first) {
                list.add(0, new DrawFeatureData(f, piece, index));
              }
              else {
                list.add(new DrawFeatureData(f, piece, index));
              }
            }
            else {
              ArrayList<DrawFeatureData> feature_list = new ArrayList<DrawFeatureData>();
              feature_list.add(new DrawFeatureData(f, piece, index));
              this.extra_features_to_draw.put(draw_square, feature_list);
            }
          }
        }
      }
      // draw extra features
      List<DrawFeatureData> extra_features = this.extra_features_to_draw.get(coordinate);
      if (extra_features != null) {
        for (DrawFeatureData draw_feature_data : extra_features) {
          this.drawFeature(new_map_display, draw_feature_data);
        }
        extra_features.clear();
      }
      this.draw_features_time += p.millis() - timer_start_time;
      timer_start_time = p.millis();
      // extra units
      List<Unit> extra_units = this.extra_units_to_draw.get(coordinate);
      if (extra_units != null) {
        for (Unit u : extra_units) {
          this.drawUnit(new_map_display, u);
        }
        extra_units.clear();
      }
      this.draw_units_time += p.millis() - timer_start_time;
      timer_start_time = p.millis();
      // extra items
      List<Item> extra_items = this.extra_items_to_draw.get(coordinate);
      if (extra_items != null) {
        for (Item i : extra_items) {
          this.drawItem(new_map_display, i);
        }
        extra_items.clear();
      }
      this.draw_items_time += p.millis() - timer_start_time;
      timer_start_time = p.millis();
      // only draw non-feature objects if square visible
      if (AbstractGameMap.this.draw_fog && !square.visible) {
        return;
      }
      // determine alternate drawing coordinate
      IntegerCoordinate alternate_drawing_coordinate = null;
      GameMapSquare alternate_square1 = AbstractGameMap.this.mapSquare(
        coordinate.x + 1, coordinate.y + 1);
      GameMapSquare alternate_square2 = AbstractGameMap.this.mapSquare(
        coordinate.x, coordinate.y + 1);
      GameMapSquare alternate_square3 = AbstractGameMap.this.mapSquare(
        coordinate.x + 1, coordinate.y);
      if (alternate_square1 != null && alternate_square1.
        terrainImageHeightOverflow() <= overflow &&
        alternate_square2 != null && alternate_square2.
        terrainImageHeightOverflow() <= overflow &&
        alternate_square3 != null && alternate_square3.
        terrainImageHeightOverflow() <= overflow) {
        alternate_drawing_coordinate = new IntegerCoordinate(coordinate.x + 1, coordinate.y + 1);
      }
      else if (alternate_square2 != null && alternate_square2.
        terrainImageHeightOverflow() <= overflow) {
        alternate_drawing_coordinate = new IntegerCoordinate(coordinate.x, coordinate.y + 1);
      }
      else if (alternate_square3 != null && alternate_square3.
        terrainImageHeightOverflow() <= overflow) {
        alternate_drawing_coordinate = new IntegerCoordinate(coordinate.x + 1, coordinate.y);
      }
      // units
      Iterator<Map.Entry<Integer, Unit>> unit_iterator = square.units.entrySet().iterator();
      while(unit_iterator.hasNext()) {
        Map.Entry<Integer, Unit> entry = unit_iterator.next();
        Unit u = entry.getValue();
        if (u == null || u.remove) {
          unit_iterator.remove();
          continue;
        }
        if (alternate_drawing_coordinate == null) {
          this.drawUnit(new_map_display, u);
          continue;
        }
        if (this.extra_units_to_draw.containsKey(alternate_drawing_coordinate)) {
          this.extra_units_to_draw.get(alternate_drawing_coordinate).add(u);
        }
        else {
          ArrayList<Unit> unit_list = new ArrayList<Unit>();
          unit_list.add(u);
          this.extra_units_to_draw.put(alternate_drawing_coordinate, unit_list);
        }
      }
      this.draw_units_time += p.millis() - timer_start_time;
      timer_start_time = p.millis();
      // items
      Iterator <Map.Entry<Integer, Item>> item_iterator = square.items.entrySet().iterator();
      while(item_iterator.hasNext()) {
        Map.Entry<Integer, Item> entry = item_iterator.next();
        Item i = entry.getValue();
        if (i == null || i.remove) {
          item_iterator.remove();
          continue;
        }
        if (alternate_drawing_coordinate == null) {
          this.drawItem(new_map_display, i);
          continue;
        }
        if (this.extra_items_to_draw.containsKey(alternate_drawing_coordinate)) {
          this.extra_items_to_draw.get(alternate_drawing_coordinate).add(i);
        }
        else {
          ArrayList<Item> item_list = new ArrayList<Item>();
          item_list.add(i);
          this.extra_items_to_draw.put(alternate_drawing_coordinate, item_list);
        }
      }
      this.draw_items_time += p.millis() - timer_start_time;
      timer_start_time = p.millis();
      // projectiles
      // display player
      if (this.display_player != null) {
        this.displayUnit(new_map_display, player_image_coordinate, this.display_player, true);
        this.display_player = null;
      }
      this.draw_units_time += p.millis() - timer_start_time;
      timer_start_time = p.millis();
      // visual effects
      // map tint
    }

    void drawFeature(DImg new_map_display, DrawFeatureData data) {
      boolean blocks_player_view = data.feature.blocksPlayerView(player_grid, player_coordinate, player_height);
      int draw_width = data.piece.extend_x ? 2 : 1;
      int draw_height = data.piece.extend_y ? 2 : 1;
      Coordinate feature_image_coordinate = AbstractGameMap.this.mapToDisplayImageCoordinate(
        data.feature.coordinate.addR(data.piece.piece_location),
        this.xi_map, this.yi_map, this.zoom, this.view);
      feature_image_coordinate.x -= (draw_height - 1) * this.zoom;
      feature_image_coordinate.y -= 0.25 * (data.feature.terrainImageHeightOverflow() +
        data.feature.curr_height) * this.zoom;
      int scaled_width = LNZApplet.round((draw_width + draw_height) * this.zoom);
      PImage scaled_img = data.feature.getScaledImage(blocks_player_view, scaled_width, data.index);
      // TODO: Speed this up somehow (and overflow squares)
      new_map_display.addScaledImage(scaled_img,
        LNZApplet.round(feature_image_coordinate.x),
        LNZApplet.round(feature_image_coordinate.y));
    }

    void drawUnit(DImg new_map_display, Unit u) {
      Coordinate unit_image_coordinate = AbstractGameMap.this.mapToScreenCoordinate(
        u.coordinate, this.zoom, this.view);
      unit_image_coordinate.y -= 0.25 * u.curr_height * this.zoom;
      if (!AbstractGameMap.this.screenCoordinateInView(unit_image_coordinate)) {
        u.in_view = false;
        return;
      }
      u.in_view = true;
      if (u.map_key == 0) {
        this.display_player = u;
        this.player_image_coordinate = unit_image_coordinate;
        return;
      }
      this.displayUnit(new_map_display, unit_image_coordinate, u, false);
    }

    void displayUnit(DImg new_map_display, Coordinate unit_image_coordinate, Unit u, boolean player_unit) {
      // TODO: Prepare unit PGraphics in separate thread(s) (with all peripherals)
      // Each unit should have flag to reset PGraphic (like when healthbar changes, etc..)
      if (u.invisible()) {
        return;
      }
      unit_image_coordinate = AbstractGameMap.this.screenImageToDisplayImageCoordinate(
        unit_image_coordinate, this.xi_map, this.yi_map);
      double pg_width = (1 + 2 * u.size) * this.zoom + 12;
      double pg_height = (0.9 + 2.2 * u.size) * this.zoom + 12;
      PGraphics pg = p.createGraphics((int)pg_width, (int)pg_height);
      pg.beginDraw();
      double net_rotation = 0;
      boolean flip = false;
      double effective_facing_a = u.facingA + 0.5 * PConstants.HALF_PI; // to account for isometric
      if (Math.abs(effective_facing_a) > PConstants.HALF_PI) {
        flip = true;
        net_rotation = (PConstants.PI - Math.abs(effective_facing_a)) *
          effective_facing_a / Math.abs(effective_facing_a) + u.facingAngleModifier();
      }
      else {
        net_rotation = effective_facing_a + u.facingAngleModifier();
      }
      PImage unit_image = u.getImage();
      boolean copied = false;
      float extra_translate_x = 0;
      float extra_translate_y = 0;
      if (u.diseased()) {
        if (!copied) {
          copied = true;
          unit_image = DImg.copy(p, unit_image);
        }
        DImg.tint(unit_image, DImg.ccolor(90, 250, 90));
      }
      else if (u.sick()) {
        if (!copied) {
          copied = true;
          unit_image = DImg.copy(p, unit_image);
        }
        DImg.tint(unit_image, DImg.ccolor(150, 255, 150));
      }
      if (u.frozen()) {
        if (!copied) {
          copied = true;
          unit_image = DImg.copy(p, unit_image);
        }
        DImg.tint(unit_image, DImg.ccolor(50, 180, 250));
      }
      else if (u.chilled()) {
        if (!copied) {
          copied = true;
          unit_image = DImg.copy(p, unit_image);
        }
        DImg.tint(unit_image, DImg.ccolor(120, 220, 255));
      }
      if (u.rageOfTheBenII()) {
        if (!copied) {
          copied = true;
          unit_image = DImg.copy(p, unit_image);
        }
        DImg.tint(unit_image, DImg.ccolor(255, 70, 70));
        extra_translate_x = (float)(LNZ.ability_110_shakeConstant -
          Misc.randomDouble(2 * LNZ.ability_110_shakeConstant));
        extra_translate_y = (float)(LNZ.ability_110_shakeConstant -
          Misc.randomDouble(2 * LNZ.ability_110_shakeConstant));
      }
      else if (u.rageOfTheBen()) {
        if (!copied) {
          copied = true;
          unit_image = DImg.copy(p, unit_image);
        }
        DImg.tint(unit_image, DImg.ccolor(255, 120, 120));
        extra_translate_x = (float)(LNZ.ability_105_shakeConstant -
          Misc.randomDouble(2 * LNZ.ability_105_shakeConstant));
        extra_translate_y = (float)(LNZ.ability_105_shakeConstant -
          Misc.randomDouble(2 * LNZ.ability_105_shakeConstant));
      }
      if (u.aposematicCamouflage() || u.aposematicCamouflageII()) {
        if (!copied) {
          copied = true;
          unit_image = DImg.copy(p, unit_image);
        }
        DImg.tint(unit_image, DImg.ccolor(255, 150));
      }
      float translate_x = 0.5f * pg.width + 6;
      float translate_y = pg.height - (float)((u.size + 0.5) * this.zoom) - 6;
      float unit_image_size = (float)(2 * u.size * this.zoom);
      pg.translate(translate_x, translate_y);
      pg.translate(extra_translate_x, extra_translate_y);
      // shadow
      int shadow_width = (int)(u.width() * this.zoom);
      int shadow_height = (int)(u.height() * 0.5 * this.zoom);
      PGraphics pg_shadow = p.createGraphics(shadow_width + 4, shadow_height + 4);
      pg_shadow.beginDraw();
      pg_shadow.ellipseMode(PConstants.CORNER);
      pg_shadow.noStroke();
      pg_shadow.fill(DImg.ccolor(0, 120));
      pg_shadow.ellipse(2, 2, shadow_width, shadow_height); // shadow
      pg_shadow.endDraw();
      double shadow_translate_x = - 0.5 * pg_shadow.width;
      double shadow_translate_y = - 0.5 * pg_shadow.height +
        0.25 * (u.curr_height - u.floor_height) * this.zoom;
      new_map_display.addImage(pg_shadow,
        LNZApplet.round(unit_image_coordinate.x + shadow_translate_x),
        LNZApplet.round(unit_image_coordinate.y + shadow_translate_y));
      if (flip) {
        pg.scale(-1, 1);
      }
      pg.rotate((float)net_rotation);
      pg.imageMode(PConstants.CENTER);
      pg.image(unit_image, 0, 0, unit_image_size, unit_image_size); // image
      Item weapon = u.weapon();
      if (weapon != null) {
        float translateItemX = (float)(0.9 * (u.xRadius() + LNZ.unit_weaponDisplayScaleFactor * LNZ.item_defaultSize) * this.zoom);
        float translateItemY = (float)(0.4 * (u.yRadius() + LNZ.unit_weaponDisplayScaleFactor * LNZ.item_defaultSize) * this.zoom);
        pg.translate(translateItemX, translateItemY);
        float weapon_adjust_x = (float)(LNZ.unit_weaponDisplayScaleFactor * weapon.width() * this.zoom);
        float weapon_adjust_y = (float)(LNZ.unit_weaponDisplayScaleFactor * weapon.height() * this.zoom);
        pg.image(weapon.getImage(), 0, 0, weapon_adjust_x, weapon_adjust_y);
        if (weapon.stack > 1) {
          pg.fill(DImg.ccolor(255));
          pg.textSize(12);
          pg.textAlign(PConstants.RIGHT, PConstants.BOTTOM);
          pg.text(Integer.toString(weapon.stack), 0.5f * weapon_adjust_x - 1, 0.5f * weapon_adjust_y - 1);
        }
        pg.translate(-translateItemX, -translateItemY);
      }
      else if (player_unit) {
        float translateItemX = (float)(0.9 * (u.xRadius() + LNZ.unit_weaponDisplayScaleFactor * LNZ.item_defaultSize) * this.zoom);
        float translateItemY = (float)(0.4 * (u.yRadius() + LNZ.unit_weaponDisplayScaleFactor * LNZ.item_defaultSize) * this.zoom);
        pg.translate(translateItemX, translateItemY);
        pg.image(p.global.images.getImage("icons/hand.png"), 0, 0,
          (float)(LNZ.unit_weaponDisplayScaleFactor * 2 * LNZ.item_defaultSize * this.zoom),
          (float)(LNZ.unit_weaponDisplayScaleFactor * 2 * LNZ.item_defaultSize * this.zoom));
        pg.translate(-translateItemX, -translateItemY);
      }
      pg.rotate(-(float)net_rotation);
      if (flip) {
        pg.scale(-1, 1);
      }
      pg.translate(-extra_translate_x, -extra_translate_y);
      // gifs
      if (u.charred()) {
        int flame_frame = (int)Math.floor(LNZ.gif_fire_frames * ((u.random_number +
          AbstractGameMap.this.last_update_time) % LNZ.gif_fire_time) / LNZ.gif_fire_time);
        PImage fire_img = p.global.images.getImage("gifs/fire/" + flame_frame + ".png");
        fire_img = DImg.copy(p, fire_img);
        DImg.tint(fire_img, DImg.ccolor(255, 200));
        p.g.removeCache(fire_img);
        pg.image(fire_img, 0, 0, (float)(u.width() * this.zoom), (float)(u.height() * this.zoom));
      }
      else if (u.burnt()) {
        int flame_frame = (int)Math.floor(LNZ.gif_fire_frames * ((u.random_number +
          AbstractGameMap.this.last_update_time) % LNZ.gif_fire_time) / LNZ.gif_fire_time);
        PImage fire_img = p.global.images.getImage("gifs/fire/" + flame_frame + ".png");
        fire_img = DImg.copy(p, fire_img);
        DImg.tint(fire_img, DImg.ccolor(255, 120));
        p.g.removeCache(fire_img);
        pg.image(fire_img, 0, 0, (float)(u.width() * this.zoom), (float)(u.height() * this.zoom));
      }
      if (u.drenched()) {
        int drenched_frame = (int)Math.floor(LNZ.gif_drenched_frames * ((u.random_number +
          AbstractGameMap.this.last_update_time) % LNZ.gif_drenched_time) / LNZ.gif_drenched_time);
        pg.image(p.global.images.getImage("gifs/drenched/" + drenched_frame + ".png"),
          0, 0, (float)(u.width() * this.zoom), (float)(u.height() * this.zoom));
      }
      // healthbar
      if (player_unit || p.global.profile.options.show_healthbars) {
        float healthbarWidth = (float)(LNZ.unit_healthbarWidth * this.zoom);
        float healthbarHeight = (float)(LNZ.unit_healthbarHeight * this.zoom);
        float manaBarHeight = 0;
        if (Hero.class.isInstance(u)) {
          manaBarHeight += LNZ.hero_manabarHeight * this.zoom;
        }
        float totalHeight = healthbarHeight + manaBarHeight;
        float translateHealthBarX = -(float)(0.5 * healthbarWidth + 3);
        float translateHealthBarY = -(float)(1.2 * u.size * this.zoom + totalHeight);
        pg.textSize(totalHeight - 0.5f);
        pg.translate(translateHealthBarX, translateHealthBarY);
        pg.stroke(200);
        pg.strokeWeight(0.8f);
        pg.fill(0);
        pg.rectMode(PConstants.CORNER);
        pg.rect(0, 0, healthbarWidth, totalHeight);
        pg.rect(0, 0, totalHeight, totalHeight);
        pg.fill(255);
        pg.textSize(healthbarHeight - 1);
        pg.textAlign(PConstants.CENTER, PConstants.TOP);
        pg.text(Integer.toString(u.level), 0.5f * totalHeight, 1 - pg.textDescent());
        if (player_unit) {
          pg.fill(50, 255, 50);
        }
        else if (u.alliance == Alliance.BEN) {
          pg.fill(50, 50, 255);
        }
        else {
          pg.fill(255, 50, 50);
        }
        pg.noStroke();
        float health_ratio = (float)(u.curr_health / u.health());
        if (health_ratio >= 1) {
          pg.rect(totalHeight + 0.5f, 0.5f, healthbarWidth - totalHeight, healthbarHeight);
          pg.fill(255);
          health_ratio = Math.min(1, health_ratio - 1);
          pg.rectMode(PConstants.CORNERS);
          pg.rect(healthbarWidth - health_ratio * (healthbarWidth - totalHeight), 0, healthbarWidth, healthbarHeight);
        }
        else {
          pg.rect(totalHeight + 0.5f, 0.5f, health_ratio * (healthbarWidth - totalHeight), healthbarHeight);
          if (u.timer_last_damage > 0) {
            pg.fill(255, 220, 50, (int)(255 * u.timer_last_damage / LNZ.unit_healthbarDamageAnimationTime));
            float damage_ratio = (float)(u.last_damage_amount / u.health());
            pg.rect(totalHeight + health_ratio * (healthbarWidth - totalHeight),
              0, damage_ratio * (healthbarWidth - totalHeight), healthbarHeight);
          }
        }
        if (Hero.class.isInstance(u)) {
          pg.rectMode(PConstants.CORNER);
          pg.fill(255, 255, 0);
          float mana_ratio = (float)(u.currMana() / u.mana());
          if (mana_ratio >= 1) {
            pg.rect(totalHeight, healthbarHeight, healthbarWidth - totalHeight, manaBarHeight);
            pg.fill(255);
            mana_ratio = Math.min(1, mana_ratio - 1);
            pg.rectMode(PConstants.CORNERS);
            pg.rect(healthbarWidth - mana_ratio * (healthbarWidth - totalHeight),
              healthbarHeight, healthbarWidth, totalHeight);
          }
          else {
            pg.rect(totalHeight, healthbarHeight, mana_ratio * (healthbarWidth - totalHeight), manaBarHeight);
          }
        }
        pg.textSize(healthbarHeight + 1);
        pg.fill(255);
        pg.textAlign(PConstants.CENTER, PConstants.BOTTOM);
        pg.text(u.displayName(), 0.5f * healthbarWidth, - 1 - pg.textDescent());
        pg.translate(-translateHealthBarX, -translateHealthBarY);
      }
      double image_height_translate = - u.size * this.zoom;
      unit_image_coordinate.y += image_height_translate;
      new_map_display.addImage(pg,
        LNZApplet.round(unit_image_coordinate.x - translate_x),
        LNZApplet.round(unit_image_coordinate.y - translate_y));
    }

    void displayUnit2(Unit u, boolean player_unit) {
      // TODO: Address player hitbox / blinking
      /*if (player_unit && p.global.player_blinking) {
        p.ellipseMode(PConstants.CENTER);
        p.noFill();
        p.stroke(DImg.ccolor(255));
        p.strokeWeight(0.5);
        p.rotate(-net_rotation);
        p.translate(0, -image_height_translate);
        // have option to show collision box
        p.ellipse(0, 0, u.width() * LNZ.root_two * this.zoom, u.height() * 0.5 * LNZ.root_two * this.zoom);
        p.translate(0, image_height_translate);
        p.rotate(net_rotation);
      }*/
    }
  
  

    void drawItem(DImg new_map_display, Item i) {
      Coordinate item_image_coordinate = AbstractGameMap.this.mapToScreenCoordinate(
        i.coordinate, this.zoom, this.view);
      item_image_coordinate.subtract(i.size * this.zoom); // to center image
      item_image_coordinate.y -= 0.25 * i.curr_height * this.zoom;
      if (!AbstractGameMap.this.screenCoordinateInView(item_image_coordinate)) {
        i.in_view = false;
        return;
      }
      i.in_view = true;
      item_image_coordinate.y -= (LNZ.item_bounceOffset * i.bounce.value() /
        (double)LNZ.item_bounceConstant) * this.zoom;
      item_image_coordinate = AbstractGameMap.this.screenImageToDisplayImageCoordinate(
        item_image_coordinate, this.xi_map, this.yi_map);
        new_map_display.addImage(i.getImage(),
        LNZApplet.round(item_image_coordinate.x), LNZApplet.round(item_image_coordinate.y),
        LNZApplet.round(i.width() * this.zoom), LNZApplet.round(i.height() * this.zoom));
      if (i.stack > 1) {
        PGraphics pg = p.createGraphics(22, 22);
        pg.beginDraw();
        pg.fill(255);
        pg.textSize(18);
        pg.textAlign(PConstants.CENTER, PConstants.CENTER);
        pg.text(Integer.toString(i.stack), 11, 11);
        pg.endDraw();
        new_map_display.addImage(pg,
          LNZApplet.round(item_image_coordinate.x + 0.7 * i.width() * this.zoom),
          LNZApplet.round(item_image_coordinate.y + 0.7 * i.height() * this.zoom));
      }
    }
  }


  class SelectedObjectTextbox extends TextBox {
    SelectedObjectTextbox(LNZ sketch) {
      super(sketch, LNZ.map_selectedObjectPanelGap, 0.2 * sketch.height, LNZ.
        map_selectedObjectPanelGap, 0.5 * sketch.height - 5);
      this.color_background = LNZ.color_transparent;
      this.color_header = LNZ.color_transparent;
      this.color_stroke = LNZ.color_transparent;
      this.scrollbar.setButtonColors(DImg.ccolor(170),
        DImg.adjustColorBrightness(sketch.global.color_panelBackground, 1.1),
        DImg.adjustColorBrightness(sketch.global.color_panelBackground, 1.2),
        DImg.adjustColorBrightness(sketch.global.color_panelBackground, 0.95), LNZ.color_black);
      this.scrollbar.button_upspace.setColors(LNZ.color_transparent, LNZ.color_transparent,
        LNZ.color_transparent, LNZ.color_black, LNZ.color_black);
      this.scrollbar.button_downspace.setColors(LNZ.color_transparent, LNZ.color_transparent,
        LNZ.color_transparent, LNZ.color_black, LNZ.color_black);
      this.scrollbar.button_up.raised_border = false;
      this.scrollbar.button_down.raised_border = false;
    }
  }


  abstract class ConfirmForm extends FormLNZ {
    protected boolean canceled = false;

    ConfirmForm(LNZ sketch, String title, String message) {
      super(sketch, 0.5 * (sketch.width - LNZ.mapEditor_formWidth_small),
        0.5 * (sketch.height - LNZ.mapEditor_formHeight_small),
        0.5 * (sketch.width + LNZ.mapEditor_formWidth_small),
        0.5 * (sketch.height + LNZ.mapEditor_formHeight_small));
      this.setTitleText(title);
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);

      SubmitCancelFormField submit = new SubmitCancelFormField(sketch, "  Ok  ", "Cancel");
      submit.button1.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      submit.button2.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      this.addField(new SpacerFormField(sketch, 0));
      this.addField(new TextBoxFormField(sketch, message, 120));
      this.addField(submit);
      this.min_x = AbstractGameMap.this.xi;
      this.min_y = AbstractGameMap.this.yi;
      this.max_x = AbstractGameMap.this.xf;
      this.max_y = AbstractGameMap.this.yf;
    }

    @Override
    public void cancel() {
      this.canceled = true;
    }
  }


  class MouseMoveThread extends Thread {
    private boolean kill_thread = false;
    private Coordinate mc = new Coordinate(0, 0);

    MouseMoveThread(Coordinate mc) {
      super("MouseMoveThread");
      this.setDaemon(true);
      this.mc = mc;
    }

    @Override
    public String toString() {
      return "!!";
    }

    @Override
    public void run() {
      // change view logic
      if (this.mc.x < LNZ.small_number) {
        AbstractGameMap.this.view_moving_left = true;
        if (!p.global.holding_right) {
          AbstractGameMap.this.view_moving_right = false;
        }
      }
      else if (this.mc.x > p.width - 1 - LNZ.small_number) {
        AbstractGameMap.this.view_moving_right = true;
        if (!p.global.holding_left) {
          AbstractGameMap.this.view_moving_left = false;
        }
      }
      else {
        if (!p.global.holding_right) {
          AbstractGameMap.this.view_moving_right = false;
        }
        if (!p.global.holding_left) {
          AbstractGameMap.this.view_moving_left = false;
        }
      }
      if (this.mc.y < LNZ.small_number) {
        AbstractGameMap.this.view_moving_up = true;
        if (!p.global.holding_down) {
          AbstractGameMap.this.view_moving_down = false;
        }
      }
      else if (this.mc.y > p.height - 1 - LNZ.small_number) {
        AbstractGameMap.this.view_moving_down = true;
        if (!p.global.holding_up) {
          AbstractGameMap.this.view_moving_up = false;
        }
      }
      else {
        if (!p.global.holding_down) {
          AbstractGameMap.this.view_moving_down = false;
        }
        if (!p.global.holding_up) {
          AbstractGameMap.this.view_moving_up = false;
        }
      }
      // update cursor
      AbstractGameMap.this.updateCursorPosition(this.mc);
      // selected object textbox
      if (AbstractGameMap.this.selected_object != null && AbstractGameMap.this.selected_object_textbox != null) {
        AbstractGameMap.this.selected_object_textbox.mouseMove((float)this.mc.x, (float)this.mc.y);
      }
      // prepare for map hover logic
      if (AbstractGameMap.this.draw_fog) {
        AbstractGameMap.this.hovered_explored = false;
        AbstractGameMap.this.hovered_visible = false;
      }
      else {
        AbstractGameMap.this.hovered_explored = true;
        AbstractGameMap.this.hovered_visible = true;
      }
      boolean default_cursor = true;
      boolean viewing_inventory = false;
      if (AbstractGameMap.this.units.containsKey(0) && Hero.class.isInstance(AbstractGameMap.this.units.get(0))) {
        viewing_inventory = ((Hero)AbstractGameMap.this.units.get(0)).inventory.viewing;
      }
      // map hover logic
      if (mc.x > AbstractGameMap.this.xi_map_old &&
        mc.y > AbstractGameMap.this.yi_map_old &&
        mc.x < AbstractGameMap.this.xf_map_old &&
        mc.y < AbstractGameMap.this.yf_map_old &&
        !viewing_inventory) {
        AbstractGameMap.this.hovered = true;
        AbstractGameMap.this.hovered_area = true;
        AbstractGameMap.this.hovered_border = false;
        // update hovered for map objects
        AbstractGameMap.this.hovered_object = null;
        AbstractGameMap.this.hovered_terrain = null;
        try {
          if (!AbstractGameMap.this.draw_fog || AbstractGameMap.this.mapSquare(
            AbstractGameMap.this.mc).visible) {
            AbstractGameMap.this.hovered_explored = true;
            AbstractGameMap.this.hovered_visible = true;
          }
          else if (!AbstractGameMap.this.draw_fog || AbstractGameMap.this.mapSquare(
            AbstractGameMap.this.mc).explored) {
            AbstractGameMap.this.hovered_explored = true;
          }
        } catch(NullPointerException e) {}
        if (!p.global.holding_alt) {
          int current_feature_priority = 0;
          for (Feature f : AbstractGameMap.this.features()) {
            if (f.map_priority < current_feature_priority) {
              f.hovered = false;
              continue;
            }
            f.mouseMove(AbstractGameMap.this.mc);
            if (f.hovered) {
              if (!AbstractGameMap.this.force_all_hoverable) {
                switch(f.ID) {
                  case 186: // outside light source
                  case 187: // invisible light source
                  case 188:
                  case 189:
                  case 190:
                    f.hovered = false;
                    continue;
                }
              }
              if (!AbstractGameMap.this.hovered_explored) {
                f.hovered = false;
                continue;
              }
              AbstractGameMap.this.hovered_object = f;
              current_feature_priority = f.map_priority;
              if (AbstractGameMap.this.units.containsKey(0)) {
                Unit player = AbstractGameMap.this.units.get(0);
                if (f.targetableByUnit() && (Hero.class.isInstance(player) || !f.targetableByHeroOnly())) {
                    p.global.setCursor("icons/cursor_interact.png");
                    default_cursor = false;
                  }
              }
            }
          }
          Iterator<Map.Entry<Integer, Unit>> unit_iterator = AbstractGameMap.this.units.entrySet().iterator();
          while(unit_iterator.hasNext()) {
            Map.Entry<Integer, Unit> entry = unit_iterator.next();
            Unit u = entry.getValue();
            Coordinate unit_mc_object = AbstractGameMap.this.last_mc.copy();
            unit_mc_object.y += 0.5 * LNZ.root_two * u.size * AbstractGameMap.this.zoom_old;
            unit_mc_object.y += 0.25 * u.curr_height * AbstractGameMap.this.zoom_old;
            unit_mc_object = AbstractGameMap.this.screenToMapCoordinate(unit_mc_object);
            u.mouseMove(unit_mc_object);
            if (u.hovered) {
              if (!AbstractGameMap.this.hovered_visible) {
                if (AbstractGameMap.this.units.containsKey(0)) {
                  if (!AbstractGameMap.this.hovered_explored || AbstractGameMap.this.units.get(0).alliance != u.alliance) {
                    u.hovered = false;
                    continue;
                  }
                }
                else {
                  u.hovered = false;
                  continue;
                }
              }
              AbstractGameMap.this.hovered_object = u;
              if (AbstractGameMap.this.units.containsKey(0) && u.alliance != AbstractGameMap.this.units.get(0).alliance) {
                p.global.setCursor("icons/cursor_attack.png");
                default_cursor = false;
              }
            }
          }
          Iterator<Map.Entry<Integer, Item>> item_iterator = AbstractGameMap.this.items.entrySet().iterator();
          while(item_iterator.hasNext()) {
            Map.Entry<Integer, Item> entry = item_iterator.next();
            Item i = entry.getValue();
            Coordinate item_mc_object = AbstractGameMap.this.last_mc.copy();
            item_mc_object.y += 0.25 * i.curr_height * AbstractGameMap.this.zoom_old;
            item_mc_object = AbstractGameMap.this.screenToMapCoordinate(item_mc_object);
            i.mouseMove(item_mc_object);
            if (i.hovered) {
              if (!AbstractGameMap.this.hovered_visible) {
                i.hovered = false;
                continue;
              }
              AbstractGameMap.this.hovered_object = i;
              if (AbstractGameMap.this.units.containsKey(0)) {
                p.global.setCursor("icons/cursor_pickup.png");
                default_cursor = false;
              }
            }
          }
        }
        // hovered for terrain
        if (AbstractGameMap.this.hovered_object == null && p.global.holding_alt) {
          AbstractGameMap.this.hovered_terrain = AbstractGameMap.this.mapSquare(AbstractGameMap.this.mc);
          if (AbstractGameMap.this.hovered_terrain != null && !AbstractGameMap.this.hovered_terrain.explored) {
            AbstractGameMap.this.hovered_terrain = null;
          }
          if (AbstractGameMap.this.units.containsKey(0) &&
            AbstractGameMap.this.in_control &&
            AbstractGameMap.this.hovered_terrain != null &&
            !AbstractGameMap.this.hovered_terrain.interactionTooltip(
              AbstractGameMap.this.units.get(0)).equals("")) {
            p.global.setCursor("icons/cursor_interact.png");
            default_cursor = false;
          }
        }
        // hovered for header message
        for (HeaderMessage message : AbstractGameMap.this.headerMessages) {
          message.mouseMove(this.mc.x, this.mc.y);
        }
      }
      else {
        AbstractGameMap.this.hovered = false;
        AbstractGameMap.this.hovered_object = null;
        AbstractGameMap.this.hovered_terrain = null;
        AbstractGameMap.this.hovered_explored = false;
        AbstractGameMap.this.hovered_visible = false;
        if (mc.x > AbstractGameMap.this.xi && mc.y > AbstractGameMap.this.yi &&
        mc.x < AbstractGameMap.this.xf && mc.y < AbstractGameMap.this.yf) {
          AbstractGameMap.this.hovered_area = true;
          if (mc.x < AbstractGameMap.this.xi + LNZ.map_borderSize ||
            mc.x > AbstractGameMap.this.xf - LNZ.map_borderSize ||
            mc.y < AbstractGameMap.this.yi + LNZ.map_borderSize ||
            mc.y > AbstractGameMap.this.yf - LNZ.map_borderSize) {
            AbstractGameMap.this.hovered_border = true;
          }
          else {
            AbstractGameMap.this.hovered_border = false;
          }
        }
        else {
          AbstractGameMap.this.hovered_area = false;
          AbstractGameMap.this.hovered_border = false;
        }
        // dehover map objects
        for (Feature f : AbstractGameMap.this.features()) {
          f.hovered = false;
        }
        for (Map.Entry<Integer, Unit> entry : AbstractGameMap.this.units.entrySet()) {
          entry.getValue().hovered = false;
        }
        for (Map.Entry<Integer, Item> entry : AbstractGameMap.this.items.entrySet()) {
          entry.getValue().hovered = false;
        }
      }
      // aiming for player
      if (AbstractGameMap.this.units.containsKey(0) && p.global.holding_ctrl && !viewing_inventory && AbstractGameMap.this.in_control) {
        switch(AbstractGameMap.this.units.get(0).curr_action) {
          case AIMING:
            AbstractGameMap.this.units.get(0).aim(AbstractGameMap.this.mc);
            break;
          case MOVING:
            AbstractGameMap.this.units.get(0).moveTo(AbstractGameMap.this.mc, AbstractGameMap.this);
            break;
          case NONE:
            AbstractGameMap.this.units.get(0).face(AbstractGameMap.this.mc);
            break;
          default:
            break;
        }
      }
      if (default_cursor) {
        p.global.defaultCursor("icons/cursor_interact.png", "icons/cursor_attack.png", "icons/cursor_pickup.png");
      }
      // restart
      if (AbstractGameMap.this.restart_mouseMoveThread && !this.kill_thread) {
        AbstractGameMap.this.startMouseMoveThread();
        AbstractGameMap.this.restart_mouseMoveThread = false;
      }
    }
  }


  protected LNZ p;

  protected GameMapCode code = GameMapCode.ERROR;
  protected String mapName = "";
  protected boolean nullify = false;
  protected double lastFPS = 0;

  protected int terrain_resolution = LNZ.map_terrainResolutionDefault; // TODO: can change in options
  protected DImg map_display; // everything but fog
  protected PImage fog_display;
  protected MapFogHandling fog_handling = MapFogHandling.DEFAULT;
  protected int fog_color = LNZ.color_fog;
  protected boolean draw_fog = true;
  protected AbstractTerrainDimgThread terrain_dimg_thread;

  protected Coordinate view = new Coordinate(0, 0);
  protected Coordinate view_old = new Coordinate(0, 0);
  protected double zoom = LNZ.map_defaultZoom;
  protected double zoom_old = LNZ.map_defaultZoom;
  protected boolean view_moving_left = false;
  protected boolean view_moving_right = false;
  protected boolean view_moving_up = false;
  protected boolean view_moving_down = false;

  protected double xi = 0; // general map area in screen coordinates
  protected double yi = 0;
  protected double xf = 0;
  protected double yf = 0;
  protected int color_border = 0;
  protected int color_background = 0;
  protected int color_tint = LNZ.color_transparent;
  protected boolean show_tint = false;
  protected boolean grayscale_image = false;

  protected double xi_map = 0; // specific map area in screen coordinates
  protected double yi_map = 0;
  protected double xf_map = 0;
  protected double yf_map = 0;
  protected double xi_map_old = 0;
  protected double yi_map_old = 0;
  protected double xf_map_old = 0;
  protected double yf_map_old = 0;

  protected Coordinate image_grid_start = new Coordinate(0, 0);
  protected Coordinate image_grid_end = new Coordinate(0, 0);
  protected Coordinate image_grid_start_old = new Coordinate(0, 0);
  protected Coordinate image_grid_end_old = new Coordinate(0, 0);

  protected boolean hovered = false; // hover map
  protected boolean hovered_area = false; // hover GameMap-given area
  protected boolean hovered_border = false; // hover border
  protected boolean hovered_explored = false;
  protected boolean hovered_visible = false;
  protected boolean force_all_hoverable = false;

  protected Coordinate mc = new Coordinate(0, 0); // map coordinates
  protected Coordinate mc_object = new Coordinate(0, 0); // map coordinates (for selecting objects)
  protected Coordinate base_mc = new Coordinate(0, 0); // map coordinates (for drawing player move visual effect)
  protected Coordinate last_mc = new Coordinate(0, 0); // screen coordinates
  protected MouseMoveThread mouse_move_thread = null;
  protected boolean restart_mouseMoveThread = false;
  protected MapObject hovered_object = null;
  protected GameMapSquare hovered_terrain = null;
  protected MapObject selected_object = null;
  protected GameMapSquare selected_terrain = null;
  protected SelectedObjectTextbox selected_object_textbox = null;
  protected ArrayList<HeaderMessage> headerMessages = new ArrayList<HeaderMessage>();

  protected int next_feature_key = 1;
  protected Map<IntegerCoordinate, List<FeatureDrawGridPiece>>
    feature_draw_grid_locations_map = new HashMap<IntegerCoordinate, List<FeatureDrawGridPiece>>();
  protected HashMap<Integer, Unit> units = new HashMap<Integer, Unit>();
  private int next_unit_key = 1;
  protected int zombie_counter = 0;
  protected boolean in_control = true;
  protected HashMap<Integer, Item> items = new HashMap<Integer, Item>();
  private int next_item_key = 1;
  protected List<Projectile> projectiles = new ArrayList<Projectile>();
  protected List<VisualEffect> visualEffects = new ArrayList<VisualEffect>();

  protected double timer_refresh_fog = 0;
  protected double base_light_level = LNZ.level_dayLightLevel;
  protected boolean outside_map = true;
  protected int max_height = LNZ.map_maxHeight;

  protected int timer_update_squares = LNZ.map_updateSquaresTimer;
  protected int last_update_time = 0;


  AbstractGameMap(LNZ sketch) {
    this.p = sketch;
    this.map_display = new DImg(sketch, 1, 1);
    this.fog_display = p.createImage(1, 1, PConstants.ARGB);
    this.color_background = sketch.global.color_mapBackground;
    this.color_border = sketch.global.color_mapBorder;
  }


  // Should always be called before nullifying references
  synchronized void close() {
    this.nullify = true;
    if (this.terrain_dimg_thread != null) {
      this.terrain_dimg_thread.killThread();
    }
    if (this.mouse_move_thread != null) {
      this.mouse_move_thread.kill_thread = true;
    }
  }


  abstract int mapXI();
  abstract int mapYI();
  abstract int mapXF();
  abstract int mapYF();
  abstract int currMapXI();
  abstract int currMapYI();
  abstract int currMapXF();
  abstract int currMapYF();
  int currWidth() {
    return this.currMapXF() - this.currMapXI();
  }
  int currHeight() {
    return this.currMapYF() - this.currMapYI();
  }
  int gridImageXI() {
    return this.mapToGridImageCoordinate(
      new IntegerCoordinate(this.currMapXI(), this.currMapYF() - 1)).x;
  }
  int gridImageYI() {
    return this.mapToGridImageCoordinate(
      new IntegerCoordinate(this.currMapXI(), this.currMapYI())).y;
  }
  int gridImageXF() {
    return this.mapToGridImageCoordinate(
      new IntegerCoordinate(this.currMapXF() + 1, this.currMapYI())).x;
  }
  int gridImageYF() {
    return this.mapToGridImageCoordinate(
      new IntegerCoordinate(this.currMapXF(), this.currMapYF())).y;
  }

  Biome getBiomeAt(double x, double y) {
    return Biome.NONE;
  }
  GameMapSquare mapSquare(IntegerCoordinate coordinate) {
    return this.mapSquare(coordinate.x, coordinate.y);
  }
  GameMapSquare mapSquare(Coordinate coordinate) {
    return this.mapSquare(coordinate.x, coordinate.y);
  }
  GameMapSquare mapSquare(double i, double j) {
    return this.mapSquare((int)Math.floor(i), (int)Math.floor(j));
  }
  abstract GameMapSquare mapSquare(int i, int j); // return null if out of bounds

  abstract void initializeSquares();
  void initializeTerrain() {
    try {
      //this.terrain_resolution = p.global.profile.options.terrain_resolution;
    } catch(Exception e) {}
    this.initializeBackgroundImage();
    this.setFogHandling(this.fog_handling);
  }
  abstract void initializeBackgroundImage();

  void setLocation(double xi, double yi, double xf, double yf) {
    this.xi = xi;
    this.yi = yi;
    this.xf = xf;
    this.yf = yf;
    if (this.selected_object_textbox != null) {
      this.selected_object_textbox.setXLocation(LNZ.map_selectedObjectPanelGap,
        xi - LNZ.map_selectedObjectPanelGap);
    }
    this.refreshDisplayMapParameters();
  }

  void setFogHandling(MapFogHandling fog_handling) {
    this.fog_handling = fog_handling;
    switch(fog_handling) {
      case DEFAULT:
        for (int i = this.currMapXI(); i < this.currMapXF(); i++) {
          for (int j = this.currMapYI(); j < this.currMapYF(); j++) {
            GameMapSquare square = this.mapSquare(i, j);
            if (square == null) {
              continue;
            }
            if (square.mapEdge()) {
              this.colorFogGrid(LNZ.color_transparent, i, j);
            }
            else if (!square.explored) {
              this.colorFogGrid(LNZ.color_black, i, j);
            }
            else if (!square.visible) {
              this.colorFogGrid(this.fog_color, i, j);
            }
            else {
              this.colorFogGrid(LNZ.color_transparent, i, j);
            }
          }
        }
        break;
      case NONE:
        for (int i = this.currMapXI(); i < this.currMapXF(); i++) {
          for (int j = this.currMapYI(); j < this.currMapYF(); j++) {
            this.exploreTerrainAndVisible(i, j, false);
            this.colorFogGrid(LNZ.color_transparent, i, j);
          }
        }
        break;
      case NOFOG:
        for (int i = this.currMapXI(); i < this.currMapXF(); i++) {
          for (int j = this.currMapYI(); j < this.currMapYF(); j++) {
            this.setTerrainVisible(true, i, j, false);
            GameMapSquare square = this.mapSquare(i, j);
            if (square == null) {
              continue;
            }
            if (square.mapEdge()) {
              this.colorFogGrid(LNZ.color_transparent, i, j);
            }
            else if (!square.explored) {
              this.colorFogGrid(LNZ.color_black, i, j);
            }
            else {
              this.colorFogGrid(LNZ.color_transparent, i, j);
            }
          }
        }
        break;
      case EXPLORED:
        for (int i = this.currMapXI(); i < this.currMapXF(); i++) {
          for (int j = this.currMapYI(); j < this.currMapYF(); j++) {
            this.exploreTerrain(i, j, false);
            GameMapSquare square = this.mapSquare(i, j);
            if (square == null) {
              continue;
            }
            if (square.mapEdge()) {
              this.colorFogGrid(LNZ.color_transparent, i, j);
            }
            else if (!square.visible) {
              this.colorFogGrid(this.fog_color, i, j);
            }
            else {
              this.colorFogGrid(LNZ.color_transparent, i, j);
            }
          }
        }
        break;
      default:
        p.global.errorMessage("ERROR: Fog handling " + fog_handling.name + " not recognized.");
        break;
    }
    this.refreshTerrainImage();
  }

  void colorFogGrid(int c, int i, int j) {
    this.colorFogGrid(c, new IntegerCoordinate(i, j));
  }
  abstract void colorFogGrid(int c, IntegerCoordinate grid);
  void terrainImageGrid(PImage img, int i, int j) {
    this.terrainImageGrid(img, new IntegerCoordinate(i, j));
  }
  void terrainImageGrid(PImage img, IntegerCoordinate grid) {
    this.terrainImageGrid(img, grid, 1, 1);
  }
  void terrainImageGrid(PImage img, int i, int j, int w, int h) {
    this.terrainImageGrid(img, new IntegerCoordinate(i, j), w, h);
  }
  abstract void terrainImageGrid(PImage img, IntegerCoordinate grid, int w, int h);
  void refreshTerrainImageGrid(IntegerCoordinate grid) {
    GameMapSquare square = this.mapSquare(grid);
    if (square == null) {
      return;
    }
    PImage terrain_image = square.imageOverflows() ?
      square.defaultImage() : square.terrainImage();
    this.terrainImageGrid(terrain_image, grid);
    for (Feature f : square.features) {
      if (f.terrainImageHeightOverflow() != 0) {
        continue;
      }
      this.refreshFeature(f);
    }
  }
  void colorTerrainGrid(int c, IntegerCoordinate grid) {
    this.colorTerrainGrid(c, grid, 1, 1);
  }
  abstract void colorTerrainGrid(int c, IntegerCoordinate grid, int w, int h);

  void refreshDisplayMapParameters() {
    Coordinate view = this.view.copy();
    Coordinate screen_start = new Coordinate(
      this.xi + LNZ.map_borderSize, this.yi + LNZ.map_borderSize);
    this.image_grid_start = this.screenToGridImageCoordinate(screen_start, this.zoom, view);
    double grid_image_xi = this.gridImageXI();
    if (image_grid_start.x < grid_image_xi) {
      this.xi_map = screen_start.x + this.zoom * (grid_image_xi - image_grid_start.x);
      image_grid_start.x = grid_image_xi;
    }
    else {
      this.xi_map = screen_start.x;
    }
    double grid_image_yi = this.gridImageYI();
    if (image_grid_start.y < grid_image_yi) {
      this.yi_map = screen_start.y + 0.5 * this.zoom * (grid_image_yi - image_grid_start.y);
      image_grid_start.y = grid_image_yi;
    }
    else {
      this.yi_map = screen_start.y;
    }
    Coordinate screen_end = new Coordinate(
      this.xf - LNZ.map_borderSize, this.yf - LNZ.map_borderSize);
    this.image_grid_end = this.screenToGridImageCoordinate(screen_end, this.zoom, view);
    double grid_image_xf = this.gridImageXF();
    if (image_grid_end.x > grid_image_xf) {
      this.xf_map = screen_end.x - this.zoom * (image_grid_end.x - grid_image_xf);
      image_grid_end.x = grid_image_xf;
    }
    else {
      this.xf_map = screen_end.x;
    }
    if (image_grid_end.x < image_grid_start.x) {
      image_grid_end.x = image_grid_start.x;
      this.xf_map = this.xi_map;
    }
    double grid_image_yf = this.gridImageYF();
    if (image_grid_end.y > grid_image_yf) {
      this.yf_map = screen_end.y - 0.5 * this.zoom * (image_grid_end.y - grid_image_yf);
      image_grid_end.y = grid_image_yf;
    }
    else {
      this.yf_map = screen_end.y;
    }
    if (image_grid_end.y < image_grid_start.y) {
      image_grid_end.y = image_grid_start.y;
      this.yf_map = this.yi_map;
    }
    this.refreshTerrainImage();
  }

  void refreshTerrainImage() {
    if (this.terrain_dimg_thread != null && this.terrain_dimg_thread.isAlive()) {
      this.terrain_dimg_thread.update_terrain_display = true;
      return;
    }
    this.startTerrainDimgThread();
  }
  abstract void startTerrainDimgThread();

  void setZoom(double zoom) {
    if (zoom > LNZ.map_maxZoom) {
      zoom = LNZ.map_maxZoom;
    }
    else if (zoom < LNZ.map_minZoom) {
      zoom = LNZ.map_minZoom;
    }
    this.zoom = zoom;
    this.refreshDisplayMapParameters();
  }
  void changeZoom(double amount) {
    this.setZoom(this.zoom + amount);
  }

  void refreshViewLocation() {
    this.setViewLocation(this.view);
  }
  void setViewLocation(double x, double y) {
    this.setViewLocation(new Coordinate(x, y));
  }
  void setViewLocation(Coordinate new_view) {
    this.setViewLocation(new_view, true);
  }
  void setViewLocation(Coordinate new_view, boolean refreshImage) {
    if (new_view.x < this.mapXI()) {
      new_view.x = this.mapXI();
    }
    else if (new_view.x > this.mapXF()) {
      new_view.x = this.mapXF();
    }
    if (new_view.y < this.mapYI()) {
      new_view.y = mapYI();
    }
    else if (new_view.y > this.mapYF()) {
      new_view.y = this.mapYF();
    }
    this.view = new_view;
    if (refreshImage) {
      this.refreshDisplayMapParameters();
      this.updateCursorPosition();
    }
  }
  void moveView(Coordinate move_view) {
    this.moveView(move_view, true);
  }
  void moveView(Coordinate move_view, boolean refreshImage) {
    this.setViewLocation(this.view.move(move_view), refreshImage);
  }


  void setTerrain(int id, IntegerCoordinate grid) {
    this.setTerrain(id, grid, true);
  }
  void setTerrain(int id, int i, int j) {
    this.setTerrain(id, new IntegerCoordinate(i, j), true);
  }
  void setTerrain(int id, int i, int j, boolean refresh_image) {
    this.setTerrain(id, new IntegerCoordinate(i, j), refresh_image);
  }
  void setTerrain(int id, IntegerCoordinate grid, boolean refresh_image) {
    try {
      this.mapSquare(grid).setTerrain(id);
      this.refreshTerrainImageGrid(grid);
      if (refresh_image) {
        this.refreshTerrainImage();
      }
    }
    catch(NullPointerException e) {}
  }
  void setTerrainBaseElevation(int h, IntegerCoordinate grid) {
    GameMapSquare square = this.mapSquare(grid);
    if (square == null) {
      p.global.log("WARNING: Could not find map square " + grid.toString() +
        " to adjust terrain base elevation.");
      return;
    }
    square.base_elevation = PApplet.constrain(h, LNZ.map_minHeight, this.max_height);
    this.refreshTerrainImage();
  }
  void exploreRectangle(Rectangle rect) {
    for (int i = (int)Math.floor(rect.xi); i < (int)Math.floor(rect.xf); i++) {
      for (int j = (int)Math.floor(rect.yi); j < (int)Math.floor(rect.yf); j++) {
        this.exploreTerrain(i, j, true);
      }
    }
  }
  void exploreTerrain(int x, int y) {
    this.exploreTerrain(new IntegerCoordinate(x, y));
  }
  void exploreTerrain(IntegerCoordinate grid) {
    this.exploreTerrain(grid, true);
  }
  void exploreTerrain(int x, int y, boolean refresh_fog_image) {
    this.exploreTerrain(new IntegerCoordinate(x, y), refresh_fog_image);
  }
  void exploreTerrain(IntegerCoordinate grid, boolean refresh_fog_image) {
    try {
      if (this.mapSquare(grid).explored) {
        return;
      }
      this.mapSquare(grid).explored = true;
      if (refresh_fog_image) {
        if (this.mapSquare(grid).mapEdge()) {
          this.colorFogGrid(LNZ.color_transparent, grid);
        }
        else if (this.mapSquare(grid).visible) {
          this.colorFogGrid(this.mapSquare(grid).getColor(LNZ.color_transparent), grid);
        }
        else {
          this.colorFogGrid(this.mapSquare(grid).getColor(this.fog_color), grid);
        }
      }
    }
    catch(NullPointerException e) {}
  }
  void exploreTerrainAndVisible(int x, int y) {
    this.exploreTerrainAndVisible(x, y, true);
  }
  void exploreTerrainAndVisible(int x, int y, boolean refresh_fog_image) {
    try {
      if (this.mapSquare(x, y).explored && this.mapSquare(x, y).visible) {
        return;
      }
      this.mapSquare(x, y).explored = true;
      this.mapSquare(x, y).visible = true;
      if (refresh_fog_image) {
        if (this.mapSquare(x, y).mapEdge()) {
          this.colorFogGrid(LNZ.color_transparent, x, y);
        }
        else {
          this.colorFogGrid(this.mapSquare(x, y).getColor(LNZ.color_transparent), x, y);
        }
      }
    }
    catch(NullPointerException e) {}
  }
  void setTerrainVisible(boolean visible, int x, int y) {
    this.setTerrainVisible(visible, x, y, true);
  }
  void setTerrainVisible(boolean visible, int x, int y, boolean refreshFogImage) {
    if (this.fog_handling == MapFogHandling.NONE || this.fog_handling == MapFogHandling.NOFOG) {
      return;
    }
    try {
      if (this.mapSquare(x, y).visible == visible) {
        return;
      }
      this.mapSquare(x, y).visible = visible;
      if (refreshFogImage) {
        if (!this.mapSquare(x, y).explored) {
        }
        else if (this.mapSquare(x, y).mapEdge()) {
          this.colorFogGrid(LNZ.color_transparent, x, y);
        }
        else if (this.mapSquare(x, y).visible) {
          this.colorFogGrid(this.mapSquare(x, y).getColor(LNZ.color_transparent), x, y);
        }
        else {
          this.colorFogGrid(this.mapSquare(x, y).getColor(this.fog_color), x, y);
        }
      }
    }
    catch(NullPointerException e) {}
  }


  // add feature
  void addFeature(int id, int x, int y) {
    this.addFeature(new Feature(p, id, x, y), true);
  }
  void addFeature(int id, IntegerCoordinate coordinate) {
    this.addFeature(new Feature(p, id, coordinate.x, coordinate.y), true);
  }
  void addFeature(Feature f) {
    this.addFeature(f, true);
  }
  void addFeature(Feature f, boolean refresh_image) {
    this.addFeature(f, refresh_image, this.nextFeatureKey());
  }
  synchronized int nextFeatureKey() {
    return this.next_feature_key++;
  }
  void addFeature(Feature f, boolean refresh_image, int code) {
    if (!f.inMap(this.mapXI(), this.mapYI(), this.mapXF(), this.mapYF())) {
      return;
    }
    this.addFeatureToTerrain(f);
    if (f.displaysImage() && f.terrainImageHeightOverflow() == 0) {
      this.terrainImageGrid(f.getImage(), f.gridLocation(), f.sizeX, f.sizeY);
    }
    if (refresh_image) {
      this.refreshTerrainImage();
    }
    f.map_key = code;
    switch(f.ID) {
      case 195: // light switches
      case 196:
      case 197:
      case 198:
        if (f.number <= 0) {
          f.number = f.map_key - 1;
        }
        break;
      default:
        break;
    }
    this.actuallyAddFeature(code, f);
  }
  boolean featureCanBePlaced(Feature f) {
    if (f == null || f.remove) {
      return false;
    }
    for (int i = LNZApplet.round(f.coordinate.x); i < LNZApplet.round(f.coordinate.x + f.sizeX); i++) {
      for (int j = LNZApplet.round(f.coordinate.y); j < LNZApplet.round(f.coordinate.y + f.sizeY); j++) {
        if (f.ignoreSquare(i - LNZApplet.round(f.coordinate.x), j - LNZApplet.round(f.coordinate.y))) {
          continue;
        }
        try {
          GameMapSquare square = this.mapSquare(i, j);
          if (!square.canPlaceOn()) {
            return false;
          }
        } catch(NullPointerException e) {
          return false;
        }
      }
    }
    return true;
  }
  abstract void actuallyAddFeature(int code, Feature f);
  abstract void featureAddedMapSquareNotFound(IntegerCoordinate coordinate, Feature f);
  abstract void featureRemovedMapSquareNotFound(IntegerCoordinate coordinate, Feature f);

  // remove feature
  void removeFeature(int code) {
    Feature f = this.getFeature(code);
    if (f == null || f.removed) {
      return;
    }
    if (!f.inMap(this.mapXI(), this.mapYI(), this.mapXF(), this.mapYF())) {
      return;
    }
    f.remove = true;
    f.removed = true;
    this.removeFeatureFromTerrain(f);
    if (!f.displaysImage() || f.terrainImageHeightOverflow() != 0) {
      return;
    }
    this.removeFeatureImage(f);
    this.refreshTerrainImage();
  }
  abstract Feature getFeature(int code);
  abstract Collection<Feature> features();

  void addFeatureToTerrain(Feature f) {
    if (f == null || f.remove) {
      return;
    }
    IntegerCoordinate feature_loc = f.gridLocation();
    for (int i = feature_loc.x; i < feature_loc.x + f.sizeX; i++) {
      for (int j = feature_loc.y; j < feature_loc.y + f.sizeY; j++) {
        if (f.ignoreSquare(i - feature_loc.x, j - feature_loc.y)) {
          continue;
        }
        GameMapSquare square = this.mapSquare(i, j);
        if (square != null) {
          square.addedFeature(f, i - feature_loc.x, j - feature_loc.y);
        }
        else {
          this.featureAddedMapSquareNotFound(new IntegerCoordinate(i, j), f);
        }
      }
    }
  }

  void removeFeatureFromTerrain(Feature f) {
    if (f == null) {
      return;
    }
    IntegerCoordinate feature_loc = f.gridLocation();
    for (int i = feature_loc.x; i < feature_loc.x + f.sizeX; i++) {
      for (int j = feature_loc.y; j < feature_loc.y + f.sizeY; j++) {
        if (f.ignoreSquare(i - feature_loc.x, j - feature_loc.y)) {
          continue;
        }
        GameMapSquare square = this.mapSquare(i, j);
        if (square != null) {
          square.removedFeature(f, i - feature_loc.x, j - feature_loc.y);
        }
        else {
          this.featureRemovedMapSquareNotFound(new IntegerCoordinate(i, j), f);
        }
      }
    }
  }

  // refresh feature image and terrain addition/removal (remove then add)
  void refreshFeature(int code) {
    this.refreshFeature(this.getFeature(code));
  }
  void refreshFeature(Feature f) {
    if (f == null || f.remove || f.terrainImageHeightOverflow() != 0) {
      return;
    }
    f.refresh_map_image = false;
    if (!f.inMap(this.mapXI(), this.mapYI(), this.mapXF(), this.mapYF())) {
      return;
    }
    if (!f.displaysImage()) {
      return;
    }
    int f_map_priority = f.map_priority;
    ArrayList<ImagePieceReturn> higher_priority_images = this.removeFeatureImage(f, true);
    this.terrainImageGrid(f.getImage(), f.gridLocation(), f.sizeX, f.sizeY);
    this.refreshTerrainImage();
    this.removeFeatureFromTerrain(f);
    this.addFeatureToTerrain(f);
    for (ImagePieceReturn img_return : higher_priority_images) {
      this.terrainImageGrid(img_return.img, img_return.overlap,
        img_return.w_overlap, img_return.h_overlap);
    }
    f.map_priority = f_map_priority;
  }

  class ImagePieceReturn {
    private PImage img;
    private IntegerCoordinate overlap;
    private int w_overlap;
    private int h_overlap;
    ImagePieceReturn(PImage img, int xi_overlap, int yi_overlap,
      int w_overlap, int h_overlap) {
      this.img = img;
      this.overlap = new IntegerCoordinate(xi_overlap, yi_overlap);
      this.w_overlap = w_overlap;
      this.h_overlap = h_overlap;
    }
  }

  ArrayList<ImagePieceReturn> removeFeatureImage(Feature f) {
    return this.removeFeatureImage(f, false);
  }
  ArrayList<ImagePieceReturn> removeFeatureImage(Feature f, boolean refreshing_feature_image) {
    ArrayList<ImagePieceReturn> higher_priority_images = new ArrayList<ImagePieceReturn>();
    IntegerCoordinate feature_loc = f.gridLocation();
    this.colorTerrainGrid(LNZ.color_transparent, feature_loc.copy(), f.sizeX, f.sizeY);
    for (int i = feature_loc.x; i < feature_loc.x + f.sizeX; i++) {
      for (int j = feature_loc.y; j < feature_loc.y + f.sizeY; j++) {
        GameMapSquare square = this.mapSquare(i, j);
        if (square == null || square.imageOverflows()) {
          continue;
        }
        this.terrainImageGrid(square.terrainImage(), i, j);
      }
    }
    for (Feature f2 : this.features()) {
      if (f2.map_key == f.map_key || f2.terrainImageHeightOverflow() != 0) {
        continue;
      }
      if (f2.coordinate.x < f.coordinate.x + f.sizeX &&
        f2.coordinate.y < f.coordinate.y + f.sizeY &&
        f2.coordinate.x + f2.sizeX > f.coordinate.x &&
        f2.coordinate.y + f2.sizeY > f.coordinate.y) {
        DImg dimg = new DImg(p, f2.getImage());
        dimg.setGrid(f2.sizeX, f2.sizeY);
        int xi_overlap = LNZApplet.round(Math.max(f.coordinate.x, f2.coordinate.x));
        int yi_overlap = LNZApplet.round(Math.max(f.coordinate.y, f2.coordinate.y));
        int w_overlap = LNZApplet.round(Math.min(f.xf() - xi_overlap, f2.xf() - xi_overlap));
        int h_overlap = LNZApplet.round(Math.min(f.yf() - yi_overlap, f2.yf() - yi_overlap));
        PImage imagePiece = dimg.getImageGridPiece(xi_overlap - LNZApplet.round(f2.coordinate.x),
          yi_overlap - LNZApplet.round(f2.coordinate.y), w_overlap, h_overlap);
        if (refreshing_feature_image && f2.map_priority > f.map_priority) {
          higher_priority_images.add(new ImagePieceReturn(imagePiece, xi_overlap,
            yi_overlap, w_overlap, h_overlap));
          continue;
        }
        this.terrainImageGrid(imagePiece, xi_overlap, yi_overlap, w_overlap, h_overlap);
      }
    }
    return higher_priority_images;
  }

  // add unit
  void addUnit(Unit u, double x, double y) {
    u.setLocation(x, y);
    this.addUnit(u);
  }
  void addUnit(Unit u) {
    this.addUnit(u, this.nextUnitKey());
  }
  synchronized int nextUnitKey() {
    return this.next_unit_key++;
  }
  void addUnit(Unit u, int code) {
    if (this.units.containsKey(code) && u != this.units.get(code)) {
      p.global.log("WARNING: nextUnitKey corrupted at " + code + " since unit " +
        "with that code already exists.");
      this.addUnit(u);
      return;
    }
    this.units.put(code, u);
    u.map_key = code;
    if (u.coordinate.x - u.size - LNZ.small_number < this.mapXI()) {
      u.coordinate.x = this.mapXI() + u.size + LNZ.small_number;
    }
    else if (u.coordinate.x + u.size + LNZ.small_number > this.mapXF()) {
      u.coordinate.x = this.mapXF() - u.size - LNZ.small_number;
    }
    if (u.coordinate.y - u.size - LNZ.small_number < this.mapYI()) {
      u.coordinate.y = this.mapYI() + u.size + LNZ.small_number;
    }
    else if (u.coordinate.y + u.size + LNZ.small_number > this.mapYF()) {
      u.coordinate.y = this.mapYF() - u.size - LNZ.small_number;
    }
    u.curr_squares_on = u.getSquaresOn();
    u.resolveFloorHeight(this);
    u.curr_height = u.floor_height;
    if (u.type.equals("Zombie")) {
      this.zombie_counter++;
    }
    GameMapSquare square = this.mapSquare(u.coordinate);
    if (square == null) {
      if (code != 0) {
        p.global.log("WARNING: Non-player unit being added to null square.");
      }
    }
    else {
      square.addUnit(u);
    }
  }
  // remove unit
  void removeUnit(int code) {
    if (this.units.containsKey(code)) {
      Unit u = this.units.get(code);
      u.remove = true;
      GameMapSquare square = this.mapSquare(u.coordinate);
      if (square != null) {
        square.removeUnit(u);
      }
    }
  }
  // add player unit
  void addPlayer(Hero player) {
    player.ai_controlled = false;
    this.addUnit(player, 0);
    this.setViewLocation(player.coordinate.copy());
  }

  // add item
  void addItem(Item i) {
    this.addItem(i, true);
  }
  void addItemAsIs(Item i) {
    int disappear_timer = i.disappear_timer;
    double curr_height = i.curr_height;
    this.addItem(i, i.disappearing);
    i.disappear_timer = disappear_timer;
    i.curr_height = curr_height;
  }
  void addItemAsIs(Item i, int code) {
    int disappear_timer = i.disappear_timer;
    double curr_height = i.curr_height;
    this.addItem(i, code, i.disappearing);
    i.disappear_timer = disappear_timer;
    i.curr_height = curr_height;
  }
  void addItem(Item i, boolean auto_disappear) {
    this.addItem(i, this.nextItemKey(), auto_disappear);
  }
  void addItem(Item i, double x, double y) {
    this.addItem(i, x, y, true);
  }
  void addItem(Item i, Coordinate coordinate) {
    this.addItem(i, coordinate, true);
  }
  void addItem(Item i, double x, double y, boolean auto_disappear) {
    i.setLocation(x, y);
    this.addItem(i, this.nextItemKey(), auto_disappear);
  }
  void addItem(Item i, Coordinate coordinate, boolean auto_disappear) {
    i.setLocation(coordinate);
    this.addItem(i, this.nextItemKey(), auto_disappear);
  }
  synchronized int nextItemKey() {
    return this.next_item_key++;
  }
  void addItem(Item i, int code) {
    this.addItem(i, code, true);
  }
  void addItem(Item i, int code, boolean auto_disappear) {
    if (auto_disappear) {
      i.disappearing = true;
      i.disappear_timer = LNZ.item_disappearTimer;
    }
    else {
      i.disappearing = false;
    }
    i.recently_dropped = 1500;
    this.items.put(code, i);
    if (i.coordinate.x - i.size - LNZ.small_number < this.mapXI()) {
      i.coordinate.x = this.mapXI() + i.size + LNZ.small_number;
    }
    else if (i.coordinate.x + i.size + LNZ.small_number > this.mapXF()) {
      i.coordinate.x = this.mapXF() - i.size - LNZ.small_number;
    }
    if (i.coordinate.y - i.size - LNZ.small_number < this.mapYI()) {
      i.coordinate.y = this.mapYI() + i.size + LNZ.small_number;
    }
    else if (i.coordinate.y + i.size + LNZ.small_number > this.mapYF()) {
      i.coordinate.y = this.mapYF() - i.size - LNZ.small_number;
    }
    i.curr_height = this.heightOfSquare(i.coordinate);
    i.map_key = code;
    GameMapSquare square = this.mapSquare(i.coordinate);
    if (square != null) {
      square.addItem(i);
    }
  }
  // remove item
  void removeItem(int code) {
    if (this.items.containsKey(code)) {
      Item i = this.items.get(code);
      i.remove = true;
      GameMapSquare square = this.mapSquare(i.coordinate);
      if (square != null) {
        square.removeItem(i);
      }
    }
  }

  // add projectile
  void addProjectile(Projectile p) {
    this.projectiles.add(p);
  }
  // remove projectile
  void removeProjectile(int index) {
    if (index < 0 || index >= this.projectiles.size()) {
      return;
    }
    this.projectiles.remove(index);
  }

  // add visual effect
  void addVisualEffect(int id, double v_x, double v_y) {
    VisualEffect v = new VisualEffect(p, id);
    v.setLocation(v_x, v_y);
    this.addVisualEffect(v);
  }
  void addVisualEffect(int id, Coordinate c) {
    VisualEffect v = new VisualEffect(p, id);
    v.setLocation(c);
    this.addVisualEffect(v);
  }
  void addVisualEffect(VisualEffect v) {
    switch(v.ID) {
      case 4001:
        for (VisualEffect ve : this.visualEffects) {
          if (ve.ID == 4001) {
            ve.remove = true;
          }
        }
        break;
    }
    this.visualEffects.add(v);
  }
  // remove visual effect
  void removeVisualEffect(int index) {
    if (index < 0 || index >= this.visualEffects.size()) {
      return;
    }
    this.visualEffects.remove(index);
  }

  void addHeaderMessage(String message) {
    this.headerMessages.add(new HeaderMessage(p, message));
    if (this.headerMessages.size() > LNZ.map_maxHeaderMessages) {
      this.headerMessages.remove(0);
    }
  }
  void addHeaderMessage(String message, int message_id) {
    HeaderMessage header_message = new HeaderMessage(p, message);
    switch(message_id) {
      case 1: // center of screen
        header_message.placeCenter();
        header_message.clickable = false;
        break;
      case 2: // center of screen and longer
        header_message.placeCenter();
        header_message.show_time = 6000;
        header_message.clickable = false;
        break;
      case 3: // longer
        header_message.show_time = 5000;
        header_message.clickable = false;
        break;
      case 4: // center of screen and bigger
        header_message.placeCenter(40);
        header_message.clickable = false;
        break;
      case 5: // center of screen and bigger and longer
        header_message.placeCenter(40);
        header_message.show_time = 6000;
        header_message.clickable = false;
        break;
      default:
        break;
    }
    this.headerMessages.add(header_message);
    if (this.headerMessages.size() > LNZ.map_maxHeaderMessages) {
      this.headerMessages.remove(0);
    }
  }


  void displayNerdStats() {
    p.fill(DImg.ccolor(255));
    p.textSize(14);
    p.textAlign(PConstants.LEFT, PConstants.TOP);
    double y_stats = this.yi + 31;
    double line_height = p.textAscent() + p.textDescent() + 2;
    p.text("Map Location: " + this.code.displayName(), this.xi + 1, y_stats);
    y_stats += line_height;
    p.text("FPS: " + (int)p.global.lastFPS + " (" + (int)this.lastFPS + ")", this.xi + 1, y_stats);
    this.displayAbstractNerdStats(y_stats, line_height);
  }

  void displayAbstractNerdStats(double y_stats, double line_height) {
    y_stats += line_height;
    Map<Thread, StackTraceElement[]> all_threads = Thread.getAllStackTraces();
    p.text("Active Threads: " + all_threads.size(), this.xi + 1, y_stats);
    y_stats += line_height;
    int gamemap_threads = 0;
    int unit_threads = 0;
    for (Thread thread : all_threads.keySet()) {
      String thread_name = thread.getName();
      if (thread_name.equals("TerrainDimgThread") || thread_name.equals("MouseMoveThread") ||
        thread_name.equals("UpdateFogDisplayThread") || thread_name.equals("CopyTerrainDisplayThread") ||
        thread_name.equals("LoadChunkThread") || thread_name.equals("FogDImgThread") ||
        thread_name.equals("HangingFeaturesThread")) {
        gamemap_threads++;
      }
      else if (thread_name.equals("PathFindingThread")) {
        unit_threads++;
      }
    }
    p.text("GameMap Threads: " + gamemap_threads, this.xi + 1, y_stats);
    y_stats += line_height;
    p.text("Unit Threads: " + unit_threads, this.xi + 1, y_stats);
    y_stats += line_height;
    p.text("Current View: " + this.view_old.roundR(1).toString(), this.xi + 1, y_stats);
    y_stats += line_height;
    p.text("Mouse On: " + this.mc.roundR(1).toString(), this.xi + 1, y_stats);
    if (this.units.containsKey(0)) {
      y_stats += line_height;
      p.text("Location: (" + this.units.get(0).coordinate.toString() +
        ", " + this.units.get(0).curr_height + ")", this.xi + 1, y_stats);
      y_stats += line_height;
      p.text("Facing: (" + this.units.get(0).facing.toString() +
        ", " + this.units.get(0).facingA + ")", this.xi + 1, y_stats);
      y_stats += line_height;
      p.text("Height: (" + this.units.get(0).curr_height + ", " + this.units.get(0).floor_height +
        ", " + this.units.get(0).unit_height + ")", this.xi + 1, y_stats);
      try {
        GameMapSquare square = this.mapSquare(this.units.get(0).coordinate);
        y_stats += line_height;
        p.text("Terrain: (" + square.terrainName() + ", " + (int)(10.0 * square.light_level)/10.0 + ")", this.xi + 1, y_stats);
      } catch(NullPointerException e) {}
    }
  }


  void updateMap(int time_elapsed) {
    // Update terrain
    this.timer_update_squares -= time_elapsed;
    if (this.timer_update_squares < 0) {
      this.timer_update_squares = LNZ.map_updateSquaresTimer;
      for (int i = this.currMapXI(); i < this.currMapXF(); i++) {
        for (int j = this.currMapYI(); j < this.currMapYF(); j++) {
          GameMapSquare square = this.mapSquare(i, j);
          if (square == null) {
            continue;
          }
          square.update(this, i, j);
        }
      }
    }
    // Update features
    this.updateFeatures(time_elapsed);
    // Update units
    Iterator<Map.Entry<Integer, Unit>> unit_iterator = this.units.entrySet().iterator();
    while(unit_iterator.hasNext()) {
      Map.Entry<Integer, Unit> entry = unit_iterator.next();
      Unit u = entry.getValue();
      if (u.remove) {
        u.destroy(this);
        unit_iterator.remove();
        if (u.type.equals("Zombie")) {
          this.zombie_counter--;
        }
        continue;
      }
      u.update(time_elapsed, this);
      if (u.remove) {
        u.destroy(this);
        unit_iterator.remove();
        if (u.type.equals("Zombie")) {
          this.zombie_counter--;
        }
      }
    }
    this.updatePlayerUnit(time_elapsed);
    // Update items
    Iterator<Map.Entry<Integer, Item>> item_iterator = this.items.entrySet().iterator();
    while(item_iterator.hasNext()) {
      Map.Entry<Integer, Item> entry = item_iterator.next();
      Item i = entry.getValue();
      if (i.remove) {
        item_iterator.remove();
        continue;
      }
      i.update(time_elapsed);
      try {
        if (this.mapSquare(i.coordinate).terrain_id == 191) {
          i.remove = true;
        }
      } catch(NullPointerException e) {
      }
      if (i.remove) {
        item_iterator.remove();
      }
    }
    // Update projectiles
    for (int i = 0; i < this.projectiles.size(); i++) {
      if (this.projectiles.get(i).remove) {
        this.removeProjectile(i);
        i--;
        continue;
      }
      this.projectiles.get(i).update(time_elapsed, this);
      if (this.projectiles.get(i).remove) {
        this.removeProjectile(i);
        i--;
      }
    }
    // Update visual effects
    for (int i = 0; i < this.visualEffects.size(); i++) {
      if (this.visualEffects.get(i).remove) {
        this.removeVisualEffect(i);
        i--;
        continue;
      }
      this.visualEffects.get(i).update(time_elapsed);
      if (this.visualEffects.get(i).remove) {
        this.removeVisualEffect(i);
        i--;
      }
    }
  }

  abstract void updateFeatures(int time_elapsed);
  void updateFeature(Feature f, Iterator<Map.Entry<Integer, Feature>> it, int time_elapsed) {
    if (f.remove) {
      this.removeFeature(f.map_key);
      it.remove();
      return;
    }
    f.update(time_elapsed, this);
    if (f.refresh_map_image) {
      this.refreshFeature(f.map_key);
    }
    if (f.remove) {
      this.removeFeature(f.map_key);
      it.remove();
    }
  }
  abstract void updateFeaturesCheckRemovalOnly();

  void updatePlayerUnit(int timeElapsed) {
    if (!this.units.containsKey(0)) {
      return;
    }
    if (!Hero.class.isInstance(this.units.get(0))) {
      return;
    }
    Hero player = (Hero)this.units.get(0);
    if (p.global.holding_shift) {
      player.addStatusEffect(StatusEffectCode.SNEAKING);
    }
    else {
      player.removeStatusEffect(StatusEffectCode.SNEAKING);
    }
  }

  void updateMapCheckObjectRemovalOnly() {
    // Check features
    this.updateFeaturesCheckRemovalOnly();
    // Check units
    Iterator<Map.Entry<Integer, Unit>> unit_iterator = this.units.entrySet().iterator();
    while(unit_iterator.hasNext()) {
      Map.Entry<Integer, Unit> entry = unit_iterator.next();
      if (entry.getValue().remove) {
        entry.getValue().destroy(this);
        unit_iterator.remove();
        if (entry.getValue().type.equals("Zombie")) {
          this.zombie_counter--;
        }
      }
    }
    // Check items
    Iterator<Map.Entry<Integer, Item>> item_iterator = this.items.entrySet().iterator();
    while(item_iterator.hasNext()) {
      Map.Entry<Integer, Item> entry = item_iterator.next();
      if (entry.getValue().remove) {
        item_iterator.remove();
      }
    }
    // Check projectiles
    for (int i = 0; i < this.projectiles.size(); i++) {
      if (this.projectiles.get(i).remove) {
        this.removeProjectile(i);
        i--;
      }
    }
    // Check visual effects
    for (int i = 0; i < this.visualEffects.size(); i++) {
      if (this.visualEffects.get(i).remove) {
        this.removeVisualEffect(i);
        i--;
        continue;
      }
    }
  }

  void updateView(int time_elapsed) {
    boolean refreshView = false;
    // lockscreen
    if ((p.global.profile.options.lock_screen || p.global.holding_space) && this.in_control && this.units.containsKey(0)) {
      this.setViewLocation(this.units.get(0).coordinate.copy());
      refreshView = true;
    }
    else {
      // moving view
      double move_amount = time_elapsed * p.global.profile.options.map_viewMoveSpeedFactor;
      if (this.view_moving_left) {
        this.moveView(new Coordinate(-move_amount, move_amount), false);
        refreshView = true;
      }
      if (this.view_moving_right) {
        this.moveView(new Coordinate(move_amount, -move_amount), false);
        refreshView = true;
      }
      if (this.view_moving_up) {
        this.moveView(new Coordinate(-move_amount, -move_amount), false);
        refreshView = true;
      }
      if (this.view_moving_down) {
        this.moveView(new Coordinate(move_amount, move_amount), false);
        refreshView = true;
      }
    }
    this.timer_refresh_fog -= time_elapsed;
    if (this.timer_refresh_fog < 0) {
      this.timer_refresh_fog += p.global.profile.options.fog_update_time;
      this.refreshFog();
    }
    if (refreshView) {
      this.refreshDisplayMapParameters();
      this.updateCursorPosition();
    }
    // header messages
    int centered = 0;
    for (int i = 0; i < this.headerMessages.size(); i++) {
      int index = i - centered;
      if (this.headerMessages.get(i).centered) {
        index = centered;
        centered++;
      }
      this.headerMessages.get(i).updateView(time_elapsed, index);
      if (this.headerMessages.get(i).remove) {
        this.headerMessages.remove(i);
      }
    }
  }

  void refreshFog() {
    for (int k = 0; k < LNZ.map_lightUpdateIterations; k++) {
      for (int i = this.mapXI(); i < this.mapXF(); i++) {
        for (int j = this.mapYI(); j < this.mapYF(); j++) {
          GameMapSquare square = this.mapSquare(i, j);
          if (square == null) {
            continue;
          }
          if (k == 0) {
            square.original_light = square.light_level;
          }
          square.updateLightLevel(this, i, j);
          if (k == LNZ.map_lightUpdateIterations - 1) {
            square.light_source = false;
            if (Math.abs(square.light_level - square.original_light) < LNZ.small_number) {
              continue;
            }
            if (square.mapEdge()) {
              this.colorFogGrid(LNZ.color_transparent, i, j);
            }
            else if (!square.explored) {
              this.colorFogGrid(square.getColor(LNZ.color_black), i, j);
            }
            else if (!square.visible) {
              this.colorFogGrid(square.getColor(this.fog_color), i, j);
            }
            else {
              this.colorFogGrid(square.getColor(LNZ.color_transparent), i, j);
            }
          }
        }
      }
    }
    if (this.units.containsKey(0)) {
      this.units.get(0).refreshPlayerSight(this);
    }
  }

  void drawMap(boolean editor) {
    // background
    p.rectMode(PConstants.CORNERS);
    p.noStroke();
    p.fill(this.color_border);
    p.rect(this.xi, this.yi, this.xf, this.yf);
    p.fill(this.color_background);
    p.rect(this.xi + LNZ.map_borderSize, this.yi + LNZ.map_borderSize,
      this.xf - LNZ.map_borderSize, this.yf - LNZ.map_borderSize);
    // hovered info
    if (this.hovered_object != null && this.hovered_object.remove) {
      this.hovered_object = null;
    }
    String nameDisplayed = null;
    int ellipseColor = DImg.ccolor(255);
    double ellipseWeight = 0.8;
    // display terrain
    p.imageMode(PConstants.CORNERS);
    PImage img = this.map_display.img;
    if (img == null) {
      p.global.log("WARNING: Map display is null.");
    }
    else {
      p.image(this.map_display.img, this.xi_map_old, this.yi_map_old, this.xf_map_old, this.yf_map_old);
    }
    // display unit peripherals TODO: put unit peripherals display in thread
    Iterator<Map.Entry<Integer, Unit>> unit_iterator = this.units.entrySet().iterator();
    while(unit_iterator.hasNext()) {
      Map.Entry<Integer, Unit> entry = (Map.Entry<Integer, Unit>)unit_iterator.next();
      Unit u = entry.getValue();
      if (u == null || u.remove || !u.in_view) {
        continue;
      }
      this.drawUnitPeripherals(u);
    }
    // display projectiles TODO: put projectile display in thread
    p.imageMode(PConstants.CENTER);
    for (Projectile proj : this.projectiles) {
      Coordinate screen = this.mapToScreenCoordinate(proj.coordinate);
      if (!this.screenCoordinateInView(screen)) {
        proj.in_view = false;
        continue;
      }
      proj.in_view = true;
      GameMapSquare square = this.mapSquare(proj.coordinate);
      if (square == null) {
        continue;
      }
      if (this.draw_fog && !square.visible) {
        continue;
      }
      p.translate(screen.x, screen.y);
      p.rotate(proj.facingA);
      p.image(proj.getImage(), 0, 0, proj.width() * this.zoom_old, proj.height() * this.zoom_old);
      p.rotate(-proj.facingA);
      p.translate(-screen.x, -screen.y);
    }
    // display visual effects TODO: put visual effects display in thread
    p.imageMode(PConstants.CENTER);
    for (VisualEffect v : this.visualEffects) {
      Coordinate screen1 = this.mapToScreenCoordinate(new Coordinate(v.xi(), v.yi()));
      Coordinate screen2 = this.mapToScreenCoordinate(new Coordinate(v.xf(), v.yf()));
      if (!this.screenCoordinateInView(screen1) && !this.screenCoordinateInView(screen2)) {
        v.in_view = false;
        continue;
      }
      v.in_view = true;
      Coordinate translate = this.mapToScreenCoordinate(v.coordinate);
      p.translate(translate.x, translate.y);
      v.display(this.zoom_old);
      p.translate(-translate.x, -translate.y);
    }
    // name displayed
    MapObject this_hovered_object = this.hovered_object;
    GameMapSquare this_hovered_terrain = this.hovered_terrain;
    if (this_hovered_object == null) {
      if (this_hovered_terrain != null && p.global.holding_alt) {
        nameDisplayed = editor ? this_hovered_terrain.terrainName() + "(" +
          this_hovered_terrain.coordinate.toString() + ")" :
          this_hovered_terrain.terrainName();
        if (p.global.profile.options.show_feature_interaction_tooltip) {
          String interaction_tooltip = this_hovered_terrain.interactionTooltip(this.units.get(0));
          if (!interaction_tooltip.equals("")) {
            nameDisplayed += "\n - " + interaction_tooltip;
          }
        }
        Coordinate center = this.mapToScreenCoordinate(this.mc.floorR());
        center.y += (1 - 0.25 * this_hovered_terrain.terrainImageHeightOverflow()) * 0.5 * this.zoom_old;
        p.ellipseMode(PConstants.CENTER);
        p.noFill();
        p.stroke(ellipseColor);
        p.strokeWeight(ellipseWeight);
        p.ellipse(center.x, center.y, 2 * this.zoom_old, (2 + 0.5 * this_hovered_terrain.terrainImageHeightOverflow()) * 0.5 * this.zoom_old);
      }
    }
    else {
      nameDisplayed = this_hovered_object.displayName();
      if (p.global.profile.options.show_feature_interaction_tooltip && Feature.class.isInstance(this_hovered_object)) {
        String interaction_tooltip = ((Feature)this_hovered_object).interactionTooltip(this.units.get(0), p.global.holding_ctrl);
        if (!interaction_tooltip.equals("")) {
          nameDisplayed += "\n - " + interaction_tooltip;
        }
      }
      Coordinate center = this.mapToScreenCoordinate(this_hovered_object.center());
      center.y -= 0.25 * this_hovered_object.curr_height * this.zoom_old;
      double ellipseDiameterX = LNZ.root_two * this.zoom_old * this_hovered_object.width();
      double ellipseDiameterY = this.zoom_old * this_hovered_object.height();
      p.ellipseMode(PConstants.CENTER);
      p.noFill();
      p.stroke(ellipseColor);
      p.strokeWeight(ellipseWeight);
      p.ellipse(center.x, center.y, ellipseDiameterX, ellipseDiameterY);
    }
    if (nameDisplayed != null) {
      p.textSize(18);
      p.textLeading(30);
      double name_width = p.textWidth(nameDisplayed) + 2;
      double name_height = p.textAscent() + p.textDescent() + 2;
      int name_height_multiplier = 1;
      for (char c : nameDisplayed.toCharArray()) {
        if (c == '\n') {
          name_height_multiplier++;
        }
      }
      name_height = name_height_multiplier * name_height + (name_height_multiplier - 1) * (30 - name_height);
      double name_xi = this.last_mc.x + 2;
      double name_yi = this.last_mc.y - name_height - p.global.configuration.cursor_size * 0.3;
      if (this.last_mc.x > 0.5 * p.width) {
        name_xi -= name_width + 4;
      }
      p.fill(p.global.color_nameDisplayed_background);
      p.rectMode(PConstants.CORNER);
      p.noStroke();
      p.rect(name_xi, name_yi, name_width, name_height);
      p.fill(p.global.color_nameDisplayed_text);
      p.textAlign(PConstants.LEFT, PConstants.TOP);
      p.text(nameDisplayed, name_xi + 1, name_yi + 1);
    }
    // display fog
    if (this.draw_fog && this.fog_display != null) {
      p.imageMode(PConstants.CORNERS);
      p.image(this.fog_display,
        this.xi_map_old,
        this.yi_map_old,
        this.xf_map_old,
        this.yf_map_old
      );
    }
    // map tint TODO: put map tint in thread
    if (this.show_tint) {
      p.rectMode(PConstants.CORNERS);
      p.fill(this.color_tint);
      p.noStroke();
      p.rect(this.xi, this.yi, this.xf, this.yf);
    }
    // header messages
    for (HeaderMessage message : this.headerMessages) {
      message.drawMessage();
    }
  }

  // Draws semi-transparent gridlines
  // TODO: Fix drawGrid() since it doesn't work
  void drawGrid() {
    /*p.stroke(DImg.ccolor(255, 200));
    p.strokeWeight(0.5f);
    // loop from top left to top right drawing both directions
    for (int i = (int)Math.ceil(this.image_grid_start_old.x); i <= (int)Math.floor(this.image_grid_end_old.x); i++) {
      if (i % 2 == 1) {
        continue;
      }
      Coordinate grid_image = new Coordinate(i + 1, this.image_grid_start.y);
      Coordinate start_coordinate = this.gridImagetoScreenCoordinate(grid_image);
      double end_y = this.yi_map_old + 0.5 * (start_coordinate.x - this.xi_map_old);
      if (end_y > this.yf_map_old) {
        double end_x = this.xi_map_old + 2 * (end_y - this.yf_map_old);
        p.line(start_coordinate.x, start_coordinate.y, end_x, this.yf_map_old);
      }
      else {
        p.line(start_coordinate.x, start_coordinate.y, this.xi_map_old, end_y);
      }
      end_y = this.yi_map_old + 0.5 * (this.xf_map_old - start_coordinate.x);
      if (end_y > this.yf_map_old) {
        double end_x = this.xf_map_old - 2 * (end_y - this.yf_map_old);
        p.line(start_coordinate.x, start_coordinate.y, end_x, this.yf_map_old);
      }
      else {
        p.line(start_coordinate.x, start_coordinate.y, this.xf_map_old, end_y);
      }
    }
    // loop down from top left
    for (int i = (int)Math.ceil(this.image_grid_start_old.y); i < (int)Math.floor(this.image_grid_end_old.y); i++) {
      if (i % 2 == 1) {
        continue;
      }
      Coordinate start_coordinate = this.gridImagetoScreenCoordinate(
        new Coordinate(this.image_grid_start.x + 1, i));
      double end_x = this.xi_map_old + 2 * (this.yf_map_old - start_coordinate.y);
      if (end_x > this.xf_map_old) {
        double end_y = this.yf_map_old - 0.5 * (end_x - this.xf_map_old);
        p.line(start_coordinate.x, start_coordinate.y, this.xf_map_old, end_y);
      }
      else {
        p.line(start_coordinate.x, start_coordinate.y, end_x, this.yf_map_old);
      }
    }
    // Loop down from top right
    for (int i = (int)Math.ceil(this.image_grid_start_old.y); i < (int)Math.floor(this.image_grid_end_old.y); i++) {
      if (i % 2 == 1) {
        continue;
      }
      Coordinate start_coordinate = this.gridImagetoScreenCoordinate(
        new Coordinate(this.image_grid_end.x + 1, i));
      double end_x = this.xf_map_old - 2 * (this.yf_map_old - start_coordinate.y);
      if (end_x < this.xi_map_old) {
        double end_y =this.yf_map_old - 0.5 * (this.xi_map_old - end_x);
        p.line(start_coordinate.x, start_coordinate.y, this.xf_map_old, end_y);
      }
      else {
        p.line(start_coordinate.x, start_coordinate.y, end_x, this.yf_map_old);
      }
    }*/
  }
  
  // TODO: Place in TerrainDImgThread
  void drawUnitPeripherals(Unit u) {
    // transform space
    Coordinate center = this.mapToScreenCoordinate(u.center());
    center.y -= 0.25 * u.curr_height * this.zoom_old;
    double net_rotation = 0;
    boolean flip = false;
    double effective_facing_a = u.facingA + 0.5 * PConstants.HALF_PI; // to account for isometric
    if (Math.abs(effective_facing_a) > PConstants.HALF_PI) {
      flip = true;
      net_rotation = (PConstants.PI - Math.abs(effective_facing_a)) *
        effective_facing_a / Math.abs(effective_facing_a) + u.facingAngleModifier();
    }
    else {
      net_rotation = effective_facing_a + u.facingAngleModifier();
    }
    p.translate(center.x, center.y);
    if (flip) {
      p.scale(-1, 1);
    }
    p.rotate(net_rotation);
    // draw peripherals
    // TODO: All these need to be adjusted how they're drawn to match what one sees
    if (u.alkaloidSecretion()) {
      p.ellipseMode(PConstants.CENTER);
      p.fill(DImg.ccolor(128, 82, 48, 100));
      p.noStroke();
      p.rotate(-net_rotation);
      if (flip) {
        p.scale(-1, 1);
      }
      p.ellipse(0, 0, 4 * LNZ.ability_114_range * this.zoom_old, 2 * LNZ.ability_114_range * this.zoom_old);
      if (flip) {
        p.scale(-1, 1);
      }
      p.rotate(net_rotation);
    }
    if (u.alkaloidSecretionII()) {
      p.ellipseMode(PConstants.CENTER);
      p.fill(DImg.ccolor(128, 82, 48, 100));
      p.noStroke();
      p.rotate(-net_rotation);
      if (flip) {
        p.scale(-1, 1);
      }
      p.ellipse(0, 0, 4 * LNZ.ability_119_range * this.zoom_old, 2 * LNZ.ability_119_range * this.zoom_old);
      if (flip) {
        p.scale(-1, 1);
      }
      p.rotate(net_rotation);
    }
    if (u.curr_action == UnitAction.CASTING) {
      Ability a = u.abilities.get(u.curr_action_id);
      float img_width = 0;
      float img_height = 0;
      if (a != null) {
        switch(a.ID) {
          case 103: // Nelson Glare
            p.ellipseMode(PConstants.RADIUS);
            p.fill(170, 200);
            p.noStroke();
            img_width = (float)(LNZ.ability_103_range * (1 - a.timer_other / LNZ.ability_103_castTime));
            p.arc(0, 0, img_width * this.zoom_old, img_width * this.zoom_old, - LNZ.
              ability_103_coneAngle, LNZ.ability_103_coneAngle, PConstants.PIE);
            p.fill(100, 200);
            p.arc(0, 0, img_width * this.zoom_old, img_width * this.zoom_old, -0.3 *
              LNZ.ability_103_coneAngle, 0.3 * LNZ.ability_103_coneAngle,PConstants.PIE);
            break;
          case 108: // Nelson Glare II
            p.ellipseMode(PConstants.RADIUS);
            p.fill(170, 200);
            p.noStroke();
            img_width = (float)(LNZ.ability_108_range * (1 - a.timer_other / LNZ.ability_108_castTime));
            p.arc(0, 0, img_width * this.zoom_old, img_width * this.zoom_old, - LNZ.
              ability_108_coneAngle, LNZ.ability_108_coneAngle, PConstants.PIE);
            p.fill(100, 200);
            p.arc(0, 0, img_width * this.zoom_old, img_width * this.zoom_old, -0.3 *
              LNZ.ability_108_coneAngle, 0.3 * LNZ.ability_108_coneAngle, PConstants.PIE);
            break;
          case 112: // Tongue Lash
            img_width = (float)(LNZ.ability_112_distance * (1 - a.timer_other / LNZ.ability_112_castTime));
            img_height = (float)u.size;
            p.image(p.global.images.getImage("abilities/tongue.png"),
              0, 0,
              img_width * this.zoom_old, img_height * this.zoom_old);
            break;
          case 117: // Tongue Lash II
            img_width = (float)(LNZ.ability_117_distance * (1 - a.timer_other / LNZ.ability_112_castTime));
            img_height = (float)u.size;
            p.image(p.global.images.getImage("abilities/tongue.png"),
              0, 0,
              img_width * this.zoom_old, img_height * this.zoom_old);
            break;
          case 1001: // Blow Smoke
            img_width = (float)(LNZ.ability_1001_range * (1 - a.timer_other / LNZ.ability_1001_castTime));
            img_height = (float)(img_width * LNZ.ability_1001_tanConeAngle);
            p.image(p.global.images.getImage("abilities/smoke.png"),
              0, 0,
              img_width * this.zoom_old, img_height * this.zoom_old);
            break;
          case 1003: // Title IX Charge
            img_width = (float)LNZ.ability_1003_size_w;
            img_height = (float)LNZ.ability_1003_size_h;
            p.strokeWeight(2);
            p.stroke(0);
            p.noFill();
            p.tint(255, LNZApplet.round(255 * (1 - a.timer_other / LNZ.ability_1003_castTime)));
            p.rectMode(PConstants.CENTER);
            p.rect(0.5 * img_width * this.zoom_old, 0, img_width * this.zoom_old, img_height * this.zoom_old);
            p.image(p.global.images.getImage("abilities/title_ix.png"), 0.5 * img_width * this.zoom_old,
              0, img_width * this.zoom_old, img_height * this.zoom_old);
            p.noTint();
            break;
          default:
            break;
        }
      }
    }
    // undo transformation
    p.rotate(-net_rotation);
    if (flip) {
      p.scale(-1, 1);
    }
    p.translate(-center.x, -center.y);
  }


  void drawLeftPanel(int millis) {
    double currY = LNZ.map_selectedObjectPanelGap;
    if (this.selected_object != null) {
      if (this.selected_object_textbox == null) {
        this.selected_object_textbox = new SelectedObjectTextbox(p);
        this.selected_object_textbox.setXLocation(LNZ.map_selectedObjectPanelGap,
            xi - LNZ.map_selectedObjectPanelGap);
      }
      // title
      p.fill(255);
      p.textSize(LNZ.map_selectedObjectTitleTextSize);
      p.textAlign(PConstants.CENTER, PConstants.TOP);
      p.text(this.selected_object.displayName(), 0.5 * this.xi, currY);
      // image
      currY += p.textAscent() + p.textDescent() + LNZ.map_selectedObjectPanelGap;
      double image_height = Math.min(this.selected_object_textbox.yi - 2 * LNZ.
        map_selectedObjectImageGap - currY, this.xi - 2 * LNZ.map_selectedObjectPanelGap);
      PImage img = this.selected_object.getImage();
      double image_width = image_height * img.width / img.height;
      if (image_width > this.xi - 2 * LNZ.map_selectedObjectPanelGap) {
        image_width = this.xi - 2 * LNZ.map_selectedObjectPanelGap;
        image_height = image_width * img.height / img.width;
      }
      p.imageMode(PConstants.CENTER);
      currY += 0.5 * image_height + LNZ.map_selectedObjectImageGap;
      p.image(img, 0.5 * this.xi, currY, image_width, image_height);
      // textbox
      if (Item.class.isInstance(this.selected_object)) {
        Item i = (Item)this.selected_object;
        if (i.stack > 1) {
          p.fill(255);
          p.textAlign(PConstants.RIGHT, PConstants.BOTTOM);
          p.textSize(24);
          p.text(Integer.toString(i.stack), 0.5 * (this.xi + image_width) - 2, currY + 0.5 * image_height - 2);
        }
        this.selected_object_textbox.setText(this.selected_object.selectedObjectTextboxText());
        this.selected_object_textbox.update(millis);
        // item tier image
        PImage tier_image = p.global.images.getImage("icons/tier_" + i.tier + ".png");
        double tier_image_width = (LNZ.map_tierImageHeight * tier_image.width) / tier_image.height;
        p.imageMode(PConstants.CORNER);
        p.image(tier_image, this.selected_object_textbox.xf - tier_image_width - 4,
          this.selected_object_textbox.yi + 4, tier_image_width, LNZ.map_tierImageHeight);
      }
      else if (Unit.class.isInstance(this.selected_object) || Hero.class.isInstance(this.selected_object)) {
        Unit u = (Unit)this.selected_object;
        // weapon
        if (u.weapon() != null) {
          double weapon_image_width = image_width * LNZ.unit_weaponDisplayScaleFactor * u.weapon().width() / u.width();
          double weapon_image_height = image_height * LNZ.unit_weaponDisplayScaleFactor * u.weapon().height() / u.height();
          double weapon_image_x = 0.5 * this.xi + 0.45 * (image_width + weapon_image_width);
          double weapon_image_y = currY + 0.2 * (image_height + weapon_image_height);
          p.image(u.weapon().getImage(), weapon_image_x, weapon_image_y, weapon_image_width, weapon_image_height);
        }
        boolean lower_textbox = (u.statuses.size() > 0);
        if (lower_textbox) {
          this.selected_object_textbox.setYLocation(this.selected_object_textbox.yi +
            LNZ.map_statusImageHeight + 4, this.selected_object_textbox.yf);
        }
        this.selected_object_textbox.setText(this.selected_object.selectedObjectTextboxText());
        this.selected_object_textbox.update(millis);
        // status effects
        if (p.global.profile.upgraded(PlayerTreeCode.ENEMY_INSIGHTII)) {
          double x_status = 3;
          double y_status = this.selected_object_textbox.yi - LNZ.map_statusImageHeight - 2;
          StatusEffectCode status_effect_hovered = null;
          StatusEffect status_effect_object_hovered = null;
          for (Map.Entry<StatusEffectCode, StatusEffect> entry : u.statuses.entrySet()) {
            p.imageMode(PConstants.CORNER);
            p.rectMode(PConstants.CORNER);
            p.ellipseMode(PConstants.CENTER);
            p.fill(255, 150);
            p.stroke(0);
            p.strokeWeight(1);
            p.rect(x_status, y_status, LNZ.map_statusImageHeight, LNZ.map_statusImageHeight);
            p.image(p.global.images.getImage(entry.getKey().getImageString()), x_status,
              y_status, LNZ.map_statusImageHeight, LNZ.map_statusImageHeight);
            if (!entry.getValue().permanent) {
              p.fill(100, 100, 255, 140);
              p.noStroke();
              try {
                double angle = -PConstants.HALF_PI + 2 * PConstants.PI * entry.getValue().timer_gone / entry.getValue().timer_gone_start;
                p.arc(x_status + 0.5 * LNZ.map_statusImageHeight, y_status +
                  0.5 * LNZ.map_statusImageHeight, LNZ.map_statusImageHeight,
                  LNZ.map_statusImageHeight, -PConstants.HALF_PI, angle, PConstants.PIE);
              } catch(Exception e) {}
            }
            if (this.last_mc.x > x_status && this.last_mc.x < x_status + LNZ.map_statusImageHeight &&
              this.last_mc.y > y_status && this.last_mc.y < y_status + LNZ.map_statusImageHeight) {
              status_effect_hovered = entry.getKey();
              status_effect_object_hovered = entry.getValue();
            }
            x_status += LNZ.map_statusImageHeight + 2;
          }
          if (status_effect_hovered != null) {
            p.noStroke();
            p.fill(p.global.color_nameDisplayed_background);
            p.textSize(14);
            String status_description = status_effect_hovered.codeName() + "\nSource: " +
              status_effect_object_hovered.damage_source.toString();
            float line_height = p.textAscent() + p.textDescent() + 1;
            double rect_height = 2 * line_height + 2;
            double rect_width = p.textWidth(status_description) + 2;
            p.rect(this.last_mc.x + 1, this.last_mc.y - rect_height - 1, rect_width, rect_height);
            p.fill(255);
            p.textAlign(PConstants.LEFT, PConstants.TOP);
            p.textLeading(line_height);
            p.text(status_description, this.last_mc.x + 2, this.last_mc.y - rect_height - 1);
          }
        }
        // unit tier image
        PImage tier_image = p.global.images.getImage("icons/tier_" + u.tier() + ".png");
        double tier_image_width = (LNZ.map_tierImageHeight * tier_image.width) / tier_image.height;
        p.imageMode(PConstants.CORNER);
        p.image(tier_image, this.selected_object_textbox.xf - tier_image_width - 4,
          this.selected_object_textbox.yi + 4, tier_image_width, LNZ.map_tierImageHeight);
        // raise textbox
        if (lower_textbox) {
          this.selected_object_textbox.setYLocation(this.selected_object_textbox.yi -
            LNZ.map_statusImageHeight - 4, this.selected_object_textbox.yf);
        }
      }
      else {
        this.selected_object_textbox.setText(this.selected_object.selectedObjectTextboxText());
        this.selected_object_textbox.update(millis);
      }
    }
    else if (this.selected_terrain != null) { // terrain
      if (this.selected_object_textbox == null) {
        this.selected_object_textbox = new SelectedObjectTextbox(p);
        this.selected_object_textbox.setXLocation(LNZ.map_selectedObjectPanelGap,
            xi - LNZ.map_selectedObjectPanelGap);
      }
      // title
      p.fill(255);
      p.textSize(LNZ.map_selectedObjectTitleTextSize);
      p.textAlign(PConstants.CENTER, PConstants.TOP);
      p.text(this.selected_terrain.terrainName(), 0.5 * this.xi, currY);
      // image
      currY += p.textAscent() + p.textDescent() + LNZ.map_selectedObjectPanelGap;
      double image_height = Math.min(this.selected_object_textbox.yi - 2 * LNZ.
        map_selectedObjectImageGap - currY, this.xi - 2 * LNZ.map_selectedObjectPanelGap);
      PImage img = this.selected_terrain.terrainImage(false);
      double image_width = image_height * img.width / img.height;
      if (image_width > this.xi - 2 * LNZ.map_selectedObjectPanelGap) {
        image_width = this.xi - 2 * LNZ.map_selectedObjectPanelGap;
        image_height = image_width * img.height / img.width;
      }
      p.imageMode(PConstants.CENTER);
      currY += 0.5 * image_height + LNZ.map_selectedObjectImageGap;
      p.image(img, 0.5 * this.xi, currY, image_width, image_height);
      // textbox
      this.selected_object_textbox.setText(this.selected_terrain.selectedObjectTextboxText());
      this.selected_object_textbox.update(millis);
    }
    p.stroke(0);
    p.strokeWeight(1.5);
    p.line(0, 0.5 * p.height, this.xi, 0.5 * p.height);
  }

  boolean leftPanelElementsHovered() {
    if (this.selected_object != null && this.selected_object_textbox != null) {
      if (this.selected_object_textbox.hovered) {
        return true;
      }
    }
    return false;
  }


  Coordinate screenToMapCoordinate(Coordinate screen) {
    return this.screenToMapCoordinate(screen, this.zoom_old, this.view_old);
  }
  Coordinate screenToMapCoordinate(Coordinate screen, double zoom, Coordinate view) {
    double x = view.x + (screen.x + 2 * screen.y - 0.5 * p.width - p.height) * 0.5 / zoom;
    double y = view.y + (-screen.x + 2 * screen.y + 0.5 * p.width - p.height) * 0.5 / zoom;
    return new Coordinate(x, y);
  }

  Coordinate mapToScreenCoordinate(Coordinate map) {
    return this.mapToScreenCoordinate(map, this.zoom_old, this.view_old);
  }
  Coordinate mapToScreenCoordinate(Coordinate map, double zoom, Coordinate view) {
    Coordinate copy = map.subtractR(view);
    double x = 0.5 * p.width + zoom * (copy.x - copy.y);
    double y = 0.5 * p.height + 0.5 * zoom * (copy.x + copy.y);
    return new Coordinate(x, y);
  }

  boolean mapCoordinateInView(Coordinate map) {
    return this.screenCoordinateInView(this.mapToScreenCoordinate(map));
  }

  boolean screenCoordinateInView(Coordinate screen) {
    return (screen.x + LNZ.small_number > this.xi &&
            screen.y + LNZ.small_number > this.yi &&
            screen.x - LNZ.small_number < this.xf &&
            screen.y - LNZ.small_number < this.yf);
  }

  Coordinate mapToScreenImageCoordinate(Coordinate map) {
    return this.mapToScreenImageCoordinate(map, this.zoom_old, this.view_old);
  }
  Coordinate mapToScreenImageCoordinate(Coordinate map, double zoom, Coordinate view) {
    Coordinate copy = map.subtractR(view);
    double x = 0.5 * p.width + zoom * (copy.x - copy.y - 1);
    double y = 0.5 * p.height + 0.5 * zoom * (copy.x + copy.y);
    return new Coordinate(x, y);
  }

  Coordinate screenImageToDisplayImageCoordinate(Coordinate screen, double xi_map, double yi_map) {
    // always uses current
    return new Coordinate(screen.x - xi_map, screen.y - yi_map);
  }
  
  Coordinate mapToDisplayImageCoordinate(Coordinate map) {
    return this.mapToDisplayImageCoordinate(map, this.xi_map_old, this.yi_map_old,
      this.zoom_old, this.view_old);
  }
  Coordinate mapToDisplayImageCoordinate(Coordinate map, double xi_map,
    double yi_map, double zoom, Coordinate view) {
    Coordinate copy = map.subtractR(view);
    double x = 0.5 * p.width - xi_map + zoom * (copy.x - copy.y - 1);
    double y = 0.5 * p.height - yi_map + 0.5 * zoom * (copy.x + copy.y);
    return new Coordinate(x, y);
  }

  abstract Coordinate mapToGridImageCoordinate(Coordinate map);
  abstract IntegerCoordinate mapToGridImageCoordinate(IntegerCoordinate map);

  abstract Coordinate gridImageToMapCoordinate(Coordinate image);
  Coordinate gridImagetoScreenCoordinate(Coordinate image) {
    return this.mapToScreenImageCoordinate(this.gridImageToMapCoordinate(image));
  }

  Coordinate screenToGridImageCoordinate(Coordinate screen) {
    return this.screenToGridImageCoordinate(screen, this.zoom_old, this.view_old);
  }
  Coordinate screenToGridImageCoordinate(Coordinate screen, double zoom, Coordinate view) {
    return this.mapToGridImageCoordinate(this.screenToMapCoordinate(screen, zoom, view));
  }


  void waterGround(int x, int y) {
    this.waterGround(new IntegerCoordinate(x, y));
  }
  void waterGround(IntegerCoordinate coordinate) {
    GameMapSquare square = this.mapSquare(coordinate);
    if (square == null) {
      return;
    }
    switch(square.terrain_id) {
      case 164: // Tilled Dirt
        this.setTerrain(165, coordinate);
        square.timer_square = 150 + Misc.randomInt(50);
        break;
      case 165: // Tilled Dirt, watered
        square.timer_square = Math.min(square.timer_square + 150 + Misc.randomInt(50), 240);
        break;
      default:
        break;
    }
  }

  void splashDamage(Coordinate coordinate, double explode_range,
    double explode_maxPower, double explode_minPower, int source_key,
    DamageType damageType, Element element, double piercing, double penetration,
    boolean friendly_fire, DamageSource damage_source) {
    if (explode_range <= 0) {
      return;
    }
    Unit source = this.units.get(source_key);
    for (Map.Entry<Integer, Unit> entry : this.units.entrySet()) {
      Unit u = entry.getValue();
      if (source != null && !friendly_fire && source.alliance == u.alliance) {
        continue;
      }
      double distance = u.distance(coordinate);
      double distance_ratio = 1 - distance / explode_range;
      if (distance_ratio <= 0) {
        continue;
      }
      double net_power = explode_minPower + distance_ratio * (explode_maxPower - explode_minPower);
      u.damage(source, u.calculateDamageFrom(net_power, damageType, element, piercing, penetration), damage_source);
    }
  }
  
  // return max height from list of map squares
  int maxHeightOfSquares(ArrayList<IntegerCoordinate> coordinates, Coordinate exact_coordinate) {
    int max_height = -100;
    for (IntegerCoordinate coordinate : coordinates) {
      Coordinate relative_coordinate = exact_coordinate.subtractR(coordinate);
      try {
        int square_elevation = this.mapSquare(coordinate).elevation(relative_coordinate);
        if (square_elevation > max_height) {
          max_height = square_elevation;
        }
      } catch(NullPointerException e) {}
    }
    return max_height;
  }

  int heightOfSquare(Coordinate exact_coordinate) {
    return this.heightOfSquare(new IntegerCoordinate(exact_coordinate), exact_coordinate);
  }
  int heightOfSquare(int x, int y, Coordinate exact_coordinate) {
    return this.heightOfSquare(new IntegerCoordinate(x, y), exact_coordinate);
  }
  int heightOfSquare(Coordinate coordinate, Coordinate exact_coordinate) {
    return this.heightOfSquare(new IntegerCoordinate(coordinate), exact_coordinate);
  }
  int heightOfSquare(IntegerCoordinate coordinate, Coordinate exact_coordinate) {
    GameMapSquare square = this.mapSquare(coordinate);
    if (square == null) {
      return -100;
    }
    Coordinate relative_coordinate = exact_coordinate.subtractR(coordinate);
    return square.elevation(relative_coordinate);
  }

  boolean containsMapSquare(IntegerCoordinate coordinate) {
    if (coordinate.x >= this.mapXI() && coordinate.x < this.mapXF() &&
      coordinate.y >= this.mapYI() && coordinate.y < this.mapYF()) {
      return true;
    }
    return false;
  }


  void update(int millis) {
    int time_elapsed = millis - this.last_update_time;
    this.updateMap(time_elapsed); // map and mapObject logic
    this.updateView(time_elapsed); // if moving or zooming, check refresh_fog
    this.drawMap(false); // everything visual
    this.last_update_time = millis;
  }

  void mouseMove(float mX, float mY) {
    this.last_mc = new Coordinate(mX, mY);
    this.startMouseMoveThread();
  }

  void startMouseMoveThread() {
    if (this.mouse_move_thread != null && this.mouse_move_thread.isAlive()) {
      this.restart_mouseMoveThread = true;
      return;
    }
    this.mouse_move_thread = new MouseMoveThread(this.last_mc);
    this.mouse_move_thread.start();
  }

  void updateCursorPosition() {
    this.updateCursorPosition(this.last_mc);
  }
  synchronized void updateCursorPosition(Coordinate mouse_coordinate) {
    this.mc = this.screenToMapCoordinate(mouse_coordinate);
    this.mc_object = this.mc.copy();
    boolean found_mc_object = false;
    this.base_mc = this.mc.copy();
    IntegerCoordinate base_coordinate = new IntegerCoordinate(this.mc);
    for (IntegerCoordinate possible_tile_overlap : base_coordinate.possibleTileOverlaps()) {
      GameMapSquare square = this.mapSquare(possible_tile_overlap);
      if (square == null || !square.in_view) {
        continue;
      }
      if (found_mc_object && square.blocking_view) {
        continue;
      }
      int square_overflow = square.terrainImageHeightOverflow();
      if (square.features.size() > 0 && !p.global.holding_alt) {
        Feature f = square.features.get(square.features.size() - 1);
        if (f != null && !f.remove && !(found_mc_object && f.blocking_view)) {
          square_overflow += Math.max(0, square.feature_elevation - f.sizeZ +
            f.terrainImageHeightOverflow());
        }
      }
      Coordinate tile_hover = this.checkTileHover(0.25 * square_overflow,
        possible_tile_overlap, mouse_coordinate);
      if (tile_hover == null) {
        continue;
      }
      if (!square.isStair()) {
        if (square.blocking_view) {
          if (!found_mc_object) {
            this.mc_object = tile_hover.copy();
            found_mc_object = true;
          }
          continue;
        }
        this.mc = tile_hover.copy();
        break;
      }
      Coordinate relative_coordinate = tile_hover.subtractR(new IntegerCoordinate(tile_hover));
      if (square.adjustElevation(relative_coordinate) == 0) {
        if (square.blocking_view) {
          if (!found_mc_object) {
            this.mc_object = tile_hover.copy();
            found_mc_object = true;
          }
          continue;
        }
        this.mc = tile_hover.copy();
        break;
      }
      tile_hover = this.checkTileHover(0.25 * (square_overflow - 1),
        possible_tile_overlap, mouse_coordinate);
      if (tile_hover == null) {
        continue;
      }
      relative_coordinate = tile_hover.subtractR(new IntegerCoordinate(tile_hover));
      if (square.adjustElevation(relative_coordinate) == -1) {
        if (square.blocking_view) {
          if (!found_mc_object) {
            this.mc_object = tile_hover.copy();
            found_mc_object = true;
          }
          continue;
        }
        this.mc = tile_hover.copy();
        break;
      }
      tile_hover = this.checkTileHover(0.25 * (square_overflow - 2),
        possible_tile_overlap, mouse_coordinate);
      if (tile_hover == null) {
        continue;
      }
      if (square.blocking_view) {
        if (!found_mc_object) {
          this.mc_object = tile_hover.copy();
          found_mc_object = true;
        }
        continue;
      }
      this.mc = tile_hover.copy();
      break;
    }
  }

  private Coordinate checkTileHover(double adjust_amount, IntegerCoordinate tile, Coordinate mouse_coordinate) {
    Coordinate adjusted_initial = new Coordinate(
      tile.x - adjust_amount,
      tile.y - adjust_amount);
    Coordinate adjusted_final = adjusted_initial.addR(1);
    if (this.mc.x > adjusted_initial.x && this.mc.y > adjusted_initial.y &&
      this.mc.x <= adjusted_final.x && this.mc.y <= adjusted_final.y) {
      return this.mc.addR(adjust_amount);
    }
    else if ((this.mc.x > adjusted_final.x || this.mc.y > adjusted_final.y) &&
      this.mc.x <= tile.x + 1 && this.mc.y <= tile.y + 1) {
      // check if on edges
      Coordinate left_edge = this.mapToScreenCoordinate(new Coordinate(
        tile.x, tile.y + 1));
      double x_coordinate = mouse_coordinate.x - left_edge.x;
      if (x_coordinate > 0 && x_coordinate <= 2 * this.zoom_old) {
        Coordinate return_coordinate = new Coordinate(0, 0);
        if (x_coordinate > this.zoom_old) {
          return_coordinate.x = tile.x + 1 - LNZ.small_number;
          double calculated_y = (this.zoom_old - (x_coordinate - this.zoom_old)) / this.zoom_old;
          return_coordinate.y = tile.y + Math.min(1 - LNZ.small_number, calculated_y);
        }
        else {
          return_coordinate.y = tile.y + 1 - LNZ.small_number;
          double calculated_x = x_coordinate / this.zoom_old;
          return_coordinate.x = tile.x + Math.min(1 - LNZ.small_number, calculated_x);
        }
        return return_coordinate;
      }
    }
    return null;
  }

  void selectHoveredObject() {
    if (this.hovered_area && !this.hovered_border) {
      this.selected_object = this.hovered_object;
      if (this.hovered_object == null) {
        this.selected_terrain = this.hovered_terrain;
      }
    }
  }

  void mousePress() {
    for (HeaderMessage message : this.headerMessages) {
      message.mousePress();
    }
    if (this.selected_object != null && this.selected_object_textbox != null) {
      this.selected_object_textbox.mousePress();
    }
    switch(p.mouseButton) {
      case PConstants.LEFT:
        this.selectHoveredObject();
        break;
      case PConstants.RIGHT:
        if (!this.hovered_area) {
          break;
        }
        boolean viewing_inventory = false;
        if (this.units.containsKey(0) && Hero.class.isInstance(this.units.get(0))) {
          viewing_inventory = ((Hero)this.units.get(0)).inventory.viewing;
        }
        if (this.units.containsKey(0) && this.in_control && !viewing_inventory) {
          Unit player = this.units.get(0);
          if (player.curr_action_unhaltable) {
            break;
          }
          if (player.weapon() != null && player.weapon().shootable() && p.global.holding_ctrl) {
            player.aim(this.mc);
          }
          else if (this.hovered_object == null || !this.hovered_object.targetable(player)) {
            if (this.hovered_terrain != null && p.global.holding_alt) {
              player.target(this);
            }
            else {
              player.moveTo(this.mc, this);
              this.addVisualEffect(4001, this.base_mc);
            }
          }
          else {
            player.target(this.hovered_object, this, p.global.holding_ctrl);
          }
        }
        break;
      case PConstants.CENTER:
        break;
    }
  }

  void mouseRelease(float mX, float mY) {
    if (this.selected_object != null && this.selected_object_textbox != null) {
      this.selected_object_textbox.mouseRelease(mX, mY);
    }
  }

  void scroll(int amount) {
    if (this.selected_object != null && this.selected_object_textbox != null) {
      this.selected_object_textbox.scroll(amount);
    }
    if (this.hovered_area && p.global.holding_ctrl) {
      this.changeZoom(LNZ.map_scrollZoomFactor * amount);
    }
  }

  void keyPress(int key, int keyCode) {
    if (key == PConstants.CODED) {
      switch(keyCode) {
        case PConstants.ALT:
          if (!p.global.holding_alt) {
            break;
          }
          this.startMouseMoveThread();
          break;
        case PConstants.LEFT:
          this.view_moving_left = true;
          break;
        case PConstants.RIGHT:
          this.view_moving_right = true;
          break;
        case PConstants.UP:
          this.view_moving_up = true;
          break;
        case PConstants.DOWN:
          this.view_moving_down = true;
          break;
      }
    }
    else {
      switch(key) {
        case ' ':
          if (this.units.containsKey(0)) {
            this.selected_object = this.units.get(0);
          }
          break;
        case 'q':
        case 'Q':
          if (this.units.containsKey(0) && this.in_control) {
            this.units.get(0).dropWeapon(this);
          }
          break;
        case 't':
        case 'T':
          if (this.units.containsKey(0) && this.in_control) {
            this.selected_object = this.units.get(0).weapon();
          }
          break;
        case 'a':
        case 'A':
          if (this.units.containsKey(0) && this.in_control) {
            if (this.units.get(0).abilities.size() < 2 || this.units.get(0).abilities.get(1) == null) {
              break;
            }
            else if (this.units.get(0).silenced()) {
              this.addHeaderMessage("You are silenced");
            }
            else {
              this.units.get(0).cast(1, this, this.hovered_object, true);
            }
          }
          break;
        case 's':
        case 'S':
          if (this.units.containsKey(0) && this.in_control) {
            if (this.units.get(0).abilities.size() < 3 || this.units.get(0).abilities.get(2) == null) {
              break;
            }
            else if (this.units.get(0).silenced()) {
              this.addHeaderMessage("You are silenced");
            }
            else {
              this.units.get(0).cast(2, this, this.hovered_object, true);
            }
          }
          break;
        case 'd':
        case 'D':
          if (this.units.containsKey(0) && this.in_control) {
            if (this.units.get(0).abilities.size() < 4 || this.units.get(0).abilities.get(3) == null) {
              break;
            }
            else if (this.units.get(0).silenced()) {
              this.addHeaderMessage("You are silenced");
            }
            else {
              this.units.get(0).cast(3, this, this.hovered_object, true);
            }
          }
          break;
        case 'f':
        case 'F':
          if (this.units.containsKey(0) && this.in_control) {
            if (this.units.get(0).abilities.size() < 5 || this.units.get(0).abilities.get(4) == null) {
              break;
            }
            else if (this.units.get(0).silenced()) {
              this.addHeaderMessage("You are silenced");
            }
            else {
              this.units.get(0).cast(4, this, this.hovered_object, true);
            }
          }
          break;
        case 'v':
        case 'V':
          if (this.units.containsKey(0) && this.in_control) {
            this.units.get(0).jump(this);
          }
          break;
        case 'y':
        case 'Y':
          if (this.units.containsKey(0) && this.in_control) {
            this.units.get(0).stopAction();
          }
          break;
      }
    }
  }

  void keyRelease(int key, int keyCode) {
    if (key == PConstants.CODED) {
      switch(keyCode) {
        case PConstants.ALT:
          if (!p.global.holding_alt) {
            break;
          }
          this.startMouseMoveThread();
          break;
        case PConstants.LEFT:
          this.view_moving_left = false;
          break;
        case PConstants.RIGHT:
          this.view_moving_right = false;
          break;
        case PConstants.UP:
          this.view_moving_up = false;
          break;
        case PConstants.DOWN:
          this.view_moving_down = false;
          break;
      }
    }
    else {
      switch(key) {
      }
    }
  }

  void loseFocus() {
  }

  void gainFocus() {
  }


  void save(String folder_path) {
    PrintWriter file;
    file = p.createWriter(folder_path + "/" + this.mapName + "." + this.fileType() + ".lnz");
    file.println("new: Map");
    file.println("code: " + this.code.fileName());
    file.println("mapName: " + this.mapName);
    file.println("maxHeight: " + this.max_height);
    file.println("outside_map: " + this.outside_map);
    file.println("color_tint: " + this.color_tint);
    file.println("show_tint: " + this.show_tint);
    this.saveTerrain(file);
    // add unit data
    for (Map.Entry<Integer, Unit> entry : this.units.entrySet()) {
      if (entry.getKey() == 0) {
        continue;
      }
      file.println("nextUnitKey: " + entry.getKey());
      file.println(entry.getValue().fileString());
    }
    // add item data
    for (Map.Entry<Integer, Item> entry : this.items.entrySet()) {
      file.println("nextItemKey: " + entry.getKey());
      file.println(entry.getValue().fileString());
    }
    file.println("end: Map");
    file.flush();
    file.close();
    p.global.profile.save(); // for ender chest
  }
  String fileType() {
    return "map";
  }
  abstract void saveTerrain(PrintWriter file);

  void open(String folderPath) {
    this.open2Data(this.open1File(folderPath));
    this.initializeTerrain();
  }

  String[] open1File(String folder_path) {
    String path = folder_path + "/" + this.mapName + "." + this.fileType() + ".lnz";
    String[] lines = p.loadStrings(path);
    if (lines == null) {
      p.global.errorMessage("ERROR: Reading map at path " + path + " but no file exists.");
      this.nullify = true;
    }
    return lines;
  }

  void open2Data(String[] lines) {
    if (lines == null) {
      p.global.errorMessage("ERROR: Trying to open map data with null data.");
      this.nullify = true;
      return;
    }

    Deque<ReadFileObject> object_queue = new ArrayDeque<ReadFileObject>();

    int max_feature_key = 0;
    Feature curr_feature = null;
    int max_unit_key = 0;
    Unit curr_unit = null;
    int max_item_key = 0;
    Item curr_item = null;
    Item curr_item_internal = null; // for item inventories
    Projectile curr_projectile = null;
    StatusEffectCode curr_status_code = StatusEffectCode.ERROR;
    StatusEffect curr_status = null;
    Ability curr_ability = null;

    for (String line : lines) {
      if (line == null) {
        p.global.errorMessage("ERROR: Trying to open map data with null line.");
        this.nullify = true;
        continue;
      }
      String[] parameters = PApplet.split(line, ':');
      if (parameters.length < 2) {
        continue;
      }

      String dataname = PApplet.trim(parameters[0]);
      String data = PApplet.trim(parameters[1]);
      for (int i = 2; i < parameters.length; i++) {
        data += ":" + parameters[i];
      }
      if (dataname.equals("new")) {
        ReadFileObject type = ReadFileObject.objectType(PApplet.trim(parameters[1]));
        switch(type) {
          case MAP:
            object_queue.push(type);
            break;
          case FEATURE:
            if (parameters.length < 3) {
              p.global.errorMessage("ERROR: Feature ID missing in Feature constructor.");
              break;
            }
            object_queue.push(type);
            curr_feature = new Feature(p, Misc.toInt(PApplet.trim(parameters[2])));
            break;
          case UNIT:
            if (parameters.length < 3) {
              p.global.errorMessage("ERROR: Unit ID missing in Unit constructor.");
              break;
            }
            object_queue.push(type);
            curr_unit = new Unit(p, Misc.toInt(PApplet.trim(parameters[2])));
            break;
          case ITEM:
            if (parameters.length < 3) {
              p.global.errorMessage("ERROR: Item ID missing in Item constructor.");
              break;
            }
            object_queue.push(type);
            if (curr_item == null) {
              curr_item = new Item(p, Misc.toInt(PApplet.trim(parameters[2])));
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
              curr_item_internal = new Item(p, Misc.toInt(PApplet.trim(parameters[2])));
            }
            break;
          case PROJECTILE:
            if (parameters.length < 3) {
              p.global.errorMessage("ERROR: Projectile ID missing in Projectile constructor.");
              break;
            }
            object_queue.push(type);
            curr_projectile = new Projectile(p, Misc.toInt(PApplet.trim(parameters[2])));
            break;
          case STATUS_EFFECT:
            object_queue.push(type);
            curr_status = new StatusEffect(p);
            break;
          case ABILITY:
            if (parameters.length < 3) {
              p.global.errorMessage("ERROR: Ability ID missing in Projectile constructor.");
              break;
            }
            object_queue.push(type);
            curr_ability = new Ability(p, Misc.toInt(PApplet.trim(parameters[2])));
            break;
          default:
            p.global.errorMessage("ERROR: Can't add a " + type + " type to GameMap data.");
            break;
        }
      }
      else if (dataname.equals("end")) {
        ReadFileObject type = ReadFileObject.objectType(PApplet.trim(parameters[1]));
        if (object_queue.isEmpty()) {
          p.global.errorMessage("ERROR: Tring to end a " + type.name + " object but not inside any object.");
        }
        else if (type.name.equals(object_queue.peek().name)) {
          switch(object_queue.pop()) {
            case MAP:
              break;
            case FEATURE:
              if (curr_feature == null) {
                p.global.errorMessage("ERROR: Trying to end a null feature.");
                break;
              }
              if (object_queue.isEmpty()) {
                p.global.errorMessage("ERROR: Trying to end a feature not inside any other object.");
                break;
              }
              if (this.next_feature_key > max_feature_key) {
                max_feature_key = this.next_feature_key;
              }
              this.addFeature(curr_feature, false);
              curr_feature = null;
              break;
            case UNIT:
              if (curr_unit == null) {
                p.global.errorMessage("ERROR: Trying to end a null unit.");
                break;
              }
              if (object_queue.isEmpty()) {
                p.global.errorMessage("ERROR: Trying to end a unit not inside any other object.");
                break;
              }
              if (this.next_unit_key > max_unit_key) {
                max_unit_key = this.next_unit_key;
              }
              this.addUnit(curr_unit);
              curr_unit = null;
              break;
            case ITEM:
              if (curr_item == null) {
                p.global.errorMessage("ERROR: Trying to end a null item.");
                break;
              }
              if (object_queue.isEmpty()) {
                p.global.errorMessage("ERROR: Trying to end an item not inside any other object.");
                break;
              }
              switch(object_queue.peek()) {
                case MAP:
                  if (this.next_item_key > max_item_key) {
                    max_item_key = this.next_item_key;
                  }
                  this.addItemAsIs(curr_item);
                  break;
                case FEATURE:
                  if (parameters.length < 3) {
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
                  if (PApplet.trim(parameters[2]).equals("item_array")) {
                    if (curr_feature.items == null) {
                      p.global.errorMessage("ERROR: Trying to add item to feature " +
                        "item array but curr_feature has no item array.");
                      break;
                    }
                    curr_feature.items.add(curr_item);
                    break;
                  }
                  if (!Misc.isInt(PApplet.trim(parameters[2]))) {
                    p.global.errorMessage("ERROR: Ending item in feature inventory " +
                      "but no slot information given.");
                    break;
                  }
                  int slot_number = Misc.toInt(PApplet.trim(parameters[2]));
                  if (slot_number < 0 || slot_number >= curr_feature.inventory.slots.size()) {
                    p.global.errorMessage("ERROR: Trying to add item to feature " +
                      "inventory but slot number " + slot_number + " out of range.");
                    break;
                  }
                  curr_feature.inventory.slots.get(slot_number).item = curr_item;
                  break;
                case UNIT:
                  if (parameters.length < 3) {
                    p.global.errorMessage("ERROR: GearSlot code missing in Item constructor.");
                    break;
                  }
                  GearSlot code = GearSlot.gearSlot(PApplet.trim(parameters[2]));
                  if (curr_unit == null) {
                    p.global.errorMessage("ERROR: Trying to add gear to null unit.");
                    break;
                  }
                  curr_unit.gear.put(code, curr_item);
                  break;
                case ITEM:
                  if (curr_item_internal == null) {
                    p.global.errorMessage("ERROR: Trying to end a null internal item.");
                    break;
                  }
                  if (parameters.length < 3 || !Misc.isInt(PApplet.trim(parameters[2]))) {
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
                  int item_slot_number = Misc.toInt(PApplet.trim(parameters[2]));
                  if (item_slot_number < 0 || item_slot_number >= curr_item.inventory.slots.size()) {
                    p.global.errorMessage("ERROR: Trying to add item to feature " +
                      "inventory but slot number " + item_slot_number + " out of range.");
                    break;
                  }
                  curr_item.inventory.slots.get(item_slot_number).item = curr_item_internal;
                  break;
                default:
                  p.global.errorMessage("ERROR: Trying to end an item inside a " + object_queue.peek().name + ".");
                  break;
              }
              if (curr_item_internal == null) {
                curr_item = null;
              }
              else {
                curr_item_internal = null;
              }
              break;
            case PROJECTILE:
              if (curr_projectile == null) {
                p.global.errorMessage("ERROR: Trying to end a null projectile.");
                break;
              }
              if (object_queue.isEmpty()) {
                p.global.errorMessage("ERROR: Trying to end a projectile not inside any other object.");
                break;
              }
              curr_projectile.refreshFacing();
              this.addProjectile(curr_projectile);
              curr_projectile = null;
              break;
            case STATUS_EFFECT:
              if (curr_status == null) {
                p.global.errorMessage("ERROR: Trying to end a null status effect.");
                break;
              }
              if (object_queue.isEmpty()) {
                p.global.errorMessage("ERROR: Trying to end a status effect not inside any other object.");
                break;
              }
              if (object_queue.peek() != ReadFileObject.UNIT && object_queue.peek() != ReadFileObject.HERO) {
                p.global.errorMessage("ERROR: Trying to end a status effect inside a " + object_queue.peek().name + ".");
                break;
              }
              if (curr_unit == null) {
                p.global.errorMessage("ERROR: Trying to end a status effect inside a null unit.");
                break;
              }
              curr_unit.statuses.put(curr_status_code, curr_status);
              curr_status = null;
              break;
            case ABILITY:
              if (curr_ability == null) {
                p.global.errorMessage("ERROR: Trying to end a null ability.");
                break;
              }
              if (object_queue.isEmpty()) {
                p.global.errorMessage("ERROR: Trying to end an ability not inside any other object.");
                break;
              }
              if (object_queue.peek() != ReadFileObject.UNIT && object_queue.peek() != ReadFileObject.HERO) {
                p.global.errorMessage("ERROR: Trying to end an ability inside a " + object_queue.peek().name + ".");
                break;
              }
              if (curr_unit == null) {
                p.global.errorMessage("ERROR: Trying to end an ability inside a null unit.");
                break;
              }
              curr_unit.abilities.add(curr_ability);
              curr_ability = null;
              break;
            default:
              p.global.errorMessage("ERROR: Trying to end a " + type.name + " which is not known.");
              break;
          }
        }
        else {
          p.global.errorMessage("ERROR: Tring to end a " + type.name + " object but current object is a " + object_queue.peek().name + ".");
        }
      }
      else {
        switch(object_queue.peek()) {
          case MAP:
            this.addData(dataname, data);
            break;
          case FEATURE:
            if (curr_feature == null) {
              p.global.errorMessage("ERROR: Trying to add feature data to a null feature.");
              break;
            }
            curr_feature.addData(dataname, data);
            break;
          case UNIT:
            if (curr_unit == null) {
              p.global.errorMessage("ERROR: Trying to add unit data to a null unit.");
              break;
            }
            if (dataname.equals("next_status_code")) {
              curr_status_code = StatusEffectCode.code(data);
            }
            else {
              curr_unit.addData(dataname, data);
            }
            break;
          case ITEM:
            if (curr_item == null) {
              p.global.errorMessage("ERROR: Trying to add item data to a null item.");
              break;
            }
            if (curr_item_internal != null) {
              curr_item_internal.addData(dataname, data);
            }
            else {
              curr_item.addData(dataname, data);
            }
            break;
          case PROJECTILE:
            if (curr_projectile == null) {
              p.global.errorMessage("ERROR: Trying to add projectile data to a null projectile.");
              break;
            }
            curr_projectile.addData(dataname, data);
            break;
          case STATUS_EFFECT:
            if (curr_status == null) {
              p.global.errorMessage("ERROR: Trying to add status effect data to a null status effect.");
              break;
            }
            curr_status.addData(dataname, data);
            break;
          case ABILITY:
            if (curr_ability == null) {
              p.global.errorMessage("ERROR: Trying to add ability data to a null ability.");
              break;
            }
            curr_status.addData(dataname, data);
            break;
          default:
            break;
        }
      }
    }

    // Refresh ability target units
    for (Map.Entry<Integer, Unit> entry : this.units.entrySet()) {
      for (Ability a : entry.getValue().abilities) {
        if (a == null) {
          continue;
        }
        if (this.units.containsKey(a.target_key)) {
          a.target_unit = this.units.get(a.target_key);
        }
      }
    }

    // Refresh hashmap keys
    this.next_feature_key = max_feature_key + 1;
    this.next_unit_key = max_unit_key + 1;
    this.next_item_key = max_item_key + 1;
  }


  void addData(String datakey, String data) {
    switch(datakey) {
      case "code":
        this.code = GameMapCode.gameMapCode(data);
        break;
      case "mapName":
        this.mapName = data;
        break;
      case "fogHandling":
      case "fog_handling":
        this.fog_handling = MapFogHandling.fogHandling(data);
        break;
      case "color_tint":
        this.color_tint = Misc.toInt(data);
        break;
      case "show_tint":
        this.show_tint = Misc.toBoolean(data);
        break;
      case "outside_map":
        this.outside_map = Misc.toBoolean(data);
        break;
      case "maxHeight":
      case "max_height":
        this.max_height = Misc.toInt(data);
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
        IntegerCoordinate grid = new IntegerCoordinate(
          Misc.toInt(PApplet.trim(terrain_dimensions[0])),
          Misc.toInt(PApplet.trim(terrain_dimensions[1])));
        String[] terrain_values = PApplet.split(data_split[1], ',');
        if (terrain_values.length < 3) {
          p.global.errorMessage("ERROR: Terrain values missing dimension in data: " + data + ".");
          break;
        }
        int terrain_id = Misc.toInt(PApplet.trim(terrain_values[0]));
        int terrain_elevation = Misc.toInt(PApplet.trim(terrain_values[1]));
        this.setTerrainBaseElevation(terrain_elevation, grid);
        this.setTerrain(terrain_id, grid, false);
        if (Misc.toBoolean(PApplet.trim(terrain_values[2]))) {
          this.exploreTerrain(grid, false);
        }
        try {
          int terrain_timer = Misc.toInt(PApplet.trim(terrain_values[3]));
          this.mapSquare(grid).timer_square = terrain_timer;
        } catch(Exception e) {}
        break;
      case "nextFeatureKey":
      case "next_feature_key":
        this.next_feature_key  = Misc.toInt(data);
        break;
      case "nextUnitKey":
      case "next_unit_key":
        this.next_unit_key = Misc.toInt(data);
        break;
      case "nextItemKey":
      case "next_item_key":
        this.next_item_key = Misc.toInt(data);
        break;
      default:
        this.addImplementationSpecificData(datakey, data);
        break;
    }
  }
  abstract void addImplementationSpecificData(String datakey, String data);
}


enum ReadFileObject {
  NONE("None"), MAP("Map"), FEATURE("Feature"), UNIT("Unit"), ITEM("Item"),
  PROJECTILE("Projectile"), LEVEL("Level"), LINKER("Linker"), TRIGGER("Trigger"),
  CONDITION("Condition"), EFFECT("Effect"), STATUS_EFFECT("StatusEffect"),
  ABILITY("Ability"), HERO("Hero"), INVENTORY("Inventory");

  private static final List<ReadFileObject> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  protected String name;

  private ReadFileObject(String name) {
    this.name = name;
  }

  public static ReadFileObject objectType(String name) {
    for (ReadFileObject type : ReadFileObject.VALUES) {
      if (type == ReadFileObject.NONE) {
        continue;
      }
      if (type.name.equals(name)) {
        return type;
      }
    }
    return ReadFileObject.NONE;
  }
}


enum MapFogHandling {
  DEFAULT("Default"), NONE("None"), EXPLORED("Explored"), NOFOG("NoFog");

  private static final List<MapFogHandling> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  protected String name;

  private MapFogHandling(String name) {
    this.name = name;
  }

  public static MapFogHandling fogHandling(String name) {
    for (MapFogHandling fogH : MapFogHandling.VALUES) {
      if (fogH.name.equals(name)) {
        return fogH;
      }
    }
    return MapFogHandling.NONE;
  }

  public boolean show_fog() {
    return MapFogHandling.show_fog(this);
  }
  public static boolean show_fog(MapFogHandling fogHandling) {
    switch(fogHandling) {
      case NONE:
      case NOFOG:
        return false;
      default:
        return true;
    }
  }
}