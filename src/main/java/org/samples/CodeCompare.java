package org.samples;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
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

    public static List<Line> diffStrings(String before, String after) {
        List<String> original = Arrays.asList(before.split("\n"));
        List<String> revised = Arrays.asList(after.split("\n"));
        Patch<String> patch = DiffUtils.diff(original, revised, true);
        List<String> diffs = new ArrayList<>();
        List<Line> diffs1 = new ArrayList<>();

        DiffRowGenerator generator = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .inlineDiffByWord(true)
                .oldTag(f -> "~~")
                .newTag(f -> "^^")
                .build();
        List<DiffRow> diffRows = generator.generateDiffRows(original, revised);
        for (DiffRow diffRow : diffRows) {
            if (diffRow.getOldLine().equals(diffRow.getNewLine())) {
                diffs1.add(Line.of(diffRow.toString()));
            } else {
                diffs1.add(Line.of("").remove(diffRow.getOldLine()));
                diffs1.add(Line.add(diffRow.getNewLine()));
            }
        }
        return diffs1;

        /*
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

         */
    }

    private static List<Line> handleChanges(AbstractDelta<String> delta) {
        List<Line> changes = new ArrayList<>();
        List<String> lines = delta.getSource().getLines();
        List<String> lines1 = delta.getTarget().getLines();
        lines.forEach(line -> changes.add(Line.of("").remove(line)));
        lines1.forEach(line -> changes.add(Line.add(line)));
        return changes;
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
