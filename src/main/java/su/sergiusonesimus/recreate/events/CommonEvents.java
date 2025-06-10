package su.sergiusonesimus.recreate.events;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import su.sergiusonesimus.recreate.ReCreate;
import su.sergiusonesimus.recreate.content.contraptions.wrench.WrenchItem;
import su.sergiusonesimus.recreate.foundation.utility.ServerSpeedProvider;

public class CommonEvents {

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        if (event.phase == Phase.START) return;
        // TODO
        // ReCreate.SCHEMATIC_RECEIVER.tick();
        // ReCreate.LAGGER.tick();
        ServerSpeedProvider.serverTick();
    }

    // TODO
    // @SubscribeEvent
    // public void onChunkUnloaded(ChunkEvent.Unload event) {
    // CapabilityMinecartController.onChunkUnloaded(event);
    // }

    // TODO
    // @SubscribeEvent
    // public void playerLoggedIn(PlayerLoggedInEvent event) {
    // ToolboxHandler.playerLogin(event.player);
    // }

    // TODO
    // @SubscribeEvent
    // public void whenFluidsMeet(FluidPlaceBlockEvent event) {
    // BlockState blockState = event.getOriginalState();
    // FluidState fluidState = blockState.getFluidState();
    // BlockPos pos = event.getPos();
    // LevelAccessor world = event.getWorld();
    //
    // if (fluidState.isSource() && FluidHelper.isLava(fluidState.getType()))
    // return;
    //
    // for (Direction direction : Iterate.directions) {
    // FluidState metFluidState =
    // fluidState.isSource() ? fluidState : world.getFluidState(pos.relative(direction));
    // if (!metFluidState.is(FluidTags.WATER))
    // continue;
    // BlockState lavaInteraction = AllFluids.getLavaInteraction(metFluidState);
    // if (lavaInteraction == null)
    // continue;
    // event.setNewState(lavaInteraction);
    // break;
    // }
    // }

    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event) {
        if (event.phase == Phase.START) return;
        World world = event.world;
        // TODO
        // CapabilityMinecartController.tick(world);
        // CouplingPhysics.tick(world);
        // LinkedControllerServerHandler.tick(world);
    }

    // TODO
    // @SubscribeEvent
    // public void onUpdateLivingEntity(LivingUpdateEvent event) {
    // EntityLivingBase entityLiving = event.entityLiving;
    // World world = entityLiving.worldObj;
    // if (world == null)
    // return;
    // ContraptionHandler.entitiesWhoJustDismountedGetSentToTheRightLocation(entityLiving, world);
    // ToolboxHandler.entityTick(entityLiving, world);
    // }

    @SubscribeEvent
    public void onEntityAttackedByPlayer(AttackEntityEvent event) {
        WrenchItem.wrenchInstaKillsMinecarts(event);
    }

    // TODO
    // @SubscribeEvent
    // public void registerCommands(RegisterCommandsEvent event) {
    // AllCommands.register(event.getDispatcher());
    // }

    // TODO
    // @SubscribeEvent
    // public void addReloadListeners(AddReloadListenerEvent event) {
    // event.addListener(RecipeFinder.LISTENER);
    // event.addListener(PotionMixingRecipeManager.LISTENER);
    // event.addListener(FluidTransferRecipes.LISTENER);
    // event.addListener(PotatoProjectileTypeManager.ReloadListener.INSTANCE);
    // }

    // TODO
    // @SubscribeEvent
    // public void onDatapackSync(OnDatapackSyncEvent event) {
    // ServerPlayer player = event.getPlayer();
    // if (player != null) {
    // PotatoProjectileTypeManager.syncTo(player);
    // } else {
    // PotatoProjectileTypeManager.syncToAll();
    // }
    // }

    // TODO
    // @SubscribeEvent
    // public void serverStopping(FMLServerStoppingEvent event) {
    // ReCreate.SCHEMATIC_RECEIVER.shutdown();
    // }

    @SubscribeEvent
    public void onLoadWorld(WorldEvent.Load event) {
        IBlockAccess world = event.world;
        // TODO
        // ReCreate.REDSTONE_LINK_NETWORK_HANDLER.onLoadWorld(world);
        ReCreate.TORQUE_PROPAGATOR.onLoadWorld(world);
    }

    @SubscribeEvent
    public void onUnloadWorld(WorldEvent.Unload event) {
        IBlockAccess world = event.world;
        // TODO
        // ReCreate.REDSTONE_LINK_NETWORK_HANDLER.onUnloadWorld(world);
        ReCreate.TORQUE_PROPAGATOR.onUnloadWorld(world);
        // TODO
        // WorldAttached.invalidateWorld(world);
    }

    // TODO
    // @SubscribeEvent
    // public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
    // CapabilityMinecartController.attach(event);
    // }

    // TODO
    // @SubscribeEvent
    // public void startTracking(PlayerEvent.StartTracking event) {
    // CapabilityMinecartController.startTracking(event);
    // }

    // TODO
    // @SubscribeEvent(priority = EventPriority.HIGH)
    // public void onBiomeLoad(BiomeLoadingEvent event) {
    // AllWorldFeatures.reload(event);
    // }

    // TODO
    // public void leftClickEmpty(ServerPlayer player) {
    // ItemStack stack = player.getMainHandItem();
    // if (stack.getItem() instanceof ZapperItem) {
    // ZapperInteractionHandler.trySelect(stack, player);
    // }
    // }

    // TODO
    // @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    // public class ModBusEvents {
    //
    // @SubscribeEvent
    // public void registerCapabilities(RegisterCapabilitiesEvent event) {
    // event.register(CapabilityMinecartController.class);
    // }
    //
    // }

}
