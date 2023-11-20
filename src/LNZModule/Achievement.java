package LNZModule;

import java.util.*;

enum AchievementType {
  COMPLETED, CONTINUOUS, HIDDEN;
}


enum AchievementCode {
  COMPLETED_TUTORIAL, COMPLETED_FRANCISCAN, COMPLETED_DANSHOUSE,

  CONTINUOUS_KILLSI, CONTINUOUS_KILLSII, CONTINUOUS_KILLSIII, CONTINUOUS_KILLSIV, CONTINUOUS_KILLSV,
  CONTINUOUS_KILLSVI, CONTINUOUS_KILLSVII, CONTINUOUS_KILLSVIII, CONTINUOUS_KILLSIX, CONTINUOUS_KILLSX,

  CONTINUOUS_DEATHSI, CONTINUOUS_DEATHSII, CONTINUOUS_DEATHSIII, CONTINUOUS_DEATHSIV, CONTINUOUS_DEATHSV,
  CONTINUOUS_DEATHSVI, CONTINUOUS_DEATHSVII, CONTINUOUS_DEATHSVIII, CONTINUOUS_DEATHSIX, CONTINUOUS_DEATHSX,

  CONTINUOUS_WALKI, CONTINUOUS_WALKII, CONTINUOUS_WALKIII, CONTINUOUS_WALKIV, CONTINUOUS_WALKV,
  CONTINUOUS_WALKVI, CONTINUOUS_WALKVII, CONTINUOUS_WALKVIII, CONTINUOUS_WALKIX, CONTINUOUS_WALKX,

  KILLED_JOHN_RANKIN,
  ;

  static final List<AchievementCode> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  static final List<AchievementCode> VALUES_COMPLETED() {
    List<AchievementCode> array = new ArrayList<AchievementCode>();
    for (AchievementCode code : AchievementCode.VALUES) {
      if (AchievementCode.achievementType(code) == AchievementType.COMPLETED) {
        array.add(code);
      }
    }
    return Collections.unmodifiableList(array);
  }

  static final List<AchievementCode> VALUES_CONTINUOUS() {
    List<AchievementCode> array = new ArrayList<AchievementCode>();
    for (AchievementCode code : AchievementCode.VALUES) {
      if (AchievementCode.achievementType(code) == AchievementType.CONTINUOUS) {
        array.add(code);
      }
    }
    return Collections.unmodifiableList(array);
  }

  static final List<AchievementCode> VALUES_HIDDEN() {
    List<AchievementCode> array = new ArrayList<AchievementCode>();
    for (AchievementCode code : AchievementCode.VALUES) {
      if (AchievementCode.achievementType(code) == AchievementType.HIDDEN) {
        array.add(code);
      }
    }
    return Collections.unmodifiableList(array);
  }

