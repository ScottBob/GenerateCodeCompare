package org.samples;

import org.lambda.query.Queryable;

public class Line {
    private Queryable<Piece> pieces = new Queryable<>(Piece.class);

    public Line(String text, State state) {
        pieces.add(new Piece(text, state));
    }

    public static Line of(String text) {
        return new Line(text, State.CONSTANT);
    }

    public static Line add(String text) {
        return new Line(text, State.ADDED);
    }

    public Line remove(String text) {
        pieces.add(new Piece(text, State.REMOVED));
        return this;
    }

    public static Line removed(String text) {
        return new Line(text, State.REMOVED);
    }

    public Line replace(String text) {
        pieces.add(new Piece(text, State.ADDED));
        return this;
    }

    public Line and(String text) {
        pieces.add(new Piece(text, State.CONSTANT));
        return this;
    }

    public boolean isUnchanged() {
        return pieces.all(p -> p.state == State.CONSTANT);
    }

    public Queryable<Piece> getPieces() {
        return pieces;
    }

    public boolean isAdded() {
        return pieces.all(p -> p.state == State.ADDED);
    }

    public boolean isModified() {
        return  pieces.any(p -> p.state != State.CONSTANT);
    }

    public Line reduce() {
        var reduced = new Queryable<Piece>();
        var last = pieces.first();
        reduced.add(last);
        for (Piece piece : pieces.skip(1)) {
            if (piece.state == last.state) {
                last = new Piece(last.text + piece.text, last.state);
                reduced.remove(reduced.size()-1);
                reduced.add(last);
            } else {
                last = piece;
                reduced.add(last);
            }
        }
        pieces = reduced;
        return this;
    }

    public static record Piece(String text, State state) {
        public String asHtml() {
            return text.replace("<", "&lt;");
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        pieces.forEach(piece -> result.append(piece.text).append(" (").append(piece.state).append(")\n"));
        return result.toString();
    }
}
