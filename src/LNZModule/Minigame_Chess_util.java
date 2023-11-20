package LNZModule;

import java.util.*;
import processing.core.*;
import Button.*;
import DImg.DImg;

enum ChessSetup {
  STANDARD;
}

enum HumanMovable {
  NONE, WHITE, BLACK, BOTH;
}

enum GameEnds {
  WHITE_CHECKMATES, BLACK_CHECKMATES, STALEMATE, REPETITION, FIFTY_MOVE,
  WHITE_TIME, BLACK_TIME, WHITE_RESIGNS, BLACK_RESIGNS, DRAW_AGREED; // abandonment, insufficient material

  public String displayName() {
    return GameEnds.displayName(this);
  }
  public static String displayName(GameEnds end) {
    switch(end) {
      case WHITE_CHECKMATES:
        return "Checkmate - White is Victorious";
      case BLACK_CHECKMATES:
        return "Checkmate - Black is Victorious";
      case STALEMATE:
        return "Stalemate - Draw";
      case REPETITION:
        return "Repetition - Draw";
      case FIFTY_MOVE:
        return "Fifty Move rule - Draw";
      case WHITE_TIME:
        return "White time out - Black is Victorious";
      case BLACK_TIME:
        return "Black time out - White is Victorious";
      case WHITE_RESIGNS:
        return "White resigns - Black is Victorious";
      case BLACK_RESIGNS:
        return "Black resigns - White is Victorious";
      case DRAW_AGREED:
        return "Draw agreed - Draw";
      default:
        return "Error";
    }
  }

  public int points() {
    return GameEnds.points(this);
  }
  public static int points(GameEnds end) {
    switch(end) {
      case WHITE_CHECKMATES:
        return 1;
      case BLACK_CHECKMATES:
        return -1;
      case STALEMATE:
        return 0;
      case REPETITION:
        return 0;
      case FIFTY_MOVE:
        return 0;
      case WHITE_TIME:
        return -1;
      case BLACK_TIME:
        return 1;
      case WHITE_RESIGNS:
        return -1;
      case BLACK_RESIGNS:
        return 1;
      case DRAW_AGREED:
        return 0;
      default:
        return 0;
    }
  }
}

class ChessBoard extends GridBoard {
  class ChessSquare extends BoardSquare {
    protected ChessColor square_color;
    protected boolean clicked = false;
    protected boolean can_move_to = false;
    protected boolean last_move_square = false;

    ChessSquare(LNZ sketch, IntegerCoordinate coordinate) {
      super(sketch, coordinate);
      this.square_color = ChessColor.colorFromSquare(coordinate);
    }
    ChessSquare copy() {
      ChessSquare square = new ChessSquare(p, this.coordinate.copy());
      square.square_color = this.square_color;
      square.clicked = this.clicked;
      square.can_move_to = this.can_move_to;
      square.last_move_square = this.last_move_square;
      return square;
    }

    void initializePieceMap() {
      this.pieces = new HashMap<Integer, GamePiece>();
    }

    void movingFrom() {
      this.clearSquare();
      this.last_move_square = true;
    }

    void movingTo(ChessPiece piece) {
      this.addPiece(piece);
      this.last_move_square = true;
    }

    @Override
    boolean empty() {
      for (GamePiece piece : this.pieces.values()) {
        if (piece == null || piece.remove) {
          continue;
        }
        return false;
      }
      return true;
    }

    ChessPiece getPiece() {
      for (GamePiece piece : this.pieces.values()) {
        if (piece == null || piece.remove) {
          continue;
        }
        return (ChessPiece)piece;
      }
      return null;
    }

    boolean canTakePiece(GamePiece piece) {
      if (piece == null || piece.remove || !ChessPiece.class.isInstance(piece)) {
        return false;
      }
      if (this.empty()) {
        return true;
      }
      return false;
    }

    void drawSquare() {
      p.rectMode(PConstants.CORNERS);
      p.stroke(LNZ.color_transparent);
      p.strokeWeight(0.01);
      if (this.last_move_square) {
        p.fill(ChessBoard.this.moveColor());
      }
      else {
        p.fill(ChessBoard.this.squareColor(this.square_color));
      }
      p.rect(this.button.xi, this.button.yi, this.button.xf, this.button.yf);
      p.imageMode(PConstants.CORNERS);
      for (GamePiece piece : this.pieces.values()) {
        if (piece == null || piece.remove) {
          continue;
        }
        ChessPiece chess_piece = (ChessPiece)piece;
        if (chess_piece.type == ChessPieceType.KING && ChessBoard.this.in_check == chess_piece.piece_color) {
          p.image(p.global.images.getImage("minigames/chess/check.png"), this.button.xi, this.button.yi, this.button.xf, this.button.yf);
        }
        p.image(chess_piece.getImage(), this.button.xi, this.button.yi, this.button.xf, this.button.yf);
      }
      if (this.can_move_to) {
        p.ellipseMode(PConstants.CENTER);
        if (this.empty()) {
          p.fill(ChessBoard.this.clickColor());
          p.stroke(ChessBoard.this.clickColor());
          p.strokeWeight(0.01);
          p.circle(this.button.xCenter(), this.button.yCenter(), 0.3 * this.button.buttonWidth());
        }
        else {
          p.fill(LNZ.color_transparent);
          p.stroke(ChessBoard.this.clickColor());
          p.strokeWeight(6);
          p.circle(this.button.xCenter(), this.button.yCenter(), this.button.buttonWidth() - 3);
        }
      }
      if ((this.clicked || this.button.clicked) && !this.empty()) {
        p.fill(ChessBoard.this.clickColor());
        p.stroke(ChessBoard.this.clickColor());
        p.strokeWeight(0.01);
        p.rect(this.button.xi, this.button.yi, this.button.xf, this.button.yf);
      }
      else if (this.button.hovered) {
        p.fill(ChessBoard.this.hoverColor());
        p.stroke(ChessBoard.this.hoverColor());
        p.strokeWeight(0.01);
        p.rect(this.button.xi, this.button.yi, this.button.xf, this.button.yf);
      }
    }

    void clicked() {
      ChessBoard.this.clicked(this.coordinate);
    }

    void released() {
      ChessBoard.this.released(this.coordinate);
    }
  }