  public String displayName() {
    return AchievementCode.displayName(this);
  }
  public static String displayName(AchievementCode code) {
    switch(code) {
      // completion
      case COMPLETED_TUTORIAL:
        return "Completed Tutorial";
      // continuous
      case CONTINUOUS_KILLSI:
        return "Killed " + LNZ.achievement_kills_I + " units";
      case CONTINUOUS_KILLSII:
        return "Killed " + LNZ.achievement_kills_II + " units";
      case CONTINUOUS_KILLSIII:
        return "Killed " + LNZ.achievement_kills_III + " units";
      case CONTINUOUS_KILLSIV:
        return "Killed " + LNZ.achievement_kills_IV + " units";
      case CONTINUOUS_KILLSV:
        return "Killed " + LNZ.achievement_kills_V + " units";
      case CONTINUOUS_KILLSVI:
        return "Killed " + LNZ.achievement_kills_VI + " units";
      case CONTINUOUS_KILLSVII:
        return "Killed " + LNZ.achievement_kills_VII + " units";
      case CONTINUOUS_KILLSVIII:
        return "Killed " + LNZ.achievement_kills_VIII + " units";
      case CONTINUOUS_KILLSIX:
        return "Killed " + LNZ.achievement_kills_IX + " units";
      case CONTINUOUS_KILLSX:
        return "Killed " + LNZ.achievement_kills_X + " units";
      case CONTINUOUS_DEATHSI:
        return "Died " + LNZ.achievement_deaths_I + " times";
      case CONTINUOUS_DEATHSII:
        return "Died " + LNZ.achievement_deaths_II + " times";
      case CONTINUOUS_DEATHSIII:
        return "Died " + LNZ.achievement_deaths_III + " times";
      case CONTINUOUS_DEATHSIV:
        return "Died " + LNZ.achievement_deaths_IV + " times";
      case CONTINUOUS_DEATHSV:
        return "Died " + LNZ.achievement_deaths_V + " times";
      case CONTINUOUS_DEATHSVI:
        return "Died " + LNZ.achievement_deaths_VI + " times";
      case CONTINUOUS_DEATHSVII:
        return "Died " + LNZ.achievement_deaths_VII + " times";
      case CONTINUOUS_DEATHSVIII:
        return "Died " + LNZ.achievement_deaths_VIII + " times";
      case CONTINUOUS_DEATHSIX:
        return "Died " + LNZ.achievement_deaths_IX + " times";
      case CONTINUOUS_DEATHSX:
        return "Died " + LNZ.achievement_deaths_X + " times";
      case CONTINUOUS_WALKI:
        return "Walked " + LNZ.achievement_walk_I + " m";
      case CONTINUOUS_WALKII:
        return "Walked " + LNZ.achievement_walk_II + " m";
      case CONTINUOUS_WALKIII:
        return "Walked " + LNZ.achievement_walk_III + " m";
      case CONTINUOUS_WALKIV:
        return "Walked " + LNZ.achievement_walk_IV + " m";
      case CONTINUOUS_WALKV:
        return "Walked " + LNZ.achievement_walk_V + " m";
      case CONTINUOUS_WALKVI:
        return "Walked " + LNZ.achievement_walk_VI + " m";
      case CONTINUOUS_WALKVII:
        return "Walked " + LNZ.achievement_walk_VII + " m";
      case CONTINUOUS_WALKVIII:
        return "Walked " + LNZ.achievement_walk_VIII + " m";
      case CONTINUOUS_WALKIX:
        return "Walked " + LNZ.achievement_walk_IX + " m";
      case CONTINUOUS_WALKX:
        return "Walked " + LNZ.achievement_walk_X + " m";
      // hidden
      case KILLED_JOHN_RANKIN:
        return "Killed John Rankin";
      default:
        return "-- Error --";
    }
  }

