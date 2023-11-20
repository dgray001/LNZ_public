package LNZModule;

import processing.core.*;
import Form.*;
import Misc.Misc;

public class FeatureEditForm extends EditMapObjectForm {
  protected Feature feature;

  FeatureEditForm(LNZ sketch, Feature feature) {
    super(sketch, feature);
    this.feature = feature;
    this.addField(new IntegerFormField(p, "Number: ", "number", Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1));
    this.addField(new IntegerFormField(p, "Number 2: ", "number 2", Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1));
    this.addField(new IntegerFormField(p, "Timer: ", "number", -1, Integer.MAX_VALUE - 1));
    this.addField(new CheckboxFormField(p, "Toggle:  "));
    switch(this.feature.ID) {
      case 151: // Sign, green
      case 152:
      case 153:
      case 154:
      case 155: // Sign, gray
      case 156:
      case 157:
      case 158:
        this.addField(new StringFormField(p, "Message: ", "enter the message for the sign"));
        break;
      default:
        break;
    }
    this.addField(new SubmitFormField(p, "Finished", false));
    this.updateForm();
  }

  void updateObject() {
    this.feature.number = Misc.toInt(this.fields.get(1).getValue());
    this.feature.number2 = Misc.toInt(this.fields.get(2).getValue());
    this.feature.timer = Misc.toInt(this.fields.get(3).getValue());
    this.feature.toggle = Misc.toBoolean(this.fields.get(4).getValue());
    switch(this.feature.ID) {
      case 151: // Sign, green
      case 152:
      case 153:
      case 154:
      case 155: // Sign, gray
      case 156:
      case 157:
      case 158:
        try {
          this.feature.description = PApplet.split(this.feature.description, LNZ.
            feature_signDescriptionDelimiter)[0] + LNZ.
            feature_signDescriptionDelimiter + this.fields.get(5).getValue();
        } catch(Exception e) {}
        break;
      default:
        break;
    }
  }

  void updateForm() {
    this.fields.get(1).setValueIfNotFocused(Integer.toString(this.feature.number));
    this.fields.get(2).setValueIfNotFocused(Integer.toString(this.feature.number2));
    this.fields.get(3).setValueIfNotFocused(Integer.toString(this.feature.timer));
    this.fields.get(4).setValueIfNotFocused(Boolean.toString(this.feature.toggle));
    switch(this.feature.ID) {
      case 151: // Sign, green
      case 152:
      case 153:
      case 154:
      case 155: // Sign, gray
      case 156:
      case 157:
      case 158:
        try {
          this.fields.get(5).setValueIfNotFocused(PApplet.split(
            this.feature.description, LNZ.feature_signDescriptionDelimiter)[1]);
        } catch(Exception e) {}
        break;
      default:
        break;
    }
  }
}