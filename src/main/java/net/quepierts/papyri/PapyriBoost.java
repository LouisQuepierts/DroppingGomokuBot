package net.quepierts.papyri;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import net.quepierts.papyri.annotation.Controller;
import net.quepierts.papyri.annotation.Handler;
import net.quepierts.papyri.dto.Request;
import net.quepierts.papyri.dto.Response;
import net.quepierts.papyri.exception.ServiceException;
import net.quepierts.papyri.model.HandlerDefinition;
import net.quepierts.papyri.model.ParameterDefinition;
import net.quepierts.papyri.model.RequestDefinition;
import net.quepierts.papyri.service.LogService;
import net.quepierts.papyri.service.impl.Slf4jLogServiceImpl;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PapyriBoost {
    private static final String TYPE_REQUEST = "request";
    private static final String TYPE_RESPONSE = "response";
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final List<Class<?>> classes = new ArrayList<>();
    private static final Map<String, HandlerDefinition> handlers = new HashMap<>();
    private static final Set<String> signals = new HashSet<>();
    private static final Object[] EMPTY = new Object[0];

    private static boolean searched = false;
    private static LogService logService;

    public static void start(
            @Nonnull Class<? extends Consumer<String[]>> clazz,
            @Nonnull Module module,
            @Nonnull String... args
    ) {
        if (searched)
            return;

        LogUtil.info("Papyri Framework Starting...");

        searched = true;

        Injector injector = Guice.createInjector(module, new ApplicationModule());
        logService = injector.getInstance(LogService.class);

        try {
            search(clazz.getPackageName().replace('.', '/'));
        } catch (Throwable e) {
            logService.error("Error occur during starting Papyri Boost", e);
        }

        injector.getInstance(clazz).accept(args);
    }

    public static void junit(
            @Nonnull String pack,
            @Nonnull Module module,
            @Nonnull String... args
    ) {
        Injector injector = Guice.createInjector(module, new ApplicationModule());
        logService = injector.getInstance(LogService.class);

        try {
            logService.title("JUnit Test");
            search(pack);
        } catch (Throwable e) {
            logService.error("Error occur during starting Papyri Boost JUnit", e);
        }
    }

    /*@SuppressWarnings("unchecked")
    public static <T, E> Response<E> request(String path, T request) {
        if (!handlers.containsKey(path))
            return Response.error("Unknown handler: " + path);

        HandlerDefinition handler = handlers.get(path);

        if (request != null && request.getClass() != handler.request) {
            return Response.error("Unmatched request type: " + request.getClass() + ", required: " + handler.request);
        } else if (request == null && handler.request != Void.class) {
            return Response.error("Nonnull request required!");
        }

        try {
            return (Response<E>) handler.method.invokeWithArguments(new Request<>(request));
        } catch (InvocationTargetException e) {
            logService.error(e.getMessage());
            if (e.getCause() instanceof ServiceException serviceException) {
                return Response.failed(serviceException.getReason());
            }
            return Response.error(e.getMessage());
        } catch (Throwable e) {
            logService.error("Error occur during request interface {}", path);
            return Response.error(e.getMessage());
        }
    }*/

    @SuppressWarnings("unchecked")
    public static <E> Response<E> request(String path, Object... parameters) {
        if (!handlers.containsKey(path)) {
            return Response.error("Unknown handler: " + path);
        }

        HandlerDefinition handler = handlers.get(path);
        RequestDefinition requestDefinition = handler.request();

        try {
            Object assembled = assemble(parameters, requestDefinition);
            Object response;
            if (assembled instanceof Object[] args) {
                response = handler.handle().invokeWithArguments(args);
            } else {
                response = handler.handle().invokeWithArguments(assembled);
            }
            return (Response<E>) response;
        } catch (Throwable e) {
            if (e.getCause() instanceof ServiceException serviceException) {
                return Response.failed(serviceException.getReason());
            }
            logService.error("Error occur during request interface {}", path, e);
            return Response.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static Object assemble(Object[] pParameters, RequestDefinition pDefinition) throws Throwable {
        if (pParameters.length == 0 && (pDefinition.nullable() || pDefinition.mapped().isEmpty())) {
            return EMPTY;
        }

        if (pParameters.length == 1) {
            Object parameter = pParameters[0];
            if (pDefinition.nullable() && parameter == null) {
                return null;
            }

            if (parameter instanceof Map<?, ?> map) {
                return assemble((Map<String, Object>) map, pDefinition);
            } else if (pDefinition.packed() == parameter.getClass()) {
                return parameter;
            }
        }

        if (pDefinition.mapped().size() != pParameters.length) {
            throw new RuntimeException("Unmatched parameters length for requesting: required " + pDefinition.mapped().size() + ", passed " + pParameters.length);
        }
        return pDefinition.constructor().assemble(pParameters);
    }

    private static Object assemble(Map<String, Object> pMap, RequestDefinition pDefinition) throws Throwable {
        Object[] parameters = new Object[pDefinition.mapped().size()];

        for (Map.Entry<String, ParameterDefinition> entry : pDefinition.mapped().entrySet()) {
            if (!entry.getValue().nullable() && !pMap.containsKey(entry.getKey())) {
                throw new RuntimeException("Missing parameter: " + entry.getKey());
            }

            parameters[entry.getValue().ordinal()] = pMap.get(entry.getKey());
        }
        return pDefinition.constructor().assemble(parameters);
    }

    public static void signal(String signal, boolean flag) {
        if (flag) {
            signals.add(signal);
        } else {
            signals.remove(signal);
        }
    }

    public static boolean hasSignal(String signal) {
        return signals.contains(signal);
    }

    private static void search(String pack) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packageName = pack.replace('.', '/');
        URL url = loader.getResource(packageName);

        if (url == null)
            throw new RuntimeException();

        String protocol = url.getProtocol();
        if (protocol.equals("file")) {
            searchFromFile(new File(url.getFile()));
        } else if (protocol.equals("jar")) {
            searchFromJar(url.getPath());
        }

        logService.info("Loaded Controllers: {}", classes.size());
        logService.info("Loaded Handles: {}", handlers.size());
    }

    private static void searchFromFile(File directory) {
        final File[] files = directory.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                searchFromFile(file);
            } else {
                String name = file.getPath();
                if (!name.endsWith(".class"))
                    continue;

                name = name.substring(name.indexOf("\\classes") + 9, name.length() - 6).replace('\\', '.');

                //System.out.println(name);
                processClass(name);
            }
        }
    }

    private static void searchFromJar(String path) {
        String[] split = path.split("!");
        String jarPath = split[0].substring(split[0].indexOf('/'));
        String packagePath = split[1].substring(1);

        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (!entryName.endsWith(".class"))
                    continue;

                if (entryName.startsWith(packagePath)) {
                    entryName = entryName.replace('/', '.').substring(0, entryName.lastIndexOf("."));
                    processClass(entryName);
                } else {
                    int index = entryName.lastIndexOf("/");
                    String lPackagePath;
                    if (index != -1) {
                        lPackagePath = entryName.substring(0, index);
                    } else {
                        lPackagePath = entryName;
                    }

                    if (lPackagePath.equals(packagePath)) {
                        entryName = entryName.replace('/', '.').substring(0, entryName.lastIndexOf('.'));
                        processClass(entryName);
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void processClass(String path) {
        try {
            Class<?> gotClass = Class.forName(path);

            if (!gotClass.isAnnotationPresent(Controller.class))
                return;

            String ctrlPath = gotClass.getAnnotation(Controller.class).value();

            for (Method method : gotClass.getMethods()) {
                if (!method.isAnnotationPresent(Handler.class)
                        || method.getReturnType() != Response.class)
                    continue;

                Handler annotation = method.getAnnotation(Handler.class);
                method.trySetAccessible();

                MethodHandle handle = LOOKUP.unreflect(method);

                String handlerPath = ctrlPath + "/" + annotation.value();

                if (handlers.containsKey(handlerPath)) {
                    throw new RuntimeException("Duplicated path: " + handlerPath);
                }

                RequestDefinition requestDefinition = extractParameterDefinitions(method, handlerPath);

                Type responseType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                handlers.put(handlerPath, new HandlerDefinition(handle, requestDefinition, responseType));
            }

            classes.add(gotClass);
        } catch (Exception e) {
            e.fillInStackTrace();
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static RequestDefinition extractParameterDefinitions(Method method, String handlerPath) throws IllegalAccessException {
        Parameter[] parameters = method.getParameters();

        if (parameters.length == 0) {
            return new RequestDefinition(Map.class, true, PapyriBoost::pass, Collections.EMPTY_MAP);
        }

        Parameter param = parameters[0];
        Class<?> type = param.getType();
        boolean nullable = param.isAnnotationPresent(net.quepierts.papyri.annotation.Parameter.class) && param.getAnnotation(net.quepierts.papyri.annotation.Parameter.class).nullable();
        if (parameters.length == 1) {
            if (type == Request.class) {
                Type typeArgument = ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
                Class<? extends Type> clazz = typeArgument.getClass();

                if (Record.class.isAssignableFrom(clazz)) {
                    Constructor<?> constructor = clazz.getConstructors()[0];
                    constructor.trySetAccessible();
                    MethodHandle handle = LOOKUP.unreflectConstructor(constructor);
                    return new RequestDefinition(clazz, nullable, handle::invokeWithArguments, extractRecordFields((Class<? extends Record>) clazz));
                }

                return new RequestDefinition(typeArgument, nullable, Request::new, ImmutableMap.of(TYPE_REQUEST, new ParameterDefinition(typeArgument, true, 0)));
            } else if (Record.class.isAssignableFrom(type)) {

                Constructor<?> constructor = type.getConstructors()[0];
                constructor.trySetAccessible();
                MethodHandle handle = LOOKUP.unreflectConstructor(constructor);
                return new RequestDefinition(type, nullable, handle::invokeWithArguments, extractRecordFields((Class<? extends Record>) type));
            }
        } else {
            for (Parameter parameter : parameters) {
                if (parameter.getType() == Request.class) {
                    throw new RuntimeException("Illegal argument types in handler: " + handlerPath);
                }
            }
        }

        return new RequestDefinition(Map.class, false, PapyriBoost::pass, extractParameters(parameters));
    }

    private static Map<String, ParameterDefinition> extractParameters(final Parameter[] parameters) {
        ImmutableMap.Builder<String, ParameterDefinition> builder = ImmutableMap.builder();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Type type = parameter.getParameterizedType();
            String name = parameter.getName();
            boolean nullable = false;

            if (parameter.isAnnotationPresent(net.quepierts.papyri.annotation.Parameter.class)) {
                net.quepierts.papyri.annotation.Parameter param = parameter.getAnnotation(net.quepierts.papyri.annotation.Parameter.class);
                name = param.value();
                nullable = param.nullable();
            }

            builder.put(name, new ParameterDefinition(type, nullable, i));
        }
        return builder.build();
    }

    private static Map<String, ParameterDefinition> extractRecordFields(Class<? extends Record> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        ImmutableMap.Builder<String, ParameterDefinition> builder = ImmutableMap.builder();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name;
            boolean nullable = false;
            if (field.isAnnotationPresent(net.quepierts.papyri.annotation.Parameter.class)) {
                net.quepierts.papyri.annotation.Parameter annotation = field.getAnnotation(net.quepierts.papyri.annotation.Parameter.class);
                name = annotation.value();
                nullable = annotation.nullable();
            } else {
                name = field.getName();
            }

            builder.put(name, new ParameterDefinition(field.getType(), nullable, i));
        }

        return builder.build();
    }

    public static final class ApplicationModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Gson.class).toInstance(new GsonBuilder().setDateFormat(DateFormat.MILLISECOND_FIELD).create());
            bind(LogService.class).toInstance(new Slf4jLogServiceImpl());
            requestStaticInjection(classes.toArray(new Class<?>[0]));
        }
    }

    private static Object pass(Object[] args) {
        return args;
    }

    @FunctionalInterface
    public interface AssembleConstructor {
        Object assemble(Object[] args) throws Throwable;
    }
}
