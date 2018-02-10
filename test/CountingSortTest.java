import org.junit.Before;
import org.junit.Test;
import sort.SortingAlgorithm;

public class CountingSortTest extends SortTest {

    @Before
    public void instantiatePort() {
        super.instantiatePort(SortingAlgorithm.countingsort, "CountingSort");
    }

    @Test
    public void testCountingSort() {
        super.testSortingAlgorithm();
    }
}
