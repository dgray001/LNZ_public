package LNZModule;

import java.util.*;
import processing.core.*;
import DImg.DImg;

class GameMapEditor extends GameMap {
  class ConfirmDeleteForm extends ConfirmForm {
    ConfirmDeleteForm(LNZ sketch) {
      super(sketch, "Confirm Delete", "Are you sure you want to delete all the map " +
        "objects (features, units, items) in the rectangle?");
    }
    @Override
    public void submit() {
      if (GameMapEditor.this.rectangle_dropping != null) {
        // Delete features
        for (Feature f : GameMapEditor.this.features.values()) {
          if (GameMapEditor.this.rectangle_dropping.contains(f)) {
            f.remove = true;
          }
        }
        // Delete units
        Iterator<Map.Entry<Integer, Unit>> unit_iterator = GameMapEditor.this.units.entrySet().iterator();
        while(unit_iterator.hasNext()) {
          Map.Entry<Integer, Unit> entry = (Map.Entry<Integer, Unit>)unit_iterator.next();
          if (GameMapEditor.this.rectangle_dropping.contains(entry.getValue())) {
            entry.getValue().remove = true;
          }
        }
        // Delete items
        Iterator<Map.Entry<Integer, Item>> item_iterator = GameMapEditor.this.items.entrySet().iterator();
        while(item_iterator.hasNext()) {
          Map.Entry<Integer, Item> entry = (Map.Entry<Integer, Item>)item_iterator.next();
          if (GameMapEditor.this.rectangle_dropping.contains(entry.getValue())) {
            entry.getValue().remove = true;
          }
        }
      }
      GameMapEditor.this.rectangle_dropping = null;
      this.canceled = true;
    }
  }

  protected GameMapSquare dropping_terrain;
  protected boolean dragging_terrain = false;
  protected int terrain_id = 0;
  protected MapObject dropping_object;
  protected MapObject prev_dropping_object;
  protected boolean draw_grid = true;
  protected boolean rectangle_mode = false;
  protected Rectangle rectangle_dropping = null;
  protected boolean drawing_rectangle = false;
  protected boolean square_mode = false;
  protected ConfirmForm confirm_form;
  protected EditMapObjectForm map_object_form;
  protected boolean prevent_terrain_overlap = false;

  GameMapEditor(LNZ sketch) {
    super(sketch);
    this.draw_fog = false;
    this.force_all_hoverable = true;
  }
  GameMapEditor(LNZ sketch, GameMapCode code, String folderPath) {
    super(sketch, code, folderPath);
    this.draw_fog = false;
    this.force_all_hoverable = true;
  }
  GameMapEditor(LNZ sketch, String mapName, String folderPath) {
    super(sketch, mapName, folderPath);
    this.draw_fog = false;
    this.force_all_hoverable = true;
  }
  GameMapEditor(LNZ sketch, String mapName, int mapWidth, int mapHeight) {
    super(sketch, mapName, mapWidth, mapHeight);
    this.draw_fog = false;
    this.force_all_hoverable = true;
  }


  void dropTerrain(int id) {
    this.dropping_terrain = new GameMapSquare(p, 0, 0, id);
    this.terrain_id = id;
  }


  void update_super(int millis) {
    super.update(millis);
  }


  @Override
  void setZoom(double zoom) {
    if (zoom > 500) {
      zoom = 500;
    }
    else if (zoom < 15) {
      zoom = 15;
    }
    this.zoom = zoom;
    this.refreshDisplayMapParameters();
  }


