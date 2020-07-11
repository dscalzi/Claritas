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

public class ForgeMetadataResolver_1_13 extends AnnotationMetadataResolver {

    private static final String A_K_MODID = "value";

    public ForgeMetadataResolver_1_13(String targetAnnotation) {
        super(targetAnnotation);
    }

    @Override
    public ModuleMetadata resolveMetadata(InputStream classStream) throws IOException {
        return getAnnotations(classStream).stream()
                .filter(a -> a.getClassName().equals(this.targetAnnotation))
                .findFirst()
                .map(a -> {
                    ModuleMetadata md = new ModuleMetadata();
                    md.setGroup(DataUtil.getPackage(a.getAnnotatedClassName()));
                    for(Map.Entry<String, Object> entry : a.getAnnotationData().entrySet()) {
                        if(entry.getKey().equals(A_K_MODID)) {
                            md.setId((String)entry.getValue());
                        }
                    }
                    return md;
                })
                .orElse(null);
    }

}
