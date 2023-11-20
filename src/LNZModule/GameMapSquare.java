package LNZModule;

import java.util.concurrent.*;
import processing.core.*;
import DImg.DImg;
import Misc.Misc;

class GameMapSquare {
  private LNZ p;

  protected IntegerCoordinate coordinate;
  protected int base_elevation = 0; // set manually by editor or map gen
  protected int terrain_elevation = 0; // determined by terrain id
  protected int feature_elevation = 0; // determined by features on terrain
  protected int terrain_id = 0;
  protected boolean explored = false;
  protected boolean visible = false;
  protected boolean in_view = false;
  protected boolean blocking_view = false;
  protected double light_level = 8; // [0, 10]
  protected boolean light_source = false;
  protected double original_light = 0;
  private double light_blocked_by_feature = 0;
  private double base_speed_slower = 0;
  private double feature_speed_slower = 0;
  protected boolean can_grow_ivy = false;
  protected CopyOnWriteArrayList<Feature> features = new CopyOnWriteArrayList<Feature>();
  protected ConcurrentHashMap<Integer, Unit> units = new ConcurrentHashMap<Integer, Unit>();
  protected ConcurrentHashMap<Integer, Item> items = new ConcurrentHashMap<Integer, Item>();
  protected int timer_square = 0;

  GameMapSquare(LNZ sketch, int i, int j) {
    this(sketch, new IntegerCoordinate(i, j), 1);
  }
  GameMapSquare(LNZ sketch, IntegerCoordinate coordinate) {
    this(sketch, coordinate, 1);
  }
  GameMapSquare(LNZ sketch, int i, int j, int terrain_id) {
    this(sketch, new IntegerCoordinate(i, j), terrain_id);
  }
  GameMapSquare(LNZ sketch, IntegerCoordinate coordinate, int terrain_id) {
    this.p = sketch;
    this.coordinate = coordinate;
    this.setTerrain(terrain_id);
  }

  void setTerrain(int id) {
    this.terrain_id = id;
    this.base_speed_slower = 0;
    if (id > 800) { // slabs (5)
      this.terrain_elevation = 5;
    }
    else if (id > 700) { // slabs (4)
      this.terrain_elevation = 4;
    }
    else if (id > 600) { // slabs (3)
      this.terrain_elevation = 3;
    }
    else if (id > 500) { // slabs (2)
      this.terrain_elevation = 2;
    }
    else if (id > 400) { // slabs (1)
      this.terrain_elevation = 1;
    }
    else if (id > 300) { // stairs
      this.terrain_elevation = 3;
      this.base_speed_slower = -0.1;
    }
    else if (id > 200) { // walls
      this.terrain_elevation = 100;
    }
    else if (id > 100) { // floors
      this.terrain_elevation = 0;
    }
    else if (id == 2) { // walkable map edge
      this.terrain_elevation = 0;
    }
    else if (id == 1) { // map edge
      this.terrain_elevation = 100;
    }
    else {
      p.global.errorMessage("ERROR: Terrain ID " + id + " not found.");
    }
    switch(id) {
      case 181: // water, rocks
      case 182: // water, dirt
        this.base_speed_slower = -0.15;
        break;
      case 183: // water, shallow
        this.base_speed_slower = -0.2;
        break;
      case 184: // water, medium
        this.base_speed_slower = -0.35;
        break;
      case 185: // water, deep
        this.base_speed_slower = -0.45;
        break;
      case 191: // lava
        this.base_speed_slower = -0.5;
        this.timer_square = Misc.randomInt(LNZ.gif_lava_time);
        break;
      default:
        break;
    }
  }

  void addedFeature(Feature f, int i, int j) {
    for (Feature feature : this.features) {
      if (f.map_key == feature.map_key) {
        p.global.errorMessage("ERROR: Feature with map key " + f.map_key + " is " +
          "already added to map square " + i + ", " + j + ".");
        return;
      }
    }
    this.features.add(f);
    if (f.ignoresOtherFeatureHeights()) {
      f.curr_height = Math.max(this.base_elevation + this.terrain_elevation, f.curr_height);
    }
    else {
      f.curr_height = Math.max(this.elevation(null), f.curr_height);
    }
    this.feature_elevation += f.featureHeight(i, j);
    this.light_blocked_by_feature += f.lightPercentageBlocked();
    this.feature_speed_slower += f.terrainSpeedSlow(i, j);
    f.map_priority = Math.max(f.map_priority, this.features.size());
    switch(f.ID) {
      case 421: // Trees
      case 422:
      case 423:
      case 424:
      case 425:
      case 426:
      case 444:
      case 445:
      case 446:
      case 447:
      case 448:
      case 449:
      case 441: // Bushes
      case 442:
      case 443:
        this.can_grow_ivy = true;
        break;
      default:
        this.can_grow_ivy = false;
        break;
    }
  }

