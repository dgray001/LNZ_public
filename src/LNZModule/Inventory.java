package LNZModule;

import java.util.*;
import processing.core.*;
import Button.*;
import DImg.DImg;

class Inventory {

  class InventorySlot {

    class InventoryButton extends RectangleButton {
      InventoryButton(LNZ sketch) {
        this(sketch, 0);
      }
      InventoryButton(LNZ sketch, double button_size) {
        super(sketch, 0, 0, 0, 0);
        this.setColors(DImg.ccolor(0, 120), DImg.ccolor(1, 0),
          DImg.ccolor(220, 70), DImg.ccolor(220, 140), DImg.ccolor(0));
        this.setStroke(DImg.ccolor(142, 75, 50), 3);
        this.setSize(button_size);
        this.roundness = 0;
        this.use_time_elapsed = true;
        this.force_left_button = false;
      }

      void setSize(double size) {
        this.setLocation(0, 0, size, size);
      }

      public void hover() {}
      public void dehover() {}
      public void click() {
        if (InventorySlot.this.item == null) {
          this.clicked = false;
        }
      }
      public void release() {}
    }

    private LNZ p;

    protected InventoryButton button;
    protected Item item = null;
    protected boolean deactivated = false;
    protected double last_mX = 0;
    protected double last_mY = 0;

    InventorySlot(LNZ sketch) {
      this(sketch, 0);
    }
    InventorySlot(LNZ sketch, double button_size) {
      this.p = sketch;
      this.button = new InventoryButton(sketch, button_size);
    }

    void removeStack() {
      if (this.item != null) {
        this.item.removeStack();
      }
    }

    double width() {
      return this.button.buttonWidth();
    }
    void setWidth(double new_width) {
      this.button.xf = new_width;
      this.button.yf = new_width;
    }

    void update(int timeElapsed) {
      this.update(timeElapsed, true);
    }
    void update(int timeElapsed, boolean show_slot_hovered_message) {
      if (this.deactivated) {
        return;
      }
      if (this.item != null) {
        p.image(this.item.getImage(), this.button.xi, this.button.yi, this.button.xf, this.button.yf);
        if (this.item.stack > 1) {
          p.fill(255);
          p.textSize(14);
          p.textAlign(PConstants.RIGHT, PConstants.BOTTOM);
          p.text(Integer.toString(this.item.stack), this.button.xf - 2, this.button.yf - 2);
        }
      }
      this.button.update(timeElapsed);
      if (this.item != null && this.item.remove) {
        this.item = null;
      }
      if (this.button.hovered && this.item != null && show_slot_hovered_message) {
        p.textSize(20);
        float rect_height = p.textAscent() + p.textDescent() + 2;
        float rect_width = p.textWidth(this.item.displayName()) + 2;
        p.rectMode(PConstants.CORNER);
        p.fill(p.global.color_nameDisplayed_background);
        p.stroke(1, 0);
        p.strokeWeight(0.1);
        p.rect(this.last_mX - rect_width - 1, this.last_mY - rect_height - 1, rect_width, rect_height);
        p.fill(255);
        p.textAlign(PConstants.LEFT, PConstants.TOP);
        p.text(this.item.displayName(), this.last_mX - rect_width - 1, this.last_mY - rect_height - 1);
      }
    }

    void mouseMove(float mX, float mY) {
      this.last_mX = mX;
      this.last_mY = mY;
      if (this.deactivated) {
        return;
      }
      this.button.mouseMove(mX, mY);
    }

    void mousePress() {
      if (this.deactivated) {
        return;
      }
      this.button.mousePress();
    }

    void mouseRelease(float mX, float mY) {
      if (this.deactivated) {
        return;
      }
      this.button.mouseRelease(mX, mY);
    }
  }

  protected LNZ p;

  protected int max_rows = 0;
  protected int max_cols = 0;
  protected ArrayList<InventorySlot> slots = new ArrayList<InventorySlot>();
  protected double button_size = 0;

