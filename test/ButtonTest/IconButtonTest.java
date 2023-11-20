package ButtonTest;

import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.mockito.*;
import processing.core.*;
import Button.IconButton;
import Mocks.MockLNZApplet;

public class IconButtonTest extends RippleRectangleButtonTest {
  private class MockIconButton extends IconButton {
    MockIconButton(MockLNZApplet p, double xi, double yi, double xf, double yf, PImage icon) {
      super(p, xi, yi, xf, yf, icon);
    }
    public void hover() {
      super.hover();
      IconButtonTest.this.hover_calls++;
    }
    public void dehover() {
      super.dehover();
      IconButtonTest.this.dehover_calls++;
    }
    public void click() {
      super.click();
      IconButtonTest.this.click_calls++;
    }
    public void release() {
      super.release();
      IconButtonTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockIconButton(p, 0, 0, 0, 0,
      p.createImage(1, 1, PConstants.ARGB)));
  }

  @Test
  public void testSetLocation() {
    IconButton button = (IconButton)this.button;
    button.setLocation(0, 0, 2, 4);
    assertEquals(4, button.icon_width, 0.001);
  }
}