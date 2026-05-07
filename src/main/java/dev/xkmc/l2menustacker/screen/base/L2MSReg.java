package dev.xkmc.l2menustacker.screen.base;

import dev.xkmc.l2core.capability.player.PlayerCapabilityNetworkHandler;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.simple.AttReg;
import dev.xkmc.l2core.init.reg.simple.AttVal;
import dev.xkmc.l2core.init.reg.simple.SR;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.l2menustacker.init.L2MenuStacker;
import dev.xkmc.l2menustacker.screen.source.*;
import dev.xkmc.l2menustacker.screen.track.*;
import dev.xkmc.l2menustacker.screen.triggers.ExitMenuTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.PlayerEnderChestContainer;

import java.util.Optional;

public class L2MSReg {

	public static final L2Registrate.RegistryInstance<ItemSource<?>> ITEM_SOURCE = L2MenuStacker.REGISTRATE.newRegistry("item_source", ItemSource.class);
	public static final L2Registrate.RegistryInstance<TrackedEntryType<?>> TRACKED_ENTRY_TYPE = L2MenuStacker.REGISTRATE.newRegistry("tracked_entry_type", TrackedEntryType.class);

	public static final SR<ItemSource<?>> SOURCES = SR.of(L2MenuStacker.REG, ITEM_SOURCE.key());
	public static final Val<InventorySource> IS_INVENTORY = SOURCES.reg("inventory", InventorySource::new);
	public static final Val<EnderSource> IS_ENDER = SOURCES.reg("ender", EnderSource::new);

	public static final SR<TrackedEntryType<?>> TRACKED = SR.of(L2MenuStacker.REG, TRACKED_ENTRY_TYPE.key());
	public static final Val<InventoryTrace> TE_INVENTORY = TRACKED.reg("inventory", InventoryTrace::new);
	public static final Val<EnderTrace> TE_ENDER = TRACKED.reg("ender", EnderTrace::new);
	public static final Val<QuickAccessTrace> TE_QUICK_ACCESS = TRACKED.reg("quick_access", QuickAccessTrace::new);
	public static final Val<MenuProviderTrace> TE_MENU_PROVIDER = TRACKED.reg("menu_provider", MenuProviderTrace::new);

	public static final SR<CriterionTrigger<?>> REG = SR.of(L2MenuStacker.REG, BuiltInRegistries.TRIGGER_TYPES);
	public static final Val<ExitMenuTrigger> EXIT_MENU = REG.reg("exit_menu", ExitMenuTrigger::new);

	public static final AttReg ATT = AttReg.of(L2MenuStacker.REG);
	public static final AttVal.PlayerVal<ScreenTracker> TRACKER = ATT.player("screen_tracker",
			ScreenTracker.class, ScreenTracker::new, PlayerCapabilityNetworkHandler::new);

	public static void register() {
	}

	public static void commonSetup() {

		MenuSourceRegistry.register(MenuType.GENERIC_9x3, (menu, slot, index, wid) ->
				menu.getContainer() instanceof PlayerEnderChestContainer ?
						Optional.of(new PlayerSlot<>(IS_ENDER.get(), new SimpleSlotData(index))) :
						Optional.empty());

		MenuTraceRegistry.register(MenuType.GENERIC_9x3, menu ->
				menu.getContainer() instanceof PlayerEnderChestContainer ?
						Optional.of(TrackedEntry.of(TE_ENDER.get(), NoData.DATA)) :
						Optional.empty());

	}

}
