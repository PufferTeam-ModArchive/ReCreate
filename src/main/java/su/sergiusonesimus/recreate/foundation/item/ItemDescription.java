package su.sergiusonesimus.recreate.foundation.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import static net.minecraft.util.EnumChatFormatting.AQUA;
import static net.minecraft.util.EnumChatFormatting.BLUE;
import static net.minecraft.util.EnumChatFormatting.DARK_GRAY;
import static net.minecraft.util.EnumChatFormatting.DARK_GREEN;
import static net.minecraft.util.EnumChatFormatting.DARK_PURPLE;
import static net.minecraft.util.EnumChatFormatting.DARK_RED;
import static net.minecraft.util.EnumChatFormatting.GOLD;
import static net.minecraft.util.EnumChatFormatting.GRAY;
import static net.minecraft.util.EnumChatFormatting.GREEN;
import static net.minecraft.util.EnumChatFormatting.LIGHT_PURPLE;
import static net.minecraft.util.EnumChatFormatting.RED;
import static net.minecraft.util.EnumChatFormatting.STRIKETHROUGH;
import static net.minecraft.util.EnumChatFormatting.WHITE;
import static net.minecraft.util.EnumChatFormatting.YELLOW;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import su.sergiusonesimus.recreate.content.contraptions.base.IRotate;
import su.sergiusonesimus.recreate.content.contraptions.base.IRotate.SpeedLevel;
import su.sergiusonesimus.recreate.content.contraptions.base.IRotate.StressImpact;
import su.sergiusonesimus.recreate.foundation.block.BlockStressValues;
import su.sergiusonesimus.recreate.foundation.config.AllConfigs;
import su.sergiusonesimus.recreate.foundation.config.CKinetics;
import su.sergiusonesimus.recreate.foundation.utility.Lang;
import su.sergiusonesimus.recreate.util.TextHelper;


public class ItemDescription {

	public static final ItemDescription MISSING = new ItemDescription(null);
	public static IChatComponent trim = new ChatComponentText("                          ")
			.setChatStyle(new ChatStyle().setColor(WHITE).setStrikethrough(true));

	public enum Palette {

		Blue(BLUE, AQUA),
		Green(DARK_GREEN, GREEN),
		Yellow(GOLD, YELLOW),
		Red(DARK_RED, RED),
		Purple(DARK_PURPLE, LIGHT_PURPLE),
		Gray(DARK_GRAY, GRAY),

		;

		private Palette(EnumChatFormatting primary, EnumChatFormatting highlight) {
			color = primary;
			hColor = highlight;
		}

		public EnumChatFormatting color;
		public EnumChatFormatting hColor;
	}

	private List<IChatComponent> lines;
	private List<IChatComponent> linesOnShift;
	private List<IChatComponent> linesOnCtrl;
	private Palette palette;

	public ItemDescription(Palette palette) {
		this.palette = palette;
		lines = new ArrayList<>();
		linesOnShift = new ArrayList<>();
		linesOnCtrl = new ArrayList<>();
	}

	public ItemDescription withSummary(IChatComponent summary) {
		addStrings(linesOnShift, TooltipHelper.cutTextComponent(summary, palette.color, palette.hColor));
		return this;
	}

