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

    public static Line createModifiedLine(String start, String end) {
        String initialPart = start.substring(0, start.indexOf("="));
        String startRemainingPart = start.replace(initialPart, "");
        String endRemainingPart = end.replace(initialPart, "");

        Line line = Line.of(initialPart)
                .remove(startRemainingPart)
                .replace(endRemainingPart);

        return line;
//        Line line = Line.of("");
//        // Break both strings into words
//        String[] startWords = start.split("\\s+");
//        String[] endWords = end.split("\\s+");
//        // Compare word by word. If they are the same, add it to the line using Line.and(), if they are different remove the startWords and add the endWords
//
//        int minLength = Math.min(startWords.length, endWords.length);
//        for (int i = 0; i < minLength; i++) {
//            if (startWords[i].equals(endWords[i])) {
//                line = line.and(startWords[i]);
//            } else {
//                line = line.remove(startWords[i]);
//                line = line.and(endWords[i]);
//            }
//        }
//// add remaining words from the longer string
//        if (startWords.length < endWords.length) {
//            for (int i = minLength; i < endWords.length; i++) {
//                line = line.and(endWords[i]);
//            }
//        } else if (startWords.length > endWords.length) {
//            for (int i = minLength; i < startWords.length; i++) {
//                line = line.remove(startWords[i]);
//            }
//        }
//
//        return line.and("\n");
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

    public static List<Line> handleChanges(AbstractDelta<String> delta) {
        List<String> startingLines = delta.getSource().getLines();
        List<String> endingLines = delta.getTarget().getLines();
        List<Line> changes = new ArrayList<>();
        List<String> lines = delta.getSource().getLines();
        List<String> lines1 = delta.getTarget().getLines();
        lines.forEach(line -> changes.add(Line.of("").remove(line)));
        lines1.forEach(line -> changes.add(Line.add(line)));
        return changes;
    }
}
