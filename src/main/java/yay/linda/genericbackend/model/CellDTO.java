package yay.linda.genericbackend.model;

import lombok.Data;

import java.util.List;

@Data
public class CellDTO {
    private List<Card> cards;

    public CellDTO(Cell cell) {
        this.cards = cell.getCards();
    }
}