  abstract class ChessMarking {
    protected IntegerCoordinate coordinate; // for translating into right place
    protected boolean smaller = false; // for current marking
    ChessMarking(IntegerCoordinate coordinate) {
      this.coordinate = coordinate;
    }
    abstract void setFill();
    abstract void drawMarking(double button_width);
    void draw(double button_width) {
      if (this.coordinate == null) {
        return;
      }
      this.setFill();
      this.drawMarking(button_width);
    }
  }
  class CircleMark extends ChessMarking {
    CircleMark(IntegerCoordinate coordinate) {
      super(coordinate);
    }
    void setFill() {
      p.fill(LNZ.color_transparent);
      p.stroke(ChessBoard.this.markingColor());
      if (this.smaller) {
        p.strokeWeight(4);
      }
      else {
        p.strokeWeight(6);
      }
      p.ellipseMode(PConstants.CENTER);
    }
    void drawMarking(double button_width) {
      p.circle(0, 0, button_width - 3);
    }
    @Override
    public int hashCode() {
      return Objects.hash(this.coordinate.x, this.coordinate.y);
    }
    @Override
    public boolean equals(Object circle_mark_object) {
      if (this == circle_mark_object) {
        return true;
      }
      if (circle_mark_object == null || this.getClass() != circle_mark_object.getClass()) {
        return false;
      }
      CircleMark circle_mark = (CircleMark)circle_mark_object;
      if (this.coordinate.equals(circle_mark.coordinate)) {
        return true;
      }
      return false;
    }
  }
  class ArrowMark extends ChessMarking {
    protected IntegerCoordinate head;
    ArrowMark(IntegerCoordinate tail, IntegerCoordinate head) {
      super(tail);
      this.head = head;
    }
    void setFill() {
      p.fill(ChessBoard.this.markingColor());
      p.stroke(ChessBoard.this.markingColor());
    }
    void drawMarking(double button_width) {
      double ratio = 0.3;
      if (this.smaller) {
        p.strokeWeight(0.14 * button_width);
        ratio = 0.2;
      }
      else {
        p.strokeWeight(0.2 * button_width);
      }
      if (this.head == null) {
        return;
      }
      double x_head = button_width * (this.head.x - this.coordinate.x);
      double y_head = button_width * (this.head.y - this.coordinate.y);
      if (this.head.x > this.coordinate.x) {
        x_head -= ratio * button_width;
      }
      else if (this.coordinate.x > this.head.x) {
        x_head += ratio * button_width;
      }
      if (this.head.y > this.coordinate.y) {
        y_head -= ratio * button_width;
      }
      else if (this.coordinate.y > this.head.y) {
        y_head += ratio * button_width;
      }
      double dist = Math.sqrt(x_head * x_head + y_head * y_head);
      if (dist == 0) {
        return;
      }
      p.line(0, 0, x_head, y_head);
      p.strokeWeight(0.01);
      p.translate(x_head, y_head);
      p.triangle(ratio * button_width * y_head / dist, -ratio * button_width * x_head / dist,
        -ratio * button_width * y_head / dist, ratio * button_width * x_head / dist,
        1.3 * ratio * button_width * x_head / dist, 1.3 * ratio * button_width * y_head / dist);
      p.translate(-x_head, -y_head);
    }
    @Override
    public int hashCode() {
      return Objects.hash(this.coordinate.x, this.coordinate.y, this.head.x, this.head.y);
    }
    @Override
    public boolean equals(Object arrow_mark_object) {
      if (this == arrow_mark_object) {
        return true;
      }
      if (arrow_mark_object == null || this.getClass() != arrow_mark_object.getClass()) {
        return false;
      }
      ArrowMark arrow_mark = (ArrowMark)arrow_mark_object;
      if (this.coordinate.equals(arrow_mark.coordinate) && this.head.equals(arrow_mark.head)) {
        return true;
      }
      return false;
    }
  }


  class PawnPromotionChooser {
    class PawnPromotionChooserButton extends ImageButton {
      protected ChessPieceType type;
      PawnPromotionChooserButton(LNZ sketch, ChessPieceType type, ChessColor piece_color) {
        super(sketch, sketch.global.images.getImage("minigames/chess/" + piece_color.fileName() +
          "_" + type.fileName() + ".png"), 0, 0, 0, 0);
        this.type = type;
        this.use_time_elapsed = true;
        this.overshadow_colors = true;
        this.setColors(DImg.ccolor(170, 170), DImg.ccolor(1, 0),
          DImg.ccolor(100, 80), DImg.ccolor(200, 160), DImg.ccolor(0));
      }

      @Override
      public void drawButton() {
        p.rectMode(PConstants.CORNERS);
        p.fill(DImg.ccolor(20, 120, 60));
        p.stroke(DImg.ccolor(120, 120, 60));
        p.strokeWeight(0.01);
        p.rect(this.xi, this.yi, this.xf, this.yf);
        super.drawButton();
      }

      public void hover() {}
      public void dehover() {}
      public void click() {}
      public void release() {
        PawnPromotionChooser.this.promote_to = this.type;
        PawnPromotionChooser.this.move.pawn_promotion = this.type;
      }
    }

    private boolean remove = false;
    private ChessMove move;
    private PawnPromotionChooserButton[] buttons = new PawnPromotionChooserButton[4];
    private ChessPieceType promote_to = null;
    private int fade_in_timer = 200;
    private boolean button_pressed = false;

    PawnPromotionChooser(LNZ sketch, ChessMove move) {
      this.move = move;
      this.buttons[0] = new PawnPromotionChooserButton(sketch, ChessPieceType.QUEEN, move.source_color);
      this.buttons[1] = new PawnPromotionChooserButton(sketch, ChessPieceType.KNIGHT, move.source_color);
      this.buttons[2] = new PawnPromotionChooserButton(sketch, ChessPieceType.ROOK, move.source_color);
      this.buttons[3] = new PawnPromotionChooserButton(sketch, ChessPieceType.BISHOP, move.source_color);
      if (move == null) {
        this.remove = true;
        return;
      }
      this.updateLocations();
    }

    void updateLocations() {
      double x_button = ChessBoard.this.squareCenterX(this.move.target) - 0.5 * ChessBoard.this.square_length;
      double y_center = ChessBoard.this.squareCenterY(this.move.target);
      boolean increase = (y_center > 0.5 * p.height);
      double y_curr = y_center - 0.5 * ChessBoard.this.square_length;
      for (PawnPromotionChooserButton button : this.buttons) {
        button.setLocation(x_button, y_curr, x_button + ChessBoard.this.square_length, y_curr + ChessBoard.this.square_length);
        if (increase) {
          y_curr -= ChessBoard.this.square_length;
        }
        else {
          y_curr += ChessBoard.this.square_length;
        }
      }
    }

    void update(int time_elapsed) {
      this.fade_in_timer -= time_elapsed;
      if (this.promote_to != null) {
        this.remove = true;
      }
      for (PawnPromotionChooserButton button : this.buttons) {
        button.update(time_elapsed);
      }
    }

    void mouseMove(float mX, float mY) {
      if (this.promote_to != null) {
        this.remove = true;
        return;
      }
      for (PawnPromotionChooserButton button : this.buttons) {
        button.mouseMove(mX, mY);
      }
    }

    void mousePress() {
      if (this.promote_to != null) {
        this.remove = true;
        return;
      }
      this.button_pressed = false;
      for (PawnPromotionChooserButton button : this.buttons) {
        button.mousePress();
        if (button.clicked) {
          this.button_pressed = true;
        }
      }
    }

    void mouseRelease(float mX, float mY) {
      if (this.fade_in_timer < 0 && !this.button_pressed) {
        this.remove = true;
      }
      if (this.promote_to != null) {
        return;
      }
      for (PawnPromotionChooserButton button : this.buttons) {
        button.mouseRelease(mX, mY);
      }
    }
  }


  protected ChessSetup setup = null;
  protected HumanMovable human_controlled = HumanMovable.BOTH;
  protected boolean toggle_human_controllable = false;
  protected ArrayList<ChessPiece> white_pieces = new ArrayList<ChessPiece>();
  protected ArrayList<ChessPiece> black_pieces = new ArrayList<ChessPiece>();

  protected ChessColor turn = ChessColor.WHITE;
  protected HashSet<ChessMove> valid_moves = new HashSet<ChessMove>();
  protected PawnPromotionChooser pawn_promotion_chooser = null;
  protected HashSet<ChessMove> return_moves = new HashSet<ChessMove>();
  protected boolean calculate_return_moves = true;

  protected ChessColor in_check = null;
  protected ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
  protected int fifty_move_counter = 0;
  protected Queue<ChessMove> move_queue = new ArrayDeque<ChessMove>();
  protected HashMap<ChessPosition, Integer> all_positions = new HashMap<ChessPosition, Integer>();
  protected GameEnds game_ended = null; // null until game ends

  protected IntegerCoordinate coordinate_dragging = null;
  protected IntegerCoordinate coordinate_clicked = null;
  protected IntegerCoordinate coordinate_marking = null;
  protected HashSet<ChessMarking> markings = new HashSet<ChessMarking>();

