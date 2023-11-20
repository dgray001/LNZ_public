package LNZModule;

import java.util.*;
import processing.core.*;
import Button.*;
import DImg.DImg;
import Element.*;
import Form.*;
import Misc.Misc;

enum ChessState {
  ANALYSIS, HUMAN_VS_HUMAN, HUMAN_VS_COMPUTER, COMPUTER_VS_COMPUTER;
  public boolean playingGame() {
    switch(this) {
      case HUMAN_VS_HUMAN:
      case HUMAN_VS_COMPUTER:
      case COMPUTER_VS_COMPUTER:
        return true;
      default:
        return false;
    }
  }
  public String titleString() {
    switch(this) {
      case ANALYSIS:
        return "Analysis";
      case HUMAN_VS_HUMAN:
        return "Human vs. Human";
      case HUMAN_VS_COMPUTER:
        return "Human vs. AI";
      case COMPUTER_VS_COMPUTER:
        return "AI vs. AI";
      default:
        return "";
    }
  }
}

class Chess extends Minigame {
  abstract class MoveBoxButton extends ImageButton {
    protected double width_ratio = 0.85; // ratio of width to height
    protected boolean show_hover_message = false;

    MoveBoxButton(LNZ sketch, String icon_name) {
      super(sketch, sketch.global.images.getImage("icons/" + icon_name + ".png"), 0, 0, 0, 0);
      this.use_time_elapsed = true;
      this.overshadow_colors = true;
      this.setColors(DImg.ccolor(170, 170), DImg.ccolor(1, 0),
        DImg.ccolor(100, 80), DImg.ccolor(200, 160), DImg.ccolor(0));
    }

    @Override
    public void drawButton() {
      super.drawButton();
      if (this.show_hover_message) {
        // show hover message
      }
    }

    public void hover() {
      this.show_hover_message = true;
    }
    public void dehover() {
      this.show_hover_message = false;
    }
    public void click() {}
    public void release() {
      if (this.hovered) {
        this.buttonFunction();
      }
    }

    abstract void buttonFunction();
  }

  class SpacerButton extends MoveBoxButton {
    SpacerButton(LNZ sketch, double width_ratio) {
      super(sketch, "first");
      this.img = Images.getTransparentPixel();
      this.width_ratio = width_ratio;
      this.overshadow_colors = false;
    }
    void buttonFunction() {}
  }

  class ViewFirstButton extends MoveBoxButton {
    ViewFirstButton(LNZ sketch) {
      super(sketch, "first");
      this.width_ratio = 1.0;
    }
    void buttonFunction() {
      Chess.this.viewFirst();
    }
  }

  class ViewLastButton extends MoveBoxButton {
    ViewLastButton(LNZ sketch) {
      super(sketch, "last");
      this.width_ratio = 1.0;
    }
    void buttonFunction() {
      Chess.this.viewLast();
    }
  }

  class ViewPreviousButton extends MoveBoxButton {
    ViewPreviousButton(LNZ sketch) {
      super(sketch, "previous");
      this.width_ratio = 1.4;
    }
    void buttonFunction() {
      Chess.this.viewPrevious();
    }
  }

  class ViewNextButton extends MoveBoxButton {
    ViewNextButton(LNZ sketch) {
      super(sketch, "next");
      this.width_ratio = 1.4;
    }
    void buttonFunction() {
      Chess.this.viewNext();
    }
  }

  class RotateBoardButton extends MoveBoxButton {
    RotateBoardButton(LNZ sketch) {
      super(sketch, "flip");
    }
    void buttonFunction() {
      Chess.this.flipBoard();
    }
  }

  class ResetButton extends MoveBoxButton {
    ResetButton(LNZ sketch) {
      super(sketch, "reset");
    }
    void buttonFunction() {
      Chess.this.resetAnalysisBoard();
    }
  }

  class OfferDrawButton extends MoveBoxButton {
    OfferDrawButton(LNZ sketch) {
      super(sketch, "draw");
    }
    void buttonFunction() {
      Chess.this.offerDraw();
    }
  }

  class ResignButton extends MoveBoxButton {
    ResignButton(LNZ sketch) {
      super(sketch, "resign");
    }
    void buttonFunction() {
      Chess.this.resign();
    }
  }

