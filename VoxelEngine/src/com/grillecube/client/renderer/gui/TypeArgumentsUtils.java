package com.grillecube.client.renderer.gui;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class TypeArgumentsUtils is a utility class for getting the type arguments a child class has
 * used to extend a generic base class. It is inspired from the article Reflecting generics by Ian
 * Robertson at <a href="http://www.artima.com/weblogs/viewpost.jsp?thread=208860"
 * >http://www.artima.com/weblogs/viewpost.jsp?thread=208860</a>. In the comments someone asked if
 * we are allowed to use the source code from the article. The answer of Ian Robertson is:
 * Absolutely, you may use this code. "Consider it open sourced".
 *
 */
public class TypeArgumentsUtils
{


	/**
	 * Gets the first type argument from the childClass.
	 *
	 * @param <T>
	 *            the generic type of the baseClass
	 * @param baseClass
	 *            the base class
	 * @param childClass
	 *            the child class
	 * @return the first type argument
	 */
	public static <T> Class<?> getFirstTypeArgument(Class<T> baseClass,
		Class<? extends T> childClass)
	{
		return getTypeArgument(baseClass, childClass, 0);
	}

	/**
	 * Gets the type argument from the childClass at the given index or null if it does not exists.
	 *
	 * @param <T>
	 *            the generic type of the baseClass
	 * @param baseClass
	 *            the base class
	 * @param childClass
	 *            the child class
	 * @param index
	 *            the index of the type argument
	 * @return the type argument from the childClass at the given index or null if it does not
	 *         exists.
	 */
	public static <T> Class<?> getTypeArgument(Class<T> baseClass, Class<? extends T> childClass,
		int index)
	{
		List<Class<?>> typeArguments = getTypeArguments(baseClass, childClass);
		if (typeArguments != null && !typeArguments.isEmpty() && index < typeArguments.size())
		{
			return typeArguments.get(index);
		}
		return null;
	}

	/**
	 * Get the actual type arguments a child class has used to extend a generic base class.
	 *
	 * @param <T>
	 *            the generic type of the baseClass
	 * @param baseClass
	 *            the base class
	 * @param childClass
	 *            the child class
	 * @return a list of the raw classes for the actual type arguments.
	 */
	public static <T> List<Class<?>> getTypeArguments(Class<T> baseClass,
		Class<? extends T> childClass)
	{
		if (baseClass == null)
		{
			throw new IllegalArgumentException("Argument baseClass should not be null.");
		}
		if (childClass == null)
		{
			throw new IllegalArgumentException("Argument childClass should not be null.");
		}
		Map<Type, Type> resolvedTypes = new HashMap<>();
		Type type = childClass;
		// start walking up the inheritance hierarchy until we hit baseClass
		while (!getClass(type).equals(baseClass))
		{
			if (type instanceof Class)
			{
				// there is no useful information for us in raw types, so just
				// keep going.
				type = ((Class<?>)type).getGenericSuperclass();
			}
			else
			{
				ParameterizedType parameterizedType = (ParameterizedType)type;
				Class<?> rawType = (Class<?>)parameterizedType.getRawType();

				resolvedTypes.putAll(getTypeArgumentsAndParameters(type));

				if (!rawType.equals(baseClass))
				{
					type = rawType.getGenericSuperclass();
				}
			}
		}

		// finally, for each actual type argument provided to baseClass,
		// determine (if possible)
		// the raw class for that type argument.
		Type[] actualTypeArguments;
		if (type instanceof Class)
		{
			actualTypeArguments = ((Class<?>)type).getTypeParameters();
		}
		else
		{
			actualTypeArguments = ((ParameterizedType)type).getActualTypeArguments();
		}
		List<Class<?>> typeArgumentsAsClasses = new ArrayList<>();
		// resolve types by chasing down type variables.
		for (Type baseType : actualTypeArguments)
		{
			while (resolvedTypes.containsKey(baseType))
			{
				baseType = resolvedTypes.get(baseType);
			}
			typeArgumentsAsClasses.add(getClass(baseType));
		}
		return typeArgumentsAsClasses;
	}

	/**
	 * Gets the type arguments and parameters.
	 *
	 * @param type
	 *            the type
	 * @return the type arguments and parameters
	 */
	private static Map<Type, Type> getTypeArgumentsAndParameters(Type type)
	{
		ParameterizedType parameterizedType = (ParameterizedType)type;
		Class<?> rawType = (Class<?>)parameterizedType.getRawType();
		Map<Type, Type> resolvedTypes = new HashMap<>();
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
		for (int i = 0; i < actualTypeArguments.length; i++)
		{
			resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
		}
		return resolvedTypes;
	}

	/**
	 * Get the underlying class for a type, or null if the type is a variable type.
	 * 
	 * @param type
	 *            the type
	 * @return the underlying class
	 */
	private static Class<?> getClass(Type type)
	{
		if (type instanceof Class)
		{
			return (Class<?>)type;
		}
		else if (type instanceof ParameterizedType)
		{
			return getClass(((ParameterizedType)type).getRawType());
		}
		else if (type instanceof GenericArrayType)
		{
			Type componentType = ((GenericArrayType)type).getGenericComponentType();
			Class<?> componentClass = getClass(componentType);
			if (componentClass != null)
			{
				return Array.newInstance(componentClass, 0).getClass();
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
}