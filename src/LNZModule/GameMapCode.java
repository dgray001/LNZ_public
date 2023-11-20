package LNZModule;

import java.util.*;

enum GameMapCode {
  ERROR,
  FRANCIS_FLOOR2, FRANCIS_FLOOR1, FRANCIS_GROUND,
  FRONTDOOR_FRONTDOOR, FRONTDOOR_AHIMDOOR, FRONTDOOR_CIRCLE, FRONTDOOR_ABANDONED,
    FRONTDOOR_CHAPEL, FRONTDOOR_CODA, FRONTDOOR_OUTSIDEFF, FRONTDOOR_INSIDEFF,
    FRONTDOOR_HILL, FRONTDOOR_LOT,
  COURTYARD_COURTYARD, COURTYARD_ROAD, COURTYARD_HILLSIDE, COURTYARD_FIELD,
  AHIMDOOR_AHIMDOOR,
  BROTHERSDOOR_BROTHERSDOOR,
  CHAPELDOOR_CHAPELDOOR,
  CUSTODIALDOOR_CUSTODIALDOOR,
  AREA_FERNWOOD
  ;

  private static final List<GameMapCode> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  public String displayName() {
    return GameMapCode.displayName(this);
  }
  public static String displayName(GameMapCode a) {
    switch(a) {
      case FRANCIS_FLOOR2:
        return "Francis Hall, 2nd floor";
      case FRANCIS_FLOOR1:
        return "Francis Hall, 1st floor";
      case FRANCIS_GROUND:
        return "Francis Hall, ground floor";

      case FRONTDOOR_FRONTDOOR:
        return "Outside Francis Hall, front door";
      case FRONTDOOR_AHIMDOOR:
        return "Outside Francis Hall, Ahim door";
      case FRONTDOOR_CIRCLE:
        return "Francisan Campus, the circle";
      case FRONTDOOR_ABANDONED:
        return "Francisan Campus, hillside";
      case FRONTDOOR_HILL:
        return "Francisan Campus, hillside";
      case FRONTDOOR_LOT:
        return "Franciscan Campus, lower parking lot";
      case FRONTDOOR_CHAPEL:
        return "Francisan Campus, outside Chapel";
      case FRONTDOOR_CODA:
        return "Francisan Campus, outside CODA";
      case FRONTDOOR_OUTSIDEFF:
        return "Francisan Campus, outside FFH";
      case FRONTDOOR_INSIDEFF:
        return "Finnegan Fieldhouse";

      case COURTYARD_COURTYARD:
        return "Franciscan Campus, Egan courtyard";
      case COURTYARD_ROAD:
        return "Franciscan Campus, Franciscan Way";
      case COURTYARD_HILLSIDE:
        return "Franciscan Campus, hillside";
      case COURTYARD_FIELD:
        return "Franciscan Campus, front lawn";

      case AHIMDOOR_AHIMDOOR:
        return "Outside Francis Hall, Ahim door";

      case BROTHERSDOOR_BROTHERSDOOR:
        return "Outside Francis Hall, Brother's door";
      case CHAPELDOOR_CHAPELDOOR:
        return "Outside Francis Hall, chapel door";
      case CUSTODIALDOOR_CUSTODIALDOOR:
        return "Outside Francis Hall, custodial door";

      case AREA_FERNWOOD:
        return "Fernwood Forest";

      default:
        return "-- Error --";
    }
  }

  public String fileName() {
    return GameMapCode.fileName(this);
  }
  public static String fileName(GameMapCode a) {
    switch(a) {

      case FRANCIS_FLOOR2:
        return "FRANCIS_FLOOR2";
      case FRANCIS_FLOOR1:
        return "FRANCIS_FLOOR1";
      case FRANCIS_GROUND:
        return "FRANCIS_GROUND";

      case FRONTDOOR_FRONTDOOR:
        return "FRONTDOOR_FRONTDOOR";
      case FRONTDOOR_AHIMDOOR:
        return "FRONTDOOR_AHIMDOOR";
      case FRONTDOOR_CIRCLE:
        return "FRONTDOOR_CIRCLE";
      case FRONTDOOR_ABANDONED:
        return "FRONTDOOR_ABANDONED";
      case FRONTDOOR_HILL:
        return "FRONTDOOR_HILL";
      case FRONTDOOR_LOT:
        return "FRONTDOOR_LOT";
      case FRONTDOOR_CHAPEL:
        return "FRONTDOOR_CHAPEL";
      case FRONTDOOR_CODA:
        return "FRONTDOOR_CODA";
      case FRONTDOOR_OUTSIDEFF:
        return "FRONTDOOR_OUTSIDEFF";
      case FRONTDOOR_INSIDEFF:
        return "FRONTDOOR_INSIDEFF";

      case COURTYARD_COURTYARD:
        return "COURTYARD_COURTYARD";
      case COURTYARD_ROAD:
        return "COURTYARD_ROAD";
      case COURTYARD_HILLSIDE:
        return "COURTYARD_HILLSIDE";
      case COURTYARD_FIELD:
        return "COURTYARD_FIELD";

      case AHIMDOOR_AHIMDOOR:
        return "AHIMDOOR_AHIMDOOR";

      case BROTHERSDOOR_BROTHERSDOOR:
        return "BROTHERSDOOR_BROTHERSDOOR";
      case CHAPELDOOR_CHAPELDOOR:
        return "CHAPELDOOR_CHAPELDOOR";
      case CUSTODIALDOOR_CUSTODIALDOOR:
        return "CUSTODIALDOOR_CUSTODIALDOOR";

      case AREA_FERNWOOD:
        return "AREA_FERNWOOD";

      default:
        return "ERROR";
    }
  }

  public static GameMapCode gameMapCode(String display_name) {
    for (GameMapCode code : GameMapCode.VALUES) {
      if (code == GameMapCode.ERROR) {
        continue;
      }
      if (GameMapCode.displayName(code).equals(display_name) ||
        GameMapCode.fileName(code).equals(display_name)) {
        return code;
      }
    }
    return GameMapCode.ERROR;
  }
}