  class StopGameButton extends MoveBoxButton {
    StopGameButton(LNZ sketch) {
      super(sketch, "hand_stop");
    }
    void buttonFunction() {
      Chess.this.stopGame();
    }
  }

  class MenuButton extends MoveBoxButton {
    MenuButton(LNZ sketch) {
      super(sketch, "menu");
    }
    void buttonFunction() {
      Chess.this.toggleMenuForm();
    }
  }

  class RestartGameButton extends MoveBoxButton {
    protected ChessState state;
    RestartGameButton(LNZ sketch, ChessState state) {
      super(sketch, "reset");
      this.state = state;
      switch(state) {
        case HUMAN_VS_HUMAN:
          this.img = sketch.global.images.getImage("icons/human.png");
          break;
        case HUMAN_VS_COMPUTER:
          this.img = sketch.global.images.getImage("icons/human_ai.png");
          break;
        case COMPUTER_VS_COMPUTER:
          this.img = sketch.global.images.getImage("icons/ai.png");
          break;
        default:
          break;
      }
    }
    void buttonFunction() {
      switch(this.state) {
        case HUMAN_VS_HUMAN:
          Chess.this.startHumanVsHumanGame();
          break;
        case HUMAN_VS_COMPUTER:
          Chess.this.startGameVsComputer(true);
          break;
        case COMPUTER_VS_COMPUTER:
          Chess.this.startComputerVsComputerGame();
          break;
        default:
          break;
      }
    }
  }


  abstract class MoveBox {
    class MoveContainer extends ListTextBox {
      MoveContainer(LNZ sketch) {
        super(sketch);
        this.useElapsedTime();
        this.color_background = DImg.ccolor(40);
        this.color_header = DImg.ccolor(80);
        this.color_stroke = DImg.ccolor(20);
        this.color_text = DImg.ccolor(245);
        this.color_title = DImg.ccolor(255);
        this.scrollbar_max_width = 35;
        this.scrollbar.setButtonColors(DImg.ccolor(170), DImg.ccolor(60),
          DImg.ccolor(100), DImg.ccolor(20), DImg.ccolor(0));
        this.scrollbar.button_upspace.setColors(DImg.ccolor(170),
          DImg.ccolor(0), DImg.ccolor(0), DImg.ccolor(200), DImg.ccolor(0));
        this.scrollbar.button_downspace.setColors(DImg.ccolor(170),
          DImg.ccolor(0), DImg.ccolor(0), DImg.ccolor(200), DImg.ccolor(0));
        this.hover_color = DImg.ccolor(200, 200, 100, 60);
        this.highlight_color = DImg.ccolor(240, 240, 60, 120);
        this.can_unclick_outside_box = false;
      }

      @Override
      public void update(int time_elapsed) {
        int last_line_clicked = this.line_clicked;
        this.line_clicked = Chess.this.current_view - 1;
        if (this.line_clicked != last_line_clicked) {
          this.jump_to_line();
        }
        super.update(time_elapsed);
      }

      @Override
      public void jump_to_line(boolean hard_jump) {
        if (hard_jump || this.line_clicked < (int)Math.floor(this.scrollbar.value)) {
          this.scrollbar.updateValue(this.line_clicked);
          return;
        }
        int lines_shown = this.text_lines.size() - (int)this.scrollbar.maxValue;
        if (this.line_clicked >= (int)this.scrollbar.value + lines_shown) {
          this.scrollbar.increaseValue(2 + this.line_clicked - (int)this.scrollbar.value - lines_shown);
        }
        else if (this.line_clicked < (int)this.scrollbar.value) {
          this.scrollbar.decreaseValue((int)this.scrollbar.value - this.line_clicked);
        }
      }

      public void click() {
        Chess.this.setCurrentView(this.line_clicked + 1);
      }

      public void doubleclick() {
      }
    }


    class MenuForm extends Form {
      private LNZ p;
      private boolean hidden = true;
      private boolean canceled = false;

      MenuForm(LNZ sketch) {
        super(sketch);
        this.color_background = DImg.ccolor(210);
        this.color_header = DImg.ccolor(170);
        this.color_title = DImg.ccolor(0);
        this.color_stroke = DImg.ccolor(20);
        this.setTitleText("Create Game");
      }

