package su.sergiusonesimus.recreate.zmixin.mixins;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import su.sergiusonesimus.recreate.zmixin.interfaces.IMixinRenderBlocks;

@Mixin(RenderBlocks.class)
public class MixinRenderBlocks implements IMixinRenderBlocks {

    private Float overrideAlpha = null;

    @Override
    public void setOverrideAlpha(int alpha) {
        overrideAlpha = (float) alpha / 255.0F;
    }

    @Override
    public void setOverrideAlpha(float alpha) {
        overrideAlpha = alpha;
    }

    @Override
    public void clearOverrideAlpha() {
        overrideAlpha = null;
    }

    @WrapOperation(
        method = { "renderFaceYPos", "renderFaceYNeg", "renderFaceXPos", "renderFaceXNeg", "renderFaceZPos",
            "renderFaceZNeg" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;setColorOpaque_F(FFF)V"))
    public void setColorWithAlpha(Tessellator tessellator, float red, float green, float blue,
        Operation<Void> original) {
        if (overrideAlpha == null) original.call(tessellator, red, green, blue);
        else tessellator.setColorRGBA_F(red, green, blue, overrideAlpha);
    }
}
