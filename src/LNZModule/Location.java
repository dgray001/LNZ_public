package LNZModule;

import java.util.*;

enum Location {
  // Non main game
  ERROR, TUTORIAL, TEST_AREA,
  // Ben Nelson Campaign
  FRANCISCAN_FRANCIS, FRANCISCAN_LEV2_FRONTDOOR, FRANCISCAN_LEV2_AHIMDOOR, FRANCISCAN_LEV2_CHAPELDOOR,
  FRANCISCAN_LEV2_BROTHERSDOOR, FRANCISCAN_LEV2_CUSTODIALDOOR, FRANCISCAN_LEV3_KILLEDHECK, FRANCISCAN_LEV3_AROUNDCODA,
  // Dan Gray Campaign
  DANS_HOUSE,
  // Areas
  AREA_FERNWOOD;

  static final List<Location> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  public String displayName() {
    return Location.displayName(this);
  }

  public static String displayName(Location a) {
    switch (a) {
    case TUTORIAL:
      return "Tutorial";
    case FRANCISCAN_FRANCIS:
      return "Francis Hall";
    case FRANCISCAN_LEV2_FRONTDOOR:
    case FRANCISCAN_LEV2_AHIMDOOR:
    case FRANCISCAN_LEV2_CHAPELDOOR:
    case FRANCISCAN_LEV2_BROTHERSDOOR:
    case FRANCISCAN_LEV2_CUSTODIALDOOR:
    case FRANCISCAN_LEV3_KILLEDHECK:
    case FRANCISCAN_LEV3_AROUNDCODA:
      return "Franciscan Campus";
    case DANS_HOUSE:
      return "Dan's House";
    case AREA_FERNWOOD:
      return "Fernwood State Forest";
    default:
      return "-- Error --";
    }
  }

  public String fileName() {
    return Location.fileName(this);
  }

  public static String fileName(Location a) {
    switch (a) {
    case TUTORIAL:
      return "tutorial";
    case FRANCISCAN_FRANCIS:
      return "franciscan_francis";
    case FRANCISCAN_LEV2_FRONTDOOR:
      return "franciscan_lev2_frontdoor";
    case FRANCISCAN_LEV2_AHIMDOOR:
      return "franciscan_lev2_ahimdoor";
    case FRANCISCAN_LEV2_CHAPELDOOR:
      return "franciscan_lev2_chapeldoor";
    case FRANCISCAN_LEV2_BROTHERSDOOR:
      return "franciscan_lev2_brothersdoor";
    case FRANCISCAN_LEV2_CUSTODIALDOOR:
      return "franciscan_lev2_custodialdoor";
    case FRANCISCAN_LEV3_KILLEDHECK:
      return "franciscan_lev3_killedheck";
    case FRANCISCAN_LEV3_AROUNDCODA:
      return "franciscan_lev3_aroundcoda";
    case DANS_HOUSE:
      return "dans_house";
    case AREA_FERNWOOD:
      return "area_fernwood";
    default:
      return "ERROR";
    }
  }

  public static Location location(String display_name) {
    for (Location location : Location.VALUES) {
      if (location == Location.ERROR) {
        continue;
      }
      if (Location.displayName(location).equals(display_name) || Location.fileName(location).equals(display_name)) {
        return location;
      }
    }
    return Location.ERROR;
  }

  // Check which minigame (if any) to start between levels
  public MinigameName minigameAfterCompletion(int completion_code) {
    return Location.minigameAfterCompletion(this, completion_code);
  }

