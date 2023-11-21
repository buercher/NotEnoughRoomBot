import java.util.List;

public class MergeRanges {
    public static void mergeAdjacentRanges(List<String> sortedCollection) {
        if (sortedCollection.isEmpty()) {
            return;
        }

        for (int i = 0; i < sortedCollection.size() - 1; i++) {
            String currentRange = sortedCollection.get(i);
            String nextRange = sortedCollection.get(i + 1);

            String[] currentBounds = currentRange.split("\\s+");
            String[] nextBounds = nextRange.split("\\s+");

            int currentEnd = Integer.parseInt(currentBounds[1]);
            int nextStart = Integer.parseInt(nextBounds[0]);

            if (currentEnd >= nextStart) {

                sortedCollection.set(i, currentBounds[0] + " " + nextBounds[1]);

                sortedCollection.remove(i + 1);

                i--;
            }
        }
    }
}
