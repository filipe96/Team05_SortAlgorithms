package main;

import factory.PortFactory;
import sort.SortingAlgorithm;
import sort.SortingPropertyKeys;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Application {
    private Object portInstance;

    private void updateConfigurationPreferences(SortingAlgorithm selectedAlgorithm, List<Integer> listToSort) {
        Configuration.instance.setSortingType(selectedAlgorithm);
        Configuration.instance.setListToSort(listToSort);
        Configuration.instance.setPathToJar(selectedAlgorithm);
    }

    private void loadSortingProperties() {
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
        for (SortingAlgorithm algorithm : SortingAlgorithm.values()) {
            if (algorithm.name().equals(sortingType)) {
                validAlgorithmSpecified = true;
            }
        }

        if (!validAlgorithmSpecified) {
            System.err.println("Error: Invalid sorting algorithm '" + sortingType + "'");
            throw new UnsupportedOperationException("unknown sorting type");
        }

        String listToSort = properties.getProperty(SortingPropertyKeys.listToSort.name());
        System.out.println("listToSort: " + listToSort);
        List<Integer> parsedList = parseList(listToSort);
        updateConfigurationPreferences(SortingAlgorithm.valueOf(sortingType), parsedList);
    }

    private void writeSortingProperties() {
        Properties properties = new Properties();
        String sortingProperties = Configuration.instance.sortingProperties;

        properties.setProperty(SortingPropertyKeys.sortingType.name(), Configuration.instance.sortingType.name());
        properties.setProperty(SortingPropertyKeys.listToSort.name(), "5, 3, 7, 6, 1, 4, 8, 2, 9");

        try (OutputStream outputStream = new FileOutputStream(sortingProperties)) {
            properties.store(outputStream, "Comment");
        } catch (FileNotFoundException exc) {
            System.err.println("FileNotFoundException: " + exc.getMessage());
        } catch (IOException exc) {
            System.err.println("IOException: " + exc.getMessage());
        }
    }

    private String convertToString(List<Integer> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size() - 1; i++) {
            stringBuilder.append(list.get(i)).append(", ");
        }
        stringBuilder.append(list.get(list.size() - 1));
        return stringBuilder.toString();
    }

    private List<Integer> parseList(String listRepresentation) {
        try {
            return parseAppliedList(listRepresentation);
        } catch (NumberFormatException exc) {
            System.err.println("Error: Invalid list format");
            System.err.println(exc.getMessage());
        }
        return null;
    }

    private List<Integer> parseAppliedList(String listRepresentation) {
        if (!listRepresentation.matches("^\\s*[0-9]+(\\s*,\\s*[0-9]+)*,?\\s*$")) {
            throw new NumberFormatException("list format is invalid (expected: n1, n2, n3, ...)");
        }

        String[] listElements = listRepresentation.replaceAll("\\s+", "").split(",");
        listElements = removeEmptyStrings(listElements);
        List<Integer> listValues = new ArrayList<>();

        try {
            for (String slot : listElements) {
                listValues.add(Integer.parseInt(slot));
            }
        } catch (NumberFormatException exc) {
            System.err.println(exc.getMessage());
        }

        return listValues;
    }

    private String[] removeEmptyStrings(String[] listElements) {
        List<String> filteredList = new ArrayList<>();
        for (String element : listElements) {
            if (!element.isEmpty()) {
                filteredList.add(element);
            }
        }
        return filteredList.toArray(new String[0]);
    }

    private String getInformationFromPort(Object distinctPort, String methodName) {
        String information = null;
        try {
            Method method = distinctPort.getClass().getMethod(methodName);
            information = (String) method.invoke(distinctPort);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exc) {
            exc.printStackTrace();
        }

        return information;
    }

    private void executeSortAlgorithm(Object distinctPort, List<Integer> listToSort) {
        try {
            Method method = distinctPort.getClass().getMethod("sort", List.class);
            method.invoke(distinctPort, listToSort);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exc) {
            exc.printStackTrace();
        }
    }

    private void execute() {
        portInstance = PortFactory.produceInstance(true);

        printAvailableCommands();
        boolean quit = false;
        while (!quit) {
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
                    case "help":
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
                        for (SortingAlgorithm component : SortingAlgorithm.values()) {
                            if (component.name().equals(arguments)) {
                                validComponent = true;
                            }
                        }

                        if (validComponent) {
                            Configuration.instance.setSortingType(SortingAlgorithm.valueOf(arguments));
                            portInstance = PortFactory.produceInstance(SortingAlgorithm.valueOf(arguments), true);
                        } else {
                            System.err.println("Error: invalid component name '" + arguments + "'.");
                            System.err.println("Call 'show components' for a list of valid component names.");
                        }

                    } else {
                        System.err.println("Error: Missing argument <name>!");
                    }
                    break;
                case "execute":
                    if (!arguments.isEmpty()) {
                        List<Integer> appliedList = parseList(arguments);
                        if (appliedList != null) {
                            System.out.println("List before sort: " + convertToString(appliedList));
                            executeSortAlgorithm(portInstance, appliedList);
                            System.out.println("List after sort:  " + convertToString(appliedList));
                        }
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
                case "help":
                    printAvailableCommands();
                    break;
                default:
                    System.err.println("Error: Unknown command '" + commandString + "'!");
            }

            writeSortingProperties();
        }
    }

    private void printCurrentComponent() {
        System.out.println("\nCurrent component:\n");
        String algorithmVersion = getInformationFromPort(portInstance, "getVersion");
        String algorithmDescription = getInformationFromPort(portInstance, "getDescription");
        String algorithmSource = getInformationFromPort(portInstance, "getAlgorithmSource");
        System.out.printf("  %c %s\n", '*', Configuration.instance.sortingType.name());
        System.out.println("      " + algorithmVersion);
        System.out.println("      " + algorithmDescription);
        System.out.println("      " + algorithmSource);
    }

    private void printAvailableComponents() {
        System.out.println("\nAvailable components:\n");
        for (SortingAlgorithm algorithm : SortingAlgorithm.values()) {
            Object algorithmPort = PortFactory.produceInstance(algorithm, false);
            String algorithmVersion = getInformationFromPort(algorithmPort, "getVersion");
            String algorithmDescription = getInformationFromPort(algorithmPort, "getDescription");
            String algorithmSource = getInformationFromPort(algorithmPort, "getAlgorithmSource");
            System.out.printf("  %c %s\n", (algorithm == Configuration.instance.sortingType ? '*' : '-'), algorithm.name());
            System.out.println("      " + algorithmVersion);
            System.out.println("      " + algorithmDescription);
            System.out.println("      " + algorithmSource);
            System.out.println();
        }
        System.out.println("\n  Current component *");
    }

    private void printAvailableCommands() {
        System.out.println("\nThe set of available commands includes following:\n");
        System.out.println("  show components               List all available sorting algorithms.");
        System.out.println("  show current component        Display the currently selected component.");
        System.out.println("  set current component <name>  Choose <name> as the current component.");
        System.out.println("                                The list of available components is displayed below.");
        System.out.println("  execute [n1, n2, n3, ...]     Execute the selected sorting algorithm with");
        System.out.println("                                a list consisting of n1, n2, n3 (integer values).");
        System.out.println("  help                          List available commands.");
        System.out.println("  quit / exit                   Quit the command prompt.");
    }

    public static void main(String[] args) {
        Application application = new Application();
        application.loadSortingProperties();
        application.execute();
    }
}
