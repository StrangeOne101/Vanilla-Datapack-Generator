package com.strangeone101.vanilladatapackgenerator;

import com.strangeone101.vanilladatapackgenerator.converters.DimensionReport;
import com.strangeone101.vanilladatapackgenerator.converters.DimensionTypeReport;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.biome.BiomeReport;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.logging.Logger;

public class Converter {

    private File jar;
    private File output;

    public Converter(File jar, File output) {
        this.jar = jar;
        this.output = output;
    }

    public void run() {
        long time = System.currentTimeMillis();
        DataGenerator provider = new DataGenerator(output.toPath(), Collections.emptyList());
        provider.addProvider(new DimensionReport(provider));
        provider.addProvider(new DimensionTypeReport(provider));
        provider.addProvider(new BiomeReport(provider));
        provider.addProvider(new RecipeProvider(provider));
        provider.addProvider(new AdvancementProvider(provider));
        provider.addProvider(new LootTableProvider(provider));
        try {
            provider.run();

            Logger.getGlobal().info("Done! Took " + (System.currentTimeMillis() - time) + "ms!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
