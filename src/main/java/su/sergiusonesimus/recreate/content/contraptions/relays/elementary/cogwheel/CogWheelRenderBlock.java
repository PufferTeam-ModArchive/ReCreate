package su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.AbstractShaftBlock;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftModel;
import su.sergiusonesimus.recreate.util.Direction.Axis;

public class CogWheelRenderBlock implements ISimpleBlockRenderingHandler {

    final int renderID;
    private final ShaftModel shaft = new ShaftModel();
    private final CogWheelModel cogwheel = new CogWheelModel();

    public CogWheelRenderBlock(int blockComplexRenderID) {
        this.renderID = blockComplexRenderID;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        GL11.glTranslatef(0.25F, 0.25F, 0.25F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        Axis axis = Axis.Y;
        shaft.setAxis(axis);
        shaft.render();
        if (!((CogWheelBlock) block).isLarge) {
            cogwheel.setAxis(axis);
            cogwheel.render();
        } else {

        }

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

        Axis axis = ((AbstractShaftBlock) block).getAxis(world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ));
        shaft.setAxis(axis);
        if (!((CogWheelBlock) block).isLarge) {
            cogwheel.setAxis(axis);
        } else {

        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);

        shaft.render();
        if (!((CogWheelBlock) block).isLarge) {
            cogwheel.render();
        } else {

        }

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
