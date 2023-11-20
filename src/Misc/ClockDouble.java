package Misc;

public class ClockDouble {
  protected double min;
  protected double max;
  protected double value;

  public ClockDouble(double max) {
    this(0, max, Misc.randomDouble(max));
  }
  public ClockDouble(double max, double start) {
    this(0, max, start);
  }
  public ClockDouble(double min, double max, double start) {
    if (min > max) {
      this.min = max;
      this.max = min;
    }
    else {
      this.min = min;
      this.max = max;
    }
    this.value = start;
    this.resolve();
  }

  public double min() {
    return this.min;
  }

  public double max() {
    return this.max;
  }

  public double value() {
    return this.value;
  }

  private void resolve() {
    if (this.min == this.max) {
      this.value = this.min;
    }
    else {
      this.value = this.min + Misc.modulo((this.value - this.min), (this.max - this.min));
    }
  }

  public void add(double amount) {
    this.value += amount;
    this.resolve();
  }

  public void set(double amount) {
    this.value = amount;
    this.resolve();
  }
}