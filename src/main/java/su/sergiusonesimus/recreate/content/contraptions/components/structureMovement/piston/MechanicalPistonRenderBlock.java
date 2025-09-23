package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.piston;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.metaworlds.util.Direction.AxisDirection;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.piston.MechanicalPistonBlock.PistonState;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftModel;
import su.sergiusonesimus.recreate.util.RenderHelper;

public class MechanicalPistonRenderBlock implements ISimpleBlockRenderingHandler {

    final int renderID;
    ShaftModel shaft = new ShaftModel();

    public MechanicalPistonRenderBlock(int blockComplexRenderID) {
        this.renderID = blockComplexRenderID;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        double pixel = 1.0D / 16.0D;
        double minX = 0.0D;
        double minY = 0.0D;
        double minZ = 0.0D;
        double maxX = 1.0D;
        double maxY = 1.0D;
        double maxZ = 1.0D;
        double topHeight = 4 * pixel;
        MechanicalPistonBlock piston = (MechanicalPistonBlock) block;
        int meta = piston.getMetadata(Direction.UP, true);

        // Firstly rendering bottom cover
        minY = 0.0D;
        maxY = 2 * pixel;
        renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
        piston.renderType = 1;
        RenderHelper.renderInvBox(renderer, block, meta);
        piston.renderType = 0;

        // Then - the piston core
        minX = minY = minZ = pixel;
        maxX = maxZ = 1.0D - pixel;
        maxY = 1.0D - topHeight;
        renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
        RenderHelper.renderInvBox(renderer, block, meta);

        // Then - rendering piston sides
        minX = 0.0D;
        maxX = 1.0D;
        minY = 2 * pixel;
        minZ = 0.0D;
        maxZ = 2 * pixel;
        renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
        RenderHelper.renderInvBox(renderer, block, meta);
        minZ = 1.0D - 2 * pixel;
        maxZ = 1.0D;
        renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
        RenderHelper.renderInvBox(renderer, block, meta);

        // And then - rendering piston head/pole
        minX = minZ = 0.0D;
        maxX = maxY = maxZ = 1.0D;
        minY = 1.0D - topHeight;
        renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
        piston.renderType = 2;
        RenderHelper.renderInvBox(renderer, block, meta);
        piston.renderType = 0;

        renderer.uvRotateEast = renderer.uvRotateWest = renderer.uvRotateSouth = renderer.uvRotateNorth = renderer.uvRotateTop = renderer.uvRotateBottom = 0;

        shaft.setAxis(Axis.X);
        shaft.render();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        double pixel = 1.0D / 16.0D;
        double minX = 0.0D;
        double minY = 0.0D;
        double minZ = 0.0D;
        double maxX = 1.0D;
        double maxY = 1.0D;
        double maxZ = 1.0D;
        double topHeight = 4 * pixel;
        int meta = world.getBlockMetadata(x, y, z);
        MechanicalPistonBlock piston = (MechanicalPistonBlock) block;
        Direction dir = piston.getDirection(meta);
        Axis axis = piston.getAxis(meta);
        PistonState state = piston.getPistonState(world, x, y, z);

        // Firstly rendering bottom cover
        switch (dir) {
            case UP:
                minY = 0.0D;
                maxY = 2 * pixel;
                break;
            case DOWN:
                minY = 1.0D - 2 * pixel;
                maxY = 1.0D;
                renderer.uvRotateEast = renderer.uvRotateWest = renderer.uvRotateSouth = renderer.uvRotateNorth = 3;
                break;
            case EAST:
                minX = 0.0D;
                maxX = 2 * pixel;
                renderer.uvRotateTop = 1;
                renderer.uvRotateBottom = 2;
                renderer.uvRotateEast = 2;
                renderer.uvRotateWest = 1;
                break;
            case WEST:
                minX = 1.0D - 2 * pixel;
                maxX = 1.0D;
                renderer.uvRotateTop = 2;
                renderer.uvRotateBottom = 1;
                renderer.uvRotateEast = 1;
                renderer.uvRotateWest = 2;
                break;
            case SOUTH:
                minZ = 0.0D;
                maxZ = 2 * pixel;
                renderer.uvRotateTop = 3;
                renderer.uvRotateBottom = 3;
                renderer.uvRotateSouth = 2;
                renderer.uvRotateNorth = 1;
                break;
            case NORTH:
                minZ = 1.0D - 2 * pixel;
                maxZ = 1.0D;
                renderer.uvRotateSouth = 1;
                renderer.uvRotateNorth = 2;
                break;
        }
        renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
        piston.renderType = 1;
        renderer.renderStandardBlock(block, x, y, z);
        piston.renderType = 0;

        // Then - the piston core
        minX = minY = minZ = pixel;
        maxX = maxY = maxZ = 1.0D - pixel;
        switch (dir) {
            case UP:
                maxY = 1.0D - topHeight;
                break;
            case DOWN:
                minY = topHeight;
                break;
            case EAST:
                maxX = 1.0D - topHeight;
                break;
            case WEST:
                minX = topHeight;
                break;
            case SOUTH:
                maxZ = 1.0D - topHeight;
                break;
            case NORTH:
                minZ = topHeight;
                break;
        }
        renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
        renderer.renderStandardBlock(block, x, y, z);

        // Then - rendering piston sides
        switch (axis) {
            case X:
                minX = 0.0D;
                maxX = 1.0D;
                if (dir.getAxis() == Axis.Y) {
                    if (dir.getAxisDirection() == AxisDirection.POSITIVE) minY = 2 * pixel;
                    else maxY = 1.0D - 2 * pixel;
                    minZ = 0.0D;
                    maxZ = 2 * pixel;
                    renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                    renderer.renderStandardBlock(block, x, y, z);
                    minZ = 1.0D - 2 * pixel;
                    maxZ = 1.0D;
                    renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                    renderer.renderStandardBlock(block, x, y, z);
                } else {
                    if (dir.getAxisDirection() == AxisDirection.POSITIVE) minZ = 2 * pixel;
                    else maxZ = 1.0D - 2 * pixel;
                    minY = 0.0D;
                    maxY = 2 * pixel;
                    renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                    renderer.renderStandardBlock(block, x, y, z);
                    minY = 1.0D - 2 * pixel;
                    maxY = 1.0D;
                    renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                    renderer.renderStandardBlock(block, x, y, z);
                }
                break;
            case Y:
                minY = 0.0D;
                maxY = 1.0D;
                if (dir.getAxis() == Axis.X) {
                    if (dir.getAxisDirection() == AxisDirection.POSITIVE) minX = 2 * pixel;
                    else maxX = 1.0D - 2 * pixel;
                    minZ = 0.0D;
                    maxZ = 2 * pixel;
                    renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                    renderer.renderStandardBlock(block, x, y, z);
                    minZ = 1.0D - 2 * pixel;
                    maxZ = 1.0D;
                    renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                    renderer.renderStandardBlock(block, x, y, z);
                } else {
                    if (dir.getAxisDirection() == AxisDirection.POSITIVE) minZ = 2 * pixel;
                    else maxZ = 1.0D - 2 * pixel;
                    minX = 0.0D;
                    maxX = 2 * pixel;
                    renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                    renderer.renderStandardBlock(block, x, y, z);
                    minX = 1.0D - 2 * pixel;
                    maxX = 1.0D;
                    renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                    renderer.renderStandardBlock(block, x, y, z);
                }
                break;
            case Z:
                minZ = 0.0D;
                maxZ = 1.0D;
                if (dir.getAxis() == Axis.X) {
                    if (dir.getAxisDirection() == AxisDirection.POSITIVE) minX = 2 * pixel;
                    else maxX = 1.0D - 2 * pixel;
                    minY = 0.0D;
                    maxY = 2 * pixel;
                    renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                    renderer.renderStandardBlock(block, x, y, z);
                    minY = 1.0D - 2 * pixel;
                    maxY = 1.0D;
                    renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                    renderer.renderStandardBlock(block, x, y, z);
                } else {
                    if (dir.getAxisDirection() == AxisDirection.POSITIVE) minY = 2 * pixel;
                    else maxY = 1.0D - 2 * pixel;
                    minX = 0.0D;
                    maxX = 2 * pixel;
                    renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                    renderer.renderStandardBlock(block, x, y, z);
                    minX = 1.0D - 2 * pixel;
                    maxX = 1.0D;
                    renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                    renderer.renderStandardBlock(block, x, y, z);
                }
                break;
        }

        // And then - rendering piston head/pole
        switch (state) {
            default:
                break;
            case EXTENDED:
                AllBlocks.piston_extension_pole.setBlockBoundsBasedOnState(world, x, y, z);
                renderer.setRenderBoundsFromBlock(AllBlocks.piston_extension_pole);

                switch (dir) {
                    case UP:
                        renderer.renderMinY = 0.5;
                        break;
                    case DOWN:
                        renderer.renderMaxY = 0.5;
                        break;
                    case EAST:
                        renderer.renderMinX = 0.5;
                        break;
                    case WEST:
                        renderer.renderMaxX = 0.5;
                        break;
                    case SOUTH:
                        renderer.renderMinZ = 0.5;
                        break;
                    case NORTH:
                        renderer.renderMaxZ = 0.5;
                        break;
                }

                renderer.renderStandardBlock(AllBlocks.piston_extension_pole, x, y, z);
                break;
            case RETRACTED:
                minX = minY = minZ = 0.0D;
                maxX = maxY = maxZ = 1.0D;
                switch (dir) {
                    case UP:
                        minY = 1.0D - topHeight;
                        maxY = 1.0D;
                        break;
                    case DOWN:
                        minY = 0.0D;
                        maxY = topHeight;
                        break;
                    case EAST:
                        minX = 1.0D - topHeight;
                        maxX = 1.0D;
                        break;
                    case WEST:
                        minX = 0.0D;
                        maxX = topHeight;
                        break;
                    case SOUTH:
                        minZ = 1.0D - topHeight;
                        maxZ = 1.0D;
                        break;
                    case NORTH:
                        minZ = 0.0D;
                        maxZ = topHeight;
                        break;
                }
                renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                piston.renderType = 2;
                renderer.renderStandardBlock(block, x, y, z);
                piston.renderType = 0;
                break;
        }

        renderer.uvRotateEast = renderer.uvRotateWest = renderer.uvRotateSouth = renderer.uvRotateNorth = renderer.uvRotateTop = renderer.uvRotateBottom = 0;

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderID;
    }

}
