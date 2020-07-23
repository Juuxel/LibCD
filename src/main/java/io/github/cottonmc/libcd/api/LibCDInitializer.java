package io.github.cottonmc.libcd.api;

import io.github.cottonmc.libcd.api.condition.ConditionManager;
import io.github.cottonmc.libcd.api.init.ConditionInitializer;

/**
 * Initializer that initializes tweakers, conditions, and advancement rewards all at once.
 */
public interface LibCDInitializer extends ConditionInitializer {
	/**
	 * Register conditions for conditional data.
	 * @param manager The condition manager to register in.
	 */
	@Override
	default void initConditions(ConditionManager manager) {}
}
