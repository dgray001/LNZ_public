package LNZModule;

import java.util.*;
import processing.core.*;
import Misc.Misc;

class Projectile extends MapObject {
  protected double size = LNZ.projectile_defaultSize; // radius

  protected int source_key;
  protected int target_key = -1; // if > -1 is homing
  protected Coordinate facing = new Coordinate(1, 0);
  protected double facingA = 0;
  protected Alliance alliance = Alliance.NONE;

  protected double power = 0;
  protected double piercing = 0;
  protected double penetration = 0;
  protected Element element = Element.GRAY;
  protected DamageType damageType = DamageType.PHYSICAL;
  protected boolean toggled = false; // various uses

  protected double speed = 0;
  protected double decay = 0;
  protected double range_left = 0;
  protected boolean friendly_fire = false;
  protected boolean waiting_to_explode = false;

  Projectile(LNZ sketch, int ID) {
    super(sketch, ID);
    this.setID();
  }
  Projectile(LNZ sketch, int ID, Unit u) {
    this(sketch, ID, u, 0);
  }
  Projectile(LNZ sketch, int ID, Unit u, double inaccuracy) {
    super(sketch, ID);
    this.source_key = u.map_key;
    this.setID();
    if (u != null) {
      this.coordinate = u.front();
      this.curr_height = u.zHalf();
      this.facing = u.facing.copy();
      this.facingA = u.facingA;
      this.alliance = u.alliance;
      switch(ID) {
        case 3001: // mighty Pen
          if (u.holding(2911, 2912)) {
            this.power = LNZ.ability_102_powerBasePen + u.power(LNZ.
              ability_102_powerRatioPen, LNZ.ability_102_powerRatioPen);
            u.pickup(null);
          }
          else {
            this.power = LNZ.ability_102_powerBase + u.power(LNZ.
              ability_102_powerRatio, LNZ.ability_102_powerRatio);
          }
          this.damageType = DamageType.MIXED;
          break;
        case 3002: // Mighty Pen II
          if (u.holding(2911, 2912)) {
            this.power = LNZ.ability_107_powerBasePen + u.power(LNZ.
              ability_107_powerRatioPen, LNZ.ability_107_powerRatioPen);
            u.pickup(null);
          }
          else {
            this.power = LNZ.ability_107_powerBase + u.power(LNZ.
              ability_107_powerRatio, LNZ.ability_107_powerRatio);
          }
          this.damageType = DamageType.MIXED;
          break;
        case 3003: // Condom Throw
          this.power = LNZ.ability_1002_basePower + u.power(0, LNZ.ability_1002_magicRatio);
          this.damageType = DamageType.MAGICAL;
          break;
        case 3004: // Rock Throw
          this.power = LNZ.ability_1022_powerBase + u.power(LNZ.ability_1022_powerRatio, 0);
          break;
        case 3005: // auto attack
          this.size = LNZ.projectile_autoAttackSize;
          this.power = u.autoAttackPower();
          this.damageType = u.autoAttackDamageType();
          break;
        case 3118: // Chicken Egg (thrown)
          if (u.holding(2118)) {
            this.toggled = u.weapon().toggled;
          }
          this.power = u.attack();
          break;
        case 3372: // Ray Gun
        case 3392: // Porter's X2 Ray Gun
          this.power = u.attack() + u.magic();
          this.damageType = DamageType.MIXED;
          break;
        default:
          this.power = u.attack();
          if (u.aposematicCamouflage()) {
            this.power *= LNZ.ability_111_powerBuff;
            u.removeStatusEffect(StatusEffectCode.APOSEMATIC_CAMOUFLAGE);
          }
          if (u.aposematicCamouflageII()) {
            this.power *= LNZ.ability_116_powerBuff;
            u.removeStatusEffect(StatusEffectCode.APOSEMATIC_CAMOUFLAGEII);
          }
          break;
      }
      this.piercing = u.piercing();
      this.penetration = u.penetration();
      this.turn(inaccuracy - 2 * Misc.randomDouble(inaccuracy));
      switch(ID) {
        case 3001: // Mighty Pen
          this.range_left = LNZ.ability_102_distance;
          break;
        case 3002: // Mighty Pen II
          this.range_left = LNZ.ability_107_distance;
          break;
        case 3003: // Condom Throw
          this.range_left = LNZ.ability_1002_range;
          break;
        case 3004: // Rock Throw
          this.range_left = LNZ.ability_1022_distance;
          break;
        case 3005: // auto attack
          this.range_left = u.attackRange() + 2; // can "dodge" auto attack by moving 2 spaces
          break;
        default:
          this.range_left = 1.1 * u.attackRange();
          break;
      }
    }
  }

