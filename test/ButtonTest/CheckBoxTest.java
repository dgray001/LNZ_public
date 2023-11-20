package ButtonTest;

import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.mockito.*;
import Button.CheckBox;
import Mocks.MockLNZApplet;

public class CheckBoxTest extends RectangleButtonTest {
  private class MockCheckBoxButton extends CheckBox {
    MockCheckBoxButton(MockLNZApplet p, double xi, double yi, double size) {
      super(p, xi, yi, size);
    }
    public void hover() {
      CheckBoxTest.this.hover_calls++;
    }
    public void dehover() {
      CheckBoxTest.this.dehover_calls++;
    }
    public void click() {
      super.click();
      CheckBoxTest.this.click_calls++;
    }
    public void release() {
      CheckBoxTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockCheckBoxButton(p, 0, 0, 0));
  }

  @Test
  public void testChecked() {
    MockCheckBoxButton button = (MockCheckBoxButton)this.button;
    assertEquals(false, button.checked);
    button.click();
    assertEquals(true, button.checked);
  }
}