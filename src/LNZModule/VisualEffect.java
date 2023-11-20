package LNZModule;

import processing.core.*;
import Misc.Misc;

class VisualEffect extends MapObject {
  protected double size_width = 0;
  protected double size_height = 0;
  protected double timer = 0;
  protected boolean scale_size = true;

  VisualEffect(LNZ sketch, int ID) {
    this(sketch, ID, 0, 0);
  }
  VisualEffect(LNZ sketch, int ID, double x, double y) {
    super(sketch, ID);
    this.setLocation(x, y);
    switch(ID) {
      // gifs
      case 4001: // move gif
        this.setValues(1.3 * p.global.configuration.cursor_size,
          1.3 * p.global.configuration.cursor_size, LNZ.gif_move_time);
        this.scale_size = false;
        break;
      case 4002: // chuck quizmo poof
        this.setValues(2.176, 1.632, 1200 + LNZ.gif_poof_time);
        break;
      case 4003: // amphibious leap land
        this.setValues(2 * LNZ.ability_113_splashRadius, 2 * LNZ.
          ability_113_splashRadius, LNZ.gif_amphibiousLeap_time);
        break;
      case 4004: // amphibious leap drenched land
        this.setValues(2 * LNZ.ability_113_drenchedSplashRadius, 2 * LNZ.
          ability_113_drenchedSplashRadius, LNZ.gif_amphibiousLeap_time);
        break;
      case 4005: // amphibious leap II land
        this.setValues(2 * LNZ.ability_118_splashRadius, 2 * LNZ.
          ability_118_splashRadius, LNZ.gif_amphibiousLeap_time);
        break;
      case 4006: // amphibious leap II drenched land
        this.setValues(2 * LNZ.ability_118_drenchedSplashRadius, 2 * LNZ.
          ability_118_drenchedSplashRadius, LNZ.gif_amphibiousLeap_time);
        break;
      case 4007: // alkaloid secretion
        this.setValues(4 * LNZ.ability_114_range, 2 * LNZ.ability_114_range, 100);
        break;
      case 4008: // alkaloid secretion II
        this.setValues(4 * LNZ.ability_119_range, 2 * LNZ.ability_119_range, 100);
        break;
      case 4009: // chat bubble
        this.setValues(0.8, 0.8, 1000);
        break;
      case 4010: // war machine explosion
        this.setValues(2 * LNZ.projectile_grenadeExplosionRadius, 2 *
          LNZ.projectile_grenadeExplosionRadius, LNZ.gif_explosionNormal_time);
        break;
      case 4011: // mustang and sally explosion
        this.setValues(2 * LNZ.projectile_mustangAndSallyExplosionRadius, 2 *
          LNZ.projectile_mustangAndSallyExplosionRadius, LNZ.gif_explosionNormal_time);
        break;
      case 4012: // RPG explosion
        this.setValues(2 * LNZ.projectile_rpgExplosionRadius, 2 *
          LNZ.projectile_rpgExplosionRadius, LNZ.gif_explosionNormal_time);
        break;
      case 4013: // dystopic demolisher explosion
        this.setValues(2 * LNZ.projectile_grenadeExplosionRadius, 2 *
          LNZ.projectile_grenadeExplosionRadius, LNZ.gif_explosionNormal_time);
        break;
      case 4014: // rocket-propelled grievance
        this.setValues(2 * LNZ.projectile_rpgIIExplosionRadius, 2 *
          LNZ.projectile_rpgIIExplosionRadius, LNZ.gif_explosionNormal_time);
        break;
      case 4015: // ray gun explosion
        this.setValues(2 * LNZ.projectile_rayGunExplosionRadius, 2 *
          LNZ.projectile_rayGunExplosionRadius, LNZ.gif_explosionGreen_time);
        break;
      case 4016: // porter's x2 ray gun explosion
        this.setValues(2 * LNZ.projectile_rayGunIIExplosionRadius, 2 *
          LNZ.projectile_rayGunIIExplosionRadius, LNZ.gif_explosionGreen_time);
        break;
      case 4017: // grenade (thrown) explosion
        this.setValues(2 * LNZ.projectile_grenadeExplosionRadius, 2 *
          LNZ.projectile_grenadeExplosionRadius, LNZ.gif_explosionNormal_time);
        break;
      case 4018: // frog splash
        this.setValues(2 * LNZ.ability_113_splashRadius, 2 * LNZ.
          ability_113_splashRadius, LNZ.gif_amphibiousLeap_time);
        break;
      default:
        p.global.errorMessage("ERROR: VisualEffect ID " + ID + " not found.");
        break;
    }
  }

