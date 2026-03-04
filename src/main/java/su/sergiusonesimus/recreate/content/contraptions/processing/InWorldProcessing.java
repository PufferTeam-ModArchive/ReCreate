package su.sergiusonesimus.recreate.content.contraptions.processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import su.sergiusonesimus.recreate.AllRecipeTypes;
import su.sergiusonesimus.recreate.content.contraptions.fan.SplashingRecipe;
import su.sergiusonesimus.recreate.content.contraptions.relays.belt.TransportedItemStackHandlerBehaviour.TransportedResult;
import su.sergiusonesimus.recreate.content.contraptions.relays.belt.transport.TransportedItemStack;
import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.util.ItemHandlerHelper;

public class InWorldProcessing {

    private static final SplashingWrapper SPLASHING_WRAPPER = new SplashingWrapper();

    public enum Type {

        SMOKING,
        BLASTING,
        SPLASHING,
        NONE

        ;

        public static Type byBlock(IBlockAccess reader, int posX, int posY, int posZ) {
            Block block = reader.getBlock(posX, posY, posZ);
            if (block == Blocks.water || block == Blocks.flowing_water) return Type.SPLASHING;
            if (block == Blocks.fire /*
                                      * TODO || AllBlocks.LIT_BLAZE_BURNER.has(blockState)
                                      * || (BlockTags.CAMPFIRES.contains(block) &&
                                      * blockState.getOptionalValue(CampfireBlock.LIT)
                                      * .orElse(false))
                                      * || getHeatLevelOf(blockState) == BlazeBurnerBlock.HeatLevel.SMOULDERING
                                      */) return Type.SMOKING;
            if (block == Blocks.lava || block == Blocks.flowing_lava /*
                                                                      * TODO || getHeatLevelOf(blockState).isAtLeast(
                                                                      * BlazeBurnerBlock.HeatLevel.FADING)
                                                                      */) return Type.BLASTING;
            return Type.NONE;
        }
    }

    public static boolean canProcess(EntityItem entity, Type type) {
        if (entity.getEntityData()
            .hasKey("CreateData")) {
            NBTTagCompound compound = entity.getEntityData()
                .getCompoundTag("CreateData");
            if (compound.hasKey("Processing")) {
                NBTTagCompound processing = compound.getCompoundTag("Processing");

                if (Type.valueOf(processing.getString("Type")) != type) {
                    boolean canProcess = canProcess(entity.getEntityItem(), type, entity.worldObj);
                    processing.setString("Type", type.name());
                    if (!canProcess) processing.setInteger("Time", -1);
                    return canProcess;
                } else if (processing.getInteger("Time") >= 0) return true;
                else if (processing.getInteger("Time") == -1) return false;
            }
        }
        return canProcess(entity.getEntityItem(), type, entity.worldObj);
    }

    private static boolean canProcess(ItemStack stack, Type type, World world) {
        if (type == Type.BLASTING) return true; // TODO May have to edit this later for items that don't burn (Item
                                                // Physics Mod integration perhaps?)

        if (type == Type.SMOKING) return FurnaceRecipes.smelting()
            .getSmeltingResult(stack)
            .getItem()
            .getItemUseAction(stack) == EnumAction.eat;

        if (type == Type.SPLASHING) return isWashable(stack, world);

        return false;
    }

    public static boolean isWashable(ItemStack stack, World world) {
        SPLASHING_WRAPPER.setInventorySlotContents(0, stack);
        Optional<SplashingRecipe> recipe = AllRecipeTypes.SPLASHING.find(SPLASHING_WRAPPER, world);
        return recipe.isPresent();
    }

