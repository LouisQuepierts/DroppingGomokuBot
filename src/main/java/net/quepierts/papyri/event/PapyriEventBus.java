package net.quepierts.papyri.event;

import net.quepierts.papyri.PapyriBoost;
import net.quepierts.papyri.annotation.HandleEvent;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PapyriEventBus {
    private static final Map<Class<? extends Event>, List<EventListener>> EVENT_LISTENERS = new HashMap<>();
    private static final Map<Method, MethodHandle> METHOD_HANDLE_MAP = new HashMap<>();

    public static void post(Event event) {
        Class<? extends Event> type = event.getClass();

        if (EVENT_LISTENERS.containsKey(type)) {
            for (EventListener listener : EVENT_LISTENERS.get(type)) {
                listener.invoke(event);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void subscribe(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            int methodModifiers = method.getModifiers();
            if (!Modifier.isStatic(methodModifiers)) {
                continue;
            }

            Parameter[] parameters = method.getParameters();
            if (parameters.length != 1) {
                // ERROR
                continue;
            }

            Parameter parameter = parameters[0];
            Class<?> eventType = parameter.getType();

            if (!Event.class.isAssignableFrom(eventType)) {
                // ILLEGAL TYPE
                continue;
            }

            if (Modifier.isAbstract(eventType.getModifiers())) {
                // ILLEGAL TYPE
                continue;
            }

            MethodHandle handle = getMethodHandle(method);
            addEventListener((Class<? extends Event>) eventType, handle);
        }
    }

    @SuppressWarnings("unchecked")
    public static void subscribe(Object object) {
        if (object instanceof Class<?> clazz) {
            subscribe(clazz);
            return;
        }

        Method[] methods = object.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (!method.isAnnotationPresent(HandleEvent.class)) {
                continue;
            }

            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            Parameter[] parameters = method.getParameters();
            Parameter parameter = parameters[0];
            Class<?> eventType = parameter.getType();

            if (!Event.class.isAssignableFrom(eventType)) {
                // ILLEGAL TYPE
                continue;
            }

            if (Modifier.isAbstract(eventType.getModifiers())) {
                // ILLEGAL TYPE
                continue;
            }

            MethodHandle handle = getMethodHandle(method);
            MethodHandle bound = handle.bindTo(object);

            addEventListener((Class<? extends Event>) eventType, bound);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Event> void subscribe(Class<T> event, Consumer<T> handler) {
        if (Modifier.isAbstract(event.getModifiers())) {
            return;
        }

        if (!EVENT_LISTENERS.containsKey(event)) {
            EVENT_LISTENERS.put(event, new ArrayList<>());
        }

        List<EventListener> listeners = EVENT_LISTENERS.get(event);
        listeners.add(new ConsumerEventListener((Consumer<Event>) handler));
    }

    private static void addEventListener(Class<? extends Event> event, MethodHandle handle) {
        if (Modifier.isAbstract(event.getModifiers())) {
            return;
        }

        if (!EVENT_LISTENERS.containsKey(event)) {
            EVENT_LISTENERS.put(event, new ArrayList<>());
        }

        List<EventListener> listeners = EVENT_LISTENERS.get(event);
        listeners.add(new SubscribeEventListener(handle));
    }

    private static MethodHandle getMethodHandle(Method method) {
        MethodHandle handle = METHOD_HANDLE_MAP.get(method);

        if (handle == null) {
            try {
                method.trySetAccessible();
                handle = PapyriBoost.LOOKUP.unreflect(method);
                METHOD_HANDLE_MAP.put(method, handle);
            } catch (IllegalAccessException ignored) {}
        }

        return handle;
    }

    private static List<EventListener> getListeners(Class<? extends Event> clazz) {
        return EVENT_LISTENERS.computeIfAbsent(clazz, c -> new ArrayList<>());
    }
}
