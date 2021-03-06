package io.github.cottonmc.libcd;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.CDSyntaxError;
import io.github.cottonmc.libcd.api.LibCDInitializer;
import io.github.cottonmc.libcd.api.condition.ConditionManager;
import io.github.cottonmc.libcd.api.condition.ConditionalData;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.tag.TagContainers;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class CDContent implements LibCDInitializer {
	@Override
	public void initConditions(ConditionManager manager) {
		manager.registerCondition(new Identifier(CDCommons.MODID, "mod_loaded"), value -> {
			if (value instanceof String) return FabricLoader.getInstance().isModLoaded((String) value);
			if (value instanceof List) {
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) return false;
					Object obj = ((JsonPrimitive)el).getValue();
					if (obj instanceof String) {
						if (!FabricLoader.getInstance().isModLoaded((String)obj)) return false;
					}  else return false;
				}
				return true;
			}
			throw new CDSyntaxError("mod_loaded must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "item_exists"), value -> {
			if (value instanceof String) return Registry.ITEM.get(new Identifier((String)value)) != Items.AIR;
			if (value instanceof List) {
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) throw new CDSyntaxError("item_exists array must only contain Strings!");
					Object obj = ((JsonPrimitive)el).getValue();
					if (obj instanceof String) {
						if (Registry.ITEM.get(new Identifier((String)obj)) == Items.AIR) return false;
					}  else throw new CDSyntaxError("item_exists array must only contain Strings!");
				}
				return true;
			}
			throw new CDSyntaxError("item_exists must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "item_tag_exists"), value -> {
			if (value instanceof String) return TagContainers.instance().items().getKeys().contains(new Identifier((String)value));
			if (value instanceof List) {
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) throw new CDSyntaxError("item_tag_exists array must only contain Strings!");
					Object obj = ((JsonPrimitive)el).getValue();
					if (obj instanceof String) {
						Identifier id = new Identifier((String) obj);
						if (!TagContainers.instance().items().getKeys().contains(id)) return false;
						if (TagContainers.instance().items().get(id).values().isEmpty()) return false;
					}  else throw new CDSyntaxError("item_tag_exists array must only contain Strings!");
				}
				return true;
			}
			throw new CDSyntaxError("item_tag_exists must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "block_exists"), value -> {
			if (value instanceof String) return Registry.BLOCK.get(new Identifier((String)value)) != Blocks.AIR;
			if (value instanceof List) {
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) throw new CDSyntaxError("block_exists array must only contain Strings!");
					Object obj = ((JsonPrimitive)el).getValue();
					if (obj instanceof String) {
						if (Registry.BLOCK.get(new Identifier((String)obj)) == Blocks.AIR) return false;
					}  else throw new CDSyntaxError("block_exists array must only contain Strings!");
				}
				return true;
			}
			throw new CDSyntaxError("block_exists must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "block_tag_exists"), value -> {
			if (value instanceof String) return TagContainers.instance().blocks().getKeys().contains(new Identifier((String)value));
			if (value instanceof List) {
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) throw new CDSyntaxError("block_tag_exists array must only contain Strings!");
					Object obj = ((JsonPrimitive)el).getValue();
					if (obj instanceof String) {
						Identifier id = new Identifier((String) obj);
						if (!TagContainers.instance().blocks().getKeys().contains(id)) return false;
						if (TagContainers.instance().blocks().get(id).values().isEmpty()) return false;
					}  else throw new CDSyntaxError("block_tag_exists array must only contain Strings!");
				}
				return true;
			}
			throw new CDSyntaxError("block_tag_exists must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "not"), value -> {
			if (value instanceof JsonObject) {
				JsonObject json = (JsonObject)value;
				for (String key : json.keySet()) {
					Identifier id = new Identifier(key);
					Object result = ConditionalData.parseElement(json.get(key));
					if (ConditionalData.hasCondition(id)) {
						return !ConditionalData.testCondition(id, result);
					} else return false;
				}
			}
			throw new CDSyntaxError("not must accept an Object!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "none"), value -> {
			if (value instanceof JsonArray) {
				JsonArray json = (JsonArray) value;
				for (JsonElement elem : json) {
					if (elem instanceof JsonObject) {
						JsonObject obj = (JsonObject) elem;
						for (String key : obj.keySet()) {
							if (ConditionalData.testCondition(new Identifier(key), ConditionalData.parseElement(obj.get(key)))) return false;
						}
					}
				}
				return true;
			}
			throw new CDSyntaxError("none must accept an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "or"), value -> {
			if (value instanceof JsonArray) {
				JsonArray json = (JsonArray) value;
				for (JsonElement elem : json) {
					if (elem instanceof JsonObject) {
						JsonObject obj = (JsonObject) elem;
						for (String key : obj.keySet()) {
							if (ConditionalData.testCondition(new Identifier(key), ConditionalData.parseElement(obj.get(key)))) return true;
						}
					}
				}
			}
			throw new CDSyntaxError("or must accept an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "xor"), value -> {
			if (value instanceof JsonArray) {
				JsonArray json = (JsonArray) value;
				boolean ret = false;
				for (JsonElement elem : json) {
					if (elem instanceof JsonObject) {
						JsonObject obj = (JsonObject) elem;
						for (String key : obj.keySet()) {
							if (ConditionalData.testCondition(new Identifier(key), ConditionalData.parseElement(obj.get(key)))) {
								if(ret) return false;
								else ret = true;
							}
						}
					}
				}
				return ret;
			}
			throw new CDSyntaxError("xor must accept an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "dev_mode"), value -> {
			if (value instanceof Boolean) return (Boolean)value == LibCD.isDevMode();
			throw new CDSyntaxError("dev_mode must accept a Boolean!");
		});
	}
}
