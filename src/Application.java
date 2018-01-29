import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class Application {
    private Object portInstance;

    public Object createSortingPortInstance(boolean debugMode) {
        return createSortingPortInstance(Configuration.instance.sortingType, debugMode);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object createSortingPortInstance(AvailableSortingAlgorithms sortingType, boolean debugMode) {
        Object port = null;
        try {
            Configuration.instance.setPathToJar(sortingType);
            URL[] urls = { new File(Configuration.instance.getPathToJar()).toURI().toURL() };
            URLClassLoader urlClassLoader = new URLClassLoader(urls, Application.class.getClassLoader());

            String classNameToLoad = Configuration.instance.getClassNameForSortAlgorithm();
            Class<?> clazz = Class.forName(classNameToLoad, true, urlClassLoader);

            Object instance = clazz.getMethod("getInstance").invoke(null);
            port = clazz.getDeclaredField("port").get(instance);

            Method getVersion = port.getClass().getMethod("getVersion");
            String version = (String) getVersion.invoke(port);

            if (debugMode) {
                System.out.println("pathToJar : " + Configuration.instance.getPathToJar());
                System.out.println("clazz     : " + clazz.toString());
                System.out.println("port      : " + port.hashCode());
                System.out.println("version   : " + version);
            }
        } catch (NoSuchMethodException exc) {
            System.out.println("--- exception");
            System.out.println(exc.getMessage());
            exc.printStackTrace();
        } catch (ClassNotFoundException | NoSuchFieldException exc) {
            exc.printStackTrace();
        } catch (IllegalAccessException exc) {
            exc.printStackTrace();
        } catch (InvocationTargetException exc) {
            exc.printStackTrace();
        } catch (MalformedURLException exc) {
            exc.printStackTrace();
        }

        return port;
    }

    public void updateConfigurationPreferences(AvailableSortingAlgorithms selectedAlgorithm, List<Integer> listToSort) {
        Configuration.instance.setSortingType(selectedAlgorithm);
        Configuration.instance.setListToSort(listToSort);
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

        boolean validAlgorithmSpecified = false;
        String sortingType = properties.getProperty(SortingPropertyKeys.sortingType.name());
        for (AvailableSortingAlgorithms algorithm : AvailableSortingAlgorithms.values()) {
            if (algorithm.name().equals(sortingType)) {
                validAlgorithmSpecified = true;
            }
        }

        if (!validAlgorithmSpecified) {
            System.err.println("Error: Invalid sorting algorithm '" + sortingType + "'");
            throw new UnsupportedOperationException("unknown sorting type");
        }

        String listToSort = properties.getProperty(SortingPropertyKeys.listToSort.name());
        List<Integer> parsedList = parseList(listToSort);
        updateConfigurationPreferences(AvailableSortingAlgorithms.valueOf(sortingType), parsedList);
    }

    public void writeSortingProperties() {
        Properties properties = new Properties();
        String sortingProperties = Configuration.instance.sortingProperties;

        properties.setProperty(SortingPropertyKeys.sortingType.name(), Configuration.instance.sortingType.name());
        properties.setProperty(SortingPropertyKeys.listToSort.name(), convertToString(Configuration.instance.listToSort));

        try (OutputStream outputStream = new FileOutputStream(sortingProperties)) {
            properties.store(outputStream, "Comment");
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public String convertToString(List<Integer> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size() - 1; i++) {
            stringBuilder.append(list.get(i)).append(", ");
        }
        stringBuilder.append(list.get(list.size() - 1));
        return stringBuilder.toString();
    }

    public List<Integer> parseList(String listRepresentation) {
        try {
            return parseAppliedList(listRepresentation);
        } catch (NumberFormatException exc) {
            System.err.println("Error: Invalid list format");
            exc.printStackTrace();
        }
        return null;
    }

    public List<Integer> parseAppliedList(String listRepresentation) {
        if (!listRepresentation.matches("^\\s*[0-9]+(\\s*,\\s*[0-9]+)*$")) {
            throw new NumberFormatException("list format is invalid (expected: n1, n2, n3, ...)");
        }

        String[] listElements = listRepresentation.replaceAll("\\s+", "").split(",");
        List<Integer> listValues = new ArrayList<>();

        try {
            for (String slot : listElements) {
                listValues.add(Integer.parseInt(slot));
            }
        } catch (NumberFormatException exc) {
            exc.printStackTrace();
        }

        return listValues;
    }

    public String getVersionFromPort(Object distinctPort) {
        String version = null;
        try {
            Method method = distinctPort.getClass().getMethod("getVersion");
            version = (String) method.invoke(distinctPort);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exc) {
            exc.printStackTrace();
        }

        return version;
    }

    public String getDescriptionFromPort(Object distinctPort) {
        String description = null;
        try {
            Method method = distinctPort.getClass().getMethod("getDescription");
            description = (String) method.invoke(distinctPort);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exc) {
            exc.printStackTrace();
        }

        return description;
    }

    public void executeSortAlgorithm(Object distinctPort, List<Integer> listToSort) {
        try {
            Method method = distinctPort.getClass().getMethod("sort", List.class);
            method.invoke(distinctPort, listToSort);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exc) {
            exc.printStackTrace();
        }
    }

    public void execute() {
        portInstance = createSortingPortInstance(true);

        printAvailableCommands();
        boolean quit = false;
        while (!quit) {
            loadSortingProperties();
            System.out.println("\nCurrent component: " + Configuration.instance.sortingType.name());
            System.out.print("Command: ");
            String commandString = new Scanner(System.in).nextLine();

            StringBuilder stringBuilder = new StringBuilder();
            StringTokenizer tokenizer = new StringTokenizer(commandString, " ");
            boolean reachedEndOfCommandWords = false;
            while (tokenizer.hasMoreTokens() && !reachedEndOfCommandWords) {
                String commandWord = tokenizer.nextToken();

                switch (commandWord) {
                    case "show":
                    case "components":
                    case "set":
                    case "current":
                    case "component":
                    case "execute":
                    case "quit":
                    case "exit":
                        stringBuilder.append(commandWord.concat(" "));
                        break;

                    default:
                        reachedEndOfCommandWords = true;
                }
            }

            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }

            String arguments = commandString.replaceFirst("^" + stringBuilder.toString(), "").trim();
            switch (stringBuilder.toString()) {
                case "show components":
                    printAvailableComponents();
                    break;
                case "show current component":
                    printCurrentComponent();
                    break;
                case "set current component":
                    if (!arguments.isEmpty()) {
                        boolean validComponent = false;
                        for (AvailableSortingAlgorithms component : AvailableSortingAlgorithms.values()) {
                            if (component.name().equals(arguments)) {
                                validComponent = true;
                            }
                        }

                        if (validComponent) {
                            Configuration.instance.setSortingType(AvailableSortingAlgorithms.valueOf(arguments));
                        } else {
                            System.err.println("Error: invalid component name '" + arguments + "'.");
                        }

                    } else {
                        System.err.println("Error: Missing argument <name>!");
                    }
                    break;
                case "execute":
                    if (!arguments.isEmpty()) {
                        List<Integer> sortedList = parseList(arguments);
                        System.out.println("List before sort: " + convertToString(sortedList));
                        executeSortAlgorithm(portInstance, sortedList);
                        System.out.println("List after sort:  " + convertToString(sortedList));
                    } else {
                        System.out.println("List before sort: " + convertToString(Configuration.instance.listToSort));
                        executeSortAlgorithm(portInstance, Configuration.instance.listToSort);
                        System.out.println("List after sort:  " + convertToString(Configuration.instance.listToSort));
                    }
                    break;
                case "quit":
                case "exit":
                    quit = true;
                    break;
                default:
                    System.err.println("Error");
            }

            writeSortingProperties();
        }
    }

    public void printCurrentComponent() {
        System.out.println("\nCurrent component:\n");
        String algorithmVersion = getVersionFromPort(portInstance);
        String algorithmDescription = getDescriptionFromPort(portInstance);
        System.out.printf("  %c %s\n", '*', Configuration.instance.sortingType.name());
        System.out.println("      " + algorithmVersion);
        System.out.println("      " + algorithmDescription);
    }

    public void printAvailableComponents() {
        System.out.println("\nAvailable components:\n");
        for (AvailableSortingAlgorithms algorithm : AvailableSortingAlgorithms.values()) {
            Object algorithmPort = createSortingPortInstance(algorithm, false);
            String algorithmVersion = getVersionFromPort(algorithmPort);
            String algorithmDescription = getDescriptionFromPort(algorithmPort);
            System.out.printf("  %c %s\n", (algorithm == Configuration.instance.sortingType ? '*' : '-'), algorithm.name());
            System.out.println("      " + algorithmVersion);
            System.out.println("      " + algorithmDescription);
            System.out.println();
        }
        System.out.println("\n  Current component *");
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
        application.execute();
    }
}
