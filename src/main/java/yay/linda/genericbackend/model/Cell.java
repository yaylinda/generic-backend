package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
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

    public Boolean isFriendlyCell(String username) {
        return isAvailable() || idToCardMap.values().stream().allMatch(c -> c.getOwner().equals(username));
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

    public void handleClash(String playerA, String playerB, Integer advancementPoints) {
        // partition
        Map<String, List<Card>> usernameToCardMap = new HashMap<>();
        usernameToCardMap.put(playerA, new ArrayList<>());
        usernameToCardMap.put(playerB, new ArrayList<>());
        this.idToCardMap.values().forEach(c -> usernameToCardMap.get(c.getOwner()).add(c));

        // sort
        usernameToCardMap.values().forEach(Collections::sort);

        // fight
        while (isOpponentAlive(usernameToCardMap)) {
            Map<String, Card> fighters = new HashMap<>();
            usernameToCardMap.forEach((k, v) -> fighters.put(k, v.get(0)));

            Card cardA = fighters.get(playerA);
            Card cardB = fighters.get(playerB);

            advancementPoints += Math.min(cardA.getMight(), cardB.getMight());

            if (cardA.getMight() > cardB.getMight()) {
                cardA.setMight(cardA.getMight() - cardB.getMight());
                usernameToCardMap.get(playerA).set(0, cardA);
                usernameToCardMap.get(playerB).remove(0);
            } else if (cardA.getMight() < cardB.getMight()) {
                cardB.setMight(cardB.getMight() - cardA.getMight());
                usernameToCardMap.get(playerA).remove(0);
                usernameToCardMap.get(playerB).set(0, cardB);
            } else {
                usernameToCardMap.get(playerA).remove(0);
                usernameToCardMap.get(playerB).remove(0);
            }
        }

        // put cards back in cell
        this.idToCardMap.clear();
        usernameToCardMap.forEach((k, v) -> v.forEach(c -> this.idToCardMap.put(c.getId(), c)));
    }

    private boolean isOpponentAlive(Map<String, List<Card>> teams) {
        for (List<Card> team : teams.values()) {
            if (team.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
