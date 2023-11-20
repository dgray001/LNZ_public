package Element;

import java.util.*;
import processing.core.*;
import LNZApplet.LNZApplet;
import DImg.DImg;

public class TextBox {
	
	protected LNZApplet p;

  public double xi = 0;
  public double yi = 0;
  public double xf = 0;
  public double yf = 0;
  public boolean hovered = false;
  protected int last_update_time = 0;
  protected boolean use_time_elapsed = false;

  public ScrollBar scrollbar;
  protected double scrollbar_max_width = 50;
  protected double scrollbar_min_width = 25;

  protected boolean word_wrap = true;
  protected ScrollBar scrollbar_horizontal;
  protected ArrayList<String> text_lines_display = new ArrayList<String>();

  public String text_ref = "";
  public ArrayList<String> text_lines = new ArrayList<String>();
  public double text_size = 15;
  public double text_leading = 0;

  protected String text_title_ref = null;
  public String text_title = null;
  protected double title_size = 22;

  public int color_background = DImg.ccolor(250);
  public int color_header = DImg.ccolor(200);
  public int color_stroke = DImg.ccolor(0);
  public int color_text = DImg.ccolor(0);
  public int color_title = DImg.ccolor(0);

  public TextBox(LNZApplet sketch) {
    this(sketch, 0, 0, 0, 0);
  }
  public TextBox(LNZApplet sketch, double xi, double yi, double xf, double yf) {
    this.p = sketch;
    this.scrollbar = new ScrollBar(sketch, xi, yi, xf, yf, true);
    this.setLocation(xi, yi, xf, yf);
  }

  public void scrollbarWidths(double scrollbar_max_width, double scrollbar_min_width) {
    this.scrollbar_max_width = scrollbar_max_width;
    this.scrollbar_min_width = scrollbar_min_width;
    this.refreshTitle();
  }

  public void useElapsedTime() {
    this.scrollbar.useElapsedTime();
    if (this.scrollbar_horizontal != null) {
      this.scrollbar_horizontal.useElapsedTime();
    }
    this.use_time_elapsed = true;
  }

  public void setXLocation(double xi, double xf) {
    this.setLocation(xi, this.yi, xf, this.yf);
  }
  public void setYLocation(double yi, double yf) {
    this.setLocation(this.xi, yi, this.xf, yf);
  }
  public void setLocation(double xi, double yi, double xf, double yf) {
    this.xi = xi;
    this.yi = yi;
    this.xf = xf;
    this.yf = yf;
    this.refreshTitle();
  }

  public void setTextSize(double text_size) {
    this.text_size = text_size;
    this.refreshText();
  }

  public void setTitleSize(double title_size) {
    this.title_size = title_size;
    this.refreshTitle();
  }

  void refreshTitle() {
    this.setTitleText(this.text_title_ref);
  }

  public void setTitleText(String title) {
    this.text_title_ref = title;
    double scrollbar_width = Math.min(this.scrollbar_max_width, 0.05 * (this.xf - this.xi));
    scrollbar_width = Math.max(this.scrollbar_min_width, scrollbar_width);
    scrollbar_width = Math.min(0.05 * (this.xf - this.xi), scrollbar_width);
    if (title == null) {
      this.text_title = null;
      this.scrollbar.setLocation(xf - scrollbar_width, this.yi, this.xf, this.yf);
    }
    else {
      this.text_title = "";
      p.textSize(this.title_size);
      for (int i = 0; i < title.length(); i++) {
        char nextChar = title.charAt(i);
        if (p.textWidth(this.text_title + nextChar) < this.xf - this.xi - 3) {
          this.text_title += nextChar;
        }
        else {
          break;
        }
      }
      this.scrollbar.setLocation(this.xf - scrollbar_width, this.yi + 1 + p.textAscent() + p.textDescent(), this.xf, this.yf);
    }
    if (!this.word_wrap) {
      this.scrollbar_horizontal.setLocation(this.xi, this.yf - this.scrollbar.bar_size, this.xf - this.scrollbar.bar_size, this.yf);
    }
    this.refreshText();
  }

  public void setWordWrap(boolean word_wrap) {
    this.word_wrap = word_wrap;
    if (!word_wrap) {
      this.scrollbar_horizontal = new ScrollBar(p, false);
      this.scrollbar_horizontal.setLocation(this.xi, this.yf - this.scrollbar.bar_size, this.xf - this.scrollbar.bar_size, this.yf);
    }
    this.refreshText();
  }

  void refreshText() {
    this.setText(this.text_ref);
  }

  public void clearText() {
    this.setText("");
  }

  public void addText(String text) {
    this.setText(this.text_ref + text);
  }

