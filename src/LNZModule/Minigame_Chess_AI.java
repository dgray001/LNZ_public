package LNZModule;

import java.util.*;

enum DecisionAlgorithm {
  RANDOM, BEST_MOVE_WHITE, BEST_MOVE_BLACK; // weighted best move, play for draw, play to win, balanced, human-like?
}

class ChessAI {
  class EvaluationThread extends Thread {
    private Queue<ChessNode> nodes_to_evaluate = new ArrayDeque<ChessNode>();
    private boolean kill_thread = false;
    protected int current_depth = 0;
    protected int nodes_evaluated = 0;
    private int nodes_evaluated_this_second = 0;
    private int time_left = 1000;
    protected int nodes_per_second = 0;

    EvaluationThread() {
      super("EvaluationThread");
      this.setDaemon(true);
    }

    void update(int time_elapsed) {
      this.time_left -= time_elapsed;
      if (this.time_left <= 0) {
        this.time_left += 1000;
        this.nodes_per_second = this.nodes_evaluated_this_second;
        this.nodes_evaluated_this_second = 0;
      }
    }

    @Override
    public void run() {
      this.nodes_to_evaluate.add(ChessAI.this.head_node);
      while(!this.nodes_to_evaluate.isEmpty()) {
        if (this.kill_thread) {
          return;
        }
        ChessNode next_node = this.nodes_to_evaluate.poll();
        if (next_node == null) {
          continue;
        }
        next_node.evaluate(EvaluationAlgorithm.MATERIAL, true);
        next_node.makeDaughters();
        this.current_depth = next_node.tree_depth;
        for (ChessNode daughter : next_node.daughters.values()) {
          this.nodes_to_evaluate.add(daughter);
        }
        this.nodes_evaluated++;
        this.nodes_evaluated_this_second++;
      }
    }
  }

  private LNZ p;

  protected ChessNode head_node;
  protected DecisionAlgorithm decision_algorithm = DecisionAlgorithm.RANDOM;
  protected EvaluationThread thread = null;

  ChessAI(LNZ sketch) {
    this.p = sketch;
    this.reset();
  }

  void update(int time_elapsed) {
    if (this.thread != null) {
      this.thread.update(time_elapsed);
    }
  }

  void reset() {
    this.head_node = new ChessNode(p, new ChessBoard(p), null, null);
    this.head_node.board.setupBoard();
    this.thread = new EvaluationThread();
    this.thread.start();
  }

  void addMove(ChessMove move) {
    ChessNode daughter = this.head_node.getDaughter(move);
    if (daughter == null) {
      p.global.errorMessage("ERROR: GameTree corrupted; can't find appropriate daughter node.");
      return;
    }
    this.head_node = daughter;
    this.head_node.parent = null;
    this.head_node.source_move = null;
    this.restartThread();
  }

  void restartThread() {
    if (this.thread.isAlive()) {
      this.thread.kill_thread = true;
    }
    this.thread = new EvaluationThread();
    this.thread.start();
  }

  ChessMove getMove() {
    ArrayList<ChessMove> possible_moves = new ArrayList<ChessMove>(this.head_node.board.valid_moves);
    if (possible_moves.size() == 0) {
      return null;
    }
    DecisionAlgorithm algorithm = this.decision_algorithm;
    if (!this.head_node.made_daughters) {
      algorithm = DecisionAlgorithm.RANDOM;
    }
    ChessMove best_move = null;
    switch(algorithm) {
      case BEST_MOVE_WHITE:
        if (this.head_node.board.turn != ChessColor.WHITE) {
          p.global.log("WARNING: Using BEST_MOVE_WHITE but not white's turn.");
        }
        best_move = possible_moves.get(0);
        for (ChessMove move : possible_moves) {
          ChessNode daughter = this.head_node.getDaughter(move);
          if (daughter == null) {
            continue;
          }
          if (daughter.evaluation.betterForWhite(this.head_node.daughters.get(best_move).evaluation)) {
            best_move = move;
          }
        }
        return best_move;
      case BEST_MOVE_BLACK:
        if (this.head_node.board.turn != ChessColor.WHITE) {
          p.global.log("WARNING: Using BEST_MOVE_BLACK but not black's turn.");
        }
        best_move = possible_moves.get(0);
        if (this.head_node.getDaughter(best_move) == null) {
          break;
        }
        for (ChessMove move : possible_moves) {
          ChessNode daughter = this.head_node.getDaughter(move);
          if (daughter == null) {
            continue;
          }
          if (daughter.evaluation.betterForBlack(this.head_node.daughters.get(best_move).evaluation)) {
            best_move = move;
          }
        }
        return best_move;
      case RANDOM:
      default:
        break;
    }
    Collections.shuffle(possible_moves);
    return possible_moves.get(0);
  }
}


enum EvaluationAlgorithm {
  NONE, MATERIAL, CHECKMATE, MATERIAL_CHECKMATE;
}


class ChessEvaluation {
  protected float evaluation = 0;
  protected boolean game_ended = false;
  protected int game_ended_result = 0;

  ChessEvaluation copy() {
    ChessEvaluation copied = new ChessEvaluation();
    copied.evaluation = this.evaluation;
    copied.game_ended = this.game_ended;
    copied.game_ended_result = this.game_ended_result;
    return copied;
  }

