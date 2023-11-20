package ButtonTest;

import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.mockito.*;
import processing.core.*;
import Button.ToggleButton;
import Mocks.MockLNZApplet;

public class ToggleButtonTest extends ImageButtonTest {
  private class MockToggleButton extends ToggleButton {
    MockToggleButton(MockLNZApplet p, PImage[] images, double xi, double yi, double xf, double yf) {
      super(p, images, xi, yi, xf, yf);
    }
    public void hover() {
      ToggleButtonTest.this.hover_calls++;
    }
    public void dehover() {
      ToggleButtonTest.this.dehover_calls++;
    }
    public void click() {
      ToggleButtonTest.this.click_calls++;
    }
    public void release() {
      ToggleButtonTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    PImage[] images = new PImage[2];
    images[0] = p.createImage(1, 1, PConstants.ARGB);
    images[1] = p.createImage(1, 1, PConstants.ARGB);
    this.button = Mockito.spy(new MockToggleButton(p, images, 0, 0, 0, 0));
  }
}

// TODO: Add tests specific to ToggleButton