	public static List<IChatComponent> getKineticStats(Block block) {
		List<IChatComponent> list = new ArrayList<>();

		CKinetics config = AllConfigs.SERVER.kinetics;
		IChatComponent rpmUnit = Lang.translate("generic.unit.rpm");

		//TODO
//		boolean hasGoggles = AllItems.GOGGLES.isIn(Minecraft.getMinecraft().thePlayer.getEquipmentInSlot(0));

		SpeedLevel minimumRequiredSpeedLevel;
		boolean showStressImpact;
		if (!(block instanceof IRotate)) {
			minimumRequiredSpeedLevel = SpeedLevel.NONE;
			showStressImpact = true;
		} else {
			minimumRequiredSpeedLevel = ((IRotate) block).getMinimumRequiredSpeedLevel();
			showStressImpact = !((IRotate) block).hideStressImpact();
		}

		boolean hasSpeedRequirement = minimumRequiredSpeedLevel != SpeedLevel.NONE;
		boolean hasStressImpact =
			StressImpact.isEnabled() && showStressImpact && BlockStressValues.getImpact(block) > 0;
		boolean hasStressCapacity = StressImpact.isEnabled() && BlockStressValues.hasCapacity(block);

		if (hasSpeedRequirement) {
			List<IChatComponent> speedLevels = Lang.translatedOptions("tooltip.speedRequirement", "none", "medium", "high");
			int index = minimumRequiredSpeedLevel.ordinal();
			IChatComponent level = new ChatComponentText(makeProgressBar(3, index));
			level.getChatStyle().setColor(minimumRequiredSpeedLevel.getTextColor());

			//TODO
//			if (hasGoggles)
//				level.appendText(String.valueOf(minimumRequiredSpeedLevel.getSpeedValue()))
//					.appendSibling(rpmUnit)
//					.appendText("+");
//			else
				level.appendSibling(speedLevels.get(index));

			IChatComponent speedRequirement = Lang.translate("tooltip.speedRequirement");
			speedRequirement.getChatStyle().setColor(GRAY);
			list.add(speedRequirement);
			list.add(level);
		}

		if (hasStressImpact) {
			List<IChatComponent> stressLevels = Lang.translatedOptions("tooltip.stressImpact", "low", "medium", "high");
			double impact = BlockStressValues.getImpact(block);
			StressImpact impactId = impact >= config.highStressImpact ? StressImpact.HIGH
				: (impact >= config.mediumStressImpact ? StressImpact.MEDIUM : StressImpact.LOW);
			int index = impactId.ordinal();
			IChatComponent level =
				new ChatComponentText(makeProgressBar(3, index));
			level.getChatStyle().setColor(impactId.getAbsoluteColor());

			//TODO
//			if (hasGoggles)
//				level.appendText(impact + "x ")
//					.appendSibling(rpmUnit);
//			else
				level.appendSibling(stressLevels.get(index));

			IChatComponent stressImpact = Lang.translate("tooltip.stressImpact");
			stressImpact.getChatStyle().setColor(GRAY);
			list.add(stressImpact);
			list.add(level);
		}

		if (hasStressCapacity) {
			List<IChatComponent> stressCapacityLevels =
				Lang.translatedOptions("tooltip.capacityProvided", "low", "medium", "high");
			double capacity = BlockStressValues.getCapacity(block);
			StressImpact impactId = capacity >= config.highCapacity ? StressImpact.LOW
				: (capacity >= config.mediumCapacity ? StressImpact.MEDIUM : StressImpact.HIGH);
			int index = StressImpact.values().length - 2 - impactId.ordinal();
			IChatComponent level = new ChatComponentText(makeProgressBar(3, index));
			level.getChatStyle().setColor(impactId.getAbsoluteColor());

			//TODO
//			if (hasGoggles)
//				level.appendText(capacity + "x ")
//					.appendSibling(rpmUnit);
//			else
				level.appendSibling(stressCapacityLevels.get(index));

			IChatComponent capacityProvided = Lang.translate("tooltip.capacityProvided");
			capacityProvided.getChatStyle().setColor(GRAY);
			list.add(capacityProvided);
			list.add(level);

			IChatComponent genSpeed = generatorSpeed(block, rpmUnit);
			if (!genSpeed.getFormattedText()
				.isEmpty()) {
				IChatComponent appendedComponent = new ChatComponentText(" ").appendSibling(genSpeed);
				appendedComponent.getChatStyle().setColor(DARK_GRAY);
				list.add(appendedComponent);
			}
		}
		return list;
	}

	public static String makeProgressBar(int length, int filledLength) {
		String bar = " ";
		int emptySpaces = length - 1 - filledLength;
		for (int i = 0; i <= filledLength; i++)
			bar += "\u2588";
		for (int i = 0; i < emptySpaces; i++)
			bar += "\u2592";
		return bar + " ";
	}

	public ItemDescription withBehaviour(String condition, String behaviour) {
		IChatComponent conditionComponent = new ChatComponentText(condition);
		conditionComponent.getChatStyle().setColor(GRAY);
		add(linesOnShift, conditionComponent);
		addStrings(linesOnShift, TooltipHelper.cutStringTextComponent(behaviour, palette.color, palette.hColor, 1));
		return this;
	}

	public ItemDescription withControl(String condition, String action) {
		IChatComponent conditionComponent = new ChatComponentText(condition);
		conditionComponent.getChatStyle().setColor(GRAY);
		add(linesOnCtrl, conditionComponent);
		addStrings(linesOnCtrl, TooltipHelper.cutStringTextComponent(action, palette.color, palette.hColor, 1));
		return this;
	}