  static String projectileName(int ID) {
    return (new Projectile(null, ID)).displayName();
  }

  void setID() {
    switch(ID) {
      // abilities
      case 3001: // Mighty Pen
        this.display_name = "Mighty Pen";
        this.speed = 9;
        this.decay = 0;
        break;
      case 3002: // Mighty Pen II
      this.display_name = "Mighty Pen II";
        this.speed = 9;
        this.decay = 0;
        break;
      case 3003: // Condom Throw
      this.display_name = "Condom";
        this.speed = 5;
        this.decay = 0;
        break;
      case 3004: // Rock Throw
      this.display_name = "Rock";
        this.speed = 6;
        this.decay = 0;
        break;
      // Auto Attack
      case 3005: // auto attack
      this.display_name = "Auto Attack";
        this.speed = LNZ.unit_defaultRangedAutoAttackSpeed;
        this.decay = 0;
        break;
      // Items
      case 3118: // Chicken Egg (thrown)
      this.display_name = "Chicken Egg";
        this.speed = 5;
        this.decay = 0.25;
        break;
      case 3301: // Slingshot
      this.display_name = "Rock from Slingshot";
        this.speed = 8;
        this.decay = 0.4267;
        break;
      case 3311: // Bow
        this.display_name = "Arrow from Bow";
        this.speed = 12;
        this.decay = 0.3656;
        break;
      case 3312: // M1911
        this.display_name = "M1911";
        this.speed = 90;
        this.decay = 0;
        break;
      case 3321: // War Machine
        this.display_name = "War Machine";
        this.speed = 18;
        this.decay = 0;
        break;
      case 3322: // Five-Seven
        this.display_name = "Five-Seven";
        this.speed = 90;
        this.decay = 1.709;
        break;
      case 3323: // Type25
        this.display_name = "Type25";
        this.speed = 90;
        this.decay = 1.157;
        break;
      case 3331: // Mustang and Sally
        this.display_name = "Mustang and Sally";
        this.speed = 60;
        this.decay = 0;
        break;
      case 3332: // FAL
        this.display_name = "FAL";
        this.speed = 100;
        this.decay = 0.6797;
        break;
      case 3333: // Python
        this.display_name = "Python";
        this.speed = 100;
        this.decay = 2.6;
        break;
      case 3341: // RPG
        this.display_name = "RPG";
        this.speed = 30;
        this.decay = 0;
        break;
      case 3342: // Dystopic Demolisher
        this.display_name = "Dystopic Demolisher";
        this.speed = 30;
        this.decay = 0;
        break;
      case 3343: // Ultra
        this.display_name = "Ultra";
        this.speed = 90;
        this.decay = 1.5;
        break;
      case 3344: // Strain25
        this.display_name = "Strain25";
        this.speed = 90;
        this.decay = 0.9888;
        break;
      case 3345: // Executioner
        this.display_name = "Executioner";
        this.speed = 90;
        this.decay = 4.6251;
        break;
      case 3351: // Galil
        this.display_name = "Galil";
        this.speed = 90;
        this.decay = 1.0417;
        break;
      case 3352: // WN
        this.display_name = "WN";
        this.speed = 100;
        this.decay = 0.7954;
        break;
      case 3353: // Ballistic Knife
        this.display_name = "Ballistic Knife";
        this.speed = 40;
        this.decay = 0;
        break;
      case 3354: // Cobra
        this.display_name = "Cobra";
        this.speed = 100;
        this.decay = 1.8286;
        break;
      case 3355: // MTAR
        this.display_name = "MTAR";
        this.speed = 90;
        this.decay = 0.8345;
        break;
      case 3361: // RPD
        this.display_name = "RPD";
        this.speed = 90;
        this.decay = 0.8905;
        break;
      case 3362: // Rocket-Propelled Grievance
        this.display_name = "Rocket-Propelled Grievance";
        this.speed = 90;
        this.decay = 0;
        break;
      case 3363: // DSR-50
        this.display_name = "DSR-50";
        this.speed = 90;
        this.decay = 0.6592;
        break;
      case 3364: // Voice of Justice
        this.display_name = "Voice of Justice";
        this.speed = 90;
        this.decay = 3.9339;
        break;
      case 3371: // HAMR
        this.display_name = "HAMR";
        this.speed = 90;
        this.decay = 1.2022;
        break;
      case 3372: // Ray Gun
        this.display_name = "Ray Gun";
        this.speed = 90;
        this.decay = 0;
        break;
      case 3373: // Lamentation
        this.display_name = "Lamentation";
        this.speed = 90;
        this.decay = 0.973;
        break;
      case 3374: // The Krauss Refibrillator
        this.display_name = "The Krauss Refibrillator";
        this.speed = 90;
        this.decay = 0;
        break;
      case 3375: // Malevolent Taxonomic Anodized Redeemer
        this.display_name = "Malevolent Taxonomic Anodized Redeemer";
        this.speed = 90;
        this.decay = 0.8163;
        break;
      case 3381: // Relativistic Punishment Device
        this.display_name = "Relativistic Punishment Device";
        this.speed = 90;
        this.decay = 0.6584;
        break;
      case 3382: // Dead Specimen Reactor 5000
        this.display_name = "Dead Specimen Reactor 5000";
        this.speed = 90;
        this.decay = 0;
        break;
      case 3391: // SLDG HAMR
        this.display_name = "SLDG HAMR";
        this.speed = 90;
        this.decay = 0.768;
        break;
      case 3392: // Porter's X2 Ray Gun
        this.display_name = "Porter's X2 Ray Gun";
        this.speed = 90;
        this.decay = 0;
        break;
      case 3924: // Glass Bottle (thrown)
        this.display_name = "Glass Bottle";
        this.speed = 4;
        this.decay = 0.3;
        break;
      case 3931: // Rock (thrown)
        this.display_name = "Rock";
        this.speed = 5;
        this.decay = 0.3;
        break;
      case 3932: // Arrow (thrown)
        this.display_name = "Arrow";
        this.speed = 4;
        this.decay = 0.3;
        break;
      case 3933: // Pebble (thrown)
        this.display_name = "Pebble";
        this.speed = 5;
        this.decay = 0.3;
        break;
      case 3944: // Grenade (thrown)
        this.display_name = "Grenade";
        this.speed = 6;
        this.decay = 0.3;
        break;
      default:
        p.global.errorMessage("ERROR: Projectile ID " + ID + " not found.");
        break;
    }
  }

