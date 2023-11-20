package LNZModule;

import DImg.DImg;

abstract class Minigame {
  protected LNZ p;

  protected boolean completed = false;
  protected MinigameName name;

  protected double xi = 0;
  protected double yi = 0;
  protected double xf = 0;
  protected double yf = 0;

  protected int color_background = DImg.ccolor(60);

  Minigame(LNZ sketch, MinigameName name) {
    this.p = sketch;
    this.name = name;
  }

  String displayName() {
    return this.name.displayName();
  }
  abstract void drawBottomPanel(int time_elapsed);
  void setLocation(double xi, double yi, double xf, double yf) {
    this.xi = xi;
    this.yi = yi;
    this.xf = xf;
    this.yf = yf;
    this.setDependencyLocations(xi, yi, xf, yf);
  }
  abstract void setDependencyLocations(double xi, double yi, double xf, double yf);
  void refreshLocation() {
    this.setLocation(this.xi, this.yi, this.xf, this.yf);
  }
  abstract void restartTimers();
  abstract void displayNerdStats();
  abstract boolean leftPanelElementsHovered();
  abstract FormLNZ getEscForm();

  abstract void update(int time_elapsed);
  abstract void mouseMove(float mX, float mY);
  abstract void mousePress();
  abstract void mouseRelease(float mX, float mY);
  abstract void scroll(int amount);
  abstract void keyPress(int key, int keyCode);
  abstract void keyRelease(int key, int keyCode);

  abstract void loseFocus();
  abstract void gainFocus();
}
