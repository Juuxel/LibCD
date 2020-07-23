package io.github.cottonmc.libcd;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.LibCDInitializer;
import io.github.cottonmc.libcd.api.condition.ConditionManager;
import io.github.cottonmc.libcd.api.init.ConditionInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileOutputStream;

public class LibCD implements ModInitializer {
	public static final String MODID = "libcd";

	public static CDConfig config;

	public static boolean isDevMode() {
		return config.dev_mode;
	}

	@Override
	public void onInitialize() {
		config = loadConfig();
		FabricLoader.getInstance().getEntrypoints(MODID + ":conditions", ConditionInitializer.class).forEach(init -> init.initConditions(ConditionManager.INSTANCE));
		FabricLoader.getInstance().getEntrypoints(MODID, LibCDInitializer.class).forEach(init -> {
			init.initConditions(ConditionManager.INSTANCE);
		});
	}

	public static CDConfig loadConfig() {
		try {
			Jankson jankson = CDCommons.newJankson();
			File file = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("libcd.json5").toFile();
			if (!file.exists()) saveConfig(new CDConfig());
			JsonObject json = jankson.load(file);
			CDConfig result =  jankson.fromJson(json, CDConfig.class);
			JsonElement jsonElementNew = jankson.toJson(new CDConfig());
			if(jsonElementNew instanceof JsonObject){
				JsonObject jsonNew = (JsonObject) jsonElementNew;
				if(json.getDelta(jsonNew).size()>= 0){
					saveConfig(result);
				}
			}
			return result;
		} catch (Exception e) {
			CDCommons.logger.error("Error loading config: {}", e.getMessage());
		}
		return new CDConfig();
	}

	public static void saveConfig(CDConfig config) {
		try {
			File file = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("libcd.json5").toFile();
			JsonElement json = CDCommons.newJankson().toJson(config);
			String result = json.toJson(true, true);
			if (!file.exists()) file.createNewFile();
			FileOutputStream out = new FileOutputStream(file,false);
			out.write(result.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			CDCommons.logger.error("Error saving config: {}", e.getMessage());
		}
	}
}
