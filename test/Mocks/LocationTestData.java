package Mocks;

public class LocationTestData {
  public double x_center;
  public double y_center;
  public double button_width;
  public double button_height;
  public LocationTestData(double x_center, double y_center, double button_width, double button_height) {
    this.x_center = x_center;
    this.y_center = y_center;
    this.button_width = button_width;
    this.button_height = button_height;
  }
  @Override
  public String toString() {
    return this.x_center + " " + this.y_center + " " + this.button_width +
      " " + this.button_height;
  }
}
