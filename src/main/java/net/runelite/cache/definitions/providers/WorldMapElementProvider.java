package net.runelite.cache.definitions.providers;

import net.runelite.cache.definitions.WorldMapElementDefinition;

public interface WorldMapElementProvider
{
	WorldMapElementDefinition provide(int worldMapElementId);
}
