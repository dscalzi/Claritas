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

package com.dscalzi.claritas.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Pattern;

public class DataUtil {

    public static final Pattern ILLEGAL_VERSION_CHARS = Pattern.compile(".*[@$]+.*");

    private static final Logger log = LoggerFactory.getLogger(DataUtil.class);

    public static final List<String> BLACKLIST = Arrays.asList(
            "common",
            "util",
            "internal",
            "tweaker",
            "tweak"
    );

    public static final List<String> BLACKLIST_WHEN_NOT_ID = Arrays.asList(
            "forge",
            "bukkit",
            "sponge"
    );

    public static List<String> buildBlacklist(@Nullable String id) {
        List<String> ret = new ArrayList<>(BLACKLIST);
        if(id == null) {
            ret.addAll(BLACKLIST_WHEN_NOT_ID);
        } else {
            BLACKLIST_WHEN_NOT_ID.stream().filter(term -> !term.equalsIgnoreCase(id)).forEach(ret::add);
        }
        return ret;
    }

    public static String getPackage(String name) {
        // If the class is not in a package, we cannot use the default package.
        if(!name.contains(".")) {
            return null;
        }
        return name.substring(0, name.lastIndexOf('.')).toLowerCase();
    }

    public static String inferGroupFromPackage(String packageName, @Nullable String id) {

        // If package name comes in as null, return null.
        if(packageName == null) {
            log.debug("Mod {} has null package.", id);
            return null;
        }

        // Linked list removeLast is O(1)
        LinkedList<String> packageBits = new LinkedList<>(Arrays.asList(packageName.split("\\.")));
        List<String> CUSTOM_BLACKLIST = buildBlacklist(id);

        boolean isBadTerm = true;
        while(isBadTerm && !packageBits.isEmpty()) {
            String term = packageBits.getLast();
            if(!Objects.equals(term, id) && !CUSTOM_BLACKLIST.contains(term)) {
                isBadTerm = false;
            } else {
                if(packageBits.size() == 1) {
                    // Don't remove the term if its the last one.
                    isBadTerm = false;
                } else if(Objects.equals(term, id) && packageBits.size() == 2) {
                    // ex. net.mymod, util.mymod
                    isBadTerm = false;
                } else {
                    packageBits.removeLast();
                }

            }
        }

        return String.join(".", packageBits);
    }

    @Nullable
    public static String getNonEmptyStringOrNull(@Nullable String str) {
        if(str == null || str.trim().length() == 0) {
            return null;
        }
        return str;
    }

    @Nullable
    public static String cleanVersion(String version) {
        if(version != null) {
            if(!ILLEGAL_VERSION_CHARS.matcher(version).matches()) {
                return version;
            }
        }
        return null;
    }

    @Nullable
    public static <T> T multiplex(@Nullable T a, @Nullable T b) {
        return a != null ? a : b;
    }

}
