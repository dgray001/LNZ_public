package Form;

import java.util.*;
import processing.core.*;
import LNZApplet.LNZApplet;
import Button.RippleRectangleButton;
import DImg.DImg;

public abstract class TabbedForm extends Form {
  public class FormTab {
    class TabButton extends RippleRectangleButton {
      protected int text_default = DImg.ccolor(0);
      protected int text_hover = DImg.ccolor(255);
      protected int text_click = DImg.ccolor(0);
      protected boolean current_tab = false;

      TabButton(LNZApplet sketch, String header) {
        super(sketch);
        this.setMessage(header);
        this.show_message = true;
        this.setColors(DImg.ccolor(180), DImg.ccolor(1, 0), DImg.ccolor(140, 80), DImg.ccolor(100, 160), DImg.ccolor(0));
      }

      @Override
      public void drawButton() {
        if (this.current_tab) {
          p.fill(DImg.brighten(this.color_hover));
          p.stroke(DImg.brighten(this.color_hover));
          p.strokeWeight(0.01);
          p.rectMode(PConstants.CORNERS);
          p.rect(this.xi, this.yi, this.xf, this.yf);
          p.fill(DImg.darken(this.color_click));
          p.rect(this.xi, this.yf - 4, this.xf, this.yf);
        }
        super.drawButton();
      }

      @Override
      public void hover() {
        super.hover();
        if (!this.clicked) {
          this.color_text = this.text_hover;
        }
      }

      @Override
      public void dehover() {
        super.dehover();
        this.color_text = this.text_default;
      }

      @Override
      public void click() {
        super.click();
        this.color_text = this.text_click;
      }

      @Override
      public void release() {
        super.release();
        this.color_text = this.text_default;
        if (this.hovered) {
          FormTab.this.switchToTab();
        }
      }
    }

    private int index;
    public Form form;
    private TabButton button;

    FormTab(LNZApplet sketch, int index, Form form, String header) {
      this.index = index;
      this.form = form;
      this.button = new TabButton(sketch, header);
    }

    void switchToTab() {
      TabbedForm.this.switchToTab(this.index);
    }

    void applyConfig(TabConfig config) {
      this.button.text_size = config.tab_text_size;
      this.form.color_background = config.color_background;
      this.form.color_header = config.color_header;
      this.form.color_stroke = config.color_stroke;
      this.form.color_title = config.color_title;
      this.form.scrollbar_min_width = config.scrollbar_min_width;
      this.form.scrollbar_max_width = config.scrollbar_max_width;
      this.form.scrollbar_width_multiplier = config.scrollbar_width_multiplier;
      this.form.scrollbar.setButtonColors(config.scrollbar_color_disabled,
        config.scrollbar_color_default, config.scrollbar_color_hovered,
        config.scrollbar_color_clicked, DImg.ccolor(0));
      this.form.scrollbar.button_upspace.setColors(config.scrollbar_color_disabled,
        config.scrollbar_color_space, config.scrollbar_color_space,
        config.scrollbar_color_space_clicked, DImg.ccolor(0));
      this.form.scrollbar.button_downspace.setColors(config.scrollbar_color_disabled,
        config.scrollbar_color_space, config.scrollbar_color_space,
        config.scrollbar_color_space_clicked, DImg.ccolor(0));
      this.form.refreshTitle();
    }
  }


  public class TabConfig {
    public double tab_text_size = 16;
    public int color_background = DImg.ccolor(210);
    public int color_header = DImg.ccolor(170);
    public int color_stroke = DImg.ccolor(0);
    public int color_title = DImg.ccolor(0);
    public double scrollbar_width_multiplier = 0.05;
    public double scrollbar_min_width = 30;
    public double scrollbar_max_width = 60;
    public int scrollbar_color_disabled = DImg.ccolor(220, 180);
    public int scrollbar_color_default = DImg.ccolor(220);
    public int scrollbar_color_hovered = DImg.ccolor(170);
    public int scrollbar_color_clicked = DImg.ccolor(120);
    public int scrollbar_color_space = DImg.ccolor(235);
    public int scrollbar_color_space_clicked = DImg.ccolor(0);

    public TabConfig() {
    }
  }


  protected ArrayList<FormTab> tabs = new ArrayList<FormTab>();
  protected TabConfig tab_config = new TabConfig();
  protected int current_tab = 0;
  protected double footer_space = 0;
  protected double tab_button_height = 0;
  protected boolean tab_button_fill_space = false;
  protected int tab_button_alignment = PConstants.LEFT;
  protected double tab_button_max_width = 100;

  public TabbedForm(LNZApplet sketch) {
    this(sketch, 0, 0, 0, 0);
  }
  public TabbedForm(LNZApplet sketch, double xi, double yi, double xf, double yf) {
    super(sketch, xi, yi, xf, yf);
  }