  void removedFeature(Feature f, int i, int j) {
    boolean missing_feature = true;
    for (int k = 0; k < this.features.size(); k++) {
      Feature feature = this.features.get(k);
      if (f.map_key == feature.map_key) {
        missing_feature = false;
        this.features.remove(k);
        break;
      }
    }
    if (missing_feature) {
      p.global.errorMessage("ERROR: Feature with map key " + f.map_key + " is " +
          "missing from map square " + i + ", " + j + ".");
    }
    this.feature_elevation -= f.featureHeight(i, j);
    this.light_blocked_by_feature -= f.lightPercentageBlocked();
    this.feature_speed_slower -= f.terrainSpeedSlow(i, j);
    switch(f.ID) {
      case 413: // Ivy
        this.can_grow_ivy = true;
        break;
      case 421: // Trees
      case 422:
      case 423:
      case 424:
      case 425:
      case 426:
      case 444:
      case 445:
      case 446:
      case 447:
      case 448:
      case 449:
      case 441: // Bushes
      case 442:
      case 443:
        this.can_grow_ivy = false;
        break;
    }
  }

  double speedMultiplier(Unit u) {
    double speed_slower = this.base_speed_slower + this.feature_speed_slower;
    if (speed_slower < 0) {
      speed_slower *= 1 - u.agility() * LNZ.unit_agilitySlowDecrease;
    }
    return 1 + speed_slower;
  }

  void addUnit(Unit u) {
    if (u == null || u.remove) {
      return;
    }
    this.units.put(u.map_key, u);
  }

  void removeUnit(Unit u) {
    if (u == null || u.remove) {
      return;
    }
    this.units.remove(u.map_key);
  }

  void addItem(Item i) {
    if (i == null || i.remove) {
      return;
    }
    this.items.put(i.map_key, i);
  }

  void removeItem(Item i) {
    if (i == null || i.remove) {
      return;
    }
    this.items.remove(i.map_key);
  }

  void update(AbstractGameMap map, int x, int y) {
    switch(this.terrain_id) {
      case 151: // Grass, light
        if (this.timer_square < 0) {
          if (Misc.randomChance(0.02)) {
            map.setTerrain(152, x, y);
          }
        }
        else {
          this.timer_square--;
        }
        break;
      case 152: // Grass, green
        if (this.timer_square < 0) {
          if (Misc.randomChance(0.02)) {
            int square_x = x - 1 + Misc.randomInt(2);
            int square_y = y - 1 + Misc.randomInt(2);
            if (square_x != x && square_y != y) {
              break; // no diagonal growth
            }
            GameMapSquare square = map.mapSquare(square_x, square_y);
            if (square == null || !square.canGrowGrass()) {
              break;
            }
            map.setTerrain(151, square_x, square_y);
            square.timer_square = 100;
          }
        }
        else {
          this.timer_square--;
        }
        break;
      case 164: // Tilled Dirt
        if (this.features.size() > 0 && this.features.get(this.features.size() - 1).tilledPlant()) {
          if (Misc.randomChance(0.001)) {
            map.setTerrain(162, x, y);
          }
          break;
        }
        if (Misc.randomChance(0.003)) {
          map.setTerrain(162, x, y);
        }
        break;
      case 165: // Tilled Dirt, watered
        this.timer_square--;
        if (this.timer_square < 0) { // out of water
          map.setTerrain(164, x, y);
        }
        break;
      default:
        break;
    }
  }

  boolean canGrowGrass() {
    switch(this.terrain_id) {
      case 161: // Dirt, light
        if (Misc.randomChance(0.5)) { // spreads half as fast in light dirt
          return true;
        }
        return false;
      case 162: // Dirt
      case 163: // Dirt, dark
        return true;
      default:
        return false;
    }
  }

  boolean canPlantSomething() {
    if (this.features.size() > 0) {
      return false;
    }
    if (!this.canGrowSomething()) {
      return false;
    }
    return true;
  }
  
  boolean canGrowSomething() {
    switch(this.terrain_id) {
      case 151: // Grass
      case 152:
      case 153:
      case 154:
      case 155:
      case 156:
      case 161: // Dirt
      case 162:
      case 163:
      case 164:
      case 165:
        return true;
      default:
        return false;
    }
  }

