package su.sergiusonesimus.recreate.content.contraptions.relays.belt.transport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ItemHandlerBeltSegment implements IInventory {

    private final BeltInventory beltInventory;
    int offset;

    public ItemHandlerBeltSegment(BeltInventory beltInventory, int offset) {
        this.beltInventory = beltInventory;
        this.offset = offset;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        TransportedItemStack stackAtOffset = this.beltInventory.getStackAtOffset(offset);
        if (stackAtOffset == null) return null;
        return stackAtOffset.stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (this.beltInventory.canInsertAt(offset)) {
            TransportedItemStack newStack = new TransportedItemStack(stack.copy());
            newStack.insertedAt = offset;
            newStack.beltPosition = offset + .5f + (beltInventory.beltMovementPositive ? -1 : 1) / 16f;
            newStack.prevBeltPosition = newStack.beltPosition;
            this.beltInventory.addItem(newStack);
            this.beltInventory.belt.markDirty();
            this.beltInventory.belt.sendData();
            stack.stackSize = 0;
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int amount) {
        TransportedItemStack transported = this.beltInventory.getStackAtOffset(offset);
        if (transported == null) return null;

        amount = Math.min(amount, transported.stack.stackSize);
        ItemStack extracted = transported.stack.splitStack(amount);
        this.beltInventory.belt.markDirty();
        this.beltInventory.belt.sendData();
        return extracted;
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        TransportedItemStack transported = this.beltInventory.getStackAtOffset(offset);
        if (transported == null) return null;

        amount = Math.min(amount, transported.stack.stackSize);
        ItemStack extracted = simulate ? transported.stack.copy()
            .splitStack(amount) : transported.stack.splitStack(amount);
        if (!simulate) {
            this.beltInventory.belt.markDirty();
            this.beltInventory.belt.sendData();
        }
        return extracted;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public int getSlotLimit(int slot) {
        return Math.min(getStackInSlot(slot).getMaxStackSize(), 64);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
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
    public void markDirty() {}

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

}