    public static void applyProcessing(EntityItem entity, Type type) {
        if (decrementProcessingTime(entity, type) != 0) return;
        List<ItemStack> stacks = process(entity.getEntityItem(), type, entity.worldObj);
        if (stacks == null) return;
        if (stacks.isEmpty()) {
            entity.setDead();
            return;
        }
        entity.setEntityItemStack(stacks.remove(0));
        for (ItemStack additional : stacks) {
            EntityItem entityIn = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, additional);
            entityIn.motionX = entity.motionX;
            entityIn.motionY = entity.motionY;
            entityIn.motionZ = entity.motionZ;
            entity.worldObj.spawnEntityInWorld(entityIn);
        }
    }

    @SuppressWarnings("static-access")
    public static TransportedResult applyProcessing(TransportedItemStack transported, World world, Type type) {
        TransportedResult ignore = TransportedResult.doNothing();
        if (transported.processedBy != type) {
            transported.processedBy = type;
            int timeModifierForStackSize = ((transported.stack.stackSize - 1) / 16) + 1;
            int processingTime = (int) (AllConfigs.SERVER.kinetics.inWorldProcessingTime * timeModifierForStackSize)
                + 1;
            transported.processingTime = processingTime;
            if (!canProcess(transported.stack, type, world)) transported.processingTime = -1;
            return ignore;
        }
        if (transported.processingTime == -1) return ignore;
        if (transported.processingTime-- > 0) return ignore;

        List<ItemStack> stacks = process(transported.stack, type, world);
        if (stacks == null) return ignore;

        List<TransportedItemStack> transportedStacks = new ArrayList<>();
        for (ItemStack additional : stacks) {
            TransportedItemStack newTransported = transported.getSimilar();
            newTransported.stack = additional.copy();
            transportedStacks.add(newTransported);
        }
        return TransportedResult.convertTo(transportedStacks);
    }

    private static List<ItemStack> process(ItemStack stack, Type type, World world) {
        if (type == Type.SPLASHING) {
            SPLASHING_WRAPPER.setInventorySlotContents(0, stack);
            Optional<SplashingRecipe> recipe = AllRecipeTypes.SPLASHING.find(SPLASHING_WRAPPER, world);
            if (recipe.isPresent()) return applyRecipeOn(stack, recipe.get());
            return null;
        }

        ItemStack smeltingResult = FurnaceRecipes.smelting()
            .getSmeltingResult(stack);
        boolean smokingRecipe = smeltingResult != null && smeltingResult.getItem()
            .getItemUseAction(smeltingResult) == EnumAction.eat;

        List<ItemStack> result = new ArrayList<ItemStack>();
        if (type == Type.BLASTING) {
            if (!smokingRecipe && smeltingResult != null) {
                result.add(smeltingResult);
                return result;
            }

            return Collections.emptyList();
        }

        if (type == Type.SMOKING && smokingRecipe) {
            result.add(smeltingResult);
            return result;
        }

        return null;
    }

    @SuppressWarnings("static-access")
    private static int decrementProcessingTime(EntityItem entity, Type type) {
        NBTTagCompound nbt = entity.getEntityData();

        if (!nbt.hasKey("CreateData")) nbt.setTag("CreateData", new NBTTagCompound());
        NBTTagCompound createData = nbt.getCompoundTag("CreateData");

        if (!createData.hasKey("Processing")) createData.setTag("Processing", new NBTTagCompound());
        NBTTagCompound processing = createData.getCompoundTag("Processing");

        if (!processing.hasKey("Type") || Type.valueOf(processing.getString("Type")) != type) {
            processing.setString("Type", type.name());
            int timeModifierForStackSize = ((entity.getEntityItem().stackSize - 1) / 16) + 1;
            int processingTime = (int) (AllConfigs.SERVER.kinetics.inWorldProcessingTime * timeModifierForStackSize)
                + 1;
            processing.setInteger("Time", processingTime);
        }

        int value = processing.getInteger("Time") - 1;
        processing.setInteger("Time", value);
        return value;
    }

    public static void applyRecipeOn(EntityItem entity, ProcessingRecipe<?> recipe) {
        List<ItemStack> stacks = applyRecipeOn(entity.getEntityItem(), recipe);
        if (stacks == null) return;
        if (stacks.isEmpty()) {
            entity.setDead();
            return;
        }
        entity.setEntityItemStack(stacks.remove(0));
        for (ItemStack additional : stacks) {
            EntityItem entityIn = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, additional);
            entityIn.motionX = entity.motionX;
            entityIn.motionY = entity.motionY;
            entityIn.motionZ = entity.motionZ;
            entity.worldObj.spawnEntityInWorld(entityIn);
        }
    }

    public static List<ItemStack> applyRecipeOn(ItemStack stackIn, ProcessingRecipe<?> recipe) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < stackIn.stackSize; i++) {
            List<ItemStack> rollResults = ((ProcessingRecipe<?>) recipe).rollResults();
            for (ItemStack stack : rollResults) {
                for (ItemStack previouslyRolled : stacks) {
                    if (stack == null) continue;
                    if (!ItemHandlerHelper.canItemStacksStack(stack, previouslyRolled)) continue;
                    int amount = Math
                        .min(previouslyRolled.getMaxStackSize() - previouslyRolled.stackSize, stack.stackSize);
                    previouslyRolled.stackSize += amount;
                    stack.stackSize -= amount;
                }

                if (stack.stackSize == 0) continue;

                stacks.add(stack);
            }
        }

        return stacks;
    }

    public static void spawnParticlesForProcessing(@Nullable World world, Vec3 vec, Type type) {
        if (world == null || !world.isRemote) return;
        if (world.rand.nextInt(8) != 0) return;

        switch (type) {
            case BLASTING:
                world.spawnParticle("largesmoke", vec.xCoord, vec.yCoord + .25f, vec.zCoord, 0, 1 / 16f, 0);
                break;
            case SMOKING:
                world.spawnParticle("explode", vec.xCoord, vec.yCoord + .25f, vec.zCoord, 0, 1 / 16f, 0);
                break;
            case SPLASHING:
                world.spawnParticle(
                    "reddust",
                    vec.xCoord + (world.rand.nextFloat() - .5f) * .5f,
                    vec.yCoord + .5f,
                    vec.zCoord + (world.rand.nextFloat() - .5f) * .5f,
                    0,
                    85 / 256F,
                    1F);
                world.spawnParticle(
                    "dripWater",
                    vec.xCoord + (world.rand.nextFloat() - .5f) * .5f,
                    vec.yCoord + .5f,
                    vec.zCoord + (world.rand.nextFloat() - .5f) * .5f,
                    0,
                    1 / 8f,
                    0);
                break;
            default:
                break;
        }
    }

    public static class SplashingWrapper extends InventoryBasic {

        public SplashingWrapper() {
            super("", false, 1);
        }
    }

}
