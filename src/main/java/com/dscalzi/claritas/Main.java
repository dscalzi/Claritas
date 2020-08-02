/*
 * This file is part of Claritas, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 Daniel D. Scalzi <https://github.com/dscalzi/Claritas>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.dscalzi.claritas;

import com.dscalzi.claritas.discovery.LibraryAnalyzer;
import com.dscalzi.claritas.discovery.dto.ModuleMetadata;
import com.dscalzi.claritas.exception.UnknownLibraryException;
import com.dscalzi.claritas.resolver.library.LibraryType;
import com.dscalzi.claritas.util.FileUtil;
import com.google.gson.Gson;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {

    public static final String VM_OPTION_ARG_FILE = "claritas.argFile";

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        String[] workingArgs = resolveArgSource(args);

        OptionParser parser = new OptionParser();
        OptionSpec<String> absoluteJarPathsOpt = parser.accepts("absoluteJarPaths").withRequiredArg();
        OptionSpec<String> libraryTypeOpt = parser.accepts("libraryType").withRequiredArg();
        OptionSpec<String> mcVersionOpt = parser.accepts("mcVersion").withRequiredArg();
        OptionSpec<String> outputFileOpt = parser.accepts("outputFile").withOptionalArg().defaultsTo("./output.json");
        OptionSpec<Boolean> previewOutputOpt = parser.accepts("previewOutput").withOptionalArg().ofType(Boolean.class).defaultsTo(false);

        OptionSet options = parser.parse(workingArgs);

        String libRaw = options.valueOf(libraryTypeOpt);
        final LibraryType type;
        try {
            type = LibraryType.valueOf(libRaw);
        } catch(IllegalArgumentException e) {
            throw new UnknownLibraryException(libRaw);
        }

        final String mcVersion = options.valueOf(mcVersionOpt);
        final String[] absoluteJarPaths = options.valueOf(absoluteJarPathsOpt).split(",");

        String outputFile = options.valueOf(outputFileOpt);
        if(!outputFile.endsWith(".json")) {
            outputFile += ".json";
        }
        final File realOutputFile = new File(outputFile);
        FileUtil.ensureParentsExist(realOutputFile);

        log.debug("TYPE       = {}", type.name());
        log.debug("MC VERSION = {}", mcVersion);
        log.debug("JAR PATHS  = {}", (Object) absoluteJarPaths);
        log.debug("OUTPUT     = {}", outputFile);

        Map<String, ModuleMetadata> results = new LinkedHashMap<>();
        for(String pth : absoluteJarPaths) {
            LibraryAnalyzer analyzer = new LibraryAnalyzer(type, mcVersion, pth);
            ModuleMetadata md = analyzer.analyze();
            results.put(pth.replace("\\\\", "\\"), md);
        }

        try(FileWriter writer = new FileWriter(realOutputFile)) {
            new Gson().toJson(results, writer);
            log.info("Result saved to {}", FileUtil.getNormalizedAbsolute(realOutputFile));
            if(options.valueOf(previewOutputOpt)) {
                System.out.println("results::" + new Gson().toJson(results));
            }
        } catch(Exception e) {
            log.error("Failed to save output.", e);
            System.exit(-1);
        }

    }

    protected static String[] resolveArgSource(String[] mainArgs) {

        String argFilePath = System.getProperty(VM_OPTION_ARG_FILE);
        if(argFilePath == null) {
            return mainArgs;
        }

        final File argFile = new File(argFilePath);
        if(!argFile.exists()) {
            log.error("Resolved argFile: {}", FileUtil.getNormalizedAbsolute(argFile));
            throw new IllegalArgumentException(String.format("ArgFile %s does not exist!", argFilePath));
        }

        try {
            log.info("Pulling arguments from argFile at {}", FileUtil.getNormalizedAbsolute(argFile));
            String argFileContent = new String(Files.readAllBytes(argFile.toPath()));
            return argFileContent.split("\\r?\\n");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("IOException while processing argFile");
            System.exit(-1);
            return null;
        }

    }

}