  public void setText(String text) {
    this.text_ref = text;
    this.text_lines.clear();
    this.text_lines_display.clear();
    double currY = this.yi + 1;
    if (this.text_title_ref != null) {
      p.textSize(this.title_size);
      currY += p.textAscent() + p.textDescent() + 2;
    }
    p.textSize(this.text_size);
    float text_height = p.textAscent() + p.textDescent();
    double effective_xf = this.xf - this.xi - 4 - this.scrollbar.bar_size;
    int lines_above = 0;
    String[] lines = PApplet.split(text, '\n');
    String currLine = "";
    boolean firstWord = true;
    int max_line_length = 0;
    for (int i = 0; i < lines.length; i++) {
      if (this.word_wrap) {
        String[] words = PApplet.split(lines[i], ' ');
        for (int j = 0; j < words.length; j++) {
          String word = " ";
          if (firstWord) {
            word = "";
          }
          word += words[j];
          if (p.textWidth(currLine + word) < effective_xf) {
            currLine += word;
            firstWord = false;
          }
          else if (firstWord) {
            for (int k = 0; k < word.length(); k++) {
              char nextChar = word.charAt(k);
              if (p.textWidth(currLine + nextChar) < effective_xf) {
                currLine += nextChar;
              }
              else {
                this.text_lines.add(currLine);
                currLine = "" + nextChar;
                firstWord = true;
                if (currY + text_height + 1 > this.yf) {
                  lines_above++;
                }
                currY += text_height + this.text_leading;
              }
            }
            firstWord = false;
          }
          else {
            this.text_lines.add(currLine);
            currLine = words[j];
            firstWord = false;
            if (currY + text_height + 1 > this.yf) {
              lines_above++;
            }
            currY += text_height + this.text_leading;
          }
        }
        this.text_lines.add(currLine);
        currLine = "";
        firstWord = true;
        if (currY + text_height + 1 > this.yf) {
          lines_above++;
        }
        currY += text_height + this.text_leading;
      }
      else {
        this.text_lines.add(lines[i]);
        for (int j = 0; j < lines[i].length(); j++) {
          char nextChar = lines[i].charAt(j);
          if (p.textWidth(currLine + nextChar) < effective_xf) {
            currLine += nextChar;
          }
          else {
            if (lines[i].length() - j > max_line_length) {
              max_line_length = lines[i].length() - j;
            }
            break;
          }
        }
        currLine = "";
        if (currY + text_height + 1 > this.yf) {
          lines_above++;
        }
        currY += text_height + this.text_leading;
      }
    }
    this.scrollbar.updateMaxValue(lines_above);
    if (!this.word_wrap) {
      this.scrollbar_horizontal.updateMaxValue(max_line_length);
    }
  }

  public String truncateLine(String line) {
    String return_line = "";
    double effective_xf = this.xf - this.xi - 4 - this.scrollbar.bar_size;
    for (int i = (int)this.scrollbar_horizontal.value; i < line.length(); i++) {
      char nextChar = line.charAt(i);
      if (p.textWidth(return_line + nextChar) < effective_xf) {
        return_line += nextChar;
      }
      else {
        break;
      }
    }
    return return_line;
  }

  public void update(int millis) {
    p.rectMode(PConstants.CORNERS);
    p.fill(this.color_background);
    p.stroke(this.color_stroke);
    p.strokeWeight(1);
    p.rect(this.xi, this.yi, this.xf, this.yf);
    double currY = this.yi + 1;
    if (this.text_title_ref != null) {
      p.fill(this.color_header);
      p.textSize(this.title_size);
      p.rect(this.xi, this.yi, this.xf, this.yi + p.textAscent() + p.textDescent() + 1);
      p.fill(this.color_title);
      p.textAlign(PConstants.CENTER, PConstants.TOP);
      p.text(this.text_title, this.xi + 0.5 * (this.xf - this.xi), currY);
      currY += p.textAscent() + p.textDescent() + 2;
    }
    p.fill(this.color_text);
    p.textAlign(PConstants.LEFT, PConstants.TOP);
    p.textSize(this.text_size);
    float text_height = p.textAscent() + p.textDescent();
    for (int i = (int)this.scrollbar.value; i < this.text_lines.size(); i++, currY += text_height + this.text_leading) {
      if (currY + text_height + 1 > this.yf) {
        break;
      }
      if (this.word_wrap) {
        p.text(this.text_lines.get(i), this.xi + 2, currY);
      }
      else {
        p.text(this.truncateLine(this.text_lines.get(i)), this.xi + 2, currY);
      }
    }
    if (this.scrollbar.maxValue != this.scrollbar.minValue) {
      this.scrollbar.update(millis);
    }
    if (!this.word_wrap) {
      if (this.scrollbar_horizontal.maxValue != this.scrollbar_horizontal.minValue) {
        this.scrollbar_horizontal.update(millis);
      }
    }
    this.last_update_time = millis;
  }

  public void mouseMove(float mX, float mY) {
    this.scrollbar.mouseMove(mX, mY);
    if (!this.word_wrap) {
      if (this.scrollbar_horizontal.maxValue != this.scrollbar_horizontal.minValue) {
        this.scrollbar_horizontal.mouseMove(mX, mY);
      }
    }
    if (mX >= this.xi && mX <= this.xf && mY >= this.yi && mY <= this.yf) {
      this.hovered = true;
    }
    else {
      this.hovered = false;
    }
  }

  public void mousePress() {
    this.scrollbar.mousePress();
    if (!this.word_wrap) {
      if (this.scrollbar_horizontal.maxValue != this.scrollbar_horizontal.minValue) {
        this.scrollbar_horizontal.mousePress();
      }
    }
  }

  public void mouseRelease(float mX, float mY) {
    this.scrollbar.mouseRelease(mX, mY);
    if (!this.word_wrap) {
      if (this.scrollbar_horizontal.maxValue != this.scrollbar_horizontal.minValue) {
        this.scrollbar_horizontal.mouseRelease(mX, mY);
      }
    }
  }

  public void scroll(int amount) {
    if (this.hovered) {
      this.scrollbar.increaseValue(amount);
      if (!this.word_wrap && this.scrollbar.maxValue == this.scrollbar.minValue) {
        if (this.scrollbar_horizontal.maxValue != this.scrollbar_horizontal.minValue) {
          this.scrollbar_horizontal.increaseValue(amount);
        }
      }
    }
  }

  public void scrollBottom() {
    this.scrollbar.updateValue(this.scrollbar.maxValue);
  }

  public void keyPress(int key, int keyCode) {
  }
}