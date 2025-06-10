package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;

import su.sergiusonesimus.recreate.foundation.config.AllConfigs;

public class AssemblyException extends Exception {

    private static final long serialVersionUID = 1L;
    public final IChatComponent component;
    private ChunkCoordinates position = null;

    public static void write(NBTTagCompound compound, AssemblyException exception) {
        if (exception == null) return;

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("IChatComponent", IChatComponent.Serializer.func_150696_a(exception.component));
        if (exception.hasPosition()) {
            NBTTagCompound pos = new NBTTagCompound();
            ChunkCoordinates position = exception.getPosition();
            pos.setInteger("X", position.posX);
            pos.setInteger("Y", position.posY);
            pos.setInteger("Z", position.posZ);
            nbt.setTag("Position", pos);
        }

        compound.setTag("LastException", nbt);
    }

    public static AssemblyException read(NBTTagCompound compound) {
        if (!compound.hasKey("LastException")) return null;

        NBTTagCompound nbt = compound.getCompoundTag("LastException");
        String string = nbt.getString("IChatComponent");
        AssemblyException exception = new AssemblyException(IChatComponent.Serializer.func_150699_a(string));
        if (nbt.hasKey("Position")) {
            nbt = compound.getCompoundTag("Position");
            int x = nbt.getInteger("X");
            int y = nbt.getInteger("Y");
            int z = nbt.getInteger("Z");
            exception.position = new ChunkCoordinates(x, y, z);
        }

        return exception;
    }

    public AssemblyException(IChatComponent component) {
        this.component = component;
    }

    public AssemblyException(String langKey, Object... objects) {
        this(new ChatComponentTranslation("create.gui.assembly.exception." + langKey, objects));
    }

    public static AssemblyException unmovableBlock(int posX, int posY, int posZ, Block block, int meta) {
        return unmovableBlock(new ChunkCoordinates(posX, posY, posZ), block, meta);
    }

    public static AssemblyException unmovableBlock(ChunkCoordinates pos, Block block, int meta) {
        AssemblyException e = new AssemblyException(
            "unmovableBlock",
            pos.posX,
            pos.posY,
            pos.posZ,
            new ChatComponentTranslation(block.getUnlocalizedName()).appendText(" with metadata " + meta));
        e.position = pos;
        return e;
    }

    public static AssemblyException unloadedChunk(int posX, int posY, int posZ) {
        return unloadedChunk(new ChunkCoordinates(posX, posY, posZ));
    }

    public static AssemblyException unloadedChunk(ChunkCoordinates pos) {
        AssemblyException e = new AssemblyException("chunkNotLoaded", pos.posX, pos.posY, pos.posZ);
        e.position = pos;
        return e;
    }

    public static AssemblyException structureTooLarge() {
        return new AssemblyException("structureTooLarge", AllConfigs.SERVER.kinetics.maxBlocksMoved);
    }

    public static AssemblyException tooManyPistonPoles() {
        return new AssemblyException("tooManyPistonPoles", AllConfigs.SERVER.kinetics.maxPistonPoles);
    }

    public static AssemblyException noPistonPoles() {
        return new AssemblyException("noPistonPoles");
    }

    public static AssemblyException notEnoughSails(int sails) {
        return new AssemblyException("not_enough_sails", sails, AllConfigs.SERVER.kinetics.minimumWindmillSails);
    }

    public boolean hasPosition() {
        return position != null;
    }

    public ChunkCoordinates getPosition() {
        return position;
    }
}
