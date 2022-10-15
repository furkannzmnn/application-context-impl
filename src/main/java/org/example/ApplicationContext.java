package org.example;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;


public class ApplicationContext {

    private final Set<Class<?>> beans;

    public ApplicationContext(Class<?> runClass) {
        final Reflections reflections = new Reflections(runClass.getPackageName());
        beans = reflections.getTypesAnnotatedWith(Component.class)
                .stream()
                .filter(clazz -> !clazz.isInterface())
                .collect(Collectors.toSet());
    }


    public <T> T getBean(Class<T> clas) {
        if (! clas.isInterface()) {
            throw new MyFrameWorkException("Class must be interface");
        }

        final Class<T> implClass = getImplClass(clas);

        final Object bean = createBean(clas, implClass);

        return (T) bean;

    }

    private <T> T createBean(Class<T> clazz, Class<T> implClass) {
        try {
            final Constructor<?> constructor = findConstructor(implClass);
            final Object[] constructorParameters = findConstructorParameters(constructor);
            final T bean = (T) constructor.newInstance(constructorParameters);

            final Object proxyInstance = Proxy.newProxyInstance(
                    ApplicationContext.class.getClassLoader(),
                    new Class[]{clazz},
                    new TransactionalProxyHandler(bean)
            );

            return clazz.cast(proxyInstance);

        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> findConstructor(Class<T> clazz) {
        final Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
        if (constructors.length == 1) {
            return constructors[0];
        }

        final Set<Constructor<T>> constructorsWithAnnotation = Arrays.stream(constructors)
                .filter(constructor -> constructor.isAnnotationPresent(Autowired.class))
                .collect(Collectors.toSet());

        if (constructorsWithAnnotation.size() > 1) {
            throw new MyFrameWorkException("There are more than 1 constructor with Autowired annotation: " + clazz.getName());
        }

        return constructorsWithAnnotation.stream()
                .findFirst()
                .orElseThrow(() -> new MyFrameWorkException("Cannot find constructor with annotation Autowired: " + clazz.getName()));
    }

    private <T> Object[] findConstructorParameters(Constructor<T> constructor) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        return Arrays.stream(parameterTypes)
                .map(this::getBean)
                .toArray(Object[]::new);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> getImplClass(Class<T> interfaceClass) {
        final Set<Class<?>> classesWithInterface = beans.stream()
                .filter(interfaceClass::isAssignableFrom)
                .collect(Collectors.toSet());

        if (classesWithInterface.size() != 1) {
            throw new MyFrameWorkException("0 or more than 1 implementation found");
        }

        return (Class<T>) classesWithInterface
                .stream()
                .findFirst()
                .orElseThrow(() -> new MyFrameWorkException("Implementation not found"));

    }
}
