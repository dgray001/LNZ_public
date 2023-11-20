package LNZModule;

// This class holds information needed to split features into pieces and
// draw each piece appropriately preserving depth information
public class FeatureDrawGridPiece {
  // coordinates relative to feature coordinate
  final IntegerCoordinate draw_location; // which square to draw piece at
  final IntegerCoordinate piece_location; // where piece is
  final boolean extend_x; // true if x = 2 for this piece
  final boolean extend_y; // true if y = 2 for this piece
  public FeatureDrawGridPiece(IntegerCoordinate location) {
    this.draw_location = location;
    this.piece_location = location.copy();
    this.extend_x = false;
    this.extend_y = false;
  }
  public FeatureDrawGridPiece(IntegerCoordinate draw_location,
    IntegerCoordinate piece_location, boolean extend_x, boolean extend_y) {
    this.draw_location = draw_location;
    this.piece_location = piece_location;
    this.extend_x = extend_x;
    this.extend_y = extend_y;
  }
  @Override
  public String toString() {
    return this.draw_location.toString() + " " + this.piece_location.toString() + " " + this.extend_x + " " + this.extend_y;
  }
  @Override
  public boolean equals(Object piece_object) {
    if (this == piece_object) {
      return true;
    }
    if (piece_object == null || this.getClass() != piece_object.getClass()) {
      return false;
    }
    FeatureDrawGridPiece piece = (FeatureDrawGridPiece)piece_object;
    if (this.draw_location.equals(piece.draw_location) &&
      this.piece_location.equals(piece.piece_location) &&
      this.extend_x == piece.extend_x &&
      this.extend_y == piece.extend_y) {
      return true;
    }
    return false;
  }
}