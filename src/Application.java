import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Application {
    private Object port;

    public void createSortingPortInstance() {
       // try {

        //}
    }

    public void updateConfigurationPreferences() {

    }

    public void setSortingProperties() {
        Properties properties = new Properties();
        String sortingProperties = Configuration.instance.sortingProperties;

        try (FileInputStream fileStream = new FileInputStream(sortingProperties)) {
            properties.load(fileStream);
        } catch (FileNotFoundException exc) {
            System.err.println("Error: Sorting properties '" + sortingProperties +
                    "' could not be found!");
            exc.printStackTrace();
            return;
        } catch (IOException exc) {
            System.err.println("Error: Cannot load file from '" + sortingProperties + "'");
            exc.printStackTrace();
            return;
        }

        String sortingType = properties.getProperty(SortingPropertyKeys.sortingType.name());
        for (AvailableSortingAlgorithms algorithm : AvailableSortingAlgorithms.values()) {
            if (algorithm.name().equals(sortingType)) {
                Configuration.instance.setSortingType(AvailableSortingAlgorithms.valueOf(sortingType));
                return;
            }
        }

        System.err.println("Error: Invalid sorting algorithm '" + sortingType + "'");
        throw new UnsupportedOperationException("unknown sorting type");

    }

    public static void main(String[] args) {
        Application application = new Application();

        System.out.println("");

        application.setSortingProperties();
    }
}
