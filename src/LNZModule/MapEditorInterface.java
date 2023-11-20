package LNZModule;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import processing.core.*;
import Button.*;
import DImg.DImg;
import Element.*;
import FileSystem.FileSystem;
import Form.*;
import Misc.Misc;

enum MapEditorPage {
  MAPS, AREAS, LEVELS, TERRAIN, FEATURES, UNITS, ITEMS, TESTMAP, OPENING_MAPEDITOR,
  CREATING_MAP, OPENING_TESTMAP, OPENING_TESTLEVEL, LEVEL_INFO, LEVEL_MAPS,
  LINKERS, TRIGGERS, TRIGGER_EDITOR, CONDITION_EDITOR, EFFECT_EDITOR, TESTLEVEL,
  EDITING_AREA, TESTING_AREA;
}

enum RightPanelElementLocation {
  BOTTOM, TOP, WHOLE;
}


class MapEditorInterface extends InterfaceLNZ {

  abstract class MapEditorButton extends RectangleButton {
    private LNZ p;
    MapEditorButton(LNZ sketch) {
      super(sketch, 0, 0.94 * sketch.height, 0,
        sketch.height - LNZ.mapEditor_buttonGapSize);
      this.p = sketch;
      this.raised_border = true;
      this.roundness = 0;
      this.setColors(DImg.ccolor(170), DImg.ccolor(222, 184, 135),
        DImg.ccolor(244, 164, 96), DImg.ccolor(205, 133, 63), DImg.ccolor(0));
      this.show_message = true;
    }
    public void hover() {
      p.global.sounds.trigger_interface("interfaces/buttonOn2");
    }
    public void dehover() {
      this.clicked = false;
    }
    public void click() {
      p.global.sounds.trigger_interface("interfaces/buttonClick1");
    }
  }

  class MapEditorButton1 extends MapEditorButton {
    MapEditorButton1(LNZ sketch) {
      super(sketch);
      this.message = "Toggle\nDisplay";
    }
    public void release() {
      MapEditorInterface.this.buttonClick1();
    }
  }

  class MapEditorButton2 extends MapEditorButton {
    MapEditorButton2(LNZ sketch) {
      super(sketch);
      this.message = "";
    }
    public void release() {
      this.stayDehovered();
      MapEditorInterface.this.buttonClick2();
    }
  }

  class MapEditorButton3 extends MapEditorButton {
    MapEditorButton3(LNZ sketch) {
      super(sketch);
      this.message = "";
    }
    public void release() {
      this.stayDehovered();
      MapEditorInterface.this.buttonClick3();
    }
  }

  class MapEditorButton4 extends MapEditorButton {
    MapEditorButton4(LNZ sketch) {
      super(sketch);
      this.message = "Main\nMenu";
    }
    public void release() {
      this.stayDehovered();
      MapEditorInterface.this.buttonClick4();
    }
  }

  class MapEditorButton5 extends MapEditorButton {
    MapEditorButton5(LNZ sketch) {
      super(sketch);
      this.setLocation(0, 0.9 * sketch.height + LNZ.mapEditor_buttonGapSize,
        0, 0.94 * sketch.height - LNZ.mapEditor_buttonGapSize);
      this.message = "Help";
    }
    public void release() {
      this.stayDehovered();
      MapEditorInterface.this.buttonClick5();
    }
  }


  class MapEditorListTextBox extends ListTextBox {
    class RightClickListTextBox extends MaxListTextBox {
      RightClickListTextBox(LNZ sketch, float mX, float mY, MapEditorPage page) {
        super(sketch, mX - LNZ.mapEditor_rightClickBoxWidth, mY, mX, mY + LNZ.mapEditor_rightClickBoxMaxHeight);
        switch(page) {
          case MAPS:
            this.setText("Open Map");
            this.addLine("Rename Map");
            this.addLine("Test Map");
            this.addLine("Delete Map");
            break;
          case AREAS:
            this.setText("Open Area");
            this.addLine("Rename Area");
            this.addLine("Test Area");
            this.addLine("Delete Area");
            break;
          case LEVELS:
            this.setText("Open Level");
            this.addLine("Rename Level");
            this.addLine("Test Level");
            this.addLine("Delete Level");
            break;
          default:
            break;
        }
        this.highlight_color = DImg.ccolor(1, 0);
        this.hover_color = DImg.ccolor(200, 150, 140, 100);
      }

      public void click() {
        MapEditorListTextBox.this.clickOption(this.line_clicked);
      }

      public void doubleclick() {}
    }

    class RenameInputBox extends InputBox {
      RenameInputBox(LNZ sketch, int line_index) {
        super(sketch, MapEditorListTextBox.this.xi + 1, 0, MapEditorListTextBox.this.xf - 1, 0);
        this.setText(MapEditorListTextBox.this.text_lines_ref.get(line_index));
        this.hint_text = "Enter a filename";
        double currY = MapEditorListTextBox.this.yi + 1;
        if (MapEditorListTextBox.this.text_title_ref != null) {
          p.textSize(MapEditorListTextBox.this.title_size);
          currY += p.textAscent() + p.textDescent() + 2;
        }
        p.textSize(MapEditorListTextBox.this.text_size);
        double text_height = p.textAscent() + p.textDescent();
        double input_yi = currY + (line_index - Math.floor(MapEditorListTextBox.this.
          scrollbar.value)) * (text_height + MapEditorListTextBox.this.text_leading);
        this.setYLocation(input_yi, input_yi + text_height);
        this.typing = true;
        this.location_cursor = this.text.length();
        this.location_display = this.text.length();
        this.updateDisplayText();
      }
    }

    private LNZ p;

    protected boolean active = false;
    protected RightClickListTextBox rightClickMenu;
    protected RenameInputBox renameInputBox;
    protected MapEditorPage previous_page = MapEditorPage.LEVEL_INFO;
    protected double scroll_maps = 0;
    protected double scroll_areas = 0;
    protected double scroll_levels = 0;
    protected double scroll_terrain = 0;
    protected double scroll_features = 0;
    protected double scroll_units = 0;
    protected double scroll_items = 0;
    protected String text_ref_maps = null;
    protected String text_ref_areas = null;
    protected String text_ref_levels = null;
    protected String text_ref_terrain = null;
    protected String text_ref_features = null;
    protected String text_ref_units = null;
    protected String text_ref_items = null;
    protected String text_ref_levelMaps = null;

    MapEditorListTextBox(LNZ sketch) {
      super(sketch, sketch.width, LNZ.mapEditor_listBoxGap, sketch.width,
        0.9 * sketch.height - LNZ.mapEditor_listBoxGap);
      this.p = sketch;
      this.color_background = DImg.ccolor(250, 190, 140);
      this.color_header = DImg.ccolor(220, 180, 130);
      this.scrollbar.setButtonColors(DImg.ccolor(220), DImg.ccolor(220, 160, 110), DImg.ccolor(
        240, 180, 130), DImg.ccolor(200, 140, 90), DImg.ccolor(0));
      this.scrollbar.button_upspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      this.scrollbar.button_downspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      this.can_unclick_outside_box = false;
    }

    @Override
    public void update(int millis) {
      super.update(millis);
      if (this.rightClickMenu != null) {
        this.rightClickMenu.update(millis);
      }
      else if (this.renameInputBox != null) {
        this.renameInputBox.update(millis);
      }
    }

    @Override
    public void mouseMove(float mX, float mY) {
      if (this.rightClickMenu != null) {
        this.rightClickMenu.mouseMove(mX, mY);
      }
      else if (this.renameInputBox != null) {
        this.renameInputBox.mouseMove(mX, mY);
      }
      else {
        super.mouseMove(mX, mY);
      }
    }

    @Override
    public void mousePress() {
      if (this.rightClickMenu != null) {
        if (this.rightClickMenu.hovered) {
          this.rightClickMenu.mousePress();
        }
        else {
          this.rightClickMenu = null;
        }
      }
      else if (this.renameInputBox != null) {
        if (this.renameInputBox.hovered) {
          this.renameInputBox.mousePress();
        }
        else {
          this.removeRenameInputBox();
        }
      }
      else {
        super.mousePress();
        if (("").equals(this.highlightedLine())) {
          this.line_clicked = -1;
        }
      }
    }

    @Override
    public void mouseRelease(float mX, float mY) {
      if (this.rightClickMenu != null) {
        this.rightClickMenu.mouseRelease(mX, mY);
      }
      else if (this.renameInputBox != null) {
        this.renameInputBox.mouseRelease(mX, mY);
      }
      else {
        super.mouseRelease(mX, mY);
      }
    }

    @Override
    public void scroll(int amount) {
      if (this.rightClickMenu != null) {
        this.rightClickMenu.scroll(amount);
      }
      else {
        super.scroll(amount);
      }
    }

    public void keyPress(int key, int keyCode) {
      if (this.renameInputBox != null) {
        this.renameInputBox.keyPress(key, keyCode);
        if (key != PConstants.CODED && (key == PConstants.ENTER || key == PConstants.RETURN)) {
          this.removeRenameInputBox();
        }
      }
    }

    public void keyRelease(int key, int keyCode) {
      if (this.renameInputBox != null) {
        this.renameInputBox.keyRelease(key, keyCode);
      }
    }

    void removeRenameInputBox() {
      if (this.renameInputBox == null) {
        return;
      }
      if (this.line_clicked < 0 || this.line_clicked >= this.text_lines_ref.size()) {
        return;
      }
      String targetName = this.renameInputBox.text;
      if (targetName != null && !targetName.equals("")) {
        switch(MapEditorInterface.this.page) {
          case MAPS:
            MapEditorInterface.this.renameMapFile(this.highlightedLine(), this.renameInputBox.text);
            break;
          case AREAS:
            MapEditorInterface.this.renameAreaFile(this.highlightedLine(), this.renameInputBox.text);
            break;
          case LEVELS:
            MapEditorInterface.this.renameLevelFolder(this.highlightedLine(), this.renameInputBox.text);
            break;
          default:
            break;
        }
      }
      this.renameInputBox = null;
      this.refresh();
    }

    void clickOption(int option) {
      switch(MapEditorInterface.this.page) {
        case MAPS:
          switch(option) {
            case 0:
              MapEditorInterface.this.openMapEditor(this.highlightedLine());
              break;
            case 1:
              if (this.line_clicked < 0 || this.line_clicked >= this.text_lines_ref.size()) {
                break;
              }
              this.renameInputBox = new RenameInputBox(p, this.line_clicked);
              break;
            case 2:
              MapEditorInterface.this.testMap();
              break;
            case 3:
              MapEditorInterface.this.deleteMap();
              break;
            default:
              p.global.errorMessage("ERROR: Option index " + option + " not recognized.");
              break;
          }
          break;
        case AREAS:
          switch(option) {
            case 0:
              MapEditorInterface.this.openAreaEditor(this.highlightedLine());
              break;
            case 1:
              if (this.line_clicked < 0 || this.line_clicked >= this.text_lines_ref.size()) {
                break;
              }
              this.renameInputBox = new RenameInputBox(p, this.line_clicked);
              break;
            case 2:
              MapEditorInterface.this.testArea(this.highlightedLine());
              break;
            case 3:
              MapEditorInterface.this.deleteArea();
              break;
            default:
              p.global.errorMessage("ERROR: Option index " + option + " not recognized.");
              break;
          }
          break;
        case LEVELS:
          switch(option) {
            case 0:
              MapEditorInterface.this.openLevelEditor(this.highlightedLine());
              break;
            case 1:
              if (this.line_clicked < 0 || this.line_clicked >= this.text_lines_ref.size()) {
                break;
              }
              this.renameInputBox = new RenameInputBox(p, this.line_clicked);
              break;
            case 2:
              MapEditorInterface.this.testLevel();
              break;
            case 3:
              MapEditorInterface.this.deleteLevel();
              break;
            default:
              p.global.errorMessage("ERROR: Option index " + option + " not recognized.");
              break;
          }
          break;
        default:
          break;
      }
      this.rightClickMenu = null;
    }

    void setPosition(RightPanelElementLocation position) {
      switch(position) {
        case TOP:
          this.setYLocation(LNZ.mapEditor_listBoxGap, 0.45 * p.height - LNZ.mapEditor_listBoxGap);
          break;
        case BOTTOM:
          this.setYLocation(0.45 * p.height + LNZ.mapEditor_listBoxGap, 0.9 * p.height - LNZ.mapEditor_listBoxGap);
          break;
        case WHOLE:
          this.setYLocation(LNZ.mapEditor_listBoxGap, 0.9 * p.height - LNZ.mapEditor_listBoxGap);
          break;
      }
    }

    void setList(MapEditorPage page) {
      if (page == null) {
        return;
      }
      switch(this.previous_page) {
        case MAPS:
          this.scroll_maps = this.scrollbar.value;
          break;
        case AREAS:
          this.scroll_areas = this.scrollbar.value;
          break;
        case LEVELS:
          this.scroll_levels = this.scrollbar.value;
          break;
        case TERRAIN:
          this.scroll_terrain = this.scrollbar.value;
          break;
        case FEATURES:
          this.scroll_features = this.scrollbar.value;
          break;
        case UNITS:
          this.scroll_units = this.scrollbar.value;
          break;
        case ITEMS:
          this.scroll_items = this.scrollbar.value;
          break;
        default:
          break;
      }
      this.previous_page = page;
      this.clearText();
      this.line_hovered = -1;
      this.line_clicked = -1;
      this.active = true;
      switch(page) {
        case MAPS:
          this.setTitleText("Maps");
          if (this.text_ref_maps == null) {
            this.setMapsText();
          }
          else {
            this.setText(this.text_ref_maps);
          }
          this.scrollbar.updateValue(this.scroll_maps);
          break;
        case AREAS:
          this.setTitleText("Areas");
          if (this.text_ref_areas == null) {
            this.setAreasText();
          }
          else {
            this.setText(this.text_ref_areas);
          }
          this.scrollbar.updateValue(this.scroll_areas);
          break;
        case LEVELS:
          this.setTitleText("Levels");
          if (this.text_ref_levels == null) {
            this.setLevelsText();
          }
          else {
            this.setText(this.text_ref_levels);
          }
          this.scrollbar.updateValue(this.scroll_levels);
          break;
        case TERRAIN:
          this.setTitleText("Terrain");
          if (this.text_ref_terrain == null) {
            this.setTerrainText();
          }
          else {
            this.setText(this.text_ref_terrain);
          }
          this.scrollbar.updateValue(this.scroll_terrain);
          break;
        case FEATURES:
          this.setTitleText("Features");
          if (this.text_ref_features == null) {
            this.setFeaturesText();
          }
          else {
            this.setText(this.text_ref_features);
          }
          this.scrollbar.updateValue(this.scroll_features);
          break;
        case UNITS:
          this.setTitleText("Units");
          if (this.text_ref_units == null) {
            this.setUnitsText();
          }
          else {
            this.setText(this.text_ref_units);
          }
          this.scrollbar.updateValue(this.scroll_units);
          break;
        case ITEMS:
          this.setTitleText("Items");
          if (this.text_ref_items == null) {
            this.setItemsText();
          }
          else {
            this.setText(this.text_ref_items);
          }
          this.scrollbar.updateValue(this.scroll_items);
          break;
        case LEVEL_MAPS:
          this.setTitleText("Saved Maps");
          if (this.text_ref_levelMaps == null) {
            this.setLevelMapsText();
          }
          else {
            this.setText(this.text_ref_levelMaps);
          }
          break;
        default:
          this.active = false;
          break;
      }
    }

