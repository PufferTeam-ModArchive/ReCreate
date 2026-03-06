package su.sergiusonesimus.recreate.content.contraptions.processing;

import java.util.Random;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class ProcessingOutput {

    public static final ProcessingOutput EMPTY = new ProcessingOutput(null, 1);

    private static final Random r = new Random();
    private final ItemStack stack;
    private final float chance;

    public ProcessingOutput(ItemStack stack, float chance) {
        this.stack = stack;
        this.chance = chance;
    }

    public ItemStack getStack() {
        return stack;
    }

    public float getChance() {
        return chance;
    }

    public ItemStack rollOutput() {
        int outputAmount = stack.stackSize;
        for (int roll = 0; roll < stack.stackSize; roll++) if (r.nextFloat() > chance) outputAmount--;
        if (outputAmount == 0) return null;
        ItemStack out = stack.copy();
        out.stackSize = outputAmount;
        return out;
    }

    public void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, getStack());
        buf.writeFloat(getChance());
    }

    public static ProcessingOutput read(ByteBuf buf) {
        return new ProcessingOutput(ByteBufUtils.readItemStack(buf), buf.readFloat());
    }

}