  ChessBoard(LNZ sketch) {
    super(sketch, 8, 8);
    this.orientation = BoardOrientation.RIGHT;
  }
  ChessBoard(LNZ sketch, ChessBoard board) {
    super(sketch, 8, 8);
    this.orientation = board.orientation;
    this.next_piece_key = board.next_piece_key;
    this.setup = board.setup;
    this.turn = board.turn;
    this.calculate_return_moves = board.calculate_return_moves;
    this.in_check = board.in_check;
    if (board.coordinate_dragging == null) {
      this.coordinate_dragging = null;
    }
    else {
      this.coordinate_dragging = board.coordinate_dragging.copy();
    }
    if (board.coordinate_clicked == null) {
      this.coordinate_clicked = null;
    }
    else {
      this.coordinate_clicked = board.coordinate_clicked.copy();
    }
    if (board.coordinate_marking == null) {
      this.coordinate_marking = null;
    }
    else {
      this.coordinate_marking = board.coordinate_marking.copy();
    }
    for (int i = 0; i < this.squares.length; i++) {
      for (int j = 0; j < this.squares[i].length; j++) {
        this.squares[i][j] = ((ChessSquare)board.squares[i][j]).copy();
      }
    }
    for (GamePiece piece : board.pieces.values()) {
      ChessPiece copied_piece = ((ChessPiece)piece).copy();
      this.addPiece(copied_piece, piece.coordinate.x, piece.coordinate.y, piece.board_key);
    }
    for (ChessMove move : board.valid_moves) {
      this.valid_moves.add(move.copy());
    }
    for (ChessMove move : board.return_moves) {
      this.return_moves.add(move.copy());
    }
    for (ChessMove move : board.moves) {
      this.moves.add(move.copy());
    }
    this.removeSquareMarkings();
  }

  void initializePieceMap() {
    this.pieces = new HashMap<Integer, GamePiece>();
  }

  void initializeSquares() {
    for (int i = 0; i < this.squares.length; i++) {
      for (int j = 0; j < this.squares[i].length; j++) {
        this.squares[i][j] = new ChessSquare(p, new IntegerCoordinate(i, j));
      }
    }
  }

  void removeSquareMarkings() {
    for (int i = 0; i < this.squares.length; i++) {
      for (int j = 0; j < this.squares[i].length; j++) {
        ChessSquare square = (ChessSquare)this.squares[i][j];
        square.clicked = false;
        square.can_move_to = false;
        square.last_move_square = false;
      }
    }
  }

  void resetAnalysis() {
    this.human_controlled = HumanMovable.BOTH;
    this.orientation = BoardOrientation.RIGHT;
    this.setupBoard();
  }