  public static MinigameName minigameAfterCompletion(Location a, int completion_code) {
    MinigameName return_location = null;
    switch (a) {
    case FRANCISCAN_LEV2_FRONTDOOR:
      switch (completion_code) {
      case 1: // ahimdoor car
        return_location = MinigameName.ZAMBOS_CAR_BREAK_DOWN;
        break;
      case 2: // lot boss car
        return_location = MinigameName.ZAMBOS_CAR_BREAK_DOWN;
        break;
      case 5: // behindcaf car
        return_location = MinigameName.ZAMBOS_CAR_BREAK_DOWN;
        break;
      }
      break;
    case FRANCISCAN_LEV3_KILLEDHECK:
      switch (completion_code) {
      case 0: // ctrl-c
        return_location = MinigameName.ZAMBOS_CAR_BREAK_DOWN;
        break;
      case 1: // down hill boss
        return_location = MinigameName.ZAMBOS_CAR_BREAK_DOWN;
        break;
      case 2: // starvaggi car
        return_location = MinigameName.ZAMBOS_CAR_BREAK_DOWN;
        break;
      }
      break;
    case FRANCISCAN_LEV3_AROUNDCODA:
      switch (completion_code) {
      case 0: // ctrl-c
        return_location = MinigameName.ZAMBOS_CAR_BREAK_DOWN;
        break;
      case 1: // down hill boss
        return_location = MinigameName.ZAMBOS_CAR_BREAK_DOWN;
        break;
      }
      break;
    case FRANCISCAN_LEV2_AHIMDOOR:
      switch (completion_code) {
      case 0: // ctrl-c
        return_location = MinigameName.ZAMBOS_CAR_BREAK_DOWN;
        break;
      case 1: // dans car
        return_location = MinigameName.ZAMBOS_CAR_BREAK_DOWN;
        break;
      }
      break;
    case FRANCISCAN_LEV2_CHAPELDOOR:
    case FRANCISCAN_LEV2_BROTHERSDOOR:
    case FRANCISCAN_LEV2_CUSTODIALDOOR:
      switch (completion_code) {
      case 0: // ctrl-c
        return_location = MinigameName.ZAMBOS_CAR_BREAK_DOWN;
        break;
      }
      break;
    default:
      break;
    }
    return return_location;
  }

  // Check which location comes next based on completion_code
  public Location nextLocation(int completion_code) {
    return Location.nextLocation(this, completion_code);
  }

  public static Location nextLocation(Location a, int completion_code) {
    Location return_location = Location.ERROR;
    switch (a) {
    case FRANCISCAN_FRANCIS:
      switch (completion_code) {
      case 0: // ctrl-c
        return_location = Location.FRANCISCAN_LEV2_FRONTDOOR;
        break;
      case 1:
        return_location = Location.FRANCISCAN_LEV2_FRONTDOOR;
        break;
      case 2:
        return_location = Location.FRANCISCAN_LEV2_AHIMDOOR;
        break;
      case 3:
        return_location = Location.FRANCISCAN_LEV2_CHAPELDOOR;
        break;
      case 4:
        return_location = Location.FRANCISCAN_LEV2_BROTHERSDOOR;
        break;
      case 5:
        return_location = Location.FRANCISCAN_LEV2_CUSTODIALDOOR;
        break;
      default:
        return_location = Location.ERROR;
        break;
      }
      break;
    case FRANCISCAN_LEV2_FRONTDOOR:
      switch (completion_code) {
      case 0: // ctrl-c
        return_location = Location.FRANCISCAN_LEV3_KILLEDHECK;
        break;
      case 1: // ahimdoor car
        return_location = Location.AREA_FERNWOOD;
        break;
      case 2: // lot boss car
        return_location = Location.AREA_FERNWOOD;
        break;
      case 3: // killed heck
        return_location = Location.FRANCISCAN_LEV3_KILLEDHECK;
        break;
      case 4: // around coda
        return_location = Location.FRANCISCAN_LEV3_AROUNDCODA;
        break;
      case 5: // behindcaf car
        return_location = Location.AREA_FERNWOOD;
        break;
      default:
        return_location = Location.ERROR;
        break;
      }
      break;
    case FRANCISCAN_LEV3_KILLEDHECK:
      switch (completion_code) {
      case 0: // ctrl-c
        return_location = Location.AREA_FERNWOOD;
        break;
      case 1: // down hill boss
        return_location = Location.AREA_FERNWOOD;
        break;
      case 2: // starvaggi car
        return_location = Location.AREA_FERNWOOD;
        break;
      default:
        return_location = Location.ERROR;
        break;
      }
      break;
    case FRANCISCAN_LEV3_AROUNDCODA:
      switch (completion_code) {
      case 0: // ctrl-c
        return_location = Location.AREA_FERNWOOD;
        break;
      case 1: // down hill boss
        return_location = Location.AREA_FERNWOOD;
        break;
      default:
        return_location = Location.ERROR;
        break;
      }
      break;
    case FRANCISCAN_LEV2_AHIMDOOR:
      switch (completion_code) {
      case 0: // ctrl-c
        return_location = Location.AREA_FERNWOOD;
        break;
      case 1: // dans car
        return_location = Location.AREA_FERNWOOD;
        break;
      default:
        return_location = Location.ERROR;
        break;
      }
      break;
    case FRANCISCAN_LEV2_CHAPELDOOR:
    case FRANCISCAN_LEV2_BROTHERSDOOR:
    case FRANCISCAN_LEV2_CUSTODIALDOOR:
    case DANS_HOUSE:
      switch (completion_code) {
      case 0: // ctrl-c
        return_location = Location.AREA_FERNWOOD;
        break;
      default:
        return_location = Location.ERROR;
        break;
      }
      break;
    default:
      return_location = Location.ERROR;
      break;
    }
    return return_location;
  }

