import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

public class CountingSortTest {
    private static Object countingSortPort;

    @BeforeClass
    public static void instantiatePort() {
        try {
            URL[] urls = { new File(Configuration.instance.constructJarPath(AvailableSortingAlgorithms.countingsort)).toURI().toURL() };
            URLClassLoader urlClassLoader = new URLClassLoader(urls, Application.class.getClassLoader());

            String classNameToLoad = "CountingSort";
            Class<?> clazz = Class.forName(classNameToLoad, true, urlClassLoader);

            Object instance = clazz.getMethod("getInstance").invoke(null);
            countingSortPort = clazz.getDeclaredField("port").get(instance);
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException |
                NoSuchFieldException | IllegalAccessException | InvocationTargetException exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void countingSortTest() {
        List<Integer> numbers = Arrays.asList(13, 21, 8, 17, 23, 17);
        List<Integer> expectedNumbers = Arrays.asList(8, 13, 17, 17, 21, 23);
        try {
            Method countingSortMethod = countingSortPort.getClass().getDeclaredMethod("sort", List.class);
            countingSortMethod.invoke(countingSortPort, numbers);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exc) {
            exc.printStackTrace();
        }

        assertEquals(expectedNumbers, numbers);
    }
}
