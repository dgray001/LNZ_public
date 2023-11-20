package Misc;

import java.util.*;

public class Misc {
  public static boolean isBoolean(String str) {
    if (str == null) {
      return false;
    }
    if (str.equals(Boolean.toString(true)) || str.equals(Boolean.toString(false))) {
      return true;
    }
    else {
      return false;
    }
  }
  public static boolean toBoolean(String str) {
    if (str == null) {
      return false;
    }
    if (str.equals(Boolean.toString(true))) {
      return true;
    }
    else {
      return false;
    }
  }

  public static boolean isInt(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch(NumberFormatException e) {
      return false;
    }
  }
  public static int toInt(String str) {
    int i = -1;
    try {
      i = Integer.parseInt(str);
    } catch(NumberFormatException e) {}
    return i;
  }

  public static boolean isFloat(String str) {
    if (str == null) {
      return false;
    }
    try {
      Float.parseFloat(str);
      return true;
    } catch(NumberFormatException e) {
      return false;
    }
  }
  public static float toFloat(String str) {
    float f = -1;
    if (str == null) {
      return f;
    }
    try {
      f = Float.parseFloat(str);
    } catch(NumberFormatException e) {}
    return f;
  }

  public static boolean isDouble(String str) {
    if (str == null) {
      return false;
    }
    try {
      Double.parseDouble(str);
      return true;
    } catch(NumberFormatException e) {
      return false;
    }
  }
  public static double toDouble(String str) {
    double d = -1;
    if (str == null) {
      return d;
    }
    try {
      d = Double.parseDouble(str);
    } catch(NumberFormatException e) {}
    return d;
  }

  public static double min(Integer[] nums) {
    if (nums == null || nums.length == 0) {
      return 0;
    }
    double min = Double.MAX_VALUE;
    for (int i : nums) {
      if (i < min) {
        min = i;
      }
    }
    return min;
  }
  public static double min(Double[] nums) {
    if (nums == null || nums.length == 0) {
      return 0;
    }
    double min = Double.MAX_VALUE;
    for (double i : nums) {
      if (i < min) {
        min = i;
      }
    }
    return min;
  }

  public static int max(Integer[] nums) {
    if (nums == null || nums.length == 0) {
      return 0;
    }
    int max = Integer.MIN_VALUE;
    for (int i : nums) {
      if (i > max) {
        max = i;
      }
    }
    return max;
  }
  public static double max(Double[] nums) {
    if (nums == null || nums.length == 0) {
      return 0;
    }
    double max = Double.NEGATIVE_INFINITY;
    for (double i : nums) {
      if (i > max) {
        max = i;
      }
    }
    return max;
  }

  public static double mean(Integer[] nums) {
    if (nums == null || nums.length == 0) {
      return 0;
    }
    double sum = 0;
    for (int i : nums) {
      sum += i;
    }
    return sum / nums.length;
  }
  public static double mean(Double[] nums) {
    if (nums == null || nums.length == 0) {
      return 0;
    }
    double sum = 0;
    for (double i : nums) {
      sum += i;
    }
    return sum / nums.length;
  }

  public static double variance(Integer[] nums) {
    if (nums == null || nums.length < 2) {
      return 0;
    }
    double mean = mean(nums);
    double sum = 0;
    for (double i : nums) {
      sum += (mean - i) * (mean - i);
    }
    return sum / (nums.length - 1);
  }
  public static double variance(Double[] nums) {
    if (nums == null || nums.length < 2) {
      return 0;
    }
    double mean = mean(nums);
    double sum = 0;
    for (double i : nums) {
      sum += (mean - i) * (mean - i);
    }
    return sum / (nums.length - 1);
  }

  public static int randomInt(int max) {
    return randomInt(0, max);
  }
  public static int randomInt(int min, int max) {
    if (max < min) {
      return randomInt(max, min);
    }
    return min + (int)(Math.random() * (1 + max - min));
  }
  public static int randomObjectRandomInt(Random random_object, int min, int max) {
    if (max < min) {
      return randomObjectRandomInt(random_object, max, min);
    }
    return min + (int)(random_object.nextDouble() * (1 + max - min));
  }

  public static double randomDouble(double max) {
    return randomDouble(0, max);
  }
  public static double randomDouble(double min, double max) {
    if (max < min) {
      return randomDouble(max, min);
    }
    return min + Math.random() * (max - min);
  }

  public static boolean randomChance(double percent) {
    if (Math.random() < percent) {
      return true;
    }
    return false;
  }

  public static boolean randomObjectRandomChance(Random random_object, double percent) {
    if (random_object.nextDouble() < percent) {
      return true;
    }
    return false;
  }
  
  public static int modulo(int a, int b) {
    if (b == 0) {
      return a;
    }
    return Math.floorMod(a, b);
  }
  public static double modulo(double a, double b) {
    return (a % b + b) % b;
  }

  public static double round(double num, int places) {
    if (places < 0) {
      places = 0;
    }
    double multiplier = Math.pow(10, places);
    return Math.round(num * multiplier) / multiplier;
  }

  // TODO: make test
  public static double map(double num, double min, double max, double map_min, double map_max) {
    if (max < min) {
      return map(num, max, min, map_min, map_max);
    }
    if (map_max < map_min) {
      return map(num, min, max, map_max, map_min);
    }
    double relative = num;
    if (max == min) {
      if (max != 0) {
        relative = num / max;
      }
    }
    relative = (num - min) / (max - min);
    return map_min + relative * (map_max - map_min);
  }
}