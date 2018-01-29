import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Application {
    private Object port;


    public void setConfigurationProperties() {
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
        switch (sortingType) {
            case "bucketsort":
            case "countingsort":
                Configuration.instance.setSortingType(AvailableSortingAlgorithms.valueOf(sortingType));
                break;

            default:
                System.err.println("Error: Invalid sorting algorithm '" + sortingType + "'");
                throw new UnsupportedOperationException("unknown sorting type");
        }

    }

    public static void main(String[] args) {
        Application application = new Application();
        application.setConfigurationProperties();
    }
}