    void setMapsText() {
      if (FileSystem.folderExists(p, "data/maps")) {
        boolean first = true;
        for (Path p : FileSystem.listFiles(p, "data/maps/")) {
          String filename = p.getFileName().toString();
          if (!filename.endsWith(".map.lnz")) {
            continue;
          }
          String mapName = PApplet.split(filename, '.')[0];
          if (first) {
            this.setText(mapName);
            first = false;
          }
          else {
            this.addLine(mapName);
          }
        }
      }
      else {
        FileSystem.mkdir(p, "data/maps");
      }
      this.text_ref_maps = this.text_ref; // need to refresh whenever a map is made/deleted/renamed
    }

    void setAreasText() {
      if (FileSystem.folderExists(p, "data/areas")) {
        boolean first = true;
        for (Path p : FileSystem.listFiles(p, "data/areas/")) {
          String filename = p.getFileName().toString();
          if (!filename.endsWith(".area.lnz")) {
            continue;
          }
          String mapName = PApplet.split(filename, '.')[0];
          if (first) {
            this.setText(mapName);
            first = false;
          }
          else {
            this.addLine(mapName);
          }
        }
      }
      else {
        FileSystem.mkdir(p, "data/areas");
      }
      this.text_ref_areas = this.text_ref;
    }

    void setLevelsText() {
      if (FileSystem.folderExists(p, "data/levels")) {
        boolean first = true;
        for (Path p : FileSystem.listFolders(p, "data/levels")) {
          String levelName = p.getFileName().toString();
          if (first) {
            this.setText(levelName);
            first = false;
          }
          else {
            this.addLine(levelName);
          }
        }
      }
      else {
        FileSystem.mkdir(p, "data/levels");
      }
      this.text_ref_levels = this.text_ref;
    }

    void setTerrainText() {
      boolean first_terrain = true;
      if (FileSystem.fileExists(p, "data/terrains.lnz")) {
        for (String line : p.loadStrings(p.sketchPath("data/terrains.lnz"))) {
          if (first_terrain) {
            first_terrain = false;
            this.setText(line);
          }
          else {
            this.addLine(line);
          }
        }
      }
      this.text_ref_terrain = this.text_ref;
    }

    void setFeaturesText() {
      boolean first_feature = true;
      if (FileSystem.fileExists(p, "data/features.lnz")) {
        for (String line : p.loadStrings(p.sketchPath("data/features.lnz"))) {
          if (first_feature) {
            first_feature = false;
            this.setText(line);
          }
          else {
            this.addLine(line);
          }
        }
      }
      this.text_ref_features = this.text_ref;
    }

    void setUnitsText() {
      boolean first_unit = true;
      if (FileSystem.fileExists(p, "data/units.lnz")) {
        for (String line : p.loadStrings(p.sketchPath("data/units.lnz"))) {
          if (first_unit) {
            first_unit = false;
            this.setText(line);
          }
          else {
            this.addLine(line);
          }
        }
      }
      this.text_ref_units = this.text_ref;
    }

    void setItemsText() {
      boolean first_item = true;
      if (FileSystem.fileExists(p, "data/items.lnz")) {
        for (String line : p.loadStrings(p.sketchPath("data/items.lnz"))) {
          if (first_item) {
            first_item = false;
            this.setText(line);
          }
          else {
            this.addLine(line);
          }
        }
      }
      this.text_ref_items = this.text_ref;
    }

    void setLevelMapsText() {
      if (FileSystem.folderExists(p, "data/maps")) {
        boolean first = true;
        for (Path p : FileSystem.listFiles(p, "data/maps/")) {
          String filename = p.getFileName().toString();
          if (!filename.endsWith(".map.lnz")) {
            continue;
          }
          String mapName = PApplet.split(filename, '.')[0];
          if (first) {
            this.setText(mapName);
            first = false;
          }
          else {
            this.addLine(mapName);
          }
        }
      }
      else {
        FileSystem.mkdir(p, "data/maps");
      }
      this.text_ref_levelMaps = this.text_ref;
    }

    void refresh() {
      switch(MapEditorInterface.this.page) {
        case MAPS:
          this.text_ref_maps = null;
          break;
        case AREAS:
          this.text_ref_areas = null;
          break;
        case LEVELS:
          this.text_ref_levels = null;
          break;
        case LEVEL_MAPS:
          this.text_ref_levelMaps = null;
          break;
        default:
          break;
      }
      this.setList(MapEditorInterface.this.page);
    }

    public void click() {
      switch(MapEditorInterface.this.page) {
        case MAPS:
          if (p.mouseButton == PConstants.RIGHT) {
            this.rightClickMenu = new RightClickListTextBox(p, p.mouseX, p.mouseY, MapEditorInterface.this.page);
          }
          break;
        case AREAS:
          if (p.mouseButton == PConstants.RIGHT) {
            this.rightClickMenu = new RightClickListTextBox(p, p.mouseX, p.mouseY, MapEditorInterface.this.page);
          }
          break;
        case LEVELS:
          if (p.mouseButton == PConstants.RIGHT) {
            this.rightClickMenu = new RightClickListTextBox(p, p.mouseX, p.mouseY, MapEditorInterface.this.page);
          }
          break;
        case TERRAIN:
          break;
        case FEATURES:
          break;
        case UNITS:
          break;
        case ITEMS:
          break;
        case LEVEL_MAPS:
          break;
        default:
          p.global.errorMessage("ERROR: MapEditorPage " + page + " not found.");
          break;
      }
    }

    public void doubleclick() {
      switch(MapEditorInterface.this.page) {
        case MAPS:
          if (p.mouseButton == PConstants.LEFT) {
            MapEditorInterface.this.openMapEditor(this.highlightedLine());
          }
          break;
        case AREAS:
          if (p.mouseButton == PConstants.LEFT) {
            MapEditorInterface.this.openAreaEditor(this.highlightedLine());
          }
          break;
        case LEVELS:
          if (p.mouseButton == PConstants.LEFT) {
            MapEditorInterface.this.openLevelEditor(this.highlightedLine());
          }
          break;
        case TERRAIN:
          if (p.mouseButton == PConstants.LEFT) {
            MapEditorInterface.this.dropTerrain(this.highlightedLine());
          }
          break;
        case FEATURES:
          if (p.mouseButton == PConstants.LEFT) {
            MapEditorInterface.this.dropFeature(this.highlightedLine());
          }
          break;
        case UNITS:
          if (p.mouseButton == PConstants.LEFT) {
            MapEditorInterface.this.dropUnit(this.highlightedLine());
          }
          break;
        case ITEMS:
          if (p.mouseButton == PConstants.LEFT) {
            MapEditorInterface.this.dropItem(this.highlightedLine());
          }
          break;
        case LEVEL_MAPS:
          if (p.mouseButton == PConstants.LEFT) {
            MapEditorInterface.this.addMapToLevel(this.highlightedLine());
          }
          break;
        default:
          p.global.errorMessage("ERROR: MapEditorPage " + page + " not found.");
          break;
      }
    }
  }


  class LevelEditorListTextBox extends ListTextBox {
    protected boolean active = false;
    protected MapEditorPage previous_page = MapEditorPage.LEVEL_INFO;
    protected double scroll_maps = 0;
    protected double scroll_linkers = 0;
    protected double scroll_triggers = 0;
    protected double scroll_components = 0;

    LevelEditorListTextBox(LNZ sketch) {
      super(sketch, sketch.width, LNZ.mapEditor_listBoxGap, sketch.width,
        0.9 * sketch.height - LNZ.mapEditor_listBoxGap);
      this.color_background = DImg.ccolor(250, 190, 140);
      this.color_header = DImg.ccolor(220, 180, 130);
      this.scrollbar.setButtonColors(DImg.ccolor(220), DImg.ccolor(220, 160, 110), DImg.ccolor(
        240, 180, 130), DImg.ccolor(200, 140, 90), DImg.ccolor(0));
      this.scrollbar.button_upspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      this.scrollbar.button_downspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      this.can_unclick_outside_box = false;
    }

    void setPosition(RightPanelElementLocation position) {
      switch(position) {
        case TOP:
          this.setYLocation(LNZ.mapEditor_listBoxGap, 0.45 * p.height - LNZ.mapEditor_listBoxGap);
          break;
        case BOTTOM:
          this.setYLocation(0.45 * p.height + LNZ.mapEditor_listBoxGap, 0.9 * p.height - LNZ.mapEditor_listBoxGap);
          break;
        case WHOLE:
          this.setYLocation(LNZ.mapEditor_listBoxGap, 0.9 * p.height - LNZ.mapEditor_listBoxGap);
          break;
      }
    }

    void setList(MapEditorPage page) {
      if (page == null) {
        return;
      }
      switch(this.previous_page) {
        case LEVEL_INFO:
        case LEVEL_MAPS:
          this.scroll_maps = this.scrollbar.value;
          break;
        case LINKERS:
          this.scroll_linkers = this.scrollbar.value;
          break;
        case TRIGGERS:
          this.scroll_triggers = this.scrollbar.value;
          break;
        case TRIGGER_EDITOR:
        case CONDITION_EDITOR:
        case EFFECT_EDITOR:
          this.scroll_components = this.scrollbar.value;
          break;
        default:
          break;
      }
      this.previous_page = page;
      this.clearText();
      this.line_hovered = -1;
      this.line_clicked = -1;
      this.active = true;
      switch(page) {
        case LEVEL_INFO:
        case LEVEL_MAPS:
          this.setTitleText("Maps");
          if (MapEditorInterface.this.curr_level != null) {
            boolean first = true;
            for (String mapName : MapEditorInterface.this.curr_level.mapNames) {
              if (first) {
                this.setText(mapName);
                first = false;
              }
              else {
                this.addLine(mapName);
              }
            }
          }
          this.scrollbar.updateValue(this.scroll_maps);
          break;
        case LINKERS:
          this.setTitleText("Linkers");
          if (MapEditorInterface.this.curr_level != null) {
            boolean first = true;
            for (Linker linker : MapEditorInterface.this.curr_level.linkers) {
              if (first) {
                this.setText(linker.rect1.fileString());
                this.addLine(linker.rect2.fileString());
                first = false;
              }
              else {
                this.addLine("");
                this.addLine(linker.rect1.fileString());
                this.addLine(linker.rect2.fileString());
              }
            }
          }
          this.scrollbar.updateValue(this.scroll_linkers);
          break;
        case TRIGGERS:
          this.setTitleText("Triggers");
          if (MapEditorInterface.this.curr_level != null) {
            boolean first = true;
            for (Map.Entry<Integer, Trigger> entry : MapEditorInterface.this.curr_level.triggers.entrySet()) {
              Trigger trigger = entry.getValue();
              if (first) {
                this.setText(entry.getKey() + ": " + trigger.triggerName);
                first = false;
              }
              else {
                this.addLine(entry.getKey() + ": " + trigger.triggerName);
              }
            }
          }
          this.scrollbar.updateValue(this.scroll_triggers);
          break;
        case TRIGGER_EDITOR:
        case CONDITION_EDITOR:
        case EFFECT_EDITOR:
          if (MapEditorInterface.this.curr_trigger == null) {
            break;
          }
          this.setTitleText("Trigger " + MapEditorInterface.this.curr_trigger.triggerID + " Components");
          boolean first = true;
          for (Condition condition : MapEditorInterface.this.curr_trigger.conditions) {
            if (first) {
              this.setText(condition.display_name);
              first = false;
            }
            else {
              this.addLine(condition.display_name);
            }
          }
          if (first) {
            this.setText("");
            first = false;
          }
          else {
            this.addLine("");
          }
          for (Effect effect : MapEditorInterface.this.curr_trigger.effects) {
            if (first) {
              this.setText(effect.display_name);
              first = false;
            }
            else {
              this.addLine(effect.display_name);
            }
          }
          this.scrollbar.updateValue(this.scroll_components);
          break;
        default:
          this.active = false;
          break;
      }
    }

    void refresh() {
      double scroll_value = this.scrollbar.value;
      this.setList(MapEditorInterface.this.page);
      this.scrollbar.updateValue(scroll_value);
    }

    @Override
    public void mousePress() {
      super.mousePress();
      if (("").equals(this.highlightedLine())) {
        this.line_clicked = -1;
      }
    }

    public void keyPress(int key, int keyCode) {
      if (key == PConstants.CODED) {
      }
      else {
        switch(key) {
          case 'a':
            switch(MapEditorInterface.this.page) {
              case LINKERS:
                MapEditorInterface.this.addLinkerToLevel();
                break;
              case TRIGGERS:
                MapEditorInterface.this.addTriggerToLevel();
                break;
              default:
                break;
            }
            break;
          case 'd':
            switch(MapEditorInterface.this.page) {
              case LEVEL_MAPS:
                MapEditorInterface.this.removeMapFromLevel(this.highlightedLine());
                break;
              case LINKERS:
                if (this.line_clicked < 0 || this.line_clicked % 3 == 0) {
                  break;
                }
                int linker_index = (int)(this.line_clicked/3.0);
                MapEditorInterface.this.removeLinkerFromLevel(linker_index);
                break;
              case TRIGGERS:
                if (this.highlightedLine() == null) {
                  break;
                }
                int triggerKey = Misc.toInt(PApplet.trim(PApplet.split(this.highlightedLine(), ':')[0]));
                if (triggerKey == 0) {
                  break;
                }
                MapEditorInterface.this.removeTriggerFromLevel(triggerKey);
                break;
              case TRIGGER_EDITOR:
              case CONDITION_EDITOR:
              case EFFECT_EDITOR:
                if (MapEditorInterface.this.curr_trigger == null) {
                  break;
                }
                if (MapEditorInterface.this.curr_trigger.conditions.size() == 0 &&
                  this.line_clicked != -1) {
                  MapEditorInterface.this.removeEffectFromTrigger(this.line_clicked);
                }
                else if (this.line_clicked > MapEditorInterface.this.curr_trigger.conditions.size()) {
                  MapEditorInterface.this.removeEffectFromTrigger(this.line_clicked -
                    MapEditorInterface.this.curr_trigger.conditions.size() - 1);
                }
                else if (this.line_clicked != -1 && this.line_clicked !=
                  MapEditorInterface.this.curr_trigger.conditions.size()) {
                  MapEditorInterface.this.removeConditionFromTrigger(this.line_clicked);
                }
                break;
              default:
                break;
            }
            break;
        }
      }
    }

    public void keyRelease(int key, int keyCode) {
    }