  void setupBoard() {
    this.setupBoard(ChessSetup.STANDARD);
  }
  void setupBoard(ChessSetup setup) {
    this.setup = setup;
    this.clearBoard();
    for (ChessPiece piece : this.white_pieces) {
      piece.remove = true;
    }
    for (ChessPiece piece : this.black_pieces) {
      piece.remove = true;
    }
    this.removeSquareMarkings();
    this.valid_moves.clear();
    this.return_moves.clear();
    this.markings.clear();
    this.moves.clear();
    this.fifty_move_counter = 0;
    this.move_queue.clear();
    this.all_positions.clear();
    this.game_ended = null;
    this.turn = ChessColor.WHITE;
    // White back-rank
    ChessPiece white_rook1 = new ChessPiece(p, ChessPieceType.ROOK, ChessColor.WHITE);
    this.addPiece(white_rook1, 0, 0);
    ChessPiece white_knight1 = new ChessPiece(p, ChessPieceType.KNIGHT, ChessColor.WHITE);
    this.addPiece(white_knight1, 0, 1);
    ChessPiece white_bishop1 = new ChessPiece(p, ChessPieceType.BISHOP, ChessColor.WHITE);
    this.addPiece(white_bishop1, 0, 2);
    ChessPiece white_queen1 = new ChessPiece(p, ChessPieceType.QUEEN, ChessColor.WHITE);
    this.addPiece(white_queen1, 0, 3);
    ChessPiece white_king1 = new ChessPiece(p, ChessPieceType.KING, ChessColor.WHITE);
    this.addPiece(white_king1, 0, 4);
    ChessPiece white_bishop2 = new ChessPiece(p, ChessPieceType.BISHOP, ChessColor.WHITE);
    this.addPiece(white_bishop2, 0, 5);
    ChessPiece white_knight2 = new ChessPiece(p, ChessPieceType.KNIGHT, ChessColor.WHITE);
    this.addPiece(white_knight2, 0, 6);
    ChessPiece white_rook2 = new ChessPiece(p, ChessPieceType.ROOK, ChessColor.WHITE);
    this.addPiece(white_rook2, 0, 7);
    // White pawns
    ChessPiece white_pawn1 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.WHITE);
    this.addPiece(white_pawn1, 1, 0);
    ChessPiece white_pawn2 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.WHITE);
    this.addPiece(white_pawn2, 1, 1);
    ChessPiece white_pawn3 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.WHITE);
    this.addPiece(white_pawn3, 1, 2);
    ChessPiece white_pawn4 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.WHITE);
    this.addPiece(white_pawn4, 1, 3);
    ChessPiece white_pawn5 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.WHITE);
    this.addPiece(white_pawn5, 1, 4);
    ChessPiece white_pawn6 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.WHITE);
    this.addPiece(white_pawn6, 1, 5);
    ChessPiece white_pawn7 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.WHITE);
    this.addPiece(white_pawn7, 1, 6);
    ChessPiece white_pawn8 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.WHITE);
    this.addPiece(white_pawn8, 1, 7);
    // Black back-rank
    ChessPiece black_rook1 = new ChessPiece(p, ChessPieceType.ROOK, ChessColor.BLACK);
    this.addPiece(black_rook1, 7, 0);
    ChessPiece black_knight1 = new ChessPiece(p, ChessPieceType.KNIGHT, ChessColor.BLACK);
    this.addPiece(black_knight1, 7, 1);
    ChessPiece black_bishop1 = new ChessPiece(p, ChessPieceType.BISHOP, ChessColor.BLACK);
    this.addPiece(black_bishop1, 7, 2);
    ChessPiece black_queen1 = new ChessPiece(p, ChessPieceType.QUEEN, ChessColor.BLACK);
    this.addPiece(black_queen1, 7, 3);
    ChessPiece black_king1 = new ChessPiece(p, ChessPieceType.KING, ChessColor.BLACK);
    this.addPiece(black_king1, 7, 4);
    ChessPiece black_bishop2 = new ChessPiece(p, ChessPieceType.BISHOP, ChessColor.BLACK);
    this.addPiece(black_bishop2, 7, 5);
    ChessPiece black_knight2 = new ChessPiece(p, ChessPieceType.KNIGHT, ChessColor.BLACK);
    this.addPiece(black_knight2, 7, 6);
    ChessPiece black_rook2 = new ChessPiece(p, ChessPieceType.ROOK, ChessColor.BLACK);
    this.addPiece(black_rook2, 7, 7);
    // Black pawns
    ChessPiece black_pawn1 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.BLACK);
    this.addPiece(black_pawn1, 6, 0);
    ChessPiece black_pawn2 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.BLACK);
    this.addPiece(black_pawn2, 6, 1);
    ChessPiece black_pawn3 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.BLACK);
    this.addPiece(black_pawn3, 6, 2);
    ChessPiece black_pawn4 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.BLACK);
    this.addPiece(black_pawn4, 6, 3);
    ChessPiece black_pawn5 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.BLACK);
    this.addPiece(black_pawn5, 6, 4);
    ChessPiece black_pawn6 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.BLACK);
    this.addPiece(black_pawn6, 6, 5);
    ChessPiece black_pawn7 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.BLACK);
    this.addPiece(black_pawn7, 6, 6);
    ChessPiece black_pawn8 = new ChessPiece(p, ChessPieceType.PAWN, ChessColor.BLACK);
    this.addPiece(black_pawn8, 6, 7);
    // calculate move
    this.startTurn(ChessColor.WHITE);
  }

  int materialDifference() {
    return this.whiteMaterial() - this.blackMaterial();
  }

  int whiteMaterial() {
    int material = 0;
    for (ChessPiece piece : this.white_pieces) {
      if (piece == null || piece.remove) {
        continue;
      }
      material += piece.type.material();
    }
    return material;
  }

  int blackMaterial() {
    int material = 0;
    for (ChessPiece piece : this.black_pieces) {
      if (piece == null || piece.remove) {
        continue;
      }
      material += piece.type.material();
    }
    return material;
  }

  void nextTurn() {
    switch(this.turn) {
      case WHITE:
        this.startTurn(ChessColor.BLACK);
        break;
      case BLACK:
        this.startTurn(ChessColor.WHITE);
        break;
    }
  }
  void startTurn(ChessColor chess_color) {
    this.valid_moves.clear();
    this.return_moves.clear();
    if (this.game_ended != null) {
      return;
    }
    this.turn = chess_color;
    this.in_check = null;
    switch(chess_color) {
      case WHITE:
        for (ChessPiece piece : this.black_pieces) {
          if (piece.remove) {
            continue;
          }
          if (this.calculate_return_moves) {
            piece.updateValidMoves(this, true);
            this.return_moves.addAll(piece.valid_moves);
          }
          piece.valid_moves.clear();
        }
        if (this.inCheck()) {
          this.in_check = chess_color;
        }
        for (ChessPiece piece : this.white_pieces) {
          if (piece.remove) {
            continue;
          }
          piece.updateValidMoves(this, !this.calculate_return_moves);
          this.valid_moves.addAll(piece.valid_moves);
        }
        break;
      case BLACK:
        for (ChessPiece piece : this.white_pieces) {
          if (piece.remove) {
            continue;
          }
          if (this.calculate_return_moves) {
            piece.updateValidMoves(this, true);
            this.return_moves.addAll(piece.valid_moves);
          }
          piece.valid_moves.clear();
        }
        if (this.inCheck()) {
          this.in_check = chess_color;
        }
        for (ChessPiece piece : this.black_pieces) {
          if (piece.remove) {
            continue;
          }
          piece.updateValidMoves(this, !this.calculate_return_moves);
          this.valid_moves.addAll(piece.valid_moves);
        }
        break;
    }
    if (this.valid_moves.size() == 0) { // game over
      switch(this.in_check) {
        case WHITE:
          this.game_ended = GameEnds.BLACK_CHECKMATES;
          break;
        case BLACK:
          this.game_ended = GameEnds.WHITE_CHECKMATES;
          break;
        default:
          this.game_ended = GameEnds.STALEMATE;
          break;
      }
    }
  }

  boolean inCheck() {
    for (ChessMove move : this.return_moves) {
      if (move.capture && this.pieceAt(move.target) != null &&
        this.pieceAt(move.target).type == ChessPieceType.KING) {
        return true;
      }
    }
    return false;
  }

  boolean canTakeKing() {
    for (ChessMove move : this.valid_moves) {
      if (move.capture && this.pieceAt(move.target) != null &&
        this.pieceAt(move.target).type == ChessPieceType.KING) {
        return true;
      }
    }
    return false;
  }

  boolean humanCanMakeMove() {
    if (this.toggle_human_controllable || this.game_ended != null) {
      return false;
    }
    switch(this.human_controlled) {
      case NONE:
        return false;
      case WHITE:
        return this.turn == ChessColor.WHITE;
      case BLACK:
        return this.turn == ChessColor.BLACK;
      case BOTH:
        return true;
    }
    return false;
  }

  void offerDraw() {
    this.offerDraw(this.turn);
  }
  void offerDraw(ChessColor offering_player) {
    if (this.game_ended != null) {
      return;
    }
    // offering player offers draw
  }

  void resign() {
    if (this.game_ended != null) {
      return;
    }
    switch(this.turn) {
      case WHITE:
        this.game_ended = GameEnds.WHITE_RESIGNS;
        break;
      case BLACK:
        this.game_ended = GameEnds.BLACK_RESIGNS;
        break;
    }
  }

  boolean computersTurn() {
    if (this.valid_moves.size() == 0) {
      return false;
    }
    switch(this.human_controlled) {
      case NONE:
        return true;
      case WHITE:
        return this.turn != ChessColor.WHITE;
      case BLACK:
        return this.turn != ChessColor.BLACK;
      case BOTH:
        return false;
    }
    return false;
  }

  void addedPiece(GamePiece piece) {
    if (!ChessPiece.class.isInstance(piece)) {
      p.global.errorMessage("ERROR: Piece with class " + piece.getClass() + " not a chess piece.");
      return;
    }
    ChessPiece chess_piece = (ChessPiece)piece;
    switch(chess_piece.piece_color) {
      case WHITE:
        this.white_pieces.add(chess_piece);
        break;
      case BLACK:
        this.black_pieces.add(chess_piece);
        break;
      default:
        p.global.errorMessage("ERROR: Chess piece color " + chess_piece.piece_color + " not recognized.");
        break;
    }
  }

  void afterUpdate() {
    for (int i = 0; i < this.white_pieces.size(); i++) {
      if (this.white_pieces.get(i).remove) {
        this.white_pieces.remove(i);
        i--;
      }
    }
    for (int i = 0; i < this.black_pieces.size(); i++) {
      if (this.black_pieces.get(i).remove) {
        this.black_pieces.remove(i);
        i--;
      }
    }
    double marking_rotate = 0;
    switch(this.orientation) {
      case LEFT:
        marking_rotate = 0.5 * Math.PI;
        break;
      case RIGHT:
        marking_rotate = -0.5 * Math.PI;
        break;
      default:
        break;
    }
    for (ChessMarking marking : this.markings) {
      double translate_x = this.squareCenterX(marking.coordinate);
      double translate_y = this.squareCenterY(marking.coordinate);
      p.translate(translate_x, translate_y);
      p.rotate(marking_rotate);
      marking.draw(this.square_length);
      p.rotate(-marking_rotate);
      p.translate(-translate_x, -translate_y);
    }
    if (this.coordinate_marking != null) {
      ChessMarking marking;
      if (this.coordinate_marking.equals(this.coordinate_hovered)) {
        marking = new CircleMark(this.coordinate_marking);
      }
      else if (this.coordinate_hovered != null) {
        marking = new ArrowMark(this.coordinate_marking, this.coordinate_hovered);
      }
      else {
        return;
      }
      marking.smaller = true;
      double translate_x = this.squareCenterX(marking.coordinate);
      double translate_y = this.squareCenterY(marking.coordinate);
      p.translate(translate_x, translate_y);
      p.rotate(marking_rotate);
      marking.draw(this.square_length);
      p.rotate(-marking_rotate);
      p.translate(-translate_x, -translate_y);
    }
  }

  @Override
  void update(int time_elapsed) {
    super.update(time_elapsed);
    if (this.pawn_promotion_chooser != null) {
      this.pawn_promotion_chooser.update(time_elapsed);
      if (this.pawn_promotion_chooser.promote_to != null) {
        this.tryMakeMove(this.pawn_promotion_chooser.move);
      }
      if (this.pawn_promotion_chooser.remove) {
        this.pawn_promotion_chooser = null;
      }
    }
  }

  @Override
  void mouseMove(float mX, float mY) {
    super.mouseMove(mX, mY);
    if (this.pawn_promotion_chooser != null) {
      this.pawn_promotion_chooser.mouseMove(mX, mY);
    }
  }

  @Override
  void mousePress() {
    if (this.pawn_promotion_chooser != null) {
      this.pawn_promotion_chooser.mousePress();
    }
    super.mousePress();
  }

  @Override
  void mouseRelease(float mX, float mY) {
    super.mouseRelease(mX, mY);
    if (this.pawn_promotion_chooser != null) {
      this.pawn_promotion_chooser.mouseRelease(mX, mY);
    }
  }

  int squareColor(ChessColor square_color) {
    switch(square_color) {
      case WHITE:
        return DImg.ccolor(248, 240, 227);
      case BLACK:
        return DImg.ccolor(165, 42, 42);
    }
    return DImg.ccolor(1, 0);
  }

  int hoverColor() {
    return DImg.ccolor(120, 80);
  }

  int clickColor() {
    return DImg.ccolor(200, 160);
  }

  int moveColor() {
    return DImg.ccolor(190, 190, 50);
  }

  int markingColor() {
    return DImg.ccolor(180, 100, 90, 170);
  }

  void clicked(IntegerCoordinate coordinate) {
    if (coordinate == null) {
      this.markings.clear();
      return;
    }
    switch(p.mouseButton) {
      case PConstants.LEFT:
        this.markings.clear();
        if (!this.humanCanMakeMove()) {
          break;
        }
        if (this.pieceAt(coordinate) != null) {
          this.coordinate_dragging = coordinate;
          //((ChessSquare)this.squareAt(coordinate)).clicked = true;
        }
        break;
      case PConstants.RIGHT:
        this.coordinate_marking = coordinate;
        break;
      default:
        this.markings.clear();
        break;
    }
  }

  void released(IntegerCoordinate coordinate) {
    if (coordinate == null) {
      this.markings.clear();
      return;
    }
    switch(p.mouseButton) {
      case PConstants.LEFT:
        this.markings.clear();
        if (!this.humanCanMakeMove()) {
          break;
        }
        ChessPiece piece = this.pieceAt(coordinate);
        if (this.coordinate_clicked == null && piece != null && piece.piece_color == this.turn) {
          ChessSquare square = ((ChessSquare)this.squareAt(coordinate));
          if (square.button.hovered) {
            this.coordinate_clicked = coordinate;
            square.clicked = true;
          }
          else {
            // use dragged
          }
        }
        else if (piece != null && piece.piece_color == this.turn) {
          ChessSquare square = ((ChessSquare)this.squareAt(coordinate));
          if (square.button.hovered) {
            ((ChessSquare)this.squareAt(this.coordinate_clicked)).clicked = false;
            this.coordinate_clicked = coordinate;
            square.clicked = true;
          }
          else {
            // use dragged
          }
        }
        else if (this.coordinate_clicked != null) {
          this.tryMovePiece(this.coordinate_clicked, coordinate);
          ((ChessSquare)this.squareAt(this.coordinate_clicked)).clicked = false;
          this.coordinate_clicked = null;
        }
        this.updateSquaresMarked();
        break;
      case PConstants.RIGHT:
        if (this.coordinate_marking == null) {
          return;
        }
        else if (this.coordinate_marking.equals(this.coordinate_hovered)) {
          this.addMarking(new CircleMark(coordinate));
        }
        else {
          this.addMarking(new ArrowMark(this.coordinate_marking, this.coordinate_hovered));
        }
        this.coordinate_marking = null;
        break;
      default:
        break;
    }
  }

  void addMarking(ChessMarking chess_marking) {
    if (chess_marking == null) {
      return;
    }
    if (this.markings.contains(chess_marking)) {
      this.markings.remove(chess_marking);
    }
    else {
      this.markings.add(chess_marking);
    }
  }

  void updateSquaresMarked() {
    boolean all_false = false;
    ChessPiece piece = this.pieceAt(this.coordinate_clicked);
    if (piece == null) {
      all_false = true;
    }
    for (BoardSquare[] squares_row : this.squares) {
      for (BoardSquare board_square : squares_row) {
        ChessSquare square = (ChessSquare)board_square;
        if (all_false) {
          square.can_move_to = false;
          continue;
        }
        square.can_move_to = piece.canMoveTo(square.coordinate);
      }
    }
  }

  // For when human tries to make a move
  void tryMovePiece(IntegerCoordinate source, IntegerCoordinate target) {
    if (!this.humanCanMakeMove()) {
      return;
    }
    ChessPiece source_piece = this.pieceAt(source);
    if (source_piece == null) {
      return;
    }
    ChessPiece target_piece = this.pieceAt(target);
    if (target_piece != null && target_piece.piece_color == source_piece.piece_color) {
      return;
    }
    if (target_piece == null) { // check for en passant
      if (source_piece.type == ChessPieceType.PAWN && source.y != target.y) {
        target_piece = this.pieceAt(new IntegerCoordinate(source.x, target.y));
      }
    }
    ChessMove potential_move = new ChessMove(p, source, target, target_piece != null,
      source_piece.piece_color, source_piece.type);
    if (source_piece.type == ChessPieceType.PAWN && (target.x == 0 || target.x == this.boardHeight() - 1)) {
      this.pawnPromotionChooser(potential_move);
      return;
    }
    if (!this.valid_moves.contains(potential_move)) {
      return;
    }
    this.makeMove(potential_move);
  }

  void tryMakeMove(ChessMove move) {
    if (!this.humanCanMakeMove()) {
      return;
    }
    if (!this.valid_moves.contains(move)) {
      return;
    }
    if (move.pawn_promotion == null && move.source_type == ChessPieceType.PAWN &&
      (move.target.x == 0 || move.target.x == this.boardHeight() - 1)) {
      this.pawnPromotionChooser(move);
      return;
    }
    this.makeMove(move);
  }

  void pawnPromotionChooser(ChessMove move) {
    if (!this.humanCanMakeMove()) {
      return;
    }
    if (move.source_type != ChessPieceType.PAWN || (move.target.x > 0 && move.target.x < this.boardHeight() - 1)) {
      return;
    }
    this.pawn_promotion_chooser = new PawnPromotionChooser(p, move);
  }

  void makeRandomMove() {
    ArrayList<ChessMove> possible_moves = new ArrayList<ChessMove>(this.valid_moves);
    Collections.shuffle(possible_moves);
    this.makeMove(possible_moves.get(0), false);
  }

  void makeMove(ChessMove move) {
    this.makeMove(move, true);
  }
  void makeMove(ChessMove move, boolean play_sound) {
    ChessPiece source_piece = this.pieceAt(move.source);
    if (source_piece == null) {
      return;
    }
    ChessPiece target_piece = this.pieceAt(move.target);
    if (target_piece != null && target_piece.piece_color == source_piece.piece_color) {
      return;
    }
    for (ChessPiece piece : this.white_pieces) {
      piece.moved_last_turn = false;
    }
    for (ChessPiece piece : this.black_pieces) {
      piece.moved_last_turn = false;
    }
    if (target_piece == null) { // check for en passant
      if (source_piece.type == ChessPieceType.PAWN && move.source.y != move.target.y) {
        target_piece = this.pieceAt(new IntegerCoordinate(move.source.x, move.target.y));
      }
    }
    if (target_piece != null) {
      target_piece.remove = true;
      if (play_sound) {
        p.global.sounds.trigger_player("minigames/chess/capture");
      }
    }
    else {
      if (move.castlingMove()) { // check for castling
        IntegerCoordinate rook_source = move.castlingMoveRookSource(this);
        ChessPiece rook = this.pieceAt(rook_source);
        if (rook == null || rook.remove || rook.piece_color != source_piece.piece_color ||
          rook.type != ChessPieceType.ROOK || rook.has_moved) {
          p.global.errorMessage("ERROR: Can't castle with invalid rook.");
          return;
        }
        IntegerCoordinate rook_target = move.castlingMoveRookTarget();
        BoardSquare rook_target_square = this.squareAt(rook_target);
        if (rook_source == null || rook_target == null || !rook_target_square.empty()) {
          p.global.errorMessage("ERROR: Can't castle with invalid rook squares.");
          return;
        }
        rook.last_coordinate = rook.coordinate.copy();
        rook.has_moved = true;
        rook.moved_last_turn = true;
        this.squareAt(rook_source).clearSquare();
        rook_target_square.addPiece(rook);
      }
      if (play_sound) {
        p.global.sounds.trigger_player("minigames/chess/move");
      }
    }
    source_piece.last_coordinate = source_piece.coordinate.copy();
    source_piece.has_moved = true;
    source_piece.moved_last_turn = true;
    if (move.pawn_promotion != null) {
      if (source_piece.type == ChessPieceType.PAWN && (move.target.x == 0 || move.target.x == this.boardHeight() - 1)) {
        source_piece.type = move.pawn_promotion;
      }
      else {
        p.global.errorMessage("ERROR: Invalid pawn promotion move.");
        return;
      }
    }
    if (this.moves.size() > 0) {
      ChessMove last_move = this.moves.get(this.moves.size() - 1);
      ((ChessSquare)this.squareAt(last_move.source)).last_move_square = false;
      ((ChessSquare)this.squareAt(last_move.target)).last_move_square = false;
    }
    ((ChessSquare)this.squareAt(move.source)).movingFrom();
    ((ChessSquare)this.squareAt(move.target)).movingTo(source_piece);
    this.moves.add(move);
    this.move_queue.add(move);
    this.markings.clear();
    ChessPosition new_position = new ChessPosition(this);
    if (this.all_positions.containsKey(new_position)) {
      this.all_positions.put(new_position, 1 + this.all_positions.get(new_position));
      if (this.all_positions.get(new_position) >= 3) {
        this.game_ended = GameEnds.REPETITION;
      }
    }
    else {
      this.all_positions.put(new_position, 1);
    }
    if (move.fiftyMoveResetMove()) {
      this.fifty_move_counter = 0;
    }
    else {
      this.fifty_move_counter++;
      if (this.fifty_move_counter >= 50) {
        this.game_ended = GameEnds.FIFTY_MOVE;
      }
    }
    this.nextTurn();
  }

  ChessPiece pieceAt(IntegerCoordinate coordinate) {
    BoardSquare square = this.squareAt(coordinate);
    if (square == null) {
      return null;
    }
    if (square.empty()) {
      return null;
    }
    ChessPiece piece = ((ChessSquare)square).getPiece();
    if (piece != null && piece.remove) {
      piece = null;
    }
    return piece;
  }


  static String chessBoardNotation(IntegerCoordinate coordinate) {
    return Character.toString('a' + coordinate.y) + Integer.toString(coordinate.x + 1);
  }
}


