package TelegramBots.hoursSearch;

import java.util.List;

/**
 * The MergeRanges class provides a method for merging adjacent ranges in an ordered collection.
 */
public class MergeRanges {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private MergeRanges() {
    }

    /**
     * Merges adjacent ranges in an ordered collection.
     *
     * @param sortedCollection The ordered collection to be merged
     */
    public static void mergeAdjacentRanges(List<String> sortedCollection) {
        if (sortedCollection.isEmpty()) {
            return;
        }

        // Iterate through the collection, merging adjacent ranges
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
