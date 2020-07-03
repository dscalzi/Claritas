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

import com.dscalzi.claritas.discovery.LibraryAnalyzer;
import com.dscalzi.claritas.discovery.dto.ModuleMetadata;
import com.dscalzi.claritas.library.LibraryType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AsmTests {

    private static final Logger log = LoggerFactory.getLogger(AsmTests.class);

    @Test
    public void generalTest() throws IOException {


        LibraryAnalyzer analyzer = new LibraryAnalyzer(
                LibraryType.FORGE,
                "1.12",
                "D:\\TestRoot113\\servers\\Test-1.12.2\\forgemods\\DynamicSurroundings.jar");

        ModuleMetadata md = analyzer.analyze();
        System.out.println(md);

//        ZipFile f = new ZipFile("D:\\TestRoot113\\servers\\Test-1.12.2\\forgemods\\DynamicSurroundings.jar");
//
//        try(InputStream target = f.getInputStream(f.getEntry("org/blockartistry/DynSurround/DSurround.class"))) {
//
//            ClassVisitor cv = new LibraryClassVisitor();
//            ClassReader cr = new ClassReader(target);
//            cr.accept(cv, 0);
//
//            //AnnotationVisitor av = cv.visitAnnotation("Mod", true);
//
//        }


    }

}
