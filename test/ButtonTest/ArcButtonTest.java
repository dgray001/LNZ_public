package ButtonTest;

import static org.junit.Assert.assertEquals;

import java.util.*;
import org.mockito.*;
import org.junit.*;
import Button.ArcButton;
import Mocks.*;

public class ArcButtonTest extends ButtonTest {
  private class MockArcButton extends ArcButton {
    MockArcButton(MockLNZApplet p, double xc, double yc, double xr,
      double yr, double start, double stop) {
      super(p, xc, yc, xr, yr, start, stop);
    }
    public void hover() {
      ArcButtonTest.this.hover_calls++;
    }
    public void dehover() {
      ArcButtonTest.this.dehover_calls++;
    }
    public void click() {
      ArcButtonTest.this.click_calls++;
    }
    public void release() {
      ArcButtonTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockArcButton(p, 0, 0, 0, 0, 0, 0));
  }

  @Test
  public void testImplementation() {
    super.testMoveButton();
  }

  @Test
  public void testLocation() {
    Map<MockArcButton, LocationTestData> test_data = this.getLocationData();
    Iterator<Map.Entry<MockArcButton, LocationTestData>> iterator =
      test_data.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<MockArcButton, LocationTestData> entry = iterator.next();
      MockArcButton button = entry.getKey();
      LocationTestData data = entry.getValue();
      String message = button.toString() + "\n" + data.toString();
      assertEquals(message, data.x_center, button.xCenter(), 0.001);
      assertEquals(message, data.y_center, button.yCenter(), 0.001);
      assertEquals(message, data.button_width, button.buttonWidth(), 0.001);
      assertEquals(message, data.button_height, button.buttonHeight(), 0.001);
    }
  }
  private Map<MockArcButton, LocationTestData> getLocationData() {
    Map<MockArcButton, LocationTestData> data =
      new HashMap<MockArcButton, LocationTestData>();
    // point button
    MockArcButton button = new MockArcButton(p, 0, 0, 0, 0, 0, 0);
    LocationTestData expected = new LocationTestData(0, 0, 0, 0);
    data.put(button, expected);
    // positive button
    button = new MockArcButton(p, 1, 0, 3, 4, 0, Math.PI);
    expected = new LocationTestData(1, 0, 6, 8);
    data.put(button, expected);
    // negative button
    button = new MockArcButton(p, -1, -3, 0.5, 4, -Math.PI, -0.1 * Math.PI);
    expected = new LocationTestData(-1, -3, 1, 8);
    data.put(button, expected);
    // inverted button
    button = new MockArcButton(p, 1, 0, 2, 1, 0.5 * Math.PI, - 0.5 * Math.PI);
    expected = new LocationTestData(1, 0, 4, 2);
    data.put(button, expected);
    return data;
  }

  @Test
  public void testMouseOn() {
    Map<MockArcButton, List<MouseOnTestData>> test_data = this.getMouseOnData();
    Iterator<Map.Entry<MockArcButton, List<MouseOnTestData>>> iterator =
      test_data.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<MockArcButton, List<MouseOnTestData>> entry = iterator.next();
      MockArcButton button = entry.getKey();
      for (MouseOnTestData data : entry.getValue()) {
        String message = button.toString() + "\n" + data.toString();
        assertEquals(message, data.expected, button.mouseOn(data.x, data.y));
      }
    }
  }
  private Map<MockArcButton, List<MouseOnTestData>> getMouseOnData() {
    Map<MockArcButton, List<MouseOnTestData>> data =
      new HashMap<MockArcButton, List<MouseOnTestData>>();
    // point button
    MockArcButton button = new MockArcButton(p, 0, 0, 0, 0, 0, 0);
    List<MouseOnTestData> list = new ArrayList<MouseOnTestData>();
    list.add(new MouseOnTestData(0, 0, false));
    data.put(button, list);
    // positive button
    button = new MockArcButton(p, 1, 1, 2, 3, 0.5 * Math.PI, 1.5 * Math.PI);
    list = new ArrayList<MouseOnTestData>();
    list.add(new MouseOnTestData(0, 0, true));
    list.add(new MouseOnTestData(2, 2, false));
    data.put(button, list);
    // inverted button
    button = new MockArcButton(p, -3, 0, 1, 1, Math.PI, 0);
    list = new ArrayList<MouseOnTestData>();
    list.add(new MouseOnTestData(-3, 0, false));
    data.put(button, list);
    return data;
  }
}