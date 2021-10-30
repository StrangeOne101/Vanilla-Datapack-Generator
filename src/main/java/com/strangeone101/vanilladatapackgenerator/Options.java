package com.strangeone101.vanilladatapackgenerator;

import org.apache.commons.cli.Option;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Options extends org.apache.commons.cli.Options {

    public static Option HELP = new Option("help", "Display all the options");
    public static Option OUTPUT = Option.builder().option("output").argName("directory").hasArg().desc("Specify the output directory").build();
    public static Option JAR = new Option("jar", "file",true, "The minecraft jar to extract from");
    public static Option DEBUG = new Option("debug", "Turn debug mode on");

    public Options() {
        this.addOption(HELP);
        this.addOption(JAR);
        this.addOption(OUTPUT);
        this.addOption(DEBUG);
    }

    public static Collection<Option> all() {
        return List.of(HELP, OUTPUT, JAR, DEBUG);
    }
}
