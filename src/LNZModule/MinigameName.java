package LNZModule;

import java.util.*;

enum MinigameName {
  CHESS, ZAMBOS_CAR_BREAK_DOWN,
  ;

  public static final List<MinigameName> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  public String displayName() {
    return MinigameName.displayName(this);
  }
  public static String displayName(MinigameName code) {
    switch(code) {
      case CHESS:
        return "Chess";
      case ZAMBOS_CAR_BREAK_DOWN:
        return "Zambos:\nnight-time";
      default:
        return "-- Error --";
    }
  }

  public String fileName() {
    return MinigameName.fileName(this);
  }
  public static String fileName(MinigameName code) {
    switch(code) {
      case CHESS:
        return "CHESS";
      case ZAMBOS_CAR_BREAK_DOWN:
        return "ZAMBOS_CAR_BREAK_DOWN";
      default:
        return "ERROR";
    }
  }

  public static MinigameName minigameName(String display_name) {
    for (MinigameName code : MinigameName.VALUES) {
      if (MinigameName.displayName(code).equals(display_name) ||
        MinigameName.fileName(code).equals(display_name)) {
        return code;
      }
    }
    return null;
  }

  public String imagePath() {
    return MinigameName.imagePath(this);
  }
  public static String imagePath(MinigameName code) {
    switch(code) {
      case CHESS:
        return "minigames/chess/logo.png";
      default:
        return "minigames/default.png";
    }
  }

  public String launchAfterLevelDescription() {
    return MinigameName.launchAfterLevelDescription(this);
  }
  public static String launchAfterLevelDescription(MinigameName code) {
    switch(code) {
      case CHESS:
        return "";
      case ZAMBOS_CAR_BREAK_DOWN:
        return "As you traveled from Steubenville, night fell and zombies " +
          "descended into your position.";
      default:
        return "";
    }
  }
}