  public String display_progress(Profile profile) {
    return AchievementCode.display_progress(this, profile);
  }
  public static String display_progress(AchievementCode code, Profile profile) {
    switch(code) {
      // completion
      case COMPLETED_TUTORIAL:
        return "Completed Tutorial";
      // continuous
      case CONTINUOUS_KILLSI:
        return "Killed " + profile.stats.units_killed + "/" + LNZ.achievement_kills_I + " units";
      case CONTINUOUS_KILLSII:
        return "Killed " + profile.stats.units_killed + "/" + LNZ.achievement_kills_II + " units";
      case CONTINUOUS_KILLSIII:
        return "Killed " + profile.stats.units_killed + "/" + LNZ.achievement_kills_III + " units";
      case CONTINUOUS_KILLSIV:
        return "Killed " + profile.stats.units_killed + "/" + LNZ.achievement_kills_IV + " units";
      case CONTINUOUS_KILLSV:
        return "Killed " + profile.stats.units_killed + "/" + LNZ.achievement_kills_V + " units";
      case CONTINUOUS_KILLSVI:
        return "Killed " + profile.stats.units_killed + "/" + LNZ.achievement_kills_VI + " units";
      case CONTINUOUS_KILLSVII:
        return "Killed " + profile.stats.units_killed + "/" + LNZ.achievement_kills_VII + " units";
      case CONTINUOUS_KILLSVIII:
        return "Killed " + profile.stats.units_killed + "/" + LNZ.achievement_kills_VIII + " units";
      case CONTINUOUS_KILLSIX:
        return "Killed " + profile.stats.units_killed + "/" + LNZ.achievement_kills_IX + " units";
      case CONTINUOUS_KILLSX:
        return "Killed " + profile.stats.units_killed + "/" + LNZ.achievement_kills_X + " units";
      case CONTINUOUS_DEATHSI:
        return "Died " + profile.stats.times_died + "/" + LNZ.achievement_deaths_I + " times";
      case CONTINUOUS_DEATHSII:
        return "Died " + profile.stats.times_died + "/" + LNZ.achievement_deaths_II + " times";
      case CONTINUOUS_DEATHSIII:
        return "Died " + profile.stats.times_died + "/" + LNZ.achievement_deaths_III + " times";
      case CONTINUOUS_DEATHSIV:
        return "Died " + profile.stats.times_died + "/" + LNZ.achievement_deaths_IV + " times";
      case CONTINUOUS_DEATHSV:
        return "Died " + profile.stats.times_died + "/" + LNZ.achievement_deaths_V + " times";
      case CONTINUOUS_DEATHSVI:
        return "Died " + profile.stats.times_died + "/" + LNZ.achievement_deaths_VI + " times";
      case CONTINUOUS_DEATHSVII:
        return "Died " + profile.stats.times_died + "/" + LNZ.achievement_deaths_VII + " times";
      case CONTINUOUS_DEATHSVIII:
        return "Died " + profile.stats.times_died + "/" + LNZ.achievement_deaths_VIII + " times";
      case CONTINUOUS_DEATHSIX:
        return "Died " + profile.stats.times_died + "/" + LNZ.achievement_deaths_IX + " times";
      case CONTINUOUS_DEATHSX:
        return "Died " + profile.stats.times_died + "/" + LNZ.achievement_deaths_X + " times";
      case CONTINUOUS_WALKI:
        return "Walked " + Math.round(profile.stats.distance_walked) + "/" + LNZ.achievement_walk_I + " m";
      case CONTINUOUS_WALKII:
        return "Walked " + Math.round(profile.stats.distance_walked) + "/" + LNZ.achievement_walk_II + " m";
      case CONTINUOUS_WALKIII:
        return "Walked " + Math.round(profile.stats.distance_walked) + "/" + LNZ.achievement_walk_III + " m";
      case CONTINUOUS_WALKIV:
        return "Walked " + Math.round(profile.stats.distance_walked) + "/" + LNZ.achievement_walk_IV + " m";
      case CONTINUOUS_WALKV:
        return "Walked " + Math.round(profile.stats.distance_walked) + "/" + LNZ.achievement_walk_V + " m";
      case CONTINUOUS_WALKVI:
        return "Walked " + Math.round(profile.stats.distance_walked) + "/" + LNZ.achievement_walk_VI + " m";
      case CONTINUOUS_WALKVII:
        return "Walked " + Math.round(profile.stats.distance_walked) + "/" + LNZ.achievement_walk_VII + " m";
      case CONTINUOUS_WALKVIII:
        return "Walked " + Math.round(profile.stats.distance_walked) + "/" + LNZ.achievement_walk_VIII + " m";
      case CONTINUOUS_WALKIX:
        return "Walked " + Math.round(profile.stats.distance_walked) + "/" + LNZ.achievement_walk_IX + " m";
      case CONTINUOUS_WALKX:
        return "Walked " + Math.round(profile.stats.distance_walked) + "/" + LNZ.achievement_walk_X + " m";
      // hidden
      case KILLED_JOHN_RANKIN:
        return "Killed John Rankin";
      default:
        return "-- Error --";
    }
  }

