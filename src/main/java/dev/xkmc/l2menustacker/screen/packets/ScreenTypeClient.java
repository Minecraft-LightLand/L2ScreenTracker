package dev.xkmc.l2menustacker.screen.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class ScreenTypeClient {

	public static void none() {
		Minecraft.getInstance().setScreen(null);
	}

	public static void player() {
		var player = Minecraft.getInstance().player;
		if (player != null)
			Minecraft.getInstance().setScreen(new InventoryScreen(player));
	}

}
