package su.sergiusonesimus.recreate.zmixin.mixins;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import org.spongepowered.asm.mixin.Mixin;

import su.sergiusonesimus.metaworlds.api.SubWorld;
import su.sergiusonesimus.metaworlds.entity.player.EntityPlayerMPSubWorldProxy;
import su.sergiusonesimus.metaworlds.world.SubWorldInfoHolder;
import su.sergiusonesimus.metaworlds.world.WorldManagerSubWorld;
import su.sergiusonesimus.metaworlds.zmixin.interfaces.minecraft.entity.IMixinEntity;
import su.sergiusonesimus.metaworlds.zmixin.interfaces.minecraft.server.IMixinMinecraftServer;
import su.sergiusonesimus.metaworlds.zmixin.interfaces.minecraft.world.IMixinWorld;
import su.sergiusonesimus.metaworlds.zmixin.interfaces.minecraft.world.storage.IMixinWorldInfo;
import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.Contraption;
import su.sergiusonesimus.recreate.zmixin.interfaces.IMixinWorldReCreate;

@Mixin(WorldServer.class)
public class MixinWorldServer extends MixinWorld {

    public World createContraptionWorld(Contraption contraption) {
        return ((IMixinWorldReCreate) ((IMixinWorld) this).getParentWorld())
            .createContraptionWorld(((IMixinWorld) this).getUnoccupiedSubworldID(), contraption);
    }

    public World createContraptionWorld(int newSubWorldID, Contraption contraption) {
        World newSubWorld = ReCreate.proxy.createContraptionWorld((World) (Object) this, newSubWorldID, contraption);
        if (((IMixinMinecraftServer) MinecraftServer.getServer()).getExistingSubWorlds()
            .put(((IMixinWorld) newSubWorld).getSubWorldID(), newSubWorld) != null) {
            throw new IllegalArgumentException("SubWorld with this ID already exists!");
        } else this.childSubWorlds.put(((IMixinWorld) newSubWorld).getSubWorldID(), newSubWorld);

        newSubWorld.worldInfo = this.worldInfo;
        ((WorldServer) newSubWorld).difficultySetting = EnumDifficulty.EASY;// Fixes AI crashes

        SubWorldInfoHolder curSubWorldInfo = ((IMixinWorldInfo) DimensionManager.getWorld(0)
            .getWorldInfo()).getSubWorldInfo(((IMixinWorld) newSubWorld).getSubWorldID());
        if (curSubWorldInfo != null) curSubWorldInfo.applyToSubWorld((SubWorld) newSubWorld);

        try {
            Class[] cArg = new Class[1];
            cArg[0] = World.class;
            Method loadWorldMethod = ForgeChunkManager.class.getDeclaredMethod("loadWorld", cArg);
            loadWorldMethod.setAccessible(true);
            try {
                loadWorldMethod.invoke(null, newSubWorld);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            System.out.println(e.toString());
        }

        newSubWorld.addWorldAccess(
            new WorldManagerSubWorld(((WorldServer) newSubWorld).func_73046_m(), (WorldServer) newSubWorld));

        for (Object curPlayer : this.playerEntities) {
            EntityPlayerMPSubWorldProxy proxyPlayer = (EntityPlayerMPSubWorldProxy) ((IMixinEntity) curPlayer)
                .getPlayerProxyMap()
                .get(((IMixinWorld) newSubWorld).getSubWorldID());

            if (proxyPlayer == null) {
                proxyPlayer = new EntityPlayerMPSubWorldProxy((EntityPlayerMP) curPlayer, newSubWorld);
                // TODO: newManager.setGameType(this.getGameType()); make this synchronized over all proxies and the
                // real player
            }

            proxyPlayer.theItemInWorldManager.setWorld((WorldServer) newSubWorld);

            newSubWorld.spawnEntityInWorld(proxyPlayer);
        }

        for (Object curPlayer : ((WorldServer) (Object) this).getPlayerManager().players) {
            EntityPlayerMPSubWorldProxy proxyPlayer = (EntityPlayerMPSubWorldProxy) ((IMixinEntity) curPlayer)
                .getPlayerProxyMap()
                .get(((IMixinWorld) newSubWorld).getSubWorldID());

            if (proxyPlayer == null) {
                proxyPlayer = new EntityPlayerMPSubWorldProxy((EntityPlayerMP) curPlayer, newSubWorld);
                // TODO: newManager.setGameType(this.getGameType()); make this synchronized over all proxies and the
                // real player
            }

            proxyPlayer.theItemInWorldManager.setWorld((WorldServer) newSubWorld);// make sure the right one is assigned
                                                                                  // if the player is not in
                                                                                  // playerEntities somehow

            ((WorldServer) newSubWorld).getPlayerManager()
                .addPlayer(proxyPlayer);
        }

        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(newSubWorld));

        return newSubWorld;
    }

}
