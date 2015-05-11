package com.litesuits.common.cache;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.litesuits.ApplicationBase;

import java.util.Map;

public class XmlCache {
    private static final String DEFAULT_NAME = "default";
    private SharedPreferences sp;
    private Editor editor;

    private XmlCache(Context ctx, String name, int mode) {
        sp = ctx.getSharedPreferences(name, mode);
        editor = sp.edit();
    }

    public static XmlCache getInstance(String name, int mode) {
        return new XmlCache(ApplicationBase.instance, name, mode);
    }

    public static XmlCache getInstance(String name) {
        return getInstance(name, Context.MODE_PRIVATE);
    }

    public static XmlCache getInstance() {
        return getInstance(DEFAULT_NAME, Context.MODE_PRIVATE);
    }

    public XmlCache put(String key, boolean value) {
        editor.putBoolean(key, value);
        return this;
    }

    public XmlCache put(String key, int value) {
        editor.putInt(key, value);
        return this;
    }

    public XmlCache put(String key, long value) {
        editor.putLong(key, value);
        return this;
    }

    public XmlCache put(String key, float value) {
        editor.putFloat(key, value);
        return this;
    }

    public XmlCache put(String key, String value) {
        editor.putString(key, value);
        return this;
    }

    public void end() {
        editor.commit();
    }

    public void removeAll() {
        editor.clear().commit();
    }

    public XmlCache remove(String key) {
        editor.remove(key);
        return this;
    }

    public void removeValue(String key) {
        editor.remove(key).commit();
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value).commit();
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value).commit();
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value).commit();
    }

    public void putString(String key, String value) {
        editor.putString(key, value).commit();
    }

    public Map<String, ?> getAll() {
        return sp.getAll();
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public long getLong(String key) {
        return sp.getLong(key, 0);
    }

    public float getFloat(String key) {
        return sp.getFloat(key, 0);
    }

    public String getString(String key) {
        return sp.getString(key, null);
    }
}
