package su.sergiusonesimus.recreate.content.contraptions.relays.elementary.cogwheel;

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
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftModel;

public class CogWheelRenderBlock implements ISimpleBlockRenderingHandler {

    final int renderID;
    private final ShaftModel shaft = new ShaftModel();
    private final CogWheelModel cogwheel = new CogWheelModel();
    private final LargeCogWheelModel largeCogwheel = new LargeCogWheelModel();

    public CogWheelRenderBlock(int blockComplexRenderID) {
        this.renderID = blockComplexRenderID;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        Axis axis = Axis.Y;
        shaft.setAxis(axis);
        shaft.render();
        if (!((CogWheelBlock) block).isLarge) {
            cogwheel.setAxis(axis);
            cogwheel.render();
        } else {
            largeCogwheel.setAxis(axis);
            largeCogwheel.render();
        }

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
        if (ICogWheel.isLargeCog(block) && ICogWheel.isLargeCog(world.getBlock(mop.blockX, mop.blockY, mop.blockZ)))
            switch (axis) {
            case X:
            axis = mop.blockY == y ? Axis.Z : Axis.Y;
            break;
            case Y:
            axis = mop.blockZ == z ? Axis.X : Axis.Z;
            break;
            case Z:
            axis = mop.blockX == x ? Axis.Y : Axis.X;
            break;
            }
        shaft.setAxis(axis);
        if (!((CogWheelBlock) block).isLarge) {
            cogwheel.setAxis(axis);
        } else {
            largeCogwheel.setAxis(axis);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);

        shaft.render();
        if (!((CogWheelBlock) block).isLarge) {
            cogwheel.render();
        } else {
            largeCogwheel.render();
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