  protected int color_background;

  protected double display_width = 0;
  protected double display_height = 0;

  protected boolean hovered = false;

  Inventory(LNZ sketch, int rows, int cols) {
    this(sketch, rows, cols, true);
  }
  Inventory(LNZ sketch, int rows, int cols, boolean fillup) {
    this.p = sketch;
    this.color_background = p.global.color_inventoryBackground;
    this.max_rows = rows;
    this.max_cols = cols;
    if (fillup) {
      this.fillMaxCapacity();
    }
  }

  void clear() {
    for (InventorySlot slot : this.slots) {
      slot.item = null;
    }
  }

  int maxCapacity() {
    return this.max_rows * this.max_cols;
  }

  void addSlot() {
    if (this.slots.size() < this.maxCapacity()) {
      this.slots.add(new InventorySlot(p, this.button_size));
    }
  }

  void addSlots(int amount) {
    int slotsLeft = this.maxCapacity() - this.slots.size();
    for (int i = 0; i < Math.min(amount, slotsLeft); i++) {
      this.slots.add(new InventorySlot(p, this.button_size));
    }
  }

  void setSlots(int amount) {
    if (amount < 0) {
      return;
    }
    if (amount > this.maxCapacity()) {
      amount = this.maxCapacity();
    }
    if (amount < this.slots.size()) {
      // remove slots
      p.global.errorMessage("ERROR: Can't reduce slots in inventory yet.");
    }
    else {
      this.addSlots(amount - this.slots.size());
    }
  }

  void deactivateSlots() {
    for (InventorySlot slot : this.slots) {
      slot.deactivated = true;
    }
  }

  void activateSlots() {
    for (InventorySlot slot : this.slots) {
      slot.deactivated = false;
    }
  }

  void fillMaxCapacity() {
    int currSize = this.slots.size();
    int maxSize = this.maxCapacity();
    for (int i = currSize; i < maxSize; i++) {
      this.slots.add(new InventorySlot(p, this.button_size));
    }
  }

  void setButtonSize(double button_size) {
    this.button_size = button_size;
    for (InventorySlot slot : this.slots) {
      slot.button.setSize(button_size);
    }
    this.refreshDisplayParameters();
  }

  void refreshDisplayParameters() {
    this.display_width = this.button_size * this.max_cols + 4;
    this.display_height = this.button_size * this.max_rows + 4;
  }


  // stashes in open drawers for specialized inventories
  void stashInDrawers(Item i) {
    this.stashInDrawers(i, false);
  }
  void stashInDrawers(Item i, boolean force) {
    this.stash(i, force);
  }

  void stash(Item i, int ... slots) {
    for (int slot : slots) {
      if (i == null) {
        break;
      }
      i = this.placeAt(i, slot, false, true);
    }
  }

  Item stash(Item i) {
    return this.stash(i, false);
  }
  Item stash(Item i, boolean force) {
    if (i == null || i.remove) {
      return null;
    }
    for (InventorySlot slot : this.slots) {
      if (!force && slot.deactivated) {
        continue;
      }
      if (slot.item != null && slot.item.ID == i.ID) {
        int stack_left = slot.item.maxStack() - slot.item.stack;
        if (stack_left < 1) {
          continue;
        }
        if (i.stack > stack_left) {
          slot.item.addStack(stack_left);
          i.removeStack(stack_left);
        }
        else {
          slot.item.addStack(i.stack);
          return null;
        }
      }
    }
    for (InventorySlot slot : this.slots) {
      if (!force && slot.deactivated) {
        continue;
      }
      if (slot.item == null) {
        slot.item = new Item(p, i);
        if (slot.item.remove) {
          slot.item = null;
        }
        return null;
      }
    }
    return i;
  }

