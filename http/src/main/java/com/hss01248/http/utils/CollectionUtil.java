package com.hss01248.http.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/1/20 0020.
 */
public class CollectionUtil {

    public static <K, V> void filterMap(Map<K, V> map, MapFilter<K, V> filter) {
        if(map == null || map.isEmpty()){
            return;
        }
        Set<Map.Entry<K, V>> set = map.entrySet();
        Iterator<Map.Entry<K, V>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, V> entry = iterator.next();
            if (!filter.isRemain(entry)) {
                iterator.remove();
            }
        }
    }

    public static <K, V> void forEach(Map<K, V> map, EveryMap<K, V> every) {
        if(map == null || map.isEmpty()){
            return;
        }
        Set<Map.Entry<K, V>> set = map.entrySet();
        Iterator<Map.Entry<K, V>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, V> entry = iterator.next();
            every.item(entry);
        }
    }

    public interface MapFilter<K, V> {
        boolean isRemain(Map.Entry<K, V> entry);
    }

    public static <T> void filter(Iterable<T> iterable, Filter filter) {
        if(iterable == null ){
            return;
        }
        Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (!filter.isRemain(item)) {
                iterator.remove();
            }
        }
    }

    public interface Filter<T> {
        boolean isRemain(T item);
    }

    public interface Every<T> {
        void item(T item);
    }

    public interface EveryMap<K, V> {
        void item(Map.Entry<K, V> entry);
    }
}
