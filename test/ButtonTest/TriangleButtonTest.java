package ButtonTest;

import static org.junit.Assert.assertEquals;

import java.util.*;
import org.mockito.*;
import org.junit.*;
import Button.TriangleButton;
import Mocks.*;

public class TriangleButtonTest extends ButtonTest {
  private class MockTriangleButton extends TriangleButton {
    MockTriangleButton(MockLNZApplet p, double x1, double y1, double x2,
      double y2, double x3, double y3) {
      super(p, x1, y1, x2, y2, x3, y3);
    }
    public void hover() {
      TriangleButtonTest.this.hover_calls++;
    }
    public void dehover() {
      TriangleButtonTest.this.dehover_calls++;
    }
    public void click() {
      TriangleButtonTest.this.click_calls++;
    }
    public void release() {
      TriangleButtonTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockTriangleButton(p, 0, 0, 0, 0, 0, 0));
  }

  @Test
  public void testImplementation() {
    super.testMoveButton();
  }

  @Test
  public void testLocation() {
    Map<MockTriangleButton, LocationTestData> test_data = this.getLocationData();
    Iterator<Map.Entry<MockTriangleButton, LocationTestData>> iterator =
      test_data.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<MockTriangleButton, LocationTestData> entry = iterator.next();
      MockTriangleButton button = entry.getKey();
      LocationTestData data = entry.getValue();
      String message = button.toString() + "\n" + data.toString();
      assertEquals(message, data.x_center, button.xCenter(), 0.001);
      assertEquals(message, data.y_center, button.yCenter(), 0.001);
      assertEquals(message, data.button_width, button.buttonWidth(), 0.001);
      assertEquals(message, data.button_height, button.buttonHeight(), 0.001);
    }
  }
  private Map<MockTriangleButton, LocationTestData> getLocationData() {
    Map<MockTriangleButton, LocationTestData> data =
      new HashMap<MockTriangleButton, LocationTestData>();
    // point button
    MockTriangleButton button = new MockTriangleButton(p, 0, 0, 0, 0, 0, 0);
    LocationTestData expected = new LocationTestData(0, 0, 0, 0);
    data.put(button, expected);
    // positive button
    button = new MockTriangleButton(p, 1, 0, 3, 4, 0, -2);
    expected = new LocationTestData(4f/3, 2f/3, 3, 6);
    data.put(button, expected);
    // negative button
    button = new MockTriangleButton(p, -1, -3, 0.5, 4, -1, 1);
    expected = new LocationTestData(-0.5, 2f/3, 1.5, 7);
    data.put(button, expected);
    // inverted button
    button = new MockTriangleButton(p, 1, 0, 2, 1, 0.5, 6);
    expected = new LocationTestData(7f/6, 7f/3, 1.5, 6);
    data.put(button, expected);
    return data;
  }

  @Test
  public void testMouseOn() {
    Map<MockTriangleButton, List<MouseOnTestData>> test_data = this.getMouseOnData();
    Iterator<Map.Entry<MockTriangleButton, List<MouseOnTestData>>> iterator =
      test_data.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<MockTriangleButton, List<MouseOnTestData>> entry = iterator.next();
      MockTriangleButton button = entry.getKey();
      for (MouseOnTestData data : entry.getValue()) {
        String message = button.toString() + "\n" + data.toString();
        assertEquals(message, data.expected, button.mouseOn(data.x, data.y));
      }
    }
  }
  private Map<MockTriangleButton, List<MouseOnTestData>> getMouseOnData() {
    Map<MockTriangleButton, List<MouseOnTestData>> data =
      new HashMap<MockTriangleButton, List<MouseOnTestData>>();
    // point button
    MockTriangleButton button = new MockTriangleButton(p, 0, 0, 0, 0, 0, 0);
    List<MouseOnTestData> list = new ArrayList<MouseOnTestData>();
    list.add(new MouseOnTestData(0, 0, false));
    data.put(button, list);
    // positive button
    button = new MockTriangleButton(p, 1, 1, 2, 3, 0.5, -0.5);
    list = new ArrayList<MouseOnTestData>();
    list.add(new MouseOnTestData(0, 0, false));
    list.add(new MouseOnTestData(2, 2, false));
    list.add(new MouseOnTestData(1.5f, 2f, true));
    data.put(button, list);
    return data;
  }
}