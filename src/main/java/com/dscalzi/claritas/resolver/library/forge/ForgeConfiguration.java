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

import com.dscalzi.claritas.resolver.MetadataResolver;
import com.dscalzi.claritas.resolver.ResolverConfiguration;
import com.dscalzi.claritas.util.MCVersionUtil;

public class ForgeConfiguration extends ResolverConfiguration {

    public static final String A_1_7 = "cpw.mods.fml.common";
    public static final String A_1_8_to_Plus = "net.minecraftforge.fml.common.Mod";

    @Override
    public MetadataResolver getResolver() {
        if(MCVersionUtil.gte(this.mcVersion, "1.13")) {
            return new ForgeMetadataResolver_1_13(A_1_8_to_Plus);
        } else if(MCVersionUtil.gte(this.mcVersion, "1.8")) {
            return new ForgeMetadataResolver_1_7(A_1_8_to_Plus);
        } else if(MCVersionUtil.gte(this.mcVersion, "1.7")) {
            return new ForgeMetadataResolver_1_7(A_1_7);
        } else {
            throw new UnsupportedOperationException("Unsupported version: " + this.mcVersion);
        }
    }

}