  public boolean achievement_met(Profile profile) {
    return AchievementCode.achievement_met(this, profile);
  }
  public static boolean achievement_met(AchievementCode code, Profile profile) {
    switch(code) {
      // only for continuous
      case CONTINUOUS_KILLSI:
        return profile.stats.units_killed >= LNZ.achievement_kills_I;
      case CONTINUOUS_KILLSII:
        return profile.stats.units_killed >= LNZ.achievement_kills_II;
      case CONTINUOUS_KILLSIII:
        return profile.stats.units_killed >= LNZ.achievement_kills_III;
      case CONTINUOUS_KILLSIV:
        return profile.stats.units_killed >= LNZ.achievement_kills_IV;
      case CONTINUOUS_KILLSV:
        return profile.stats.units_killed >= LNZ.achievement_kills_V;
      case CONTINUOUS_KILLSVI:
        return profile.stats.units_killed >= LNZ.achievement_kills_VI;
      case CONTINUOUS_KILLSVII:
        return profile.stats.units_killed >= LNZ.achievement_kills_VII;
      case CONTINUOUS_KILLSVIII:
        return profile.stats.units_killed >= LNZ.achievement_kills_VIII;
      case CONTINUOUS_KILLSIX:
        return profile.stats.units_killed >= LNZ.achievement_kills_IX;
      case CONTINUOUS_KILLSX:
        return profile.stats.units_killed >= LNZ.achievement_kills_X;
      case CONTINUOUS_DEATHSI:
        return profile.stats.times_died >= LNZ.achievement_deaths_I;
      case CONTINUOUS_DEATHSII:
        return profile.stats.times_died >= LNZ.achievement_deaths_II;
      case CONTINUOUS_DEATHSIII:
        return profile.stats.times_died >= LNZ.achievement_deaths_III;
      case CONTINUOUS_DEATHSIV:
        return profile.stats.times_died >= LNZ.achievement_deaths_VI;
      case CONTINUOUS_DEATHSV:
        return profile.stats.times_died >= LNZ.achievement_deaths_V;
      case CONTINUOUS_DEATHSVI:
        return profile.stats.times_died >= LNZ.achievement_deaths_VI;
      case CONTINUOUS_DEATHSVII:
        return profile.stats.times_died >= LNZ.achievement_deaths_VII;
      case CONTINUOUS_DEATHSVIII:
        return profile.stats.times_died >= LNZ.achievement_deaths_VIII;
      case CONTINUOUS_DEATHSIX:
        return profile.stats.times_died >= LNZ.achievement_deaths_IX;
      case CONTINUOUS_DEATHSX:
        return profile.stats.times_died >= LNZ.achievement_deaths_X;
      case CONTINUOUS_WALKI:
        return profile.stats.distance_walked >= LNZ.achievement_walk_I;
      case CONTINUOUS_WALKII:
        return profile.stats.distance_walked >= LNZ.achievement_walk_II;
      case CONTINUOUS_WALKIII:
        return profile.stats.distance_walked >= LNZ.achievement_walk_III;
      case CONTINUOUS_WALKIV:
        return profile.stats.distance_walked >= LNZ.achievement_walk_IV;
      case CONTINUOUS_WALKV:
        return profile.stats.distance_walked >= LNZ.achievement_walk_V;
      case CONTINUOUS_WALKVI:
        return profile.stats.distance_walked >= LNZ.achievement_walk_VI;
      case CONTINUOUS_WALKVII:
        return profile.stats.distance_walked >= LNZ.achievement_walk_VII;
      case CONTINUOUS_WALKVIII:
        return profile.stats.distance_walked >= LNZ.achievement_walk_VIII;
      case CONTINUOUS_WALKIX:
        return profile.stats.distance_walked >= LNZ.achievement_walk_IX;
      case CONTINUOUS_WALKX:
        return profile.stats.distance_walked >= LNZ.achievement_walk_X;
      default:
        return false;
    }
  }

