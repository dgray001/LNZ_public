package LNZModule;

import java.util.*;

enum ToolCode {
  SAW, MECHANICAL_SAW, GLUE, PAINTBRUSH, CLAMP, FASTENER, DRIVER, MECHANICAL_FASTENER,
  MECHANICAL_DRIVER, STRING;
 
   private static final List<ToolCode> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
 
   public String displayName() {
     return ToolCode.displayName(this);
   }
   public static String displayName(ToolCode code) {
     if (code == null) {
       return "Null";
     }
     switch(code) {
       case SAW:
         return "Saw";
       case MECHANICAL_SAW:
         return "Mechanical Saw";
       case GLUE:
         return "Glue";
       case PAINTBRUSH:
         return "Paintbrush";
       case CLAMP:
         return "Clamp";
       case FASTENER:
         return "Fastener";
       case DRIVER:
         return "Driver";
       case MECHANICAL_FASTENER:
         return "Mechanical Fastener";
       case MECHANICAL_DRIVER:
         return "Mechanical Driver";
       case STRING:
         return "String";
       default:
         return "ERROR";
     }
   }
 
   public static ToolCode toolCodeFrom(String s) {
     for (ToolCode code : ToolCode.VALUES) {
       if (ToolCode.displayName(code).equals(s)) {
         return code;
       }
     }
     return null;
   }
 
   public static HashMap<ToolCode, Integer> toolCodesFrom(Item i) {
     HashMap<ToolCode, Integer> codes = new HashMap<ToolCode, Integer>();
     if (i == null || i.remove) {
       return codes;
     }
     switch(i.ID) {
       case 2809: // String
         ToolCode.addToolToHashmap(codes, ToolCode.STRING, i.stack);
         break;
       case 2934: // Hammerstone
         ToolCode.addToolToHashmap(codes, ToolCode.DRIVER, i.durability);
         break;
       case 2970: // Wooden Peg
         ToolCode.addToolToHashmap(codes, ToolCode.FASTENER, i.stack);
         break;
       case 2971: // Paintbrush
         ToolCode.addToolToHashmap(codes, ToolCode.PAINTBRUSH, i.durability);
         break;
       case 2972: // Clamp
         ToolCode.addToolToHashmap(codes, ToolCode.CLAMP, i.durability);
         break;
       case 2973: // Wrench
         break;
       case 2974: // Rope
         break;
       case 2975: // Hammer
         ToolCode.addToolToHashmap(codes, ToolCode.DRIVER, i.durability);
         break;
       case 2976: // Window Breaker
         break;
       case 2977: // Stone Hatchet
         ToolCode.addToolToHashmap(codes, ToolCode.SAW, i.durability);
         break;
       case 2978: // Wire Clippers
         break;
       case 2979: // Saw
         ToolCode.addToolToHashmap(codes, ToolCode.SAW, i.durability);
         break;
       case 2980: // Drill
         ToolCode.addToolToHashmap(codes, ToolCode.DRIVER, i.durability);
         ToolCode.addToolToHashmap(codes, ToolCode.MECHANICAL_DRIVER, i.durability);
         break;
       case 2981: // Roundsaw
         ToolCode.addToolToHashmap(codes, ToolCode.SAW, i.durability);
         ToolCode.addToolToHashmap(codes, ToolCode.MECHANICAL_SAW, i.durability);
         break;
       case 2982: // Beltsander
         break;
       case 2983: // Chainsaw
         ToolCode.addToolToHashmap(codes, ToolCode.SAW, i.durability);
         ToolCode.addToolToHashmap(codes, ToolCode.MECHANICAL_SAW, i.durability);
         break;
       case 2984: // Woodglue
         ToolCode.addToolToHashmap(codes, ToolCode.GLUE, i.durability);
         break;
       case 2985: // Nails
         ToolCode.addToolToHashmap(codes, ToolCode.FASTENER, i.durability);
         break;
       case 2986: // Screws
         ToolCode.addToolToHashmap(codes, ToolCode.FASTENER, i.durability);
         ToolCode.addToolToHashmap(codes, ToolCode.MECHANICAL_FASTENER, i.durability);
         break;
       default:
         break;
     }
     return codes;
   }
 
   public static HashMap<ToolCode, Integer> toolCodesFrom(Item ... items) {
     HashMap<ToolCode, Integer> codes = new HashMap<ToolCode, Integer>();
     for (Item i : items) {
       for (Map.Entry<ToolCode, Integer> entry : ToolCode.toolCodesFrom(i).entrySet()) {
         ToolCode.addToolToHashmap(codes, entry.getKey(), entry.getValue());
       }
     }
     return codes;
   }
 
