package su.sergiusonesimus.recreate.util;

import net.minecraft.nbt.NBTTagCompound;

public interface IPartialSafeNBT {

    public void writeSafe(NBTTagCompound compound, boolean clientPacket);
}
