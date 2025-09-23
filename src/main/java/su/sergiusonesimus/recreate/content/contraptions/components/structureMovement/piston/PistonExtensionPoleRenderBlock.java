package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.piston;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import su.sergiusonesimus.recreate.util.RenderHelper;

public class PistonExtensionPoleRenderBlock implements ISimpleBlockRenderingHandler {

    final int renderID;

    public PistonExtensionPoleRenderBlock(int blockComplexRenderID) {
        this.renderID = blockComplexRenderID;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        double pixel = 1D / 16D;

        renderer.setRenderBounds(6 * pixel, 0.0D, 6 * pixel, 1 - 6 * pixel, 1.0D, 1 - 6 * pixel);
        RenderHelper.renderInvBox(renderer, block, 0);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);
        boolean renderGhost = false;
        if (world.getBlock(x, y, z) != block) {
            MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
            if (mop == null || mop.typeOfHit != MovingObjectType.BLOCK
                || !(world.getBlock(mop.blockX, mop.blockY, mop.blockZ) instanceof PistonExtensionPoleBlock pole))
                return false;
            meta = world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ);
            pole.setBlockBoundsBasedOnState(world, mop.blockX, mop.blockY, mop.blockZ);
            renderer.setRenderBounds(
                pole.getBlockBoundsMinX(),
                pole.getBlockBoundsMinY(),
                pole.getBlockBoundsMinZ(),
                pole.getBlockBoundsMaxX(),
                pole.getBlockBoundsMaxY(),
                pole.getBlockBoundsMaxZ());
            renderGhost = true;
        }

        switch (((PistonExtensionPoleBlock) block).getDirection(meta)) {
            case UP:
                break;
            case DOWN:
                renderer.uvRotateEast = renderer.uvRotateWest = renderer.uvRotateSouth = renderer.uvRotateNorth = 3;
                break;
            case EAST:
                renderer.uvRotateTop = 1;
                renderer.uvRotateBottom = 2;
                renderer.uvRotateEast = 2;
                renderer.uvRotateWest = 1;
                break;
            case WEST:
                renderer.uvRotateTop = 2;
                renderer.uvRotateBottom = 1;
                renderer.uvRotateEast = 1;
                renderer.uvRotateWest = 2;
                break;
            case SOUTH:
                renderer.uvRotateNorth = 1;
                renderer.uvRotateSouth = 2;
                break;
            case NORTH:
                renderer.uvRotateNorth = 2;
                renderer.uvRotateSouth = 1;
                break;
        }

        if (renderGhost) {
            Tessellator tessellator = Tessellator.instance;
            tessellator.setNormal(0, -1, 0);
            renderer.renderFaceYNeg(block, x, y, z, block.getIcon(0, meta));
            tessellator.setNormal(0, 1, 0);
            renderer.renderFaceYPos(block, x, y, z, block.getIcon(1, meta));
            tessellator.setNormal(0, 0, -1);
            renderer.renderFaceZNeg(block, x, y, z, block.getIcon(2, meta));
            tessellator.setNormal(0, 0, 1);
            renderer.renderFaceZPos(block, x, y, z, block.getIcon(3, meta));
            tessellator.setNormal(-1, 0, 0);
            renderer.renderFaceXNeg(block, x, y, z, block.getIcon(4, meta));
            tessellator.setNormal(1, 0, 0);
            renderer.renderFaceXPos(block, x, y, z, block.getIcon(5, meta));
        } else renderer.renderStandardBlock(block, x, y, z);

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
