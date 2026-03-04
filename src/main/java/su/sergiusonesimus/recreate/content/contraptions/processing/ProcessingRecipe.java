package su.sergiusonesimus.recreate.content.contraptions.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import su.sergiusonesimus.recreate.ReCreate;

@ParametersAreNonnullByDefault
public abstract class ProcessingRecipe<T extends IInventory> {

    protected String id;
    protected List<ItemStack> ingredients = new ArrayList<ItemStack>();
    protected List<String> oreDictIngredients = new ArrayList<String>();
    protected List<ProcessingOutput> results = new ArrayList<ProcessingOutput>();
    protected List<FluidStack> fluidIngredients = new ArrayList<FluidStack>();
    protected List<FluidStack> fluidResults = new ArrayList<FluidStack>();
    protected int processingDuration = 0;
    protected HeatCondition requiredHeat = HeatCondition.NONE;

    private String type;
    private Supplier<ItemStack> forcedResult = null;

    public ProcessingRecipe(String type, String id) {
        this.type = type;
        this.id = id;
    }

    // Recipe type options:

    protected abstract int getMaxInputCount();

    protected abstract int getMaxOutputCount();

    protected boolean canRequireHeat() {
        return false;
    }

    protected boolean canSpecifyDuration() {
        return true;
    }

    protected int getMaxFluidInputCount() {
        return 0;
    }

    protected int getMaxFluidOutputCount() {
        return 0;
    }

    //

    public void validate() {
        String messageHeader = "Your custom " + type + " recipe (" + id + ")";
        Logger logger = ReCreate.LOGGER;
        int ingredientCount = ingredients.size() + oreDictIngredients.size();
        int outputCount = results.size();

        if (ingredientCount > getMaxInputCount()) logger.warn(
            messageHeader + " has more item inputs ("
                + ingredientCount
                + ") than supported ("
                + getMaxInputCount()
                + ").");

        if (outputCount > getMaxOutputCount()) logger.warn(
            messageHeader + " has more item outputs ("
                + outputCount
                + ") than supported ("
                + getMaxOutputCount()
                + ").");

        if (processingDuration > 0 && !canSpecifyDuration())
            logger.warn(messageHeader + " specified a duration. Durations have no impact on this type of recipe.");

        if (requiredHeat != HeatCondition.NONE && !canRequireHeat()) logger.warn(
            messageHeader + " specified a heat condition. Heat conditions have no impact on this type of recipe.");

        ingredientCount = fluidIngredients.size();
        outputCount = fluidResults.size();

        if (ingredientCount > getMaxFluidInputCount()) logger.warn(
            messageHeader + " has more fluid inputs ("
                + ingredientCount
                + ") than supported ("
                + getMaxFluidInputCount()
                + ").");

        if (outputCount > getMaxFluidOutputCount()) logger.warn(
            messageHeader + " has more fluid outputs ("
                + outputCount
                + ") than supported ("
                + getMaxFluidOutputCount()
                + ").");
    }

    public List<ItemStack> getIngredients() {
        return ingredients;
    }

    public void addIngredient(ItemStack ingredient) {
        ingredients.add(ingredient);
    }

    public List<String> getOreDictIngredients() {
        return oreDictIngredients;
    }

    public void addIngredient(String ingredient) {
        oreDictIngredients.add(ingredient);
    }

    public List<FluidStack> getFluidIngredients() {
        return fluidIngredients;
    }

    public void addFluidIngredient(FluidStack ingredient) {
        fluidIngredients.add(ingredient);
    }

    public List<ProcessingOutput> getRollableResults() {
        return results;
    }

    public void addResult(ProcessingOutput result) {
        results.add(result);
    }

    public void addResult(ProcessingOutput... result) {
        for (ProcessingOutput o : result) results.add(o);
    }

    public void addResult(List<ProcessingOutput> result) {
        result.forEach((o) -> results.add(o));
    }

    public List<FluidStack> getFluidResults() {
        return fluidResults;
    }

    public void addFluidResult(FluidStack result) {
        fluidResults.add(result);
    }

    public List<ItemStack> getRollableResultsAsItemStacks() {
        return getRollableResults().stream()
            .map(ProcessingOutput::getStack)
            .collect(Collectors.toList());
    }

    public void enforceNextResult(Supplier<ItemStack> stack) {
        forcedResult = stack;
    }

    public List<ItemStack> rollResults() {
        List<ItemStack> results = new ArrayList<>();
        List<ProcessingOutput> rollableResults = getRollableResults();
        for (int i = 0; i < rollableResults.size(); i++) {
            ProcessingOutput output = rollableResults.get(i);
            ItemStack stack = i == 0 && forcedResult != null ? forcedResult.get() : output.rollOutput();
            if (stack != null) results.add(stack);
        }
        return results;
    }

    public int getProcessingDuration() {
        return processingDuration;
    }

    public void setProcessingDuration(int duration) {
        processingDuration = duration;
    }

    public HeatCondition getRequiredHeat() {
        return requiredHeat;
    }

    public void getRequiredHeat(HeatCondition heat) {
        requiredHeat = heat;
    }

    public ItemStack assemble(T inv) {
        return getResultItem();
    }

    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public ItemStack getResultItem() {
        return getRollableResults().isEmpty() ? null
            : getRollableResults().get(0)
                .getStack();
    }

    public boolean isSpecial() {
        return true;
    }

    // Additional Data added by subtypes

    public void readAdditional(ByteBuf buffer) {}

    public void writeAdditional(ByteBuf buffer) {}

    public boolean matches(T inv, World worldIn) {
        return true;
    }

}
