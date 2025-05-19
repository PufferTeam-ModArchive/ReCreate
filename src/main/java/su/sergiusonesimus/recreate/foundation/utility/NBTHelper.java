package su.sergiusonesimus.recreate.foundation.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;

public class NBTHelper {

    public static void putMarker(NBTTagCompound nbt, String marker) {
        nbt.setBoolean(marker, true);
    }

    public static <T extends Enum<?>> T readEnum(NBTTagCompound nbt, String key, Class<T> enumClass) {
        T[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants == null)
            throw new IllegalArgumentException("Non-Enum class passed to readEnum: " + enumClass.getName());
        if (nbt.hasKey(key) && nbt.getTag(key) instanceof NBTTagString) {
            String name = nbt.getString(key);
            for (T t : enumConstants) {
                if (t.name()
                    .equals(name)) return t;
            }
        }
        return enumConstants[0];
    }

    public static <T extends Enum<?>> void writeEnum(NBTTagCompound nbt, String key, T enumConstant) {
        nbt.setString(key, enumConstant.name());
    }

    public static <T> NBTTagList writeCompoundList(Iterable<T> list, Function<T, NBTTagCompound> serializer) {
        NBTTagList listNBT = new NBTTagList();
        list.forEach(t -> listNBT.appendTag(serializer.apply(t)));
        return listNBT;
    }

    public static <T> List<T> readCompoundList(NBTTagList listNBT, Function<NBTTagCompound, T> deserializer) {
        List<T> list = new ArrayList<>(listNBT.tagCount());
        for (int i = 0; i < listNBT.tagCount(); i++) {
            list.add(deserializer.apply((NBTTagCompound) listNBT.getCompoundTagAt(i)));
        }
        return list;
    }

    public static <T> void iterateCompoundList(NBTTagList listNBT, Consumer<NBTTagCompound> consumer) {
        for (int i = 0; i < listNBT.tagCount(); i++) {
            consumer.accept((NBTTagCompound) listNBT.getCompoundTagAt(i));
        }
    }

    public static NBTTagList writeItemList(Iterable<ItemStack> stacks) {
        return writeCompoundList(stacks, NBTHelper::getTagFromItemStack);
    }

    public static NBTTagCompound getTagFromItemStack(ItemStack itemStack) {
        return itemStack.writeToNBT(new NBTTagCompound());
    }

    public static List<ItemStack> readItemList(NBTTagList stacks) {
        return readCompoundList(stacks, ItemStack::loadItemStackFromNBT);
    }

    public static NBTTagList writeAABB(AxisAlignedBB bb) {
        NBTTagList bbtag = new NBTTagList();
        bbtag.appendTag(new NBTTagFloat((float) bb.minX));
        bbtag.appendTag(new NBTTagFloat((float) bb.minY));
        bbtag.appendTag(new NBTTagFloat((float) bb.minZ));
        bbtag.appendTag(new NBTTagFloat((float) bb.maxX));
        bbtag.appendTag(new NBTTagFloat((float) bb.maxY));
        bbtag.appendTag(new NBTTagFloat((float) bb.maxZ));
        return bbtag;
    }

    public static AxisAlignedBB readAABB(NBTTagList bbtag) {
        if (bbtag == null || bbtag.tagCount() == 0) return null;
        return AxisAlignedBB.getBoundingBox(
            bbtag.func_150308_e(0),
            bbtag.func_150308_e(1),
            bbtag.func_150308_e(2),
            bbtag.func_150308_e(3),
            bbtag.func_150308_e(4),
            bbtag.func_150308_e(5));
    }

    public static NBTTagList writeChunkCoordinates(ChunkCoordinates ck) {
        NBTTagList tag = new NBTTagList();
        tag.appendTag(new NBTTagIntArray(new int[] { ck.posX, ck.posY, ck.posZ }));
        return tag;
    }

    public static ChunkCoordinates readChunkCoordinates(NBTTagList tag) {
        int[] array = tag.func_150306_c(0);
        return new ChunkCoordinates(array[0], array[1], array[2]);
    }

    @Nonnull
    public static NBTBase getINBT(NBTTagCompound nbt, String id) {
        NBTBase inbt = nbt.getTag(id);
        if (inbt != null) return inbt;
        return new NBTTagCompound();
    }

}
