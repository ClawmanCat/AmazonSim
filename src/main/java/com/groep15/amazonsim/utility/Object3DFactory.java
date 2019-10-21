package com.groep15.amazonsim.utility;

import com.groep15.amazonsim.models.Object3D;
import org.json.simple.JSONObject;
import org.reflections.Reflections;

import java.util.Set;

public final class Object3DFactory {
    private Object3DFactory() {}

    // List of all classes that extend Object3D
    private static Set<Class<? extends Object3D>> classes = new Reflections("com.groep15.amazonsim").getSubTypesOf(Object3D.class);

    public static Object3D Create(JSONObject json, Object... args) {
        String type = (String) json.get("type");
        if (type == null) throw new IllegalArgumentException("Attempted to construct Object3D from invalid JSON.");

        // Find the class with the same name as specified in the JSON, and return a new instance of it.
        for (Class<? extends Object3D> cls : classes) {
            if (cls.getSimpleName().toLowerCase().equals(type)) {
                try {
                    Object3D o = cls.getDeclaredConstructor(GetAllClasses(args)).newInstance(args);
                    o.fromJSON(json);

                    return o;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to construct new " + type + " reflectively: " + e.getMessage());
                }
            }
        }

        throw new RuntimeException("Failed to construct new Object3D reflectively: given class " + type + " does not exist.");
    }

    // Get the class of each provided object.
    private static Class<?>[] GetAllClasses(Object... objs) {
        Class<?>[] result = new Class<?>[objs.length];
        for (int i = 0; i < objs.length; ++i) result[i] = objs[i].getClass();
        return result;
    }
}
