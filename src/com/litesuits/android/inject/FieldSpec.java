package com.litesuits.android.inject;

import java.lang.reflect.Field;

/**
 * Created by taosj on 15/2/2.
 */
public class FieldSpec {

    public FieldSpec(Field field, String name,int id) {
        super();
        this.field = field;
        this.name = name;
        this.id = id;
        field.setAccessible(true);
    }

    private final int id;

    public int getId() {
        return id;
    }

    private final Field field;

    public Field getField() {
        return field;
    }

    public String getName() {
        return name;
    }

    private final String name;
}
