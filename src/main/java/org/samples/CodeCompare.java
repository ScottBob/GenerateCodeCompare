package org.samples;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import org.approvaltests.core.Options;
import org.approvaltests.core.Verifiable;
import org.approvaltests.core.VerifyParameters;
import org.lambda.query.Queryable;

import java.util.Arrays;
import java.util.List;

public class CodeCompare implements Verifiable {
    private final String text;

    public CodeCompare(String text) {
        this.text = text;
    }

    public static CodeCompare generateMarkdown(String snippet1, String snippet2) {
        List<Line> lines = Diff.diffStrings(snippet1, snippet2);
        return generateMarkdown(lines);
    }

    public static CodeCompare generateMarkdown(List<Line> comparison) {
        StringBuffer text = new StringBuffer();
        text.append(getStart());
        for (Line line : comparison) {
            if (line.isUnchanged()) {
                text.append(line.getPieces().first().asHtml()).append("\n");
            } else if (line.isModified()) {
                Queryable<Line.Piece> pieces = line.getPieces().where(p -> p.state() != State.ADDED);
                if (!pieces.isEmpty()) {
                    for (Line.Piece piece : pieces) {
                        if (piece.state() == State.REMOVED) {
                            text.append("<b style=\"color: red\">%s</b>".formatted(piece.asHtml()));
                        } else {
                            text.append(piece.asHtml());
                        }
                    }
                    text.append("\n");
                }
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

    @Override
    public VerifyParameters getVerifyParameters(Options options) {
        return new VerifyParameters(options.forFile().withExtension(".md"));
    }

    @Override
    public String toString() {
        return text;
    }
}
