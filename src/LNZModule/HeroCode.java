package LNZModule;

import java.util.*;

enum HeroCode {
  ERROR, BEN, DAN, JF, SPINNY, MATTUS, PATRICK;

  private static final List<HeroCode> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  public String displayName() {
    return HeroCode.displayName(this);
  }
  static public String displayName(HeroCode code) {
    switch(code) {
      case BEN:
        return "Ben Nelson";
      case DAN:
        return "Dan Gray";
      case JF:
        return "JIF";
      case SPINNY:
        return "Mark Spinny";
      case MATTUS:
        return "Mad Dog Mattus";
      case PATRICK:
        return "Jeremiah";
      default:
        return "-- Error --";
    }
  }

  public Element element() {
    return HeroCode.element(this);
  }
  static public Element element(HeroCode code) {
    switch(code) {
      case BEN:
        return Element.GRAY;
      case DAN:
        return Element.BROWN;
      case JF:
        return Element.CYAN;
      case SPINNY:
        return Element.RED;
      case MATTUS:
        return Element.MAGENTA;
      case PATRICK:
        return Element.GRAY;
      default:
        return Element.GRAY;
    }
  }

  public String title() {
    return HeroCode.title(this);
  }
  static public String title(HeroCode code) {
    switch(code) {
      case BEN:
        return "The Rage of Wisconsin";
      case DAN:
        return "The Half-Frog of Hopedale";
      case JF:
        return "";
      case SPINNY:
        return "The Scourge of Sinners";
      case MATTUS:
        return "";
      case PATRICK:
        return "";
      default:
        return "-- Error --";
    }
  }

  public String description() {
    return HeroCode.description(this);
  }
  static public String description(HeroCode code) {
    switch(code) {
      case BEN:
        return "According to legend, Ben simply glared at the nurse who " +
          "delivered him and she went rave starking mad. Like the kind of mad " +
          "where she quit her job and moved to Canada. But not like the mad " +
          "where she has to suppress the desire to kill everything that moves. " +
          "No, that second type of mad is more descriptive of Ben, whose " +
          "suppressed rage has been slowing taking ground from his sanity for " +
          "years. Don't be fooled by the slow, lazy, dorky-looking kid he " +
          "seems, Ben exists with a wild, raging nature which will never stop " +
          "trying to tear you to pieces once he finds the idea. Evidence of " +
          "this is found when you realize he plays Dark Souls.";
      case DAN:
        return "Once a socially-awkward, yo-yoing little boy, Dan Gray used " +
          "his knowledge of chemisty to concoct a concentrated frog serum. " +
          "He is now a terrifying human-frog hybrid with great thunder " +
          "thighs and a voracious appetite. He knows when to hide from an " +
          "enemy and when to pounce with frog-like fury. If his enemies try " +
          "running, Dan will easily catch them with his enormous (and most " +
          "certainly underbrushed) blood-red tongue.";
      case JF:
        return "Hide your husbands; hide your wives, because the one and only " +
          "heart breaking, love taking, tool making lean and mean country boy " +
          "is here. The epitome of modern American nationalism, JIF doesn't " +
          "need a mandate, minute, or man barking orders to defend his country " +
          "from danger. Soon after zombies overran continental United States, " +
          "JIF became head of the nation's finest paramilitary force. If he's " +
          "not chopping off the head of a normie zombie with an ax he hand-" +
          "crafted in his backyard, you can find him cracking a cold one with " +
          "the BOIZ while smokin' these meats.";
      case SPINNY:
        return "The incarnation of Catholic guilt in the flesh, Spinny roams " +
          "Earth looking for his next victim to cleanse. Any who stand in " +
          "his way find themselves engulfed in the fires of Hell. Even Pope " +
          "Francis, after trying to excommunicate Spinny, was charred to holy " +
          "toast before Spinny declared Francis an anti-pope and himself the " +
          "true Pope. Ever since, the entire Catholic Church has supported " +
          "Spinny, since Right of Conquest is no less than a Biblical mandate.";
      case MATTUS:
        return "Some say he is the rebirth of General Lee, others realize Lee " +
          "was less on the loud stuff and more on the proud stuff. Regardless, " +
          "Mad Dog Mattus became the overall commander of the New People's Freedon " +
          "Confederacy of American Workers and also States, which Mattus " +
          "believes is exactly like the Confederacy Lee once commanded. " +
          "Mattus obtained this position after falling into Georgia's largest " +
          "nuclear reactor, and emerging a tall-but-still-a-little-chubby " +
          "nuclear-emitting being with great insight in leading armies.";
      case PATRICK:
        return "Jeremiah has always had a bit of an identity crisis, at least " +
          "in the eyes of his beholders. Sometimes he even claims his name " +
          "isn't Jeremiah. Most of his hobbies involve things only gay trans " +
          "1/8 latinx half-American men are into, but if you dare try making " +
          "fun of him for any of it he'll mop the floor with your ass (since " +
          "he doesn't have one) without breaking a sweat. Although if you make " +
          "fun of him in a normal voice he probably won't hear you since " +
          "he has to keep pretending he's deaf as part of his identity.";
      default:
        return "-- Error --";
    }
  }

