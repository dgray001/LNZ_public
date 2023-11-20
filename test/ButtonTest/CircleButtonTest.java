package ButtonTest;

import org.mockito.*;
import Button.CircleButton;
import Mocks.MockLNZApplet;

public class CircleButtonTest extends EllipseButtonTest {
  private class MockCircleButton extends CircleButton {
    MockCircleButton(MockLNZApplet p, double xc, double yc, double r) {
      super(p, xc, yc, r);
    }
    public void hover() {
      CircleButtonTest.this.hover_calls++;
    }
    public void dehover() {
      CircleButtonTest.this.dehover_calls++;
    }
    public void click() {
      CircleButtonTest.this.click_calls++;
    }
    public void release() {
      CircleButtonTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockCircleButton(p, 0, 0, 0));
  }
}