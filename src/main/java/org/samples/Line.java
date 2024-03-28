package org.samples;

import org.lambda.query.Queryable;

public class Line {
    private final Queryable<Piece> pieces = new Queryable<>(Piece.class);

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
        return 1 < pieces.size();
    }

    public static record Piece(String text, State state) {
        public String asHtml() {
            return text.replace("<", "&lt;");
        }
    }
}
