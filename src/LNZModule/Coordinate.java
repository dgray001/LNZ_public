package LNZModule;

import Misc.Misc;

public class Coordinate {
  protected double x = 0;
  protected double y = 0;
  Coordinate(double x, double y) {
    this.x = x;
    this.y = y;
  }
  Coordinate(IntegerCoordinate coordinate) {
    this.x = coordinate.x;
    this.y = coordinate.y;
  }
  @Override
  public String toString() {
    return this.x + ", " + this.y;
  }
  Coordinate copy() {
    return new Coordinate(this.x, this.y);
  }
  protected Coordinate move(Coordinate move) {
    return new Coordinate(this.x + move.x, this.y + move.y);
  }
  boolean equals(Coordinate coordinate) {
    if (Math.abs(this.x - coordinate.x) < LNZ.small_number &&
      Math.abs(this.y - coordinate.y) < LNZ.small_number) {
      return true;
    }
    return false;
  }
  double distance() {
    return Math.sqrt(this.x * this.x + this.y * this.y);
  }
  double distance(Coordinate other) {
    return Math.sqrt(
      (this.x - other.x) * (this.x - other.x) +
      (this.y - other.y) * (this.y - other.y));
  }
  void add(double amount) {
    this.x += amount;
    this.y += amount;
  }
  void add(Coordinate amount) {
    this.x += amount.x;
    this.y += amount.y;
  }
  void subtract(double amount) {
    this.x -= amount;
    this.y -= amount;
  }
  void subtract(Coordinate amount) {
    this.x -= amount.x;
    this.y -= amount.y;
  }
  void multiply(double amount) {
    this.x *= amount;
    this.y *= amount;
  }
  void divide(double amount) {
    if (amount == 0) {
      return;
    }
    this.x /= amount;
    this.y /= amount;
  }
  Coordinate addR(double amount) {
    return new Coordinate(this.x + amount, this.y + amount);
  }
  Coordinate addR(IntegerCoordinate amount) {
    return new Coordinate(this.x + amount.x, this.y + amount.y);
  }
  Coordinate addR(Coordinate amount) {
    return new Coordinate(this.x + amount.x, this.y + amount.y);
  }
  Coordinate subtractR(double amount) {
    return new Coordinate(this.x - amount, this.y - amount);
  }
  Coordinate subtractR(IntegerCoordinate amount) {
    return new Coordinate(this.x - amount.x, this.y - amount.y);
  }
  Coordinate subtractR(Coordinate amount) {
    return new Coordinate(this.x - amount.x, this.y - amount.y);
  }
  Coordinate multiplyR(double amount) {
    return new Coordinate(this.x * amount, this.y * amount);
  }
  Coordinate divideR(double amount) {
    if (amount == 0) {
      return this.copy();
    }
    return new Coordinate(this.x / amount, this.y / amount);
  }
  Coordinate floorR() {
    return new Coordinate(Math.floor(this.x), Math.floor(this.y));
  }
  Coordinate maxR(Coordinate compare_to) {
    return new Coordinate(
      Math.max(this.x, compare_to.x),
      Math.max(this.y, compare_to.y)
    );
  }
  Coordinate minR(Coordinate compare_to) {
    return new Coordinate(
      Math.min(this.x, compare_to.x),
      Math.min(this.y, compare_to.y)
    );
  }
  Coordinate roundR(int digits) {
    return new Coordinate(
      Misc.round(this.x, digits),
      Misc.round(this.y, digits)
    );
  }
}