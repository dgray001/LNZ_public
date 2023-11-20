package LNZModule;

import java.util.*;

enum HeroTreeCode {
  INVENTORYI, PASSIVEI, AI, SI, DI, FI, PASSIVEII, AII, SII, DII, FII,
  HEALTHI, ATTACKI, DEFENSEI, PIERCINGI, SPEEDI, SIGHTI, TENACITYI, AGILITYI, MAGICI,
    RESISTANCEI, PENETRATIONI, HEALTHII, ATTACKII, DEFENSEII, PIERCINGII, SPEEDII,
    SIGHTII, TENACITYII, AGILITYII, MAGICII, RESISTANCEII, PENETRATIONII, HEALTHIII,
  OFFHAND, BELTI, BELTII, INVENTORYII, INVENTORY_BARI, INVENTORY_BARII,
  CRAFTI, CRAFTII_ROW, CRAFTII_COL, CRAFTIII_ROW, CRAFTIII_COL,
  FOLLOWERI,
  ;
  protected static final List<HeroTreeCode> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  public String fileName() {
    switch(this) {
      case INVENTORYI:
        return "Inventory";
      case PASSIVEI:
        return "Passive";
      case AI:
        return "A";
      case SI:
        return "S";
      case DI:
        return "D";
      case FI:
        return "F";
      case PASSIVEII:
        return "PassiveII";
      case AII:
        return "AII";
      case SII:
        return "SII";
      case DII:
        return "DII";
      case FII:
        return "FII";
      case HEALTHI:
        return "Health";
      case ATTACKI:
        return "Attack";
      case DEFENSEI:
        return "Defense";
      case PIERCINGI:
        return "Piercing";
      case SPEEDI:
        return "Speed";
      case SIGHTI:
        return "Sight";
      case TENACITYI:
        return "Tenacity";
      case AGILITYI:
        return "Agility";
      case MAGICI:
        return "Magic";
      case RESISTANCEI:
        return "Resistance";
      case PENETRATIONI:
        return "Penetration";
      case HEALTHII:
        return "HealthII";
      case ATTACKII:
        return "AttackII";
      case DEFENSEII:
        return "DefenseII";
      case PIERCINGII:
        return "PiercingII";
      case SPEEDII:
        return "SpeedII";
      case SIGHTII:
        return "SightII";
      case TENACITYII:
        return "TenacityII";
      case AGILITYII:
        return "AgilityII";
      case MAGICII:
        return "MagicII";
      case RESISTANCEII:
        return "ResistanceII";
      case PENETRATIONII:
        return "PenetrationII";
      case HEALTHIII:
        return "HealthIII";
      case OFFHAND:
        return "Offhand";
      case BELTI:
        return "Belt";
      case BELTII:
        return "BeltII";
      case INVENTORYII:
        return "InventoryII";
      case INVENTORY_BARI:
        return "InventoryBar";
      case INVENTORY_BARII:
        return "InventoryBarII";
      case CRAFTI:
        return "CraftI";
      case CRAFTII_ROW:
        return "CraftII_row";
      case CRAFTII_COL:
        return "CraftII_col";
      case CRAFTIII_ROW:
        return "CraftIII_row";
      case CRAFTIII_COL:
        return "CraftIII_col";
      case FOLLOWERI:
        return "Follower";
      default:
        return "--Error--";
    }
  }

  public static HeroTreeCode codeFromId(int id) {
    switch(id) {
      case 0:
        return HeroTreeCode.INVENTORYI;
      case 1:
        return HeroTreeCode.PASSIVEI;
      case 2:
        return HeroTreeCode.AI;
      case 3:
        return HeroTreeCode.SI;
      case 4:
        return HeroTreeCode.DI;
      case 5:
        return HeroTreeCode.FI;
      case 6:
        return HeroTreeCode.PASSIVEII;
      case 7:
        return HeroTreeCode.AII;
      case 8:
        return HeroTreeCode.SII;
      case 9:
        return HeroTreeCode.DII;
      case 10:
        return HeroTreeCode.FII;
      case 11:
        return HeroTreeCode.HEALTHI;
      case 12:
        return HeroTreeCode.ATTACKI;
      case 13:
        return HeroTreeCode.DEFENSEI;
      case 14:
        return HeroTreeCode.PIERCINGI;
      case 15:
        return HeroTreeCode.SPEEDI;
      case 16:
        return HeroTreeCode.SIGHTI;
      case 17:
        return HeroTreeCode.TENACITYI;
      case 18:
        return HeroTreeCode.AGILITYI;
      case 19:
        return HeroTreeCode.MAGICI;
      case 20:
        return HeroTreeCode.RESISTANCEI;
      case 21:
        return HeroTreeCode.PENETRATIONI;
      case 22:
        return HeroTreeCode.HEALTHII;
      case 23:
        return HeroTreeCode.ATTACKII;
      case 24:
        return HeroTreeCode.DEFENSEII;
      case 25:
        return HeroTreeCode.PIERCINGII;
      case 26:
        return HeroTreeCode.SPEEDII;
      case 27:
        return HeroTreeCode.SIGHTII;
      case 28:
        return HeroTreeCode.TENACITYII;
      case 29:
        return HeroTreeCode.AGILITYII;
      case 30:
        return HeroTreeCode.MAGICII;
      case 31:
        return HeroTreeCode.RESISTANCEII;
      case 32:
        return HeroTreeCode.PENETRATIONII;
      case 33:
        return HeroTreeCode.HEALTHIII;
      case 34:
        return HeroTreeCode.OFFHAND;
      case 35:
        return HeroTreeCode.BELTI;
      case 36:
        return HeroTreeCode.BELTII;
      case 37:
        return HeroTreeCode.INVENTORYII;
      case 38:
        return HeroTreeCode.INVENTORY_BARI;
      case 39:
        return HeroTreeCode.INVENTORY_BARII;
      case 40:
        return HeroTreeCode.FOLLOWERI;
      case 41:
        return HeroTreeCode.CRAFTI;
      case 42:
        return HeroTreeCode.CRAFTII_ROW;
      case 43:
        return HeroTreeCode.CRAFTII_COL;
      case 44:
        return HeroTreeCode.CRAFTIII_ROW;
      case 45:
        return HeroTreeCode.CRAFTIII_COL;
      default:
        return HeroTreeCode.INVENTORYI;
    }
  }

  public static HeroTreeCode code(String display_name) {
    for (HeroTreeCode code : HeroTreeCode.VALUES) {
      if (code.fileName().equals(display_name)) {
        return code;
      }
    }
    return null;
  }
}