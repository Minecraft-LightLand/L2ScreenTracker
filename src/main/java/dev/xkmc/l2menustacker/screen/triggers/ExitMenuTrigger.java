package dev.xkmc.l2menustacker.screen.triggers;

import dev.xkmc.l2core.serial.advancements.BaseCriterion;
import dev.xkmc.l2core.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2menustacker.screen.base.L2MSReg;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.server.level.ServerPlayer;

@SerialClass
public class ExitMenuTrigger extends BaseCriterion<ExitMenuTrigger.Ins, ExitMenuTrigger> {

	public static Ins exitOne() {
		return new Ins();
	}

	public static Ins exitAll() {
		Ins ans = exitOne();
		ans.all = true;
		return ans;
	}

	public ExitMenuTrigger() {
		super(Ins.class);
	}

	public void trigger(ServerPlayer player, boolean all) {
		this.trigger(player, e -> e.all == all);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, ExitMenuTrigger> {

		@SerialField
		private boolean all = false;

		public Ins() {
			super(L2MSReg.EXIT_MENU.get());
		}

	}

}