  void updateLightLevel(AbstractGameMap map, int x, int y) {
    if (this.terrain_id == 191) { // lava
      this.light_source = true;
      this.light_level = 9.2;
      return;
    }
    double light = this.light_level;
    double lightDecay = LNZ.map_lightDecay;
    if (light < 7) {
      lightDecay = LNZ.map_lightDecayDim + light *
        (LNZ.map_lightDecay - LNZ.map_lightDecayDim) / 7;
    }
    if (!this.light_source) {
      light -= lightDecay;
    }
    if (map.outside_map && map.base_light_level > light) {
      light = map.base_light_level;
    }
    try {
      GameMapSquare square = map.mapSquare(x + 1, y);
      if (square.light_source || square.passesLight()) {
        double light_right = square.light_level - lightDecay;
        light_right *= 1 - square.light_blocked_by_feature;
        if (light_right > light) {
          light = light_right;
        }
      }
    } catch(NullPointerException e) {}
    try {
      GameMapSquare square = map.mapSquare(x - 1, y);
      if (square.light_source || square.passesLight()) {
        double light_left = square.light_level - lightDecay;
        light_left *= 1 - square.light_blocked_by_feature;
        if (light_left > light) {
          light = light_left;
        }
      }
    } catch(NullPointerException e) {}
    try {
      GameMapSquare square = map.mapSquare(x, y + 1);
      if (square.light_source || square.passesLight()) {
        double light_down = square.light_level - lightDecay;
        light_down *= 1 - square.light_blocked_by_feature;
        if (light_down > light) {
          light = light_down;
        }
      }
    } catch(NullPointerException e) {}
    try {
      GameMapSquare square = map.mapSquare(x, y - 1);
      if (square.light_source || square.passesLight()) {
        double light_up = square.light_level - lightDecay;
        light_up *= 1 - square.light_blocked_by_feature;
        if (light_up > light) {
          light = light_up;
        }
      }
    } catch(NullPointerException e) {}
    if (light < 0) {
      light = 0;
    }
    this.light_level = light;
  }

  int getColor(int fog_color) {
    double light_factor = 0.1 * this.light_level; // [0, 1]
    if (light_factor > 1) {
      light_factor = 1;
    }
    else if (light_factor < 0) {
      light_factor = 0;
    }
    double r = (fog_color >> 16 & 0xFF) * light_factor;
    double g = (fog_color >> 8 & 0xFF) * light_factor;
    double b = (fog_color & 0xFF) * light_factor;
    double a = (fog_color >> 24 & 0xFF) + (1 - light_factor) * (255 - (fog_color >> 24 & 0xFF));
    return DImg.ccolor(r, g, b, a);
  }

  boolean canPlaceOn() {
    if (this.terrain_id < 101 || this.terrain_id > 180) {
      return false;
    }
    if (this.feature_elevation != 0) {
      return false;
    }
    return true;
  }

  boolean passesLight() {
    switch(this.terrain_id) {
      case 1:
      case 201:
      case 202:
      case 203:
      case 204:
      case 205:
      case 206:
      case 207:
      case 211:
      case 212:
      case 213:
        return false;
      default:
        if (this.light_blocked_by_feature < 1) {
          return true;
        }
        else {
          return false;
        }
    }
  }

  boolean passesVision() {
    return this.passesLight() && this.light_blocked_by_feature < 0.85;
  }

  boolean mapEdge() {
    switch(this.terrain_id) {
      case 1:
      case 2:
        return true;
      default:
        return false;
    }
  }

  boolean isWall() {
    if (this.terrain_elevation > LNZ.map_maxHeight) {
      return true;
    }
    return false;
  }

  boolean isStair() {
    return (this.terrain_id > 300 && this.terrain_id < 401);
  }

  int adjustElevation(Coordinate relative_coordinate) {
    if (relative_coordinate == null) {
      return 0;
    }
    switch(this.terrain_id) { // check stairs
      case 301: // up
      case 305:
      case 309:
      case 313:
      case 317:
      case 321:
        if (relative_coordinate.y > 1) {
          return -3;
        }
        else if (relative_coordinate.y > 0.666) {
          return -2;
        }
        else if (relative_coordinate.y > 0.333) {
          return -1;
        }
        return 0;
      case 302: // down
      case 306:
      case 310:
      case 314:
      case 318:
      case 322:
        if (relative_coordinate.y < 0) {
          return -3;
        }
        else if (relative_coordinate.y < 0.333) {
          return -2;
        }
        else if (relative_coordinate.y < 0.666) {
          return -1;
        }
        return 0;
      case 303: // left
      case 307:
      case 311:
      case 315:
      case 319:
      case 323:
        if (relative_coordinate.x > 1) {
          return -3;
        }
        else if (relative_coordinate.x > 0.666) {
          return -2;
        }
        else if (relative_coordinate.x > 0.333) {
          return -1;
        }
        return 0;
      case 304: // right
      case 308:
      case 312:
      case 316:
      case 320:
      case 324:
        if (relative_coordinate.x < 0) {
          return -3;
        }
        else if (relative_coordinate.x < 0.333) {
          return -2;
        }
        else if (relative_coordinate.x < 0.666) {
          return -1;
        }
        return 0;
      default:
        return 0;
    }
  }

  int elevation(Coordinate relative_coordinate) {
    int net_elevation = this.base_elevation + this.terrain_elevation + this.feature_elevation;
    net_elevation += this.adjustElevation(relative_coordinate);
    return net_elevation;
  }

