import main.Application;
import main.Configuration;
import sort.SortingAlgorithm;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class SortTest {
    protected Object portInstance;

    public abstract void instantiatePort();

    public void instantiatePort(SortingAlgorithm variant, String className) {
        try {
            URL[] urls = { new File(Configuration.instance.constructJarPath(variant)).toURI().toURL() };
            URLClassLoader urlClassLoader = new URLClassLoader(urls, Application.class.getClassLoader());

            Class<?> clazz = Class.forName(className, true, urlClassLoader);

            Object instance = clazz.getMethod("getInstance").invoke(null);
            portInstance = clazz.getDeclaredField("port").get(instance);
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException |
                NoSuchFieldException | IllegalAccessException | InvocationTargetException exc) {
            exc.printStackTrace();
        }
    }

    public void testSortingAlgorithm() {
        List<Integer> numbers = Arrays.asList(13, 21, 8, 17, 23, 17);
        List<Integer> expectedNumbers = Arrays.asList(8, 13, 17, 17, 21, 23);
        try {
            Method sortMethod = portInstance.getClass().getDeclaredMethod("sort", List.class);
            sortMethod.invoke(portInstance, numbers);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exc) {
            exc.printStackTrace();
        }

        assertEquals(expectedNumbers, numbers);
    }
}