  void setValues(double size_width, double size_height, double timer) {
    this.size_width = size_width;
    this.size_height = size_height;
    this.timer = timer;
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

  // Note when scale_size != true we have no way of knowing map size (since it has no definite map size)
  double xi() {
    if (this.scale_size) {
      return this.coordinate.x - 0.5 * this.size_width;
    }
    return this.coordinate.x;
  }
  double yi() {
    if (this.scale_size) {
      return this.coordinate.y - 0.5 * this.size_height;
    }
    return this.coordinate.y;
  }
  double xf() {
    if (this.scale_size) {
      return this.coordinate.x + 0.5 * this.size_width;
    }
    return this.coordinate.x;
  }
  double yf() {
    if (this.scale_size) {
      return this.coordinate.y + 0.5 * this.size_height;
    }
    return this.coordinate.y;
  }
  double xCenter() {
    return this.coordinate.x;
  }
  double yCenter() {
    return this.coordinate.y;
  }
  double width() {
    if (this.scale_size) {
      return this.size_width;
    }
    return 0;
  }
  double height() {
    if (this.scale_size) {
      return this.size_height;
    }
    return 0;
  }
  double xRadius() {
    if (this.scale_size) {
      return 0.5 * this.size_width;
    }
    return 0;
  }
  double yRadius() {
    if (this.scale_size) {
      return 0.5 * this.size_height;
    }
    return 0;
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
  @Override
  double distance(MapObject object) {
    double xDistance = Math.max(0, Math.abs(this.xCenter() - object.xCenter()) - object.xRadius());
    double yDistance = Math.max(0, Math.abs(this.yCenter() - object.yCenter()) - object.yRadius());
    return Math.sqrt(xDistance * xDistance + yDistance * yDistance);
  }

  @Override
  boolean touching(MapObject object) {
    if ( ((Math.abs(this.xCenter() - object.xCenter()) - object.xRadius()) <= 0) ||
      ((Math.abs(this.yCenter() - object.yCenter()) - object.yRadius()) <= 0) ) {
        return true;
    }
    return false;
  }

  PImage getImage() {
    String path = "gifs/";
    int frame = 0;
    switch(this.ID) {
      case 4001:
        path += "move/";
        frame = (int)(LNZ.gif_move_frames *
          (1.0 - this.timer / (1 + LNZ.gif_move_time)));
        path += frame + ".png";
        break;
      case 4002:
        if (this.timer > LNZ.gif_poof_time) {
          path = "features/chuck_quizmo.png";
        }
        else {
          this.size_width = 1.6;
          path += "poof/";
          frame = (int)(LNZ.gif_poof_frames *
            (1.0 - this.timer / (1 + LNZ.gif_poof_time)));
          path += frame + ".png";
        }
        break;
      case 4003:
      case 4004:
      case 4005:
      case 4006:
      case 4018:
        path += "amphibious_leap/";
        frame = (int)(LNZ.gif_amphibiousLeap_frames *
          (1.0 - this.timer / (1 + LNZ.gif_amphibiousLeap_time)));
        path += frame + ".png";
        break;
      case 4009:
        path = "icons/chat.png";
        break;
      case 4010:
      case 4011:
      case 4012:
      case 4013:
      case 4014:
      case 4017:
        path += "explosion_normal/";
        frame = (int)(LNZ.gif_explosionNormal_frames *
          (1.0 - this.timer / (1 + LNZ.gif_explosionNormal_time)));
        path += frame + ".png";
        break;
      case 4015:
      case 4016:
        path += "explosion_green/";
        frame = (int)(LNZ.gif_explosionGreen_frames *
          (1.0 - this.timer / (1 + LNZ.gif_explosionGreen_time)));
        path += frame + ".png";
        break;
      default:
        p.global.errorMessage("ERROR: Visual Effect ID " + ID + " not found.");
        path = "default.png";
        break;
    }
    return p.global.images.getImage(path);
  }


  boolean targetable(Unit u) {
    return false;
  }


  void update(int timeElapsed) {
    this.timer -= timeElapsed;
    if (this.timer < 0) {
      this.remove = true;
    }
  }


  void display(double zoom) {
    switch(this.ID) {
      case 4007:
      case 4008:
        p.ellipseMode(PConstants.CENTER);
        p.fill(98, 52, 18, 130);
        p.noStroke();
        p.ellipse(0, 0, this.size_width * zoom, this.size_height * zoom);
        break;
      case 4003:
      case 4004:
      case 4005:
      case 4006:
        p.ellipseMode(PConstants.CENTER);
        p.fill(98, 52, 18, 100);
        p.noStroke();
        p.ellipse(0, 0, this.size_width * zoom, this.size_height * zoom);
      default:
        if (this.scale_size) {
          p.image(this.getImage(), 0, 0, this.size_width * zoom, this.size_height * zoom);
        }
        else {
          p.image(this.getImage(), 0, 0, this.size_width, this.size_height);
        }
        break;
    }
  }


  String fileString() {
    String fileString = "\nnew: VisualEffect: " + this.ID;
    fileString += this.objectFileString();
    fileString += "\nsize_width: " + this.size_width;
    fileString += "\nsize_height: " + this.size_height;
    fileString += "\ntimer: " + this.timer;
    fileString += "\nend: VisualEffect\n";
    return fileString;
  }

  void addData(String datakey, String data) {
    if (this.addObjectData(datakey, data)) {
      return;
    }
    switch(datakey) {
      case "size_width":
        this.size_width = Misc.toDouble(data);
        break;
      case "size_height":
        this.size_height = Misc.toDouble(data);
        break;
      case "timer":
        this.timer = Misc.toDouble(data);
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not found for visual effect data.");
        break;
    }
  }
}