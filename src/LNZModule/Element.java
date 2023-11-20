package LNZModule;

import java.util.*;
import DImg.DImg;

enum Element {
  GRAY("Gray"), BLUE("Blue"), RED("Red"), CYAN("Cyan"), ORANGE("Orange"),
    BROWN("Brown"), PURPLE("Purple"), YELLOW("Yellow"), MAGENTA("Magenta");

  private static final List<Element> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

  private String element_name;
  private Element(String element_name) {
    this.element_name = element_name;
  }
  public String element_name() {
    return this.element_name;
  }
  public static String element_name(Element element) {
    return element.element_name();
  }

  public static Element element(String element_name) {
    for (Element element : Element.VALUES) {
      if (element.element_name().equals(element_name)) {
        return element;
      }
    }
    return Element.GRAY;
  }

  public static int color(Global global, Element e) {
    switch(e) {
      case GRAY:
        return DImg.ccolor(170);
      case BLUE:
        return DImg.ccolor(0, 0, 230);
      case RED:
        return DImg.ccolor(230, 0, 0);
      case CYAN:
        return DImg.ccolor(0, 230, 230);
      case ORANGE:
        return DImg.ccolor(240, 160, 0);
      case BROWN:
        return DImg.ccolor(51, 45, 26);
      case PURPLE:
        return DImg.ccolor(115, 0, 115);
      case YELLOW:
        return DImg.ccolor(230, 230, 0);
      case MAGENTA:
        return DImg.ccolor(230, 0, 230);
      default:
        global.errorMessage("ERROR: Element " + e.element_name() + " doesn't have a color.");
        return DImg.ccolor(0);
    }
  }

  public static int colorDark(Global global, Element e) {
    switch(e) {
      case GRAY:
        return DImg.ccolor(145);
      case BLUE:
        return DImg.ccolor(0, 0, 175);
      case RED:
        return DImg.ccolor(175, 0, 0);
      case CYAN:
        return DImg.ccolor(0, 175, 175);
      case ORANGE:
        return DImg.ccolor(180, 135, 0);
      case BROWN:
        return DImg.ccolor(20, 15, 10);
      case PURPLE:
        return DImg.ccolor(85, 0, 85);
      case YELLOW:
        return DImg.ccolor(175, 175, 0);
      case MAGENTA:
        return DImg.ccolor(175, 0, 175);
      default:
        global.errorMessage("ERROR: Element " + e.element_name() + " doesn't have a color.");
        return DImg.ccolor(0);
    }
  }

  public static int colorLight(Global global, Element e) {
    switch(e) {
      case GRAY:
        return DImg.ccolor(195);
      case BLUE:
        return DImg.ccolor(40, 40, 255);
      case RED:
        return DImg.ccolor(255, 40, 40);
      case CYAN:
        return DImg.ccolor(40, 255, 255);
      case ORANGE:
        return DImg.ccolor(255, 190, 25);
      case BROWN:
        return DImg.ccolor(70, 65, 45);
      case PURPLE:
        return DImg.ccolor(140, 0, 140);
      case YELLOW:
        return DImg.ccolor(255, 255, 40);
      case MAGENTA:
        return DImg.ccolor(255, 40, 255);
      default:
        global.errorMessage("ERROR: Element " + e.element_name() + " doesn't have a color.");
        return DImg.ccolor(0);
    }
  }

  public static int colorText(Global global, Element e) {
    switch(e) {
      case GRAY:
      case BLUE:
      case RED:
      case CYAN:
      case ORANGE:
      case PURPLE:
      case YELLOW:
      case MAGENTA:
        return DImg.ccolor(0);
      case BROWN:
        return DImg.ccolor(255);
      default:
        global.errorMessage("ERROR: Element " + e.element_name() + " doesn't have a color.");
        return DImg.ccolor(0);
    }
  }

  public static int colorLocked(Global global, Element e) {
    switch(e) {
      case GRAY:
        return DImg.ccolor(120);
      case BLUE:
        return DImg.ccolor(100, 100, 150);
      case RED:
        return DImg.ccolor(150, 100, 100);
      case CYAN:
        return DImg.ccolor(100, 150, 150);
      case ORANGE:
        return DImg.ccolor(160, 120, 100);
      case BROWN:
        return DImg.ccolor(80, 75, 70);
      case PURPLE:
        return DImg.ccolor(90, 50, 90);
      case YELLOW:
        return DImg.ccolor(150, 150, 100);
      case MAGENTA:
        return DImg.ccolor(150, 100, 150);
      default:
        global.errorMessage("ERROR: Element " + e.element_name() + " doesn't have a color.");
        return DImg.ccolor(0);
    }
  }

  public double resistanceFactorTo(Element element) {
    return Element.resistanceFactorTo(this, element);
  }
  public static double resistanceFactorTo(Element target, Element source) {
    switch(target) {
      case BLUE:
        switch(source) {
          case BLUE:
            return LNZ.resistance_blue_blue;
          case RED:
            return LNZ.resistance_blue_red;
          case BROWN:
            return LNZ.resistance_blue_brown;
          default:
            return LNZ.resistance_default;
        }
      case RED:
        switch(source) {
          case RED:
            return LNZ.resistance_red_red;
          case CYAN:
            return LNZ.resistance_red_cyan;
          case BLUE:
            return LNZ.resistance_red_blue;
          default:
            return LNZ.resistance_default;
        }
      case CYAN:
        switch(source) {
          case CYAN:
            return LNZ.resistance_cyan_cyan;
          case ORANGE:
            return LNZ.resistance_cyan_orange;
          case RED:
            return LNZ.resistance_cyan_red;
          default:
            return LNZ.resistance_default;
        }
      case ORANGE:
        switch(source) {
          case ORANGE:
            return LNZ.resistance_orange_orange;
          case BROWN:
            return LNZ.resistance_orange_brown;
          case CYAN:
            return LNZ.resistance_orange_cyan;
          default:
            return LNZ.resistance_default;
        }
      case BROWN:
        switch(source) {
          case BROWN:
            return LNZ.resistance_brown_brown;
          case BLUE:
            return LNZ.resistance_brown_blue;
          case ORANGE:
            return LNZ.resistance_brown_orange;
          default:
            return LNZ.resistance_default;
        }
      case PURPLE:
        switch(source) {
          case PURPLE:
            return LNZ.resistance_purple_purple;
          case YELLOW:
            return LNZ.resistance_purple_yellow;
          case MAGENTA:
            return LNZ.resistance_purple_magenta;
          default:
            return LNZ.resistance_default;
        }
      case YELLOW:
        switch(source) {
          case YELLOW:
            return LNZ.resistance_yellow_yellow;
          case MAGENTA:
            return LNZ.resistance_yellow_magenta;
          case PURPLE:
            return LNZ.resistance_yellow_purple;
          default:
            return LNZ.resistance_default;
        }
      case MAGENTA:
        switch(source) {
          case MAGENTA:
            return LNZ.resistance_magenta_magenta;
          case PURPLE:
            return LNZ.resistance_magenta_purple;
          case YELLOW:
            return LNZ.resistance_magenta_yellow;
          default:
            return LNZ.resistance_default;
        }
      default:
        return LNZ.resistance_default;
    }
  }
}