  void setPower(int source_key, double power, double piercing, double penetration) {
    this.power = power;
    this.piercing = piercing;
    this.penetration = penetration;
  }

  void face(MapObject object) {
    this.face(object.xCenter(), object.yCenter());
  }
  void face(double faceX, double faceY) {
    this.setFacing(faceX - this.coordinate.x, faceY - this.coordinate.y);
  }
  void face(Coordinate face) {
    this.setFacing(face.subtractR(this.coordinate));
  }
  void setFacing(double facingX, double facingY) {
    this.setFacing(new Coordinate(facingX, facingY));
  }
  void setFacing(Coordinate new_facing) {
    double normConstant = new_facing.distance();
    if (normConstant == 0.0) {
      return; // happens when exactly on target location
    }
    this.facing = new_facing.divideR(normConstant);
    this.facingA = Math.atan2(this.facing.y, this.facing.x);
  }

  void turn(double angle_change) {
    this.turnTo(this.facingA + angle_change);
  }
  void turnTo(double facingA) {
    this.facingA = facingA;
    this.facing.x = Math.cos(this.facingA);
    this.facing.y = Math.sin(this.facingA);
  }

  void refreshFacing() {
    this.facingA = Math.atan2(this.facing.y, this.facing.x);
  }

  String displayName() {
    return this.display_name;
  }
  String type() {
    return this.type;
  }
  String description() {
    return this.description;
  }
  String selectedObjectTextboxText() {
    String text = "-- " + this.type() + " --";
    return text + "\n\n" + this.description();
  }

  void setLocation(double x, double y) {
    this.coordinate = new Coordinate(x, y);
  }
  void setLocation(Coordinate coordinate) {
    this.coordinate = coordinate.copy();
  }

  double xi() {
    return this.coordinate.x - this.size;
  }
  double yi() {
    return this.coordinate.y - this.size;
  }
  double xf() {
    return this.coordinate.x + this.size;
  }
  double yf() {
    return this.coordinate.y + this.size;
  }
  double xCenter() {
    return this.coordinate.x;
  }
  double yCenter() {
    return this.coordinate.y;
  }
  double width() {
    return 2 * this.size;
  }
  double height() {
    return 2 * this.size;
  }
  double xRadius() {
    return this.size;
  }
  double yRadius() {
    return this.size;
  }
  double zi() {
    return this.curr_height;
  }
  double zf() {
    return this.curr_height;
  }
  double zHalf() {
    return this.curr_height;
  }