  @Override
  void update(int millis) {
    if (this.confirm_form != null) {
      this.confirm_form.update(millis);
      if (this.confirm_form.canceled) {
        this.confirm_form = null;
      }
      return;
    }
    else if (this.map_object_form != null) {
      this.map_object_form.update(millis);
      if (this.map_object_form.canceled) {
        this.map_object_form = null;
      }
      return;
    }
    int timeElapsed = millis - this.last_update_time;
    // check map object removals
    this.updateMapCheckObjectRemovalOnly();
    // update view
    this.updateView(timeElapsed);
    // draw map
    this.drawMap(true);
    // draw grid
    if (this.draw_grid) {
      this.drawGrid();
    }
    // draw hovered tile
    p.imageMode(PConstants.CORNER);
    GameMapSquare hovered_tile = this.mapSquare(this.mc);
    if (hovered_tile != null) {
      Coordinate image_coordinate = this.mapToScreenImageCoordinate(
        new Coordinate((int)Math.floor(this.mc.x), (int)Math.floor(this.mc.y)));
      if (p.global.holding_leftclick || p.global.holding_rightclick) {
        p.image(p.global.images.getImage("terrain/tile_select.png"),
          image_coordinate.x, image_coordinate.y, 2 * this.zoom, this.zoom);
      }
      else {
        p.image(p.global.images.getImage("terrain/tile_hover.png"),
          image_coordinate.x, image_coordinate.y, 2 * this.zoom, this.zoom);
      }
    }
    // draw rectangle dropping
    if (this.rectangle_mode && this.rectangle_dropping != null) {
      this.drawRectangle(this.rectangle_dropping);
    }
    // draw object dropping
    if (this.hovered_area) {
      if (this.dropping_terrain != null) {
        p.imageMode(PConstants.CENTER);
        double y_coordinate = this.last_mc.y - (0.5 * this.dropping_terrain.terrainImageHeightOverflow() - 1) * 0.5 * this.zoom;
        p.image(this.dropping_terrain.terrainImage(false), this.last_mc.x, y_coordinate, 2 * this.zoom,
          0.5 * (2 + 0.5 * this.dropping_terrain.terrainImageHeightOverflow()) * this.zoom);
      }
      else if (this.dropping_object == null) {
        p.imageMode(PConstants.CORNER);
        p.image(p.global.images.getImage("items/eraser.png"),
          this.last_mc.x, this.last_mc.y, this.zoom, this.zoom);
      }
      else {
        if (Feature.class.isInstance(this.dropping_object)) {
          Feature f = (Feature)this.dropping_object;
          p.imageMode(PConstants.CORNER);
          p.image(this.dropping_object.getImage(),
            this.last_mc.x - 0.5 * this.zoom, this.last_mc.y - 0.5 * this.zoom,
            this.zoom * (f.sizeX + f.sizeY), 0.5 * this.zoom * (f.sizeX +
            f.sizeY + 0.5 * f.terrainImageHeightOverflow()));
        }
        else {
          p.imageMode(PConstants.CENTER);
          p.image(this.dropping_object.getImage(), this.last_mc.x, this.last_mc.y, this.zoom *
            this.dropping_object.width(), this.zoom * this.dropping_object.height());
        }
      }
    }
    this.last_update_time = millis;
  }

  void drawRectangle(Rectangle rect) {
    p.noStroke();
    p.fill(DImg.ccolor(220, 100));
    p.beginShape();
    for (Coordinate screen : rect.screenCoordinates(this)) {
      p.vertex(screen.x, screen.y);
    }
    p.endShape();
  }

  void mouseMove_super(float mX, float mY) {
    super.mouseMove(mX, mY);
  }

  @Override
  void mouseMove(float mX, float mY) {
    if (this.confirm_form != null) {
      this.confirm_form.mouseMove(mX, mY);
      return;
    }
    else if (this.map_object_form != null) {
      this.map_object_form.mouseMove(mX, mY);
      return;
    }
    super.mouseMove(mX, mY);
    if (this.drawing_rectangle) {
      if (this.square_mode) {
        this.rectangle_dropping.xf = Math.ceil(this.mc.x);
        this.rectangle_dropping.yf = Math.ceil(this.mc.y);
      }
      else {
        this.rectangle_dropping.xf = this.mc.x;
        this.rectangle_dropping.yf = this.mc.y;
      }
    }
    if (this.dragging_terrain) {
      if (this.dropDragging()) {
        this.setTerrain(this.terrain_id, new IntegerCoordinate(this.mc));
      }
    }
  }

  boolean dropDragging() {
    if (!this.prevent_terrain_overlap) {
      return true;
    }
    GameMapSquare hovered_terrain = this.mapSquare(this.mc);
    if (hovered_terrain != null && hovered_terrain.terrain_id < 2) {
      return true;
    }
    return false;
  }

  void mousePress_super() {
    super.mousePress();
  }