  public String file_name() {
    return AchievementCode.file_name(this);
  }
  public static String file_name(AchievementCode code) {
    switch(code) {
      // completion
      case COMPLETED_TUTORIAL:
        return "Completed_Tutorial";
      // continuous
      case CONTINUOUS_KILLSI:
        return "CONTINUOUS_KILLSI";
      case CONTINUOUS_KILLSII:
        return "CONTINUOUS_KILLSII";
      case CONTINUOUS_KILLSIII:
        return "CONTINUOUS_KILLSIII";
      case CONTINUOUS_KILLSIV:
        return "CONTINUOUS_KILLSIV";
      case CONTINUOUS_KILLSV:
        return "CONTINUOUS_KILLSV";
      case CONTINUOUS_KILLSVI:
        return "CONTINUOUS_KILLSVI";
      case CONTINUOUS_KILLSVII:
        return "CONTINUOUS_KILLSVII";
      case CONTINUOUS_KILLSVIII:
        return "CONTINUOUS_KILLSVIII";
      case CONTINUOUS_KILLSIX:
        return "CONTINUOUS_KILLSIX";
      case CONTINUOUS_KILLSX:
        return "CONTINUOUS_KILLSX";
      case CONTINUOUS_DEATHSI:
        return "CONTINUOUS_DEATHSI";
      case CONTINUOUS_DEATHSII:
        return "CONTINUOUS_DEATHSII";
      case CONTINUOUS_DEATHSIII:
        return "CONTINUOUS_DEATHSIII";
      case CONTINUOUS_DEATHSIV:
        return "CONTINUOUS_DEATHSIV";
      case CONTINUOUS_DEATHSV:
        return "CONTINUOUS_DEATHSV";
      case CONTINUOUS_DEATHSVI:
        return "CONTINUOUS_DEATHSVI";
      case CONTINUOUS_DEATHSVII:
        return "CONTINUOUS_DEATHSVII";
      case CONTINUOUS_DEATHSVIII:
        return "CONTINUOUS_DEATHSVIII";
      case CONTINUOUS_DEATHSIX:
        return "CONTINUOUS_DEATHSIX";
      case CONTINUOUS_DEATHSX:
        return "CONTINUOUS_DEATHSX";
      case CONTINUOUS_WALKI:
        return "CONTINUOUS_WALKI";
      case CONTINUOUS_WALKII:
        return "CONTINUOUS_WALKII";
      case CONTINUOUS_WALKIII:
        return "CONTINUOUS_WALKIII";
      case CONTINUOUS_WALKIV:
        return "CONTINUOUS_WALKIV";
      case CONTINUOUS_WALKV:
        return "CONTINUOUS_WALKV";
      case CONTINUOUS_WALKVI:
        return "CONTINUOUS_WALKVI";
      case CONTINUOUS_WALKVII:
        return "CONTINUOUS_WALKVII";
      case CONTINUOUS_WALKVIII:
        return "CONTINUOUS_WALKVIII";
      case CONTINUOUS_WALKIX:
        return "CONTINUOUS_WALKIX";
      case CONTINUOUS_WALKX:
        return "CONTINUOUS_WALKX";
      // hidden
      case KILLED_JOHN_RANKIN:
        return "Killed_John_Rankin";
      default:
        return "ERROR";
    }
  }

  public AchievementType achievementType() {
    return AchievementCode.achievementType(this);
  }
  public static AchievementType achievementType(AchievementCode code) {
    switch(code) {
      // completion
      case COMPLETED_TUTORIAL:
      case COMPLETED_FRANCISCAN:
      case COMPLETED_DANSHOUSE:
        return AchievementType.COMPLETED;
      // continuous
      case CONTINUOUS_KILLSI:
      case CONTINUOUS_KILLSII:
      case CONTINUOUS_KILLSIII:
      case CONTINUOUS_KILLSIV:
      case CONTINUOUS_KILLSV:
      case CONTINUOUS_KILLSVI:
      case CONTINUOUS_KILLSVII:
      case CONTINUOUS_KILLSVIII:
      case CONTINUOUS_KILLSIX:
      case CONTINUOUS_KILLSX:
      case CONTINUOUS_DEATHSI:
      case CONTINUOUS_DEATHSII:
      case CONTINUOUS_DEATHSIII:
      case CONTINUOUS_DEATHSIV:
      case CONTINUOUS_DEATHSV:
      case CONTINUOUS_DEATHSVI:
      case CONTINUOUS_DEATHSVII:
      case CONTINUOUS_DEATHSVIII:
      case CONTINUOUS_DEATHSIX:
      case CONTINUOUS_DEATHSX:
      case CONTINUOUS_WALKI:
      case CONTINUOUS_WALKII:
      case CONTINUOUS_WALKIII:
      case CONTINUOUS_WALKIV:
      case CONTINUOUS_WALKV:
      case CONTINUOUS_WALKVI:
      case CONTINUOUS_WALKVII:
      case CONTINUOUS_WALKVIII:
      case CONTINUOUS_WALKIX:
      case CONTINUOUS_WALKX:
        return AchievementType.CONTINUOUS;
      // hidden
      case KILLED_JOHN_RANKIN:
        return AchievementType.HIDDEN;
      default:
        return null;
    }
  }

