package dev.xkmc.l2menustacker.init;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum L2MSLangData {
	QUICK_ACCESS("tooltip.quick_access", "Right click in GUI to open", 0);

	private final String key, def;
	private final int arg;


	L2MSLangData(String key, String def, int arg) {
		this.key = L2MenuStacker.MODID + "." + key;
		this.def = def;
		this.arg = arg;
	}

	public MutableComponent get(Object... args) {
		if (args.length != arg)
			throw new IllegalArgumentException("for " + name() + ": expect " + arg + " parameters, got " + args.length);
		return Component.translatable(key, args);
	}

	public static void genLang(RegistrateLangProvider pvd) {
		for (L2MSLangData lang : L2MSLangData.values()) {
			pvd.add(lang.key, lang.def);
		}
	}

}
