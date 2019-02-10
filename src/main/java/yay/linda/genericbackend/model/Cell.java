package yay.linda.genericbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import sun.jvm.hotspot.jdi.ArrayReferenceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public void handleClash() {
        // partition
        Map<String, List<Card>> usernameToCardMap = new HashMap<>();
        this.idToCardMap.values().forEach(c -> {
            if (!usernameToCardMap.containsKey(c.getOwner())) {
                usernameToCardMap.put(c.getOwner(), new ArrayList<>());
            }
            usernameToCardMap.get(c.getOwner()).add(c);
        });

        // sort
        usernameToCardMap.values().forEach(Collections::sort);

        // fight
        while (isOpponentAlive(usernameToCardMap)) {
            Map<String, Card> fighters = new HashMap<>();
            usernameToCardMap.forEach((k, v) -> fighters.put(k, v.get(0)));

            List<String> usernames = new ArrayList<>(fighters.keySet());
            String playerA = usernames.get(0);
            String playerB = usernames.get(1);
            Card cardA = fighters.get(playerA);
            Card cardB = fighters.get(playerB);

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

        // put winners back in cell
        usernameToCardMap.forEach((k, v) -> {
            if (!v.isEmpty()) {
                this.idToCardMap.clear();
                v.forEach(c -> this.idToCardMap.put(c.getId(), c));
            }
        });
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
