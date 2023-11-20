package ButtonTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.doAnswer;

import org.junit.*;
import org.mockito.*;

import processing.core.PConstants;
import Button.Button;
import Mocks.MockLNZApplet;

public class ButtonTest {
  public int hover_calls = 0;
  public int dehover_calls = 0;
  public int click_calls = 0;
  public int release_calls = 0;

  public class MockButton extends Button {
    MockButton(MockLNZApplet p) {
      super(p);
    }
    public double xCenter() {
      return 0;
    }
    public double yCenter() {
      return 0;
    }
    public double buttonWidth() {
      return 0;
    }
    public double buttonHeight() {
      return 0;
    }
    public void drawButton() {}
    public void moveButton(double xMove, double yMove) {}
    public boolean mouseOn(float mX, float mY) {
      return false;
    }
    public void hover() {
      ButtonTest.this.hover_calls++;
    }
    public void dehover() {
      ButtonTest.this.dehover_calls++;
    }
    public void click() {
      ButtonTest.this.click_calls++;
    }
    public void release() {
      ButtonTest.this.release_calls++;
    }
  }

  public MockLNZApplet p;
  public Button button;

  public void assertCalls(int hover_calls, int dehover_calls,
    int click_calls, int release_calls) {
    assertEquals(hover_calls, this.hover_calls);
    assertEquals(dehover_calls, this.dehover_calls);
    assertEquals(click_calls, this.click_calls);
    assertEquals(release_calls, this.release_calls);
  }

  public void instantiateButton() {
    this.button = Mockito.spy(new MockButton(p));
  }

  @Before
  public void setup() {
    this.p = new MockLNZApplet();
    this.instantiateButton();
    doAnswer(
      invocation -> {
        Object[] arguments = invocation.getArguments();
        float mX = (float)arguments[0];
        float mY = (float)arguments[1];
        if (mX == 0 && mY == 0) {
          return true;
        }
        return false;
      }
    ).when(this.button).mouseOn(anyFloat(), anyFloat());
    this.hover_calls = 0;
    this.dehover_calls = 0;
    this.click_calls = 0;
    this.release_calls = 0;
    p.mouseButton = PConstants.LEFT;
  }

  // Should be called on button implementations that define moveButton
  public void testMoveButton() {
    double x_center = this.button.xCenter();
    double y_center = this.button.yCenter();
    double button_width = this.button.buttonWidth();
    double button_height = this.button.buttonHeight();
    this.button.moveButton(-1, 2);
    assertEquals(x_center - 1, this.button.xCenter(), 0.001);
    assertEquals(y_center + 2, this.button.yCenter(), 0.001);
    assertEquals(button_width, this.button.buttonWidth(), 0.001);
    assertEquals(button_height, this.button.buttonHeight(), 0.001);
  }
  
  @Test
  public void testMouseMove() {
    this.button.mouseMove(0, 1); // test not hovered
    assertEquals(false, button.hovered);
    this.assertCalls(0, 0, 0, 0);

    this.button.mouseMove(0, 0); // test hovered
    assertEquals(true, button.hovered);
    this.assertCalls(1, 0, 0, 0);

    this.button.mouseMove(0, 0); // test stay hovered (doesn't call hovered again)
    assertEquals(true, button.hovered);
    this.assertCalls(1, 0, 0, 0);

    this.button.mouseMove(1, 0); // test dehovered
    assertEquals(false, button.hovered);
    this.assertCalls(1, 1, 0, 0);
  }

  @Test
  public void testMousePress() {
    this.button.mousePress(); // test not hovered
    assertEquals(false, button.clicked);
    this.assertCalls(0, 0, 0, 0);

    this.button.mouseMove(0, 0); // test hovered
    this.button.mousePress();
    assertEquals(true, button.clicked);
    this.assertCalls(1, 0, 1, 0);

    this.button.mousePress(); // test repeat clicks
    assertEquals(true, button.clicked);
    this.assertCalls(1, 0, 2, 0);

    p.mouseButton = PConstants.RIGHT; // test force left button
    this.button.mousePress();
    assertEquals(true, button.clicked);
    this.assertCalls(1, 0, 2, 0);

    this.button.force_left_button = false; // turn off force left button
    this.button.mousePress();
    assertEquals(true, button.clicked);
    this.assertCalls(1, 0, 3, 0);
  }

