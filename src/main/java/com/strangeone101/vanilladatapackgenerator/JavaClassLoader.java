package com.strangeone101.vanilladatapackgenerator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;

public class JavaClassLoader extends URLClassLoader {

    static {
        //ClassLoader.registerAsParallelCapable();
    }

    public JavaClassLoader(File jar) throws IOException, ClassNotFoundException {
        super(new URL[]{jar.toURI().toURL()}, Main.class.getClassLoader());

        JarFile jarjar = new JarFile(jar);
        Manifest manifest = jarjar.getManifest();
        String main = manifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
        load(main); //Load the main class specified from the manifest file of the jar

        //Everything on from this point on SHOULDNT be needed, but I used it to try fix the issue. I was unsuccessful

        Enumeration<JarEntry> entries = jarjar.entries();

        List<String> whitelist = List.of("net.minecraft.data");

        if (entries.hasMoreElements()) {
            for (JarEntry entry = entries.nextElement(); entries.hasMoreElements(); entry = entries.nextElement()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    try {
                        /* this "true" in the line below is the whole reason this class is necessary; it makes the URLClassLoader this class extends "resolve" the class,
                         * meaning it also loads all the classes this class refers to */
                        String name = entry.getName().substring(0, entry.getName().length() - 6).replaceAll("/", ".");
                        for (String whitename : whitelist) {
                            if (name.startsWith(whitename)) { //If the name of the class is in the whitelisted packages list
                                Class<?> clazz = loadClass(name, true);
                                Logger.getGlobal().info("Loaded class " + entry.getName());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Thread.currentThread().setContextClassLoader(this);
    }

    public void load(String name) throws ClassNotFoundException {
        Class<?> clazz = super.loadClass(name, true);
        Logger.getGlobal().info("Loaded " + name);
    }
}
