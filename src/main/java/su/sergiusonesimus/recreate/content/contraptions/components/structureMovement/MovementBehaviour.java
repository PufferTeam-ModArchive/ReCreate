package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.util.ItemHandlerHelper;
import su.sergiusonesimus.recreate.util.VecHelper;

public abstract class MovementBehaviour {

    public boolean isActive(MovementContext context) {
        return true;
    }

    public void tick(MovementContext context) {}

    public void startMoving(MovementContext context) {}

    public void visitNewPosition(MovementContext context, int x, int y, int z) {}

    public Vec3 getActiveAreaOffset(MovementContext context) {
        return VecHelper.ZERO;
    }

    @SuppressWarnings("static-access")
    public void dropItem(MovementContext context, ItemStack stack) {
        ItemStack remainder;
        if (AllConfigs.SERVER.kinetics.moveItemsToStorage)
            remainder = ItemHandlerHelper.insertItem(context.contraption.inventory, stack, false);
        else remainder = stack;
        if (remainder == null) return;

        Vec3 vec = context.position;
        EntityItem entityItem = new EntityItem(context.world, vec.xCoord, vec.yCoord, vec.zCoord, remainder);
        Vec3 motion = context.motion.addVector(0, 0.5f, 0);
        float scale = context.world.rand.nextFloat() * .3f;
        entityItem.motionX = motion.xCoord * scale;
        entityItem.motionY = motion.yCoord * scale;
        entityItem.motionZ = motion.zCoord * scale;
        context.world.spawnEntityInWorld(entityItem);
    }

    public void stopMoving(MovementContext context) {

    }

    public void writeExtraData(MovementContext context) {

    }

    public boolean renderAsNormalTileEntity() {
        return false;
    }

    public boolean hasSpecialInstancedRendering() {
        return false;
    }

    public void onSpeedChanged(MovementContext context, Vec3 oldMotion, Vec3 motion) {}
}
