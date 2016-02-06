package sample;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class MyClassAdapter extends ClassNode implements Opcodes{
	int percentage;
	public MyClassAdapter(int percentage) {
		this.percentage = percentage;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Main routing to manipulate the class.
	 * This function do various transform to every methods
	 * and also change the method order.
	 */
	void modify() {
		List<MethodNode> methods = this.methods;
		this.methods = new ArrayList<MethodNode>();
		Iterator<MethodNode> i = methods.iterator();
		MethodNode method = null;
		AddDeadCodeTransformer at = new AddDeadCodeTransformer(null);
		SwapOrderTransformer st = new SwapOrderTransformer(null);
		while (i.hasNext()) {
			method = i.next();
			double rnd = Math.random();
			System.err.println(rnd);
			if (rnd < percentage / 100.0) {
				System.err.println(method.name + " transform");
				at.transform(method);
				st.transform(method);
			}
			this.methods.add(0, method);
		}
	}
	
	/**
	 * clear the class, i.e. make the class empty.
	 */
	void clear() {
		if (attrs != null) attrs.clear();
		if (fields != null) fields.clear();
		if (innerClasses != null) innerClasses.clear();
		if (interfaces != null) interfaces.clear();
		if (invisibleAnnotations != null) invisibleAnnotations.clear();
		if (methods != null) methods.clear();
		if (visibleAnnotations != null) visibleAnnotations.clear();
	}
	
	/**
	 * read class file named <fileName>
	 * @param fileName name of file to read.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	void readClass(String fileName) throws FileNotFoundException, IOException {
		clear();
		ClassReader cr = new ClassReader(new FileInputStream(fileName));
		cr.accept(this, 0);
	}
	
	/**
	 * write class to file named <fileName>
	 * @param fileName
	 * @throws IOException
	 */
	void writeClass(String fileName) throws IOException {
		ClassWriter cw = new ClassWriter(0);
		this.accept(cw);
		DataOutputStream dout = new DataOutputStream(new FileOutputStream(fileName));
		dout.write(cw.toByteArray());
		dout.close();
	}
	
	/**
	 * prototype of method transformer.
	 * @author Administrator
	 *
	 */
	public class MethodTransformer {
		protected MethodTransformer mt;
		public MethodTransformer(MethodTransformer mt) {
			this.mt = mt;
		}
		public void transform(MethodNode mn) {
			if (mt != null) {
				mt.transform(mn);
			}
		}
	}
	
	/**
	 * change the method inserting dead code
	 * @author Administrator
	 *
	 */
	public class AddDeadCodeTransformer extends MethodTransformer {
		private int position;
		public AddDeadCodeTransformer(MethodTransformer mt) {
			super(mt);
			if (mt == null) this.mt = this;
		}
		@SuppressWarnings("unchecked")
		/**
		 * Insert dead code to the given method
		 */
		public void transform(MethodNode mn) {
			ListIterator<AbstractInsnNode> lit = mn.instructions.iterator();
			InsnList ail = new InsnList();
			while (lit.hasNext()) {
				AbstractInsnNode anode = lit.next();
				String expression = InstructionView.expression(anode);
				if (expression.endsWith("LOAD")) {
					ail.add(anode.clone(null));
					mn.maxStack ++;
				}
				if (expression.endsWith("RETURN")) mn.instructions.insertBefore(anode, ail); 
			}
			lit = mn.instructions.iterator();
		}
	}
	/**
	 * manipulate the class by reordering the flow.
	 * @author Administrator
	 *
	 */
	public class SwapOrderTransformer extends MethodTransformer {
		private int position;
		public SwapOrderTransformer(MethodTransformer mt) {
			super(mt);
			if (mt == null) this.mt = this;
		}
		@SuppressWarnings("unchecked")
		/**
		 * change the method by modifying the flow with same meaning.
		 */
		public void transform(MethodNode mn) {
			InsnList il = new InsnList();
			il.add(new VarInsnNode(ILOAD, 1));
			LabelNode label = new LabelNode();
			il.add(new JumpInsnNode(IFLT, label));
			il.add(new VarInsnNode(ALOAD, 0));
			il.add(new VarInsnNode(ILOAD, 1));
			il.add(new FieldInsnNode(PUTFIELD, "pkg/Bean", "f", "I"));
			LabelNode end = new LabelNode();
			il.add(new JumpInsnNode(GOTO, end));
			il.add(label);
			il.add(new FrameNode(F_SAME, 0, null, 0, null));
			il.add(new TypeInsnNode(NEW, "java/lang/IllegalArgumentException"));
			il.add(new InsnNode(DUP));
			il.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V"));
			il.add(new InsnNode(ATHROW));
			il.add(end);
			il.add(new FrameNode(F_SAME, 0, null, 0, null));
			il.add(new InsnNode(RETURN));
			int cnt = mn.instructions.size() / 2;
			InsnList fil = new InsnList();
			InsnList lil = new InsnList();
			int i = 0;
			for (ListIterator<AbstractInsnNode> lit = mn.instructions.iterator(); lit.hasNext(); i++) {
				if (i <= cnt) fil.add(lit.next());
				else lil.add(lit.next());
			}
			mn.instructions = new InsnList();
			LabelNode first = new LabelNode();
			LabelNode mid = new LabelNode();
			if (false) {
				mn.instructions.add(new JumpInsnNode(GOTO, first));
				mn.instructions.add(il);
				mn.instructions.add(mid);
				mn.instructions.add(lil);
				mn.instructions.add(first);
				mn.instructions.add(fil);
				mn.instructions.add(new JumpInsnNode(GOTO, mid));
			} else {
				mn.instructions.add(fil);
				mn.instructions.add(new JumpInsnNode(GOTO, mid));
				mn.instructions.add(il);
				mn.instructions.add(mid);
				mn.instructions.add(lil);
				mn.maxLocals += 10;
			}
			
			Iterator it = mn.localVariables.iterator();
			while (it.hasNext()) {
				LocalVariableNode lvnode = (LocalVariableNode) it.next();
				//System.err.println(lvnode.name);
			} //System.err.println("");
		}
	}
	
	/**
	 * This class has method to print human-readable Instrution Node.
	 * @author Administrator
	 *
	 */
	public static class InstructionView{
		public static String expression(AbstractInsnNode anode) {
			Field[] fields = Opcodes.class.getDeclaredFields();
			int start = 0;
			while (start < fields.length) {
				if (fields[start].getName().equals("NOP")) break;
				start++;
			}
			StringBuilder sb = new StringBuilder();
			int opcode = anode.getOpcode();
			if (opcode < 0) return anode.toString();
			for (int i = start; i < fields.length; i++) {
				int val;
				try {
					val = fields[i].getInt(fields[i]);
				} catch (Exception e) {
					continue;
				} 
				if (val == opcode) {
					sb.append(fields[i].getName());
					break;
				}
			}
			return sb.toString();
		}
	}
	
	/**
	 * Program entry.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		if (args.length < 0) {
			System.out.println("arguments: classfilename [percentage]");
		}
		String fileName = args[0];
		String outputFileName = "modified_" + fileName;
		
		int percentage = 100;
		if (args.length > 1)  percentage = Math.min(100, Integer.valueOf(args[1]));
		MyClassAdapter adapter = new MyClassAdapter(percentage);
		adapter.readClass(fileName);
		adapter.modify();
		adapter.writeClass(outputFileName);
	}
}
