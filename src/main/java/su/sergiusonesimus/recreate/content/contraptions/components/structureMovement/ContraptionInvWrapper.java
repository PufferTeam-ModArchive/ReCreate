package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContraptionInvWrapper implements IInventory {

    protected final boolean isExternal;
    protected final IInventory[] inventories;
    protected final int[] indexes;
    protected int inventorySize;

    public ContraptionInvWrapper(boolean isExternal, IInventory... itemHandler) {
        this.isExternal = isExternal;
        inventories = itemHandler;
        indexes = new int[inventories.length];
        inventorySize = 0;
        for (int i = 0; i < inventories.length; i++) {
            IInventory inventory = inventories[i];
            indexes[i] = inventorySize;
            inventorySize += inventory.getSizeInventory();
        }
    }

    public ContraptionInvWrapper(IInventory... itemHandler) {
        this(false, itemHandler);
    }

    public boolean isSlotExternal(int slot) {
        if (isExternal) return true;
        IInventory handler = getHandlerFromIndex(getIndexForSlot(slot));
        return handler instanceof ContraptionInvWrapper && ((ContraptionInvWrapper) handler).isSlotExternal(slot);
    }

    private IInventory getHandlerFromIndex(int index) {
        return inventories[index];
    }

    private int getIndexForSlot(int slot) {
        for (int i = 0; i < indexes.length; i++) {
            if (slot >= indexes[i]) return i;
        }
        return indexes.length - 1;
    }

    @Override
    public int getSizeInventory() {
        return inventorySize;
    }

    private int getLocalSlot(int slotIn) {
        return getLocalSlot(slotIn, getIndexForSlot(slotIn));
    }

    private int getLocalSlot(int slotIn, int inventoryIndex) {
        return slotIn - inventoryIndex;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        int index = getIndexForSlot(slotIn);
        return inventories[index].getStackInSlot(getLocalSlot(slotIn, index));
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        int index = getIndexForSlot(slot);
        return inventories[index].decrStackSize(getLocalSlot(slot, index), count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        int index = getIndexForSlot(slot);
        return inventories[index].getStackInSlotOnClosing(getLocalSlot(slot, index));
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        int index = getIndexForSlot(slot);
        inventories[index].setInventorySlotContents(getLocalSlot(slot, index), stack);
    }

    @Override
    public String getInventoryName() {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        for (IInventory inventory : inventories) inventory.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        int index = getIndexForSlot(slot);
        return inventories[index].isItemValidForSlot(getLocalSlot(slot, index), stack);
    }
}
