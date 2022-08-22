/*
 * SerializableFunction.java
 *
 */
package ml.egoztyle.builder.sql;

import java.io.Serializable;
import java.util.function.Function;

/**
 * SerializableFunction
 *
 * @param <I>
 * @param <O>
 */
@FunctionalInterface
public interface SerializableFunction<I, O> extends Function<I, O>, Serializable
{
}
