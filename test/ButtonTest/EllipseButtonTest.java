package ButtonTest;

import static org.junit.Assert.assertEquals;

import java.util.*;
import org.mockito.*;
import org.junit.*;
import Button.EllipseButton;
import Mocks.*;

public class EllipseButtonTest extends ButtonTest {
  private class MockEllipseButton extends EllipseButton {
    MockEllipseButton(MockLNZApplet p, double xc, double yc, double xr, double yr) {
      super(p, xc, yc, xr, yr);
    }
    public void hover() {
      EllipseButtonTest.this.hover_calls++;
    }
    public void dehover() {
      EllipseButtonTest.this.dehover_calls++;
    }
    public void click() {
      EllipseButtonTest.this.click_calls++;
    }
    public void release() {
      EllipseButtonTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockEllipseButton(p, 0, 0, 0, 0));
  }

  @Test
  public void testImplementation() {
    super.testMoveButton();
  }

  @Test
  public void testLocation() {
    Map<MockEllipseButton, LocationTestData> test_data = this.getLocationData();
    Iterator<Map.Entry<MockEllipseButton, LocationTestData>> iterator =
      test_data.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<MockEllipseButton, LocationTestData> entry = iterator.next();
      MockEllipseButton button = entry.getKey();
      LocationTestData data = entry.getValue();
      String message = button.toString() + "\n" + data.toString();
      assertEquals(message, data.x_center, button.xCenter(), 0.001);
      assertEquals(message, data.y_center, button.yCenter(), 0.001);
      assertEquals(message, data.button_width, button.buttonWidth(), 0.001);
      assertEquals(message, data.button_height, button.buttonHeight(), 0.001);
    }
  }
  private Map<MockEllipseButton, LocationTestData> getLocationData() {
    Map<MockEllipseButton, LocationTestData> data =
      new HashMap<MockEllipseButton, LocationTestData>();
    // point button
    MockEllipseButton button = new MockEllipseButton(p, 0, 0, 0, 0);
    LocationTestData expected = new LocationTestData(0, 0, 0, 0);
    data.put(button, expected);
    // positive button
    button = new MockEllipseButton(p, 1, 0, 3, 4);
    expected = new LocationTestData(1, 0, 6, 8);
    data.put(button, expected);
    // negative button
    button = new MockEllipseButton(p, -1, -3, -0.5, 4);
    expected = new LocationTestData(-1, -3, -1, 8);
    data.put(button, expected);
    // inverted button
    button = new MockEllipseButton(p, 1, 0, 2, 1);
    expected = new LocationTestData(1, 0, 4, 2);
    data.put(button, expected);
    return data;
  }

  @Test
  public void testMouseOn() {
    Map<MockEllipseButton, List<MouseOnTestData>> test_data = this.getMouseOnData();
    Iterator<Map.Entry<MockEllipseButton, List<MouseOnTestData>>> iterator =
      test_data.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<MockEllipseButton, List<MouseOnTestData>> entry = iterator.next();
      MockEllipseButton button = entry.getKey();
      for (MouseOnTestData data : entry.getValue()) {
        String message = button.toString() + "\n" + data.toString();
        assertEquals(message, data.expected, button.mouseOn(data.x, data.y));
      }
    }
    this.button = new MockEllipseButton(p, 1, 1, 2, 2);
  }
  private Map<MockEllipseButton, List<MouseOnTestData>> getMouseOnData() {
    Map<MockEllipseButton, List<MouseOnTestData>> data =
      new HashMap<MockEllipseButton, List<MouseOnTestData>>();
    // point button
    MockEllipseButton button = new MockEllipseButton(p, 0, 0, 0, 0);
    List<MouseOnTestData> list = new ArrayList<MouseOnTestData>();
    list.add(new MouseOnTestData(0, 0, false));
    data.put(button, list);
    // positive button
    button = new MockEllipseButton(p, 1, -1, 2, 3);
    list = new ArrayList<MouseOnTestData>();
    list.add(new MouseOnTestData(0, 0, true));
    list.add(new MouseOnTestData(1, 2.01f, false));
    list.add(new MouseOnTestData(1, 2, true));
    list.add(new MouseOnTestData(2, 2, false));
    list.add(new MouseOnTestData(2, 1.55f, true));
    data.put(button, list);
    // inverted button
    button = new MockEllipseButton(p, -3, 0, -1, 1);
    list = new ArrayList<MouseOnTestData>();
    list.add(new MouseOnTestData(0, 0, false));
    data.put(button, list);
    return data;
  }
}