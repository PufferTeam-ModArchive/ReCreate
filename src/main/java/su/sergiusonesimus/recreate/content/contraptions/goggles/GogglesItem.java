package su.sergiusonesimus.recreate.content.contraptions.goggles;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.recreate.AllItems;
import su.sergiusonesimus.recreate.ReCreate;

public class GogglesItem extends ItemArmor {

    public GogglesItem(ArmorMaterial material, int renderId) {
        super(material, renderId, 0);
    }

    public static boolean canSeeParticles(EntityPlayer player) {
        ItemStack helmet = player.inventory.armorInventory[0];
        return helmet != null && helmet.getItem() == AllItems.goggles;
    }

    @Override
    public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, String layer) {
        return "recreate:textures/models/armor/goggles.png";
    }

    /**
     * Properly register icon source
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(
            ReCreate.ID + ":"
                + this.getUnlocalizedName()
                    .substring(5));
    }

    /**
     * Override this method to have an item handle its own armor rendering.
     * 
     * @param entityLiving The entity wearing the armor
     * @param itemStack    The itemStack to render the model of
     * @param armorSlot    0=head, 1=torso, 2=legs, 3=feet
     * 
     * @return A ModelBiped to render instead of the default
     */
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        return ReCreate.proxy.getGogglesArmorModel();
    }

}
