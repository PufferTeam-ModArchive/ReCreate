package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.piston;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.util.BlockVolatilityMap;
import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.AllSubWorldTypes;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.AssemblyException;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.BlockMovementChecks;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.ContraptionType;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.IControlContraption;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.TranslatingContraption;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.piston.MechanicalPistonBlock.PistonState;
import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.util.VecHelper;

public class PistonContraption extends TranslatingContraption {

    protected int extensionLength;
    protected int initialExtensionProgress;
    protected Direction orientation;

    private AxisAlignedBB pistonExtensionCollisionBox;
    private boolean retract;

    @Override
    protected ContraptionType getType() {
        return ContraptionType.PISTON;
    }

    public PistonContraption() {}

    public PistonContraption(World parentWorld, IControlContraption controller, Direction direction, boolean retract) {
        super(parentWorld, controller, direction.getAxis());
        this.orientation = direction;
        this.retract = retract;
    }

    @Override
    public boolean assemble(World world, int x, int y, int z) throws AssemblyException {
        if (!collectExtensions(world, x, y, z, orientation)) return false;
        if (!searchMovedStructure(world, anchorX, anchorY, anchorZ, retract ? orientation.getOpposite() : orientation))
            return false;
        startMoving(world);
        return true;
    }

    private boolean collectExtensions(World world, int x, int y, int z, Direction direction) throws AssemblyException {
        List<ChunkCoordinates> poles = new ArrayList<ChunkCoordinates>();
        int startX = x;
        int startY = y;
        int startZ = z;
        ChunkCoordinates normal = direction.getNormal();
        Block nextBlock = world.getBlock(x + normal.posX, y + normal.posY, z + normal.posZ);
        int nextMeta = world.getBlockMetadata(x + normal.posX, y + normal.posY, z + normal.posZ);
        int extensionsInFront = 0;
        Block currentBlock = world.getBlock(x, y, z);

        if (!MechanicalPistonBlock.isPiston(currentBlock)) return false;

        if (((MechanicalPistonBlock) currentBlock).getPistonState(world, x, y, z) == PistonState.EXTENDED) {
            while (PistonExtensionPoleBlock.PlacementHelper.get()
                .matchesAxis(nextBlock, nextMeta, direction.getAxis())
                || (MechanicalPistonBlock.isPistonHead(nextBlock)
                    && ((MechanicalPistonHeadBlock) nextBlock).getDirection(nextMeta) == direction)) {

                startX += normal.posX;
                startY += normal.posY;
                startZ += normal.posZ;
                poles.add(new ChunkCoordinates(startX, startY, startZ));
                extensionsInFront++;

                if (MechanicalPistonBlock.isPistonHead(nextBlock)) break;

                nextBlock = world.getBlock(startX + normal.posX, startY + normal.posY, startZ + normal.posZ);
                nextMeta = world.getBlockMetadata(startX + normal.posX, startY + normal.posY, startZ + normal.posZ);
                if (extensionsInFront > MechanicalPistonBlock.maxAllowedPistonPoles())
                    throw AssemblyException.tooManyPistonPoles();
            }
        }

        poles.add(new ChunkCoordinates(x, y, z));

        int endX = x;
        int endY = y;
        int endZ = z;
        normal = direction.getOpposite()
            .getNormal();
        nextBlock = world.getBlock(endX + normal.posX, endY + normal.posY, endZ + normal.posZ);
        nextMeta = world.getBlockMetadata(endX + normal.posX, endY + normal.posY, endZ + normal.posZ);
        int extensionsInBack = 0;

        while (PistonExtensionPoleBlock.PlacementHelper.get()
            .matchesAxis(nextBlock, nextMeta, direction.getAxis())) {
            endX += normal.posX;
            endY += normal.posY;
            endZ += normal.posZ;
            poles.add(new ChunkCoordinates(endX, endY, endZ));
            extensionsInBack++;
            nextBlock = world.getBlock(endX + normal.posX, endY + normal.posY, endZ + normal.posZ);
            nextMeta = world.getBlockMetadata(endX + normal.posX, endY + normal.posY, endZ + normal.posZ);

            if (extensionsInFront + extensionsInBack > MechanicalPistonBlock.maxAllowedPistonPoles())
                throw AssemblyException.tooManyPistonPoles();
        }

        normal = direction.getNormal();
        initialExtensionProgress = extensionsInFront;
        anchorX = x + normal.posX * (initialExtensionProgress + 1);
        anchorY = y + normal.posY * (initialExtensionProgress + 1);
        anchorZ = z + normal.posZ * (initialExtensionProgress + 1);
        extensionLength = extensionsInBack + extensionsInFront;
        pistonExtensionCollisionBox = AxisAlignedBB
            .getBoundingBox(
                -normal.posX,
                -normal.posY,
                -normal.posZ,
                (-extensionLength - 1) * normal.posX,
                (-extensionLength - 1) * normal.posY,
                (-extensionLength - 1) * normal.posZ)
            .addCoord(1, 1, 1);

        if (extensionLength == 0) throw AssemblyException.noPistonPoles();

        for (ChunkCoordinates pole : poles) getBlocks().add(pole);

        return true;
    }

    @Override
    protected boolean isAnchoringBlockAt(int x, int y, int z) {
        // TODO May have done this wrong
        return pistonExtensionCollisionBox.isVecInside(VecHelper.getCenterOf(x, y, z));
    }

