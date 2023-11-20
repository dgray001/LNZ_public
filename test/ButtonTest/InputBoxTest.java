package ButtonTest;

import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.mockito.*;
import processing.core.*;
import Button.InputBox;
import Mocks.MockLNZApplet;

public class InputBoxTest extends RectangleButtonTest {
  private class MockInputBox extends InputBox {
    MockInputBox(MockLNZApplet p, double xi, double yi, double xf, double yf) {
      super(p, xi, yi, xf, yf);
    }
    public void hover() {
      InputBoxTest.this.hover_calls++;
    }
    public void dehover() {
      InputBoxTest.this.dehover_calls++;
    }
    public void click() {
      InputBoxTest.this.click_calls++;
    }
    public void release() {
      InputBoxTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockInputBox(p, 0, 0, 0, 0));
  }
}

// TODO: Add tests for input box