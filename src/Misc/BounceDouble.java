package Misc;

public class BounceDouble {
  protected double min;
  protected double max;
  protected double value;
  protected boolean moving_forward = true;

  public BounceDouble(double max) {
    this(0, max, Misc.randomDouble(max));
  }
  public BounceDouble(double max, double start) {
    this(0, max, start);
  }
  public BounceDouble(double min, double max, double start) {
    if (min > max) {
      this.min = max;
      this.max = min;
    }
    else {
      this.min = min;
      this.max = max;
    }
    this.value = start;
    if (this.value < this.min) {
      this.moving_forward = false;
    }
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
    while(true) {
      if (this.moving_forward) {
        if (this.value > this.max) {
          this.moving_forward = false;
          this.value = this.max + this.max - this.value;
          continue;
        }
        else {
          break;
        }
      }
      else {
        if (this.value < this.min) {
          this.moving_forward = true;
          this.value = this.min + this.min - this.value;
          continue;
        }
        else {
          break;
        }
      }
    }
  }

  public void add(double amount) {
    boolean flipped = false;
    if (amount < 0) {
      this.moving_forward = !this.moving_forward;
      amount = -amount;
      flipped = true;
    }
    if (this.moving_forward) {
      this.value += amount;
    }
    else {
      this.value -= amount;
    }
    this.resolve();
    if (flipped) {
      this.moving_forward = !this.moving_forward;
    }
  }

  public void set(double amount) {
    if ((amount < this.min && this.moving_forward) ||
      (amount > this.max && !this.moving_forward)) {
      this.moving_forward = !this.moving_forward;
    }
    this.value = amount;
    this.resolve();
  }
}
