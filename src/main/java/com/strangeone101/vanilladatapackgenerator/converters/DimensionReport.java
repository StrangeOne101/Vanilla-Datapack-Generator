package com.strangeone101.vanilladatapackgenerator.converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.strangeone101.vanilladatapackgenerator.VanillaBridge;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.level.dimension.LevelStem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import java.util.logging.Logger;

public class DimensionReport implements DataProvider {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    private final DataGenerator generator;

    public DimensionReport(DataGenerator $$0) {
        this.generator = $$0;
    }


    public void run(HashCache cache) {
        Path outputFolder = this.generator.getOutputFolder();


        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : VanillaBridge.getWorldGenSettings().dimensions().entrySet()) {
            Path path = createPath(outputFolder, entry.getKey().location());
            LevelStem stem = entry.getValue();
            Function<LevelStem, DataResult<JsonElement>> func = JsonOps.INSTANCE.withEncoder(LevelStem.CODEC);
            try {
                Optional<JsonElement> element = (func.apply(stem)).result();
                if (element.isPresent()) {
                    DataProvider.save(GSON, cache, element.get(), path); continue;
                }
                LOGGER.severe("Couldn't serialize dimension " + path);
            }
            catch (IOException $$7) {
                LOGGER.severe("Couldn't save dimension " + path + " " + $$7);
            }
        }
    }

    private static Path createPath(Path $$0, ResourceLocation $$1) {
        return $$0.resolve("reports/dimension/" + $$1.getPath() + ".json");
    }

    public String getName() {
        return "Biomes";
    }
}