  String displayString() {
    if (this.game_ended) {
      if (game_ended_result > 0) {
        return "White Wins";
      }
      if (game_ended_result < 0) {
        return "Black Wins";
      }
      return "Draw";
    }
    return Double.toString(Math.round(10.0 * evaluation) / 10.0);
  }

  boolean betterForWhite(ChessEvaluation evaluation) {
    // white wins
    if (this.game_ended && this.game_ended_result > 0 && (!evaluation.game_ended || evaluation.game_ended_result <= 0)) {
      return true;
    }
    // white prevents black win
    if (evaluation.game_ended && evaluation.game_ended_result < 0 && (!this.game_ended || this.game_ended_result >= 0)) {
      return true;
    }
    // white better
    if (this.evaluation > evaluation.evaluation) {
      return true;
    }
    // else
    return false;
  }

  boolean betterForBlack(ChessEvaluation evaluation) {
    // black wins
    if (this.game_ended && this.game_ended_result < 0 && (!evaluation.game_ended || evaluation.game_ended_result >= 0)) {
      return true;
    }
    // black prevents white win
    if (evaluation.game_ended && evaluation.game_ended_result > 0 && (!this.game_ended || this.game_ended_result <= 0)) {
      return true;
    }
    // black better
    if (this.evaluation < evaluation.evaluation) {
      return true;
    }
    // else
    return false;
  }
}


class ChessNode {
  private LNZ p;
  protected ChessNode parent = null;
  protected int tree_depth = 0;
  protected ChessMove source_move = null;
  protected ChessBoard board;
  protected Map<ChessMove, ChessNode> daughters = new HashMap<ChessMove, ChessNode>();
  protected boolean made_daughters = false;
  protected boolean evaluated = false;
  protected ChessEvaluation base_evaluation = new ChessEvaluation(); // from this node only
  protected ChessEvaluation evaluation = new ChessEvaluation(); // from daughter nodes

  ChessNode(LNZ sketch, ChessBoard board, ChessNode parent, ChessMove source_move) {
    this.p = sketch;
    this.board = board;
    this.parent = parent;
    this.source_move = source_move;
  }

  ChessNode getDaughter(ChessMove move) {
    if (!this.made_daughters) {
      if (this.board.valid_moves.contains(move)) {
        ChessBoard copied = new ChessBoard(p, this.board);
        copied.makeMove(move, false);
        return new ChessNode(p, copied, this, move);
      }
      else {
        return null;
      }
    }
    ChessNode daughter = this.daughters.get(move);
    if (daughter == null) { // race condition
      if (this.board.valid_moves.contains(move)) {
        ChessBoard copied = new ChessBoard(p, this.board);
        copied.makeMove(move, false);
        return new ChessNode(p, copied, this, move);
      }
      else {
        return null;
      }
    }
    return daughter;
  }

  void makeDaughters() {
    if (this.parent == null) {
      this.tree_depth = 0;
    }
    else {
      this.tree_depth = this.parent.tree_depth + 1;
    }
    if (this.made_daughters) {
      return;
    }
    if (!this.evaluated) {
      this.evaluate(EvaluationAlgorithm.NONE, true);
    }
    this.made_daughters = true;
    for (ChessMove move : this.board.valid_moves) {
      ChessBoard copied = new ChessBoard(p, this.board);
      copied.makeMove(move, false);
      this.daughters.put(move, new ChessNode(p, copied, this, move));
    }
  }

  void evaluate(EvaluationAlgorithm algorithm, boolean my_turn) {
    if (this.evaluated) {
      return;
    }
    this.evaluated = true;
    switch(algorithm) {
      case MATERIAL:
        this.base_evaluation.evaluation = this.board.materialDifference();
        break;
      case CHECKMATE:
        if (this.board.game_ended == null) {
          break;
        }
        this.base_evaluation.game_ended = true;
        this.base_evaluation.game_ended_result = this.board.game_ended.points();
        break;
      case MATERIAL_CHECKMATE:
        this.base_evaluation.evaluation = this.board.materialDifference();
        if (this.board.game_ended == null) {
          break;
        }
        this.base_evaluation.game_ended = true;
        this.base_evaluation.game_ended_result = this.board.game_ended.points();
        break;
      case NONE:
      default:
        break;
    }
    this.evaluation.evaluation = this.base_evaluation.evaluation;
    this.evaluation.game_ended = this.base_evaluation.game_ended;
    this.evaluation.game_ended_result = this.base_evaluation.game_ended_result;
    if (this.parent != null) {
      this.parent.daughterEvaluation(this.base_evaluation);
    }
  }

  void daughterEvaluation(ChessEvaluation evaluation) {
    if (this.board.turn == ChessColor.WHITE && evaluation.betterForWhite(this.evaluation)) {
      this.evaluation = evaluation.copy();
      if (this.parent != null) {
        this.parent.daughterEvaluation(evaluation);
      }
    }
    else if (this.board.turn == ChessColor.BLACK && evaluation.betterForBlack(this.evaluation)) {
      this.evaluation = evaluation.copy();
      if (this.parent != null) {
        this.parent.daughterEvaluation(evaluation);
      }
    }
  }
}