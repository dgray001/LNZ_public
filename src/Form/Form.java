package Form;

import java.util.*;
import processing.core.*;
import LNZApplet.LNZApplet;
import Button.RectangleButton;
import DImg.DImg;
import Element.ScrollBar;
import Misc.ClockInt;

public abstract class Form {
  class CancelButton extends RectangleButton {
    CancelButton(LNZApplet sketch, double xi, double yi, double xf, double yf) {
      super(sketch, xi, yi, xf, yf);
      this.roundness = 0;
      this.setColors(DImg.ccolor(170), DImg.ccolor(240, 30, 30), DImg.ccolor(255, 60, 60), DImg.ccolor(180, 0, 0), DImg.ccolor(0));
      this.color_stroke = DImg.ccolor(0, 1);
    }
    @Override
    public void drawButton() {
      super.drawButton();
      p.stroke(DImg.ccolor(0));
      p.strokeWeight(1.5);
      double offset = 0.05 * this.buttonWidth();
      p.line(this.xi + offset, this.yi + offset, this.xf - offset, this.yf - offset);
      p.line(this.xi + offset, this.yf - offset, this.xf - offset, this.yi + offset);
    }
    public void hover() {
    }
    public void dehover() {
    }
    public void click() {
    }
    public void release() {
      if (this.hovered) {
        Form.this.cancelForm();
      }
    }
  }
	
	protected LNZApplet p;

  protected double xi = 0;
  protected double yi = 0;
  protected double xf = 0;
  protected double yf = 0;
  protected boolean hovered = false;
  protected CancelButton cancel;

  protected ScrollBar scrollbar;
  protected double scrollbar_max_width = 40;
  protected double scrollbar_min_width = 20;
  protected double scrollbar_width_multiplier = 0.05;

  public List<FormField> fields = new ArrayList<FormField>();
  protected double fieldCushion = 20;
  protected double yStart = 0;

  protected String text_title_ref = null;
  protected String text_title = null;
  protected double title_size = 22;

  protected int color_background = DImg.ccolor(210);
  protected int color_header = DImg.ccolor(170);
  protected int color_stroke = DImg.ccolor(0);
  protected int color_title = DImg.ccolor(0);

  protected boolean draggable = false;
  protected boolean hovered_header = false;
  protected boolean dragging = false;
  protected double dragX = 0;
  protected double dragY = 0;
  protected double max_x = 0;
  protected double min_x = 0;
  protected double max_y = 0;
  protected double min_y = 0;

  public Form(LNZApplet sketch) {
    this(sketch, 0, 0, 0, 0);
  }
  public Form(LNZApplet sketch, double xi, double yi, double xf, double yf) {
    this.p = sketch;
    this.scrollbar = new ScrollBar(sketch, true);
    this.max_x = sketch.width;
    this.max_y = sketch.height;
    this.setLocation(xi, yi, xf, yf);
  }

  public void cancelButton() {
    p.textSize(this.title_size);
    this.cancelButton(p.textAscent() + p.textDescent() + 1);
  }
  void cancelButton(double size) {
    this.cancel = new CancelButton(p, this.xf - size, this.yi + 1, this.xf, this.yi + size);
    this.refreshTitle();
  }

  public double formWidth() {
    return this.xf - this.xi;
  }
  public double formHeight() {
    return this.yf - this.yi;
  }

  public double xCenter() {
    return this.xi + 0.5 * (this.xf - this.xi);
  }
  public double yCenter() {
    return this.yi + 0.5 * (this.yf - this.yi);
  }

  public void setLocation(double xi, double yi, double xf, double yf) {
    this.xi = xi;
    this.yi = yi;
    this.xf = xf;
    this.yf = yf;
    this.refreshTitle();
    for (FormField field : this.fields) {
      field.setWidth(this.xf - this.xi - 3 - this.scrollbar.bar_size);
    }
  }
  public void setXLocation(double xi, double xf) {
    this.xi = xi;
    this.xf = xf;
    this.refreshTitle();
    for (FormField field : this.fields) {
      field.setWidth(this.xf - this.xi - 3 - this.scrollbar.bar_size);
    }
  }
  public void setYLocation(double yi, double yf) {
    this.yi = yi;
    this.yf = yf;
    this.refreshTitle();
  }

  public void moveForm(double xMove, double yMove) {
    this.xi += xMove;
    this.yi += yMove;
    this.xf += xMove;
    this.yf += yMove;
    this.scrollbar.move(xMove, yMove);
    if (this.cancel != null) {
      this.cancel.moveButton(xMove, yMove);
    }
    this.yStart += yMove;
    if (this.xi >= this.max_x || this.xf <= this.min_x ||
      (this.cancel != null && this.xf <= this.cancel.buttonWidth()) ||
      this.yi >= this.max_y || this.yStart <= this.min_y) {
      this.toCenter();
      this.dragging = false;
    }
  }