  String terrainName() {
    switch(this.terrain_id) {
      case 101: // floors
      case 102:
      case 103:
      case 104:
        return "Carpet";
      case 111:
      case 112:
      case 113:
        return "Wood Floor";
      case 121:
      case 122:
      case 123:
        return "Tile Floor";
      case 131:
        return "Concrete Floor";
      case 132:
      case 133:
        return "Sidewalk";
      case 134:
        return "Gravel";
      case 135:
        return "Brick";
      case 141:
      case 142:
      case 143:
      case 144:
      case 145:
        return "Sand";
      case 151:
      case 152:
      case 153:
      case 154:
      case 155:
      case 156:
        return "Grass";
      case 161:
      case 162:
      case 163:
        return "Dirt";
      case 164:
      case 165:
        return "Tilled Dirt";
      case 170:
      case 171:
      case 172:
      case 173:
      case 174:
      case 175:
      case 176:
      case 177:
      case 178:
      case 179:
      case 180:
        return "Road";
      case 181:
      case 182:
      case 183:
      case 184:
      case 185:
        return "Water";
      case 191:
        return "Lava";
      case 201:
      case 202:
      case 203:
      case 204:
      case 205:
      case 206:
      case 207:
      case 208:
        return "Brick Wall";
      case 211:
      case 212:
      case 213:
        return "Wooden Wall";
      case 301:
      case 302:
      case 303:
      case 304:
      case 305:
      case 306:
      case 307:
      case 308:
      case 309:
      case 310:
      case 311:
      case 312:
      case 313:
      case 314:
      case 315:
      case 316:
      case 317:
      case 318:
      case 319:
      case 320:
      case 321:
      case 322:
      case 323:
      case 324:
        return "Stairs";
      case 401:
      case 501:
      case 601:
      case 701:
      case 801:
        return "Brick Slab";
      case 402:
      case 502:
      case 602:
      case 702:
      case 802:
        return "Dirt Slab";
      default:
        return "";
    }
  }

  String selectedObjectTextboxText() {
    switch(this.terrain_id) {
      case 151: // Grass, light
      case 152: // Grass, green
      case 153: // Grass, dark
      case 154: // Grass, dead
      case 155: // Grass, green, line left
      case 156: // Grass, green, line up
        if (!p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "";
        }
        return "Can use a hoe on this grass to scythe it, perhaps finding useful items in the process.";
      case 161: // Dirt
      case 162:
      case 163:
        if (!p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "";
        }
        return "Can use a hoe on this dirt to till it, allowing for seeds to be planted.";
      case 164: // Tilled Dirt
        if (!p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "";
        }
        return "Tilled soil where seeds can be planted. Currently unwatered " +
          "and at danger of becoming untilled soil. Most plants grow much slower.";
      case 165: // Tilled Dirt, watered
        if (!p.global.profile.upgraded(PlayerTreeCode.FARMING_INSIGHT)) {
          return "";
        }
        return "Tilled soil where seeds can be planted. Currently watered " +
          "with " + Math.max(1, Math.round(100.0*this.timer_square/240.0)) + "% saturation.";
      default:
        return "";
    }
  }

  boolean blocksPlayerView(IntegerCoordinate square_grid, IntegerCoordinate player_grid,
    Coordinate player_coordinate, double player_height) {
    IntegerCoordinate dif_coordinate = square_grid.subtractR(player_grid);
    Coordinate relative_coordinate = player_coordinate.subtractR(square_grid);
    if (dif_coordinate.x < 0 || dif_coordinate.y < 0) {
      return false;
    }
    if (Math.abs(dif_coordinate.x - dif_coordinate.y) > 2) {
      return false;
    }
    if (player_height >= this.terrainImageHeightOverflow(relative_coordinate) - 1) {
      return false;
    }
    if (Math.max(dif_coordinate.x, dif_coordinate.y) - 1 >
      Math.ceil(this.terrainImageHeightOverflow(relative_coordinate) * 0.5)) {
      return false;
    }
    return true;
  }