  @Override // center has to touch object
  double distance(MapObject object) {
    double xDistance = Math.max(0, Math.abs(this.xCenter() - object.xCenter()) - object.xRadius());
    double yDistance = Math.max(0, Math.abs(this.yCenter() - object.yCenter()) - object.yRadius());
    return Math.sqrt(xDistance * xDistance + yDistance * yDistance);
  }

  @Override // center has to touch object
  boolean touching(MapObject object) {
    if ( ((Math.abs(this.xCenter() - object.xCenter()) - object.xRadius()) <= 0) ||
      ((Math.abs(this.yCenter() - object.yCenter()) - object.yRadius()) <= 0) ) {
        return true;
    }
    return false;
  }

  PImage getImage() {
    String path = "projectiles/";
    switch(this.ID) {
      case 3001:
      case 3002:
        path += "pen.png";
        break;
      case 3003:
        path += "condom.png";
        break;
      case 3004:
        path += "rock.png";
        break;
      case 3005:
        path += "auto_attack.png";
        break;
      case 3118:
        path += "chicken_egg.png";
        break;
      case 3301:
        path += "rock.png";
        break;
      case 3311:
        path += "arrow.png";
        break;
      case 3312:
        path += "45_acp.png";
        break;
      case 3321:
        path += "grenade.png";
        break;
      case 3322:
        path += "fn_57_28mm.png";
        break;
      case 3323:
        path += "556_45mm.png";
        break;
      case 3331:
        path += "grenade.png";
        break;
      case 3332:
        path += "762_39mm.png";
        break;
      case 3333:
        path += "357_magnum.png";
        break;
      case 3341:
        path += "grenade.png";
        break;
      case 3342:
        path += "grenade.png";
        break;
      case 3343:
        path += "fn_57_28mm.png";
        break;
      case 3344:
        path += "556_45mm.png";
        break;
      case 3345:
        path += "28_gauge.png";
        break;
      case 3351:
        path += "556_45mm.png";
        break;
      case 3352:
        path += "762_39mm.png";
        break;
      case 3353:
        path += "ballistic_knife.png";
        break;
      case 3354:
        path += "357_magnum.png";
        break;
      case 3355:
        path += "556_45mm.png";
        break;
      case 3361:
        path += "762_39mm.png";
        break;
      case 3362:
        path += "grenade.png";
        break;
      case 3363:
        path += "50_bmg.png";
        break;
      case 3364:
        path += "28_gauge.png";
        break;
      case 3371:
        path += "762_39mm.png";
        break;
      case 3372:
        path += "ray.png";
        break;
      case 3373:
        path += "762_39mm.png";
        break;
      case 3374:
        path += "ballistic_knife.png";
        break;
      case 3375:
        path += "762_39mm.png";
        break;
      case 3381:
        path += "762_39mm.png";
        break;
      case 3382:
        path += "50_bmg.png";
        break;
      case 3391:
        path += "762_39mm.png";
        break;
      case 3392:
        path += "ray.png";
        break;
      case 3924:
        path += "glass_bottle.png";
        break;
      case 3931:
        path += "rock.png";
        break;
      case 3932:
        path += "arrow.png";
        break;
      case 3933:
        path += "pebble.png";
        break;
      case 3944:
        path += "grenade.png";
        break;
      default:
        p.global.errorMessage("ERROR: Projectile ID " + ID + " not found.");
        path += "default.png";
        break;
    }
    return p.global.images.getImage(path);
  }


  boolean targetable(Unit u) {
    return false;
  }


  void update(int timeElapsed, AbstractGameMap map) {
    if (this.remove) {
      return;
    }
    this.update(timeElapsed);
    if (this.waiting_to_explode) {
      this.range_left -= timeElapsed;
      if (this.range_left < 0) {
        this.explode(map);
      }
      return;
    }
    if (this.target_key > -1) {
      Unit u = map.units.get(this.target_key);
      if (u == null || u.remove) {
        this.remove = true;
        return;
      }
      this.face(u);
    }
    double distance_moved = this.speed * timeElapsed / 1000.0;
    Coordinate try_move = this.facing.multiplyR(distance_moved);
    // move in x direction
    if (!this.moveX(try_move.x, map)) {
      // move in y direction
      this.moveY(try_move.y, map);
    }
    // decay
    double decay_percentage = 1 - this.decay * timeElapsed / 1000.0;
    if (decay_percentage < 0) {
      this.speed = 0;
      this.power = 0;
      this.piercing = 0;
      this.penetration = 0;
    }
    else {
      this.speed *= decay_percentage;
      this.power *= decay_percentage;
      this.piercing *= decay_percentage;
      this.penetration *= decay_percentage;
    }
    if (this.speed < this.threshholdSpeed()) {
      this.dropOnGround(map);
      this.dropSound(map);
    }
    this.range_left -= distance_moved;
    if (this.range_left < 0) {
      this.dropOnGround(map);
      this.dropSound(map);
    }
  }

