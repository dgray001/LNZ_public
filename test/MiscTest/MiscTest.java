package MiscTest;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import Misc.Misc;
// TODO: reverse order of all asserts
public class MiscTest {
  
  @Test
  public void testIsBoolean() {
    assertTrue(null, Misc.isBoolean("true"));
    assertTrue(null, Misc.isBoolean("false"));
    assertFalse(null, Misc.isBoolean("True"));
    assertFalse(null, Misc.isBoolean("some nonsense"));
    assertFalse(null, Misc.isBoolean(null));
  }
  @Test
  public void testToBoolean() {
    assertTrue(null, Misc.toBoolean("true"));
    assertFalse(null, Misc.toBoolean("false"));
    assertFalse(null, Misc.toBoolean("True"));
    assertFalse(null, Misc.toBoolean("some nonsense"));
    assertFalse(null, Misc.toBoolean(null));
  }
  
  @Test
  public void testIsInt() {
    assertTrue(null, Misc.isInt("1"));
    assertTrue(null, Misc.isInt("-12"));
    assertFalse(null, Misc.isInt("0.11"));
    assertFalse(null, Misc.isInt(null));
  }
  @Test
  public void testToInt() {
    assertEquals(1, Misc.toInt("1"));
    assertEquals(-11, Misc.toInt("-11"));
    assertEquals(-1, Misc.toInt("a1"));
    assertEquals(-1, Misc.toInt(null));
  }
  
  @Test
  public void testIsFloat() {
    assertTrue(null, Misc.isFloat("1"));
    assertTrue(null, Misc.isFloat("-12.0"));
    assertTrue(null, Misc.isFloat("0.11f"));
    assertFalse(null, Misc.isFloat(null));
  }
  @Test
  public void testToFloat() {
    assertEquals(1, Misc.toFloat("1"), 0.01);
    assertEquals(-12, Misc.toFloat("-12.0"), 0.01);
    assertEquals(0.11, Misc.toFloat("0.11f"), 0.01);
    assertEquals(-1, Misc.toFloat("nonsense"), 0.01);
    assertEquals(-1, Misc.toFloat(null), 0.01);
  }
  
  @Test
  public void testIsDouble() {
    assertTrue(null, Misc.isDouble("1"));
    assertTrue(null, Misc.isDouble("-12.0"));
    assertTrue(null, Misc.isDouble("0.11f"));
    assertFalse(null, Misc.isDouble(null));
  }
  @Test
  public void testToDouble() {
    assertEquals(1, Misc.toDouble("1"), 0.01);
    assertEquals(-12, Misc.toDouble("-12.0"), 0.01);
    assertEquals(0.11, Misc.toDouble("0.11f"), 0.01);
    assertEquals(-1, Misc.toDouble("nonsense"), 0.01);
    assertEquals(-1, Misc.toDouble(null), 0.01);
  }

  private class SampleData<T extends Number> {
    T[] data;
    T min;
    T max;
    double mean;
    double variance;
    SampleData(T[] data, T min, T max, double mean, double variance) {
      this.data = data;
      this.min = min;
      this.max = max;
      this.mean = mean;
      this.variance = variance;
    }
  }
  final List<SampleData<Integer>> intData = new ArrayList<SampleData<Integer>>(){{
    add(new SampleData<Integer>(null, 0, 0, 0, 0));
    add(new SampleData<Integer>(new Integer[]{}, 0, 0, 0, 0));
    add(new SampleData<Integer>(new Integer[]{0, 1, 2, 3},
      0, 3, 1.5, 1.667));
    add(new SampleData<Integer>(new Integer[]{-12, 4, 0, 1, -16},
      -16, 4, -4.6, 77.8));
    add(new SampleData<Integer>(new Integer[]{-111, 222, -110},
      -111, 222, 0.333, 36852.333));
  }};
  final List<SampleData<Double>> doubleData = new ArrayList<SampleData<Double>>(){{
    add(new SampleData<Double>(null, 0d, 0d, 0, 0));
    add(new SampleData<Double>(new Double[]{}, 0d, 0d, 0, 0));
    add(new SampleData<Double>(new Double[]{0d, 1d, 2d, 3d},
      0d, 3d, 1.5, 1.667));
    add(new SampleData<Double>(new Double[]{-12.2, 4.1, 0.03, 1d, -16.33},
      -16.33, 4.1, -4.68, 80.952));
    add(new SampleData<Double>(new Double[]{-111d, 222.666, -111d},
      -111d, 222.666, 0.222, 37111));
  }};

  @Test
  public void testMin() {
    for (SampleData<Integer> int_data : intData) {
      assertEquals(int_data.min, Misc.min(int_data.data), 0.01);
    }
    for (SampleData<Double> double_data : doubleData) {
      assertEquals(double_data.min, Misc.min(double_data.data), 0.01);
    }
  }