  public boolean isCampaignStart() {
    return Location.isCampaignStart(this);
  }

  public static boolean isCampaignStart(Location a) {
    switch (a) {
    case FRANCISCAN_FRANCIS:
    case DANS_HOUSE:
      return true;
    default:
      return false;
    }
  }

  public Location getCampaignStart() {
    return Location.getCampaignStart(this);
  }

  public static Location getCampaignStart(Location a) {
    switch (a) {
    case FRANCISCAN_FRANCIS:
    case FRANCISCAN_LEV2_FRONTDOOR:
    case FRANCISCAN_LEV2_AHIMDOOR:
    case FRANCISCAN_LEV2_CHAPELDOOR:
    case FRANCISCAN_LEV2_BROTHERSDOOR:
    case FRANCISCAN_LEV2_CUSTODIALDOOR:
    case FRANCISCAN_LEV3_KILLEDHECK:
    case FRANCISCAN_LEV3_AROUNDCODA:
      return Location.FRANCISCAN_FRANCIS;
    case DANS_HOUSE:
      return Location.DANS_HOUSE;
    default:
      return Location.ERROR;
    }
  }

  public String getCampaignName() {
    return Location.getCampaignName(this);
  }

  public static String getCampaignName(Location a) {
    switch (a) {
    case FRANCISCAN_FRANCIS:
    case FRANCISCAN_LEV2_FRONTDOOR:
    case FRANCISCAN_LEV2_AHIMDOOR:
    case FRANCISCAN_LEV2_CHAPELDOOR:
    case FRANCISCAN_LEV2_BROTHERSDOOR:
    case FRANCISCAN_LEV2_CUSTODIALDOOR:
    case FRANCISCAN_LEV3_KILLEDHECK:
    case FRANCISCAN_LEV3_AROUNDCODA:
      return "Franciscan University";
    case DANS_HOUSE:
      return "Water Works Rd";
    case AREA_FERNWOOD:
      return "Fernwood Forest";
    default:
      return "-- Error --";
    }
  }

  public String getCampaignSubtitle() {
    return Location.getCampaignSubtitle(this);
  }

  public static String getCampaignSubtitle(Location a) {
    switch (a) {
    case FRANCISCAN_FRANCIS:
    case FRANCISCAN_LEV2_FRONTDOOR:
    case FRANCISCAN_LEV2_AHIMDOOR:
    case FRANCISCAN_LEV2_CHAPELDOOR:
    case FRANCISCAN_LEV2_BROTHERSDOOR:
    case FRANCISCAN_LEV2_CUSTODIALDOOR:
    case FRANCISCAN_LEV3_KILLEDHECK:
    case FRANCISCAN_LEV3_AROUNDCODA:
      return "Ben Nelson Campaign";
    case DANS_HOUSE:
      return "Dan Gray Campaign";
    case AREA_FERNWOOD:
      return "Area - Forest";
    default:
      return "-- Error --";
    }
  }

  public String campaignImagePath() {
    return Location.campaignImagePath(this);
  }

  public static String campaignImagePath(Location a) {
    switch (a) {
    case FRANCISCAN_FRANCIS:
    case FRANCISCAN_LEV2_FRONTDOOR:
    case FRANCISCAN_LEV2_AHIMDOOR:
    case FRANCISCAN_LEV2_CHAPELDOOR:
    case FRANCISCAN_LEV2_BROTHERSDOOR:
    case FRANCISCAN_LEV2_CUSTODIALDOOR:
    case FRANCISCAN_LEV3_KILLEDHECK:
    case FRANCISCAN_LEV3_AROUNDCODA:
      return "icons/franciscan.png";
    case DANS_HOUSE:
      return "icons/water_works.png";
    case AREA_FERNWOOD:
      return "icons/fernwood.jpg";
    default:
      return "transparent.png";
    }
  }

  public String campaignDescription() {
    return Location.campaignDescription(this);
  }

