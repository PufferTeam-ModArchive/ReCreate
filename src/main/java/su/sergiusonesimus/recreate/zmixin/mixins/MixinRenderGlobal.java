package su.sergiusonesimus.recreate.zmixin.mixins;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import su.sergiusonesimus.metaworlds.zmixin.interfaces.minecraft.util.IMixinAxisAlignedBB;
import su.sergiusonesimus.metaworlds.zmixin.interfaces.minecraft.util.IMixinMovingObjectPosition;
import su.sergiusonesimus.recreate.zmixin.interfaces.IMixinBlock;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {

    @Shadow(remap = true)
    public static void drawOutlinedBoundingBox(AxisAlignedBB p_147590_0_, int p_147590_1_) {}

    private MovingObjectPosition storedRayTraceHit;
    private Block storedBlock;
    private float storedF1;
    private double storedD0;
    private double storedD1;
    private double storedD2;

    @Inject(
        method = "drawSelectionBox",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderGlobal;drawOutlinedBoundingBox(Lnet/minecraft/util/AxisAlignedBB;I)V",
            opcode = Opcodes.INVOKESTATIC,
            shift = Shift.BEFORE))
    private void saveVariables(EntityPlayer entityPlayer, MovingObjectPosition rayTraceHit, int i,
        float partialTickTime, CallbackInfo ci, @Local(name = "block") Block block, @Local(name = "f1") float f1,
        @Local(name = "d0") double d0, @Local(name = "d1") double d1, @Local(name = "d2") double d2) {
        storedRayTraceHit = rayTraceHit;
        storedBlock = block;
        storedF1 = f1;
        storedD0 = d0;
        storedD1 = d1;
        storedD2 = d2;
    }

    @WrapOperation(
        method = "drawSelectionBox",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderGlobal;drawOutlinedBoundingBox(Lnet/minecraft/util/AxisAlignedBB;I)V",
            opcode = Opcodes.INVOKESTATIC))
    private void wrapDrawOutlinedBoundingBox(AxisAlignedBB aabb, int localI, Operation<Void> original) {
        List<AxisAlignedBB> bbList = ((IMixinBlock) storedBlock).getSelectedBoundingBoxesList(
            ((IMixinMovingObjectPosition) storedRayTraceHit).getWorld(),
            storedRayTraceHit.blockX,
            storedRayTraceHit.blockY,
            storedRayTraceHit.blockZ);
        for (AxisAlignedBB partAABB : bbList) {
            boolean expandX = true;
            boolean expandY = true;
            boolean expandZ = true;
            for (AxisAlignedBB partAABB1 : bbList) {
                if (partAABB1 == partAABB) continue;
                if (partAABB1.minX == partAABB.maxX || partAABB1.maxX == partAABB.minX) expandX = false;
                if (partAABB1.minY == partAABB.maxY || partAABB1.maxY == partAABB.minY) expandY = false;
                if (partAABB1.minZ == partAABB.maxZ || partAABB1.maxZ == partAABB.minZ) expandZ = false;
            }
            original.call(
                ((IMixinAxisAlignedBB) partAABB.expand(
                    expandX ? (double) storedF1 : 0,
                    expandY ? (double) storedF1 : 0,
                    expandZ ? (double) storedF1 : 0))
                        .getTransformedToGlobalBoundingBox(((IMixinMovingObjectPosition) storedRayTraceHit).getWorld())
                        .offset(-storedD0, -storedD1, -storedD2),
                -1);
        }
    }

    /**
     * Plays a pre-canned sound effect along with potentially auxiliary data-driven one-shot behaviour (particles, etc).
     */
    @Inject(method = "playAuxSFX", at = @At(value = "TAIL"))
    private void playAuxSFX(EntityPlayer player, int id, int x, int y, int z, int data, CallbackInfo ci) {
        switch (id) {
            // Rotation indicator
            case 3000:

                break;
        }
    }

}
