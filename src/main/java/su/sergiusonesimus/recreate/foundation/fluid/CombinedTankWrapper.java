package su.sergiusonesimus.recreate.foundation.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * Combines multiple IFluidHandlers into one interface (See CombinedInvWrapper
 * for items)
 */
public class CombinedTankWrapper implements IFluidHandler {

    protected final IFluidHandler[] itemHandler;
    protected final FluidTankInfo[] allTanks;
    protected final Map<FluidTankInfo, ForgeDirection> tanksWithDirection;
    protected final int[] baseIndex;
    protected final int tankCount;
    protected boolean enforceVariety;

    public CombinedTankWrapper(IFluidHandler... fluidHandlers) {
        this.itemHandler = fluidHandlers;
        this.baseIndex = new int[fluidHandlers.length];
        this.tanksWithDirection = new HashMap<FluidTankInfo, ForgeDirection>();
        int index = 0;
        for (int i = 0; i < fluidHandlers.length; i++) {
            List<FluidTankInfo> tanks = new ArrayList<FluidTankInfo>();
            for (ForgeDirection direction : ForgeDirection.values()) {
                FluidTankInfo[] sideTanks = fluidHandlers[i].getTankInfo(direction);
                for (FluidTankInfo sideTank : sideTanks) if (!tanks.contains(sideTank)) {
                    tanks.add(sideTank);
                    tanksWithDirection.put(sideTank, direction);
                }
            }
            index += tanks.size();
            baseIndex[i] = index;
        }
        this.tankCount = index;
        this.allTanks = new FluidTankInfo[index];
        Object[] tanksObject = tanksWithDirection.keySet()
            .toArray();
        for (int i = 0; i < index; i++) this.allTanks[i] = (FluidTankInfo) tanksObject[i];
    }

    public CombinedTankWrapper enforceVariety() {
        enforceVariety = true;
        return this;
    }

    public int getTanks() {
        return tankCount;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount == 0) return 0;

        int filled = 0;
        int amountToFill = resource.amount;

        for (int i = 0; i < tankCount; i++) {
            FluidTankInfo tank = allTanks[i];
            FluidStack fluid = tank.fluid;
            if (fluid == null || fluid.equals(resource)) {
                IFluidHandler handler = getHandlerFromIndex(getIndexForSlot(i));
                filled += handler.fill(tanksWithDirection.get(tank), resource, doFill);
            }
            if (filled >= amountToFill) return amountToFill;
        }

        return filled;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount == 0) return resource;

        int drained = 0;
        int toDrain = resource.amount;

        for (int i = 0; i < tankCount; i++) {
            FluidTankInfo tank = allTanks[i];
            FluidStack fluid = tank.fluid;
            if (fluid != null && fluid.equals(resource)) {
                IFluidHandler handler = getHandlerFromIndex(getIndexForSlot(i));
                FluidStack drainedStack = handler.drain(tanksWithDirection.get(tank), resource, doDrain);
                drained += drainedStack.amount;
            }
            if (drained >= toDrain) {
                drained = toDrain;
                break;
            }
        }

        resource.amount = drained;
        return resource;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (maxDrain == 0) return null;

        for (int i = 0; i < tankCount; i++) {
            FluidTankInfo tank = allTanks[i];
            FluidStack fluid = tank.fluid;
            if (fluid != null) return drain(from, new FluidStack(fluid.getFluid(), maxDrain), doDrain);
        }
        return null;
    }

    protected int getIndexForSlot(int slot) {
        if (slot < 0) return -1;
        for (int i = 0; i < baseIndex.length; i++) if (slot - baseIndex[i] < 0) return i;
        return -1;
    }

    protected IFluidHandler getHandlerFromIndex(int index) {
        if (index < 0 || index >= itemHandler.length) return null;
        return itemHandler[index];
    }

    protected int getSlotFromIndex(int slot, int index) {
        if (index <= 0 || index >= baseIndex.length) return slot;
        return slot - baseIndex[index - 1];
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return allTanks;
    }
}
