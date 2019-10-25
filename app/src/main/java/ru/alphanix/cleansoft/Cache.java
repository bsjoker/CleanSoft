package ru.alphanix.cleansoft;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import ru.alphanix.cleansoft.App.App;

public class Cache {
    private static Context contex;
    private static Cache instance;
    private Editor editor;
    private SharedPreferences preferences;

    public static Cache getInstance() {
        instance = new Cache();
        return instance;
    }

    public static Cache newContex(Context context) {
        return new Cache(context);
    }

    private Cache() {
        contex = App.getInstance().getApplicationContext();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(contex);
    }

    private Cache(Context context) {
        contex = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void set(String str, int i) {
        try {
            this.editor = this.preferences.edit();
            this.editor.putInt(str, i);
            this.editor.commit();
        } catch (Exception | OutOfMemoryError unused) {
        }
    }

    public void set(String str, Long l) {
        try {
            this.editor = this.preferences.edit();
            this.editor.putLong(str, l.longValue());
            this.editor.commit();
        } catch (Exception | OutOfMemoryError unused) {
        }
    }

    public void set(String str, String str2) {
        try {
            this.editor = this.preferences.edit();
            this.editor.putString(str, str2);
            this.editor.commit();
        } catch (Exception | OutOfMemoryError unused) {
        }
    }

    public void set(String str, Float f) {
        try {
            this.editor = this.preferences.edit();
            this.editor.putFloat(str, f.floatValue());
            this.editor.commit();
        } catch (OutOfMemoryError unused) {
        }
    }

    public void set(String str, Boolean bool) {
        try {
            this.editor = this.preferences.edit();
            this.editor.putBoolean(str, bool.booleanValue());
            this.editor.commit();
        } catch (Exception | OutOfMemoryError unused) {
        }
    }

    public int getInt(String str, int i) {
        return this.preferences.getInt(str, i);
    }

    public String getString(String str, String str2) {
        return this.preferences.getString(str, str2);
    }

    public Float getFloat(String str, Float f) {
        return Float.valueOf(this.preferences.getFloat(str, f.floatValue()));
    }

    public Long getLong(String str, int i) {
        return Long.valueOf(this.preferences.getLong(str, (long) i));
    }

    public Boolean getBoolean(String str, Boolean bool) {
        return Boolean.valueOf(this.preferences.getBoolean(str, bool.booleanValue()));
    }

    public void remove(String str) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(contex);
        this.editor = this.preferences.edit();
        this.editor.remove(str);
        this.editor.commit();
    }
}
