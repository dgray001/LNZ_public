package LNZModule;

import java.util.*;
import processing.core.*;
import DImg.DImg;
import Form.SpacerFormField;
import Misc.Misc;

abstract class EditMapObjectForm extends FormLNZ {
  EditMapObjectForm(LNZ sketch, MapObject mapObject) {
    super(sketch, 0.5 * (sketch.width - LNZ.mapEditor_formWidth),
      0.5 * (sketch.height - LNZ.mapEditor_formHeight),
      0.5 * (sketch.width + LNZ.mapEditor_formWidth),
      0.5 * (sketch.height + LNZ.mapEditor_formHeight));
    this.setTitleText(mapObject.displayNameEditor());
    this.setTitleSize(18);
    this.addField(new SpacerFormField(sketch, 1));
    this.color_background = DImg.ccolor(160, 220, 220);
    this.color_header = DImg.ccolor(30, 150, 150);
  }

  @Override
  public void update(int millis) {
    super.update(millis);
    this.submitForm();
  }

  public void submit() {
    this.updateObject();
    this.updateForm();
  }

  abstract void updateObject();
  abstract void updateForm();
}


abstract class MapObject {
  protected LNZ p;

  protected int ID = 0;
  protected String display_name = "-- Error --";
  protected String type = "-- Error --";
  protected String description = "";

  protected Coordinate coordinate;

  protected double curr_height = 0; // bottom of object

  protected boolean hovered = false;
  protected boolean in_view = false; // whether object is drawn on screen
  protected boolean remove = false; // GameMap will remove object
  protected boolean removed = false; // GameMap has removed object

  MapObject(LNZ sketch) {
    this(sketch, 0);
  }
  MapObject(LNZ sketch, int ID) {
    this.p = sketch;
    this.ID = ID;
    this.coordinate = new Coordinate(0, 0);
  }

  void setStrings(String display_name, String type, String description) {
    this.display_name = display_name;
    this.type = type;
    this.description = description;
  }

  abstract String displayName();
  String displayNameEditor() {
    return this.displayName();
  }
  abstract String type();
  abstract String description();
  void setDescription(String description) {
    this.description = description;
  }
  abstract String selectedObjectTextboxText();

  abstract void setLocation(double x, double y);
  abstract double xi();
  abstract double yi();
  Coordinate initialCoordinate() {
    return new Coordinate(this.xi(), this.yi());
  }
  abstract double xf();
  abstract double yf();
  Coordinate finalCoordinate() {
    return new Coordinate(this.xf(), this.yf());
  }
  abstract double xCenter();
  abstract double yCenter();
  Coordinate center() {
    return new Coordinate(this.xCenter(), this.yCenter());
  }
  abstract double width();
  abstract double height();
  abstract double xRadius();
  abstract double yRadius();
  abstract double zi();
  abstract double zf();
  abstract double zHalf();

  boolean inMap(int mapXI, int mapYI, int mapXF, int mapYF) {
    if (this.xi() >= mapXI && this.yi() >= mapYI && this.xf() <= mapXF && this.yf() <= mapYF) {
      return true;
    }
    return false;
  }
  boolean inMapX(int mapXI, int mapXF) {
    if (this.xi() >= mapXI && this.xf() <= mapXF) {
      return true;
    }
    return false;
  }
  boolean inMapY(int mapYI, int mapYF) {
    if (this.yi() >= mapYI && this.yf() <= mapYF) {
      return true;
    }
    return false;
  }

  boolean inView(double xStart, double yStart, double xEnd, double yEnd) {
    if (this.xi() >= xStart - LNZ.small_number && this.yi() >= yStart - LNZ.small_number &&
      this.xf() <= xEnd + LNZ.small_number && this.yf() <= yEnd + LNZ.small_number) {
      this.in_view = true;
      return true;
    }
    this.in_view = false;
    return false;
  }

