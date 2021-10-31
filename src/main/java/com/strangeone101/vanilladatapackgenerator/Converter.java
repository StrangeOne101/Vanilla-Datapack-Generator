package com.strangeone101.vanilladatapackgenerator;

import java.io.File;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

public class Converter {

    private File output;

    public Converter(File jar, File output) {
        this.output = output;
    }

    public void run(ClassLoader loader) {
        long time = System.currentTimeMillis();

        String custom = "com.strangeone101.vanilladatapackgenerator.converters.";
        String vanilla = "net.minecraft.data.";
        String[] providers = {custom + "DimensionReport", custom + "DimensionTypeReport", vanilla + "recipes.RecipeProvider",
        vanilla + "worldgen.biome.BiomeReport", vanilla + "advancements.AdvancementProvider", vanilla + "loot.LootTableProvider"};

        try {
            Class dataGeneratorClass = Class.forName(vanilla + "DataGenerator", true, loader);
            Class dataProviderClass = Class.forName(vanilla + "DataProvider", true, loader);
            Object generatorObject = dataGeneratorClass.getConstructor(Path.class, Collection.class)
                    .newInstance(output.toPath(), Collections.EMPTY_LIST);
            Method addProvderMethod = dataGeneratorClass.getDeclaredMethod("addProvider", dataProviderClass);

            for (String prov : providers) {
                Object newProvider = Class.forName(prov, true, loader).getConstructor(dataGeneratorClass).newInstance(generatorObject);
                addProvderMethod.invoke(generatorObject, newProvider);
            }

            Method runMethod = dataGeneratorClass.getDeclaredMethod("run");
            runMethod.invoke(generatorObject); //GO

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Logger.getGlobal().info("Done! Took " + (System.currentTimeMillis() - time) + "ms!");

        /*DataGenerator provider = new DataGenerator(output.toPath(), Collections.emptyList());
        provider.addProvider(new DimensionReport(provider));
        provider.addProvider(new DimensionTypeReport(provider));
        provider.addProvider(new BiomeReport(provider));
        provider.addProvider(new RecipeProvider(provider));
        provider.addProvider(new AdvancementProvider(provider));
        provider.addProvider(new LootTableProvider(provider));*/

            //provider.run();



    }




}