  private String getImagePath() {
    String image_name = "terrain/";
    switch(this.terrain_id) {
      case 1:
      case 2:
        image_name += "default.png";
        break;
      case 101:
        image_name += "carpet_light.png";
        break;
      case 102:
        image_name += "carpet_gray.png";
        break;
      case 103:
        image_name += "carpet_dark.png";
        break;
      case 104:
        image_name += "carpet_green.png";
        break;
      case 111:
        image_name += "woodFloor_light.png";
        break;
      case 112:
        image_name += "woodFloor_brown.png";
        break;
      case 113:
        image_name += "woodFloor_dark.png";
        break;
      case 121:
        image_name += "tile_red.png";
        break;
      case 122:
        image_name += "tile_green.png";
        break;
      case 123:
        image_name += "tile_gray.png";
        break;
      case 131:
        image_name += "concrete.png";
        break;
      case 132:
        image_name += "sidewalk_smooth.png";
        break;
      case 133:
        image_name += "sidewalk_cracked.png";
        break;
      case 134:
        image_name += "gravel.png";
        break;
      case 135:
        image_name += "brick_floor.png";
        break;
      case 141:
        image_name += "sand_light.png";
        break;
      case 142:
        image_name += "sand_tan.png";
        break;
      case 143:
        image_name += "sand_dark.png";
        break;
      case 144:
        image_name += "sand_dark_line_left.png";
        break;
      case 145:
        image_name += "sand_dark_line_up.png";
        break;
      case 151:
        image_name += "grass_light.png";
        break;
      case 152:
        image_name += "grass_green.png";
        break;
      case 153:
        image_name += "grass_dark.png";
        break;
      case 154:
        image_name += "grass_dead.png";
        break;
      case 155:
        image_name += "grass_green_line_left.png";
        break;
      case 156:
        image_name += "grass_green_line_up.png";
        break;
      case 161:
        image_name += "dirt_light.png";
        break;
      case 162:
        image_name += "dirt_gray.png";
        break;
      case 163:
        image_name += "dirt_dark.png";
        break;
      case 164:
        image_name += "tilled_dirt.png";
        break;
      case 165:
        image_name += "tilled_dirt_watered.png";
        break;
      case 170:
        image_name += "road_asphalt_white_left.png";
        break;
      case 171:
        image_name += "road_light.png";
        break;
      case 172:
        image_name += "road_asphalt.png";
        break;
      case 173:
        image_name += "road_dark.png";
        break;
      case 174:
        image_name += "road_light_left.png";
        break;
      case 175:
        image_name += "road_light_up.png";
        break;
      case 176:
        image_name += "road_asphalt_left.png";
        break;
      case 177:
        image_name += "road_asphalt_up.png";
        break;
      case 178:
        image_name += "road_asphalt_left_double.png";
        break;
      case 179:
        image_name += "road_asphalt_up_double.png";
        break;
      case 180:
        image_name += "road_asphalt_white_up.png";
        break;
      case 181:
        image_name += "water_rocks.png";
        break;
      case 182:
        image_name += "water_dirt.png";
        break;
      case 183:
        image_name += "water_shallow.png";
        break;
      case 184:
        image_name += "water_medium.png";
        break;
      case 185:
        image_name += "water_deep.png";
        break;
      case 191:
        int lava_frame = (int)(LNZ.gif_lava_frames * ((this.timer_square + p.millis()) %
          LNZ.gif_lava_time) / LNZ.gif_lava_time);
        image_name = "gifs/lava/" + lava_frame + ".png";
        break;
      case 201:
        image_name += "brickWall_blue.png";
        break;
      case 202:
        image_name += "brickWall_gray.png";
        break;
      case 203:
        image_name += "brickWall_green.png";
        break;
      case 204:
        image_name += "brickWall_pink.png";
        break;
      case 205:
        image_name += "brickWall_red.png";
        break;
      case 206:
        image_name += "brickWall_yellow.png";
        break;
      case 207:
        image_name += "brickWall_white.png";
        break;
      case 208:
        image_name += "brickWall_brown.png";
        break;
      case 211:
        image_name += "woodWall_light.png";
        break;
      case 212:
        image_name += "woodWall_brown.png";
        break;
      case 213:
        image_name += "woodWall_dark.png";
        break;
      case 301:
        image_name += "stairs_gray_up.png";
        break;
      case 302:
        image_name += "stairs_gray_down.png";
        break;
      case 303:
        image_name += "stairs_gray_left.png";
        break;
      case 304:
        image_name += "stairs_gray_right.png";
        break;
      case 305:
        image_name += "stairs_green_up.png";
        break;
      case 306:
        image_name += "stairs_green_down.png";
        break;
      case 307:
        image_name += "stairs_green_left.png";
        break;
      case 308:
        image_name += "stairs_green_right.png";
        break;
      case 309:
        image_name += "stairs_red_up.png";
        break;
      case 310:
        image_name += "stairs_red_down.png";
        break;
      case 311:
        image_name += "stairs_red_left.png";
        break;
      case 312:
        image_name += "stairs_red_right.png";
        break;
      case 313:
        image_name += "stairs_sidewalk_up.png";
        break;
      case 314:
        image_name += "stairs_sidewalk_down.png";
        break;
      case 315:
        image_name += "stairs_sidewalk_left.png";
        break;
      case 316:
        image_name += "stairs_sidewalk_right.png";
        break;
      case 317:
        image_name += "stairs_brown_up.png";
        break;
      case 318:
        image_name += "stairs_brown_down.png";
        break;
      case 319:
        image_name += "stairs_brown_left.png";
        break;
      case 320:
        image_name += "stairs_brown_right.png";
        break;
      case 321:
        image_name += "stairs_concrete_up.png";
        break;
      case 322:
        image_name += "stairs_concrete_down.png";
        break;
      case 323:
        image_name += "stairs_concrete_left.png";
        break;
      case 324:
        image_name += "stairs_concrete_right.png";
        break;
      case 401:
        image_name += "slab_brick_brown1.png";
        break;
      case 402:
        image_name += "slab_dirt1.png";
        break;
      case 501:
        image_name += "slab_brick_brown2.png";
        break;
      case 502:
        image_name += "slab_dirt2.png";
        break;
      case 601:
        image_name += "slab_brick_brown3.png";
        break;
      case 602:
        image_name += "slab_dirt3.png";
        break;
      case 701:
        image_name += "slab_brick_brown4.png";
        break;
      case 702:
        image_name += "slab_dirt4.png";
        break;
      case 801:
        image_name += "slab_brick_brown5.png";
        break;
      case 802:
        image_name += "slab_dirt5.png";
        break;

      default:
        image_name += "default.png";
        break;
    }
    return image_name;
  }