  double distance(MapObject object) {
    return this.distance(object, true);
  }
  double distance(MapObject object, boolean account_for_height) {
    double x_distance = Math.max(0, Math.abs(this.xCenter() - object.xCenter()) - this.xRadius() - object.xRadius());
    double y_distance = Math.max(0, Math.abs(this.yCenter() - object.yCenter()) - this.yRadius() - object.yRadius());
    if (account_for_height) {
      double z_distance = Math.max(
        Math.min(0, Math.abs(this.zi() - object.zf())),
        Math.min(0, Math.abs(object.zi() - this.zf())));
      return Math.sqrt(x_distance * x_distance + y_distance * y_distance + z_distance * z_distance);
    } // TODO: Test for object interactions
    // TODO: Can use to replace current height logic in collision logic
    return Math.sqrt(x_distance * x_distance + y_distance * y_distance);
  }
  double centerDistance(MapObject object) {
    double xDistance = Math.abs(this.xCenter() - object.xCenter());
    double yDistance = Math.abs(this.yCenter() - object.yCenter());
    return Math.sqrt(xDistance * xDistance + yDistance * yDistance);
  }
  double distance(double pointX, double pointY) {
    double xDistance = Math.max(0, Math.abs(this.xCenter() - pointX) - this.xRadius());
    double yDistance = Math.max(0, Math.abs(this.yCenter() - pointY) - this.yRadius());
    return Math.sqrt(xDistance * xDistance + yDistance * yDistance);
  }
  double distance(Coordinate coordinate) {
    double xDistance = Math.max(0, Math.abs(this.xCenter() - coordinate.x) - this.xRadius());
    double yDistance = Math.max(0, Math.abs(this.yCenter() - coordinate.y) - this.yRadius());
    return Math.sqrt(xDistance * xDistance + yDistance * yDistance);
  }
  double distanceFromPoint(double pointX, double pointY) {
    double xDistance = this.xCenter() - pointX;
    double yDistance = this.yCenter() - pointY;
    return Math.sqrt(xDistance * xDistance + yDistance * yDistance);
  }

  boolean touching(MapObject object) {
    if ( ((Math.abs(this.xCenter() - object.xCenter()) - this.xRadius() - object.xRadius()) <= 0) ||
      ((Math.abs(this.yCenter() - object.yCenter()) - this.yRadius() - object.yRadius()) <= 0) ) {
        return true;
    }
    return false;
  }

  // returns arraylist of squares the unit is on
  ArrayList<IntegerCoordinate> getSquaresOn() {
    ArrayList<IntegerCoordinate> squares_on = new ArrayList<IntegerCoordinate>();
    for (int i = (int)Math.floor(this.xi()); i < (int)Math.ceil(this.xf()); i++) {
      for (int j = (int)Math.floor(this.yi()); j < (int)Math.ceil(this.yf()); j++) {
        squares_on.add(new IntegerCoordinate(i, j));
      }
    }
    return squares_on;
  }

  abstract PImage getImage();

  abstract void update(int timeElapsed);

  void mouseMove(Coordinate mc) {
    this.mouseMove(mc.x, mc.y);
  }
  void mouseMove(double mX, double mY) {
    if (mX >= this.xi() && mY >= this.yi() &&
      mX <= this.xf() && mY <= this.yf()) {
      this.hovered = true;
    }
    else {
      this.hovered = false;
    }
  }

  abstract boolean targetable(Unit u);

  abstract String fileString();
  String objectFileString() {
    return "\nlocation: " + this.coordinate.x + ", " + this.coordinate.y + ", " + this.curr_height +
      "\ncurr_height: " + this.curr_height + "\nremove: " + this.remove + "\nremoved: " + this.removed;
  }

  abstract void addData(String datakey, String data);
  boolean addObjectData(String datakey, String data) {
    switch(datakey) {
      case "location":
        String[] locationdata = PApplet.split(data, ',');
        if (locationdata.length < 3) {
          p.global.errorMessage("ERROR: Location data for object too short: " + data + ".");
          return false;
        }
        this.coordinate = new Coordinate(
          Misc.toDouble(PApplet.trim(locationdata[0])),
          Misc.toDouble(PApplet.trim(locationdata[1])));
        this.curr_height = Misc.toInt(PApplet.trim(locationdata[2]));
        return true;
      case "curr_height":
        this.curr_height = Misc.toDouble(data);
        return true;
      case "remove":
        this.remove = Misc.toBoolean(data);
        return true;
      case "removed":
        this.removed = Misc.toBoolean(data);
        return true;
      case "description":
        this.setDescription(data);
        return true;
    }
    return false;
  }
}