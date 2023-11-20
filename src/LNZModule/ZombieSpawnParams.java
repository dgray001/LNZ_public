package LNZModule;

import Misc.Misc;

class ZombieSpawnParams {
  private LNZ p;
  protected double max_zombies_per_square = 0.04;
  protected int min_level = 0;
  protected int max_level = 100;
  protected int del_level = 3;
  protected int group_size = 3;
  protected int del_group_size = 2;
  protected double group_radius = 2.5;
  protected double min_distance = 7;
  protected double max_distance = 45;
  protected boolean save_params = false;
  protected int try_spawn_timer = 50;

  ZombieSpawnParams(LNZ sketch) {
    this.p = sketch;
  }

  int getLevel(int player_level) {
    int level = player_level - this.del_level + Misc.randomInt(1 + 2 * this.del_level);
    if (level < this.min_level) {
      level = this.min_level;
    }
    if (level > this.max_level) {
      level = this.max_level;
    }
    return level;
  }

  boolean badSpawnSpace(double x, double y, AbstractGameMap map) {
    /*if (GameMapArea.class.isInstance(map)) {
      GameMapArea area_map = (GameMapArea)map;
      Biome biome = area_map.getBiomeAt(x, y);
      if (!Biome.allowZombieSpawn(biome)) {
        return true;
      }
    }*/
    if ((int)x < map.currMapXI() || (int)x >= map.currMapXF() ||
      (int)y < map.currMapYI() || (int)y >= map.currMapYF()) {
      return true;
    }
    GameMapSquare square = map.mapSquare((int)x, (int)y);
    if (square == null || square.isWall() || square.feature_elevation > 4 || square.light_level > 5) {
      return true;
    }
    return false;
  }

  int getZombieID() {
    if (Misc.randomChance(0.8)) {
      return 1291;
    }
    if (Misc.randomChance(0.5)) {
      return 1292;
    }
    else {
      return 1293;
    }
  }

  void update(int time_elapsed, Level level) {
    if (level.curr_map == null || !level.curr_map.outside_map || level.player == null) {
      return;
    }
    this.try_spawn_timer -= time_elapsed;
    if (this.try_spawn_timer > 0) {
      return;
    }
    this.try_spawn_timer = 50;
    if (level.curr_map.zombie_counter > LNZ.map_maxUnits) {
      return;
    }
    if (level.curr_map.zombie_counter > this.max_zombies_per_square * level.curr_map.currWidth() * level.curr_map.currHeight()) {
      return;
    }
    double x_facing = Misc.randomDouble(-1.0, 1.0);
    double y_facing = 1.0 - Math.abs(x_facing);
    if (Misc.randomChance(0.5)) {
      y_facing = -y_facing;
    }
    double distance = Misc.randomDouble(this.min_distance, this.max_distance);
    double x = level.player.coordinate.x + x_facing * distance;
    double y = level.player.coordinate.y + y_facing * distance;
    if (this.badSpawnSpace(x, y, level.curr_map)) {
      return;
    }
    // successful 'group spawn'
    this.try_spawn_timer = 5000;
    int source_id = this.getZombieID();
    Unit zambo = new Unit(p, source_id);
    zambo.setLevel(this.getLevel(level.player.level));
    level.curr_map.addUnit(zambo, x, y);
    int group_size = this.group_size - this.del_group_size + Misc.randomInt(1 + 2 * this.del_group_size);
    for (int i = 0; i < group_size; i++) {
      x_facing = Misc.randomDouble(-1.0, 1.0);
      y_facing = 1.0 - Math.abs(x_facing);
      if (Misc.randomChance(0.5)) {
        y_facing = -y_facing;
      }
      distance = Misc.randomDouble(this.group_radius);
      double x_group = x + x_facing * distance;
      double y_group = y + y_facing * distance;
      if (this.badSpawnSpace(x_group, y_group, level.curr_map)) {
        continue;
      }
      if (Misc.randomChance(0.5)) {
        zambo = new Unit(p, source_id);
      }
      else {
        zambo = new Unit(p, this.getZombieID());
      }
      zambo.setLevel(this.getLevel(level.player.level));
      level.curr_map.addUnit(zambo, x_group, y_group);
    }
  }
}