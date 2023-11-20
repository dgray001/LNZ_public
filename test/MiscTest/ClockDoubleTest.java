package MiscTest;

import org.junit.*;
import static org.junit.Assert.*;
import Misc.ClockDouble;
// TODO: reverse order of all asserts
public class ClockDoubleTest {

  @Test
  public void testConstructorOne() {
    ClockDouble test = new ClockDouble(1);
    assertEquals(test.min(), 0, 0.01);
    assertEquals(test.max(), 1, 0.01);
    test = new ClockDouble(4);
    assertEquals(test.min(), 0, 0.01);
    assertEquals(test.max(), 4, 0.01);
    test = new ClockDouble(-3);
    assertEquals(test.min(), -3, 0.01);
    assertEquals(test.max(), 0, 0.01);
  }

  @Test
  public void testConstructorTwo() {
    ClockDouble test = new ClockDouble(2, 1);
    assertEquals(test.min(), 0, 0.01);
    assertEquals(test.max(), 2, 0.01);
    assertEquals(test.value(), 1, 0.01);
    test = new ClockDouble(4, 5);
    assertEquals(test.min(), 0, 0.01);
    assertEquals(test.max(), 4, 0.01);
    assertEquals(test.value(), 1, 0.01);
    test = new ClockDouble(-4, 1);
    assertEquals(test.min(), -4, 0.01);
    assertEquals(test.max(), 0, 0.01);
    assertEquals(test.value(), -3, 0.01);
    test = new ClockDouble(1, -1);
    assertEquals(test.min(), 0, 0.01);
    assertEquals(test.max(), 1, 0.01);
    assertEquals(test.value(), 0, 0.01);
  }

  @Test
  public void testConstructorThree() {
    ClockDouble test = new ClockDouble(2, 6, 3);
    assertEquals(test.min(), 2, 0.01);
    assertEquals(test.max(), 6, 0.01);
    assertEquals(test.value(), 3, 0.01);
    test = new ClockDouble(4, -1, 5);
    assertEquals(test.min(), -1, 0.01);
    assertEquals(test.max(), 4, 0.01);
    assertEquals(test.value(), 0, 0.01);
    test = new ClockDouble(-4, -2, 0);
    assertEquals(test.min(), -4, 0.01);
    assertEquals(test.max(), -2, 0.01);
    assertEquals(test.value(), -4, 0.01);
    test = new ClockDouble(1, -18, 163);
    assertEquals(test.min(), -18, 0.01);
    assertEquals(test.max(), 1, 0.01);
    assertEquals(test.value(), -8, 0.01);
  }

  @Test
  public void testAdd() {
    ClockDouble test = new ClockDouble(-2, 6, 3);
    assertEquals(test.min(), -2, 0.01);
    assertEquals(test.max(), 6, 0.01);
    assertEquals(test.value(), 3, 0.01);
    test.add(3);
    assertEquals(test.value(), -2, 0.01);
    test.add(2);
    assertEquals(test.value(), 0, 0.01);
    test.add(8);
    assertEquals(test.value(), 0, 0.01);
    test.add(24);
    assertEquals(test.value(), 0, 0.01);
    test.add(-3);
    assertEquals(test.value(), 5, 0.01);
    test.add(-6);
    assertEquals(test.value(), -1, 0.01);
  }

  @Test
  public void testSet() {
    ClockDouble test = new ClockDouble(-2, 6, 3);
    assertEquals(test.min(), -2, 0.01);
    assertEquals(test.max(), 6, 0.01);
    assertEquals(test.value(), 3, 0.01);
    test.set(-1);
    assertEquals(test.value(), -1, 0.01);
    test.set(14);
    assertEquals(test.value(), -2, 0.01);
    test.set(-21);
    assertEquals(test.value(), 3, 0.01);
  }
}