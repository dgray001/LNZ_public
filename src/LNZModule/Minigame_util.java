package LNZModule;

import java.util.*;
import processing.core.*;
import Button.*;

enum BoardOrientation {
  STANDARD, LEFT, RIGHT;
}

abstract class GridBoard {
  abstract class BoardSquare {
    class SquareButton extends RectangleButton {
      protected boolean no_draw_button = false;

      SquareButton(LNZ sketch) {
        super(sketch, 0, 0, 0, 0);
        this.use_time_elapsed = true;
        this.force_left_button = false;
        this.roundness = 0;
        this.setColors(LNZ.color_transparent, LNZ.color_transparent,
          LNZ.color_transparent, LNZ.color_transparent, LNZ.color_transparent);
      }

      void turnOffDrawing() {
        this.no_draw_button = true;
      }
      void turnOnDrawing() {
        this.no_draw_button = false;
      }

      @Override
      public void drawButton() {
        if (this.no_draw_button) {
          return;
        }
        super.drawButton();
      }

      public void dehover() {}
      public void hover() {}
      public void click() {
        BoardSquare.this.clicked();
      }
      public void release() {
        BoardSquare.this.released();
      }
    }

    protected LNZ p;

    protected IntegerCoordinate coordinate;
    protected Map<Integer, GamePiece> pieces;
    protected SquareButton button;

    BoardSquare(LNZ sketch, IntegerCoordinate coordinate) {
      this.button = new SquareButton(sketch);
      this.coordinate = coordinate;
      this.initializePieceMap();
    }

    abstract void initializePieceMap();

    void clearSquare() {
      this.pieces.clear();
    }

    void addPiece(GamePiece piece) {
      if (this.pieces.containsKey(piece.board_key)) {
        p.global.errorMessage("ERROR: Can't add piece with key " + piece.board_key +
          " to square " + this.coordinate.x + ", " + this.coordinate.y + ".");
        return;
      }
      this.pieces.put(piece.board_key, piece);
      piece.coordinate = new IntegerCoordinate(this.coordinate.x, this.coordinate.y);
    }

    abstract boolean canTakePiece(GamePiece piece);
    abstract GamePiece getPiece();

    boolean empty() {
      return this.pieces.isEmpty();
    }

    void setSize(double size) {
      this.button.setLocation(0, 0, size, size);
    }

    void update(int time_elapsed) {
      this.updateWithoutDisplay(time_elapsed);
      this.drawSquare();
    }
    void updateWithoutDisplay(int time_elapsed) {
      this.button.turnOffDrawing();
      this.button.update(time_elapsed);
      this.button.turnOnDrawing();
      Iterator<Map.Entry<Integer, GamePiece>> iterator = this.pieces.entrySet().iterator();
      while(iterator.hasNext()) {
        Map.Entry<Integer, GamePiece> entry = iterator.next();
        if (entry.getValue().remove) {
          iterator.remove();
        }
      }
    }
    abstract void drawSquare();

    void mouseMove(float mX, float mY) {
      this.button.mouseMove(mX, mY);
    }

    void mousePress() {
      this.button.mousePress();
    }

    void mouseRelease(float mX, float mY) {
      this.button.mouseRelease(mX, mY);
    }

    abstract void clicked();
    abstract void released();
  }

  protected LNZ p;

  protected BoardSquare[][] squares;
  protected BoardOrientation orientation = BoardOrientation.STANDARD;
  protected HashMap<Integer, GamePiece> pieces;
  protected int next_piece_key = 1;

  protected double xi = 0;
  protected double yi = 0;
  protected double xf = 0;
  protected double yf = 0;
  protected double xi_draw = 0;
  protected double yi_draw = 0;
  protected double square_length = 0;

  protected IntegerCoordinate coordinate_hovered = null;

  GridBoard(LNZ sketch, int w, int h) {
    this.p = sketch;
    this.squares = new BoardSquare[w][h];
    this.initializeSquares();
    this.initializePieceMap();
  }

  void setOrientation(BoardOrientation orientation) {
    if (this.boardWidth() != this.boardHeight()) {
      return;
    }
    this.orientation = orientation;
  }

  int boardWidth() {
    return this.squares.length;
  }
  int boardHeight() {
    if (this.squares.length > 0) {
      return this.squares[0].length;
    }
    return 0;
  }

  boolean contains(IntegerCoordinate coordinate) {
    return this.squareAt(coordinate) != null;
  }

  abstract void initializePieceMap();
  abstract void initializeSquares();

