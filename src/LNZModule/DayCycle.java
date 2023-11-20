package LNZModule;

enum DayCycle {
  DAWN, DAY, DUSK, NIGHT;

  public static double dayTimeStart(DayCycle time) {
    switch(time) {
      case DAWN:
        return 6;
      case DAY:
        return 6.6;
      case DUSK:
        return 20.5;
      case NIGHT:
        return 21;
    }
    return 0;
  }

  public static DayCycle dayTime(double time) {
    if (time >= DayCycle.dayTimeStart(DayCycle.NIGHT)) {
      return DayCycle.NIGHT;
    }
    else if (time >= DayCycle.dayTimeStart(DayCycle.DUSK)) {
      return DayCycle.DUSK;
    }
    else if (time >= DayCycle.dayTimeStart(DayCycle.DAY)) {
      return DayCycle.DAY;
    }
    else if (time >= DayCycle.dayTimeStart(DayCycle.DAWN)) {
      return DayCycle.DAWN;
    }
    else {
      return DayCycle.NIGHT;
    }
  }

  public static double lightFraction(double time) {
    if (time > 21) {
      return LNZ.level_nightLightLevel;
    }
    else if (time > 20.5) {
      return LNZ.level_nightLightLevel + 2.0 * (21 - time) *
        (LNZ.level_dayLightLevel - LNZ.level_nightLightLevel);
    }
    else if (time > 6.5) {
      return LNZ.level_dayLightLevel;
    }
    else if (time > 6) {
      return LNZ.level_nightLightLevel + 2.0 * (time - 6) *
        (LNZ.level_dayLightLevel - LNZ.level_nightLightLevel);
    }
    else {
      return LNZ.level_nightLightLevel;
    }
  }
}