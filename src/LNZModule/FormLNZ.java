package LNZModule;

import processing.core.*;
import DImg.DImg;
import Form.Form;

abstract class FormLNZ extends Form {
	
	protected LNZ p;

  protected boolean canceled = false;
  protected double shadow_distance = 10;
  protected PImage img;
  protected int color_shadow = DImg.ccolor(0, 150);
  protected boolean need_to_reset_cursor = true;

  FormLNZ(LNZ sketch, double xi, double yi, double xf, double yf) {
    super(sketch, xi, yi, xf, yf);
    this.p = sketch;
    this.img = p.getCurrImage();
    this.cancelButton();
    this.draggable = true;
  }

  @Override
  public void update(int millis) {
    if (this.need_to_reset_cursor) {
      p.global.defaultCursor();
    }
    p.rectMode(PConstants.CORNERS);
    p.fill(0);
    p.imageMode(PConstants.CORNER);
    p.image(this.img, 0, 0);
    p.fill(color_shadow);
    p.stroke(0, 1);
    p.translate(shadow_distance, shadow_distance);
    p.rect(this.xi, this.yi, this.xf, this.yf);
    p.translate(-shadow_distance, -shadow_distance);
    super.update(millis);
  }

  public void cancel() {
    this.canceled = true;
  }

  public void buttonPress(int i) {}

  @Override
  public void keyPress(int key, int keyCode) {
    super.keyPress(key, keyCode);
    if (key == PConstants.ESC) {
      this.cancel();
    }
  }

  public static PImage getFormLNZImage(Global global, Form form) {
    Class<?> cls = form.getClass();
    if (FormLNZ.class.isAssignableFrom(cls)) {
      return ((FormLNZ)form).img;
    }
    else if (TabbedFormLNZ.class.isAssignableFrom(cls)) {
      return ((TabbedFormLNZ)form).img;
    }
    global.errorMessage("ERROR: Interface Forms must be extended from FormLNZ " +
      "or TabbedFormLNZ. This form is of type " + cls.getName() + ".");
    return null;
  }

  public static void setFormLNZImage(Global global, Form form, PImage img) {
    Class<?> cls = form.getClass();
    if (FormLNZ.class.isAssignableFrom(cls)) {
      ((FormLNZ)form).img = img;
    }
    else if (TabbedFormLNZ.class.isAssignableFrom(cls)) {
      ((TabbedFormLNZ)form).img = img;
    }
    else {
      global.errorMessage("ERROR: Interface Forms must be extended from FormLNZ " +
        "or TabbedFormLNZ. This form is of type " + cls.getName() + ".");
    }
  }

  public static boolean formCanceled(Global global, Form form) {
    Class<?> cls = form.getClass();
    if (FormLNZ.class.isAssignableFrom(cls)) {
      return ((FormLNZ)form).canceled;
    }
    else if (TabbedFormLNZ.class.isAssignableFrom(cls)) {
      return ((TabbedFormLNZ)form).canceled;
    }
    global.errorMessage("ERROR: Interface Forms must be extended from FormLNZ " +
      "or TabbedFormLNZ. This form is of type " + cls.getName() + ".");
    return true;
  }
}