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

package com.dscalzi.claritas.resolver.library.forge;

import com.dscalzi.claritas.asm.ClaritasClassVisitor;
import com.dscalzi.claritas.discovery.dto.ModuleMetadata;
import com.dscalzi.claritas.discovery.dto.forge.ForgeMetadata_1_7;
import com.dscalzi.claritas.discovery.dto.forge.ForgeModType_1_7;
import com.dscalzi.claritas.discovery.dto.internal.TweakMetaFile;
import com.dscalzi.claritas.resolver.MetadataResolver;
import com.dscalzi.claritas.util.DataUtil;
import com.dscalzi.claritas.util.Tuple;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ForgeMetadataResolver_1_7 extends MetadataResolver {

    private static final Logger logger = LoggerFactory.getLogger(ForgeMetadataResolver_1_7.class);

    private static final String A_STANDARD_K_MODID   = "modid";
    private static final String A_STANDARD_K_VERSION = "version";
    private static final String A_STANDARD_K_NAME    = "name";

    private static final String A_COREMOD_K_VALUE   = "value";

    private final String standardAnnotation;
    private final String coreModBase;
    private final String coreModAnnotation;
    private final String dummyContainerClass;

    // Core Mods
    private boolean isCoreMod;
    public final ForgeMetadata_1_7 coreModMetadata = new ForgeMetadata_1_7(ForgeModType_1_7.CORE_MOD);

    // Tweakers
    private boolean isTweaker;
    private final ForgeMetadata_1_7 tweakMetadata = new ForgeMetadata_1_7(ForgeModType_1_7.TWEAKER);

    public ForgeMetadataResolver_1_7(String standardAnnotation, String coreModBase, String coreModAnnotation, String dummyContainerClass) {
        this.standardAnnotation = standardAnnotation;
        this.coreModBase = coreModBase;
        this.coreModAnnotation = coreModAnnotation;
        this.dummyContainerClass = dummyContainerClass;
    }

    @Override
    public void preAnalyze(String absoluteJarPath) {
        if(absoluteJarPath.endsWith(".jar")) {
            try {
                JarFile jar = new JarFile(absoluteJarPath);
                Manifest manifest = jar.getManifest();
                if(manifest != null) {
                    this.isCoreMod = manifest.getMainAttributes().getValue("FMLCorePlugin") != null;
                    this.isTweaker = manifest.getMainAttributes().getValue("TweakClass") != null;
                    if(isTweaker) {
                        fetchTweakerMeta(absoluteJarPath, manifest.getMainAttributes());
                    }
                }
            } catch(IOException e) {
                logger.error("IO exception while pre-analyzing {}", absoluteJarPath);
            }
        } else {
            logger.warn("Resolve invoked on non-jar file {}", absoluteJarPath);
        }
    }

    private void fetchTweakerMeta(String absoluteJarPath, Attributes mainAttributes) {
        // TweakMetaFile is not standard but some mods have it anyway.
        // https://github.com/MinecraftForge/MinecraftForge/pull/2892

        String manifestTweakClass = mainAttributes.getValue("TweakClass");
        String manifestTweakName = DataUtil.getNonEmptyStringOrNull(mainAttributes.getValue("TweakName"));
        String manifestTweakVersion = DataUtil.getNonEmptyStringOrNull(mainAttributes.getValue("TweakVersion"));
        String manifestTweakMetaFile = DataUtil.getNonEmptyStringOrNull(mainAttributes.getValue("TweakMetaFile"));

        String tweakMetaId = null;
        String tweakMetaName = null;
        String tweakMetaVersion = null;

        if(manifestTweakMetaFile != null) {
            try {
                ZipFile zipFile = new ZipFile(absoluteJarPath);

                try (ZipInputStream zis = new ZipInputStream(new FileInputStream(absoluteJarPath))) {
                    ZipEntry entry;
                    while((entry = zis.getNextEntry()) != null) {

                        if(!entry.isDirectory() && entry.getName().endsWith(manifestTweakMetaFile)) {
                            try(InputStream is = zipFile.getInputStream(entry)) {
                                TweakMetaFile tweakMetaFile = (new Gson()).fromJson(new InputStreamReader(is), TweakMetaFile.class);
                                tweakMetaId = DataUtil.getNonEmptyStringOrNull(tweakMetaFile.getId());
                                tweakMetaName = DataUtil.getNonEmptyStringOrNull(tweakMetaFile.getName());
                                tweakMetaVersion = DataUtil.getNonEmptyStringOrNull(tweakMetaFile.getVersion());
                            }
                        }
                    }
                } catch(IOException e) {
                    logger.error("IOException while processing jar file {}.", absoluteJarPath, e);
                }

            } catch(IOException e) {
                logger.error("IO exception while pulling tweak meta file {} from {}.", manifestTweakMetaFile, absoluteJarPath);
            }
        }

        this.tweakMetadata.setId(tweakMetaId == null ? (tweakMetaName == null ? (manifestTweakName == null ? null : manifestTweakName.toLowerCase()) : tweakMetaName.toLowerCase()) : tweakMetaId);
        this.tweakMetadata.setName(DataUtil.multiplex(tweakMetaName, manifestTweakName));
        this.tweakMetadata.setVersion(DataUtil.multiplex(tweakMetaVersion, manifestTweakVersion));
        this.tweakMetadata.setGroup(DataUtil.inferGroupFromPackage(DataUtil.getPackage(manifestTweakClass), this.tweakMetadata.getId()));

    }

    @Override
    public ModuleMetadata resolveMetadata(InputStream classStream) throws IOException {
        ClaritasClassVisitor cv = this.getClaritasClassVisitor(classStream);

        // Standard mods have @Mod annotation.
        ForgeMetadata_1_7 fromModAnn = cv.getAnnotations().stream()
                .filter(a -> a.getClassName().equals(this.standardAnnotation))
                .findFirst()
                .map(a -> {
                    ForgeMetadata_1_7 md = new ForgeMetadata_1_7();
                    md.setModType(ForgeModType_1_7.MOD);
                    for(Map.Entry<String, Object> entry : a.getAnnotationData().entrySet()) {
                        switch (entry.getKey()) {
                            case A_STANDARD_K_MODID:
                                md.setId((String) entry.getValue());
                                break;
                            case A_STANDARD_K_VERSION:
                                md.setVersion(DataUtil.cleanVersion((String) entry.getValue()));
                                break;
                            case A_STANDARD_K_NAME:
                                md.setName((String) entry.getValue());
                                break;
                        }
                    }
                    md.setGroup(DataUtil.inferGroupFromPackage(DataUtil.getPackage(a.getAnnotatedClassName()), md.getId()));
                    return md;
                })
                .orElse(null);
        if(fromModAnn != null) {
            return fromModAnn;
        }

        if(isCoreMod) {
            if(this.coreModMetadata.getId() == null) {
                cv.getAnnotations().stream()
                        .filter(a -> a.getClassName().equals(this.coreModAnnotation))
                        .findFirst()
                        .ifPresent(a -> {
                            this.coreModMetadata.setId(DataUtil.getNonEmptyStringOrNull((String) a.getAnnotationData().get(A_COREMOD_K_VALUE)));
                            this.coreModMetadata.setGroup(DataUtil.inferGroupFromPackage(DataUtil.getPackage(a.getAnnotatedClassName()), this.coreModMetadata.getId()));
                        });
            }
            if(this.coreModMetadata.getGroup() == null) {

                // Try by looking for annotation.
                cv.getAnnotations().stream().filter(a -> a.getClassName().contains(this.coreModBase))
                        .findFirst()
                        .ifPresent(a -> this.coreModMetadata.setGroup(DataUtil.inferGroupFromPackage(DataUtil.getPackage(a.getAnnotatedClassName()), this.coreModMetadata.getId())));

                // Try by looking for implements
                if(this.coreModMetadata.getGroup() == null) {
                    Tuple<String, List<String>> interfaces = cv.getInterfaces();
                    interfaces.getValue().stream().filter(i -> i.equals(this.coreModBase))
                            .findFirst()
                            .ifPresent(i -> this.coreModMetadata.setGroup(DataUtil.inferGroupFromPackage(DataUtil.getPackage(interfaces.getKey()), this.coreModMetadata.getId())));
                }

                // Try looking for extends
                if(this.coreModMetadata.getGroup() == null) {
                    if(Objects.equals(cv.getSuperClassName(), this.dummyContainerClass)) {
                        this.coreModMetadata.setGroup(DataUtil.inferGroupFromPackage(DataUtil.getPackage(cv.getClassName()), this.coreModMetadata.getId()));
                    }
                }
            }
        }

        return null;
    }

    @Override
    public ModuleMetadata getIfNoneFound() {
        if(isCoreMod) {
            return this.coreModMetadata;
        } else if(isTweaker) {
            return this.tweakMetadata;
        } else {
            return new ForgeMetadata_1_7(ForgeModType_1_7.UNKNOWN);
        }
    }
}
