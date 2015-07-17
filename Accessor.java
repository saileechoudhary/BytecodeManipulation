package com;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Accessor{

	public static void main(String[] args) throws IOException {
		ClassVisitor cl = new ClassVisitor(Opcodes.ASM4) {

			// Called when a class is visited. This is the method called first
			@Override
			public void visit(int version, int access, String name,
					String signature, String superName, String[] interfaces) {

				System.out.println("Visiting class: " + name);
				System.out.println("Class Major Version: " + version);
				System.out.println("Super class: " + superName);
				NameGenerator r = new NameGenerator();
				String new1 = r.generateRandomString();
				super.visit(version, access, name + new1, signature, superName,
						interfaces);
			}

			// Invoked when the inner class is visited
			@Override
			public void visitOuterClass(String owner, String name, String desc) {
				System.out.println("Outer class: " + owner);
				super.visitOuterClass(owner, name, desc);
			}

			// Invoked when a class level annotation is encountered
			@Override
			public AnnotationVisitor visitAnnotation(String desc,
					boolean visible) {
				System.out.println("Annotation: " + desc);
				return super.visitAnnotation(desc, visible);
			}

			// on encountering a class attribute
			@Override
			public void visitAttribute(Attribute attr) {
				System.out.println("Class Attribute: " + attr.type);
				super.visitAttribute(attr);
			}

			// When an inner class is encountered

			@Override
			public void visitInnerClass(String name, String outerName,
					String innerName, int access) {
				NameGenerator r = new NameGenerator();
				String new2 = r.generateRandomString();
				System.out.println("Inner Class: " + innerName + " defined in "
						+ outerName);
				super.visitInnerClass(name, outerName + new2, innerName, access);
			}

			// when a field is encountered
			@Override
			public FieldVisitor visitField(int access, String name,
					String desc, String signature, Object value) {
				System.out.println("Field: " + name + " " + desc + " value:"
						+ value);
				NameGenerator r = new NameGenerator();
				String new3 = r.generateRandomString();
				return super.visitField(access, name + new3, desc, signature,
						value);
			}

			@Override
			public void visitEnd() {
				System.out.println("Method ends here");
				super.visitEnd();
			}

			// When a method is encountered

			@Override
			public MethodVisitor visitMethod(int access, String name,
					String desc, String signature, String[] exceptions) {
				System.out.println("Method: " + name + " " + desc);
				// return super.visitMethod(access, name, desc, signature,
				// exceptions);
				return super.visitMethod(access, name, desc, signature,
						exceptions);

			}

		};
		InputStream in = Accessor.class.getResourceAsStream("");
		ClassReader classReader = new ClassReader(in);
		classReader.accept(cl, 0);

	}

}