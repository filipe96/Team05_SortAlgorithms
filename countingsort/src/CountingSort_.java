import java.util.List;

public class CountingSort_ {
    public static void sort(int[] numbers) {
        // erwartet als Parameter ein int-Array und gibt dieses sortiert wieder zurück
            // Maximum der Zahlen berechnen
            int max = numbers[0];
            for (int i = 1; i < numbers.length; i++) {
                // wenn es größeres als das aktuelle gibt, ist das nun das neue größte
                if (numbers[i] > max)
                    max = numbers[i];
            }

            // temporäres Array erzeugen mit: Länge = Maximum des Zahlenarrays + die "0"
            int[] sortedNumbers = new int[max+1];

            // Indizes des Zahlen-Arrays durchgehen
            for (int i = 0; i < numbers.length; i++) {
                // wir zählen, wie oft jede Zahl aus numbers vorkommt und
                // speichern diese Anzahl in sortedNumbers[] bei Index number[i]
                sortedNumbers[numbers[i]]++;
            }

            // insertPosition steht für die Schreib-Position im Ausgabe-Array
            int insertPosition = 0;

            // Indizes von sortedNumbers[] durchgehen, um zu sehen, wie oft jede Zahl vorkommt
            for (int i = 0; i <= max; i++) {
                // Anzahl von i durchgehen, um gleiche Zahlen hintereinander einzutragen
                for (int j = 0; j < sortedNumbers[i]; j++) {
                    // das Zahlen-Array wird jetzt sortiert neu geschrieben für jedes
                    // Auftreten von i
                    numbers[insertPosition] = i;
                    insertPosition++;
                }
            }
            return numbers;
        }

    public class Port {

    }

}

