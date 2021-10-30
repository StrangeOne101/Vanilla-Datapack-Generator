package com.strangeone101.vanilladatapackgenerator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

public class Main {

    private static int DATAPACK_VERSION = -1;
    private static final int TARGET_DATAPACK_VERSION = 8;

    public static void main(String[] args) {

        if (args.length == 0) {
            new GUI();
            return;
        }

        CommandLineParser parser = new DefaultParser();

        try {
            Options options = new Options();
            CommandLine line = parser.parse(options, args);

            if (line.hasOption(Options.HELP)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Use the params bellow", options);
                return;
            }

            String output = "output";
            if (line.hasOption(Options.OUTPUT)) output = line.getOptionValue(Options.OUTPUT);

            File fileOut = new File(output);
            if (fileOut.exists() && fileOut.isFile()) throw new IOException("Output folder cannot be the same name as a file!");

            if (!line.hasOption(Options.JAR)) {
                System.out.println("Server jar not specified! Use -help to see all options!");
                return;
            }

            File jarFile = new File(line.getOptionValue(Options.JAR));

            if (!jarFile.exists()) throw new IOException("Could not find file \"" + line.getOptionValue(Options.JAR) + "\"!");

            if (!importMinecraft(jarFile)) return;

            Converter conv = new Converter(jarFile, fileOut);
            conv.run();
        }
        catch (Exception exp) {
            Logger.getGlobal().severe("Could not complete conversion!");
            exp.printStackTrace();
        }


    }

    public static boolean importMinecraft(File jar) {
        try {
            Logger.getGlobal().info("Importing minecraft jar...");
            JavaClassLoader loader = new JavaClassLoader(jar);

            InputStream stream = loader.getResourceAsStream("version.json");
            if (stream == null) {
                Logger.getGlobal().severe("Could not find version.json file in the jar!");
                Logger.getGlobal().severe("Make sure you are using a version higher than 21w41a!");
                return false;
            }

            JsonObject json = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
            String id = json.get("id").getAsString();
            Logger.getGlobal().info("Imported server jar. Jar version is " + id);

            DATAPACK_VERSION = json.get("pack_version").getAsJsonObject().get("data").getAsInt();
            if (TARGET_DATAPACK_VERSION != DATAPACK_VERSION) {
                Logger.getGlobal().warning("Datapack version is " + DATAPACK_VERSION
                        + "Supported version is " + TARGET_DATAPACK_VERSION + ", so issues may come up!");
            } else {
                Logger.getGlobal().info("Datapack version is " + DATAPACK_VERSION + ". All okay!");
            }

            return true;
        } catch (IOException | ClassNotFoundException e) {
            Logger.getGlobal().severe("Failed to load the minecraft jar!");
            e.printStackTrace();
        }
        return false;
    }
}
