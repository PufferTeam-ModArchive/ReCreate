package su.sergiusonesimus.recreate.content.contraptions.components.waterwheel;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftModel;

public class WaterWheelRenderBlock implements ISimpleBlockRenderingHandler {

    final int renderID;
    private final ShaftModel shaft = new ShaftModel();
    private final WaterWheelModel wheel = new WaterWheelModel();

    public WaterWheelRenderBlock(int blockComplexRenderID) {
        this.renderID = blockComplexRenderID;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        Direction.Axis axis = Direction.Axis.Y;
        shaft.setAxis(axis);
        shaft.render();

        wheel.setAxis(axis);
        wheel.render();

        block.setBlockBoundsForItemRender();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
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
