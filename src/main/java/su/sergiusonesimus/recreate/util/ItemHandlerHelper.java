package su.sergiusonesimus.recreate.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ItemHandlerHelper {

    public static ItemStack insertItem(IInventory inventory, ItemStack stack, boolean simulate) {
        if (stack == null || stack.stackSize == 0) {
            return null;
        }

        ItemStack remaining = stack.copy();

        for (int i = 0; i < inventory.getSizeInventory() && remaining.stackSize > 0; i++) {
            ItemStack slotStack = inventory.getStackInSlot(i);

            if (slotStack != null && slotStack.isItemEqual(remaining)
                && ItemStack.areItemStackTagsEqual(slotStack, remaining)) {
                int maxStackSize = Math.min(inventory.getInventoryStackLimit(), remaining.getMaxStackSize());
                int space = maxStackSize - slotStack.stackSize;

                if (space > 0) {
                    int toAdd = Math.min(space, remaining.stackSize);

                    if (!simulate) {
                        ItemStack newStack = slotStack.copy();
                        newStack.stackSize += toAdd;
                        inventory.setInventorySlotContents(i, newStack);
                    }

                    remaining.stackSize -= toAdd;
                }
            }
        }

        for (int i = 0; i < inventory.getSizeInventory() && remaining.stackSize > 0; i++) {
            if (inventory.getStackInSlot(i) == null) {
                int maxStackSize = Math.min(inventory.getInventoryStackLimit(), remaining.getMaxStackSize());
                int toAdd = Math.min(maxStackSize, remaining.stackSize);

                if (!simulate) {
                    ItemStack newStack = remaining.copy();
                    newStack.stackSize = toAdd;
                    inventory.setInventorySlotContents(i, newStack);
                }

                remaining.stackSize -= toAdd;
            }
        }

        return remaining.stackSize > 0 ? remaining : null;
    }

}
