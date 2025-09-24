package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.bearing;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.recreate.util.RenderHelper;
import su.sergiusonesimus.recreate.zmixin.interfaces.IMixinRenderBlocks;

public class SailRenderBlock implements ISimpleBlockRenderingHandler {

    final int renderID;

    public SailRenderBlock(int blockComplexRenderID) {
        this.renderID = blockComplexRenderID;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        double pixel = 1.0D / 16D;
        SailBlock sail = (SailBlock) block;
        double min = sail.frame ? 0.0D : pixel;
        int correctMeta = Direction.WEST.get3DDataValue();

        if (!sail.frame) {
            renderer.setRenderBounds(1 - 7 * pixel, 0.0D, 0.0D, 1 - 6 * pixel, 1.0D, 1.0D);
            renderer.setOverrideBlockTexture(sail.getIcon(correctMeta, correctMeta));
            ((IMixinRenderBlocks) renderer).overrideTextureBlockBounds(15, 0, 0, 16, 16, 16);
            RenderHelper.renderInvBox(renderer, block, correctMeta);
            renderer.clearOverrideBlockTexture();
            ((IMixinRenderBlocks) renderer).clearTextureBlockBounds();
        }

        renderer.uvRotateTop = 1;
        renderer.uvRotateBottom = 2;
        renderer.uvRotateEast = 2;
        renderer.uvRotateWest = 1;

        renderer.setRenderBounds(6 * pixel, 0.0D, 0.0D, 1.0D - 6 * pixel - min, 3 * pixel, 3 * pixel);
        RenderHelper.renderInvBox(renderer, block, correctMeta);
        renderer.setRenderBounds(6 * pixel, 1.0D - 3 * pixel, 0.0D, 1.0D - 6 * pixel - min, 1.0D, 3 * pixel);
        RenderHelper.renderInvBox(renderer, block, correctMeta);
        renderer.setRenderBounds(6 * pixel, 0.0D, 1.0D - 3 * pixel, 1.0D - 6 * pixel - min, 3 * pixel, 1.0D);
        RenderHelper.renderInvBox(renderer, block, correctMeta);
        renderer.setRenderBounds(6 * pixel, 1.0D - 3 * pixel, 1.0D - 3 * pixel, 1.0D - 6 * pixel - min, 1.0D, 1.0D);
        RenderHelper.renderInvBox(renderer, block, correctMeta);

        renderer.setRenderBounds(7 * pixel, 0.0D, 3 * pixel, 1.0D - 6 * pixel - min, 2 * pixel, 1.0D - 3 * pixel);
        RenderHelper.renderInvBox(renderer, block, correctMeta);
        renderer
            .setRenderBounds(7 * pixel, 1.0D - 2 * pixel, 3 * pixel, 1.0D - 6 * pixel - min, 1.0D, 1.0D - 3 * pixel);
        RenderHelper.renderInvBox(renderer, block, correctMeta);
        renderer.setRenderBounds(7 * pixel, 3 * pixel, 0.0D, 1.0D - 6 * pixel - min, 1.0D - 3 * pixel, 2 * pixel);
        RenderHelper.renderInvBox(renderer, block, correctMeta);
        renderer
            .setRenderBounds(7 * pixel, 3 * pixel, 1.0D - 2 * pixel, 1.0D - 6 * pixel - min, 1.0D - 3 * pixel, 1.0D);
        RenderHelper.renderInvBox(renderer, block, correctMeta);

        renderer.uvRotateBottom = renderer.uvRotateTop = renderer.uvRotateNorth = renderer.uvRotateSouth = renderer.uvRotateWest = renderer.uvRotateEast = 0;

        block.setBlockBoundsForItemRender();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        double pixel = 1.0D / 16D;
        SailBlock sail = (SailBlock) block;
        double min = sail.frame ? 0.0D : pixel;
        int meta;
        if (world.getBlock(x, y, z) instanceof SailBlock) meta = world.getBlockMetadata(x, y, z);
        else {
            MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
            if (mop == null || mop.typeOfHit != MovingObjectType.BLOCK
                || !(world.getBlock(mop.blockX, mop.blockY, mop.blockZ) instanceof SailBlock)) return false;
            meta = world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ);
        }
        Direction dir = sail.getDirection(meta);
        boolean damageTexture = false;
        if (renderer.overrideBlockTexture != null)
            for (Object object : Minecraft.getMinecraft().renderGlobal.damagedBlocks.values()) {
                DestroyBlockProgress dbp = (DestroyBlockProgress) object;
                if (dbp.getPartialBlockX() == x && dbp.getPartialBlockY() == y && dbp.getPartialBlockZ() == z) {
                    damageTexture = true;
                    break;
                }
            }