  @Override
  void mousePress() {
    for (HeaderMessage message : this.headerMessages) {
      message.mousePress();
    }
    if (this.confirm_form != null) {
      this.confirm_form.mousePress();
      return;
    }
    else if (this.map_object_form != null) {
      this.map_object_form.mousePress();
      return;
    }
    if (this.selected_object != null && this.selected_object_textbox != null) {
      this.selected_object_textbox.mousePress();
    }
    switch(p.mouseButton) {
      case PConstants.LEFT:
        this.selectHoveredObject();
        break;
      case PConstants.RIGHT:
        if (this.rectangle_mode) {
          if (this.square_mode) {
            this.rectangle_dropping = new Rectangle(p, this.mapName, Math.floor(this.mc.x),
            Math.floor(this.mc.y), Math.floor(this.mc.x), Math.floor(this.mc.y));
          }
          else {
            this.rectangle_dropping = new Rectangle(p, this.mapName, this.mc.x, this.mc.y, this.mc.x, this.mc.y);
          }
          this.drawing_rectangle = true;
          break;
        }
        if (this.dropping_terrain != null) {
          this.setTerrain(this.terrain_id, new IntegerCoordinate(this.mc));
          this.dragging_terrain = true;
        }
        else if (this.dropping_object == null) { // erase
          if (this.hovered_object != null) {
            this.hovered_object.remove = true;
          }
        }
        else {
          if (Feature.class.isInstance(this.dropping_object)) {
            this.dropping_object.setLocation(this.mc.x, this.mc.y);
            this.addFeature((Feature)this.dropping_object);
            this.dropping_object = new Feature(p, this.dropping_object.ID);
          }
          else if (Unit.class.isInstance(this.dropping_object)) {
            this.dropping_object.setLocation(this.mc.x, this.mc.y);
            this.addUnit((Unit)this.dropping_object);
            this.dropping_object = new Unit(p, this.dropping_object.ID);
          }
          else if (Item.class.isInstance(this.dropping_object)) {
            if (!p.global.holding_ctrl && Feature.class.isInstance(this.hovered_object)) {
              Feature hovered_object_feature = (Feature)this.hovered_object;
              if (hovered_object_feature.inventory != null) {
                hovered_object_feature.inventory.stashInDrawers(new Item(p, this.dropping_object.ID), true);
                break;
              }
            }
            else if (!p.global.holding_ctrl && Unit.class.isInstance(this.hovered_object)) {
              Unit hovered_object_unit = (Unit)this.hovered_object;
              if (hovered_object_unit.canPickup()) {
                hovered_object_unit.pickup(new Item(p, this.dropping_object.ID));
                break;
              }
            }
            this.dropping_object.setLocation(this.mc.x, this.mc.y);
            this.addItem((Item)this.dropping_object, false);
            this.dropping_object = new Item(p, this.dropping_object.ID);
          }
        }
        break;
      case PConstants.CENTER:
        if (this.dropping_terrain != null) {
          this.dragging_terrain = false;
          this.dropping_terrain = null;
          this.dropping_object = null;
          this.prev_dropping_object = null;
        }
        else {
          if (this.dropping_object == null) {
            this.dropping_object = this.prev_dropping_object;
          }
          else {
            this.prev_dropping_object = this.dropping_object;
            this.dropping_object = null;
          }
        }
        break;
    }
  }

  void mouseRelease_super(float mX, float mY) {
    super.mouseRelease(mX, mY);
  }

