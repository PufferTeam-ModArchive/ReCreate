package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

import su.sergiusonesimus.metaworlds.world.SubWorldServer;

public class ContraptionWorldFactory {

    public static World createContraptionWorld(World parentWorld, int newSubWorldID, Contraption contraption) {
        World subWorld;
        if (parentWorld.isRemote) {
            subWorld = new ContraptionWorldClient(
                (WorldClient) parentWorld,
                newSubWorldID,
                ((WorldClient) parentWorld).sendQueue,
                new WorldSettings(
                    0L,
                    parentWorld.getWorldInfo()
                        .getGameType(),
                    false,
                    parentWorld.getWorldInfo()
                        .isHardcoreModeEnabled(),
                    parentWorld.getWorldInfo()
                        .getTerrainType()),
                ((WorldClient) parentWorld).provider.dimensionId,
                parentWorld.difficultySetting,
                parentWorld.theProfiler,
                contraption);
        } else {
            SubWorldServer.global_newSubWorldID = newSubWorldID;
            subWorld = new ContraptionWorldServer(
                (WorldServer) parentWorld,
                newSubWorldID,
                ((WorldServer) parentWorld).func_73046_m(),
                parentWorld.getSaveHandler(),
                parentWorld.getWorldInfo()
                    .getWorldName(),
                parentWorld.provider.dimensionId,
                new WorldSettings(
                    0L,
                    parentWorld.getWorldInfo()
                        .getGameType(),
                    false,
                    parentWorld.getWorldInfo()
                        .isHardcoreModeEnabled(),
                    parentWorld.getWorldInfo()
                        .getTerrainType()),
                parentWorld.theProfiler,
                contraption);
        }
        if (contraption != null) ((ContraptionWorld) subWorld).setSubWorldType(contraption.getSubWorldType());
        return subWorld;
    }

}
