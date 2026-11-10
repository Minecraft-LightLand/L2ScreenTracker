package dev.xkmc.l2screentracker.compat;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2screentracker.compat.track.CurioInvTrace;
import dev.xkmc.l2screentracker.compat.track.CurioSource;
import dev.xkmc.l2screentracker.compat.track.CurioTabTrace;
import dev.xkmc.l2screentracker.init.L2ScreenTracker;
import dev.xkmc.l2screentracker.screen.base.ScreenTrackerRegistry;
import dev.xkmc.l2screentracker.screen.source.MenuSourceRegistry;
import dev.xkmc.l2screentracker.screen.source.PlayerSlot;
import dev.xkmc.l2screentracker.screen.source.SimpleSlotData;
import dev.xkmc.l2screentracker.screen.track.MenuTraceRegistry;
import dev.xkmc.l2screentracker.screen.track.NoData;
import dev.xkmc.l2screentracker.screen.track.TrackedEntry;
import dev.xkmc.l2tabs.compat.CuriosListMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.CuriosRegistry;
import top.theillusivec4.curios.common.inventory.CurioSlot;
import top.theillusivec4.curios.common.inventory.container.CuriosContainer;
import top.theillusivec4.curios.common.inventory.container.CuriosContainerProvider;

import java.util.Map;
import java.util.Optional;

public class CuriosTrackCompatImpl {

	private static CuriosTrackCompatImpl INSTANCE;

	public static CuriosTrackCompatImpl get() {
		if (INSTANCE == null) {
			INSTANCE = new CuriosTrackCompatImpl();
		}
		return INSTANCE;
	}

	ItemStack getItemFromSlotImpl(Player player, int slot) {
		return CuriosApi.getCuriosInventory(player)
				.lazyMap(ICuriosItemHandler::getEquippedCurios)
				.resolve()
				.map(e -> e.getStackInSlot(slot))
				.orElse(ItemStack.EMPTY);
	}

	public RegistryEntry<CurioSource> IS_CURIOS;
	public RegistryEntry<CurioInvTrace> TE_CURIO_INV;
	public RegistryEntry<CurioTabTrace> TE_CURIO_TAB;

	void onStartUp() {
		IS_CURIOS = L2ScreenTracker.REGISTRATE.simple("curios", ScreenTrackerRegistry.ITEM_SOURCE.key(), CurioSource::new);
		TE_CURIO_INV = L2ScreenTracker.REGISTRATE.simple("curios_inv", ScreenTrackerRegistry.TRACKED_ENTRY_TYPE.key(), CurioInvTrace::new);
		TE_CURIO_TAB = L2ScreenTracker.REGISTRATE.simple("curios_tab", ScreenTrackerRegistry.TRACKED_ENTRY_TYPE.key(), CurioTabTrace::new);
	}

	void onCommonSetup() {
		registerCurioMenu(CuriosRegistry.CURIO_MENU.get());
		registerCurioMenu(CuriosRegistry.CURIO_MENU_NEW.get());
	}

	private <T extends CuriosContainer> void registerCurioMenu(MenuType<T> type) {
		MenuSourceRegistry.register(type, (menu, slot, index, wid) ->
				getPlayerSlotImpl(slot, index, wid, menu));

		MenuTraceRegistry.register(type, menu ->
				Optional.of(TrackedEntry.of(TE_CURIO_INV.get(), NoData.DATA)));
	}

	void onCompatSetup(MenuType<?> type) {
		MenuSourceRegistry.register(type, (menu, slot, index, wid) -> getPlayerSlotImpl(slot, index, wid, menu));
		MenuTraceRegistry.register(type, menu -> Optional.of(TrackedEntry.of(TE_CURIO_TAB.get(), NoData.DATA)));

	}

	private Optional<PlayerSlot<?>> getPlayerSlotImpl(int slot, int index, int wid, AbstractContainerMenu menu) {
		if (menu.containerId != wid) return Optional.empty();
		Slot s = menu.getSlot(index);
		if (menu instanceof CuriosContainer cont && s instanceof CurioSlot curio) {
			return getSlotData(cont.player, curio.getIdentifier(), curio.getSlotIndex());
		}
		if (ModList.get().isLoaded("l2tabs") && menu instanceof CuriosListMenu cont
				&& cont.curios.entity instanceof Player player) {
			var data = TabCurioSlotAccess.of(s);
			if (data != null) {
				return getSlotData(player, data.identifier(), data.index());
			}
		}
		return Optional.empty();
	}

	private Optional<PlayerSlot<?>> getSlotData(Player player, String id, int index) {
		int slotIndex = getSlotIndexInContainer(player, id);
		if (slotIndex < 0) return Optional.empty();
		return Optional.of(new PlayerSlot<>(IS_CURIOS.get(), new SimpleSlotData(slotIndex + index)));
	}

	private static int getSlotIndexInContainer(Player player, String id) {
		Optional<ICuriosItemHandler> handler = CuriosApi.getCuriosInventory(player).resolve();
		if (handler.isEmpty()) return -1;
		Map<String, ICurioStacksHandler> curios = handler.get().getCurios();
		int index = 0;

		for (ICurioStacksHandler stacksHandler : curios.values()) {
			if (stacksHandler.getIdentifier().equals(id)) {
				return index;
			}
			index += stacksHandler.getSlots();
		}
		return -1;
	}

	void openCurioImpl(ServerPlayer player) {
		NetworkHooks.openScreen(player, new CuriosContainerProvider());
	}

}
