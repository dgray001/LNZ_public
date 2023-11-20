package LNZModule;

import java.util.*;
import processing.core.*;
import Button.*;
import DImg.DImg;
import Form.*;

class WorldMap {
  class MapDimgThread extends Thread {
    protected double start_percent_x = 0;
    protected double start_percent_y = 0;
    protected double vis_percent_x = 0;
    protected double vis_percent_y = 0;
    protected double xi_map = 0;
    protected double yi_map = 0;
    protected double xf_map = 0;
    protected double yf_map = 0;
    protected double zoom = 0;
    MapDimgThread() {
      super("MapDimgThread");
      this.setDaemon(true);
    }
    @Override
    public void run() {
      if (WorldMap.this.map_dimg == null) {
        return;
      }
      this.start_percent_x = WorldMap.this.start_percent_x;
      this.start_percent_y = WorldMap.this.start_percent_y;
      this.vis_percent_x = WorldMap.this.vis_percent_x;
      this.vis_percent_y = WorldMap.this.vis_percent_y;
      this.xi_map = WorldMap.this.xi_map;
      this.yi_map = WorldMap.this.yi_map;
      this.xf_map = WorldMap.this.xf_map;
      this.yf_map = WorldMap.this.yf_map;
      this.zoom = WorldMap.this.zoom;
      PImage next_image = WorldMap.this.map_dimg.getImagePercent(
        (float)this.start_percent_x, (float)this.start_percent_y,
        (float)this.vis_percent_x, (float)this.vis_percent_y);
      next_image = DImg.resizeImage(p, next_image,
        (int)Math.round(this.xf_map - this.xi_map),
        (int)Math.round(this.yf_map - this.yi_map));
      WorldMap.this.display_img = next_image;
      WorldMap.this.start_percent_x_old = this.start_percent_x;
      WorldMap.this.start_percent_y_old = this.start_percent_y;
      WorldMap.this.vis_percent_x_old = this.vis_percent_x;
      WorldMap.this.vis_percent_y_old = this.vis_percent_y;
      WorldMap.this.xi_map_old = this.xi_map;
      WorldMap.this.yi_map_old = this.yi_map;
      WorldMap.this.xf_map_old = this.xf_map;
      WorldMap.this.yf_map_old = this.yf_map;
      WorldMap.this.xi_map_dif = 0;
      WorldMap.this.yi_map_dif = 0;
      WorldMap.this.xf_map_dif = 0;
      WorldMap.this.yf_map_dif = 0;
      WorldMap.this.zoom_old = this.zoom;
    }
  }


  public static final double circleRadius = 10;

  class LocationCircle {
    private Location location;
    private double x_percent = 0;
    private double y_percent = 0;

    private double distance_from_cursor = 0;
    private boolean hovered = false;
    private boolean clicked = false;

    LocationCircle(Location a) {
      this.location = a;
      this.x_percent = a.worldMapLocationX();
      this.y_percent = a.worldMapLocationY();
    }

    boolean outsideView(double start_x, double start_y, double vis_x, double vis_y, double zoom) {
      double circleRadiusX = zoom * WorldMap.circleRadius / WorldMap.this.map_dimg.img.width;
      double circleRadiusY = zoom * WorldMap.circleRadius / WorldMap.this.map_dimg.img.height;
      if (this.x_percent - circleRadiusX < start_x || this.y_percent - circleRadiusY < start_y ||
        this.x_percent + circleRadiusX < start_x || this.y_percent + circleRadiusY < start_y) {
        return true;
      }
      return false;
    }
  }


  class LeftPanelForm extends Form {
    class HeroListFormField extends MessageFormField {
      class HeroCodeButton extends ImageButton {
        protected HeroCode code;
        HeroCodeButton(LNZ sketch, HeroCode code) {
          super(sketch, sketch.global.images.getImage("units/" + code.imagePathHeader() + ".png"), 0, 0, 0, 0);
          this.code = code;
          this.overshadow_colors = true;
          this.setColors(DImg.ccolor(200, 100), DImg.ccolor(1, 0),
            DImg.ccolor(220, 80), DImg.ccolor(180, 140), DImg.ccolor(0));
        }
        public void hover() {}
        public void dehover() {}
        public void click() {}
        public void release() {
          if (!this.hovered) {
            return;
          }
          HeroListFormField.this.switchToHero(this.code);
        }
      }

