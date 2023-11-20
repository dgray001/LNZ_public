package LNZModule;

import java.util.*;
import Misc.Misc;

enum AreaLocation {
  NONE, FERNWOOD_FOREST;

  private static final List<AreaLocation> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  public String displayName() {
    return AreaLocation.displayName(this);
  }
  public static String displayName(AreaLocation area_location) {
    switch(area_location) {
      case FERNWOOD_FOREST:
        return "Fernwood State Forest";
      default:
        return "Error";
    }
  }

  public String fileName() {
    return AreaLocation.fileName(this);
  }
  public static String fileName(AreaLocation area_location) {
    switch(area_location) {
      case FERNWOOD_FOREST:
        return "FERNWOOD_FOREST";
      default:
        return "NONE";
    }
  }

  public static AreaLocation areaLocation(String area_location_name) {
    for (AreaLocation area_location : AreaLocation.VALUES) {
      if (area_location == AreaLocation.NONE) {
        continue;
      }
      if (AreaLocation.displayName(area_location).equals(area_location_name) ||
        AreaLocation.fileName(area_location).equals(area_location_name)) {
        return area_location;
      }
    }
    return AreaLocation.NONE;
  }

  static Biome getBiomeFromPerlinNoise(AreaLocation location, IntegerCoordinate coordinate, float noise_value, int seed) {
    switch(location) {
      case FERNWOOD_FOREST:
        return fernwoodForestBiome(coordinate, noise_value, seed);
      default:
        return Biome.NONE;
    }
  }
  
  private static Biome fernwoodForestBiome(IntegerCoordinate coordinate, float noise_value, int seed) {
    if (coordinate.x == 519 && coordinate.y == 7) {
      return Biome.FERNWOOD_START;
    }
    Random random_object = new Random(seed + coordinate.hashCode());
    random_object.nextDouble();
    random_object.nextDouble(); // throw away a couple values since first is always near same
    if (Misc.randomObjectRandomChance(random_object, 0.05)) {
      return Biome.FERNWOOD_POND;
    }
    if (noise_value > 0.7) {
      if (Misc.randomObjectRandomChance(random_object, 0.05)) {
        return Biome.FERNWOOD_CAMPGROUND_FOREST3;
      }
      return Biome.MAPLE_FOREST3;
    }
    else if (noise_value > 0.45) {
      if (Misc.randomObjectRandomChance(random_object, 0.05)) {
        return Biome.FERNWOOD_CAMPGROUND_FOREST2;
      }
      return Biome.MAPLE_FOREST2;
    }
    else if (noise_value > 0.33) {
      if (Misc.randomObjectRandomChance(random_object, 0.05)) {
        return Biome.FERNWOOD_CAMPGROUND_FOREST1;
      }
      return Biome.MAPLE_FOREST1;
    }
    else {
      if (Misc.randomObjectRandomChance(random_object, 0.05)) {
        return Biome.FERNWOOD_CAMPGROUND_CLEARING;
      }
      return Biome.CLEARING;
    }
  }
}


class BiomeReturn {
  protected int terrain_code = 0;
  protected ArrayList<BiomeReturnFeature> features = new ArrayList<BiomeReturnFeature>();
  BiomeReturn() {}
  void addFeature(int id) {
    this.features.add(new BiomeReturnFeature(id));
  }
}

class BiomeReturnFeature {
  protected int feature_id = 0;
  protected boolean check_overlap = true;
  protected int x_adjustment = 0;
  protected int y_adjustment = 0;
  protected boolean specify_feature_toggle = false;
  protected boolean feature_toggle = false;
  protected boolean specify_feature_number = false;
  protected int feature_number = 0;
  BiomeReturnFeature() {}
  BiomeReturnFeature(int id) {
    this.feature_id = id;
  }
}


enum Biome {
  NONE,

  MAPLE_FOREST1, MAPLE_FOREST2, MAPLE_FOREST3, CLEARING, FERNWOOD_START,
  FERNWOOD_POND, FERNWOOD_CAMPGROUND_FOREST1, FERNWOOD_CAMPGROUND_FOREST2,
  FERNWOOD_CAMPGROUND_FOREST3, FERNWOOD_CAMPGROUND_CLEARING, MAIN_ROAD1,
  MAIN_ROAD2, MAIN_ROAD3, MAIN_ROAD4, MAIN_ROAD5, CROSS_CREEK1, CROSS_CREEK2,
  CROSS_CREEK3, CROSS_CREEK4, CROSS_CREEK5,

  GRASS;

  private static final List<Biome> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  public String displayName() {
    return Biome.displayName(this);
  }
  public static String displayName(Biome biome) {
    switch(biome) {
      case MAPLE_FOREST1:
        return "Maple Forest, open";
      case MAPLE_FOREST2:
        return "Maple Forest";
      case MAPLE_FOREST3:
        return "Maple Forest, dense";
      case CLEARING:
        return "Clearing";
      case FERNWOOD_START:
        return "Fernwood State Forest entrance";
      case FERNWOOD_POND:
        return "Pond";
      case FERNWOOD_CAMPGROUND_FOREST1:
      case FERNWOOD_CAMPGROUND_FOREST2:
      case FERNWOOD_CAMPGROUND_FOREST3:
      case FERNWOOD_CAMPGROUND_CLEARING:
        return "Campground";
      case GRASS:
        return "GRASS";
      default:
        return "Error";
    }
  }