      void toggle(ChessState state) {
        this.hidden = !this.hidden;
        if (this.hidden) {
          this.clearFields();
          return;
        }
        this.canceled = false;
        switch(state) {
          case ANALYSIS:
            ArrayList<ToggleFormFieldInput> toggle_input = new ArrayList<ToggleFormFieldInput>();
            toggle_input.add(new ToggleFormFieldInput("  vs Human", p.global.images.getImage("icons/human.png")));
            toggle_input.add(new ToggleFormFieldInput("  vs AI", p.global.images.getImage("icons/human_ai.png")));
            toggle_input.add(new ToggleFormFieldInput("  AI vs AI", p.global.images.getImage("icons/ai.png")));
            ToggleFormField toggle = new ToggleFormField(p, toggle_input);
            toggle.setTextSize(36);
            SubmitCancelFormField submit_cancel = new SubmitCancelFormField(p, "Play", "Cancel");

            this.addField(new SpacerFormField(p, 60));
            this.addField(toggle);
            this.addField(submit_cancel);
            break;
          default:
            this.hidden = true;
            this.clearFields();
            break;
        }
      }

      public void submit() {
        switch(this.fields.get(1).getValue()) {
          case "0":
            Chess.this.startHumanVsHumanGame();
            break;
          case "1":
            Chess.this.startGameVsComputer();
            break;
          case "2":
            Chess.this.startComputerVsComputerGame();
            break;
          default:
            break;
        }
        this.cancel();
      }

      public void cancel() {
        this.canceled = true;
      }

      public void buttonPress(int index) {
      }

      @Override
      public void update(int time_elapsed) {
        if (this.hidden) {
          return;
        }
        super.update(time_elapsed);
        if (this.canceled) {
          Chess.this.toggleMenuForm();
          this.canceled = false;
        }
      }

      @Override
      public void mouseMove(float mX, float mY) {
        if (this.hidden) {
          return;
        }
        super.mouseMove(mX, mY);
      }

      @Override
      public void mousePress() {
        if (this.hidden) {
          return;
        }
        super.mousePress();
      }

      @Override
      public void mouseRelease(float mX, float mY) {
        if (this.hidden) {
          return;
        }
        super.mouseRelease(mX, mY);
      }
    }


    protected MoveContainer moves;
    protected MenuForm menu_form;
    protected List<MoveBoxButton> buttons = new ArrayList<MoveBoxButton>();
    protected double button_min_size = 50;
    protected double button_gap = 5;

    protected double xi = 0;
    protected double yi = 0;
    protected double xf = 0;
    protected double yf = 0;

    MoveBox(LNZ sketch) {
      this.moves = new MoveContainer(sketch);
      this.menu_form = new MenuForm(sketch);
      this.addButtons();
      this.moves.setTitleText(Chess.this.state.titleString());
    }

    abstract void addButtons();

    void refreshLocation() {
      this.setLocation(this.xi, this.yi, this.xf, this.yf);
    }
    void setLocation(double xi, double yi, double xf, double yf) {
      this.xi = xi;
      this.yi = yi;
      this.xf = xf;
      this.yf = yf;
      double button_weight = 0;
      for (MoveBoxButton button : this.buttons) {
        button_weight += button.width_ratio;
      }
      double button_height = 0;
      if (button_weight != 0) {
        button_height = Math.min(this.button_min_size, (xf - xi - (this.buttons.size() - 1) * this.button_gap) / button_weight);
      }
      this.moves.setLocation(xi + this.button_gap, yi + this.button_gap,
        xf - this.button_gap, yf - 2 * this.button_gap - button_height);
      this.menu_form.setLocation(xi + this.button_gap, yi + this.button_gap,
        xf - this.button_gap, yf - 2 * this.button_gap - button_height);
      double x_curr = xi;
      for (MoveBoxButton button : this.buttons) {
        double button_width = button.width_ratio * button_height;
        button.setLocation(x_curr, yf - this.button_gap - button_height,
          x_curr + button_width, yf - this.button_gap);
        x_curr += button_width + this.button_gap;
      }
    }

    void addMove(ChessMove move, ChessColor in_check) {
      String moveString = move.pgnString();
      if (in_check != null) {
        moveString += "+";
      }
      if (move.source_color == ChessColor.WHITE) {
        moveString = Integer.toString(1 + Chess.this.chessboard.moves.size() / 2) +
          ". " + moveString + " ...";
      }
      else {
        moveString = "       ... " + moveString;
      }
      this.moves.addLine(moveString);
    }

