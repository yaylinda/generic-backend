package yay.linda.genericbackend.model;

public class Cell {

    private CellState state;
    private Card card;

    public Cell() {
        this.state = CellState.EMPTY;
    }

    public Cell(CellState state, Card card) {
        this.state = state;
        this.card = card;
    }

    public CellState getState() {
        return state;
    }

    public Cell setState(CellState state) {
        this.state = state;
        return this;
    }

    public Card getCard() {
        return card;
    }

    public Cell setCard(Card card) {
        this.card = card;
        return this;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "state=" + state +
                ", card=" + card +
                '}';
    }
}
