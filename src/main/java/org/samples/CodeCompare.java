package org.samples;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import org.approvaltests.core.Options;
import org.approvaltests.core.Verifiable;
import org.approvaltests.core.VerifyParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeCompare implements Verifiable {
    private final String text;

    public CodeCompare(String text) {
        this.text = text;
    }

    public static CodeCompare generateMarkdown(String snippet1, String snippet2) {
        return null;
    }

    public static CodeCompare generateMarkdown(List<Line> comparison) {
        StringBuffer text = new StringBuffer();
        text.append(getStart());
        for (Line line : comparison) {
            if (line.isUnchanged()) {
                text.append(line.getPieces().first().asHtml()).append("\n");
            } else if (line.isModified()) {
                for (Line.Piece piece : line.getPieces().where(p -> p.state() != State.ADDED)) {
                    if (piece.state() == State.REMOVED) {
                        text.append("<b style=\"color: red\">%s</b>".formatted(piece.asHtml()));
                    } else {
                        text.append(piece.asHtml());
                    }
                }
                text.append("\n");
            }
        }
        text.append(getMiddle());
        for (Line line : comparison) {
            if (line.isUnchanged()) {
                text.append(line.getPieces().first().asHtml()).append("\n");
            } else if (line.isAdded()) {
                text.append("<b style=\"color: green\">%s</b>\n".formatted(line.getPieces().first().asHtml()));
            } else if (line.isModified()) {
                for (Line.Piece piece : line.getPieces()) {
                    if (piece.state() == State.REMOVED) {
                        text.append("<s style=\"color: red\">%s</s> ".formatted(piece.asHtml()));
                    } else if (piece.state() == State.ADDED) {
                        text.append("<b style=\"color: green\">%s</b>".formatted(piece.asHtml()));
                    } else {
                        text.append(piece.asHtml());
                    }
                }
                text.append("\n");
            }
        }
        // showCompletedStep(comparison, text);
        text.append(getEnd());
        return new CodeCompare(text.toString());
    }

    private static void showCompletedStep(List<Line> comparison, StringBuffer text) {
        text.append(getMiddle());
        for (Line line : comparison) {
            if (line.isUnchanged()) {
                text.append(line.getPieces().first().asHtml()).append("\n");
            } else if (line.isAdded()) {
                text.append("<b>%s</b>\n".formatted(line.getPieces().first().asHtml()));
            } else if (line.isModified()) {
                for (Line.Piece piece : line.getPieces().where(p -> p.state() != State.REMOVED)) {
                    if (piece.state() == State.ADDED) {
                        text.append("<b>%s</b>".formatted(piece.asHtml()));
                    } else {
                        text.append(piece.asHtml());
                    }
                }
                text.append("\n");
            }
        }
    }

    private static String getMiddle() {
        return """
                </pre>
                # ⇓
                <pre style="color: gray">
                """;
    }

    private static String getStart() {
        return """
                <pre style="color: gray">
                """;
    }

    private static String getEnd() {
        return """
                </pre>
                """;
    }

    public static Patch<String> createDiff(String snippet1, String snippet2) {
        var lines1 = Arrays.asList(snippet1.split("\n"));
        var lines2 = Arrays.asList(snippet2.split("\n"));
        var patch = DiffUtils.diff(lines1, lines2);
        return patch;
    }

    public static List<String> diffStrings(String before, String after) {
        List<String> original = Arrays.asList(before.split("\n"));
        List<String> revised = Arrays.asList(after.split("\n"));
        Patch<String> patch = DiffUtils.diff(original, revised);
        List<String> diffs = new ArrayList<>();

        int originalIndex = 0, revisedIndex = 0;

        for (var delta : patch.getDeltas()) {
            // Handle unchanged lines before the delta
            while (originalIndex < delta.getSource().getPosition()) {
                diffs.add("  " + original.get(originalIndex));
                originalIndex++;
                revisedIndex++;
            }

            // Handle the delta
            switch (delta.getType()) {
                case DELETE:
                    delta.getSource().getLines().forEach(line -> diffs.add("- " + line));
                    originalIndex += delta.getSource().getLines().size();
                    break;
                case INSERT:
                    delta.getTarget().getLines().forEach(line -> diffs.add("+ " + line));
                    revisedIndex += delta.getTarget().getLines().size();
                    break;
                case CHANGE:
                    delta.getSource().getLines().forEach(line -> diffs.add("- " + line));
                    originalIndex += delta.getSource().getLines().size();
                    delta.getTarget().getLines().forEach(line -> diffs.add("+ " + line));
                    revisedIndex += delta.getTarget().getLines().size();
                    break;
            }
        }

        // Handle any remaining unchanged lines after the last delta
        while (originalIndex < original.size()) {
            diffs.add("  " + original.get(originalIndex));
            originalIndex++;
            revisedIndex++;
        }

        return diffs;
    }

    @Override
    public VerifyParameters getVerifyParameters(Options options) {
        return new VerifyParameters(options.forFile().withExtension(".md"));
    }

    @Override
    public String toString() {
        return text;
    }
}
