package MiscTest;

import org.junit.*;
import static org.junit.Assert.*;
import Misc.ClockInt;
// TODO: reverse order of all asserts
public class ClockIntTest {

  @Test
  public void testConstructorOne() {
    ClockInt test = new ClockInt(1);
    assertEquals(test.min(), 0);
    assertEquals(test.max(), 1);
    test = new ClockInt(4);
    assertEquals(test.min(), 0);
    assertEquals(test.max(), 4);
    test = new ClockInt(-3);
    assertEquals(test.min(), -3);
    assertEquals(test.max(), 0);
  }

  @Test
  public void testConstructorTwo() {
    ClockInt test = new ClockInt(2, 1);
    assertEquals(test.min(), 0);
    assertEquals(test.max(), 2);
    assertEquals(test.value(), 1);
    test = new ClockInt(4, 5);
    assertEquals(test.min(), 0);
    assertEquals(test.max(), 4);
    assertEquals(test.value(), 0);
    test = new ClockInt(-4, 1);
    assertEquals(test.min(), -4);
    assertEquals(test.max(), 0);
    assertEquals(test.value(), -4);
    test = new ClockInt(1, -1);
    assertEquals(test.min(), 0);
    assertEquals(test.max(), 1);
    assertEquals(test.value(), 1);
  }

  @Test
  public void testConstructorThree() {
    ClockInt test = new ClockInt(2, 6, 3);
    assertEquals(test.min(), 2);
    assertEquals(test.max(), 6);
    assertEquals(test.value(), 3);
    test = new ClockInt(4, -1, 5);
    assertEquals(test.min(), -1);
    assertEquals(test.max(), 4);
    assertEquals(test.value(), -1);
    test = new ClockInt(-4, -2, 0);
    assertEquals(test.min(), -4);
    assertEquals(test.max(), -2);
    assertEquals(test.value(), -3);
    test = new ClockInt(1, -18, 163);
    assertEquals(test.min(), -18);
    assertEquals(test.max(), 1);
    assertEquals(test.value(), -17);
  }

  @Test
  public void testAdd() {
    ClockInt test = new ClockInt(-2, 6, 3);
    assertEquals(test.min(), -2);
    assertEquals(test.max(), 6);
    assertEquals(test.value(), 3);
    test.add(3);
    assertEquals(test.value(), 6);
    test.add(2);
    assertEquals(test.value(), -1);
    test.add(8);
    assertEquals(test.value(), -2);
    test.add(24);
    assertEquals(test.value(), 4);
    test.add(-3);
    assertEquals(test.value(), 1);
    test.add(-6);
    assertEquals(test.value(), 4);
  }

  @Test
  public void testSet() {
    ClockInt test = new ClockInt(-2, 6, 3);
    assertEquals(test.min(), -2);
    assertEquals(test.max(), 6);
    assertEquals(test.value(), 3);
    test.set(-1);
    assertEquals(test.value(), -1);
    test.set(14);
    assertEquals(test.value(), 5);
    test.set(-21);
    assertEquals(test.value(), 6);
  }
}