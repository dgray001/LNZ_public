package ButtonTest;

import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.mockito.*;
import processing.core.*;
import Button.IconInverseButton;
import Mocks.MockLNZApplet;

public class IconInverseButtonTest extends IconButtonTest {
  private class MockIconInverseButton extends IconInverseButton {
    MockIconInverseButton(MockLNZApplet p, double xi, double yi, double xf, double yf, PImage icon) {
      super(p, xi, yi, xf, yf, icon);
    }
    public void hover() {
      super.hover();
      IconInverseButtonTest.this.hover_calls++;
    }
    public void dehover() {
      super.dehover();
      IconInverseButtonTest.this.dehover_calls++;
    }
    public void click() {
      super.click();
      IconInverseButtonTest.this.click_calls++;
    }
    public void release() {
      super.release();
      IconInverseButtonTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockIconInverseButton(p, 0, 0, 0, 0,
      p.createImage(0, 0, PConstants.ARGB)));
  }
}