    void gameEnded(GameEnds ends) {
      this.moves.addLine(ends.displayName());
      this.adjustButtonsForGameEnd();
      this.refreshLocation();
    }
    abstract void adjustButtonsForGameEnd();

    void update(int time_elapsed) {
      if (this.menu_form.hidden) {
        this.moves.update(time_elapsed);
      }
      else {
        this.menu_form.update(time_elapsed);
      }
      for (MoveBoxButton button : this.buttons) {
        button.update(time_elapsed);
      }
    }

    void mouseMove(float mX, float mY) {
      if (this.menu_form.hidden) {
        this.moves.mouseMove(mX, mY);
      }
      else {
        this.menu_form.mouseMove(mX, mY);
      }
      for (MoveBoxButton button : this.buttons) {
        button.mouseMove(mX, mY);
      }
    }

    void mousePress() {
      if (this.menu_form.hidden) {
        this.moves.mousePress();
      }
      else {
        this.menu_form.mousePress();
      }
      for (MoveBoxButton button : this.buttons) {
        button.mousePress();
      }
    }

    void mouseRelease(float mX, float mY) {
      if (this.menu_form.hidden) {
        this.moves.mouseRelease(mX, mY);
      }
      else {
        this.menu_form.mouseRelease(mX, mY);
      }
      for (MoveBoxButton button : this.buttons) {
        button.mouseRelease(mX, mY);
      }
    }

    void scroll(int amount) {
      this.moves.scroll(amount);
    }

    void keyPress(int key, int keyCode) {
      this.moves.keyPress(key, keyCode);
    }

    void keyRelease(int key, int keyCode) {
    }
  }


  class AnalysisMoveBox extends MoveBox {
    AnalysisMoveBox(LNZ sketch) {
      super(sketch);
    }

    void addButtons() {
      this.buttons.add(new RotateBoardButton(p));
      this.buttons.add(new SpacerButton(p, 0.3));
      this.buttons.add(new ViewFirstButton(p));
      this.buttons.add(new ViewPreviousButton(p));
      this.buttons.add(new ViewNextButton(p));
      this.buttons.add(new ViewLastButton(p));
      this.buttons.add(new SpacerButton(p, 0.3));
      this.buttons.add(new ResetButton(p));
      this.buttons.add(new MenuButton(p));
    }

    void adjustButtonsForGameEnd() {
    }
  }


  class PlayingMoveBox extends MoveBox {
    PlayingMoveBox(LNZ sketch) {
      super(sketch);
    }

    void addButtons() {
      this.buttons.add(new RotateBoardButton(p));
      this.buttons.add(new SpacerButton(p, 0.3));
      this.buttons.add(new ViewFirstButton(p));
      this.buttons.add(new ViewPreviousButton(p));
      this.buttons.add(new ViewNextButton(p));
      this.buttons.add(new ViewLastButton(p));
      this.buttons.add(new SpacerButton(p, 0.3));
      switch(Chess.this.state) {
        case HUMAN_VS_HUMAN:
        case HUMAN_VS_COMPUTER:
          this.buttons.add(new OfferDrawButton(p));
          this.buttons.add(new ResignButton(p));
          break;
        case COMPUTER_VS_COMPUTER:
          this.buttons.add(new SpacerButton(p, 0.85));
          this.buttons.add(new StopGameButton(p));
          break;
        default:
          break;
      }
    }

    void adjustButtonsForGameEnd() {
      this.buttons.set(7, new RestartGameButton(p, Chess.this.state));
      this.buttons.set(8, new MenuButton(p));
    }
  }

  private ChessBoard chessboard;
  private ArrayList<ChessBoard> chessboard_views = new ArrayList<ChessBoard>();
  private int current_view = 0;
  private ChessState state = ChessState.ANALYSIS;
  private boolean game_ended = false;
  private MoveBox move_box;

  private ChessAI chess_ai;
  private ChessAI opposing_chess_ai = null;
  private boolean computers_turn = false;
  private int computers_time_left = 0;