  @Test
  public void testMax() {
    for (SampleData<Integer> int_data : intData) {
      assertEquals(int_data.max, Misc.max(int_data.data), 0.01);
    }
    for (SampleData<Double> double_data : doubleData) {
      assertEquals(double_data.max, Misc.max(double_data.data), 0.01);
    }
  }

  @Test
  public void testMean() {
    for (SampleData<Integer> int_data : intData) {
      assertEquals(int_data.mean, Misc.mean(int_data.data), 0.01);
    }
    for (SampleData<Double> double_data : doubleData) {
      assertEquals(double_data.mean, Misc.mean(double_data.data), 0.01);
    }
  }

  @Test
  public void testVariance() {
    for (SampleData<Integer> int_data : intData) {
      assertEquals(int_data.variance, Misc.variance(int_data.data), 0.01);
    }
    for (SampleData<Double> double_data : doubleData) {
      assertEquals(double_data.variance, Misc.variance(double_data.data), 0.01);
    }
  }

  @Test
  public void testRandomInt() {
    Integer[] data = generateRandomInt(10);
    assertEquals(0, Misc.min(data), 0.01);
    assertEquals(10, Misc.max(data), 0.01);
    assertEquals(5, Misc.mean(data), 0.1);
    assertEquals(10, Misc.variance(data), 0.2);
    data = generateRandomInt(-12, -1);
    assertEquals(-12, Misc.min(data), 0.01);
    assertEquals(-1, Misc.max(data), 0.01);
    assertEquals(-6.5, Misc.mean(data), 0.12);
    assertEquals(11.9, Misc.variance(data), 0.3);
    data = generateRandomInt(-1, -12);
    assertEquals(-12, Misc.min(data), 0.01);
    assertEquals(-1, Misc.max(data), 0.01);
    assertEquals(-6.5, Misc.mean(data), 0.12);
    assertEquals(11.9, Misc.variance(data), 0.3);
  }
  private Integer[] generateRandomInt(int max) {
    return this.generateRandomInt(0, max);
  }
  private Integer[] generateRandomInt(int min, int max) {
    Integer[] ints = new Integer[10000];
    for (int i = 0; i < 10000; i++) {
      ints[i] = Misc.randomInt(min, max);
    }
    return ints;
  }

  @Test
  public void testRandomObjectRandomInt() {
    Random random_object = new Random((int)(Math.random() * Integer.MAX_VALUE));
    Integer[] data = generateRandomObjectRandomInt(random_object, 10);
    assertEquals(0, Misc.min(data), 0.01);
    assertEquals(10, Misc.max(data), 0.01);
    assertEquals(5, Misc.mean(data), 0.1);
    assertEquals(10, Misc.variance(data), 0.2);
    data = generateRandomObjectRandomInt(random_object, -12, -1);
    assertEquals(-12, Misc.min(data), 0.01);
    assertEquals(-1, Misc.max(data), 0.01);
    assertEquals(-6.5, Misc.mean(data), 0.12);
    assertEquals(11.9, Misc.variance(data), 0.3);
    data = generateRandomObjectRandomInt(random_object, -1, -12);
    assertEquals(-12, Misc.min(data), 0.01);
    assertEquals(-1, Misc.max(data), 0.01);
    assertEquals(-6.5, Misc.mean(data), 0.12);
    assertEquals(11.9, Misc.variance(data), 0.3);
  }
  private Integer[] generateRandomObjectRandomInt(Random random_object, int max) {
    return this.generateRandomObjectRandomInt(random_object, 0, max);
  }
  private Integer[] generateRandomObjectRandomInt(Random random_object, int min, int max) {
    Integer[] ints = new Integer[10000];
    for (int i = 0; i < 10000; i++) {
      ints[i] = Misc.randomObjectRandomInt(random_object, min, max);
    }
    return ints;
  }

  @Test
  public void testRandomDouble() {
    Double[] data = generateRandomDouble(10);
    assertEquals(0, Misc.min(data), 0.01);
    assertEquals(10, Misc.max(data), 0.01);
    assertEquals(5, Misc.mean(data), 0.1);
    assertEquals(8.33, Misc.variance(data), 0.2);
    data = generateRandomDouble(-12, -1);
    assertEquals(-12, Misc.min(data), 0.01);
    assertEquals(-1, Misc.max(data), 0.01);
    assertEquals(-6.5, Misc.mean(data), 0.1);
    assertEquals(10.08, Misc.variance(data), 0.2);
  }
  private Double[] generateRandomDouble(double max) {
    return this.generateRandomDouble(0, max);
  }
  private Double[] generateRandomDouble(double min, double max) {
    Double[] doubles = new Double[10000];
    for (int i = 0; i < 10000; i++) {
      doubles[i] = Misc.randomDouble(min, max);
    }
    return doubles;
  }

