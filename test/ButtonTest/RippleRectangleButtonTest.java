package ButtonTest;

import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.mockito.*;
import processing.core.*;
import Button.RippleRectangleButton;
import Mocks.MockLNZApplet;

public class RippleRectangleButtonTest extends ImageButtonTest {
  private class MockRippleRectangleButton extends RippleRectangleButton {
    MockRippleRectangleButton(MockLNZApplet p, double xi, double yi, double xf, double yf) {
      super(p, xi, yi, xf, yf);
    }
    public void hover() {
      RippleRectangleButtonTest.this.hover_calls++;
    }
    public void dehover() {
      RippleRectangleButtonTest.this.dehover_calls++;
    }
    public void click() {
      RippleRectangleButtonTest.this.click_calls++;
    }
    public void release() {
      RippleRectangleButtonTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockRippleRectangleButton(p, 0, 0, 0, 0));
  }
}

// TODO: Add tests specific to ripple rectangle button