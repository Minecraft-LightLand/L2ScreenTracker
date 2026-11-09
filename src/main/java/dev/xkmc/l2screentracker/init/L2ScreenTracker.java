package dev.xkmc.l2screentracker.init;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.serial.config.PacketHandler;
import dev.xkmc.l2screentracker.click.SlotClickToServer;
import dev.xkmc.l2screentracker.click.quickaccess.DefaultQuickAccessActions;
import dev.xkmc.l2screentracker.click.quickaccess.QuickAccessClickHandler;
import dev.xkmc.l2screentracker.compat.L2CuriosCompat;
import dev.xkmc.l2screentracker.screen.base.ScreenTracker;
import dev.xkmc.l2screentracker.screen.base.ScreenTrackerRegistry;
import dev.xkmc.l2screentracker.screen.packets.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

@Mod(L2ScreenTracker.MODID)
@Mod.EventBusSubscriber(modid = L2ScreenTracker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class L2ScreenTracker {

	public static final String MODID = "l2screentracker";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);

	public static final PacketHandler PACKET_HANDLER = new PacketHandler(new ResourceLocation(MODID, "main"), 1,
			e -> e.create(SlotClickToServer.class, PLAY_TO_SERVER),
			e -> e.create(RestoreMenuToServer.class, PLAY_TO_SERVER),
			e -> e.create(AddTrackedToClient.class, PLAY_TO_CLIENT),
			e -> e.create(SetScreenToClient.class, PLAY_TO_CLIENT),
			e -> e.create(PopLayerToClient.class, PLAY_TO_CLIENT),
			e -> e.create(CacheMouseToClient.class, PLAY_TO_CLIENT)
	);

	public L2ScreenTracker() {
		L2ScreenTrackerConfig.init();
		ScreenTracker.register();
		ScreenTrackerRegistry.register();
		L2CuriosCompat.onStartup();
		QuickAccessClickHandler.INS = new QuickAccessClickHandler(new ResourceLocation(MODID, "quick_access"));
		REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, L2STTagGen::genItemTags);
		REGISTRATE.addDataGenerator(ProviderType.LANG, L2STLangData::genLang);
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			DefaultQuickAccessActions.register();
			ScreenTrackerRegistry.commonSetup();
			L2CuriosCompat.commonSetup();
		});
	}

}
