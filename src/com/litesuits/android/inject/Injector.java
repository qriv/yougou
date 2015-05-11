package com.litesuits.android.inject;

/**
 * Created by taosj on 15/2/2.
 */

import android.app.Activity;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Injector {

    self;

    private final Map<Class<?>, List<FieldSpec>> specMap = new HashMap<Class<?>, List<FieldSpec>>();

    public void inject(Activity activity) {

        View root = activity.findViewById(android.R.id.content).getRootView();

        List<FieldSpec> specs = specMap.get(activity);
        if (specs == null) {
            specs = new ArrayList<FieldSpec>();
            Class<?> cls = activity.getClass();
            for (Field field : cls.getDeclaredFields()) {
                for (Annotation a : field.getAnnotations()) {
                    if (a instanceof InjectView) {
                        specs.add(new FieldSpec(field, field.getName(),
                                ((InjectView) a).id()));
                    }
                }
            }
            specMap.put(cls, specs);
        }

        for (FieldSpec spec : specs) {
            int id = spec.getId();
            if (id == 0)
                id = activity.getResources().getIdentifier(spec.getName(),
                        "id", activity.getPackageName());
            try {
                spec.getField().set(activity, root.findViewById(id));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}