/*
 * BuilderUtil.java
 *
 */
package ml.egoztyle.builder.sql.util;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ml.egoztyle.builder.sql.SerializableFunction;


/**
 * BuilderUtil
 *
 */
public final class BuilderUtil
{
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("(?<=[a-z])[A-Z]");

    private BuilderUtil()
    {
    }

    /**
     * get DB field name
     *
     * @param <T>
     * @param fieldGetterName the getter method of field
     * @return field name in DB
     */
    public static <T> String getDBField(SerializableFunction<T, ?> fieldGetterName)
    {
        Method findMethod = ReflectionUtil.findMethod(fieldGetterName.getClass(), "writeReplace");
        findMethod.setAccessible(true);

        SerializedLambda invokeMethod = (SerializedLambda)ReflectionUtil.invokeMethod(findMethod, fieldGetterName);
        String getterName = invokeMethod.getImplMethodName();

        // remove "get" prefix
        getterName = getterName.replace("get", "");

        // convert from camelCase to underscores
        return camelCaseToUnderscore(getterName);
    }

    private static String camelCaseToUnderscore(String text)
    {
        if (text == null)
        {
            return null;
        }
        Matcher matcher = CAMEL_CASE_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find())
        {
            matcher.appendReplacement(sb, "_" + matcher.group());
        }
        matcher.appendTail(sb);
        return sb.toString().toLowerCase();
    }

}
