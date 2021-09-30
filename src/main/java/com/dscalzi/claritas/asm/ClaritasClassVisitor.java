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

package com.dscalzi.claritas.asm;

import com.dscalzi.claritas.asm.dto.AnnotationData;
import com.dscalzi.claritas.util.Tuple;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ClaritasClassVisitor extends ClassVisitor {

    private Type classType;
    private Type superType;
    private final LinkedList<Type> interfaceTypes = new LinkedList<>();
    private final LinkedList<AnnotationData> annotations = new LinkedList<>();

    public ClaritasClassVisitor() {
        super(Opcodes.ASM9);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.classType = Type.getObjectType(name);
        this.superType = superName != null && !superName.isEmpty() ? Type.getObjectType(superName) : null;

        if(interfaces != null) {
            Arrays.stream(interfaces).map(Type::getObjectType).forEach(interfaceTypes::add);
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        Type t = Type.getType(descriptor);

        AnnotationData ann = new AnnotationData(t.getClassName(), this.getClassName());
        annotations.addFirst(ann);

        return new AccumulatingAnnotationVisitor(ann);
    }

    public String getClassName() {
        return classType.getClassName();
    }

    public String getSuperClassName() {
        return superType != null ? superType.getClassName() : null;
    }

    public Tuple<String, List<String>> getInterfaces() {
        return new Tuple<>(getClassName(), interfaceTypes.stream().map(Type::getClassName).collect(Collectors.toList()));
    }

    public LinkedList<AnnotationData> getAnnotations() {
        return this.annotations;
    }

}
