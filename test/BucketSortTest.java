import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BucketSortTest {
    private static Object bucketSortPort;

    @BeforeClass
    public static void instantiatePort() {
        try {
            URL[] urls = { new File(Configuration.instance.constructJarPath(AvailableSortingAlgorithms.bucketsort)).toURI().toURL() };
            URLClassLoader urlClassLoader = new URLClassLoader(urls, Application.class.getClassLoader());

            String classNameToLoad = "BucketSort";
            Class<?> clazz = Class.forName(classNameToLoad, true, urlClassLoader);

            Object instance = clazz.getMethod("getInstance").invoke(null);
            bucketSortPort = clazz.getDeclaredField("port").get(instance);
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException |
                NoSuchFieldException | IllegalAccessException | InvocationTargetException exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void bucketSortTest() {
        List<Integer> numbers = Arrays.asList(13, 21, 8, 17, 23, 17);
        List<Integer> expectedNumbers = Arrays.asList(8, 13, 17, 17, 21, 23);
        try {
            Method countingSortMethod = bucketSortPort.getClass().getDeclaredMethod("sort", List.class);
            countingSortMethod.invoke(bucketSortPort, numbers);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exc) {
            exc.printStackTrace();
        }

        assertEquals(expectedNumbers, numbers);
    }

}
