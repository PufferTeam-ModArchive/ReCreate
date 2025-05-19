package su.sergiusonesimus.recreate.content.contraptions.relays.elementary;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import su.sergiusonesimus.recreate.foundation.achievement.ITriggerable;
import su.sergiusonesimus.recreate.foundation.tileentity.SmartTileEntity;
import su.sergiusonesimus.recreate.foundation.tileentity.TileEntityBehaviour;
import su.sergiusonesimus.recreate.foundation.tileentity.behaviour.BehaviourType;

public class BracketedTileEntityBehaviour extends TileEntityBehaviour {

    public static final BehaviourType<BracketedTileEntityBehaviour> TYPE = new BehaviourType<>();

    private Optional<Block> bracket;
    private Optional<Integer> bracketMeta;
    private boolean reRender;

    private Predicate<Block> pred;
    private Function<Block, ITriggerable> trigger;

    public BracketedTileEntityBehaviour(SmartTileEntity te) {
        this(te, state -> true);
    }

    public BracketedTileEntityBehaviour(SmartTileEntity te, Predicate<Block> pred) {
        super(te);
        this.pred = pred;
        bracket = Optional.empty();
    }

    public BracketedTileEntityBehaviour withTrigger(Function<Block, ITriggerable> trigger) {
        this.trigger = trigger;
        return this;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public void applyBracket(Block state, int meta) {
        this.bracket = Optional.of(state);
        this.bracketMeta = Optional.of(meta);
        reRender = true;
        tileEntity.notifyUpdate();
    }

    // TODO
    // public void triggerAdvancements(World world, Player player, BlockState state) {
    // if (trigger == null)
    // return;
    // AllTriggers.triggerFor(trigger.apply(state), player);
    // }

    public void removeBracket(boolean inOnReplacedContext) {
        World world = getWorld();
        if (!world.isRemote)
            world.playAuxSFX(2001, getPosX(), getPosY(), getPosZ(), Block.getIdFromBlock(getBracket()));
        this.bracket = Optional.empty();
        reRender = true;
        if (inOnReplacedContext) tileEntity.sendData();
        else tileEntity.notifyUpdate();
    }

    public boolean isBracketPresent() {
        return getBracket() != Blocks.air;
    }

    public Block getBracket() {
        return bracket.orElse(Blocks.air);
    }

    public Integer getBracketMeta() {
        return bracketMeta.orElse(0);
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }

    @Override
    public void write(NBTTagCompound nbt, boolean clientPacket) {
        bracket.ifPresent(p -> {
            NBTTagCompound bracket = new NBTTagCompound();
            bracket.setInteger("id", Block.getIdFromBlock(p));
            bracket.setInteger("meta", bracketMeta.orElse(0));
            nbt.setTag("Bracket", bracket);
        });
        if (clientPacket && reRender) {
            nbt.setBoolean("Redraw", true);
            reRender = false;
        }
        super.write(nbt, clientPacket);
    }

    @Override
    public void read(NBTTagCompound nbt, boolean clientPacket) {
        bracket = Optional.empty();
        if (nbt.hasKey("Bracket")) {
            NBTTagCompound bracket = nbt.getCompoundTag("Bracket");
            this.bracket = Optional.of(Block.getBlockById(bracket.getInteger("id")));
            this.bracketMeta = Optional.of(bracket.getInteger("meta"));
        }
        if (clientPacket && nbt.hasKey("Redraw")) getWorld().markBlockForUpdate(getPosX(), getPosY(), getPosZ());
        super.read(nbt, clientPacket);
    }

    public boolean canHaveBracket() {
        return pred.test(tileEntity.blockType);
    }

}
