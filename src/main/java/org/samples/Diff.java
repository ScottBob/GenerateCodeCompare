package org.samples;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Diff {
    public static List<Line> diffStrings(String before, String after) {
        List<String> original = Arrays.asList(before.split("\n"));
        List<String> revised = Arrays.asList(after.split("\n"));
        Patch<String> patch = DiffUtils.diff(original, revised, true);
        List<String> diffs = new ArrayList<>();
        List<Line> diffs1 = new ArrayList<>();

        for (var delta : patch.getDeltas()) {
            // Handle the delta
            switch (delta.getType()) {
                case DELETE:
                    delta.getSource().getLines().forEach(line -> diffs.add("- " + line));
                    break;
                case INSERT:
                    delta.getTarget().getLines().forEach(line -> diffs1.add(Line.add(line)));
                    break;
                case CHANGE: {
                    diffs1.addAll(handleChanges(delta));
                }
                break;
                case EQUAL: {
                    List<String> lines1 = delta.getTarget().getLines();
                    lines1.forEach(line -> diffs1.add(Line.of(line)));
                }
                break;
            }
        }

        return diffs1;

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