  @Test
  public void testMouseRelease() {
    this.button.mouseRelease(0, 1); // test not hovered
    assertEquals(false, button.clicked);
    this.assertCalls(0, 0, 0, 0);

    this.button.mouseMove(0, 0); // test not clicked
    this.button.mouseRelease(0, 0);
    assertEquals(false, button.clicked);
    this.assertCalls(1, 0, 0, 0);

    this.button.mousePress(); // test clicked
    this.button.mouseRelease(0, 0);
    assertEquals(false, button.clicked);
    this.assertCalls(1, 0, 1, 1);

    this.button.mouseRelease(0, 0); // test repeat releases
    assertEquals(false, button.clicked);
    this.assertCalls(1, 0, 1, 1);

    this.button.mouseRelease(0, 1); // test hover_check_after_release
    assertEquals(false, button.hovered);
    assertEquals(false, button.clicked);
    this.assertCalls(1, 1, 1, 1);

    p.mouseButton = PConstants.RIGHT; // test force left button
    this.button.mouseMove(0, 0);
    this.button.mousePress();
    this.button.mouseRelease(0, 0);
    assertEquals(true, button.hovered);
    assertEquals(false, button.clicked);
    this.assertCalls(2, 1, 1, 1);
  }

  @Test
  public void testKeyPress() {
    this.button.keyPress(PConstants.RETURN, 0); // test not focused
    assertEquals(false, button.clicked);
    this.assertCalls(0, 0, 0, 0);

    this.button.button_focused = true; // test focused
    this.button.keyPress(PConstants.RETURN, 0);
    assertEquals(true, button.clicked);
    this.assertCalls(0, 0, 1, 0);

    this.button.button_focused = true; // test repeat clicks
    this.button.keyPress(PConstants.ENTER, 0);
    assertEquals(true, button.clicked);
    this.assertCalls(0, 0, 2, 0);

    this.button.button_focused = true; // test wrong key
    this.button.clicked = false;
    this.button.keyPress('a', 0);
    assertEquals(false, button.clicked);
    this.assertCalls(0, 0, 2, 0);
  }

  @Test
  public void testKeyRelease() {
    this.button.keyRelease(PConstants.RETURN, 0); // test not focused
    button.clicked = true;
    assertEquals(true, button.clicked);
    this.assertCalls(0, 0, 0, 0);

    this.button.button_focused = true; // test focused
    this.button.keyRelease(PConstants.RETURN, 0);
    assertEquals(false, button.clicked);
    this.assertCalls(0, 0, 0, 1);

    this.button.button_focused = true; // test ENTER key
    button.clicked = true;
    this.button.keyRelease(PConstants.ENTER, 0);
    assertEquals(false, button.clicked);
    this.assertCalls(0, 0, 0, 2);

    this.button.button_focused = true; // test repeat releases
    this.button.keyRelease(PConstants.ENTER, 0);
    assertEquals(false, button.clicked);
    this.assertCalls(0, 0, 0, 2);

    this.button.button_focused = true; // test wrong key
    button.clicked = true;
    this.button.keyRelease('a', 0);
    assertEquals(true, button.clicked);
    this.assertCalls(0, 0, 0, 2);
  }

  @Test
  public void testDisable() {
    this.button.disable();

    this.button.mouseMove(0, 0); // test mouse move
    assertEquals(false, button.hovered);
    this.assertCalls(0, 0, 0, 0);

    this.button.hovered = true; // test mouse press
    this.button.mousePress();
    assertEquals(false, button.clicked);
    this.assertCalls(0, 0, 0, 0);

    this.button.clicked = true; // test mouse release
    this.button.mouseRelease(0, 0);
    assertEquals(true, button.clicked);
    this.assertCalls(0, 0, 0, 0);

    this.button.button_focused = true; // test key press
    this.button.keyPress(PConstants.ENTER, 0);
    assertEquals(true, button.clicked);
    this.assertCalls(0, 0, 0, 0);

    this.button.keyRelease(PConstants.ENTER, 0); // test key release
    assertEquals(true, button.clicked);
    this.assertCalls(0, 0, 0, 0);
  }
}