enum ChessPieceType {
  KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN;

  public String fileName() {
    return ChessPieceType.fileName(this);
  }
  public static String fileName(ChessPieceType type) {
    switch(type) {
      case KING:
        return "king";
      case QUEEN:
        return "queen";
      case ROOK:
        return "rook";
      case BISHOP:
        return "bishop";
      case KNIGHT:
        return "knight";
      case PAWN:
        return "pawn";
      default:
        return "";
    }
  }

  public String characterString() {
    return this.characterString(false);
  }
  public String characterString(boolean show_pawn) {
    return ChessPieceType.characterString(this, show_pawn);
  }
  public static String characterString(ChessPieceType type, boolean show_pawn) {
    switch(type) {
      case KING:
        return "K";
      case QUEEN:
        return "Q";
      case ROOK:
        return "R";
      case BISHOP:
        return "B";
      case KNIGHT:
        return "N";
      case PAWN:
        if (show_pawn) {
          return "P";
        }
        else {
          return "";
        }
      default:
        return "";
    }
  }

  public int material() {
    return ChessPieceType.material(this);
  }
  public static int material(ChessPieceType type) {
    switch(type) {
      case KING:
        return 0;
      case QUEEN:
        return 8;
      case ROOK:
        return 5;
      case BISHOP:
        return 3;
      case KNIGHT:
        return 3;
      case PAWN:
        return 1;
      default:
        return 0;
    }
  }
}

