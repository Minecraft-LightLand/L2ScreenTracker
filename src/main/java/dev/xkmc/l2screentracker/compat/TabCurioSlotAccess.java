package dev.xkmc.l2screentracker.compat;

import dev.xkmc.l2tabs.compat.CurioSlot;
import net.minecraft.world.inventory.Slot;

import javax.annotation.Nullable;

/**
 * L2 Tabs uses its own curio slot class, so it cannot be matched against Curios {@code CurioSlot}.
 */
public class TabCurioSlotAccess {

	@Nullable
	public static SlotData of(Slot slot) {
		if (slot instanceof CurioSlot curio) {
			return new SlotData(curio.getIdentifier(), curio.getSlotIndex());
		}
		return null;
	}

	public record SlotData(String identifier, int index) {
	}

}
