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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MCVersionUtil {

    public static boolean gt(String left, String right) {
        return compare(left, right) > 0;
    }

    public static boolean gte(String left, String right) {
        return compare(left, right) >= 0;
    }

    public static boolean lt(String left, String right) {
        return compare(left, right) < 0;
    }

    public static boolean lte(String left, String right) {
        return compare(left, right) <= 0;
    }

    public static int compare(String left, String right) {

        List<Integer> lVer = Arrays.stream(left.split("\\.")).map(Integer::parseInt).collect(Collectors.toList());
        List<Integer> rVer = Arrays.stream(right.split("\\.")).map(Integer::parseInt).collect(Collectors.toList());

        for(int i=0; i<Math.min(lVer.size(), rVer.size()); i++) {
            if(lVer.get(i) > rVer.get(i)) {
                return 1;
            } else if(lVer.get(i) < rVer.get(i)) {
                return -1;
            }
        }

        return 0;
    }

}
