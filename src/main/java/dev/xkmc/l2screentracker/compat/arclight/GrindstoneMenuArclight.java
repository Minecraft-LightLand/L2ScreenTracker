package dev.xkmc.l2screentracker.compat.arclight;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.StonecutterMenu;

public class GrindstoneMenuArclight extends GrindstoneMenu {

	public GrindstoneMenuArclight(int p_40248_, Inventory p_40249_, ContainerLevelAccess p_40250_) {
		super(p_40248_, p_40249_, p_40250_);
	}

	@Override
	public boolean stillValid(Player p_39780_) {
		return true;
	}
	
}
