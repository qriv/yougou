package com.litesuits.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by taosj on 15/2/6.
 */
//提供Collections和Map的构建便利方法
public final class CMUtil {

    private CMUtil() {
    }

    public static <K, V> HashMap<K, V> getHashMap() {
        return new HashMap<K, V>();
    }

    public static <E> ArrayList<E> getArrayList() {
        return new ArrayList<E>();
    }

    public static <E> LinkedList<E> getLinkedList() {
        return new LinkedList<E>();
    }
}
