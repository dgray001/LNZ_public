package Element;

import processing.core.*;
import LNZApplet.LNZApplet;
import Button.RectangleButton;
import DImg.DImg;

public class Panel {
  class PanelButton extends RectangleButton {
    protected double image_rotation = 0;
    protected double image_rotation_speed = 0.01;
    protected double image_rotation_target = 0;
    protected PImage icon;
    protected boolean removed = false;

    PanelButton(LNZApplet sketch) {
      super(sketch, 0, 0, 0, 0);
      this.setColors(DImg.ccolor(220), DImg.ccolor(1, 0), DImg.ccolor(170, 80), DImg.ccolor(170, 180), DImg.ccolor(0));
      this.noStroke();
      this.roundness = 0;
      this.hover_check_after_release = false;
    }

    @Override
    public void update(int millis) {
      if (this.removed) {
        return;
      }
      super.update(millis);
      if (this.icon != null) {
        double rotate_change = (millis - Panel.this.lastUpdateTime) * this.image_rotation_speed;
        if (this.image_rotation < this.image_rotation_target) {
          this.image_rotation += rotate_change;
          if (this.image_rotation > this.image_rotation_target) {
            this.image_rotation = this.image_rotation_target;
          }
        }
        else if (this.image_rotation > this.image_rotation_target) {
          this.image_rotation -= rotate_change;
          if (this.image_rotation < this.image_rotation_target) {
            this.image_rotation = this.image_rotation_target;
          }
        }
        p.translate(this.xCenter(), this.yCenter());
        p.rotate(this.image_rotation);
        p.imageMode(PConstants.CENTER);
        p.image(this.icon, 0, 0, this.buttonWidth(), this.buttonHeight());
        p.rotate(-this.image_rotation);
        p.translate(-this.xCenter(), -this.yCenter());
      }
    }

    public void hover() {}
    public void dehover() {}
    public void click() {}
    public void release() {
      if (this.removed) {
        return;
      }
      if (this.hovered) {
        Panel.this.collapse();
        this.hovered = false;
      }
    }
  }
	
	protected LNZApplet p;

  protected int location;
  protected double size_min;
  protected double size_max;
  public double size_curr;
  public double size;

  public boolean hovered = false;
  public boolean cant_resize = false;
  public boolean clicked = false;
  protected double hovered_delta = 5;

  public boolean open = true;
  public boolean collapsing = false;
  protected double collapse_speed = 1.2;
  protected int lastUpdateTime = 0;
  protected PImage img;

  protected PanelButton button;
  protected double panelButtonSize = 30;

  public int color_background = DImg.ccolor(220);

  public Panel(LNZApplet sketch, int location, double size) {
    this(sketch, location, size, size, size);
  }
  public Panel(LNZApplet sketch, int location, double size_min, double size_max, double size) {
    this.p = sketch;
    this.button = new PanelButton(sketch);
    switch(location) {
      case PConstants.LEFT:
      case PConstants.RIGHT:
      case PConstants.UP:
      case PConstants.DOWN:
        this.location = location;
        break;
      default:
        this.location = PConstants.LEFT;
        break;
    }
    this.size_min = size_min;
    this.size_max = size_max;
    this.size_curr = size;
    this.size = size;
    this.resetButtonLocation();
  }

  void resetButtonLocation() {
    switch(this.location) {
      case PConstants.LEFT:
        this.button.setLocation(this.size, 0, this.size + this.panelButtonSize, this.panelButtonSize);
        if (this.open) {
          this.button.image_rotation_target = -PConstants.HALF_PI;
        }
        else {
          this.button.image_rotation_target = PConstants.HALF_PI;
        }
        break;
      case PConstants.RIGHT:
        this.button.setLocation(p.width - this.size - this.panelButtonSize, 0, p.width - this.size, this.panelButtonSize);
        if (this.open) {
          this.button.image_rotation_target = PConstants.HALF_PI;
        }
        else {
          this.button.image_rotation_target = -PConstants.HALF_PI;
        }
        break;
      case PConstants.UP:
        this.button.setLocation(p.width - this.panelButtonSize, this.size, p.width, this.size + this.panelButtonSize);
          if (this.open) {
            this.button.image_rotation_target = 0;
          }
          else {
            this.button.image_rotation_target = Math.PI;
          }
        break;
      case PConstants.DOWN:
        this.button.setLocation(p.width - this.panelButtonSize, p.height - this.size - this.panelButtonSize, p.width, p.height - this.size);
          if (this.open) {
            this.button.image_rotation_target = Math.PI;
          }
          else {
            this.button.image_rotation_target = 0;
          }
        break;
    }
  }
  public void addIcon(PImage icon) {
    this.button.icon = icon;
  }
  public void removeButton() {
    this.button.removed = true;
  }

