package dev.xkmc.l2screentracker.screen.packets;

import dev.xkmc.l2screentracker.init.MouseCache;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class CacheMouseToClient extends SerialPacketBase {

	@Deprecated
	public CacheMouseToClient() {
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		MouseCache.cacheMousePos();
	}

}
