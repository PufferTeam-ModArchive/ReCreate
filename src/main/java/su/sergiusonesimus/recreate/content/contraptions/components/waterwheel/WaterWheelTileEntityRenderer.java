package su.sergiusonesimus.recreate.content.contraptions.components.waterwheel;

import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.metaworlds.util.Direction.AxisDirection;
import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.compat.tebreaker.TileEntityBreakerIntegration;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntity;
import su.sergiusonesimus.recreate.content.contraptions.base.KineticTileEntityRenderer;
import su.sergiusonesimus.recreate.content.contraptions.relays.elementary.shaft.ShaftModel;
import su.sergiusonesimus.recreate.foundation.utility.Color;

public class WaterWheelTileEntityRenderer extends KineticTileEntityRenderer {

    private final ShaftModel shaft = new ShaftModel();
    private final WaterWheelModel wheel = new WaterWheelModel();

    public void renderSafe(KineticTileEntity tileEntity, double x, double y, double z, float partialTicks) {
        WaterWheelBlock block = (WaterWheelBlock) tileEntity.getBlockType();
        Direction direction = block.getDirection(tileEntity.getBlockMetadata());
        Axis axis = direction.getAxis();
        float angle = getAngleForTe(tileEntity, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, axis);
        boolean shouldFlip = direction.getAxisDirection() == AxisDirection.POSITIVE;
        Vec3 flipVec = Vec3.createVectorHelper(0, 0, 0);
        if (shouldFlip) {
            angle *= -1;
            switch (axis) {
                case X:
                case Z:
                    flipVec.yCoord = 1;
                    break;
                case Y:
                    flipVec.xCoord = 1;
                    break;
            }
        }
        shaft.setAxis(axis);
        shaft.setRotation(angle);
        wheel.setAxis(axis);
        wheel.setRotation(angle);

        Color color = getColor(tileEntity);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        if (shouldFlip) GL11.glRotated(180D, flipVec.xCoord, flipVec.yCoord, flipVec.zCoord);

        GL11.glColor4f(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), color.getAlphaAsFloat());

        boolean damageTexture = ReCreate.isTileEntityBreakerLoaded
            && TileEntityBreakerIntegration.shouldRenderDamageTexture(this);

        shaft.render(this);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (damageTexture) TileEntityBreakerIntegration.setBreakTexture(
            this,
            TileEntityBreakerIntegration.WATER_WHEEL,
            TileEntityBreakerIntegration.getTileEntityDestroyProgress(tileEntity));

        wheel.render(this);

        GL11.glPopMatrix();
    }
}