  Item placeAt(Item i, int index) {
    return this.placeAt(i, index, false);
  }
  Item placeAt(Item i, int index, boolean replace) {
    return this.placeAt(i, index, replace, false);
  }
  Item placeAt(Item i, int index, boolean replace, boolean ignore_deactivate) {
    if (index < 0 || index >= this.slots.size()) {
      return i;
    }
    if (!ignore_deactivate && this.slots.get(index).deactivated) {
      return i;
    }
    if (i == null || i.remove) {
      return null;
    }
    if (this.slots.get(index).item == null) {
      this.slots.get(index).item = new Item(p, i);
      if (this.slots.get(index).item.remove) {
        this.slots.get(index).item = null;
      }
      return null;
    }
    else if (this.slots.get(index).item.ID == i.ID) {
      int stack_left = this.slots.get(index).item.maxStack() - this.slots.get(index).item.stack;
      if (i.stack > stack_left) {
        this.slots.get(index).item.addStack(stack_left);
        i.removeStack(stack_left);
      }
      else {
        this.slots.get(index).item.addStack(i.stack);
        return null;
      }
    }
    else if (replace) {
      Item replaced = new Item(p, this.slots.get(index).item);
      if (replaced.remove) {
        replaced = null;
      }
      this.slots.get(index).item = new Item(p, i);
      if (this.slots.get(index).item.remove) {
        this.slots.get(index).item = null;
      }
      return replaced;
    }
    return i;
  }

  ArrayList<Item> items() {
    ArrayList<Item> items = new ArrayList<Item>();
    for (InventorySlot slot : this.slots) {
      if (slot.item != null) {
        items.add(slot.item);
      }
    }
    return items;
  }


  // default 0 means default inventories can't craft anything
  int getCraftingHashCode() {
    return 0;
  }


  void drawBackground() {
    p.rectMode(PConstants.CORNER);
    p.fill(this.color_background);
    p.noStroke();
    p.rect(0, 0, this.display_width, this.display_height);
  }

