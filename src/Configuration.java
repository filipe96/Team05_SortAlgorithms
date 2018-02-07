import java.util.ArrayList;
import java.util.List;

public enum Configuration {
    instance;

    public AvailableSortingAlgorithms sortingType = AvailableSortingAlgorithms.bucketsort;
    public List<Integer> listToSort = new ArrayList<>();

    public String userDirectory = System.getProperty("user.dir");
    public String fileSeparator = System.getProperty("file.separator");

    // [userDirectory]/[sortingType]/jar/[sortingType].jar
    // e.g. ../bucketsort/jar/bucketsort.jar
    public String pathToJar = userDirectory + fileSeparator + sortingType.name() +
            fileSeparator + "jar" + fileSeparator + sortingType.name() + ".jar";

    public String propertiesFile = "sorting.properties";
    public String sortingProperties = userDirectory + fileSeparator + propertiesFile;

    public void setSortingType(AvailableSortingAlgorithms type) {
        sortingType = type;
    }

    public void setPathToJar(AvailableSortingAlgorithms type) {
        pathToJar = userDirectory + fileSeparator + type.name() + fileSeparator +
                "jar" + fileSeparator + type.name() + ".jar";
    }

    public String getPathToJar() {
        return pathToJar;
    }

    public String constructJarPath(AvailableSortingAlgorithms algorithm) {
        return userDirectory + fileSeparator + algorithm.name() + fileSeparator + "jar" +
                fileSeparator + algorithm.name() + ".jar";
    }

    public String getClassNameForSortAlgorithm(AvailableSortingAlgorithms algorithm) {
        switch (algorithm) {
            case bucketsort:
                return "BucketSort";

            case countingsort:
                return "CountingSort";
        }

        return "Component";
    }

    public void setListToSort(List<Integer> list) {
        listToSort = list;
    }
}