  public String fileName() {
    return Biome.fileName(this);
  }
  public static String fileName(Biome biome) {
    switch(biome) {
      case MAPLE_FOREST1:
        return "MAPLE_FOREST1";
      case MAPLE_FOREST2:
        return "MAPLE_FOREST2";
      case MAPLE_FOREST3:
        return "MAPLE_FOREST3";
      case CLEARING:
        return "CLEARING";
      case FERNWOOD_START:
        return "FERNWOOD_START";
      case FERNWOOD_POND:
        return "FERNWOOD_POND";
      case FERNWOOD_CAMPGROUND_FOREST1:
        return "FERNWOOD_CAMPGROUND_FOREST1";
      case FERNWOOD_CAMPGROUND_FOREST2:
        return "FERNWOOD_CAMPGROUND_FOREST2";
      case FERNWOOD_CAMPGROUND_FOREST3:
        return "FERNWOOD_CAMPGROUND_FOREST3";
      case FERNWOOD_CAMPGROUND_CLEARING:
        return "FERNWOOD_CAMPGROUND_CLEARING";
      case GRASS:
        return "GRASS";
      default:
        return "";
    }
  }

  public static Biome biome(String biome_name) {
    for (Biome biome : Biome.VALUES) {
      if (biome == Biome.NONE) {
        continue;
      }
      if (Biome.displayName(biome).equals(biome_name) ||
        Biome.fileName(biome).equals(biome_name)) {
        return biome;
      }
    }
    return Biome.NONE;
  }

  public boolean allowZombieSpawn() {
    return Biome.allowZombieSpawn(this);
  }
  public static boolean allowZombieSpawn(Biome biome) {
    if (biome == null) {
      return false;
    }
    switch(biome) {
      case MAPLE_FOREST1:
      case MAPLE_FOREST2:
      case MAPLE_FOREST3:
      case CLEARING:
      case FERNWOOD_POND:
      case FERNWOOD_CAMPGROUND_FOREST1:
      case FERNWOOD_CAMPGROUND_FOREST2:
      case FERNWOOD_CAMPGROUND_FOREST3:
      case FERNWOOD_CAMPGROUND_CLEARING:
      case GRASS:
        return true;
      case FERNWOOD_START:
      default:
        return false;
    }
  }

  int scytheGrassItemId() {
    int item_id = 0;
    double random_number = Math.random();
    switch(this) {
      case MAPLE_FOREST1:
      case MAPLE_FOREST2:
      case MAPLE_FOREST3:
      case CLEARING:
      case FERNWOOD_START:
      case FERNWOOD_CAMPGROUND_FOREST1:
      case FERNWOOD_CAMPGROUND_FOREST2:
      case FERNWOOD_CAMPGROUND_FOREST3:
      case FERNWOOD_CAMPGROUND_CLEARING:
      case MAIN_ROAD1:
      case MAIN_ROAD2:
      case MAIN_ROAD3:
      case MAIN_ROAD4:
      case MAIN_ROAD5:
      default:
        if (random_number < 0.25) {
          item_id = 2007;
        }
        else if (random_number < 0.5) {
          item_id = 2008;
        }
        else if (random_number < 0.65) {
          item_id = 2005;
        }
        else if (random_number < 0.8) {
          item_id = 2006;
        }
        else if (random_number < 0.85) {
          item_id = 2003;
        }
        else if (random_number < 0.9) {
          item_id = 2004;
        }
        else if (random_number < 0.93) {
          item_id = 2001;
        }
        else if (random_number < 0.96) {
          item_id = 2002;
        }
        else if (random_number < 0.98) {
          item_id = 2009;
        }
        else {
          item_id = 2010;
        }
        break;
      case FERNWOOD_POND:
      case CROSS_CREEK1:
      case CROSS_CREEK2:
      case CROSS_CREEK3:
      case CROSS_CREEK4:
      case CROSS_CREEK5:
        if (random_number < 0.2) {
          item_id = 2007;
        }
        else if (random_number < 0.4) {
          item_id = 2008;
        }
        else if (random_number < 0.5) {
          item_id = 2005;
        }
        else if (random_number < 0.6) {
          item_id = 2006;
        }
        else if (random_number < 0.65) {
          item_id = 2003;
        }
        else if (random_number < 0.7) {
          item_id = 2004;
        }
        else if (random_number < 0.83) {
          item_id = 2001;
        }
        else if (random_number < 0.96) {
          item_id = 2002;
        }
        else if (random_number < 0.98) {
          item_id = 2009;
        }
        else {
          item_id = 2010;
        }
        break;
    }
    return item_id;
  }


  static BiomeReturn processPerlinNoise(Biome biome, float noise_value, Random random_object, IntegerCoordinate coordinate) {
    switch(biome) {
      case MAPLE_FOREST1:
        return mapleForestProcessPerlinNoise(1, noise_value, random_object);
      case MAPLE_FOREST2:
        return mapleForestProcessPerlinNoise(2, noise_value, random_object);
      case MAPLE_FOREST3:
        return mapleForestProcessPerlinNoise(3, noise_value, random_object);
      case CLEARING:
        return clearingProcessPerlinNoise(noise_value, random_object);
      case FERNWOOD_START:
        return fernwoodStartProcessPerlinNoise(noise_value, random_object, coordinate);
      case FERNWOOD_POND:
        return fernwoodPondProcessPerlinNoise(noise_value, random_object, coordinate);
      case FERNWOOD_CAMPGROUND_FOREST1:
        return fernwoodCampgroundProcessPerlinNoise(1, noise_value, random_object, coordinate);
      case FERNWOOD_CAMPGROUND_FOREST2:
        return fernwoodCampgroundProcessPerlinNoise(2, noise_value, random_object, coordinate);
      case FERNWOOD_CAMPGROUND_FOREST3:
        return fernwoodCampgroundProcessPerlinNoise(3, noise_value, random_object, coordinate);
      case FERNWOOD_CAMPGROUND_CLEARING:
        return fernwoodCampgroundProcessPerlinNoise(0, noise_value, random_object, coordinate);
      case GRASS:
        return grassProcessPerlinNoise(noise_value, random_object);
      default:
        break;
    }
    return new BiomeReturn();
  }