    @SuppressWarnings("static-access")
    @Override
    protected boolean addToInitialFrontier(World world, int x, int y, int z, Direction direction,
        Queue<ChunkCoordinates> frontier) throws AssemblyException {
        frontier.clear();
        ChunkCoordinates normal = orientation.getOpposite()
            .getNormal();
        Block neighbourBlock = world.getBlock(x + normal.posX, y + normal.posY, z + normal.posZ);
        boolean sticky = MechanicalPistonBlock.isStickyPiston(neighbourBlock)
            || (neighbourBlock instanceof MechanicalPistonHeadBlock head
                && head.isSticky(world.getBlockMetadata(x + normal.posX, y + normal.posY, z + normal.posZ)));
        boolean retracting = direction != orientation;
        if (retracting && !sticky) return true;
        normal = orientation.getNormal();
        for (int offset = 0; offset <= AllConfigs.SERVER.kinetics.maxChassisRange; offset++) {
            if (offset == 1 && retracting) return true;
            int currentX = x + normal.posX * offset;
            int currentY = y + normal.posY * offset;
            int currentZ = z + normal.posZ * offset;
            if (retracting && currentY >= world.getHeight()) return true;
            if (!world.blockExists(currentX, currentY, currentZ))
                throw AssemblyException.unloadedChunk(currentX, currentY, currentZ);
            Block block = world.getBlock(currentX, currentY, currentZ);
            int meta = world.getBlockMetadata(currentX, currentY, currentZ);
            if (!BlockMovementChecks.isMovementNecessary(block, meta, world, currentX, currentY, currentZ)) return true;
            if (BlockVolatilityMap.checkBlockVolatility(block) && !(block instanceof BlockCarpet)) return true;
            if (MechanicalPistonBlock.isPistonHead(block)
                && ((MechanicalPistonHeadBlock) block).getDirection(meta) == direction.getOpposite()) return true;
            if (!BlockMovementChecks.isMovementAllowed(block, meta, world, currentX, currentY, currentZ))
                if (retracting) return true;
                else throw AssemblyException.unmovableBlock(currentX, currentY, currentZ, block, meta);
            // No blocks with such pull reaction on 1.7.10
            // if (retracting && block.getMobilityFlag() == PushReaction.PUSH_ONLY) return true;
            frontier.add(new ChunkCoordinates(currentX, currentY, currentZ));
            if (BlockMovementChecks.isNotSupportive(block, meta, orientation)) return true;
        }
        return true;
    }

    // TODO
    // @Override
    // protected void addBlock(World world, int x, int y, int z) {
    // ChunkCoordinates normal = orientation.getNormal();
    // super.addBlock(
    // world,
    // x - initialExtensionProgress * normal.posX,
    // y - initialExtensionProgress * normal.posY,
    // z - initialExtensionProgress * normal.posZ);
    // }

    @Override
    protected boolean customBlockPlacement(IBlockAccess world, int x, int y, int z, Block block, int meta) {
        if (x == controllerX && y == controllerY && z == controllerZ) {
            TileEntity te = world.getTileEntity(controllerX, controllerY, controllerZ);
            if (te == null || te.isInvalid()) return true;
            if (!MechanicalPistonBlock.isExtensionPole(block) && te instanceof MechanicalPistonTileEntity mpte) {
                mpte.state = PistonState.RETRACTED;
                mpte.notifyUpdate();
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean customBlockRemoval(IBlockAccess world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(controllerX, controllerY, controllerZ);
        if (x == controllerX && y == controllerY && z == controllerZ && te instanceof MechanicalPistonTileEntity mpte) {
            switch (mpte.state) {
                default:
                    break;
                case EXTENDED:
                    meta %= 6;
                    contraptionWorld.setBlock(x, y, z, AllBlocks.piston_extension_pole, meta, 0);
                    contraptionWorld.setBlockMetadataWithNotify(x, y, z, meta, 0);
                    break;
                case RETRACTED:
                    meta %= 6;
                    if (((MechanicalPistonBlock) block).isSticky) meta += 6;
                    contraptionWorld.setBlock(x, y, z, AllBlocks.mechanical_piston_head, meta, 0);
                    contraptionWorld.setBlockMetadataWithNotify(x, y, z, meta, 0);
                    break;
            }
            mpte.state = PistonState.MOVING;
            mpte.notifyUpdate();
            return true;
        }
        return false;
    }

    @Override
    public void readNBT(NBTTagCompound nbt, boolean spawnData) {
        super.readNBT(nbt, spawnData);
        initialExtensionProgress = nbt.getInteger("InitialLength");
        extensionLength = nbt.getInteger("ExtensionLength");
        orientation = Direction.from3DDataValue(nbt.getInteger("Orientation"));
    }

    @Override
    public NBTTagCompound writeNBT(boolean spawnPacket) {
        NBTTagCompound tag = super.writeNBT(spawnPacket);
        tag.setInteger("InitialLength", initialExtensionProgress);
        tag.setInteger("ExtensionLength", extensionLength);
        tag.setInteger("Orientation", orientation.get3DDataValue());
        return tag;
    }

    @Override
    public String getSubWorldType() {
        return AllSubWorldTypes.SUBWORLD_TYPE_CONTRAPTION_PISTON;
    }
}