enum ChessColor {
  WHITE, BLACK;

  public String fileName() {
    return ChessColor.fileName(this);
  }
  public static String fileName(ChessColor type) {
    switch(type) {
      case WHITE:
        return "white";
      case BLACK:
        return "black";
      default:
        return "";
    }
  }

  public static ChessColor colorFromSquare(IntegerCoordinate coordinate) {
    if ((coordinate.x + coordinate.y) % 2 == 0) {
      return ChessColor.BLACK;
    }
    return ChessColor.WHITE;
  }
}

class ChessPiece extends GamePiece {
  protected ChessPieceType type;
  protected ChessColor piece_color;
  protected HashSet<ChessMove> valid_moves = new HashSet<ChessMove>();
  protected boolean has_moved = false;
  protected boolean moved_last_turn = false;
  protected IntegerCoordinate last_coordinate = null;

  ChessPiece(LNZ sketch, ChessPieceType type, ChessColor piece_color) {
    super(sketch);
    this.type = type;
    this.piece_color = piece_color;
  }
  ChessPiece copy() {
    ChessPiece piece = new ChessPiece(p, this.type, this.piece_color);
    piece.board_key = this.board_key;
    piece.remove = this.remove;
    piece.coordinate = this.coordinate.copy();
    piece.has_moved = this.has_moved;
    piece.moved_last_turn = this.moved_last_turn;
    if (this.last_coordinate == null) {
      piece.last_coordinate = null;
    }
    else {
      piece.last_coordinate = this.last_coordinate.copy();
    }
    for (ChessMove move : this.valid_moves) {
      piece.valid_moves.add(move.copy());
    }
    return piece;
  }

  PImage getImage() {
    return p.global.images.getImage("minigames/chess/" + this.piece_color.fileName() +
      "_" + this.type.fileName() + ".png");
  }

  boolean canMoveTo(IntegerCoordinate coordinate) {
    for (ChessMove move : this.valid_moves) {
      if (move.target.equals(coordinate)) {
        return true;
      }
    }
    return false;
  }

