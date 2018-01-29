import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Application {
    private Object port;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void createSortingPortInstance() {
        Object instance;

        try {
            System.out.println("pathToJar : " + Configuration.instance.getPathToJar());
            URL[] urls = { new File(Configuration.instance.getPathToJar()).toURI().toURL() };
            URLClassLoader urlClassLoader = new URLClassLoader(urls, Application.class.getClassLoader());

            String classNameToLoad = "Component";
            switch (Configuration.instance.sortingType) {
                case bucketsort:
                    classNameToLoad = "BucketSort";
                    break;

                case countingsort:
                    classNameToLoad = "CountingSort";
                    break;
            }

            Class clazz = Class.forName(classNameToLoad, true, urlClassLoader);
            System.out.println("clazz     : " + clazz.toString());

            instance = clazz.getMethod("getInstance").invoke(null);
            port = clazz.getDeclaredField("port").get(instance);
            System.out.println("port      : " + port.hashCode());

            Method getVersion = port.getClass().getMethod("getVersion");
            String version = (String) getVersion.invoke(port);
            System.out.println("version   : " + version);
        } catch (Exception e) {
            System.out.println("--- exception");
            System.out.println(e.getMessage());
        }
    }

    public void updateConfigurationPreferences() {

    }

    public void loadSortingProperties() {
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

    public void execute() {
        boolean quit = false;
        while (!quit) {
            System.out.print("Command: ");
            String commandString = new Scanner(System.in).nextLine();

            StringBuilder stringBuilder = new StringBuilder();
            StringTokenizer tokenizer = new StringTokenizer(commandString);
            boolean reachedEndOfCommandWords = false;
            while (tokenizer.hasMoreTokens() && !reachedEndOfCommandWords) {
                String commandWord = tokenizer.nextToken();

                switch (commandString) {
                    case "show":
                    case "components":
                    case "set":
                    case "current":
                    case "component":
                    case "execute":
                    case "quit":
                    case "exit":
                        stringBuilder.append(commandString.concat(" "));
                        break;

                    default:
                        reachedEndOfCommandWords = true;
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            String arguments = commandString.replaceFirst("^" + stringBuilder.toString(), "");
            switch (stringBuilder.toString()) {
                case "show components":

                    break;
                case "show current component":
                case "set current component":
                case "execute":
                case "quit":
                case "exit":
            }
        }
    }

    public void printAvailableCommands() {
        System.out.println("The set of available commands includes following:\n");
        System.out.println("  show components               List all available sorting algorithms.");
        System.out.println("  show current component        Display the currently selected component.");
        System.out.println("  set current component <name>  Choose <name> as the current component.");
        System.out.println("                                The list of available components is displayed below.");
        System.out.println("  execute n1, n2, n3, ...       Execute the selected sorting algorithm with");
        System.out.println("                                a list consisting of n1, n2, n3 (integer values).");
        System.out.println("  quit / exit                   Quit the command prompt.");
    }

    public static void main(String[] args) {
        Application application = new Application();
        application.loadSortingProperties();
        application.createSortingPortInstance();
        application.printAvailableCommands();
        application.execute();
    }
}