    public void click() {
      switch(page) {
        case LEVEL_INFO:
          break;
        case LEVEL_MAPS:
          break;
        case LINKERS:
          break;
        case TRIGGERS:
          break;
        case TRIGGER_EDITOR:
          break;
        case CONDITION_EDITOR:
          break;
        case EFFECT_EDITOR:
          break;
        default:
          break;
      }
    }
    public void doubleclick() {
      switch(page) {
        case LEVEL_INFO:
        case LEVEL_MAPS:
          if (MapEditorInterface.this.curr_level != null && this.highlightedLine() != null) {
            if (this.highlightedLine().equals(MapEditorInterface.this.curr_level.currMapName)) {
              MapEditorInterface.this.curr_level.closeMap();
            }
            else {
              MapEditorInterface.this.curr_level.openMap(this.highlightedLine());
            }
          }
          break;
        case LINKERS:
          if (this.line_clicked < 0 || this.line_clicked % 3 == 0) {
            break;
          }
          int linker_index = (int)(this.line_clicked/3.0);
          MapEditorInterface.this.openLinkEditorForm(linker_index);
          break;
        case TRIGGERS:
          if (MapEditorInterface.this.curr_level == null || this.highlightedLine() == null) {
            break;
          }
          int triggerKey = Misc.toInt(PApplet.trim(PApplet.split(this.highlightedLine(), ':')[0]));
          if (triggerKey == 0) {
            break;
          }
          MapEditorInterface.this.openTriggerEditor(triggerKey);
          break;
        case TRIGGER_EDITOR:
        case CONDITION_EDITOR:
        case EFFECT_EDITOR:
          if (MapEditorInterface.this.curr_trigger == null) {
            break;
          }
          if (MapEditorInterface.this.curr_trigger.conditions.size() == 0 &&
            this.line_clicked != -1) {
            MapEditorInterface.this.openEffectEditor(this.line_clicked);
          }
          else if (this.line_clicked > MapEditorInterface.this.curr_trigger.conditions.size()) {
            MapEditorInterface.this.openEffectEditor(this.line_clicked -
              MapEditorInterface.this.curr_trigger.conditions.size() - 1);
          }
          else if (this.line_clicked != -1 && this.line_clicked !=
            MapEditorInterface.this.curr_trigger.conditions.size()) {
            MapEditorInterface.this.openConditionEditor(this.line_clicked);
          }
          break;
        default:
          break;
      }
    }
  }


  class LevelHeroSelectorForm extends FormLNZ {
    private LNZ p;
    protected Level level;
    protected boolean added_hero = false;

    LevelHeroSelectorForm(LNZ sketch, Level level) {
      super(sketch, 0.5 * (sketch.width - LNZ.mapEditor_formWidth),
        0.5 * (sketch.height - LNZ.mapEditor_formHeight),
        0.5 * (sketch.width + LNZ.mapEditor_formWidth),
        0.5 * (sketch.height + LNZ.mapEditor_formHeight));
      this.p = sketch;
      this.setTitleText("Hero Selector");
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);
      this.setFieldCushion(10);
      this.level = level;
      if (this.level == null) {
        this.canceled = true;
        return;
      }