  private static BiomeReturn mapleForestProcessPerlinNoise(int forest_density, float noise_value, Random random_object) {
    BiomeReturn biome_return = new BiomeReturn();
    switch(forest_density) {
      case 1: // low density forest
        if (noise_value > 0.85) { // dark dirt
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.05)) {
            biome_return.terrain_code = 163; // dark dirt
          }
          else {
            biome_return.terrain_code = 162; // gray dirt
          }
        }
        else if (noise_value > 0.7) { // dirty
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.2)) {
            biome_return.terrain_code = 162; // gray dirt
          }
          else {
            biome_return.terrain_code = 161; // light dirt
          }
        }
        else if (noise_value > 0.55) { // light dirt
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.3)) {
            biome_return.terrain_code = 161; // light dirt
          }
          else {
            biome_return.terrain_code = 154; // dead grass
          }
        }
        else { // grassy
          if (Misc.randomObjectRandomChance(random_object, 2 * noise_value)) {
            biome_return.terrain_code = 154; // dead grass
          }
          else {
            biome_return.terrain_code = 153; // dark grass
          }
        }
        break;
      case 2: // medium density forest
        if (noise_value > 0.78) { // dark dirt
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.15)) {
            biome_return.terrain_code = 163; // dark dirt
          }
          else {
            biome_return.terrain_code = 162; // gray dirt
          }
        }
        else if (noise_value > 0.6) { // dirty
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.3)) {
            biome_return.terrain_code = 162; // gray dirt
          }
          else {
            biome_return.terrain_code = 161; // light dirt
          }
        }
        else if (noise_value > 0.45) { // light dirt
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.4)) {
            biome_return.terrain_code = 161; // light dirt
          }
          else {
            biome_return.terrain_code = 154; // dead grass
          }
        }
        else { // grassy
          if (Misc.randomObjectRandomChance(random_object, Misc.map(noise_value,
            0.25, 0.45, 0.1, 0.9))) {
            biome_return.terrain_code = 154; // dead grass
          }
          else {
            biome_return.terrain_code = 153; // dark grass
          }
        }
        break;
      case 3: // high density forest
        if (noise_value > 0.66) { // dark dirt
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.2)) {
            biome_return.terrain_code = 163; // dark dirt
          }
          else {
            biome_return.terrain_code = 162; // gray dirt
          }
        }
        else if (noise_value > 0.5) { // dirty
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.35)) {
            biome_return.terrain_code = 162; // gray dirt
          }
          else {
            biome_return.terrain_code = 161; // light dirt
          }
        }
        else if (noise_value > 0.35) { // light dirt
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.5)) {
            biome_return.terrain_code = 161; // light dirt
          }
          else {
            biome_return.terrain_code = 154; // dead grass
          }
        }
        else { // grassy
          if (Misc.randomObjectRandomChance(random_object, Misc.map(noise_value,
            0.25, 0.35, 0.1, 0.9))) {
            biome_return.terrain_code = 154; // dead grass
          }
          else {
            biome_return.terrain_code = 153; // dark grass
          }
        }
        break;
    }
    double feature_spawn_chance = 0;
    switch(biome_return.terrain_code) {
      case 153: // Grass, dark
        feature_spawn_chance = 0.08;
        break;
      case 154: // Grass, dead
        feature_spawn_chance = 0.16;
        break;
      case 161: // Dirt, light
        feature_spawn_chance = 0.24;
        break;
      case 162: // Dirt, gray
        feature_spawn_chance = 0.32;
        break;
      case 163: // Dirt, dark
        feature_spawn_chance = 0.4;
        break;
    }
    if (forest_density == 1) { // low density
      feature_spawn_chance *= 0.6;
    }
    if (forest_density == 3) { // high density
      feature_spawn_chance *= 1.8;
    }
    if (Misc.randomObjectRandomChance(random_object, feature_spawn_chance)) {
      addTreesToFernwood(biome_return, random_object);
    }
    return biome_return;
  }


  private static void addTreesToFernwood(BiomeReturn biome_return, Random random_object) {
    BiomeReturnFeature biome_return_feature = new BiomeReturnFeature();
    BiomeReturnFeature ivy = new BiomeReturnFeature(413);
    ivy.check_overlap = false;
    boolean add_ivy = false;
    double random_num = random_object.nextDouble();
    Tree tree = null;
    if (random_num > 0.8) {
      biome_return_feature.feature_id = 441; // Bush, light
    }
    else if (random_num > 0.75) {
      biome_return_feature.feature_id = 442; // Bush, dark
    }
    else if (random_num > 0.52) {
      tree = Tree.MAPLE;
    }
    else if (random_num > 0.34) {
      tree = Tree.WALNUT;
    }
    else if (random_num > 0.18) {
      tree = Tree.OAK;
    }
    else if (random_num > 0.06) {
      tree = Tree.DEAD;
    }
    else {
      tree = Tree.PINE;
    }
    if (tree != null) {
      random_num = random_object.nextDouble();
      if (random_num > 0.7) {
        biome_return_feature.feature_id = Tree.large(tree);
        if (Misc.randomObjectRandomChance(random_object, 0.06)) {
          add_ivy = true;
          if (tree == Tree.OAK) {
            ivy.x_adjustment =  Misc.randomObjectRandomInt(random_object, 0, 3);
            ivy.y_adjustment =  Misc.randomObjectRandomInt(random_object, 0, 3);
          }
          else {
            ivy.x_adjustment =  Misc.randomObjectRandomInt(random_object, 0, 2);
            ivy.y_adjustment =  Misc.randomObjectRandomInt(random_object, 0, 2);
          }
        }
      }
      else if (random_num > 0.3) {
        biome_return_feature.feature_id = Tree.small(tree);
        if (Misc.randomObjectRandomChance(random_object, 0.04)) {
          add_ivy = true;
          if (tree == Tree.OAK) {
            ivy.x_adjustment =  Misc.randomObjectRandomInt(random_object, 0, 2);
            ivy.y_adjustment =  Misc.randomObjectRandomInt(random_object, 0, 2);
          }
          else {
            ivy.x_adjustment =  Misc.randomObjectRandomInt(random_object, 0, 1);
            ivy.y_adjustment =  Misc.randomObjectRandomInt(random_object, 0, 1);
          }
        }
      }
      else if (random_num > 0.15) {
        biome_return_feature.feature_id = Tree.sapling(tree);
      }
      else {
        biome_return_feature.feature_id = Tree.sprout(tree);
      }
    }
    biome_return.features.add(biome_return_feature);
    if (add_ivy) {
      biome_return.features.add(ivy);
    }
  }


  private static BiomeReturn clearingProcessPerlinNoise(float noise_value, Random random_object) {
    BiomeReturn biome_return = new BiomeReturn();
    if (noise_value > 0.8) { // dirty
      if (Misc.randomObjectRandomChance(random_object, noise_value + 0.1)) {
        biome_return.terrain_code = 162; // gray dirt
      }
      else {
        biome_return.terrain_code = 161; // light dirt
      }
    }
    else if (noise_value > 0.69) { // dirt to grass
      if (Misc.randomObjectRandomChance(random_object, noise_value + 0.15)) {
        biome_return.terrain_code = 161; // light dirt
        if (Misc.randomObjectRandomChance(random_object, 0.1)) {
          biome_return.terrain_code = 154; // dead grass
        }
      }
      else {
        biome_return.terrain_code = 153; // dark grass
        if (Misc.randomObjectRandomChance(random_object, 0.2)) {
          biome_return.terrain_code = 154; // dead grass
        }
      }
    }
    else if (noise_value > 0.57) { // dark grass
      if (Misc.randomObjectRandomChance(random_object, 0.2)) {
        biome_return.terrain_code = 154; // dead grass
      }
      else if (Misc.randomObjectRandomChance(random_object, noise_value + 0.2)) {
        biome_return.terrain_code = 153; // dark grass
      }
      else {
        biome_return.terrain_code = 152; // green grass
      }
    }
    else if (noise_value > 0.45) { // green grass
      if (Misc.randomObjectRandomChance(random_object, 0.15)) {
        biome_return.terrain_code = 154; // dead grass
      }
      else if (Misc.randomObjectRandomChance(random_object, noise_value + 0.2)) {
        biome_return.terrain_code = 152; // green grass
      }
      else {
        biome_return.terrain_code = 151; // light grass
      }
    }
    else { // light grass
      if (Misc.randomObjectRandomChance(random_object, 0.1)) {
        biome_return.terrain_code = 154; // dead grass
      }
      else {
        biome_return.terrain_code = 151; // light grass
      }
    }
    double tree_bush_spawn_chance = 0;
    switch(biome_return.terrain_code) {
      case 153: // Grass, dark
        tree_bush_spawn_chance = 0.02;
        break;
      case 154: // Grass, dead
        tree_bush_spawn_chance = 0.04;
        break;
      case 161: // Dirt, light
        tree_bush_spawn_chance = 0.06;
        break;
      case 162: // Dirt, gray
        tree_bush_spawn_chance = 0.08;
        break;
    }
    if (Misc.randomObjectRandomChance(random_object, tree_bush_spawn_chance)) {
      addTreesToFernwood(biome_return, random_object);
      return biome_return;
    }
    switch(biome_return.terrain_code) {
      case 151: // Grass, light
      case 153: // Grass, dark
        if (Misc.randomObjectRandomChance(random_object, 0.57 - noise_value)) { // dandelions
          biome_return.addFeature(401);
        }
        break;
      case 152: // Grass, green
        if (Misc.randomObjectRandomChance(random_object, 0.63 - noise_value)) { // dandelions
          biome_return.addFeature(401);
        }
        break;
    }
    return biome_return;
  }


  private static BiomeReturn fernwoodStartProcessPerlinNoise(float noise_value,
    Random random_object, IntegerCoordinate coordinate) {
    BiomeReturn biome_return = new BiomeReturn();
    boolean use_perlin_noise_for_terrain_assignment = true;
    if (coordinate.x > 6 && coordinate.y > 5 && coordinate.y < LNZ.map_chunkWidth - 9) {
      if (coordinate.x > 20) {
        if (coordinate.y > 8 && coordinate.y < LNZ.map_chunkWidth - 16) {
          use_perlin_noise_for_terrain_assignment = false;
          if (noise_value > 0.65) {
            biome_return.terrain_code = 172; // road, asphalt
          }
          else {
            biome_return.terrain_code = 173; // road, dark
          }
        }
      }
      else {
        use_perlin_noise_for_terrain_assignment = false;
        if (noise_value > 0.7) {
          biome_return.terrain_code = 134; // gravel
        }
        else {
          biome_return.terrain_code = 171; // road, light
        }
        switch(coordinate.x) {
          case 7:
          case 20:
            if (coordinate.y < 9 || coordinate.y > LNZ.map_chunkWidth - 13) {
              use_perlin_noise_for_terrain_assignment = true;
            }
            break;
          case 8:
          case 19:
            if (coordinate.y < 8 || coordinate.y > LNZ.map_chunkWidth - 12) {
              use_perlin_noise_for_terrain_assignment = true;
            }
            break;
          case 9:
          case 18:
            if (coordinate.y < 7 || coordinate.y > LNZ.map_chunkWidth - 11) {
              use_perlin_noise_for_terrain_assignment = true;
            }
            break;
          default:
            break;
        }
      }
    }
    if (use_perlin_noise_for_terrain_assignment) {
      if (noise_value > 0.75) {
        biome_return.terrain_code = 162; // gray dirt
      }
      else if (noise_value > 0.65) {
        biome_return.terrain_code = 154; // dead grass
      }
      else if (noise_value > 0.55) {
        biome_return.terrain_code = 151; // light grass
      }
      else {
        biome_return.terrain_code = 152; // green grass
      }
    }
    if (coordinate.x == 10 && coordinate.y == 7) {
      biome_return.addFeature(506); // Honda CRV (broken down)
    }
    else if (coordinate.x == 7 && coordinate.y == 9) {
      biome_return.addFeature(411); // pebbles
    }
    else if (coordinate.x == 9 && coordinate.y == 6) {
      biome_return.addFeature(412); // rocks
    }
    else if (coordinate.x == 19 && coordinate.y == 8) {
      biome_return.addFeature(412); // rocks
    }
    else if (coordinate.x == 16 && coordinate.y == LNZ.map_chunkWidth - 10) {
      biome_return.addFeature(411); // pebbles
    }
    else if (coordinate.x == 15 && coordinate.y == LNZ.map_chunkWidth - 11) {
      biome_return.addFeature(412); // rocks
    }
    else if (coordinate.x == 16 && coordinate.y == LNZ.map_chunkWidth - 11) {
      biome_return.addFeature(411); // pebbles
    }
    if (biome_return.features.size() == 0) {
      switch(biome_return.terrain_code) {
        case 151: // Grass, light
          if (Misc.randomObjectRandomChance(random_object, Math.min(0.02, 0.57 - noise_value))) {
            biome_return.addFeature(401);
          }
          break;
        case 152: // Grass, green
          if (Misc.randomObjectRandomChance(random_object, 0.57 - noise_value)) {
            biome_return.addFeature(401);
          }
          break;
      }
    }
    return biome_return;
  }


  private static BiomeReturn fernwoodPondProcessPerlinNoise(float noise_value,
    Random random_object, IntegerCoordinate coordinate) {
    BiomeReturn biome_return = new BiomeReturn();
    double distance_from_center_x = coordinate.x - 0.5 * (LNZ.map_chunkWidth - 1);
    double distance_from_center_y = coordinate.y - 0.5 * (LNZ.map_chunkWidth - 1);
    double distance_from_center = Math.sqrt(distance_from_center_x * distance_from_center_x + distance_from_center_y * distance_from_center_y);
    double adjusted_noise_value = noise_value + 0.05 * distance_from_center;
    if (adjusted_noise_value > 1.3) {
      if (Misc.randomObjectRandomChance(random_object, noise_value)) {
        biome_return.terrain_code = 162; // gray dirt
      }
      else {
        biome_return.terrain_code = 161; // light dirt
      }
    }
    else if (adjusted_noise_value > 1.2) {
      if (Misc.randomObjectRandomChance(random_object, noise_value - 0.2)) {
        biome_return.terrain_code = 161; // light dirt
      }
      else {
        biome_return.terrain_code = 154; // dead grass
      }
    }
    else if (adjusted_noise_value > 1.09) {
      if (Misc.randomObjectRandomChance(random_object, noise_value - 0.2)) {
        biome_return.terrain_code = 154; // dead grass
      }
      else {
        biome_return.terrain_code = 151; // light grass
      }
    }
    else if (adjusted_noise_value > 1.02) {
      if (noise_value > 0.7) {
        biome_return.terrain_code = 134; // gravel
      }
      else if (noise_value > 0.5) {
        biome_return.terrain_code = 163; // dirt, dark
      }
      else {
        biome_return.terrain_code = 143; // sand, dark
      }
    }
    else if (adjusted_noise_value > 0.9) {
      if (noise_value > 0.7) {
        biome_return.terrain_code = 181; // water, rocks
      }
      else if (noise_value > 0.5) {
        biome_return.terrain_code = 182; // water, dirt
      }
      else {
        biome_return.terrain_code = 183; // water, shallow
      }
    }
    else {
      biome_return.terrain_code = 184; // water, medium
    }
    double tree_bush_spawn_chance = 0;
    switch(biome_return.terrain_code) {
      case 154: // Grass, dead
        tree_bush_spawn_chance = 0.07;
        break;
      case 161: // Dirt, light
        tree_bush_spawn_chance = 0.07;
        break;
      case 162: // Dirt, gray
        tree_bush_spawn_chance = 0.08;
        break;
    }
    if (Misc.randomObjectRandomChance(random_object, tree_bush_spawn_chance)) {
      addTreesToFernwood(biome_return, random_object);
    }
    if (biome_return.terrain_code == 134) { // gravel
      if (Misc.randomObjectRandomChance(random_object, noise_value - 0.5)) {
        if (Misc.randomObjectRandomChance(random_object, noise_value - 0.4)) {
          biome_return.addFeature(412);
        }
        else {
          biome_return.addFeature(411);
        }
      }
    }
    return biome_return;
  }


  private static BiomeReturn fernwoodCampgroundProcessPerlinNoise(int forest_density,
    float noise_value, Random random_object, IntegerCoordinate coordinate) {
    BiomeReturn biome_return = new BiomeReturn();
    // terrain
    switch(forest_density) {
      case 0: // clearing
        if (noise_value > 0.8) { // dirty
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.1)) {
            biome_return.terrain_code = 162; // gray dirt
          }
          else {
            biome_return.terrain_code = 161; // light dirt
          }
        }
        else if (noise_value > 0.69) { // dirt to grass
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.15)) {
            biome_return.terrain_code = 161; // light dirt
            if (Misc.randomObjectRandomChance(random_object, 0.1)) {
              biome_return.terrain_code = 154; // dead grass
            }
          }
          else {
            biome_return.terrain_code = 153; // dark grass
            if (Misc.randomObjectRandomChance(random_object, 0.2)) {
              biome_return.terrain_code = 154; // dead grass
            }
          }
        }
        else if (noise_value > 0.57) { // dark grass
          if (Misc.randomObjectRandomChance(random_object, 0.2)) {
            biome_return.terrain_code = 154; // dead grass
          }
          else if (Misc.randomObjectRandomChance(random_object, noise_value + 0.2)) {
            biome_return.terrain_code = 153; // dark grass
          }
          else {
            biome_return.terrain_code = 152; // green grass
          }
        }
        else if (noise_value > 0.45) { // green grass
          if (Misc.randomObjectRandomChance(random_object, 0.15)) {
            biome_return.terrain_code = 154; // dead grass
          }
          else if (Misc.randomObjectRandomChance(random_object, noise_value + 0.2)) {
            biome_return.terrain_code = 152; // green grass
          }
          else {
            biome_return.terrain_code = 151; // light grass
          }
        }
        else { // light grass
          if (Misc.randomObjectRandomChance(random_object, 0.1)) {
            biome_return.terrain_code = 154; // dead grass
          }
          else {
            biome_return.terrain_code = 151; // light grass
          }
        } 
        break;
      case 1: // low density forest
        if (noise_value > 0.85) { // dark dirt
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.05)) {
            biome_return.terrain_code = 163; // dark dirt
          }
          else {
            biome_return.terrain_code = 162; // gray dirt
          }
        }
        else if (noise_value > 0.7) { // dirty
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.2)) {
            biome_return.terrain_code = 162; // gray dirt
          }
          else {
            biome_return.terrain_code = 161; // light dirt
          }
        }
        else if (noise_value > 0.55) { // light dirt
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.3)) {
            biome_return.terrain_code = 161; // light dirt
          }
          else {
            biome_return.terrain_code = 154; // dead grass
          }
        }
        else { // grassy
          if (Misc.randomObjectRandomChance(random_object, 2 * noise_value)) {
            biome_return.terrain_code = 154; // dead grass
          }
          else {
            biome_return.terrain_code = 153; // dark grass
          }
        }
        break;
      case 2: // medium density forest
        if (noise_value > 0.78) { // dark dirt
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.15)) {
            biome_return.terrain_code = 163; // dark dirt
          }
          else {
            biome_return.terrain_code = 162; // gray dirt
          }
        }
        else if (noise_value > 0.6) { // dirty
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.3)) {
            biome_return.terrain_code = 162; // gray dirt
          }
          else {
            biome_return.terrain_code = 161; // light dirt
          }
        }
        else if (noise_value > 0.45) { // light dirt
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.4)) {
            biome_return.terrain_code = 161; // light dirt
          }
          else {
            biome_return.terrain_code = 154; // dead grass
          }
        }
        else { // grassy
          if (Misc.randomObjectRandomChance(random_object, Misc.map(noise_value,
            0.25, 0.45, 0.1, 0.9))) {
            biome_return.terrain_code = 154; // dead grass
          }
          else {
            biome_return.terrain_code = 153; // dark grass
          }
        }
        break;
      case 3: // high density forest
        if (noise_value > 0.66) { // dark dirt
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.2)) {
            biome_return.terrain_code = 163; // dark dirt
          }
          else {
            biome_return.terrain_code = 162; // gray dirt
          }
        }
        else if (noise_value > 0.5) { // dirty
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.35)) {
            biome_return.terrain_code = 162; // gray dirt
          }
          else {
            biome_return.terrain_code = 161; // light dirt
          }
        }
        else if (noise_value > 0.35) { // light dirt
          if (Misc.randomObjectRandomChance(random_object, noise_value + 0.5)) {
            biome_return.terrain_code = 161; // light dirt
          }
          else {
            biome_return.terrain_code = 154; // dead grass
          }
        }
        else { // grassy
          if (Misc.randomObjectRandomChance(random_object, Misc.map(noise_value,
            0.25, 0.35, 0.1, 0.9))) {
            biome_return.terrain_code = 154; // dead grass
          }
          else {
            biome_return.terrain_code = 153; // dark grass
          }
        }
        break;
    }
    // nature
    double feature_spawn_chance = 0;
    switch(biome_return.terrain_code) {
      case 153: // Grass, dark
        feature_spawn_chance = 0.07;
        break;
      case 154: // Grass, dead
        feature_spawn_chance = 0.14;
        break;
      case 161: // Dirt, light
        feature_spawn_chance = 0.21;
        break;
      case 162: // Dirt, gray
        feature_spawn_chance = 0.28;
        break;
      case 163: // Dirt, dark
        feature_spawn_chance = 0.35;
        break;
    }
    if (forest_density == 0) { // Clearing
      feature_spawn_chance *= 0.2;
    }
    else if (forest_density == 1) { // low density
      feature_spawn_chance *= 0.6;
    }
    else if (forest_density == 3) { // high density
      feature_spawn_chance *= 1.8;
    }
    if (Misc.randomObjectRandomChance(random_object, feature_spawn_chance)) {
      addTreesToFernwood(biome_return, random_object);
    }
    switch(biome_return.terrain_code) {
      case 151: // Grass, light
      case 153: // Grass, dark
        if (Misc.randomObjectRandomChance(random_object, 0.57 - noise_value)) { // dandelions
          biome_return.addFeature(401);
        }
        break;
      case 152: // Grass, green
        if (Misc.randomObjectRandomChance(random_object, 0.63 - noise_value)) { // dandelions
          biome_return.addFeature(401);
        }
        break;
    }
    // campground
    if (coordinate.x == 14 && coordinate.y == 14) {
      biome_return.features.clear();
      BiomeReturnFeature biome_return_feature = new BiomeReturnFeature(261);
      biome_return_feature.specify_feature_toggle = true;
      if (Misc.randomObjectRandomChance(random_object, 0.4)) {
        biome_return_feature.feature_toggle = true;
      }
      biome_return_feature.specify_feature_number = true;
      biome_return_feature.feature_number =  Misc.randomObjectRandomInt(random_object, 0, 20);
      biome_return.features.add(biome_return_feature);
    }
    else if (coordinate.x > 8 && coordinate.x < 20 && coordinate.y > 8 && coordinate.y < 20) {
      if (Misc.randomObjectRandomChance(random_object, 0.4)) {
        biome_return.features.clear();
      }
      if (coordinate.x > 10 && coordinate.x < 17 && coordinate.y > 10 && coordinate.y < 17) {
        biome_return.features.clear();
      }
      else if (Misc.randomObjectRandomChance(random_object, 0.01)) {
        BiomeReturnFeature biome_return_feature = new BiomeReturnFeature(256);
        biome_return.features.add(biome_return_feature);
      }
      else if (Misc.randomObjectRandomChance(random_object, 0.01)) {
        BiomeReturnFeature biome_return_feature = new BiomeReturnFeature(257);
        biome_return.features.add(biome_return_feature);
      }
      else if (Misc.randomObjectRandomChance(random_object, 0.01)) {
        BiomeReturnFeature biome_return_feature = new BiomeReturnFeature(258);
        biome_return_feature.specify_feature_toggle = true;
        if (Misc.randomObjectRandomChance(random_object, 0.5)) {
          biome_return_feature.feature_toggle = true;
        }
        biome_return.features.add(biome_return_feature);
      }
      else if (Misc.randomObjectRandomChance(random_object, 0.01)) {
        BiomeReturnFeature biome_return_feature = new BiomeReturnFeature(259);
        biome_return.features.add(biome_return_feature);
      }
      else if (Misc.randomObjectRandomChance(random_object, 0.01)) {
        BiomeReturnFeature biome_return_feature = new BiomeReturnFeature(260);
        biome_return.features.add(biome_return_feature);
      }
    }
    return biome_return;
  }


  private static BiomeReturn grassProcessPerlinNoise(float noise_value, Random random_object) {
    BiomeReturn biome_return = new BiomeReturn();
    if (noise_value > 0.8) {
      biome_return.terrain_code = 154; // grass, dead
    }
    else if (noise_value > 0.6) {
      biome_return.terrain_code = 151; // grass, light
    }
    else {
      biome_return.terrain_code = 152; // grass, green
    }
    return biome_return;
  }


  static int scytheGrassItemId(Biome biome) {
    int item_id = 0;
    double random_number = Math.random();
    switch(biome) {
      case MAPLE_FOREST1:
      case MAPLE_FOREST2:
      case MAPLE_FOREST3:
      case CLEARING:
      case FERNWOOD_START:
      case FERNWOOD_CAMPGROUND_FOREST1:
      case FERNWOOD_CAMPGROUND_FOREST2:
      case FERNWOOD_CAMPGROUND_FOREST3:
      case FERNWOOD_CAMPGROUND_CLEARING:
      case MAIN_ROAD1:
      case MAIN_ROAD2:
      case MAIN_ROAD3:
      case MAIN_ROAD4:
      case MAIN_ROAD5:
      default:
        if (random_number < 0.25) {
          item_id = 2007;
        }
        else if (random_number < 0.5) {
          item_id = 2008;
        }
        else if (random_number < 0.65) {
          item_id = 2005;
        }
        else if (random_number < 0.8) {
          item_id = 2006;
        }
        else if (random_number < 0.85) {
          item_id = 2003;
        }
        else if (random_number < 0.9) {
          item_id = 2004;
        }
        else if (random_number < 0.93) {
          item_id = 2001;
        }
        else if (random_number < 0.96) {
          item_id = 2002;
        }
        else if (random_number < 0.98) {
          item_id = 2009;
        }
        else {
          item_id = 2010;
        }
        break;
      case FERNWOOD_POND:
      case CROSS_CREEK1:
      case CROSS_CREEK2:
      case CROSS_CREEK3:
      case CROSS_CREEK4:
      case CROSS_CREEK5:
        if (random_number < 0.2) {
          item_id = 2007;
        }
        else if (random_number < 0.4) {
          item_id = 2008;
        }
        else if (random_number < 0.5) {
          item_id = 2005;
        }
        else if (random_number < 0.6) {
          item_id = 2006;
        }
        else if (random_number < 0.65) {
          item_id = 2003;
        }
        else if (random_number < 0.7) {
          item_id = 2004;
        }
        else if (random_number < 0.83) {
          item_id = 2001;
        }
        else if (random_number < 0.96) {
          item_id = 2002;
        }
        else if (random_number < 0.98) {
          item_id = 2009;
        }
        else {
          item_id = 2010;
        }
        break;
    }
    return item_id;
  }
}


