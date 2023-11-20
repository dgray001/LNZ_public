package MiscTest;

import org.junit.*;
import static org.junit.Assert.*;
import Misc.BounceDouble;

public class BounceDoubleTest {
// TODO: reverse order of all asserts
  @Test
  public void testConstructorOne() {
    BounceDouble test = new BounceDouble(1);
    assertEquals(test.min(), 0, 0.01);
    assertEquals(test.max(), 1, 0.01);
    test = new BounceDouble(4);
    assertEquals(test.min(), 0, 0.01);
    assertEquals(test.max(), 4, 0.01);
    test = new BounceDouble(-3);
    assertEquals(test.min(), -3, 0.01);
    assertEquals(test.max(), 0, 0.01);
  }

  @Test
  public void testConstructorTwo() {
    BounceDouble test = new BounceDouble(2, 1);
    assertEquals(test.min(), 0, 0.01);
    assertEquals(test.max(), 2, 0.01);
    assertEquals(test.value(), 1, 0.01);
    test = new BounceDouble(4.1, 5.1);
    assertEquals(test.min(), 0, 0.01);
    assertEquals(test.max(), 4.1, 0.01);
    assertEquals(test.value(), 3.1, 0.01);
    test = new BounceDouble(-4.5, 0.5);
    assertEquals(test.min(), -4.5, 0.01);
    assertEquals(test.max(), 0, 0.01);
    assertEquals(test.value(), -0.5, 0.01);
    test = new BounceDouble(1, -0.9);
    assertEquals(test.min(), 0, 0.01);
    assertEquals(test.max(), 1, 0.01);
    assertEquals(test.value(), 0.9, 0.01);
  }

  @Test
  public void testConstructorThree() {
    BounceDouble test = new BounceDouble(2, 6, 3);
    assertEquals(test.min(), 2, 0.01);
    assertEquals(test.max(), 6, 0.01);
    assertEquals(test.value(), 3, 0.01);
    test = new BounceDouble(4, -1, 5);
    assertEquals(test.min(), -1, 0.01);
    assertEquals(test.max(), 4, 0.01);
    assertEquals(test.value(), 3, 0.01);
    test = new BounceDouble(-4.1, -2, -0.5);
    assertEquals(test.min(), -4.1, 0.01);
    assertEquals(test.max(), -2, 0.01);
    assertEquals(test.value(), -3.5, 0.01);
    test = new BounceDouble(1, -18, 163);
    assertEquals(test.min(), -18, 0.01);
    assertEquals(test.max(), 1, 0.01);
    assertEquals(test.value(), -9, 0.01);
  }

  @Test
  public void testAdd() {
    BounceDouble test = new BounceDouble(-2.5, 6.5, 3);
    assertEquals(test.min(), -2.5, 0.01);
    assertEquals(test.max(), 6.5, 0.01);
    assertEquals(test.value(), 3, 0.01);
    test.add(3);
    assertEquals(test.value(), 6, 0.01);
    test.add(2);
    assertEquals(test.value(), 5, 0.01);
    test.add(8);
    assertEquals(test.value(), -2, 0.01);
    test.add(24);
    assertEquals(test.value(), 4, 0.01);
    test.add(-3);
    assertEquals(test.value(), 1, 0.01);
    test.add(2);
    assertEquals(test.value(), 3, 0.01);
    test.add(-6);
    assertEquals(test.value(), -2, 0.01);
  }

  @Test
  public void testSet() {
    BounceDouble test = new BounceDouble(-2, 6, 3);
    assertEquals(test.min(), -2, 0.01);
    assertEquals(test.max(), 6, 0.01);
    assertEquals(test.value(), 3, 0.01);
    test.set(-1);
    assertEquals(test.value(), -1, 0.01);
    test.set(14.2);
    assertEquals(test.value(), -1.8, 0.01);
    test.set(-21.5);
    assertEquals(test.value(), 1.5, 0.01);
  }
}