  @Override
  void mouseRelease(float mX, float mY) {
    if (this.confirm_form != null) {
      this.confirm_form.mouseRelease(mX, mY);
      return;
    }
    else if (this.map_object_form != null) {
      this.map_object_form.mouseRelease(mX, mY);
      return;
    }
    super.mouseRelease(mX, mY);
    switch(p.mouseButton) {
      case PConstants.LEFT:
        break;
      case PConstants.RIGHT:
        this.dragging_terrain = false;
        this.drawing_rectangle = false;
        if (this.rectangle_mode && this.rectangle_dropping != null) {
          if (this.dropping_terrain != null) {
            for (int i = (int)Math.floor(this.rectangle_dropping.xi);
              i < Math.ceil(this.rectangle_dropping.xf); i++) {
              for (int j = (int)Math.floor(this.rectangle_dropping.yi);
                j < Math.ceil(this.rectangle_dropping.yf); j++) {
                this.setTerrain(this.terrain_id, new IntegerCoordinate(i, j));
              }
            }
            this.rectangle_dropping = null;
          }
          else if (this.dropping_object != null) {
            if (Feature.class.isInstance(this.dropping_object)) {
              for (int i = (int)Math.floor(this.rectangle_dropping.xi);
                i < Math.ceil(this.rectangle_dropping.xf); i += this.dropping_object.width()) {
                for (int j = (int)Math.floor(this.rectangle_dropping.yi);
                  j < Math.ceil(this.rectangle_dropping.yf); j += this.dropping_object.height()) {
                  this.dropping_object.setLocation(i, j);
                  this.addFeature((Feature)this.dropping_object);
                  this.dropping_object = new Feature(p, this.dropping_object.ID);
                }
              }
            }
            else if (Unit.class.isInstance(this.dropping_object)) {
              // no support for unit rectangle adding
            }
            else if (Item.class.isInstance(this.dropping_object)) {
              // no support for item rectangle adding
            }
            this.rectangle_dropping = null;
          }
          else {
            this.confirm_form = new ConfirmDeleteForm(p);
          }
        }
        break;
      case PConstants.CENTER:
        break;
    }
  }

  void scroll_super(int amount) {
    super.scroll(amount);
  }

  @Override
  void scroll(int amount) {
    if (this.confirm_form != null) {
      this.confirm_form.scroll(amount);
      return;
    }
    else if (this.map_object_form != null) {
      this.map_object_form.scroll(amount);
      return;
    }
    if (this.hovered_terrain != null && p.global.holding_alt) {
      IntegerCoordinate coordinate = new IntegerCoordinate(this.mc);
      this.setTerrainBaseElevation(this.hovered_terrain.base_elevation - amount, coordinate);
      this.addHeaderMessage("Base elevation of " + coordinate.toString() +
        " changed to " + this.hovered_terrain.base_elevation + ".");
    }
    super.scroll(amount);
  }

  void keyPress_super(int key, int keyCode) {
    super.keyPress(key, keyCode);
  }

  @Override
  void keyPress(int key, int keyCode) {
    if (this.confirm_form != null) {
      this.confirm_form.keyPress(key, keyCode);
      return;
    }
    else if (this.map_object_form != null) {
      this.map_object_form.keyPress(key, keyCode);
      return;
    }
    if (key == PConstants.CODED) {
      switch(keyCode) {
        case PConstants.ALT:
          if (!p.global.holding_alt) {
            break;
          }
          if (this.hovered_object != null) {
            break;
          }
          this.hovered_terrain = this.mapSquare(this.mc);
          if (this.hovered_terrain != null && !this.hovered_terrain.interactionTooltip(this.units.get(0)).equals("")) {
            p.global.setCursor("icons/cursor_interact.png");
          }
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
        case 'p':
          this.terrain_dimg.img.save("data/images/maps/terrain.png");
          this.fog_dimg.img.save("data/images/maps/fog.png");
          this.map_display.img.save("data/images/maps/terrain_display.png");
          this.fog_display.save("data/images/maps/fog_display.png");
          break;
        case 'z':
          this.draw_grid = !this.draw_grid;
          if (this.draw_grid) {
            this.addHeaderMessage("Showing Grid");
          }
          else {
            this.addHeaderMessage("Hiding Grid");
          }
          break;
        case 'x':
          this.draw_fog = !this.draw_fog;
          if (this.draw_fog) {
            this.addHeaderMessage("Showing Fog");
          }
          else {
            this.addHeaderMessage("Hiding Fog");
          }
          break;
        case 'c':
          this.rectangle_mode = !this.rectangle_mode;
          this.drawing_rectangle = false;
          if (this.rectangle_mode) {
            this.addHeaderMessage("Rectangle Mode on");
          }
          else {
            this.addHeaderMessage("Rectangle Mode off");
          }
          break;
        case 'v':
          this.square_mode = !this.square_mode;
          if (this.square_mode) {
            this.addHeaderMessage("Square Mode on");
          }
          else {
            this.addHeaderMessage("Square Mode off");
          }
          break;
        case 'b':
          if (this.selected_object != null) {
            if (Feature.class.isInstance(this.selected_object)) {
              this.map_object_form = new FeatureEditForm(p, (Feature)this.selected_object);
              p.global.defaultCursor();
            }
            else if (Unit.class.isInstance(this.selected_object)) {
              this.map_object_form = new UnitEditForm(p, (Unit)this.selected_object);
              p.global.defaultCursor();
            }
            else if (Item.class.isInstance(this.selected_object)) {
              this.map_object_form = new ItemEditForm(p, (Item)this.selected_object);
              p.global.defaultCursor();
            }
          }
          break;
        case 'n':
          this.prevent_terrain_overlap = true;
          break;
      }
    }
  }

