package dev.xkmc.l2screentracker.screen.base;

import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.capability.player.PlayerCapabilityNetworkHandler;
import dev.xkmc.l2library.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2screentracker.init.L2ScreenTracker;
import dev.xkmc.l2screentracker.screen.packets.AddTrackedToClient;
import dev.xkmc.l2screentracker.screen.packets.CacheMouseToClient;
import dev.xkmc.l2screentracker.screen.packets.PopLayerToClient;
import dev.xkmc.l2screentracker.screen.track.MenuTraceRegistry;
import dev.xkmc.l2screentracker.screen.track.NoData;
import dev.xkmc.l2screentracker.screen.track.TrackedEntry;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import javax.annotation.Nullable;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * 1. Server open BaseOpenableContainer: add previous and current menu to stack, tell client
 * 2. Client open BaseOpenableContainer: record title of previous and current title to stack
 */
@SerialClass
@SuppressWarnings("unused")
public class ScreenTracker extends PlayerCapabilityTemplate<ScreenTracker> {

	public static final Capability<ScreenTracker> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static final PlayerCapabilityHolder<ScreenTracker> HOLDER = new PlayerCapabilityHolder<>(
			new ResourceLocation(L2ScreenTracker.MODID, "screen_tracker"), CAPABILITY,
			ScreenTracker.class, ScreenTracker::new, PlayerCapabilityNetworkHandler::new);

	public static void register() {
	}

	public static ScreenTracker get(Player player) {
		return HOLDER.get(player);
	}

	public static void onServerOpen(ServerPlayer player) {
		get(player).serverOpen(player);
	}

	public static void onServerOpenMenu(ServerPlayer player, MenuProvider next, MenuTriggerType type, @Nullable Consumer<FriendlyByteBuf> buf) {
		get(player).serverOpenMenu(player, MenuCache.of(player.containerMenu, next, type, buf));
	}

	public static void removeAll(ServerPlayer player) {
		get(player).stack.clear();
	}

	public static int size(ServerPlayer player) {
		return get(player).stack.size();
	}

	// non-static

	final Stack<TrackedEntry<?>> stack = new Stack<>();

	int wid;

	// --- server only values
	private TrackedEntry<?> temp;
	private boolean restoring = false;
	private MenuCache current;

	private void serverOpenMenu(ServerPlayer player, MenuCache next) {
		if (temp != null) {
			if (current != null)
				temp.setTitle(current.title());
			serverOpenMenu(player, temp, next.menu());
		}
		this.current = next;
		temp = null;
	}

	@Nullable
	private TrackedEntry<?> getEntry(AbstractContainerMenu prev) {
		if (prev.containerId == 0) {
			return TrackedEntry.of(ScreenTrackerRegistry.TE_INVENTORY.get(), NoData.DATA);
		}
		var getter = MenuTraceRegistry.get(prev.getType());
		if (getter != null) {
			var entry = Wrappers.get(() -> getter.track(Wrappers.cast(prev)));
			if (entry != null && entry.isPresent()) {
				return entry.get();

			}
		}
		if (current != null && current.menu() == prev) {
			return current.constructEntry();
		}
		return null;
	}

	private void serverOpen(ServerPlayer player) {
		if (restoring) return;
		temp = getEntry(player.containerMenu);
	}

	private void serverOpenMenu(ServerPlayer player, TrackedEntry<?> prev, AbstractContainerMenu menu) {
		int toRemove = 0;
		if (!stack.isEmpty()) {
			TrackedEntry<?> next = getEntry(menu);
			if (next != null) {
				TrackedEntry<?> itr = stack.peek();
				if (itr.shouldReturn(next)) {
					toRemove = 1;
					stack.pop();
				}
			}
		}
		stack.push(prev);
		wid = menu.containerId;
		L2ScreenTracker.PACKET_HANDLER.toClientPlayer(new AddTrackedToClient(prev, toRemove, wid), player);
	}

	public boolean serverRestore(ServerPlayer player, int wid) {
		if (stack.isEmpty()) return false;
		if (this.wid == wid) {
			restoring = true;
			L2ScreenTracker.PACKET_HANDLER.toClientPlayer(new CacheMouseToClient(), player);
			LayerPopType type = Wrappers.get(() -> stack.pop().restoreServerMenu(player));
			restoring = false;
			if (type != null && type != LayerPopType.FAIL) {
				int id = player.containerMenu.containerId;
				this.wid = id;
				L2ScreenTracker.PACKET_HANDLER.toClientPlayer(new PopLayerToClient(type, id), player);
				return true;
			}
		}
		return false;
	}

	// --- client only values
	boolean isWaiting;

}
