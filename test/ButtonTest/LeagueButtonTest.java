package ButtonTest;

import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.mockito.*;
import processing.core.*;
import Button.LeagueButton;
import Mocks.MockLNZApplet;

public class LeagueButtonTest extends ArcButtonTest {
  private class MockLeagueButton extends LeagueButton {
    MockLeagueButton(MockLNZApplet p, double x_bottom, double y_bottom, double x_radius,
      double y_radius, double radians, double trapezoid_height, double trapezoid_shift) {
      super(p, x_bottom, y_bottom, x_radius, y_radius, radians, trapezoid_height, trapezoid_shift);
    }
    public void hover() {
      LeagueButtonTest.this.hover_calls++;
    }
    public void dehover() {
      LeagueButtonTest.this.dehover_calls++;
    }
    public void click() {
      LeagueButtonTest.this.click_calls++;
    }
    public void release() {
      LeagueButtonTest.this.release_calls++;
    }
  }

  @Override
  public void instantiateButton() {
    this.button = Mockito.spy(new MockLeagueButton(p, 0, 0,
      0, 0, 0, 0, 0));
  }
}
// TODO: Add cases to ArcButtonTest that fail for LeagueButtonTest
// TODO: Add cases to existing tests to properly test league button