package Element;

import java.util.*;
import processing.core.*;
import LNZApplet.LNZApplet;
import DImg.DImg;

public abstract class ListTextBox extends TextBox {
  protected ArrayList<String> text_lines_ref;
  protected int line_hovered = -1;
  protected int line_clicked = -1;
  protected int hover_color = DImg.ccolor(180, 180, 200, 60);
  protected int highlight_color = DImg.ccolor(100, 100, 250, 120);
  protected int doubleclick_timer = 0;
  protected int doubleclick_time = 400;
  protected boolean can_unclick_outside_box = true;

  public ListTextBox(LNZApplet sketch) {
    this(sketch, 0, 0, 0, 0);
  }
  public ListTextBox(LNZApplet sketch, double xi, double yi, double xf, double yf) {
    super(sketch, xi, yi, xf, yf);
  }

  @Override
  public void clearText() {
    this.setText("");
    this.text_lines.clear();
  }

  @Override
  public void setText(String text) {
    this.text_ref = text;
    this.text_lines.clear();
    this.text_lines_ref = new ArrayList<String>();
    double currY = this.yi + 1;
    if (this.text_title_ref != null) {
      p.textSize(this.title_size);
      currY += p.textAscent() + p.textDescent() + 2;
    }
    p.textSize(this.text_size);
    float text_height = p.textAscent() + p.textDescent();
    double effective_xf = this.xf - this.xi - 3 - this.scrollbar.bar_size;
    int lines_above = 0;
    String[] lines = PApplet.split(text, '\n');
    for (String line : lines) {
      this.text_lines_ref.add(line);
      String currLine = "";
      for (int i = 0; i < line.length(); i++) {
        char nextChar = line.charAt(i);
        if (p.textWidth(currLine + nextChar) < effective_xf) {
          currLine += nextChar;
        }
        else {
          break;
        }
      }
      this.text_lines.add(currLine);
      if (currY + text_height + 1 > this.yf) {
        lines_above++;
      }
      currY += text_height + this.text_leading;
    }
    this.scrollbar.updateMaxValue(lines_above);
  }

  public void addLine(String line) {
    if (this.text_ref == null || this.text_ref.equals("")) {
      this.setText(line);
    }
    else {
      this.addText("\n" + line);
    }
  }

  public String highlightedLine() {
    if (this.line_clicked < 0 || this.line_clicked >= this.text_lines_ref.size()) {
      return null;
    }
    return this.text_lines_ref.get(this.line_clicked);
  }

  @Override
  public void update(int millis) {
    int time_elapsed = millis - this.last_update_time;
    if (this.use_time_elapsed) {
      time_elapsed = millis;
    }
    super.update(millis);
    if (this.doubleclick_timer > 0) {
      this.doubleclick_timer -= time_elapsed;
    }
    double currY = this.yi + 1;
    if (this.text_title_ref != null) {
      p.textSize(this.title_size);
      currY += p.textAscent() + p.textDescent() + 2;
    }
    p.textSize(this.text_size);
    float text_height = p.textAscent() + p.textDescent();
    if (this.line_hovered >= Math.floor(this.scrollbar.value)) {
      double hovered_yi = currY + (this.line_hovered - Math.floor(this.scrollbar.value)) * (text_height + this.text_leading);
      if (hovered_yi + text_height + 1 < this.yf) {
        p.rectMode(PConstants.CORNERS);
        p.fill(this.hover_color);
        p.strokeWeight(0.001);
        p.stroke(this.hover_color);
        p.rect(this.xi + 1, hovered_yi, this.xf - 2 - this.scrollbar.bar_size, hovered_yi + text_height);
      }
    }
    if (this.line_clicked >= Math.floor(this.scrollbar.value)) {
      double clicked_yi = currY + (this.line_clicked - Math.floor(this.scrollbar.value)) * (text_height + this.text_leading);
      if (clicked_yi + text_height + 1 < this.yf) {
        p.rectMode(PConstants.CORNERS);
        p.fill(this.highlight_color);
        p.strokeWeight(0.001);
        p.stroke(this.highlight_color);
        p.rect(this.xi + 1, clicked_yi, this.xf - 2 - this.scrollbar.bar_size, clicked_yi + text_height);
      }
    }
  }

  @Override
  public void mouseMove(float mX, float mY) {
    this.scrollbar.mouseMove(mX, mY);
    if (mX > this.xi && mX < this.xf && mY > this.yi && mY < this.yf) {
      this.hovered = true;
      double currY = this.yi + 1;
      if (this.text_title_ref != null) {
        p.textSize(this.title_size);
        currY += p.textAscent() + p.textDescent() + 2;
      }
      p.textSize(this.text_size);
      double line_height = p.textAscent() + p.textDescent() + this.text_leading;
      int target_line = (int)(Math.floor(this.scrollbar.value) + Math.floor((mY - currY) / line_height));
      int lines_shown = this.text_lines.size() - (int)this.scrollbar.maxValue;
      if (target_line < 0 || mX > (this.xf - this.scrollbar.bar_size) || target_line >= this.text_lines_ref.size() ||
        target_line - (int)this.scrollbar.value >= lines_shown) {
        this.line_hovered = -1;
      }
      else {
        this.line_hovered = target_line;
      }
    }
    else {
      this.hovered = false;
      this.line_hovered = -1;
    }
  }

  @Override
  public void mousePress() {
    super.mousePress();
    if (this.line_hovered > -1) {
      if (this.doubleclick_timer > 0  && this.line_clicked == this.line_hovered) {
        this.line_clicked = this.line_hovered;
        this.doubleclick();
        this.doubleclick_timer = 0;
      }
      else {
        this.line_clicked = this.line_hovered;
        this.click();
        this.doubleclick_timer = this.doubleclick_time;
      }
    }
    else if (this.can_unclick_outside_box || this.hovered) {
      this.line_clicked = this.line_hovered;
    }
  }

  @Override
  public void mouseRelease(float mX, float mY) {
    if (this.line_hovered < 0 && !this.scrollbar.clicked() && (this.can_unclick_outside_box || this.hovered)) {
      this.line_clicked = this.line_hovered;
    }
    super.mouseRelease(mX, mY);
  }

  public void jump_to_line() {
    this.jump_to_line(false);
  }
  public void jump_to_line(boolean hard_jump) {
    if (this.line_clicked < 0) {
      return;
    }
    if (hard_jump || this.line_clicked < (int)this.scrollbar.value) {
      this.scrollbar.updateValue(this.line_clicked);
      return;
    }
    int lines_shown = this.text_lines.size() - (int)this.scrollbar.maxValue;
    if (this.line_clicked >= (int)this.scrollbar.value + lines_shown) {
      this.scrollbar.increaseValue(1 + this.line_clicked - (int)this.scrollbar.value - lines_shown);
    }
    else if (this.line_clicked < (int)this.scrollbar.value) {
      this.scrollbar.decreaseValue((int)this.scrollbar.value - this.line_clicked);
    }
  }

  @Override
  public void keyPress(int key, int keyCode) {
    if (!this.hovered) {
      return;
    }
    if (key == PConstants.CODED) {
      switch(keyCode) {
        case PConstants.UP:
          if (this.line_clicked > 0) {
            this.line_clicked--;
            this.jump_to_line(false);
          }
          break;
        case PConstants.DOWN:
          if (this.line_clicked < this.text_lines_ref.size() - 1) {
            this.line_clicked++;
            this.jump_to_line(false);
          }
          break;
        default:
          break;
      }
    }
  }

  public abstract void click(); // click on line
  public abstract void doubleclick(); // doubleclick on line
}