  void updateValidMoves(ChessBoard board) {
    this.updateValidMoves(board, false);
  }
  void updateValidMoves(ChessBoard board, boolean ignore_check) {
    this.valid_moves.clear();
    if (this.remove) {
      return;
    }
    switch(this.type) {
      case KING:
        for (IntegerCoordinate target : this.coordinate.adjacentAndCornerCoordinates()) {
          if (!board.contains(target)) {
            continue;
          }
          ChessPiece target_piece = board.pieceAt(target);
          if (target_piece != null && target_piece.piece_color == this.piece_color) {
            continue;
          }
          this.valid_moves.add(new ChessMove(p, this.coordinate, target,
            target_piece != null, this.piece_color, this.type));
        }
        this.addCastlingMoves(board);
        break;
      case QUEEN:
        this.addBishopMoves(board);
        this.addRookMoves(board);
        break;
      case ROOK:
        this.addRookMoves(board);
        break;
      case BISHOP:
        this.addBishopMoves(board);
        break;
      case KNIGHT:
        for (IntegerCoordinate target : this.coordinate.knightMoves()) {
          if (!board.contains(target)) {
            continue;
          }
          ChessPiece target_piece = board.pieceAt(target);
          if (target_piece != null && target_piece.piece_color == this.piece_color) {
            continue;
          }
          this.valid_moves.add(new ChessMove(p, this.coordinate, target,
            target_piece != null, this.piece_color, this.type));
        }
        break;
      case PAWN:
        IntegerCoordinate move1 = null;
        IntegerCoordinate capture_left = null;
        IntegerCoordinate capture_right = null;
        IntegerCoordinate capture_left_en_passant = null;
        IntegerCoordinate capture_right_en_passant = null;
        IntegerCoordinate move2 = null;
        switch(this.piece_color) {
          case WHITE:
            move1 = new IntegerCoordinate(this.coordinate.x + 1, this.coordinate.y);
            capture_left = new IntegerCoordinate(this.coordinate.x + 1, this.coordinate.y + 1);
            capture_left_en_passant = new IntegerCoordinate(this.coordinate.x, this.coordinate.y + 1);
            capture_right = new IntegerCoordinate(this.coordinate.x + 1, this.coordinate.y - 1);
            capture_right_en_passant = new IntegerCoordinate(this.coordinate.x, this.coordinate.y - 1);
            if (!this.has_moved) {
              move2 = new IntegerCoordinate(this.coordinate.x + 2, this.coordinate.y);
            }
            break;
          case BLACK:
            move1 = new IntegerCoordinate(this.coordinate.x - 1, this.coordinate.y);
            capture_left = new IntegerCoordinate(this.coordinate.x - 1, this.coordinate.y + 1);
            capture_left_en_passant = new IntegerCoordinate(this.coordinate.x, this.coordinate.y + 1);
            capture_right = new IntegerCoordinate(this.coordinate.x - 1, this.coordinate.y - 1);
            capture_right_en_passant = new IntegerCoordinate(this.coordinate.x, this.coordinate.y - 1);
            if (!this.has_moved) {
              move2 = new IntegerCoordinate(this.coordinate.x - 2, this.coordinate.y);
            }
            break;
        }
        boolean move1_valid = false;
        if (move1 != null) {
          ChessPiece target_piece = board.pieceAt(move1);
          if (board.contains(move1) && (target_piece == null || target_piece.remove)) {
            this.valid_moves.add(new ChessMove(p, this.coordinate, move1, false,
              this.piece_color, this.type));
            move1_valid = true;
          }
        }
        if (capture_left != null) {
          ChessPiece target_piece = board.pieceAt(capture_left);
          if (target_piece == null || target_piece.remove) {
            ChessPiece maybe_target_piece = board.pieceAt(capture_left_en_passant);
            if (maybe_target_piece != null && maybe_target_piece.type == ChessPieceType.
              PAWN && maybe_target_piece.moved_last_turn && Math.abs(maybe_target_piece.
              coordinate.x - maybe_target_piece.last_coordinate.x) == 2) {
              target_piece = maybe_target_piece;
            }
          }
          if (board.contains(capture_left) && target_piece != null && target_piece.piece_color != this.piece_color) {
            this.valid_moves.add(new ChessMove(p, this.coordinate, capture_left, true,
              this.piece_color, this.type));
          }
        }
        if (capture_right != null) {
          ChessPiece target_piece = board.pieceAt(capture_right);
          if (target_piece == null || target_piece.remove) {
            ChessPiece maybe_target_piece = board.pieceAt(capture_right_en_passant);
            if (maybe_target_piece != null && maybe_target_piece.type == ChessPieceType.
              PAWN && maybe_target_piece.moved_last_turn && Math.abs(maybe_target_piece.
              coordinate.x - maybe_target_piece.last_coordinate.x) == 2) {
              target_piece = maybe_target_piece;
            }
          }
          if (board.contains(capture_right) && target_piece != null && target_piece.piece_color != this.piece_color) {
            this.valid_moves.add(new ChessMove(p, this.coordinate, capture_right, true,
              this.piece_color, this.type));
          }
        }
        if (move2 != null && move1_valid) {
          ChessPiece target_piece = board.pieceAt(move2);
          if (board.contains(move2) && (target_piece == null || target_piece.remove)) {
            this.valid_moves.add(new ChessMove(p, this.coordinate, move2, false,
              this.piece_color, this.type));
          }
        }
        ArrayList<ChessMove> new_moves_to_add = new ArrayList<ChessMove>();
        for (ChessMove move : this.valid_moves) {
          if (move.target.x > 0 && move.target.x < board.boardHeight() - 1) {
            continue;
          }
          move.pawn_promotion = ChessPieceType.QUEEN;
          ChessMove copied_move = move.copy();
          copied_move.pawn_promotion = ChessPieceType.ROOK;
          new_moves_to_add.add(copied_move);
          copied_move = move.copy();
          copied_move.pawn_promotion = ChessPieceType.BISHOP;
          new_moves_to_add.add(copied_move);
          copied_move = move.copy();
          copied_move.pawn_promotion = ChessPieceType.KNIGHT;
          new_moves_to_add.add(copied_move);
        }
        for (ChessMove move : new_moves_to_add) {
          this.valid_moves.add(move);
        }
        break;
      default:
        break;
    }
    if (ignore_check) {
      return;
    }
    for (Iterator<ChessMove> i = this.valid_moves.iterator(); i.hasNext();) {
      ChessMove move = i.next();
      ChessBoard copied_board = new ChessBoard(p, board);
      copied_board.calculate_return_moves = false;
      copied_board.makeMove(move, false);
      if (copied_board.canTakeKing()) {
        i.remove();
      }
    }
  }

  void addCastlingMoves(ChessBoard board) {
    if (this.has_moved) {
      return;
    }
    ChessPiece[] rooks = new ChessPiece[2];
    rooks[0] = board.pieceAt(new IntegerCoordinate(this.coordinate.x, 0));
    rooks[1] = board.pieceAt(new IntegerCoordinate(this.coordinate.x, board.boardHeight() - 1));
    for (ChessPiece rook : rooks) {
      if (rook == null || rook.remove || rook.piece_color != this.piece_color ||
        rook.type != ChessPieceType.ROOK || rook.has_moved) {
        continue;
      }
      if (board.in_check == this.piece_color) {
        continue;
      }
      int direction = 1;
      if (rook.coordinate.y < this.coordinate.y) {
        direction = -1;
      }
      boolean blocking = false;
      for (int i = this.coordinate.y + direction; (i > 0 && i < board.boardHeight() - 1); i += direction) {
        ChessPiece piece = board.pieceAt(new IntegerCoordinate(this.coordinate.x, i));
        if (piece == null || piece.remove) {
          continue;
        }
        blocking = true;
        break;
      }
      if (blocking) {
        continue;
      }
      ChessMove through_check_check = new ChessMove(p, this.coordinate, new IntegerCoordinate(
        this.coordinate.x, this.coordinate.y + direction), false, this.piece_color, this.type);
      ChessBoard copied_board = new ChessBoard(p, board);
      copied_board.calculate_return_moves = false;
      copied_board.makeMove(through_check_check, false);
      if (copied_board.canTakeKing()) {
        continue;
      }
      this.valid_moves.add(new ChessMove(p, this.coordinate, new IntegerCoordinate(
        this.coordinate.x, this.coordinate.y + 2 * direction), false, this.piece_color, this.type));
    }
  }

  void addBishopMoves(ChessBoard board) {
    for (int x = this.coordinate.x + 1, y = this.coordinate.y + 1; (x <
      board.boardWidth() && y < board.boardHeight()); x++, y++) {
      IntegerCoordinate target = new IntegerCoordinate(x, y);
      ChessPiece target_piece = board.pieceAt(target);
      if (target_piece == null || target_piece.remove || target_piece.piece_color != this.piece_color) {
        this.valid_moves.add(new ChessMove(p, this.coordinate, target,
          (target_piece != null && !target_piece.remove), this.piece_color, this.type));
      }
      if (target_piece != null && !target_piece.remove) {
        break;
      }
    }
    for (int x = this.coordinate.x + 1, y = this.coordinate.y - 1; x <
      board.boardWidth() && y >= 0; x++, y--) {
      IntegerCoordinate target = new IntegerCoordinate(x, y);
      ChessPiece target_piece = board.pieceAt(target);
      if (target_piece == null || target_piece.remove || target_piece.piece_color != this.piece_color) {
        this.valid_moves.add(new ChessMove(p, this.coordinate, target,
          (target_piece != null && !target_piece.remove), this.piece_color, this.type));
      }
      if (target_piece != null && !target_piece.remove) {
        break;
      }
    }
    for (int x = this.coordinate.x - 1, y = this.coordinate.y + 1; x
      >= 0 && y < board.boardHeight(); x--, y++) {
      IntegerCoordinate target = new IntegerCoordinate(x, y);
      ChessPiece target_piece = board.pieceAt(target);
      if (target_piece == null || target_piece.remove || target_piece.piece_color != this.piece_color) {
        this.valid_moves.add(new ChessMove(p, this.coordinate, target,
          (target_piece != null && !target_piece.remove), this.piece_color, this.type));
      }
      if (target_piece != null && !target_piece.remove) {
        break;
      }
    }
    for (int x = this.coordinate.x - 1, y = this.coordinate.y - 1; x >= 0 && y >= 0; x--, y--) {
      IntegerCoordinate target = new IntegerCoordinate(x, y);
      ChessPiece target_piece = board.pieceAt(target);
      if (target_piece == null || target_piece.remove || target_piece.piece_color != this.piece_color) {
        this.valid_moves.add(new ChessMove(p, this.coordinate, target,
          (target_piece != null && !target_piece.remove), this.piece_color, this.type));
      }
      if (target_piece != null && !target_piece.remove) {
        break;
      }
    }
  }