  public void changeSize(double size_delta) {
    if (this.size_curr + size_delta > this.size_max) {
      this.size_curr = this.size_max;
    }
    else if (this.size_curr + size_delta < this.size_min) {
      this.size_curr = this.size_min;
    }
    else {
      this.size_curr += size_delta;
    }
    if (this.open) {
      this.size = this.size_curr;
    }
    this.resetButtonLocation();
  }

  void collapse() {
    this.collapsing = true;
    switch(this.location) {
      case PConstants.LEFT:
        this.img = p.getCurrImage(0, 0, (int)this.size_curr, p.height);
        break;
      case PConstants.RIGHT:
        this.img = p.getCurrImage(p.width - (int)this.size_curr, 0, p.width, p.height);
        break;
      case PConstants.UP:
        this.img = p.getCurrImage(0, 0, p.width, (int)this.size_curr);
        break;
      case PConstants.DOWN:
        this.img = p.getCurrImage(0, p.height - (int)this.size_curr, p.width, p.height);
        break;
    }
  }

  public void update(int millis) {
    int timeElapsed = millis - this.lastUpdateTime;
    this.button.update(millis);
    p.rectMode(PConstants.CORNER);
    p.fill(this.color_background);
    p.noStroke();
    switch(this.location) {
      case PConstants.LEFT:
        p.rect(0, 0, this.size, p.height);
        break;
      case PConstants.RIGHT:
        p.rect(p.width - this.size, 0, this.size, p.height);
        break;
      case PConstants.UP:
        p.rect(0, 0, p.width, this.size);
        break;
      case PConstants.DOWN:
        p.rect(0, p.height - this.size, p.width, this.size);
        break;
    }
    if (this.collapsing) {
      this.button.clicked = false;
      this.button.hovered = false;
      double buttonMove = 0;
      boolean buttonReset = false;
      if (this.open) {
        buttonMove = -this.collapse_speed * timeElapsed;
        this.size += buttonMove;
        if (this.size < 0) {
          this.size = 0;
          this.open = false;
          this.collapsing = false;
          this.resetButtonLocation();
          buttonReset = true;
        }
        if (this.img != null) {
          p.imageMode(PConstants.CORNER);
          switch(this.location) {
            case PConstants.LEFT:
              p.image(this.img, this.size - this.size_curr, 0);
              break;
            case PConstants.RIGHT:
              p.image(this.img, p.width - this.size, 0);
              break;
            case PConstants.UP:
              p.image(this.img, 0, this.size - this.size_curr);
              break;
            case PConstants.DOWN:
              p.image(this.img, 0, p.height - this.size);
              break;
          }
        }
      }
      else {
        buttonMove = this.collapse_speed * timeElapsed;
        this.size += buttonMove;
        if (this.size > this.size_curr) {
          this.size = this.size_curr;
          this.open = true;
          this.collapsing = false;
          this.resetButtonLocation();
          buttonReset = true;
        }
      }
      if (!buttonReset) {
        switch(this.location) {
          case PConstants.LEFT:
            this.button.moveButton(buttonMove, 0);
            break;
          case PConstants.RIGHT:
            this.button.moveButton(-buttonMove, 0);
            break;
          case PConstants.UP:
            this.button.moveButton(0, buttonMove);
            break;
          case PConstants.DOWN:
            this.button.moveButton(0, -buttonMove);
            break;
        }
      }
    }
    this.lastUpdateTime = millis;
  }

  public void mouseMove(float mX, float mY) {
    this.button.mouseMove(mX, mY);
    if (this.cant_resize) {
      return;
    }
    if (!this.open || this.button.hovered) {
      this.hovered = false;
      return;
    }
    switch(this.location) {
      case PConstants.LEFT:
        if (this.clicked) {
          this.changeSize(mX - this.size);
        }
        else if (Math.abs(mX - this.size) < this.hovered_delta) {
          this.hovered = true;
        }
        else {
          this.hovered = false;
        }
        break;
      case PConstants.RIGHT:
        if (this.clicked) {
          this.changeSize(p.width - this.size - mX);
        }
        else if (Math.abs(mX - p.width + this.size) < this.hovered_delta) {
          this.hovered = true;
        }
        else {
          this.hovered = false;
        }
        break;
      case PConstants.UP:
        if (this.clicked) {
          this.changeSize(mY - this.size);
        }
        else if (Math.abs(mY - this.size) < this.hovered_delta) {
          this.hovered = true;
        }
        else {
          this.hovered = false;
        }
        break;
      case PConstants.DOWN:
        if (this.clicked) {
          this.changeSize(p.height - this.size - mY);
        }
        else if (Math.abs(mY - p.height + this.size) < this.hovered_delta) {
          this.hovered = true;
        }
        else {
          this.hovered = false;
        }
        break;
    }
  }

  public void mousePress() {
    this.button.mousePress();
    if (this.hovered && p.mouseButton == PConstants.LEFT) {
      this.clicked = true;
    }
  }

  public void mouseRelease(float mX, float mY) {
    this.button.mouseRelease(mX, mY);
    this.clicked = false;
    this.mouseMove(mX, mY);
  }
}