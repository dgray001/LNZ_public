package ButtonTest;

import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.mockito.*;
import processing.core.*;
import Button.RippleCircleButton;
import Mocks.MockLNZApplet;

public class RippleCircleButtonTest extends RippleRectangleButtonTest {
  private class MockRippleCircleButton extends RippleCircleButton {
    MockRippleCircleButton(MockLNZApplet p, double xc, double yc, double r) {
      super(p, xc, yc, r);
    }
    public void hover() {
      RippleCircleButtonTest.this.hover_calls++;
    }
    public void dehover() {
      RippleCircleButtonTest.this.dehover_calls++;
    }
    public void click() {
      RippleCircleButtonTest.this.click_calls++;
    }
    public void release() {
      RippleCircleButtonTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockRippleCircleButton(p, 0, 0, 1));
  }
}

// TODO: Add tests specific to RippleCircleButton