  public String file_name() {
    return HeroCode.file_name(this);
  }
  static public String file_name(HeroCode code) {
    switch(code) {
      case BEN:
        return "BEN";
      case DAN:
        return "DAN";
      case JF:
        return "JF";
      case SPINNY:
        return "SPINNY";
      case MATTUS:
        return "MATTUS";
      case PATRICK:
        return "PATRICK";
      default:
        return "ERROR";
    }
  }

  public int unit_id() {
    return HeroCode.unit_id(this);
  }
  static public int unit_id(HeroCode code) {
    switch(code) {
      case BEN:
        return 1101;
      case DAN:
        return 1102;
      case JF:
        return 1103;
      case SPINNY:
        return 1104;
      case MATTUS:
        return 1105;
      case PATRICK:
        return 1106;
      default:
        return 1100;
    }
  }

  public String imagePathHeader() {
    return HeroCode.imagePathHeader(this);
  }
  static public String imagePathHeader(HeroCode code) {
    switch(code) {
      case BEN:
        return "ben";
      case DAN:
        return "dan";
      case JF:
        return "jf";
      case SPINNY:
        return "spinny";
      case MATTUS:
        return "mattus";
      case PATRICK:
        return "patrick";
      default:
        return "default";
    }
  }

  public String getImagePath() {
    return HeroCode.getImagePath(this);
  }
  public String getImagePath(boolean ben_has_eyes) {
    return HeroCode.getImagePath(this, ben_has_eyes);
  }
  public String getImagePath(boolean ben_has_eyes, boolean circle_image) {
    return HeroCode.getImagePath(this, ben_has_eyes, circle_image);
  }
  static public String getImagePath(HeroCode code) {
    return HeroCode.getImagePath(code, true);
  }
  static public String getImagePath(HeroCode code, boolean ben_has_eyes) {
    return HeroCode.getImagePath(code, ben_has_eyes, true);
  }
  static public String getImagePath(HeroCode code, boolean ben_has_eyes, boolean circle_image) {
    String file_path = "units/" + HeroCode.imagePathHeader(code);
    if (circle_image) {
      file_path += "_circle";
    }
    if (code == HeroCode.BEN && !ben_has_eyes) {
      file_path += "_noeyes";
    }
    return file_path + ".png";
  }

  static public HeroCode heroCode(String display_name) {
    for (HeroCode code : HeroCode.VALUES) {
      if (code == HeroCode.ERROR) {
        continue;
      }
      if (HeroCode.displayName(code).equals(display_name) ||
        HeroCode.file_name(code).equals(display_name)) {
        return code;
      }
    }
    return ERROR;
  }

  static public HeroCode heroCodeFromId(int id) {
    switch(id) {
      case 1101:
        return HeroCode.BEN;
      case 1102:
        return HeroCode.DAN;
      case 1103:
        return HeroCode.JF;
      case 1104:
        return HeroCode.SPINNY;
      case 1105:
        return HeroCode.MATTUS;
      case 1106:
        return HeroCode.PATRICK;
      default:
        return HeroCode.ERROR;
    }
  }
}