  void clearBoard() {
    for (int i = 0; i < this.squares.length; i++) {
      for (int j = 0; j < this.squares[i].length; j++) {
        this.squares[i][j].clearSquare();
      }
    }
  }

  BoardSquare squareAt(IntegerCoordinate coordinate) {
    if (coordinate == null) {
      return null;
    }
    try {
      return this.squares[coordinate.x][coordinate.y];
    } catch(ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }

  void addPiece(GamePiece piece, IntegerCoordinate coordinate) {
    this.addPiece(piece, coordinate.x, coordinate.y);
  }
  void addPiece(GamePiece piece, int x, int y) {
    if (x < 0 || y < 0 || x >= this.boardWidth() || y >= this.boardHeight()) {
      p.global.errorMessage("ERROR: Can't add piece to square " + x + ", " + y +
        " since that square is not on the board.");
      return;
    }
    if (!this.squares[x][y].canTakePiece(piece)) {
      p.global.errorMessage("ERROR: Can't add piece with key " + piece.board_key +
        " to square " + x + ", " + y + " since it won't take it.");
      return;
    }
    this.addPiece(piece, x, y, this.next_piece_key);
    this.next_piece_key++;
  }
  void addPiece(GamePiece piece, int x, int y, int board_key) {
    piece.board_key = board_key;
    this.pieces.put(board_key, piece);
    this.squares[x][y].addPiece(piece);
    this.addedPiece(piece);
  }

  abstract void addedPiece(GamePiece piece);

  void setLocation(double xi, double yi, double xf, double yf) {
    this.xi = xi;
    this.yi = yi;
    this.xf = xf;
    this.yf = yf;
    if (this.boardWidth() == 0 || this.boardHeight() == 0) {
      return;
    }

    double square_length_from_width = (xf - xi) / this.boardWidth();
    double square_length_from_height = (yf - yi) / this.boardHeight();
    this.square_length = Math.min(square_length_from_width, square_length_from_height);
    for (int i = 0; i < this.squares.length; i++) {
      for (int j = 0; j < this.squares[i].length; j++) {
        this.squares[i][j].setSize(this.square_length);
      }
    }
    this.xi_draw = xi + 0.5 * (xf - xi - this.boardWidth() * this.square_length);
    this.yi_draw = yi + 0.5 * (yf - yi - this.boardHeight() * this.square_length);
  }

  double squareCenterX(IntegerCoordinate coordinate) {
    if (coordinate == null) {
      return 0;
    }
    double x_curr = this.xi_draw;
    switch(this.orientation) {
      case STANDARD:
        x_curr += coordinate.x * this.square_length;
        break;
      case LEFT:
        x_curr += (this.squares[0].length - 1 - coordinate.y) * this.square_length;
        break;
      case RIGHT:
        x_curr += coordinate.y * this.square_length;
        break;
    }
    return x_curr + 0.5 * this.square_length;
  }

  double squareCenterY(IntegerCoordinate coordinate) {
    if (coordinate == null) {
      return 0;
    }
    double y_curr = this.yi_draw;
    switch(this.orientation) {
      case STANDARD:
        y_curr += coordinate.y * this.square_length;
        break;
      case LEFT:
        y_curr += coordinate.x * this.square_length;
        break;
      case RIGHT:
        y_curr += (this.squares.length - 1 - coordinate.x) * this.square_length;
        break;
    }
    return y_curr + 0.5 * this.square_length;
  }

  void update(int time_elapsed) {
    double x_curr = this.xi_draw;
    double y_curr = this.yi_draw;
    switch(this.orientation) {
      case STANDARD:
        for (int i = 0; i < this.squares.length; i++, x_curr += this.square_length) {
          y_curr = this.yi_draw;
          for (int j = 0; j < this.squares[i].length; j++, y_curr += this.square_length) {
            p.translate(x_curr, y_curr);
            this.squares[i][j].update(time_elapsed);
            p.translate(-x_curr, -y_curr);
          }
        }
        break;
      case LEFT:
        for (int j = this.squares[0].length - 1; j >= 0; j--, x_curr += this.square_length) {
          y_curr = this.yi_draw;
          for (int i = 0; i < this.squares.length; i++, y_curr += this.square_length) {
            p.translate(x_curr, y_curr);
            this.squares[i][j].update(time_elapsed);
            p.translate(-x_curr, -y_curr);
          }
        }
        break;
      case RIGHT:
        for (int j = 0; j < this.squares[0].length; j++, x_curr += this.square_length) {
          y_curr = this.yi_draw;
          for (int i = this.squares.length - 1; i >= 0; i--, y_curr += this.square_length) {
            p.translate(x_curr, y_curr);
            this.squares[i][j].update(time_elapsed);
            p.translate(-x_curr, -y_curr);
          }
        }
        break;
    }
    this.removePieces();
    this.afterUpdate();
  }
  void updateWithoutDisplay(int time_elapsed) {
    for (int i = 0; i < this.squares.length; i++) {
      for (int j = 0; j < this.squares[i].length; j++) {
        this.squares[i][j].updateWithoutDisplay(time_elapsed);
      }
    }
    this.removePieces();
  }
  void removePieces() {
    Iterator<Map.Entry<Integer, GamePiece>> iterator = this.pieces.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<Integer, GamePiece> entry = iterator.next();
      if (entry.getValue().remove) {
        iterator.remove();
      }
    }
  }
  abstract void afterUpdate();

