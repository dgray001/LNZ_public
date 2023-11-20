package Form;

import java.util.*;
import Button.RectangleButton;
import DImg.DImg;
import LNZApplet.LNZApplet;
import Misc.Callable;

public abstract class FormField {
  public class FormFieldButton extends RectangleButton {
    FormFieldButton(LNZApplet sketch) {
      super(sketch);
      int c = DImg.ccolor(1, 0);
      this.setColors(c, c, c, c, c);
      this.noStroke();
    }
    public void dehover() {
      Callable<Object, FormField> callable = FormField.this.dehover;
      if (callable != null) {
        callable.call(FormField.this);
      }
    }
    public void hover() {
      Callable<Object, FormField> callable = FormField.this.hover;
      if (callable != null) {
        callable.call(FormField.this);
      }
    }
    public void click() {
      Callable<Object, FormField> callable = FormField.this.click;
      if (callable != null) {
        callable.call(FormField.this);
      }
    }
    public void release() {
      Callable<Object, FormField> callable = FormField.this.release;
      if (callable != null) {
        callable.call(FormField.this);
      }
    }
  }
	
	protected LNZApplet p;

  public FormFieldButton button;
  public Callable<Object, FormField> dehover;
  public Callable<Object, FormField> hover;
  public Callable<Object, FormField> click;
  public Callable<Object, FormField> release;

  public Set<Integer> classes = new HashSet<Integer>();
  public boolean hidden = false;
  protected String message;
  protected double field_width = 0;

  public FormField(LNZApplet sketch, String message) {
    this.p = sketch;
    this.button = new FormFieldButton(sketch);
    this.message = message;
  }

  public void addClass(int cls) {
    this.classes.add(cls);
  }
  boolean hasClass(int cls) {
    return this.classes.contains(cls);
  }

  public double getWidth() {
    return this.field_width;
  }
  public void setWidth(double new_width) {
    this.field_width = new_width;
    this.updateWidthDependencies();
    this.button.setLocation(0, 0, new_width, this.getHeight());
  }

  public void setValue(int new_value) {
    this.setValue(Integer.toString(new_value));
  }
  public void setValue(long new_value) {
    this.setValue(Long.toString(new_value));
  }
  public void setValue(float new_value) {
    this.setValue(Float.toString(new_value));
  }
  public void setValue(double new_value) {
    this.setValue(Double.toString(new_value));
  }
  public void setValue(boolean new_value) {
    this.setValue(Boolean.toString(new_value));
  }

  public abstract void enable();
  public abstract void disable();

  public abstract boolean focusable();
  public abstract void focus();
  public abstract void defocus();
  public abstract boolean focused();

  public abstract void updateWidthDependencies();
  public abstract double getHeight();
  public abstract String getValue();
  public abstract void setValue(String newValue);
  public void setValueIfNotFocused(String newValue) {
    if (!this.focused()) {
      this.setValue(newValue);
    }
  }

  public FormFieldSubmit update(int millis) {
    this.button.update(millis);
    return this.updateField(millis);
  }
  public void mouseMove(float mX, float mY) {
    this.button.mouseMove(mX, mY);
    this.mouseMoveField(mX, mY);
  }
  public void mousePress() {
    this.button.mousePress();
    this.mousePressField();
  }
  public void mouseRelease(float mX, float mY) {
    this.button.mouseRelease(mX, mY);
    this.mouseReleaseField(mX, mY);
  }
  public void scroll(int amount) {
    this.scrollField(amount);
  }
  public void keyPress(int key, int keyCode) {
    this.button.keyPress(key, keyCode);
    this.keyPressField(key, keyCode);
  }
  public void keyRelease(int key, int keyCode) {
    this.button.keyRelease(key, keyCode);
    this.keyReleaseField(key, keyCode);
  }

  public abstract FormFieldSubmit updateField(int millis);
  public abstract void mouseMoveField(float mX, float mY);
  public abstract void mousePressField();
  public abstract void mouseReleaseField(float mX, float mY);
  public abstract void keyPressField(int key, int keyCode);
  public abstract void keyReleaseField(int key, int keyCode);
  public abstract void scrollField(int amount);

  public abstract void submit();
}