  void keyRelease_super(int key, int keyCode) {
    super.keyRelease(key, keyCode);
  }

  @Override
  void keyRelease(int key, int keyCode) {
    if (this.confirm_form != null) {
      this.confirm_form.keyRelease(key, keyCode);
      return;
    }
    else if (this.map_object_form != null) {
      this.map_object_form.keyRelease(key, keyCode);
      return;
    }
    if (key == PConstants.CODED) {
      switch(keyCode) {
        case PConstants.ALT:
          if (p.global.holding_alt) {
            break;
          }
          if (this.hovered_object != null) {
            break;
          }
          p.global.defaultCursor("icons/cursor_interact.png");
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
        case 'n':
          this.prevent_terrain_overlap = false;
          break;
      }
    }
  }
}

class GameMapLevelEditor extends GameMapEditor {
  GameMapLevelEditor(LNZ sketch, String mapName, String folderPath) {
    super(sketch, mapName, folderPath);
    this.rectangle_mode = true;
    this.square_mode = true;
  }

  @Override
  void update(int millis) {
    if (this.confirm_form != null) {
      this.confirm_form.update(millis);
      if (this.confirm_form.canceled) {
        this.confirm_form = null;
      }
      return;
    }
    int time_elapsed = millis - this.last_update_time;
    // check map object removals
    this.updateMapCheckObjectRemovalOnly();
    // update view
    this.updateView(time_elapsed);
    // draw map
    this.drawMap(true);
    // draw grid
    if (this.draw_grid) {
      this.drawGrid();
    }
    // draw rectangle dropping
    if (this.drawing_rectangle && this.rectangle_dropping != null) {
      this.drawRectangle(this.rectangle_dropping);
    }
    this.last_update_time = millis;
  }

  @Override
  void mouseMove(float mX, float mY) {
    if (this.confirm_form != null) {
      this.confirm_form.mouseMove(mX, mY);
      return;
    }
    this.mouseMove_super(mX, mY);
    if (this.drawing_rectangle) {
      if (this.square_mode) {
        this.rectangle_dropping.xf = Math.ceil(this.mc.x);
        this.rectangle_dropping.yf = Math.ceil(this.mc.y);
      }
      else {
        this.rectangle_dropping.xf = this.mc.x;
        this.rectangle_dropping.yf = this.mc.y;
      }
    }
  }

  @Override
  void mousePress() {
    for (HeaderMessage message : this.headerMessages) {
      message.mousePress();
    }
    if (this.confirm_form != null) {
      this.confirm_form.mousePress();
      return;
    }
    if (this.selected_object != null && this.selected_object_textbox != null) {
      this.selected_object_textbox.mousePress();
    }
    switch(p.mouseButton) {
      case PConstants.LEFT:
        this.selectHoveredObject();
        break;
      case PConstants.RIGHT:
        if (this.rectangle_mode) {
          if (this.square_mode) {
            this.rectangle_dropping = new Rectangle(p, this.mapName, Math.floor(this.mc.x),
            Math.floor(this.mc.y), Math.floor(this.mc.x), Math.floor(this.mc.y));
          }
          else {
            this.rectangle_dropping = new Rectangle(p, this.mapName, this.mc.x, this.mc.y, this.mc.x, this.mc.y);
          }
          this.drawing_rectangle = true;
          break;
        }
        break;
      case PConstants.CENTER:
        break;
    }
  }

  @Override
  void mouseRelease(float mX, float mY) {
    if (this.confirm_form != null) {
      this.confirm_form.mouseRelease(mX, mY);
      return;
    }
    this.mouseRelease_super(mX, mY);
    switch(p.mouseButton) {
      case PConstants.LEFT:
        break;
      case PConstants.RIGHT:
        this.drawing_rectangle = false;
        break;
      case PConstants.CENTER:
        break;
    }
  }

