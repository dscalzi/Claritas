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

import com.dscalzi.claritas.util.DataUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DataUtilTests {

    private static final Logger log = LoggerFactory.getLogger(DataUtilTests.class);

    @Test
    public void testGroupInference() {

        Map<String[], String> dataMap = new HashMap<>();
        dataMap.put(new String[]{"com.someauthor.somemod.MainModClass", "somemod"}, "com.someauthor"); // Great
        dataMap.put(new String[]{"author.mod.ThisClass", "mod"}, "author.mod"); // Meh
        dataMap.put(new String[]{"mymodname.MainClass", "mymodname"}, "mymodname"); // Sad
        dataMap.put(new String[]{"com.author.mod.util.MainClass", "mod"}, "com.author"); // Acceptable
        dataMap.put(new String[]{"com.author.util.MainClass", "mod"}, "com.author"); // Meh
        dataMap.put(new String[]{"mod.util.MainClass", "mod"}, "mod"); // Sad
        dataMap.put(new String[]{"com.mod.util.common.AClass", "mod"}, "com.mod"); // Meh
        dataMap.put(new String[]{"com.someauthor.somemod.common.MainModClass", "somemod"}, "com.someauthor"); // Great

        dataMap.put(new String[]{"com.dscalzi.skychanger.bukkit.internal.MainClass", "skychanger"}, "com.dscalzi");
        dataMap.put(new String[]{"com.dscalzi.skychanger.forge.MainClass", "skychanger"}, "com.dscalzi");

        dataMap.put(new String[]{"guichaguri.betterfps.tweaker.BetterFpsTweaker", "betterfps"}, "guichaguri.betterfps");

        // No id
        dataMap.put(new String[]{"com.someauthor.somemod.common.MainModClass", null}, "com.someauthor.somemod"); // Great
        dataMap.put(new String[]{"com.mymod.MyClass", null}, "com.mymod"); // Meh
        dataMap.put(new String[]{"com.author.TheClass", null}, "com.author"); // Great
        dataMap.put(new String[]{"mod.util.MainClass", null}, "mod"); // Sad
        dataMap.put(new String[]{"mymodname.MainClass", null}, "mymodname"); // Sad

        for(Map.Entry<String[], String> entry : dataMap.entrySet()) {
            Assertions.assertEquals(
                    entry.getValue(),
                    DataUtil.inferGroupFromPackage(DataUtil.getPackage(entry.getKey()[0]), entry.getKey()[1])
            );
        }

    }

    @Test
    public void testVersionClean() {

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("@VERSION@", null);
        dataMap.put("1.2.2", "1.2.2");
        dataMap.put("${whyWouldIBeAVersionLOL}", null);

        for(Map.Entry<String, String> entry : dataMap.entrySet()) {
            Assertions.assertEquals(
                    entry.getValue(),
                    DataUtil.cleanVersion(entry.getKey())
            );
        }

    }

}