  Chess(LNZ sketch) {
    super(sketch, MinigameName.CHESS);
    this.chessboard = new ChessBoard(sketch);
    this.move_box = new AnalysisMoveBox(sketch);
    this.chess_ai = new ChessAI(sketch);
    this.color_background = DImg.ccolor(90);
    this.chessboard.setupBoard();
    this.initialChessboardView();
  }

  ChessBoard chessboardView() {
    if (this.current_view >= this.chessboard_views.size()) {
      return null;
    }
    return this.chessboard_views.get(this.current_view);
  }

  void initialChessboardView() {
    ChessBoard copied_board = new ChessBoard(p, this.chessboard);
    copied_board.toggle_human_controllable = true;
    this.chessboard_views.clear();
    this.chessboard_views.add(copied_board);
    this.current_view = 0;
    this.chessboard.toggle_human_controllable = false;
  }

  void updateView(ChessMove move) {
    ChessBoard copied_board = new ChessBoard(p, this.chessboard_views.get(this.chessboard_views.size() - 1));
    copied_board.makeMove(move, false);
    copied_board.toggle_human_controllable = true;
    this.chessboard_views.add(copied_board);
    this.current_view = this.chessboard_views.size() - 1;
    this.chessboard.toggle_human_controllable = false;
  }

  void setCurrentView(int new_view) {
    this.current_view = new_view;
    if (this.current_view < 0) {
      this.current_view = 0;
    }
    else if (this.current_view > this.chessboard_views.size() - 1) {
      this.current_view = this.chessboard_views.size() - 1;
    }
    if (this.current_view == this.chessboard_views.size() - 1) {
      this.chessboard.toggle_human_controllable = true;
    }
    else {
      this.chessboard.toggle_human_controllable = false;
    }
    this.refreshChessboardViewLocation();
  }

  void viewFirst() {
    this.current_view = 0;
    if (this.chessboard_views.size() > 1) {
      this.chessboard.toggle_human_controllable = true;
    }
    this.refreshChessboardViewLocation();
  }

  void viewLast() {
    this.current_view = this.chessboard_views.size() - 1;
    this.chessboard.toggle_human_controllable = false;
    this.refreshChessboardViewLocation();
  }

  void viewPrevious() {
    if (this.current_view > 0) {
      this.current_view--;
      this.chessboard.toggle_human_controllable = true;
    }
    this.refreshChessboardViewLocation();
  }

  void viewNext() {
    if (this.current_view < this.chessboard_views.size() - 1) {
      this.current_view++;
      if (this.current_view == this.chessboard_views.size() - 1) {
        this.chessboard.toggle_human_controllable = false;
      }
    }
    this.refreshChessboardViewLocation();
  }

  void flipBoard() {
    switch(this.chessboard.orientation) {
      case RIGHT:
        this.chessboard.orientation = BoardOrientation.LEFT;
        break;
      case LEFT:
        this.chessboard.orientation = BoardOrientation.RIGHT;
        break;
      default:
        break;
    }
    this.chessboardView().orientation = this.chessboard.orientation;
  }

  void resetAnalysisBoard() {
    if (this.state != ChessState.ANALYSIS) {
      p.global.errorMessage("ERROR: Can't reset analyis when not in analysis.");
      return;
    }
    this.chessboard.resetAnalysis();
    this.initialChessboardView();
    this.chess_ai.reset();
    this.move_box = new AnalysisMoveBox(p);
    this.refreshLocation();
  }

  void toggleMenuForm() {
    if (this.state == ChessState.ANALYSIS || this.chessboard.game_ended != null) {
      this.move_box.menu_form.toggle(ChessState.ANALYSIS);
    }
    else {
      this.move_box.menu_form.toggle(this.state);
    }
  }

