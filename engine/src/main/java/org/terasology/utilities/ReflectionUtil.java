/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;

/**
 *
 */
public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    /**
     * Attempts to return the type of a parameter of a parameterised field. This uses compile-time information only - the
     * type should be obtained from a field with a the generic types bound.
     *
     * @param type
     * @param index
     * @return The type of the generic parameter at index for the given type, or null if it cannot be obtained.
     */
    // TODO - Improve parameter lookup to go up the inheritance tree more
    public static Type getTypeParameter(Type type, int index) {
        if (!(type instanceof ParameterizedType)) {
            return null;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        if (parameterizedType.getActualTypeArguments().length < index + 1) {
            return null;
        }
        return parameterizedType.getActualTypeArguments()[index];
    }

    public static Class<?> getClassOfType(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getRawType();
        }
        return null;
    }

    public static Method findGetter(Field field) {
        Method result = findMethod(field.getDeclaringClass(), "get" + field.getName().substring(0, 1).toUpperCase(Locale.ENGLISH) + field.getName().substring(1));
        if (result != null && field.getType().equals(result.getReturnType())) {
            result.setAccessible(true);
            return result;
        }
        result = findMethod(field.getDeclaringClass(), "is" + field.getName().substring(0, 1).toUpperCase(Locale.ENGLISH) + field.getName().substring(1));
        if (result != null && field.getType().equals(result.getReturnType())) {
            result.setAccessible(true);
            return result;
        }
        return null;
    }

    public static Method findSetter(Field field) {
        String setterName = "set" + field.getName().substring(0, 1).toUpperCase(Locale.ENGLISH) + field.getName().substring(1);
        Method result = findMethod(field.getDeclaringClass(), setterName, field.getType());
        if (result != null) {
            result.setAccessible(true);
        }
        return result;
    }

    public static Method findMethod(Class<?> targetType, String methodName, Class<?>... parameters) {
        try {
            return targetType.getMethod(methodName, parameters);
        } catch (NoSuchMethodException me) {
            // We're expecting not to find methods
            return null;
        }
    }
}