  @Test
  public void testRandomChance() {
    for (double chance = -0.05; chance < 1.1; chance += 0.087654) {
      Integer[] data = generateRandomChance(chance);
      double min = 0;
      if (chance >= 1) {
        min = 1;
      }
      double max = 1;
      if (chance <= 0) {
        max = 0;
      }
      double mean = Math.min(1, Math.max(0, chance));
      double variance = mean * Math.pow(1 - mean, 2) + (1 - mean) * Math.pow(-mean, 2);
      assertEquals(min, Misc.min(data), 0);
      assertEquals(max, Misc.max(data), 0);
      assertEquals(mean, Misc.mean(data), mean * 0.1);
      assertEquals(variance, Misc.variance(data), variance * 0.1);
    }
  }
  private Integer[] generateRandomChance(double chance) {
    Integer[] ints = new Integer[10000];
    for (int i = 0; i < 10000; i++) {
      ints[i] = Misc.randomChance(chance) ? 1 : 0;
    }
    return ints;
  }
  @Test
  public void testRandomObjectRandomChance() {
    for (double chance = -0.05; chance < 1.1; chance += 0.087654) {
      Random random_object = new Random((int)(chance * 12345));
      Integer[] data = generateRandomObjectRandomChance(random_object, chance);
      double min = 0;
      if (chance >= 1) {
        min = 1;
      }
      double max = 1;
      if (chance <= 0) {
        max = 0;
      }
      double mean = Math.min(1, Math.max(0, chance));
      double variance = mean * Math.pow(1 - mean, 2) + (1 - mean) * Math.pow(-mean, 2);
      assertEquals(min, Misc.min(data), 0);
      assertEquals(max, Misc.max(data), 0);
      assertEquals(mean, Misc.mean(data), mean * 0.1);
      assertEquals(variance, Misc.variance(data), variance * 0.1);
    }
  }
  private Integer[] generateRandomObjectRandomChance(Random random_object, double chance) {
    Integer[] ints = new Integer[10000];
    for (int i = 0; i < 10000; i++) {
      ints[i] = Misc.randomObjectRandomChance(random_object, chance) ? 1 : 0;
    }
    return ints;
  }

  @Test
  public void testModuloInt() {
    assertEquals(Misc.modulo(1, 0), 1);
    assertEquals(Misc.modulo(4, 4), 0);
    assertEquals(Misc.modulo(-4, -4), 0);
    assertEquals(Misc.modulo(4, -2), 0);
    assertEquals(Misc.modulo(-4, 2), 0);
    assertEquals(Misc.modulo(5, 2), 1);
    assertEquals(Misc.modulo(-5, 2), 1);
    assertEquals(Misc.modulo(-5, -2), -1);
    assertEquals(Misc.modulo(5, -2), -1);
    assertEquals(Misc.modulo(-2, -5), -2);
    assertEquals(Misc.modulo(-2, 5), 3);
  }

  @Test
  public void testModuloDouble() {
    assertEquals(Misc.modulo(1d, 0d), Double.NaN, 0.01);
    assertEquals(Misc.modulo(4d, 4d), 0, 0.01);
    assertEquals(Misc.modulo(-4d, -4d), 0, 0.01);
    assertEquals(Misc.modulo(4d, -2d), 0, 0.01);
    assertEquals(Misc.modulo(-4d, 2d), 0, 0.01);
    assertEquals(Misc.modulo(5d, 2.1d), 0.8, 0.01);
    assertEquals(Misc.modulo(2.1d, 5d), 2.1, 0.01);
    assertEquals(Misc.modulo(-5d, -2.1d), -0.8, 0.01);
    assertEquals(Misc.modulo(2.1d, -5d), -2.9, 0.01);
    assertEquals(Misc.modulo(-2.1d, 5d), 2.9, 0.01);
    assertEquals(Misc.modulo(-2.1d, -5d), -2.1, 0.01);
  }

  @Test
  public void testRound() {
    assertEquals(Misc.round(1.123456789, 0), 1, 0.000001);
    assertEquals(Misc.round(1.123456789, -1), 1, 0.000001);
    assertEquals(Misc.round(1.123456789, 1), 1.1, 0.000001);
    assertEquals(Misc.round(1.123456789, 2), 1.12, 0.000001);
    assertEquals(Misc.round(1.123456789, 3), 1.123, 0.000001);
    assertEquals(Misc.round(1.123456789, 4), 1.1235, 0.000001);
    assertEquals(Misc.round(1.123456789, 5), 1.12346, 0.000001);
    assertEquals(Misc.round(1.123456789, 6), 1.123457, 0.000001);
  }
}