  void startGameVsComputer() {
    this.startGameVsComputer(false);
  }
  void startGameVsComputer(boolean restarted) {
    if (this.state.playingGame() && this.chessboard.game_ended == null) {
      p.global.errorMessage("ERROR: Can't start game vs computer while in game.");
      return;
    }
    this.state = ChessState.HUMAN_VS_COMPUTER;
    this.move_box = new PlayingMoveBox(p);
    this.chessboard.setupBoard();
    this.initialChessboardView();
    this.chess_ai.reset();
    this.opposing_chess_ai = null;
    if (restarted && this.chessboard.human_controlled == HumanMovable.WHITE) {
      this.chessboard.human_controlled = HumanMovable.BLACK;
      this.chessboard.orientation = BoardOrientation.LEFT;
      this.chess_ai.decision_algorithm = DecisionAlgorithm.BEST_MOVE_WHITE;
      this.startComputersTurn();
    }
    else if ((restarted && this.chessboard.human_controlled == HumanMovable.BLACK) || Misc.randomChance(0.5)) {
      this.chessboard.human_controlled = HumanMovable.WHITE;
      this.chessboard.orientation = BoardOrientation.RIGHT;
      this.chess_ai.decision_algorithm = DecisionAlgorithm.BEST_MOVE_BLACK;
    }
    else {
      this.chessboard.human_controlled = HumanMovable.BLACK;
      this.chessboard.orientation = BoardOrientation.LEFT;
      this.chess_ai.decision_algorithm = DecisionAlgorithm.BEST_MOVE_WHITE;
      this.startComputersTurn();
    }
    this.refreshLocation();
  }

  void startComputerVsComputerGame() {
    if (this.state.playingGame() && this.chessboard.game_ended == null) {
      p.global.errorMessage("ERROR: Can't start computer vs computer during a game.");
      return;
    }
    this.state = ChessState.COMPUTER_VS_COMPUTER;
    this.move_box = new PlayingMoveBox(p);
    this.chessboard.setupBoard();
    this.initialChessboardView();
    this.chess_ai.reset(); // white
    this.opposing_chess_ai = new ChessAI(p); // black
    this.chessboard.human_controlled = HumanMovable.NONE;
    this.chessboard.orientation = BoardOrientation.RIGHT;
    this.startComputersTurn();
    this.refreshLocation();
  }

  void startHumanVsHumanGame() {
    if (this.state.playingGame() && this.chessboard.game_ended == null) {
      p.global.errorMessage("ERROR: Can't start computer vs computer during a game.");
      return;
    }
    this.state = ChessState.HUMAN_VS_HUMAN;
    this.move_box = new PlayingMoveBox(p);
    this.chessboard.setupBoard();
    this.initialChessboardView();
    this.chess_ai.reset(); // white
    this.opposing_chess_ai = null;
    this.chessboard.human_controlled = HumanMovable.BOTH;
    this.chessboard.orientation = BoardOrientation.RIGHT;
    this.refreshLocation();
  }

  void offerDraw() {
    if (!this.state.playingGame() || this.chessboard.game_ended != null) {
      p.global.errorMessage("ERROR: Can't offer draw not during a game.");
      return;
    }
    if (this.chessboard.humanCanMakeMove()) {
      this.chessboard.offerDraw();
      if (this.chessboard.game_ended != null) {
        this.move_box.gameEnded(this.chessboard.game_ended);
      }
    }
  }

  void resign() {
    if (!this.state.playingGame() || this.chessboard.game_ended != null) {
      p.global.errorMessage("ERROR: Can't resign not during a game.");
      return;
    }
    if (this.chessboard.humanCanMakeMove()) {
      this.chessboard.resign();
      if (this.chessboard.game_ended != null) {
        this.move_box.gameEnded(this.chessboard.game_ended);
      }
    }
  }

  void stopGame() {
    this.chessboard.human_controlled = HumanMovable.BOTH;
    this.move_box.adjustButtonsForGameEnd();
  }

  void drawBottomPanel(int time_elapsed) {}
  void setDependencyLocations(double xi, double yi, double xf, double yf) {
    this.chessboard.setLocation(xi + LNZ.minigames_chessPanelsSize +
      LNZ.minigames_edgeGap, yi + LNZ.minigames_edgeGap, xf -
      LNZ.minigames_chessPanelsSize - LNZ.minigames_edgeGap, yf -
      LNZ.minigames_edgeGap);
    this.refreshChessboardViewLocation();
    this.move_box.setLocation(xf - LNZ.minigames_chessPanelsSize, yi, xf, yf);
  }
  void refreshChessboardViewLocation() {
    if (this.chessboardView() == null) {
      return;
    }
    this.chessboardView().setLocation(this.xi + LNZ.minigames_chessPanelsSize +
      LNZ.minigames_edgeGap, this.yi + LNZ.minigames_edgeGap, this.xf -
      LNZ.minigames_chessPanelsSize - LNZ.minigames_edgeGap, this.yf -
      LNZ.minigames_edgeGap);
    this.chessboardView().orientation = this.chessboard.orientation;
  }
  void restartTimers() {}
  void displayNerdStats() {
    p.fill(255);
    p.textSize(14);
    p.textAlign(PConstants.LEFT, PConstants.TOP);
    float y_stats = 1;
    float line_height = p.textAscent() + p.textDescent() + 2;
    p.text("FPS: " + (int)p.global.lastFPS, 1, y_stats);
  }
  boolean leftPanelElementsHovered() {
    return false;
  }
  FormLNZ getEscForm() {
    return null;
  }

