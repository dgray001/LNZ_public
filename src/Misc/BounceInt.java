package Misc;

public class BounceInt {
  protected int min;
  protected int max;
  protected int value;
  protected boolean moving_forward = true;

  public BounceInt(int max) {
    this(0, max, (int)max);
  }
  public BounceInt(int max, int start) {
    this(0, max, start);
  }
  public BounceInt(int min, int max, int start) {
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

  public void add(int amount) {
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

  public void set(int amount) {
    if ((amount < this.min && this.moving_forward) ||
      (amount > this.max && !this.moving_forward)) {
      this.moving_forward = !this.moving_forward;
    }
    this.value = amount;
    this.resolve();
  }
}