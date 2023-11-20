package LNZModule;

import java.util.*;
import processing.core.*;
import Button.*;
import DImg.DImg;
import Element.*;
import Form.*;
import Misc.Misc;

class Hero extends Unit {

  enum InventoryLocation {
    INVENTORY, GEAR, FEATURE, CRAFTING;
    private static final List<InventoryLocation> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
  }

  class GearInventory extends Inventory {
    private LNZ p;
    GearInventory(LNZ sketch) {
      super(sketch, 4, 3, true);
      this.p = sketch;
    }

    GearSlot indexToGearSlot(int index) {
      switch(index) {
        case 0:
          return GearSlot.HAND_THIRD;
        case 1:
          return GearSlot.HEAD;
        case 2:
          return GearSlot.HAND_FOURTH;
        case 3:
          return GearSlot.WEAPON;
        case 4:
          return GearSlot.CHEST;
        case 5:
          return GearSlot.OFFHAND;
        case 6:
          return GearSlot.BELT_RIGHT;
        case 7:
          return GearSlot.LEGS;
        case 8:
          return GearSlot.BELT_LEFT;
        case 9:
          return GearSlot.FEET_SECOND;
        case 10:
          return GearSlot.FEET;
        case 11:
          return GearSlot.FEET_THIRD;
        default:
          p.global.errorMessage("ERROR: Gear inventory index " + index + " out of range.");
          return GearSlot.ERROR;
      }
    }

    Item getItem(int index) {
      return Hero.this.gear.get(this.indexToGearSlot(index));
    }

    void setItem(int index, Item i) {
      Hero.this.gear.put(this.indexToGearSlot(index), i);
    }

    @Override
    Item placeAt(Item i, int index, boolean replace) {
      if (!i.equippable(this.indexToGearSlot(index))) {
        return i;
      }
      if (index < 0 || index >= this.slots.size()) {
        return i;
      }
      if (this.getItem(index) == null) {
        this.setItem(index, i);
        return null;
      }
      else if (this.getItem(index).ID == i.ID) {
        int stack_left = this.getItem(index).maxStack() - this.getItem(index).stack;
        if (i.stack > stack_left) {
          this.getItem(index).addStack(stack_left);
          i.removeStack(stack_left);
        }
        else {
          this.getItem(index).addStack(i.stack);
          return null;
        }
      }
      else if (replace) {
        Item replaced = new Item(p, this.getItem(index));
        this.setItem(index, i);
        return replaced;
      }
      return i;
    }

    @Override
    void update(int timeElapsed) {
      p.rectMode(PConstants.CORNER);
      p.fill(this.color_background);
      p.noStroke();
      p.rect(0, 0, this.display_width, this.display_height);
      p.imageMode(PConstants.CORNERS);
      for (Map.Entry<GearSlot, Item> entry : Hero.this.gear.entrySet()) {
        switch(entry.getKey()) {
          case WEAPON:
            this.updateSlot(timeElapsed, 3, entry.getValue());
            break;
          case HEAD:
            this.updateSlot(timeElapsed, 1, entry.getValue());
            break;
          case CHEST:
            this.updateSlot(timeElapsed, 4, entry.getValue());
            break;
          case LEGS:
            this.updateSlot(timeElapsed, 7, entry.getValue());
            break;
          case FEET:
            this.updateSlot(timeElapsed, 10, entry.getValue());
            break;
          case OFFHAND:
            this.updateSlot(timeElapsed, 5, entry.getValue());
            break;
          case BELT_LEFT:
            this.updateSlot(timeElapsed, 6, entry.getValue());
            break;
          case BELT_RIGHT:
            this.updateSlot(timeElapsed, 8, entry.getValue());
            break;
          case HAND_THIRD:
            this.updateSlot(timeElapsed, 0, entry.getValue());
            break;
          case HAND_FOURTH:
            this.updateSlot(timeElapsed, 2, entry.getValue());
            break;
          case FEET_SECOND:
            this.updateSlot(timeElapsed, 9, entry.getValue());
            break;
          case FEET_THIRD:
            this.updateSlot(timeElapsed, 11, entry.getValue());
            break;
          case ERROR:
            p.global.errorMessage("ERROR: Gear slot has error value.");
            break;
        }
      }
    }

    void updateSlot(int timeElapsed, int index, Item i) {
      this.updateSlot(timeElapsed, index, i, true, 0, true);
    }
    void updateSlot(int timeElapsed, int index, Item i, boolean translate_first, double temp_slot_width, boolean show_slot_hovered_message) {
      int x = index % this.max_cols;
      if (x < 0 || x >= this.max_cols) {
        return;
      }
      int y = index / this.max_cols;
      if (y < 0 || y >= this.max_rows) {
        return;
      }
      this.slots.get(index).item = i;
      if (translate_first) {
        p.translate(2 + x * this.button_size, 2 + y * this.button_size);
      }
      else {
        this.slots.get(index).setWidth(temp_slot_width);
        if (index == 3) { // hand white border
          this.slots.get(index).button.setStroke(DImg.ccolor(255), 4);
        }
      }
      this.slots.get(index).update(timeElapsed, show_slot_hovered_message);
      if (this.slots.get(index).item == null) {
        String iconName = "icons/";
        switch(index) {
          case 0:
            iconName += "";
            break;
          case 1:
            iconName += "head.png";
            break;
          case 2:
            iconName += "";
            break;
          case 3:
            iconName += "hand_inverted.png";
            break;
          case 4:
            iconName += "chest.png";
            break;
          case 5:
            iconName += "hand.png";
            break;
          case 6:
            iconName += "belt_right.png";
            break;
          case 7:
            iconName += "legs.png";
            break;
          case 8:
            iconName += "belt_left.png";
            break;
          case 9:
            iconName += "";
            break;
          case 10:
            iconName += "feet.png";
            break;
          case 11:
            iconName += "";
            break;
          default:
            p.global.errorMessage("ERROR: Gear inventory index " + index + " out of range.");
            break;
        }
        p.image(p.global.images.getImage(iconName), 0, 0, this.slots.get(index).width(), this.slots.get(index).width());
      }
      if (translate_first) {
        p.translate(-2 - x * this.button_size, -2 - y * this.button_size);
      }
      else {
        this.slots.get(index).setWidth(this.button_size);
        if (index == 3) { // hand white border
          this.slots.get(index).button.setStroke(DImg.ccolor(142, 75, 50), 3);
        }
      }
    }

    @Override
    void mouseMove(float mX, float mY) {
      if (mX + 5 < 0 || mY + 5 < 0 || mX - 5 > this.display_width || mY - 5 > this.display_height) {
        this.hovered = false;
      }
      else {
        this.hovered = true;
      }
      for (int x = 0; x < this.max_cols; x++) {
        for (int y = 0; y < this.max_rows; y++) {
          int i = y * this.max_cols + x;
          if (i >= this.slots.size()) {
            break;
          }
          if (!this.slotActive(i)) {
            continue;
          }
          this.slots.get(i).mouseMove(
            (float)(mX - 2 - x * this.button_size),
            (float)(mY - 2 - y * this.button_size));
        }
      }
    }

    boolean slotActive(int index) {
      return Hero.this.gear.containsKey(this.indexToGearSlot(index));
    }
  }

  class CraftingInventory extends Inventory {
    class CraftButton extends RectangleButton {
      protected int craft_timer = 0;

      CraftButton(LNZ sketch) {
        super(sketch, 0, 0, 0, 0);
        this.disabled = true;
        this.show_message = true;
        this.force_left_button = false;
        this.setMessage("Craft");
        this.use_time_elapsed = true;
        this.noStroke();
        this.setColors(DImg.ccolor(170, 170), DImg.ccolor(1, 0),
          DImg.ccolor(200, 100), DImg.ccolor(200, 200), DImg.ccolor(0));
      }

      @Override
      public void update(int time_elapsed) {
        super.update(time_elapsed);
        if (this.clicked && p.mouseButton == PConstants.RIGHT) {
          this.craft_timer -= time_elapsed;
          if (this.craft_timer < 0) {
            this.craft_timer = LNZ.hero_multicraftTimer;
            this.release();
          }
        }
      }

      public void dehover() {}
      public void hover() {}
      public void click() {
        this.craft_timer = LNZ.hero_multicraftTimer;
      }
      public void release() {
        if (!this.hovered) {
          return;
        }
        if (CraftingInventory.this.craftable_item == null) {
          return;
        }
        CraftingInventory.this.craft();
      }
    }


    class ToolsButton extends RectangleButton {
      class ToolsButtonTextBox extends TextBox {
        ToolsButtonTextBox(LNZ sketch) {
          super(sketch, 0, 0, 0, 0);
          this.setTitleSize(19);
          this.setTextSize(17);
          this.color_background = sketch.global.color_nameDisplayed_background;
          this.color_header = sketch.global.color_nameDisplayed_background;
          this.color_stroke = DImg.ccolor(1, 0);
          this.color_text = DImg.ccolor(255);
          this.color_title = DImg.ccolor(255);
          this.scrollbar.setButtonColors(DImg.ccolor(170),
            DImg.adjustColorBrightness(sketch.global.color_nameDisplayed_background, 1.1),
            DImg.adjustColorBrightness(sketch.global.color_nameDisplayed_background, 1.2),
            DImg.adjustColorBrightness(sketch.global.color_nameDisplayed_background, 0.95), DImg.ccolor(0));
          this.scrollbar.button_upspace.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
            DImg.ccolor(1, 0), DImg.ccolor(0), DImg.ccolor(0));
          this.scrollbar.button_downspace.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
            DImg.ccolor(1, 0), DImg.ccolor(0), DImg.ccolor(0));
          this.useElapsedTime();
        }
      }

      protected ToolsButtonTextBox description;

      ToolsButton(LNZ sketch) {
        super(sketch, 0, 0, 0, 0);
        this.description = new ToolsButtonTextBox(sketch);
        this.show_message = true;
        this.force_left_button = false;
        this.setMessage("Tools");
        this.use_time_elapsed = true;
        this.noStroke();
        this.setColors(DImg.ccolor(170, 170), DImg.ccolor(1, 0),
          DImg.ccolor(200, 100), DImg.ccolor(200, 200), DImg.ccolor(0));
      }

      @Override
      public void update(int time_elapsed) {
        super.update(time_elapsed);
        if (this.hovered) {
          String message_text = "";
          double description_width = LNZ.feature_workbenchMinimumToolsButtonWidth;
          if (CraftingInventory.this.crafting_recipe == null) {
            message_text += "Tools Have:";
            for (Map.Entry<ToolCode, Integer> entry : CraftingInventory.this.currentTools().entrySet()) {
              message_text += "\n - " + entry.getKey().displayName() + " (" + entry.getValue() + ")";
            }
          }
          else {
            message_text += "Tools Need:";
            for (ToolCode code : CraftingInventory.this.crafting_recipe.tools) {
              message_text += "\n - " + code.displayName();
            }
            message_text += "\n\nTools Have:";
            for (Map.Entry<ToolCode, Integer> entry : CraftingInventory.this.currentTools().entrySet()) {
              message_text += "\n - " + entry.getKey().displayName() + " (" + entry.getValue() + ")";
            }
            this.description.setTitleText(CraftingInventory.this.craftable_item.displayName());
            description_width = Math.max(LNZ.feature_workbenchMinimumToolsButtonWidth,
              p.textWidth(CraftingInventory.this.craftable_item.displayName()) + 2);
          }
          p.textSize(this.description.text_size);
          this.description.setXLocation(this.xf - description_width, this.xf);
          double description_height = p.textAscent() + p.textDescent() + 6;
          this.description.setText(message_text);
          description_height += (2 + this.description.text_lines.size()) *
            (p.textAscent() + p.textDescent() + this.description.text_leading);
          this.description.setYLocation(this.yi - description_height, this.yi);
          this.description.update(time_elapsed);
        }
      }