      SubmitFormField submit = new SubmitFormField(p, " Ok ");
      submit.button.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));

      this.addField(new SpacerFormField(p, 10));
      this.addField(new IntegerFormField(p, "Hero ID: ", 1101, 1102));
      this.addField(new IntegerFormField(p, "Hero Level: ", 0, 100));
      this.addField(new IntegerFormField(p, "Level Tokens: ", 0, 5050));
      this.addField(new FloatFormField(p, "Curr Mana: ", 0, 100));
      this.addField(new CheckboxFormField(p, "Remove Fog: "));
      this.addField(new SpacerFormField(p, 10));
      this.addField(submit);
    }

    @Override
    public void cancel() {
      if (!this.added_hero && this.level != null) {
        this.level.setPlayer(new Hero(p, HeroCode.BEN));
      }
      super.cancel();
    }

    public void submit() {
      Hero h = new Hero(p, Misc.toInt(this.fields.get(1).getValue()));
      h.setLevel(Misc.toInt(this.fields.get(2).getValue()));
      h.level_tokens = Misc.toInt(this.fields.get(3).getValue());
      h.curr_mana = Misc.toDouble(this.fields.get(4).getValue());
      this.level.setPlayer(h);
      if (this.level.curr_map != null && this.fields.get(5).getValue().equals("true")) {
        this.level.curr_map.setFogHandling(MapFogHandling.NONE);
      }
      this.added_hero = true;
      this.canceled = true;
    }
  }


  class HeroSelectorForm extends FormLNZ {
    protected Level level;
    protected boolean added_hero = false;

    HeroSelectorForm(LNZ sketch, Level level) {
      super(sketch, 0.5 * (sketch.width - LNZ.mapEditor_formWidth),
        0.5 * (sketch.height - LNZ.mapEditor_formHeight),
        0.5 * (sketch.width + LNZ.mapEditor_formWidth),
        0.5 * (sketch.height + LNZ.mapEditor_formHeight));
        this.setTitleText("Hero Selector");
        this.setTitleSize(18);
        this.color_background = DImg.ccolor(180, 250, 180);
        this.color_header = DImg.ccolor(30, 170, 30);
        this.setFieldCushion(10);
        this.level = level;
        if (this.level == null) {
          this.canceled = true;
          return;
        }

        SubmitFormField submit = new SubmitFormField(p, " Ok ");
        submit.button.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
          DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));

        this.addField(new SpacerFormField(p, 10));
        this.addField(new IntegerFormField(p, "Hero ID: ", 1101, 1102));
        this.addField(new IntegerFormField(p, "Hero Level: ", 0, 100));
        this.addField(new IntegerFormField(p, "Level Tokens: ", 0, 5050));
        this.addField(new FloatFormField(p, "Curr Mana: ", 0, 100));
        this.addField(new FloatFormField(p, "Location (x): ", 0, Float.MAX_VALUE - 1));
        this.addField(new FloatFormField(p, "Location (y): ", 0, Float.MAX_VALUE - 1));
        this.addField(new SpacerFormField(p, 10));
        this.addField(submit);
    }

    @Override
    public void cancel() {
      if (!this.added_hero && this.level != null) {
        this.level.addTestPlayer();
      }
      super.cancel();
    }

    public void submit() {
      Hero h = new Hero(p, Misc.toInt(this.fields.get(1).getValue()));
      h.setLevel(Misc.toInt(this.fields.get(2).getValue()));
      h.level_tokens = Misc.toInt(this.fields.get(3).getValue());
      h.curr_mana = Misc.toDouble(this.fields.get(4).getValue());
      h.setLocation(Misc.toDouble(this.fields.get(5).getValue()),
        Misc.toDouble(this.fields.get(6).getValue()));
      this.level.addPlayer(h);
      this.added_hero = true;
      this.canceled = true;
    }
  }


  class NewMapForm extends FormLNZ {
    NewMapForm(LNZ sketch) {
      super(sketch, 0.5 * (sketch.width - LNZ.mapEditor_formWidth),
        0.5 * (sketch.height - LNZ.mapEditor_formHeight),
        0.5 * (sketch.width + LNZ.mapEditor_formWidth),
        0.5 * (sketch.height + LNZ.mapEditor_formHeight));
      this.setTitleText("New Map");
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);
      this.setFieldCushion(0);

      MessageFormField error = new MessageFormField(p, "");
      error.text_color = DImg.ccolor(150, 20, 20);
      error.setTextSize(16);
      SubmitCancelFormField submit = new SubmitCancelFormField(p, "  Ok  ", "Cancel");
      submit.button1.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      submit.button2.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));

      this.addField(new SpacerFormField(p, 20));
      this.addField(new StringFormField(p, "", "Map Name"));
      this.addField(error);
      this.addField(new SpacerFormField(p, 10));
      this.addField(new IntegerFormField(p, "", "Map Width", 1, 3000));
      this.addField(new SpacerFormField(p, 20));
      this.addField(new IntegerFormField(p, "", "Map Height", 1, 3000));
      this.addField(new SpacerFormField(p, 20));
      this.addField(submit);
      this.focusNextField();
    }

    public void submit() {
      if (FileSystem.fileExists(p, "data/maps/" + this.fields.get(1).getValue() + ".map.lnz")) {
        this.fields.get(2).setValue("A map with that name already exists");
        return;
      }
      MapEditorInterface.this.navigate(MapEditorPage.CREATING_MAP);
      MapEditorInterface.this.create_map_thread = new NewMapThread(p, this.fields.get(1).getValue(),
        Misc.toInt(this.fields.get(4).getValue()), Misc.toInt(this.fields.get(6).getValue()));
      MapEditorInterface.this.create_map_thread.start();
      this.canceled = true;
    }
  }


  class NewAreaForm extends FormLNZ {
    NewAreaForm(LNZ sketch) {
      super(sketch, 0.5 * (sketch.width - LNZ.mapEditor_formWidth),
        0.5 * (sketch.height - LNZ.mapEditor_formHeight),
        0.5 * (sketch.width + LNZ.mapEditor_formWidth),
        0.5 * (sketch.height + LNZ.mapEditor_formHeight));
      this.setTitleText("New Area");
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);
      this.setFieldCushion(0);

      MessageFormField error = new MessageFormField(p, "");
      error.text_color = DImg.ccolor(150, 20, 20);
      error.setTextSize(16);
      SubmitCancelFormField submit = new SubmitCancelFormField(p, "  Ok  ", "Cancel");
      submit.button1.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      submit.button2.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));

      this.addField(new SpacerFormField(p, 20));
      this.addField(new StringFormField(p, "", "Area Name"));
      this.addField(error);
      this.addField(new SpacerFormField(p, 10));
      this.addField(new IntegerFormField(p, "", "Chunks from zero", 0, 30000));
      this.addField(new SpacerFormField(p, 20));
      this.addField(submit);
    }

    public void submit() {
      if (FileSystem.fileExists(p, "data/areas/" + this.fields.get(1).getValue() + ".area.lnz")) {
        this.fields.get(2).setValue("An area with that name already exists");
        return;
      }
      MapEditorInterface.this.createArea(this.fields.get(1).getValue());
      this.canceled = true;
    }
  }


  class NewLevelForm extends FormLNZ {
    NewLevelForm(LNZ sketch) {
      super(sketch, 0.5 * (sketch.width - LNZ.mapEditor_formWidth),
        0.5 * (sketch.height - LNZ.mapEditor_formHeight),
        0.5 * (sketch.width + LNZ.mapEditor_formWidth),
        0.5 * (sketch.height + LNZ.mapEditor_formHeight));
      this.setTitleText("New Level");
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);
      this.setFieldCushion(0);

      MessageFormField error = new MessageFormField(p, "");
      error.text_color = DImg.ccolor(150, 20, 20);
      error.setTextSize(16);
      SubmitCancelFormField submit = new SubmitCancelFormField(p, "  Ok  ", "Cancel");
      submit.button1.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      submit.button2.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));

      this.addField(new SpacerFormField(p, 20));
      this.addField(new StringFormField(p, "", "Level Name"));
      this.addField(error);
      this.addField(new SpacerFormField(p, 20));
      this.addField(submit);
    }

    public void submit() {
      if (FileSystem.folderExists(p, "data/levels/" + this.fields.get(1).getValue())) {
        this.fields.get(2).setValue("A level with that name already exists");
        return;
      }
      MapEditorInterface.this.newLevel(this.fields.get(1).getValue());
      this.canceled = true;
    }
  }


  class MessageForm extends FormLNZ {
    MessageForm(LNZ sketch, String title, String message) {
      super(sketch, 0.5 * (sketch.width - LNZ.mapEditor_formWidth_small),
        0.5 * (sketch.height - LNZ.mapEditor_formHeight_small),
        0.5 * (sketch.width + LNZ.mapEditor_formWidth_small),
        0.5 * (sketch.height + LNZ.mapEditor_formHeight_small));
      this.setTitleText(title);
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);

      SubmitFormField submit = new SubmitFormField(p, "  Ok  ");
      submit.button.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      this.addField(new SpacerFormField(p, 0));
      this.addField(new TextBoxFormField(p, message, 120));
      this.addField(submit);
    }
    public void submit() {
      this.canceled = true;
    }
  }


  class DeleteMapForm extends ConfirmForm {
    private String mapName;
    DeleteMapForm(LNZ sketch, String mapName) {
      super(sketch, "Delete Map", "Are you sure you want to delete this map?\n" + mapName);
      this.mapName = mapName;
    }
    public void submit() {
      FileSystem.deleteFile(p, "data/maps/" + this.mapName + ".map.lnz");
      this.canceled = true;
      MapEditorInterface.this.listBox1.refresh();
    }
  }


  class DeleteAreaForm extends ConfirmForm {
    private String area_name;
    DeleteAreaForm(LNZ sketch, String area_name) {
      super(sketch, "Delete Area", "Are you sure you want to delete this area?\n" + area_name);
      this.area_name = area_name;
    }
    public void submit() {
      FileSystem.deleteFile(p, "data/areas/" + this.area_name + ".area.lnz");
      this.canceled = true;
      MapEditorInterface.this.listBox1.refresh();
    }
  }


  class DeleteLevelForm extends ConfirmForm {
    private String levelName;
    DeleteLevelForm(LNZ sketch, String levelName) {
      super(sketch, "Delete Level", "Are you sure you want to delete this level?\n" + levelName);
      this.levelName = levelName;
    }
    public void submit() {
      FileSystem.deleteFolder(p, "data/levels/" + this.levelName);
      this.canceled = true;
      MapEditorInterface.this.listBox1.refresh();
    }
  }


  class GoToMainMenuForm extends ConfirmForm {
    GoToMainMenuForm(LNZ sketch) {
      super(sketch, "Main Menu", "Are you sure you want to exit to the main menu?\nAll unsaved changes will be lost.");
    }
    public void submit() {
      this.canceled = true;
      MapEditorInterface.this.exitToMainMenu();
    }
  }


  class HelpForm extends ConfirmForm {
    HelpForm(LNZ sketch, String helpString) {
      super(sketch, "Help", helpString, true);
    }
    public void submit() {
      this.canceled = true;
    }
  }


  class ChooseSeedForm extends FormLNZ {
    ChooseSeedForm(LNZ sketch) {
      super(sketch, 0.5 * (sketch.width - LNZ.mapEditor_formWidth_small),
        0.5 * (sketch.height - LNZ.mapEditor_formHeight_small),
        0.5 * (sketch.width + LNZ.mapEditor_formWidth_small),
        0.5 * (sketch.height + LNZ.mapEditor_formHeight_small));
      this.setTitleText("Choose Seed");
      this.setTitleSize(18);
      this.color_background = DImg.ccolor(180, 250, 180);
      this.color_header = DImg.ccolor(30, 170, 30);

      SubmitFormField submit = new SubmitFormField(p, " Load ");
      submit.button.setColors(DImg.ccolor(220), DImg.ccolor(190, 240, 190),
        DImg.ccolor(140, 190, 140), DImg.ccolor(90, 140, 90), DImg.ccolor(0));
      this.addField(new SpacerFormField(p, 0));
      this.addField(new MessageFormField(p, "Choose new seed to reload area."));
      this.addField(new IntegerFormField(p, "   ", "Enter an integer"));
      this.addField(submit);

      this.fields.get(2).focus();
    }
    public void submit() {
      this.canceled = true;
      MapEditorInterface.this.specificAreaSeed(Misc.toInt(this.fields.get(2).getValue()));
    }
  }


  class AreaEditorForm extends Form {
    protected GameMapArea area;

    AreaEditorForm(LNZ sketch, GameMapArea area, double xi, double xf) {
      super(sketch, xi, LNZ.mapEditor_listBoxGap, xf, 0.9 * sketch.height - LNZ.mapEditor_listBoxGap);
      this.color_background = DImg.ccolor(250, 190, 140);
      this.color_header = DImg.ccolor(220, 180, 130);
      this.scrollbar.setButtonColors(DImg.ccolor(220), DImg.ccolor(220, 160, 110), DImg.ccolor(
        240, 180, 130), DImg.ccolor(200, 140, 90), DImg.ccolor(0));
      this.scrollbar.button_upspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      this.scrollbar.button_downspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      this.setFieldCushion(10);
      this.setTitleText("Area Editor");
      this.area = area;

      IntegerFormField area_edge_xi = new IntegerFormField(p, "Edge XI: ", "Enter integer", -1000000, 1000000);
      IntegerFormField area_edge_yi = new IntegerFormField(p, "Edge YI: ", "Enter integer", -1000000, 1000000);
      IntegerFormField area_edge_xf = new IntegerFormField(p, "Edge XF: ", "Enter integer", -1000000, 1000000);
      IntegerFormField area_edge_yf = new IntegerFormField(p, "Edge YF: ", "Enter integer", -1000000, 1000000);
      IntegerFormField spawn_chunk_x = new IntegerFormField(p, "Spawn Chunk X: ", "Enter integer", -1000000, 1000000);
      IntegerFormField spawn_chunk_y = new IntegerFormField(p, "Spawn Chunk Y: ", "Enter integer", -1000000, 1000000);
      SubmitFormField button = new SubmitFormField(p, " Reload\nMap");
      button.setButtonHeight(45);
      button.button.setColors(DImg.ccolor(220), DImg.ccolor(240, 190, 150), DImg.ccolor(190, 140, 115),
        DImg.ccolor(140, 90, 50), DImg.ccolor(0));

      this.addField(new SpacerFormField(p, 30));
      this.addField(area_edge_xi);
      this.addField(area_edge_yi);
      this.addField(area_edge_xf);
      this.addField(area_edge_yf);
      this.addField(new SpacerFormField(p, 0));
      this.addField(spawn_chunk_x);
      this.addField(spawn_chunk_y);
      this.addField(new SpacerFormField(p, 0));
      this.addField(button);

      this.setValues();
    }

    void setValues() {
      this.fields.get(1).setValue(this.area.mapEdgeXi);
      this.fields.get(2).setValue(this.area.mapEdgeYi);
      this.fields.get(3).setValue(this.area.mapEdgeXf);
      this.fields.get(4).setValue(this.area.mapEdgeYf);
      this.fields.get(6).setValue(this.area.default_spawn_chunk.x);
      this.fields.get(7).setValue(this.area.default_spawn_chunk.y);
    }

    public void cancel() {}
    public void buttonPress(int i) {}

    public void submit() {
      int area_edge_xi = Misc.toInt(this.fields.get(1).getValue());
      int area_edge_yi = Misc.toInt(this.fields.get(2).getValue());
      int area_edge_xf = Misc.toInt(this.fields.get(3).getValue());
      if (area_edge_xf <= area_edge_xi) {
        area_edge_xf = area_edge_xi + 1;
      }
      int area_edge_yf = Misc.toInt(this.fields.get(4).getValue());
      if (area_edge_yf <= area_edge_yi) {
        area_edge_yf = area_edge_yi + 1;
      }
      int spawn_chunk_x = Misc.toInt(this.fields.get(6).getValue());
      if (spawn_chunk_x < area_edge_xi) {
        spawn_chunk_x = area_edge_xi;
      }
      else if (spawn_chunk_x > area_edge_xf) {
        spawn_chunk_x = area_edge_xf;
      }
      int spawn_chunk_y = Misc.toInt(this.fields.get(7).getValue());
      if (spawn_chunk_y < area_edge_yi) {
        spawn_chunk_y = area_edge_yi;
      }
      else if (spawn_chunk_y > area_edge_yf) {
        spawn_chunk_y = area_edge_yf;
      }
      this.area.mapEdgeXi = area_edge_xi;
      this.area.mapEdgeYi = area_edge_yi;
      this.area.mapEdgeXf = area_edge_xf;
      this.area.mapEdgeYf = area_edge_yf;
      this.area.default_spawn_chunk = new IntegerCoordinate(spawn_chunk_x, spawn_chunk_y);
      this.setValues();
      this.area.save("data/areas");
      MapEditorInterface.this.reloadArea();
    }
  }


  abstract class LevelEditorForm extends Form {
    LevelEditorForm(LNZ sketch, double xi, double xf) {
      super(sketch, xi, LNZ.mapEditor_listBoxGap, xf, 0.45 * sketch.height - LNZ.mapEditor_listBoxGap);
      this.color_background = DImg.ccolor(250, 190, 140);
      this.color_header = DImg.ccolor(220, 180, 130);
      this.scrollbar.setButtonColors(DImg.ccolor(220), DImg.ccolor(220, 160, 110), DImg.ccolor(
        240, 180, 130), DImg.ccolor(200, 140, 90), DImg.ccolor(0));
      this.scrollbar.button_upspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      this.scrollbar.button_downspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      this.setFieldCushion(0);
    }

    @Override
    public void update(int millis) {
      super.update(millis);
      this.submitForm();
    }

    public void cancel() {
    }

    abstract void updateFields();
  }


  class LinkerEditorForm extends FormLNZ {
    private Linker linker;

    LinkerEditorForm(LNZ sketch, Linker linker) {
      super(sketch, 0.5 * (sketch.width - LNZ.mapEditor_formWidth),
      0.5 * (sketch.height - LNZ.mapEditor_formHeight),
      0.5 * (sketch.width + LNZ.mapEditor_formWidth),
      0.5 * (sketch.height + LNZ.mapEditor_formHeight));
      this.linker = linker;
      this.setTitleText("Linker Editor");
      this.addField(new MessageFormField(p, "entrance: " + linker.rect1.mapName));
      this.addField(new FloatFormField(p, "xi: ", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
      this.addField(new FloatFormField(p, "yi: ", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
      this.addField(new FloatFormField(p, "xf: ", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
      this.addField(new FloatFormField(p, "yf: ", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
      this.addField(new SpacerFormField(p, 0));
      this.addField(new MessageFormField(p, "exit: " + linker.rect2.mapName));
      this.addField(new FloatFormField(p, "xi: ", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
      this.addField(new FloatFormField(p, "yi: ", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
      this.addField(new FloatFormField(p, "xf: ", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
      this.addField(new FloatFormField(p, "yf: ", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
      this.addField(new SubmitCancelFormField(p, " Save ", "Cancel"));
      this.updateForm();
    }

    void updateForm() {
      this.fields.get(1).setValueIfNotFocused(Double.toString(this.linker.rect1.xi));
      this.fields.get(2).setValueIfNotFocused(Double.toString(this.linker.rect1.yi));
      this.fields.get(3).setValueIfNotFocused(Double.toString(this.linker.rect1.xf));
      this.fields.get(4).setValueIfNotFocused(Double.toString(this.linker.rect1.yf));
      this.fields.get(7).setValueIfNotFocused(Double.toString(this.linker.rect2.xi));
      this.fields.get(8).setValueIfNotFocused(Double.toString(this.linker.rect2.yi));
      this.fields.get(9).setValueIfNotFocused(Double.toString(this.linker.rect2.xf));
      this.fields.get(10).setValueIfNotFocused(Double.toString(this.linker.rect2.yf));
    }

    void updateLinker() {
      this.linker.rect1.xi = Misc.toDouble(this.fields.get(1).getValue());
      this.linker.rect1.yi = Misc.toDouble(this.fields.get(2).getValue());
      this.linker.rect1.xf = Misc.toDouble(this.fields.get(3).getValue());
      this.linker.rect1.yf = Misc.toDouble(this.fields.get(4).getValue());
      this.linker.rect2.xi = Misc.toDouble(this.fields.get(7).getValue());
      this.linker.rect2.yi = Misc.toDouble(this.fields.get(8).getValue());
      this.linker.rect2.xf = Misc.toDouble(this.fields.get(9).getValue());
      this.linker.rect2.yf = Misc.toDouble(this.fields.get(10).getValue());
    }

    @Override
    public void update(int millis) {
      super.update(millis);
      this.updateLinker();
      this.updateForm();
    }

    public void submit() {
      this.updateLinker();
      this.cancel();
      MapEditorInterface.this.listBox2.refresh();
    }
  }


  class LevelInfoForm extends LevelEditorForm {
    LevelInfoForm(LNZ sketch, double xi, double xf) {
      super(sketch, xi, xf);
      if (MapEditorInterface.this.curr_level != null) {
        this.setTitleText(MapEditorInterface.this.curr_level.levelName);
        this.addField(new SpacerFormField(p, 20));
        this.addField(new MessageFormField(p, MapEditorInterface.this.curr_level.getCurrMapNameDisplay()));
        this.addField(new SpacerFormField(p, 20));
        this.addField(new MessageFormField(p, "Location: " + MapEditorInterface.this.curr_level.location.toString()));
        this.addField(new SpacerFormField(p, 20));
        this.addField(new MessageFormField(p, MapEditorInterface.this.curr_level.getPlayerStartLocationDisplay()));
        this.addField(new SpacerFormField(p, 10));
        this.addField(new MessageFormField(p, MapEditorInterface.this.curr_level.getPlayerSpawnLocationDisplay()));
      }
    }

    public void submit() {
      this.updateFields();
    }

    public void buttonPress(int i) {
    }

    void updateFields() {
      if (MapEditorInterface.this.curr_level == null) {
        return;
      }
      this.fields.get(1).setValue(MapEditorInterface.this.curr_level.getCurrMapNameDisplay());
      this.fields.get(3).setValue("Location: " + MapEditorInterface.this.curr_level.location.toString());
      this.fields.get(5).setValue(MapEditorInterface.this.curr_level.getPlayerStartLocationDisplay());
      this.fields.get(7).setValue(MapEditorInterface.this.curr_level.getPlayerSpawnLocationDisplay());
    }
  }


  class TriggerEditorForm extends LevelEditorForm {
    protected Trigger trigger;

    TriggerEditorForm(LNZ sketch, Trigger trigger, double xi, double xf) {
      super(sketch, xi, xf);
      this.trigger = trigger;
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new StringFormField(sketch, "  ", "Trigger Name"));
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new CheckboxFormField(sketch, "     Active:  "));
      this.addField(new CheckboxFormField(sketch, "  Looping:  "));
      this.addField(new CheckboxFormField(sketch, "Amalgam:  "));
      this.addField(new SpacerFormField(sketch, 45));
      ButtonsFormField buttons = new ButtonsFormField(sketch, "Add\nCondition", "Add\nEffect");
      buttons.setButtonHeight(45);
      buttons.button1.setColors(DImg.ccolor(220), DImg.ccolor(240, 190, 150), DImg.ccolor(190, 140, 115),
        DImg.ccolor(140, 90, 50), DImg.ccolor(0));
      buttons.button2.setColors(DImg.ccolor(220), DImg.ccolor(240, 190, 150), DImg.ccolor(190, 140, 115),
        DImg.ccolor(140, 90, 50), DImg.ccolor(0));
      this.addField(buttons);
      this.updateFields();
    }

    public void submit() {
      this.trigger.triggerName = this.fields.get(1).getValue();
      this.trigger.active = Misc.toBoolean(this.fields.get(3).getValue());
      this.trigger.looping = Misc.toBoolean(this.fields.get(4).getValue());
      this.trigger.amalgam = Misc.toBoolean(this.fields.get(5).getValue());
    }

    public void buttonPress(int i) {
      if (i != 7) {
        return;
      }
      if (!Misc.isInt(this.fields.get(7).getValue())) {
        return;
      }
      int buttonPressed = Misc.toInt(this.fields.get(7).getValue());
      if (buttonPressed == 0) {
        MapEditorInterface.this.addConditionToTrigger();
      }
      else if (buttonPressed == 1) {
        MapEditorInterface.this.addEffectToTrigger();
      }
    }

    void updateFields() {
      this.setTitleText(trigger.triggerName);
      this.fields.get(1).setValueIfNotFocused(this.trigger.triggerName);
      this.fields.get(3).setValueIfNotFocused(Boolean.toString(this.trigger.active));
      this.fields.get(4).setValueIfNotFocused(Boolean.toString(this.trigger.looping));
      this.fields.get(5).setValueIfNotFocused(Boolean.toString(this.trigger.amalgam));
    }
  }


  class ConditionEditorForm extends LevelEditorForm {
    Condition condition;

    ConditionEditorForm(LNZ sketch, Condition condition, double xi, double xf) {
      super(sketch, xi, xf);
      condition.setName();
      this.condition = condition;
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new IntegerFormField(sketch, "ID: ", "Enter an integer from 0-17", 0, 17));
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new IntegerFormField(sketch, "Number 1: ", "enter an integer", 0, Integer.MAX_VALUE - 1));
      this.addField(new IntegerFormField(sketch, "Number 2: ", "enter an integer", 0, Integer.MAX_VALUE - 1));
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new MessageFormField(sketch, "Rectangle: "));
      this.addField(new SpacerFormField(sketch, 10));
      this.addField(new CheckboxFormField(sketch, "Not: "));
      this.updateFields();
    }

    public void submit() {
      this.condition.setID(Misc.toInt(this.fields.get(1).getValue()));
      this.condition.number1 = Misc.toInt(this.fields.get(3).getValue());
      this.condition.number2 = Misc.toInt(this.fields.get(4).getValue());
      this.condition.setName();
      this.setTitleText(condition.display_name);
      this.condition.not_condition = Misc.toBoolean(this.fields.get(8).getValue());
      this.updateFields();
    }

    public void buttonPress(int i) {}

    void updateFields() {
      this.setTitleText(condition.display_name);
      this.fields.get(1).setValueIfNotFocused(Integer.toString(this.condition.ID));
      this.fields.get(3).setValueIfNotFocused(Integer.toString(this.condition.number1));
      this.fields.get(4).setValueIfNotFocused(Integer.toString(this.condition.number2));
      this.fields.get(6).setValueIfNotFocused("Rectangle: " + this.condition.rectangle.fileString());
      this.fields.get(8).setValueIfNotFocused(Boolean.toString(this.condition.not_condition));
    }

    @Override
    public void keyPress(int key, int keyCode) {
      super.keyPress(key, keyCode);
      if (key == 'a') {
        MapEditorInterface.this.addRectangleToCondition(this);
      }
    }
  }


  class EffectEditorForm extends LevelEditorForm {
    Effect effect;

    EffectEditorForm(LNZ sketch, Effect effect, double xi, double xf) {
      super(sketch, xi, xf);
      effect.setName();
      this.effect = effect;
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new IntegerFormField(sketch, "ID: ", "enter an integer from 0-66", 0, 66));
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new IntegerFormField(sketch, "Number: ", "enter an integer", Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1));
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new FloatFormField(sketch, "Decimal1: ", "enter a double", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new FloatFormField(sketch, "Decimal2: ", "enter a double", -Float.MAX_VALUE + 1, Float.MAX_VALUE - 1));
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new StringFormField(sketch, "Message: ", "enter a string"));
      this.addField(new SpacerFormField(sketch, 20));
      this.addField(new MessageFormField(sketch, "Rectangle: "));
      this.updateFields();
    }

    public void submit() {
      this.effect.setID(Misc.toInt(this.fields.get(1).getValue()));
      this.effect.number = Misc.toInt(this.fields.get(3).getValue());
      this.effect.decimal1 = Misc.toFloat(this.fields.get(5).getValue());
      this.effect.decimal2 = Misc.toFloat(this.fields.get(7).getValue());
      this.effect.message = this.fields.get(9).getValue();
      this.effect.setName();
      this.setTitleText(this.effect.display_name);
      this.updateFields();
    }

    public void buttonPress(int i) {}

    void updateFields() {
      this.setTitleText(effect.display_name);
      this.fields.get(1).setValueIfNotFocused(Integer.toString(this.effect.ID));
      this.fields.get(3).setValueIfNotFocused(Integer.toString(this.effect.number));
      this.fields.get(5).setValueIfNotFocused(Double.toString(this.effect.decimal1));
      this.fields.get(7).setValueIfNotFocused(Double.toString(this.effect.decimal2));
      this.fields.get(9).setValueIfNotFocused(this.effect.message);
      this.fields.get(11).setValueIfNotFocused("Rectangle: " + this.effect.rectangle.fileString());
    }

    @Override
    public void keyPress(int key, int keyCode) {
      super.keyPress(key, keyCode);
      if (key == 'a') {
        MapEditorInterface.this.addRectangleToEffect(this);
      }
    }
  }


  class NewMapThread extends Thread {
    private LNZ p;
    private GameMapEditor map_creating;
    private String curr_status = "";
    private String mapName = "";
    private int mapWidth = 1;
    private int mapHeight = 1;

    NewMapThread(LNZ sketch, String mapName, int mapWidth, int mapHeight) {
      super("NewMapThread");
      this.setDaemon(true);
      this.p = sketch;
      this.mapName = mapName;
      this.mapWidth = mapWidth;
      this.mapHeight = mapHeight;
    }

    @Override
    public void run() {
      this.curr_status = "Creating Map";
      this.map_creating = new GameMapEditor(p);
      this.map_creating.mapName = this.mapName;
      this.map_creating.mapWidth = this.mapWidth;
      this.map_creating.mapHeight = this.mapHeight;
      this.map_creating.initializeSquares();
      if (this.map_creating.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      this.curr_status += "\nSaving Map";
      this.map_creating.save(p.sketchPath("data/maps/"));
      if (this.map_creating.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      if (!p.global.images.loaded_map_gifs) {
        this.curr_status += "\nLoading Animations";
        p.global.images.loadMapGifs();
      }
      this.curr_status += "\nGenerating Map Image";
      this.map_creating.initializeTerrain();
      if (this.map_creating.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
    }
  }


  class OpenMapEditorThread extends Thread {
    private LNZ p;
    private String mapName;
    private String folderPath;
    private GameMapEditor map_opening;
    private String curr_status = "";

    OpenMapEditorThread(LNZ sketch, String mapName, String folderPath) {
      super("OpenMapEditorThread");
      this.setDaemon(true);
      this.p = sketch;
      this.mapName = mapName;
      this.folderPath = folderPath;
    }

    @Override
    public void run() {
      this.curr_status = "Opening File";
      this.map_opening = new GameMapEditor(p);
      this.map_opening.mapName = this.mapName;
      String[] lines = this.map_opening.open1File(this.folderPath);
      if (this.map_opening.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      this.curr_status += "\nSetting Data";
      this.map_opening.open2Data(lines);
      if (this.map_opening.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      if (!p.global.images.loaded_map_gifs) {
        this.curr_status += "\nLoading Animations";
        p.global.images.loadMapGifs();
      }
      this.curr_status += "\nGenerating Images";
      this.map_opening.initializeTerrain();
      if (this.map_opening.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
    }
  }


  class OpenTestMapThread extends Thread {
    private LNZ p;
    private String mapName;
    private Level level_opening;
    private String curr_status = "";

    OpenTestMapThread(LNZ sketch, String mapName) {
      super("OpenTestMapThread");
      this.setDaemon(true);
      this.p = sketch;
      this.mapName = mapName;
    }

    @Override
    public void run() {
      this.curr_status = "Opening File";
      GameMap map_testing = new GameMap(p);
      map_testing.mapName = this.mapName;
      String[] lines = map_testing.open1File("data/maps/");
      if (map_testing.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      this.curr_status += "\nSetting Data";
      map_testing.open2Data(lines);
      if (map_testing.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      if (!p.global.images.loaded_map_gifs) {
        this.curr_status += "\nLoading Animations";
        p.global.images.loadMapGifs();
      }
      this.curr_status += "\nGenerating Images";
      map_testing.initializeTerrain();
      if (map_testing.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      this.curr_status += "\nCreating Level";
      this.level_opening = new Level(p, map_testing);
      p.delay(500);
    }
  }


  class OpenTestLevelThread extends Thread {
    private String levelName;
    private Level level_opening;
    private String curr_status = "";

    OpenTestLevelThread(String levelName) {
      super("OpenTestLevelThread");
      this.setDaemon(true);
      this.levelName = levelName;
    }

    @Override
    public void run() {
      this.curr_status = "Opening File";
      this.level_opening = new Level(p, "data/levels", this.levelName);
      if (this.level_opening.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      this.curr_status += "\nCopying Data";
      FileSystem.deleteFolder(p, "data/profiles/" + p.global.profile.display_name.toLowerCase() + "/leveltester");
      FileSystem.mkdir(p, "data/profiles/" + p.global.profile.display_name.toLowerCase() + "/leveltester");
      FileSystem.copyFolder(p, "data/levels/" + this.levelName, "data/profiles/" + p.global.
        profile.display_name.toLowerCase() + "/leveltester/" + this.levelName);
      this.level_opening.folderPath = "data/profiles/" + p.global.profile.display_name.toLowerCase() + "/leveltester";
      this.level_opening.save();
      if (this.level_opening.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
      if (!p.global.images.loaded_map_gifs) {
        this.curr_status += "\nLoading Animations";
        p.global.images.loadMapGifs();
      }
      this.curr_status += "\nGenerating Images";
      if (this.level_opening.nullify) {
        this.curr_status += " -> " + p.global.lastErrorMessage();
        p.delay(2500);
        return;
      }
    }
  }


  private MapEditorPage page;

  private MapEditorButton[] buttons = new MapEditorButton[5];
  private Panel leftPanel;
  private Panel rightPanel;
  private MapEditorListTextBox listBox1;
  private LevelEditorListTextBox listBox2;
  private LevelEditorForm levelForm;
  private AreaEditorForm areaForm;

  private GameMapEditor curr_map;
  private GameMapAreaEditor curr_area;
  private Level curr_level;
  private Trigger curr_trigger;
  private int curr_trigger_component = -1;

  private OpenMapEditorThread open_mapEditor_thread;
  private NewMapThread create_map_thread;
  private OpenTestMapThread open_testMap_thread;
  private OpenTestLevelThread open_testLevel_thread;


  MapEditorInterface(LNZ sketch) {
    super(sketch);
    this.leftPanel = new Panel(sketch, PConstants.LEFT, LNZ.mapEditor_panelMinWidth,
    LNZ.mapEditor_panelMaxWidth, LNZ.mapEditor_panelStartWidth);
    this.rightPanel = new Panel(sketch, PConstants.RIGHT, LNZ.mapEditor_panelMinWidth,
      LNZ.mapEditor_panelMaxWidth, LNZ.mapEditor_panelStartWidth);
    this.listBox1 = new MapEditorListTextBox(sketch);
    this.listBox2 = new LevelEditorListTextBox(sketch);
    this.buttons[0] = new MapEditorButton1(sketch);
    this.buttons[1] = new MapEditorButton2(sketch);
    this.buttons[2] = new MapEditorButton3(sketch);
    this.buttons[3] = new MapEditorButton4(sketch);
    this.buttons[4] = new MapEditorButton5(sketch);
    this.leftPanel.addIcon(sketch.global.images.getImage("icons/triangle_gray.png"));
    this.rightPanel.addIcon(sketch.global.images.getImage("icons/triangle_gray.png"));
    this.leftPanel.color_background = sketch.global.color_panelBackground;
    this.rightPanel.color_background = sketch.global.color_panelBackground;
    this.navigate(MapEditorPage.MAPS);
    this.resizeButtons();
  }


  void navigate(MapEditorPage page) {
    this.page = page;
    this.listBox1.setList(this.page);
    this.listBox2.setList(this.page);
    boolean nullifyAreaForm = true;
    boolean nullifyLevelForm = true;
    boolean nullifyCurrTrigger = true;
    switch(this.page) {
      case MAPS:
        this.buttons[0].message = "Toggle\nDisplay";
        this.buttons[1].message = "New\nMap";
        this.buttons[2].message = "Test\nMap";
        this.listBox1.setPosition(RightPanelElementLocation.WHOLE);
        break;
      case AREAS:
        this.buttons[0].message = "Toggle\nDisplay";
        this.buttons[1].message = "New\nArea";
        this.buttons[2].message = "";
        this.listBox1.setPosition(RightPanelElementLocation.WHOLE);
        break;
      case LEVELS:
        this.buttons[0].message = "Toggle\nDisplay";
        this.buttons[1].message = "New\nLevel";
        this.buttons[2].message = "Test\nLevel";
        this.listBox1.setPosition(RightPanelElementLocation.WHOLE);
        break;
      case TERRAIN:
      case FEATURES:
      case UNITS:
      case ITEMS:
        this.buttons[0].message = "Toggle\nDisplay";
        this.buttons[1].message = "Save\nMap";
        this.buttons[2].message = "Cancel\nMap";
        this.listBox1.setPosition(RightPanelElementLocation.WHOLE);
        break;
      case TESTMAP:
        this.buttons[0].message = "";
        this.buttons[1].message = "Save\nMap";
        this.buttons[2].message = "Cancel\nMap";
        break;
      case OPENING_MAPEDITOR:
      case CREATING_MAP:
      case OPENING_TESTMAP:
      case OPENING_TESTLEVEL:
        this.buttons[0].message = "";
        this.buttons[1].message = "";
        this.buttons[2].message = "";
        break;
      case LEVEL_INFO:
        this.buttons[0].message = "Toggle\nDisplay";
        this.buttons[1].message = "Save\nLevel";
        this.buttons[2].message = "Cancel\nLevel";
        this.levelForm = new LevelInfoForm(p, p.width - this.rightPanel.size_curr +
          LNZ.mapEditor_listBoxGap, p.width - LNZ.mapEditor_listBoxGap);
        nullifyLevelForm = false;
        this.listBox2.setPosition(RightPanelElementLocation.BOTTOM);
        break;
      case LEVEL_MAPS:
        this.buttons[0].message = "Toggle\nDisplay";
        this.buttons[1].message = "Save\nLevel";
        this.buttons[2].message = "Cancel\nLevel";
        this.listBox1.setPosition(RightPanelElementLocation.TOP);
        this.listBox2.setPosition(RightPanelElementLocation.BOTTOM);
        break;
      case LINKERS:
        this.buttons[0].message = "Toggle\nDisplay";
        this.buttons[1].message = "Save\nLevel";
        this.buttons[2].message = "Cancel\nLevel";
        this.listBox2.setPosition(RightPanelElementLocation.WHOLE);
        break;
      case TRIGGERS:
        this.buttons[0].message = "Toggle\nDisplay";
        this.buttons[1].message = "Save\nLevel";
        this.buttons[2].message = "Cancel\nLevel";
        this.listBox2.setPosition(RightPanelElementLocation.WHOLE);
        break;
      case TRIGGER_EDITOR:
      case CONDITION_EDITOR:
      case EFFECT_EDITOR:
        this.buttons[0].message = "Return";
        this.buttons[1].message = "Save\nLevel";
        this.buttons[2].message = "Cancel\nLevel";
        nullifyLevelForm = false;
        nullifyCurrTrigger = false;
        this.listBox2.setPosition(RightPanelElementLocation.BOTTOM);
        break;
      case TESTLEVEL:
        this.buttons[0].message = "";
        this.buttons[1].message = "Save\nLevel";
        this.buttons[2].message = "Cancel\nLevel";
        break;
      case EDITING_AREA:
        this.buttons[0].message = "Random\nSeed";
        this.buttons[1].message = "Input\nSeed";
        this.buttons[2].message = "Cancel\nArea";
        nullifyAreaForm = false;
        this.areaForm = new AreaEditorForm(p, this.curr_area, p.width - this.rightPanel.size_curr +
          LNZ.mapEditor_listBoxGap, p.width - LNZ.mapEditor_listBoxGap);
        break;
      case TESTING_AREA:
        this.buttons[0].message = "";
        this.buttons[1].message = "";
        this.buttons[2].message = "Cancel\nLevel";
        break;
      default:
        p.global.errorMessage("ERROR: MapEditorPage " + this.page + " not found.");
        break;
    }
    if (nullifyAreaForm) {
      this.areaForm = null;
    }
    if (nullifyLevelForm) {
      this.levelForm = null;
    }
    if (nullifyCurrTrigger) {
      this.curr_trigger = null;
    }
  }

  void resizeButtons() {
    double buttonSize = (this.rightPanel.size_curr - 5 * LNZ.mapEditor_buttonGapSize) / 4.0;
    double xi = p.width - this.rightPanel.size_curr + LNZ.mapEditor_buttonGapSize;
    this.buttons[0].setXLocation(xi, xi + buttonSize);
    xi += buttonSize + LNZ.mapEditor_buttonGapSize;
    this.buttons[1].setXLocation(xi, xi + buttonSize);
    xi += buttonSize + LNZ.mapEditor_buttonGapSize;
    this.buttons[2].setXLocation(xi, xi + buttonSize);
    xi += buttonSize + LNZ.mapEditor_buttonGapSize;
    this.buttons[3].setXLocation(xi, xi + buttonSize);
    this.buttons[4].setXLocation(xi, xi + buttonSize);
    this.listBox1.setXLocation(p.width - this.rightPanel.size_curr + LNZ.mapEditor_listBoxGap,
      p.width - LNZ.mapEditor_listBoxGap);
    this.listBox2.setXLocation(p.width - this.rightPanel.size_curr + LNZ.mapEditor_listBoxGap,
      p.width - LNZ.mapEditor_listBoxGap);
    if (this.levelForm != null) {
      this.levelForm.setXLocation(p.width - this.rightPanel.size_curr + LNZ.mapEditor_listBoxGap,
        p.width - LNZ.mapEditor_listBoxGap);
    }
    if (this.areaForm != null) {
      this.areaForm.setXLocation(p.width - this.rightPanel.size_curr + LNZ.mapEditor_listBoxGap,
        p.width - LNZ.mapEditor_listBoxGap);
    }
  }

  void buttonClick1() {
    switch(this.page) {
      case MAPS:
        this.navigate(MapEditorPage.AREAS);
        break;
      case AREAS:
        this.navigate(MapEditorPage.LEVELS);
        break;
      case LEVELS:
        this.navigate(MapEditorPage.MAPS);
        break;
      case TERRAIN:
        this.navigate(MapEditorPage.FEATURES);
        break;
      case FEATURES:
        this.navigate(MapEditorPage.UNITS);
        break;
      case UNITS:
        this.navigate(MapEditorPage.ITEMS);
        break;
      case ITEMS:
        this.navigate(MapEditorPage.TERRAIN);
        break;
      case TESTMAP:
        break;
      case OPENING_MAPEDITOR:
      case CREATING_MAP:
        break;
      case OPENING_TESTMAP:
      case OPENING_TESTLEVEL:
        break;
      case LEVEL_INFO:
        this.navigate(MapEditorPage.LEVEL_MAPS);
        break;
      case LEVEL_MAPS:
        this.navigate(MapEditorPage.LINKERS);
        break;
      case LINKERS:
        this.navigate(MapEditorPage.TRIGGERS);
        break;
      case TRIGGERS:
        this.navigate(MapEditorPage.LEVEL_INFO);
        break;
      case TRIGGER_EDITOR:
        this.navigate(MapEditorPage.TRIGGERS);
        break;
      case CONDITION_EDITOR:
      case EFFECT_EDITOR:
        this.levelForm = new TriggerEditorForm(p, this.curr_trigger,
          p.width - this.rightPanel.size_curr + LNZ.mapEditor_listBoxGap,
            p.width - LNZ.mapEditor_listBoxGap);
        this.navigate(MapEditorPage.TRIGGER_EDITOR);
        break;
      case TESTLEVEL:
      case TESTING_AREA:
        break;
      case EDITING_AREA:
        this.randomAreaSeed();
        break;
      default:
        p.global.errorMessage("ERROR: MapEditorPage " + this.page + " not found.");
        break;
    }
  }

  void buttonClick2() {
    switch(this.page) {
      case MAPS:
        this.form = new NewMapForm(p);
        break;
      case AREAS:
        this.form = new NewAreaForm(p);
        break;
      case LEVELS:
        this.form = new NewLevelForm(p);
        break;
      case TERRAIN:
      case FEATURES:
      case UNITS:
      case ITEMS:
        this.saveMapEditor();
        break;
      case TESTMAP:
        this.saveMapTester();
        break;
      case OPENING_MAPEDITOR:
      case CREATING_MAP:
        break;
      case OPENING_TESTMAP:
      case OPENING_TESTLEVEL:
      case TESTING_AREA:
        break;
      case LEVEL_INFO:
      case LEVEL_MAPS:
      case LINKERS:
      case TRIGGERS:
      case TRIGGER_EDITOR:
      case CONDITION_EDITOR:
      case EFFECT_EDITOR:
        this.saveLevelEditor();
        break;
      case TESTLEVEL:
        this.saveLevelTester();
        break;
      case EDITING_AREA:
        this.inputAreaSeed();
        break;
      default:
        p.global.errorMessage("ERROR: MapEditorPage " + this.page + " not found.");
        break;
    }
  }

  void buttonClick3() {
    switch(this.page) {
      case MAPS:
        this.testMap();
        break;
      case AREAS:
        //this.testArea();
        break;
      case LEVELS:
        this.testLevel();
        break;
      case TERRAIN:
      case FEATURES:
      case UNITS:
      case ITEMS:
        this.closeMapEditor();
        break;
      case TESTMAP:
        this.closeMapTester();
        break;
      case OPENING_MAPEDITOR:
      case CREATING_MAP:
        break;
      case OPENING_TESTMAP:
      case OPENING_TESTLEVEL:
        break;
      case LEVEL_INFO:
      case LEVEL_MAPS:
      case LINKERS:
      case TRIGGERS:
      case TRIGGER_EDITOR:
      case CONDITION_EDITOR:
      case EFFECT_EDITOR:
        this.closeLevelEditor();
        break;
      case TESTLEVEL:
        this.closeLevelTester();
        break;
      case EDITING_AREA:
      case TESTING_AREA:
        this.closeAreaTester();
        break;
      default:
        p.global.errorMessage("ERROR: MapEditorPage " + this.page + " not found.");
        break;
    }
  }

  void buttonClick4() {
    switch(this.page) {
      case MAPS:
      case AREAS:
      case LEVELS:
        if (MapEditorInterface.this.curr_map != null) {
          MapEditorInterface.this.curr_map.close();
          MapEditorInterface.this.curr_map = null;
        }
        if (MapEditorInterface.this.curr_level != null) {
          MapEditorInterface.this.curr_level.close();
          MapEditorInterface.this.curr_level = null;
        }
        p.global.state = ProgramState.ENTERING_MAINMENU;
        break;
      default:
        this.form = new GoToMainMenuForm(p);
        break;
    }
  }

  void buttonClick5() {
    switch(this.page) {
      case MAPS:
        this.form = new HelpForm(p, LNZ.help_mapEditor_maps);
        break;
      case AREAS:
        this.form = new HelpForm(p, LNZ.help_mapEditor_areas);
        break;
      case LEVELS:
        this.form = new HelpForm(p, LNZ.help_mapEditor_levels);
        break;
      case TERRAIN:
        this.form = new HelpForm(p, LNZ.help_mapEditor_terrain);
        break;
      case FEATURES:
        this.form = new HelpForm(p, LNZ.help_mapEditor_features);
        break;
      case UNITS:
        this.form = new HelpForm(p, LNZ.help_mapEditor_units);
        break;
      case ITEMS:
        this.form = new HelpForm(p, LNZ.help_mapEditor_items);
        break;
      case TESTMAP:
      case OPENING_MAPEDITOR:
      case CREATING_MAP:
      case OPENING_TESTMAP:
      case OPENING_TESTLEVEL:
      case TESTLEVEL:
      case EDITING_AREA:
      case TESTING_AREA:
        break;
      case LEVEL_INFO:
        this.form = new HelpForm(p, LNZ.help_mapEditor_levelInfo);
        break;
      case LEVEL_MAPS:
        this.form = new HelpForm(p, LNZ.help_mapEditor_levelMaps);
        break;
      case LINKERS:
        this.form = new HelpForm(p, LNZ.help_mapEditor_linkers);
        break;
      case TRIGGERS:
        this.form = new HelpForm(p, LNZ.help_mapEditor_triggers);
        break;
      case TRIGGER_EDITOR:
        this.form = new HelpForm(p, LNZ.help_mapEditor_triggerEditor);
        break;
      case CONDITION_EDITOR:
        this.form = new HelpForm(p, LNZ.help_mapEditor_conditionEditor);
        break;
      case EFFECT_EDITOR:
        this.form = new HelpForm(p, LNZ.help_mapEditor_effectEditor);
        break;
      default:
        p.global.errorMessage("ERROR: MapEditorPage " + this.page + " not found.");
        break;
    }
  }


  void testMap() {
    String mapName = this.listBox1.highlightedLine();
    if (mapName == null) {
      this.form = new MessageForm(p, "Test Map", "No map selected to test.");
    }
    else {
      this.navigate(MapEditorPage.OPENING_TESTMAP);
      this.open_testMap_thread = new OpenTestMapThread(p, mapName);
      this.open_testMap_thread.start();
    }
  }

  void deleteMap() {
    String mapName = this.listBox1.highlightedLine();
    if (mapName == null) {
      this.form = new MessageForm(p, "Delete Map", "No map selected to delete.");
    }
    else {
      this.form = new DeleteMapForm(p, mapName);
      this.listBox1.setMapsText();
    }
  }

  void setCurrMap(GameMapEditor map) {
    this.curr_map = map;
    this.curr_map.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
    this.navigate(MapEditorPage.TERRAIN);
  }

  void renameMapFile(String mapName, String targetName) {
    if (!FileSystem.entryExists(p, "data/maps/" + mapName + ".map.lnz")) {
      p.global.errorMessage("ERROR: Can't rename map file that doesn't exist.");
      return;
    }
    if (FileSystem.entryExists(p, "data/maps/" + targetName + ".map.lnz")) {
      return; // name exists
    }
    GameMap map = new GameMap(p);
    map.mapName = mapName;
    String[] lines = map.open1File("data/maps/");
    PrintWriter mapFile = p.createWriter("data/maps/" + targetName + ".map.lnz");
    for (String line : lines) {
      if (PApplet.trim(PApplet.split(line, ':')[0]).equals("mapName")) {
        line = "mapName: " + targetName;
      }
      mapFile.println(line);
    }
    mapFile.flush();
    mapFile.close();
    FileSystem.deleteFile(p, "data/maps/" + mapName + ".map.lnz");
    this.listBox1.setMapsText();
  }

  void openMapEditor(String mapName) {
    this.navigate(MapEditorPage.OPENING_MAPEDITOR);
    this.open_mapEditor_thread = new OpenMapEditorThread(p, mapName, p.sketchPath("data/maps/"));
    this.open_mapEditor_thread.start();
  }

  void saveMapEditor() {
    if (this.curr_map != null) {
      this.curr_map.save(p.sketchPath("data/maps/"));
    }
    //this.closeMapEditor();
  }

  void closeMapEditor() {
    if (this.curr_map != null) {
      this.curr_map.close();
      this.curr_map = null;
    }
    this.navigate(MapEditorPage.MAPS);
  }

  void saveMapTester() {
    if (this.curr_level != null) {
      if (this.curr_level.curr_map != null) {
        this.curr_level.curr_map.save(p.sketchPath("data/maps/"));
      }
    }
    this.closeMapTester();
  }

  void closeMapTester() {
    if (this.curr_level != null) {
      this.curr_level.close();
      this.curr_level = null;
    }
    p.global.viewing_ender_chest = false;
    this.navigate(MapEditorPage.MAPS);
  }


  void createArea(String area_name) {
    if (area_name == null || area_name.equals("")) {
      return;
    }
    if (FileSystem.fileExists(p, "data/areas/" + area_name + ".area.lnz")) {
      return;
    }
    FileSystem.deleteFolder(p, "data/areas/temp");
    this.curr_area = new GameMapAreaEditor(p, area_name, "data/areas/temp");
    this.curr_area.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
    this.curr_area.initializeArea();
    this.curr_area.save(p.sketchPath("data/areas/"));
    this.navigate(MapEditorPage.EDITING_AREA);
  }

  void inputAreaSeed() {
    this.form = new ChooseSeedForm(p);
  }
  void randomAreaSeed() {
    this.specificAreaSeed((int)PApplet.map((float)Math.random(), 0, 1, 0, Integer.MAX_VALUE - 1));
  }
  void specificAreaSeed(int new_seed) {
    if (this.curr_area == null || this.page != MapEditorPage.EDITING_AREA) {
      return;
    }
    boolean old_draw_grid = this.curr_area.draw_grid;
    boolean old_draw_fog = this.curr_area.draw_fog;
    double old_zoom = this.curr_area.zoom;
    this.curr_area.seed = new_seed;
    this.curr_area.next_feature_key = 0;
    String area_name = this.curr_area.mapName;
    this.saveAreaTester();
    this.openAreaEditor(area_name);
    this.curr_area.draw_grid = old_draw_grid;
    this.curr_area.draw_fog = old_draw_fog;
    this.curr_area.setZoom(old_zoom);
    this.curr_area.viewDefaultChunk();
    this.curr_area.addHeaderMessage("Now using seed: " + this.curr_area.seed);
  }

  void reloadArea() {
    if (this.curr_area == null || this.page != MapEditorPage.EDITING_AREA) {
      return;
    }
    this.specificAreaSeed(this.curr_area.seed);
  }

  void saveAreaTester() {
    if (this.curr_area == null) {
      return;
    }
    this.curr_area.save("data/areas");
    this.closeAreaTester();
  }

  void closeAreaTester() {
    FileSystem.deleteFolder(p, "data/areas/temp");
    if (this.curr_area != null) {
      this.curr_area.close();
      this.curr_area = null;
    }
    if (this.curr_level != null) {
      this.curr_level.close();
      this.curr_level = null;
    }
    this.navigate(MapEditorPage.AREAS);
  }

  void openAreaEditor(String area_name) {
    if (area_name == null || area_name.equals("")) {
      return;
    }
    if (!FileSystem.fileExists(p, "data/areas/" + area_name + ".area.lnz")) {
      return;
    }
    FileSystem.deleteFolder(p, "data/areas/temp");
    this.curr_area = new GameMapAreaEditor(p, area_name, "data/areas/temp");
    this.curr_area.open("data/areas");
    this.curr_area.viewDefaultChunk();
    this.curr_area.initializeArea();
    this.curr_area.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
    this.navigate(MapEditorPage.EDITING_AREA);
  }

  void testArea(String area_name) {
    if (area_name == null || area_name.equals("")) {
      return;
    }
    if (!FileSystem.fileExists(p, "data/areas/" + area_name + ".area.lnz")) {
      return;
    }
    FileSystem.deleteFolder(p, "data/areas/temp");
    GameMapArea area = new GameMapArea(p, area_name, "data/areas/temp");
    area.open("data/areas");
    area.initializeArea();
    this.curr_level = new Level(p, area);
    this.curr_level.location = Location.TEST_AREA;
    this.curr_level.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
    this.form = new LevelHeroSelectorForm(p, this.curr_level);
    this.curr_level.restartTimers();
    this.navigate(MapEditorPage.TESTING_AREA);
  }

  void deleteArea() {
    String area_name = this.listBox1.highlightedLine();
    if (area_name == null) {
      this.form = new MessageForm(p, "Delete Area", "No area selected to delete.");
    }
    else {
      this.form = new DeleteAreaForm(p, area_name);
    }
  }

  void renameAreaFile(String area_name, String target_name) {
    if (!FileSystem.entryExists(p, "data/areas/" + area_name + ".area.lnz")) {
      p.global.errorMessage("ERROR: Can't rename area file that doesn't exist.");
      return;
    }
    if (FileSystem.entryExists(p, "data/areas/" + target_name + ".area.lnz")) {
      return;
    }
    GameMapAreaEditor map = new GameMapAreaEditor(p, area_name, "data/areas/temp");
    map.mapName = area_name;
    String[] lines = map.open1File("data/areas/");
    PrintWriter mapFile = p.createWriter("data/areas/" + target_name + ".area.lnz");
    for (String line : lines) {
      if (PApplet.trim(PApplet.split(line, ':')[0]).equals("mapName")) {
        line = "mapName: " + target_name;
      }
      mapFile.println(line);
    }
    mapFile.flush();
    mapFile.close();
    FileSystem.deleteFile(p, "data/areas/" + area_name + ".area.lnz");
  }


  void newLevel(String levelName) {
    Level new_level = new LevelEditor(p);
    new_level.folderPath = "data/levels/";
    new_level.levelName = levelName;
    new_level.save();
    this.curr_level = new_level;
    this.curr_level.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
    this.navigate(MapEditorPage.LEVEL_INFO);
  }

  void testLevel() {
    String levelName = this.listBox1.highlightedLine();
    if (levelName == null) {
      this.form = new MessageForm(p, "Test Level", "No level selected to test.");
      return;
    }
    this.navigate(MapEditorPage.OPENING_TESTLEVEL);
    this.open_testLevel_thread = new OpenTestLevelThread(levelName);
    this.open_testLevel_thread.start();
  }

  void deleteLevel() {
    String levelName = this.listBox1.highlightedLine();
    if (levelName == null) {
      this.form = new MessageForm(p, "Delete Level", "No level selected to delete.");
    }
    else {
      this.form = new DeleteLevelForm(p, levelName);
    }
  }

  void renameLevelFolder(String levelName, String targetName) {
    if (levelName.equals(targetName)) {
      return;
    }
    if (!FileSystem.entryExists(p, "data/levels/" + levelName)) {
      p.global.errorMessage("ERROR: Can't rename level that doesn't exist.");
      return;
    }
    if (FileSystem.entryExists(p, "data/levels/" + targetName)) {
      p.global.errorMessage("ERROR: Can't rename level to a name that already exists.");
      return;
    }
    FileSystem.moveFolder(p, "data/levels/" + levelName, "data/levels/" + targetName);
    Level level = new Level(p);
    level.folderPath = "data/levels/";
    level.levelName = targetName;
    String[] lines = level.open1File();
    PrintWriter levelFile = p.createWriter(level.finalFolderPath() + "/level.lnz");
    for (String line : lines) {
      if (PApplet.trim(PApplet.split(line, ':')[0]).equals("levelName")) {
        line = "levelName: " + targetName;
      }
      levelFile.println(line);
    }
    levelFile.flush();
    levelFile.close();
  }

  void openLevelEditor(String levelName) {
    this.curr_level = new LevelEditor(p, "data/levels", levelName);
    if (this.curr_level.nullify) {
      this.curr_level.close();
      this.curr_level = null;
      this.navigate(MapEditorPage.LEVELS);
    }
    else {
      this.curr_level.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
      this.navigate(MapEditorPage.LEVEL_INFO);
    }
  }

  void addMapToLevel(String mapName) {
    if (mapName == null) {
      this.form = new MessageForm(p, "Add Map", "No map selected to add to level.");
      return;
    }
    if (!FileSystem.fileExists(p, "data/maps/" + mapName + ".map.lnz")) {
      this.form = new MessageForm(p, "Add Map", "Map is missing from maps folder:\n" + mapName);
      return;
    }
    if (this.curr_level == null) {
      this.form = new MessageForm(p, "Add Map", "No current level to add map to.");
      return;
    }
    if (this.curr_level.hasMap(mapName)) {
      return;
    }
    FileSystem.copyFile(p, "data/maps/" + mapName + ".map.lnz", "data/levels/" +
      this.curr_level.folderName() + "/" + mapName + ".map.lnz");
    this.curr_level.mapNames.add(mapName);
    this.curr_level.save(false);
    this.listBox2.refresh();
  }

  void removeMapFromLevel(String mapName) {
    if (mapName == null) {
      this.form = new MessageForm(p, "Remove Map", "No map selected to remove from level.");
      return;
    }
    if (!FileSystem.fileExists(p, "data/levels/" + this.curr_level.folderName() + "/" + mapName + ".map.lnz")) {
      this.form = new MessageForm(p, "Remove Map", "Map is missing from level folder:\n" + mapName);
      return;
    }
    if (this.curr_level == null) {
      this.form = new MessageForm(p, "Remove Map", "No current level to remove map from.");
      return;
    }
    if (!this.curr_level.hasMap(mapName)) {
      return;
    }
    FileSystem.deleteFile(p, "data/levels/" + this.curr_level.folderName() + "/" + mapName + ".map.lnz");
    this.curr_level.removeMap(mapName);
    this.curr_level.save(false);
    this.listBox2.refresh();
  }

  void saveLevelEditor() {
    if (this.curr_level != null) {
      this.curr_level.save();
    }
  }

  void closeLevelEditor() {
    if (this.curr_level != null) {
      this.curr_level.close();
      this.curr_level = null;
    }
    this.navigate(MapEditorPage.LEVELS);
  }

  void saveLevelTester() {
    if (this.curr_level != null) {
      this.curr_level.folderPath = "data/levels";
      this.curr_level.save();
    }
    this.closeLevelTester();
  }

  void closeLevelTester() {
    if (this.curr_level != null) {
      this.curr_level.close();
      this.curr_level = null;
    }
    p.global.viewing_ender_chest = false;
    FileSystem.deleteFolder(p, "data/profiles/" + p.global.profile.display_name.toLowerCase() + "/leveltester");
    this.navigate(MapEditorPage.LEVELS);
  }

  void openLinkEditorForm(int linker_index) {
    if (this.curr_level == null) {
      return;
    }
    if (linker_index < 0 || linker_index >= this.curr_level.linkers.size()) {
      return;
    }
    this.form = new LinkerEditorForm(p, this.curr_level.linkers.get(linker_index));
  }

  void openTriggerEditor(int triggerKey) {
    if (this.curr_level == null) {
      return;
    }
    if (!this.curr_level.triggers.containsKey(triggerKey)) {
      return;
    }
    this.curr_trigger = this.curr_level.triggers.get(triggerKey);
    this.levelForm = new TriggerEditorForm(p, this.curr_trigger,
      p.width - this.rightPanel.size_curr + LNZ.mapEditor_listBoxGap,
      p.width - LNZ.mapEditor_listBoxGap);
    this.navigate(MapEditorPage.TRIGGER_EDITOR);
  }

  void openConditionEditor(int conditionIndex) {
    if (this.curr_trigger == null) {
      return;
    }
    if (conditionIndex < 0 || conditionIndex >= this.curr_trigger.conditions.size()) {
      return;
    }
    if (conditionIndex == this.curr_trigger_component && ConditionEditorForm.class.isInstance(this.levelForm)) {
      this.curr_trigger_component = -1;
      this.levelForm = new TriggerEditorForm(p, this.curr_trigger,
        p.width - this.rightPanel.size_curr + LNZ.mapEditor_listBoxGap,
          p.width - LNZ.mapEditor_listBoxGap);
      this.navigate(MapEditorPage.TRIGGER_EDITOR);
      return;
    }
    this.curr_trigger_component = conditionIndex;
    this.levelForm = new ConditionEditorForm(p, this.curr_trigger.conditions.get(
      conditionIndex), p.width - this.rightPanel.size_curr + LNZ.mapEditor_listBoxGap,
      p.width - LNZ.mapEditor_listBoxGap);
      this.navigate(MapEditorPage.CONDITION_EDITOR);
  }

  void openEffectEditor(int effectIndex) {
    if (this.curr_trigger == null) {
      return;
    }
    if (effectIndex < 0 || effectIndex >= this.curr_trigger.effects.size()) {
      return;
    }
    if (effectIndex == this.curr_trigger_component && EffectEditorForm.class.isInstance(this.levelForm)) {
      this.curr_trigger_component = -1;
      this.levelForm = new TriggerEditorForm(p, this.curr_trigger,
        p.width - this.rightPanel.size_curr + LNZ.mapEditor_listBoxGap,
          p.width - LNZ.mapEditor_listBoxGap);
      this.navigate(MapEditorPage.TRIGGER_EDITOR);
      return;
    }
    this.curr_trigger_component = effectIndex;
    this.levelForm = new EffectEditorForm(p, this.curr_trigger.effects.get(
      effectIndex), p.width - this.rightPanel.size_curr + LNZ.mapEditor_listBoxGap,
      p.width - LNZ.mapEditor_listBoxGap);
      this.navigate(MapEditorPage.EFFECT_EDITOR);
  }

  void addRectangleToCondition(ConditionEditorForm form) {
    if (this.curr_level == null) {
      return;
    }
    if (!LevelEditor.class.isInstance(this.curr_level)) {
      return;
    }
    Rectangle rect = ((LevelEditor)this.curr_level).getCurrentRectangle();
    if (rect == null) {
      return;
    }
    form.condition.rectangle = rect;
  }

  void addRectangleToEffect(EffectEditorForm form) {
    if (this.curr_level == null) {
      return;
    }
    if (!LevelEditor.class.isInstance(this.curr_level)) {
      return;
    }
    Rectangle rect = ((LevelEditor)this.curr_level).getCurrentRectangle();
    if (rect == null) {
      return;
    }
    form.effect.rectangle = rect;
  }


  void dropTerrain(String line) {
    if (this.curr_map == null) {
      return;
    }
    String[] line_split = PApplet.split(line, ':');
    if (line_split.length < 2) {
      return;
    }
    String terrainID = PApplet.trim(line_split[1]);
    if (Misc.isInt(terrainID)) {
      this.curr_map.dropTerrain(Misc.toInt(terrainID));
    }
  }

  void dropFeature(String line) {
    if (this.curr_map == null) {
      return;
    }
    String[] line_split = PApplet.split(line, ':');
    if (line_split.length < 2) {
      return;
    }
    String featureID = PApplet.trim(line_split[1]);
    if (Misc.isInt(featureID)) {
      this.curr_map.dropping_object = new Feature(p, Misc.toInt(featureID));
      this.curr_map.dropping_terrain = null;
    }
  }

  void dropUnit(String line) {
    if (this.curr_map == null) {
      return;
    }
    String[] line_split = PApplet.split(line, ':');
    if (line_split.length < 2) {
      return;
    }
    String unitID = PApplet.trim(line_split[1]);
    if (Misc.isInt(unitID)) {
      this.curr_map.dropping_object = new Unit(p, Misc.toInt(unitID));
      this.curr_map.dropping_terrain = null;
    }
  }

  void dropItem(String line) {
    if (this.curr_map == null) {
      return;
    }
    String[] line_split = PApplet.split(line, ':');
    if (line_split.length < 2) {
      return;
    }
    String itemID = PApplet.trim(line_split[1]);
    if (Misc.isInt(itemID)) {
      this.curr_map.dropping_object = new Item(p, Misc.toInt(itemID));
      this.curr_map.dropping_terrain = null;
    }
  }

  void addLinkerToLevel() {
    if (this.curr_level == null) {
      return;
    }
    if (!LevelEditor.class.isInstance(this.curr_level)) {
      return;
    }
    ((LevelEditor)this.curr_level).newLinker();
    this.listBox2.refresh();
    this.listBox2.scrollBottom();
  }

  void removeLinkerFromLevel(int linker_index) {
    if (this.curr_level == null) {
      return;
    }
    if (!LevelEditor.class.isInstance(this.curr_level)) {
      return;
    }
    ((LevelEditor)this.curr_level).removeLinker(linker_index);
    this.listBox2.refresh();
  }

  void addTriggerToLevel() {
    if (this.curr_level == null) {
      return;
    }
    if (!LevelEditor.class.isInstance(this.curr_level)) {
      return;
    }
    ((LevelEditor)this.curr_level).newTrigger();
    this.listBox2.refresh();
    this.listBox2.scrollBottom();
  }

  void removeTriggerFromLevel(int trigger_key) {
    if (this.curr_level == null) {
      return;
    }
    if (!LevelEditor.class.isInstance(this.curr_level)) {
      return;
    }
    ((LevelEditor)this.curr_level).removeTrigger(trigger_key);
    this.listBox2.refresh();
  }

  void addConditionToTrigger() {
    if (this.curr_trigger == null) {
      return;
    }
    this.curr_trigger.conditions.add(new Condition(p));
    this.listBox2.refresh();
  }

  void addEffectToTrigger() {
    if (this.curr_trigger == null) {
      return;
    }
    this.curr_trigger.effects.add(new Effect(p));
    this.listBox2.refresh();
  }

  void removeConditionFromTrigger(int index) {
    if (this.curr_trigger == null) {
      return;
    }
    if (index < 0 || index >= this.curr_trigger.conditions.size()) {
      return;
    }
    this.curr_trigger.conditions.remove(index);
    this.listBox2.refresh();
  }

  void removeEffectFromTrigger(int index) {
    if (this.curr_trigger == null) {
      return;
    }
    if (index < 0 || index >= this.curr_trigger.effects.size()) {
      return;
    }
    this.curr_trigger.effects.remove(index);
    this.listBox2.refresh();
  }


  Hero getCurrentHeroIfExists() {
    if (this.curr_level != null) {
      return this.curr_level.player;
    }
    return null;
  }


  void exitToMainMenu() {
    if (this.curr_map != null) {
      this.curr_map.close();
      this.curr_map = null;
    }
    if (curr_level != null) {
      this.curr_level.close();
      this.curr_level = null;
    }
    p.global.state = ProgramState.ENTERING_MAINMENU;
  }

  void saveAndExitToMainMenu() {
    switch(this.page) {
      case TERRAIN:
      case FEATURES:
      case UNITS:
      case ITEMS:
        this.saveMapEditor();
        break;
      case TESTMAP:
        this.saveMapTester();
        break;
      case TESTLEVEL:
        this.saveLevelTester();
        break;
      case LEVEL_INFO:
      case LEVEL_MAPS:
      case LINKERS:
      case TRIGGERS:
      case TRIGGER_EDITOR:
      case CONDITION_EDITOR:
      case EFFECT_EDITOR:
        this.saveLevelEditor();
        break;
      default:
        break;
    }
    this.exitToMainMenu();
  }


  void update(int millis) {
    boolean refreshMapLocation = false;
    switch(this.page) {
      case CREATING_MAP:
        if (this.create_map_thread.isAlive()) {
          p.fill(p.global.color_mapBorder);
          p.noStroke();
          p.rectMode(PConstants.CORNERS);
          p.rect(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
          p.fill(p.global.color_loadingScreenBackground);
          p.rect(this.leftPanel.size + LNZ.map_borderSize, LNZ.map_borderSize,
              p.width - this.rightPanel.size - LNZ.map_borderSize, p.height - LNZ.map_borderSize);
          p.fill(0);
          p.textSize(24);
          p.textAlign(PConstants.LEFT, PConstants.TOP);
          p.text(this.create_map_thread.curr_status + " ...", this.leftPanel.size +
            LNZ.map_borderSize + 30, LNZ.map_borderSize + 30);
          p.imageMode(PConstants.CENTER);
          int frame = (int)(LNZ.gif_loading_frames * (double)(millis %
            LNZ.gif_loading_time) / (1 + LNZ.gif_loading_time));
          p.image(p.global.images.getImage("gifs/loading/" + frame + ".png"), 0.5 * p.width, 0.5 * p.height, 250, 250);
        }
        else {
          if (this.create_map_thread.map_creating.nullify) {
            this.create_map_thread.map_creating.close();
            this.create_map_thread.map_creating = null;
            this.navigate(MapEditorPage.MAPS);
          }
          else {
            this.curr_map = this.create_map_thread.map_creating;
            this.curr_map.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
            this.listBox1.setMapsText();
            this.navigate(MapEditorPage.TERRAIN);
          }
          this.create_map_thread = null;
          return;
        }
        break;
      case OPENING_MAPEDITOR:
        if (this.open_mapEditor_thread.isAlive()) {
          p.fill(p.global.color_mapBorder);
          p.noStroke();
          p.rectMode(PConstants.CORNERS);
          p.rect(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
          p.fill(p.global.color_loadingScreenBackground);
          p.rect(this.leftPanel.size + LNZ.map_borderSize, LNZ.map_borderSize,
              p.width - this.rightPanel.size - LNZ.map_borderSize, p.height - LNZ.map_borderSize);
          p.fill(0);
          p.textSize(24);
          p.textAlign(PConstants.LEFT, PConstants.TOP);
          p.text(this.open_mapEditor_thread.curr_status + " ...", this.leftPanel.size +
            LNZ.map_borderSize + 30, LNZ.map_borderSize + 30);
          p.imageMode(PConstants.CENTER);
          int frame = (int)(LNZ.gif_loading_frames * (float)(millis %
            LNZ.gif_loading_time) / (1 + LNZ.gif_loading_time));
          p.image(p.global.images.getImage("gifs/loading/" + frame + ".png"), 0.5 * p.width, 0.5 * p.height, 250, 250);
        }
        else {
          if (this.open_mapEditor_thread.map_opening.nullify) {
            this.open_mapEditor_thread.map_opening.close();
            this.open_mapEditor_thread.map_opening = null;
            this.navigate(MapEditorPage.MAPS);
          }
          else {
            this.curr_map = this.open_mapEditor_thread.map_opening;
            this.curr_map.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
            this.navigate(MapEditorPage.TERRAIN);
          }
          this.open_mapEditor_thread = null;
          return;
        }
        break;
      case OPENING_TESTMAP:
        if (this.open_testMap_thread.isAlive()) {
          p.fill(p.global.color_mapBorder);
          p.noStroke();
          p.rectMode(PConstants.CORNERS);
          p.rect(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
          p.fill(p.global.color_loadingScreenBackground);
          p.rect(this.leftPanel.size + LNZ.map_borderSize, LNZ.map_borderSize,
              p.width - this.rightPanel.size - LNZ.map_borderSize, p.height - LNZ.map_borderSize);
          p.fill(0);
          p.textSize(24);
          p.textAlign(PConstants.LEFT, PConstants.TOP);
          p.text(this.open_testMap_thread.curr_status + " ...", this.leftPanel.size +
            LNZ.map_borderSize + 30, LNZ.map_borderSize + 30);
          p.imageMode(PConstants.CENTER);
          int frame = (int)(LNZ.gif_loading_frames * (float)(millis %
            LNZ.gif_loading_time) / (1 + LNZ.gif_loading_time));
          p.image(p.global.images.getImage("gifs/loading/" + frame + ".png"), 0.5 * p.width, 0.5 * p.height, 250, 250);
        }
        else {
          if (this.open_testMap_thread.level_opening == null || this.open_testMap_thread.level_opening.nullify) {
            if (this.open_testMap_thread.level_opening != null) {
              this.open_testMap_thread.level_opening.close();
              this.open_testMap_thread.level_opening = null;
            }
            this.navigate(MapEditorPage.MAPS);
          }
          else {
            this.curr_level = this.open_testMap_thread.level_opening;
            this.curr_level.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
            this.navigate(MapEditorPage.TESTMAP);
            this.form = new HeroSelectorForm(p, this.curr_level);
            this.curr_level.restartTimers();
          }
          this.open_testMap_thread = null;
          return;
        }
        break;
      case OPENING_TESTLEVEL:
        if (this.open_testLevel_thread.isAlive()) {
          p.fill(p.global.color_mapBorder);
          p.noStroke();
          p.rectMode(PConstants.CORNERS);
          p.rect(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
          p.fill(p.global.color_loadingScreenBackground);
          p.rect(this.leftPanel.size + LNZ.map_borderSize, LNZ.map_borderSize,
              p.width - this.rightPanel.size - LNZ.map_borderSize, p.height - LNZ.map_borderSize);
          p.fill(0);
          p.textSize(24);
          p.textAlign(PConstants.LEFT, PConstants.TOP);
          p.text(this.open_testLevel_thread.curr_status + " ...", this.leftPanel.size +
            LNZ.map_borderSize + 30, LNZ.map_borderSize + 30);
          p.imageMode(PConstants.CENTER);
          int frame = (int)(LNZ.gif_loading_frames * (float)(millis %
            LNZ.gif_loading_time) / (1 + LNZ.gif_loading_time));
          p.image(p.global.images.getImage("gifs/loading/" + frame + ".png"), 0.5 * p.width, 0.5 * p.height, 250, 250);
        }
        else {
          if (this.open_testLevel_thread.level_opening == null || this.open_testLevel_thread.level_opening.nullify) {
            if (this.open_testLevel_thread.level_opening != null) {
              this.open_testLevel_thread.level_opening.close();
              this.open_testLevel_thread.level_opening = null;
            }
            this.navigate(MapEditorPage.LEVELS);
          }
          else {
            this.curr_level = this.open_testLevel_thread.level_opening;
            this.curr_level.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
            this.navigate(MapEditorPage.TESTLEVEL);
            this.form = new LevelHeroSelectorForm(p, this.curr_level);
            this.curr_level.restartTimers();
          }
          this.open_testLevel_thread = null;
          return;
        }
        break;
      default:
        if (this.curr_level != null) {
          this.curr_level.update(millis);
          if (this.leftPanel.collapsing || this.rightPanel.collapsing) {
            refreshMapLocation = true;
          }
        }
        else if (this.curr_map != null) {
          this.curr_map.update(millis);
          if (this.leftPanel.collapsing || this.rightPanel.collapsing) {
            refreshMapLocation = true;
          }
        }
        else if (this.curr_area != null) {
          this.curr_area.update(millis);
          if (this.leftPanel.collapsing || this.rightPanel.collapsing) {
            refreshMapLocation = true;
          }
        }
        else {
          p.rectMode(PConstants.CORNERS);
          p.noStroke();
          p.fill(DImg.ccolor(60));
          p.rect(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
        }
        break;
    }
    this.leftPanel.update(millis);
    this.rightPanel.update(millis);
    if (this.rightPanel.open && !this.rightPanel.collapsing) {
      for (MapEditorButton button : this.buttons) {
        button.update(millis);
      }
      if (this.listBox1.active) {
        this.listBox1.update(millis);
      }
      if (this.listBox2.active) {
        this.listBox2.update(millis);
      }
      if (this.areaForm != null) {
        this.areaForm.update(millis);
      }
      if (this.levelForm != null) {
        this.levelForm.update(millis);
      }
      if (this.curr_level != null) {
        switch(this.page) {
          case TESTMAP:
          case TESTLEVEL:
            this.curr_level.drawRightPanel(millis);
            break;
          default:
            break;
        }
      }
    }
    if (this.leftPanel.open && !this.leftPanel.collapsing) {
      if (this.curr_level != null) {
        this.curr_level.drawLeftPanel(millis);
      }
      else if (this.curr_map != null) {
        this.curr_map.drawLeftPanel(millis);
      }
      else if (this.curr_area != null) {
        this.curr_area.drawLeftPanel(millis);
      }
    }
    if (refreshMapLocation) {
      if (this.curr_level != null) {
        this.curr_level.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
      }
      else if (this.curr_map != null) {
        this.curr_map.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
      }
      else if (this.curr_area != null) {
        this.curr_area.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
      }
    }
  }

  void showNerdStats() {
    if (this.curr_level != null) {
      this.curr_level.displayNerdStats();
    }
    else if (this.curr_map != null) {
      this.curr_map.displayNerdStats();
    }
    else if (this.curr_area != null) {
      this.curr_area.displayNerdStats();
    }
    else {
      this.showDefaultNerdStats(DImg.ccolor(255));
    }
  }

  void mouseMove(float mX, float mY) {
    boolean refreshMapLocation = false;
    // map / level mouse move
    if (this.curr_level != null) {
      this.curr_level.mouseMove(mX, mY);
      if (this.leftPanel.clicked || this.rightPanel.clicked) {
        refreshMapLocation = true;
      }
    }
    else if (this.curr_map != null) {
      this.curr_map.mouseMove(mX, mY);
      if (this.leftPanel.clicked || this.rightPanel.clicked) {
        refreshMapLocation = true;
      }
    }
    else if (this.curr_area != null) {
      this.curr_area.mouseMove(mX, mY);
      if (this.leftPanel.clicked || this.rightPanel.clicked) {
        refreshMapLocation = true;
      }
    }
    // left panel mouse move
    this.leftPanel.mouseMove(mX, mY);
    if (this.leftPanel.open && !this.leftPanel.collapsing) {
      if (this.curr_level != null) {
        if (this.curr_level.leftPanelElementsHovered()) {
          this.leftPanel.hovered = false;
        }
      }
      else if (this.curr_map != null) {
        if (this.curr_map.leftPanelElementsHovered()) {
          this.leftPanel.hovered = false;
        }
      }
      else if (this.curr_area != null) {
        if (this.curr_area.leftPanelElementsHovered()) {
          this.leftPanel.hovered = false;
        }
      }
    }
    // right panel mouse move
    this.rightPanel.mouseMove(mX, mY);
    if (this.rightPanel.open && !this.rightPanel.collapsing) {
      for (MapEditorButton button : this.buttons) {
        button.mouseMove(mX, mY);
      }
      if (this.listBox1.active) {
        this.listBox1.mouseMove(mX, mY);
        if (this.listBox1.rightClickMenu != null && this.listBox1.rightClickMenu.hovered) {
          this.rightPanel.hovered = false;
        }
      }
      if (this.listBox2.active) {
        this.listBox2.mouseMove(mX, mY);
      }
      if (this.areaForm != null) {
        this.areaForm.mouseMove(mX, mY);
      }
      if (this.levelForm != null) {
        this.levelForm.mouseMove(mX, mY);
      }
    }
    // refresh map location
    if (refreshMapLocation) {
      if (this.curr_level != null) {
        this.curr_level.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
      }
      else if (this.curr_map != null) {
        this.curr_map.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
      }
      else if (this.curr_area != null) {
        this.curr_area.setLocation(this.leftPanel.size, 0, p.width - this.rightPanel.size, p.height);
      }
    }
    // cursor icon resolution
    if (this.leftPanel.clicked || this.rightPanel.clicked) {
      this.resizeButtons();
      p.global.setCursor("icons/cursor_resizeh_white.png");
    }
    else if (this.leftPanel.hovered || this.rightPanel.hovered) {
      p.global.setCursor("icons/cursor_resizeh.png");
    }
    else {
      p.global.defaultCursor("icons/cursor_resizeh_white.png", "icons/cursor_resizeh.png");
    }
  }

  void mousePress() {
    if (this.curr_level != null) {
      this.curr_level.mousePress();
    }
    else if (this.curr_map != null) {
      this.curr_map.mousePress();
    }
    else if (this.curr_area != null) {
      this.curr_area.mousePress();
    }
    this.leftPanel.mousePress();
    this.rightPanel.mousePress();
    if (this.leftPanel.clicked || this.rightPanel.clicked) {
      p.global.setCursor("icons/cursor_resizeh_white.png");
    }
    else {
      p.global.defaultCursor("icons/cursor_resizeh_white.png");
    }
    if (this.rightPanel.open && !this.rightPanel.collapsing) {
      for (MapEditorButton button : this.buttons) {
        button.mousePress();
      }
      if (this.listBox1.active) {
        this.listBox1.mousePress();
      }
      if (this.listBox2.active) {
        this.listBox2.mousePress();
      }
      if (this.areaForm != null) {
        this.areaForm.mousePress();
      }
      if (this.levelForm != null) {
        this.levelForm.mousePress();
      }
    }
  }

  void mouseRelease(float mX, float mY) {
    if (this.curr_level != null) {
      this.curr_level.mouseRelease(mX, mY);
    }
    else if (this.curr_map != null) {
      this.curr_map.mouseRelease(mX, mY);
    }
    else if (this.curr_area != null) {
      this.curr_area.mouseRelease(mX, mY);
    }
    this.leftPanel.mouseRelease(mX, mY);
    this.rightPanel.mouseRelease(mX, mY);
    if (this.leftPanel.hovered || this.rightPanel.hovered) {
      p.global.setCursor("icons/cursor_resizeh.png");
    }
    else {
      p.global.defaultCursor("icons/cursor_resizeh.png", "icons/cursor_resizeh_white.png");
    }
    if (this.rightPanel.open && !this.rightPanel.collapsing) {
      for (MapEditorButton button : this.buttons) {
        button.mouseRelease(mX, mY);
      }
      if (this.listBox1.active) {
        this.listBox1.mouseRelease(mX, mY);
      }
      if (this.listBox2.active) {
        this.listBox2.mouseRelease(mX, mY);
      }
      if (this.levelForm != null) {
        this.levelForm.mouseRelease(mX, mY);
      }
      if (this.areaForm != null) {
        this.areaForm.mouseRelease(mX, mY);
      }
    }
  }

  void scroll(int amount) {
    if (this.curr_level != null) {
      this.curr_level.scroll(amount);
    }
    else if (this.curr_map != null) {
      this.curr_map.scroll(amount);
    }
    else if (this.curr_area != null) {
      this.curr_area.scroll(amount);
    }
    if (this.rightPanel.open && !this.rightPanel.collapsing) {
      if (this.listBox1.active) {
        this.listBox1.scroll(amount);
      }
      if (this.listBox2.active) {
        this.listBox2.scroll(amount);
      }
      if (this.levelForm != null) {
        this.levelForm.scroll(amount);
      }
      if (this.areaForm != null) {
        this.areaForm.scroll(amount);
      }
    }
  }

  void keyPress(int key, int keyCode) {
    switch(this.page) {
      case TRIGGER_EDITOR:
      case CONDITION_EDITOR:
      case EFFECT_EDITOR:
        break;
      default:
        if (this.curr_level != null) {
          this.curr_level.keyPress(key, keyCode);
        }
        else if (this.curr_map != null) {
          this.curr_map.keyPress(key, keyCode);
        }
        else if (this.curr_area != null) {
          this.curr_area.keyPress(key, keyCode);
        }
        break;
    }
    if (this.rightPanel.open && !this.rightPanel.collapsing) {
      if (this.listBox1.active) {
        this.listBox1.keyPress(key, keyCode);
      }
      if (this.listBox2.active) {
        this.listBox2.keyPress(key, keyCode);
      }
      if (this.levelForm != null) {
        this.levelForm.keyPress(key, keyCode);
      }
      if (this.areaForm != null) {
        this.areaForm.keyPress(key, keyCode);
      }
    }
    switch(key) {
      case 'z':
      case 'Z':
        if (p.global.holding_ctrl && this.curr_level != null) {
          this.curr_level.time.set(DayCycle.dayTimeStart(DayCycle.DAY));
        }
        break;
      case 'x':
      case 'X':
        if (p.global.holding_ctrl && this.curr_level != null) {
          this.curr_level.time.set(DayCycle.dayTimeStart(DayCycle.NIGHT));
        }
        break;
    }
  }

  void openEscForm() {
    if (this.curr_level != null) {
      if (this.curr_level.was_viewing_hero_tree) {
        return; // don't open esc menu if viewing hero tree
      }
    }
    this.form = new EscForm(p);
  }

  void keyRelease(int key, int keyCode) {
    if (this.curr_level != null) {
      this.curr_level.keyRelease(key, keyCode);
    }
    else if (this.curr_map != null) {
      this.curr_map.keyRelease(key, keyCode);
    }
    else if (this.curr_area != null) {
      this.curr_area.keyRelease(key, keyCode);
    }
    if (this.rightPanel.open && !this.rightPanel.collapsing) {
      if (this.listBox1.active) {
        this.listBox1.keyRelease(key, keyCode);
      }
      if (this.listBox2.active) {
        this.listBox2.keyRelease(key, keyCode);
      }
      if (this.levelForm != null) {
        this.levelForm.keyRelease(key, keyCode);
      }
      if (this.areaForm != null) {
        this.areaForm.keyRelease(key, keyCode);
      }
    }
  }


  void loseFocus() {
    if (this.curr_level != null) {
      this.curr_level.loseFocus();
    }
    else if (this.curr_map != null) {
      this.curr_map.loseFocus();
    }
    else if (this.curr_area != null) {
      this.curr_area.loseFocus();
    }
  }

  void gainFocus() {
    if (this.curr_level != null) {
      this.curr_level.gainFocus();
    }
    else if (this.curr_map != null) {
      this.curr_map.gainFocus();
    }
    else if (this.curr_area != null) {
      this.curr_area.gainFocus();
    }
  }

  void restartTimers() {
    if (this.curr_level != null) {
      this.curr_level.restartTimers();
    }
  }
}