  public static String campaignDescription(Location a) {
    switch (a) {
    case FRANCISCAN_FRANCIS:
    case FRANCISCAN_LEV2_FRONTDOOR:
    case FRANCISCAN_LEV2_AHIMDOOR:
    case FRANCISCAN_LEV2_CHAPELDOOR:
    case FRANCISCAN_LEV2_BROTHERSDOOR:
    case FRANCISCAN_LEV2_CUSTODIALDOOR:
    case FRANCISCAN_LEV3_KILLEDHECK:
    case FRANCISCAN_LEV3_AROUNDCODA:
      return "Academically excellent, passionately Catholic, thoroughly "
          + "zombified, Franciscan University is the flagship Catholic University "
          + "of the Midwest, boasting famous names like Scott Hahn and Cathy "
          + "Heck. This is also where Ben Nelson was at the time of the zombie "
          + "apocalypse. Start in the Soldier's common room, where Ben was the "
          + "coordinator and fearless leader, and venture out into the university "
          + "campus, slaying Frannies left and right like you're some kind of vigilante Holy Ghost.";
    case DANS_HOUSE:
      return "Water Works Rd was annexed by the State of Texas in 1997, by "
          + "Dan's legendary-but-now-a-brainless-zombie the Great Redneck Philip "
          + "of Denison. Even if you can kill Dan's zombified family, escape the "
          + "sexy clutches of Dan's homosexual dog, and not get gored by a wild "
          + "yak, you'll be hard-pressed to make it out of Water Works Rd with "
          + "a working vehicle. Countless redneck zombies are down yonder by "
          + "the fishing hole and the road is blocked by fallen trees.\nAlso, "
          + "one last piece of advice: if the cop says to drop the rake, just drop the useless rake man.";
    case AREA_FERNWOOD:
      return "In the backdrop of rural Ohio Valley lies an ancient forest "
          + "maintained by state park rangers. This vast woodland area might "
          + "not be free of zombies, but still maintains a healthy wildlife "
          + "habitat and much fertile ground for plants. If man-made supplies "
          + "are needed, simply raid a park ranger outpost or hiker campground.";
    default:
      return "";
    }
  }

  public boolean isArea() {
    return Location.isArea(this);
  }

  public static boolean isArea(Location a) {
    switch (a) {
    case TEST_AREA:
    case AREA_FERNWOOD:
      return true;
    default:
      return false;
    }
  }

  public String levelVsAreaStringLabel() {
    return Location.levelVsAreaStringLabel(this);
  }

  public static String levelVsAreaStringLabel(Location a) {
    if (Location.isArea(a)) {
      return "Area";
    }
    return "Level";
  }

  public Location areaLocation() {
    return Location.areaLocation(this);
  }

  public static Location areaLocation(Location a) {
    switch (a) {
    case FRANCISCAN_FRANCIS:
    case FRANCISCAN_LEV2_FRONTDOOR:
    case FRANCISCAN_LEV2_AHIMDOOR:
    case FRANCISCAN_LEV2_CHAPELDOOR:
    case FRANCISCAN_LEV2_BROTHERSDOOR:
    case FRANCISCAN_LEV2_CUSTODIALDOOR:
    case FRANCISCAN_LEV3_KILLEDHECK:
    case FRANCISCAN_LEV3_AROUNDCODA:
    case DANS_HOUSE:
      return Location.AREA_FERNWOOD;
    default:
      return Location.ERROR;
    }
  }

  public ArrayList<Location> locationsFromArea() {
    return Location.locationsFromArea(this);
  }

  public static ArrayList<Location> locationsFromArea(Location a) {
    ArrayList<Location> locations = new ArrayList<Location>();
    switch (a) {
    case AREA_FERNWOOD:
      locations.add(Location.FRANCISCAN_FRANCIS);
      locations.add(Location.DANS_HOUSE);
      break;
    default:
      break;
    }
    return locations;
  }

  public double worldMapLocationX() {
    return Location.worldMapLocationX(this);
  }

  public static double worldMapLocationX(Location a) {
    switch (a) {
    case FRANCISCAN_FRANCIS:
      return 0.2758333;
    case DANS_HOUSE:
      return 0.2736666;
    case AREA_FERNWOOD:
      return 0.275;
    default:
      return -1;
    }
  }

  public double worldMapLocationY() {
    return Location.worldMapLocationY(this);
  }

  public static double worldMapLocationY(Location a) {
    switch (a) {
    case FRANCISCAN_FRANCIS:
      return 0.2763333;
    case DANS_HOUSE:
      return 0.2756666;
    case AREA_FERNWOOD:
      return 0.2783333;
    default:
      return -1;
    }
  }
}