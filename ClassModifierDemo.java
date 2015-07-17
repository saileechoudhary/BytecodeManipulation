package com;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassModifierDemo {
	
		
		public static String fileName;
		
        
        public static class ModifierMethodWriter extends MethodVisitor{
        
        
        private static final String NOP = null;
		private String methodName;
		public MethodVisitor methodVisitor;
        
        public ModifierMethodWriter(int api, MethodVisitor mv, String methodName) {
            super(api, mv);
            this.methodName=methodName;
        }
        NameGenerator r = new NameGenerator();
        String new1=r.generateRandomString();
        //This is the point we insert the code. Note that the instructions are added right after
        //the visitCode method of the super class. This ordering is very important.
        @Override
        public void visitCode() {
        	
        	//this.visitCode();
        	//this.visitFieldInsn(Opcodes.NOP, "java/lang/System", "out", "null");
        	this.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            this.visitLdcInsn("method: "+methodName+""+new1 +"");
            this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
            this.visitFieldInsn(Opcodes.NOP, "java/lang/System", "out", "null");
            
        	super.visitCode();
        	super.visitFieldInsn(Opcodes.NOP, "java/lang/System", "out", "null");
        	super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitLdcInsn("method: "+methodName+""+new1 +"");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
            //super.visitFieldInsn(Opcodes.NOP, "java/lang/System", "out", "null");
            
        }

        
        
    }
    
    //Our class modifier class visitor. It delegate all calls to the super class
    //Only makes sure that it returns our MethodVisitor for every method
    public static class ModifierClassWriter extends ClassVisitor{
        private int api;
      NameGenerator r = new NameGenerator();
      String new2=r.generateRandomString();
        
        public ModifierClassWriter(int api, ClassWriter cv) {
            super(api, cv);
            this.api=api;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                String signature, String[] exceptions) {

            MethodVisitor mv= super.visitMethod(access, name+new2, desc, signature, exceptions);
            ModifierMethodWriter mvw=new ModifierMethodWriter(api, mv, name+new2);
           
           // MethodVisitor methodVisitor = cv.visitMethod(access, name+"_new", desc, signature, exceptions);         
	        return mvw;
	        //return methoVisitor;
	        
           
        }
        
       // @Override
	    //public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
	      //  MethodVisitor methodVisitor = cv.visitMethod(access, name+"_new", desc, signature, exceptions);         
	        //return methodVisitor;
	    //}
        
        
    }
    

 public static void main(String[] args) throws IOException {
        
        if(args.length == 0) {
  	System.out.println("you need to pass argument <.class file>");
        }
        String fileName = args[0];
        //String new1="abc";
		//String dir =args[1];
        //String dir = fileName;
       // String dir = null;
        
        //new File(dir).mkdir();
        
        String outputFileName = "modified_" + fileName;
       
        System.out.println(fileName);
        
        InputStream in=Accessor.class.getResourceAsStream(fileName);
        ClassReader classReader=new ClassReader(in);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);
        
        //Wrap the ClassWriter with our custom ClassVisitor
        ModifierClassWriter mcw=new ModifierClassWriter(Opcodes.ASM4, cw);
        classReader.accept(mcw, 0);
        
          
        
        //Change the path as per needs 
        File outputDir=new File("out");
        outputDir.mkdirs();
        DataOutputStream dout=new DataOutputStream(new FileOutputStream(new File(outputDir,outputFileName)));
        
        //System.out.println(cw.toString());
        
               dout.write(cw.toByteArray());

}
}