  void update(int timeElapsed) {
    this.update(timeElapsed, true);
  }
  void update(int timeElapsed, boolean draw_background) {
    if (draw_background) {
      this.drawBackground();
    }
    p.imageMode(PConstants.CORNERS);
    for (int x = 0; x < this.max_cols; x++) {
      for (int y = 0; y < this.max_rows; y++) {
        int i = y * this.max_cols + x;
        if (i >= this.slots.size()) {
          break;
        }
        p.translate(2 + x * this.button_size, 2 + y * this.button_size);
        this.slots.get(i).update(timeElapsed);
        p.translate(-2 - x * this.button_size, -2 - y * this.button_size);
      }
    }
  }

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
        this.slots.get(i).mouseMove(
          (float)(mX - 2 - x * this.button_size),
          (float)(mY - 2 - y * this.button_size));
      }
    }
  }

  void mousePress() {
    for (InventorySlot slot : this.slots) {
      slot.mousePress();
    }
  }

  void mouseRelease(float mX, float mY) {
    for (int x = 0; x < this.max_cols; x++) {
      for (int y = 0; y < this.max_rows; y++) {
        int i = y * this.max_cols + x;
        if (i >= this.slots.size()) {
          break;
        }
        this.slots.get(i).mouseRelease(
          (float)(mX - 2 - x * this.button_size),
          (float)(mY - 2 - y * this.button_size));
      }
    }
  }


  String fileString() {
    String fileString = "\nnew: Inventory: " + this.max_rows + ", " + this.max_cols;
    for (int i = 0; i < this.slots.size(); i++) {
      fileString += "\naddSlot:";
      if (this.slots.get(i).item != null) {
        fileString += this.slots.get(i).item.fileString() + ": " + i;
      }
    }
    fileString += "\nend: Inventory";
    return fileString;
  }

  String internalFileString() {
    String fileString = "";
    for (int i = 0; i < this.slots.size(); i++) {
      if (this.slots.get(i).item != null) {
        fileString += this.slots.get(i).item.fileString() + ": " + i;
      }
    }
    return fileString;
  }

  void addData(String datakey, String data) {
    switch(datakey) {
      case "addSlot":
        this.addSlot();
        break;
      default:
        p.global.errorMessage("ERROR: Datakey " + datakey + " not found for inventory data.");
        break;
    }
  }


  static Inventory getKhalilInventory(LNZ p, int khalil_code) {
    Inventory inv = null;
    Item i;
    switch(khalil_code) {
      case 0:
        break;
      case 1: // Francis Hall
        inv = new Inventory(p, 1, 10, true);
        i = new Item(p, 2118); // chicken egg
        i.stack = 4;
        inv.stash(i);
        i = new Item(p, 2203); // knife
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2301); // slingshot
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2401); // talc helmet
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2501); // talc chestplate
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2601); // talc greaves
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2701); // talc boots
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2802); // talc crystal
        i.stack = 12;
        inv.stash(i);
        i = new Item(p, 2809); // string
        i.stack = 3;
        inv.stash(i);
        i = new Item(p, 2925); // water bottle
        i.stack = 1;
        inv.stash(i);
        break;
      case 2: // Frontdoor
        inv = new Inventory(p, 1, 10, true);
        i = new Item(p, 2124); // hot pocket box
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2205); // wooden sword
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2311); // recurve bow
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2411); // gypsum helmet
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2511); // gypsum chestplate
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2611); // gypsum greaves
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2711); // gypsum boots
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2812); // gypsum crystal
        i.stack = 12;
        inv.stash(i);
        i = new Item(p, 2904); // small keyring
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2923); // purse
        i.stack = 1;
        inv.stash(i);
        break;
      case 3: // Ahimdoor
        inv = new Inventory(p, 1, 10, true);
        i = new Item(p, 2141); // holy water
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2901); // key (42)
        i.ammo = 42;
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2212); // gypsum sword
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2411); // gypsum helmet
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2511); // gypsum chestplate
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2611); // gypsum greaves
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2711); // gypsum boots
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2812); // gypsum crystal
        i.stack = 12;
        inv.stash(i);
        i = new Item(p, 2918); // scissors
        i.stack = 1;
        inv.stash(i);
        i = new Item(p, 2923); // purse
        i.stack = 2;
        inv.stash(i);
        break;
      case 4: // courtyard (killed heck)
        inv = new Inventory(p, 1, 0, true);
        break;
      default:
        p.global.errorMessage("ERROR: Khalil code " + khalil_code + " not found.");
        break;
    }
    return inv;
  }
  
  static List<Double> getKhalilInventoryCosts(LNZ p, int khalil_code) {
    List<Double> costs = new ArrayList<Double>();
    switch(khalil_code) {
      case 0:
        break;
      case 1: // Francis Hall
        costs.add(2.6);
        costs.add(65.0);
        costs.add(55.0);
        costs.add(12.5);
        costs.add(20.0);
        costs.add(17.5);
        costs.add(10.0);
        costs.add(2.8);
        costs.add(2.2);
        costs.add(45.0);
        break;
      case 2: // Frontdoor
        costs.add(13.0);
        costs.add(50.0);
        costs.add(95.0);
        costs.add(20.0);
        costs.add(32.0);
        costs.add(28.0);
        costs.add(16.0);
        costs.add(5.0);
        costs.add(25.0);
        costs.add(18.0);
        break;
      case 3: // Ahimdoor
        costs.add(22.0);
        costs.add(50.0);
        costs.add(95.0);
        costs.add(20.0);
        costs.add(32.0);
        costs.add(28.0);
        costs.add(16.0);
        costs.add(5.0);
        costs.add(8.0);
        costs.add(18.0);
        break;
      case 4: // courtyard (killed heck)
        break;
      default:
        p.global.errorMessage("ERROR: Khalil code " + khalil_code + " not found.");
        break;
    }
    return costs;
  }
}