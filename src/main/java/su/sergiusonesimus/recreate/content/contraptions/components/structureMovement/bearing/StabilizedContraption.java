package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.bearing;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.metaworlds.api.SubWorld;
import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.AllSubWorldTypes;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.AssemblyException;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.Contraption;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.ContraptionType;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.ContraptionWorld;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.ControlledContraption;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.IControlContraption;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.OrientedContraption;

public class StabilizedContraption extends ControlledContraption {

    private Direction facing;

    public StabilizedContraption() {
        super();
    }

    public StabilizedContraption(World world, IControlContraption controller, Direction facing) {
        super(world, controller, facing.getAxis());
        this.facing = facing;
    }

    @Override
    public boolean assemble(World world, int x, int y, int z) throws AssemblyException {
        ChunkCoordinates normal = facing.getNormal();
        if (!searchMovedStructure(world, x + normal.posX, y + normal.posY, z + normal.posZ, null)) return false;
        startMoving(world);
        if (this.blocks.isEmpty()) return false;
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void doTickPartial(double interpolationFactor) {
        super.doTickPartial(interpolationFactor);
        IBearingTileEntity controller = (IBearingTileEntity) getController();
        if (controller != null && parentWorld instanceof ContraptionWorld contraption) {
            controller.setAngle(
                getCounterRotationAngle(
                    contraption.getContraption(),
                    controller.getPositionX(),
                    controller.getPositionY(),
                    controller.getPositionZ(),
                    facing,
                    (float) interpolationFactor));
        }
    }

    static float getCounterRotationAngle(Contraption contraption, int localX, int localY, int localZ, Direction facing,
        float renderPartialTicks) {
        float offset = 0;

        Axis axis = facing.getAxis();

        if (contraption instanceof ControlledContraption cc) {
            if (contraption.canBeStabilized(facing, localX, localY, localZ)) offset = -cc.getAngle(renderPartialTicks);

        } else if (contraption instanceof OrientedContraption orientedContraption) {
            if (axis.isVertical()) offset = -orientedContraption.getViewYRot(renderPartialTicks);
            else {
                if (orientedContraption.isInitialOrientationPresent() && orientedContraption.getInitialOrientation()
                    .getAxis() == axis) offset = -orientedContraption.getViewXRot(renderPartialTicks);
            }
        }
        return offset;
    }

    protected void processSubContraptionPosition(SubWorld anchorWorld) {
        super.processSubContraptionPosition(anchorWorld);
        SubWorld contraptionSubworld = this.getContraptionWorld();
        switch (facing.getAxis()) {
            case X:
                contraptionSubworld.setRotationPitch(0);
                break;
            case Y:
                contraptionSubworld.setRotationYaw(0);
                break;
            case Z:
                contraptionSubworld.setRotationRoll(0);
                break;
        }
    }

    @Override
    protected boolean isAnchoringBlockAt(int x, int y, int z) {
        return false;
    }

    @Override
    protected ContraptionType getType() {
        return ContraptionType.STABILIZED;
    }

    @Override
    public NBTTagCompound writeNBT(boolean spawnPacket) {
        NBTTagCompound tag = super.writeNBT(spawnPacket);
        tag.setInteger("Facing", facing.get3DDataValue());
        return tag;
    }

    @Override
    public void readNBT(NBTTagCompound tag, boolean spawnData) {
        super.readNBT(tag, spawnData);
        facing = Direction.from3DDataValue(tag.getInteger("Facing"));
    }

    @Override
    public boolean canBeStabilized(Direction facing, int localX, int localY, int localZ) {
        return false;
    }

    public Direction getFacing() {
        return facing;
    }

    @Override
    public String getSubWorldType() {
        return AllSubWorldTypes.SUBWORLD_TYPE_CONTRAPTION_STABILIZED;
    }
}
