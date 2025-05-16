package su.sergiusonesimus.recreate.zmixin.mixins;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {

    /**
     * Plays a pre-canned sound effect along with potentially auxiliary data-driven one-shot behaviour (particles, etc).
     */
    @Inject(method = "playAuxSFX", at = @At(value = "TAIL"))
    public void playAuxSFX(EntityPlayer player, int id, int x, int y, int z, int data, CallbackInfo ci) {
        switch (id) {
            // Rotation indicator
            case 3000:

                break;
        }
    }

}
