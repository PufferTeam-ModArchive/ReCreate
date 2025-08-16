package su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.AbstractShaftBlock;

public class ShaftRenderBlock implements ISimpleBlockRenderingHandler {

    final int renderID;
    private final ShaftModel model = new ShaftModel();

    public ShaftRenderBlock(int blockComplexRenderID) {
        this.renderID = blockComplexRenderID;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        GL11.glTranslatef(0.25F, 0.25F, 0.25F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        model.setAxis(Axis.Y);
        model.render();

        GL11.glTranslatef(-0.25F, -0.25F, -0.25F);

        block.setBlockBoundsForItemRender();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        if (world == null || world.getTileEntity(x, y, z) != null) return false;
        MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
        if (mop == null || mop.typeOfHit != MovingObjectType.BLOCK
            || !(world.getBlock(mop.blockX, mop.blockY, mop.blockZ) instanceof AbstractShaftBlock)) return false;

        model.setAxis(((AbstractShaftBlock) block).getAxis(world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ)));

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);

        model.render();

        GL11.glPopMatrix();
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
