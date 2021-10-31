package com.strangeone101.vanilladatapackgenerator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class Main {

    private static int DATAPACK_VERSION = -1;
    private static final int TARGET_DATAPACK_VERSION = 8;

    public static void main(String[] args) {

        if (args.length == 0) {
            throw new UnsupportedOperationException("GUI not ready yet! Use -help instead!");
            //new GUI();
        }

        CommandLineParser parser = new DefaultParser();

        try {
            Options options = new Options();
            CommandLine line = parser.parse(options, args); //Parses all console options

            if (line.hasOption(Options.HELP)) { //-help
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Use the params bellow", options); //Prints the help
                return;
            }

            String output = "output"; //Default directory to output to
            if (line.hasOption(Options.OUTPUT)) output = line.getOptionValue(Options.OUTPUT);

            File fileOut = new File(output);
            if (fileOut.exists() && fileOut.isFile()) throw new IOException("Output folder cannot be the same name as a file!");

            //DEBUG DISABLED
            /*if (!line.hasOption(Options.JAR)) {
                System.out.println("Server jar not specified! Use -help to see all options!");
                return;
            }

            File jarFile = new File(line.getOptionValue(Options.JAR));

            if (!jarFile.exists()) throw new IOException("Could not find file \"" + line.getOptionValue(Options.JAR) + "\"!");*/
            //DEBUG DISABLED END

            ClassLoader loader = Main.class.getClassLoader();
            if (line.hasOption(Options.LIBS)) {
                File folder = new File(line.getOptionValue(Options.LIBS));
                if (!folder.exists()) throw new FileNotFoundException("Could not find libs folder \"" + line.getOptionValue(Options.LIBS) + "\"");

                File[] files = folder.listFiles((pathname) -> pathname.getName().toLowerCase().endsWith(".jar")
                        || pathname.getName().toLowerCase().endsWith(".zip"));
                URL[] urls = new URL[files.length];
                int counter = 0;
                for (File innerFile : files) {
                    urls[counter++] = innerFile.toURI().toURL();
                    Logger.getGlobal().info("Loaded library " + innerFile.getName());
                }
                loader = new URLClassLoader(urls, loader);
                Thread.currentThread().setContextClassLoader(loader);

                /*for (File file : files) {
                    JarFile jar = new JarFile(file);
                    Enumeration<JarEntry> entries = jar.entries();
                    if (entries.hasMoreElements()) {
                        for (JarEntry entry = entries.nextElement(); entries.hasMoreElements(); entry = entries.nextElement()) {
                            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                                String name = entry.getName().substring(0, entry.getName().length() - 6).replaceAll("/", ".");
                                try {
                                    loader.loadClass(name);
                                } catch (ClassNotFoundException e) {
                                    Logger.getGlobal().warning("Failed to load class " + name);
                                }
                            }
                        }
                    }
                }*/

            }

            //DEBUG DISABLED
            //Import the jar into the java runtime so we can use it. Like how bukkit loads plugins.
            //if (!importMinecraft(jarFile, loader)) return;
            //DEBUG DISABLED END

            try {

                Class clazz = Class.forName("net.minecraft.data.DataProvider", true, loader);
            } catch (ClassNotFoundException e) {
                Logger.getGlobal().info("DataProvider not found!");
            }

            Class convClass = Class.forName("com.strangeone101.vanilladatapackgenerator.Converter", true, loader);
            Object convObj = convClass.getConstructor(File.class).newInstance(fileOut);
            Method runMethod = convClass.getDeclaredMethod("run", ClassLoader.class);
            runMethod.invoke(convObj, loader);
            //Converter conv = new Converter(jarFile, fileOut);
            //conv.run(); //Go go go go go
        }
        catch (Exception exp) {
            Logger.getGlobal().severe("Could not complete conversion!");
            exp.printStackTrace();
        }


    }

    /**
     * Imports the jar from file to use at runtime
     * @param jar The jar file
     * @return True if successful
     */
    public static boolean importMinecraft(File jar, ClassLoader loader) {
        try {
            Logger.getGlobal().info("Importing minecraft jar...");
            loader = new JavaClassLoader(jar, loader);
            ((JavaClassLoader)loader).loadMinecraftStuff();

            InputStream stream = loader.getResourceAsStream("version.json");
            if (stream == null) {
                Logger.getGlobal().severe("Could not find version.json file in the jar!");
                Logger.getGlobal().severe("Make sure you are using a version higher than 21w41a!");
                return false;
            }

            JsonObject json = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
            String id = json.get("id").getAsString();
            Logger.getGlobal().info("Imported server jar. Jar version is " + id);

            //TODO Probably remove this bit, since we are using the vanilla system instead of parsing our own now
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
