package ButtonTest;

import static org.junit.Assert.assertEquals;

import java.util.*;
import org.junit.*;
import org.mockito.*;
import processing.core.*;
import Button.RectangleButton;
import Mocks.*;

public class RectangleButtonTest extends ButtonTest {
  private class MockRectangleButton extends RectangleButton {
    MockRectangleButton(MockLNZApplet p, double xi, double yi, double xf, double yf) {
      super(p, xi, yi, xf, yf);
    }
    public void hover() {
      RectangleButtonTest.this.hover_calls++;
    }
    public void dehover() {
      RectangleButtonTest.this.dehover_calls++;
    }
    public void click() {
      RectangleButtonTest.this.click_calls++;
    }
    public void release() {
      RectangleButtonTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockRectangleButton(p, 1, 2, 4, 6));
  }

  @Test
  public void testImplementation() {
    super.testMoveButton();
  }

  @Test
  public void testLocation() {
    Map<MockRectangleButton, LocationTestData> test_data = this.getLocationData();
    Iterator<Map.Entry<MockRectangleButton, LocationTestData>> iterator =
      test_data.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<MockRectangleButton, LocationTestData> entry = iterator.next();
      MockRectangleButton button = entry.getKey();
      LocationTestData data = entry.getValue();
      String message = button.toString() + "\n" + data.toString();
      assertEquals(message, data.x_center, button.xCenter(), 0.001);
      assertEquals(message, data.y_center, button.yCenter(), 0.001);
      assertEquals(message, data.button_width, button.buttonWidth(), 0.001);
      assertEquals(message, data.button_height, button.buttonHeight(), 0.001);
    }
  }
  private Map<MockRectangleButton, LocationTestData> getLocationData() {
    Map<MockRectangleButton, LocationTestData> data =
      new HashMap<MockRectangleButton, LocationTestData>();
    // point button
    MockRectangleButton button = new MockRectangleButton(p, 0, 0, 0, 0);
    LocationTestData expected = new LocationTestData(0, 0, 0, 0);
    data.put(button, expected);
    // positive button
    button = new MockRectangleButton(p, 1, 0, 3, 4);
    expected = new LocationTestData(2, 2, 2, 4);
    data.put(button, expected);
    // negative button
    button = new MockRectangleButton(p, -1, -3, -0.5, 4);
    expected = new LocationTestData(-0.75, 0.5, 0.5, 7);
    data.put(button, expected);
    // inverted button
    button = new MockRectangleButton(p, 1, 0, -2, 2);
    expected = new LocationTestData(-0.5, 1, -3, 2);
    data.put(button, expected);
    return data;
  }

  @Test
  public void testStretchButton() {
    RectangleButton button = (RectangleButton)this.button;
    // test up
    double x_center = this.button.xCenter();
    double y_center = this.button.yCenter();
    double button_width = this.button.buttonWidth();
    double button_height = this.button.buttonHeight();
    button.stretchButton(-1, PConstants.UP);
    assertEquals(x_center, this.button.xCenter(), 0.001);
    assertEquals(y_center + 0.5, this.button.yCenter(), 0.001);
    assertEquals(button_width, this.button.buttonWidth(), 0.001);
    assertEquals(button_height - 1, this.button.buttonHeight(), 0.001);
    // test left
    x_center = this.button.xCenter();
    y_center = this.button.yCenter();
    button_width = this.button.buttonWidth();
    button_height = this.button.buttonHeight();
    button.stretchButton(2, PConstants.LEFT);
    assertEquals(x_center - 1, this.button.xCenter(), 0.001);
    assertEquals(y_center, this.button.yCenter(), 0.001);
    assertEquals(button_width + 2, this.button.buttonWidth(), 0.001);
    assertEquals(button_height, this.button.buttonHeight(), 0.001);
    // test down
    x_center = this.button.xCenter();
    y_center = this.button.yCenter();
    button_width = this.button.buttonWidth();
    button_height = this.button.buttonHeight();
    button.stretchButton(-2, PConstants.DOWN);
    assertEquals(x_center, this.button.xCenter(), 0.001);
    assertEquals(y_center - 1, this.button.yCenter(), 0.001);
    assertEquals(button_width, this.button.buttonWidth(), 0.001);
    assertEquals(button_height - 2, this.button.buttonHeight(), 0.001);
    // test right
    x_center = this.button.xCenter();
    y_center = this.button.yCenter();
    button_width = this.button.buttonWidth();
    button_height = this.button.buttonHeight();
    button.stretchButton(3, PConstants.RIGHT);
    assertEquals(x_center + 1.5, this.button.xCenter(), 0.001);
    assertEquals(y_center, this.button.yCenter(), 0.001);
    assertEquals(button_width + 3, this.button.buttonWidth(), 0.001);
    assertEquals(button_height, this.button.buttonHeight(), 0.001);
  }

  @Test
  public void testMouseOn() {
    Map<MockRectangleButton, List<MouseOnTestData>> test_data = this.getMouseOnData();
    Iterator<Map.Entry<MockRectangleButton, List<MouseOnTestData>>> iterator =
      test_data.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<MockRectangleButton, List<MouseOnTestData>> entry = iterator.next();
      MockRectangleButton button = entry.getKey();
      for (MouseOnTestData data : entry.getValue()) {
        String message = button.toString() + "\n" + data.toString();
        assertEquals(message, data.expected, button.mouseOn(data.x, data.y));
      }
    }
  }
  private Map<MockRectangleButton, List<MouseOnTestData>> getMouseOnData() {
    Map<MockRectangleButton, List<MouseOnTestData>> data =
      new HashMap<MockRectangleButton, List<MouseOnTestData>>();
    // point button
    MockRectangleButton button = new MockRectangleButton(p, 0, 0, 0, 0);
    List<MouseOnTestData> list = new ArrayList<MouseOnTestData>();
    list.add(new MouseOnTestData(0, 0, true));
    list.add(new MouseOnTestData(-0.1f, 0, false));
    list.add(new MouseOnTestData(0, 0.1f, false));
    list.add(new MouseOnTestData(0.000001f, 0, false));
    data.put(button, list);
    // positive button
    button = new MockRectangleButton(p, 1, -1, 2, 3);
    list = new ArrayList<MouseOnTestData>();
    list.add(new MouseOnTestData(0, 0, false));
    list.add(new MouseOnTestData(2, 3, true));
    list.add(new MouseOnTestData(2.01f, 3, false));
    list.add(new MouseOnTestData(1.5f, -1, true));
    data.put(button, list);
    // inverted button
    button = new MockRectangleButton(p, -3, 0, -1, -1);
    list = new ArrayList<MouseOnTestData>();
    list.add(new MouseOnTestData(0, 0, false));
    data.put(button, list);
    return data;
  }
}
