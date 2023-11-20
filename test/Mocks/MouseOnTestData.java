package Mocks;

public class MouseOnTestData {
  public float x;
  public float y;
  public boolean expected;
  public MouseOnTestData(float x, float y, boolean expected) {
    this.x = x;
    this.y = y;
    this.expected = expected;
  }
  @Override
  public String toString() {
    return this.x + ", " + this.y + ", " + this.expected;
  }
}