  void addRookMoves(ChessBoard board) {
    for (int x = this.coordinate.x + 1, y = this.coordinate.y; x < board.boardWidth(); x++) {
      IntegerCoordinate target = new IntegerCoordinate(x, y);
      ChessPiece target_piece = board.pieceAt(target);
      if (target_piece == null || target_piece.remove || target_piece.piece_color != this.piece_color) {
        this.valid_moves.add(new ChessMove(p, this.coordinate, target,
          (target_piece != null && !target_piece.remove), this.piece_color, this.type));
      }
      if (target_piece != null && !target_piece.remove) {
        break;
      }
    }
    for (int x = this.coordinate.x, y = this.coordinate.y + 1; y < board.boardHeight(); y++) {
      IntegerCoordinate target = new IntegerCoordinate(x, y);
      ChessPiece target_piece = board.pieceAt(target);
      if (target_piece == null || target_piece.remove || target_piece.piece_color != this.piece_color) {
        this.valid_moves.add(new ChessMove(p, this.coordinate, target,
          (target_piece != null && !target_piece.remove), this.piece_color, this.type));
      }
      if (target_piece != null && !target_piece.remove) {
        break;
      }
    }
    for (int x = this.coordinate.x - 1, y = this.coordinate.y; x >= 0; x--) {
      IntegerCoordinate target = new IntegerCoordinate(x, y);
      ChessPiece target_piece = board.pieceAt(target);
      if (target_piece == null || target_piece.remove || target_piece.piece_color != this.piece_color) {
        this.valid_moves.add(new ChessMove(p, this.coordinate, target,
          (target_piece != null && !target_piece.remove), this.piece_color, this.type));
      }
      if (target_piece != null && !target_piece.remove) {
        break;
      }
    }
    for (int x = this.coordinate.x, y = this.coordinate.y - 1; y >= 0; y--) {
      IntegerCoordinate target = new IntegerCoordinate(x, y);
      ChessPiece target_piece = board.pieceAt(target);
      if (target_piece == null || target_piece.remove || target_piece.piece_color != this.piece_color) {
        this.valid_moves.add(new ChessMove(p, this.coordinate, target,
          (target_piece != null && !target_piece.remove), this.piece_color, this.type));
      }
      if (target_piece != null && !target_piece.remove) {
        break;
      }
    }
  }
}


class ChessMove {
  private LNZ p;
  protected IntegerCoordinate source;
  protected IntegerCoordinate target;
  protected boolean capture;
  protected ChessColor source_color;
  protected ChessPieceType source_type;
  protected ChessPieceType pawn_promotion = null;

  ChessMove(LNZ sketch, IntegerCoordinate source, IntegerCoordinate target, boolean capture,
    ChessColor source_color, ChessPieceType source_type) {
    this(sketch, source, target, capture, source_color, source_type, null);
  }
  ChessMove(LNZ sketch, IntegerCoordinate source, IntegerCoordinate target, boolean capture,
    ChessColor source_color, ChessPieceType source_type, ChessPieceType pawn_promotion) {
    this.p = sketch;
    this.source = source;
    this.target = target;
    this.capture = capture;
    this.source_color = source_color;
    this.source_type = source_type;
    this.pawn_promotion = pawn_promotion;
  }
  ChessMove copy() {
    return new ChessMove(p, this.source.copy(), this.target.copy(), this.capture,
      this.source_color, this.source_type, this.pawn_promotion);
  }

  String pgnString() {
    String source_string = ChessBoard.chessBoardNotation(this.source);
    String target_string = ChessBoard.chessBoardNotation(this.target);
    String piece_string = this.source_type.characterString();
    if (this.capture) {
      target_string = "x" + target_string;
    }
    if (this.pawn_promotion != null) {
      target_string += "=" + this.pawn_promotion.characterString();
    }
    return piece_string + source_string + target_string;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.source.x, this.source.y, this.target.x, this.target.y, this.capture, this.source_color, this.source_type);
  }

  @Override
  public boolean equals(Object chessmove_object) {
    if (this == chessmove_object) {
      return true;
    }
    if (chessmove_object == null || this.getClass() != chessmove_object.getClass()) {
      return false;
    }
    ChessMove chessmove = (ChessMove)chessmove_object;
    if (this.source.equals(chessmove.source) && this.target.equals(chessmove.target) &&
      this.capture == chessmove.capture && this.source_color == chessmove.source_color &&
      this.source_type == chessmove.source_type && this.pawn_promotion == chessmove.pawn_promotion) {
      return true;
    }
    return false;
  }

  boolean castlingMove() {
    return (this.source.x == this.target.x && Math.abs(this.source.y - this.target.y) == 2 &&
      !this.capture && this.source_type == ChessPieceType.KING);
  }

  boolean fiftyMoveResetMove() {
    return (this.capture || this.source_type == ChessPieceType.PAWN);
  }

  IntegerCoordinate castlingMoveRookSource(ChessBoard board) {
    if (!this.castlingMove()) {
      return null;
    }
    if (this.source.y < this.target.y) {
      return new IntegerCoordinate(this.source.x, board.boardHeight() - 1);
    }
    else {
      return new IntegerCoordinate(this.source.x, 0);
    }
  }

  IntegerCoordinate castlingMoveRookTarget() {
    if (!this.castlingMove()) {
      return null;
    }
    if (this.source.y < this.target.y) {
      return new IntegerCoordinate(this.source.x, this.source.y + 1);
    }
    else {
      return new IntegerCoordinate(this.source.x, this.source.y - 1);
    }
  }
}


class ChessPosition {
  private String[][] square_strings;
  private int hash_code = 0;
  ChessPosition(ChessBoard board) {
    this.square_strings = new String[board.boardWidth()][board.boardHeight()];
    String hash_string = "";
    for (int i = 0; i < board.squares.length; i++) {
      for (int j = 0; j < board.squares[i].length; j++) {
        ChessPiece piece = (ChessPiece)board.squares[i][j].getPiece();
        if (piece == null || piece.remove) {
          square_strings[i][j] = "-";
          hash_string += "-";
        }
        else {
          square_strings[i][j] = piece.type.characterString(true);
          hash_string += piece.type.characterString(true);
        }
      }
    }
    this.hash_code = hash_string.hashCode();
  }
  @Override
  public int hashCode() {
    return this.hash_code;
  }
  @Override
  public boolean equals(Object chess_position_object) {
    if (this == chess_position_object) {
      return true;
    }
    if (chess_position_object == null || this.getClass() != chess_position_object.getClass()) {
      return false;
    }
    ChessPosition chess_position = (ChessPosition)chess_position_object;
    for (int i = 0; i < this.square_strings.length; i++) {
      for (int j = 0; j < this.square_strings[i].length; j++) {
        try {
          if (this.square_strings[i][j].equals(chess_position.square_strings[i][j])) {
            continue;
          }
        } catch (ArrayIndexOutOfBoundsException e) {}
        return false;
      }
    }
    return true;
  }
}