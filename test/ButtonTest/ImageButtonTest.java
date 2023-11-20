package ButtonTest;

import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.mockito.*;
import processing.core.*;
import Button.ImageButton;
import Mocks.MockLNZApplet;

public class ImageButtonTest extends RectangleButtonTest {
  private class MockImageButton extends ImageButton {
    MockImageButton(MockLNZApplet p, PImage img, double xi, double yi, double xf, double yf) {
      super(p, img, xi, yi, xf, yf);
    }
    public void hover() {
      ImageButtonTest.this.hover_calls++;
    }
    public void dehover() {
      ImageButtonTest.this.dehover_calls++;
    }
    public void click() {
      ImageButtonTest.this.click_calls++;
    }
    public void release() {
      ImageButtonTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockImageButton(p,
      p.createImage(1, 1, PConstants.ARGB), 0, 0, 0, 0));
  }
}