  double threshholdSpeed() {
    switch(this.ID) {
      case 3005: // auto attack
        return 0;
      default:
        return LNZ.projectile_threshholdSpeed;
    }
  }


  void update(int timeElapsed) {}


  // returns true if collision occurs
  boolean moveX(double tryMoveX, AbstractGameMap map) {
    while(Math.abs(tryMoveX) > LNZ.map_moveLogicCap) {
      if (tryMoveX > 0) {
        if (this.collisionLogicX(LNZ.map_moveLogicCap, map)) {
          return true;
        }
        tryMoveX -= LNZ.map_moveLogicCap;
      }
      else {
        if (this.collisionLogicX(-LNZ.map_moveLogicCap, map)) {
          return true;
        }
        tryMoveX += LNZ.map_moveLogicCap;
      }
    }
    if (this.collisionLogicX(tryMoveX, map)) {
      return true;
    }
    return false;
  }

  // returns true if collision occurs
  boolean moveY(double tryMoveY, AbstractGameMap map) {
    while(Math.abs(tryMoveY) > LNZ.map_moveLogicCap) {
      if (tryMoveY > 0) {
        if (this.collisionLogicY(LNZ.map_moveLogicCap, map)) {
          return true;
        }
        tryMoveY -= LNZ.map_moveLogicCap;
      }
      else {
        if (this.collisionLogicY(-LNZ.map_moveLogicCap, map)) {
          return true;
        }
        tryMoveY += LNZ.map_moveLogicCap;
      }
    }
    if (this.collisionLogicY(tryMoveY, map)) {
      return true;
    }
    return false;
  }