  PImage terrainImage() {
    if (this.terrainImageHeightOverflow() > 1) {
      p.global.errorMessage("ERROR: Need to check player view when " +
        "drawing images with height overflow > 1");
    }
    return this.terrainImage(false);
  }
  PImage terrainImage(boolean blocks_player_view) {
    this.blocking_view = blocks_player_view;
    String path = this.getImagePath();
    if (blocks_player_view) {
      return p.global.images.getTransparentImage(path);
    }
    return p.global.images.getImage(path);
  }

  PImage terrainImageScaled(int scaled_width) {
    if (this.terrainImageHeightOverflow() > 1) {
      p.global.errorMessage("ERROR: Need to check player view when " +
        "drawing images with height overflow > 1");
    }
    return this.terrainImageScaled(false, scaled_width);
  }
  PImage terrainImageScaled(boolean blocks_player_view, int scaled_width) {
    this.blocking_view = blocks_player_view;
    String path = this.getImagePath();
    if (blocks_player_view) {
      return p.global.images.getScaledTransparentImage(path, scaled_width);
    }
    return p.global.images.getScaledImage(path, scaled_width);
  }

  PImage defaultImage() {
    String image_name = "terrain/";
    switch(this.terrain_id) {
      default:
        image_name += "default.png";
        break;
    }
    return p.global.images.getImage(image_name);
  }

  PImage slabImage(boolean blocks_player_view, int scaled_width) {
    String image_name = "terrain/";
    switch(this.terrain_id) {
      case 208: // brick wall, brown
      case 317: // stairs, brown
      case 318:
      case 319:
      case 320:
      case 401: // slab (1), brown
      case 501: // slab (2), brown
      case 601: // slab (3), brown
      case 701: // slab (4), brown
      case 801: // slab (5), brown
        image_name += "slab_brick_brown";
        break;
      case 131: // concrete
      case 132: // sidewalk
      case 133:
      case 313: // sidewalk stairs
      case 314:
      case 315:
      case 316:
      case 321: // concrete stairs
      case 322:
      case 323:
      case 324:
        image_name += "slab_concrete";
        break;
      case 151: // grass
      case 152:
      case 153:
      case 154:
      case 155:
      case 156:
      case 161: // dirt
      case 162:
      case 163:
      case 164:
      case 165:
        image_name += "slab_dirt";
        break;
      default:
        return null; // valid value
    }
    if (this.base_elevation < 1 || this.base_elevation > 5) {
      return null; // only allow base elevation up to 5 (max total elevation is 10)
    }
    image_name += this.base_elevation + ".png";
    if (blocks_player_view) {
      return p.global.images.getScaledTransparentImage(image_name, scaled_width);
    }
    return p.global.images.getScaledImage(image_name, scaled_width);
  }

  boolean imageOverflows() {
    return this.terrain_id > 200 || this.base_elevation != 0;
  }

  int terrainImageHeightOverflow() {
    return this.terrainImageHeightOverflow(null);
  }
  int terrainImageHeightOverflow(Coordinate relative_coordinate) {
    int base_overflow = 0;
    switch(this.terrain_id) {
      case 201: // Brick wall
      case 202:
      case 203:
      case 204:
      case 205:
      case 206:
      case 207:
      case 208:
        base_overflow = 6;
        break;
      case 301: // Stairs
      case 302:
      case 303:
      case 304:
      case 305:
      case 306:
      case 307:
      case 308:
      case 309:
      case 310:
      case 311:
      case 312:
      case 313:
      case 314:
      case 315:
      case 316:
      case 317:
      case 318:
      case 319:
      case 320:
      case 321:
      case 322:
      case 323:
      case 324:
        base_overflow = 3;
        break;
      case 401: // Slab (1)
      case 402:
        base_overflow = 1;
        break;
      case 501: // Slab (2)
      case 502:
        base_overflow = 2;
        break;
      case 601: // Slab (3)
      case 602:
        base_overflow = 3;
        break;
      case 701: // Slab (4)
      case 702:
        base_overflow = 4;
        break;
      case 801: // Slab (5)
      case 802:
        base_overflow = 5;
        break;
      default:
        break;
    }
    if (relative_coordinate != null) {
      base_overflow += this.adjustElevation(relative_coordinate);
    }
    return this.base_elevation + base_overflow;
  }