	public ItemDescription createTabs() {
		boolean hasDescription = !linesOnShift.isEmpty();
		boolean hasControls = !linesOnCtrl.isEmpty();

		if (hasDescription || hasControls) {
			String[] holdDesc = Lang.translate("tooltip.holdForDescription", "$").getFormattedText().split("\\$");
			String[] holdCtrl = Lang.translate("tooltip.holdForControls", "$").getFormattedText().split("\\$");
			IChatComponent keyShift = Lang.translate("tooltip.keyShift");
			IChatComponent keyCtrl = Lang.translate("tooltip.keyCtrl");
			for (List<IChatComponent> list : Arrays.asList(lines, linesOnShift, linesOnCtrl)) {
				boolean shift = list == linesOnShift;
				boolean ctrl = list == linesOnCtrl;

				if (holdDesc.length != 2 || holdCtrl.length != 2) {
					list.add(0, new ChatComponentText("Invalid lang formatting!"));
					continue;
				}

				if (hasControls) {
					IChatComponent tabBuilder = new ChatComponentText("");
					IChatComponent appender = new ChatComponentText(holdCtrl[0]);
					appender.getChatStyle().setColor(DARK_GRAY);
					tabBuilder.appendSibling(appender);
					appender = TextHelper.plainCopy(keyCtrl);
					appender.getChatStyle().setColor(ctrl ? WHITE : GRAY);
					tabBuilder.appendSibling(appender);
					appender = new ChatComponentText(holdCtrl[1]);
					appender.getChatStyle().setColor(DARK_GRAY);
					tabBuilder.appendSibling(appender);
					list.add(0, tabBuilder);
				}

				if (hasDescription) {
					IChatComponent tabBuilder = new ChatComponentText("");
					IChatComponent appender = new ChatComponentText(holdDesc[0]);
					appender.getChatStyle().setColor(DARK_GRAY);
					tabBuilder.appendSibling(appender);
					appender = TextHelper.plainCopy(keyShift);
					appender.getChatStyle().setColor(ctrl ? WHITE : GRAY);
					tabBuilder.appendSibling(appender);
					appender = new ChatComponentText(holdDesc[1]);
					appender.getChatStyle().setColor(DARK_GRAY);
					tabBuilder.appendSibling(appender);
					list.add(0, tabBuilder);
				}

				if (shift || ctrl)
					list.add(hasDescription && hasControls ? 2 : 1, new ChatComponentText(""));
			}
		}

		if (!hasDescription)
			linesOnShift = lines;
		if (!hasControls)
			linesOnCtrl = lines;

		return this;
	}

	public static String hightlight(String s, Palette palette) {
		return palette.hColor + s + palette.color;
	}

	public static void addStrings(List<IChatComponent> infoList, List<IChatComponent> textLines) {
		textLines.forEach(s -> add(infoList, s));
	}

	public static void add(List<IChatComponent> infoList, List<IChatComponent> textLines) {
		infoList.addAll(textLines);
	}

	public static void add(List<IChatComponent> infoList, IChatComponent line) {
		infoList.add(line);
	}

	public Palette getPalette() {
		return palette;
	}

	public List<IChatComponent> addInformation(List<IChatComponent> tooltip) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			tooltip.addAll(linesOnShift);
			return tooltip;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
			tooltip.addAll(linesOnCtrl);
			return tooltip;
		}

		tooltip.addAll(lines);
		return tooltip;
	}

	public List<IChatComponent> getLines() {
		return lines;
	}

	public List<IChatComponent> getLinesOnCtrl() {
		return linesOnCtrl;
	}

	public List<IChatComponent> getLinesOnShift() {
		return linesOnShift;
	}

	private static IChatComponent generatorSpeed(Block block, IChatComponent unitRPM) {
		String value = "";

		//TODO
//		if (block instanceof WaterWheelBlock) {
//			int baseSpeed = AllConfigs.SERVER.kinetics.waterWheelBaseSpeed;
//			int speedmod = AllConfigs.SERVER.kinetics.waterWheelFlowSpeed;
//			value = (speedmod + baseSpeed) + "-" + (baseSpeed + (speedmod * 3));
//		}
//
//		else if (block instanceof EncasedFanBlock)
//			value = AllConfigs.SERVER.kinetics.generatingFanSpeed
//				.toString();
//
//		else if (block instanceof FurnaceEngineBlock) {
//			int baseSpeed = AllConfigs.SERVER.kinetics.furnaceEngineSpeed;
//			value = baseSpeed + "-" + (baseSpeed * 2);
//		}

		return !value.equals("") ? Lang.translate("tooltip.generationSpeed", value, unitRPM)
			: new ChatComponentText("");
	}

}
