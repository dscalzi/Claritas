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
    private final LinkedList<Type> interfaceTypes = new LinkedList<>();
    private final LinkedList<AnnotationData> annotations = new LinkedList<>();

    public ClaritasClassVisitor() {
        super(Opcodes.ASM8);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.classType = Type.getObjectType(name);
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

    public Tuple<String, List<String>> getInterfaces() {
        return new Tuple<>(getClassName(), interfaceTypes.stream().map(Type::getClassName).collect(Collectors.toList()));
    }

    public LinkedList<AnnotationData> getAnnotations() {
        return this.annotations;
    }

}
