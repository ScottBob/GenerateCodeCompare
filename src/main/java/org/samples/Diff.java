package org.samples;

import com.github.difflib.patch.AbstractDelta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Diff {
    public static List<Line> diffStrings(String before, String after) {
        List<String> startingLines = Arrays.asList(before.split("\n"));
        List<String> endingLines = Arrays.asList(after.split("\n"));
        List<Line> diffs1 = new ArrayList<>();
        int startIndex = 0;
        int endIndex = 0;
        while (startIndex < startingLines.size() && endIndex < endingLines.size()) {
            String startLine = getLine(startingLines, startIndex);
            String endLine = getLine(endingLines, endIndex);
            if (startLine.equals(endLine)) {
                diffs1.add(Line.of(startLine));
                startIndex++;
                endIndex++;
            } else if (isLineAdded(startLine)) {
                diffs1.add(Line.add(removeTrailingInfo(startLine)));
                startIndex++;
            } else if (isLineAdded(endLine)) {
                diffs1.add(Line.add(removeTrailingInfo(endLine)));
                endIndex++;
            } else if (isLineModified(startLine, endLine)) {
                diffs1.add(createModifiedLine(removeTrailingInfo(startLine), removeTrailingInfo(endLine)));
                startIndex++;
                endIndex++;
            } else {
                throw new RuntimeException("Unrecognized line:\n" + startLine + "\n" + endLine);
            }
        }

        return diffs1;

    }

    private static String removeTrailingInfo(String line) {
        // Remove everything after the "// "
        return line.replaceAll("//.*$", "");
    }

    private static boolean isLineModified(String startLine, String endLine) {
        return startLine.trim().endsWith("*") && endLine.trim().endsWith("*");
    }

    private static boolean isLineAdded(String line) {
        return line.trim().endsWith("+");
    }

    private static String getLine(List<String> lines, int index) {
        if (lines.size() <= index) {
            return "";
        }
        return lines.get(index);
    }



    /****************************************/


    public static Line createModifiedLine(String start, String end) {
        String[] startWords = start.split("\\s+");
        String[] endWords = end.split("\\s+");

        List<Change> changes = calculateChanges(startWords, endWords);

        Line line = new Line("", State.CONSTANT);
        for (Change change : changes) {
            switch (change.type) {
                case ADDED:
                    line.replace(change.text);
                    break;
                case REMOVED:
                    line.remove(change.text);
                    break;
                case CONSTANT:
                    line.and(change.text);
                    break;
            }
        }

        return line;
    }

    private static List<Change> calculateChanges(String[] startWords, String[] endWords) {
        int startLength = startWords.length;
        int endLength = endWords.length;
        int[][] dp = new int[startLength + 1][endLength + 1];

        // Calculate the length of the longest common subsequence (LCS)
        for (int i = 1; i <= startLength; i++) {
            for (int j = 1; j <= endLength; j++) {
                if (startWords[i - 1].equals(endWords[j - 1])) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        // Trace back the LCS to find changes
        List<Change> changes = new ArrayList<>();
        int i = startLength, j = endLength;
        while (i > 0 && j > 0) {
            if (startWords[i - 1].equals(endWords[j - 1])) {
                changes.add(0, new Change(State.CONSTANT, startWords[i - 1]));
                i--;
                j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                changes.add(0, new Change(State.REMOVED, startWords[i - 1]));
                i--;
            } else {
                changes.add(0, new Change(State.ADDED, endWords[j - 1]));
                j--;
            }
        }

        while (i > 0) {
            changes.add(0, new Change(State.REMOVED, startWords[i - 1]));
            i--;
        }
        while (j > 0) {
            changes.add(0, new Change(State.ADDED, endWords[j - 1]));
            j--;
        }

        return changes;
    }

    private static class Change {
        State type;
        String text;

        public Change(State type, String text) {
            this.type = type;
            this.text = text;
        }
    }
}
