package su.sergiusonesimus.recreate.content.contraptions.processing;

import java.util.function.Consumer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ProcessingInventory extends InventoryBasic {

    public int[] limits;

    public float remainingTime;
    public float recipeDuration;
    public boolean appliedRecipe;
    public Consumer<ItemStack> callback;
    private boolean limit;

    public ProcessingInventory(Consumer<ItemStack> callback) {
        super("", false, 16);
        limits = new int[16];
        for (int i = 0; i < 16; i++) limits[i] = 64;
        this.callback = callback;
    }

    public ProcessingInventory withSlotLimit(boolean limit) {
        this.limit = limit;
        return this;
    }

    public int getSlotLimit(int slot) {
        return !limit ? limits[slot] : 1;
    }

    public int setSlotLimit(int slot, int limit) {
        return limits[slot] = limit;
    }

    public void clear() {
        for (int i = 0; i < getSizeInventory(); i++) setInventorySlotContents(i, null);
        remainingTime = 0;
        recipeDuration = 0;
        appliedRecipe = false;
    }

    public boolean isEmpty() {
        for (int i = 0; i < getSizeInventory(); i++) if (getStackInSlot(i) != null) return false;
        return true;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        super.setInventorySlotContents(slot, stack);

        if (slot == 0 && !this.getStackInSlot(slot)
            .equals(stack)) callback.accept(getStackInSlot(slot));
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setFloat("ProcessingTime", remainingTime);
        nbt.setFloat("RecipeTime", recipeDuration);
        nbt.setBoolean("AppliedRecipe", appliedRecipe);
        return nbt;
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        remainingTime = nbt.getFloat("ProcessingTime");
        recipeDuration = nbt.getFloat("RecipeTime");
        appliedRecipe = nbt.getBoolean("AppliedRecipe");
        if (isEmpty()) appliedRecipe = false;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot == 0 && isEmpty();
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return null;
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

}