        switch (dir) {
            case UP:
                if (!sail.frame) {
                    renderer.setRenderBounds(0.0D, 1 - 7 * pixel, 0.0D, 1.0D, 1 - 6 * pixel, 1.0D);
                    if (!damageTexture) {
                        renderer.setOverrideBlockTexture(sail.getIcon(dir.get3DDataValue(), meta));
                        ((IMixinRenderBlocks) renderer).overrideTextureBlockBounds(0, 15, 0, 16, 16, 16);
                    }
                    renderer.renderStandardBlock(block, x, y, z);
                    if (!damageTexture) {
                        renderer.clearOverrideBlockTexture();
                        ((IMixinRenderBlocks) renderer).clearTextureBlockBounds();
                    }
                }

                renderer.uvRotateEast = 0;
                renderer.uvRotateWest = 0;
                renderer.uvRotateSouth = 0;
                renderer.uvRotateNorth = 0;

                renderer.setRenderBounds(0.0D, 6 * pixel, 0.0D, 3 * pixel, 1.0D - 6 * pixel - min, 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(1.0D - 3 * pixel, 6 * pixel, 0.0D, 1.0D, 1.0D - 6 * pixel - min, 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0D, 6 * pixel, 1.0D - 3 * pixel, 3 * pixel, 1.0D - 6 * pixel - min, 1.0D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer
                    .setRenderBounds(1.0D - 3 * pixel, 6 * pixel, 1.0D - 3 * pixel, 1.0D, 1.0D - 6 * pixel - min, 1.0D);
                renderer.renderStandardBlock(block, x, y, z);

                renderer
                    .setRenderBounds(0.0D, 7 * pixel, 3 * pixel, 2 * pixel, 1.0D - 6 * pixel - min, 1.0D - 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(
                    1.0D - 2 * pixel,
                    7 * pixel,
                    3 * pixel,
                    1.0D,
                    1.0D - 6 * pixel - min,
                    1.0D - 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer
                    .setRenderBounds(3 * pixel, 7 * pixel, 0.0D, 1.0D - 3 * pixel, 1.0D - 6 * pixel - min, 2 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(
                    3 * pixel,
                    7 * pixel,
                    1.0D - 2 * pixel,
                    1.0D - 3 * pixel,
                    1.0D - 6 * pixel - min,
                    1.0D);
                renderer.renderStandardBlock(block, x, y, z);
                break;
            case DOWN:
                if (!sail.frame) {
                    renderer.setRenderBounds(0.0D, 6 * pixel, 0.0D, 1.0D, 7 * pixel, 1.0D);
                    if (!damageTexture) {
                        renderer.setOverrideBlockTexture(sail.getIcon(dir.get3DDataValue(), meta));
                        ((IMixinRenderBlocks) renderer).overrideTextureBlockBounds(0, 0, 0, 16, 1, 16);
                    }
                    renderer.renderStandardBlock(block, x, y, z);
                    if (!damageTexture) {
                        renderer.clearOverrideBlockTexture();
                        ((IMixinRenderBlocks) renderer).clearTextureBlockBounds();
                    }
                }

                renderer.uvRotateEast = 3;
                renderer.uvRotateWest = 3;
                renderer.uvRotateSouth = 3;
                renderer.uvRotateNorth = 3;

                renderer.setRenderBounds(0.0D, 6 * pixel + min, 0.0D, 3 * pixel, 1 - 6 * pixel, 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(1.0D - 3 * pixel, 6 * pixel + min, 0.0D, 1.0D, 1 - 6 * pixel, 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0D, 6 * pixel + min, 1.0D - 3 * pixel, 3 * pixel, 1 - 6 * pixel, 1.0D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer
                    .setRenderBounds(1.0D - 3 * pixel, 6 * pixel + min, 1.0D - 3 * pixel, 1.0D, 1 - 6 * pixel, 1.0D);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(0.0D, 6 * pixel + min, 3 * pixel, 2 * pixel, 1 - 7 * pixel, 1.0D - 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(
                    1.0D - 2 * pixel,
                    6 * pixel + min,
                    3 * pixel,
                    1.0D,
                    1 - 7 * pixel,
                    1.0D - 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(3 * pixel, 6 * pixel + min, 0.0D, 1.0D - 3 * pixel, 1 - 7 * pixel, 2 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(
                    3 * pixel,
                    6 * pixel + min,
                    1.0D - 2 * pixel,
                    1.0D - 3 * pixel,
                    1 - 7 * pixel,
                    1.0D);
                renderer.renderStandardBlock(block, x, y, z);
                break;
            case EAST:
                if (!sail.frame) {
                    renderer.setRenderBounds(1 - 7 * pixel, 0.0D, 0.0D, 1 - 6 * pixel, 1.0D, 1.0D);
                    if (!damageTexture) {
                        renderer.setOverrideBlockTexture(sail.getIcon(dir.get3DDataValue(), meta));
                        ((IMixinRenderBlocks) renderer).overrideTextureBlockBounds(15, 0, 0, 16, 16, 16);
                    }
                    renderer.renderStandardBlock(block, x, y, z);
                    if (!damageTexture) {
                        renderer.clearOverrideBlockTexture();
                        ((IMixinRenderBlocks) renderer).clearTextureBlockBounds();
                    }
                }

                renderer.uvRotateTop = 1;
                renderer.uvRotateBottom = 2;
                renderer.uvRotateEast = 2;
                renderer.uvRotateWest = 1;

                renderer.setRenderBounds(6 * pixel, 0.0D, 0.0D, 1.0D - 6 * pixel - min, 3 * pixel, 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(6 * pixel, 1.0D - 3 * pixel, 0.0D, 1.0D - 6 * pixel - min, 1.0D, 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(6 * pixel, 0.0D, 1.0D - 3 * pixel, 1.0D - 6 * pixel - min, 3 * pixel, 1.0D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer
                    .setRenderBounds(6 * pixel, 1.0D - 3 * pixel, 1.0D - 3 * pixel, 1.0D - 6 * pixel - min, 1.0D, 1.0D);
                renderer.renderStandardBlock(block, x, y, z);

                renderer
                    .setRenderBounds(7 * pixel, 0.0D, 3 * pixel, 1.0D - 6 * pixel - min, 2 * pixel, 1.0D - 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(
                    7 * pixel,
                    1.0D - 2 * pixel,
                    3 * pixel,
                    1.0D - 6 * pixel - min,
                    1.0D,
                    1.0D - 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer
                    .setRenderBounds(7 * pixel, 3 * pixel, 0.0D, 1.0D - 6 * pixel - min, 1.0D - 3 * pixel, 2 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(
                    7 * pixel,
                    3 * pixel,
                    1.0D - 2 * pixel,
                    1.0D - 6 * pixel - min,
                    1.0D - 3 * pixel,
                    1.0D);
                renderer.renderStandardBlock(block, x, y, z);
                break;
            case WEST:
                if (!sail.frame) {
                    renderer.setRenderBounds(6 * pixel, 0.0D, 0.0D, 7 * pixel, 1.0D, 1.0D);
                    if (!damageTexture) {
                        renderer.setOverrideBlockTexture(sail.getIcon(dir.get3DDataValue(), meta));
                        ((IMixinRenderBlocks) renderer).overrideTextureBlockBounds(0, 0, 0, 1, 16, 16);
                    }
                    renderer.renderStandardBlock(block, x, y, z);
                    if (!damageTexture) {
                        renderer.clearOverrideBlockTexture();
                        ((IMixinRenderBlocks) renderer).clearTextureBlockBounds();
                    }
                }

                renderer.uvRotateTop = 2;
                renderer.uvRotateBottom = 1;
                renderer.uvRotateEast = 1;
                renderer.uvRotateWest = 2;

                renderer.setRenderBounds(6 * pixel + min, 0.0D, 0.0D, 1 - 6 * pixel, 3 * pixel, 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(6 * pixel + min, 1.0D - 3 * pixel, 0.0D, 1 - 6 * pixel, 1.0D, 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(6 * pixel + min, 0.0D, 1.0D - 3 * pixel, 1 - 6 * pixel, 3 * pixel, 1.0D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer
                    .setRenderBounds(6 * pixel + min, 1.0D - 3 * pixel, 1.0D - 3 * pixel, 1 - 6 * pixel, 1.0D, 1.0D);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(6 * pixel + min, 0.0D, 3 * pixel, 1 - 7 * pixel, 2 * pixel, 1.0D - 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(
                    6 * pixel + min,
                    1.0D - 2 * pixel,
                    3 * pixel,
                    1 - 7 * pixel,
                    1.0D,
                    1.0D - 3 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(6 * pixel + min, 3 * pixel, 0.0D, 1 - 7 * pixel, 1.0D - 3 * pixel, 2 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(
                    6 * pixel + min,
                    3 * pixel,
                    1.0D - 2 * pixel,
                    1 - 7 * pixel,
                    1.0D - 3 * pixel,
                    1.0D);
                renderer.renderStandardBlock(block, x, y, z);
                break;
            case SOUTH:
                if (!sail.frame) {
                    renderer.setRenderBounds(0.0D, 0.0D, 1 - 7 * pixel, 1.0D, 1.0D, 1 - 6 * pixel);
                    if (!damageTexture) {
                        renderer.setOverrideBlockTexture(sail.getIcon(dir.get3DDataValue(), meta));
                        ((IMixinRenderBlocks) renderer).overrideTextureBlockBounds(0, 0, 15, 16, 16, 16);
                    }
                    renderer.renderStandardBlock(block, x, y, z);
                    if (!damageTexture) {
                        renderer.clearOverrideBlockTexture();
                        ((IMixinRenderBlocks) renderer).clearTextureBlockBounds();
                    }
                }

                renderer.uvRotateTop = 3;
                renderer.uvRotateBottom = 0;
                renderer.uvRotateSouth = 2;
                renderer.uvRotateNorth = 1;

                renderer.setRenderBounds(0.0D, 0.0D, 6 * pixel, 3 * pixel, 3 * pixel, 1.0D - 6 * pixel - min);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(1.0D - 3 * pixel, 0.0D, 6 * pixel, 1.0D, 3 * pixel, 1.0D - 6 * pixel - min);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0D, 1.0D - 3 * pixel, 6 * pixel, 3 * pixel, 1.0D, 1.0D - 6 * pixel - min);
                renderer.renderStandardBlock(block, x, y, z);
                renderer
                    .setRenderBounds(1.0D - 3 * pixel, 1.0D - 3 * pixel, 6 * pixel, 1.0D, 1.0D, 1.0D - 6 * pixel - min);
                renderer.renderStandardBlock(block, x, y, z);

                renderer
                    .setRenderBounds(0.0D, 3 * pixel, 7 * pixel, 2 * pixel, 1.0D - 3 * pixel, 1.0D - 6 * pixel - min);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(
                    1.0D - 2 * pixel,
                    3 * pixel,
                    7 * pixel,
                    1.0D,
                    1.0D - 3 * pixel,
                    1.0D - 6 * pixel - min);
                renderer.renderStandardBlock(block, x, y, z);
                renderer
                    .setRenderBounds(3 * pixel, 0.0D, 7 * pixel, 1.0D - 3 * pixel, 2 * pixel, 1.0D - 6 * pixel - min);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(
                    3 * pixel,
                    1.0D - 2 * pixel,
                    7 * pixel,
                    1.0D - 3 * pixel,
                    1.0D,
                    1.0D - 6 * pixel - min);
                renderer.renderStandardBlock(block, x, y, z);
                break;
            case NORTH:
                if (!sail.frame) {
                    renderer.setRenderBounds(0.0D, 0.0D, 6 * pixel, 1.0D, 1.0D, 7 * pixel);
                    if (!damageTexture) {
                        renderer.setOverrideBlockTexture(sail.getIcon(dir.get3DDataValue(), meta));
                        ((IMixinRenderBlocks) renderer).overrideTextureBlockBounds(0, 0, 0, 16, 16, 1);
                    }
                    renderer.renderStandardBlock(block, x, y, z);
                    if (!damageTexture) {
                        renderer.clearOverrideBlockTexture();
                        ((IMixinRenderBlocks) renderer).clearTextureBlockBounds();
                    }
                }

                renderer.uvRotateTop = 0;
                renderer.uvRotateBottom = 3;
                renderer.uvRotateSouth = 1;
                renderer.uvRotateNorth = 2;

                renderer.setRenderBounds(0.0D, 0.0D, 6 * pixel + min, 3 * pixel, 3 * pixel, 1 - 6 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(1.0D - 3 * pixel, 0.0D, 6 * pixel + min, 1.0D, 3 * pixel, 1 - 6 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0D, 1.0D - 3 * pixel, 6 * pixel + min, 3 * pixel, 1.0D, 1 - 6 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer
                    .setRenderBounds(1.0D - 3 * pixel, 1.0D - 3 * pixel, 6 * pixel + min, 1.0D, 1.0D, 1 - 6 * pixel);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(0.0D, 3 * pixel, 6 * pixel + min, 2 * pixel, 1.0D - 3 * pixel, 1 - 7 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(
                    1.0D - 2 * pixel,
                    3 * pixel,
                    6 * pixel + min,
                    1.0D,
                    1.0D - 3 * pixel,
                    1 - 7 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(3 * pixel, 0.0D, 6 * pixel + min, 1.0D - 3 * pixel, 2 * pixel, 1 - 7 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(
                    3 * pixel,
                    1.0D - 2 * pixel,
                    6 * pixel + min,
                    1.0D - 3 * pixel,
                    1.0D,
                    1 - 7 * pixel);
                renderer.renderStandardBlock(block, x, y, z);
                break;
        }

        renderer.uvRotateBottom = renderer.uvRotateTop = renderer.uvRotateNorth = renderer.uvRotateSouth = renderer.uvRotateWest = renderer.uvRotateEast = 0;

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
