import org.junit.Before;
import org.junit.Test;
import sort.SortingAlgorithm;

public class BucketSortTest extends SortTest {

    @Before
    public void instantiatePort() {
        super.instantiatePort(SortingAlgorithm.bucketsort, "BucketSort");
    }

    @Test
    public void testBucketSort() {
        super.testSortingAlgorithm();
    }

}
