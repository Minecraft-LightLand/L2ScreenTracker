package dev.xkmc.l2menustacker.click;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record SlotClickToServer(Identifier type, int index, int slot, int wid)
		implements SerialPacketBase<SlotClickToServer> {

	@Override
	public void handle(Player player) {
		var handler = SlotClickHandler.MAP.get(type);
		if (handler != null && player instanceof ServerPlayer sp) {
			handler.handle(sp, index, slot, wid);
		}
	}

}
