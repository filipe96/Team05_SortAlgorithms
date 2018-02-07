
import java.util.ArrayList;
import java.util.List;

public class CountingSort {

    private static CountingSort instance = new CountingSort();
    public Port port;

    private CountingSort() {
        port = new Port();
    }

    public static CountingSort getInstance() {
        return instance;
    }

    private static void countingSort(List<Integer> list) {

        if (list == null || list.size() == 0) {
            return;
        }

        int max = list.get(0);
        for (int number : list) {
            if (number > max)
                max = number;
        }

        List<Integer> sortedList = new ArrayList<>();

        for (int i = 0; i <= max; i++) {
            sortedList.add(0);
        }


        for (int index : list) {
            sortedList.set(index, sortedList.get(index) + 1);
        }

        int insertPositionOfIndex = 0;

        for (int i = 0; i <= max; i++) {
            for (int j = 0; j < sortedList.get(i); j++) {
                list.set(insertPositionOfIndex, i);
                insertPositionOfIndex++;
            }
        }
    }

    private String getInnerVersion() {
        return "CountingSort Algorithm - Version 1.0";
    }

    private String getInnerDescription() {
        return "Counting sort is an algorithm for sorting a collection of objects according to keys that are small integers.";
    }

    private String getInnerAlgorithmSource() {
        return "Source: https://de.wikibooks.org/wiki/Algorithmensammlung:_Sortierverfahren:_Countingsort";
    }

    public class Port implements ISort {

        @Override
        public void sort(List<Integer> listToSort) {
            CountingSort.countingSort(listToSort);
        }

        public String getVersion() {
            return getInnerVersion();
        }

        public String getDescription() {
            return getInnerDescription();
        }

        public String getAlgorithmSource() {
            return getInnerAlgorithmSource();
        }

    }

}