  @Override
  void scroll(int amount) {
    if (this.confirm_form != null) {
      this.confirm_form.scroll(amount);
      return;
    }
    this.scroll_super(amount);
  }

  @Override
  void keyPress(int key, int keyCode) {
    if (this.confirm_form != null) {
      this.confirm_form.keyPress(key, keyCode);
      return;
    }
    if (key == PConstants.CODED) {
      switch(keyCode) {
        case PConstants.ALT:
          if (!p.global.holding_alt) {
            break;
          }
          if (this.hovered_object != null) {
            break;
          }
          this.hovered_terrain = this.mapSquare(this.mc.x, this.mc.y);
          if (this.hovered_terrain != null && !this.hovered_terrain.interactionTooltip(this.units.get(0)).equals("")) {
            p.global.setCursor("icons/cursor_interact.png");
          }
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
        case 'z':
          this.draw_grid = !this.draw_grid;
          if (this.draw_grid) {
            this.addHeaderMessage("Showing Grid");
          }
          else {
            this.addHeaderMessage("Hiding Grid");
          }
          break;
        case 'x':
          this.draw_fog = !this.draw_fog;
          if (this.draw_fog) {
            this.addHeaderMessage("Showing Fog");
          }
          else {
            this.addHeaderMessage("Hiding Fog");
          }
          break;
        case 'c':
          this.rectangle_mode = !this.rectangle_mode;
          this.drawing_rectangle = false;
          if (this.rectangle_mode) {
            this.addHeaderMessage("Rectangle Mode on");
          }
          else {
            this.addHeaderMessage("Rectangle Mode off");
          }
          break;
        case 'v':
          this.square_mode = !this.square_mode;
          if (this.square_mode) {
            this.addHeaderMessage("Square Mode on");
          }
          else {
            this.addHeaderMessage("Square Mode off");
          }
          break;
      }
    }
  }

  @Override
  void keyRelease(int key, int keyCode) {
    if (this.confirm_form != null) {
      this.confirm_form.keyRelease(key, keyCode);
      return;
    }
    if (key == PConstants.CODED) {
      switch(keyCode) {
        case PConstants.ALT:
          if (p.global.holding_alt) {
            break;
          }
          if (this.hovered_object != null) {
            break;
          }
          p.global.defaultCursor("icons/cursor_interact.png");
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
}


class GameMapAreaEditor extends GameMapArea {
  protected boolean draw_grid = true;

  GameMapAreaEditor(LNZ sketch, String map_name, String map_folder) {
    super(sketch, map_name, map_folder);
    this.draw_fog = false;
  }

  @Override
  void setZoom(double zoom) {
    if (zoom > 500) {
      zoom = 500;
    }
    else if (zoom < 15) {
      zoom = 15;
    }
    this.zoom = zoom;
    this.refreshDisplayMapParameters();
  }

  @Override
  void update(int millis) {
    int time_elapsed = millis - this.last_update_time;
    // check map object removals
    this.updateMapCheckObjectRemovalOnly();
    // update view
    this.updateView(time_elapsed);
    // draw map
    this.drawMap(true);
    // draw grid
    if (this.draw_grid) {
      this.drawGrid();
    }
    this.last_update_time = millis;
  }

  @Override
  void keyPress(int key, int keyCode) {
    if (key == PConstants.CODED) {
      switch(keyCode) {
        case PConstants.ALT:
          if (!p.global.holding_alt) {
            break;
          }
          if (this.hovered_object != null) {
            break;
          }
          this.hovered_terrain = this.mapSquare(this.mc.x, this.mc.y);
          if (this.hovered_terrain != null && !this.hovered_terrain.interactionTooltip(this.units.get(0)).equals("")) {
            p.global.setCursor("icons/cursor_interact.png");
          }
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
        case 'p':
          this.map_display.img.save("data/areas/terrain_display.png");
          break;
        case 'z':
          this.draw_grid = !this.draw_grid;
          if (this.draw_grid) {
            this.addHeaderMessage("Showing Grid");
          }
          else {
            this.addHeaderMessage("Hiding Grid");
          }
          break;
        case 'x':
          this.draw_fog = !this.draw_fog;
          if (this.draw_fog) {
            this.addHeaderMessage("Showing Fog");
          }
          else {
            this.addHeaderMessage("Hiding Fog");
          }
          break;
      }
    }
  }
}