      private LNZ p;
      protected ArrayList<HeroCodeButton> heroes = new ArrayList<HeroCodeButton>();
      protected double default_hero_image_height = 70;
      protected double hero_image_height = 0;
      protected double button_gap = 3;

      HeroListFormField(LNZ sketch) {
        super(sketch, "Heroes at this location:");
        this.p = sketch;
        this.setTextSize(20);
      }

      void addHeroCode(HeroCode code) {
        this.heroes.add(new HeroCodeButton(p, code));
        this.updateWidthDependencies();
      }
      void switchToHero(HeroCode code) {
        LeftPanelForm.this.switchToHero(code);
      }

      @Override
      public void updateWidthDependencies() {
        super.updateWidthDependencies();
        if (this.heroes.size() == 0) {
          this.hero_image_height = 0;
          return;
        }
        this.hero_image_height = Math.min(this.default_hero_image_height,
          (this.field_width - this.button_gap * this.heroes.size() + this.button_gap) / this.heroes.size());
        double x_curr = 0;
        double y_start = super.getHeight() + this.button_gap;
        for (HeroCodeButton button : this.heroes) {
          button.setLocation(x_curr, y_start, x_curr + this.hero_image_height, y_start + this.hero_image_height);
          x_curr += this.hero_image_height + this.button_gap;
        }
      }
      @Override
      public double getHeight() {
        return super.getHeight() + this.hero_image_height + this.button_gap;
      }
      @Override
      public FormFieldSubmit update(int millis) {
        for (HeroCodeButton button : this.heroes) {
          button.update(millis);
        }
        if (this.heroes.size() == 0) {
          return FormFieldSubmit.NONE;
        }
        return super.update(millis);
      }
      @Override
      public void mouseMove(float mX, float mY) {
        for (HeroCodeButton button : this.heroes) {
          button.mouseMove(mX, mY);
        }
      }
      @Override
      public void mousePress() {
        for (HeroCodeButton button : this.heroes) {
          button.mousePress();
        }
      }
      @Override
      public void mouseRelease(float mX, float mY) {
        for (HeroCodeButton button : this.heroes) {
          button.mouseRelease(mX, mY);
        }
      }
    }

    private LNZ p;
    protected Location location;

    LeftPanelForm(LNZ sketch, Location location, double xi, double yi, double xf, double yf) {
      super(sketch, xi, yi, xf, yf);
      this.p = sketch;
      this.location = location;
      this.color_background = DImg.ccolor(250, 190, 140);
      this.scrollbar.setButtonColors(DImg.ccolor(220), DImg.ccolor(220, 160, 110), DImg.ccolor(
        240, 180, 130), DImg.ccolor(200, 140, 90), DImg.ccolor(0));
      this.scrollbar.button_upspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      this.scrollbar.button_downspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));

      MessageFormField title = new MessageFormField(sketch, location.getCampaignName());
      title.text_align = PConstants.CENTER;
      title.setTextSize(26);
      MessageFormField subtitle = new MessageFormField(sketch, location.getCampaignSubtitle());
      subtitle.text_align = PConstants.CENTER;
      subtitle.setTextSize(20);
      TextBoxFormField description = new TextBoxFormField(sketch, location.campaignDescription(), 250);
      description.textbox.color_background = this.color_background;
      description.textbox.scrollbar.setButtonColors(DImg.ccolor(220), DImg.ccolor(220, 160, 110), DImg.ccolor(
        240, 180, 130), DImg.ccolor(200, 140, 90), DImg.ccolor(0));
      description.textbox.scrollbar.button_upspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      description.textbox.scrollbar.button_downspace.setColors(DImg.ccolor(170), DImg.ccolor(255, 200, 150),
        DImg.ccolor(255, 200, 150), DImg.ccolor(60, 30, 0), DImg.ccolor(0));
      HeroListFormField heroes = new HeroListFormField(sketch);
      for (Hero hero : p.global.profile.heroes.values()) {
        if (hero.location.getCampaignName() != location.getCampaignName()) {
          continue;
        }
        heroes.addHeroCode(hero.code);
      }