  public void setTabConfig(TabConfig config) {
    this.tab_config = config;
    for (FormTab tab : this.tabs) {
      tab.applyConfig(config);
    }
  }

  @Override
  double yStart() {
    return yf - this.footer_space;
  }

  @Override
  public void setLocation(double xi, double yi, double xf, double yf) {
    super.setLocation(xi, yi, xf, yf);
    try {
      for (FormTab tab : this.tabs) {
        tab.form.setLocation(xi + 1, this.yStart + this.tab_button_height, xf - 1, yf - this.footer_space);
      }
      this.resizeTabs();
    } catch (NullPointerException e) {} // called in super constructor
  }

  @Override
  public void moveForm(double xMove, double yMove) {
    super.moveForm(xMove, yMove);
    for (FormTab tab : this.tabs) {
      tab.form.moveForm(xMove, yMove);
      tab.button.moveButton(xMove, yMove);
    }
  }

  public void addTab(Form form, String message) {
    form.setLocation(this.xi + 1, this.yStart + this.tab_button_height + 1, this.xf - 1, this.yf - this.footer_space - 1);
    FormTab tab = new FormTab(p, this.tabs.size(), form, message);
    tab.applyConfig(this.tab_config);
    this.tabs.add(tab);
    this.resizeTabs();
  }

  void resizeTabs() {
    if (this.tabs.size() == 0) {
      return;
    }
    double tab_button_width = 0;
    double x_curr = this.xi;
    double tab_width = this.tabs.size() * (this.tab_button_max_width + 1) - 1;
    if (this.tab_button_fill_space || tab_width > this.formWidth()) {
      tab_button_width = (this.formWidth() - this.tabs.size() + 1) / this.tabs.size();
    }
    else {
      tab_button_width = this.tab_button_max_width;
      switch(this.tab_button_alignment) {
        case PConstants.CENTER:
          x_curr = this.xi + 0.5 * (this.formWidth() - tab_width);
          break;
        case PConstants.RIGHT:
          x_curr = this.xf - tab_width;
          break;
        case PConstants.LEFT:
        default:
          break;
      }
    }
    for (FormTab tab : this.tabs) {
      tab.button.setLocation(x_curr, this.yStart, x_curr + tab_button_width, this.yStart + this.tab_button_height);
      x_curr += tab_button_width + 1;
    }
  }

  void switchToTab(int index) {
    if (index < 0 || index >= this.tabs.size() || index == this.current_tab) {
      return;
    }
    this.current_tab = index;
  }

  @Override
  public void update(int millis) {
    this.drawHeader(millis);
    for (int i = 0; i < this.tabs.size(); i++) {
      if (i == this.current_tab) {
        this.tabs.get(i).form.update(millis);
        this.tabs.get(i).button.current_tab = true;
      }
      else {
        this.tabs.get(i).button.current_tab = false;
      }
      this.tabs.get(i).button.update(millis);
    }
    this.drawContent(this.yf - this.footer_space, millis);
  }

  @Override
  public void mouseMove(float mX, float mY) {
    super.mouseMove(mX, mY);
    for (int i = 0; i < this.tabs.size(); i++) {
      this.tabs.get(i).button.mouseMove(mX, mY);
      if (i == this.current_tab) {
        this.tabs.get(i).form.mouseMove(mX, mY);
      }
    }
  }

  @Override
  public void mousePress() {
    super.mousePress();
    for (int i = 0; i < this.tabs.size(); i++) {
      this.tabs.get(i).button.mousePress();
      if (i == this.current_tab) {
        this.tabs.get(i).form.mousePress();
      }
    }
  }

  @Override
  public void mouseRelease(float mX, float mY) {
    super.mouseRelease(mX, mY);
    for (int i = 0; i < this.tabs.size(); i++) {
      this.tabs.get(i).button.mouseRelease(mX, mY);
      if (i == this.current_tab) {
        this.tabs.get(i).form.mouseRelease(mX, mY);
      }
    }
  }

  @Override
  public void scroll(int amount) {
    boolean scroll_super = true;
    for (int i = 0; i < this.tabs.size(); i++) {
      if (i == this.current_tab) {
        this.tabs.get(i).form.scroll(amount);
        scroll_super = false;
      }
    }
    if (scroll_super) {
      super.scroll(amount);
    }
  }

  @Override
  public void keyPress(int key, int keyCode) {
    super.keyPress(key, keyCode);
    for (int i = 0; i < this.tabs.size(); i++) {
      if (i == this.current_tab) {
        this.tabs.get(i).form.keyPress(key, keyCode);
      }
    }
  }

  @Override
  public void keyRelease(int key, int keyCode) {
    super.keyRelease(key, keyCode);
    for (int i = 0; i < this.tabs.size(); i++) {
      if (i == this.current_tab) {
        this.tabs.get(i).form.keyRelease(key, keyCode);
      }
    }
  }
}
