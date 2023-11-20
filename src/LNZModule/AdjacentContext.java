package LNZModule;

import processing.core.PConstants;

enum AdjacentContextPossibilities {
  CENTER, LEFT, RIGHT, UP, DOWN, STRAIGHT_LEFT, STRAIGHT_UP, CORNER_LEFT,
  CORNER_RIGHT, CORNER_UP, CORNER_DOWN, DOUBLECORNER_LEFT, DOUBLECORNER_RIGHT,
  DOUBLECORNER_UP, DOUBLECORNER_DOWN, DOUBLE_STRAIGHT, ERROR;

  static String fileString(AdjacentContextPossibilities possibility) {
    switch(possibility) {
      case CENTER:
        return "center";
      case LEFT:
        return "left";
      case RIGHT:
        return "right";
      case UP:
        return "up";
      case DOWN:
        return "down";
      case STRAIGHT_LEFT:
        return "straight_left";
      case STRAIGHT_UP:
        return "straight_up";
      case CORNER_LEFT:
        return "corner_left";
      case CORNER_RIGHT:
        return "corner_right";
      case CORNER_UP:
        return "corner_up";
      case CORNER_DOWN:
        return "corner_down";
      case DOUBLECORNER_LEFT:
        return "doublecorner_left";
      case DOUBLECORNER_RIGHT:
        return "doublecorner_right";
      case DOUBLECORNER_UP:
        return "doublecorner_up";
      case DOUBLECORNER_DOWN:
        return "doublecorner_down";
      case DOUBLE_STRAIGHT:
        return "double_straight";
      default:
        return "error";
    }
  }
}

class AdjacentContext {
  String filestring = AdjacentContextPossibilities.fileString(AdjacentContextPossibilities.CENTER);
  boolean left = false;
  boolean right = false;
  boolean up = false;
  boolean down = false;

  @Override
  public String toString() {
    return this.filestring + " (" + this.left + ", " + this.right + ", " +
      this.up + ", " + this.down + ")";
  }

  void set(int location, boolean b) {
    switch(location) {
      case PConstants.LEFT:
        this.left = b;
        break;
      case PConstants.RIGHT:
        this.right = b;
        break;
      case PConstants.UP:
        this.up = b;
        break;
      case PConstants.DOWN:
        this.down = b;
        break;
      default:
        break;
    }
  }

  void setFileString() {
    this.filestring = AdjacentContextPossibilities.fileString(AdjacentContext.possibility(this));
  }

  static AdjacentContextPossibilities possibility(AdjacentContext c) {
    if (c.left && c.right && c.up && c.down) {
      return AdjacentContextPossibilities.DOUBLE_STRAIGHT;
    }
    if (!c.left && c.right && c.up && c.down) {
      return AdjacentContextPossibilities.DOUBLECORNER_LEFT;
    }
    if (c.left && !c.right && c.up && c.down) {
      return AdjacentContextPossibilities.DOUBLECORNER_RIGHT;
    }
    if (c.left && c.right && !c.up && c.down) {
      return AdjacentContextPossibilities.DOUBLECORNER_UP;
    }
    if (c.left && c.right && c.up && !c.down) {
      return AdjacentContextPossibilities.DOUBLECORNER_DOWN;
    }
    if (!c.left && !c.right && c.up && c.down) {
      return AdjacentContextPossibilities.STRAIGHT_UP;
    }
    if (c.left && !c.right && !c.up && c.down) {
      return AdjacentContextPossibilities.CORNER_LEFT;
    }
    if (c.left && c.right && !c.up && !c.down) {
      return AdjacentContextPossibilities.STRAIGHT_LEFT;
    }
    if (!c.left && c.right && c.up && !c.down) {
      return AdjacentContextPossibilities.CORNER_RIGHT;
    }
    if (c.left && !c.right && c.up && !c.down) {
      return AdjacentContextPossibilities.CORNER_UP;
    }
    if (!c.left && c.right && !c.up && c.down) {
      return AdjacentContextPossibilities.CORNER_DOWN;
    }
    if (c.left && !c.right && !c.up && !c.down) {
      return AdjacentContextPossibilities.LEFT;
    }
    if (!c.left && c.right && !c.up && !c.down) {
      return AdjacentContextPossibilities.RIGHT;
    }
    if (!c.left && !c.right && c.up && !c.down) {
      return AdjacentContextPossibilities.UP;
    }
    if (!c.left && !c.right && !c.up && c.down) {
      return AdjacentContextPossibilities.DOWN;
    }
    if (!c.left && !c.right && !c.up && !c.down) {
      return AdjacentContextPossibilities.CENTER;
    }
    return AdjacentContextPossibilities.ERROR;
  }
}
