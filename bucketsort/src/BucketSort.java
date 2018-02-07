import java.util.ArrayList;
import java.util.List;

/**
 * Container for bucket sort implementation
 *
 * @author Paul Osborne
 * @version December 20, 2008
 */
public class BucketSort {

    private static BucketSort instance = new BucketSort();
    public Port port;

    private BucketSort() {
        port = new Port();
    }

    public static BucketSort getInstance() {
        return instance;
    }

    /**
     * Bucket sort where nothing is known about the data items other than that they
     * are all Integers.  This will create buckets between the minimum and maximum
     * allowable Integer values.
     * <p>
     * The bucket sort (or bin sort) is a non-comparison sort in the same family
     * as the radix sort, counting sort, pigeonhole sort, and burstsort.
     * <p>
     * The bucket sort has four main steps:
     * 1. Setup a list of empty buckets
     * 2. Scatter: go over original list, placing items in appropriate bucket
     * 3. Sort each non-empty bucket (recursively)
     * 4. Gather: visit the buckets in order and place into original.
     * <p>
     * The number of buckets can be predetermined or it can be a function of the
     * number of data items in a list.  Each bucket has a fraction of the domain
     * of the data items.  The problem is that it is impossible to predict the
     * domain and distribution of the data items contained in the provided list.
     * Due to this problem, this sort is limited to a list containing Integer
     * values.  The sort method is overloaded to allow for the explicit specifying
     * of minimum and maximum value of items in the list.  Providing this will
     * improve performance, decreasing the sparseness of buckets.
     * <p>
     * <a href="http://en.wikipedia.org/wiki/Bucket_sort">Bucket Sort on Wikipedia</a>.
     *
     * @param list The list of Integers to be sorted (in place).
     */
    private static void bucketSort(List<Integer> list) {
        bucketSort(list, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Overloaded sort method that allows for explicit specification of the minimum
     * and maximum values contained in the list.  The algorithm will sort no matter
     * what, placing outlying values in the end containers.  So, it is better to
     * use this method than the generic sort if a majority of data items are between
     * some range with outliers.  This will result in more even bucket distributions
     * (which is the main determining factor in how efficient the sort will be).
     *
     * @param list    The list to sort.
     * @param listMin The lower end of the bucket spectrum
     * @param listMax The upper end of the bucket spectrum
     */
    private static void bucketSort(List<Integer> list, Integer listMin, Integer listMax) {
        if (list == null || list.size() == 0) {
            return;
        }

        int numBuckets = (int) Math.ceil(list.size() / ((list.size() / 6) == 0 ? 1 : list.size() / 6)); // ~ 30 items/bucket

        // initialize the buckets
        ArrayList<ArrayList<Integer>> buckets = new ArrayList<ArrayList<Integer>>(numBuckets);
        for (int i = 0; i < numBuckets; i++) {
            buckets.add(new ArrayList<Integer>());
        }

        // go through the list and put each item in the correct bucket
        for (int i = 0; i < list.size(); i++) {
            int bucket = bucketForNumber(list.get(i), listMin, listMax, numBuckets);
            buckets.get(bucket).add(list.get(i));
        }

        // sort each of the buckets using insertion sort
        int listIndex = 0;
        for (ArrayList<Integer> bucket : buckets) {
            net.posborne.algorithms.sorting.InsertionSort.sort(bucket);
            for (Integer item : bucket) {
                list.set(listIndex++, item);
            }
        }
    }

    /**
     * Determine what bucket a number should be in based on the parameters given.  There
     * are some tricky conditions in here that need to be dealt with as this is not
     * a simple pigeonholable implementation of bucket sort.
     *
     * @param number     The number to
     * @param listMin    The lower end of the bucket spectrum
     * @param listMax    The upper end of the bucket spectrum
     * @param numBuckets The number of buckets given
     * @return the bucket number
     */
    private static int bucketForNumber(Integer number, Integer listMin, Integer listMax, Integer numBuckets) {
        Long difference = (long) listMax - listMin;
        int increment = (int) Math.ceil(difference / numBuckets);
        increment = increment <= 0 ? 1 : increment;
        int bucket = number / increment;
        if (bucket >= numBuckets) {
            bucket = numBuckets - 1;
        } else if (bucket < 0) {
            bucket = 0;
        }
        return bucket;
    }

    private String getInnerVersion() {
        return "BucketSort Algorithm - Version 1.0";
    }

    private String getInnerAlgorithmSource() {
        return "Source: https://github.com/posborne/java-algorithms/" +
                "blob/master/src/net/posborne/algorithms/sorting/BucketSort.java";
    }

    private String getInnerDescription() {
        return "BucketSort sorts a list of integers by adding them into so-called buckets.";
    }

    public class Port implements ISort {

        @Override
        public void sort(List<Integer> listToSort) {
            BucketSort.bucketSort(listToSort);
        }

        @Override
        public void sort(List<Integer> listToSort, int listMin, int listMax) {
            BucketSort.bucketSort(listToSort, listMin, listMax);
        }

        public String getVersion() {
            return getInnerVersion();
        }

        public String getAlgorithmSource() {
            return getInnerAlgorithmSource();
        }

        public String getDescription() {
            return getInnerDescription();
        }
    }

}
