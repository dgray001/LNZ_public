package LNZModuleTest;

import static org.junit.Assert.*;

import org.junit.*;
import java.util.*;
import LNZModule.*;

public class FeatureTest {
  @Test
  public void testDrawGridLocations() {
    Iterator<Map.Entry<IntegerCoordinate, List<FeatureDrawGridPiece>>>
      data_iterator = this.getTestData().entrySet().iterator();
    while(data_iterator.hasNext()) {
      Map.Entry<IntegerCoordinate, List<FeatureDrawGridPiece>>
        entry = data_iterator.next();
      List<FeatureDrawGridPiece> expected = entry.getValue();
      List<FeatureDrawGridPiece> calculated = Feature.drawGridLocations(
        new HashMap<IntegerCoordinate, List<FeatureDrawGridPiece>>(), entry.getKey());
      System.out.println(expected.toString() + "\n" +  calculated.toString());
      assertArrayEquals(expected.toArray(), calculated.toArray());
    }
  }
  Map<IntegerCoordinate, List<FeatureDrawGridPiece>> getTestData() {
    Map<IntegerCoordinate, List<FeatureDrawGridPiece>> data =
      new HashMap<IntegerCoordinate, List<FeatureDrawGridPiece>>();
    // 1x1
    IntegerCoordinate size = new IntegerCoordinate(1, 1);
    List<FeatureDrawGridPiece> pieces = new ArrayList<FeatureDrawGridPiece>();
    pieces.add(new FeatureDrawGridPiece(new IntegerCoordinate(0, 0)));
    data.put(size, pieces);
    // 2x1
    size = new IntegerCoordinate(2, 1);
    pieces = new ArrayList<FeatureDrawGridPiece>();
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(1, 0), new IntegerCoordinate(0, 0),
      true, false));
    data.put(size, pieces);
    // 1x2
    size = new IntegerCoordinate(1, 2);
    pieces = new ArrayList<FeatureDrawGridPiece>();
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(-1, 1), new IntegerCoordinate(0, 0),
      false, true));
    data.put(size, pieces);
    // 3x1
    size = new IntegerCoordinate(3, 1);
    pieces = new ArrayList<FeatureDrawGridPiece>();
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(1, 0), new IntegerCoordinate(0, 0),
      true, false));
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(2, 0)));
    data.put(size, pieces);
    // 1x3
    size = new IntegerCoordinate(1, 3);
    pieces = new ArrayList<FeatureDrawGridPiece>();
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(-1, 1), new IntegerCoordinate(0, 0),
      false, true));
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(0, 2)));
    data.put(size, pieces);
    // 3x2
    size = new IntegerCoordinate(3, 2);
    pieces = new ArrayList<FeatureDrawGridPiece>();
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(1, 0), new IntegerCoordinate(0, 0),
      true, false));
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(1, 1), new IntegerCoordinate(2, 0),
      false, true));
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(1, 1), new IntegerCoordinate(0, 1),
      true, false));
    data.put(size, pieces);
    // 2x3
    size = new IntegerCoordinate(2, 3);
    pieces = new ArrayList<FeatureDrawGridPiece>();
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(1, 0), new IntegerCoordinate(0, 0),
      true, false));
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(1, 1), new IntegerCoordinate(0, 1),
      true, false));
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(1, 2), new IntegerCoordinate(0, 2),
      true, false));
    data.put(size, pieces);
    // 3x3
    size = new IntegerCoordinate(3, 3);
    pieces = new ArrayList<FeatureDrawGridPiece>();
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(1, 0), new IntegerCoordinate(0, 0),
      true, false));
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(1, 1), new IntegerCoordinate(2, 0),
      false, true));
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(1, 1), new IntegerCoordinate(0, 1),
      true, false));
    pieces.add(new FeatureDrawGridPiece(
      new IntegerCoordinate(1, 2), new IntegerCoordinate(0, 2),
      true, false));
    pieces.add(new FeatureDrawGridPiece(new IntegerCoordinate(2, 2)));
    data.put(size, pieces);
    return data;
  }
}