  // returns true if collision occurs
  boolean collisionLogicX(double tryMoveX, AbstractGameMap map) {
    double startX = this.coordinate.x;
    this.coordinate.x += tryMoveX;
    // map collisions
    if (!this.inMapX(map.mapXI(), map.mapXF())) {
      this.remove = true;
      return true;
    }
    // terrain collisions
    int coordinate_height = map.heightOfSquare(coordinate, this.coordinate);
    if (coordinate_height > this.curr_height) {
      this.coordinate.x = startX;
      this.dropOnGround(map);
      this.collideSound(map);
      return true;
    }
    // unit collisions
    for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
      if (entry.getKey() == this.source_key) {
        continue;
      }
      if (this.target_key > -1 && entry.getKey() != this.target_key) {
        continue;
      }
      Unit u = entry.getValue();
      if (u.alliance == this.alliance && this.alliance != Alliance.NONE && !this.friendly_fire) {
        continue;
      }
      if (this.curr_height < u.zi() && this.curr_height > u.zi()) {
        continue;
      }
      double distance_to = this.distance(u);
      if (distance_to > 0) {
        continue;
      }
      if ( (this.coordinate.x > u.coordinate.x && this.facing.x > 0) ||
        (this.coordinate.x < u.coordinate.x && this.facing.x < 0) ) {
        continue;
      }
      this.coordinate.x = startX;
      this.collideWithUnit(map, u);
      this.collideSound(map);
      return true;
    }
    return false;
  }

  // returns true if collision occurs
  boolean collisionLogicY(double tryMoveY, AbstractGameMap map) {
    double startY = this.coordinate.y;
    this.coordinate.y += tryMoveY;
    // map collisions
    if (!this.inMapY(map.mapYI(), map.mapYF())) {
      this.remove = true;
      return true;
    }
    // terrain collisions
    int coordinate_height = map.heightOfSquare(coordinate, this.coordinate);
    if (coordinate_height > this.curr_height) {
      this.coordinate.y = startY;
      this.dropOnGround(map);
      this.collideSound(map);
      return true;
    }
    // unit collisions
    for (Map.Entry<Integer, Unit> entry : map.units.entrySet()) {
      if (entry.getKey() == this.source_key) {
        continue;
      }
      if (this.target_key > -1 && entry.getKey() != this.target_key) {
        continue;
      }
      Unit u = entry.getValue();
      if (u.alliance == this.alliance && this.alliance != Alliance.NONE && !this.friendly_fire) {
        continue;
      }
      if (this.curr_height < u.zi() && this.curr_height > u.zi()) {
        continue;
      }
      double distance_to = this.distance(u);
      if (distance_to > 0) {
        continue;
      }
      if ( (this.coordinate.y > u.coordinate.y && this.facing.y > 0) ||
        (this.coordinate.y < u.coordinate.y && this.facing.y < 0) ) {
        continue;
      }
      this.coordinate.y = startY;
      this.collideWithUnit(map, u);
      this.collideSound(map);
      return true;
    }
    return false;
  }

  ArrayList<Item> droppedItems(boolean hit_unit) {
    ArrayList<Item> droppedItems = new ArrayList<Item>();
    switch(this.ID) {
      case 3001: // Mighty Pen
      case 3002: // Mighty Pen II
        droppedItems.add(new Item(p, 2911));
        break;
      case 3004: // Rock Throw
      case 3301: // Slingshot
      case 3931: // Rock
        if (!hit_unit || Misc.randomChance(0.7)) {
          droppedItems.add(new Item(p, 2931));
        }
        break;
      case 3311: // Recurve Bow
      case 3932: // Arrow
        if (!hit_unit) {
          droppedItems.add(new Item(p, 2932));
        }
        break;
      case 3353: // Ballistic Knife
      case 3374: // The Krauss Refibrillator
        droppedItems.add(new Item(p, 2203));
        break;
      case 3924: // Glass Bottle
        droppedItems.add(new Item(p, 2805));
        break;
      case 3933: // Pebble
        droppedItems.add(new Item(p, 2933));
        break;
      default:
        break;
    }
    return droppedItems;
  }

  void dropItems(AbstractGameMap map, boolean hit_unit) {
    for (Item i : this.droppedItems(hit_unit)) {
      map.addItem(i, this.coordinate);
    }
    switch(this.ID) {
      case 3118: // Chicken Egg (thrown)
        if (this.toggled) {
          Unit u = new Unit(p, 1003);
          Coordinate spawn_coordinate = this.coordinate.copy();
          spawn_coordinate.subtract(this.facing.multiplyR(u.size));
          spawn_coordinate.subtract(LNZ.small_number);
          u.setLocation(spawn_coordinate);
          u.curr_height = this.curr_height;
          map.addUnit(u);
        }
        break;
      default:
        break;
    }
  }

  void dropOnGround(AbstractGameMap map) {
    this.dropItems(map, false);
    if (this.waitsToExplode()) {
      this.startExplodeTimer(map);
    }
    else if (this.explodesOnImpact()) {
      this.explode(map);
    }
    else {
      this.remove = true;
    }
  }

  void collideWithUnit(AbstractGameMap map, Unit u) {
    this.dropItems(map, true);
    double damage = u.calculateDamageFrom(this.collidePower(), this.damageType,
      this.element, this.piercing, this.penetration);
    Unit source_unit = map.units.get(this.source_key);
    DamageSource damage_source = source_unit == null ?
      new DamageSource(2, this.ID) :
      new DamageSource(13, source_unit.ID, this.ID);
    u.damage(source_unit, damage, damage_source);
    switch(this.ID) {
      case 3001: // Mighty Pen
        u.heal(LNZ.ability_102_healRatio * damage);
        break;
      case 3002: // Mighty Pen II
        u.heal(LNZ.ability_107_healRatio * damage);
        break;
      default:
        break;
    }
    if (this.waitsToExplode()) {
      this.startExplodeTimer(map);
    }
    else if (this.explodesOnImpact()) {
      this.explode(map);
    }
    else {
      this.remove = true;
    }
  }


  void dropSound(AbstractGameMap map) {
    switch(this.ID) {
      case 3118: // Chicken Egg
        p.global.sounds.trigger_units("items/egg_crack",
          this.coordinate.subtractR(map.view));
        break;
      case 3924: // Glass Bottle
        p.global.sounds.trigger_units("items/glass_bottle_hit",
          this.coordinate.subtractR(map.view));
        break;
      default:
      // default drop sound
        break;
    }
  }


  void collideSound(AbstractGameMap map) {
    switch(this.ID) {
      case 3005: // auto attack
        p.global.sounds.trigger_units("units/attack/auto_attack_hit",
          this.coordinate.subtractR(map.view));
        break;
      case 3118: // Chicken Egg
        p.global.sounds.trigger_units("items/egg_crack",
          this.coordinate.subtractR(map.view));
        break;
      case 3311: // Recurve Bow
      case 3932: // Arrow
        p.global.sounds.trigger_units("items/recurve_bow_hit",
          this.coordinate.subtractR(map.view));
        break;
      case 3001: // Mighty Pen
      case 3002: // Mighty Pen II
      case 3312: // M1911
      case 3322: // Five-Seven
      case 3323: // Type25
      case 3332: // FAL
      case 3333: // Python
      case 3343: // Ultra
      case 3344: // Strain25
      case 3345: // Executioner
      case 3351: // Galil
      case 3352: // WN
      case 3354: // Cobra
      case 3355: // MTAR
      case 3361: // RPD
      case 3363: // DSR-50
      case 3364: // Voice of Justice
      case 3371: // HAMR
      case 3373: // Lamentation
      case 3375: // Malevolent Taxonomic Anodized Redeemer
      case 3381: // Relativistic Punishment Device
      case 3382: // Dead Specimen Reactor 5000
      case 3391: // SLDG HAMR
        p.global.sounds.trigger_units("items/bullet_hit",
          this.coordinate.subtractR(map.view));
        break;
      case 3353: // Ballistic Knife
      case 3374: // The Krauss Refibrillator
        p.global.sounds.trigger_units("items/ballistic_knife_hit",
          this.coordinate.subtractR(map.view));
        break;
      case 3004: // Rock Throw (ability)
      case 3301: // Slingshot
      case 3321: // War Machine
      case 3931: // Rock
      case 3933: // Pebble
      case 3944: // Grenade
        p.global.sounds.trigger_units("items/rock_hit",
          this.coordinate.subtractR(map.view));
        break;
      case 3924: // Glass Bottle
        p.global.sounds.trigger_units("items/glass_bottle_hit",
          this.coordinate.subtractR(map.view));
        break;
      default:
        break;
    }
  }


  double collidePower() {
    switch(this.ID) {
      case 3321: // War Machine
        return 10;
      case 3331: // Mustang and Sally
        return 18;
      case 3341: // RPG
        return 20;
      case 3342: // Dystopic Demolisher
        return 12;
      case 3362: // Rocket-Propelled Grievance
        return 24;
      case 3372: // Ray Gun
        return 1000;
      case 3392: // Porter's X2 Ray Gun
        return 1000;
      case 3944: // Grenade
        return 4;
      default:
        return this.power;
    }
  }


  boolean waitsToExplode() {
    switch(this.ID) {
      case 3321: // War Machine
      case 3944: // Grenade
        return true;
      default:
        return false;
    }
  }

  void startExplodeTimer(AbstractGameMap map) {
    this.waiting_to_explode = true;
    switch(this.ID) {
      case 3321: // War Machine
        this.range_left = 1980;
        p.global.sounds.trigger_units("items/grenade_ticking",
          this.coordinate.subtractR(map.view));
        break;
      case 3944: // Grenade
        this.range_left = 1980;
        p.global.sounds.trigger_units("items/grenade_ticking",
          this.coordinate.subtractR(map.view));
        break;
      default:
        p.global.errorMessage("ERROR: Projectile ID " + this.ID + " doesn't wait to explode.");
        this.waiting_to_explode = false;
        return;
    }
  }

  boolean explodesOnImpact() {
    switch(this.ID) {
      case 3331: // Mustang and Sally
      case 3341: // RPG
      case 3342: // Dystopic Demolisher
      case 3362: // Rocket-Propelled Grievance
      case 3372: // Ray Gun
      case 3392: // Porter's X2 Ray Gun
        return true;
      default:
        return false;
    }
  }

  void explode(AbstractGameMap map) {
    double explode_range = 0;
    double explode_maxPower = 0;
    double explode_minPower = 0;
    switch(this.ID) { // set values and add visual effects
      case 3321: // War Machine
        explode_range = LNZ.projectile_grenadeExplosionRadius;
        explode_minPower = 125;
        explode_maxPower = 450;
        map.addVisualEffect(4010, this.coordinate);
        p.global.sounds.trigger_units("items/grenade",
          this.coordinate.subtractR(map.view));
        break;
      case 3331: // Mustang and Sally
        explode_range = LNZ.projectile_grenadeExplosionRadius;
        explode_minPower = 75;
        explode_maxPower = 1200;
        map.addVisualEffect(4011, this.coordinate);
        p.global.sounds.trigger_units("items/grenade",
          this.coordinate.subtractR(map.view));
        break;
      case 3341: // RPG
        explode_range = LNZ.projectile_rpgExplosionRadius;
        explode_minPower = 100;
        explode_maxPower = 500;
        map.addVisualEffect(4012, this.coordinate);
        p.global.sounds.trigger_units("items/grenade_RPG",
          this.coordinate.subtractR(map.view));
        break;
      case 3342: // Dystopic Demolisher
        explode_range = LNZ.projectile_grenadeExplosionRadius;
        explode_minPower = 125;
        explode_maxPower = 900;
        map.addVisualEffect(4013, this.coordinate);
        p.global.sounds.trigger_units("items/grenade",
          this.coordinate.subtractR(map.view));
        break;
      case 3362: // Rocket-Propelled Grievance
        explode_range = LNZ.projectile_rpgIIExplosionRadius;
        explode_minPower = 100;
        explode_maxPower = 600;
        map.addVisualEffect(4014, this.coordinate);
        p.global.sounds.trigger_units("items/grenade_RPG",
          this.coordinate.subtractR(map.view));
        break;
      case 3372: // Ray Gun
        explode_range = LNZ.projectile_rayGunExplosionRadius;
        explode_minPower = 300;
        explode_maxPower = 1500;
        map.addVisualEffect(4015, this.coordinate.x, this.coordinate.y - 0.5 * LNZ.projectile_rayGunExplosionRadius);
        break;
      case 3392: // Porter's X2 Ray Gun
        explode_range = LNZ.projectile_rayGunIIExplosionRadius;
        explode_minPower = 300;
        explode_maxPower = 2000;
        map.addVisualEffect(4016, this.coordinate.x, this.coordinate.y - 0.5 * LNZ.projectile_rayGunIIExplosionRadius);
        break;
      case 3944: // Grenade
        explode_range = LNZ.projectile_grenadeExplosionRadius;
        explode_minPower = 100;
        explode_maxPower = 400;
        map.addVisualEffect(4017, this.coordinate);
        p.global.sounds.trigger_units("items/grenade",
          this.coordinate.subtractR(map.view));
        break;
      default:
        p.global.errorMessage("ERROR: Projectile ID " + this.ID + " doesn't explode.");
        break;
    }
    Unit source_unit = map.units.get(this.source_key);
    DamageSource damage_source = source_unit == null ?
      new DamageSource(2, this.ID) :
      new DamageSource(13, source_unit.ID, this.ID);
    map.splashDamage(this.coordinate.copy(), explode_range, explode_maxPower,
      explode_minPower, this.source_key, this.damageType, this.element,
      this.piercing, this.penetration, true, damage_source);
    this.remove = true;
  }


  String fileString() {
    String fileString = "\nnew: Projectile: " + this.ID;
    fileString += this.objectFileString();
    fileString += "\nsize: " + this.size;
    fileString += "\nsource_key: " + this.source_key;
    fileString += "\nfacingX: " + this.facing.x;
    fileString += "\nfacingY: " + this.facing.y;
    fileString += "\nalliance: " + this.alliance.alliance_name();
    fileString += "\nelement: " + this.element.element_name();
    fileString += "\npower: " + this.power;
    fileString += "\npiercing: " + this.piercing;
    fileString += "\npenetration: " + this.penetration;
    fileString += "\nrangeLeft: " + this.range_left;
    fileString += "\nwaitingToExplode: " + this.waiting_to_explode;
    fileString += "\nend: Projectile\n";
    return fileString;
  }

  void addData(String datakey, String data) {
    if (this.addObjectData(datakey, data)) {
      return;
    }
    switch(datakey) {
      case "size":
        this.size = Misc.toDouble(data);
        break;
      case "source_key":
        this.source_key = Misc.toInt(data);
        break;
      case "facingX":
        this.facing.x = Misc.toDouble(data);
        break;
      case "facingY":
        this.facing.y = Misc.toDouble(data);
        break;
      case "alliance":
        this.alliance = Alliance.alliance(data);
        break;
      case "element":
        this.element = Element.element(data);
        break;
      case "power":
        this.power = Misc.toDouble(data);
        break;
      case "piercing":
        this.piercing = Misc.toDouble(data);
        break;
      case "penetration":
        this.penetration = Misc.toDouble(data);
        break;
      case "rangeLeft":
        this.range_left = Misc.toDouble(data);
        break;
      case "waitingToExplode":
        this.waiting_to_explode = Misc.toBoolean(data);
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not found for projectile data.");
        break;
    }
  }
}