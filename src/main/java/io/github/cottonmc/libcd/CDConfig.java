package io.github.cottonmc.libcd;

import blue.endless.jankson.Comment;

public class CDConfig {
	@Comment("Whether dev-env files, like the test tweaker, should be loaded.\n" +
			"This will affect the loaded data for your game.")
	public boolean dev_mode = false;
}