  String interactionTooltip(Unit u) {
    if (u == null || u.remove) {
      return "";
    }
    Item weapon = u.weapon();
    if (weapon != null && !weapon.remove && weapon.placeable() && this.canPlaceOn()) {
      return "Place";
    }
    switch(this.terrain_id) {
      case 134: // Gravel
        if (weapon == null) {
          return "";
        }
        else if (weapon.shovel()) {
          return "Shovel Gravel";
        }
        return "";
      case 151: // Grass, light
      case 152: // Grass, green
      case 153: // Grass, dark
      case 155: // Grass, green, line left
      case 156: // Grass, green, line up
        if (weapon == null) {
          return "";
        }
        else if (weapon.plantableAnwhere()) {
          return "Plant Seed";
        }
        else if (weapon.shovel()) {
          return "Shovel Dirt";
        }
        else if (weapon.hoe()) {
          return "Scythe Grass";
        }
        return "";
      case 154: // Grass, dead
        if (weapon == null) {
          return "";
        }
        else if (weapon.plantableAnwhere()) {
          return "Plant Seed";
        }
        else if (weapon.shovel()) {
          return "Shovel Dirt";
        }
        else if (weapon.hoe()) {
          return "Scythe Hay";
        }
        return "";
      case 161: // Dirt, light
      case 162: // Dirt, gray
      case 163: // Dirt, dark
        if (weapon == null) {
          return "";
        }
        else if (weapon.plantableAnwhere()) {
          return "Plant Seed";
        }
        else if (weapon.shovel()) {
          return "Shovel Dirt";
        }
        else if (weapon.hoe()) {
          return "Hoe Ground";
        }
        return "";
      case 164: // Tilled Dirt
        if (weapon == null) {
          return "";
        }
        else if (weapon.plantable()) {
          if (weapon.sapling()) {
            return "Plant Sapling";
          }
          return "Plant Seed";
        }
        else if (weapon.waterBottle()) {
          return "Water Ground";
        }
        return "";
      case 165: // Tilled Dirt, watered
        if (weapon == null) {
          return "";
        }
        else if (weapon.plantable()) {
          if (weapon.sapling()) {
            return "Plant Sapling";
          }
          return "Plant Seed";
        }
        else if (weapon.waterBottle()) {
          return "Water Ground";
        }
        return "";
      case 181: // Water, rocks
      case 182: // Water, dirt
      case 183: // Water, shallow
      case 184: // Water, medium
      case 185: // Water, deep
        return "Drink";
      default:
        return "";
    }
  }

  int interactionTime(AbstractGameMap map, Unit u, boolean play_sound) {
    if (u == null || u.remove) {
      return 0;
    }
    Item weapon = u.weapon();
    String sound_path = "";
    int interaction_time = 0;
    if (weapon != null && !weapon.remove && weapon.placeable() && this.canPlaceOn()) {
      if (play_sound) {
        p.global.sounds.trigger_units("player/place" + Misc.randomInt(1, 6), u.coordinate.subtractR(map.view));
      }
      return 0;
    }
    switch(this.terrain_id) {
      case 134: // Gravel
        if (weapon == null) {
          break;
        }
        else if (weapon.shovel()) {
          interaction_time = 900;
          sound_path = "items/shovel_gravel";
        }
        break;
      case 151: // Grass, light
      case 152: // Grass, green
      case 153: // Grass, dark
      case 154: // Grass, dead
      case 155: // Grass, green, line left
      case 156: // Grass, green, line up
        if (weapon == null) {
          break;
        }
        else if (weapon.plantableAnwhere()) {
          interaction_time = 0;
          sound_path = "features/plant" + Misc.randomInt(1, 2);
        }
        else if (weapon.shovel()) {
          interaction_time = 750;
          sound_path = "items/shovel_dirt";
        }
        else if (weapon.hoe()) {
          interaction_time = 0;
          sound_path = "items/hoe" + Misc.randomInt(1, 4);
        }
        break;
      case 161: // Dirt, light
      case 162: // Dirt, gray
      case 163: // Dirt, dark
        if (weapon == null) {
          break;
        }
        else if (weapon.plantableAnwhere()) {
          interaction_time = 0;
          sound_path = "features/plant" + Misc.randomInt(1, 2);
        }
        else if (weapon.shovel()) {
          interaction_time = 750;
          sound_path = "items/shovel_dirt";
        }
        else if (weapon.hoe()) {
          interaction_time = 0;
          sound_path = "items/hoe" + Misc.randomInt(1, 4);
        }
        break;
      case 164: // Tilled Dirt
        if (weapon == null) {
          break;
        }
        else if (weapon.plantable()) {
          interaction_time = 0;
          sound_path = "features/plant" + Misc.randomInt(1, 2);
        }
        else if (weapon.waterBottle() && weapon.ammo >= 20) {
          interaction_time = 500;
          sound_path = "items/water_plant";
        }
        break;
      case 165: // Tilled Dirt, watered
        if (weapon == null) {
          break;
        }
        else if (weapon.plantable()) {
          interaction_time = 0;
          sound_path = "features/plant" + Misc.randomInt(1, 2);
        }
        else if (weapon.waterBottle() && weapon.ammo >= 20) {
          interaction_time = 500;
          sound_path = "items/water_plant";
        }
        break;
      case 181: // Water, rocks
      case 182: // Water, dirt
      case 183: // Water, shallow
      case 184: // Water, medium
      case 185: // Water, deep
        interaction_time = 1500;
        sound_path = "player/drink";
        break;
      default:
        break;
    }
    if (play_sound && !sound_path.equals("")) {
      p.global.sounds.trigger_units(sound_path, u.coordinate.subtractR(map.view));
    }
    return interaction_time;
  }