  void mouseMove(float mX, float mY) {
    this.coordinate_hovered = null;
    double x_curr = this.xi_draw;
    double y_curr = this.yi_draw;
    switch(this.orientation) {
      case STANDARD:
        for (int i = 0; i < this.squares.length; i++, x_curr += this.square_length) {
          y_curr = this.yi_draw;
          for (int j = 0; j < this.squares[i].length; j++, y_curr += this.square_length) {
            this.squares[i][j].mouseMove(mX - (float)x_curr, mY - (float)y_curr);
            if (this.squares[i][j].button.hovered) {
              this.coordinate_hovered = this.squares[i][j].coordinate;
            }
          }
        }
        break;
      case LEFT:
        for (int j = this.squares[0].length - 1; j >= 0; j--, x_curr += this.square_length) {
          y_curr = this.yi_draw;
          for (int i = 0; i < this.squares.length; i++, y_curr += this.square_length) {
            this.squares[i][j].mouseMove(mX - (float)x_curr, mY - (float)y_curr);
            if (this.squares[i][j].button.hovered) {
              this.coordinate_hovered = this.squares[i][j].coordinate;
            }
          }
        }
        break;
      case RIGHT:
        for (int j = 0; j < this.squares[0].length; j++, x_curr += this.square_length) {
          y_curr = this.yi_draw;
          for (int i = this.squares.length - 1; i >= 0; i--, y_curr += this.square_length) {
            this.squares[i][j].mouseMove(mX - (float)x_curr, mY - (float)y_curr);
            if (this.squares[i][j].button.hovered) {
              this.coordinate_hovered = this.squares[i][j].coordinate;
            }
          }
        }
        break;
    }
  }

  void mousePress() {
    for (int i = 0; i < this.squares.length; i++) {
      for (int j = 0; j < this.squares[i].length; j++) {
        this.squares[i][j].mousePress();
      }
    }
  }

  void mouseRelease(float mX, float mY) {
    double x_curr = this.xi_draw;
    double y_curr = this.yi_draw;
    switch(this.orientation) {
      case STANDARD:
        for (int i = 0; i < this.squares.length; i++, x_curr += this.square_length) {
          y_curr = this.yi_draw;
          for (int j = 0; j < this.squares[i].length; j++, y_curr += this.square_length) {
            this.squares[i][j].mouseRelease(mX - (float)x_curr, mY - (float)y_curr);
          }
        }
        break;
      case LEFT:
        for (int j = this.squares[0].length - 1; j >= 0; j--, x_curr += this.square_length) {
          y_curr = this.yi_draw;
          for (int i = 0; i < this.squares.length; i++, y_curr += this.square_length) {
            this.squares[i][j].mouseRelease(mX - (float)x_curr, mY - (float)y_curr);
          }
        }
        break;
      case RIGHT:
        for (int j = 0; j < this.squares[0].length; j++, x_curr += this.square_length) {
          y_curr = this.yi_draw;
          for (int i = this.squares.length - 1; i >= 0; i--, y_curr += this.square_length) {
            this.squares[i][j].mouseRelease(mX - (float)x_curr, mY - (float)y_curr);
          }
        }
        break;
    }
  }
}


abstract class GamePiece {
  protected LNZ p;
  protected int board_key = -1;
  protected boolean remove = false;
  protected IntegerCoordinate coordinate = new IntegerCoordinate(0, 0);

  GamePiece(LNZ sketch) {
    this.p = sketch;
  }

  abstract PImage getImage();
}