   private static void addToolToHashmap(HashMap<ToolCode, Integer> map, ToolCode code, int amount) {
     if (map == null || code == null || amount < 1) {
       return;
     }
     if (map.containsKey(code)) {
       map.put(code, map.get(code) + amount);
     }
     else {
       map.put(code, amount);
     }
   }
 }
 
 
 class CraftingRecipe {
   protected final int[][] ingredients;
   protected final int output;
   protected final int amount;
   protected final ToolCode[] tools;
   
   CraftingRecipe(int[][] ingredients, int output, int amount, ToolCode[] tools) {
     this.ingredients = ingredients;
     this.output = output;
     this.amount = amount;
     this.tools = tools;
   }

   boolean hasTools(HashMap<ToolCode, Integer> codes) {
     HashMap<ToolCode, Integer> used_codes = new HashMap<ToolCode, Integer>();
     for (ToolCode code : this.tools) {
       if (!codes.containsKey(code)) {
         return false; // not any of tool
       }
       if (used_codes.containsKey(code)) {
         if (codes.get(code) > used_codes.get(code)) {
           used_codes.put(code, used_codes.get(code) + 1);
         }
         else {
           return false; // not enough of tool
         }
       }
       else {
         used_codes.put(code, 1);
       }
     }
     return true;
   }

   void useTools(LNZ p, List<Item> tools_available) {
     for (ToolCode code : this.tools) {
       boolean no_tool = true;
       for (Item i : tools_available) {
         if (i == null || i.remove) {
           continue;
         }
         if (ToolCode.toolCodesFrom(i).containsKey(code)) {
           i.lowerDurability(1, true);
           no_tool = false;
           break;
         }
       }
       if (no_tool) {
         p.global.log("ERROR: No tool contained ToolCode " + code + " when crafting " + this.output + ".");
       }
     }
   }
 

  static int[][] reduceItemGrid(LNZ p, int[][] item_grid) {
   if (item_grid == null) {
     return null;
   }
   for (int i = 0; i < item_grid.length; i++) {
     boolean empty = true;
     for (int j = 0; j < item_grid[i].length; j++) {
       if (item_grid[i][j] != 0) {
         empty = false;
       }
     }
     if (empty) {
       item_grid = Arrays.copyOfRange(item_grid, 1, item_grid.length);
       i--;
     }
     else {
       break;
     }
   }
   for (int i = item_grid.length - 1; i >= 0; i--) {
     boolean empty = true;
     for (int j = 0; j < item_grid[i].length; j++) {
       if (item_grid[i][j] != 0) {
         empty = false;
       }
     }
     if (empty) {
       item_grid = Arrays.copyOfRange(item_grid, 0, item_grid.length - 1);
     }
     else {
       break;
     }
   }
   if (item_grid.length == 0) {
     return item_grid;
   }
   for (int i = 0; i < item_grid[0].length; i++) {
     boolean empty = true;
     try {
       for (int j = 0; j < item_grid.length; j++) {
         if (item_grid[j][i] != 0) {
           empty = false;
         }
       }
     } catch(ArrayIndexOutOfBoundsException e) {
       p.global.errorMessage("ERROR: Input item grid is corrupted: " + Arrays.deepToString(item_grid));
     }
     if (empty) {
       for (int j = 0; j < item_grid.length; j++) {
         item_grid[j] = Arrays.copyOfRange(item_grid[j], 1, item_grid[j].length);
       }
       i--;
     }
     else {
       break;
     }
   }
   if (item_grid.length == 0) {
     return item_grid;
   }
   for (int i = item_grid[0].length - 1; i >= 0; i--) {
     boolean empty = true;
     try {
       for (int j = 0; j < item_grid.length; j++) {
         if (item_grid[j][i] != 0) {
           empty = false;
         }
       }
     } catch(ArrayIndexOutOfBoundsException e) {
       p.global.errorMessage("ERROR: Input item grid is corrupted: " + Arrays.deepToString(item_grid));
     }
     if (empty) {
       for (int j = 0; j < item_grid.length; j++) {
         item_grid[j] = Arrays.copyOfRange(item_grid[j], 0, item_grid[j].length - 1);
       }
     }
     else {
       break;
     }
   }
   return item_grid;
 }
}