      public void dehover() {}
      public void hover() {}
      public void click() {}
      public void release() {
        if (!this.hovered) {
          return;
        }
        // open crafting recipe form
      }
    }

    private LNZ p;

    private int curr_crafting_hash_code = 0;
    private Item craftable_item = null;
    private CraftingRecipe crafting_recipe = null;
    private boolean craftable_item_hovered = false;
    private double last_mX = 0;
    private double last_mY = 0;
    private CraftButton craft;
    private ToolsButton tools;

    CraftingInventory(LNZ sketch) {
      super(sketch, 3, 6, true);
      this.craft = new CraftButton(sketch);
      this.tools = new ToolsButton(sketch);
      this.p = sketch;
      this.deactivateSlots();
    }

    HashMap<ToolCode, Integer> currentTools() {
      return ToolCode.toolCodesFrom(this.currentToolItems().toArray(new Item[0]));
    }

    ArrayList<Item> currentToolItems() {
      ArrayList<Item> tool_items = Hero.this.inventory.items();
      tool_items.add(Hero.this.weapon());
      tool_items.add(Hero.this.offhand());
      return tool_items;
    }


    void craft() {
      if (this.craftable_item == null) {
        return;
      }
      if (this.slots.get(11).item != null && (this.slots.get(11).item.ID !=
        this.craftable_item.ID || this.slots.get(11).item.maxStack() - this.
        slots.get(11).item.stack < this.craftable_item.stack)) {
        return;
      }
      if (!this.crafting_recipe.hasTools(this.currentTools())) {
        return;
      }
      this.crafting_recipe.useTools(p, this.currentToolItems());
      this.slots.get(0).removeStack();
      this.slots.get(1).removeStack();
      this.slots.get(2).removeStack();
      this.slots.get(6).removeStack();
      this.slots.get(7).removeStack();
      this.slots.get(8).removeStack();
      this.slots.get(12).removeStack();
      this.slots.get(13).removeStack();
      this.slots.get(14).removeStack();
      if (this.slots.get(11).item == null) {
        this.slots.get(11).item = new Item(p, this.craftable_item);
      }
      else {
        this.slots.get(11).item.addStack(this.craftable_item.stack);
      }
      this.slots.get(11).deactivated = false;
      this.craftable_item = null;
    }


    @Override
    public void setButtonSize(double button_size) {
      super.setButtonSize(button_size);
      this.craft.setLocation(2 + 3.5 * button_size, 2 + 2.1 * button_size,
        2 + 5 * button_size, 2 + 2.9 * button_size);
      this.craft.text_size = button_size * 0.35;
      this.tools.setLocation(2 + 3.5 * button_size, 2 + 0.1 * button_size,
        2 + 5 * button_size, 2 + 0.9 * button_size);
      this.tools.text_size = button_size * 0.35;
    }

    @Override
    void update(int timeElapsed) {
      if (this.slots.get(7).deactivated) {
        return;
      }
      if (!this.slots.get(11).deactivated && this.slots.get(11).item == null) {
        this.slots.get(11).deactivated = true;
        this.slots.get(11).button.hovered = false;
        this.slots.get(11).button.clicked = false;
      }
      super.update(timeElapsed);
      this.curr_crafting_hash_code = this.getCraftingHashCode();
      p.imageMode(PConstants.CORNER);
      if (p.global.crafting_recipes.containsKey(this.curr_crafting_hash_code)) {
        this.crafting_recipe = p.global.crafting_recipes.get(this.curr_crafting_hash_code);
        this.craftable_item = new Item(p, this.crafting_recipe.output);
        this.craftable_item.stack = this.crafting_recipe.amount;
        this.craft.disabled = !this.crafting_recipe.hasTools(this.currentTools());
        boolean draw_craftable_item = true;
        if (this.slots.get(11).item != null && (this.slots.get(11).item.ID !=
          this.craftable_item.ID || this.slots.get(11).item.maxStack() - this.
          slots.get(11).item.stack < this.craftable_item.stack)) {
          this.craft.disabled = true;
          draw_craftable_item = false;
        }
        if (this.craft.disabled) {
          p.image(p.global.images.getImage("icons/crafting_arrow_red.png"), 2 + 3.5 * this.button_size,
            2 + 1 * this.button_size, this.button_size, this.button_size);
        }
        else {
          p.image(p.global.images.getImage("icons/crafting_arrow_green.png"), 2 + 3.5 * this.button_size,
            2 + 1 * this.button_size, this.button_size, this.button_size);
        }
        if (draw_craftable_item) {
          p.image(this.craftable_item.getImage(), 2 + 5 * this.button_size,
            2 + 1 * this.button_size, this.button_size, this.button_size);
          if (this.craftable_item.stack > 1) {
            p.fill(255);
            p.textSize(14);
            p.textAlign(PConstants.RIGHT, PConstants.BOTTOM);
            p.text(Integer.toString(this.craftable_item.stack), 6 * this.button_size, 2 * this.button_size);
          }
          if (this.craftable_item_hovered) {
            p.textSize(20);
            double rect_height = p.textAscent() + p.textDescent() + 2;
            double rect_width = p.textWidth(this.craftable_item.displayName()) + 2;
            p.rectMode(PConstants.CORNER);
            p.fill(p.global.color_nameDisplayed_background);
            p.stroke(1, 0);
            p.strokeWeight(0.1);
            p.rect(this.last_mX - rect_width - 1, this.last_mY - rect_height - 1, rect_width, rect_height);
            p.fill(255);
            p.textAlign(PConstants.LEFT, PConstants.TOP);
            p.text(this.craftable_item.displayName(), this.last_mX - rect_width - 1, this.last_mY - rect_height - 1);
          }
        }
      }
      else {
        this.craftable_item = null;
        this.crafting_recipe = null;
        this.craft.disable();
        p.image(p.global.images.getImage("icons/crafting_arrow.png"), 2 + 3.5 * this.button_size,
          2 + 1 * this.button_size, this.button_size, this.button_size);
      }
      this.craft.update(timeElapsed);
      this.tools.update(timeElapsed);
    }

    @Override
    void mouseMove(float mX, float mY) {
      this.last_mX = mX;
      this.last_mY = mY;
      super.mouseMove(mX, mY);
      this.craft.mouseMove(mX, mY);
      this.tools.mouseMove(mX, mY);
      if (this.craftable_item == null) {
        this.craftable_item_hovered = false;
      }
      else {
        if (mX > 2 + 5 * this.button_size && mY > 2 + 1 * this.button_size &&
          mX < 2 + 6 * this.button_size && mY < 2 + 2 * this.button_size) {
          this.craftable_item_hovered = true;
        }
        else {
          this.craftable_item_hovered = false;
        }
      }
    }

    @Override
    void mouseRelease(float mX, float mY) {
      super.mouseRelease(mX, mY);
      this.craft.mouseRelease(mX, mY);
      this.tools.mouseRelease(mX, mY);
    }

    @Override
    int getCraftingHashCode() {
      int[][] item_grid = new int[3][3];
      if (this.slots.get(0).item != null) {
        item_grid[0][0] = this.slots.get(0).item.ID;
      }
      if (this.slots.get(1).item != null) {
        item_grid[0][1] = this.slots.get(1).item.ID;
      }
      if (this.slots.get(2).item != null) {
        item_grid[0][2] = this.slots.get(2).item.ID;
      }
      if (this.slots.get(6).item != null) {
        item_grid[1][0] = this.slots.get(6).item.ID;
      }
      if (this.slots.get(7).item != null) {
        item_grid[1][1] = this.slots.get(7).item.ID;
      }
      if (this.slots.get(8).item != null) {
        item_grid[1][2] = this.slots.get(8).item.ID;
      }
      if (this.slots.get(12).item != null) {
        item_grid[2][0] = this.slots.get(12).item.ID;
      }
      if (this.slots.get(13).item != null) {
        item_grid[2][1] = this.slots.get(13).item.ID;
      }
      if (this.slots.get(14).item != null) {
        item_grid[2][2] = this.slots.get(14).item.ID;
      }
      return Arrays.deepHashCode(CraftingRecipe.reduceItemGrid(p, item_grid));
    }
  }

  class InventoryKey {
    private InventoryLocation location;
    private int index;
    InventoryKey(InventoryLocation location, int index) {
      this.location = location;
      this.index = index;
    }
  }

  class HeroInventory extends Inventory {
    private LNZ p;

    protected Item item_holding = null;
    protected InventoryKey item_origin = null;
    protected Item item_dropping = null;
    protected ArrayList<Item> more_items_dropping = new ArrayList<Item>();

    protected GearInventory gear_inventory;
    protected Inventory feature_inventory = null;
    protected CraftingInventory crafting_inventory;

    protected double last_mX = 0;
    protected double last_mY = 0;
    protected boolean viewing = false;
    protected boolean any_hovered = false;
    protected int grab_mouse_button = PConstants.LEFT;

    HeroInventory(LNZ sketch) {
      super(sketch, LNZ.hero_inventoryMaxRows, LNZ.hero_inventoryMaxCols, false);
      this.p = sketch;
      this.gear_inventory = new GearInventory(sketch);
      this.crafting_inventory = new CraftingInventory(sketch);
      this.setSlots(Hero.this.inventoryStartSlots());
      this.setButtonSize(LNZ.hero_defaultInventoryButtonSize);
    }


    void dropItemHolding() {
      if (this.item_holding == null || this.item_holding.remove) {
        return;
      }
      if (this.item_origin == null || !this.any_hovered) {
        this.item_dropping = this.item_holding;
      }
      else {
        switch(this.item_origin.location) {
          case INVENTORY:
            this.item_dropping = this.placeAt(this.item_holding, this.item_origin.index);
            break;
          case GEAR:
            this.item_dropping = this.gear_inventory.placeAt(this.item_holding, this.item_origin.index);
            break;
          case FEATURE:
            if (this.feature_inventory == null) {
              this.item_dropping = this.item_holding;
            }
            else {
              this.item_dropping = this.feature_inventory.placeAt(this.item_holding, this.item_origin.index);
            }
            break;
          case CRAFTING:
            if (this.item_origin.index == 11) {
              this.item_dropping = this.stash(this.item_holding);
            }
            else {
              this.item_dropping = this.crafting_inventory.placeAt(this.item_holding, this.item_origin.index);
            }
            break;
        }
      }
      this.item_holding = null;
      this.item_origin = null;
    }


    void clearCraftingInventory() {
      for (InventorySlot slot : this.crafting_inventory.slots) {
        if (slot.item == null || slot.item.remove) {
          continue;
        }
        Item i = this.stash(slot.item);
        slot.item = null;
        if (i == null || i.remove) {
          continue;
        }
        this.more_items_dropping.add(i);
      }
    }


    InventoryKey itemLocation(int item_id) {
      InventoryKey location = null;
      for (InventoryLocation invLocation : InventoryLocation.VALUES) {
        location = this.itemLocation(item_id, invLocation);
        if (location != null) {
          break;
        }
      }
      return location;
    }
    InventoryKey itemLocation(int item_id, InventoryLocation invLocation) {
      switch(invLocation) {
        case INVENTORY:
          for (int i = 0; i < this.slots.size(); i++) {
            if (this.slots.get(i).item != null && this.slots.get(i).item.ID == item_id) {
              return new InventoryKey(InventoryLocation.INVENTORY, i);
            }
          }
          break;
        case GEAR:
          for (int i = 0; i < this.gear_inventory.slots.size(); i++) {
            if (this.gear_inventory.slots.get(i).item != null
              && this.gear_inventory.slots.get(i).item.ID == item_id) {
              return new InventoryKey(InventoryLocation.GEAR, i);
            }
          }
          break;
        case FEATURE:
          if (this.feature_inventory == null) {
            break;
          }
          for (int i = 0; i < this.feature_inventory.slots.size(); i++) {
            if (this.feature_inventory.slots.get(i).item != null &&
              this.feature_inventory.slots.get(i).item.ID == item_id) {
              return new InventoryKey(InventoryLocation.FEATURE, i);
            }
          }
          break;
        case CRAFTING:
          for (int i = 0; i < this.crafting_inventory.slots.size(); i++) {
            if (this.crafting_inventory.slots.get(i).item != null &&
              this.crafting_inventory.slots.get(i).item.ID == item_id) {
              return new InventoryKey(InventoryLocation.CRAFTING, i);
            }
          }
          break;
      }
      return null;
    }


    Item getItem(InventoryKey inventory_key) {
      if (inventory_key == null) {
        return null;
      }
      switch(inventory_key.location) {
        case INVENTORY:
          try {
            return this.slots.get(inventory_key.index).item;
          } catch(Exception e) {}
          break;
        case GEAR:
          try {
            return this.gear_inventory.slots.get(inventory_key.index).item;
          } catch(Exception e) {}
          break;
        case FEATURE:
          try {
            return this.feature_inventory.slots.get(inventory_key.index).item;
          } catch(Exception e) {}
          break;
        case CRAFTING:
          try {
            return this.crafting_inventory.slots.get(inventory_key.index).item;
          } catch(Exception e) {}
          break;
      }
      return null;
    }

    void setItem(Item i, InventoryKey inventory_key) {
      if (i == null) {
        return;
      }
      if (inventory_key == null) {
        this.item_dropping = i;
        return;
      }
      switch(inventory_key.location) {
        case INVENTORY:
          try {
            this.slots.get(inventory_key.index).item = i;
            return;
          } catch(Exception e) {}
          break;
        case GEAR:
          try {
            this.gear_inventory.setItem(inventory_key.index, i);
            return;
          } catch(Exception e) {}
          break;
        case FEATURE:
          try {
            this.feature_inventory.slots.get(inventory_key.index).item = i;
            return;
          } catch(Exception e) {}
          break;
        case CRAFTING:
          try {
            this.crafting_inventory.slots.get(inventory_key.index).item = i;
          } catch(Exception e) {}
          break;
      }
      this.item_dropping = i;
    }


    void featureInventory(Inventory feature_inventory) {
      if (feature_inventory == null) {
        return;
      }
      this.feature_inventory = feature_inventory;
      this.setButtonSize(this.button_size);
    }


    @Override
    void setButtonSize(double button_size) {
      super.setButtonSize(button_size);
      this.gear_inventory.setButtonSize(button_size);
      this.crafting_inventory.setButtonSize(button_size);
      if (this.feature_inventory != null) {
        this.feature_inventory.setButtonSize(button_size);
      }
    }


    @Override
    Item stash(Item i) {
      if (i == null || i.remove) {
        return null;
      }
      if (this.feature_inventory != null) {
        Item leftover = this.feature_inventory.stash(i);
        if (leftover == null || leftover.remove) {
          return null;
        }
        i = new Item(p, leftover);
      }
      for (int j = 0; j < this.gear_inventory.slots.size(); j++) {
        if (this.gear_inventory.indexToGearSlot(j) == GearSlot.WEAPON) {
          continue;
        }
        Item leftover = this.gear_inventory.placeAt(i, j, false);
        if (leftover == null || leftover.remove) {
          i.equipSound();
          return null;
        }
        i = new Item(p, leftover);
      }
      return super.stash(i);
    }

    Item superStash(Item i) {
      return super.stash(i);
    }


    @Override
    void update(int timeElapsed) {
      // main inventory
      super.update(timeElapsed);
      p.noFill();
      p.stroke(255);
      p.strokeWeight(2);
      p.rectMode(PConstants.CORNER);
      if (Hero.this.inventory_bar.unlocked_inventory_bar2) {
        p.rect(2, 2, 9 * this.button_size, this.button_size);
      }
      else if (Hero.this.inventory_bar.unlocked_inventory_bar1) {
        p.rect(2, 2, 4 * this.button_size, this.button_size);
      }
      // gear
      double gearInventoryTranslateX = - this.gear_inventory.display_width - 2;
      double gearInventoryTranslateY = 0.5 * (this.display_height - this.gear_inventory.display_height);
      p.translate(gearInventoryTranslateX, gearInventoryTranslateY);
      this.gear_inventory.update(timeElapsed);
      p.translate(-gearInventoryTranslateX, -gearInventoryTranslateY);
      // crafting
      double craftingInventoryTranslateX = this.display_width + 2;
      double craftingInventoryTranslateY = 0.5 * (this.display_height - this.crafting_inventory.display_height);
      p.translate(craftingInventoryTranslateX, craftingInventoryTranslateY);
      this.crafting_inventory.update(timeElapsed);
      p.translate(-craftingInventoryTranslateX, -craftingInventoryTranslateY);
      // feature
      if (this.feature_inventory != null) {
        double featureInventoryTranslateX = 0.5 * (this.display_width - this.feature_inventory.display_width);
        double featureInventoryTranslateY = - this.feature_inventory.display_height - 2;
        p.translate(featureInventoryTranslateX, featureInventoryTranslateY);
        this.feature_inventory.update(timeElapsed);
        p.translate(-featureInventoryTranslateX, -featureInventoryTranslateY);
      }
      // item holding
      if (this.item_holding != null) {
        p.imageMode(PConstants.CENTER);
        p.image(this.item_holding.getImage(), this.item_holding.coordinate.x,
          this.item_holding.coordinate.y, this.button_size, this.button_size);
        if (this.item_holding.stack > 1) {
          p.fill(255);
          p.textSize(14);
          p.textAlign(PConstants.RIGHT, PConstants.BOTTOM);
          p.text(Integer.toString(this.item_holding.stack),
            this.item_holding.coordinate.x + 0.5 * this.button_size - 2,
            this.item_holding.coordinate.y + 0.5 * this.button_size - 2);
        }
        if (this.item_holding.remove) {
          this.item_holding = null;
        }
      }
    }

    @Override
    void mouseMove(float mX, float mY) {
      this.any_hovered = false;
      // item holding
      if (this.item_holding != null) {
        this.item_holding.coordinate.x += mX - this.last_mX;
        this.item_holding.coordinate.y += mY - this.last_mY;
      }
      this.last_mX = mX;
      this.last_mY = mY;
      // main inventory
      super.mouseMove(mX, mY);
      if (this.hovered) {
        this.any_hovered = true;
      }
      // gear
      float gearInventoryTranslateX = (float)(- this.gear_inventory.display_width - 2);
      float gearInventoryTranslateY = (float)(0.5 * (this.display_height - this.gear_inventory.display_height));
      this.gear_inventory.mouseMove(mX - gearInventoryTranslateX, mY - gearInventoryTranslateY);
      if (this.gear_inventory.hovered) {
        this.any_hovered = true;
      }
      // crafting
      float craftingInventoryTranslateX = (float)(this.display_width + 2);
      float craftingInventoryTranslateY = (float)(0.5 * (this.display_height - this.crafting_inventory.display_height));
      this.crafting_inventory.mouseMove(mX - craftingInventoryTranslateX, mY - craftingInventoryTranslateY);
      if (this.crafting_inventory.hovered) {
        this.any_hovered = true;
      }
      // feature
      if (this.feature_inventory != null) {
        float featureInventoryTranslateX = (float)(0.5 * (this.display_width - this.feature_inventory.display_width));
        float featureInventoryTranslateY = (float)(- this.feature_inventory.display_height - 2);
        this.feature_inventory.mouseMove(mX - featureInventoryTranslateX, mY - featureInventoryTranslateY);
        if (this.feature_inventory.hovered) {
          this.any_hovered = true;
        }
      }
    }

    @Override
    void mousePress() {
      super.mousePress();
      if (this.feature_inventory != null) {
        this.feature_inventory.mousePress();
      }
      boolean found_clicked = false;
      Item source_item = null;
      double item_holding_x = 0;
      double item_holding_y = 0;
      // main inventory
      for (int x = 0; x < this.max_cols; x++) {
        for (int y = 0; y < this.max_rows; y++) {
          int i = y * this.max_cols + x;
          if (i >= this.slots.size()) {
            break;
          }
          this.slots.get(i).mousePress();
          if (this.slots.get(i).button.hovered) {
            source_item = this.slots.get(i).item;
            if (this.item_holding == null || this.item_holding.remove) {
              this.item_origin = new InventoryKey(InventoryLocation.INVENTORY, i);
              item_holding_x = 2 + (x + 0.5) * this.button_size;
              item_holding_y = 2 + (y + 0.5) * this.button_size;
              this.grab_mouse_button = p.mouseButton;
            }
            else {
              if (p.mouseButton != this.grab_mouse_button) {
                if (source_item == null || source_item.remove) {
                  this.slots.get(i).item = new Item(p, this.item_holding);
                  this.slots.get(i).item.stack = 1;
                  this.item_holding.removeStack();
                }
                else if (source_item.ID == this.item_holding.ID &&
                  source_item.maxStack() > source_item.stack) {
                  source_item.addStack();
                  this.item_holding.removeStack();
                }
              }
              return;
            }
            found_clicked = true;
            break;
          }
        }
      }
      // gear
      if (!found_clicked) {
        for (int x = 0; x < this.gear_inventory.max_cols; x++) {
          for (int y = 0; y < this.gear_inventory.max_rows; y++) {
            int i = y * this.gear_inventory.max_cols + x;
            if (i >= this.gear_inventory.slots.size()) {
              break;
            }
            if (!this.gear_inventory.slotActive(i)) {
              continue;
            }
            this.gear_inventory.slots.get(i).mousePress();
            if (this.gear_inventory.slots.get(i).button.hovered) {
              source_item = this.gear_inventory.getItem(i);
              if (this.item_holding == null || this.item_holding.remove) {
                this.item_origin = new InventoryKey(InventoryLocation.GEAR, i);
                item_holding_x = (x + 0.5) * this.button_size - this.gear_inventory.display_width;
                item_holding_y = 0.5 * (this.display_height - this.gear_inventory.
                  display_height) + 2 + (y + 0.5) * this.button_size;
                this.grab_mouse_button = p.mouseButton;
              }
              else {
                if (p.mouseButton != this.grab_mouse_button) {
                  if (source_item == null || source_item.remove) {
                    this.gear_inventory.slots.get(i).item = new Item(p, this.item_holding);
                    this.gear_inventory.slots.get(i).item.stack = 1;
                    this.item_holding.removeStack();
                  }
                  else if (source_item.ID == this.item_holding.ID &&
                    source_item.maxStack() > source_item.stack) {
                    source_item.addStack();
                    this.item_holding.removeStack();
                  }
                }
                return;
              }
              found_clicked = true;
              break;
            }
          }
          if (found_clicked) {
            break;
          }
        }
      }
      // crafting
      this.crafting_inventory.craft.mousePress();
      this.crafting_inventory.tools.mousePress();
      if (!found_clicked) {
        for (int x = 0; x < this.crafting_inventory.max_cols; x++) {
          for (int y = 0; y < this.crafting_inventory.max_rows; y++) {
            int i = y * this.crafting_inventory.max_cols + x;
            if (i >= this.crafting_inventory.slots.size()) {
              break;
            }
            this.crafting_inventory.slots.get(i).mousePress();
            if (this.crafting_inventory.slots.get(i).button.hovered) {
              source_item = this.crafting_inventory.slots.get(i).item;
              if (this.item_holding == null || this.item_holding.remove) {
                this.item_origin = new InventoryKey(InventoryLocation.CRAFTING, i);
                item_holding_x = this.display_width + 4 + (x + 0.5) * this.button_size +
                  this.crafting_inventory.slots.get(i).button.xi;
                item_holding_y = 0.5 * (this.display_height - this.crafting_inventory.
                  display_height) + 2 + (y + 0.5) * this.button_size + this.crafting_inventory.slots.get(i).button.yi;
                this.grab_mouse_button = p.mouseButton;
              }
              else {
                if (p.mouseButton != this.grab_mouse_button) {
                  if (source_item == null || source_item.remove) {
                    this.crafting_inventory.slots.get(i).item = new Item(p, this.item_holding);
                    this.crafting_inventory.slots.get(i).item.stack = 1;
                    this.item_holding.removeStack();
                  }
                  else if (source_item.ID == this.item_holding.ID &&
                    source_item.maxStack() > source_item.stack) {
                    source_item.addStack();
                    this.item_holding.removeStack();
                  }
                }
                return;
              }
              found_clicked = true;
              break;
            }
          }
          if (found_clicked) {
            break;
          }
        }
      }
      // feature
      if (!found_clicked) {
        if (this.feature_inventory != null) {
          for (int x = 0; x < this.feature_inventory.max_cols; x++) {
            for (int y = 0; y < this.feature_inventory.max_rows; y++) {
              int i = y * this.feature_inventory.max_cols + x;
              if (i >= this.feature_inventory.slots.size()) {
                break;
              }
              this.feature_inventory.slots.get(i).mousePress();
              if (this.feature_inventory.slots.get(i).button.hovered) {
                source_item = this.feature_inventory.slots.get(i).item;
                if (this.item_holding == null || this.item_holding.remove) {
                  this.item_origin = new InventoryKey(InventoryLocation.FEATURE, i);
                  item_holding_x = 0.5 * (this.display_width - this.
                    feature_inventory.display_width) + 2 + (x + 0.5) * this.button_size +
                    this.feature_inventory.slots.get(i).button.xi;
                  item_holding_y = (y + 0.5) * this.button_size - this.feature_inventory.
                    display_height + this.feature_inventory.slots.get(i).button.yi;
                  this.grab_mouse_button = p.mouseButton;
                }
                else {
                  if (p.mouseButton != this.grab_mouse_button) {
                    if (source_item == null || source_item.remove) {
                      this.feature_inventory.slots.get(i).item = new Item(p, this.item_holding);
                      this.feature_inventory.slots.get(i).item.stack = 1;
                      this.item_holding.removeStack();
                    }
                    else if (source_item.ID == this.item_holding.ID &&
                      source_item.maxStack() > source_item.stack) {
                      source_item.addStack();
                      this.item_holding.removeStack();
                    }
                  }
                  return;
                }
                found_clicked = true;
                break;
              }
            }
            if (found_clicked) {
              break;
            }
          }
        }
      }
      if (found_clicked) {
        switch(p.mouseButton) {
          case PConstants.LEFT:
            if (source_item == null) {
              break;
            }
            this.item_holding = new Item(p, source_item);
            source_item.remove = true;
            break;
          case PConstants.RIGHT:
            if (source_item == null) {
              break;
            }
            this.item_holding = new Item(p, source_item);
            if (this.item_holding != null && !this.item_holding.remove) {
              this.item_holding.stack = 1;
            }
            source_item.removeStack(1);
            break;
          case PConstants.CENTER:
            if (source_item == null) {
              break;
            }
            int stack_to_transfer = (int)(Math.ceil(0.5 * source_item.stack));
            this.item_holding = new Item(p, source_item);
            if (this.item_holding != null && !this.item_holding.remove) {
              this.item_holding.stack = stack_to_transfer;
            }
            source_item.removeStack(stack_to_transfer);
            break;
        }
        if (this.item_holding != null) {
          if (p.global.holding_shift) {
            switch(this.item_origin.location) {
              case INVENTORY:
                if (this.feature_inventory == null) {
                  this.item_holding = this.gear_inventory.placeAt(this.item_holding, 3, false);
                }
                else {
                  this.item_holding = this.feature_inventory.stash(this.item_holding);
                }
                break;
              case GEAR:
                this.item_holding = this.stash(this.item_holding);
                break;
              case FEATURE:
                this.item_holding = super.stash(this.item_holding);
                if (this.item_holding != null && !this.item_holding.remove) {
                  this.item_holding = this.gear_inventory.placeAt(this.item_holding, 3, false);
                }
                break;
              case CRAFTING:
                this.item_holding = this.stash(this.item_holding);
                break;
            }
          }
          if (this.item_holding != null) {
            this.item_holding.coordinate.x = item_holding_x;
            this.item_holding.coordinate.y = item_holding_y;
          }
        }
      }
    }

    @Override
    void mouseRelease(float mX, float mY) {
      // process latest hovered information
      super.mouseRelease(mX, mY);
      float gearInventoryTranslateX = (float)(- this.gear_inventory.display_width - 2);
      float gearInventoryTranslateY = (float)(0.5 * (this.display_height - this.gear_inventory.display_height));
      this.gear_inventory.mouseRelease(mX - gearInventoryTranslateX, mY - gearInventoryTranslateY);
      float craftingInventoryTranslateX = (float)(this.display_width + 2);
      float craftingInventoryTranslateY = (float)(0.5 * (this.display_height - this.crafting_inventory.display_height));
      this.crafting_inventory.mouseRelease(mX - craftingInventoryTranslateX, mY - craftingInventoryTranslateY);
      if (this.feature_inventory != null) {
        float featureInventoryTranslateX = (float)(0.5 * (this.display_width - this.feature_inventory.display_width));
        float featureInventoryTranslateY = (float)(- this.feature_inventory.display_height - 2);
        this.feature_inventory.mouseRelease(mX - featureInventoryTranslateX, mY - featureInventoryTranslateY);
      }
      // process item holding
      if (this.item_holding == null || this.item_holding.remove || p.mouseButton != this.grab_mouse_button) {
        return;
      }
      // main inventory
      boolean found_hovered = false;
      for (int i = 0; i < this.slots.size(); i++) {
        if (this.slots.get(i).button.hovered) {
          this.item_holding = this.placeAt(this.item_holding, i, true);
          found_hovered = true;
          this.dropItemHolding();
          break;
        }
      }
      // gear
      if (found_hovered) {
        return;
      }
      for (int i = 0; i < this.gear_inventory.slots.size(); i++) {
        if (!this.gear_inventory.slotActive(i)) {
          continue;
        }
        if (this.gear_inventory.slots.get(i).button.hovered) {
          this.item_holding = this.gear_inventory.placeAt(this.item_holding, i, true);
          found_hovered = true;
          this.dropItemHolding();
          break;
        }
      }
      // crafting
      if (found_hovered) {
        return;
      }
      for (int i = 0; i < this.crafting_inventory.slots.size(); i++) {
        if (this.crafting_inventory.slots.get(i).button.hovered) {
          this.item_holding = this.crafting_inventory.placeAt(this.item_holding, i, true);
          found_hovered = true;
          this.dropItemHolding();
          break;
        }
      }
      // feature
      if (found_hovered) {
        return;
      }
      if (this.feature_inventory != null) {
        for (int i = 0; i < this.feature_inventory.slots.size(); i++) {
          if (this.feature_inventory.slots.get(i).button.hovered) {
            this.item_holding = this.feature_inventory.placeAt(this.item_holding, i, true);
            found_hovered = true;
            this.dropItemHolding();
            break;
          }
        }
      }
      if (!found_hovered) {
        this.dropItemHolding();
      }
    }
  }


  class InventoryBar {
    class StatusEffectTextBox extends TextBox {
      private boolean display = false;
      StatusEffectTextBox(LNZ sketch) {
        super(sketch, 0, 0, 0, 0);
        this.setTitleSize(15);
        this.setTextSize(13);
        this.color_background = sketch.global.color_nameDisplayed_background;
        this.color_header = sketch.global.color_nameDisplayed_background;
        this.color_stroke = DImg.ccolor(1, 0);
        this.color_text = DImg.ccolor(255);
        this.color_title = DImg.ccolor(255);
        this.scrollbar.setButtonColors(DImg.ccolor(170),
          DImg.adjustColorBrightness(sketch.global.color_nameDisplayed_background, 1.1),
          DImg.adjustColorBrightness(sketch.global.color_nameDisplayed_background, 1.2),
          DImg.adjustColorBrightness(sketch.global.color_nameDisplayed_background, 0.95), DImg.ccolor(0));
        this.scrollbar.button_upspace.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
          DImg.ccolor(1, 0), DImg.ccolor(0), DImg.ccolor(0));
        this.scrollbar.button_downspace.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
          DImg.ccolor(1, 0), DImg.ccolor(0), DImg.ccolor(0));
        this.useElapsedTime();
      }
    }

    class AbilityButton extends RectangleButton {
      class AbilityTextBox extends TextBox {
        private boolean display = false;
        AbilityTextBox(LNZ sketch, double xi, double yi, double xf, double yf) {
          super(sketch, xi, yi, xf, yf);
          this.setTitleSize(18);
          this.setTextSize(15);
          this.color_background = sketch.global.color_nameDisplayed_background;
          this.color_header = sketch.global.color_nameDisplayed_background;
          this.color_stroke = DImg.ccolor(1, 0);
          this.color_text = DImg.ccolor(255);
          this.color_title = DImg.ccolor(255);
          this.scrollbar.setButtonColors(DImg.ccolor(170),
            DImg.adjustColorBrightness(sketch.global.color_nameDisplayed_background, 1.1),
            DImg.adjustColorBrightness(sketch.global.color_nameDisplayed_background, 1.2),
            DImg.adjustColorBrightness(sketch.global.color_nameDisplayed_background, 0.95), DImg.ccolor(0));
          this.scrollbar.button_upspace.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
            DImg.ccolor(1, 0), DImg.ccolor(0), DImg.ccolor(0));
          this.scrollbar.button_downspace.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
            DImg.ccolor(1, 0), DImg.ccolor(0), DImg.ccolor(0));
          this.useElapsedTime();
        }
      }

      private LNZ p;
      protected Ability a = null;
      protected AbilityTextBox description;

      AbilityButton(LNZ sketch, double xi, double yi, double xf, double yf, Ability a, String letter) {
        super(sketch, xi, yi, xf, yf);
        this.p = sketch;
        this.setMessage(letter);
        this.roundness = 10;
        this.setColors(DImg.ccolor(0), DImg.ccolor(0), DImg.ccolor(0), DImg.ccolor(0), DImg.ccolor(0));
        this.setStroke(InventoryBar.this.color_ability_border, 2);
        this.use_time_elapsed = true;
        this.description = new AbilityTextBox(sketch, xi, yi, xi, yi);
        this.setAbility(a);
      }

      void setAbility(Ability a) {
        this.a = a;
        if (a == null) {
          this.description.setTitleText("");
          this.description.setText("");
          this.description.setXLocation(this.xi, this.xi);
          this.description.setYLocation(this.yi, this.yi);
        }
        else {
          p.textSize(18);
          double description_width = Math.max(LNZ.hero_abilityDescriptionMinWidth, 4 + p.textWidth(a.displayName()));
          this.description.setXLocation(this.xi - 0.5 * description_width, this.xf + 0.5 * description_width);
          double description_height = p.textAscent() + p.textDescent() + 6;
          p.textSize(15);
          this.description.setTitleText(a.displayName());
          this.description.setText(a.description());
          description_height += (2 + this.description.text_lines.size()) *
            (p.textAscent() + p.textDescent() + this.description.text_leading);
          this.description.setYLocation(this.yi - description_height, this.yi);
        }
      }

      @Override
      public void update(int timeElapsed) {
        super.update(timeElapsed);
        if (this.description.display) {
          this.description.update(timeElapsed);
        }
      }

      @Override
      public void drawButton() {
        super.drawButton();
        if (this.a != null) {
          p.imageMode(PConstants.CORNERS);
          p.ellipseMode(PConstants.CENTER);
          p.image(this.a.getImage(), this.xi, this.yi, this.xf, this.yf);
          if (InventoryBar.this.hero().silenced()) {
            p.image(p.global.images.getImage("statuses/silenced.png"), this.xi, this.yi, this.xf, this.yf);
          }
          if (this.a.timer_cooldown > 0) {
            p.fill(100, 100, 255, 140);
            p.noStroke();
            try {
              double angle = -PConstants.HALF_PI + 2 * PConstants.PI* this.a.timer_cooldown / this.a.timer_cooldown();
              p.arc(this.xCenter(), this.yCenter(), this.buttonWidth(),
                this.buttonHeight(), -PConstants.HALF_PI, angle, PConstants.PIE);
            } catch(Exception e) {}
            p.fill(255);
            p.textSize(24);
            p.textAlign(PConstants.CENTER, PConstants.CENTER);
            p.text(Integer.toString((int)Math.ceil(0.001 * this.a.timer_cooldown)),
              this.xCenter(), this.yCenter());
          }
          if (this.a.checkMana()) {
            p.fill(255);
            p.textSize(18);
            p.textAlign(PConstants.LEFT, PConstants.BOTTOM);
            p.text(Integer.toString((int)Math.ceil(this.a.manaCost())),
              this.xi + 2, this.yf - 1);
          }
          p.fill(255);
          p.textSize(18);
          p.textAlign(PConstants.LEFT, PConstants.TOP);
          p.text(this.message, this.xi + 2, this.yi + 1);
        }
      }

      public void hover() {
        if (this.a != null) {
          this.description.display = true;
          // show cast indicators
        }
      }
      public void dehover() {
        this.description.display = false;
      }
      public void click() {
        InventoryBar.this.tryCast(this.message);
      }
      public void release() {}
    }

    private LNZ p;

    private double xi_border = 0;
    protected double yi = 0;
    private double xf_border = 0;
    private double yf = 0;
    private double xi_bar = 0;
    private double xf_bar = 0;
    private double xi_picture = 0;
    private double yi_picture = 0;
    private double xf_picture = 0;
    private double yf_picture = 0;
    private double radius_picture = 0;
    private double ability_width = 0;
    private double slot_width = 0;
    private double status_width = 0;
    private double yi_status = 0;
    private double yi_slot = 0;

    private double last_mX = 0;
    private double last_mY = 0;
    private StatusEffectCode code_hovered = null;
    private StatusEffect effect_hovered = null;
    private StatusEffectTextBox code_description;
    private ArrayList<AbilityButton> ability_buttons = new ArrayList<AbilityButton>();
    private boolean portrait_hovered = false;
    private boolean portrait_clicked = false;
    protected boolean hovered = false;

    protected int color_background = DImg.ccolor(210, 153, 108);
    protected int color_ability_border = DImg.ccolor(120, 70, 40);

    protected int equipped_index = 0;
    protected double equipped_text_size = 15;
    protected boolean unlocked_inventory_bar1 = false;
    protected boolean unlocked_inventory_bar2 = false;


    InventoryBar(LNZ sketch) {
      this.p = sketch;
      this.code_description = new StatusEffectTextBox(sketch);
      if (p.global.profile == null) {
        this.setHeight(LNZ.hero_defaultInventoryBarHeight);
      }
      else {
        this.setHeight(p.global.profile.options.inventory_bar_size);
      }
    }


    Hero hero() {
      return Hero.this;
    }


    void setHeight(double new_height) {
      this.yf = p.height - LNZ.hero_inventoryBarGap;
      this.yi = this.yf - new_height;
      p.textSize(this.equipped_text_size);
      double new_height_buttons = new_height - p.textAscent() - p.textDescent() - 2;
      this.yi_slot = this.yi + p.textAscent() + p.textDescent() + 2;
      this.xf_bar = 0.5 * (p.width + 3.333 * new_height_buttons) + 2;
      this.xi_bar = 0.5 * (p.width - 3.333 * new_height_buttons) - 2;
      this.xf_border = this.xi_bar - LNZ.hero_inventoryBarGap;
      this.xi_border = this.xf_border - new_height;
      double border_thickness = 0.04166667 * new_height;
      this.xi_picture = this.xi_border + border_thickness;
      this.yi_picture = this.yi + border_thickness;
      this.xf_picture = this.xf_border - border_thickness;
      this.yf_picture = this.yf - border_thickness;
      this.radius_picture = 0.5 * (this.xf_picture - this.xi_picture);
      this.ability_width = 0.2 * (this.xf_bar - this.xi_bar - 4) - 4;
      this.slot_width = 0.1 * (this.xf_bar - this.xi_bar - 4) - 2;
      this.status_width = 0.08 * (this.xf_bar - this.xi_bar);
      this.yi_status = this.yi - 2 - this.status_width;
      double xi = this.xi_bar + 4;
      double yi = this.yf - this.ability_width - 4;
      this.ability_buttons.clear();
      for (int i = 0; i < LNZ.hero_abilityNumber; i++, xi += this.ability_width + 4) {
        String letter = "";
        switch(i) {
          case 0:
            letter = "P";
            break;
          case 1:
            letter = "A";
            break;
          case 2:
            letter = "S";
            break;
          case 3:
            letter = "D";
            break;
          case 4:
            letter = "F";
            break;
        }
        Ability a = null;
        try {
          a = Hero.this.abilities.get(i);
        } catch(Exception e) {}
        this.ability_buttons.add(
          new AbilityButton(p, xi, yi, xi + this.ability_width,
          yi + this.ability_width, a, letter));
      }
    }

    void tryCast(String letter) {
      switch(letter) {
        case "A":
          Hero.this.bufferCast(1);
          break;
        case "S":
          Hero.this.bufferCast(2);
          break;
        case "D":
          Hero.this.bufferCast(3);
          break;
        case "F":
          Hero.this.bufferCast(4);
          break;
      }
    }

    PImage getBorderImage() {
      String imageName = "icons/border";
      switch(Hero.this.code) {
        case BEN:
          imageName += "_gray.png";
          break;
        case DAN:
          imageName += "_brown.png";
          break;
        default:
          imageName += "_template.png";
          break;
      }
      return p.global.images.getImage(imageName);
    }

    PImage getHeroImage() {
      String imageName = "units/";
      switch(Hero.this.code) {
        case BEN:
          if (p.global.profile.ben_has_eyes) {
            imageName += "ben_circle.png";
          }
          else {
            imageName += "ben_circle_noeyes.png";
          }
          break;
        case DAN:
          imageName += "dan_circle.png";
          break;
        default:
          imageName += "default.png";
          break;
      }
      return p.global.images.getImage(imageName);
    }


    void setEquippedIndex(int new_index) {
      int max_equipped_index = 0;
      if (this.unlocked_inventory_bar2) {
        max_equipped_index = 9;
      }
      else if (this.unlocked_inventory_bar1) {
        max_equipped_index = 4;
      }
      else {
        return;
      }
      if (new_index < 0 || new_index > max_equipped_index) {
        return;
      }
      int last_index = this.equipped_index;
      this.equipped_index = new_index;
      if (new_index == last_index) {
        return;
      }
      Item curr_item = new Item(p, Hero.this.weapon());
      if (curr_item.remove) {
        curr_item = null;
      }
      if (new_index > last_index) {
        for (int i = last_index; i < new_index; i++) {
          Item next_item = new Item(p, Hero.this.inventory.slots.get(i).item);
          Hero.this.inventory.slots.get(i).item = curr_item;
          curr_item = next_item;
        }
        Hero.this.pickup(curr_item);
      }
      else {
        for (int i = last_index-1; i >= new_index; i--) {
          Item next_item = new Item(p, Hero.this.inventory.slots.get(i).item);
          Hero.this.inventory.slots.get(i).item = curr_item;
          curr_item = next_item;
        }
        Hero.this.pickup(curr_item);
      }
    }


    void update(int timeElapsed) {
      if (p.global.profile.options.inventory_bar_hidden) {
        return;
      }
      p.rectMode(PConstants.CORNERS);
      p.noStroke();
      p.fill(this.color_background);
      p.rect(this.xi_bar, this.yi, this.xf_bar, this.yf, 12);
      p.imageMode(PConstants.CORNERS);
      p.image(this.getBorderImage(), this.xi_border, this.yi, this.xf_border, this.yf);
      if (this.portrait_clicked) {
        p.tint(150);
      }
      p.image(this.getHeroImage(), this.xi_picture, this.yi_picture, this.xf_picture, this.yf_picture);
      if (this.portrait_clicked) {
        p.g.removeCache(this.getHeroImage());
        p.noTint();
      }
      double xi = this.xi_bar;
      this.code_hovered = null;
      this.effect_hovered = null;
      for (Map.Entry<StatusEffectCode, StatusEffect> entry : Hero.this.statuses.entrySet()) {
        p.imageMode(PConstants.CORNER);
        p.rectMode(PConstants.CORNER);
        p.ellipseMode(PConstants.CENTER);
        p.fill(255, 150);
        p.stroke(0);
        p.strokeWeight(1);
        p.rect(xi, this.yi_status, this.status_width, this.status_width);
        p.image(p.global.images.getImage(entry.getKey().getImageString()), xi, this.yi_status, this.status_width, this.status_width);
        if (!entry.getValue().permanent) {
          p.fill(100, 100, 255, 140);
          p.noStroke();
          try {
            double angle = -PConstants.HALF_PI + 2 * PConstants.PI* entry.getValue().timer_gone / entry.getValue().timer_gone_start;
            p.arc(xi + 0.5 * this.status_width, this.yi_status + 0.5 * this.status_width,
              this.status_width, this.status_width, -PConstants.HALF_PI, angle, PConstants.PIE);
          } catch(Exception e) {}
        }
        if (this.last_mX > xi && this.last_mX < xi + this.status_width &&
          this.last_mY > this.yi_status && this.last_mY < this.yi_status + this.status_width) {
          if (!this.code_description.display) {
            p.noStroke();
            p.fill(p.global.color_nameDisplayed_background);
            p.textSize(14);
            p.rectMode(PConstants.CORNER);
            double rect_height = p.textAscent() + p.textDescent() + 2;
            double rect_width = p.textWidth(entry.getKey().codeName()) + 2;
            p.rect(this.last_mX - rect_width - 1, this.last_mY - rect_height - 1, rect_width, rect_height);
            p.fill(255);
            p.textAlign(PConstants.LEFT, PConstants.TOP);
            p.text(entry.getKey().codeName(), this.last_mX - rect_width - 1, this.last_mY - rect_height - 1);
          }
          this.code_hovered = entry.getKey();
          this.effect_hovered = entry.getValue();
        }
        xi += this.status_width + 2;
      }
      p.textAlign(PConstants.LEFT, PConstants.TOP);
      p.textSize(this.equipped_text_size);
      p.fill(0);
      if (Hero.this.weapon() == null) {
        p.text("-- no weapon --", this.xi_bar + 1, this.yi + 1);
      }
      else {
        p.text(Hero.this.weapon().displayName(), this.xi_bar + 1, this.yi + 1);
      }
      p.translate(this.xi_bar + 3, this.yi_slot);
      double translate_x = this.equipped_index * (this.slot_width + 2);
      p.translate(translate_x, 0);
      Hero.this.inventory.gear_inventory.updateSlot(timeElapsed, 3, Hero.this.weapon(), false, this.slot_width, !Hero.this.inventory.viewing);
      p.translate(-translate_x, 0);
      int inventory_slots_to_show = 0;
      if (this.unlocked_inventory_bar2) {
        inventory_slots_to_show = 9;
      }
      else if (this.unlocked_inventory_bar1) {
        inventory_slots_to_show = 4;
      }
      for (int i = 0; i < inventory_slots_to_show; i++) {
        int translate_index = i;
        if (translate_index >= this.equipped_index) {
          translate_index++;
        }
        translate_x = translate_index * (this.slot_width + 2);
        p.translate(translate_x, 0);
        Hero.this.inventory.slots.get(i).setWidth(this.slot_width);
        Hero.this.inventory.slots.get(i).update(timeElapsed, !Hero.this.inventory.viewing);
        Hero.this.inventory.slots.get(i).setWidth(Hero.this.inventory.button_size);
        p.translate(-translate_x, 0);
      }
      p.translate(-this.xi_bar - 3, -this.yi_slot);
      for (AbilityButton ability : this.ability_buttons) {
        ability.update(timeElapsed);
      }
      if (this.code_description.display) {
        this.code_description.update(timeElapsed);
      }
    }

    void mouseMove(float mX, float mY) {
      this.hovered = false;
      if (p.global.profile.options.inventory_bar_hidden) {
        return;
      }
      this.last_mX = mX;
      this.last_mY = mY;
      for (AbilityButton ability : this.ability_buttons) {
        ability.mouseMove(mX, mY);
      }
      if (this.code_description.display) {
        this.code_description.mouseMove(mX, mY);
      }
      if (this.code_hovered != null || this.code_description.hovered) {
        this.hovered = true;
      }
      if ((this.code_hovered == null || !this.code_hovered.codeName().equals(
        this.code_description.text_title)) && !this.code_description.hovered) {
        this.code_description.display = false;
      }
      double portrait_distance_x = mX - this.xi_picture - this.radius_picture;
      double portrait_distance_y = mY - this.yi_picture - this.radius_picture;
      if (Math.sqrt(portrait_distance_x * portrait_distance_x + portrait_distance_y * portrait_distance_y) < this.radius_picture) {
        this.portrait_hovered = true;
        this.hovered = true;
      }
      else {
        this.portrait_hovered = false;
      }
      if (!this.hovered) {
        if (mX > this.xi_bar && mY > this.yi && mX < this.xf_bar && mY < this.yf) {
          this.hovered = true;
        }
      }
      if (!Hero.this.inventory.viewing) {
        mX -= this.xi_bar + 3;
        mY -= this.yi_slot;
        float translate_x = (float)(this.equipped_index * (this.slot_width + 2));
        Hero.this.inventory.gear_inventory.slots.get(3).setWidth(this.slot_width);
        Hero.this.inventory.gear_inventory.slots.get(3).mouseMove(mX - translate_x, mY);
        Hero.this.inventory.gear_inventory.slots.get(3).setWidth(Hero.this.inventory.gear_inventory.button_size);
        int inventory_slots_to_show = 0;
        if (this.unlocked_inventory_bar2) {
          inventory_slots_to_show = 9;
        }
        else if (this.unlocked_inventory_bar1) {
          inventory_slots_to_show = 4;
        }
        for (int i = 0; i < inventory_slots_to_show; i++) {
          int translate_index = i;
          if (translate_index >= this.equipped_index) {
            translate_index++;
          }
          translate_x = (float)(translate_index * (this.slot_width + 2));
          Hero.this.inventory.slots.get(i).setWidth(this.slot_width);
          Hero.this.inventory.slots.get(i).mouseMove(mX - translate_x, mY);
          Hero.this.inventory.slots.get(i).setWidth(Hero.this.inventory.button_size);
        }
      }
    }

    void mousePress() {
      if (p.global.profile.options.inventory_bar_hidden) {
        return;
      }
      for (AbilityButton ability : this.ability_buttons) {
        ability.mousePress();
      }
      if (this.code_description.display) {
        this.code_description.mousePress();
      }
      if (this.code_hovered == null && !this.code_description.hovered) {
        this.code_description.display = false;
      }
      else if (code_hovered != null) {
        this.code_description.display = true;
        this.code_description.setLocation(this.last_mX - LNZ.hero_statusDescription_width,
          this.last_mY - LNZ.hero_statusDescription_height, this.last_mX, this.last_mY);
        this.code_description.setTitleText(this.code_hovered.codeName());
        this.code_description.setText("Source: " +
          this.effect_hovered.damage_source.toString() +
          "\n\n" + this.code_hovered.description());
      }
      if (this.portrait_hovered) {
        this.portrait_clicked = true;
      }
      if (!Hero.this.inventory.viewing) {
        boolean use_item = false;
        if (this.unlocked_inventory_bar2 && p.global.holding_ctrl) {
          use_item = true;
        }
        if (Hero.this.inventory.gear_inventory.slots.get(3).button.hovered) {
          Hero.this.inventory.gear_inventory.slots.get(3).button.hovered = false;
          if (use_item) {
            Hero.this.useItem(null);
          }
        }
        int inventory_slots_to_show = 0;
        if (this.unlocked_inventory_bar2) {
          inventory_slots_to_show = 9;
        }
        else if (this.unlocked_inventory_bar1) {
          inventory_slots_to_show = 4;
        }
        for (int i = 0; i < inventory_slots_to_show; i++) {
          int translate_index = i;
          if (translate_index >= this.equipped_index) {
            translate_index++;
          }
          if (Hero.this.inventory.slots.get(i).button.hovered) {
            Hero.this.inventory.slots.get(i).button.hovered = false;
            if (use_item) {
              Hero.this.useItem(null, new InventoryKey(InventoryLocation.INVENTORY, i));
            }
            else {
              this.setEquippedIndex(translate_index);
            }
          }
        }
      }
    }

    void mouseRelease(float mX, float mY) {
      if (p.global.profile.options.inventory_bar_hidden) {
        return;
      }
      for (AbilityButton ability : this.ability_buttons) {
        ability.mouseRelease(mX, mY);
      }
      if (this.code_description.display) {
        this.code_description.mouseRelease(mX, mY);
      }
      if (this.portrait_hovered && this.portrait_clicked) {
        Hero.this.openLeftPanelMenu(LeftPanelMenuPage.PLAYER);
      }
      this.portrait_clicked = false;
    }

    void scroll(int amount) {
      if (p.global.profile.options.inventory_bar_hidden) {
        return;
      }
      if (this.code_description.display) {
        this.code_description.scroll(amount);
        return;
      }
      if (!Hero.this.inventory.viewing && !p.global.holding_ctrl) {
        int max_equipped_index = 0;
        if (this.unlocked_inventory_bar2) {
          max_equipped_index = 9;
        }
        else if (this.unlocked_inventory_bar1) {
          max_equipped_index = 4;
        }
        else {
          return;
        }
        int new_equipped_index = this.equipped_index + amount;
        while (new_equipped_index > max_equipped_index) {
          new_equipped_index -= max_equipped_index + 1;
        }
        while (new_equipped_index < 0) {
          new_equipped_index += max_equipped_index + 1;
        }
        this.setEquippedIndex(new_equipped_index);
      }
    }
  }

  enum LeftPanelMenuPage {
    NONE, PLAYER; // others in future as game becomes more complex
  }

  abstract class LeftPanelMenu {
    protected LNZ p;
    LeftPanelMenu(LNZ sketch) {
      this.p = sketch;
    }
    Hero hero() {
      return Hero.this;
    }
    abstract void deposited(double money);
    abstract void drawPanel(int timeElapsed, double panel_width);
    abstract void mouseMove(float mX, float mY);
    abstract void mousePress();
    abstract void mouseRelease(float mX, float mY);
  }


  class PlayerLeftPanelMenu extends LeftPanelMenu {
    abstract class LeftPanelButton extends RectangleButton {
      private LNZ p;

      protected double hover_timer = LNZ.hero_leftPanelButtonHoverTimer;
      protected boolean show_hover_message = false;
      protected String hover_message = "";
      protected double hover_message_text_size = 15;
      protected double hover_message_offset = 0;

      LeftPanelButton(LNZ sketch, double xi, double yi, double xf, double yf) {
        super(sketch, xi, yi, xf, yf);
        this.p = sketch;
        p.textSize(15);
        this.hover_message_offset = 0.5 * (sketch.textAscent() + sketch.textDescent()) + 2;
      }

      @Override
      public void update(int millis) {
        int time_elapsed = millis - this.last_update_time;
        super.update(millis);
        if (this.show_hover_message) {
          p.fill(p.global.color_nameDisplayed_background);
          p.stroke(1, 0);
          p.rectMode(PConstants.CENTER);
          p.textSize(this.hover_message_text_size);
          double xCenter = p.mouseX + 0.5 * p.textWidth(this.hover_message + 2);
          double yCenter = p.mouseY - this.hover_message_offset;
          p.rect(xCenter, yCenter, p.textWidth(this.hover_message + 2), p.textAscent() + p.textDescent());
          p.textAlign(PConstants.CENTER, PConstants.CENTER);
          p.fill(255);
          p.text(this.hover_message, xCenter, yCenter);
          p.stroke(0);
        }
        else if (this.hovered) {
          this.hover_timer -= time_elapsed;
          if (this.hover_timer < 0) {
            this.show_hover_message = true;
          }
        }
      }

      public void hover() {
        this.updateHoverMessage();
      }
      public void dehover() {
        this.hover_timer = LNZ.hero_leftPanelButtonHoverTimer;
        this.show_hover_message = false;
      }
      public void click() {
        this.updateHoverMessage();
        this.show_hover_message = true;
      }
      public void release() {
        this.updateHoverMessage();
      }

      abstract void updateHoverMessage();
    }


    class MoneyButton extends LeftPanelButton {
      protected int blinks = 0;
      protected double blink_timer = 300;
      protected boolean blinking = false;

      MoneyButton(LNZ sketch, double yi, double yf) {
        super(sketch, 0, yi, 0, yf);
        this.setColors(DImg.ccolor(100, 100), DImg.ccolor(1, 0), DImg.ccolor(1, 0), DImg.ccolor(1, 0), DImg.ccolor(0));
        this.text_size = 18;
        this.show_message = true;
        this.roundness = 2;
        this.noStroke();
      }

      @Override
      public void update(int millis) {
        int time_elapsed = millis - this.last_update_time;
        if (this.blinks > 0) {
          this.blink_timer -= time_elapsed;
          if (this.blink_timer < 0) {
            this.blink_timer += 300;
            this.blinking = !this.blinking;
            if (this.blinking) {
              this.blinks--;
            }
          }
          if (this.blinking) {
            this.color_text = DImg.ccolor(1, 0);
          }
          else {
            this.color_text = DImg.ccolor(255, 255, 0);
          }
        }
        else {
          this.color_text = DImg.ccolor(0);
        }
        super.update(millis);
      }

      @Override
      public void writeText() {
        if (this.show_message) {
          p.fill(this.color_text);
          p.textAlign(PConstants.LEFT, PConstants.CENTER);
          p.textSize(this.text_size);
          if (this.adjust_for_text_descent) {
            p.text(this.message, this.xi + 2, this.yCenter() - p.textDescent());
          }
          else {
            p.text(this.message, this.xi + 2, this.yCenter());
          }
        }
      }

      @Override
      public void release() {
        super.release();
        // open wallet
      }

      void updateHoverMessage() {
        this.hover_message = "$" + Math.round(PlayerLeftPanelMenu.this.hero().money * 100.0) / 100.0 + " deposited";
      }
    }


    class LevelTokensButton extends LeftPanelButton {
      LevelTokensButton(LNZ sketch, double yi, double yf) {
        super(sketch, 0, yi, 0, yf);
        this.setColors(DImg.ccolor(170), DImg.ccolor(1, 0),
          DImg.ccolor(150, 100), DImg.ccolor(150, 200), DImg.ccolor(0));
        this.text_size = 18;
        this.show_message = true;
        this.roundness = 2;
        this.noStroke();
      }

      @Override
      public void writeText() {
        if (this.show_message) {
          p.fill(this.color_text);
          p.textAlign(PConstants.LEFT, PConstants.CENTER);
          p.textSize(this.text_size);
          if (this.adjust_for_text_descent) {
            p.text(this.message, this.xi + 2, this.yCenter() - p.textDescent());
          }
          else {
            p.text(this.message, this.xi + 2, this.yCenter());
          }
        }
      }

      @Override
      public void release() {
        if (!this.hovered) {
          return;
        }
        if (PlayerLeftPanelMenu.this.hero().hero_tree.curr_viewing) {
          PlayerLeftPanelMenu.this.hero().hero_tree.curr_viewing = false;
        }
        else {
          PlayerLeftPanelMenu.this.hero().hero_tree.curr_viewing = true;
          PlayerLeftPanelMenu.this.hero().hero_tree.set_screen_location = true;
        }
        super.release();
      }

      void updateHoverMessage() {
        if (PlayerLeftPanelMenu.this.hero().hero_tree.curr_viewing) {
          this.hover_message = "Close Level Tree";
        }
        else {
          this.hover_message = "Open Level Tree";
        }
      }
    }


    class LevelButton extends LeftPanelButton {
      LevelButton(LNZ sketch, double yi, double yf) {
        super(sketch, 0, yi, 0, yf);
        this.setColors(DImg.ccolor(100, 100), DImg.ccolor(1, 0),
          DImg.ccolor(1, 0), DImg.ccolor(1, 0), DImg.ccolor(0));
        this.text_size = 18;
        this.show_message = true;
        this.noStroke();
      }

      void updateHoverMessage() {
        this.hover_message = "Tier: " + PlayerLeftPanelMenu.this.hero().tier();
      }
    }


    class ExperienceButton extends LeftPanelButton {
      ExperienceButton(LNZ sketch, double yi, double yf) {
        super(sketch, 0, yi, 0, yf);
        this.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
          DImg.ccolor(1, 0), DImg.ccolor(1, 0), DImg.ccolor(1, 0));
        this.setStroke(DImg.ccolor(0), 1.5);
        this.roundness = 0;
      }

      @Override
      public void drawButton() {
        super.drawButton();
        p.rectMode(PConstants.CORNER);
        double xp_ratio = PlayerLeftPanelMenu.this.hero().experience /
          PlayerLeftPanelMenu.this.hero().experience_next_level;
        p.fill(0);
        p.rect(this.xi, this.yi, xp_ratio * this.buttonWidth(), LNZ.hero_leftPanelBarHeight);
      }

      void updateHoverMessage() {
        this.hover_message = "Experience: " + (int)PlayerLeftPanelMenu.this.hero().experience +
          "/" + PlayerLeftPanelMenu.this.hero().experience_next_level;
      }
    }


    class HealthButton extends LeftPanelButton {
      protected double bar_xi = 0;
      protected double bar_yi = 0;
      protected double bar_yf = 0;

      HealthButton(LNZ sketch, double yi, double yf) {
        super(sketch, 0, yi, 0, yf);
        this.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
          DImg.ccolor(1, 0), DImg.ccolor(1, 0), DImg.ccolor(1, 0));
        this.noStroke();
        this.roundness = 0;
        this.bar_xi = 4 + 2 * LNZ.hero_leftPanelBarHeight;
        this.bar_yi = yi + 0.25 * (yf - yi);
        this.bar_yf = yi + 0.75 * (yf - yi);
      }

      @Override
      public void drawButton() {
        super.drawButton();
        p.imageMode(PConstants.CORNERS);
        p.image(p.global.images.getImage("icons/health.png"), this.xi, this.yi,
          this.bar_xi - 2, this.yf);
        p.rectMode(PConstants.CORNERS);
        p.fill(0);
        p.noStroke();
        p.rect(this.bar_xi, this.bar_yi, this.xf, this.bar_yf);
        p.rectMode(PConstants.CORNER);
        double health_ratio = Math.min(1.0, PlayerLeftPanelMenu.this.hero().curr_health
          / PlayerLeftPanelMenu.this.hero().health());
        p.fill(0, 255, 0);
        p.rect(this.bar_xi, this.bar_yi, health_ratio * (this.xf - this.bar_xi),
          LNZ.hero_leftPanelBarHeight);
      }

      void updateHoverMessage() {
        this.hover_message = "Health: " + Misc.round(PlayerLeftPanelMenu.this.
          hero().curr_health, 1) + "/" + Misc.round(PlayerLeftPanelMenu.
          this.hero().health(), 1);
      }
    }

    class ManaButton extends LeftPanelButton {
      protected double bar_xi = 0;
      protected double bar_yi = 0;
      protected double bar_yf = 0;

      ManaButton(LNZ sketch, double yi, double yf) {
        super(sketch, 0, yi, 0, yf);
        this.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
          DImg.ccolor(1, 0), DImg.ccolor(1, 0), DImg.ccolor(1, 0));
        this.noStroke();
        this.roundness = 0;
        this.bar_xi = 4 + 2 * LNZ.hero_leftPanelBarHeight;
        this.bar_yi = yi + 0.25 * (yf - yi);
        this.bar_yf = yi + 0.75 * (yf - yi);
      }

      @Override
      public void drawButton() {
        super.drawButton();
        p.imageMode(PConstants.CORNERS);
        p.image(p.global.images.getImage("icons/mana_" + PlayerLeftPanelMenu.
          this.hero().manaFileName() + ".png"), this.xi, this.yi, this.bar_xi - 2, this.yf);
        p.rectMode(PConstants.CORNERS);
        p.fill(0);
        p.noStroke();
        p.rect(this.bar_xi, this.bar_yi, this.xf, this.bar_yf);
        p.rectMode(PConstants.CORNER);
        double mana_ratio = Math.min(1, PlayerLeftPanelMenu.this.hero().currMana() /
          PlayerLeftPanelMenu.this.hero().mana());
        p.fill(255, 255, 0);
        p.rect(this.bar_xi, this.bar_yi, mana_ratio * (this.xf - this.bar_xi),
          LNZ.hero_leftPanelBarHeight);
      }

      void updateHoverMessage() {
        this.hover_message = PlayerLeftPanelMenu.this.hero().manaDisplayName() +
          ": " + (int)Math.round(PlayerLeftPanelMenu.this.hero().currMana()) + "/" +
          (int)Math.round(PlayerLeftPanelMenu.this.hero().mana());
      }
    }

    class HungerButton extends LeftPanelButton {
      protected double bar_xi = 0;
      protected double bar_yi = 0;
      protected double bar_yf = 0;

      HungerButton(LNZ sketch, double yi, double yf) {
        super(sketch, 0, yi, 0, yf);
        this.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
          DImg.ccolor(1, 0), DImg.ccolor(1, 0), DImg.ccolor(1, 0));
        this.noStroke();
        this.roundness = 0;
        this.bar_xi = 4 + 2 * LNZ.hero_leftPanelBarHeight;
        this.bar_yi = yi + 0.25 * (yf - yi);
        this.bar_yf = yi + 0.75 * (yf - yi);
      }

      @Override
      public void drawButton() {
        super.drawButton();
        p.imageMode(PConstants.CORNERS);
        p.image(p.global.images.getImage("icons/hunger.png"), this.xi, this.yi,
          this.bar_xi - 2, this.yf);
        p.rectMode(PConstants.CORNERS);
        p.fill(0);
        p.noStroke();
        p.rect(this.bar_xi, this.bar_yi, this.xf, this.bar_yf);
        p.rectMode(PConstants.CORNER);
        double hunger_ratio = PlayerLeftPanelMenu.this.hero().hunger / (double)LNZ.hero_maxHunger;
        p.fill(140, 70, 20);
        p.rect(this.bar_xi, this.bar_yi, hunger_ratio * (this.xf - this.bar_xi), LNZ.hero_leftPanelBarHeight);
      }

      void updateHoverMessage() {
        this.hover_message = "Hunger: " + (int)(100 * PlayerLeftPanelMenu.
          this.hero().hunger / (double)LNZ.hero_maxHunger) + "%";
      }
    }


    class ThirstButton extends LeftPanelButton {
      protected double bar_xi = 0;
      protected double bar_yi = 0;
      protected double bar_yf = 0;

      ThirstButton(LNZ sketch, double yi, double yf) {
        super(sketch, 0, yi, 0, yf);
        this.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
          DImg.ccolor(1, 0), DImg.ccolor(1, 0), DImg.ccolor(1, 0));
        this.noStroke();
        this.roundness = 0;
        this.bar_xi = 4 + 2 * LNZ.hero_leftPanelBarHeight;
        this.bar_yi = yi + 0.25 * (yf - yi);
        this.bar_yf = yi + 0.75 * (yf - yi);
      }

      @Override
      public void drawButton() {
        super.drawButton();
        p.imageMode(PConstants.CORNERS);
        p.image(p.global.images.getImage("icons/thirst.png"), this.xi, this.yi,
          this.bar_xi - 2, this.yf);
        p.rectMode(PConstants.CORNERS);
        p.fill(0);
        p.noStroke();
        p.rect(this.bar_xi, this.bar_yi, this.xf, this.bar_yf);
        p.rectMode(PConstants.CORNER);
        double thirst_ratio = PlayerLeftPanelMenu.this.hero().thirst / (double)LNZ.hero_maxThirst;
        p.fill(0, 0, 255);
        p.rect(this.bar_xi, this.bar_yi, thirst_ratio * (this.xf - this.bar_xi), LNZ.hero_leftPanelBarHeight);
      }

      void updateHoverMessage() {
        this.hover_message = "Thirst: " + (int)(100 * PlayerLeftPanelMenu.
          this.hero().thirst / (double)LNZ.hero_maxThirst) + "%";
      }
    }


    abstract class StatButton extends LeftPanelButton {
      protected double icon_xf = 0;
      protected String icon_name = "";

      StatButton(LNZ sketch, double yi, double yf) {
        super(sketch, 0, yi, 0, yf);
        this.show_message = true;
        this.updateMessage();
        this.setColors(DImg.ccolor(1, 0), DImg.ccolor(1, 0),
          DImg.ccolor(1, 0), DImg.ccolor(1, 0), DImg.ccolor(0));
        this.noStroke();
        this.roundness = 0;
        this.icon_xf = yf - yi;
        this.text_size = 17;
      }

      @Override
      public void setXLocation(double xi, double xf) {
        super.setXLocation(xi, xf);
        this.icon_xf = this.xi + this.yf - this.yi;
      }

      @Override
      public void writeText() {
        if (this.show_message) {
          p.fill(this.color_text);
          p.textAlign(PConstants.LEFT, PConstants.CENTER);
          p.textSize(this.text_size);
          if (this.adjust_for_text_descent) {
            p.text(this.message, this.icon_xf + 6, this.yCenter() - p.textDescent());
          }
          else {
            p.text(this.message, this.icon_xf + 6, this.yCenter());
          }
        }
      }

      @Override
      public void drawButton() {
        this.updateMessage();
        super.drawButton();
        p.imageMode(PConstants.CORNERS);
        p.image(p.global.images.getImage("icons/" + this.icon_name + ".png"),
          this.xi, this.yi, this.icon_xf, this.yf);
      }

      abstract void updateMessage();
    }


    class AttackButton extends StatButton {
      AttackButton(LNZ sketch, double yi, double yf) {
        super(sketch, yi, yf);
        this.icon_name = "stat_attack";
      }

      void updateMessage() {
        this.setMessage(Integer.toString((int)Math.round(
          PlayerLeftPanelMenu.this.hero().attack())));
      }

      void updateHoverMessage() {
        this.hover_message = "Attack: " + Misc.round(
          PlayerLeftPanelMenu.this.hero().attack(), 2);
      }
    }


    class MagicButton extends StatButton {
      MagicButton(LNZ sketch, double yi, double yf) {
        super(sketch, yi, yf);
        this.icon_name = "stat_magic";
      }

      void updateMessage() {
        this.setMessage(Integer.toString((int)Math.round(
          PlayerLeftPanelMenu.this.hero().magic())));
      }

      void updateHoverMessage() {
        this.hover_message = "Magic: " + Misc.round(
          PlayerLeftPanelMenu.this.hero().magic(), 2);
      }
    }


    class DefenseButton extends StatButton {
      DefenseButton(LNZ sketch, double yi, double yf) {
        super(sketch, yi, yf);
        this.icon_name = "stat_defense";
      }

      void updateMessage() {
        this.setMessage(Integer.toString((int)Math.round(
          PlayerLeftPanelMenu.this.hero().defense())));
      }

      void updateHoverMessage() {
        this.hover_message = "Defense: " + Misc.round(
          PlayerLeftPanelMenu.this.hero().defense(), 2);
      }
    }


    class ResistanceButton extends StatButton {
      ResistanceButton(LNZ sketch, double yi, double yf) {
        super(sketch, yi, yf);
        this.icon_name = "stat_resistance";
      }

      void updateMessage() {
        this.setMessage(Integer.toString((int)Math.round(
          PlayerLeftPanelMenu.this.hero().resistance())));
      }

      void updateHoverMessage() {
        this.hover_message = "Resistance: " + Misc.round(
          PlayerLeftPanelMenu.this.hero().resistance(), 2);
      }
    }


    class PiercingButton extends StatButton {
      PiercingButton(LNZ sketch, double yi, double yf) {
        super(sketch, yi, yf);
        this.icon_name = "stat_piercing";
      }

      void updateMessage() {
        this.setMessage(Integer.toString((int)Math.round(100.0 *
          PlayerLeftPanelMenu.this.hero().piercing())) + "%");
      }

      void updateHoverMessage() {
        this.hover_message = "Piercing: " + (int)Math.round(1000.0 *
          PlayerLeftPanelMenu.this.hero().piercing()) / 10.0 + "%";
      }
    }


    class PenetrationButton extends StatButton {
      PenetrationButton(LNZ sketch, double yi, double yf) {
        super(sketch, yi, yf);
        this.icon_name = "stat_penetration";
      }

      void updateMessage() {
        this.setMessage(Integer.toString((int)Math.round(100.0 *
          PlayerLeftPanelMenu.this.hero().penetration())) + "%");
      }

      void updateHoverMessage() {
        this.hover_message = "Penetration: " + (int)Math.round(1000.0 *
          PlayerLeftPanelMenu.this.hero().penetration()) / 10.0 + "%";
      }
    }


    class RangeButton extends StatButton {
      RangeButton(LNZ sketch, double yi, double yf) {
        super(sketch, yi, yf);
        this.icon_name = "stat_range";
      }

      void updateMessage() {
        this.setMessage(Double.toString((int)Math.round(10 *
          PlayerLeftPanelMenu.this.hero().attackRange()) / 10.0));
      }

      void updateHoverMessage() {
        this.hover_message = "Attack Range: " + Misc.round(
          PlayerLeftPanelMenu.this.hero().attackRange(), 2) + "m";
      }
    }


    class SpeedButton extends StatButton {
      SpeedButton(LNZ sketch, double yi, double yf) {
        super(sketch, yi, yf);
        this.icon_name = "stat_speed";
      }

      void updateMessage() {
        this.setMessage(Double.toString((int)Math.round(10 *
          PlayerLeftPanelMenu.this.hero().speed()) / 10.0));
      }

      void updateHoverMessage() {
        this.hover_message = "Speed: " + Misc.round(
          PlayerLeftPanelMenu.this.hero().speed(), 2) + " m/s";
      }
    }


    class TenacityButton extends StatButton {
      TenacityButton(LNZ sketch, double yi, double yf) {
        super(sketch, yi, yf);
        this.icon_name = "stat_tenacity";
      }

      void updateMessage() {
        this.setMessage(Double.toString((int)Math.round(100 *
          PlayerLeftPanelMenu.this.hero().tenacity())) + "%");
      }

      void updateHoverMessage() {
        this.hover_message = "Tenacity: " + (int)Math.round(1000.0 *
          PlayerLeftPanelMenu.this.hero().tenacity()) / 10.0 + "%";
      }
    }


    class AgilityButton extends StatButton {
      AgilityButton(LNZ sketch, double yi, double yf) {
        super(sketch, yi, yf);
        this.icon_name = "stat_agility";
      }

      void updateMessage() {
        this.setMessage(Integer.toString(PlayerLeftPanelMenu.this.hero().agility()));
      }

      void updateHoverMessage() {
        this.hover_message = "Agility: " + PlayerLeftPanelMenu.this.hero().agility();
      }
    }


    protected double yi;
    protected double image_yi;
    protected double image_size;
    protected MoneyButton money;
    protected LevelTokensButton level_tokens;
    protected LevelButton level;
    protected ExperienceButton experience;
    protected HealthButton health;
    protected ManaButton mana;
    protected HungerButton hunger;
    protected ThirstButton thirst;
    protected AttackButton attack;
    protected MagicButton magic;
    protected DefenseButton defense;
    protected ResistanceButton resistance;
    protected PiercingButton piercing;
    protected PenetrationButton penetration;
    protected RangeButton range;
    protected SpeedButton speed;
    protected TenacityButton tenacity;
    protected AgilityButton agility;

    PlayerLeftPanelMenu(LNZ sketch) {
      super(sketch);
      this.yi = 0.5 * sketch.height + LNZ.map_selectedObjectPanelGap;
      p.textSize(LNZ.map_selectedObjectTitleTextSize);
      double currY = this.yi + sketch.textAscent() + sketch.textDescent() + LNZ.map_selectedObjectPanelGap;
      this.image_yi = currY;
      this.image_size = 0.1 * sketch.height;
      p.textSize(18);
      double button_text_height = sketch.textAscent() + sketch.textDescent() + LNZ.map_selectedObjectPanelGap;
      this.money = new MoneyButton(sketch, currY + this.image_size - 2 * button_text_height - LNZ.map_selectedObjectPanelGap,
        currY + this.image_size - button_text_height - LNZ.map_selectedObjectPanelGap);
      this.level_tokens = new LevelTokensButton(sketch, currY + this.image_size - button_text_height,
        currY + this.image_size - LNZ.map_selectedObjectPanelGap);
      currY += image_size + LNZ.map_selectedObjectPanelGap;
      this.level = new LevelButton(sketch, currY, currY + sketch.textAscent() + sketch.textDescent() + LNZ.map_selectedObjectPanelGap);
      currY += sketch.textAscent() + sketch.textDescent() + LNZ.map_selectedObjectPanelGap + 2;
      this.experience = new ExperienceButton(sketch, currY, currY + LNZ.hero_leftPanelBarHeight);
      currY += 2 * LNZ.hero_leftPanelBarHeight + LNZ.map_selectedObjectPanelGap;
      this.health = new HealthButton(sketch, currY, currY + 2 * LNZ.hero_leftPanelBarHeight);
      currY += 2 * LNZ.hero_leftPanelBarHeight + LNZ.map_selectedObjectPanelGap;
      this.mana = new ManaButton(sketch, currY, currY + 2 * LNZ.hero_leftPanelBarHeight);
      currY += 2 * LNZ.hero_leftPanelBarHeight + LNZ.map_selectedObjectPanelGap;
      this.hunger = new HungerButton(sketch, currY, currY + 2 * LNZ.hero_leftPanelBarHeight);
      currY += 2 * LNZ.hero_leftPanelBarHeight + LNZ.map_selectedObjectPanelGap;
      this.thirst = new ThirstButton(sketch, currY, currY + 2 * LNZ.hero_leftPanelBarHeight);
      currY += 2 * LNZ.hero_leftPanelBarHeight + 4 * LNZ.map_selectedObjectPanelGap;
      // Stats
      double stat_button_height = Math.max(0, 0.2 * (sketch.height - currY - 6 * LNZ.map_selectedObjectPanelGap));
      this.attack = new AttackButton(sketch, currY, currY + stat_button_height);
      this.magic = new MagicButton(sketch, currY, currY + stat_button_height);
      currY += stat_button_height + LNZ.map_selectedObjectPanelGap;
      this.defense = new DefenseButton(sketch, currY, currY + stat_button_height);
      this.resistance = new ResistanceButton(sketch, currY, currY + stat_button_height);
      currY += stat_button_height + LNZ.map_selectedObjectPanelGap;
      this.piercing = new PiercingButton(sketch, currY, currY + stat_button_height);
      this.penetration = new PenetrationButton(sketch, currY, currY + stat_button_height);
      currY += stat_button_height + LNZ.map_selectedObjectPanelGap;
      this.range = new RangeButton(sketch, currY, currY + stat_button_height);
      this.speed = new SpeedButton(sketch, currY, currY + stat_button_height);
      currY += stat_button_height + LNZ.map_selectedObjectPanelGap;
      this.tenacity = new TenacityButton(sketch, currY, currY + stat_button_height);
      this.agility = new AgilityButton(sketch, currY, currY + stat_button_height);
      currY += stat_button_height + LNZ.map_selectedObjectPanelGap;
    }

    void deposited(double money) {
      this.money.blinks = Math.max(this.money.blinks, Math.min((int)Math.ceil(money * 0.4), 5));
      this.money.blink_timer = 300;
      this.money.blinking = false;
    }

    void drawPanel(int millis, double panel_width) {
      double half_panel_width = 0.5 * panel_width;
      // name
      p.fill(255);
      p.textSize(LNZ.map_selectedObjectTitleTextSize);
      p.textAlign(PConstants.CENTER, PConstants.TOP);
      p.text(Hero.this.displayName(), half_panel_width, this.yi);
      // picture
      p.imageMode(PConstants.CORNER);
      p.image(Hero.this.getImage(), 1, this.image_yi, this.image_size, this.image_size);
      // money
      this.money.message = "Wallet: $" + Math.round(100.0 * Hero.this.money) / 100.0;
      this.money.setXLocation(half_panel_width + 2, panel_width - 2);
      this.money.update(millis);
      // level tokens
      this.level_tokens.message = "Tokens: " + Hero.this.level_tokens;
      this.level_tokens.setXLocation(half_panel_width + 2, panel_width - 2);
      this.level_tokens.update(millis);
      // level
      this.level.message = "Level " + Hero.this.level;
      this.level.setXLocation(2, panel_width - 2);
      this.level.update(millis);
      // experience
      this.experience.setXLocation(2, panel_width - 2);
      this.experience.update(millis);
      // health
      this.health.setXLocation(2, panel_width - 2);
      this.health.update(millis);
      // mana
      this.mana.setXLocation(2, panel_width - 2);
      this.mana.update(millis);
      // hunger
      this.hunger.setXLocation(2, panel_width - 2);
      this.hunger.update(millis);
      // thirst
      this.thirst.setXLocation(2, panel_width - 2);
      this.thirst.update(millis);
      // stats
      this.magic.setXLocation(half_panel_width + 2, panel_width - 2);
      this.magic.update(millis);
      this.attack.setXLocation(2, half_panel_width);
      this.attack.update(millis);
      this.resistance.setXLocation(half_panel_width + 2, panel_width - 2);
      this.resistance.update(millis);
      this.defense.setXLocation(2, 0.5 * panel_width);
      this.defense.update(millis);
      this.penetration.setXLocation(half_panel_width + 2, panel_width - 2);
      this.penetration.update(millis);
      this.piercing.setXLocation(2, 0.5 * panel_width);
      this.piercing.update(millis);
      this.speed.setXLocation(half_panel_width + 2, panel_width - 2);
      this.speed.update(millis);
      this.range.setXLocation(2, 0.5 * panel_width);
      this.range.update(millis);
      this.agility.setXLocation(half_panel_width + 2, panel_width - 2);
      this.agility.update(millis);
      this.tenacity.setXLocation(2, 0.5 * panel_width);
      this.tenacity.update(millis);
    }

    void mouseMove(float mX, float mY) {
      this.money.mouseMove(mX, mY);
      this.level_tokens.mouseMove(mX, mY);
      this.level.mouseMove(mX, mY);
      this.experience.mouseMove(mX, mY);
      this.health.mouseMove(mX, mY);
      this.mana.mouseMove(mX, mY);
      this.hunger.mouseMove(mX, mY);
      this.thirst.mouseMove(mX, mY);
      this.attack.mouseMove(mX, mY);
      this.magic.mouseMove(mX, mY);
      this.defense.mouseMove(mX, mY);
      this.resistance.mouseMove(mX, mY);
      this.piercing.mouseMove(mX, mY);
      this.penetration.mouseMove(mX, mY);
      this.speed.mouseMove(mX, mY);
      this.range.mouseMove(mX, mY);
      this.tenacity.mouseMove(mX, mY);
      this.agility.mouseMove(mX, mY);
    }

    void mousePress() {
      this.money.mousePress();
      this.level_tokens.mousePress();
      this.level.mousePress();
      this.experience.mousePress();
      this.health.mousePress();
      this.mana.mousePress();
      this.hunger.mousePress();
      this.thirst.mousePress();
      this.attack.mousePress();
      this.magic.mousePress();
      this.defense.mousePress();
      this.resistance.mousePress();
      this.piercing.mousePress();
      this.penetration.mousePress();
      this.speed.mousePress();
      this.range.mousePress();
      this.tenacity.mousePress();
      this.agility.mousePress();
    }

    void mouseRelease(float mX, float mY) {
      this.money.mouseRelease(mX, mY);
      this.level_tokens.mouseRelease(mX, mY);
      this.level.mouseRelease(mX, mY);
      this.experience.mouseRelease(mX, mY);
      this.health.mouseRelease(mX, mY);
      this.mana.mouseRelease(mX, mY);
      this.hunger.mouseRelease(mX, mY);
      this.thirst.mouseRelease(mX, mY);
      this.attack.mouseRelease(mX, mY);
      this.magic.mouseRelease(mX, mY);
      this.defense.mouseRelease(mX, mY);
      this.resistance.mouseRelease(mX, mY);
      this.piercing.mouseRelease(mX, mY);
      this.penetration.mouseRelease(mX, mY);
      this.speed.mouseRelease(mX, mY);
      this.range.mouseRelease(mX, mY);
      this.tenacity.mouseRelease(mX, mY);
      this.agility.mouseRelease(mX, mY);
    }
  }


  class XpLeftPanelMenu extends LeftPanelMenu {
    XpLeftPanelMenu(LNZ sketch) {
      super(sketch);
    }

    void deposited(double money) {}

    void drawPanel(int timeElapsed, double panel_width) {
    }

    void mouseMove(float mX, float mY) {
    }

    void mousePress() {
    }

    void mouseRelease(float mX, float mY) {
    }
  }



  class HeroTree {
    class HeroTreeButton extends RippleCircleButton {
      private LNZ p;
      protected HeroTreeCode code;
      protected ArrayList<HeroTreeCode> dependencies = new ArrayList<HeroTreeCode>();
      protected boolean in_view = false;
      protected boolean visible = false;
      protected boolean unlocked = false;

      HeroTreeButton(LNZ sketch, HeroTreeCode code, double xc, double yc, double r) {
        super(sketch, xc, yc, r);
        this.p = sketch;
        this.code = code;
        Element e = HeroTree.this.hero().element;
        this.setColors(Element.colorLocked(sketch.global, e),
          Element.colorLocked(sketch.global, e), Element.colorLocked(sketch.global, e),
          Element.colorDark(sketch.global, e), Element.colorText(sketch.global, e));
        this.setStroke(Element.colorDark(sketch.global, e), 6);
        this.setMessage(HeroTree.this.shortMessage(code));
        this.use_time_elapsed = true;
        this.text_size = 16;
        this.setDependencies();
        this.refreshColor();
      }

      void setDependencies() {
        switch(this.code) {
          case INVENTORYI:
            break;
          case PASSIVEI:
            this.dependencies.add(HeroTreeCode.INVENTORYI);
            break;
          case AI:
            this.dependencies.add(HeroTreeCode.INVENTORYI);
            break;
          case SI:
            this.dependencies.add(HeroTreeCode.INVENTORYI);
            break;
          case DI:
            this.dependencies.add(HeroTreeCode.INVENTORYI);
            break;
          case FI:
            this.dependencies.add(HeroTreeCode.PASSIVEI);
            this.dependencies.add(HeroTreeCode.AI);
            this.dependencies.add(HeroTreeCode.SI);
            this.dependencies.add(HeroTreeCode.DI);
            break;
          case PASSIVEII:
            this.dependencies.add(HeroTreeCode.FI);
            break;
          case AII:
            this.dependencies.add(HeroTreeCode.FI);
            break;
          case SII:
            this.dependencies.add(HeroTreeCode.FI);
            break;
          case DII:
            this.dependencies.add(HeroTreeCode.FI);
            break;
          case FII:
            this.dependencies.add(HeroTreeCode.PASSIVEII);
            this.dependencies.add(HeroTreeCode.AII);
            this.dependencies.add(HeroTreeCode.SII);
            this.dependencies.add(HeroTreeCode.DII);
            break;
          case HEALTHI:
            this.dependencies.add(HeroTreeCode.INVENTORYI);
            break;
          case ATTACKI:
            this.dependencies.add(HeroTreeCode.HEALTHI);
            break;
          case DEFENSEI:
            this.dependencies.add(HeroTreeCode.HEALTHI);
            break;
          case PIERCINGI:
            this.dependencies.add(HeroTreeCode.HEALTHI);
            break;
          case SPEEDI:
            this.dependencies.add(HeroTreeCode.HEALTHI);
            break;
          case SIGHTI:
            this.dependencies.add(HeroTreeCode.HEALTHI);
            break;
          case TENACITYI:
            this.dependencies.add(HeroTreeCode.HEALTHI);
            break;
          case AGILITYI:
            this.dependencies.add(HeroTreeCode.HEALTHI);
            break;
          case MAGICI:
            this.dependencies.add(HeroTreeCode.HEALTHI);
            break;
          case RESISTANCEI:
            this.dependencies.add(HeroTreeCode.HEALTHI);
            break;
          case PENETRATIONI:
            this.dependencies.add(HeroTreeCode.HEALTHI);
            break;
          case HEALTHII:
            this.dependencies.add(HeroTreeCode.HEALTHI);
            break;
          case ATTACKII:
            this.dependencies.add(HeroTreeCode.ATTACKI);
            break;
          case DEFENSEII:
            this.dependencies.add(HeroTreeCode.DEFENSEI);
            break;
          case PIERCINGII:
            this.dependencies.add(HeroTreeCode.PIERCINGI);
            break;
          case SPEEDII:
            this.dependencies.add(HeroTreeCode.SPEEDI);
            break;
          case SIGHTII:
            this.dependencies.add(HeroTreeCode.SIGHTI);
            break;
          case TENACITYII:
            this.dependencies.add(HeroTreeCode.TENACITYI);
            break;
          case AGILITYII:
            this.dependencies.add(HeroTreeCode.AGILITYI);
            break;
          case MAGICII:
            this.dependencies.add(HeroTreeCode.MAGICI);
            break;
          case RESISTANCEII:
            this.dependencies.add(HeroTreeCode.RESISTANCEI);
            break;
          case PENETRATIONII:
            this.dependencies.add(HeroTreeCode.PENETRATIONI);
            break;
          case HEALTHIII:
            this.dependencies.add(HeroTreeCode.HEALTHII);
            break;
          case OFFHAND:
            this.dependencies.add(HeroTreeCode.INVENTORYII);
            break;
          case BELTI:
            this.dependencies.add(HeroTreeCode.INVENTORYII);
            break;
          case BELTII:
            this.dependencies.add(HeroTreeCode.BELTI);
            break;
          case INVENTORYII:
            this.dependencies.add(HeroTreeCode.INVENTORYI);
            break;
          case INVENTORY_BARI:
            this.dependencies.add(HeroTreeCode.INVENTORYII);
            break;
          case INVENTORY_BARII:
            this.dependencies.add(HeroTreeCode.INVENTORY_BARI);
            break;
          case CRAFTI:
            this.dependencies.add(HeroTreeCode.INVENTORYI);
            break;
          case CRAFTII_ROW:
            this.dependencies.add(HeroTreeCode.CRAFTI);
            break;
          case CRAFTII_COL:
            this.dependencies.add(HeroTreeCode.CRAFTI);
            break;
          case CRAFTIII_ROW:
            this.dependencies.add(HeroTreeCode.CRAFTII_ROW);
            break;
          case CRAFTIII_COL:
            this.dependencies.add(HeroTreeCode.CRAFTII_COL);
            break;
          case FOLLOWERI:
            this.dependencies.add(HeroTreeCode.INVENTORYI);
            break;
          default:
            p.global.errorMessage("ERROR: HeroTreeCode " + this.code + " not recognized.");
            break;
        }
      }

      void visible() {
        this.visible = true;
        this.show_message = true;
        if (this.unlocked) {
          return;
        }
        Element e = HeroTree.this.hero().element;
        this.setColors(Element.colorLocked(p.global, e), Element.colorDark(p.global, e),
          Element.color(p.global, e), Element.colorLight(p.global, e), Element.colorText(p.global, e));
        this.setStroke(Element.colorLight(p.global, e), 9);
        this.refreshColor();
      }

      void unlock() {
        this.unlocked = true;
        this.show_message = true;
        Element e = HeroTree.this.hero().element;
        this.setColors(Element.colorLocked(p.global, e), Element.colorLight(p.global, e), Element.colorLight(p.global, e),
          Element.colorLight(p.global, e), Element.colorText(p.global, e));
        this.setStroke(Element.colorLight(p.global, e), 12);
        this.refreshColor();
      }

      @Override
      public int fillColor() {
        if (this.disabled) {
          return this.color_disabled;
        }
        else if (this.clicked && this.visible) {
          return this.color_click;
        }
        else if (this.hovered) {
          return this.color_hover;
        }
        else {
          return this.color_default;
        }
      }

      @Override
      public void drawButton() {
        super.drawButton();
        p.fill(LNZ.color_transparent);
        p.stroke(this.color_stroke);
        p.strokeWeight(this.stroke_weight);
        p.ellipseMode(PConstants.CORNERS);
        p.ellipse(this.xi, this.yi, this.xf, this.yf);
      }

      @Override
      public void hover() {
        super.hover();
        if (this.unlocked) {
          this.setMessage("Unlocked");
        }
        else {
          this.setMessage(HeroTree.this.longMessage(code));
        }
      }

      @Override
      public void dehover() {
        super.hover();
        this.setMessage(HeroTree.this.shortMessage(code));
      }

      @Override
      public void click() {
        if (this.unlocked || this.visible) {
          super.click();
        }
      }

      @Override
      public void release() {
        super.release();
        if (this.hovered) {
          if (this.unlocked || this.visible) {
            HeroTree.this.showDetails(this.code);
          }
        }
      }
    }


    class NodeDetailsForm extends Form {
      protected boolean canceled = false;
      protected HeroTreeButton button;
      protected double shadow_distance = 10;
      protected PImage img;

      NodeDetailsForm(LNZ sketch, HeroTreeButton button) {
        super(sketch,
          0.5 * (sketch.width - LNZ.hero_treeForm_width),
          0.5 * (sketch.height - LNZ.hero_treeForm_height),
          0.5 * (sketch.width + LNZ.hero_treeForm_width),
          0.5 * (sketch.height + LNZ.hero_treeForm_height));
        this.img = Images.getCurrImage(p);
        this.cancelButton();
        this.draggable = false;
        this.button = button;
        this.setTitleText(HeroTree.this.upgradeName(button.code));
        this.setTitleSize(20);
        this.setFieldCushion(0);
        Element e = HeroTree.this.hero().element;
        this.color_background = Element.colorLocked(sketch.global, e);
        this.color_header = Element.colorLight(sketch.global, e);
        this.color_stroke = Element.colorDark(sketch.global, e);
        this.color_title = Element.colorText(sketch.global, e);

        this.addField(new SpacerFormField(p, 20));
        TextBoxFormField textbox = new TextBoxFormField(p, HeroTree.this.upgradeDescription(button.code), 200);
        textbox.textbox.scrollbar.setButtonColors(Element.colorLocked(sketch.global, e), Element.color(sketch.global, e),
          Element.colorLight(sketch.global, e), Element.colorDark(sketch.global, e), Element.colorText(sketch.global, e));
        textbox.textbox.useElapsedTime();
        this.addField(textbox);
        this.addField(new SpacerFormField(p, 20));
        boolean has_enough = HeroTree.this.hero().level_tokens >= HeroTree.this.upgradeCost(button.code);
        SubmitCancelFormField buttons = new SubmitCancelFormField(p, HeroTree.this.hero().
          level_tokens + "/" + HeroTree.this.upgradeCost(button.code), "Cancel");
        buttons.button1.setColors(Element.colorLocked(sketch.global, e), Element.color(sketch.global, e),
          Element.colorLight(sketch.global, e), Element.colorDark(sketch.global, e), Element.colorText(sketch.global, e));
        buttons.button2.setColors(Element.colorLocked(sketch.global, e), Element.color(sketch.global, e),
          Element.colorLight(sketch.global, e), Element.colorDark(sketch.global, e), Element.colorText(sketch.global, e));
        if (has_enough && button.visible && !button.unlocked) {
        }
        else {
          buttons.button1.disabled = true;
          if (button.unlocked) {
            buttons.button1.message = "Unlocked";
          }
        }
        this.addField(buttons);
      }

      @Override
      public void update(int millis) {
        p.rectMode(PConstants.CORNERS);
        p.fill(0);
        p.imageMode(PConstants.CORNER);
        p.image(this.img, 0, 0);
        p.fill(0, 150);
        p.stroke(0, 1);
        p.translate(shadow_distance, shadow_distance);
        p.rect(this.xi, this.yi, this.xf, this.yf);
        p.translate(-shadow_distance, -shadow_distance);
        super.update(millis);
      }

      public void cancel() {
        this.canceled = true;
      }

      public void submit() {
        HeroTree.this.unlockNode(this.button.code);
        this.canceled = true;
      }

      public void buttonPress(int index) {}
    }


    class BackButton extends RectangleButton {
      BackButton(LNZ sketch) {
        super(sketch, 0, 0, 0, 0);
        this.setColors(DImg.ccolor(170), DImg.ccolor(1, 0),
          DImg.ccolor(40, 120), DImg.ccolor(20, 150), DImg.ccolor(255));
        this.noStroke();
        this.show_message = true;
        this.setMessage("Back");
        this.use_time_elapsed = true;
        this.text_size = 18;
        this.adjust_for_text_descent = true;
      }

      public void hover() {}
      public void dehover() {}
      public void click() {}
      public void release() {
        if (this.hovered) {
          HeroTree.this.curr_viewing = false;
        }
      }
    }

    private LNZ p;

    protected double xi = 0;
    protected double yi = 0;
    protected double xf = 0;
    protected double yf = 0;
    protected double xCenter;
    protected double yCenter;

    protected double tree_xi = 0;
    protected double tree_yi = 0;
    protected double tree_xf = 0;
    protected double tree_yf = 0;
    protected double translateX = 0;
    protected double translateY = 0;

    protected double viewX = 0;
    protected double viewY = 0;
    protected double zoom = 1.0;
    protected double inverse_zoom = 1.0;
    protected boolean curr_viewing = false;
    protected boolean set_screen_location = false;

    protected boolean dragging = false;
    protected double last_mX = 0;
    protected double last_mY = 0;
    protected boolean hovered = false;

    protected double lowestX = 0;
    protected double lowestY = 0;
    protected double highestX = 0;
    protected double highestY = 0;

    protected int color_background = DImg.ccolor(50);
    protected int color_connectorStroke_locked;
    protected int color_connectorStroke_visible;
    protected int color_connectorStroke_unlocked;
    protected int color_connectorFill_locked;
    protected int color_connectorFill_visible;
    protected int color_connectorFill_unlocked;

    protected HashMap<HeroTreeCode, HeroTreeButton> nodes = new HashMap<HeroTreeCode, HeroTreeButton>();
    protected NodeDetailsForm node_details = null;
    protected BackButton back_button;

    HeroTree(LNZ sketch) {
      this.p = sketch;
      this.xCenter = 0.5 * sketch.width;
      this.yCenter = 0.5 * sketch.height;
      this.back_button = new BackButton(sketch);
      this.color_connectorStroke_locked = Element.colorDark(sketch.global, Hero.this.element);
      this.color_connectorStroke_visible = Element.color(sketch.global, Hero.this.element);
      this.color_connectorStroke_unlocked = Element.colorLight(sketch.global, Hero.this.element);
      this.color_connectorFill_locked = Element.colorLocked(sketch.global, Hero.this.element);
      this.color_connectorFill_visible = Element.colorDark(sketch.global, Hero.this.element);
      this.color_connectorFill_unlocked = Element.color(sketch.global, Hero.this.element);
      this.initializeNodes();
      this.updateDependencies();
      this.setView(0, 0);
    }


    void showDetails(HeroTreeCode code) {
      if (!this.nodes.containsKey(code)) {
        return;
      }
      this.node_details = new NodeDetailsForm(p, this.nodes.get(code));
    }

    void unlockNode(HeroTreeCode code) {
      this.unlockNode(code, false, true);
    }
    void unlockNode(HeroTreeCode code, boolean force_unlock, boolean first_time) {
      if (!this.nodes.containsKey(code)) {
        return;
      }
      if (this.nodes.get(code).unlocked || (!this.nodes.get(code).visible && !force_unlock)) {
        return;
      }
      if (!force_unlock && Hero.this.level_tokens < this.upgradeCost(code)) {
        return;
      }
      if (!force_unlock) {
        Hero.this.level_tokens -= this.upgradeCost(code);
      }
      this.nodes.get(code).unlock();
      this.updateDependencies();
      Hero.this.upgrade(code);
      if (first_time) {
        p.global.sounds.trigger_player("player/unlock_node");
        switch(code) {
          case INVENTORYI:
            Hero.this.inventory.addSlots(LNZ.upgrade_inventoryI);
            break;
          case INVENTORYII:
            Hero.this.inventory.addSlots(LNZ.upgrade_inventoryII);
            break;
          case INVENTORY_BARI:
            Hero.this.inventory.addSlots(LNZ.upgrade_inventory_bar_slots);
            break;
          case INVENTORY_BARII:
            Hero.this.inventory.addSlots(LNZ.upgrade_inventory_bar_slots);
            break;
          case HEALTHI:
            Hero.this.curr_health += LNZ.upgrade_healthI;
            break;
          case HEALTHII:
            Hero.this.curr_health += LNZ.upgrade_healthII;
            break;
          case HEALTHIII:
            Hero.this.curr_health += LNZ.upgrade_healthIII;
            break;
          default:
            break;
        }
      }
    }

    ArrayList<HeroTreeCode> unlockedCodes() {
      ArrayList<HeroTreeCode> codes = new ArrayList<HeroTreeCode>();
      for (Map.Entry<HeroTreeCode, HeroTreeButton> entry : this.nodes.entrySet()) {
        if (entry.getValue().unlocked) {
          codes.add(entry.getKey());
        }
      }
      return codes;
    }

    void updateDependencies() {
      for (Map.Entry<HeroTreeCode, HeroTreeButton> entry : this.nodes.entrySet()) {
        if (entry.getValue().visible) {
          continue;
        }
        boolean visible = true;
        for (HeroTreeCode code : entry.getValue().dependencies) {
          if (!this.nodes.get(code).unlocked) {
            visible = false;
            break;
          }
        }
        if (visible) {
          entry.getValue().visible();
        }
      }
    }


    void setLocation(double xi, double yi, double xf, double yf) {
      this.xi = xi;
      this.yi = yi;
      this.xf = xf;
      this.yf = yf;
      this.setView(this.viewX, this.viewY);
      this.back_button.setLocation(xf - 120, yf - 70, xf - 30, yf - 30);
    }

    void moveView(double moveX, double moveY) {
      this.setView(this.viewX + moveX, this.viewY + moveY);
    }
    void setView(double viewX, double viewY) {
      if (viewX < this.lowestX) {
        viewX = this.lowestX;
      }
      else if (viewX > this.highestX) {
        viewX = this.highestX;
      }
      if (viewY < this.lowestY) {
        viewY = this.lowestY;
      }
      else if (viewY > this.highestY) {
        viewY = this.highestY;
      }
      this.viewX = viewX;
      this.viewY = viewY;
      this.tree_xi = viewX - this.inverse_zoom * (this.xCenter - this.xi);
      this.tree_yi = viewY - this.inverse_zoom * (this.yCenter - this.yi);
      this.tree_xf = viewX - this.inverse_zoom * (this.xCenter - this.xf);
      this.tree_yf = viewY - this.inverse_zoom * (this.yCenter - this.yf);
      this.translateX = this.xCenter - this.zoom * viewX;
      this.translateY = this.yCenter - this.zoom * viewY;
      for (Map.Entry<HeroTreeCode, HeroTreeButton> entry : this.nodes.entrySet()) {
        if (entry.getValue().xi > this.tree_xi && entry.getValue().yi > this.tree_yi &&
          entry.getValue().xf < this.tree_xf && entry.getValue().yf < this.tree_yf) {
          entry.getValue().in_view = true;
        }
        else {
          entry.getValue().in_view = false;
        }
      }
    }


    void update(int timeElapsed) {
      if (this.node_details != null) {
        this.node_details.update(timeElapsed);
        if (this.node_details.canceled) {
          this.node_details = null;
        }
        return;
      }
      p.rectMode(PConstants.CORNERS);
      p.fill(this.color_background);
      p.noStroke();
      p.rect(this.xi, this.yi, this.xf, this.yf);
      p.translate(this.translateX, this.translateY);
      p.scale(this.zoom, this.zoom);
      for (Map.Entry<HeroTreeCode, HeroTreeButton> entry : this.nodes.entrySet()) {
        p.rectMode(PConstants.CORNERS);
        p.translate(entry.getValue().xCenter(), entry.getValue().yCenter());
        for (HeroTreeCode dependency : entry.getValue().dependencies) {
          HeroTreeButton dependent = this.nodes.get(dependency);
          if (!entry.getValue().in_view && !dependent.in_view) {
            continue;
          }
          p.strokeWeight(2);
          double connector_width = 6;
          if (entry.getValue().unlocked) {
            p.fill(this.color_connectorFill_unlocked);
            p.stroke(this.color_connectorStroke_unlocked);
            p.strokeWeight(4);
            connector_width = 10;
          }
          else if (entry.getValue().visible || dependent.unlocked) {
            p.fill(this.color_connectorFill_visible);
            p.stroke(this.color_connectorStroke_visible);
            p.strokeWeight(3);
            connector_width = 8;
          }
          else {
            p.fill(this.color_connectorFill_locked);
            p.stroke(this.color_connectorStroke_locked);
          }
          double xDif = dependent.xCenter() - entry.getValue().xCenter();
          double yDif = dependent.yCenter() - entry.getValue().yCenter();
          double rotation = (float)Math.atan2(yDif, xDif);
          double distance = Math.sqrt(xDif * xDif + yDif * yDif);
          p.rotate(rotation);
          p.rect(0, -connector_width, distance, connector_width);
          p.rotate(-rotation);
        }
        p.translate(-entry.getValue().xCenter(), -entry.getValue().yCenter());
      }
      for (Map.Entry<HeroTreeCode, HeroTreeButton> entry : this.nodes.entrySet()) {
        if (entry.getValue().in_view) {
          entry.getValue().update(timeElapsed);
        }
      }
      p.scale(this.inverse_zoom, this.inverse_zoom);
      p.translate(-this.translateX, -this.translateY);
      this.back_button.update(timeElapsed);
      p.fill(255);
      p.textAlign(PConstants.CENTER, PConstants.TOP);
      p.textSize(30);
      p.text("Hero Tree", this.xCenter, this.yi + 5);
      p.text("Level Tokens: " + Hero.this.level_tokens, this.xCenter, p.textAscent() + p.textDescent() + 10);
    }

    void mouseMove(float mX, float mY) {
      if (this.node_details != null) {
        this.node_details.mouseMove(mX, mY);
        return;
      }
      this.back_button.mouseMove(mX, mY);
      if (this.dragging) {
        this.moveView(this.inverse_zoom * (this.last_mX - mX), this.inverse_zoom * (this.last_mY - mY));
      }
      this.last_mX = mX;
      this.last_mY = mY;
      if (mX > this.xi && mY > this.yi && mX < this.xf && mY < this.yf) {
        this.hovered = true;
      }
      else {
        this.hovered = false;
      }
      mX -= this.translateX;
      mY -= this.translateY;
      mX *= this.inverse_zoom;
      mY *= this.inverse_zoom;
      for (Map.Entry<HeroTreeCode, HeroTreeButton> entry : this.nodes.entrySet()) {
        if (entry.getValue().in_view) {
          entry.getValue().mouseMove(mX, mY);
        }
      }
    }

    void mousePress() {
      if (this.node_details != null) {
        this.node_details.mousePress();
        return;
      }
      this.back_button.mousePress();
      boolean button_hovered = false;
      if (this.back_button.hovered) {
        button_hovered = true;
      }
      for (Map.Entry<HeroTreeCode, HeroTreeButton> entry : this.nodes.entrySet()) {
        if (entry.getValue().in_view) {
          entry.getValue().mousePress();
          if (entry.getValue().hovered) {
            button_hovered = true;
          }
        }
      }
      if (!button_hovered && p.mouseButton == PConstants.LEFT && this.hovered) {
        this.dragging = true;
      }
    }

    void mouseRelease(float mX, float mY) {
      if (this.node_details != null) {
        this.node_details.mouseRelease(mX, mY);
        return;
      }
      this.back_button.mouseRelease(mX, mY);
      if (p.mouseButton == PConstants.LEFT) {
        this.dragging = false;
      }
      mX -= this.translateX;
      mY -= this.translateY;
      for (Map.Entry<HeroTreeCode, HeroTreeButton> entry : this.nodes.entrySet()) {
        if (entry.getValue().in_view) {
          entry.getValue().mouseRelease(mX, mY);
        }
      }
    }

    void scroll(int amount) {
      if (this.node_details != null) {
        this.node_details.scroll(amount);
        return;
      }
      this.zoom -= amount * 0.01;
      if (this.zoom < 0.5) {
        this.zoom = 0.5;
      }
      if (this.zoom > 1.5) {
        this.zoom = 1.5;
      }
      this.inverse_zoom = 1 / this.zoom;
      this.setView(this.viewX, this.viewY);
    }

    void keyPress(int key, int keyCode) {
      if (this.node_details != null) {
        this.node_details.keyPress(key, keyCode);
        return;
      }
      if (key == PConstants.ESC) {
        this.curr_viewing = false;
      }
    }

    void keyRelease(int key, int keyCode) {
      if (this.node_details != null) {
        this.node_details.keyRelease(key, keyCode);
        return;
      }
    }


    void initializeNodes() {
      for (HeroTreeCode code : HeroTreeCode.VALUES) {
        double xc = 0;
        double yc = 0;
        double r = LNZ.hero_treeButtonDefaultRadius;
        switch(code) {
          case INVENTORYI:
            r = LNZ.hero_treeButtonCenterRadius;
            break;
          case PASSIVEI:
            xc = 3 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            yc = -3.45 * LNZ.hero_treeButtonDefaultRadius;
            break;
          case AI:
            xc = 3 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            yc = -1.15 * LNZ.hero_treeButtonDefaultRadius;
            break;
          case SI:
            xc = 3 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            yc = 1.15 * LNZ.hero_treeButtonDefaultRadius;
            break;
          case DI:
            xc = 3 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            yc = 3.45 * LNZ.hero_treeButtonDefaultRadius;
            break;
          case FI:
            xc = 7 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            yc = 0;
            break;
          case PASSIVEII:
            xc = 11 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            yc = -3.45 * LNZ.hero_treeButtonDefaultRadius;
            break;
          case AII:
            xc = 11 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            yc = -1.15 * LNZ.hero_treeButtonDefaultRadius;
            break;
          case SII:
            xc = 11 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            yc = 1.15 * LNZ.hero_treeButtonDefaultRadius;
            break;
          case DII:
            xc = 11 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            yc = 3.45 * LNZ.hero_treeButtonDefaultRadius;
            break;
          case FII:
            xc = 15 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            yc = 0;
            break;
          case HEALTHI:
            xc = 0;
            yc = -3 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case ATTACKI:
            xc = 2.3 * LNZ.hero_treeButtonDefaultRadius;
            yc = -8 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case DEFENSEI:
            xc = 4.6 * LNZ.hero_treeButtonDefaultRadius;
            yc = -8 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case PIERCINGI:
            xc = 6.9 * LNZ.hero_treeButtonDefaultRadius;
            yc = -8 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case SPEEDI:
            xc = 9.2 * LNZ.hero_treeButtonDefaultRadius;
            yc = -8 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case SIGHTI:
            xc = 11.5 * LNZ.hero_treeButtonDefaultRadius;
            yc = -8 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case TENACITYI:
            xc = 13.8 * LNZ.hero_treeButtonDefaultRadius;
            yc = -8 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case AGILITYI:
            xc = 16.1 * LNZ.hero_treeButtonDefaultRadius;
            yc = -8 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case MAGICI:
            xc = -2.3 * LNZ.hero_treeButtonDefaultRadius;
            yc = -8 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case RESISTANCEI:
            xc = -4.6 * LNZ.hero_treeButtonDefaultRadius;
            yc = -8 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case PENETRATIONI:
            xc = -6.9 * LNZ.hero_treeButtonDefaultRadius;
            yc = -8 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case HEALTHII:
            xc = 0;
            yc = -8 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case ATTACKII:
            xc = 2.3 * LNZ.hero_treeButtonDefaultRadius;
            yc = -13 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case DEFENSEII:
            xc = 4.6 * LNZ.hero_treeButtonDefaultRadius;
            yc = -13 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case PIERCINGII:
            xc = 6.9 * LNZ.hero_treeButtonDefaultRadius;
            yc = -13 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case SPEEDII:
            xc = 9.2 * LNZ.hero_treeButtonDefaultRadius;
            yc = -13 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case SIGHTII:
            xc = 11.5 * LNZ.hero_treeButtonDefaultRadius;
            yc = -13 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case TENACITYII:
            xc = 13.8 * LNZ.hero_treeButtonDefaultRadius;
            yc = -13 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case AGILITYII:
            xc = 16.1 * LNZ.hero_treeButtonDefaultRadius;
            yc = -13 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case MAGICII:
            xc = -2.3 * LNZ.hero_treeButtonDefaultRadius;
            yc = -13 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case RESISTANCEII:
            xc = -4.6 * LNZ.hero_treeButtonDefaultRadius;
            yc = -13 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case PENETRATIONII:
            xc = -6.9 * LNZ.hero_treeButtonDefaultRadius;
            yc = -13 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case HEALTHIII:
            xc = 0;
            yc = -13 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            break;
          case OFFHAND:
            xc = 1.15 * LNZ.hero_treeButtonDefaultRadius;
            yc = 7 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            break;
          case BELTI:
            xc = 3.45 * LNZ.hero_treeButtonDefaultRadius;
            yc = 7 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            break;
          case BELTII:
            xc = 3.45 * LNZ.hero_treeButtonDefaultRadius;
            yc = 11 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            break;
          case INVENTORYII:
            xc = 0;
            yc = 3 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            break;
          case INVENTORY_BARI:
            xc = -1.15 * LNZ.hero_treeButtonDefaultRadius;
            yc = 7 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            break;
          case INVENTORY_BARII:
            xc = -1.15 * LNZ.hero_treeButtonDefaultRadius;
            yc = 11 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            break;
          case CRAFTI:
            xc = -3 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            yc = 3 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            break;
          case CRAFTII_ROW:
            xc = -3.45 * LNZ.hero_treeButtonDefaultRadius;
            yc = 7 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            break;
          case CRAFTII_COL:
            xc = -5.75 * LNZ.hero_treeButtonDefaultRadius;
            yc = 7 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            break;
          case CRAFTIII_ROW:
            xc = -3.45 * LNZ.hero_treeButtonDefaultRadius;
            yc = 11 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            break;
          case CRAFTIII_COL:
            xc = -5.75 * LNZ.hero_treeButtonDefaultRadius;
            yc = 11 * LNZ.hero_treeButtonDefaultRadius + LNZ.hero_treeButtonCenterRadius;
            break;
          case FOLLOWERI:
            xc = -3 * LNZ.hero_treeButtonDefaultRadius - LNZ.hero_treeButtonCenterRadius;
            yc = 0;
            break;
          default:
            p.global.errorMessage("ERROR: No place to put " + code + " in HeroTree.");
            break;
        }
        this.nodes.put(code, new HeroTreeButton(p, code, xc, yc, r));
        if (xc - r < this.lowestX) {
          this.lowestX = xc - r;
        }
        else if (xc + r > this.highestX) {
          this.highestX = xc + r;
        }
        if (yc - r < this.lowestY) {
          this.lowestY = yc - r;
        }
        else if (yc + r > this.highestY) {
          this.highestY = yc + r;
        }
      }
    }

    Hero hero() {
      return Hero.this;
    }

    String upgradeName(HeroTreeCode code) {
      switch(code) {
        case INVENTORYI:
          return "Unlock Inventory";
        case PASSIVEI:
          return (new Ability(p, Hero.this.abilityId(0))).displayName();
        case AI:
          return (new Ability(p, Hero.this.abilityId(1))).displayName();
        case SI:
          return (new Ability(p, Hero.this.abilityId(2))).displayName();
        case DI:
          return (new Ability(p, Hero.this.abilityId(3))).displayName();
        case FI:
          return (new Ability(p, Hero.this.abilityId(4))).displayName();
        case PASSIVEII:
          return (new Ability(p, Hero.this.upgradedAbilityId(0))).displayName();
        case AII:
          return (new Ability(p, Hero.this.upgradedAbilityId(1))).displayName();
        case SII:
          return (new Ability(p, Hero.this.upgradedAbilityId(2))).displayName();
        case DII:
          return (new Ability(p, Hero.this.upgradedAbilityId(3))).displayName();
        case FII:
          return (new Ability(p, Hero.this.upgradedAbilityId(4))).displayName();
        case HEALTHI:
          return "Increase Health";
        case ATTACKI:
          return "Increase Attack";
        case DEFENSEI:
          return "Increase Defense";
        case PIERCINGI:
          return "Increase Piercing";
        case SPEEDI:
          return "Increase Speed";
        case SIGHTI:
          return "Increase Sight";
        case TENACITYI:
          return "Increase Tenacity";
        case AGILITYI:
          return "Increase Agility";
        case MAGICI:
          return "Increase Magic";
        case RESISTANCEI:
          return "Increase Resistance";
        case PENETRATIONI:
          return "Increase Penetration";
        case HEALTHII:
          return "Increase Health II";
        case ATTACKII:
          return "Increase Attack II";
        case DEFENSEII:
          return "Increase Defense II";
        case PIERCINGII:
          return "Increase Piercing II";
        case SPEEDII:
          return "Increase Speed II";
        case SIGHTII:
          return "Increase Sight II";
        case TENACITYII:
          return "Increase Tenacity II";
        case AGILITYII:
          return "Increase Agility II";
        case MAGICII:
          return "Increase Magic II";
        case RESISTANCEII:
          return "Increase Resistance II";
        case PENETRATIONII:
          return "Increase Penetration II";
        case HEALTHIII:
          return "Increase Health III";
        case OFFHAND:
          return "Offhand Gear Slot";
        case BELTI:
          return "Belt Gear Slot";
        case BELTII:
          return "Belt Gear Slot II";
        case INVENTORYII:
          return "Inventory Slots";
        case INVENTORY_BARI:
          return "Inventory Bar";
        case INVENTORY_BARII:
          return "Inventory Bar II";
        case CRAFTI:
          return "Unlock Self-Crafting";
        case CRAFTII_ROW:
          return "Unlock Self-Crafting Second Row";
        case CRAFTII_COL:
          return "Unlock Self-Crafting Second Column";
        case CRAFTIII_ROW:
          return "Unlock Self-Crafting Third Row";
        case CRAFTIII_COL:
          return "Unlock Self-Crafting Third Column";
        case FOLLOWERI:
          return "Unlock Follower";
        default:
          return "-- Error --";
      }
    }

    String shortMessage(HeroTreeCode code) {
      switch(code) {
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
          return "Passive II";
        case AII:
          return "A II";
        case SII:
          return "S II";
        case DII:
          return "D II";
        case FII:
          return "F II";
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
          return "Health II";
        case ATTACKII:
          return "Attack II";
        case DEFENSEII:
          return "Defense II";
        case PIERCINGII:
          return "Piercing II";
        case SPEEDII:
          return "Speed II";
        case SIGHTII:
          return "Sight II";
        case TENACITYII:
          return "Tenacity II";
        case AGILITYII:
          return "Agility II";
        case MAGICII:
          return "Magic II";
        case RESISTANCEII:
          return "Resistance II";
        case PENETRATIONII:
          return "Penetration II";
        case HEALTHIII:
          return "Health III";
        case OFFHAND:
          return "Offhand";
        case BELTI:
          return "Belt";
        case BELTII:
          return "Belt II";
        case INVENTORYII:
          return "Inventory II";
        case INVENTORY_BARI:
          return "Inventory Bar";
        case INVENTORY_BARII:
          return "Inventory Bar II";
        case CRAFTI:
          return "Self-Crafting";
        case CRAFTII_ROW:
          return "2nd Row";
        case CRAFTII_COL:
          return "2nd Column";
        case CRAFTIII_ROW:
          return "3rd Row";
        case CRAFTIII_COL:
          return "3rd Column";
        case FOLLOWERI:
          return "Follower";
        default:
          return "-- Error --";
      }
    }

    String longMessage(HeroTreeCode code) {
      switch(code) {
        case INVENTORYI:
          return "Unlock\nInventory";
        case PASSIVEI:
          return "Unlock\nPassive\nAbility";
        case AI:
          return "Unlock\nA\nAbility";
        case SI:
          return "Unlock\nS\nAbility";
        case DI:
          return "Unlock\nD\nAbility";
        case FI:
          return "Unlock\nF\nAbility";
        case PASSIVEII:
          return "Upgrade\nPassive\nAbility";
        case AII:
          return "Upgrade\nA\nAbility";
        case SII:
          return "Upgrade\nS\nAbility";
        case DII:
          return "Upgrade\nD\nAbility";
        case FII:
          return "Upgrade\nF\nAbility";
        case HEALTHI:
          return "Increase\nHealth";
        case ATTACKI:
          return "Increase\nAttack";
        case DEFENSEI:
          return "Increase\nDefense";
        case PIERCINGI:
          return "Increase\nPiercing";
        case SPEEDI:
          return "Increase\nSpeed";
        case SIGHTI:
          return "Increase\nSight";
        case TENACITYI:
          return "Increase\nTenacity";
        case AGILITYI:
          return "Increase\nAgility";
        case MAGICI:
          return "Increase\nMagic";
        case RESISTANCEI:
          return "Increase\nResistance";
        case PENETRATIONI:
          return "Increase\nPenetration";
        case HEALTHII:
          return "Increase\nHealth";
        case ATTACKII:
          return "Increase\nAttack";
        case DEFENSEII:
          return "Increase\nDefense";
        case PIERCINGII:
          return "Increase\nPiercing";
        case SPEEDII:
          return "Increase\nSpeed";
        case SIGHTII:
          return "Increase\nSight";
        case TENACITYII:
          return "Increase\nTenacity";
        case AGILITYII:
          return "Increase\nAgility";
        case MAGICII:
          return "Increase\nMagic";
        case RESISTANCEII:
          return "Increase\nResistance";
        case PENETRATIONII:
          return "Increase\nPenetration";
        case HEALTHIII:
          return "Increase\nHealth";
        case OFFHAND:
          return "Unlock\nOffhand\nGear Slot";
        case BELTI:
          return "Unlock\nBelt\nGear Slot";
        case BELTII:
          return "Unlock\nBelt II\nGear Slot";
        case INVENTORYII:
          return "More\nInventory\nSlots";
        case INVENTORY_BARI:
          return "Unlock\nInventory Bar";
        case INVENTORY_BARII:
          return "Upgrade\nInventory Bar";
        case CRAFTI:
          return "Unlock\nSelf-Crafting";
        case CRAFTII_ROW:
          return "Unlock\n2nd Row\nSelf-Crafting";
        case CRAFTII_COL:
          return "Unlock\n2nd Column\nSelf-Crafting";
        case CRAFTIII_ROW:
          return "Unlock\n3rd Row\nSelf-Crafting";
        case CRAFTIII_COL:
          return "Unlock\n3rd Column\nSelf-Crafting";
        case FOLLOWERI:
          return "Unlock\nFollower";
        default:
          return "-- Error --";
      }
    }

    String upgradeDescription(HeroTreeCode code) {
      switch(code) {
        case INVENTORYI:
          return "Unlock your inventory and increase your available slots by " +
            LNZ.upgrade_inventoryI + ".";
        case PASSIVEI:
          return (new Ability(p, Hero.this.abilityId(0))).description();
        case AI:
          return (new Ability(p, Hero.this.abilityId(1))).description();
        case SI:
          return (new Ability(p, Hero.this.abilityId(2))).description();
        case DI:
          return (new Ability(p, Hero.this.abilityId(3))).description();
        case FI:
          return (new Ability(p, Hero.this.abilityId(4))).description();
        case PASSIVEII:
          return (new Ability(p, Hero.this.upgradedAbilityId(0))).description();
        case AII:
          return (new Ability(p, Hero.this.upgradedAbilityId(1))).description();
        case SII:
          return (new Ability(p, Hero.this.upgradedAbilityId(2))).description();
        case DII:
          return (new Ability(p, Hero.this.upgradedAbilityId(3))).description();
        case FII:
          return (new Ability(p, Hero.this.upgradedAbilityId(4))).description();
        case HEALTHI:
          return "Increase your base health by " + LNZ.upgrade_healthI + ".";
        case ATTACKI:
          return "Increase your base attack by " + LNZ.upgrade_attackI + ".";
        case DEFENSEI:
          return "Increase your base defense by " + LNZ.upgrade_defenseI + ".";
        case PIERCINGI:
          return "Increase your base piercing by " + (100.0 * LNZ.upgrade_piercingI) + "%.";
        case SPEEDI:
          return "Increase your base speed by " + LNZ.upgrade_speedI + ".";
        case SIGHTI:
          return "Increase your base sight by " + LNZ.upgrade_sightI + ".";
        case TENACITYI:
          return "Increase your base tenacity by " + (100.0 * LNZ.upgrade_tenacityI) + "%.";
        case AGILITYI:
          return "Increase your base agility by " + LNZ.upgrade_agilityI + ".";
        case MAGICI:
          return "Increase your base magic by " + LNZ.upgrade_magicI + ".";
        case RESISTANCEI:
          return "Increase your base resistance by " + LNZ.upgrade_resistanceI + ".";
        case PENETRATIONI:
          return "Increase your base penetration by " + (100.0 * LNZ.upgrade_penetrationI) + "%.";
        case HEALTHII:
          return "Increase your base health by " + LNZ.upgrade_healthII + ".";
        case ATTACKII:
          return "Increase your base attack by " + LNZ.upgrade_attackII + ".";
        case DEFENSEII:
          return "Increase your base defense by " + LNZ.upgrade_defenseII + ".";
        case PIERCINGII:
          return "Increase your base piercing by " + (100.0 * LNZ.upgrade_piercingII) + "%.";
        case SPEEDII:
          return "Increase your base speed by " + LNZ.upgrade_speedII + ".";
        case SIGHTII:
          return "Increase your base sight by " + LNZ.upgrade_sightII + ".";
        case TENACITYII:
          return "Increase your base tenacity by " + (100.0 * LNZ.upgrade_tenacityII) + "%.";
        case AGILITYII:
          return "Increase your base agility by " + LNZ.upgrade_agilityII + ".";
        case MAGICII:
          return "Increase your base magic by " + LNZ.upgrade_magicII + ".";
        case RESISTANCEII:
          return "Increase your base resistance by " + LNZ.upgrade_resistanceII + ".";
        case PENETRATIONII:
          return "Increase your base penetration by " + (100.0 * LNZ.upgrade_penetrationII) + "%.";
        case HEALTHIII:
          return "Increase your base health by " + LNZ.upgrade_healthIII + ".";
        case OFFHAND:
          return "Unlock the Offhand gear slot, allowing you to wield offhand items.";
        case BELTI:
          return "Unlock the first Belt gear slot, allowing you to wield belt items.";
        case BELTII:
          return "Unlock the second Belt gear slot, allowing you to wield two belt items.";
        case INVENTORYII:
          return "Increase your available inventory slots by " + LNZ.upgrade_inventoryII + ".";
        case INVENTORY_BARI:
          return "Unlock the inventory bar, which allows you to view and quickly " +
            "switch between active items using the number keys or by scrolling.";
        case INVENTORY_BARII:
          return "Upgrade the inventory bar, doubling its capacity and allowing " +
            "direct use of items in it without first switching to them.";
        case CRAFTI:
          return "Unlock the ability to craft items directly in your inventory.\n" +
            "This upgrade will unlock a 1x1 grid to craft items.\nNote to use " +
            "a tool in self-crafting you must have it in your inventory or hand.";
        case CRAFTII_ROW:
          return "Unlock the second row in your self-crafting grid.\nIf you " +
            "have already unlocked the second column this will give you a full " +
            "2x2 grid, but if you have not you will have a 2x1 grid.";
        case CRAFTII_COL:
          return "Unlock the second column in your self-crafting grid.\nIf you " +
            "have already unlocked the second row this will give you a full " +
            "2x2 grid, but if you have not you will have a 1x2 grid.";
        case CRAFTIII_ROW:
          return "Unlock the third row in your self-crafting grid.";
        case CRAFTIII_COL:
          return "Unlock the third column in your self-crafting grid.";
        case FOLLOWERI:
          return "Unlock your follower (will be released in future update).";
        default:
          return "-- Error --";
      }
    }

    int upgradeCost(HeroTreeCode code) {
      switch(code) {
        case INVENTORYI:
          return 1;
        case PASSIVEI:
          return 5;
        case AI:
          return 7;
        case SI:
          return 7;
        case DI:
          return 7;
        case FI:
          return 20;
        case PASSIVEII:
          return 100;
        case AII:
          return 100;
        case SII:
          return 100;
        case DII:
          return 100;
        case FII:
          return 300;
        case HEALTHI:
          return 2;
        case ATTACKI:
          return 3;
        case DEFENSEI:
          return 3;
        case PIERCINGI:
          return 3;
        case SPEEDI:
          return 3;
        case SIGHTI:
          return 3;
        case TENACITYI:
          return 3;
        case AGILITYI:
          return 3;
        case MAGICI:
          return 7;
        case RESISTANCEI:
          return 7;
        case PENETRATIONI:
          return 7;
        case HEALTHII:
          return 15;
        case ATTACKII:
          return 30;
        case DEFENSEII:
          return 30;
        case PIERCINGII:
          return 30;
        case SPEEDII:
          return 30;
        case SIGHTII:
          return 30;
        case TENACITYII:
          return 30;
        case AGILITYII:
          return 30;
        case MAGICII:
          return 35;
        case RESISTANCEII:
          return 35;
        case PENETRATIONII:
          return 35;
        case HEALTHIII:
          return 70;
        case OFFHAND:
          return 150;
        case BELTI:
          return 50;
        case BELTII:
          return 150;
        case INVENTORYII:
          return 12;
        case INVENTORY_BARI:
          return 40;
        case INVENTORY_BARII:
          return 75;
        case CRAFTI:
          return 5;
        case CRAFTII_ROW:
          return 20;
        case CRAFTII_COL:
          return 20;
        case CRAFTIII_ROW:
          return 85;
        case CRAFTIII_COL:
          return 85;
        case FOLLOWERI:
          return 25000;
        default:
          return 0;
      }
    }
  }

  private LNZ p;

  protected HeroCode code;
  protected Location location = Location.ERROR;

  protected int level_tokens = 0;
  protected double experience = 0;
  protected int experience_next_level = 1;
  protected double money = 0;
  protected double curr_mana = 0;
  protected int hunger = 100;
  protected int thirst = 100;
  protected int hunger_timer = LNZ.hero_hungerTimer;
  protected int thirst_timer = LNZ.hero_thirstTimer;

  protected LeftPanelMenu left_panel_menu;
  protected HeroInventory inventory;
  protected boolean can_view_inventory = false;
  protected InventoryBar inventory_bar;
  protected HeroTree hero_tree;

  protected Queue<String> messages = new LinkedList<String>();
  protected boolean in_control = true;
  protected int timer_magnetic_hands = LNZ.hero_timerMagneticHands;

  Hero(LNZ sketch, int ID) {
    super(sketch, ID);
    this.p = sketch;
    this.initializeSubclasses();
    this.code = HeroCode.heroCodeFromId(ID);
    this.addAbilities();
  }
  Hero(LNZ sketch, HeroCode code) {
    super(sketch, HeroCode.unit_id(code));
    this.p = sketch;
    this.initializeSubclasses();
    this.code = code;
    this.addAbilities();
  }

  private void initializeSubclasses() {
    this.left_panel_menu = new PlayerLeftPanelMenu(p);
    this.inventory = new HeroInventory(p);
    this.inventory_bar = new InventoryBar(p);
    this.hero_tree = new HeroTree(p);
  }


  boolean isUpgraded(HeroTreeCode code) {
    return this.hero_tree.nodes.get(code).unlocked;
  }

  void upgrade(HeroTreeCode code) {
    switch(code) {
      case INVENTORYI:
        this.can_view_inventory = true;
        break;
      case PASSIVEI:
        this.activateAbility(0);
        break;
      case AI:
        this.activateAbility(1);
        break;
      case SI:
        this.activateAbility(2);
        break;
      case DI:
        this.activateAbility(3);
        break;
      case FI:
        this.activateAbility(4);
        break;
      case PASSIVEII:
        this.upgradeAbility(0);
        break;
      case AII:
        this.upgradeAbility(1);
        break;
      case SII:
        this.upgradeAbility(2);
        break;
      case DII:
        this.upgradeAbility(3);
        break;
      case FII:
        this.upgradeAbility(4);
        break;
      case HEALTHI:
        this.base_health += LNZ.upgrade_healthI;
        break;
      case ATTACKI:
        this.base_attack += LNZ.upgrade_attackI;
        break;
      case DEFENSEI:
        this.base_defense += LNZ.upgrade_defenseI;
        break;
      case PIERCINGI:
        this.base_piercing += LNZ.upgrade_piercingI;
        break;
      case SPEEDI:
        this.base_speed += LNZ.upgrade_speedI;
        break;
      case SIGHTI:
        this.base_sight += LNZ.upgrade_sightI;
        break;
      case TENACITYI:
        this.base_tenacity += LNZ.upgrade_tenacityI;
        break;
      case AGILITYI:
        this.base_agility += LNZ.upgrade_agilityI;
        break;
      case MAGICI:
        this.base_magic += LNZ.upgrade_magicI;
        break;
      case RESISTANCEI:
        this.base_resistance += LNZ.upgrade_resistanceI;
        break;
      case PENETRATIONI:
        this.base_penetration += LNZ.upgrade_penetrationI;
        break;
      case HEALTHII:
        this.base_health += LNZ.upgrade_healthII;
        break;
      case ATTACKII:
        this.base_attack += LNZ.upgrade_attackII;
        break;
      case DEFENSEII:
        this.base_defense += LNZ.upgrade_defenseII;
        break;
      case PIERCINGII:
        this.base_piercing += LNZ.upgrade_piercingII;
        break;
      case SPEEDII:
        this.base_speed += LNZ.upgrade_speedII;
        break;
      case SIGHTII:
        this.base_sight += LNZ.upgrade_sightII;
        break;
      case TENACITYII:
        this.base_tenacity += LNZ.upgrade_tenacityII;
        break;
      case AGILITYII:
        this.base_agility += LNZ.upgrade_agilityII;
        break;
      case MAGICII:
        this.base_magic += LNZ.upgrade_magicII;
        break;
      case RESISTANCEII:
        this.base_resistance += LNZ.upgrade_resistanceII;
        break;
      case PENETRATIONII:
        this.base_penetration += LNZ.upgrade_penetrationII;
        break;
      case HEALTHIII:
        this.base_health += LNZ.upgrade_healthIII;
        break;
      case OFFHAND:
        this.gearSlots("Offhand");
        break;
      case BELTI:
        this.gearSlots("Belt (left)");
        break;
      case BELTII:
        this.gearSlots("Belt (right)");
        break;
      case INVENTORYII:
        break;
      case INVENTORY_BARI:
        this.inventory_bar.unlocked_inventory_bar1 = true;
        break;
      case INVENTORY_BARII:
        this.inventory_bar.unlocked_inventory_bar2 = true;
        break;
      case CRAFTI:
        this.inventory.crafting_inventory.slots.get(7).deactivated = false;
        break;
      case CRAFTII_ROW:
        this.inventory.crafting_inventory.slots.get(13).deactivated = false;
        if (this.isUpgraded(HeroTreeCode.CRAFTII_COL)) {
          this.inventory.crafting_inventory.slots.get(14).deactivated = false;
        }
        if (this.isUpgraded(HeroTreeCode.CRAFTIII_COL)) {
          this.inventory.crafting_inventory.slots.get(12).deactivated = false;
        }
        break;
      case CRAFTII_COL:
        this.inventory.crafting_inventory.slots.get(8).deactivated = false;
        if (this.isUpgraded(HeroTreeCode.CRAFTII_ROW)) {
          this.inventory.crafting_inventory.slots.get(14).deactivated = false;
        }
        if (this.isUpgraded(HeroTreeCode.CRAFTIII_ROW)) {
          this.inventory.crafting_inventory.slots.get(2).deactivated = false;
        }
        break;
      case CRAFTIII_ROW:
        this.inventory.crafting_inventory.slots.get(1).deactivated = false;
        if (this.isUpgraded(HeroTreeCode.CRAFTII_COL)) {
          this.inventory.crafting_inventory.slots.get(2).deactivated = false;
        }
        if (this.isUpgraded(HeroTreeCode.CRAFTIII_COL)) {
          this.inventory.crafting_inventory.slots.get(0).deactivated = false;
        }
        break;
      case CRAFTIII_COL:
        this.inventory.crafting_inventory.slots.get(6).deactivated = false;
        if (this.isUpgraded(HeroTreeCode.CRAFTII_ROW)) {
          this.inventory.crafting_inventory.slots.get(12).deactivated = false;
        }
        if (this.isUpgraded(HeroTreeCode.CRAFTIII_ROW)) {
          this.inventory.crafting_inventory.slots.get(0).deactivated = false;
        }
        break;
      case FOLLOWERI:
        // follower
        break;
      default:
        p.global.errorMessage("ERROR: Trying to upgrade but code " + code + " not found.");
        break;
    }
  }


  void openLeftPanelMenu(LeftPanelMenuPage menu) {
    switch(menu) {
      case PLAYER:
        this.left_panel_menu = new PlayerLeftPanelMenu(p);
        break;
      default:
        this.left_panel_menu = null;
        break;
    }
  }


  void addAbilities() {
    this.addAbilities(false);
  }
  void addAbilities(boolean powerful_version) {
    for (int i = 0; i < LNZ.hero_abilityNumber; i++) {
      this.abilities.add(null);
    }
  }

  int abilityId(int index) {
    return 2 * LNZ.hero_abilityNumber * (this.ID - 1101) + index + 101;
  }
  int upgradedAbilityId(int index) {
    return 2 * LNZ.hero_abilityNumber * (this.ID - 1101) + index + 101 + 5;
  }

  void activateAbility(int index) {
    if (index < 0 || index >= this.abilities.size()) {
      p.global.log("WARNING: Trying to activate ability index " + index + " but it doesn't exist.");
      return;
    }
    this.abilities.set(index, new Ability(p, this.abilityId(index)));
    this.inventory_bar.ability_buttons.get(index).setAbility(this.abilities.get(index));
  }

  void upgradeAbility(int index) {
    if (index < 0 || index >= this.abilities.size()) {
      p.global.log("WARNING: Trying to upgrade ability index " + index + " but it doesn't exist.");
      return;
    }
    Ability a = this.abilities.get(index);
    if (a == null) {
      p.global.log("WARNING: Trying to upgrade a null ability.");
      return;
    }
    if (a.ID % 10 > 5) {
      p.global.log("WARNING: Trying to upgrade a tier II ability.");
      return;
    }
    a = new Ability(p, this.upgradedAbilityId(index));
  }


  void refreshExperienceNextLevel() {
    if (this.level == LNZ.hero_maxLevel) {
      this.experience_next_level = 0;
      return;
    }
    this.experience_next_level = (int)Math.ceil(Math.pow(
      this.level * LNZ.hero_experienceNextLevel_level *
      (1 + (this.tier() - 1) * LNZ.hero_experienceNextLevel_tier),
      LNZ.hero_experienceNextLevel_power) + 1);
  }


  String manaDisplayName() {
    switch(this.code) {
      case BEN:
        return "% Rage";
      case DAN:
        return "Frog Energy";
      default:
        return "Error";
    }
  }

  String manaFileName() {
    switch(this.code) {
      case BEN:
        return "rage";
      case DAN:
        return "frog";
      default:
        return "error";
    }
  }


  @Override
  double currMana() {
    return this.curr_mana;
  }

  @Override
  double mana() {
    double mana = 0;
    switch(this.code) {
      case BEN:
        return 100;
      case DAN:
        mana = 80;
        break;
      default:
        break;
    }
    return mana;
  }


  int inventoryStartSlots() {
    return LNZ.hero_inventoryDefaultStartSlots;
  }


  void addExperience(double amount) {
    this.experience += amount * this.experienceMultiplier();
    if (this.level == LNZ.hero_maxLevel) {
      return;
    }
    while(this.experience > this.experience_next_level) {
      this.experience -= this.experience_next_level;
      this.levelup();
      if (this.level == LNZ.hero_maxLevel) {
        break;
      }
    }
  }

  double experienceMultiplier() {
    double multiplier = 1.0;
    if (p.global.profile.upgraded(PlayerTreeCode.XP_V)) {
      multiplier *= LNZ.profile_xpMultiplierV;
    }
    else if (p.global.profile.upgraded(PlayerTreeCode.XP_IV)) {
      multiplier *= LNZ.profile_xpMultiplierIV;
    }
    else if (p.global.profile.upgraded(PlayerTreeCode.XP_III)) {
      multiplier *= LNZ.profile_xpMultiplierIII;
    }
    else if (p.global.profile.upgraded(PlayerTreeCode.XP_II)) {
      multiplier *= LNZ.profile_xpMultiplierII;
    }
    else if (p.global.profile.upgraded(PlayerTreeCode.XP_I)) {
      multiplier *= LNZ.profile_xpMultiplierI;
    }
    return multiplier;
  }

  void levelup() {
    this.setLevel(this.level + 1);
    this.refreshExperienceNextLevel();
    this.level_tokens += this.level;
    p.global.sounds.trigger_player("player/levelup");
  }

  @Override
  void setLevel(int level) {
    if (level == this.level) {
      return;
    }
    double missing_health = this.health() - this.curr_health;
    super.setLevel(level);
    this.base_health = 4 + 0.5 * LNZ.hero_scaling_health * level * (level + 1.0);
    this.base_attack = 1 + 0.5 * LNZ.hero_scaling_attack * level * (level + 1.0);
    this.base_magic = 0.5 * LNZ.hero_scaling_magic * level * (level + 1.0);
    this.base_defense = 0.5 * LNZ.hero_scaling_defense * level * (level + 1.0);
    this.base_resistance = 0.5 * LNZ.hero_scaling_resistance * level * (level + 1.0);
    this.base_piercing = 0.5 * LNZ.hero_scaling_piercing * level * (level + 1.0);
    this.base_penetration = 0.5 * LNZ.hero_scaling_penetration * level * (level + 1.0);
    switch(this.ID) {
      case 1102: // Dan Gray
        this.base_magic += 2.5;
        break;
      default:
        break;
    }
    this.curr_health = Math.max(1, this.health() - missing_health);
  }


  @Override
  void pickup(Item i) {
    super.pickup(i);
    if (i != null && !i.remove && i.money() && p.global.profile.upgraded(PlayerTreeCode.MAGNETIC_WALLET)) {
      this.depositMoney(i);
    }
  }

  Item pickupOrStash(Item i) {
    if (i != null && !i.remove && i.money() && p.global.profile.upgraded(PlayerTreeCode.MAGNETIC_WALLET)) {
      this.depositMoney(i);
      return null;
    }
    if (this.canPickup()) {
      this.pickup(i);
      return null;
    }
    return this.inventory.superStash(i);
  }

  void depositMoney(Item i) {
    if (i == null || i.remove || !i.money()) {
      return;
    }
    double money_deposited = i.money * i.stack;
    this.money += money_deposited;
    i.remove = true;
    p.global.sounds.trigger_player("player/money");
    this.left_panel_menu.deposited(money_deposited);
  }


  @Override
  void killed(Unit u) {
    super.killed(u);
    this.addExperience((int)Math.ceil(1 + Math.pow(u.level, LNZ.hero_killExponent)));
  }


  boolean seesTime() {
    String interface_class = p.global.menu.getClass().getSimpleName();
    if (interface_class.equals("MapEditorInterface")) {
      return true;
    }
    return false;
  }

  void startUseItemTimer() {
    if (this.weapon() == null) {
      return;
    }
    if (this.curr_action == UnitAction.MOVING || this.curr_action == UnitAction.MOVING_AND_USING_ITEM) {
      this.curr_action = UnitAction.MOVING_AND_USING_ITEM;
    }
    else {
      this.curr_action = UnitAction.USING_ITEM;
    }
    this.timer_actionTime = this.weapon().useTime();
    switch(this.weapon().ID) {
      case 2002: // Wapato
      case 2004: // Leek
      case 2006: // Plantain Leaves
      case 2007: // Ryegrass Seeds
      case 2008: // Barnyard Grass Seeds
      case 2010: // Nettle Leaves
      case 2012: // Black Walnut
      case 2013: // Juniper Berries
      case 2014: // Acorn
      case 2101: // Crumb
      case 2102: // Unknown Food
      case 2103: // Unknown Food
      case 2104: // Unknown Food
      case 2105: // Unknown Food
      case 2106: // Pickle
      case 2107: // kKetchup
      case 2108: // Chicken Wing
      case 2109: // Steak
      case 2110: // Poptart
      case 2111: // Donut
      case 2112: // Chocolate
      case 2113: // Chips
      case 2114: // Cheese
      case 2115: // Peanuts
      case 2116: // Raw Chicken
      case 2117: // Cooked Chicken
      case 2118: // Chicken Egg
      case 2119: // Rotten Flesh
      case 2120: // Apple
      case 2121: // Banana
      case 2122: // Pear
      case 2123: // Bread
      case 2125: // Hot Pocket
      case 2126: // raw chicken wing
      case 2127: // quail egg
      case 2128: // raw quail
      case 2129: // cooked quail
      case 2130: // Raw Venison
      case 5101: // Cooked Venison
      case 2142: // Golden Apple
      case 2961: // Dandelion
        p.global.sounds.trigger_player("player/eat");
        break;
      case 2124: // hot pocket package
        p.global.sounds.trigger_player("player/open_package");
        break;
      case 2131: // water cup
      case 2132: // coke
      case 2133: // wine
      case 2134: // beer
      case 2141: // holy water
      case 2924: // Glass Bottle
      case 2925: // Water Bottle
      case 2926: // Canteen
      case 2927: // Water Jug
        p.global.sounds.trigger_player("player/drink");
        break;
      case 2301: // Slingshot
        p.global.sounds.trigger_player("items/slingshot_reload");
        break;
      case 2311: // Recurve Bow
        p.global.sounds.trigger_player("items/recurve_bow_reload");
        break;
      case 2312: // M1911
        p.global.sounds.trigger_player("items/m1911_reload");
        break;
      case 2321: // War Machine
        p.global.sounds.trigger_player("items/war_machine_reload");
        break;
      case 2322: // Five-Seven
      case 2343:
        p.global.sounds.trigger_player("items/five_seven_reload");
        break;
      case 2323: // Type25
      case 2344:
        p.global.sounds.trigger_player("items/type25_reload");
        break;
      case 2331: // Mustang and Sally
        p.global.sounds.trigger_player("items/mustang_and_sally_reload");
        break;
      case 2332: // FAL
      case 2352:
        p.global.sounds.trigger_player("items/FAL_reload");
        break;
      case 2333: // Python
      case 2354:
        p.global.sounds.trigger_player("items/python_reload");
      case 2341: // RPG
      case 2362:
        p.global.sounds.trigger_player("items/RPG_reload");
        break;
      case 2342: // Dystopic Demolisher
        p.global.sounds.trigger_player("items/dystopic_demolisher_reload");
        break;
      case 2345: // Executioner
      case 2364:
        p.global.sounds.trigger_player("items/executioner_reload");
        break;
      case 2351: // Galil
      case 2373:
        p.global.sounds.trigger_player("items/galil_reload");
        break;
      case 2353: // Ballistic Knife
      case 2374:
        p.global.sounds.trigger_player("items/ballistic_knife_reload");
        break;
      case 2355: // MTAR
      case 2375:
        p.global.sounds.trigger_player("items/MTAR_reload");
        break;
      case 2361: // RPD
      case 2381: // Relativistic Punishment Device
        p.global.sounds.trigger_player("items/RPD_reload");
        break;
      case 2363: // DSR-50
      case 2382:
        p.global.sounds.trigger_player("items/DSR50_reload");
        break;
      case 2371: // HAMR
      case 2391:
        p.global.sounds.trigger_player("items/HAMR_reload");
        break;
      case 2372: // Ray Gun
      case 2392: // Porter's X2 Ray Gun
        p.global.sounds.trigger_player("items/ray_gun_reload");
        break;
      case 2921: // Backpack
      case 2922: // Ben's Backpack
      case 2923: // Purse
        p.global.sounds.trigger_player("player/armor_cloth");
        break;
      default:
        break;
    }
  }

  @Override
  void useItem(AbstractGameMap map) {
    this.useItem(map, new InventoryKey(InventoryLocation.GEAR, 3));
  }
  void useItem(AbstractGameMap map, InventoryKey location) {
    Item i = this.inventory.getItem(location);
    if (i == null || !i.usable() || i.remove) {
      return;
    }
    if (i.consumable()) {
      this.heal((int)i.curr_health);
      this.healPercent(i.curr_health - (int)i.curr_health, true);
      this.increaseHunger(i.hunger);
      this.increaseThirst(i.thirst);
      this.money += i.money;
      switch(i.ID) {
        case 2116: // raw chicken
          if (Misc.randomChance(0.6)) {
            this.addStatusEffect(StatusEffectCode.SICK, 10000,
              new DamageSource(16, i.ID, StatusEffectCode.SICK));
          }
          break;
        case 2118: // chicken egg
          if (Misc.randomChance(0.3)) {
            this.addStatusEffect(StatusEffectCode.SICK, 8000,
              new DamageSource(16, i.ID, StatusEffectCode.SICK));
          }
          break;
        case 2119: // rotten flesh
          this.addStatusEffect(StatusEffectCode.SICK, 12000,
            new DamageSource(16, i.ID, StatusEffectCode.SICK));
          break;
        case 2126: // raw chicken wing
          if (Misc.randomChance(0.6)) {
            this.addStatusEffect(StatusEffectCode.SICK, 8000,
              new DamageSource(16, i.ID, StatusEffectCode.SICK));
          }
          break;
        case 2127: // quail egg
          if (Misc.randomChance(0.3)) {
            this.addStatusEffect(StatusEffectCode.SICK, 7000,
              new DamageSource(16, i.ID, StatusEffectCode.SICK));
          }
          break;
        case 2128: // raw quail
          if (Misc.randomChance(0.6)) {
            this.addStatusEffect(StatusEffectCode.SICK, 8000,
              new DamageSource(16, i.ID, StatusEffectCode.SICK));
          }
          break;
        case 2130: // Raw Venison
          if (Misc.randomChance(0.3)) {
            this.addStatusEffect(StatusEffectCode.SICK, 8000,
              new DamageSource(16, i.ID, StatusEffectCode.SICK));
          }
          break;
        case 2133: // wine
        case 2134: // beer
          this.addStatusEffect(StatusEffectCode.RELAXED, 9000,
            new DamageSource(16, i.ID, StatusEffectCode.SICK));
          this.addStatusEffect(StatusEffectCode.WOOZY, 9000,
            new DamageSource(16, i.ID, StatusEffectCode.SICK));
          break;
      }
      i.consumed();
      return;
    }
    if (i.reloadable()) {
      while(i.maximumAmmo() - i.availableAmmo() > 0) {
        ArrayList<Integer> possible_ammo = i.possibleAmmo();
        boolean noAmmo = true;
        for (int id : possible_ammo) {
          InventoryKey ammoLocation = this.inventory.itemLocation(id, InventoryLocation.INVENTORY);
          if (ammoLocation != null) {
            Item ammo = this.inventory.slots.get(ammoLocation.index).item;
            int ammoLoaded = Math.min(i.maximumAmmo() - i.availableAmmo(), ammo.stack);
            ammo.removeStack(ammoLoaded);
            i.ammo += ammoLoaded;
            noAmmo = false;
            break;
          }
        }
        if (noAmmo) {
          break;
        }
      }
      return;
    }
    if (i.money()) {
      this.depositMoney(i);
      return;
    }
    if (i.utility()) {
      switch(i.ID) {
        case 2904: // small key ring
        case 2905: // large key ring
          this.inventory.featureInventory(i.inventory);
          this.inventory.viewing = true;
          p.global.sounds.trigger_environment("items/keychain" + Misc.randomInt(1, 3),
            i.center().subtractR(map.view));
          break;
        case 2921: // backpack
          this.inventory.addSlots(2);
          i.remove = true;
          break;
        case 2922: // Ben's backpack
          this.inventory.addSlots(4);
          i.remove = true;
          break;
        case 2923: // purse
          this.inventory.addSlots(1);
          i.remove = true;
          break;
        case 2924: // water bottles
        case 2925:
        case 2926:
        case 2927:
          int thirst_quenched = Math.min(LNZ.hero_maxThirst - this.thirst, i.ammo);
          i.ammo -= thirst_quenched;
          this.increaseThirst(thirst_quenched);
          break;
        case 2929: // gas can
          break;
      }
      return;
    }
    p.global.log("WARNING: Trying to use item " + i.displayName() + " but no logic exists to use it.");
  }


  @Override
  void destroy(AbstractGameMap map) {
    super.destroy(map);
    this.inventory.clear();
  }

  @Override
  ArrayList<Item> drops() {
    ArrayList<Item> drops = super.drops();
    for (Item i : this.inventory.items()) {
      drops.add(i);
    }
    return drops;
  }


  void drawLeftPanel(int timeElapsed, double panel_width) {
    if (this.left_panel_menu != null) {
      this.left_panel_menu.drawPanel(timeElapsed, panel_width);
    }
  }


  void hungerTick() {
    this.decreaseHunger(1);
    this.hunger_timer = LNZ.hero_hungerTimer;
  }

  void thirstTick() {
    this.decreaseThirst(1);
    this.thirst_timer = LNZ.hero_thirstTimer;
  }

  void increaseHunger(int amount) {
    this.changeHunger(amount);
  }

  void decreaseHunger(int amount) {
    this.changeHunger(-amount);
  }

  void changeHunger(int amount) {
    this.setHunger(this.hunger + amount);
  }

  void setHunger(int amount) {
    this.hunger = amount;
    if (this.hunger > 100) {
      this.hunger = 100;
    }
    else if (this.hunger < 0) {
      this.hunger = 0;
    }
  }

  void increaseThirst(int amount) {
    this.changeThirst(amount);
  }

  void decreaseThirst(int amount) {
    this.changeThirst(-amount);
  }

  void changeThirst(int amount) {
    this.setThirst(this.thirst + amount);
  }

  void setThirst(int amount) {
    this.thirst = amount;
    if (this.thirst > 100) {
      this.thirst = 100;
    }
    else if (this.thirst < 0) {
      this.thirst = 0;
    }
  }

  @Override
  void increaseMana(double amount) {
    this.changeMana(amount);
  }

  @Override
  void decreaseMana(double amount) {
    this.changeMana(-amount);
  }

  void changeMana(double amount) {
    this.setMana(this.curr_mana + amount);
  }

  void setMana(double amount) {
    this.curr_mana = amount;
    if (this.curr_mana < 0) {
      this.curr_mana = 0;
    }
    else if (this.curr_mana > this.mana()) {
      this.curr_mana = this.mana();
    }
  }


  @Override
  void update(int time_elapsed, AbstractGameMap map) {
    super.update(time_elapsed, map);
    if (!this.ai_controlled && p.global.profile.upgraded(PlayerTreeCode.MAGNETIC_HANDS) && p.global.profile.options.magnetic_hands) {
      this.timer_magnetic_hands -= time_elapsed;
      if (this.timer_magnetic_hands < 0) {
        this.timer_magnetic_hands += LNZ.hero_timerMagneticHands;
        this.resolveMagneticHands(map);
      }
    }
  }

  void resolveMagneticHands(AbstractGameMap map) {
    for (Map.Entry<Integer, Item> entry : map.items.entrySet()) {
      Item i = entry.getValue();
      if (i == null || i.remove || i.recently_dropped > 0) {
        continue;
      }
      if (this.distance(i) > LNZ.hero_magneticHandsDistanceMultiplier * i.interactionDistance()) {
        continue;
      }
      Item i_copy = new Item(p, i);
      if (this.map_key == 0) {
        map.selected_object = i_copy;
      }
      Item j = this.pickupOrStash(i_copy);
      if (j == null || j.remove) {
        i.remove = true;
        i.pickupSound();
      }
      else {
        j.recently_dropped = 1200;
        map.items.put(entry.getKey(), j);
      }
    }
  }


  void update_hero(int time_elapsed) {
    this.hunger_timer -= time_elapsed;
    if (this.hunger_timer < 0) {
      this.hungerTick();
    }
    this.thirst_timer -= time_elapsed;
    if (this.thirst_timer < 0) {
      this.thirstTick();
    }
    this.inventory_bar.update(time_elapsed);
    if (this.inventory.viewing) {
      double inventoryTranslateX = 0.5 * (p.width - this.inventory.display_width);
      double inventoryTranslateY = 0.5 * (p.height - this.inventory.display_height);
      p.translate(inventoryTranslateX, inventoryTranslateY);
      this.inventory.update(time_elapsed);
      p.translate(-inventoryTranslateX, -inventoryTranslateY);
    }
    if (this.hunger < LNZ.hero_hungerThreshhold) {
      this.refreshStatusEffect(StatusEffectCode.HUNGRY, 3000);
    }
    if (this.thirst < LNZ.hero_thirstThreshhold) {
      this.refreshStatusEffect(StatusEffectCode.THIRSTY, 3000);
    }
  }

  void mouseMove_hero(float mX, float mY) {
    this.inventory_bar.mouseMove(mX, mY);
    if (this.inventory.viewing) {
      float inventoryTranslateX = (float)(0.5f * (p.width - this.inventory.display_width));
      float inventoryTranslateY = (float)(0.5f * (p.height - this.inventory.display_height));
      this.inventory.mouseMove(mX - inventoryTranslateX, mY - inventoryTranslateY);
    }
    if (this.left_panel_menu != null) {
      this.left_panel_menu.mouseMove(mX, mY);
    }
  }

  void mousePress_hero() {
    this.inventory_bar.mousePress();
    if (this.inventory.viewing) {
      this.inventory.mousePress();
    }
    if (this.left_panel_menu != null) {
      this.left_panel_menu.mousePress();
    }
  }

  void mouseRelease_hero(float mX, float mY) {
    this.inventory_bar.mouseRelease(mX, mY);
    if (this.inventory.viewing) {
      float inventoryTranslateX = (float)(0.5 * (p.width - this.inventory.display_width));
      float inventoryTranslateY = (float)(0.5 * (p.height - this.inventory.display_height));
      this.inventory.mouseRelease(mX - inventoryTranslateX, mY - inventoryTranslateY);
    }
    if (this.left_panel_menu != null) {
      this.left_panel_menu.mouseRelease(mX, mY);
    }
  }

  void scroll_hero(int amount) {
    this.inventory_bar.scroll(amount);
  }

  void keyPress_hero(int key, int keyCode) {
    if (key == PConstants.CODED) {
      switch(keyCode) {
        default:
          break;
      }
    }
    else {
      switch(key) {
        case '1':
          this.inventory_bar.setEquippedIndex(0);
          break;
        case '2':
          this.inventory_bar.setEquippedIndex(1);
          break;
        case '3':
          this.inventory_bar.setEquippedIndex(2);
          break;
        case '4':
          this.inventory_bar.setEquippedIndex(3);
          break;
        case '5':
          this.inventory_bar.setEquippedIndex(4);
          break;
        case '6':
          this.inventory_bar.setEquippedIndex(5);
          break;
        case '7':
          this.inventory_bar.setEquippedIndex(6);
          break;
        case '8':
          this.inventory_bar.setEquippedIndex(7);
          break;
        case '9':
          this.inventory_bar.setEquippedIndex(8);
          break;
        case '0':
          this.inventory_bar.setEquippedIndex(9);
          break;
        case 'w':
        case 'W':
          if (this.weapon() != null) {
            this.pickup(this.inventory.stash(this.weapon()));
          }
          break;
        case 'e':
        case 'E':
          this.inventory.viewing = !this.inventory.viewing;
          if (!this.inventory.viewing) {
            if (EnderChestInventory.class.isInstance(this.inventory.feature_inventory)) {
              p.global.notViewingEnderChest();
            }
            this.inventory.feature_inventory = null;
            this.inventory.dropItemHolding();
            this.inventory.clearCraftingInventory();
          }
          break;
        case 'r':
        case 'R':
          if (!this.in_control || this.curr_action == UnitAction.USING_ITEM ||
            this.curr_action == UnitAction.MOVING_AND_USING_ITEM) {
            break;
          }
          if (this.weapon() != null && this.weapon().usable()) {
            if (this.weapon().reloadable()) {
              ArrayList<Integer> possible_ammo = this.weapon().possibleAmmo();
              boolean noAmmo = true;
              for (int id : possible_ammo) {
                if (this.inventory.itemLocation(id, InventoryLocation.INVENTORY) != null) {
                  noAmmo = false;
                  break;
                }
              }
              if (noAmmo) {
                this.messages.add("Out of ammo");
                break;
              }
            }
            this.startUseItemTimer();
          }
          break;
        default:
          break;
      }
    }
  }

  void keyRelease_hero(int key, int keyCode) {
    if (key == PConstants.CODED) {
      switch(keyCode) {
        default:
          break;
      }
    }
    else {
      switch(key) {
        default:
          break;
      }
    }
  }


  @Override
  String fileString() {
    String fileString = "\nnew: Hero: " + this.ID;
    fileString += super.fileString(false);
    for (Ability a : this.abilities) {
      if (a == null) {
        fileString += "\naddNullAbility:";
      }
      else {
        fileString += a.fileString();
      }
    }
    fileString += "\nlevel_location: " + this.location.fileName();
    fileString += "\nlevel_tokens: " + this.level_tokens;
    fileString += "\nexperience: " + this.experience;
    fileString += "\nexperience_next_level: " + this.experience_next_level;
    fileString += "\nmoney: " + this.money;
    fileString += "\ncurr_mana: " + this.curr_mana;
    fileString += "\nhunger: " + this.hunger;
    fileString += "\nthirst: " + this.thirst;
    fileString += "\nhunger_timer: " + this.hunger_timer;
    fileString += "\nthirst_timer: " + this.thirst_timer;
    fileString += this.inventory.fileString();
    for (HeroTreeCode code : this.hero_tree.unlockedCodes()) {
      fileString += "\nperk: " + code.fileName();
    }
    fileString += "\nend: Hero\n";
    return fileString;
  }


  @Override
  void addData(String datakey, String data) {
    switch(datakey) {
      case "level_location":
        this.location = Location.location(data);
        break;
      case "perk":
        HeroTreeCode tree_code = HeroTreeCode.code(data);
        if (tree_code != null) {
          this.hero_tree.unlockNode(tree_code, true, false);
        }
        break;
      case "level_tokens":
        this.level_tokens = Misc.toInt(data);
        break;
      case "experience":
        this.experience = Misc.toDouble(data);
        break;
      case "experience_next_level":
        this.experience_next_level = Misc.toInt(data);
        break;
      case "money":
        this.money = Misc.toDouble(data);
        break;
      case "curr_mana":
        this.curr_mana = Misc.toDouble(data);
        break;
      case "hunger":
        this.hunger = Misc.toInt(data);
        break;
      case "thirst":
        this.thirst = Misc.toInt(data);
        break;
      case "hunger_timer":
        this.hunger_timer = Misc.toInt(data);
        break;
      case "thirst_timer":
        this.thirst_timer = Misc.toInt(data);
        break;
      default:
        super.addData(datakey, data);
        break;
    }
  }

  static Hero readHeroFile(LNZ p, String filePath) {
    Hero hero = null;
    String[] lines = p.loadStrings(filePath);
    if (lines == null) {
      p.global.errorMessage("ERROR: Reading hero at path " + filePath + " but no hero file exists.");
      return null;
    }
    Stack<ReadFileObject> object_queue = new Stack<ReadFileObject>();
    StatusEffectCode curr_status_code = StatusEffectCode.ERROR;
    StatusEffect curr_status = null;
    Ability curr_ability = null;
    Item curr_item = null;
    boolean ended_hero = false;
    for (String line : lines) {
      String[] parameters = PApplet.split(line, ':');
      if (parameters.length < 2) {
        continue;
      }
      String dataname = PApplet.trim(parameters[0]);
      String data = PApplet.trim(parameters[1]);
      for (int i = 2; i < parameters.length; i++) {
        data += ":" + parameters[i];
      }
      if (dataname.equals("new")) {
        ReadFileObject type = ReadFileObject.objectType(PApplet.trim(parameters[1]));
        switch(type) {
          case HERO:
            if (parameters.length < 3) {
              p.global.errorMessage("ERROR: Unit ID missing in Hero constructor.");
              break;
            }
            object_queue.push(type);
            hero = new Hero(p, Misc.toInt(PApplet.trim(parameters[2])));
            hero.abilities.clear();
            break;
          case INVENTORY:
            if (hero == null) {
              p.global.errorMessage("ERROR: Trying to start an inventory in a null hero.");
            }
            object_queue.push(type);
            break;
          case ITEM:
            if (hero == null) {
              p.global.errorMessage("ERROR: Trying to start an item in a null hero.");
            }
            if (parameters.length < 3) {
              p.global.errorMessage("ERROR: Item ID missing in Item constructor.");
              break;
            }
            object_queue.push(type);
            curr_item = new Item(p, Misc.toInt(PApplet.trim(parameters[2])));
            break;
          case STATUS_EFFECT:
            if (hero == null) {
              p.global.errorMessage("ERROR: Trying to start a status effect in a null hero.");
            }
            object_queue.push(type);
            curr_status = new StatusEffect(p);
            break;
          case ABILITY:
            if (hero == null) {
              p.global.errorMessage("ERROR: Trying to start an ability in a null hero.");
            }
            if (parameters.length < 3) {
              p.global.errorMessage("ERROR: Ability ID missing in Projectile constructor.");
              break;
            }
            object_queue.push(type);
            curr_ability = new Ability(p, Misc.toInt(PApplet.trim(parameters[2])));
            break;
          default:
            p.global.errorMessage("ERROR: Can't add a " + type + " type to Heroes data.");
            break;
        }
      }
      else if (dataname.equals("end")) {
        ReadFileObject type = ReadFileObject.objectType(PApplet.trim(parameters[1]));
        if (object_queue.empty()) {
          p.global.errorMessage("ERROR: Tring to end a " + type.name + " object but not inside any object.");
        }
        else if (type.name.equals(object_queue.peek().name)) {
          switch(object_queue.pop()) {
            case HERO:
              if (hero == null) {
                p.global.errorMessage("ERROR: Trying to end a null hero.");
                break;
              }
              if (!object_queue.empty()) {
                p.global.errorMessage("ERROR: Trying to end a hero but inside another object.");
                break;
              }
              if (hero.code == HeroCode.ERROR) {
                p.global.errorMessage("ERROR: Trying to end hero with errored code.");
                break;
              }
              ended_hero = true;
              break;
            case INVENTORY:
              if (hero == null) {
                p.global.errorMessage("ERROR: Trying to end an inventory in a null hero.");
                break;
              }
              break;
            case ITEM:
              if (curr_item == null) {
                p.global.errorMessage("ERROR: Trying to end a null item.");
                break;
              }
              if (object_queue.empty()) {
                p.global.errorMessage("ERROR: Trying to end an item not inside any other object.");
                break;
              }
              switch(object_queue.peek()) {
                case HERO:
                  if (parameters.length < 3) {
                    p.global.errorMessage("ERROR: GearSlot code missing in Item constructor.");
                    break;
                  }
                  GearSlot code = GearSlot.gearSlot(PApplet.trim(parameters[2]));
                  if (hero == null) {
                    p.global.errorMessage("ERROR: Trying to add gear to null hero.");
                    break;
                  }
                  hero.gear.put(code, curr_item);
                  break;
                case INVENTORY:
                  if (parameters.length < 3) {
                    p.global.errorMessage("ERROR: No positional information for inventory item.");
                    break;
                  }
                  int index = Misc.toInt(PApplet.trim(parameters[2]));
                  if (hero == null) {
                    p.global.errorMessage("ERROR: Trying to add inventory item to null hero.");
                    break;
                  }
                  Item i = hero.inventory.placeAt(curr_item, index, true);
                  if (i != null) {
                    p.global.errorMessage("ERROR: Item already exists at position " + index + ".");
                    break;
                  }
                  break;
                default:
                  p.global.errorMessage("ERROR: Trying to end an item inside a " + object_queue.peek().name + ".");
                  break;
              }
              curr_item = null;
              break;
            case STATUS_EFFECT:
              if (curr_status == null) {
                p.global.errorMessage("ERROR: Trying to end a null status effect.");
                break;
              }
              if (object_queue.empty()) {
                p.global.errorMessage("ERROR: Trying to end a status effect not inside any other object.");
                break;
              }
              if (object_queue.peek() != ReadFileObject.HERO) {
                p.global.errorMessage("ERROR: Trying to end a status effect not inside a hero.");
                break;
              }
              if (hero == null) {
                p.global.errorMessage("ERROR: Trying to end a status effect inside a null hero.");
                break;
              }
              hero.statuses.put(curr_status_code, curr_status);
              curr_status = null;
              break;
            case ABILITY:
              if (curr_ability == null) {
                p.global.errorMessage("ERROR: Trying to end a null ability.");
                break;
              }
              if (object_queue.empty()) {
                p.global.errorMessage("ERROR: Trying to end an ability not inside any other object.");
                break;
              }
              if (object_queue.peek() != ReadFileObject.HERO) {
                p.global.errorMessage("ERROR: Trying to end an ability inside a " + object_queue.peek().name + ".");
                break;
              }
              if (hero == null) {
                p.global.errorMessage("ERROR: Trying to end an ability inside a null hero.");
                break;
              }
              hero.abilities.add(curr_ability);
              curr_ability = null;
              break;
            default:
              p.global.errorMessage("ERROR: Invalid ReadFileType for Hero.");
              break;
          }
        }
        else {
          p.global.errorMessage("ERROR: Tring to end a " + type.name + " object but current object is a " + object_queue.peek().name + ".");
        }
      }
      else {
        switch(object_queue.peek()) {
          case HERO:
            if (hero == null) {
              p.global.errorMessage("ERROR: Trying to add hero data to a null hero.");
              break;
            }
            if (dataname.equals("next_status_code")) {
              curr_status_code = StatusEffectCode.code(data);
            }
            else {
              hero.addData(dataname, data);
            }
            break;
          case INVENTORY:
            if (hero == null) {
              p.global.errorMessage("ERROR: Trying to add hero inventory data to a null hero.");
              break;
            }
            hero.inventory.addData(dataname, data);
            break;
          case ITEM:
            if (curr_item == null) {
              p.global.errorMessage("ERROR: Trying to add item data to a null item.");
              break;
            }
            curr_item.addData(dataname, data);
            break;
          case STATUS_EFFECT:
            if (curr_status == null) {
              p.global.errorMessage("ERROR: Trying to add status effect data to a null status effect.");
              break;
            }
            curr_status.addData(dataname, data);
            break;
          case ABILITY:
            if (curr_ability == null) {
              p.global.errorMessage("ERROR: Trying to add ability data to a null ability.");
              break;
            }
            curr_ability.addData(dataname, data);
            break;
          default:
            break;
        }
      }
    }
    if (!ended_hero) {
      p.global.errorMessage("ERROR: Hero data never ended.");
      return null;
    }
    return hero;
  }
}