class BiomeSpawnReturn {
  protected boolean spawn_unit = false;
  protected int unit_id = 0;
  protected boolean limit_by_terrain = false;
  protected HashSet<Integer> terrain_ids = new HashSet<Integer>();
  protected boolean only_spawn_night = false;
  BiomeSpawnReturn() {}
  void addUnit(int id) {
    this.spawn_unit = true;
    this.unit_id = id;
  }

  static BiomeSpawnReturn unitSpawnReturn(Biome biome, int current_units) {
    BiomeSpawnReturn spawn_return = new BiomeSpawnReturn();
    switch(biome) {
      case FERNWOOD_POND:
        if (Misc.randomChance(0.12)) {
          spawn_return.addUnit(1009);
          spawn_return.limit_by_terrain = true;
          spawn_return.terrain_ids.add(134);
          spawn_return.terrain_ids.add(163);
          spawn_return.terrain_ids.add(143);
        }
        else if (Misc.randomChance(1 - 0.012 * current_units)) {
          break;
        }
        else if (Misc.randomChance(0.06)) {
          spawn_return.addUnit(1010);
          spawn_return.limit_by_terrain = true;
          spawn_return.terrain_ids.add(134);
          spawn_return.terrain_ids.add(163);
          spawn_return.terrain_ids.add(143);
          spawn_return.terrain_ids.add(161);
          spawn_return.terrain_ids.add(162);
          spawn_return.terrain_ids.add(154);
          spawn_return.terrain_ids.add(152);
        }
        else if (Misc.randomChance(0.06)) {
          spawn_return.addUnit(1011);
          spawn_return.limit_by_terrain = true;
          spawn_return.terrain_ids.add(134);
          spawn_return.terrain_ids.add(163);
          spawn_return.terrain_ids.add(143);
          spawn_return.terrain_ids.add(161);
          spawn_return.terrain_ids.add(162);
          spawn_return.terrain_ids.add(154);
          spawn_return.terrain_ids.add(152);
        }
        else if (Misc.randomChance(0.03)) {
          spawn_return.addUnit(1013);
          spawn_return.only_spawn_night = true;
          spawn_return.limit_by_terrain = true;
          spawn_return.terrain_ids.add(134);
          spawn_return.terrain_ids.add(163);
          spawn_return.terrain_ids.add(143);
          spawn_return.terrain_ids.add(161);
          spawn_return.terrain_ids.add(162);
          spawn_return.terrain_ids.add(154);
          spawn_return.terrain_ids.add(152);
        }
        break;
      case MAPLE_FOREST1:
      case MAPLE_FOREST2:
      case MAPLE_FOREST3:
        if (Misc.randomChance(1 - 0.012 * current_units)) {
          break;
        }
        if (Misc.randomChance(0.02)) {
          spawn_return.addUnit(1010);
        }
        else if (Misc.randomChance(0.12)) {
          spawn_return.addUnit(1011);
        }
        else if (Misc.randomChance(0.03)) {
          spawn_return.addUnit(1013);
          spawn_return.only_spawn_night = true;
        }
        break;
      case CLEARING:
        if (Misc.randomChance(1 - 0.012 * current_units)) {
          break;
        }
        if (Misc.randomChance(0.05)) {
          spawn_return.addUnit(1010);
        }
        else if (Misc.randomChance(0.04)) {
          spawn_return.addUnit(1011);
        }
        else if (Misc.randomChance(0.03)) {
          spawn_return.addUnit(1013);
          spawn_return.only_spawn_night = true;
        }
        break;
      case FERNWOOD_CAMPGROUND_FOREST1:
      case FERNWOOD_CAMPGROUND_FOREST2:
      case FERNWOOD_CAMPGROUND_FOREST3:
        if (Misc.randomChance(1 - 0.012 * current_units)) {
          break;
        }
        if (Misc.randomChance(0.02)) {
          spawn_return.addUnit(1010);
        }
        else if (Misc.randomChance(0.12)) {
          spawn_return.addUnit(1011);
        }
        else if (Misc.randomChance(0.03)) {
          spawn_return.addUnit(1013);
          spawn_return.only_spawn_night = true;
        }
        break;
      case FERNWOOD_CAMPGROUND_CLEARING:
        if (Misc.randomChance(1 - 0.012 * current_units)) {
          break;
        }
        if (Misc.randomChance(0.05)) {
          spawn_return.addUnit(1010);
        }
        else if (Misc.randomChance(0.04)) {
          spawn_return.addUnit(1011);
        }
        else if (Misc.randomChance(0.03)) {
          spawn_return.addUnit(1013);
          spawn_return.only_spawn_night = true;
        }
        break;
      default:
        break;
    }
    return spawn_return;
  }
}