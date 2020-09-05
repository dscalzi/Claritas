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

package com.dscalzi.claritas.discovery;

import com.dscalzi.claritas.discovery.dto.ModuleMetadata;
import com.dscalzi.claritas.resolver.MetadataResolver;
import com.dscalzi.claritas.resolver.ResolverRegistry;
import com.dscalzi.claritas.resolver.library.LibraryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class LibraryAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(LibraryAnalyzer.class);
    private static final String CLASS_EXTENSION = ".class";

    private final LibraryType type;
    private final String mcVersion;
    private final String absoluteJarPath;

    public LibraryAnalyzer(
            LibraryType type,
            String mcVersion,
            String absoluteJarPath) {
        this.type = type;
        this.mcVersion = mcVersion;
        this.absoluteJarPath = absoluteJarPath;
    }


    private boolean isClassEntry(ZipEntry entry) {
        return entry.getName().toLowerCase().endsWith(CLASS_EXTENSION);
    }

    public ModuleMetadata analyze() {

        ZipFile zipFile;

        try {
            zipFile = new ZipFile(this.absoluteJarPath);
        } catch(IOException e) {
            log.error("IOException while opening jar file.", e);
            return null;
        }

        MetadataResolver resolver = ResolverRegistry.getMetadataResolver(this.type, this.mcVersion);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(this.absoluteJarPath))) {

            resolver.preAnalyze(absoluteJarPath);

            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {

                if(!entry.isDirectory() && isClassEntry(entry)) {
                    try(InputStream is = zipFile.getInputStream(entry)) {

                        ModuleMetadata md = resolver.resolveMetadata(is);
                        if(md != null) {
                            return md;
                        }

                    }
                }

            }
        } catch(IOException e) {
            log.error("IOException while processing jar file {}.", absoluteJarPath, e);
            return null;
        }

        ModuleMetadata md = resolver.getIfNoneFound();
        if(md == null) {
            log.error("Failed to resolve metadata for {}.", this.absoluteJarPath);
        }
        return md;
    }


}
