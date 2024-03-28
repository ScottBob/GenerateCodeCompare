package org.samples;

import org.approvaltests.core.Options;
import org.approvaltests.core.Verifiable;
import org.approvaltests.core.VerifyParameters;

import java.io.File;
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
                        text.append("<b>%s</b>".formatted(piece.asHtml()));
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
                text.append("<b>%s</b>\n".formatted(line.getPieces().first().asHtml()));
            } else if (line.isModified()) {
                for (Line.Piece piece : line.getPieces()) {
                    if (piece.state() == State.REMOVED) {
                        text.append("<s>%s</s> ".formatted(piece.asHtml()));
                    } else if (piece.state() == State.ADDED) {
                        text.append("<b>%s</b>".formatted(piece.asHtml()));
                    } else {
                        text.append(piece.asHtml());
                    }
                }
                text.append("\n");
            }
        }
        text.append(getEnd());
        return new CodeCompare(text.toString());
    }

    private static String getMiddle() {
        return """
                </pre>
                # ⇓
                <pre>
                """;
    }

    private static String getStart() {
        return """
                <pre>
                """;
    }

    private static String getEnd() {
        return """
                </pre>
                """;
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