  void startComputersTurn() {
    this.computers_turn = true;
    this.computers_time_left = 100;
  }

  void update(int time_elapsed) {
    if (this.current_view < this.chessboard_views.size() - 1) {
      this.chessboardView().update(time_elapsed);
      this.chessboard.updateWithoutDisplay(time_elapsed);
    }
    else {
      this.chessboard.update(time_elapsed);
    }
    if (this.computers_turn) {
      this.computers_time_left -= time_elapsed;
      if (this.computers_time_left < 0) {
        this.computers_turn = false;
        ChessMove computers_move = this.chess_ai.getMove();
        if (computers_move == null) {
          if (this.chessboard.game_ended == null) {
            p.global.errorMessage("ERROR: Chess AI returned null move when game not over.");
            this.chessboard.makeRandomMove();
          }
        }
        else if (chessboard.valid_moves.contains(computers_move)) {
          this.chessboard.makeMove(computers_move);
        }
        else {
          String move_string = "Null";
          if (computers_move != null) {
            move_string = computers_move.pgnString();
          }
          String valid_moves = "";
          for (ChessMove move : this.chessboard.valid_moves) {
            valid_moves += "\n" + move.pgnString();
          }
          p.global.errorMessage("ERROR: Chess AI returned invalid move:\n" + move_string +
            "\n\nValid moves are:" + valid_moves);
          this.chessboard.makeRandomMove();
        }
      }
    }
    while (this.chessboard.move_queue.peek() != null) {
      ChessMove move = this.chessboard.move_queue.poll();
      this.chess_ai.addMove(move);
      this.updateView(move);
      this.move_box.addMove(move, this.chessboard_views.get(this.chessboard_views.size() - 1).in_check);
      if (this.chessboard.game_ended != null) {
        this.move_box.gameEnded(this.chessboard.game_ended);
      }
      else if (this.chessboard.computersTurn()) {
        this.startComputersTurn();
      }
    }
    this.move_box.update(time_elapsed);
    this.chess_ai.update(time_elapsed);
    p.fill(DImg.ccolor(255));
    p.textSize(20);
    p.textAlign(PConstants.RIGHT, PConstants.TOP);
    float curr_x = 26;
    p.text(this.chess_ai.head_node.evaluation.displayString(), this.chessboard.xi - 4, curr_x);
    if (this.chess_ai.thread == null) {
      return;
    }
    curr_x += 26;
    p.textSize(18);
    p.text("at depth: " + this.chess_ai.thread.current_depth, this.chessboard.xi - 4, curr_x);
    curr_x += 22;
    p.text("nodes: " + this.chess_ai.thread.nodes_evaluated, this.chessboard.xi - 4, curr_x);
    curr_x += 22;
    p.text("nodes / s: " + this.chess_ai.thread.nodes_per_second, this.chessboard.xi - 4, curr_x);
  }
  void mouseMove(float mX, float mY) {
    this.chessboard.mouseMove(mX, mY);
    this.move_box.mouseMove(mX, mY);
  }
  void mousePress() {
    this.chessboard.mousePress();
    this.move_box.mousePress();
  }
  void mouseRelease(float mX, float mY) {
    this.chessboard.mouseRelease(mX, mY);
    this.move_box.mouseRelease(mX, mY);
  }
  void scroll(int amount) {
    this.move_box.scroll(amount);
  }
  void keyPress(int key, int keyCode) {
    this.move_box.keyPress(key, keyCode);
  }
  void keyRelease(int key, int keyCode) {
    this.move_box.keyRelease(key, keyCode);
  }

  void loseFocus() {}
  void gainFocus() {}
}