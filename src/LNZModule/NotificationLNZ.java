package LNZModule;

import processing.core.*;
import DImg.DImg;

abstract class NotificationLNZ {
  protected boolean sliding_in = true;
  protected boolean sliding_out = false;
  protected boolean finished = false;
  protected boolean hovered = false;
  protected float time_left = LNZ.notification_slide_time;

  NotificationLNZ() {
  }

  void update(int time_elapsed) {
    if (this.finished) {
      return;
    }
    this.drawNotification();
    this.time_left -= time_elapsed;
    if (this.time_left < 0) {
      if (this.sliding_in) {
        this.sliding_in = false;
        this.time_left = LNZ.notification_display_time;
      }
      else if (this.sliding_out) {
        this.finished = true;
      }
      else {
        this.sliding_out = true;
        this.time_left = LNZ.notification_slide_time;
      }
    }
  }

  abstract void drawNotification();

  void mouseMove(float mX, float mY) {
    if (!this.sliding_in && !this.sliding_out && this.hovered(mX, mY)) {
      this.time_left = LNZ.notification_display_time;
      this.hovered = true;
    }
    else {
      this.hovered = false;
    }
  }

  abstract boolean hovered(float mX, float mY);

  void mousePress() {
    if (this.hovered) {
      this.finished = true;
    }
  }
}


abstract class BottomRightNotification extends NotificationLNZ {
  private LNZ p;

  private String header;
  private String content;
  private int color_background;
  private int color_text;

  BottomRightNotification(LNZ sketch, String header, String content, int color_background, int color_text) {
    super();
    this.p = sketch;
    this.header = header;
    this.content = content;
    this.color_background = color_background;
    this.color_text = color_text;
  }

  void drawNotification() {
    p.fill(this.color_background);
    p.noStroke();
    p.rectMode(PConstants.CORNERS);
    if (this.sliding_in) {
      double curr_height = LNZ.notification_achievement_height *
        (1 - this.time_left / LNZ.notification_slide_time);
      p.rect(p.width - LNZ.notification_achievement_width, p.height - curr_height, p.width, p.height);
    }
    else if (this.sliding_out) {
      double curr_height = LNZ.notification_achievement_height *
        this.time_left / LNZ.notification_slide_time;
      p.rect(p.width - LNZ.notification_achievement_width, p.height - curr_height, p.width, p.height);
    }
    else {
      p.rect(p.width - LNZ.notification_achievement_width, p.height -
        LNZ.notification_achievement_height, p.width, p.height);
      p.fill(this.color_text);
      p.textSize(18);
      p.textAlign(PConstants.CENTER, PConstants.TOP);
      p.text(this.header, p.width - 0.5 * LNZ.notification_achievement_width,
        p.height - LNZ.notification_achievement_height - 1);
        double offset = p.textAscent() + p.textDescent() + 2;
      p.stroke(this.color_text);
      p.strokeWeight(2);
      p.line(p.width - LNZ.notification_achievement_width + 3, p.height -
        LNZ.notification_achievement_height + offset, p.width - 3, p.height -
        LNZ.notification_achievement_height + offset);
      p.textSize(16);
      p.textAlign(PConstants.CENTER, PConstants.CENTER);
      p.text(this.content, p.width - 0.5 * LNZ.notification_achievement_width,
        p.height - 0.5 * (LNZ.notification_achievement_height - offset));
    }
  }

  boolean hovered(float mX, float mY) {
    if (p.width - mX < LNZ.notification_achievement_width && p.height - mY <
      LNZ.notification_achievement_height && mX < p.width && mY < p.height) {
      return true;
    }
    return false;
  }
}


class AchievementNotification extends BottomRightNotification {
  AchievementNotification(LNZ sketch, AchievementCode code) {
    super(sketch, "Achievement Complete!", code.displayName(), DImg.ccolor(160, 155, 88, 220), DImg.ccolor(0));
  }
}


class AreaUnlockNotification extends BottomRightNotification {
  AreaUnlockNotification(LNZ sketch, Location location) {
    super(sketch, "Area Unlocked!", location.displayName(), DImg.ccolor(177, 156, 217, 220), DImg.ccolor(0));
  }
}


class HeroUnlockNotification extends BottomRightNotification {
  HeroUnlockNotification(LNZ sketch, HeroCode code) {
    super(sketch, "Hero Unlocked!", code.displayName(), DImg.ccolor(255, 127, 127, 220), DImg.ccolor(0));
  }
}


class MinigameUnlockNotification extends BottomRightNotification {
  MinigameUnlockNotification(LNZ sketch, MinigameName name) {
    super(sketch, "Minigame Unlocked!", name.displayName(), DImg.ccolor(100, 255, 255, 220), DImg.ccolor(0));
  }
}