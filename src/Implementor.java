import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Objects;

import java.lang.reflect.Modifier;

/*
 *  TODO: 07.03.2023
 *   Добавить выбрасывание исключений у методов/конструкторов если это требуется.
 *   Сделать writeHeader(Executable) вместо того, что есть
 *   Сделать writeAnnotation вместо того, что есть
 *   Попробовать добавить дженерики
 */

public class Implementor implements Impler {
    final static String CLASS_SUFFIX = "Impl";
    final static String FILE_SUFFIX = ".java";

    public static void main(String[] args) {
        try {
            checkConsoleArgs(args);
            final Impler implementor = new Implementor();
            final Class<?> token = getClassToken(args[0]);
            final Path root = Path.of(args[1]);
            implementor.implement(token, root);
        } catch (ImplerException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void implement(final Class<?> token, final Path root) throws ImplerException {
        checkImplementInput(token, root);
        createClassFile(token, root);
        implementClass(token, root);
    }

    private static void checkConsoleArgs(final String[] args) throws ImplerException {
        if (Objects.isNull(args) || args.length != 2) throw new ImplerException("Usage: [class] [root directory]");
    }

    private static Class<?> getClassToken(final String clazz) throws ImplerException {
        try {
            return Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            throw new ImplerException("Incorrect class", e);
        }
    }

    private void checkImplementInput(final Class<?> token, final Path root) throws ImplerException {
        if (Objects.isNull(root)) {
            throw new ImplerException("Root can not be null");
        }
        if (Objects.isNull(token)) {
            throw new ImplerException("Token can not be null");
        }
        if (token.isPrimitive() || token.isArray() || token.isAnnotation() || token.isEnum()) {
            throw new ImplerException("Incorrect class token: ", token);
        }
        if (Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Impossible to extend private class");
        }
        if (Modifier.isFinal(token.getModifiers())) {
            throw new ImplerException("Impossible to extend final class");
        }
    }

    private void createClassFile(final Class<?> token, final Path root) throws ImplerException {
        final Path classFile = getClassFilePath(token, root);
        try {
            Files.createDirectories(classFile.getParent());
            Files.createFile(classFile);
        } catch (IOException e) {
            throw new ImplerException("I/O error occurs or the parent directory does not exist", e);
        }
    }

    private Path getClassFilePath(final Class<?> token, final Path root) {
        final String classDir = getClassDirectory(token);
        final String classFile = getClassFileName(token);
        return root.resolve(classDir).resolve(classFile);
    }

    private String getClassDirectory(final Class<?> token) {
        return token.getPackageName().replace('.', File.separatorChar);
    }

    private String getClassFileName(final Class<?> token) {
        return getClassName(token) + FILE_SUFFIX;
    }

    private String getClassName(final Class<?> token) {
        return token.getSimpleName() + CLASS_SUFFIX;
    }

    private void implementClass(final Class<?> token, final Path root) throws ImplerException {
        final Path classFile = getClassFilePath(token, root);
        try (final Writer writer = Files.newBufferedWriter(classFile)) {
            new ClassWriter(writer, token, getClassName(token)).writeClass();
        } catch (IOException e) {
            throw new ImplerException("File writting error", e);
        }
    }
}