      this.addField(title);
      this.addField(subtitle);
      this.addField(new ImageFormField(sketch, sketch.global.images.getImage(location.campaignImagePath()), 90));
      this.addField(description);
      this.addField(heroes);
      // TODO: unlocked / how to unlock
    }

    void switchToHero(HeroCode code) {
      WorldMap.this.switchToHero(code);
    }

    public void submit() {}
    public void cancel() {}
    public void buttonPress(int i) {}
  }

  private LNZ p;

  private DImg map_dimg = null;
  private PImage display_img = null;
  private MapDimgThread map_dimg_thread = null;
  private boolean update_display = false;

  private double xi = 0;
  private double yi = 0;
  private double xf = 0;
  private double yf = 0;

  protected double viewX = 0;
  protected double viewY = 0;
  protected double zoom = LNZ.playing_worldMapDefaultZoom;
  protected double zoom_old = LNZ.playing_worldMapDefaultZoom;
  protected boolean view_moving_left = false;
  protected boolean view_moving_right = false;
  protected boolean view_moving_up = false;
  protected boolean view_moving_down = false;

  protected double start_percent_x = 0;
  protected double start_percent_y = 0;
  protected double vis_percent_x = 0;
  protected double vis_percent_y = 0;
  protected double start_percent_x_old = 0;
  protected double start_percent_y_old = 0;
  protected double vis_percent_x_old = 0;
  protected double vis_percent_y_old = 0;

  private double xi_map = 0;
  private double yi_map = 0;
  private double xf_map = 0;
  private double yf_map = 0;
  private double xi_map_old = 0;
  private double yi_map_old = 0;
  private double xf_map_old = 0;
  private double yf_map_old = 0;
  private double xi_map_dif = 0;
  private double yi_map_dif = 0;
  private double xf_map_dif = 0;
  private double yf_map_dif = 0;

  private double last_x = 0;
  private double last_y = 0;

  private boolean hovered = false;
  protected boolean dragging = false;
  private LocationCircle location_hovered = null;
  private Location location_clicked = null;
  private LeftPanelForm left_panel_form = null;

  private HashMap<Location, LocationCircle> location_circles = new HashMap<Location, LocationCircle>();

  private int last_update_time = 0;

  private PlayingInterface playing_interface;

  WorldMap(LNZ sketch, PlayingInterface playing_interface) {
    this.p = sketch;
    this.playing_interface = playing_interface;
    this.map_dimg = new DImg(sketch, sketch.global.images.getImage("world_map.jpg"));
    for (Location location : Location.VALUES) {
      if (location.isArea() || location.isCampaignStart()) {
        this.location_circles.put(location, new LocationCircle(location));
      }
    }
    Hero curr_hero = p.global.profile.heroes.get(p.global.profile.curr_hero);
    if (curr_hero == null) {
      return;
    }
    Location curr_location = curr_hero.location;
    if (curr_location != null && curr_location != Location.ERROR) {
      if (!curr_location.isArea()) {
        curr_location = Location.getCampaignStart(curr_location);
      }
      this.setViewLocation(curr_location.worldMapLocationX(), curr_location.worldMapLocationY(), false);
    }
  }


  void switchToHero(HeroCode code) {
    this.playing_interface.switchHero(code, true);
  }


  void setLocation(double xi, double yi, double xf, double yf) {
    this.xi = xi;
    this.yi = yi;
    this.xf = xf;
    this.yf = yf;
    if (this.left_panel_form != null) {
      this.left_panel_form.setLocation(LNZ.mapEditor_listBoxGap,
        this.yi + LNZ.mapEditor_listBoxGap,
        this.xi - LNZ.mapEditor_listBoxGap,
        this.yi + 0.5 * (this.yf - this.yi) - 1);
    }
    this.refreshDisplayMapParameters();
  }

  void setZoom(double zoom) {
    if (Double.isNaN(zoom)) {
      return;
    }
    if (zoom > LNZ.playing_worldMapMaxZoom) {
      zoom = LNZ.playing_worldMapMaxZoom;
    }
    else if (zoom < LNZ.playing_worldMapMinZoom) {
      zoom = LNZ.playing_worldMapMinZoom;
    }
    this.zoom = zoom;
    this.refreshDisplayMapParameters();
  }
  void changeZoom(double amount) {
    this.setZoom(this.zoom + amount);
  }

  void setViewLocation(double viewX, double viewY) {
    this.setViewLocation(viewX, viewY, true);
  }
  void setViewLocation(double viewX, double viewY, boolean refreshImage) {
    if (viewX < 0.0) {
      viewX = 0.0;
    }
    else if (viewX > 1.0) {
      viewX = 1.0;
    }
    if (viewY < 0.0) {
      viewY = 0.0;
    }
    else if (viewY > 1.0) {
      viewY = 1.0;
    }
    this.viewX = viewX;
    this.viewY = viewY;
    if (refreshImage) {
      this.refreshDisplayMapParameters();
      this.updateCursorPosition();
    }
  }
  void moveView(double changeX, double changeY) {
    this.moveView(changeX, changeY, true);
  }
  void moveView(double changeX, double changeY, boolean refreshImage) {
    this.setViewLocation(this.viewX + changeX, this.viewY + changeY, refreshImage);
  }

  void refreshDisplayMapParameters() {
    this.start_percent_x = Math.max(0, this.viewX - (0.5 * p.width - this.xi) * this.zoom / this.map_dimg.img.width);
    this.start_percent_y = Math.max(0, this.viewY - (0.5 * p.height - this.yi) * this.zoom / this.map_dimg.img.height);
    this.xi_map = 0.5 * p.width - (this.viewX - this.start_percent_x) * this.map_dimg.img.width / this.zoom;
    this.yi_map = 0.5 * p.height - (this.viewY - this.start_percent_y) * this.map_dimg.img.height / this.zoom;
    this.vis_percent_x = Math.min(1.0 - this.start_percent_x, (this.xf - this.xi_map) * this.zoom / this.map_dimg.img.width);
    this.vis_percent_y = Math.min(1.0 - this.start_percent_y, (this.yf - this.yi_map) * this.zoom / this.map_dimg.img.height);
    this.xf_map = this.xi_map + this.vis_percent_x * this.map_dimg.img.width / this.zoom;
    this.yf_map = this.yi_map + this.vis_percent_y * this.map_dimg.img.height / this.zoom;
    this.xi_map_dif = this.start_percent_x - this.start_percent_x_old;
    this.yi_map_dif = this.start_percent_y - this.start_percent_y_old;
    this.xf_map_dif = xi_map_dif + this.vis_percent_x - this.vis_percent_x_old;
    this.yf_map_dif = yi_map_dif + this.vis_percent_y - this.vis_percent_y_old;
    this.refreshDisplayImage();
  }

  void refreshDisplayImage() {
    if (this.map_dimg_thread != null && this.map_dimg_thread.isAlive()) {
      this.update_display = true;
    }
    else {
      this.update_display = false;
      this.startMapThread();
    }
  }
  void startMapThread() {
    this.map_dimg_thread = new MapDimgThread();
    this.map_dimg_thread.start();
  }


  void drawLeftPanel(int millis) {
    if (this.left_panel_form != null) {
      this.left_panel_form.update(millis);
    }
    p.stroke(0);
    p.strokeWeight(1.5);
    p.line(0, this.yi + 0.5 * (this.yf - this.yi), this.xi, this.yi + 0.5 * (this.yf - this.yi));
    if (p.global.profile.currHero() != null) {
      p.global.profile.currHero().drawLeftPanel(millis, this.xi);
    }
  }


  void update(int millis) {
    if (this.update_display) {
      this.update_display = false;
      this.refreshDisplayImage();
    }
    int time_elapsed = millis - this.last_update_time;
    boolean refreshView = false;
    // moving view
    if (this.view_moving_left) {
      this.moveView(-time_elapsed * LNZ.playing_viewMoveSpeedFactor, 0, false);
      refreshView = true;
    }
    if (this.view_moving_right) {
      this.moveView(time_elapsed * LNZ.playing_viewMoveSpeedFactor, 0, false);
      refreshView = true;
    }
    if (this.view_moving_up) {
      this.moveView(0, -time_elapsed * LNZ.playing_viewMoveSpeedFactor *
        this.map_dimg.img.width / this.map_dimg.img.height, false);
      refreshView = true;
    }
    if (this.view_moving_down) {
      this.moveView(0, time_elapsed * LNZ.playing_viewMoveSpeedFactor *
        this.map_dimg.img.width / this.map_dimg.img.height, false);
      refreshView = true;
    }
    if (refreshView) {
      this.refreshDisplayMapParameters();
      this.updateCursorPosition();
    }
    // display map
    p.rectMode(PConstants.CORNERS);
    p.noStroke();
    p.fill(DImg.ccolor(60));
    p.rect(this.xi, this.yi, this.xf, this.yf);
    if (this.display_img != null) {
      p.imageMode(PConstants.CORNERS);
      p.image(this.display_img, this.xi_map_old + this.xi_map_dif, this.yi_map_old +
        this.yi_map_dif, this.xf_map_old + this.xf_map_dif, this.yf_map_old + this.yf_map_dif);
    }
    // display location circles
    for (LocationCircle location : this.location_circles.values()) {
      if (location.outsideView(this.start_percent_x_old, this.start_percent_y_old,
        this.vis_percent_x_old, this.vis_percent_y_old, this.zoom_old)) {
        continue;
      }
      double translate_x = this.xi_map_old + this.xi_map_dif + (location.x_percent -
        this.start_percent_x_old) * this.map_dimg.img.width / this.zoom_old;
        double translate_y = this.yi_map_old + this.yi_map_dif + (location.y_percent -
        this.start_percent_y_old) * this.map_dimg.img.height / this.zoom_old;
      p.translate(translate_x, translate_y);
      if (location.hovered) {
        p.fill(DImg.ccolor(255, 255, 0));
      }
      else {
        p.noFill();
      }
      p.stroke(DImg.ccolor(255, 255, 0));
      p.strokeWeight(2);
      p.ellipseMode(PConstants.RADIUS);
      p.circle(0, 0, WorldMap.circleRadius);
      if (location.hovered) { // hovered info
        p.fill(p.global.color_nameDisplayed_background);
        p.stroke(p.global.color_nameDisplayed_background);
        p.strokeWeight(0.01);
        p.triangle(0, 0, -2 * WorldMap.circleRadius, -3.5 * WorldMap.circleRadius,
          2 * WorldMap.circleRadius, -3.5 * WorldMap.circleRadius);
        String location_name = location.location.getCampaignName();
        p.textSize(24);
        double name_width = p.textWidth(location_name) + 2;
        double name_height = p.textAscent() + p.textDescent() + 2;
        double name_xi = -2 * WorldMap.circleRadius;
        double name_yi = -3.5 * WorldMap.circleRadius - name_height;
        p.fill(p.global.color_nameDisplayed_background);
        p.rectMode(PConstants.CORNER);
        p.noStroke();
        p.rect(name_xi, name_yi, name_width, name_height);
        p.fill(p.global.color_nameDisplayed_text);
        p.textAlign(PConstants.LEFT, PConstants.TOP);
        p.text(location_name, name_xi + 1, name_yi + 1);
      }
      p.translate(-translate_x, -translate_y);
    }
    this.last_update_time = millis;
  }

  void mouseMove(float mX, float mY) {
    if (mX < LNZ.small_number) {
      WorldMap.this.view_moving_left = true;
      if (!p.global.holding_right) {
        WorldMap.this.view_moving_right = false;
      }
    }
    else if (mX > p.width - 1 - LNZ.small_number) {
      WorldMap.this.view_moving_right = true;
      if (!p.global.holding_left) {
        WorldMap.this.view_moving_left = false;
      }
    }
    else {
      if (!p.global.holding_right) {
        WorldMap.this.view_moving_right = false;
      }
      if (!p.global.holding_left) {
        WorldMap.this.view_moving_left = false;
      }
    }
    if (mY < LNZ.small_number) {
      WorldMap.this.view_moving_up = true;
      if (!p.global.holding_down) {
        WorldMap.this.view_moving_down = false;
      }
    }
    else if (mY > p.height - 1 - LNZ.small_number) {
      WorldMap.this.view_moving_down = true;
      if (!p.global.holding_up) {
        WorldMap.this.view_moving_up = false;
      }
    }
    else {
      if (!p.global.holding_down) {
        WorldMap.this.view_moving_down = false;
      }
      if (!p.global.holding_up) {
        WorldMap.this.view_moving_up = false;
      }
    }
    if (this.dragging) {
      this.moveView((this.last_x - mX) * this.zoom_old / this.map_dimg.img.width,
        (this.last_y - mY) * this.zoom_old / this.map_dimg.img.height);
    }
    this.last_x = mX;
    this.last_y = mY;
    if (mX < this.xi || mY < this.yi || mX > this.xf || mY > this.yf) {
      this.hovered = false;
    }
    else {
      this.hovered = true;
    }
    if (this.left_panel_form != null) {
      this.left_panel_form.mouseMove(mX, mY);
    }
    this.location_hovered = null;
    for (LocationCircle location : this.location_circles.values()) {
      location.hovered = false;
      if (!this.hovered) {
        continue;
      }
      double translate_x = this.xi_map_old + this.xi_map_dif + (location.x_percent -
        this.start_percent_x_old) * this.map_dimg.img.width / this.zoom_old;
      double translate_y = this.yi_map_old + this.yi_map_dif + (location.y_percent -
        this.start_percent_y_old) * this.map_dimg.img.height / this.zoom_old;
      double x_dist = mX - translate_x;
      double y_dist = mY - translate_y;
      double dist = Math.sqrt(x_dist * x_dist + y_dist * y_dist);
      location.distance_from_cursor = dist;
      if (dist > WorldMap.circleRadius) {
        continue;
      }
      if (this.location_hovered == null) {
        this.location_hovered = location;
        location.hovered = true;
        continue;
      }
      if (dist >= this.location_hovered.distance_from_cursor) {
        continue;
      }
      this.location_hovered.hovered = false;
      this.location_hovered = location;
      location.hovered = true;
    }
  }

  void updateCursorPosition() {
    this.updateCursorPosition(this.last_x, this.last_y);
  }
  void updateCursorPosition(double mouse_x, double mouse_y) {
    //this.map_mX = this.start_percent_x + (this.last_x - this.xi_map) / this.zoom;
    //this.map_mY = this.start_percent_y + (this.last_y - this.yi_map) / this.zoom;
  }

  void mousePress() {
    if (this.left_panel_form != null) {
      this.left_panel_form.mousePress();
    }
    if (!this.hovered) {
      return;
    }
    if (this.location_hovered == null) {
      if (p.mouseButton == PConstants.LEFT) {
        this.dragging = true;
      }
    }
    else {
      this.location_hovered.clicked = true;
    }
  }

  void mouseRelease(float mX, float mY) {
    if (this.left_panel_form != null) {
      this.left_panel_form.mouseRelease(mX, mY);
    }
    if (p.mouseButton == PConstants.LEFT) {
      this.dragging = false;
    }
    for (LocationCircle location : this.location_circles.values()) {
      if (!this.hovered || !location.hovered) {
        location.clicked = false;
        continue;
      }
      if (location.clicked) {
        location.clicked = false;
        this.location_clicked = location.location;
      }
    }
    if (this.location_clicked == null) {
      this.left_panel_form = null;
    }
    else if (this.left_panel_form == null || this.left_panel_form.location != this.location_clicked) {
      this.left_panel_form = new LeftPanelForm(p, this.location_clicked, LNZ.mapEditor_listBoxGap,
        this.yi + LNZ.mapEditor_listBoxGap, this.xi - LNZ.mapEditor_listBoxGap,
        this.yi + 0.5 * (this.yf - this.yi) - 1);
    }
  }

  void scroll(int amount) {
    if (this.left_panel_form != null) {
      this.left_panel_form.scroll(amount);
    }
    if (this.hovered) {
      double x = Math.log(this.zoom) + LNZ.playing_scrollZoomFactor * amount;
      this.setZoom(Math.exp(x));
    }
  }
}


/*PImage bufferedImageToPImage(BufferedImage bimg) {
  PImage pimg = new PImage(bimg.getWidth(), bimg.getHeight(), ARGB);
  pimg.loadPixels();
  bimg.getRGB(0, 0, pimg.width, pimg.height, pimg.pixels, 0, pimg.width);
  pimg.updatePixels();
  return pimg;
}*/