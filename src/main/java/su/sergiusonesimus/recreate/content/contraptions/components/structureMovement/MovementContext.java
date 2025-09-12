package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import java.util.function.UnaryOperator;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import su.sergiusonesimus.recreate.util.VecHelper;

public class MovementContext {

    public Vec3 position;
    public Vec3 motion;
    public Vec3 relativeMotion;
    public UnaryOperator<Vec3> rotation;

    public World world;
    public Block block;
    public int meta;
    public int localX;
    public int localY;
    public int localZ;
    public TileEntity tileEntity;

    public boolean stall;
    public boolean firstMovement;
    public NBTTagCompound data;
    public Contraption contraption;
    public Object temporaryData;

    public MovementContext(World world, int x, int y, int z, Contraption contraption) {
        this.world = world;
        World contraptionWorld = contraption.getWorld();
        this.block = contraptionWorld.getBlock(x, y, z);
        this.meta = contraptionWorld.getBlockMetadata(x, y, z);
        this.tileEntity = contraptionWorld.getTileEntity(x, y, z);
        this.contraption = contraption;
        this.localX = x;
        this.localY = y;
        this.localZ = z;

        firstMovement = true;
        motion = VecHelper.ZERO;
        relativeMotion = VecHelper.ZERO;
        rotation = v -> v;
        position = null;
        data = new NBTTagCompound();
        stall = false;
    }

    public float getAnimationSpeed() {
        int modifier = 1000;
        double length = -motion.lengthVector();
        if (world.isRemote && contraption.stalled) return 700;
        if (Math.abs(length) < 1 / 512f) return 0;
        return (((int) (length * modifier + 100 * Math.signum(length))) / 100) * 100;
    }

    public static MovementContext readNBT(World world, int x, int y, int z, NBTTagCompound nbt,
        Contraption contraption) {
        MovementContext context = new MovementContext(world, x, y, z, contraption);
        context.motion = VecHelper.readNBT(nbt.getTagList("Motion", 6));
        context.relativeMotion = VecHelper.readNBT(nbt.getTagList("RelativeMotion", 6));
        if (nbt.hasKey("Position")) context.position = VecHelper.readNBT(nbt.getTagList("Position", 6));
        context.stall = nbt.getBoolean("Stall");
        context.firstMovement = nbt.getBoolean("FirstMovement");
        context.data = nbt.getCompoundTag("Data");
        return context;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("Motion", VecHelper.writeNBT(motion));
        nbt.setTag("RelativeMotion", VecHelper.writeNBT(relativeMotion));
        if (position != null) nbt.setTag("Position", VecHelper.writeNBT(position));
        nbt.setBoolean("Stall", stall);
        nbt.setBoolean("FirstMovement", firstMovement);
        nbt.setTag("Data", data.copy());
        return nbt;
    }

}
