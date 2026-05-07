package dev.xkmc.l2menustacker.screen.track;

import dev.xkmc.l2core.init.reg.registrate.NamedEntry;
import dev.xkmc.l2menustacker.screen.base.L2MSReg;
import dev.xkmc.l2menustacker.screen.base.LayerPopType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public abstract class TrackedEntryType<T extends Record & TrackedEntryData<T>> extends NamedEntry<TrackedEntryType<?>> {

	public TrackedEntryType() {
		super(L2MSReg.TRACKED_ENTRY_TYPE);
	}

	public abstract LayerPopType restoreMenuNotifyClient(ServerPlayer player, T data, @Nullable Component comp);

}
