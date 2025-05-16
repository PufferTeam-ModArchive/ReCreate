package su.sergiusonesimus.recreate.content.contraptions.base;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;

import su.sergiusonesimus.recreate.content.contraptions.wrench.IWrenchable;
import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.util.Direction;

public interface IRotate extends IWrenchable {

    public enum SpeedLevel {

        NONE,
        MEDIUM,
        FAST;

        public EnumChatFormatting getTextColor() {
            return this == NONE ? EnumChatFormatting.GREEN
                : this == MEDIUM ? EnumChatFormatting.AQUA : EnumChatFormatting.LIGHT_PURPLE;
        }

        public int getColor() {
            return this == NONE ? 0x22FF22 : this == MEDIUM ? 0x0084FF : 0xFF55FF;
        }

        public int getParticleSpeed() {
            return this == NONE ? 10 : this == MEDIUM ? 20 : 30;
        }

        public static SpeedLevel of(float speed) {
            speed = Math.abs(speed);

            if (speed >= AllConfigs.SERVER.kinetics.fastSpeed) {
                return FAST;
            } else if (speed >= AllConfigs.SERVER.kinetics.mediumSpeed) {
                return MEDIUM;
            }
            return NONE;
        }

        public float getSpeedValue() {
            switch (this) {
                case FAST:
                    return AllConfigs.SERVER.kinetics.fastSpeed;
                case MEDIUM:
                    return AllConfigs.SERVER.kinetics.mediumSpeed;
                case NONE:
                default:
                    return 0;
            }
        }

        // public static Component getFormattedSpeedText(float speed, boolean overstressed){
        // SpeedLevel speedLevel = of(speed);
        //
        // MutableComponent level = new TextComponent(ItemDescription.makeProgressBar(3, speedLevel.ordinal()));
        //
        // if (speedLevel == SpeedLevel.MEDIUM)
        // level.append(Lang.translate("tooltip.speedRequirement.medium"));
        // if (speedLevel == SpeedLevel.FAST)
        // level.append(Lang.translate("tooltip.speedRequirement.high"));
        //
        // level.append(" (" +
        // IHaveGoggleInformation.format(Math.abs(speed))).append(Lang.translate("generic.unit.rpm")).append(") ");
        //
        // if (overstressed)
        // level.withStyle(EnumChatFormatting.DARK_GRAY, EnumChatFormatting.STRIKETHROUGH);
        // else
        // level.withStyle(speedLevel.getTextColor());
        //
        // return level;
        // }

    }

    enum StressImpact {

        LOW,
        MEDIUM,
        HIGH,
        OVERSTRESSED;

        public EnumChatFormatting getAbsoluteColor() {
            return this == LOW ? EnumChatFormatting.YELLOW
                : this == MEDIUM ? EnumChatFormatting.GOLD : EnumChatFormatting.RED;
        }

        public EnumChatFormatting getRelativeColor() {
            return this == LOW ? EnumChatFormatting.GREEN
                : this == MEDIUM ? EnumChatFormatting.YELLOW
                    : this == HIGH ? EnumChatFormatting.GOLD : EnumChatFormatting.RED;
        }

        public static StressImpact of(double stressPercent) {
            if (stressPercent > 1) return StressImpact.OVERSTRESSED;
            else if (stressPercent > .75d) return StressImpact.HIGH;
            else if (stressPercent > .5d) return StressImpact.MEDIUM;
            else return StressImpact.LOW;
        }

        public static boolean isEnabled() {
            return !AllConfigs.SERVER.kinetics.disableStress;
        }

        // public static Component getFormattedStressText(double stressPercent){
        // StressImpact stressLevel = of(stressPercent);
        // EnumChatFormatting color = stressLevel.getRelativeColor();
        //
        // MutableComponent level = new TextComponent(ItemDescription.makeProgressBar(3, Math.min(stressLevel.ordinal(),
        // 2)));
        // level.append(Lang.translate("tooltip.stressImpact." + Lang.asId(stressLevel.name())));
        //
        // level.append(String.format(" (%s%%) ", (int) (stressPercent * 100)));
        //
        // return level.withStyle(color);
        // }
    }
    
	/**
	 * Checks if a block on given coordinates has a shaft towards given direction.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param face
	 * @return
	 */
    public boolean hasShaftTowards(IBlockAccess world, int x, int y, int z, Direction face);

    public default SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.NONE;
    }

    public default boolean hideStressImpact() {
        return false;
    }

    public default boolean showCapacityWithAnnotation() {
        return false;
    }

}
