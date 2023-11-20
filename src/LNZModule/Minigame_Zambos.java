package LNZModule;

import processing.core.*;
import DImg.DImg;

enum ZambosMap {
  CAR_BREAK_DOWN, TOWN, FARM;

  public MinigameName mapToMinigameName() {
    return ZambosMap.mapToMinigameName(this);
  }
  public static MinigameName mapToMinigameName(ZambosMap map) {
    switch(map) {
      case CAR_BREAK_DOWN:
        return MinigameName.ZAMBOS_CAR_BREAK_DOWN;
      case TOWN:
        //return MinigameName.ZAMBOS_TOWN;
      case FARM:
        //return MinigameName.ZAMBOS_FARM;
    }
    return null;
  }
}


class Zambos extends Minigame {
  private ZambosMap map;

  Zambos(LNZ sketch, ZambosMap map) {
    super(sketch, map.mapToMinigameName());
  }

  void drawBottomPanel(int time_elapsed) {}
  void setDependencyLocations(double xi, double yi, double xf, double yf) {}
  void restartTimers() {}
  void displayNerdStats() {}
  boolean leftPanelElementsHovered() {
    return false;
  }
  FormLNZ getEscForm() {
    return null;
  }

  void update(int time_elapsed) {
    p.fill(DImg.ccolor(0));
    p.textSize(48);
    p.textAlign(PConstants.CENTER, PConstants.CENTER);
    p.text("Minigame will be added in future update", 0.5 * p.width, 0.5 * p.height);
  }
  void mouseMove(float mX, float mY) {}
  void mousePress() {}
  void mouseRelease(float mX, float mY) {}
  void scroll(int amount) {}
  void keyPress(int key, int keyCode) {}
  void keyRelease(int key, int keyCode) {}

  void loseFocus() {}
  void gainFocus() {}
}
