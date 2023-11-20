package LNZModule;

enum Tree {
  MAPLE, WALNUT, CEDAR, DEAD, OAK, PINE
  ;

  public static int sprout(Tree tree) {
    switch(tree) {
      case MAPLE:
      default:
        return 462;
      case WALNUT:
        return 463;
      case CEDAR:
        return 464;
      case OAK:
        return 465;
      case PINE:
        return 466;
    }
  }

  public static int sapling(Tree tree) {
    switch(tree) {
      case MAPLE:
      default:
        return 457;
      case WALNUT:
        return 458;
      case CEDAR:
        return 489;
      case OAK:
        return 460;
      case PINE:
        return 461;
    }
  }

  public static int small(Tree tree) {
    switch(tree) {
      case MAPLE:
      default:
        return 421;
      case WALNUT:
        return 422;
      case CEDAR:
        return 423;
      case DEAD:
        return 424;
      case OAK:
        return 425;
      case PINE:
        return 426;
    }
  }

  public static int large(Tree tree) {
    switch(tree) {
      case MAPLE:
      default:
        return 444;
      case WALNUT:
        return 445;
      case CEDAR:
        return 446;
      case DEAD:
        return 447;
      case OAK:
        return 448;
      case PINE:
        return 449;
    }
  }

  public static Tree species(int id) {
    switch(id) {
      case 421: // Maple
      case 444:
      case 457:
      case 462:
        return Tree.MAPLE;
      case 422: // Walnut
      case 445:
      case 458:
      case 463:
        return Tree.WALNUT;
      case 423: // Cedar
      case 446:
      case 459:
      case 464:
        return Tree.CEDAR;
      case 424: // Dead
      case 447:
        return Tree.DEAD;
      case 425: // Oak
      case 448:
      case 460:
      case 465:
        return Tree.OAK;
      case 426: // Pine
      case 449:
      case 461:
      case 466:
        return Tree.PINE;
      default:
        return null;
    }
  }

  public static int branchId(int tree_id) {
    switch(tree_id) {
      case 421: // Maple
      case 444:
      case 457:
      case 462:
        return 2965;
      case 422: // Walnut
      case 445:
      case 458:
      case 463:
        return 2966;
      case 423: // Cedar
      case 446:
      case 459:
      case 464:
        return 2967;
      case 424: // Dead
      case 447:
        return 2963;
      case 425: // Oak
      case 448:
      case 460:
      case 465:
        return 2960;
      case 426: // Pine
      case 449:
      case 461:
      case 466:
        return 2968;
      default:
        return 0;
    }
  }

  public static int fruitId(int tree_id) {
    switch(tree_id) {
      case 421: // Maple
      case 444:
      case 457:
      case 462:
        return 2011;
      case 422: // Walnut
      case 445:
      case 458:
      case 463:
        return 2012;
      case 423: // Cedar
      case 446:
      case 459:
      case 464:
        return 2013;
      case 425: // Oak
      case 448:
      case 460:
      case 465:
        return 2014;
      case 426: // Pine
      case 449:
      case 461:
      case 466:
        return 2015;
      default:
        return 0;
    }
  }
}