package com.strangeone101.vanilladatapackgenerator;

import org.apache.commons.cli.Option;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Options extends org.apache.commons.cli.Options {

    public static Option HELP = new Option("help", "Display all the options");
    public static Option OUTPUT = Option.builder().option("output").argName("directory").hasArg().desc("Specify the output directory").build();
    public static Option LIBS = Option.builder().option("libs").argName("directory").hasArg().desc("Specify the libs directory").build();
    public static Option JAR = Option.builder().option("jar").argName("file").hasArg().desc("The minecraft jar to extract from").required().build();
    public static Option DEBUG = new Option("debug", "Turn debug mode on");

    public Options() {
        this.addOption(HELP);
        this.addOption(JAR);
        this.addOption(OUTPUT);
        this.addOption(DEBUG);
        this.addOption(LIBS);
    }

    public static Collection<Option> all() {
        return List.of(HELP, OUTPUT, JAR, DEBUG, LIBS);
    }
}
