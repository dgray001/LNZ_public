package Form;

import java.util.*;
import processing.core.*;
import LNZApplet.LNZApplet;
import Button.RadioButton;
import DImg.DImg;
import Misc.Misc;

public class RadiosFormField extends MessageFormField {
  class DefaultRadioButton extends RadioButton {
    DefaultRadioButton(LNZApplet sketch, String message) {
      super(sketch, 0, 0, 0);
      this.setMessage(message);
    }
    public void hover() {
    }
    public void dehover() {
    }
    public void release() {
    }
  }

  public List<RadioButton> radios = new ArrayList<RadioButton>();
  protected float radio_padding = 6;
  protected int index_selected = -1;
  protected boolean message_first = false;

  public RadiosFormField(LNZApplet sketch, String message) {
    super(sketch, message);
  }

  public void addRadio() {
    this.addRadio("");
  }
  public void addRadio(String message) {
    this.addRadio(new DefaultRadioButton(p, message));
  }
  public void addDisabledRadio(String message) {
    DefaultRadioButton radio = new DefaultRadioButton(p, message);
    radio.disabled = true;
    radio.color_text = DImg.ccolor(80);
    this.addRadio(radio);
  }
  public void addRadio(RadioButton radio) {
    this.radios.add(radio);
    this.updateWidthDependencies();
  }

  @Override
  public void updateWidthDependencies() {
    super.updateWidthDependencies();
    double currY = super.getHeight() + this.radio_padding;
    p.textSize(this.text_size - 2);
    for (RadioButton radio : this.radios) {
      radio.text_size = this.text_size - 2;
      double radius = 0.5 * Math.min(0.8 * (p.textAscent() + p.textDescent() + 2),
        Math.abs(this.field_width - p.textWidth(radio.message) - 2 * this.radio_padding));
        double xc = radius + this.radio_padding;
      if (message_first) {
        xc += p.textWidth(radio.message) + this.radio_padding;
      }
      double yc = currY + 0.5 * (p.textAscent() + p.textDescent() + 2);
      radio.setLocation(xc, yc, radius);
      currY += p.textAscent() + p.textDescent() + 2 + this.radio_padding;
    }
  }

  @Override
  public double getHeight() {
    double field_height = super.getHeight();
    field_height += this.radios.size() * this.radio_padding;
    for (RadioButton radio : this.radios) {
      p.textSize(radio.text_size);
      field_height += p.textAscent() + p.textDescent() + 2;
    }
    return field_height;
  }

  @Override
  public String getValue() {
    return Integer.toString(this.index_selected);
  }
  @Override
  public void setValue(String newValue) {
    if (Misc.isInt(newValue)) {
      this.index_selected = Misc.toInt(newValue);
      this.uncheckOthers();
    }
  }
  public void setMessage(String message) {
    super.setValue(message);
  }

  @Override
  public FormFieldSubmit updateField(int millis) {
    FormFieldSubmit returnValue = super.updateField(millis);
    for (RadioButton radio : this.radios) {
      p.textSize(radio.text_size);
      p.textAlign(PConstants.LEFT, PConstants.TOP);
      p.fill(radio.color_text);
      if (this.message_first) {
        p.text(radio.message, this.radio_padding, radio.yCenter() - radio.radius() + 1);
      }
      else {
        p.text(radio.message, radio.buttonWidth() + 2 * this.radio_padding, radio.yCenter() - radio.radius() + 1);
      }
      radio.update(millis);
    }
    return returnValue;
  }

  @Override
  public void mouseMoveField(float mX, float mY) {
    for (RadioButton radio : this.radios) {
      radio.mouseMove(mX, mY);
    }
  }

  @Override
  public void mousePressField() {
    for (int i = 0; i < this.radios.size(); i++) {
      RadioButton radio = this.radios.get(i);
      boolean pressed = radio.checked;
      radio.mousePress();
      if (!pressed && radio.checked) {
        this.index_selected = i;
        this.uncheckOthers();
      }
      else if (pressed && !radio.checked) {
        this.index_selected = -1;
        this.uncheckOthers();
      }
    }
  }

  void uncheckOthers() {
    for (int i = 0; i < this.radios.size(); i++) {
      if (i == this.index_selected) {
        continue;
      }
      this.radios.get(i).checked = false;
    }
  }

  @Override
  public void mouseReleaseField(float mX, float mY) {
    for (RadioButton radio : this.radios) {
      radio.mouseRelease(mX, mY);
    }
  }
}