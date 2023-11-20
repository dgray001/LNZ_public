package LNZModule;

import java.util.Objects;
import processing.core.PConstants;

public class IntegerCoordinate {
  protected int x = 0;
  protected int y = 0;
  private int hashCode;

  public IntegerCoordinate(Coordinate coordinate) {
    this((int)Math.floor(coordinate.x), (int)Math.floor(coordinate.y));
  }
  public IntegerCoordinate(int x, int y) {
    this.x = x;
    this.y = y;
    this.hashCode = Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return this.x + ", " + this.y;
  }
  IntegerCoordinate copy() {
    return new IntegerCoordinate(this.x, this.y);
  }
  @Override
  public boolean equals(Object coordinate_object) {
    if (this == coordinate_object) {
      return true;
    }
    if (coordinate_object == null || this.getClass() != coordinate_object.getClass()) {
      return false;
    }
    IntegerCoordinate coordinate = (IntegerCoordinate)coordinate_object;
    if (this.x == coordinate.x && this.y == coordinate.y) {
      return true;
    }
    return false;
  }
  @Override
  public int hashCode() {
    return this.hashCode;
  }

  IntegerCoordinate[] adjacentCoordinates() {
    IntegerCoordinate[] adjacent_coordinates = new IntegerCoordinate[4];
    adjacent_coordinates[0] = new IntegerCoordinate(this.x + 1, this.y);
    adjacent_coordinates[1] = new IntegerCoordinate(this.x - 1, this.y);
    adjacent_coordinates[2] = new IntegerCoordinate(this.x, this.y + 1);
    adjacent_coordinates[3] = new IntegerCoordinate(this.x, this.y - 1);
    return adjacent_coordinates;
  }
  static int[] adjacentCoordinateOrder() {
    return new int[]{PConstants.RIGHT, PConstants.LEFT, PConstants.DOWN, PConstants.UP};
  }

  IntegerCoordinate[] cornerCoordinates() {
    IntegerCoordinate[] corner_coordinates = new IntegerCoordinate[4];
    corner_coordinates[0] = new IntegerCoordinate(this.x + 1, this.y + 1);
    corner_coordinates[1] = new IntegerCoordinate(this.x - 1, this.y + 1);
    corner_coordinates[2] = new IntegerCoordinate(this.x + 1, this.y - 1);
    corner_coordinates[3] = new IntegerCoordinate(this.x - 1, this.y - 1);
    return corner_coordinates;
  }

  IntegerCoordinate[] adjacentAndCornerCoordinates() {
    IntegerCoordinate[] adjacent_and_corner_coordinates = new IntegerCoordinate[8];
    adjacent_and_corner_coordinates[0] = new IntegerCoordinate(this.x + 1, this.y);
    adjacent_and_corner_coordinates[1] = new IntegerCoordinate(this.x - 1, this.y);
    adjacent_and_corner_coordinates[2] = new IntegerCoordinate(this.x, this.y + 1);
    adjacent_and_corner_coordinates[3] = new IntegerCoordinate(this.x, this.y - 1);
    adjacent_and_corner_coordinates[4] = new IntegerCoordinate(this.x + 1, this.y + 1);
    adjacent_and_corner_coordinates[5] = new IntegerCoordinate(this.x - 1, this.y + 1);
    adjacent_and_corner_coordinates[6] = new IntegerCoordinate(this.x + 1, this.y - 1);
    adjacent_and_corner_coordinates[7] = new IntegerCoordinate(this.x - 1, this.y - 1);
    return adjacent_and_corner_coordinates;
  }

  IntegerCoordinate[] knightMoves() {
    IntegerCoordinate[] knight_moves = new IntegerCoordinate[8];
    knight_moves[0] = new IntegerCoordinate(this.x + 1, this.y + 2);
    knight_moves[1] = new IntegerCoordinate(this.x + 1, this.y - 2);
    knight_moves[2] = new IntegerCoordinate(this.x - 1, this.y + 2);
    knight_moves[3] = new IntegerCoordinate(this.x - 1, this.y - 2);
    knight_moves[4] = new IntegerCoordinate(this.x + 2, this.y + 1);
    knight_moves[5] = new IntegerCoordinate(this.x + 2, this.y - 1);
    knight_moves[6] = new IntegerCoordinate(this.x - 2, this.y + 1);
    knight_moves[7] = new IntegerCoordinate(this.x - 2, this.y - 1);
    return knight_moves;
  }

  // this functions corresponds to AbstractGameMap::adjustMcForHeight()
  // NOTE: here order matters since the "lower" tiles must be checked first
  IntegerCoordinate[] possibleTileOverlaps() {
    IntegerCoordinate[] possible_tile_overlaps = new IntegerCoordinate[10];
    possible_tile_overlaps[0] = new IntegerCoordinate(this.x + 3, this.y + 3);
    possible_tile_overlaps[1] = new IntegerCoordinate(this.x + 3, this.y + 2);
    possible_tile_overlaps[2] = new IntegerCoordinate(this.x + 2, this.y + 3);
    possible_tile_overlaps[3] = new IntegerCoordinate(this.x + 2, this.y + 2);
    possible_tile_overlaps[4] = new IntegerCoordinate(this.x + 2, this.y + 1);
    possible_tile_overlaps[5] = new IntegerCoordinate(this.x + 1, this.y + 2);
    possible_tile_overlaps[6] = new IntegerCoordinate(this.x + 1, this.y + 1);
    possible_tile_overlaps[7] = new IntegerCoordinate(this.x + 1, this.y);
    possible_tile_overlaps[8] = new IntegerCoordinate(this.x, this.y + 1);
    possible_tile_overlaps[9] = new IntegerCoordinate(this.x, this.y);
    return possible_tile_overlaps;
  }

  IntegerCoordinate addR(int amount) {
    return new IntegerCoordinate(this.x + amount, this.y + amount);
  }
  IntegerCoordinate addR(IntegerCoordinate amount) {
    return new IntegerCoordinate(this.x + amount.x, this.y + amount.y);
  }
  IntegerCoordinate subtractR(int amount) {
    return new IntegerCoordinate(this.x - amount, this.y - amount);
  }
  IntegerCoordinate subtractR(IntegerCoordinate amount) {
    return new IntegerCoordinate(this.x - amount.x, this.y - amount.y);
  }
  IntegerCoordinate multiplyR(int amount) {
    return new IntegerCoordinate(this.x * amount, this.y * amount);
  }

  boolean diagonallyAfter(IntegerCoordinate coordinate) {
    return false;
  }
}