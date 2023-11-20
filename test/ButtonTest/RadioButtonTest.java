package ButtonTest;

import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.mockito.*;
import processing.core.*;
import Button.RadioButton;
import Mocks.MockLNZApplet;

public class RadioButtonTest extends CircleButtonTest {
  private class MockRadioButton extends RadioButton {
    MockRadioButton(MockLNZApplet p, double xc, double yc, double r) {
      super(p, xc, yc, r);
    }
    public void hover() {
      RadioButtonTest.this.hover_calls++;
    }
    public void dehover() {
      RadioButtonTest.this.dehover_calls++;
    }
    public void click() {
      RadioButtonTest.this.click_calls++;
    }
    public void release() {
      RadioButtonTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockRadioButton(p, 0, 0, 0));
  }
}