  public void toCenter() {
    double xMove = 0.5 * (p.width - this.formWidth()) - this.xi;
    double yMove = 0.5 * (p.height - this.formHeight()) - this.yi;
    this.moveForm(xMove, yMove);
  }

  public void scrollbarWidths(double scrollbar_max_width, double scrollbar_min_width, double scrollbar_width_multiplier) {
    this.scrollbar_max_width = scrollbar_max_width;
    this.scrollbar_min_width = scrollbar_min_width;
    this.scrollbar_width_multiplier = scrollbar_width_multiplier;
  }

  public void refreshTitle() {
    this.setTitleText(this.text_title_ref);
  }
  public void setTitleSize(double title_size) {
    this.title_size = title_size;
    this.refreshTitle();
    if (this.cancel != null) {
      p.textSize(this.title_size);
      if (this.cancel.buttonHeight() > p.textAscent() + p.textDescent() + 1) {
        this.cancelButton();
      }
    }
  }
  public void setTitleText(String title) {
    this.text_title_ref = title;
    double scrollbar_width = Math.min(this.scrollbar_max_width, this.scrollbar_width_multiplier * (this.xf - this.xi));
    scrollbar_width = Math.max(this.scrollbar_min_width, scrollbar_width);
    scrollbar_width = Math.min(this.scrollbar_width_multiplier * (this.xf - this.xi), scrollbar_width);
    if (title == null) {
      this.text_title = null;
      this.yStart = this.yi + 1;
      this.scrollbar.setLocation(this.xf - scrollbar_width, this.yStart(), this.xf, this.yf);
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
      this.yStart = this.yi + 2 + p.textAscent() + p.textDescent();
      this.scrollbar.setLocation(xf - scrollbar_width, this.yStart(), this.xf, this.yf);
    }
    this.refreshScrollbar();
  }

  double yStart() {
    return this.yStart;
  }

  public List<FormField> fieldsOfClass(int cls) {
    List<FormField> fields_of_class = new ArrayList<FormField>();
    for (FormField field : this.fields) {
      if (field.hasClass(cls)) {
        fields_of_class.add(field);
      }
    }
    return fields_of_class;
  }

  public void setFieldCushion(double fieldCushion) {
    this.fieldCushion = fieldCushion;
    this.refreshScrollbar();
  }

  public void addField(FormField field) {
    field.setWidth(this.xf - this.xi - 3 - this.scrollbar.bar_size);
    this.fields.add(field);
    this.refreshScrollbar();
  }

  public void removeField(int index) {
    if (index < 0 || index >= this.fields.size()) {
      return;
    }
    this.fields.remove(index);
    this.refreshScrollbar();
  }

  public void clearFields() {
    this.fields.clear();
    this.refreshScrollbar();
  }

  void refreshScrollbar() {
    // TODO: Draw all fields onto a PGraphics so can scroll smoothly
    double currY = this.yStart();
    for (int i = 0; i < this.fields.size(); i++) {
      currY += this.fields.get(this.fields.size() - 1 - i).getHeight() + this.fieldCushion;
      if (currY + 2 > this.yf) {
        this.scrollbar.updateMaxValue(this.fields.size() - i);
        return;
      }
    }
    this.scrollbar.updateMaxValue(0);
  }

  public void update(int millis) {
    this.drawHeader(millis);
    this.drawContent(this.yStart, millis);
  }

  void drawHeader(int millis) {
    p.rectMode(PConstants.CORNERS);
    p.fill(this.color_background);
    p.stroke(this.color_stroke);
    p.strokeWeight(1);
    p.rect(this.xi, this.yi, this.xf, this.yf);
    if (this.text_title_ref != null) {
      p.fill(this.color_header);
      p.textSize(this.title_size);
      p.rect(this.xi, this.yi, this.xf, this.yi + p.textAscent() + p.textDescent() + 1);
      p.fill(this.color_title);
      p.textAlign(PConstants.CENTER, PConstants.TOP);
      double center = this.xi + 0.5 * (this.xf - this.xi);
      if (this.cancel != null) {
        center -= 0.5 * this.cancel.buttonWidth();
      }
      p.text(this.text_title, center, this.yi + 1);
    }
    if (this.cancel != null) {
      this.cancel.update(millis);
    }
  }

  void drawContent(double currY, int millis) {
    boolean submit_this_frame = false;
    boolean cancel_this_frame = false;
    boolean buttonpress_this_frame = false;
    int buttonpress_index = -1;
    p.translate(this.xi + 1, 0);
    for (int i = (int)this.scrollbar.value; i < this.fields.size(); i++) {
      FormField field = this.fields.get(i);
      if (field.hidden) {
        continue;
      }
      if (currY + field.getHeight() > this.yf) {
        break;
      }
      p.translate(0, currY);
      FormFieldSubmit submit = field.update(millis);
      if (submit == FormFieldSubmit.SUBMIT) {
        submit_this_frame = true;
      }
      else if (submit == FormFieldSubmit.CANCEL) {
        cancel_this_frame = true;
      }
      else if (submit == FormFieldSubmit.BUTTON) { // alternate button
        buttonpress_this_frame = true;
        buttonpress_index = i;
      }
      p.translate(0, -currY);
      currY += field.getHeight() + this.fieldCushion;
    }
    p.translate(-this.xi - 1, 0);
    if (this.scrollbar.maxValue != this.scrollbar.minValue) {
      this.scrollbar.update(millis);
    }
    if (submit_this_frame) {
      this.submitForm();
    }
    if (cancel_this_frame) {
      this.cancelForm();
    }
    if (buttonpress_this_frame) {
      this.buttonPress(buttonpress_index);
    }
  }

