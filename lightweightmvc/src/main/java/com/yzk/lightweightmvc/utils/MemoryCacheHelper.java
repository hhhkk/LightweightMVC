package com.yzk.lightweightmvc.utils;

import android.util.ArrayMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import timber.log.Timber;

public class MemoryCacheHelper {

    private static Map<String, Object> cache = Collections.synchronizedMap(new ArrayMap<String, Object>());
    /**
     * A map from primitive types to their corresponding wrapper types.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;

    /**
     * A map from wrapper types to their corresponding primitive types.
     */
    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_TYPE;

    static {
        Map<Class<?>, Class<?>> primToWrap = new HashMap<Class<?>, Class<?>>(16);
        Map<Class<?>, Class<?>> wrapToPrim = new HashMap<Class<?>, Class<?>>(16);

        add(primToWrap, wrapToPrim, boolean.class, Boolean.class);
        add(primToWrap, wrapToPrim, byte.class, Byte.class);
        add(primToWrap, wrapToPrim, char.class, Character.class);
        add(primToWrap, wrapToPrim, double.class, Double.class);
        add(primToWrap, wrapToPrim, float.class, Float.class);
        add(primToWrap, wrapToPrim, int.class, Integer.class);
        add(primToWrap, wrapToPrim, long.class, Long.class);
        add(primToWrap, wrapToPrim, short.class, Short.class);
        add(primToWrap, wrapToPrim, void.class, Void.class);

        PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(primToWrap);
        WRAPPER_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(wrapToPrim);
    }

    private static void add(Map<Class<?>, Class<?>> forward,
                            Map<Class<?>, Class<?>> backward, Class<?> key, Class<?> value) {
        forward.put(key, value);
        backward.put(value, key);
    }

    private static MemoryCacheHelper instance = new MemoryCacheHelper();

    public static MemoryCacheHelper getInstance() {
        return instance;
    }

    public static <T> T getObject(Object key, Class<T> tClass) {
        Object o = cache.get(key);
        if (o == null) {
            return null;
        }
        return wrap(tClass).cast(o);
    }

    public static Object getObject(Object key) {
        Object o = cache.get(key);
        if (o == null) {
            return null;
        }
        return o;
    }

    public static void put(String key, Object o) {
        cache.put(key, o);
    }

    public static void remove(String key) {
        cache.remove(key);
    }


    public static <T> Class<T> wrap(Class<T> type) {
        // cast is safe: long.class and Long.class are both of type Class<Long>
        @SuppressWarnings("unchecked")
        Class<T> wrapped = (Class<T>) PRIMITIVE_TO_WRAPPER_TYPE.get(type);
        return (wrapped == null) ? type : wrapped;
    }


    public static void printMemoryCache() {
        Set<Map.Entry<String, Object>> entries = cache.entrySet();
        for (Map.Entry<String, Object> temp : entries) {
            String key = temp.getKey();
            Object key1 = temp.getValue();
            Timber.e("key " + key);
            Timber.e("values " + String.valueOf(key1));
        }
    }


    public static boolean containsKey(String versionObject) {

        return cache.containsKey(versionObject);
    }
}
