package factory;

import main.Application;
import main.Configuration;
import sort.SortingAlgorithm;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class PortFactory {
    private static final String getInstanceMethod = "getInstance";
    private static final String getVersionMethod = "getVersion";
    private static final String portFieldName = "port";

    public static Object produceInstance(boolean debugMode) {
        return produceInstance(Configuration.instance.sortingType, debugMode);
    }

    public static Object produceInstance(SortingAlgorithm algorithm, boolean debugMode) {
        Object port = null;
        try {
            String jarPath = Configuration.instance.constructJarPath(algorithm);
            URL[] urls = { new File(jarPath).toURI().toURL() };
            URLClassLoader urlClassLoader = new URLClassLoader(urls, Application.class.getClassLoader());

            String classNameToLoad = Configuration.instance.getClassNameForSortAlgorithm(algorithm);
            Class<?> clazz = Class.forName(classNameToLoad, true, urlClassLoader);

            Object instance = clazz.getMethod(getInstanceMethod).invoke(null);
            port = clazz.getDeclaredField(portFieldName).get(instance);

            Method getVersion = port.getClass().getMethod(getVersionMethod);
            String version = (String) getVersion.invoke(port);

            if (debugMode) {
                System.out.println("pathToJar : " + jarPath);
                System.out.println("clazz     : " + clazz.toString());
                System.out.println("port      : " + port.hashCode());
                System.out.println("version   : " + version);
            }
        } catch (NoSuchMethodException exc) {
            System.out.println("--- exception: No such method");
            System.out.println(exc.getMessage());
            exc.printStackTrace();
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException |
                InvocationTargetException | MalformedURLException exc) {
            exc.printStackTrace();
        }

        return port;
    }
}
