package LNZModule;

import java.util.*;

enum PlayerTreeCode {
  CAN_PLAY,
  UNLOCK_DAN, UNLOCK_JF, UNLOCK_SPINNY, UNLOCK_MATTUS, UNLOCK_PATRICK,
  XP_I, XP_II, XP_III, XP_IV, XP_V,
  MAGNETIC_WALLET, MAGNETIC_HANDS,
  HEALTHBARS, ENEMY_INSIGHTI, ENEMY_INSIGHTII, FARMING_INSIGHT,
  ;
  protected static final List<PlayerTreeCode> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  public String message() {
    switch(this) {
      case CAN_PLAY:
        return "Can launch game";
      case UNLOCK_DAN:
        return "Can play " + HeroCode.displayName(HeroCode.DAN);
      case UNLOCK_JF:
        return "Can play " + HeroCode.displayName(HeroCode.JF);
      case UNLOCK_SPINNY:
        return "Can play " + HeroCode.displayName(HeroCode.SPINNY);
      case UNLOCK_MATTUS:
        return "Can play " + HeroCode.displayName(HeroCode.MATTUS);
      case UNLOCK_PATRICK:
        return "Can play " + HeroCode.displayName(HeroCode.PATRICK);
      case XP_I:
      case XP_II:
      case XP_III:
      case XP_IV:
      case XP_V:
        return "Increase XP gained";
      case MAGNETIC_WALLET:
        return "Auto-deposit money";
      case MAGNETIC_HANDS:
        return "Auto-pickup items";
      case HEALTHBARS:
        return "Can toggle healthbars";
      case ENEMY_INSIGHTI:
        return "View unit stats";
      case ENEMY_INSIGHTII:
        return "View advanced unit stats";
      case FARMING_INSIGHT:
        return "View farming stats";
      default:
        return "";
    }
  }

  public String display_name() {
    switch(this) {
      case CAN_PLAY:
        return "Launch Game";
      case UNLOCK_DAN:
        return "Unlock Dan";
      case UNLOCK_JF:
        return "Unlock JIF";
      case UNLOCK_SPINNY:
        return "Unlock Spinny";
      case UNLOCK_MATTUS:
        return "Unlock Mattus";
      case UNLOCK_PATRICK:
        return "Unlock Jeremiah";
      case XP_I:
      case XP_II:
      case XP_III:
      case XP_IV:
      case XP_V:
        return "XP Multiplier";
      case MAGNETIC_WALLET:
        return "Auto Deposit";
      case MAGNETIC_HANDS:
        return "Auto Pickup";
      case HEALTHBARS:
        return "Toggle Healthbars";
      case ENEMY_INSIGHTI:
        return "Basic Stats";
      case ENEMY_INSIGHTII:
        return "Advanced Stats";
      case FARMING_INSIGHT:
        return "Farming Stats";
      default:
        return "";
    }
  }

  public String file_name() {
    switch(this) {
      case CAN_PLAY:
        return "Launch_Game";
      case UNLOCK_DAN:
        return "UNLOCK_DAN";
      case UNLOCK_JF:
        return "UNLOCK_JF";
      case UNLOCK_SPINNY:
        return "UNLOCK_SPINNY";
      case UNLOCK_MATTUS:
        return "UNLOCK_MATTUS";
      case UNLOCK_PATRICK:
        return "UNLOCK_PATRICK";
      case XP_I:
        return "XP_I";
      case XP_II:
        return "XP_II";
      case XP_III:
        return "XP_III";
      case XP_IV:
        return "XP_IV";
      case XP_V:
        return "XP_V";
      case MAGNETIC_WALLET:
        return "MAGNETIC_WALLET";
      case MAGNETIC_HANDS:
        return "MAGNETIC_HANDS";
      case HEALTHBARS:
        return "HEALTHBARS";
      case ENEMY_INSIGHTI:
        return "ENEMY_INSIGHTI";
      case ENEMY_INSIGHTII:
        return "ENEMY_INSIGHTII";
      case FARMING_INSIGHT:
        return "FARMING_INSIGHT";
      default:
        return "";
    }
  }