  public static AchievementCode achievementCode(String display_name) {
    for (AchievementCode code : AchievementCode.VALUES) {
      if (AchievementCode.displayName(code).equals(display_name) ||
        AchievementCode.file_name(code).equals(display_name)) {
        return code;
      }
    }
    return null;
  }
  public static AchievementCode achievementCode(int id) {
    for (AchievementCode code : AchievementCode.VALUES) {
      if (AchievementCode.id(code) == id) {
        return code;
      }
    }
    return null;
  }

  public int tokens() {
    return AchievementCode.tokens(this);
  }
  public static int tokens(AchievementCode code) {
    if (code.achievementType() == AchievementType.HIDDEN) {
      return 3;
    }
    return 1;
  }

  public int id() {
    return AchievementCode.id(this);
  }
  public static int id(AchievementCode code) {
    switch(code) {
      // completion
      case COMPLETED_TUTORIAL:
        return 0;
      case COMPLETED_FRANCISCAN:
        return 1;
      case COMPLETED_DANSHOUSE:
        return 2;
      // continuous
      case CONTINUOUS_KILLSI:
        return 101;
      case CONTINUOUS_KILLSII:
        return 102;
      case CONTINUOUS_KILLSIII:
        return 103;
      case CONTINUOUS_KILLSIV:
        return 104;
      case CONTINUOUS_KILLSV:
        return 105;
      case CONTINUOUS_KILLSVI:
        return 106;
      case CONTINUOUS_KILLSVII:
        return 107;
      case CONTINUOUS_KILLSVIII:
        return 108;
      case CONTINUOUS_KILLSIX:
        return 109;
      case CONTINUOUS_KILLSX:
        return 110;
      case CONTINUOUS_DEATHSI:
        return 111;
      case CONTINUOUS_DEATHSII:
        return 112;
      case CONTINUOUS_DEATHSIII:
        return 113;
      case CONTINUOUS_DEATHSIV:
        return 114;
      case CONTINUOUS_DEATHSV:
        return 115;
      case CONTINUOUS_DEATHSVI:
        return 116;
      case CONTINUOUS_DEATHSVII:
        return 117;
      case CONTINUOUS_DEATHSVIII:
        return 118;
      case CONTINUOUS_DEATHSIX:
        return 119;
      case CONTINUOUS_DEATHSX:
        return 120;
      case CONTINUOUS_WALKI:
        return 121;
      case CONTINUOUS_WALKII:
        return 122;
      case CONTINUOUS_WALKIII:
        return 123;
      case CONTINUOUS_WALKIV:
        return 124;
      case CONTINUOUS_WALKV:
        return 125;
      case CONTINUOUS_WALKVI:
        return 126;
      case CONTINUOUS_WALKVII:
        return 127;
      case CONTINUOUS_WALKVIII:
        return 128;
      case CONTINUOUS_WALKIX:
        return 129;
      case CONTINUOUS_WALKX:
        return 130;
      // hidden
      case KILLED_JOHN_RANKIN:
        return 501;
      default:
        return -1;
    }
  }
}