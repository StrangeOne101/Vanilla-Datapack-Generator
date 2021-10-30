package com.strangeone101.vanilladatapackgenerator;

import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.WorldGenSettings;

import java.util.Properties;

public class VanillaBridge {

    @Getter
    private static WorldGenSettings worldGenSettings;

    @Getter
    private static Registry<DimensionType> dimensionTypeRegistry;

    static {
        RegistryAccess.RegistryHolder registry = RegistryAccess.builtin();
        worldGenSettings = WorldGenSettings.create(registry, new Properties());

        dimensionTypeRegistry = registry.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
    }
}
