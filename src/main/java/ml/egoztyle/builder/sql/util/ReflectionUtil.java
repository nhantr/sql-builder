/*
 * ReflectionUtil.java
 *
 */
package ml.egoztyle.builder.sql.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * ReflectionUtil
 *
 */
public final class ReflectionUtil
{
    private ReflectionUtil()
    {
    }

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final Class< ? >[] EMPTY_CLASS_ARRAY = new Class< ? >[0];
    private static final Method[] EMPTY_METHOD_ARRAY = new Method[0];

    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and no parameters. Searches all superclasses up to {@code Object}.
     * <p>
     * Returns {@code null} if no {@link Method} can be found.
     *
     * @param clazz the class to introspect
     * @param name the name of the method
     * @return the Method object, or {@code null} if none found
     */
    public static Method findMethod(Class< ? > clazz, String name)
    {
        return findMethod(clazz, name, EMPTY_CLASS_ARRAY);
    }


    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and parameter types. Searches all superclasses up to {@code Object}.
     * <p>
     * Returns {@code null} if no {@link Method} can be found.
     *
     * @param clazz the class to introspect
     * @param name the name of the method
     * @param paramTypes the parameter types of the method
     *        (may be {@code null} to indicate any signature)
     * @return the Method object, or {@code null} if none found
     */
    public static Method findMethod(Class< ? > clazz, String name, Class< ? >... paramTypes)
    {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(name, "Method name must not be null");
        Class< ? > searchType = clazz;
        while (searchType != null)
        {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType, false));
            for (Method method : methods)
            {
                if (name.equals(method.getName()) && (paramTypes == null || hasSameParams(method, paramTypes)))
                {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }


    private static boolean hasSameParams(Method method, Class< ? >[] paramTypes)
    {
        return (paramTypes.length == method.getParameterCount() && Arrays.equals(paramTypes, method.getParameterTypes()));
    }


    private static Method[] getDeclaredMethods(Class< ? > clazz, boolean defensive)
    {
        Assert.notNull(clazz, "Class must not be null");
        Method[] result = EMPTY_METHOD_ARRAY;
        try
        {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
            if (defaultMethods != null)
            {
                result = new Method[declaredMethods.length + defaultMethods.size()];
                System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
                int index = declaredMethods.length;
                for (Method defaultMethod : defaultMethods)
                {
                    result[index] = defaultMethod;
                    index++;
                }
            }
            else
            {
                result = declaredMethods;
            }
        }
        catch (Throwable ex)
        {
            throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
        }
        return (result.length == 0 || !defensive) ? result : result.clone();
    }


    private static List<Method> findConcreteMethodsOnInterfaces(Class< ? > clazz)
    {
        List<Method> result = null;
        for (Class< ? > ifc : clazz.getInterfaces())
        {
            for (Method ifcMethod : ifc.getMethods())
            {
                if (!Modifier.isAbstract(ifcMethod.getModifiers()))
                {
                    if (result == null)
                    {
                        result = new ArrayList<>();
                    }
                    result.add(ifcMethod);
                }
            }
        }
        return result;
    }


    /**
     * Invoke the specified {@link Method} against the supplied target object with no arguments.
     * The target object can be {@code null} when invoking a static {@link Method}.
     * <p>
     * Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     *
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @return the invocation result, if any
     * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
     */
    public static Object invokeMethod(Method method, Object target)
    {
        return invokeMethod(method, target, EMPTY_OBJECT_ARRAY);
    }


    /**
     * Invoke the specified {@link Method} against the supplied target object with the
     * supplied arguments. The target object can be {@code null} when invoking a
     * static {@link Method}.
     * <p>
     * Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     *
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @param args the invocation arguments (may be {@code null})
     * @return the invocation result, if any
     */

    public static Object invokeMethod(Method method, Object target, Object... args)
    {
        try
        {
            return method.invoke(target, args);
        }
        catch (Exception ex)
        {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }


    private static void handleReflectionException(Exception ex)
    {
        if (ex instanceof NoSuchMethodException)
        {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException)
        {
            throw new IllegalStateException("Could not access method or field: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException)
        {
            Throwable targetException = ((InvocationTargetException)ex).getTargetException();
            if (targetException instanceof RuntimeException)
            {
                throw (RuntimeException)targetException;
            }
            if (targetException instanceof Error)
            {
                throw (Error)targetException;
            }
            throw new UndeclaredThrowableException(targetException);
        }
        if (ex instanceof RuntimeException)
        {
            throw (RuntimeException)ex;
        }
        throw new UndeclaredThrowableException(ex);
    }
}

/*
 * Changes:
 * $Log: $
 */