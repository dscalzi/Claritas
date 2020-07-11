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

package com.dscalzi.claritas.resolver.library.liteloader;

import com.dscalzi.claritas.discovery.dto.ModuleMetadata;
import com.dscalzi.claritas.resolver.InterfaceMetadataResolver;
import com.dscalzi.claritas.util.DataUtil;
import com.dscalzi.claritas.util.Tuple;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

public class LiteloaderMetadataResolver extends InterfaceMetadataResolver {

    private static final Pattern MATCHING_INTERFACE = Pattern.compile("^com\\.mumfrey\\.liteloader\\.[^.]+$");

    @Override
    public ModuleMetadata resolveMetadata(InputStream classStream) throws IOException {

        Tuple<String, List<String>> interfaces = getInterfaces(classStream);
        if(interfaces.getValue().stream().anyMatch(i -> MATCHING_INTERFACE.matcher(i).matches())) {
            ModuleMetadata moduleMetadata = new ModuleMetadata();
            moduleMetadata.setGroup(DataUtil.getPackage(interfaces.getKey()));
            return moduleMetadata;
        }

        return null;
    }

}
