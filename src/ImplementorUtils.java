import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class ImplementorUtils {
    public static Constructor<?> getConstructor(final Class<?> token) throws ImplerException {
        final List<Constructor<?>> constructors = Arrays.stream(token.getConstructors())
                .filter(c -> Modifier.isPublic(c.getModifiers()))
                .toList();
        if (constructors.isEmpty()) throw new ImplerException("Superclass doesn't have a public constructor");
        return constructors.get(0);
    }

    public static List<Method> getMethods(final Class<?> token) {
        return Arrays.stream(token.getMethods())
                .filter(ImplementorUtils::needImpl)
                .toList();
    }

    public static String getParams(final Executable executable) {
        return String.join(
                ", ",
                IntStream.rangeClosed(0, executable.getParameterTypes().length - 1)
                        .boxed()
                        .map(i -> "arg" + i)
                        .toList()
        );
    }

    public static String getReturn(final Method method) {
        final Class<?> returnType = method.getReturnType();
        if (returnType.equals(void.class)) return "";
        if (returnType.equals(boolean.class)) return " true";
        if (returnType.isPrimitive()) return " 0";
        return " null";
    }

    private static boolean needImpl(final Method method) {
        return Modifier.isAbstract(method.getModifiers()) ||
                method.getDeclaringClass().isInterface() && !method.isDefault();
    }
}
