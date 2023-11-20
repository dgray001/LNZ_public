package LNZModule;

import processing.core.*;
import Misc.Misc;

class Rectangle {
  private LNZ p;

  protected String mapName = "";
  protected double xi = 0;
  protected double yi = 0;
  protected double xf = 0;
  protected double yf = 0;

  Rectangle(LNZ sketch) {
    this.p = sketch;
  }
  Rectangle(LNZ sketch, String mapName, double xi, double yi, double xf, double yf) {
    this.p = sketch;
    this.mapName = mapName;
    if (xf < xi) {
      this.xi = xf;
      this.xf = xi;
    }
    else {
      this.xi = xi;
      this.xf = xf;
    }
    if (yf < yi) {
      this.yi = yf;
      this.yf = yi;
    }
    else {
      this.yi = yi;
      this.yf = yf;
    }
  }
  Rectangle(String mapName, MapObject object) {
    this.mapName = mapName;
    this.setLocation(object);
  }

  @Override
  public String toString() {
    return this.mapName + ": " + this.xi + ", " + this.yi + ", " + this.xf + ", " + this.yf;
  }

  double centerX() {
    return this.xi + 0.5 * (this.xf - this.xi);
  }
  double centerY() {
    return this.yi + 0.5 * (this.yf - this.yi);
  }
  Coordinate center() {
    return new Coordinate(this.centerX(), this.centerY());
  }

  Coordinate[] screenCoordinates(AbstractGameMap map) {
    Coordinate[] screen_coordinates = new Coordinate[4];
    screen_coordinates[0] = map.mapToScreenCoordinate(new Coordinate(this.xi, this.yi));
    screen_coordinates[1] = map.mapToScreenCoordinate(new Coordinate(this.xi, this.yf));
    screen_coordinates[2] = map.mapToScreenCoordinate(new Coordinate(this.xf, this.yf));
    screen_coordinates[3] = map.mapToScreenCoordinate(new Coordinate(this.xf, this.yi));
    return screen_coordinates;
  }

  boolean touching(MapObject object, String object_map_name) {
    if (!this.mapName.equals(object_map_name)) {
      return false;
    }
    if (object.xf() >= this.xi && object.yf() >= this.yi &&
      object.xi() <= this.xf && object.yi() <= this.yf) {
      return true;
    }
    return false;
  }
  boolean touching(MapObject object) {
    if (object.xf() >= this.xi && object.yf() >= this.yi &&
      object.xi() <= this.xf && object.yi() <= this.yf) {
      return true;
    }
    return false;
  }

  boolean contains(MapObject object, String object_map_name) {
    if (!this.mapName.equals(object_map_name)) {
      return false;
    }
    if (object.xi() >= this.xi && object.yi() >= this.yi &&
      object.xf() <= this.xf && object.yf() <= this.yf) {
      return true;
    }
    return false;
  }
  boolean contains(MapObject object) {
    if (object.xi() >= this.xi && object.yi() >= this.yi &&
      object.xf() <= this.xf && object.yf() <= this.yf) {
      return true;
    }
    return false;
  }

  void setLocation(MapObject object) {
    this.xi = object.xi();
    this.yi = object.yi();
    this.xf = object.xf();
    this.yf = object.yf();
  }

  String fileString() {
    return this.mapName + ", " + this.xi + ", " + this.yi + ", " + this.xf + ", " + this.yf;
  }

  void addData(String fileString) {
    String[] data = PApplet.split(fileString, ',');
    if (data.length < 5) {
      p.global.errorMessage("ERROR: Data dimensions not sufficient for Rectangle data.");
      return;
    }
    this.mapName = PApplet.trim(data[0]);
    this.xi = Misc.toDouble(PApplet.trim(data[1]));
    this.yi = Misc.toDouble(PApplet.trim(data[2]));
    this.xf = Misc.toDouble(PApplet.trim(data[3]));
    this.yf = Misc.toDouble(PApplet.trim(data[4]));
  }
}
