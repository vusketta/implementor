import java.io.IOException;
import java.io.Writer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;

import java.util.List;
import java.util.stream.IntStream;

public class ClassWriter {
    private final Writer writer;
    private final Class<?> superClass;
    private final String className;

    public ClassWriter(final Writer writer, final Class<?> token, final String className) {
        this.writer = writer;
        this.superClass = token;
        this.className = className;
    }

    public void writeClass() throws IOException, ImplerException {
        writePackage();
        writeIdentifier();
        writeInheritance();
        writeClassBody();
    }

    private void writePackage() throws IOException {
        final String classPackage = superClass.getPackageName();
        if (!classPackage.isEmpty()) {
            write("package " + classPackage + ";");
            writeNewLine();
        }
    }

    private void writeIdentifier() throws IOException {
        write("public class " + className);
    }

    private void writeInheritance() throws IOException {
        final String inheritance = superClass.isInterface() ? "implements" : "extends";
        write(" " + inheritance + " " + superClass.getSimpleName());
    }

    private void writeClassBody() throws IOException, ImplerException {
        writeOpenBlock();
        writeConstructor();
        writeMethods();
        writeNewLine();
        writeCloseBlock();
    }

    private void writeConstructor() throws IOException, ImplerException {
        if (superClass.isInterface()) return;
        final Constructor<?> constructor = ImplementorUtils.getConstructor(superClass);
        writeExecutable(constructor);
    }

    private void writeMethods() throws IOException {
        final List<Method> methods = ImplementorUtils.getMethods(superClass);
        for (final Method method : methods) {
            writeExecutable(method);
        }
    }

    private void writeExecutable(final Executable executable) throws IOException {
        writeIndent(1);
        if (executable instanceof Constructor<?> constructor) {
            write("public ");
            write(className);
            writeParams(constructor);
            writeConstructorBody(constructor);
        } else if (executable instanceof Method method) {
            write("@Override");
            writeNewLine();
            writeIndent(1);
            write("public ");
            write(method.getReturnType().getSimpleName() + " " + method.getName());
            writeParams(method);
            writeMethodBody(method);
        }
        writeNewLine();
    }

    private void writeParams(final Executable executable) throws IOException {
        write("(");
        final Class<?>[] params = executable.getParameterTypes();
        for (int i = 0; i < params.length - 1; i++) {
            write(params[i].getSimpleName() + " arg" + i + ", ");
        }
        if (params.length != 0) write(params[params.length - 1].getSimpleName() + " arg" + (params.length - 1));
        write(")");
    }

    private void writeConstructorBody(final Constructor constructor) throws IOException {
        writeOpenBlock();
        writeIndent(2);
        write("super(");
        write(ImplementorUtils.getParams(constructor));
        write(");");
        writeNewLine();
        writeIndent(1);
        writeCloseBlock();
    }

    private void writeMethodBody(final Method method) throws IOException {
        writeOpenBlock();
        writeIndent(2);
        final String defaultReturn = ImplementorUtils.getReturn(method);
        if (!defaultReturn.isEmpty()) {
            write("return");
            write(defaultReturn);
            write(";");
        }
        writeNewLine();
        writeIndent(1);
        writeCloseBlock();
    }

    private void writeIndent(final int times) throws IOException {
        write("\t".repeat(times));
    }

    private void writeOpenBlock() throws IOException {
        write(" {");
        writeNewLine();
    }

    private void writeCloseBlock() throws IOException {
        write("}");
    }

    private void writeNewLine() throws IOException {
        write(System.lineSeparator());
    }

    private void write(final String str) throws IOException {
        writer.write(str);
    }
}
