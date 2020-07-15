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

import com.dscalzi.claritas.discovery.dto.ModuleMetadata;
import com.dscalzi.claritas.resolver.AnnotationMetadataResolver;
import com.dscalzi.claritas.util.DataUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ForgeMetadataResolver_1_7 extends AnnotationMetadataResolver {

    private static final String A_K_MODID   = "modid";
    private static final String A_K_VERSION = "version";
    private static final String A_K_NAME    = "name";

    public ForgeMetadataResolver_1_7(String annotationClass) {
        super(annotationClass);
    }

    @Override
    public ModuleMetadata resolveMetadata(InputStream classStream) throws IOException {
        return getAnnotations(classStream).stream()
                .filter(a -> a.getClassName().equals(this.targetAnnotation))
                .findFirst()
                .map(a -> {
                    ModuleMetadata md = new ModuleMetadata();
                    for(Map.Entry<String, Object> entry : a.getAnnotationData().entrySet()) {
                        switch (entry.getKey()) {
                            case A_K_MODID:
                                md.setId((String) entry.getValue());
                                break;
                            case A_K_VERSION:
                                md.setVersion((String) entry.getValue());
                                break;
                            case A_K_NAME:
                                md.setName((String) entry.getValue());
                                break;
                        }
                    }
                    md.setGroup(DataUtil.inferGroupFromPackage(DataUtil.getPackage(a.getAnnotatedClassName()), md.getId()));
                    return md;
                })
                .orElse(null);
    }

}
