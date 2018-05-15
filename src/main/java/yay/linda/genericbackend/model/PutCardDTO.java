package yay.linda.genericbackend.model;

public class PutCardDTO {

    private int row;
    private int col;
    private Card card;

    public PutCardDTO() {
    }

    public PutCardDTO(int row, int col, Card card) {
        this.row = row;
        this.col = col;
        this.card = card;
    }

    public int getRow() {
        return row;
    }

    public PutCardDTO setRow(int row) {
        this.row = row;
        return this;
    }

    public int getCol() {
        return col;
    }

    public PutCardDTO setCol(int col) {
        this.col = col;
        return this;
    }

    public Card getCard() {
        return card;
    }

    public PutCardDTO setCard(Card card) {
        this.card = card;
        return this;
    }
}
