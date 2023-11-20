package ElementTest;

import static org.junit.Assert.assertEquals;

import org.junit.*;

import org.mockito.*;
import ButtonTest.RectangleButtonTest;
import Element.ScrollBar;
import Mocks.MockLNZApplet;

public class ScrollBarTest {
  private class MockScrollBar extends ScrollBar {
    public class ScrollBarButtonTest extends RectangleButtonTest {
      private class MockScrollBarButton extends ScrollBarButton {
        MockScrollBarButton(MockLNZApplet p, double xi, double yi, double xf, double yf) {
          super(p, xi, yi, xf, yf);
        }
        public void hover() {
          ScrollBarButtonTest.this.hover_calls++;
        }
        public void dehover() {
          ScrollBarButtonTest.this.dehover_calls++;
        }
        public void click() {
          ScrollBarButtonTest.this.click_calls++;
        }
        public void release() {
          ScrollBarButtonTest.this.release_calls++;
        }
      }
      @Override
      public void instantiateButton() {
        this.button = Mockito.spy(new MockScrollBarButton(p, 0, 0, 0, 0));
      }
    }

    MockScrollBar(MockLNZApplet p, double xi, double yi, double xf, double yf, boolean vertical) {
      super(p, xi, yi, xf, yf, vertical);
    }
  }
  
  @Test
  public void testTest() {
    assertEquals(true, true);
  } 
}