  public void mouseMove(float mX, float mY) {
    this.scrollbar.mouseMove(mX, mY);
    if (this.cancel != null) {
      this.cancel.mouseMove(mX, mY);
    }
    if (this.dragging) {
      this.moveForm(mX - this.dragX, mY - this.dragY);
    }
    this.dragX = mX;
    this.dragY = mY;
    this.hovered_header = false;
    if (mX > this.xi && mX < this.xf && mY > this.yi && mY < this.yf) {
      this.hovered = true;
      if (this.text_title_ref != null) {
        if (mY < this.yStart) {
          if (this.cancel == null || !this.cancel.hovered) {
            this.hovered_header = true;
          }
        }
      }
    }
    else {
      this.hovered = false;
    }
    mX -= this.xi + 1;
    mY -= this.yStart();
    double currY = this.yStart();
    for (int i = (int)this.scrollbar.value; i < this.fields.size(); i++) {
      FormField field = this.fields.get(i);
      if (field.hidden) {
        continue;
      }
      if (currY + field.getHeight() > this.yf) {
        break;
      }
      field.mouseMove(mX, mY);
      mY -= field.getHeight() + this.fieldCushion;
      currY += field.getHeight() + this.fieldCushion;
    }
  }

  public void mousePress() {
    this.scrollbar.mousePress();
    if (this.cancel != null) {
      this.cancel.mousePress();
    }
    for (int i = 0; i < (int)this.scrollbar.value; i++) {
      this.fields.get(i).defocus();
    }
    double currY = this.yStart();
    for (int i = (int)this.scrollbar.value; i < this.fields.size(); i++) {
      FormField field = this.fields.get(i);
      if (field.hidden) {
        continue;
      }
      if (currY + field.getHeight() > this.yf) {
        field.defocus();
        continue;
      }
      field.mousePress();
      currY += field.getHeight() + this.fieldCushion;
    }
    if (this.hovered_header && this.draggable) {
      this.dragging = true;
    }
  }

  public void mouseRelease(float mX, float mY) {
    this.scrollbar.mouseRelease(mX, mY);
    if (this.cancel != null) {
      this.cancel.mouseRelease(mX, mY);
    }
    mX -= this.xi + 1;
    mY -= this.yStart();
    double currY = this.yStart();
    for (int i = (int)this.scrollbar.value; i < this.fields.size(); i++) {
      FormField field = this.fields.get(i);
      if (field.hidden) {
        continue;
      }
      if (currY + field.getHeight() > this.yf) {
        break;
      }
      field.mouseRelease(mX, mY);
      mY -= field.getHeight() + this.fieldCushion;
      currY += field.getHeight() + this.fieldCushion;
    }
    this.dragging = false;
  }

  public void scroll(int amount) {
    for (FormField field : this.fields) {
      if (field.hidden) {
        continue;
      }
      field.scroll(amount);
    }
    if (this.hovered) {
      this.scrollbar.increaseValue(amount);
    }
  }

  public void keyPress(int key, int keyCode) {
    for (FormField field : this.fields) {
      if (field.hidden) {
        continue;
      }
      field.keyPress(key, keyCode);
    }
    if (key != PConstants.CODED && key == PConstants.TAB) {
      this.focusNextField();
    }
  }

  public void focusNextField() {
    int field_focused = 0;
    for (int i = 0; i < this.fields.size(); i++) {
      if (this.fields.get(i).focused()) {
        field_focused = i;
        break;
      }
    }
    ClockInt index = new ClockInt(0, this.fields.size() - 1, field_focused);
    for (int i = 0; i < this.fields.size(); i++, index.add(1)) {
      if (this.fields.get(index.value()).focusable()) {
        this.fields.get(index.value()).focus();
        break;
      }
    }
    if (index.value() != field_focused) {
      this.fields.get(field_focused).defocus();
    }
  }

  public void keyRelease(int key, int keyCode) {
    for (FormField field : this.fields) {
      if (field.hidden) {
        continue;
      }
      field.keyRelease(key, keyCode);
    }
  }

  public void submitForm() {
    for (FormField field : this.fields) {
      if (field.hidden) {
        continue;
      }
      field.submit();
    }
    this.submit();
  }

  public void cancelForm() {
    this.cancel();
  }

  public abstract void submit();
  public abstract void cancel();
  public abstract void buttonPress(int i);
}