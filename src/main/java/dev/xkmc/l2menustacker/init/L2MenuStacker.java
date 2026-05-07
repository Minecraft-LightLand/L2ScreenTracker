package dev.xkmc.l2menustacker.init;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.simple.Reg;
import dev.xkmc.l2menustacker.click.SlotClickToServer;
import dev.xkmc.l2menustacker.click.quickaccess.DefaultQuickAccessActions;
import dev.xkmc.l2menustacker.click.quickaccess.QuickAccessClickHandler;
import dev.xkmc.l2menustacker.screen.base.L2MSReg;
import dev.xkmc.l2menustacker.screen.packets.*;
import dev.xkmc.l2serial.network.PacketHandler;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static dev.xkmc.l2serial.network.PacketHandler.NetDir.PLAY_TO_CLIENT;
import static dev.xkmc.l2serial.network.PacketHandler.NetDir.PLAY_TO_SERVER;

@Mod(L2MenuStacker.MODID)
@EventBusSubscriber(modid = L2MenuStacker.MODID)
public class L2MenuStacker {

	public static final String MODID = "l2menustacker";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Reg REG = new Reg(MODID);
	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);

	public static final PacketHandler PACKET_HANDLER = new PacketHandler(MODID, 1,
			e -> e.create(SlotClickToServer.class, PLAY_TO_SERVER),
			e -> e.create(RestoreMenuToServer.class, PLAY_TO_SERVER),
			e -> e.create(AddTrackedToClient.class, PLAY_TO_CLIENT),
			e -> e.create(SetScreenToClient.class, PLAY_TO_CLIENT),
			e -> e.create(PopLayerToClient.class, PLAY_TO_CLIENT),
			e -> e.create(CacheMouseToClient.class, PLAY_TO_CLIENT)
	);

	public L2MenuStacker(IEventBus bus) {
		L2MSConfig.init();
		L2MSReg.register();
		QuickAccessClickHandler.INS = new QuickAccessClickHandler(loc("quick_access"));
		REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, L2MSTagGen::genItemTags);
		REGISTRATE.addDataGenerator(ProviderType.LANG, L2MSLangData::genLang);
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			DefaultQuickAccessActions.register();
			L2MSReg.commonSetup();
		});
	}

	public static Identifier loc(String id) {
		return Identifier.fromNamespaceAndPath(MODID, id);
	}

}
