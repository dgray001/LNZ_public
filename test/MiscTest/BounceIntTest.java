package MiscTest;

import org.junit.*;
import static org.junit.Assert.*;
import Misc.BounceInt;
// TODO: reverse order of all asserts
public class BounceIntTest {

  @Test
  public void testConstructorOne() {
    BounceInt test = new BounceInt(1);
    assertEquals(test.min(), 0);
    assertEquals(test.max(), 1);
    test = new BounceInt(4);
    assertEquals(test.min(), 0);
    assertEquals(test.max(), 4);
    test = new BounceInt(-3);
    assertEquals(test.min(), -3);
    assertEquals(test.max(), 0);
  }

  @Test
  public void testConstructorTwo() {
    BounceInt test = new BounceInt(2, 1);
    assertEquals(test.min(), 0);
    assertEquals(test.max(), 2);
    assertEquals(test.value(), 1);
    test = new BounceInt(4, 5);
    assertEquals(test.min(), 0);
    assertEquals(test.max(), 4);
    assertEquals(test.value(), 3);
    test = new BounceInt(-4, 1);
    assertEquals(test.min(), -4);
    assertEquals(test.max(), 0);
    assertEquals(test.value(), -1);
    test = new BounceInt(1, -1);
    assertEquals(test.min(), 0);
    assertEquals(test.max(), 1);
    assertEquals(test.value(), 1);
  }

  @Test
  public void testConstructorThree() {
    BounceInt test = new BounceInt(2, 6, 3);
    assertEquals(test.min(), 2);
    assertEquals(test.max(), 6);
    assertEquals(test.value(), 3);
    test = new BounceInt(4, -1, 5);
    assertEquals(test.min(), -1);
    assertEquals(test.max(), 4);
    assertEquals(test.value(), 3);
    test = new BounceInt(-4, -2, 0);
    assertEquals(test.min(), -4);
    assertEquals(test.max(), -2);
    assertEquals(test.value(), -4);
    test = new BounceInt(1, -18, 163);
    assertEquals(test.min(), -18);
    assertEquals(test.max(), 1);
    assertEquals(test.value(), -9);
  }

  @Test
  public void testAdd() {
    BounceInt test = new BounceInt(-2, 6, 3);
    assertEquals(test.min(), -2);
    assertEquals(test.max(), 6);
    assertEquals(test.value(), 3);
    test.add(3);
    assertEquals(test.value(), 6);
    test.add(2);
    assertEquals(test.value(), 4);
    test.add(8);
    assertEquals(test.value(), 0);
    test.add(24);
    assertEquals(test.value(), 4);
    test.add(-3);
    assertEquals(test.value(), 5);
    test.add(2);
    assertEquals(test.value(), 5);
    test.add(-5);
    assertEquals(test.value(), 2);
  }

  @Test
  public void testSet() {
    BounceInt test = new BounceInt(-2, 6, 3);
    assertEquals(test.min(), -2);
    assertEquals(test.max(), 6);
    assertEquals(test.value(), 3);
    test.set(-1);
    assertEquals(test.value(), -1);
    test.set(15);
    assertEquals(test.value(), -1);
    test.set(-21);
    assertEquals(test.value(), 1);
  }
}