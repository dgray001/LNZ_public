package Misc;

public class ClockInt {
  protected int min;
  protected int max;
  protected int value;

  public ClockInt(int max) {
    this(0, max, Misc.randomInt(max));
  }
  public ClockInt(int max, int start) {
    this(0, max, start);
  }
  public ClockInt(int min, int max, int start) {
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

  public int min() {
    return this.min;
  }

  public int max() {
    return this.max;
  }

  public int value() {
    return this.value;
  }

  private void resolve() {
    // note max >= min so max - min + 1 > 0
    this.value = this.min + Misc.modulo((this.value - this.min), (this.max - this.min + 1));
  }

  public void add(int amount) {
    this.value += amount;
    this.resolve();
  }

  public void set(int amount) {
    this.value = amount;
    this.resolve();
  }
}