package su.sergiusonesimus.recreate.zmixin.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderList;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import su.sergiusonesimus.metaworlds.client.entity.EntityClientPlayerMPSubWorldProxy;
import su.sergiusonesimus.metaworlds.zmixin.interfaces.minecraft.client.renderer.IMixinRenderGlobal;
import su.sergiusonesimus.metaworlds.zmixin.interfaces.minecraft.world.IMixinWorld;
import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.Contraption;
import su.sergiusonesimus.recreate.zmixin.interfaces.IMixinWorldReCreate;

@Mixin(WorldClient.class)
public class MixinWorldClient extends MixinWorld {

    @Shadow(remap = true)
    private Minecraft mc;

    public World createContraptionWorld(Contraption contraption) {
        return ((IMixinWorldReCreate) ((IMixinWorld) this).getParentWorld())
            .createContraptionWorld(((IMixinWorld) this).getUnoccupiedSubworldID(), contraption);
    }

    public World createContraptionWorld(int newSubWorldID, Contraption contraption) {
        World newSubWorld = ReCreate.proxy.createContraptionWorld(((World) (Object) this), newSubWorldID, contraption);
        if (((IMixinWorld) this).getSubWorldsMap()
            .get(((IMixinWorld) newSubWorld).getSubWorldID()) == null) {
            ((IMixinWorld) this).getSubWorldsMap()
                .put(((IMixinWorld) newSubWorld).getSubWorldID(), newSubWorld);

            // The constructor assigns the proxy to the real player
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            EntityClientPlayerMPSubWorldProxy proxyPlayer = new EntityClientPlayerMPSubWorldProxy(player, newSubWorld);
            proxyPlayer.getMinecraft().renderViewEntity = proxyPlayer;

            newSubWorld.addWorldAccess(proxyPlayer.getMinecraft().renderGlobal);

            ((IMixinRenderGlobal) this.mc.renderGlobal)
                .loadRenderersForNewSubWorld(((IMixinWorld) newSubWorld).getSubWorldID());
            this.mc.renderGlobal.allRenderLists = new RenderList[4 * ((IMixinWorld) this).getWorldsCount()];
            for (int i = 0; i < 4 * ((IMixinWorld) this).getWorldsCount(); ++i) {
                this.mc.renderGlobal.allRenderLists[i] = new RenderList();
            }
        }
        return newSubWorld;
    }

}
