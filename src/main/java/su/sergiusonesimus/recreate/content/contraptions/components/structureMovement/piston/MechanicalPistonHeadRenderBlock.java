package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.piston;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.recreate.AllBlocks;

public class MechanicalPistonHeadRenderBlock implements ISimpleBlockRenderingHandler {

    final int renderID;

    public MechanicalPistonHeadRenderBlock(int blockComplexRenderID) {
        this.renderID = blockComplexRenderID;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        double minX = 0.0D;
        double minY = 0.0D;
        double minZ = 0.0D;
        double maxX = 1.0D;
        double maxY = 1.0D;
        double maxZ = 1.0D;
        double topHeight = 4.0D / 16.0D;
        int meta = world.getBlockMetadata(x, y, z);
        Direction dir = ((MechanicalPistonHeadBlock) block).getDirection(meta);

        AllBlocks.piston_extension_pole.setBlockBoundsBasedOnState(world, x, y, z);
        renderer.setRenderBoundsFromBlock(AllBlocks.piston_extension_pole);

        switch (dir) {
            case UP:
                renderer.renderMaxY -= topHeight;
                break;
            case DOWN:
                renderer.renderMinY += topHeight;
                renderer.uvRotateEast = renderer.uvRotateWest = renderer.uvRotateSouth = renderer.uvRotateNorth = 3;
                break;
            case EAST:
                renderer.renderMaxX -= topHeight;
                renderer.uvRotateTop = 1;
                renderer.uvRotateBottom = 2;
                renderer.uvRotateEast = 2;
                renderer.uvRotateWest = 1;
                break;
            case WEST:
                renderer.renderMinX += topHeight;
                renderer.uvRotateTop = 2;
                renderer.uvRotateBottom = 1;
                renderer.uvRotateEast = 1;
                renderer.uvRotateWest = 2;
                break;
            case SOUTH:
                renderer.renderMaxZ -= topHeight;
                renderer.uvRotateTop = 3;
                renderer.uvRotateBottom = 3;
                renderer.uvRotateSouth = 2;
                renderer.uvRotateNorth = 1;
                break;
            case NORTH:
                renderer.renderMinZ += topHeight;
                renderer.uvRotateSouth = 1;
                renderer.uvRotateNorth = 2;
                break;
        }
        renderer.renderStandardBlock(AllBlocks.piston_extension_pole, x, y, z);

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
        renderer.renderStandardBlock(block, x, y, z);

        renderer.uvRotateEast = renderer.uvRotateWest = renderer.uvRotateSouth = renderer.uvRotateNorth = renderer.uvRotateTop = renderer.uvRotateBottom = 0;

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return renderID;
    }

}
