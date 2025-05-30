package su.sergiusonesimus.recreate.content.contraptions.wrench;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.recreate.AllItems;
import su.sergiusonesimus.recreate.AllSounds;
import su.sergiusonesimus.recreate.AllTags;
import su.sergiusonesimus.recreate.ReCreate;

public class WrenchItem extends Item {

    public WrenchItem() {
        this.maxStackSize = 1;
        this.setCreativeTab(AllItems.BASE_CREATIVE_TAB);
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    @Nonnull
    @Override
    public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (player == null || player.isRiding() || player.isPlayerSleeping())
            return super.onItemUse(item, player, world, x, y, z, side, hitX, hitY, hitZ);

        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        if (!(block instanceof IWrenchable)) {
            if (canWrenchPickup(block, meta))
                return onItemUseOnOther(item, player, world, block, meta, x, y, z, side, hitX, hitY, hitZ);
            return super.onItemUse(item, player, world, x, y, z, side, hitX, hitY, hitZ);
        }

        IWrenchable actor = (IWrenchable) block;
        if (player.isSneaking()) return actor.onSneakWrenched(world, x, y, z, side, player);
        return actor.onWrenched(world, x, y, z, side, player);
    }

    private boolean canWrenchPickup(Block block, int meta) {
        return AllTags.AllBlockTags.WRENCH_PICKUP.matches(block, meta);
    }

    private boolean onItemUseOnOther(ItemStack item, EntityPlayer player, World world, Block block, int meta, int x,
        int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!(world instanceof WorldServer)) return true;
        if (player != null && !player.capabilities.isCreativeMode) block.getDrops(world, x, y, z, meta, 0)
            .forEach(itemStack -> player.inventory.addItemStackToInventory(itemStack));
        world.setBlockToAir(x, y, z);
        AllSounds.WRENCH_REMOVE.playOnServer(world, x, y, z, 1, ReCreate.RANDOM.nextFloat() * .5f + .5f);
        return true;
    }

    public static void wrenchInstaKillsMinecarts(AttackEntityEvent event) {
        Entity target = event.target;
        if (!(target instanceof EntityMinecart)) return;
        EntityPlayer player = event.entityPlayer;
        ItemStack heldItem = player.getHeldItem();
        if (heldItem == null || heldItem.getItem() != AllItems.wrench) return;
        if (player.capabilities.isCreativeMode) return;
        EntityMinecart minecart = (EntityMinecart) target;
        minecart.attackEntityFrom(DamageSource.causePlayerDamage(player), 100);
    }

}