  public String description() {
    switch(this) {
      case CAN_PLAY:
        return "Unlocking this perk allows you to start the storyline and " +
          "unlocks Ben Nelson.";
      case UNLOCK_DAN:
        return "Will be available in future update.";//"Unlocks Dan and the accompanying campaign.";
      case UNLOCK_JF:
        return "Will be available in future update.";//"Unlocks JIF and the accompanying campaign.";
      case UNLOCK_SPINNY:
        return "Will be available in future update.";//"Unlocks Spinny and the accompanying campaign.";
      case UNLOCK_MATTUS:
        return "Will be available in future update.";//"Unlocks Mad Dog Mattus and the accompanying campaign.";
      case UNLOCK_PATRICK:
        return "Will be available in future update.";//"Unlocks Jeremiah and the accompanying campaign.";
      case XP_I:
        return "This perk increases XP gained for all heroes from all sources " +
          "by " + Math.round((LNZ.profile_xpMultiplierI-1)*100) + "%.";
      case XP_II:
        return "This perk increases XP gained for all heroes from all sources " +
          "by " + Math.round((LNZ.profile_xpMultiplierII-1)*100) + "%.";
      case XP_III:
        return "This perk increases XP gained for all heroes from all sources " +
          "by " + Math.round((LNZ.profile_xpMultiplierIII-1)*100) + "%.";
      case XP_IV:
        return "This perk increases XP gained for all heroes from all sources " +
          "by " + Math.round((LNZ.profile_xpMultiplierIV-1)*100) + "%.";
      case XP_V:
        return "This perk increases XP gained for all heroes from all sources " +
          "by " + Math.round((LNZ.profile_xpMultiplierV-1)*100) + "%.";
      case MAGNETIC_WALLET:
        return "Unlocking this perk allows you to auto-deposit money into " +
          "your hero's wallet. When toggled, any money picked up will be " +
          "auto-deposited into your wallet.";
      case MAGNETIC_HANDS:
        return "Unlocking this perk allows you to auto-pickup items. When " +
          "toggled, moving near an item will place it in your hero's inventory.";
      case HEALTHBARS:
        return "This perk allows you to toggle healthbars above units in-game.";
      case ENEMY_INSIGHTI:
        return "This perk allows you to view basic stats on other units in-game.";
      case ENEMY_INSIGHTII:
        return "This perk allows you to view advanced stats on other units in-game.";
      case FARMING_INSIGHT:
        return "This allows you to view farming stats of plants / tilled soil in-game.";
      default:
        return "";
    }
  }

  public int cost() {
    switch(this) {
      case CAN_PLAY:
        return 1;
      case UNLOCK_DAN:
        return 300;
      case UNLOCK_JF:
      case UNLOCK_SPINNY:
      case UNLOCK_MATTUS:
      case UNLOCK_PATRICK:
        return 300;
      case XP_I:
        return 2;
      case XP_II:
        return 4;
      case XP_III:
        return 8;
      case XP_IV:
        return 16;
      case XP_V:
        return 32;
      case MAGNETIC_WALLET:
        return 4;
      case MAGNETIC_HANDS:
        return 8;
      case HEALTHBARS:
        return 1;
      case ENEMY_INSIGHTI:
        return 3;
      case ENEMY_INSIGHTII:
        return 5;
      case FARMING_INSIGHT:
        return 3;
      default:
        return 0;
    }
  }

  public static PlayerTreeCode code(String display_name) {
    for (PlayerTreeCode code : PlayerTreeCode.VALUES) {
      if (code.display_name().equals(display_name) ||
        code.file_name().equals(display_name)) {
        return code;
      }
    }
    return null;
  }
}