  void interact(AbstractGameMap map, Unit u, IntegerCoordinate coordinate) {
    this.interact(map, u, coordinate.x, coordinate.y);
  }
  void interact(AbstractGameMap map, Unit u, int x, int y) {
    if (u == null || u.remove) {
      return;
    }
    Item weapon = u.weapon();
    if (weapon != null && !weapon.remove && weapon.placeable() && this.canPlaceOn()) {
      u.face(x + 0.5, y + 0.5);
      int feature_id = weapon.placeableFeatureId(u);
      x += Feature.featurePlacingOffsetX(feature_id);
      y += Feature.featurePlacingOffsetY(feature_id);
      Feature new_feature = new Feature(p, feature_id, x, y);
      if (map.featureCanBePlaced(new_feature)) {
        map.addFeature(new_feature);
        weapon.removeStack();
        return;
      }
    }
    switch(this.terrain_id) {
      case 134: // Gravel
        if (weapon == null) {
          break;
        }
        else if (weapon.shovel()) {
          weapon.lowerDurability(3);
          for (int i = 0; i < 5; i++) {
            if (Misc.randomChance(0.5)) {
              map.addItem(new Item(p, 2931, x + 0.2 + Misc.randomDouble(0.6), y + 0.2 + Misc.randomDouble(0.6)));
            }
            else {
              map.addItem(new Item(p, 2933, x + 0.2 + Misc.randomDouble(0.6), y + 0.2 + Misc.randomDouble(0.6)));
            }
          }
          map.setTerrain(162, x, y);
        }
        break;
      case 151: // Grass, light
      case 152: // Grass, green
      case 153: // Grass, dark
      case 154: // Grass, dead
      case 155: // Grass, green, line left
      case 156: // Grass, green, line up
        if (weapon == null) {
          break;
        }
        else if (weapon.plantableAnwhere()) {
          u.plantSeed(map, x, y);
        }
        else if (weapon.shovel()) {
          weapon.lowerDurability();
          map.setTerrain(162, x, y);
          if (Misc.randomChance(0.05)) {
            map.addItem(new Item(p, map.getBiomeAt(x, y).scytheGrassItemId(),
              x + 0.2 + Misc.randomDouble(0.6), y + 0.2 + Misc.randomDouble(0.6)));
          }
          // less chance of dirt / worms than with dirt
        }
        else if (weapon.hoe()) {
          weapon.lowerDurability();
          map.setTerrain(162, x, y);
          if (Misc.randomChance(0.25)) {
            map.addItem(new Item(p, map.getBiomeAt(x, y).scytheGrassItemId(),
              x + 0.2 + Misc.randomDouble(0.6), y + 0.2 + Misc.randomDouble(0.6)));
          }
        }
        break;
      case 161: // Dirt, light
      case 162: // Dirt, gray
      case 163: // Dirt, dark
        if (weapon == null) {
          break;
        }
        else if (weapon.plantableAnwhere()) {
          u.plantSeed(map, x, y);
        }
        else if (weapon.shovel()) {
          weapon.lowerDurability();
          // add dirt (worms) on ground
          // clay if dark
          // no worms if light
        }
        else if (weapon.hoe()) {
          weapon.lowerDurability();
          map.setTerrain(164, x, y);
        }
        break;
      case 164: // Tilled Dirt
        if (weapon == null) {
          break;
        }
        else if (weapon.plantable()) {
          u.plantSeed(map, x, y);
        }
        else if (weapon.waterBottle()) {
          if (weapon.ammo >= 20) {
            map.waterGround(x, y);
            weapon.ammo -= 20;
          }
          else if (!u.ai_controlled) {
            map.addHeaderMessage("Not enough water to water ground.");
          }
        }
        break;
      case 165: // Tilled Dirt, watered
        if (weapon == null) {
          break;
        }
        else if (weapon.plantable()) {
          u.plantSeed(map, x, y);
        }
        else if (weapon.waterBottle()) {
          if (weapon.ammo >= 20) {
            map.waterGround(x, y);
            weapon.ammo -= 20;
          }
          else if (!u.ai_controlled) {
            map.addHeaderMessage("Not enough water to water ground.");
          }
        }
        break;
      case 181: // Water, rocks
      case 182: // Water, dirt
      case 183: // Water, shallow
      case 184: // Water, medium
      case 185: // Water, deep
        if (Hero.class.isInstance(u)) {
          ((Hero)u).increaseThirst(20);
        }
        break;
      default:
        break;
    }
  }
}
