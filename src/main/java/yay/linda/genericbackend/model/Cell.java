package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Cell {
    private Map<String, Card> idToCardMap;

    public Cell() {
        this.idToCardMap = new HashMap<>();
    }

    public List<Card> getCards() { // this should be read-only
        return new ArrayList<>(this.idToCardMap.values());
    }

    public Boolean isAvailable() {
        return CollectionUtils.isEmpty(this.idToCardMap);
    }

    public void incrementCardsNumTurnsOnBoard(String username) {
        this.idToCardMap.values().forEach(c -> c.incrementNumTurnsOnBoard(username));
    }

    public void addCard(Card card) {
        this.idToCardMap.put(card.getId(), card);
    }

    public void removeCard(Card card) {
        this.idToCardMap.remove(card.getId());
    }
}
