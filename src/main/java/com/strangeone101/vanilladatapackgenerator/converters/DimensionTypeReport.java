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

import net.minecraft.world.level.dimension.DimensionType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class DimensionTypeReport implements DataProvider {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    private final DataGenerator generator;

    public DimensionTypeReport(DataGenerator $$0) {
        this.generator = $$0;
    }


    public void run(HashCache cache) {
        Path outputFolder = this.generator.getOutputFolder();

        //Foreach dimension
        for (Map.Entry<ResourceKey<DimensionType>, DimensionType> entry : VanillaBridge.getDimensionTypeRegistry().entrySet()) {
            Path path = createPath(outputFolder, entry.getKey().location());
            DimensionType type = entry.getValue();

            //The CODEC specifies how to encode the classes' values, in this case, how a dimension type class converts to JSON
            Function<Supplier<DimensionType>, DataResult<JsonElement>> func = JsonOps.INSTANCE.withEncoder(DimensionType.CODEC);
            try {
                Optional<JsonElement> optionalJson = (func.apply(() -> type)).result(); //Pulling an optional JSON element from the encoded dimension type
                if (optionalJson.isPresent()) {
                    DataProvider.save(GSON, cache, optionalJson.get(), path); continue;
                }
                LOGGER.severe("Couldn't serialize dimension type " + path);
            }
            catch (IOException $$7) {
                LOGGER.severe("Couldn't save dimension type " + path + " " + $$7);
            }
        }
    }

    private static Path createPath(Path $$0, ResourceLocation $$1) {
        return $$0.resolve("data/minecraft/dimension_type/" + $$1.getPath() + ".json");
    }

    public String getName() {
        return "DimensionTypes";
    }
}
